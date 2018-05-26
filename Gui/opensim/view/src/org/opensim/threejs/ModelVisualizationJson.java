/* -------------------------------------------------------------------------- *
 * OpenSim: ModelVisualizationJson.java                                       *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib                                                     *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may    *
 * not use this file except in compliance with the License. You may obtain a  *
 * copy of the License at http://www.apache.org/licenses/LICENSE-2.0          *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 * -------------------------------------------------------------------------- */
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
import java.util.prefs.Preferences;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.openide.util.NbBundle;
import org.opensim.modeling.AbstractPathPoint;
import org.opensim.modeling.AbstractProperty;
import org.opensim.modeling.ArrayDecorativeGeometry;
import org.opensim.modeling.ArrayPathPoint;
import org.opensim.modeling.ArrayVec3;
import org.opensim.modeling.BodyList;
import org.opensim.modeling.Body;
import org.opensim.modeling.BodyIterator;
import org.opensim.modeling.Component;
import org.opensim.modeling.ComponentIterator;
import org.opensim.modeling.ComponentsList;
import org.opensim.modeling.ConditionalPathPoint;
import org.opensim.modeling.DecorativeGeometry;
import org.opensim.modeling.Frame;
import org.opensim.modeling.FrameGeometry;
import org.opensim.modeling.Geometry;
import org.opensim.modeling.GeometryPath;
import org.opensim.modeling.Marker;
import org.opensim.modeling.Mesh;
import org.opensim.modeling.Model;
import org.opensim.modeling.ModelDisplayHints;
import org.opensim.modeling.MovingPathPoint;
import org.opensim.modeling.Muscle;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.PathPoint;
import org.opensim.modeling.PathPointSet;
import org.opensim.modeling.PathWrapPoint;
import org.opensim.modeling.PhysicalFrame;
import org.opensim.modeling.PropertyHelper;
import org.opensim.modeling.State;
import org.opensim.modeling.Transform;
import org.opensim.modeling.Vec3;
import org.opensim.modeling.WrapObject;
import org.opensim.utils.TheApp;
import org.opensim.view.experimentaldata.ModelForExperimentalData;
import org.opensim.view.motions.MotionDisplayer;
import org.opensim.view.pub.OpenSimDB;

/**
 *
 * @author Ayman
 */
public class ModelVisualizationJson extends JSONObject {

    /**
     * @return the currentPathColorMap
     */
    public PathColorMap getCurrentPathColorMap() {
        return currentPathColorMap;
    }

    static {
        PathColorMapFactory.registerPathColorMap("Classic", new LegacyPathColorMap());
        PathColorMapFactory.registerPathColorMap("Modern", new ModernPathColorMap());
    }
    /**
     * @param currentPathColorMap the currentPathColorMap to set
     */
    public void setCurrentPathColorMap(PathColorMap currentPathColorMap) {
        this.currentPathColorMap = currentPathColorMap;
    }

    /**
     * @return the showCom
     */
    public boolean isShowCom() {
        return showCom;
    }

    /**
     * @param showCom the showCom to set
     */
    public void setShowCom(boolean showCom) {
        this.showCom = showCom;
    }
    private final Model model;
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
    private final HashMap<GeometryPath, UUID> pathList = new HashMap<GeometryPath, UUID>();
    private ModelDisplayHints mdh;
    private DecorativeGeometryImplementationJS dgimp = null;
    private static String boneSuffix = "_Bone";
    private JSONArray json_geometries;
    private JSONArray json_materials;
    private JSONObject model_object;
    private Transform transformWRTScene = new Transform();
    private UUID modelUUID;
    private UUID pathpointMatUUID;
    private UUID markerMatUUID;
    private JSONObject pathPointGeometryJSON = null;
    private JSONObject marker_mat_json;
    public static boolean verbose=false;
    private boolean ready = false;
    private static final HashMap<String, Boolean> movableOpensimTypes = new HashMap<String, Boolean>();
    private final ArrayList<MotionDisplayer> motionDisplayers = new ArrayList<MotionDisplayer>();
    private JSONObject modelGroundJson=null;
    private boolean movable=true;
    // List of all Components that need special treatment as in not statically attached:
    // MovingPathPoint for now
    private final HashMap<Component, UUID> movingComponents = new HashMap<Component, UUID>();
    // For ConditionalPathPoint we use Active PathPoints as proxy when inactive.
    private final HashMap<AbstractPathPoint, ComputedPathPointInfo> proxyPathPoints = new HashMap<AbstractPathPoint, ComputedPathPointInfo>();
    private final HashMap<UUID, ComputedPathPointInfo> computedPathPoints = new HashMap<UUID, ComputedPathPointInfo>();
    private final HashMap<PathWrapPoint, ArrayList<UUID>> wrapPathPoints = new HashMap<PathWrapPoint, ArrayList<UUID>>();
    // Keep track of which paths have wrapping since they need special handling
    // When wrapping comes in/out
    private final HashMap<GeometryPath, JSONArray> pathsWithWrapping = new HashMap<GeometryPath, JSONArray>();
    private final HashMap<Frame, VisualizerFrame> visualizerFrames = new HashMap<Frame, VisualizerFrame>();
    private ArrayList<VisualizerAddOn> visualizerAddOns = new ArrayList<VisualizerAddOn>();
    private VisualizerAddOnCom comVizAddOn = new VisualizerAddOnCom();
    private boolean showCom = false;
    private PathColorMap currentPathColorMap;
    // Preferences
    private double prefMuscleDisplayRadius=0.005;
    
    public Boolean getFrameVisibility(Frame b) {
        return visualizerFrames.get(b).visible;
    }
    public void setFrameVisibility(Frame b, Boolean newValue) {
        Boolean oldValue = visualizerFrames.get(b).visible;
        if (oldValue != newValue){
            visualizerFrames.get(b).visible = newValue;
            // send Visibility change command to visualizer
        }
    }

    public FrameGeometry getGeometryForFrame(Frame frame) {
        return visualizerFrames.get(frame).fg;
    }
    /**
     * Update transforms cached on the visualizer side when frame definition changes
     * @param subtreeRoot
     * @return 
     */
    public JSONObject createUpdateDecorationsMessageJson(Component subtreeRoot) {
        JSONObject msg = new JSONObject();
        msg.put("Op", "Frame");
        JSONArray geomTransforms_json = new JSONArray();
        msg.put("Transforms", geomTransforms_json);
        ComponentsList mcList = subtreeRoot.getComponentsList();
        ComponentIterator mcIter = mcList.begin();
        mcIter = mcList.begin();
        while (!mcIter.equals(mcList.end())) {
            Component comp = mcIter.__deref__();
            boolean visibleStatus = true;
            AbstractProperty visibleProp = null;
            if (comp.hasProperty("Appearance")){
                visibleProp = comp.getPropertyByName("Appearance").getValueAsObject().getPropertyByName("visible");
                visibleStatus = PropertyHelper.getValueBool(visibleProp);
                if (!visibleStatus)
                    PropertyHelper.setValueBool(true, visibleProp);
            }
            ArrayDecorativeGeometry adg = new ArrayDecorativeGeometry();
            comp.generateDecorations(true, mdh, state, adg);
            ArrayList<UUID> vis_uuidList = mapComponentToUUID.get(comp);
            if (vis_uuidList !=null){
                int idx = 0;
                if (vis_uuidList.size()==adg.size()){
                    for (UUID uuid:vis_uuidList){
                        JSONObject oneGeomXform_json = new JSONObject();
                        oneGeomXform_json.put("uuid", uuid.toString());
                        oneGeomXform_json.put("matrix", 
                                JSONUtilities.createMatrixFromTransform(adg.at(idx).getTransform(), adg.at(idx).getScaleFactors(), 
                                        visScaleFactor));
                        geomTransforms_json.add(oneGeomXform_json);
                        idx++;
                    }
                }
            }
            if (!visibleStatus){
                PropertyHelper.setValueBool(false, visibleProp);
            }
            mcIter.next();
        }
        return msg;
    }

    private void addCusomAddons() {
        comVizAddOn.init(this);
        visualizerAddOns.add(comVizAddOn);
    }

    public BodyVisualizationJson getBodyRep(Component comp) {
       Body bdy = Body.safeDownCast(comp);
       return (BodyVisualizationJson) mapBodyIndicesToJson.get(bdy.getMobilizedBodyIndex());
    }

