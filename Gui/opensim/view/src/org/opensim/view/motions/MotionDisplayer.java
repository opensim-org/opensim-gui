/* -------------------------------------------------------------------------- *
 * OpenSim: MotionDisplayer.java                                              *
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
 * MotionDisplayer.java
 *
 * Created on January 19, 2007, 9:06 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.view.motions;

import java.awt.Color;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.opensim.logger.OpenSimLogger;
import org.opensim.modeling.ArrayDouble;
import org.opensim.modeling.ArrayStr;
import org.opensim.modeling.Body;
import org.opensim.modeling.BodySet;
import org.opensim.modeling.Component;
import org.opensim.modeling.Coordinate;
import org.opensim.modeling.CoordinateSet;
import org.opensim.modeling.ForceSet;
import org.opensim.modeling.Ground;
import org.opensim.modeling.Marker;
import org.opensim.modeling.MarkerSet;
import org.opensim.modeling.Model;
import org.opensim.modeling.Muscle;
import org.opensim.modeling.MuscleIterator;
import org.opensim.modeling.MuscleList;
import org.opensim.modeling.OpenSimContext;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.PhysicalFrame;
import org.opensim.modeling.SimbodyEngine;
import org.opensim.modeling.StateVector;
import org.opensim.modeling.Storage;
import org.opensim.modeling.Transform;
import org.opensim.modeling.TransformAxis;
import org.opensim.modeling.UnitVec3;
import org.opensim.modeling.Vec3;
import org.opensim.threejs.JSONUtilities;
import org.opensim.threejs.ModelVisualizationJson;
import org.opensim.view.MuscleColoringFunction;
import org.opensim.view.OpenSimvtkGlyphCloud;
import org.opensim.view.SingleModelVisuals;
import org.opensim.view.experimentaldata.AnnotatedMotion;
import org.opensim.view.experimentaldata.ExperimentalDataItemType;
import org.opensim.view.experimentaldata.ExperimentalDataObject;
import org.opensim.view.experimentaldata.ExperimentalMarker;
import org.opensim.view.experimentaldata.ModelForExperimentalData;
import org.opensim.view.experimentaldata.MotionObjectBodyPoint;
import org.opensim.view.experimentaldata.MotionObjectPointForce;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;
import vtk.vtkActor;
import vtk.vtkAppendPolyData;
import vtk.vtkLineSource;
import vtk.vtkPolyDataMapper;

/**
 * 
 * 
 * 
 * @author Ayman. This class is used to preprocess motion files (Storage or similar) so that
 * 1. Mapping column indices to markersRep, gcs, ... is done only once.
 * 2. If additional objects need to be created for force or other markersRep they are maintained here.
 * 3. This isolates the display code from the specifics of Storage so that OpenSim proper creatures can be used.
 */

public class MotionDisplayer {

    private double[] defaultForceColor = new double[]{0., 1.0, 0.};
    private Vec3 defaultForceColorVec3 = new Vec3(0., 1.0, 0.);
    private MuscleColoringFunction mcf=null;
    // Create JSONs for geometry and material and use them for all objects of this type so that they all change together
    private JSONObject experimenalMarkerGeometryJson=null;
    private JSONObject experimenalMarkerMaterialJson=null;
    private Vec3 defaultExperimentalMarkerColor = new Vec3(0., 0., 1.);
    private ModelVisualizationJson modelVisJson=null;
    JSONObject motionObjectsRoot=null;
    private final HashMap<UUID, Component> mapUUIDToComponent = new HashMap<UUID, Component>();
    private final HashMap<OpenSimObject, ArrayList<UUID>> mapComponentToUUID = 
            new HashMap<OpenSimObject, ArrayList<UUID>>();

    public enum ObjectTypesInMotionFiles{GenCoord, 
                                         GenCoord_Velocity, 
                                         GenCoord_Force, 
                                         State,
                                         Marker, 
                                         Segment, 
                                         Segment_marker_p1, 
                                         Segment_marker_p2, 
                                         Segment_marker_p3, 
                                         Segment_force_p1, 
                                         Segment_force_p2, 
                                         Segment_force_p3, 
                                         Segment_force_p4, 
                                         Segment_force_p5, 
                                         Segment_force_p6, 
                                         Segment_torque_p1, 
                                         Segment_torque_p2, 
                                         Segment_torque_p3, 
                                         UNKNOWN};

    Hashtable<Integer, ObjectTypesInMotionFiles> mapIndicesToObjectTypes=new Hashtable<Integer, ObjectTypesInMotionFiles>(40);
    Hashtable<Integer, Object> mapIndicesToObjects=new Hashtable<Integer, Object>(40);
    private OpenSimvtkGlyphCloud  groundForcesRep = null;
    OpenSimvtkGlyphCloud  bodyForcesRep = null;
    OpenSimvtkGlyphCloud  generalizedForcesRep = null;
    private OpenSimvtkGlyphCloud  markersRep = null;
    private final Storage simmMotionData; // Storage used to provide data
    private Storage motionAsStates=null; // In case we're playing back states this is complete State on std. layout for internal use only
    private Model model;
    OpenSimContext dContext; 
    ArrayStr stateNames;
    private boolean renderMuscleActivations=false;
    private double experimentalMarkerScaleFactor;
    private double experimentalForceScaleFactor=1;
    String DEFAULT_FORCE_SHAPE="arrow";
    private String currentForceShape;
    
    // For columns that start with a body name, this is the map from column index to body reference.
    // The map is currently used only for body forces and generalized forces.
    private Hashtable<Integer, Body> mapIndicesToBodies = new Hashtable<Integer, Body>(10);
    // For generalized forces, this is the map from column index to DOF reference.
    private Hashtable<Integer, TransformAxis> mapIndicesToDofs = new Hashtable<Integer, TransformAxis>(10);
    
    protected Hashtable<ExperimentalDataObject, vtkActor> objectTrails = new Hashtable<ExperimentalDataObject, vtkActor>();

    private ArrayList<MotionDisplayer> associatedMotions = new  ArrayList<MotionDisplayer>();
    private ArrayStr colNames; // Will cache in labels and construct map to states for quick setting
    
