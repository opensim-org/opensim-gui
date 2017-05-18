/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.threejs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.opensim.modeling.AbstractPathPoint;
import org.opensim.modeling.AbstractProperty;
import org.opensim.modeling.ArrayDecorativeGeometry;
import org.opensim.modeling.BodyList;
import org.opensim.modeling.Body;
import org.opensim.modeling.BodyIterator;
import org.opensim.modeling.Component;
import org.opensim.modeling.ComponentIterator;
import org.opensim.modeling.ComponentsList;
import org.opensim.modeling.DecorativeGeometry;
import org.opensim.modeling.FrameGeometry;
import org.opensim.modeling.GeometryPath;
import org.opensim.modeling.Model;
import org.opensim.modeling.ModelDisplayHints;
import org.opensim.modeling.Muscle;
import org.opensim.modeling.MuscleList;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.PathPoint;
import org.opensim.modeling.PathPointSet;
import org.opensim.modeling.PhysicalFrame;
import org.opensim.modeling.State;
import org.opensim.modeling.Transform;
import org.opensim.modeling.Vec3;
import org.opensim.modeling.WrapObject;

/**
 *
 * @author Ayman
 */
public class ModelVisualizationJson extends JSONObject {
    private State state;
    private final HashMap<Integer, PhysicalFrame> mapBodyIndicesToFrames = new HashMap<Integer, PhysicalFrame>();
    private final HashMap<Integer, JSONObject> mapBodyIndicesToJson = new HashMap<Integer, JSONObject>();
    private final static double visScaleFactor = 1000.0;
    private final HashMap<String, UUID> mapDecorativeGeometryToUUID = new HashMap<String, UUID>();
    private final HashMap<UUID, Component> mapUUIDToComponent = new HashMap<UUID, Component>();
    private final HashMap<OpenSimObject, ArrayList<UUID>> mapComponentToUUID = 
            new HashMap<OpenSimObject, ArrayList<UUID>>();
    private final HashMap<String, UUID> mapPathMaterialToUUID = new HashMap<String, UUID>();
    private static final String GEOMETRY_SEP = ".";
    private final Vec3 vec3Unit = new Vec3(1.0, 1.0, 1.0);
    private final HashMap<GeometryPath, UUID> pathList = new HashMap<GeometryPath, UUID>();
    private ModelDisplayHints mdh;
    private DecorativeGeometryImplementationJS dgimp = null;
    private static String boneSuffix = "_Bone";
    private JSONArray json_geometries;
    private JSONArray json_materials;
    private JSONObject model_object;
    private UUID modelUUID;
    private UUID pathpointMatUUID;
    public static boolean verbose=false;
    private boolean ready = false;
    