    public UUID getComObjectUUID() {
        return comVizAddOn.getObjectUUID();
    }
     // The following inner class and Map are used to cache "computed" pathpoints to speed up 
    // recomputation on the fly
    class ComputedPathPointInfo {
        AbstractPathPoint pt1;
        AbstractPathPoint pt2;
        double ratio;
        ComputedPathPointInfo(AbstractPathPoint p1, AbstractPathPoint p2, double ratio){
            this.pt1 = p1; this.pt2 = p2; this.ratio = ratio;
        }
    }
    
    class VisualizerFrame {
        FrameGeometry fg;
        boolean visible;
    }
    private HashMap<AbstractPathPoint, ComputedPathPointInfo> computedPathPointMap = new HashMap<AbstractPathPoint, ComputedPathPointInfo>();
    
    static {
        movableOpensimTypes.put("Marker", true);
        movableOpensimTypes.put("PathPoint", true);
    }
    public ModelVisualizationJson(JSONObject jsonTopIn, Model model) {
        // implicit super()
        if (verbose)
            System.out.println("start building json for "+model.getName());
        this.model = model;
        movable = (model instanceof ModelForExperimentalData);
        createModelJsonNode(); // Model node
        // Decide color Scheme for muscles
        String saved = "Classic";
        String currentTemplate =Preferences.userNodeForPackage(TheApp.class).get("Muscle Color Scheme", saved);
        Preferences.userNodeForPackage(TheApp.class).put("Muscle Color Scheme", currentTemplate);
        currentPathColorMap = PathColorMapFactory.getColorMap(currentTemplate);
        
        // Decide on bone shape/width
        saved = ".005";
        String currentSize= Preferences.userNodeForPackage(TheApp.class).get("Muscle Display Radius", saved);
        Preferences.userNodeForPackage(TheApp.class).put("Muscle Display Radius", currentSize);
        prefMuscleDisplayRadius = Double.parseDouble(currentSize);
        
        createJsonForModel(model);
        ready = true;
        if (verbose)
            System.out.println("finished building json for "+model.getName());
        addCusomAddons();
    }
    private void createJsonForModel(Model model) {
        modelGroundJson = processGroundFrame(model);
        state = OpenSimDB.getInstance().getContext(model).getCurrentStateRef();
        mdh = model.getDisplayHints();
        mdh.set_show_frames(true);
        ComponentsList mcList = model.getComponentsList();
        ComponentIterator mcIter = mcList.begin();
        
        BodyList bodies = model.getBodyList();
        BodyIterator body = bodies.begin();
        //System.out.println(model_json.toJSONString());
        JSONArray bodies_json = new JSONArray();
        modelGroundJson.put("children", bodies_json);
        while (!body.equals(bodies.end())) {
            int id = body.getMobilizedBodyIndex();
            mapBodyIndicesToFrames.put(id, body.__deref__());
            //System.out.println("id=" + id + " body =" + body.getName());
            UUID body_uuid = UUID.randomUUID();
            BodyVisualizationJson bodyJson = createBodyJson(body.__deref__(), body_uuid);
            mapBodyIndicesToJson.put(id, bodyJson);
            addComponentToUUIDMap(body.__deref__(), body_uuid);
            bodies_json.add(bodyJson);
            bodyJson.addBodyCom(this);
            //System.out.println(bodyJson.toJSONString());
            body.next();
        }
        //createSkeleton(model, jsonTop);
        // Create material for PathPoints, Markers
        pathpointMatUUID= createPathPointMaterial();
        markerMatUUID= createMarkerMaterial(mdh);
        pathPointGeometryJSON = createPathPointGeometryJSON();
        dgimp = new DecorativeGeometryImplementationJS(json_geometries, json_materials, visScaleFactor);
        while (!mcIter.equals(mcList.end())) {
            Component comp = mcIter.__deref__();
            processDecorationsForComponent(comp);
            mcIter.next();
        }
        mdh.set_show_frames(false);
    }

    private void processDecorationsForComponent(Component comp) {
        if (verbose){
            System.out.println("Processing:"+comp.getAbsolutePathString()+" Type:"+comp.getConcreteClassName());
        }
        ArrayDecorativeGeometry adg = new ArrayDecorativeGeometry();
        // generateDecoration skips over when "visible" is false
        // we temporarily turn on and use the setVisible attribute so that
        // geometry can be shown later. 
        boolean visibleStatus = true;
        AbstractProperty visibleProp = null;
        if (comp.hasProperty("Appearance")){
            visibleProp = comp.getPropertyByName("Appearance").getValueAsObject().getPropertyByName("visible");
            visibleStatus = PropertyHelper.getValueBool(visibleProp);
            if (!visibleStatus)
                PropertyHelper.setValueBool(true, visibleProp);
        }
        comp.generateDecorations(true, mdh, state, adg);
        if (adg.size() > 0) {
            processDecorativeGeometry(adg, comp, dgimp, json_materials, visibleStatus);
        }
        FrameGeometry frameGeometry = FrameGeometry.safeDownCast(comp);
        if (frameGeometry!= null){
            Frame ownerFrame = Frame.safeDownCast(comp.getOwner());
            if (ownerFrame !=null){
                // Frame visualization 
                VisualizerFrame vf = new VisualizerFrame();
                vf.fg = frameGeometry;
                vf.visible = false;
                visualizerFrames.put(ownerFrame, vf);
            }
        }
        GeometryPath gPath = GeometryPath.safeDownCast(comp);
        boolean isGeometryPath = (gPath!=null);
        if (isGeometryPath){
            UUID pathUUID = createJsonForGeometryPath(gPath, mdh, json_geometries, json_materials, visibleStatus);
            pathList.put(gPath, pathUUID);
            // Add to the ID map so that PathOwner translates to GeometryPath
            Component parentComp = gPath.getOwner();
            addComponentToUUIDMap(parentComp, pathUUID);
        }
        else{
            adg.clear();
            comp.generateDecorations(false, mdh, state, adg);
            if (adg.size() > 0) {
                processDecorativeGeometry(adg, comp, dgimp, json_materials, visibleStatus);
            }
        }
        // restore visibleStatus that may have been switched earlier, 
        if (!visibleStatus){
             PropertyHelper.setValueBool(false, visibleProp);
        }

    }

    private JSONObject processGroundFrame(Model model) {
        mapBodyIndicesToFrames.put(0, model.getGround());
        JSONArray json_model_children = (JSONArray) ((JSONObject) get("object")).get("children");
        JSONObject model_ground_json = new JSONObject();
        // create model node
        UUID groundUuid = UUID.randomUUID();
        model_ground_json.put("uuid", groundUuid.toString());
        model_ground_json.put("type", "Group");
        model_ground_json.put("opensimType", "Frame");
        model_ground_json.put("name", model.getGround().getAbsolutePathString());
        model_ground_json.put("userData", "NonEditable");
        model_ground_json.put("model_ground", true);
        json_model_children.add(model_ground_json);
        addComponentToUUIDMap(model.getGround(), groundUuid);
        mapBodyIndicesToJson.put(0, model_ground_json);
        return model_ground_json;
    }