    public class ObjectIndexPair {
       public Object object;
       public int stateVectorIndex; // Actual (0-based) index into state vector
       public ObjectIndexPair(Object obj, int idx) { this.object = obj; this.stateVectorIndex = idx; }
    }
    // For faster access to gencoords/markers/forces to update in applyFrameToModel
    ArrayList<ObjectIndexPair> genCoordColumns=null;
    ArrayList<ObjectIndexPair> genCoordForceColumns=null;
    ArrayList<ObjectIndexPair> segmentMarkerColumns=null; // state vector index of the first of three (x y z) coordinates for a marker
    ArrayList<ObjectIndexPair> segmentForceColumns=null; // state vector index of the first of six (px py pz vx vy vz) coordinates for a force vector
    ArrayList<ObjectIndexPair> anyStateColumns=null; // state vector index of muscle excitations and other generic states
    ArrayList<String> canonicalStateNames = new ArrayList<String>();
    ArrayDouble interpolatedStates = null;

    boolean statesFile = false; // special type of file that contains full state vectors
     /**
     * @return the associatedMotions
     */
    public ArrayList<MotionDisplayer> getAssociatedMotions() {
        return associatedMotions;
    }

    private void maskForceComponent(double[] vectorGlobal, String forceComponent) {
        if (forceComponent.equals("All")) return;
        if (forceComponent.equals("x")) {vectorGlobal[1]=0.0; vectorGlobal[2]=0.0; return;};
        if (forceComponent.equals("y")) {vectorGlobal[0]=0.0; vectorGlobal[2]=0.0; return;};
        if (forceComponent.equals("z")) {vectorGlobal[0]=0.0; vectorGlobal[1]=0.0; return;};
        
    }

    /**
     * @return the groundForcesRep
     */
    public OpenSimvtkGlyphCloud getGroundForcesRep() {
        return groundForcesRep;
    }

    /**
     * @return the markersRep
     */
    public OpenSimvtkGlyphCloud getMarkersRep() {
        return markersRep;
    }

    /**
     * @return the currentForceShape
     */
    public String getCurrentForceShape() {
        return currentForceShape;
    }

    /**
     * @param currentForceShape the currentForceShape to set
     */
    public void setCurrentForceShape(String currentForceShape) {
        this.currentForceShape = currentForceShape;
    }

    /**
     * @return the defaultForceColor
     */
    public Color getDefaultForceColor() {
        return new Color((float)defaultForceColor[0], (float)defaultForceColor[1], (float)defaultForceColor[2]);
    }


    
    /**
     * @param defaultForceColor the defaultForceColor to set
     */
    public void setDefaultForceColor(Color defaultForceColor) {
        float[] colorFloat = new float[3];
        defaultForceColor.getColorComponents(colorFloat);
        for (int i=0;i<3;i++) this.defaultForceColor[i] = (double) colorFloat[i];
        
        Vec3 colorAsVec3 = new Vec3();
        for (int i =0; i <3; i++) 
            colorAsVec3.set(i, this.defaultForceColor[i]);
        this.defaultExperimentalMarkerColor = colorAsVec3;
        Set<OpenSimObject> expermintalDataObjects = mapComponentToUUID.keySet();
        for (OpenSimObject expObj : expermintalDataObjects){
            // Find first ExperimentalMarker and change its Material, this will affect all of them
            if (expObj instanceof MotionObjectPointForce){
                UUID expObjectUUID = mapComponentToUUID.get(expObj).get(0); 
                String colorString = JSONUtilities.mapColorToRGBA(getDefaultExperimentalMarkerColor());
                ViewDB.getInstance().applyColorToObjectByUUID(model, expObjectUUID, colorAsVec3);  
            }
        }

        
    }

    public void setMuscleColoringFunction(MuscleColoringFunction mcbya) {
        mcf = mcbya;
        // Push it down to muscle displayers
        
    }

    public void addMotionObjectsToFrame(JSONArray transforms_json, JSONArray paths_json) {
       if (mcf!=null){
            // Cycle through the muscles in the model, get their color and either add
            // to json, or replace if already in paths_json
            MuscleList mList = model.getMuscleList();
            MuscleIterator mIter = mList.begin();
            while (!mIter.equals(mList.end())) {
                Muscle msl = mIter.__deref__();
                double newColorInBlueToRed = mcf.getColor(msl);
                Vec3 pathColor = new Vec3(newColorInBlueToRed, 0, 1-newColorInBlueToRed);
                ArrayList<UUID>  uuids = modelVisJson.findUUIDForObject(msl);
                if (uuids != null && uuids.size()==1){
                    UUID pathUUID = uuids.get(0);
                    JSONObject pathUpdate_json = new JSONObject();
                    pathUpdate_json.put("uuid", pathUUID.toString());
                    pathUpdate_json.put("color", JSONUtilities.mapColorToRGBA(pathColor));
                    paths_json.add(pathUpdate_json);   
                }
                mIter.next();
            }
        }
        if (!(simmMotionData instanceof AnnotatedMotion)) 
            return;
        AnnotatedMotion mot = (AnnotatedMotion)simmMotionData;
        Vector<ExperimentalDataObject> objects=mot.getClassified();        
        Vec3 unitScale = new Vec3(1., 1., 1.);
        for (ExperimentalDataObject nextObject : objects) {
            //if (!nextObject.isDisplayed()) 
            //    continue;
            JSONObject motionObjectTransform = new JSONObject();
            Transform xform = new Transform();
            if (nextObject instanceof ExperimentalMarker) {
                double[] point = ((ExperimentalMarker) nextObject).getPoint();
                xform.setP(new Vec3(point[0], point[1], point[2]));
                motionObjectTransform.put("uuid", nextObject.getDataObjectUUID().toString());
                motionObjectTransform.put("matrix",
                        JSONUtilities.createMatrixFromTransform(xform, unitScale, modelVisJson.getVisScaleFactor()));
                transforms_json.add(motionObjectTransform);
            } else if (nextObject instanceof MotionObjectPointForce) {
                double[] point = ((MotionObjectPointForce) nextObject).getPoint();
                Vec3 dir =  ((MotionObjectPointForce) nextObject).getDirection();
                double length = Math.sqrt(Math.pow(dir.get(0),2)+Math.pow(dir.get(1),2)+Math.pow(dir.get(2),2))/1000;
                UnitVec3 dirNorm = new UnitVec3(dir);
                xform.setP(new Vec3(point[0], point[1], point[2]));
                for (int i=0; i<3; i++)
                    xform.R().set(i, 1, dirNorm.get(i));
                motionObjectTransform.put("uuid", nextObject.getDataObjectUUID().toString());
                motionObjectTransform.put("matrix",
                        JSONUtilities.createMatrixFromTransform(xform, new Vec3(1, length ,1), modelVisJson.getVisScaleFactor()));
                transforms_json.add(motionObjectTransform);
            }
        }
     }

