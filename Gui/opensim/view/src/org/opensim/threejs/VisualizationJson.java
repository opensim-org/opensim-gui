/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.threejs;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.opensim.modeling.ArrayDecorativeGeometry;
import org.opensim.modeling.BodiesList;
import org.opensim.modeling.BodyIterator;
import org.opensim.modeling.Component;
import org.opensim.modeling.ComponentIterator;
import org.opensim.modeling.ComponentsList;
import org.opensim.modeling.DecorativeGeometry;
import org.opensim.modeling.Mesh;
import org.opensim.modeling.Model;
import org.opensim.modeling.ModelDisplayHints;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.PhysicalFrame;
import org.opensim.modeling.State;
import org.opensim.modeling.Transform;
import org.opensim.modeling.Vec3;
import org.opensim.view.ObjectSelectedEvent;
import org.opensim.view.pub.ViewDB;

/**
 *
 * @author Ayman
 */
public class VisualizationJson {
    private State state;
    private Model model;
    private final JSONObject topJson;
    private final HashMap<Integer, PhysicalFrame> mapBodyIndicesToFrames = new HashMap<Integer, PhysicalFrame>();
    private final HashMap<Integer, String> mapBodyIndicesToVisNames = new HashMap<Integer, String>();
    private final double visScaleFactor = 1000.0;
    private HashMap<String, UUID> mapDecorativeGeometryToUUID = new HashMap<String, UUID>();
    private HashMap<String, UUID> mapMaterialToUUID = new HashMap<String, UUID>();
    private HashMap<UUID, Component> mapUUIDToComponent = new HashMap<UUID, Component>();
    private HashMap<OpenSimObject, UUID> mapComponentToUUID = new HashMap<OpenSimObject, UUID>();
    private static String GEOMETRY_SEP = ".";
    
    public VisualizationJson(Model model) {
        topJson =  createJsonForModel(model);
    }
    private JSONObject createJsonForModel(Model model) {
        this.model = model;
        state = model.getWorkingState();
        ModelDisplayHints mdh = model.getDisplayHints();
        ComponentsList mcList = model.getComponentsList();
        ComponentIterator mcIter = mcList.begin();
        JSONObject jsonTop = loadTemplateJSON();
        if (jsonTop == null) {
        }
        BodiesList bodies = model.getBodiesList();
        BodyIterator body = bodies.begin();
        mapBodyIndicesToFrames.put(0, model.getGround());
        while (!body.equals(bodies.end())) {
            int id = body.getMobilizedBodyIndex();
            mapBodyIndicesToFrames.put(id, PhysicalFrame.safeDownCast(body.__deref__()));
            System.out.println("id=" + id + " body =" + body.getName());
            body.next();
        }
        JSONArray json_geometries = (JSONArray) jsonTop.get("geometries");
        JSONArray json_materials = (JSONArray) jsonTop.get("materials");
        JSONObject sceneObject = (JSONObject) jsonTop.get("object");
        JSONArray json_scene_objects = (JSONArray) sceneObject.get("children");
        DecorativeGeometryImplementationJS dgimp = new DecorativeGeometryImplementationJS(json_geometries, visScaleFactor);
        while (!mcIter.equals(mcList.end())) {
            Component comp = mcIter.__deref__();
            ArrayDecorativeGeometry adg = new ArrayDecorativeGeometry();
            comp.generateDecorations(true, mdh, model.getWorkingState(), adg);
            if (adg.size() > 0) {
                DecorativeGeometry dg;
                for (int idx = 0; idx < adg.size(); idx++) {
                    dg = adg.getElt(idx);
                    String geomId = comp.getPathName().concat(GEOMETRY_SEP+String.valueOf(dg.getIndexOnBody()));
                    UUID uuid = UUID.randomUUID();
                    mapDecorativeGeometryToUUID.put(geomId, uuid);
                    dgimp.setGeomID(uuid);
                    dg.implementGeometry(dgimp);
                    UUID uuid_mat = UUID.randomUUID();
                    mapMaterialToUUID.put(geomId, uuid_mat);
                    addMaterialJsonForGeometry(uuid_mat, dg, json_materials);
                    UUID uuid_mesh = addSceneJsonObject(dg, geomId, uuid, uuid_mat, json_scene_objects);
                    mapUUIDToComponent.put(uuid_mesh, comp);
                    // HACK since comp in general has multiple meshes
                    // FIXME
                    mapComponentToUUID.put(comp, uuid_mesh);
                    if (mapBodyIndicesToVisNames.get(dg.getBodyId())==null && Mesh.safeDownCast(comp)!=null){
                        mapBodyIndicesToVisNames.put(dg.getBodyId(), geomId);
                        //System.out.println("Map body id="+dg.getBodyId()+" to mesh uuid"+uuid_mesh+" obj="+geomId);
                    }
                }
            }
            mcIter.next();
        }
        return jsonTop;
    }

