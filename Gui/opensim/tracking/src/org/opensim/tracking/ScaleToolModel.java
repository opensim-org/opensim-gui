/* -------------------------------------------------------------------------- *
 * OpenSim: ScaleToolModel.java                                               *
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

package org.opensim.tracking;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Cancellable;
import org.opensim.modeling.ArrayDouble;
import org.opensim.modeling.ArrayStr;
import org.opensim.modeling.BodyScale;
import org.opensim.modeling.BodyScaleSet;
import org.opensim.modeling.BodySet;
import org.opensim.modeling.GenericModelMaker;
import org.opensim.modeling.MarkerData;
import org.opensim.modeling.MarkerPair;
import org.opensim.modeling.MarkerPlacer;
import org.opensim.modeling.MarkerSet;
import org.opensim.modeling.Measurement;
import org.opensim.modeling.MeasurementSet;
import org.opensim.modeling.Model;
import org.opensim.modeling.ModelScaler;
import org.opensim.modeling.OpenSimContext;
import org.opensim.modeling.Scale;
import org.opensim.modeling.ScaleSet;
import org.opensim.modeling.ScaleTool;
import org.opensim.modeling.Storage;
import org.opensim.modeling.Vec3;
import org.opensim.view.motions.MotionsDB;
import org.opensim.swingui.SwingWorker;
import org.opensim.utils.ErrorDialog;
import org.opensim.utils.FileUtils;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;

//==================================================================
// OptionalFile (helper class)
//==================================================================
class OptionalFile {
   public String fileName = "";
   public boolean enabled = false;
   public boolean isValid() { return enabled && !FileUtils.effectivelyNull(fileName); }
   public void fromProperty(String fileName) { 
      if(FileUtils.effectivelyNull(fileName)) { enabled = false; this.fileName = ""; }
      else { enabled = true; this.fileName = fileName; }
   }
   public String toProperty() { 
      return (enabled && !FileUtils.effectivelyNull(fileName)) ? fileName : "Unassigned";
   }
}

//==================================================================
// BodyScaleFactors
//==================================================================
class BodyScaleFactors {
   public boolean useManual = false;
   public int[] measurements = new int[]{-1, -1, -1}; // -1 means no measurement applied to that axis, otherwise it's the index of a measurement in the measurement set
   public double[] manualScales = new double[]{1., 1., 1.};

   // UTILITY
   public boolean uniformMeasurements() { return measurements[0]==measurements[1] && measurements[0]==measurements[2]; }
   public boolean uniformManualScales() { return manualScales[0]==manualScales[1] && manualScales[0]==manualScales[2]; }
   public boolean useManualScale() { return useManual; }
   public boolean manualScalesAreIdentity() { return manualScales[0]==1 && manualScales[1]==1 && manualScales[2]==1; }
   public void setUseManualScale(boolean useManual) { this.useManual = useManual; }

   public String toString() {
      return "BodyScaleFactors[measurements={"+measurements[0]+","+measurements[1]+","+measurements[2]+"}"+
               ((manualScales!=null)?(",manualScales={"+manualScales[0]+","+manualScales[1]+","+manualScales[2]+"}"):"")+"]";
   }
}

//==================================================================
// BodySetScaleFactors
//==================================================================
class BodySetScaleFactors extends Vector<BodyScaleFactors> {

   private ScaleToolModel scaleToolModel;
   private BodySet bodySet;
   private Hashtable<String,Integer> mapBodyNameToIndex;

   public BodySetScaleFactors(ScaleToolModel scaleToolModel, BodySet bodySet) {
      super(bodySet.getSize());
      this.scaleToolModel = scaleToolModel;
      this.bodySet = bodySet;

      setSize(bodySet.getSize());
      mapBodyNameToIndex = new Hashtable<String,Integer>(size());

      for(int i=0; i<size(); i++) {
         mapBodyNameToIndex.put(bodySet.get(i).getName(),new Integer(i));
         set(i, new BodyScaleFactors());
      }
   }


   public void removeMeasurement(int index) {
      for(int i=0; i<size(); i++) {
         for(int j=0; j<3; j++) {
            if(get(i).measurements[j]==index) get(i).measurements[j]=-1;
            else if(get(i).measurements[j]>index) get(i).measurements[j]--;
         }
      }
   }

   private int axisToIndex(String axis) {
      String axisUpper = axis.toUpperCase();
      return (axisUpper.equals("X") ? 0 : (axisUpper.equals("Y") ? 1 : (axisUpper.equals("Z") ? 2 : -1)));
   }
   private String indexToAxis(int index) {
      return (index==0 ? "X" : (index==1 ? "Y" : (index==2 ? "Z" : null)));
   }

   public void fromModelScaler() {
      ModelScaler modelScaler = scaleToolModel.getScaleTool().getModelScaler();

      // Initialize assuming nonuniform
      for(int i=0; i<size(); i++) set(i, new BodyScaleFactors());

      for(int order=0; order<modelScaler.getScalingOrder().getSize(); order++) {
         //------------------------------------------------------------------
         // Measurement-based scaling
         //------------------------------------------------------------------
         if(modelScaler.getScalingOrder().getitem(order).equals("measurements")) {

            for(int i=0; i<modelScaler.getScalingOrder().getSize(); i++) {
               if(modelScaler.getScalingOrder().getitem(i).equals("measurements")) {
                  MeasurementSet measurementSet = modelScaler.getMeasurementSet();
                  for(int j=0; j<measurementSet.getSize(); j++) {
                     Measurement meas = modelScaler.getMeasurementSet().get(j);
                     if(!meas.getApply()) continue; // TODO -- we should somehow store this in our internal data structure so the user can see the fact that the measurement exists
                     for(int k=0; k<meas.getBodyScaleSet().getSize(); k++) {
                        BodyScale bodyScale = meas.getBodyScaleSet().get(k);
                        Integer bodyIndex = mapBodyNameToIndex.get(bodyScale.getName());
                        if(bodyIndex!=null) {
                           BodyScaleFactors scaleFactors = get(bodyIndex);
                           scaleFactors.useManual = false;
                           ArrayStr axisNames = bodyScale.getAxisNames();
                           for(int l=0; l<axisNames.getSize(); l++) {
                              int index = axisToIndex(axisNames.getitem(l));
                              if(index>=0) scaleFactors.measurements[index] = j;
                           }
                        } else { 
                           System.out.println("ERROR: Body '"+bodyScale.getName()+"' referred to by measurement '"+meas.getName()+"' doesn't exist!"); 
                        }
                     }
                  }
               }
            }
         } 
         //------------------------------------------------------------------
         // Manual scaling
         //------------------------------------------------------------------
         else if(modelScaler.getScalingOrder().getitem(order).equals("manualScale")) {
            for(int i=0; i<modelScaler.getScaleSet().getSize(); i++) {
               Scale scale = modelScaler.getScaleSet().get(i);
               Integer bodyIndex = mapBodyNameToIndex.get(scale.getSegmentName());
               if(bodyIndex!=null) {
                  BodyScaleFactors scaleFactors = get(bodyIndex);
                  scaleFactors.useManual = scale.getApply();
                  Vec3 scales = new Vec3();
                  scale.getScaleFactors(scales);
                  for(int j=0; j<3; j++) scaleFactors.manualScales[j] = scales.get(j);
               } else {
                  System.out.println("ERROR: Body '"+scale.getSegmentName()+"' referred to by scale '"+scale.getName()+"' doesn't exist!");
               }
            }
         } else {
            System.out.println("ERROR: Unrecognized string '"+modelScaler.getScalingOrder().getitem(order)+" in scaling order property");
         }
      }

      //print();
   }

   public void toModelScaler() {
      ModelScaler modelScaler = scaleToolModel.getScaleTool().getModelScaler();

      ArrayStr array = new ArrayStr();

      //------------------------------------------------------------------
      // Measurement-based scaling
      //------------------------------------------------------------------
      // The measurements are up to date, but what they're applied to isn't
      MeasurementSet measurementSet = modelScaler.getMeasurementSet();
      if(measurementSet.getSize()>0) array.append("measurements");
      for(int i=0; i<measurementSet.getSize(); i++) {
         measurementSet.get(i).getBodyScaleSet().setSize(0);
         measurementSet.get(i).setApply(false);
      }
      for(int i=0; i<size(); i++) {
         for(int j=0; j<3; j++) {
            if(get(i).measurements[j]>=0)
               addToMeasurement(get(i).measurements[j], bodySet.get(i).getName(), j);
         }
      }

      //------------------------------------------------------------------
      // Manual scaling
      //------------------------------------------------------------------
      // Clear the scale set
      modelScaler.getScaleSet().setSize(0);
      for(int i=0; i<size(); i++) {
         // If using manual scales, we'll add it to the scale set with apply==true
         // Otherwise, if it's not the default {1,1,1} scale we'll write it out with apply==false so that 
         // the user can recover these values when they load the setup file
         if(get(i).useManualScale() || !get(i).manualScalesAreIdentity()) {
            Scale scale = new Scale();
            scale.setSegmentName(bodySet.get(i).getName());
            scale.setApply(get(i).useManualScale());
            Vec3 scales=new Vec3();
            for(int j=0; j<3; j++){ 
                scales.set(j, get(i).manualScales[j]);
            }
            scale.setScaleFactors(scales);
            modelScaler.getScaleSet().cloneAndAppend(scale);
         }
      }
      if(modelScaler.getScaleSet().getSize()>0) array.append("manualScale");

      modelScaler.setScalingOrder(array);
   }

   //------------------------------------------------------------------------
   // Private utility functions
   //------------------------------------------------------------------------

   // axis = 0 (X), 1(Y), 2(Z), or -1 for all axes
   // TODO: make more efficient
   private void addToMeasurement(int index, String bodyName, int axis) {
      ModelScaler modelScaler = scaleToolModel.getScaleTool().getModelScaler();
      Measurement meas = modelScaler.getMeasurementSet().get(index);
      meas.setApply(true);
      BodyScaleSet bodyScaleSet = meas.getBodyScaleSet();
      int bodyScaleIndex = bodyScaleSet.getIndex(bodyName);
      BodyScale bodyScale = null;
      if(bodyScaleIndex < 0) {
         bodyScale = new BodyScale(); // Create it on C++ side
         bodyScale.setName(bodyName);
         modelScaler.getMeasurementSet().get(index).getBodyScaleSet().cloneAndAppend(bodyScale);
         // get refrence to clone to proceed
         bodyScale =  modelScaler.getMeasurementSet().get(index).getBodyScaleSet().get(bodyName);
      } else 
          bodyScale = bodyScaleSet.get(bodyScaleIndex);
      if(axis==0 || axis==-1) bodyScale.getAxisNames().append("X");
      if(axis==1 || axis==-1) bodyScale.getAxisNames().append("Y");
      if(axis==2 || axis==-1) bodyScale.getAxisNames().append("Z");
      
   }
}

//==================================================================
// ScaleToolModel
//==================================================================
public class ScaleToolModel extends Observable implements Observer {

    /**
     * @return the originalModel
     */
    public Model getOriginalModel() {
        return originalModel;
    }
   //========================================================================
   // ScaleToolWorker
   //========================================================================
   class ScaleToolWorker extends SwingWorker {
      private ProgressHandle progressHandle = null;
      boolean result = false;
      private Model processedModel = null;
      OpenSimContext processedModelContext=null;
      
      ScaleToolWorker() throws Exception {
         updateScaleTool();
 
         progressHandle = ProgressHandleFactory.createHandle("Executing scaling...",
                              new Cancellable() {
                                 public boolean cancel() {
                                    result = false;
                                    finished();
                                    return true;
                                 }
                              });
         progressHandle.start();
         // Crash here
         processedModel = new Model(unscaledModel);
         processedModel.setName(scaleTool.getName());
         processedModel.setInputFileName("");
         processedModel.setOriginalModelPathFromModel(unscaledModel); // important to keep track of the original path so bone loading works
         //processedModel.setup();

         setExecuting(true);
      }

      // TODO: we can't actually interrupt model scaler / marker placer part way...
      public void interrupt() {
         //OpenSim23 if(getMarkerPlacerEnabled() && scaleTool.getMarkerPlacer().getIKTrial()!=null)
         //OpenSim23    scaleTool.getMarkerPlacer().getIKTrial().interrupt();
      }

      public Object construct() {
         result = true;

         // TODO: use the Storage's we've already read in rather than reading them againag
         if(getModelScalerEnabled()) {
            System.out.println("ModelScaler...");
            processedModelContext = OpenSimDB.getInstance().createContext(processedModel, false);
            // Pass empty path as path to subject, since we already have the measurement trial as an absolute path
            //String t=unscaledModel.getFilePath();
            scaleTool.getMarkerPlacer().setOutputModelFileName(scaleTool.getMarkerPlacer().getOutputModelFileName());

            if(!processedModelContext.processModelScale(scaleTool.getModelScaler(), processedModel, "", scaleTool.getSubjectMass())) {
               result = false;
               return this;
            }
         }
         
         if(getMarkerPlacerEnabled()) {
            // Pass empty path as path to subject, since we already have the static trial as an absolute path
            if(!processedModelContext.processModelMarkerPlacer(scaleTool.getMarkerPlacer(), processedModel, "")) {
               result = false;
               return this;
            }
         }

         return this;
      }

      @Override
      public void finished() {
         progressHandle.finish();

         if(result) {
            OpenSimDB.getInstance().replaceModel(scaledModel, processedModel, processedModelContext);
            scaledModel = processedModel;
            if(OpenSimDB.getInstance().getModelGuiElements(scaledModel)!=null)
               OpenSimDB.getInstance().getModelGuiElements(scaledModel).setUnsavedChangesFlag(true);
               
            if(getMarkerPlacerEnabled() && scaleTool.getMarkerPlacer().getOutputStorage()!=null) {
               Storage motion = new Storage(scaleTool.getMarkerPlacer().getOutputStorage());
               motion.setName("static pose");
               MotionsDB.getInstance().addMotion(scaledModel, motion, null);
            }
            resetModified();
         }

         setExecuting(false);
         if (cleanupAfterExecuting)
            cleanup();
         processedModel = null;
         worker = null;
      }
   }
   private ScaleToolWorker worker = null;
   //========================================================================
   // END ScaleToolWorker
   //========================================================================


   enum Operation { AllDataChanged, SubjectDataChanged, MarkerSetChanged, MeasurementSetChanged, ModelScalerDataChanged, MarkerPlacerDataChanged, ExecutionStateChanged };

   private ScaleTool scaleTool = null;
   private Model originalModel = null;
   private MarkerSet originalMarkerSet = null;
   private Model unscaledModel = null;  //Working copy to scale inplace
   private Model scaledModel = null;

   private boolean modifiedSinceLastExecute = true;

   private OptionalFile extraMarkerSetFile = new OptionalFile();
   private MarkerSet extraMarkerSet = null;

   private Hashtable<String,Boolean> markerExistsInModel = new Hashtable<String,Boolean>();
   private Hashtable<String,Boolean> markerExistsInMeasurementTrial = new Hashtable<String,Boolean>();

   private OptionalFile measurementTrialFile = new OptionalFile();
   private MarkerData measurementTrial = null;
   private Vector<Double> measurementValues = null;

   private BodySetScaleFactors bodySetScaleFactors;
   
   private IKCommonModel ikCommonModel; // Stores marker placer stuff that's also common to IKTool
   private boolean executing = false;
   private boolean cleanupAfterExecuting = false;  // Keep track if cleaning up needs to be done on execution finish vs. dialog close

   public ScaleToolModel(Model originalModel) throws IOException {
      // Store original model; create copy of the original model as our unscaled model (i.e. the model we'll scale)
      this.originalModel = originalModel;
      unscaledModel = new Model(originalModel);
      unscaledModel.setInputFileName("");
      unscaledModel.setOriginalModelPathFromModel(originalModel); // important to keep track of the original path so bone loading works
      //unscaledModel.setup();
      originalMarkerSet = new MarkerSet(unscaledModel.getMarkerSet());

      // Create scale tool
      scaleTool = new ScaleTool();
      setName(originalModel.getName()+"-scaled"); // initialize name of output (scaled) model
      setMass(getModelMass(originalModel)); // initialize mass to the subject's current mass

      measurementValues = new Vector<Double>();

      bodySetScaleFactors = new BodySetScaleFactors(this, unscaledModel.getBodySet());

      ikCommonModel = new IKCommonModel(unscaledModel);
      ikCommonModel.addObserver(this);
   }

   // Simple accessors
   public Model getUnscaledModel() { return unscaledModel; }
   public ScaleTool getScaleTool() { return scaleTool; }
   public GenericModelMaker getGenericModelMaker() { return scaleTool.getGenericModelMaker(); }
   public ModelScaler getModelScaler() { return scaleTool.getModelScaler(); }
   public MarkerPlacer getMarkerPlacer() { return scaleTool.getMarkerPlacer(); }
   public IKCommonModel getIKCommonModel() { return ikCommonModel; }

   //------------------------------------------------------------------------
   // Utilities for running/canceling tool
   //------------------------------------------------------------------------
   
   private void updateScaleTool() {
      scaleTool.setPrintResultFiles((scaleTool.getMarkerPlacer().getOutputModelFileName()!=""));
      scaleTool.getGenericModelMaker().setMarkerSetFileName(extraMarkerSetFile.toProperty());
      scaleTool.getModelScaler().setMarkerFileName(measurementTrialFile.toProperty());

      bodySetScaleFactors.toModelScaler();
      ikCommonModel.toMarkerPlacer(scaleTool.getMarkerPlacer());
   }

   public void execute() {
      if(isModified() && worker==null) {
         try {
            worker = new ScaleToolWorker();
            worker.start();
         } catch (Exception ex) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(ex.getMessage(), NotifyDescriptor.ERROR_MESSAGE));
            worker = null;
         }
      }
   }

   // TODO: may need to use locks and such to ensure that worker doesn't get set to null (by IKToolWorker.finished()) in between worker!=null check and worker.interrupt()
   // But I think both will typically run on the swing thread so probably safe
   public void interrupt(boolean promptToKeepPartialResult) {
      if(worker!=null) worker.interrupt();
   }

   public void cancel() {
      interrupt(false);
      /*
      if(scaledModel!=null) OpenSimDB.getInstance().removeModel(scaledModel);
      scaledModel = null;*/
   }

   //------------------------------------------------------------------------
   // Handle updates in the IK Task Set
   //------------------------------------------------------------------------
   public void update(Observable observable, Object obj) {
      if(observable==ikCommonModel) setModified(Operation.MarkerPlacerDataChanged);
   }

   //------------------------------------------------------------------------
   // Execution status
   //------------------------------------------------------------------------

   private void setExecuting(boolean executing) {
      if(this.executing != executing) {
         this.executing = executing;
         setChanged();
         notifyObservers(Operation.ExecutionStateChanged);
      }
   }
   public boolean isExecuting() {
      return executing;
   }

   //------------------------------------------------------------------------
   // Modified flag
   //------------------------------------------------------------------------

   private void setModified(Operation change) {
      modifiedSinceLastExecute = true;
      setChanged(); // need to call this before calling notifyObservers
      notifyObservers(change);
   }
   private void resetModified() {
      modifiedSinceLastExecute = false;
   }
   public boolean isModified() {
      return modifiedSinceLastExecute;
   }

   //------------------------------------------------------------------------
   // Validation
   //------------------------------------------------------------------------

   private boolean getBodyScaleFactorsValid(BodyScaleFactors scaleFactors) {
      if(!scaleFactors.useManualScale()) {
         for(int i=0; i<3; i++)
            if(scaleFactors.measurements[i]!=-1 && getMeasurementValue(scaleFactors.measurements[i])==null)
               return false;
      }
      return true;
   }

   private boolean getBodySetScaleFactorsValid() {
      for(int i=0; i<getBodySetScaleFactors().size(); i++)
         if(!getBodyScaleFactorsValid(getBodySetScaleFactors().get(i)))
            return false;
      return true;
   }

   private boolean getModelScalerValid() { return !getModelScalerEnabled() || getBodySetScaleFactorsValid(); }
   private boolean getMarkerPlacerValid() { return !getMarkerPlacerEnabled() || ikCommonModel.isValid(); }

   public boolean isValid() {
      return (!getUseExtraMarkerSet() || getExtraMarkerSetValid()) && getModelScalerValid() && getMarkerPlacerValid();
   }

   //------------------------------------------------------------------------
   // Model Properties
   //------------------------------------------------------------------------

   public void setName(String name) {
      if(!scaleTool.getName().equals(name)) {
         scaleTool.setName(name);
         setModified(Operation.SubjectDataChanged);
      }
   }
   public String getName() {
      return scaleTool.getName();
   }

   public void setMass(double mass) {
      if(scaleTool.getSubjectMass() != mass) {
         scaleTool.setSubjectMass(mass);
         setModified(Operation.SubjectDataChanged);
      }
   }
   public double getMass() {
      return scaleTool.getSubjectMass();
   }

   //------------------------------------------------------------------------
   // Filename effectively null if it's null, or "", or "Unassigned"
   //------------------------------------------------------------------------
   private boolean fileNameEffectivelyNull(String fileName) {
      return fileName==null || fileName.equals("") || fileName.equalsIgnoreCase("Unassigned");
   }
   private String getFileName(String fileName) {
      return FileUtils.effectivelyNull(fileName) ? "" : fileName;
   }

   //------------------------------------------------------------------------
   // Marker Set Utilities
   //------------------------------------------------------------------------

   private boolean loadExtraMarkerSet(boolean recompute) {
      boolean success = true;
      extraMarkerSet = null;
      if(extraMarkerSetFile.isValid()) {
         try {
            extraMarkerSet = new MarkerSet(unscaledModel, extraMarkerSetFile.fileName);
         } catch (IOException ex) {
            extraMarkerSet = null;
            success = false;
         }
      }
      
      resetMarkers(); // reset markers in our unscaled model
      if(recompute) recomputeMeasurements();
      ikCommonModel.getIKMarkerTasksModel().markerSetChanged();
      return success;
   }

   private void resetMarkers() {
      OpenSimContext context = OpenSimDB.getInstance().createContext(unscaledModel, false); //Call(1) 
      //context.updateMarkerSet(originalMarkerSet);
      if(extraMarkerSet!=null)
         unscaledModel.updateMarkerSet(extraMarkerSet);
      context = OpenSimDB.getInstance().createContext(unscaledModel, true);
      // Update hash table
      markerExistsInModel.clear();
      for(int i=0; i<getMarkerSet().getSize(); i++) markerExistsInModel.put(getMarkerSet().get(i).getName(),(Boolean)true);
   }

   public boolean setExtraMarkerSetFileName(String fileName) {
      extraMarkerSetFile.fileName = fileName;
      boolean success = loadExtraMarkerSet(true);
      setModified(Operation.MarkerSetChanged);
      return success;
   }
   public String getExtraMarkerSetFileName() {
      return extraMarkerSetFile.fileName;
   }
   public MarkerSet getExtraMarkerSet() {
      return extraMarkerSet;
   }
   public boolean getExtraMarkerSetValid() {
      return extraMarkerSet!=null;
   }

   public boolean getUseExtraMarkerSet() {
      return extraMarkerSetFile.enabled;
   }
   public void setUseExtraMarkerSet(boolean useIt) {
      if(extraMarkerSetFile.enabled != useIt) {
         extraMarkerSetFile.enabled = useIt;
         loadExtraMarkerSet(true);
         setModified(Operation.MarkerSetChanged);
      }
   }

   public MarkerSet getMarkerSet() {
      return getUnscaledModel().getMarkerSet();
   }

   public boolean getMarkerExistsInModel(String markerName) {
      return markerExistsInModel.get(markerName)!=null;
   }

   //------------------------------------------------------------------------
   // Model Scaler
   //------------------------------------------------------------------------

   // Model scaler enabled
   public void setModelScalerEnabled(boolean enabled) {
      if(getModelScalerEnabled() != enabled) {
         scaleTool.getModelScaler().setApply(enabled);
         setModified(Operation.ModelScalerDataChanged);
      }
   }
   public boolean getModelScalerEnabled() {
      return scaleTool.getModelScaler().getApply();
   }

   // Preserve mass distribution
   public void setPreserveMassDistribution(boolean enabled) {
      if(scaleTool.getModelScaler().getPreserveMassDist() != enabled) {
         scaleTool.getModelScaler().setPreserveMassDist(enabled);
         setModified(Operation.ModelScalerDataChanged);
      }
   }
   public boolean getPreserveMassDistribution() {
      return scaleTool.getModelScaler().getPreserveMassDist();
   }

   private boolean loadMeasurementTrial(boolean resetTimeRange, boolean recompute) {
      boolean success = true;
      measurementTrial = null; 
      if(measurementTrialFile.isValid()) {
         try {
            measurementTrial = new MarkerData(measurementTrialFile.fileName);
            measurementTrial.convertToUnits(getUnscaledModel().getLengthUnits());
         } catch (IOException ex) {
            measurementTrial = null;
            success = false;
         }
      }
      if(resetTimeRange && measurementTrial!=null) setMeasurementTrialTimeRange(measurementTrial.getTimeRange());
      if(recompute) recomputeMeasurements();

      // Update hash table
      markerExistsInMeasurementTrial.clear();
      if(measurementTrial!=null) {
         for(int i=0; i<measurementTrial.getMarkerNames().getSize(); i++)
            markerExistsInMeasurementTrial.put(measurementTrial.getMarkerNames().getitem(i), (Boolean)true);
      }

      return success;
   }

   // Measurement trial file name
   public boolean setMeasurementTrialFileName(String fileName) {
      measurementTrialFile.fileName = fileName;      
      boolean success = loadMeasurementTrial(true, true);
      setModified(Operation.ModelScalerDataChanged);
      return success;
   }
   public String getMeasurementTrialFileName() {
      return measurementTrialFile.fileName;
   }
   public MarkerData getMeasurementTrial() {
      return measurementTrial;
   }
   public boolean getMeasurementTrialValid() {
      return measurementTrial!=null;
   }

   public void setMeasurementTrialEnabled(boolean enabled) {
      if(measurementTrialFile.enabled != enabled) {
         measurementTrialFile.enabled = enabled;
         loadMeasurementTrial(false,true);
         setModified(Operation.ModelScalerDataChanged);
      }
   }
   public boolean getMeasurementTrialEnabled() {
      return measurementTrialFile.enabled;
   }

   public boolean getMarkerExistsInMeasurementTrial(String markerName) {
      return markerExistsInMeasurementTrial.get(markerName)!=null;
   }

   // Measurement trial time range
   public void setMeasurementTrialTimeRange(double[] timeRange) {
      clampMeasurementTrialTimeRange(timeRange);
      double[] oldRange = getMeasurementTrialTimeRange();
      if(oldRange[0] != timeRange[0] || oldRange[1] != timeRange[1]) {
         ArrayDouble array = new ArrayDouble();
         array.append(timeRange[0]);
         array.append(timeRange[1]);
         scaleTool.getModelScaler().setTimeRange(array);
         recomputeMeasurements();
         setModified(Operation.ModelScalerDataChanged);
      }
   }
   public double[] getMeasurementTrialTimeRange() {
      ArrayDouble array = scaleTool.getModelScaler().getTimeRange();
      return new double[]{array.getitem(0), array.getitem(1)};
   }

   private boolean clampMeasurementTrialTimeRange(double[] timeRange) {
      boolean clamped = false;
      if(measurementTrial!=null) {
         if(timeRange[0] < measurementTrial.getStartFrameTime()) { timeRange[0] = measurementTrial.getStartFrameTime(); clamped = true; }
         if(timeRange[1] > measurementTrial.getLastFrameTime()) { timeRange[1] = measurementTrial.getLastFrameTime(); clamped = true; }
      }
      return clamped;
   }

   //------------------------------------------------------------------------
   // Measurement Set (focuses on the marker pairs in the measurements, not the bodies which the measurement is applied to)
   //------------------------------------------------------------------------

   private ScaleSet createIdentityScaleSet() {
      BodySet bodySet = getUnscaledModel().getBodySet();
      ScaleSet scaleSet = new ScaleSet();
      Vec3 identityScale = new Vec3(1.);
      for(int i=0; i<bodySet.getSize(); i++) {
         Scale scale = new Scale();
         scale.setSegmentName(bodySet.get(i).getName());
         scale.setScaleFactors(identityScale);
         scale.setApply(true);
         scaleSet.adoptAndAppend(Scale.safeDownCast(scale.clone()));
      }
      return scaleSet;
   }

   private void resetMeasurementValues() {
      MeasurementSet measurementSet = scaleTool.getModelScaler().getMeasurementSet();
      measurementValues = new Vector<Double>(measurementSet.getSize());
      for(int i=0; i<measurementSet.getSize(); i++) measurementValues.add(null);
   }

   private void recomputeMeasurements() {
      if(measurementTrial==null) resetMeasurementValues();
      else {
         MeasurementSet measurementSet = scaleTool.getModelScaler().getMeasurementSet();
         for(int i=0; i<measurementSet.getSize(); i++) recomputeMeasurement(i);
      }
   }

   private void recomputeMeasurement(int i) {
      if(measurementTrial==null) return;
      MeasurementSet measurementSet = scaleTool.getModelScaler().getMeasurementSet();
      OpenSimContext context = OpenSimDB.getInstance().getContext(getUnscaledModel());
      double scaleFactor = context.computeMeasurementScaleFactor(scaleTool.getModelScaler(), getUnscaledModel(), measurementTrial, measurementSet.get(i));
      if(OpenSimContext.isNaN(scaleFactor)) measurementValues.set(i,null);
      else measurementValues.set(i,new Double(scaleFactor));
   }

   public MeasurementSet getMeasurementSet() {
      return scaleTool.getModelScaler().getMeasurementSet();
   }

   String getMeasurementName(int i) {
      return getMeasurementSet().get(i).getName();
   }

   Double getMeasurementValue(int i) {
      return measurementValues.get(i);
   }

   void setMeasurementName(int i, String name) {
      MeasurementSet measurementSet = getMeasurementSet();
      if(i>=measurementSet.getSize()) return; // This does seem to happen if you add and remove measurements quickly in the measurement set panel
                                              // Seems to be because the EditMeasurementNameActionAndFocusListener can process
                                              // the focusLost event even after we've removed that measurement.
      if(!measurementSet.get(i).getName().equals(name)) {
         // Update name
         measurementSet.get(i).setName(name);
         // Fire event
         setModified(Operation.MeasurementSetChanged); // MeasurementRenamed, i
      }
   }

   void setMarkerPairMarker(int i, int pairIndex, int markerIndex, String markerName) {
      MeasurementSet measurementSet = getMeasurementSet();
      assert(0 <= pairIndex && pairIndex < measurementSet.get(i).getMarkerPairSet().getSize());
      assert(markerIndex == 0 || markerIndex == 1);
      measurementSet.get(i).getMarkerPairSet().get(pairIndex).setMarkerName(markerIndex,markerName);
      // Recompute
      recomputeMeasurement(i);
      // Fire event
      setModified(Operation.MeasurementSetChanged); // MeasurementChanged, i
   }

   void addMarkerPair(int i) {
      MeasurementSet measurementSet = getMeasurementSet();
      MarkerPair pair = new MarkerPair("Unassigned", "Unassigned");
      measurementSet.get(i).getMarkerPairSet().insert(0,MarkerPair.safeDownCast(pair.clone()));
      // Recompute
      recomputeMeasurement(i);
      // Fire event
      setModified(Operation.MeasurementSetChanged); // MeasurementChanged, i
   }

   void removeMarkerPair(int i, int pairIndex) {
      MeasurementSet measurementSet = getMeasurementSet();
      measurementSet.get(i).getMarkerPairSet().remove(pairIndex);
      // Recompute
      recomputeMeasurement(i);
      // Fire event
      setModified(Operation.MeasurementSetChanged); // MeasurementChanged, i
   }

   void addMeasurement(String name) {
      MeasurementSet measurementSet = getMeasurementSet();
      Measurement measurement = new Measurement();
      measurement.setName(name);
      measurementSet.adoptAndAppend(Measurement.safeDownCast(measurement.clone()));
      // Update parallel measurementValues array
      measurementValues.add(null);
      // Fire event
      setModified(Operation.MeasurementSetChanged); // MeasurementAdded, measurementSet.getSize();
   }

   void removeMeasurement(int i) {
      MeasurementSet measurementSet = getMeasurementSet();
      measurementSet.remove(i);
      // Update indices of all scales that refer to index i or higher
      bodySetScaleFactors.removeMeasurement(i);
      measurementValues.remove(i);
      // Fire event
      setModified(Operation.MeasurementSetChanged); // MeasurementDeleted, i
   }

   //------------------------------------------------------------------------
   // BodyScaleFactors
   //------------------------------------------------------------------------

   public BodySetScaleFactors getBodySetScaleFactors() {
      return bodySetScaleFactors;
   }

   public void bodySetScaleFactorsModified() {
      setModified(Operation.ModelScalerDataChanged);
   }

   //------------------------------------------------------------------------
   // Marker Placer
   //------------------------------------------------------------------------
   // Marker placer enabled
   public void setMarkerPlacerEnabled(boolean enabled) {
      if(getMarkerPlacerEnabled() != enabled) {
         scaleTool.getMarkerPlacer().setApply(enabled);
         setModified(Operation.MarkerPlacerDataChanged);
      }
   }
   public boolean getMarkerPlacerEnabled() {
      return  scaleTool.getMarkerPlacer().getApply();
   }

   public boolean getMoveModelMarkers() { return scaleTool.getMarkerPlacer().getMoveModelMarkers(); }
   public void setMoveModelMarkers(boolean move) { 
      if(getMoveModelMarkers() != move) {
         scaleTool.getMarkerPlacer().setMoveModelMarkers(move);
         setModified(Operation.MarkerPlacerDataChanged);
      }
   }

   //------------------------------------------------------------------------
   // Load/Save Settings
   //------------------------------------------------------------------------

   private void relativeToAbsolutePaths(String parentFileName) {
      String parentDir = (new File(parentFileName)).getParent();
      scaleTool.getGenericModelMaker().setMarkerSetFileName(FileUtils.makePathAbsolute(scaleTool.getGenericModelMaker().getMarkerSetFileName(),parentDir));
      scaleTool.getModelScaler().setMarkerFileName(FileUtils.makePathAbsolute(scaleTool.getModelScaler().getMarkerFileName(),parentDir));
      scaleTool.getMarkerPlacer().setStaticPoseFileName(FileUtils.makePathAbsolute(scaleTool.getMarkerPlacer().getStaticPoseFileName(),parentDir));
      scaleTool.getMarkerPlacer().setCoordinateFileName(FileUtils.makePathAbsolute(scaleTool.getMarkerPlacer().getCoordinateFileName(),parentDir));
   }

   private void AbsoluteToRelativePaths(String parentFileName) {
      String parentDir = (new File(parentFileName)).getParent();
      scaleTool.getGenericModelMaker().setMarkerSetFileName(FileUtils.makePathRelative(scaleTool.getGenericModelMaker().getMarkerSetFileName(),parentDir));
      scaleTool.getModelScaler().setMarkerFileName(FileUtils.makePathRelative(scaleTool.getModelScaler().getMarkerFileName(),parentDir));
      scaleTool.getMarkerPlacer().setStaticPoseFileName(FileUtils.makePathRelative(scaleTool.getMarkerPlacer().getStaticPoseFileName(),parentDir));
      scaleTool.getMarkerPlacer().setCoordinateFileName(FileUtils.makePathRelative(scaleTool.getMarkerPlacer().getCoordinateFileName(),parentDir));
   }

   public boolean loadSettings(String fileName) {
      // TODO: set current working directory before trying to read it?
      ScaleTool newScaleTool = null;
      try {
         newScaleTool = new ScaleTool(fileName);
      } catch (IOException ex) {
         ErrorDialog.displayIOExceptionDialog("Error loading file","Could not load "+fileName,ex);
         return false;
      }
      scaleTool = newScaleTool;
      relativeToAbsolutePaths(fileName);

      // reset some things in the scale tool which we will not use
      scaleTool.getGenericModelMaker().setModelFileName("Unassigned"); // TODO: what should we really set this to?

      // keep internal data in sync
      modifiedSinceLastExecute = true;

      // marker set
      extraMarkerSet = null;
      extraMarkerSetFile.fromProperty(scaleTool.getGenericModelMaker().getMarkerSetFileName());
      loadExtraMarkerSet(false); // will recompute measurements below

      // measurement set and scale factors
      resetMeasurementValues();
      bodySetScaleFactors.fromModelScaler();

      // measurement trial
      measurementTrial = null;
      measurementTrialFile.fromProperty(scaleTool.getModelScaler().getMarkerFileName());
      loadMeasurementTrial(false,false); // will recompute measurements below
      recomputeMeasurements();

      ikCommonModel.fromMarkerPlacer(scaleTool.getMarkerPlacer());

      setModified(Operation.AllDataChanged);
      return true;
   }

   public boolean saveSettings(String fileName) {
      String fullFilename = FileUtils.addExtensionIfNeeded(fileName, ".xml");
      /*
      XMLExternalFileChooserHelper helper = new XMLExternalFileChooserHelper(fullFilename);
      helper.addObject(scaleTool.getModelScaler().getMeasurementSet(), "Measurement Set");
      helper.addObject(scaleTool.getModelScaler().getScaleSet(), "Scale Set (manual scale factors)");
      helper.addObject(scaleTool.getMarkerPlacer().getIKTaskSet(), "IK Task Set (for static pose)");
      if(!helper.promptUser()) return false;*/
      scaleTool.getModelScaler().getMeasurementSet().setInlined(true);
      scaleTool.getModelScaler().getScaleSet().setInlined(true);
      scaleTool.getMarkerPlacer().getIKTaskSet().setInlined(true);
      updateScaleTool();
      AbsoluteToRelativePaths(fullFilename);
      scaleTool.print(fullFilename);
      relativeToAbsolutePaths(fullFilename);
      return true;
   }

   //------------------------------------------------------------------------
   // Utility
   //------------------------------------------------------------------------

   public static double getModelMass(Model model) {
      BodySet bodySet = model.getBodySet();
      double mass = 0;
      for(int i=0; i<bodySet.getSize(); i++)
         mass += bodySet.get(i).getMass();
      return mass;
   }
   // Release C++ resources on exit (either dialog closing or execution finish whichever is later
   void cleanup() {
      if (isExecuting()){
         cleanupAfterExecuting = true;
      }
      else{
         ikCommonModel.deleteObservers();
         scaleTool = null;
         measurementTrial = null;
         extraMarkerSet = null;
         System.gc();
      }
   }
}