    public boolean hasMotionObjects() {
        return (simmMotionData instanceof AnnotatedMotion);
    }

    public void createMotionObjectsGroupJson() {
        // Create aan objects under gnd/children to serve as top node for motion objects
        // all will be in ground frame.
        if (motionObjectsRoot==null) {
            motionObjectsRoot = new JSONObject();
            motionObjectsRoot.put("parent", modelVisJson.getModelUUID().toString());
            motionObjectsRoot.put("uuid", UUID.randomUUID().toString());
            motionObjectsRoot.put("type", "Group");
            motionObjectsRoot.put("opensimType", "MotionObjects");
            motionObjectsRoot.put("name", simmMotionData.getName().concat("_Objects"));
            motionObjectsRoot.put("userData", "NonEditable");
            motionObjectsRoot.put("children", new JSONArray());
            motionObjectsRoot.put("matrix", JSONUtilities.createMatrixFromTransform(new Transform(), new Vec3(1.), 1.0));

        }
        
    }

    private JSONObject createJsonForMotionObjects() {
        JSONObject topJson = new JSONObject();
        JSONArray jsonGeomArray = new JSONArray();
        jsonGeomArray.add(getExperimenalMarkerGeometryJson());
        JSONArray jsonMatArray = new JSONArray();
        jsonMatArray.add(getExperimenalMarkerMaterialJson());
        topJson.put("geometries", jsonGeomArray);
        topJson.put("materials", jsonMatArray);
        topJson.put("object", motionObjectsRoot);
        return topJson;
    }

    /**
     * @return the experimenalMarkerGeometryJson
     */
    public JSONObject getExperimenalMarkerGeometryJson() {
        return experimenalMarkerGeometryJson;
    }

    /**
     * @return the experimenalMarkerMaterialJson
     */
    public JSONObject getExperimenalMarkerMaterialJson() {
        return experimenalMarkerMaterialJson;
    }

    /**
     * @return the defaultForceColorVec3
     */
    public Vec3 getDefaultForceColorVec3() {
        return defaultForceColorVec3;
    }
    
    
    // A local copy of motionObjects so that different motions have different motion objects
    //Hashtable<String, vtkActor> motionObjectInstances =new Hashtable<String, vtkActor>(10);
    
    /** Creates a new instance of MotionDisplayer */
    public MotionDisplayer(Storage motionData, Model model) {
        this.experimentalForceScaleFactor = 1.0;
        this.experimentalMarkerScaleFactor = 1.0;
        this.model = model;
        dContext= OpenSimDB.getInstance().getContext(model);
        simmMotionData = motionData;
        currentForceShape = DEFAULT_FORCE_SHAPE;
        modelVisJson = ViewDB.getInstance().getModelVisualizationJson(model);
        setupMotionDisplay();
        // create a buffer to be used for comuptation of constrained states
        //statesBuffer = new double[model.getNumStateVariables()];
        
        modelVisJson.addMotionDisplayer(this);
        if (model instanceof ModelForExperimentalData) return;

    }
    public void setupMotionDisplay() { 
        if (simmMotionData == null)
           return;

        // We create a temporary array to hold interpolated values, in case Slider doesn't coincide with data
        colNames = simmMotionData.getColumnLabels();
        int numColumnsIncludingTime = colNames.getSize();
        interpolatedStates = new ArrayDouble(0.0, numColumnsIncludingTime-1);
        // If provided simmMotionData is empty or have one frame, then we're building it live and can't
        // convert to full State form, will keep using old mapping until Tool run is finished
        // Performance will be limited by the running computation anyway.
        boolean liveMotion = simmMotionData.getSize()<=1;
        //AddMotionObjectsRep(model);
        if (simmMotionData instanceof AnnotatedMotion){
            // Add place hoders for markers
            AnnotatedMotion mot= (AnnotatedMotion) simmMotionData;
            Vector<ExperimentalDataObject> objects=mot.getClassified();
            mot.setMotionDisplayer(this);
            createMotionObjectsGroupJson();
            addExperimentalDataObjectsToJson(objects);
            for(ExperimentalDataObject nextObject:objects){
                if (nextObject.getObjectType()==ExperimentalDataItemType.MarkerData){
                    bindMarkerToVisualizerObjectKeepHandle(nextObject);
                } else if (nextObject.getObjectType()==ExperimentalDataItemType.PointForceData){
                    bindForceVisualizerObjectKeepHandle(nextObject);
                } 
                
            }
            // create objects and cache their uuids
            //createTrails(model);
            ViewDB.getInstance().addVisualizerObject(createJsonForMotionObjects(), mot.getBoundingBox());
            JSONObject modelObjectJson = (JSONObject) modelVisJson.get("object");
            if (modelObjectJson.get("children") == null) {
                modelObjectJson.put("children", new JSONArray());
            }
            ((JSONArray)modelObjectJson.get("children")).add(motionObjectsRoot);
            return;
        }
        mapIndicesToBodies.clear();
        mapIndicesToDofs.clear();

        stateNames = model.getStateVariableNames();
        
        stateNames.insert(0, "time");
        if(colNames.arrayEquals(stateNames)) {
           // This is a states file
           statesFile = true;
           setRenderMuscleActivations(true);
        } 
        else if (!liveMotion){ // This is the most common handling of old Result files
            // create a local Storage and use that to drive animation
            motionAsStates = new Storage();
            model.formStateStorage(simmMotionData, motionAsStates, false);
            statesFile = true;
            // Fix size of temporary array to hold interpolated values
            numColumnsIncludingTime = motionAsStates.getColumnLabels().getSize();
            interpolatedStates = new ArrayDouble(0.0, numColumnsIncludingTime-1);
            setRenderMuscleActivations(true);
        }
        else {
           // We should build sorted lists of object names so that we can find them easily
           for(int i=0; i < numColumnsIncludingTime; i++){
              String columnName = colNames.getitem(i);   // Time is included in labels
              int numClassified = classifyColumn(model, i, columnName); // find out if column is gencord/muscle/segment/...etc.
              ObjectTypesInMotionFiles cType = mapIndicesToObjectTypes.get(i);
              //System.out.println("Classified "+columnName+" as "+cType);
              if (numClassified>1)  // If we did a group then skip the group
                 i += (numClassified-1);
           }

           genCoordColumns = new ArrayList<ObjectIndexPair>(numColumnsIncludingTime);
           genCoordForceColumns = new ArrayList<ObjectIndexPair>(numColumnsIncludingTime);
           segmentMarkerColumns = new ArrayList<ObjectIndexPair>(numColumnsIncludingTime);
           segmentForceColumns = new ArrayList<ObjectIndexPair>(numColumnsIncludingTime);
           anyStateColumns = new ArrayList<ObjectIndexPair>(numColumnsIncludingTime);
           for (int i = 1; i< numColumnsIncludingTime; i++){
               ObjectTypesInMotionFiles cType = mapIndicesToObjectTypes.get(i);
               Object o=mapIndicesToObjects.get(i);
               if (cType==null)
                  continue;
               ObjectIndexPair newPair = new ObjectIndexPair(o,i-1);    // -1 to account for time
               switch(cType){
                  case GenCoord: 
                     genCoordColumns.add(newPair);
                     break;
                  case GenCoord_Force: 
                     genCoordForceColumns.add(newPair);
                     break;
                  case State: 
                     anyStateColumns.add(newPair);
                     break;
                  case Segment_marker_p1:
                     segmentMarkerColumns.add(newPair);
                     break;
                  case Segment_force_p1:
                     segmentForceColumns.add(newPair);
                     break;
               }
            }
        }
    }