    private void addMaterialJsonForGeometry(UUID uuid_mat, DecorativeGeometry dg, JSONArray json_materials) {
        Map<String, Object> mat_json = new LinkedHashMap<String, Object>();
        mat_json.put("uuid", uuid_mat.toString());
        mat_json.put("type", "MeshPhongMaterial");
        String colorString = JSONUtilities.mapColorToRGBA(dg.getColor());
        mat_json.put("color", colorString);
        mat_json.put("shininess", 30);
        mat_json.put("emissive", JSONUtilities.mapColorToRGBA(new Vec3(0., 0., 0.)));
        mat_json.put("specular", JSONUtilities.mapColorToRGBA(new Vec3(0., 0., 0.)));
        mat_json.put("side", 2);
        double opacity = dg.getOpacity();
        if (opacity < 0.999) {
            mat_json.put("opacity", opacity);
            mat_json.put("transparent", true);
        }
        json_materials.add(mat_json);
    }

    private JSONObject loadTemplateJSON() {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            Object obj = parser.parse(new FileReader("visResources/templateScene.json"));
            jsonObject = (JSONObject) obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private UUID addSceneJsonObject(DecorativeGeometry dg, String geomName, UUID uuid, UUID uuid_mat, JSONArray scene_objects) {
        Map<String, Object> obj_json = new LinkedHashMap<String, Object>();
        UUID mesh_uuid = UUID.randomUUID();
        obj_json.put("uuid", mesh_uuid.toString());
        obj_json.put("type", "Mesh");
        obj_json.put("name", geomName);
        obj_json.put("geometry", uuid.toString());
        obj_json.put("material", uuid_mat.toString());
        Transform fullTransform = computeTransform(dg);
        obj_json.put("matrix", JSONUtilities.createMatrixFromTransform(fullTransform, dg.getScaleFactors(), visScaleFactor));
        scene_objects.add(obj_json);
        return mesh_uuid;
    }

    protected Transform computeTransform(DecorativeGeometry dg) {
        int bod = dg.getBodyId();
        Transform relativeTransform = dg.getTransform();
        PhysicalFrame bodyFrame = mapBodyIndicesToFrames.get(bod);
        Transform xform = bodyFrame.getGroundTransform(state);
        Transform fullTransform = xform.compose(relativeTransform);
        return fullTransform;
    }

    /**
     * @return the topJson
     */
    public JSONObject getJson() {
        return topJson;
    }    

    public OpenSimObject findObjectForUUID(String uuidString) {
        return mapUUIDToComponent.get(UUID.fromString(uuidString));
    }

    public UUID findUUIDForObject(OpenSimObject obj) {
        return mapComponentToUUID.get(obj);
    }

    public JSONObject makeXformsJson() {
        JSONObject msg = new JSONObject();
        Iterator<Integer> bodyIdIter = mapBodyIndicesToFrames.keySet().iterator();
        msg.put("Op", "Frame");
        while (bodyIdIter.hasNext()){
            int bodyId = bodyIdIter.next();
            PhysicalFrame bodyFrame = mapBodyIndicesToFrames.get(bodyId);
            Transform xform = bodyFrame.getGroundTransform(state);
            // Get uuid for first Mesh in body
            
            msg.put("name", mapBodyIndicesToVisNames.get(bodyId));
            msg.put("matrix", JSONUtilities.createMatrixFromTransform(xform, new Vec3(1., 1., 1.), visScaleFactor));
            
        }
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
}
