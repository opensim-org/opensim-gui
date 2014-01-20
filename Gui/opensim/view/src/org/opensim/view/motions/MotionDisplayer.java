/*
 * Copyright (c)  2005-2008, Stanford University
 * Use of the OpenSim software in source form is permitted provided that the following
 * conditions are met:
 * 	1. The software is used only for non-commercial research and education. It may not
 *     be used in relation to any commercial activity.
 * 	2. The software is not distributed or redistributed.  Software distribution is allowed 
 *     only through https://simtk.org/home/opensim.
 * 	3. Use of the OpenSim software or derivatives must be acknowledged in all publications,
 *      presentations, or documents describing work in which OpenSim or derivatives are used.
 * 	4. Credits to developers may not be removed from executables
 *     created from modifications of the source.
 * 	5. Modifications of source code must retain the above copyright notice, this list of
 *     conditions and the following disclaimer. 
 * 
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 *  SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 *  TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 *  HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR BUSINESS INTERRUPTION) OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 *  WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
import java.util.Collection;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;
import org.opensim.logger.OpenSimLogger;
import org.opensim.modeling.SimbodyEngine;
import org.opensim.view.MuscleColoringFunction;
import org.opensim.view.SelectedGlyphUserObject;
import org.opensim.view.SelectionListener;
import org.opensim.view.experimentaldata.AnnotatedMotion;
import org.opensim.modeling.Body;
import org.opensim.modeling.Coordinate;
import org.opensim.modeling.TransformAxis;
import org.opensim.modeling.Marker;
import org.opensim.modeling.ForceSet;
import org.opensim.modeling.ArrayDouble;
import org.opensim.modeling.Model;
import org.opensim.modeling.ArrayStr;
import org.opensim.modeling.BodySet;
import org.opensim.modeling.CoordinateSet;
import org.opensim.modeling.MarkerSet;
import org.opensim.modeling.OpenSimContext;
import org.opensim.modeling.OpenSimObject;
import org.opensim.view.experimentaldata.ExperimentalDataObject;
import org.opensim.view.experimentaldata.ModelForExperimentalData;
import org.opensim.modeling.StateVector;
import org.opensim.modeling.Storage;
import org.opensim.modeling.Vec3;
import org.opensim.view.OpenSimvtkGlyphCloud;
import org.opensim.view.SingleModelVisuals;
import org.opensim.view.experimentaldata.ExperimentalDataItemType;
import org.opensim.view.experimentaldata.MotionObjectPointForce;
import org.opensim.view.experimentaldata.MotionObjectBodyPoint;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;
import vtk.vtkActor;
import vtk.vtkAppendPolyData;
import vtk.vtkAssemblyNode;
import vtk.vtkAssemblyPath;
import vtk.vtkLineSource;
import vtk.vtkPolyDataMapper;
import vtk.vtkProp;

/**
 * 
 * 
 * 
 * @author Ayman. This class is used to preprocess motion files (Storage or similar) so that
 * 1. Mapping column indices to markersRep, gcs, ... is done only once.
 * 2. If additional objects need to be created for force or other markersRep they are maintained here.
 * 3. This isolates the display code from the specifics of Storage so that OpenSim proper creatures can be used.
 */

public class MotionDisplayer implements SelectionListener {