    private void bindForceVisualizerObjectKeepHandle(ExperimentalDataObject nextObject) {
        if (ViewDB.isVtkGraphicsAvailable()){
            int glyphIndex=groundForcesRep.addLocation(nextObject);
            nextObject.setGlyphInfo(glyphIndex, groundForcesRep);
        }
        nextObject.setDataObjectUUID(findUUIDForObject(nextObject).get(0));
    }

    private void bindMarkerToVisualizerObjectKeepHandle(ExperimentalDataObject nextObject) {
        if (ViewDB.isVtkGraphicsAvailable()){
            int glyphIndex=markersRep.addLocation(nextObject);
            nextObject.setGlyphInfo(glyphIndex, markersRep);
        }
        nextObject.setDataObjectUUID(findUUIDForObject(nextObject).get(0));
    }

    //interface applyValue
    //{
    //   public void apply(double val);
    //}
    /*
     * Check what kind of data is at columnIndex, and add object of relevance
     * to the map "mapIndicesToObjects" for quick access during animation
     *
     * returns the number of columns that has been classified since _px may lead to _py, _pz
     * so we don't want to start name searching from scratch.
     * A side effect is the creation of motion object instances and adding them to the model
     **/
   private int classifyColumn(Model model, int columnIndex, String columnName) 
   {
      ObjectTypesInMotionFiles retType = ObjectTypesInMotionFiles.UNKNOWN;
      if (model instanceof ModelForExperimentalData) {
          return 0;
      }
      int newIndex = simmMotionData.getStateIndex(columnName);
      if (newIndex ==-1)
          return 0;
      String canonicalCcolumnName = columnName.replace('.', '/');
      CoordinateSet coords = model.getCoordinateSet();
      for (int i = 0; i<coords.getSize(); i++){
         Coordinate co = coords.get(i);
         // GenCoord
         String cName = co.getName();
         if (cName.equals(columnName)||cName.equals(co.getRelativePathName(model))){
            mapIndicesToObjectTypes.put(columnIndex, ObjectTypesInMotionFiles.GenCoord);
            mapIndicesToObjects.put(columnIndex, co); //co.setValue();
            return 1;
         }
         // GenCoord_Velocity
         if (columnName.endsWith("_vel")|| columnName.endsWith("_u")){ //_u
            if (columnName.equals(cName+"_vel")|| columnName.equals(cName+"_u")) //_u
               mapIndicesToObjectTypes.put(columnIndex, ObjectTypesInMotionFiles.GenCoord_Velocity);
               mapIndicesToObjects.put(columnIndex, co); 
               return 1;
         }         
         // GenCoord_Force
         if (columnName.endsWith("_torque") || columnName.endsWith("_force")){
            if (columnName.equals(cName+"_torque") || columnName.equals(cName+"_force")) {
               mapIndicesToObjectTypes.put(columnIndex, ObjectTypesInMotionFiles.GenCoord_Force);
               //mapIndicesToObjects.put(columnIndex, co);
               //Joint joint = null;
               //TransformAxis unconstrainedDof = model.getSimbodyEngine().findUnconstrainedDof(co, joint);
               //Body body = unconstrainedDof.getJoint().getBody();
               //mapIndicesToBodies.put(columnIndex, body);
               //mapIndicesToDofs.put(columnIndex, unconstrainedDof);
               //int index = generalizedForcesRep.addLocation(0., 0., 0.);
               //mapIndicesToObjects.put(columnIndex, new Integer(index));
               return 1;
            }
         }         
      }
      // Allow "/" instead of in addition to "."
      // Add method to convert column labels to state names so it's centralized
      if (columnName.contains("excitation") || columnName.contains("activation")){
          setRenderMuscleActivations(true);
      }
      ForceSet acts = model.getForceSet();
      for (int i=0; i< acts.getSize(); i++)
          if (columnName.startsWith(acts.get(i).getName())){    // Make sure it's a muscle state'
          // Any other state
          int stateIndex=stateNames.findIndex(canonicalCcolumnName);  // includes time so 0 is time
          if (stateIndex>0){
              int stateIndexMinusTime = stateIndex-1;
              mapIndicesToObjectTypes.put(columnIndex, ObjectTypesInMotionFiles.State);
              mapIndicesToObjects.put(columnIndex, new Integer(stateIndexMinusTime));  
              canonicalStateNames.add(canonicalCcolumnName);
              return 1;
          }
     }

       MarkerSet markers = model.getMarkerSet();
       for (int i = 0; i<markers.getSize(); i++){
         Marker marker = markers.get(i);
         // 
         String cName = marker.getName();
         if (columnName.startsWith(cName+"_")){
            mapIndicesToObjectTypes.put(columnIndex, ObjectTypesInMotionFiles.Segment_marker_p1);
            mapIndicesToObjectTypes.put(columnIndex+1, ObjectTypesInMotionFiles.Segment_marker_p2);
            mapIndicesToObjectTypes.put(columnIndex+2, ObjectTypesInMotionFiles.Segment_marker_p3);
            /* we don't handle mix of markers and coordinates in one file
            int index= markersRep.addLocation(0., 0., 0.);
            mapIndicesToObjects.put(columnIndex, new Integer(index));
            mapIndicesToObjects.put(columnIndex+1, new Integer(index));
            mapIndicesToObjects.put(columnIndex+2, new Integer(index)); */
            return 3;
         }
      }
     // Body segment since experimental markersRep are in ground frame as ground_marker_??
       BodySet bodySet = model.getBodySet();
       String[] motionObjectNames=MotionObjectsDB.getInstance().getAvailableNames();
       for (int i = 0; i<bodySet.getSize(); i++){
         Body bdy = bodySet.get(i);
         // 
         String bName = bdy.getName();
         if (columnName.startsWith(bName+"_")){
            if (columnName.startsWith(bName+"_marker_")){
               if (columnName.equals(bName+"_marker_px")){
                  mapIndicesToObjectTypes.put(columnIndex,   ObjectTypesInMotionFiles.Segment_marker_p1);
                  mapIndicesToObjectTypes.put(columnIndex+1, ObjectTypesInMotionFiles.Segment_marker_p2);
                  mapIndicesToObjectTypes.put(columnIndex+2, ObjectTypesInMotionFiles.Segment_marker_p3);
                  int index= markersRep.addLocation(0., 0., 0.);
                  mapIndicesToObjects.put(columnIndex, new Integer(index));
                  mapIndicesToObjects.put(columnIndex+1, new Integer(index));
                  mapIndicesToObjects.put(columnIndex+2, new Integer(index));
                  return 3;
               }
               else {
                  mapIndicesToObjectTypes.put(columnIndex, ObjectTypesInMotionFiles.UNKNOWN);
                  return 0;  // Something else
               }
               
            }
            int numColumnsIncludingTime = simmMotionData.getColumnLabels().getSize();
            if (columnName.startsWith(bName) && columnName.contains("force")){
               if (columnName.startsWith(bName) && columnName.endsWith("_vx")){
                  // Make sure we're not going outside # columns due to assumption about column ordering'
                  if ((columnIndex+5) > numColumnsIncludingTime){
                      OpenSimLogger.logMessage("Unexpected column headers for forces at column  "+columnIndex+" will be ignored", OpenSimLogger.INFO);
                      continue;
                  }
                  mapIndicesToObjectTypes.put(columnIndex, ObjectTypesInMotionFiles.Segment_force_p1);
                  mapIndicesToObjectTypes.put(columnIndex+1, ObjectTypesInMotionFiles.Segment_force_p2);
                  mapIndicesToObjectTypes.put(columnIndex+2, ObjectTypesInMotionFiles.Segment_force_p3);
                  mapIndicesToObjectTypes.put(columnIndex+3, ObjectTypesInMotionFiles.Segment_force_p4);
                  mapIndicesToObjectTypes.put(columnIndex+4, ObjectTypesInMotionFiles.Segment_force_p5);
                  mapIndicesToObjectTypes.put(columnIndex+5, ObjectTypesInMotionFiles.Segment_force_p6);
                  int index = 0;
                  if (bName.equalsIgnoreCase("ground"))
                     index = groundForcesRep.addLocation(0., 0., 0.);
                  else
                     index = bodyForcesRep.addLocation(0., 0., 0.);
                  mapIndicesToObjects.put(columnIndex, new Integer(index));
                  mapIndicesToObjects.put(columnIndex+1, new Integer(index));
                  mapIndicesToObjects.put(columnIndex+2, new Integer(index));
                  mapIndicesToObjects.put(columnIndex+3, new Integer(index));
                  mapIndicesToObjects.put(columnIndex+4, new Integer(index));
                  mapIndicesToObjects.put(columnIndex+5, new Integer(index));
                  mapIndicesToBodies.put(columnIndex, bdy);
                  return 6;
               }
               else{
                  mapIndicesToObjectTypes.put(columnIndex, ObjectTypesInMotionFiles.UNKNOWN);
                  return 0;  // Something else, maybe a velocity component
               }
            }
            mapIndicesToObjectTypes.put(columnIndex, ObjectTypesInMotionFiles.Segment);
            mapIndicesToObjects.put(columnIndex, bdy);
            return 1;
         }
      }
      mapIndicesToObjectTypes.put(columnIndex, ObjectTypesInMotionFiles.UNKNOWN);
      return 0;
   }