    public ModelVisualizationJson(JSONObject jsonTopIn, Model model) {
        // implicit super()
        if (verbose)
            System.out.println("start building json for "+model.getName());
        createModelJsonNode(); // Model node
        createJsonForModel(model);
        ready = true;
        if (verbose)
            System.out.println("finished building json for "+model.getName());
    }
    private void createJsonForModel(Model model) {
        state = model.getWorkingState();
        mdh = model.getDisplayHints();
        ComponentsList mcList = model.getComponentsList();
        ComponentIterator mcIter = mcList.begin();
        
        BodyList bodies = model.getBodyList();
        BodyIterator body = bodies.begin();
        mapBodyIndicesToFrames.put(0, model.getGround());
        
        JSONArray json_model_children = (JSONArray) ((JSONObject) get("object")).get("children");
        
        JSONObject model_ground_json = new JSONObject();
        // create model node
        UUID groundUuid = UUID.randomUUID();
        model_ground_json.put("uuid", groundUuid.toString());
        model_ground_json.put("type", "Group");
        model_ground_json.put("opensimtype", "Frame");
        model_ground_json.put("name", model.getGround().getAbsolutePathName());
        model_ground_json.put("userData", "NonEditable");
        model_ground_json.put("model_ground", true);
        json_model_children.add(model_ground_json);
        addComponentToUUIDMap(model.getGround(), groundUuid);
        //System.out.println(model_json.toJSONString());
        JSONArray bodies_json = new JSONArray();
        model_ground_json.put("children", bodies_json);
        mapBodyIndicesToJson.put(0, model_ground_json);
        while (!body.equals(bodies.end())) {
            int id = body.getMobilizedBodyIndex();
            mapBodyIndicesToFrames.put(id, body.__deref__());
            //System.out.println("id=" + id + " body =" + body.getName());
            UUID body_uuid = UUID.randomUUID();
            JSONObject bodyJson = createBodyJson(body.__deref__(), body_uuid);
            mapBodyIndicesToJson.put(id, bodyJson);
            addComponentToUUIDMap(body.__deref__(), body_uuid);
            bodies_json.add(bodyJson);
            //System.out.println(bodyJson.toJSONString());
            body.next();
        }
        //createSkeleton(model, jsonTop);
        // Create material for PathPoints, Markers
        pathpointMatUUID= createPathPointMaterial();
        dgimp = new DecorativeGeometryImplementationJS(json_geometries, json_materials, visScaleFactor);
        while (!mcIter.equals(mcList.end())) {
            Component comp = mcIter.__deref__();
            //System.out.println("Processing:"+comp.getAbsolutePathName()+" Type:"+comp.getConcreteClassName());
            ArrayDecorativeGeometry adg = new ArrayDecorativeGeometry();
            comp.generateDecorations(true, mdh, state, adg);
            if (adg.size() > 0) {
                processDecorativeGeometry(adg, comp, dgimp, json_materials);
            }
            GeometryPath gPath = GeometryPath.safeDownCast(comp);
            boolean isGeometryPath = (gPath!=null);
            if (isGeometryPath){
                UUID pathUUID = createJsonForGeometryPath(gPath, mdh, state, json_geometries, json_materials);
                pathList.put(gPath, pathUUID);
                // Add to the ID map so that PathOwner translates to GeometryPath
                Component parentComp = gPath.getOwner();
                addComponentToUUIDMap(parentComp, pathUUID);
            }
            else{
                adg.clear();
                comp.generateDecorations(false, mdh, state, adg);
                 if (adg.size() > 0) {
                     processDecorativeGeometry(adg, comp, dgimp, json_materials);
                }
            }
            mcIter.next();
        }
    }

    private void addComponentToUUIDMap(Component comp, UUID groupUuid) {
        ArrayList<UUID> comp_uuids = new ArrayList<UUID>();
        comp_uuids.add(groupUuid);
        mapComponentToUUID.put(comp, comp_uuids);
        if (verbose)
            System.out.println("Map component="+comp.getAbsolutePathName()+" to "+comp_uuids.size());   
        
    }

    private void processDecorativeGeometry(ArrayDecorativeGeometry adg, Component comp, 
            DecorativeGeometryImplementationJS dgimp, JSONArray json_materials) {
        DecorativeGeometry dg;
        
        ArrayList<UUID> vis_uuidList = mapComponentToUUID.get(comp);
        if (vis_uuidList == null)
            vis_uuidList = new ArrayList<UUID>(1);
        // Detect partial wrap object and if true set quadrant in dgimp so it's observed
        WrapObject wo = WrapObject.safeDownCast(comp);
        boolean partialWrapObject = (wo != null) && !wo.get_quadrant().toLowerCase().equals("all");
        if (partialWrapObject)
            dgimp.setQuadrants(wo.get_quadrant());
        for (int idx = 0; idx < adg.size(); idx++) {
            dg = adg.getElt(idx);
            String geomId = comp.getAbsolutePathName();
            if (adg.size()>1)
                geomId = geomId.concat(GEOMETRY_SEP+String.valueOf(dg.getIndexOnBody()));
            UUID uuid = UUID.randomUUID();
            mapDecorativeGeometryToUUID.put(geomId, uuid);
            dgimp.setGeomID(uuid);
            // If partialWrapObject set value in dgimp 

            dg.implementGeometry(dgimp);
            JSONObject bodyJson = mapBodyIndicesToJson.get(dg.getBodyId());
            if (bodyJson.get("children")==null)
                bodyJson.put("children", new JSONArray());
            UUID uuid_mesh = addtoFrameJsonObject(dg, geomId, uuid, dgimp.getMat_uuid(), (JSONArray)bodyJson.get("children"));
            vis_uuidList.add(uuid_mesh);
            
            mapUUIDToComponent.put(uuid_mesh, comp);
        }
        if (partialWrapObject)
            dgimp.setQuadrants("");
         mapComponentToUUID.put(comp, vis_uuidList);
        if (verbose)
            System.out.println("Map component="+comp.getAbsolutePathName()+" to "+vis_uuidList.size());   
 
    }