    private void addComponentToUUIDMap(Component comp, UUID groupUuid) {
        ArrayList<UUID> comp_uuids = new ArrayList<UUID>();
        comp_uuids.add(groupUuid);
        mapComponentToUUID.put(comp, comp_uuids);
        
    }
    // This method handles the DecorativeGeometry array produced by the component. It does special
    // handling to deal with WrapObjects whose generateDecorations doesn't handle partial
    // primitives at API level, also frames since theydon't use the Geometry/Material arrangement
    // but rather plug directly into the scene graph
    private void processDecorativeGeometry(ArrayDecorativeGeometry adg, Component comp, 
            DecorativeGeometryImplementationJS dgimp, JSONArray json_materials, boolean visible) {
        DecorativeGeometry dg;
        
        ArrayList<UUID> vis_uuidList = mapComponentToUUID.get(comp);
        if (vis_uuidList == null)
            vis_uuidList = new ArrayList<UUID>(1);
        // Detect partial wrap object and if true set quadrant in dgimp so it's observed
        WrapObject wo = WrapObject.safeDownCast(comp);
        boolean partialWrapObject = (wo != null) && !wo.get_quadrant().toLowerCase().equals("all");
        if (partialWrapObject)
            dgimp.setQuadrants(wo.get_quadrant());
        // If Marker, don't create a new metrial, instead reuse MarkerMat
        boolean isMarker = Marker.safeDownCast(comp)!=null;
        if (isMarker)
            dgimp.useMaterial(markerMatUUID);

        for (int idx = 0; idx < adg.size(); idx++) {
            dg = adg.getElt(idx);
            String geomId = comp.getAbsolutePathString();
            if (adg.size()>1)
                geomId = geomId.concat(GEOMETRY_SEP+String.valueOf(dg.getIndexOnBody()));
            UUID uuid = UUID.randomUUID();
            mapDecorativeGeometryToUUID.put(geomId, uuid);
            // FrameGeometry is not a "Mesh" but rather an Object that has both
            // Geometry and Material embedded, will treat as special here and add
            // directly to scene graph.
            if (FrameGeometry.safeDownCast(comp)!=null){
                JSONObject bodyJson = mapBodyIndicesToJson.get(dg.getBodyId());
                if (bodyJson.get("children")==null)
                    bodyJson.put("children", new JSONArray());
                 UUID uuid_frame = createFrameObjectJSON(dg, FrameGeometry.safeDownCast(comp));
                 vis_uuidList.add(uuid_frame);
                 mapUUIDToComponent.put(uuid_frame, comp);              
            }
            else {
                // If partialWrapObject set value in dgimp 
                dgimp.setGeomID(uuid);
                dg.implementGeometry(dgimp);
                
                JSONObject bodyJson = mapBodyIndicesToJson.get(dg.getBodyId());
                if (bodyJson.get("children")==null)
                    bodyJson.put("children", new JSONArray());
                UUID uuid_mesh = addtoFrameJsonObject(dg, geomId, uuid, dgimp.getMat_uuid(), (JSONArray)bodyJson.get("children"), 
                        comp, visible);
                vis_uuidList.add(uuid_mesh);

                mapUUIDToComponent.put(uuid_mesh, comp);
            }
        }
        if (partialWrapObject)
            dgimp.setQuadrants("");
        if (isMarker)
            dgimp.useMaterial(null);

        mapComponentToUUID.put(comp, vis_uuidList);
        if (verbose)
            System.out.println("Map component="+comp.getAbsolutePathString()+" to "+vis_uuidList.size());   
 
    }

    private void createModelJsonNode() {
        modelUUID = UUID.randomUUID();
        model_object = new JSONObject();
        model_object.put("uuid", modelUUID.toString());
        model_object.put("type", "Group");
        model_object.put("opensimType", "Model");
        model_object.put("name", "OpenSimModel");
        model_object.put("children", new JSONArray());
        model_object.put("matrix", JSONUtilities.createMatrixFromTransform(getTransformWRTScene(), new Vec3(1.), 1.0));
        put("object", model_object);
        json_geometries = new JSONArray();
        put("geometries", json_geometries);
        json_materials = new JSONArray();
        put("materials", json_materials);

    }

    public Model getModel() {
        return this.model;
    }
    
    public State getState() {
        return state;
    }
    
    private UUID addtoFrameJsonObject(DecorativeGeometry dg, String geomName, UUID uuid, UUID uuid_mat, JSONArray mobody_objects, Component opensimComponent, boolean visible) {
        Map<String, Object> obj_json = createJSONObjectFromDisplayGeometry(geomName, opensimComponent, uuid, uuid_mat, dg, mobody_objects, visible);
        UUID mesh_uuid = UUID.fromString((String) obj_json.get("uuid"));
        return mesh_uuid;
    }   

    protected JSONObject createJSONObjectFromDisplayGeometry(String geomName, Component opensimComponent, UUID uuid_geom, UUID uuid_mat, 
            DecorativeGeometry dg, JSONArray mobody_objects, boolean visible) {
        UUID mesh_uuid = UUID.randomUUID();
        JSONObject obj_json = new JSONObject();
        obj_json.put("uuid", mesh_uuid.toString());
        obj_json.put("type", "Mesh");
        obj_json.put("name", geomName);
        obj_json.put("opensimType", opensimComponent.getConcreteClassName());
        obj_json.put("geometry", uuid_geom.toString());
        obj_json.put("material", uuid_mat.toString());
        obj_json.put("matrix", JSONUtilities.createMatrixFromTransform(dg.getTransform(), dg.getScaleFactors(), visScaleFactor));
        obj_json.put("castShadow", false);
        if (!visible){
            obj_json.put("visible", false);
        }
        String concreteType = opensimComponent.getConcreteClassName();
        if (!movableOpensimTypes.keySet().contains(concreteType))
            obj_json.put("userData", "NonEditable");
        mobody_objects.add(obj_json);
        return obj_json;
    }   

    private BodyVisualizationJson createBodyJson(Body body, UUID uuid){
        return new BodyVisualizationJson(body, uuid, this);
    }
    
    public OpenSimObject findObjectForUUID(String uuidString) {
        OpenSimObject obj = mapUUIDToComponent.get(UUID.fromString(uuidString));
        if (obj != null) return obj;
        for (MotionDisplayer motDisplayer:motionDisplayers){
            obj = motDisplayer.findObjectForUUID(uuidString);
            if (obj != null) return obj;
        }
        return obj;
    }