   void applyFrameToModel(int currentFrame) 
   {
      boolean profile=(OpenSimObject.getDebugLevel()>=2);
      long before = 0, after=0;
      if (profile)
          before =System.nanoTime();
      StateVector states;
      if (motionAsStates!=null){
          states = motionAsStates.getStateVector(currentFrame);
      }
      else
          states=simmMotionData.getStateVector(currentFrame);
      
      applyStatesToModel(states.getData(), states.getTime());
      ViewDB.getInstance().updateAnnotationAnchors();
      if (profile) {
          after=System.nanoTime();
          System.out.println("applyFrameToModel time: "+1e-6*(after-before)+" ms");
      }
   }

   public void applyTimeToModel(double currentTime)
   {
      //TODO: Option to snap to nearest valid storage time rather than interpolate...
      //
      // Better clamp the time otherwise we'll get linear extrapolation outside the valid time range (we might get out-of-range times
      // if we're playing this motion synced with another motion)
      double clampedTime = (currentTime < simmMotionData.getFirstTime()) ? simmMotionData.getFirstTime() : 
                           (currentTime > simmMotionData.getLastTime()) ? simmMotionData.getLastTime() : currentTime;
      if (motionAsStates ==null || simmMotionData instanceof AnnotatedMotion)
            simmMotionData.getDataAtTime(clampedTime, interpolatedStates.getSize(), interpolatedStates);
      else
            motionAsStates.getDataAtTime(clampedTime, interpolatedStates.getSize(), interpolatedStates);
         
      applyStatesToModel(interpolatedStates, clampedTime);
      // Repeat for associated motions
      for (MotionDisplayer assocMotion:associatedMotions){
          assocMotion.applyTimeToModel(currentTime);
      }
   }