    private void createModelJsonNode() {
        modelUUID = UUID.randomUUID();
        model_object = new JSONObject();
        model_object.put("uuid", modelUUID.toString());
        model_object.put("type", "Group");
        model_object.put("opensimtype", "Model");
        model_object.put("name", "OpenSimModel");
        model_object.put("children", new JSONArray());
        model_object.put("matrix", JSONUtilities.createMatrixFromTransform(new Transform(), new Vec3(1.), 1.0));
        put("object", model_object);
        json_geometries = new JSONArray();
        put("geometries", json_geometries);
        json_materials = new JSONArray();
        put("materials", json_materials);

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
        obj_json.put("castShadow", false);
        obj_json.put("userData", "NonEditable");
        mobody_objects.add(obj_json);
        return mesh_uuid;
    }   

    private JSONObject createBodyJson(Body body, UUID uuid){
        JSONObject bdyJson = new JSONObject();
        bdyJson.put("uuid", uuid.toString());
        bdyJson.put("type", "Group");
        bdyJson.put("opensimtype", "Frame");
        bdyJson.put("userData", "NonEditable");
        bdyJson.put("name", body.getAbsolutePathName());
        PhysicalFrame bodyFrame = mapBodyIndicesToFrames.get(body.getMobilizedBodyIndex());
        Transform bodyXform = bodyFrame.getTransformInGround(state);
        bdyJson.put("matrix", JSONUtilities.createMatrixFromTransform(bodyXform, vec3Unit, visScaleFactor));
        return bdyJson;
    }
    
    public OpenSimObject findObjectForUUID(String uuidString) {
        return mapUUIDToComponent.get(UUID.fromString(uuidString));
    }

    public ArrayList<UUID> findUUIDForObject(OpenSimObject obj) {
        return mapComponentToUUID.get(obj);
    }

    public JSONObject createFrameMessageJson() {
        JSONObject msg = new JSONObject();
        Iterator<Integer> bodyIdIter = mapBodyIndicesToFrames.keySet().iterator();
        msg.put("Op", "Frame");
        JSONArray bodyTransforms_json = new JSONArray();
        msg.put("Transforms", bodyTransforms_json);
        if (ready) { // Avoid trying to send a frame before Json is completely populated
            while (bodyIdIter.hasNext()) {
                int bodyId = bodyIdIter.next();
                if (bodyId ==0) continue; // Skip over "Ground, as unnecessary
                JSONObject oneBodyXform_json = new JSONObject();
                PhysicalFrame bodyFrame = mapBodyIndicesToFrames.get(bodyId);
                if (verbose)
                    System.out.println("Getting transform of "+bodyFrame.getName());
                Transform xform = bodyFrame.getTransformInGround(state);
                // Get uuid for first Mesh in body
                oneBodyXform_json.put("uuid", mapBodyIndicesToJson.get(bodyId).get("uuid"));
                oneBodyXform_json.put("matrix", JSONUtilities.createMatrixFromTransform(xform, new Vec3(1., 1., 1.), visScaleFactor));
                bodyTransforms_json.add(oneBodyXform_json);
            }
            JSONArray geompaths_json = new JSONArray();
            msg.put("paths", geompaths_json);

            Set<GeometryPath> paths = pathList.keySet();
            Iterator<GeometryPath> pathIter = paths.iterator();
            while (pathIter.hasNext()) {
                // get path and call generateDecorations on it
                GeometryPath geomPathObject = pathIter.next();
                UUID pathUUID = pathList.get(geomPathObject);
                JSONObject pathUpdate_json = new JSONObject();
                pathUpdate_json.put("uuid", pathUUID.toString());
                Vec3 pathColor = geomPathObject.getDefaultColor();
                String colorString = JSONUtilities.mapColorToRGBA(pathColor);
                pathUpdate_json.put("color", colorString);
                geompaths_json.add(pathUpdate_json);
                /*
            ArrayDecorativeGeometry adg = new ArrayDecorativeGeometry();
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
       
            }*/
            }
        }
        return msg;
    }