    double[] defaultExperimentalMarkerColor = new double[]{0.0, 0.35, 0.65};
    private double[] defaultForceColor = new double[]{0., 1.0, 0.};
    private MuscleColoringFunction mcf=null;
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
        getGroundForcesRep().setColor(defaultForceColor);
        
    }

    void setMuscleColoringFunction(MuscleColoringFunction mcbya) {
        mcf = mcbya;
        // Push it down to muscle displayers
        SingleModelVisuals vis = ViewDB.getInstance().getModelVisuals(model);
        vis.setMuscleColoringFunction(mcf);
        
    }
    
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
    private Storage simmMotionData;
    private Model model;
    OpenSimContext dContext; 
    ArrayStr stateNames;
    private double[] statesBuffer;
    private boolean renderMuscleActivations=false;
    double DEFAULT_FACTOR_SCALE_FACTOR=.001;
    double currentScaleFactor;
    String DEFAULT_FORCE_SHAPE="arrow";
    private String currentForceShape;
    
    // For columns that start with a body name, this is the map from column index to body reference.
    // The map is currently used only for body forces and generalized forces.
    private Hashtable<Integer, Body> mapIndicesToBodies = new Hashtable<Integer, Body>(10);
    // For generalized forces, this is the map from column index to DOF reference.
    private Hashtable<Integer, TransformAxis> mapIndicesToDofs = new Hashtable<Integer, TransformAxis>(10);
    
    protected Hashtable<ExperimentalDataObject, vtkActor> objectTrails = new Hashtable<ExperimentalDataObject, vtkActor>();

    private ArrayList<MotionDisplayer> associatedMotions = new  ArrayList<MotionDisplayer>();
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

    ArrayDouble interpolatedStates = null;

    boolean statesFile = false; // special type of file that contains full state vectors
    
    // A local copy of motionObjects so that different motions have different motion objects
    //Hashtable<String, vtkActor> motionObjectInstances =new Hashtable<String, vtkActor>(10);
    
    /** Creates a new instance of MotionDisplayer */
    public MotionDisplayer(Storage motionData, Model model) {
        this.model = model;
        dContext= OpenSimDB.getInstance().getContext(model);
        simmMotionData = motionData;
        currentScaleFactor = DEFAULT_FACTOR_SCALE_FACTOR;
        currentForceShape = DEFAULT_FORCE_SHAPE;
        setupMotionDisplay();
        // create a buffer to be used for comuptation of constrained states
        statesBuffer = new double[model.getNumStateVariables()];
        SingleModelVisuals vis = ViewDB.getInstance().getModelVisuals(model);
        if (model instanceof ModelForExperimentalData) return;
        if(vis!=null) vis.setApplyMuscleColors(isRenderMuscleActivations());
    }

    public void setupMotionDisplay() { 
        if (simmMotionData == null)
           return;


        ArrayStr colNames = simmMotionData.getColumnLabels();
        int numColumnsIncludingTime = colNames.getSize();
        interpolatedStates = new ArrayDouble(0.0, numColumnsIncludingTime-1);
        AddMotionObjectsRep(model);
        if (simmMotionData instanceof AnnotatedMotion){
            // Add place hoders for markers
            AnnotatedMotion mot= (AnnotatedMotion) simmMotionData;
            Vector<ExperimentalDataObject> objects=mot.getClassified();
            mot.setMotionDisplayer(this);
            for(ExperimentalDataObject nextObject:objects){
                if (nextObject.getObjectType()==ExperimentalDataItemType.MarkerData){
                    int glyphIndex=markersRep.addLocation(nextObject);
                    nextObject.setGlyphInfo(glyphIndex, markersRep);
                } else if (nextObject.getObjectType()==ExperimentalDataItemType.PointForceData){
                    int glyphIndex=groundForcesRep.addLocation(nextObject);
                    nextObject.setGlyphInfo(glyphIndex, groundForcesRep);
                } else if (nextObject.getObjectType()==ExperimentalDataItemType.BodyForceData){
                    int glyphIndex=groundForcesRep.addLocation(nextObject);
                    nextObject.setGlyphInfo(glyphIndex, groundForcesRep);
                }
                
            }
            //createTrails(model);
            return;
        }
        mapIndicesToBodies.clear();
        mapIndicesToDofs.clear();

        stateNames = model.getStateVariableNames();
        stateNames.insert(0, "time");

        if(colNames.arrayEquals(stateNames)) {
           // This is a states file
           statesFile = true;
           SingleModelVisuals vis = ViewDB.getInstance().getModelVisuals(model);
           if(vis!=null) vis.setApplyMuscleColors(true);
           setRenderMuscleActivations(true);
        } else  {
           // We should build sorted lists of object names so that we can find them easily
           for(int i=0; i < numColumnsIncludingTime; i++){
              String columnName = colNames.getitem(i);   // Time is included in labels
              int numClassified = classifyColumn(model, i, columnName); // find out if column is gencord/muscle/segment/...etc.
              ObjectTypesInMotionFiles cType = mapIndicesToObjectTypes.get(i);
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

    private void AddMotionObjectsRep(final Model model) {
        if (groundForcesRep != null)
           ViewDB.getInstance().removeUserObjectFromModel(model, groundForcesRep.getVtkActor());
        if (bodyForcesRep != null)
           ViewDB.getInstance().removeUserObjectFromModel(model, bodyForcesRep.getVtkActor());
        if (generalizedForcesRep != null)
           ViewDB.getInstance().removeUserObjectFromModel(model, generalizedForcesRep.getVtkActor());
        if (markersRep != null)
           ViewDB.getInstance().removeUserObjectFromModel(model, markersRep.getVtkActor());
        
        groundForcesRep = new OpenSimvtkGlyphCloud(true);   groundForcesRep.setName("GRF");
        bodyForcesRep = new OpenSimvtkGlyphCloud(true);     bodyForcesRep.setName("BodyForce");
        generalizedForcesRep = new OpenSimvtkGlyphCloud(true); bodyForcesRep.setName("JointForce");
        markersRep = new OpenSimvtkGlyphCloud(false);   bodyForcesRep.setName("Exp. Markers");

        groundForcesRep.setShapeName(currentForceShape);
        groundForcesRep.setColor(defaultForceColor);
        groundForcesRep.setColorRange(defaultForceColor, defaultForceColor);
        groundForcesRep.setOpacity(0.7);
        groundForcesRep.setScaleFactor(currentScaleFactor);
        groundForcesRep.orientByNormalAndScaleByVector();

        bodyForcesRep.setShapeName("arrow");
        bodyForcesRep.setColor(new double[]{0., 0., 1.0});
        bodyForcesRep.setOpacity(0.7);
        bodyForcesRep.setScaleFactor(currentScaleFactor);
        bodyForcesRep.orientByNormalAndScaleByVector();

        generalizedForcesRep.setShapeName("arrow");
        generalizedForcesRep.setColor(new double[]{0., 1.0, 1.0});
        generalizedForcesRep.setOpacity(0.7);
        generalizedForcesRep.setScaleFactor(currentScaleFactor);
        generalizedForcesRep.orientByNormalAndScaleByVector();

        markersRep.setShapeName("marker");
        markersRep.setColor(defaultExperimentalMarkerColor); //Scale , scaleBy
        markersRep.setColorRange(defaultExperimentalMarkerColor, defaultExperimentalMarkerColor);
        markersRep.scaleByVectorComponents();
        markersRep.setScaleFactor(ViewDB.getInstance().getExperimentalMarkerDisplayScale());
        
        ViewDB.getInstance().addUserObjectToModel(model, groundForcesRep.getVtkActor());
        ViewDB.getInstance().addUserObjectToModel(model, bodyForcesRep.getVtkActor());
        ViewDB.getInstance().addUserObjectToModel(model, generalizedForcesRep.getVtkActor());
        ViewDB.getInstance().addUserObjectToModel(model, markersRep.getVtkActor());
        ViewDB.getInstance().addSelectionListener(this);
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
      CoordinateSet coords = model.getCoordinateSet();
      for (int i = 0; i<coords.getSize(); i++){
         Coordinate co = coords.get(i);
         // GenCoord
         String cName = co.getName();
         if (cName.equals(columnName)){
            mapIndicesToObjectTypes.put(columnIndex, ObjectTypesInMotionFiles.GenCoord);
            mapIndicesToObjects.put(columnIndex, co); //co.setValue();
            return 1;
         }
         // GenCoord_Velocity
         if (columnName.endsWith("_vel")){ //_u
            if (columnName.equals(cName+"_vel")) //_u
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
      if (columnName.contains(".excitation") || columnName.contains("activation")){
          setRenderMuscleActivations(true);
      }
      ForceSet acts = model.getForceSet();
      for (int i=0; i< acts.getSize(); i++)
          if (columnName.startsWith(acts.get(i).getName())){    // Make sure it's a muscle state'
          // Any other state
          int stateIndex=stateNames.findIndex(columnName);  // includes time so 0 is time
          if (stateIndex>0){
              int stateIndexMinusTime = stateIndex-1;
              mapIndicesToObjectTypes.put(columnIndex, ObjectTypesInMotionFiles.State);
              mapIndicesToObjects.put(columnIndex, new Integer(stateIndexMinusTime));  
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
            int index= markersRep.addLocation(0., 0., 0.);
            mapIndicesToObjects.put(columnIndex, new Integer(index));
            mapIndicesToObjects.put(columnIndex+1, new Integer(index));
            mapIndicesToObjects.put(columnIndex+2, new Integer(index));
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
      StateVector states=simmMotionData.getStateVector(currentFrame);
      applyStatesToModel(states.getData());
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
      OpenSimDB.getInstance().getContext(model).getCurrentStateRef().setTime(clampedTime);
      simmMotionData.getDataAtTime(clampedTime, interpolatedStates.getSize(), interpolatedStates);
      applyStatesToModel(interpolatedStates);
      // Repeat for associated motions
      for (MotionDisplayer assocMotion:associatedMotions){
          assocMotion.applyTimeToModel(currentTime);
      }
   }

   private void applyStatesToModel(ArrayDouble states) 
   {
     boolean profile=false;//(OpenSimObject.getDebugLevel()>=2);
     long before = 0, after=0;
     if (profile)
          before =System.nanoTime();
     if (simmMotionData instanceof AnnotatedMotion){
          int dataSize = states.getSize();
          AnnotatedMotion mot = (AnnotatedMotion)simmMotionData;
          Vector<ExperimentalDataObject> objects=mot.getClassified();
          boolean markersModified=false;
          boolean forcesModified=false;
           for(ExperimentalDataObject nextObject:objects){
                if (!nextObject.isDisplayed()) continue;
                if (nextObject.getObjectType()==ExperimentalDataItemType.MarkerData){
                    int startIndex = nextObject.getStartIndexInFileNotIncludingTime();
                    markersRep.setLocation(nextObject.getGlyphIndex(), 
                            states.getitem(startIndex)/mot.getUnitConversion(), 
                            states.getitem(startIndex+1)/mot.getUnitConversion(), 
                            states.getitem(startIndex+2)/mot.getUnitConversion());
                    markersModified = true;
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
                    dContext.transform(b, forceLocal, model.getGroundBody(), forceGlobal);
                    groundForcesRep.setNormalAtLocation(nextObject.getGlyphIndex(), 
                            forceGlobal[0], 
                            forceGlobal[1], 
                            forceGlobal[2]);
                    forcesModified=true;
              } else if (nextObject.getObjectType()==ExperimentalDataItemType.BodyForceData){
                    int startIndex = nextObject.getStartIndexInFileNotIncludingTime();
                    MotionObjectBodyPoint bodyPointObject = (MotionObjectBodyPoint)nextObject;
                    double[] bodyPoint =bodyPointObject.getPoint();
                    Body b = model.getBodySet().get(bodyPointObject.getPointExpressedInBody());
                    double[] bodyPointGlobal = new double[3]; 
                    // Transform to ground from body frame
                    dContext.transformPosition(b, bodyPoint, bodyPointGlobal);
                    groundForcesRep.setLocation(nextObject.getGlyphIndex(), 
                            bodyPointGlobal[0], bodyPointGlobal[1], bodyPointGlobal[2]);
                    double[] vectorGlobal = new double[]{states.getitem(startIndex), 
                            states.getitem(startIndex+1), 
                            states.getitem(startIndex+2)}; 
                   
                    if (b==model.getGroundBody())
                         maskForceComponent(vectorGlobal, ((MotionObjectPointForce)nextObject).getForceComponent());
                    else{
                        double[] vectorLocal = new double[]{
                                states.getitem(startIndex), 
                                states.getitem(startIndex+1), 
                                states.getitem(startIndex+2)
                        };
                        maskForceComponent(vectorLocal, ((MotionObjectPointForce)nextObject).getForceComponent());
                        // Transform to ground from body frame
                        dContext.transform(b, vectorLocal, model.getGroundBody(), vectorGlobal);
                    }
                    
                    groundForcesRep.setNormalAtLocation(nextObject.getGlyphIndex(), 
                            vectorGlobal[0], vectorGlobal[1], vectorGlobal[2]);
                    forcesModified=true;
              }
              
              if (forcesModified) groundForcesRep.setModified();
              if (markersModified) markersRep.setModified();
          }
           //groundForcesRep.hide(0);
          return;
      }
      OpenSimContext context = OpenSimDB.getInstance().getContext(model);

      if(statesFile) {
         context.setStates(states);
      } else {
         boolean realize=false;
         int which=-1;
         for(int i=0; i<genCoordColumns.size(); i++) {
            Coordinate coord=(Coordinate)(genCoordColumns.get(i).object);
            if(!context.getLocked(coord)) {
               int index = genCoordColumns.get(i).stateVectorIndex;
               context.setValue(coord, states.getitem(index), false);
               realize=true;
               which=i;
            }
            // Make sure we realize once IF a coordinate has bben set
            if (i==genCoordColumns.size()-1 && realize){
                coord=(Coordinate)(genCoordColumns.get(which).object);
                int index = genCoordColumns.get(which).stateVectorIndex;
                context.setValue(coord, states.getitem(index), true);
            }
         }
         // update states to make sure constraints are valid
         context.getStates(statesBuffer);
         //OpenSim20 model.getDynamicsEngine().computeConstrainedCoordinates(statesBuffer);
         // Any other states including muscles
         for(int i=0; i<anyStateColumns.size(); i++) {
              int index = anyStateColumns.get(i).stateVectorIndex;
              double newValue=states.getitem(index);
              // Set value in statesBuffer
              Object o=mapIndicesToObjects.get(index+1);
              int bufferIndex = ((Integer)o).intValue();
              statesBuffer[bufferIndex]=newValue;
         }
         context.setStates(statesBuffer);
         
         for(int i=0; i<segmentMarkerColumns.size(); i++) {
            int markerIndex = ((Integer)(segmentMarkerColumns.get(i).object)).intValue();
            int index = segmentMarkerColumns.get(i).stateVectorIndex;
            markersRep.setLocation(markerIndex, states.getitem(index), states.getitem(index+1), states.getitem(index+2));
         }
         if(segmentMarkerColumns.size()>0) markersRep.setModified();

         for(int i=0; i<genCoordForceColumns.size(); i++) {
            int forceIndex = ((Integer)(genCoordForceColumns.get(i).object)).intValue();
            int index = genCoordForceColumns.get(i).stateVectorIndex;
            SimbodyEngine de = model.getSimbodyEngine();
            Body body = mapIndicesToBodies.get(index+1);
            TransformAxis dof = mapIndicesToDofs.get(index+1);
            Vec3 vOffset = new Vec3();
            double[] offset = new double[3];
            double[] gOffset = new double[3];
            dof.getAxis(offset); // in parent frame, right?
            double magnitude = states.getitem(index);
            for (int j=0; j<3; j++)
               offset[j] *= (magnitude * 10.0); // * 10.0 because test data is small
            context.transform(body, offset, de.getGroundBody(), gOffset);
            generalizedForcesRep.setNormalAtLocation(forceIndex, gOffset[0], gOffset[1], gOffset[2]);
            vOffset = dof.getJoint().getLocationInChild();
            for (int ix=0; ix<3; ix++) offset[ix]=vOffset.get(ix);
            context.transformPosition(body, offset, gOffset);
            generalizedForcesRep.setLocation(forceIndex, gOffset[0], gOffset[1], gOffset[2]);
         }
         if(genCoordForceColumns.size()>0) {
            generalizedForcesRep.setModified();
         }

         for(int i=0; i<segmentForceColumns.size(); i++) {
            int forceIndex = ((Integer)(segmentForceColumns.get(i).object)).intValue();
            int index = segmentForceColumns.get(i).stateVectorIndex;
            SimbodyEngine de = model.getSimbodyEngine();
            Body body = mapIndicesToBodies.get(index+1);
            double[] offset = new double[3];
            double[] gOffset = new double[3];
            for (int j=0; j<3; j++)
               offset[j] = states.getitem(index+j);
            if (body.equals(de.getGroundBody())) {
               groundForcesRep.setNormalAtLocation(forceIndex, offset[0], offset[1], offset[2]);
            } else {
               context.transform(body, offset, de.getGroundBody(), gOffset);
               bodyForcesRep.setNormalAtLocation(forceIndex, gOffset[0], gOffset[1], gOffset[2]);
            }
            for (int j=0; j<3; j++)
               offset[j] = states.getitem(index+j+3);
            if (body.equals(de.getGroundBody())) {
               groundForcesRep.setLocation(forceIndex, offset[0], offset[1], offset[2]);
            } else {
               context.transformPosition(body, offset, gOffset);
               bodyForcesRep.setLocation(forceIndex, gOffset[0], gOffset[1], gOffset[2]);
            }
         }
         if(segmentForceColumns.size()>0) {
            groundForcesRep.setModified();
            bodyForcesRep.setModified();
         }
      }
      if (profile) {
          after=System.nanoTime();
          OpenSimLogger.logMessage("applyFrameToModel time: "+1e-6*(after-before)+" ms.\n", OpenSimLogger.INFO);
      }
    }

    /*
     * cleanupDisplay is called when the motion is mode non-current either explicitly by the user or by selecting
     * another motion for the same model and making it current */
    void cleanupDisplay() {
        if (groundForcesRep!=null)
            ViewDB.getInstance().removeUserObjectFromModel(model, groundForcesRep.getVtkActor());
        if (bodyForcesRep!=null)
            ViewDB.getInstance().removeUserObjectFromModel(model, bodyForcesRep.getVtkActor());
        if (generalizedForcesRep!=null)
            ViewDB.getInstance().removeUserObjectFromModel(model, generalizedForcesRep.getVtkActor());
        if (markersRep!=null)
            ViewDB.getInstance().removeUserObjectFromModel(model, markersRep.getVtkActor());

        // Don't attempt to change muscle activation color if we're here because
        // the model is closing... check this by checking model is still in models list
        // This may help fix a crash that Sam got when he closed a model that had a MotionDisplayer
        // associated with it.  It may be because setRenderMuscleActivations ends up updating the actuator
        // geometry, and if the model is closing it may be that it was in the process of being deleted when
        // those actuators were referred to...  So we avoid all that with this if statement.
        if(OpenSimDB.getInstance().hasModel(model) && renderMuscleActivations) {
           SingleModelVisuals vis = ViewDB.getInstance().getModelVisuals(model);
           if(vis!=null) vis.setApplyMuscleColors(false);
        }
        // If trails are shown, hide them too
        Enumeration<vtkActor> trailActors = objectTrails.elements();
        while (trailActors.hasMoreElements()){
            ViewDB.getInstance().removeUserObjectFromModel(model, trailActors.nextElement());
        }
        for (MotionDisplayer assocMotion:associatedMotions){
          assocMotion.cleanupDisplay();
      }
      setMuscleColoringFunction(null);
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

    public void pickUserObject(vtkAssemblyPath asmPath, int cellId) {
        if (asmPath != null) {
         vtkAssemblyNode pickedAsm = asmPath.GetLastNode();
         vtkProp dProp = pickedAsm.GetViewProp();
         int index=getActors().indexOf(dProp);
         if (index >=0){
             vtkActor dActor=getActors().get(index);
             if (dProp==groundForcesRep.getVtkActor())
                 handleSelection(groundForcesRep, cellId);
             else if(dProp==bodyForcesRep.getVtkActor())
                 handleSelection(bodyForcesRep, cellId);
             else if (dProp==generalizedForcesRep.getVtkActor())
                 handleSelection(generalizedForcesRep, cellId);
             else if (dProp==markersRep.getVtkActor()){
                 handleSelection(markersRep, cellId);
             }
             else  
                 System.out.println("Unknown user object ");
             
         }
         return;
        }  
        
    }

    private void handleSelection(final OpenSimvtkGlyphCloud glyphRep, final int cellId) {
            final OpenSimObject obj = glyphRep.getPickedObject(cellId);
            if (obj!=null)
            // SelectedGlyphUserObject provies the bbox, name, other attributes needed for selection mgmt
                ViewDB.getInstance().markSelected(new SelectedGlyphUserObject(obj, model, glyphRep), true, false, true);
    }
    
    public void updateMotionObjects(){
        if (simmMotionData instanceof AnnotatedMotion){
            // Add place hoders for markers
            AnnotatedMotion mot= (AnnotatedMotion) simmMotionData;
            currentScaleFactor = mot.getDisplayForceScale();
            currentForceShape = mot.getDisplayForceShape();
            AddMotionObjectsRep(model);
            Vector<ExperimentalDataObject> objects=mot.getClassified();
            mot.setMotionDisplayer(this);
            for(ExperimentalDataObject nextObject:objects){
                if (nextObject.getObjectType()==ExperimentalDataItemType.MarkerData){
                    int glyphIndex=markersRep.addLocation(nextObject);
                    nextObject.setGlyphInfo(glyphIndex, markersRep);
                } else if (nextObject.getObjectType()==ExperimentalDataItemType.PointForceData){
                    int glyphIndex=groundForcesRep.addLocation(nextObject);
                    nextObject.setGlyphInfo(glyphIndex, groundForcesRep);
                } else if (nextObject.getObjectType()==ExperimentalDataItemType.BodyForceData){
                    int glyphIndex=groundForcesRep.addLocation(nextObject);
                    nextObject.setGlyphInfo(glyphIndex, groundForcesRep);
                }
                
            }
            //createTrails(model);
            return;
        }      
    }
}