   private void applyStatesToModel(ArrayDouble states, double assocTime) 
   {
     boolean profile=false;//(OpenSimObject.getDebugLevel()>=2);
     long before = 0, after=0;
     if (profile)
          before =System.nanoTime();
     if (simmMotionData instanceof AnnotatedMotion){ // Experimental Data
          int dataSize = states.getSize();
          AnnotatedMotion mot = (AnnotatedMotion)simmMotionData;
          Vector<ExperimentalDataObject> objects=mot.getClassified();
          boolean markersModified=false;
          boolean forcesModified=false;
          mot.updateDecorations(interpolatedStates);
          if (ViewDB.isVtkGraphicsAvailable()){
            SingleModelVisuals vis = ViewDB.getInstance().getModelVisuals(model);
             for(ExperimentalDataObject nextObject:objects){
                  if (!nextObject.isDisplayed()) continue;
                  vis.upateDisplay(nextObject);
                  // The following blocks need to be moved inside updateDecorations
                  if (nextObject.getObjectType()==ExperimentalDataItemType.MarkerData){

                      int startIndex = nextObject.getStartIndexInFileNotIncludingTime();
                      /*
                      markersRep.setLocation(nextObject.getGlyphIndex(), 
                              states.getitem(startIndex)/mot.getUnitConversion(), 
                              states.getitem(startIndex+1)/mot.getUnitConversion(), 
                              states.getitem(startIndex+2)/mot.getUnitConversion());
                      markersModified = true;*/

                  }
                  else if (nextObject.getObjectType()==ExperimentalDataItemType.PointForceData){
                      String pointId = ((MotionObjectPointForce)nextObject).getPointIdentifier();
                      String forceId = ((MotionObjectPointForce)nextObject).getForceIdentifier();
                      String bodyId = ((MotionObjectPointForce)nextObject).getPointExpressedInBody();  
                      Body b = model.getBodySet().get(bodyId);
                      int startPointIndex = simmMotionData.getColumnIndicesForIdentifier(pointId).getitem(0)-1;
                      double[] locationLocal = new double[]{states.getitem(startPointIndex), 
                              states.getitem(startPointIndex+1), 
                              states.getitem(startPointIndex+2)};
                      double[] locationGlobal = new double[3]; 
                      // Transform to ground from body frame
                      dContext.transformPosition(b, locationLocal, locationGlobal);
                      groundForcesRep.setLocation(nextObject.getGlyphIndex(), 
                              locationGlobal[0], locationGlobal[1], locationGlobal[2]);
                      int startForceIndex = simmMotionData.getColumnIndicesForIdentifier(forceId).getitem(0)-1;
                      double[] forceLocal = new double[]{states.getitem(startForceIndex), 
                              states.getitem(startForceIndex+1), 
                              states.getitem(startForceIndex+2)};
                      maskForceComponent(forceLocal, ((MotionObjectPointForce)nextObject).getForceComponent());
                      double[] forceGlobal = new double[3]; 
                      dContext.transform(b, forceLocal, model.get_ground(), forceGlobal);
                      groundForcesRep.setNormalAtLocation(nextObject.getGlyphIndex(), 
                              forceGlobal[0], 
                              forceGlobal[1], 
                              forceGlobal[2]);
                      forcesModified=true;
                } else if (nextObject.getObjectType()==ExperimentalDataItemType.BodyForceData){
                      int startIndex = nextObject.getStartIndexInFileNotIncludingTime();
                      MotionObjectBodyPoint bodyPointObject = (MotionObjectBodyPoint)nextObject;
                      double[] bodyPoint =bodyPointObject.getPoint();
                      PhysicalFrame b = model.getBodySet().get(bodyPointObject.getPointExpressedInBody());
                      double[] bodyPointGlobal = new double[3]; 
                      // Transform to ground from body frame
                      dContext.transformPosition(b, bodyPoint, bodyPointGlobal);
                      groundForcesRep.setLocation(nextObject.getGlyphIndex(), 
                              bodyPointGlobal[0], bodyPointGlobal[1], bodyPointGlobal[2]);
                      double[] vectorGlobal = new double[]{states.getitem(startIndex), 
                              states.getitem(startIndex+1), 
                              states.getitem(startIndex+2)}; 

                      if (b==model.get_ground())
                           maskForceComponent(vectorGlobal, ((MotionObjectPointForce)nextObject).getForceComponent());
                      else{
                          double[] vectorLocal = new double[]{
                                  states.getitem(startIndex), 
                                  states.getitem(startIndex+1), 
                                  states.getitem(startIndex+2)
                          };
                          maskForceComponent(vectorLocal, ((MotionObjectPointForce)nextObject).getForceComponent());
                          // Transform to ground from body frame
                          dContext.transform(b, vectorLocal, model.get_ground(), vectorGlobal);
                      }

                      groundForcesRep.setNormalAtLocation(nextObject.getGlyphIndex(), 
                              vectorGlobal[0], vectorGlobal[1], vectorGlobal[2]);
                      forcesModified=true;
                }

                if (forcesModified) groundForcesRep.setModified();
                if (markersModified) markersRep.setModified();
            }
          }
          // Create one frame and send to Visualizer this would have:
          // updated positions for markers, 
         // updated transforms for forces         
          //groundForcesRep.hide(0);
          return;
      }
     // Here handling a motion file with potentially extra columns for Forces, Markers
      OpenSimContext context = OpenSimDB.getInstance().getContext(model);
      
      if(statesFile || motionAsStates!=null) {
          // FIX40 speed this up by using map or YIndex
          context.getCurrentStateRef().setTime(assocTime);
          model.setStateVariableValues(context.getCurrentStateRef(), states.getAsVector());
          // Assemble to satify constraints. Fixes issue #617
          model.assemble(context.getCurrentStateRef());
          context.realizeVelocity();
      } else {
         // Any other states including muscles
         for(int i=0; i<anyStateColumns.size(); i++) {
              int index = anyStateColumns.get(i).stateVectorIndex;
              double newValue=states.getitem(index);
              // Set value in statesBuffer
              //Object o=mapIndicesToObjects.get(index+1);
              //int bufferIndex = ((Integer)o).intValue();
              model.setStateVariableValue(context.getCurrentStateRef(), canonicalStateNames.get(i), newValue);
              //statesBuffer[bufferIndex]=newValue;
         }
      }
      if (profile) {
          after=System.nanoTime();
          OpenSimLogger.logMessage("applyFrameToModel time: "+1e-6*(after-before)+" ms.\n", OpenSimLogger.INFO);
      }
      model.realizeDynamics(context.getCurrentStateRef());
    }