    public JSONObject createSelectionJson(OpenSimObject obj) {
        JSONObject formJSON = new JSONObject();
        ArrayList<UUID> uuids = findUUIDForObject(obj);
        if (uuids != null && uuids.size()==1){
            UUID obj_uuid = uuids.get(0);
            formJSON.put("UUID", obj_uuid.toString());  
            formJSON.put("Op", "Select");  
         }
         return formJSON;
    }

    /**
     * @return the visScaleFactor
     */
    public static double getVisScaleFactor() {
        return visScaleFactor;
    }

    public JSONObject createCloseModelJson() {
        JSONObject guiJson = new JSONObject();
        guiJson.put("UUID", modelUUID.toString());  
        guiJson.put("Op", "CloseModel");
        return guiJson;
    }

    
    public JSONObject createOpenModelJson() {
       JSONObject guiJson = new JSONObject();
        guiJson.put("UUID", modelUUID.toString());  
        guiJson.put("Op", "OpenModel");
        return guiJson;
    }

    private UUID createJsonForGeometryPath(GeometryPath path, ModelDisplayHints mdh, State workingState, JSONArray json_geometries, JSONArray json_materials) {
        // Create material for path
        Map<String, Object> mat_json = new LinkedHashMap<String, Object>();
        UUID mat_uuid = UUID.randomUUID();
        mapPathMaterialToUUID.put(path.getAbsolutePathName(), mat_uuid);
        mat_json.put("uuid", mat_uuid.toString());
        mat_json.put("name", path.getAbsolutePathName()+"Mat");
        mat_json.put("type", "MeshBasicMaterial");
        Vec3 pathColor = path.getDefaultColor();
        String colorString = JSONUtilities.mapColorToRGBA(pathColor);
        mat_json.put("color", colorString);
        mat_json.put("side", 2);
        mat_json.put("skinning", true);
        mat_json.put("transparent", true);
        json_materials.add(mat_json);

        JSONArray pathpoint_jsonArr = new JSONArray();
        // Create plain Geometry with vertices at PathPoints it will have 0 vertices
        // but will be populated libe in the visualizer from the Pathppoints
        JSONObject pathGeomJson = new JSONObject();
        UUID uuidForPathGeomGeometry = UUID.randomUUID();
        pathGeomJson.put("uuid", uuidForPathGeomGeometry.toString());
        pathGeomJson.put("type", "PathGeometry");
        pathGeomJson.put("name", path.getAbsolutePathName()+"Control");
        pathGeomJson.put("segments", path.getPathPointSet().getSize()-1);
        json_geometries.add(pathGeomJson);
        
        PathPointSet ppts = path.getPathPointSet();
        for (int i=0; i< ppts.getSize(); i++){
            AbstractPathPoint pathPoint = ppts.get(i);
            // Create a Sphere with internal opensimType PathPoint
            // attach it to the frame it lives on.
            UUID pathpoint_uuid = addPathPointGeometryToParent(pathPoint, json_geometries, pathpointMatUUID.toString());
            //UUID ppt_json = createJsonForPathPoint(pathPoint);
            pathpoint_jsonArr.add(pathpoint_uuid.toString());
        }
        JSONObject gndJson = mapBodyIndicesToJson.get(0);
        if (gndJson.get("children")==null)
                gndJson.put("children", new JSONArray());
        JSONArray gndChildren = (JSONArray) gndJson.get("children");
        Map<String, Object> obj_json = new LinkedHashMap<String, Object>();
        UUID mesh_uuid = UUID.randomUUID();
        obj_json.put("uuid", mesh_uuid.toString());
        obj_json.put("type", "GeometryPath");
        obj_json.put("name", path.getAbsolutePathName());
        obj_json.put("points", pathpoint_jsonArr);
        obj_json.put("geometry", uuidForPathGeomGeometry.toString());
        obj_json.put("opensimType", "Path");
        gndChildren.add(obj_json);
        // Create json entry for material (path_material) and set skinning to true
        obj_json.put("material", mat_uuid.toString());
        return mesh_uuid;       
    }