    public ArrayList<UUID> findUUIDForObject(OpenSimObject obj) {
        return mapComponentToUUID.get(obj);
    }
    //============
    // PER FRAME
    //============
    public JSONObject createFrameMessageJson(boolean colorByState) {
        JSONObject msg = new JSONObject();
        Iterator<Integer> bodyIdIter = mapBodyIndicesToFrames.keySet().iterator();
        msg.put("Op", "Frame");
        JSONArray bodyTransforms_json = new JSONArray();
        msg.put("Transforms", bodyTransforms_json);

        if (ready) { // Avoid trying to send a frame before Json is completely populated
            while (bodyIdIter.hasNext()) {
                int bodyId = bodyIdIter.next();
                if (bodyId == 0) {
                    continue; // Skip over "Ground, as unnecessary
                }
                JSONObject oneBodyXform_json = new JSONObject();
                PhysicalFrame bodyFrame = mapBodyIndicesToFrames.get(bodyId);
                if (verbose) {
                    System.out.println("Getting transform of " + bodyFrame.getName());
                }
                Transform xform = bodyFrame.getTransformInGround(state);
                // Get uuid for first Mesh in body
                oneBodyXform_json.put("uuid", mapBodyIndicesToJson.get(bodyId).get("uuid"));
                oneBodyXform_json.put("matrix", JSONUtilities.createMatrixFromTransform(xform, new Vec3(1., 1., 1.), visScaleFactor));
                bodyTransforms_json.add(oneBodyXform_json);
            }
            // If we have special components
            // eg. ConditionalPathPoints, MovingPathPoints or WrapPoints, will handle here
            for (Component comp: movingComponents.keySet()){
                // Update position of MovingPathpoints on each frame
                MovingPathPoint mPathPoint = MovingPathPoint.safeDownCast(comp);
                if (mPathPoint!=null){
                    Transform localTransform = new Transform();
                    Vec3 location = mPathPoint.getLocation(state);
                    localTransform.setP(location);
                    JSONObject pathpointXform_json = new JSONObject();
                    pathpointXform_json.put("uuid", movingComponents.get(comp).toString());
                    pathpointXform_json.put("matrix", JSONUtilities.createMatrixFromTransform(localTransform, new Vec3(1., 1., 1.), visScaleFactor));
                    bodyTransforms_json.add(pathpointXform_json);
                    continue;
                }
            };
            for (AbstractPathPoint app: proxyPathPoints.keySet()){
                //System.out.println("Process Conditional Path point "+app.getName());
                  if (!app.isActive(state)){
                    ComputedPathPointInfo proxyPointInfo = proxyPathPoints.get(app);
                    //System.out.println("Use proxy "+proxyPoint.getName());
                    Vec3 loc = computePointLocationFromNeighbors(proxyPointInfo.pt1, app.getBody(), proxyPointInfo.pt2, proxyPointInfo.ratio);
                    Transform localTransform = new Transform();
                       localTransform.setP(loc);
                       JSONObject pathpointXform_json = new JSONObject();
                       pathpointXform_json.put("uuid", mapComponentToUUID.get(app).get(0).toString());
                       pathpointXform_json.put("matrix", JSONUtilities.createMatrixFromTransform(localTransform, new Vec3(1., 1., 1.), visScaleFactor));
                       bodyTransforms_json.add(pathpointXform_json);
                }
                else {
                    //System.out.println("Pathpoint " + app.getName() + " active");
                    Transform localTransform = new Transform();
                    Vec3 location = app.getLocation(state);
                    localTransform.setP(location);
                    JSONObject pathpointXform_json = new JSONObject();
                    pathpointXform_json.put("uuid", mapComponentToUUID.get(app).get(0).toString());
                    pathpointXform_json.put("matrix", JSONUtilities.createMatrixFromTransform(localTransform, new Vec3(1., 1., 1.), visScaleFactor));
                    bodyTransforms_json.add(pathpointXform_json);
                 } 
            }
            PhysicalFrame ground = mapBodyIndicesToFrames.get(0);
            for (UUID computedPointUUID: computedPathPoints.keySet()){
                ComputedPathPointInfo computedPointInfo = computedPathPoints.get(computedPointUUID);
                Vec3 loc = computePointLocationFromNeighbors(computedPointInfo.pt1, ground, computedPointInfo.pt2, computedPointInfo.ratio);
                Transform localTransform = new Transform();
                localTransform.setP(loc);
                JSONObject pathpointXform_json = new JSONObject();
                pathpointXform_json.put("uuid", computedPointUUID.toString());
                pathpointXform_json.put("matrix", JSONUtilities.createMatrixFromTransform(localTransform, new Vec3(1., 1., 1.), visScaleFactor));
                bodyTransforms_json.add(pathpointXform_json);
            }
            if (!pathsWithWrapping.isEmpty()){
                
                // Update status of Wrappoints accordingly
                for (GeometryPath path:pathsWithWrapping.keySet()){
                    updatePathWithWrapping(path, bodyTransforms_json);
                }
            }            // Computed points need recomputation
            
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
                if (Muscle.safeDownCast(geomPathObject.getOwner())!= null){
                    Vec3 pathColor = colorByState ? currentPathColorMap.getColor(geomPathObject, state, -1) : geomPathObject.getDefaultColor();
                
                    if (verbose)
                        System.out.println("Color:"+geomPathObject.getOwner().getName()+"="+pathColor.toString());
                    String colorString = JSONUtilities.mapColorToRGBA(pathColor);
                    pathUpdate_json.put("color", colorString);
                    geompaths_json.add(pathUpdate_json);
                }
            }
            // Have AddOns update their tranasforms
            for (VisualizerAddOn nextAddOn:visualizerAddOns){
                nextAddOn.updateVisuals(bodyTransforms_json);
            }
            // Process motion displayers
            for (MotionDisplayer nextMotionDisplayer: motionDisplayers){
                nextMotionDisplayer.addMotionObjectsToFrame(bodyTransforms_json, geompaths_json);
            }
        }
        return msg;
    }
        
     private void updatePathWithWrapping(GeometryPath path, JSONArray bodyTransforms) {
        ArrayPathPoint actualPath =path.getCurrentPath(state);
        JSONArray pathpointJsonArray = pathsWithWrapping.get(path);
        int numWrapObjects = path.getWrapSet().getSize();
        PathPointSet pathPointSetNoWrap = path.getPathPointSet();
        int firstIndex = 0; // by construction
        AbstractPathPoint firstPoint = pathPointSetNoWrap.get(firstIndex);
        int numIntermediatePoints = 2 * numWrapObjects;
        for (int ppointSetIndex=1; ppointSetIndex < pathPointSetNoWrap.getSize(); ppointSetIndex++) {
            // Consider the segment between pathPointSetNoWrap[ppointSetIndex], ppointSetIndex+1
            AbstractPathPoint secondPoint = pathPointSetNoWrap.get(ppointSetIndex);
            // Find points in actual path
             int secondIndex = actualPath.findIndex(secondPoint);
            //different scenarios depending on whether and where we find 
            if (secondIndex == firstIndex+1 || (firstIndex ==-1 && secondIndex!=-1)){
                // if intermediate points aren't computed, mark as such
             }
            else if (secondIndex == -1){ // Conditional Path point that's inactive
            }
            else {
                for (int wrappointIndex = firstIndex + 1; wrappointIndex < secondIndex; wrappointIndex++) {
                    AbstractPathPoint nextPathPoint = actualPath.get(wrappointIndex);
                    PathWrapPoint pathWrapPoint = PathWrapPoint.safeDownCast(nextPathPoint);
                    if (pathWrapPoint != null) {
                        // Count how many wrap points in sequence and distribute 2 * numWrapObjects among them 
                        // for now we'll assume only one
                        ArrayVec3 pathwrap = pathWrapPoint.getWrapPath();
                        PhysicalFrame wrapPtsFrame = pathWrapPoint.getParentFrame();
                        int size = pathwrap.size();
                        if (size >= 2) {
                            int x = 0;
                            int[] indicesToUse = new int[]{0, size - 1};
                            JSONObject bodyJson = mapBodyIndicesToJson.get(0); // These points live in Ground
                            ArrayList<UUID> wrapPointUUIDs = wrapPathPoints.get(pathWrapPoint);
                            if (wrapPointUUIDs==null){
                                // Wrapping was never encountered, treat as new
                                if (verbose) System.out.println("New Contact Encountered muscle pt "+firstPoint.getName());
                                wrapPointUUIDs = new ArrayList<UUID>();
                                // Find IDs and insert in wrapPathPoints map
                                for (int intermediatePpt=(1+numIntermediatePoints)*firstIndex+1; 
                                        intermediatePpt < (1+numIntermediatePoints)*firstIndex+3; 
                                        intermediatePpt++){
                                    wrapPointUUIDs.add(UUID.fromString((String) pathpointJsonArray.get(intermediatePpt)));
                                }
                                wrapPathPoints.put(pathWrapPoint, wrapPointUUIDs);
                            }
                            for (int j = 0; j < indicesToUse.length; j++) {
                                Vec3 globalLocation = wrapPtsFrame.findStationLocationInAnotherFrame(state, pathwrap.get(indicesToUse[j]), mapBodyIndicesToFrames.get(0));
                                // Update location from wrapping
                                JSONObject oneBodyXform_json = new JSONObject();
                                oneBodyXform_json.put("uuid", wrapPointUUIDs.get(j).toString());
                                Transform xform = new Transform();
                                xform.setP(globalLocation);
                                oneBodyXform_json.put("matrix", JSONUtilities.createMatrixFromTransform(xform, new Vec3(1., 1., 1.), visScaleFactor));
                                bodyTransforms.add(oneBodyXform_json);
                            }
                        }
                    }
                }
            }
            firstIndex = secondIndex;
            firstPoint = secondPoint;
        }
        
    }

    public JSONObject createSelectionJson(OpenSimObject obj) {
        JSONObject formJSON = new JSONObject();
        ArrayList<UUID> uuids = findUUIDForObject(obj);
        if (uuids != null && uuids.size()==1){
            UUID obj_uuid = uuids.get(0);
            formJSON.put("UUID", obj_uuid.toString());  
            formJSON.put("Op", "Select");  
         }
         else { // Check motionDisplayers
            for (MotionDisplayer motDisplayer:motionDisplayers){
                uuids = motDisplayer.findUUIDForObject(obj);
                if (uuids != null && uuids.size()==1){
                    UUID obj_uuid = uuids.get(0);
                    formJSON.put("UUID", obj_uuid.toString());  
                    formJSON.put("Op", "Select");  
                    break;
                }
            }
            
         }
         return formJSON;
    }

    public JSONObject createDeselectionJson() {
         JSONObject formJSON = new JSONObject();
         formJSON.put("Op", "Deselect");  
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
        guiJson.put("use_offset", movable);
        return guiJson;
    }

    private UUID createJsonForGeometryPath(GeometryPath path, ModelDisplayHints mdh, JSONArray json_geometries, 
                                            JSONArray json_materials, boolean visible) {
        // Create material for path
        Map<String, Object> mat_json = new LinkedHashMap<String, Object>();
        UUID mat_uuid = UUID.randomUUID();
        mapPathMaterialToUUID.put(path.getAbsolutePathString(), mat_uuid);
        mat_json.put("uuid", mat_uuid.toString());
        mat_json.put("name", path.getAbsolutePathString()+"Mat");
        mat_json.put("type", "MeshPhongMaterial");
        Vec3 pathColor = path.getDefaultColor();
        String colorString = JSONUtilities.mapColorToRGBA(pathColor);
        mat_json.put("color", colorString);
        mat_json.put("side", 2);
        mat_json.put("skinning", true);
        mat_json.put("transparent", true);
        json_materials.add(mat_json);

        // Create plain Geometry with vertices at PathPoints it will have 0 vertices
        // but will be populated live in the visualizer from the Pathppoints
        JSONObject pathGeomJson = new JSONObject();
        UUID uuidForPathGeomGeometry = UUID.randomUUID();
        pathGeomJson.put("uuid", uuidForPathGeomGeometry.toString());
        pathGeomJson.put("type", "PathGeometry");
        pathGeomJson.put("radius", 8*200*prefMuscleDisplayRadius);
        pathGeomJson.put("name", path.getAbsolutePathString()+"Control");
        // This includes inactive ConditionalPoints but no Wrapping
        int numWrapObjects = path.getWrapSet().getSize();
        final PathPointSet pathPointSetNoWrap = path.getPathPointSet();
        // Every segment will now contain 2 wrappoints per WrapObject that will stay inActive until needed
        //int numSegments = (pathPointSetNoWrap.getSize()-1)*(1+2*numWrapObjects); 
        json_geometries.add(pathGeomJson);
        JSONArray pathpoint_jsonArr = new JSONArray();
        JSONArray pathpointActive_jsonArr = new JSONArray();
        boolean hasWrapping = (numWrapObjects > 0);
        ArrayPathPoint actualPath = path.getCurrentPath(state);
        AbstractPathPoint firstPoint = pathPointSetNoWrap.get(0);
        // Create viz for firstPoint
        UUID pathpoint_uuid = addPathPointObjectToParent(firstPoint);
        pathpoint_jsonArr.add(pathpoint_uuid.toString());
        addComponentToUUIDMap(firstPoint, pathpoint_uuid);
        int firstIndex = 0; // by construction
        int numIntermediatePoints = 2 * numWrapObjects;
        for (int ppointSetIndex=1; ppointSetIndex < pathPointSetNoWrap.getSize(); ppointSetIndex++) {
            // Consider the segment between pathPointSetNoWrap[ppointSetIndex], ppointSetIndex+1
            AbstractPathPoint secondPoint = pathPointSetNoWrap.get(ppointSetIndex);
            // Find points in actual path
             int secondIndex = actualPath.findIndex(secondPoint);
             boolean pointAdded = false;
            //different scenarios depending on whether and where we find 
            if (secondIndex == firstIndex+1 || (firstIndex ==-1 && secondIndex!=-1)){
                pathpointActive_jsonArr.add(true);
                // Normal segment 
                // create numIntermediatePoints between firstPoint and secondPoint
                if (numWrapObjects > 0)
                    createComputedPathPoints(numIntermediatePoints, firstPoint, secondPoint, pathpoint_jsonArr);
            }
            else if (secondIndex == -1){ // Conditional Path point that's inactive
                ConditionalPathPoint cpp = ConditionalPathPoint.safeDownCast(secondPoint);
                //System.out.println("Not found in path, ppt:"+secondPoint.getName());
                pathpoint_uuid = addComputedPathPointObjectToParent(ppointSetIndex, pathPointSetNoWrap);
                pointAdded = true;
                ArrayList<UUID> comp_uuids = new ArrayList<UUID>();
                comp_uuids.add(pathpoint_uuid);
                mapComponentToUUID.put(cpp, comp_uuids);
                mapUUIDToComponent.put(pathpoint_uuid, cpp);                
                pathpointActive_jsonArr.add(false);
                pathpoint_jsonArr.add(pathpoint_uuid.toString());
                firstIndex = secondIndex;
                firstPoint = secondPoint;            
            }
            else {
                for (int wrappointIndex = firstIndex + 1; wrappointIndex < secondIndex; wrappointIndex++) {
                    AbstractPathPoint nextPathPoint = actualPath.get(wrappointIndex);
                    PathWrapPoint pathWrapPoint = PathWrapPoint.safeDownCast(nextPathPoint);
                    if (pathWrapPoint != null) {
                        // Count how many wrap points in sequence and distribute 2 * numWrapObjects among them 
                        // for now we'll assume only one
                        ArrayVec3 pathwrap = pathWrapPoint.getWrapPath();
                        PhysicalFrame wrapPtsFrame = pathWrapPoint.getParentFrame();
                        int size = pathwrap.size();
                        if (size >= 2) {
                            int x = 0;
                            int[] indicesToUse = new int[]{0, size - 1};
                            double step = 1.0/(indicesToUse.length+1.0);
                            JSONObject bodyJson = mapBodyIndicesToJson.get(0); // These points live in Ground
                            JSONArray children = (JSONArray) bodyJson.get("children");
                            ArrayList<UUID> wrapPointUUIDs = new ArrayList<UUID>();
                            for (int j = 0; j < indicesToUse.length; j++) {
                                Vec3 globalLocation = wrapPtsFrame.findStationLocationInAnotherFrame(state, pathwrap.get(indicesToUse[j]), mapBodyIndicesToFrames.get(0));
                                JSONObject bpptInBodyJson = createPathPointObjectJson(null, "", false, globalLocation);
                                UUID ppt_uuid = UUID.fromString((String) bpptInBodyJson.get("uuid"));
                                children.add(bpptInBodyJson);
                                pathpoint_jsonArr.add(ppt_uuid.toString());
                                wrapPointUUIDs.add(ppt_uuid);
                                computedPathPoints.put(ppt_uuid, new ComputedPathPointInfo(firstPoint, secondPoint, step*(j+1)));
                                // Also create a computed ppt for use when wrapping is inactive
                                
                            }
                            wrapPathPoints.put(pathWrapPoint, wrapPointUUIDs);
                        }
                    }
                }
            }
            // Create viz for secondPoint
            if (!pointAdded){
                pathpoint_uuid = addPathPointObjectToParent(secondPoint);
                pathpoint_jsonArr.add(pathpoint_uuid.toString());
                addComponentToUUIDMap(secondPoint, pathpoint_uuid);
            }
            if (MovingPathPoint.safeDownCast(secondPoint) != null) {
                movingComponents.put(secondPoint, pathpoint_uuid);
                //System.out.println("Process Moving Path point "+pathPoint.getName());
            }

            firstIndex = secondIndex;
            firstPoint = secondPoint;
        }
        if (hasWrapping)
            pathsWithWrapping.put(path, pathpoint_jsonArr);

        pathGeomJson.put("segments", pathpoint_jsonArr.size()-1); 
        JSONObject gndJson = mapBodyIndicesToJson.get(0);
        if (gndJson.get("children")==null)
                gndJson.put("children", new JSONArray());
        JSONArray gndChildren = (JSONArray) gndJson.get("children");
        Map<String, Object> obj_json = new LinkedHashMap<String, Object>();
        UUID mesh_uuid = UUID.randomUUID();
        obj_json.put("uuid", mesh_uuid.toString());
        obj_json.put("type", "GeometryPath");
        obj_json.put("name", path.getAbsolutePathString());
        obj_json.put("points", pathpoint_jsonArr);
        obj_json.put("active", pathpointActive_jsonArr);
        obj_json.put("geometry", uuidForPathGeomGeometry.toString());
        obj_json.put("opensimType", "Path");
        gndChildren.add(obj_json);
        // Create json entry for material (path_material) and set skinning to true
        obj_json.put("material", mat_uuid.toString());
        if (!visible){
            obj_json.put("visible", false);
        }
        return mesh_uuid;
    }

    private UUID createJsonForPathPoint(PathPoint ppt) {
        Map<String, Object> ppt_json = new LinkedHashMap<String, Object>();
        JSONArray locationArray = new JSONArray();
        UUID uuid_ppt = UUID.randomUUID();
        ppt_json.put("uuid", uuid_ppt.toString());
        ppt_json.put("name", ppt.getName());
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
         groundBone_json.put("name", model.getGround().getAbsolutePathString()+boneSuffix);
         groundBone_json.put("pos", posVec3);
         groundBone_json.put("rotq", unitQuaternion);
         UUID bone_uuid = UUID.randomUUID();
         bones_json.add(groundBone_json);
         while (!body.equals(bodies.end())) {
            Map<String, Object> obj_json = new LinkedHashMap<String, Object>();
            obj_json.put("name", body.getAbsolutePathString()+boneSuffix);
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

    private UUID addPathPointObjectToParent(AbstractPathPoint pathPoint) {
        
        // Parent
        PhysicalFrame bodyFrame = pathPoint.getBody();
        JSONObject bodyJson = mapBodyIndicesToJson.get(bodyFrame.getMobilizedBodyIndex());
        JSONArray children = (JSONArray)bodyJson.get("children");
        if (children==null){
                bodyJson.put("children", new JSONArray());
                children = (JSONArray)bodyJson.get("children");
        }
        JSONObject bpptInBodyJson = createPathPointObjectJson(pathPoint, pathPoint.getName(), true, null);
        children.add(bpptInBodyJson);
        return UUID.fromString((String)bpptInBodyJson.get("uuid"));
    }
    
    private UUID addComputedPathPointObjectToParent(int index, PathPointSet pathpointSet) {
        
        // Parent
        AbstractPathPoint pathPoint = pathpointSet.get(index);
        PhysicalFrame bodyFrame = pathPoint.getBody();
        JSONObject bodyJson = mapBodyIndicesToJson.get(bodyFrame.getMobilizedBodyIndex());
        JSONArray children = (JSONArray)bodyJson.get("children");
        if (children==null){
                bodyJson.put("children", new JSONArray());
                children = (JSONArray)bodyJson.get("children");
        }
        Vec3 computedLocation = computePathPointLocation(index, pathpointSet);
        JSONObject bpptInBodyJson = createPathPointObjectJson(pathPoint, "", false, computedLocation);
        children.add(bpptInBodyJson);
        return UUID.fromString((String)bpptInBodyJson.get("uuid"));
    }

    public JSONObject createPathPointObjectJson(AbstractPathPoint pathPoint, String name, boolean active, Vec3 computedLocation) {
        // Now add to scene graph
        String material = pathpointMatUUID.toString();
        JSONObject bpptGeometryJson = pathPointGeometryJSON;
        JSONObject bpptInBodyJson = new JSONObject();
        UUID ppoint_uuid = UUID.randomUUID();
        bpptInBodyJson.put("uuid", ppoint_uuid.toString());
        bpptInBodyJson.put("type", "Mesh");
        if (pathPoint!= null)
            bpptInBodyJson.put("opensimType", pathPoint.getConcreteClassName());
        else
            bpptInBodyJson.put("opensimType", "ComputedPathPoint");
        bpptInBodyJson.put("name", name);
        bpptInBodyJson.put("geometry", bpptGeometryJson.get("uuid"));
        bpptInBodyJson.put("material", material);
        bpptInBodyJson.put("status", active?"active":"inactive");
        Transform localTransform = new Transform();
        if (active){
            Vec3 location = pathPoint.getLocation(state);
            localTransform.setP(location);
        }
        else
            localTransform.setP(computedLocation);
        bpptInBodyJson.put("matrix", JSONUtilities.createMatrixFromTransform(localTransform, new Vec3(1.0), visScaleFactor));
        bpptInBodyJson.put("visible", false);
        return bpptInBodyJson;
    }

    protected JSONObject createPathPointGeometryJSON() {
        JSONObject bpptJson = new JSONObject();
        UUID uuidForPathpointGeometry = UUID.randomUUID();
        bpptJson.put("uuid", uuidForPathpointGeometry.toString());
        bpptJson.put("type", "BoxGeometry");
        bpptJson.put("width", 5);
        bpptJson.put("height", 5);
        bpptJson.put("depth", 5);
        json_geometries.add(bpptJson);
        return bpptJson;
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
        JSONObject guiJson = createSetVisibilityCommandForUUID(newValue, modelUUID);
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
        ArrayList<UUID> uuids = findUUIDForObject(obj);
        JSONObject guiJson = new JSONObject();
        if (uuids != null && uuids.size()==1){
            UUID objectUuid = uuids.get(0);
            guiJson = createSetVisibilityCommandForUUID(newValue, objectUuid);
        }
        return guiJson;
    }

    public JSONObject createSetVisibilityCommandForUUID(boolean newValue, UUID objectUuid) {
        JSONObject guiJson = new JSONObject();
        guiJson.put("Op", "execute");
        JSONObject commandJson = CommandComposerThreejs.createSetVisibleCommandJson(newValue, objectUuid);
        guiJson.put("command", commandJson);
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
    // This function checks that Geometry in the visualizar need change (other than appearance and scale)
    // and if so create a "ReplaceGeometry" message for the specific UUIDs
    public boolean createReplaceGeometryMessage(Geometry mc, JSONObject msg) {
        // Call geberate decorations on 
        ArrayDecorativeGeometry adg = new ArrayDecorativeGeometry();
        boolean saveVisible = mc.get_Appearance().get_visible();
        mc.get_Appearance().set_visible(true);
        mc.generateDecorations(true, mdh, state, adg);
        mc.get_Appearance().set_visible(saveVisible);
        ArrayList<UUID> uuids = findUUIDForObject(mc);
        if (adg.size() == uuids.size()){
            JSONArray geoms = new JSONArray();
            for (int i=0; i<adg.size(); i++){
                UUID uuid = uuids.get(i);
                dgimp.setGeomID(uuid);
                adg.getElt(i).implementGeometry(dgimp);
                JSONObject jsonObject = dgimp.getGeometryJson();
                msg.put("Op", "ReplaceGeometry");
                msg.put("uuid", uuid.toString());
                geoms.add(jsonObject);
                msg.put("geometries", geoms);
            }
        }
        return true;
    }
 
    public JSONObject createAddObjectCommand(JSONObject newObject, double[] bounds) {
        JSONObject guiJson = new JSONObject();
        guiJson.put("Op", "addModelObject");
        JSONObject commandJson = CommandComposerThreejs.createAddObjectCommandJson(newObject);
        /* No bounding box changes for now
        if (bounds != null){
            JSONArray bbox = new JSONArray();
            for (int i=0; i<6; i++)
                bbox.add(bounds[i]);
            commandJson.put("bbox", bbox);
        } */ 
        
        guiJson.put("command", commandJson);
        return guiJson;
    }

    // Export Path in Json format to visualizer, as of now 
    // only supports Stationary PathPoints
    // @typeOfEdit = 2 -> delete
    public JSONObject createPathUpdateJson(GeometryPath path, int typeOfEdit, int atIndex) {
        JSONObject topJson = new JSONObject();
        UUID pathUuid = pathList.get(path);
        topJson.put("Op", "PathOperation");
        topJson.put("uuid", pathUuid.toString());
        // send only pathpoint uuids
        if (typeOfEdit == 2) {
            JSONArray pathpoint_jsonArr = new JSONArray();
            for (int i = 0; i < path.getPathPointSet().getSize(); i++) {
                AbstractPathPoint pathPoint = path.getPathPointSet().get(i);
                ArrayList<UUID> vis_uuidList = mapComponentToUUID.get(pathPoint);
                if (vis_uuidList != null){ // If point is being deleted, it's removed from map first
                    UUID pathpoint_uuid = mapComponentToUUID.get(pathPoint).get(0);
                    pathpoint_jsonArr.add(pathpoint_uuid.toString());
                }
            }
            topJson.put("points", pathpoint_jsonArr);
            topJson.put("SubOperation", "delete");
        }
        else if (typeOfEdit == 1){
            topJson.put("SubOperation", "insert");
            AbstractPathPoint newPoint = path.getPathPointSet().get(atIndex);
            JSONObject newPointJson = createPathPointObjectJson(newPoint, newPoint.getName(), true, null);
            newPointJson.put("parent_uuid", mapComponentToUUID.get(newPoint.getBody()).get(0).toString());
            topJson.put("NewPoint", newPointJson);
            // Add new point to maps
            ArrayList<UUID> vis_uuidList = new ArrayList<UUID>(1);
            UUID newPointMeshUUID = UUID.fromString((String) newPointJson.get("uuid"));
            vis_uuidList.add(newPointMeshUUID);
            mapComponentToUUID.put(newPoint, vis_uuidList);
            mapUUIDToComponent.put(newPointMeshUUID, newPoint);
            JSONArray pathpoint_jsonArr = new JSONArray();
            for (int i = 0; i < path.getPathPointSet().getSize(); i++) {
                AbstractPathPoint pathPoint = path.getPathPointSet().get(i);
                UUID pathpoint_uuid = mapComponentToUUID.get(pathPoint).get(0);
                pathpoint_jsonArr.add(pathpoint_uuid.toString());
            }
            topJson.put("points", pathpoint_jsonArr);
        }
        else { // Refresh create multicommand to setpositions
            topJson.put("SubOperation", "refresh");
            // For each pathpoint compute transform
            JSONArray pathpoint_jsonArr = new JSONArray();
            for (int i = 0; i < path.getPathPointSet().getSize(); i++) {
                AbstractPathPoint pathPoint = path.getPathPointSet().get(i);
                JSONObject pathpointupdateJson = new JSONObject();
                UUID pathpoint_uuid = mapComponentToUUID.get(pathPoint).get(0);
                pathpointupdateJson.put("uuid", pathpoint_uuid.toString());
                Transform localTransform = new Transform();
                Vec3 location = null;
                if (true)
                    location = pathPoint.getLocation(state);
                else { // location is computed by getting the location of proxyPoint in bodyFrame
                    //Vec3 proxyLocationInParent = proxyPathPoint.getLocation(state);
                    //location = proxyPathPoint.getBody().findStationLocationInAnotherFrame(state, proxyLocationInParent, bodyFrame);
                }
                localTransform.setP(location);
                pathpointupdateJson.put("matrix", JSONUtilities.createMatrixFromTransform(localTransform, new Vec3(1.0), visScaleFactor));
                pathpoint_jsonArr.add(pathpointupdateJson);
            }
            topJson.put("points", pathpoint_jsonArr);
        }
       return topJson;
    }

    public JSONObject createSetPositionCommand(UUID pathpointUuid, Vec3 location) {
        JSONObject nextpptPositionCommand = new JSONObject();
        nextpptPositionCommand.put("type", "SetPositionCommand");
        nextpptPositionCommand.put("objectUuid", pathpointUuid.toString());
        JSONArray locationArray = new JSONArray();
        JSONArray oldLocationArray = new JSONArray();
        for (int p =0; p <3; p++){
            locationArray.add(location.get(p)*visScaleFactor);
            oldLocationArray.add(0);
        }
        nextpptPositionCommand.put("newPosition", locationArray);
        nextpptPositionCommand.put("oldPosition", oldLocationArray);
        return nextpptPositionCommand;
    }
    private UUID createPathPointMaterial() {
        Map<String, Object> mat_json = new LinkedHashMap<String, Object>();
        UUID mat_uuid = UUID.randomUUID();
        mat_json.put("uuid", mat_uuid.toString());
        mat_json.put("name", "PathPointMat");
        mat_json.put("type", "MeshBasicMaterial");
        String colorString = JSONUtilities.mapColorToRGBA(new Vec3(.8, .1, .1));
        mat_json.put("color", colorString);
        mat_json.put("side", 2);
        json_materials.add(mat_json);
        return mat_uuid;
    }
     private UUID createBoneStandardMaterialAndColor() {
        Map<String, Object> mat_json = new LinkedHashMap<String, Object>();
        UUID mat_uuid = UUID.randomUUID();
        mat_json.put("uuid", mat_uuid.toString());
        mat_json.put("name", "BoneStandardMat");
        mat_json.put("type", "MeshStandardMaterial");
        mat_json.put("metalness", 0);
        mat_json.put("roughness", 1.0);
        String colorString = JSONUtilities.mapColorToRGBA(new Vec3(0.949,0.863,0.757));
        mat_json.put("color", colorString);
        mat_json.put("side", 2);
        json_materials.add(mat_json);
        return mat_uuid;
    }

    private UUID createMarkerMaterial(ModelDisplayHints hints) {
        JSONObject mat_json = new JSONObject();
        UUID mat_uuid = UUID.randomUUID();
        mat_json.put("uuid", mat_uuid.toString());
        mat_json.put("name", "MarkerMat");
        mat_json.put("type", "MeshStandardMaterial");
        mat_json.put("transparent", true);
        mat_json.put("metalness", 0);
        mat_json.put("roughness", 1.0);
        mat_json.put("side", 2);
        //mat_json.put("transparent", true);
        String colorString = JSONUtilities.mapColorToRGBA(hints.get_marker_color());
        mat_json.put("color", colorString);
        mat_json.put("emissive", colorString);
        json_materials.add(mat_json);
        marker_mat_json = mat_json;
        return mat_uuid;
    }

    public void addMotionDisplayer(MotionDisplayer aMotionDisplayer) {
        motionDisplayers.add(aMotionDisplayer);
        if (aMotionDisplayer.hasMotionObjects()){
            aMotionDisplayer.createMotionObjectsGroupJson();
        }
    }

    private UUID createFrameObjectJSON(DecorativeGeometry dg, FrameGeometry frameObject) {
        Map<String, Object> frame_json = new LinkedHashMap<String, Object>();
        UUID uuidForFrameGeometry = UUID.randomUUID();
        frame_json.put("uuid", uuidForFrameGeometry.toString());
        frame_json.put("type", "Frame");
        frame_json.put("size", visScaleFactor);
        frame_json.put("visible", false);
        frame_json.put("name", frameObject.getAbsolutePathString());
        frame_json.put("matrix", JSONUtilities.createMatrixFromTransform(dg.getTransform(), frameObject.get_scale_factors(), visScaleFactor));
        // insert frame_json as child of BodyObject based on dg.getBodyId
        JSONObject bodyJson = mapBodyIndicesToJson.get(dg.getBodyId());
        if (bodyJson.get("children")==null)
            bodyJson.put("children", new JSONArray());
        ((JSONArray)bodyJson.get("children")).add(frame_json);
        return uuidForFrameGeometry;
    }
    /**
     * @return the modelGroundJson
     */
    public JSONObject getModelGroundJson() {
        return modelGroundJson;
    }

    public JSONObject createRemoveObjectCommand(JSONObject object2Remove, String parent) {
        JSONObject guiJson = new JSONObject();
        guiJson.put("Op", "execute");
        JSONObject commandJson = CommandComposerThreejs.createRemoveObjectCommandJson(object2Remove, parent);
        guiJson.put("command", commandJson);
        return guiJson;
    }

    public JSONObject createRemoveObjectCommand(OpenSimObject object2Remove, OpenSimObject parent) {
        JSONObject guiJson = new JSONObject();
        guiJson.put("Op", "execute");
        UUID objectUUID = mapComponentToUUID.get(object2Remove).get(0);
        UUID parentUUID = mapComponentToUUID.get(parent).get(0);
        JSONObject commandJson = CommandComposerThreejs.createRemoveObjectByUUIDCommandJson(objectUUID, parentUUID);
        guiJson.put("command", commandJson);
        return guiJson;
    }

    public JSONObject createTranslateObjectCommand(OpenSimObject marker, Vec3 newLocation) {
        JSONObject guiJson = new JSONObject();
        guiJson.put("Op", "execute");
        UUID markerUuid = mapComponentToUUID.get(marker).get(0);
        JSONObject commandJson = createSetPositionCommand(markerUuid, newLocation);
        guiJson.put("command", commandJson);
        return guiJson;
    }
    /**
     * @return the movable
     */
    public boolean isMovable() {
        return movable;
    }

    public JSONObject createMarkerJson(Marker marker) {
        ArrayDecorativeGeometry adg = new ArrayDecorativeGeometry();
        marker.generateDecorations(true, mdh, state, adg);
        String geomName = marker.getAbsolutePathString();
        DecorativeGeometry dg = adg.getElt(0);
        UUID markerGeomUUID=UUID.randomUUID();
        dgimp.setGeomID(markerGeomUUID);
        dg.implementGeometry(dgimp);
        JSONObject geomObject = (JSONObject) dgimp.getJsonArr().get(dgimp.getJsonArr().size()-1);
        geomName = geomName.concat(GEOMETRY_SEP+String.valueOf(dg.getIndexOnBody()));
        JSONObject bodyJson = mapBodyIndicesToJson.get(dg.getBodyId());
        if (bodyJson.get("children")==null)
           bodyJson.put("children", new JSONArray());
        //String geomName, Component opensimComponent, UUID uuid, UUID uuid_mat, DecorativeGeometry dg, JSONArray mobody_objects
        JSONObject retObject =  createJSONObjectFromDisplayGeometry(geomName, marker, markerGeomUUID, markerMatUUID, dg, (JSONArray) bodyJson.get("children"), true);
        // Establish mapping between Marker and uuid for selection/display purposes
        UUID objectUuid = UUID.fromString((String) retObject.get("uuid"));
        retObject.put("parent", bodyJson.get("uuid"));
        mapUUIDToComponent.put(objectUuid, marker);
        addComponentToUUIDMap(marker, objectUuid);
        JSONObject topObject = new JSONObject();
        topObject.put("object", retObject);
        JSONArray geoms = new JSONArray();
        geoms.add(geomObject);
        topObject.put("geometries", geoms);
        JSONArray materials = new JSONArray();
        materials.add(marker_mat_json);
        topObject.put("materials", materials);
        geoms.add(geomObject);
        
        return topObject;
    }

    private Vec3 computePathPointLocation(int index, PathPointSet pathpointsArray) {
        // find closest Active points before and after
        int numPre = 1;
        int numPost = 1;
        while (!pathpointsArray.get(index - numPre).isActive(state)) {
            numPre++;
        }
        while (!pathpointsArray.get(index + numPost).isActive(state)) {
            numPost++;
        }
        double ratio = 0.99;//((double) numPre) / (numPre + numPost);
        AbstractPathPoint curPoint = pathpointsArray.get(index);
        AbstractPathPoint prePoint = pathpointsArray.get(index - numPre);
        AbstractPathPoint postPoint = pathpointsArray.get(index + numPost);
        final PhysicalFrame parentFrame = curPoint.getParentFrame();
        proxyPathPoints.put(curPoint, new ComputedPathPointInfo(prePoint, postPoint, ratio));
        Vec3 retVec3 = computePointLocationFromNeighbors(prePoint, parentFrame, postPoint, ratio);
        return retVec3;
    }

    private Vec3 computePointLocationFromNeighbors(AbstractPathPoint prePoint, final PhysicalFrame parentFrame, AbstractPathPoint postPoint, double ratio) {
        Vec3 localLocation = prePoint.getLocation(state);
        Vec3 preLocation = prePoint.getBody().findStationLocationInAnotherFrame(state, localLocation, parentFrame);
        localLocation = postPoint.getLocation(state);
        Vec3 postLocation = postPoint.getBody().findStationLocationInAnotherFrame(state, localLocation, parentFrame);
        Vec3 retVec3 = new Vec3();
        for (int i =0; i < 3; i++){
            retVec3.set(i, (preLocation.get(i)*(1-ratio)+postLocation.get(i)*ratio));
        }
        return retVec3;
    }
    /* createComputedPathPoints that could turn into WrapPoints on the fly
     * the points are created by linear interpolation between lastPathPoint and currentPathPoint
     * Number of points (count) is passed in.
     * points is the collection of uuids of pathpoints so far
     * The function creates the points, add them to Ground frame and insert their uuids in the
     * points array following the uuid cooresponding to currentPathPoint
     */
    private void createComputedPathPoints(int count, AbstractPathPoint lastPathPoint, 
        AbstractPathPoint currentPathPoint, JSONArray points) {
        JSONObject bodyJson = mapBodyIndicesToJson.get(0); // These points live in Ground
        JSONArray children = (JSONArray) bodyJson.get("children");
        if (children == null) {
            bodyJson.put("children", new JSONArray());
            children = (JSONArray) bodyJson.get("children");
        }
        for (int i = 0; i < count; i++) {
            double ratio = (1.0 + i) / (count + 1.0);
            Vec3 location = computePointLocationFromNeighbors(lastPathPoint, mapBodyIndicesToFrames.get(0), currentPathPoint, ratio);
            JSONObject bpptInBodyJson =createPathPointObjectJson(null, "", false, location);
            UUID ppt_uuid = UUID.fromString((String) bpptInBodyJson.get("uuid"));
            computedPathPoints.put(ppt_uuid, new ComputedPathPointInfo(lastPathPoint, currentPathPoint, ratio));
            children.add(bpptInBodyJson);
            points.add(ppt_uuid.toString());
        }
    }

	// Utility command to set the Color of an object given its UUID
    public JSONObject createSetMaterialColorCommand(UUID objectUUID, Vec3 newColor) {
        JSONObject guiJson = new JSONObject();
        guiJson.put("Op", "execute");
        JSONObject commandJson = CommandComposerThreejs.createSetMaterialColorCommand(newColor, objectUUID);
        guiJson.put("command", commandJson);
        return guiJson;
    }
    
    public JSONObject createScaleObjectCommand(UUID objectUUID, double newScale) {
        JSONObject guiJson = new JSONObject();
        guiJson.put("Op", "execute");
        JSONObject commandJson = CommandComposerThreejs.createScaleObjectCommand(newScale, objectUUID);
        guiJson.put("command", commandJson);
        return guiJson;
    }
    public JSONObject createResizeGeometryCommand(UUID objectUUID, double newSize) {
        JSONObject guiJson = new JSONObject();
        guiJson.put("Op", "scaleGeometry");
        JSONObject commandJson = CommandComposerThreejs.createScaleObjectCommand(newSize, objectUUID);
        guiJson.put("command", commandJson);
        return guiJson;
    }
    public JSONObject updateComponentVisuals(Component comp, Boolean isFrame) {
        // Component has no visible representation, pass, shouldn't be here
        if (!componentHasVisuals(comp)) return null;
        // make sure attached to right Frame in SceneGraph            
        // Here we know comp has some visuals, if we have a change in Frame and
        // we can detect it, then we can move the Geometry to proper Frame
        // otherwise will have to do the more expensive remove and add to be safe
        JSONObject topMsg = new JSONObject();
        ArrayDecorativeGeometry adg = new ArrayDecorativeGeometry();
        comp.generateDecorations(true, mdh, state, adg);
        if (isFrame && adg.size() > 0) {
            topMsg.put("Op", "execute");
            JSONObject msgMulti = new JSONObject();
            topMsg.put("command",msgMulti);
            msgMulti.put("type", "MultiCmdsCommand");
            JSONArray commands = new JSONArray();
            ArrayList<UUID> uuids = mapComponentToUUID.get(comp);
            String geomId = comp.getAbsolutePathString();
            for (int i=0; i < uuids.size(); i++){
                DecorativeGeometry dg = adg.getElt(i);
                if (adg.size()>1)
                    geomId = geomId.concat(GEOMETRY_SEP+String.valueOf(dg.getIndexOnBody()));
                JSONObject bodyJson = mapBodyIndicesToJson.get(dg.getBodyId());
                String newParentUUIDString = (String) bodyJson.get("uuid");
                UUID parentUUID = UUID.fromString(newParentUUIDString);
                //UUID geometryUUID = mapDecorativeGeometryToUUID.get(geomId);
                JSONObject moveOneObject = CommandComposerThreejs.createMoveObjectByUUIDCommandJson(uuids.get(i), parentUUID);
                commands.add(moveOneObject);
                // May need to update location as well
                JSONObject translateOneObject = CommandComposerThreejs.createTranslateObjectCommandJson(dg.getTransform().T(), uuids.get(i));
                commands.add(translateOneObject);
            }
            msgMulti.put("cmds", commands);
            //System.out.println(msgMulti.toJSONString());
        }
        return topMsg;
    }
   
    public Boolean componentHasVisuals(Component comp){
        ArrayList<UUID> uuids = mapComponentToUUID.get(comp);
        // Component has no visible representation, pass
        return (uuids != null && uuids.size() > 0);

    }
    // Delete the selected Pathpoint from Maps used to back Visualization
    public void deletePathPointVisuals(GeometryPath currentPath, int index) {
        AbstractPathPoint appoint = currentPath.getPathPointSet().get(index);
        ArrayList<UUID> uuids = mapComponentToUUID.get(appoint);
        // Remove uuids[0] from visualizer
        // cleanup maps
        mapComponentToUUID.remove(appoint);
        mapUUIDToComponent.remove(uuids.get(0));
        // If Conditional remove it from 
        if (ConditionalPathPoint.safeDownCast(appoint)!= null)
            proxyPathPoints.remove(appoint);
        else if (MovingPathPoint.safeDownCast(appoint)!=null){
            movingComponents.remove(appoint);
        }
        // Need to update the GometryPath control polygon to remove the ppt and communicate the new one to visulizer
    }

    /* Utility to allow utility visualization classes to provide their corresponding
     * Geometry, Material and Obbjects to the Model Json structure.
    */
    public UUID addFrameJsonObject(PhysicalFrame bdy, 
            JSONObject geometryJson,
            JSONObject materialJson,
            JSONObject sceneGraphObjectJson){
        UUID obj_uuid = UUID.randomUUID();
        json_geometries.add(geometryJson);
        json_materials.add(materialJson);
        JSONObject bodyJson = mapBodyIndicesToJson.get(bdy.getMobilizedBodyIndex());
        if (bodyJson.get("children")==null)
            bodyJson.put("children", new JSONArray());
        ((JSONArray)bodyJson.get("children")).add(sceneGraphObjectJson);
        return obj_uuid;
    }

    /**
     * @return the transformWRTScene
     */
    public Transform getTransformWRTScene() {
        return transformWRTScene;
    }

    /**
     * @param transformWRTScene the transformWRTScene to set
     */
    public void setTransformWRTScene(Transform transformWRTScene) {
        this.transformWRTScene = transformWRTScene;
    }

}