    /*
     * cleanupDisplay is called when the motion is mode non-current either explicitly by the user or by selecting
     * another motion for the same model and making it current */
    void cleanupDisplay() {
        if (motionObjectsRoot!=null) {
            ViewDB.getInstance().removeVisualizerObject(motionObjectsRoot, 
                    modelVisJson.getModelUUID().toString());
        }
        mcf = null;
        // Recursively cleanup
        ArrayList<MotionDisplayer> associatedDisplayers = getAssociatedMotions();
        for (MotionDisplayer assoc:associatedDisplayers){
            assoc.cleanupDisplay();
        }
    }

   public Storage getSimmMotionData() {
      return simmMotionData;
   }

   public Model getModel() {
      return model;
   }

    public boolean isRenderMuscleActivations() {
        return renderMuscleActivations;
    }

    public void setRenderMuscleActivations(boolean renderMuscleActivations) {
        this.renderMuscleActivations = renderMuscleActivations;
    }
    
    public void toggleTrail(ExperimentalDataObject aExperimentalDataObject)
    {
        vtkActor prop=objectTrails.get(aExperimentalDataObject);
        if (prop!=null){    // Created visuals already at some point
            prop.Modified();
            prop.SetVisibility(1-prop.GetVisibility());
             return;
        }
        // Else recreate
        prop = createTrail(aExperimentalDataObject);
        ViewDB.getInstance().addUserObjectToModel(model, prop);
        prop.Modified();
    }

    // This method precreates the Trails for motion objects regardless.
    // user uses commands to add or remove them from the scene.
    private vtkActor createTrail(ExperimentalDataObject object) {
        AnnotatedMotion mot= (AnnotatedMotion) simmMotionData;
        ArrayDouble xCoord = new ArrayDouble();
        ArrayDouble yCoord = new ArrayDouble();
        ArrayDouble zCoord = new ArrayDouble();
        double scale = 1.0;
        if (object.getObjectType()==ExperimentalDataItemType.MarkerData){
            int startIndex = object.getStartIndexInFileNotIncludingTime();
            mot.getDataColumn(startIndex, xCoord);
            mot.getDataColumn(startIndex+1, yCoord);
            mot.getDataColumn(startIndex+2, zCoord);
            scale = mot.getUnitConversion();
        } 
        else if (object.getObjectType()==ExperimentalDataItemType.PointForceData){
            int startIndex = object.getStartIndexInFileNotIncludingTime();
            mot.getDataColumn(startIndex+2, xCoord);
            mot.getDataColumn(startIndex+3, yCoord);
            mot.getDataColumn(startIndex+4, zCoord);           
        }
        else 
            return null;
        vtkAppendPolyData traceLinePolyData = new vtkAppendPolyData();
        int numPoints = xCoord.getSize();
        for(int i=0;i<numPoints-1;i++){
            vtkLineSource nextLine = new vtkLineSource();
            double vals[] = new double[]{xCoord.getitem(i), yCoord.getitem(i), zCoord.getitem(i)};
            double valsp1[] = new double[]{xCoord.getitem(i+1), yCoord.getitem(i+1), zCoord.getitem(i+1)};
            nextLine.SetPoint1(xCoord.getitem(i)/scale, yCoord.getitem(i)/scale, zCoord.getitem(i)/scale);
            nextLine.SetPoint2(xCoord.getitem(i+1)/scale, yCoord.getitem(i+1)/scale, zCoord.getitem(i+1)/scale);
            if (Double.isNaN(xCoord.getitem(i))||Double.isNaN(xCoord.getitem(i+1)))
                continue;   // Gap in data
            /*System.out.println("Line ("+nextLine.GetPoint1()[0]+", "+
                    nextLine.GetPoint1()[1]+", "+nextLine.GetPoint1()[2]+")- to "+nextLine.GetPoint2()[0]+
                    nextLine.GetPoint2()[1]+", "+nextLine.GetPoint2()[2]);*/
            traceLinePolyData.AddInput(nextLine.GetOutput());
        }
        vtkPolyDataMapper traceLineMapper = new vtkPolyDataMapper();
        traceLineMapper.SetInput(traceLinePolyData.GetOutput());
        vtkActor traceLineActor = new vtkActor();
        traceLineActor.SetMapper(traceLineMapper);
        objectTrails.put(object, traceLineActor);
        return traceLineActor;

    }
    public Vector<vtkActor> getActors()
    {
        Vector<vtkActor> dActors = new Vector<vtkActor>(4);
        if (groundForcesRep != null)
           dActors.add(groundForcesRep.getVtkActor());
        if (bodyForcesRep != null)
           dActors.add(bodyForcesRep.getVtkActor());
        if (generalizedForcesRep != null)
           dActors.add(generalizedForcesRep.getVtkActor());
        if (markersRep != null)
           dActors.add(markersRep.getVtkActor());
        Collection<vtkActor> trails = objectTrails.values();
        for(vtkActor nextActor:trails)
            dActors.add(nextActor);
        return dActors;
    }
    
    public void updateMotionObjects(){
        if (simmMotionData instanceof AnnotatedMotion){
            // Add place hoders for markers
            AnnotatedMotion mot= (AnnotatedMotion) simmMotionData;
            setExperimentalMarkerScaleFactor(mot.getDisplayForceScale());
            currentForceShape = mot.getDisplayForceShape();
            //AddMotionObjectsRep(model);
            Vector<ExperimentalDataObject> objects=mot.getClassified();
            mot.setMotionDisplayer(this);
            for(ExperimentalDataObject nextObject:objects){
                if (nextObject.getObjectType()==ExperimentalDataItemType.MarkerData){
                    bindMarkerToVisualizerObjectKeepHandle(nextObject);
                } else if (nextObject.getObjectType()==ExperimentalDataItemType.PointForceData){
                    bindForceVisualizerObjectKeepHandle(nextObject);
                } else if (nextObject.getObjectType()==ExperimentalDataItemType.BodyForceData){
                    bindForceVisualizerObjectKeepHandle(nextObject);
                }
                
            }
            //createTrails(model);
            return;
        }      
    }