    private UUID createJsonForPathPoint(PathPoint ppt) {
        Map<String, Object> ppt_json = new LinkedHashMap<String, Object>();
        JSONArray locationArray = new JSONArray();
        UUID uuid_ppt = UUID.randomUUID();
        ppt_json.put("uuid", uuid_ppt.toString());
        ppt_json.put("name", ppt.getName());
        ppt_json.put("opensimtype", "PathPoint");
        // insert inappropriate body/frame
        PhysicalFrame bdy = ppt.getBody();
        JSONObject bodyJson = mapBodyIndicesToJson.get(bdy.getMobilizedBodyIndex());
        if (bodyJson.get("children")==null)
            bodyJson.put("children", new JSONArray());
        ((JSONArray)bodyJson.get("children")).add(ppt_json);
        return uuid_ppt;
    }

    private void createSkeleton(Model model, JSONObject top_model_json) {
        Map<String, Object> skel_json = new LinkedHashMap<String, Object>();
        UUID skel_uuid = UUID.randomUUID();
        skel_json.put("uuid", skel_uuid.toString());
        skel_json.put("type", "Skeleton");
        skel_json.put("name", "ModelSkeleton");
        skel_json.put("opensimType", "Skeleton");
        //{"parent":-1,"name":"Bone.000","pos":[-1.2628,0.155812,0.0214679],"rotq":[0,0,0,1]}
         JSONArray bones_json = new JSONArray();
         skel_json.put("children", bones_json);
         BodyList bodies = model.getBodyList();
         BodyIterator body = bodies.begin();
         JSONArray posVec3 = new JSONArray();
         JSONArray unitQuaternion = new JSONArray();
         for (int i=0; i<3; i++){
            posVec3.add(0.0);
            unitQuaternion.add(0.0);
         }
         unitQuaternion.add(1.0);
         Map<String, Object> groundBone_json = new LinkedHashMap<String, Object>();
         groundBone_json.put("parent", -1);
         groundBone_json.put("name", model.getGround().getAbsolutePathName()+boneSuffix);
         groundBone_json.put("pos", posVec3);
         groundBone_json.put("rotq", unitQuaternion);
         UUID bone_uuid = UUID.randomUUID();
         bones_json.add(groundBone_json);
         while (!body.equals(bodies.end())) {
            Map<String, Object> obj_json = new LinkedHashMap<String, Object>();
            obj_json.put("name", body.getAbsolutePathName()+boneSuffix);
            obj_json.put("parent", 0);
            Transform xform = body.findTransformBetween(state, model.get_ground());
            Vec3 pos = xform.p();
            JSONArray bodyPosVec3 = new JSONArray();
            for (int i=0; i<3; i++){
                bodyPosVec3.add(visScaleFactor*pos.get(i));
            }
            obj_json.put("pos", bodyPosVec3);
            obj_json.put("rotq", unitQuaternion);
            bones_json.add(obj_json);
            body.next();
         }
        //if (top_model_json.get("children")==null)
        //        top_model_json.put("children", new JSONArray());
        top_model_json.put("skeleton", skel_json);
    }

