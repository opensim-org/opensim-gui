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
import java.util.prefs.Preferences;
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
import org.opensim.modeling.Marker;
import org.opensim.modeling.MarkerSet;
import org.opensim.modeling.Model;
import org.opensim.modeling.Muscle;
import org.opensim.modeling.MuscleIterator;
import org.opensim.modeling.MuscleList;
import org.opensim.modeling.OpenSimContext;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.PhysicalFrame;
import org.opensim.modeling.StateVector;
import org.opensim.modeling.Storage;
import org.opensim.modeling.Transform;
import org.opensim.modeling.TransformAxis;
import org.opensim.modeling.UnitVec3;
import org.opensim.modeling.Vec3;
import org.opensim.threejs.JSONUtilities;
import org.opensim.threejs.ModelVisualizationJson;
import org.opensim.utils.TheApp;
import org.opensim.view.MuscleColoringFunction;
import org.opensim.view.experimentaldata.AnnotatedMotion;
import org.opensim.view.experimentaldata.ExperimentalDataItemType;
import org.opensim.view.experimentaldata.ExperimentalDataObject;
import org.opensim.view.experimentaldata.ExperimentalMarker;
import org.opensim.view.experimentaldata.ModelForExperimentalData;
import org.opensim.view.experimentaldata.MotionObjectBodyPoint;
import org.opensim.view.experimentaldata.MotionObjectPointForce;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;


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
    private JSONObject experimentalMarkerGeometryJson=null;
    private JSONObject experimentalMarkerMaterialJson=null;
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
    private final Storage simmMotionData; // Storage used to provide data
    private Storage motionAsStates=null; // In case we're playing back states this is complete State on std. layout for internal use only
    private Model model;
    OpenSimContext dContext; 
    ArrayStr stateNames;
    private boolean renderMuscleActivations=false;
    private double experimentalMarkerRadius=1000;
    private double experimentalForceScaleFactor=1;
    String DEFAULT_FORCE_SHAPE="arrow";
    private String currentForceShape;
    
    // For columns that start with a body name, this is the map from column index to body reference.
    // The map is currently used only for body forces and generalized forces.
    private Hashtable<Integer, Body> mapIndicesToBodies = new Hashtable<Integer, Body>(10);
    // For generalized forces, this is the map from column index to DOF reference.
    private Hashtable<Integer, TransformAxis> mapIndicesToDofs = new Hashtable<Integer, TransformAxis>(10);

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
                Vec3 pathColor = modelVisJson.getCurrentPathColorMap().getColor(msl.getGeometryPath(), modelVisJson.getState(), newColorInBlueToRed);
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
        jsonGeomArray.add(getExperimentalMarkerGeometryJson());
        JSONArray jsonMatArray = new JSONArray();
        jsonMatArray.add(getExperimentalMarkerMaterialJson());
        topJson.put("geometries", jsonGeomArray);
        topJson.put("materials", jsonMatArray);
        topJson.put("object", motionObjectsRoot);
        return topJson;
    }

    /**
     * @return the experimentalMarkerGeometryJson
     */
    public JSONObject getExperimentalMarkerGeometryJson() {
        return experimentalMarkerGeometryJson;
    }

    /**
     * @return the experimentalMarkerMaterialJson
     */
    public JSONObject getExperimentalMarkerMaterialJson() {
        return experimentalMarkerMaterialJson;
    }

    /**
     * @return the defaultForceColorVec3
     */
    public Vec3 getDefaultForceColorVec3() {
        return defaultForceColorVec3;
    }
    
    
    /** Creates a new instance of MotionDisplayer */
    public MotionDisplayer(Storage motionData, Model model) {
         // The following value should stay the same as the value in Installer.java
        String defaultMarkerRadiusString = STRING_EXPMARKER_DEFAULT_RADIUS; // new default per issue #643
        String currentSize =TheApp.getCurrentVersionPreferences().get("Visualizer: Experimental Marker Radius (mm)", 
                defaultMarkerRadiusString);
        TheApp.getCurrentVersionPreferences().put("Visualizer: Experimental Marker Radius (mm)", currentSize);
        this.experimentalForceScaleFactor = 1.0;
        this.experimentalMarkerRadius = Double.parseDouble(currentSize);
        this.model = model;
        dContext= OpenSimDB.getInstance().getContext(model);
        simmMotionData = motionData;
        currentForceShape = DEFAULT_FORCE_SHAPE;
        modelVisJson = ViewDB.getInstance().getModelVisualizationJson(model);
        // We create a temporary array to hold interpolated values, in case Slider doesn't coincide with data
        colNames = simmMotionData.getColumnLabels();
        int numColumnsIncludingTime = colNames.getSize();
        interpolatedStates = new ArrayDouble(0.0, numColumnsIncludingTime-1);
        
        modelVisJson.addMotionDisplayer(this);
        if (model instanceof ModelForExperimentalData) return;

    }
    public static final String STRING_EXPMARKER_DEFAULT_RADIUS = "10";
    public void setupMotionDisplay() { 
        if (simmMotionData == null)
           return;

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
            dContext.resetStateToDefault(); // TODO revisit fix #279
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
        nextObject.setDataObjectUUID(findUUIDForObject(nextObject).get(0));
    }

    private void bindMarkerToVisualizerObjectKeepHandle(ExperimentalDataObject nextObject) {
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
       // in live RRA and other situations where we get 4.0 style state-pathnames as columns, use them
       if (stateNames != null && stateNames.findIndex(columnName) != -1) {
           int stateIndex = stateNames.findIndex(columnName);  // includes time so 0 is time
           int stateIndexMinusTime = stateIndex - 1;
           mapIndicesToObjectTypes.put(columnIndex, ObjectTypesInMotionFiles.State);
           mapIndicesToObjects.put(columnIndex, new Integer(stateIndexMinusTime));
           canonicalStateNames.add(columnName);
           return 1;
       }
      String canonicalCcolumnName = columnName.replace('.', '/');
      CoordinateSet coords = model.getCoordinateSet();
      for (int i = 0; i<coords.getSize(); i++){
         Coordinate co = coords.get(i);
         // GenCoord
         String cName = co.getName();
         if (cName.equals(columnName)||columnName.equals(co.getStateVariableNames().get(0))) {
            mapIndicesToObjectTypes.put(columnIndex, ObjectTypesInMotionFiles.GenCoord);
            mapIndicesToObjects.put(columnIndex, co); //co.setValue();
            return 1;
         }
         // GenCoord_Velocity
         if (columnName.equals(cName+"_vel")|| columnName.equals(cName+"_u") ||
                columnName.equals(co.getStateVariableNames().get(1))) { //_u 
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
       for (int i = 0; i<bodySet.getSize(); i++){
         Body bdy = bodySet.get(i);
         // 
         String bName = bdy.getName();
         if (columnName.startsWith(bName+"_")){
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
          mot.updateDecorations(interpolatedStates);
          return;
      }
     // Here handling a motion file with potentially extra columns for Forces, Markers
      OpenSimContext context = OpenSimDB.getInstance().getContext(model);
      
      if(statesFile || motionAsStates!=null) {
          // FIX40 speed this up by using map or YIndex
          context.getCurrentStateRef().setTime(assocTime);
          model.setStateVariableValues(context.getCurrentStateRef(), states.getAsVector());
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
    
   
    public void updateMotionObjects(){
        if (simmMotionData instanceof AnnotatedMotion){
            // Add place hoders for markers
            AnnotatedMotion mot= (AnnotatedMotion) simmMotionData;
            setExperimentalForceScaleFactor(mot.getDisplayForceScale());
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
        if (getExperimentalMarkerGeometryJson() == null) {
            experimentalMarkerGeometryJson = new JSONObject();
            UUID uuidForMarkerGeometry = UUID.randomUUID();
            experimentalMarkerGeometryJson.put("uuid", uuidForMarkerGeometry.toString());
            experimentalMarkerGeometryJson.put("type", "SphereGeometry");
            experimentalMarkerGeometryJson.put("radius", experimentalMarkerRadius);
            experimentalMarkerGeometryJson.put("widthSegments", 32);
            experimentalMarkerGeometryJson.put("heightSegments", 16);            
            getExperimentalMarkerGeometryJson().put("name", "DefaultExperimentalMarker");
            JSONArray json_geometries = (JSONArray) modelVisJson.get("geometries");
            json_geometries.add(getExperimentalMarkerGeometryJson());

            experimentalMarkerMaterialJson = new JSONObject();
            UUID uuidForMarkerMaterial = UUID.randomUUID();
            experimentalMarkerMaterialJson.put("uuid", uuidForMarkerMaterial.toString());
            String colorString = JSONUtilities.mapColorToRGBA(getDefaultExperimentalMarkerColor());
            experimentalMarkerMaterialJson.put("type", "MeshPhongMaterial");
            experimentalMarkerMaterialJson.put("shininess", 30);
            experimentalMarkerMaterialJson.put("transparent", true);
            experimentalMarkerMaterialJson.put("emissive", JSONUtilities.mapColorToRGBA(new Vec3(0., 0., 0.)));
            experimentalMarkerMaterialJson.put("specular", JSONUtilities.mapColorToRGBA(new Vec3(0., 0., 0.)));
            experimentalMarkerMaterialJson.put("side", 2);
            experimentalMarkerMaterialJson.put("wireframe", false);
            experimentalMarkerMaterialJson.put("color", colorString);
            JSONArray json_materials = (JSONArray) modelVisJson.get("materials");
            json_materials.add(getExperimentalMarkerMaterialJson());
        }
        
    }

    public void addExperimentalDataObjectsToJson(AbstractList<ExperimentalDataObject> expObjects) {
        
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
            mapUUIDToComponent.put(comp_uuids.get(0), nextExpObject);
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
                experimentalMarkerMaterialJson.put("color", colorString);
                ViewDB.getInstance().applyColorToObjectByUUID(model, expMarkerUUID, colorAsVec3);  
                break;
            }
        }
        
    }

    /**
     * @return the experimentalMarkerRadius
     */
    public double getExperimentalMarkerRadius() {
        return experimentalMarkerRadius;
    }

    /**
     * @param experimentalMarkerRadius the experimentalMarkerRadius to set
     */
    public void setExperimentalMarkerRadius(double experimentalMarkerRadius) {
        this.experimentalMarkerRadius = experimentalMarkerRadius;
        Set<OpenSimObject> expermintalDataObjects = mapComponentToUUID.keySet();
        for (OpenSimObject expObj : expermintalDataObjects){
            // Find first ExperimentalMarker and change its Material, this will affect all of them
            if (expObj instanceof ExperimentalMarker){
                UUID expMarkerUUID = mapComponentToUUID.get(expObj).get(0); 
                ViewDB.getInstance().resizeGeometryOfObjectByUUID(model, expMarkerUUID, 
                        experimentalMarkerRadius);  
            }
        }
        // update cached experimentalMarkerGeometryJson
         experimentalMarkerGeometryJson.put("radius", experimentalMarkerRadius);
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
                ViewDB.getInstance().resizeGeometryOfObjectByUUID(model, expForceUUID, experimentalForceScaleFactor);  
            }
        }
   }
}