    private void createDefaultMotionObjects() {
        if (getExperimenalMarkerGeometryJson() == null) {
            experimenalMarkerGeometryJson = new JSONObject();
            UUID uuidForMarkerGeometry = UUID.randomUUID();
            getExperimenalMarkerGeometryJson().put("uuid", uuidForMarkerGeometry.toString());
            getExperimenalMarkerGeometryJson().put("type", "SphereGeometry");
            getExperimenalMarkerGeometryJson().put("radius", 5);
            getExperimenalMarkerGeometryJson().put("name", "DefaultExperimentalMarker");
            JSONArray json_geometries = (JSONArray) modelVisJson.get("geometries");
            json_geometries.add(getExperimenalMarkerGeometryJson());

            experimenalMarkerMaterialJson = new JSONObject();
            UUID uuidForMarkerMaterial = UUID.randomUUID();
            getExperimenalMarkerMaterialJson().put("uuid", uuidForMarkerMaterial.toString());
            String colorString = JSONUtilities.mapColorToRGBA(getDefaultExperimentalMarkerColor());
            getExperimenalMarkerMaterialJson().put("type", "MeshPhongMaterial");
            getExperimenalMarkerMaterialJson().put("shininess", 30);
            getExperimenalMarkerMaterialJson().put("transparent", true);
            getExperimenalMarkerMaterialJson().put("emissive", JSONUtilities.mapColorToRGBA(new Vec3(0., 0., 0.)));
            getExperimenalMarkerMaterialJson().put("specular", JSONUtilities.mapColorToRGBA(new Vec3(0., 0., 0.)));
            getExperimenalMarkerMaterialJson().put("side", 2);
            getExperimenalMarkerMaterialJson().put("wireframe", false);
            getExperimenalMarkerMaterialJson().put("color", colorString);
            JSONArray json_materials = (JSONArray) modelVisJson.get("materials");
            json_materials.add(getExperimenalMarkerMaterialJson());
        }
        
    }

    public void addExperimentalDataObjectsToJson(AbstractList<ExperimentalDataObject> expObjects) {
        // Make sure this
        createDefaultMotionObjects();
        // create default top Group for motion
        JSONObject topJson = motionObjectsRoot;
        if (topJson.get("children") == null) {
            topJson.put("children", new JSONArray());
        }
        JSONArray motObjectsChildren = (JSONArray) topJson.get("children");
        for (ExperimentalDataObject nextExpObject : expObjects) {
            if (mapComponentToUUID.get(nextExpObject)!= null)
                continue;
            ArrayList<UUID> comp_uuids = new ArrayList<UUID>();
            motObjectsChildren.add(nextExpObject.createDecorationJson(comp_uuids, this));
            mapComponentToUUID.put(nextExpObject, comp_uuids);
        }
    }
    
    public OpenSimObject findObjectForUUID(String uuidString) {
        return mapUUIDToComponent.get(UUID.fromString(uuidString));
    }

    public ArrayList<UUID> findUUIDForObject(OpenSimObject obj) {
        return mapComponentToUUID.get(obj);
    }
   /**
     * @return the defaultExperimentalMarkerColor
     */
    public Vec3 getDefaultExperimentalMarkerColor() {
        return defaultExperimentalMarkerColor;
    }

    /**
     * @param defaultExperimentalMarkerColor the defaultExperimentalMarkerColor to set
     */
    public void setDefaultExperimentalMarkerColor(Color defaultExperimentalMarkerColor) {
        Vec3 colorAsVec3 = new Vec3();
        float[] colorComp = defaultExperimentalMarkerColor.getRGBColorComponents(null);
        for (int i =0; i <3; i++) 
            colorAsVec3.set(i, colorComp[i]);
        this.defaultExperimentalMarkerColor = colorAsVec3;
        Set<OpenSimObject> expermintalDataObjects = mapComponentToUUID.keySet();
        for (OpenSimObject expObj : expermintalDataObjects){
            // Find first ExperimentalMarker and change its Material, this will affect all of them
            if (expObj instanceof ExperimentalMarker){
                UUID expMarkerUUID = mapComponentToUUID.get(expObj).get(0); 
                String colorString = JSONUtilities.mapColorToRGBA(getDefaultExperimentalMarkerColor());
                experimenalMarkerMaterialJson.put("color", colorString);
                ViewDB.getInstance().applyColorToObjectByUUID(model, expMarkerUUID, colorAsVec3);  
                break;
            }
        }
        
    }

    /**
     * @return the experimentalMarkerScaleFactor
     */
    public double getExperimentalMarkerScaleFactor() {
        return experimentalMarkerScaleFactor;
    }

    /**
     * @param experimentalMarkerScaleFactor the experimentalMarkerScaleFactor to set
     */
    public void setExperimentalMarkerScaleFactor(double experimentalMarkerScaleFactor) {
        this.experimentalMarkerScaleFactor = experimentalMarkerScaleFactor;
        Set<OpenSimObject> expermintalDataObjects = mapComponentToUUID.keySet();
        for (OpenSimObject expObj : expermintalDataObjects){
            // Find first ExperimentalMarker and change its Material, this will affect all of them
            if (expObj instanceof ExperimentalMarker){
                UUID expMarkerUUID = mapComponentToUUID.get(expObj).get(0); 
                ViewDB.getInstance().scaleGeometryOfObjectByUUID(model, expMarkerUUID, experimentalMarkerScaleFactor);  
            }
        }
    }
    
    public double getExperimentalForceScaleFactor() {
        return experimentalForceScaleFactor;
    }

    public void setExperimentalForceScaleFactor(double newFactor) {
        this.experimentalForceScaleFactor = newFactor;
         Set<OpenSimObject> expermintalDataObjects = mapComponentToUUID.keySet();
         for (OpenSimObject expObj : expermintalDataObjects){
            // Find first ExperimentalMarker and change its Material, this will affect all of them
            if (expObj instanceof MotionObjectPointForce){
                UUID expForceUUID = mapComponentToUUID.get(expObj).get(0); 
                ViewDB.getInstance().scaleGeometryOfObjectByUUID(model, expForceUUID, experimentalForceScaleFactor);  
            }
        }
   }
}