    private UUID addPathPointGeometryToParent(AbstractPathPoint pathPoint, JSONArray json_geometries, String material ) {
        JSONObject bpptJson = new JSONObject();
        UUID uuidForPathpointGeometry = UUID.randomUUID();
        bpptJson.put("uuid", uuidForPathpointGeometry.toString());
        bpptJson.put("type", "SphereGeometry");
        bpptJson.put("radius", 5);
        bpptJson.put("opensimtype", "PathPoint");
        bpptJson.put("name", pathPoint.getName());
 	bpptJson.put("widthSegments", 32);
	bpptJson.put("heightSegments", 16);
        json_geometries.add(bpptJson);
        // Now add to scene graph
        JSONObject bpptInBodyJson = new JSONObject();
        UUID ppoint_uuid = UUID.randomUUID();
        bpptInBodyJson.put("uuid", ppoint_uuid.toString());
        bpptInBodyJson.put("type", "Mesh");
        bpptInBodyJson.put("name", pathPoint.getName());
        bpptInBodyJson.put("geometry", uuidForPathpointGeometry.toString());
        bpptInBodyJson.put("material", material);
 
        PhysicalFrame bodyFrame = pathPoint.getBody();
        JSONObject bodyJson = mapBodyIndicesToJson.get(bodyFrame.getMobilizedBodyIndex());
        JSONArray children = (JSONArray)bodyJson.get("children");
        Transform localTransform = new Transform();
        Vec3 location = pathPoint.getLocation(state);
        localTransform.setP(location);
        bpptInBodyJson.put("matrix", JSONUtilities.createMatrixFromTransform(localTransform, new Vec3(1.0), visScaleFactor));
        children.add(bpptInBodyJson);
        return ppoint_uuid;
    }

    /**
     * @return the modelUUID
     */
    public UUID getModelUUID() {
        return modelUUID;
    }

    public JSONObject createSetCurrentModelJson() {
        JSONObject guiJson = new JSONObject();
        guiJson.put("UUID", modelUUID.toString());  
        guiJson.put("Op", "SetCurrentModel");
        return guiJson;
    }

    public JSONObject createToggleModelVisibilityCommand(boolean newValue) {
        JSONObject guiJson = new JSONObject();
        guiJson.put("Op", "execute");
        JSONObject commandJson = CommandComposerThreejs.createSetVisibleCommandJson(newValue, modelUUID);
        guiJson.put("command", commandJson);
        return guiJson;
    }

    public JSONObject createTranslateObjectCommand(Vec3 newValue) {
        JSONObject guiJson = new JSONObject();
        guiJson.put("Op", "execute");
        JSONObject commandJson = CommandComposerThreejs.createTranslateObjectCommandJson(newValue, modelUUID);
        guiJson.put("command", commandJson);
        return guiJson;
    }
    
    public JSONObject createToggleObjectVisibilityCommand(OpenSimObject obj, boolean newValue) {
        JSONObject guiJson = new JSONObject();
        guiJson.put("Op", "execute");
        ArrayList<UUID> uuids = findUUIDForObject(obj);
        if (uuids != null && uuids.size()==1){
        UUID objectUuid = uuids.get(0);
            JSONObject commandJson = CommandComposerThreejs.createSetVisibleCommandJson(newValue, objectUuid);
            guiJson.put("command", commandJson);
        }
        return guiJson;
    }

    public JSONObject createAppearanceMessage(Component mc, AbstractProperty prop) {
        JSONObject guiJson = new JSONObject();
        guiJson.put("Op", "execute");
        ArrayList<UUID> uuids = findUUIDForObject(mc);
        if (uuids != null && uuids.size()==1){
        UUID objectUuid = uuids.get(0);
            JSONObject commandJson = CommandComposerThreejs.createAppearanceChangeJson(prop, objectUuid);
            guiJson.put("command", commandJson);
        }
        return guiJson;
    }

    private UUID createPathPointMaterial() {
        Map<String, Object> mat_json = new LinkedHashMap<String, Object>();
        UUID mat_uuid = UUID.randomUUID();
        mat_json.put("uuid", mat_uuid.toString());
        mat_json.put("name", "PathPointMat");
        mat_json.put("type", "MeshBasicMaterial");
        String colorString = JSONUtilities.mapColorToRGBA(new Vec3(0., 0., 1.));
        mat_json.put("color", colorString);
        mat_json.put("side", 2);
        json_materials.add(mat_json);
        return mat_uuid;
    }

}
