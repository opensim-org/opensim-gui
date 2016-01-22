/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.threejs;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openide.util.Exceptions;
import org.opensim.modeling.ArrayDecorativeGeometry;
import org.opensim.modeling.BodiesList;
import org.opensim.modeling.Body;
import org.opensim.modeling.BodyIterator;
import org.opensim.modeling.Component;
import org.opensim.modeling.ComponentIterator;
import org.opensim.modeling.ComponentsList;
import org.opensim.modeling.DecorativeGeometry;
import org.opensim.modeling.GeometryPath;
import org.opensim.modeling.Mesh;
import org.opensim.modeling.Model;
import org.opensim.modeling.ModelDisplayHints;
import org.opensim.modeling.MuscleIterator;
import org.opensim.modeling.MusclesList;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.PhysicalFrame;
import org.opensim.modeling.State;
import org.opensim.modeling.Transform;
import org.opensim.modeling.Vec3;

/**
 *
 * @author Ayman
 */
public class VisualizationJson {
    private State state;
    private final JSONObject topJson;
    private final HashMap<Integer, PhysicalFrame> mapBodyIndicesToFrames = new HashMap<Integer, PhysicalFrame>();
    private final HashMap<Integer, JSONObject> mapBodyIndicesToJson = new HashMap<Integer, JSONObject>();
    private final static double visScaleFactor = 1000.0;
    private final HashMap<String, UUID> mapDecorativeGeometryToUUID = new HashMap<String, UUID>();
    private final HashMap<UUID, Component> mapUUIDToComponent = new HashMap<UUID, Component>();
    private final HashMap<OpenSimObject, ArrayList<UUID>> mapComponentToUUID = 
            new HashMap<OpenSimObject, ArrayList<UUID>>();
    private static final String GEOMETRY_SEP = ".";
    private final Vec3 vec3Unit = new Vec3(1.0, 1.0, 1.0);
    private MusclesList muscleList = null;
    private ModelDisplayHints mdh;
    private DecorativeGeometryImplementationJS dgimp = null;
    
    public VisualizationJson(Model model) {
        topJson =  createJsonForModel(model);
    }
    private JSONObject createJsonForModel(Model model) {
        state = model.getWorkingState();
        mdh = model.getDisplayHints();
        ComponentsList mcList = model.getComponentsList();
        muscleList = model.getMusclesList();
        ComponentIterator mcIter = mcList.begin();
        // Load template 
        JSONObject jsonTop = loadTemplateJSON();
        BodiesList bodies = model.getBodiesList();
        BodyIterator body = bodies.begin();
        mapBodyIndicesToFrames.put(0, model.getGround());
        
        JSONArray json_geometries = (JSONArray) jsonTop.get("geometries");
        JSONArray json_materials = (JSONArray) jsonTop.get("materials");
        JSONObject sceneObject = (JSONObject) jsonTop.get("object");
        JSONArray json_scene_children = (JSONArray) sceneObject.get("children");
        
        JSONObject model_json = new JSONObject();
        json_scene_children.add(model_json);
        // create model node
        model_json.put("uuid", UUID.randomUUID().toString());
        model_json.put("type", "Group");
        model_json.put("name", model.getName()+":Ground");
        //System.out.println(model_json.toJSONString());
        JSONArray bodies_json = new JSONArray();
        model_json.put("children", bodies_json);
        mapBodyIndicesToJson.put(0, model_json);
        while (!body.equals(bodies.end())) {
            int id = body.getMobilizedBodyIndex();
            mapBodyIndicesToFrames.put(id, body.__deref__());
            //System.out.println("id=" + id + " body =" + body.getName());
            JSONObject bodyJson = createBodyJson(body.__deref__());
            mapBodyIndicesToJson.put(id, bodyJson);
            bodies_json.add(bodyJson);
            //System.out.println(bodyJson.toJSONString());
            body.next();
        }
        dgimp = new DecorativeGeometryImplementationJS(json_geometries, json_materials, visScaleFactor);
        while (!mcIter.equals(mcList.end())) {
            Component comp = mcIter.__deref__();
            ArrayDecorativeGeometry adg = new ArrayDecorativeGeometry();
            comp.generateDecorations(true, mdh, model.getWorkingState(), adg);
            if (adg.size() > 0) {
                processDecorativeGeometry(adg, comp, dgimp, json_materials);
            }
            adg.clear();
            comp.generateDecorations(false, mdh, model.getWorkingState(), adg);
            boolean isGeometryPath = (GeometryPath.safeDownCast(comp)!=null);
            if (adg.size() > 0) {
                processDecorativeGeometry(adg, comp, dgimp, json_materials);
            }
            mcIter.next();
        }
        return jsonTop;
    }

    private void processDecorativeGeometry(ArrayDecorativeGeometry adg, Component comp, 
            DecorativeGeometryImplementationJS dgimp, JSONArray json_materials) {
        DecorativeGeometry dg;
        ArrayList<UUID> vis_uuidList = new ArrayList<UUID>(1);
        for (int idx = 0; idx < adg.size(); idx++) {
            dg = adg.getElt(idx);
            String geomId = comp.getPathName();
            if (adg.size()>1)
                geomId = geomId.concat(GEOMETRY_SEP+String.valueOf(dg.getIndexOnBody()));
            UUID uuid = UUID.randomUUID();
            mapDecorativeGeometryToUUID.put(geomId, uuid);
            dgimp.setGeomID(uuid);
            dg.implementGeometry(dgimp);
            JSONObject bodyJson = mapBodyIndicesToJson.get(dg.getBodyId());
            if (bodyJson.get("children")==null)
                bodyJson.put("children", new JSONArray());
            UUID uuid_mesh = addtoFrameJsonObject(dg, geomId, uuid, dgimp.getMat_uuid(), (JSONArray)bodyJson.get("children"));
            vis_uuidList.add(uuid_mesh);
            
            mapUUIDToComponent.put(uuid_mesh, comp);
        }
        mapComponentToUUID.put(comp, vis_uuidList);
        System.out.println("Map component="+comp.getPathName()+" to "+vis_uuidList.size());   
 
    }

    private JSONObject loadTemplateJSON() {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = null;
        String current;
        try {
            current = new java.io.File( "." ).getCanonicalPath();
            System.out.println("Current dir:"+current);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        try {
            Object obj = parser.parse(new FileReader("visResources/templateScene.json"));
            jsonObject = (JSONObject) obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private UUID addtoFrameJsonObject(DecorativeGeometry dg, String geomName, UUID uuid, UUID uuid_mat, JSONArray mobody_objects) {
        Map<String, Object> obj_json = new LinkedHashMap<String, Object>();
        UUID mesh_uuid = UUID.randomUUID();
        obj_json.put("uuid", mesh_uuid.toString());
        obj_json.put("type", "Mesh");
        obj_json.put("name", geomName);
        obj_json.put("geometry", uuid.toString());
        obj_json.put("material", uuid_mat.toString());
        obj_json.put("matrix", JSONUtilities.createMatrixFromTransform(dg.getTransform(), dg.getScaleFactors(), visScaleFactor));
        mobody_objects.add(obj_json);
        return mesh_uuid;
    }


    /**
     * @return the topJson
     */
    public JSONObject getJson() {
        return topJson;
    }    

    private JSONObject createBodyJson(Body body){
        JSONObject bdyJson = new JSONObject();
        bdyJson.put("uuid", UUID.randomUUID().toString());
        bdyJson.put("type", "Group");
        bdyJson.put("name", body.getName());
        PhysicalFrame bodyFrame = mapBodyIndicesToFrames.get(body.getMobilizedBodyIndex());
        Transform bodyXform = bodyFrame.getGroundTransform(state);
        bdyJson.put("matrix", JSONUtilities.createMatrixFromTransform(bodyXform, vec3Unit, visScaleFactor));
        return bdyJson;
    }
    
    public OpenSimObject findObjectForUUID(String uuidString) {
        return mapUUIDToComponent.get(UUID.fromString(uuidString));
    }

    public UUID findUUIDForObject(OpenSimObject obj) {
        return mapComponentToUUID.get(obj).get(0);
    }

    public JSONObject createFrameMessageJson() {
        JSONObject msg = new JSONObject();
        Iterator<Integer> bodyIdIter = mapBodyIndicesToFrames.keySet().iterator();
        msg.put("Op", "Frame");
        JSONArray bodyTransforms_json = new JSONArray();
        msg.put("Transforms", bodyTransforms_json);
        while (bodyIdIter.hasNext()){
            int bodyId = bodyIdIter.next();
            JSONObject oneBodyXform_json = new JSONObject();
            PhysicalFrame bodyFrame = mapBodyIndicesToFrames.get(bodyId);
            Transform xform = bodyFrame.getGroundTransform(state);
            // Get uuid for first Mesh in body
            oneBodyXform_json.put("name", mapBodyIndicesToJson.get(bodyId).get("name"));
            oneBodyXform_json.put("matrix", JSONUtilities.createMatrixFromTransform(xform, new Vec3(1., 1., 1.), visScaleFactor));
            bodyTransforms_json.add(oneBodyXform_json);
        }
        /*
        JSONArray geompaths_json = new JSONArray();
        msg.put("paths", geompaths_json);
        MuscleIterator muscleIter = muscleList.begin();
        while(!muscleIter.equals(muscleList.end())){
            // get path and call generateDecorations on it
            GeometryPath geomPathObject = muscleIter.getGeometryPath();
            ArrayDecorativeGeometry adg = new ArrayDecorativeGeometry();
            JSONObject pathUpdate_json = new JSONObject();
            geomPathObject.generateDecorations(false, mdh, state, adg);
            ArrayList<UUID> existing_uuids = mapComponentToUUID.get(geomPathObject);
            // segments are at index 2, 4, 6, ... in uuid_list
            for(int decoGeomIndex = 2; decoGeomIndex < adg.size(); decoGeomIndex+=2){
                DecorativeGeometry dg = adg.getElt(decoGeomIndex);
                UUID current_uuid = existing_uuids.get(decoGeomIndex);
                dgimp.updateGeometry(dg, current_uuid);
                System.out.println("Update decorative geomtry for uuid "+existing_uuids.get(decoGeomIndex).toString());
                pathUpdate_json.put("uuid", existing_uuids.get(decoGeomIndex).toString());
                Map<String, Object> positionsJson = (Map<String, Object>)dgimp.getLast_json().get("positions");
                pathUpdate_json.put("positions", positionsJson.get("array"));
                geompaths_json.add(pathUpdate_json);
       
            }
            // Find uuid for muscle based on Path and send new coordinates
            muscleIter.next();
        }
        */
        //System.out.println("Sending:"+msg.toJSONString());
        return msg;
    }

    public JSONObject createSelectionJson(OpenSimObject obj) {
        JSONObject formJSON = new JSONObject();
        UUID obj_uuid = findUUIDForObject(obj);
        formJSON.put("UUID", obj_uuid.toString());  
        formJSON.put("Op", "Select");  
        return formJSON;
    }

    /**
     * @return the visScaleFactor
     */
    public static double getVisScaleFactor() {
        return visScaleFactor;
    }
}
