/* -------------------------------------------------------------------------- *
 * OpenSim: IMUIKToolModel.java                                                
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
import java.util.Observable;
import java.util.Observer;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Cancellable;
import org.opensim.modeling.IMUInverseKinematicsTool;
import org.opensim.modeling.InverseKinematicsTool;
import org.opensim.modeling.InterruptCallback;
import org.opensim.modeling.MarkerData;
//import org.opensim.modeling.InterruptingIntegCallback;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimContext;
import org.opensim.modeling.OrientationWeight;
import org.opensim.modeling.OrientationWeightSet;
import org.opensim.modeling.StdVectorDouble;
import org.opensim.modeling.StdVectorString;
import org.opensim.modeling.Storage;
import org.opensim.modeling.TimeSeriesTableQuaternion;
import org.opensim.modeling.Vec3;
import org.opensim.view.motions.MotionsDB;
import org.opensim.swingui.SwingWorker;
import org.opensim.tracking.tools.SimulationDB;
import org.opensim.utils.ErrorDialog;
import org.opensim.utils.FileUtils;
import org.opensim.view.motions.JavaMotionDisplayerCallback;
import org.opensim.view.pub.OpenSimDB;

//==================================================================
// IKToolModel
//==================================================================
// Only deals with a single (the first) IKTrial in the IKTrialSet of IKTool
public class IMUIKToolModel extends Observable implements Observer {

    /**
     * @return the fullOutputFileName
     */
    public String getFullOutputFileName() {
        return fullOutputFileName;
    }

    /**
     * @param fullOutputFileName the fullOutputFileName to set
     */
    public void setFullOutputFileName(String fullOutputFileName) {
        this.fullOutputFileName = fullOutputFileName;
    }

    /**
     * @return the sensorData
     */
    public TimeSeriesTableQuaternion getSensorData() {
        if (sensorData == null && sensorOrientationsFileName!="")
            sensorData = new TimeSeriesTableQuaternion(sensorOrientationsFileName);
        return sensorData;
    }

    double[] getTimeRange() {
        return timeRange;
    }

    void setSensorDataFileName(String fileName) {
        if (fileName != sensorOrientationsFileName){
            sensorOrientationsFileName = fileName;
            sensorData = new TimeSeriesTableQuaternion(sensorOrientationsFileName);
            StdVectorDouble timeColumn = sensorData.getIndependentColumn();
            timeRange[0] = timeColumn.get(0);
            timeRange[1] = timeColumn.get((int)timeColumn.size()-1);
            setModified(Operation.AllDataChanged);
        }
    }

    void setTimeRange(double[] newTimeRange) {
      //clampTimeRangeAgainstMarkerData(newTimeRange);
      if(timeRange[0] != newTimeRange[0] || timeRange[1] != newTimeRange[1]) {
         timeRange = newTimeRange;
      }
    }

    void setReportErrors(boolean selected) {
        this.reportErrors=selected;
        setModified(Operation.AllDataChanged);
    }

    private void populateOrientationWeights() {
        StdVectorString lbls=getSensorData().getColumnLabels();
        if (orientation_weightset==null)
            orientation_weightset = new OrientationWeightSet();
        for (int i=0; i<lbls.size(); i++){
            orientation_weightset.cloneAndAppend(new OrientationWeight(lbls.get(i), 1.0));
        }
    }
    
   //========================================================================
   // IMUIKToolWorker
   //========================================================================
   class IMUIKToolWorker extends SwingWorker {
      private ProgressHandle progressHandle = null;
      private JavaMotionDisplayerCallback animationCallback = null;
      private InterruptCallback interruptingCallback = null;
      boolean result = false;
      boolean promptToKeepPartialResult = true;
      boolean cleanup=true;
      Model modelCopy = new Model(getOriginalModel());
      final OpenSimContext context=OpenSimDB.getInstance().getContext(getOriginalModel());

      
      IMUIKToolWorker() throws Exception {
         // Give the thread a nudge so that we're not much slower than command line'
         setPriority(Thread.MAX_PRIORITY);
         
         modelCopy.initSystem();
         updateIKTool();

         // Operate on a copy of the model -- this way if users play with parameters in the GUI it won't affect the model we're actually computing on
         imuIkTool.setModel(modelCopy);
         // Make no motion be currently selected (so model doesn't have extraneous ground forces/experimental markers from
         // another motion show up on it)
         MotionsDB.getInstance().clearCurrent();

         // Initialize progress bar, given we know the number of frames to process
         double startTime = imuIkTool.getStartTime();
         double endTime = imuIkTool.getEndTime();
         progressHandle = ProgressHandleFactory.createHandle("Executing Inverse Kinematics with IMU data ...",
                              new Cancellable() {
                                 public boolean cancel() {
                                    interrupt(true);
                                    SimulationDB.getInstance().fireToolFinish();
                                    return true;
                                 }
                              });

         // Animation callback will update the display during IK solve
         animationCallback = new JavaMotionDisplayerCallback(modelCopy, getOriginalModel(), null, progressHandle, false);
         modelCopy.addAnalysis(animationCallback);
         animationCallback.setStepInterval(1);
         animationCallback.startProgressUsingTime(startTime, endTime);

         // Do this manouver (there's gotta be a nicer way) to create the object so that C++ owns it and not Java (since 
         // removeIntegCallback in finished() will cause the C++-side callback to be deleted, and if Java owned this object
         // it would then later try to delete it yet again)
         interruptingCallback = new InterruptCallback(modelCopy);
         modelCopy.addAnalysis(interruptingCallback);
         setExecuting(true);
         SimulationDB.getInstance().fireToolStart();

      }

      public void interrupt(boolean promptToKeepPartialResult)  {
         this.promptToKeepPartialResult = promptToKeepPartialResult;
         if(interruptingCallback!=null){
              interruptingCallback.interrupt();
         }
      }

      public Object construct() {
         try {
           imuIkTool.run();
         }
         catch(Exception ex) {
            progressHandle.finish();
            SimulationDB.getInstance().fireToolFinish();
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message("Inverse kinematics did not complete.\n"+ex.getMessage()+"\n", NotifyDescriptor.ERROR_MESSAGE));
            worker=null;
            cleanup=false;
            modelCopy.removeAnalysis(interruptingCallback, false);
            
         }
         return this;
      }

        public void finished() {
            progressHandle.finish();
            SimulationDB.getInstance().fireToolFinish();
            if (!cleanup) {
                setExecuting(false);
                return;
            }
            // Clean up motion displayer (this is necessary!)
            animationCallback.cleanupMotionDisplayer();

            modelCopy.removeAnalysis(animationCallback, false);
            modelCopy.removeAnalysis(interruptingCallback, false);
            interruptingCallback = null;

            if (result) {
                resetModified();
            }

            boolean addMotion = true;
            if (!result) {
                boolean havePartialResult = false;//OpenSim23 imuIkTool.getOutputStorage()!=null && imuIkTool.getOutputStorage().getSize()>0;
                if (havePartialResult && promptToKeepPartialResult) {
                    Object answer = DialogDisplayer.getDefault().notify(new NotifyDescriptor.Confirmation("Inverse kinematics did not complete.  Keep partial result?", NotifyDescriptor.YES_NO_OPTION));
                    if (answer == NotifyDescriptor.NO_OPTION) {
                        addMotion = false;
                    }
                } else {
                    addMotion = false;
                }
            }
            // We don't create full state to avoid polluting results
            // This will be done internally in MotionDisplayer
            Storage ikmotion = new Storage(animationCallback.getStorage()); // Java-side copy
            ikmotion.setName("IKResults");
            updateMotion(ikmotion);

            setExecuting(false);

            modelCopy = null;
            worker = null;
        }
    }
   private IMUIKToolWorker worker = null;
   //========================================================================
   // END IMUIKToolWorker
   //========================================================================

   public enum Operation { AllDataChanged, IKTrialNameChanged, IKTaskSetChanged, ExecutionStateChanged };

   private IMUInverseKinematicsTool imuIkTool = null;
   private Model originalModel = null;
   private boolean modifiedSinceLastExecute = true;
   private Storage motion = null;
   private boolean executing = false;
   private String trialName = "ik trial";
   private boolean cleanupAfterExecuting = false;  // Keep track if cleaning up needs to be done on execution finish vs. dialog close
   private String sensorOrientationsFileName = "";
   private String fullOutputFileName = "";
   private TimeSeriesTableQuaternion sensorData = null;
   private Vec3 rotations = new Vec3(0);
   private double[] timeRange = new double[]{-1,-1};
   boolean reportErrors = false;
   private OrientationWeightSet orientation_weightset=null;
   
   public IMUIKToolModel(Model originalModel) throws IOException {
      // Store original model
      this.originalModel = originalModel;

      // Create IK tool
      imuIkTool = new IMUInverseKinematicsTool();
      //addTrialIfNecessary();
   }

   public Model getOriginalModel() { return originalModel; }
   public IMUInverseKinematicsTool getIKTool() { return imuIkTool; }
   
   //------------------------------------------------------------------------
   // Setting the motion in the model
   //------------------------------------------------------------------------
   private void updateMotion(Storage newMotion) {
      if(motion!=null) {
         MotionsDB.getInstance().closeMotion(getOriginalModel(), motion, false, false);
      }
      motion = newMotion;
      if(motion!=null) {
         MotionsDB.getInstance().addMotion(getOriginalModel(), motion, null);
         //MotionControlJPanel.getInstance().setUserTime(motion.getLastTime());
         MotionsDB.getInstance().setMotionModified(motion, true);
      }
   }

   //------------------------------------------------------------------------
   // Utilities for running/canceling tool
   //------------------------------------------------------------------------

   private void updateIKTool() {
       // Copy values from self to imuIkTool
       Vec3 rotationsInRadians = new Vec3(rotations).scalarTimesEq(Math.toRadians(1.0));
       imuIkTool.set_sensor_to_opensim_rotations(rotationsInRadians);
       imuIkTool.set_orientations_file(sensorOrientationsFileName);
       imuIkTool.setOutputMotionFileName(fullOutputFileName);
       if (fullOutputFileName.isEmpty()){
            imuIkTool.setResultsDir(new File(sensorOrientationsFileName).getParent());
       }
       else{
            File outputFile = new File(fullOutputFileName);
            if (outputFile.getParent()==null && sensorOrientationsFileName != null){
                // No folder, just file namw, use folder from 
                imuIkTool.setResultsDir(new File(sensorOrientationsFileName).getParent());
            }    
            else
                imuIkTool.setResultsDir(outputFile.getParent());
            // Convert fullOutputFileName to only filename
            imuIkTool.setOutputMotionFileName(outputFile.getName());
       }
       imuIkTool.set_time_range(0, timeRange[0]);
       imuIkTool.set_time_range(1, timeRange[1]);
       imuIkTool.set_report_errors(reportErrors);
       // Replace OrientationWeightSet in tool with new set.
       imuIkTool.upd_orientation_weights().setSize(0);
       for (int j=0; j < orientation_weightset.getSize(); j++)
        imuIkTool.upd_orientation_weights().adoptAndAppend(orientation_weightset.get(j));
   }

   public void execute() {  
      if(isModified() && worker==null) {
         try {
            worker = new IMUIKToolWorker();
            SimulationDB.getInstance().fireToolStart();
            worker.start();
         } catch (Exception ex) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(ex.getMessage(), NotifyDescriptor.ERROR_MESSAGE));
            worker = null;
         }
      }
   }

   // TODO: may need to use locks and such to ensure that worker doesn't get set to null (by IMUIKToolWorker.finished()) in between worker!=null check and worker.interrupt()
   // But I think both will typically run on the swing thread so probably safe
   public void interrupt(boolean promptToKeepPartialResult) {
      if(worker!=null) worker.interrupt(promptToKeepPartialResult);
   }

   public void cancel() {
      interrupt(false);
      updateMotion(null);
   }

   //------------------------------------------------------------------------
   // Handle updates in the IK Task Set
   //------------------------------------------------------------------------

   public void update(Observable observable, Object obj) {
      //if(observable==ikCommonModel) setModified(Operation.IKTaskSetChanged);
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

   public boolean isValid() {
      return getSensorData()!=null;
   }

   //------------------------------------------------------------------------
   // Load/Save Settings
   //------------------------------------------------------------------------
   
   private void relativeToAbsolutePaths(String parentFileName) {
      String parentDir = (new File(parentFileName)).getParent();
      /*
        imuIkTool.setMarkerDataFileName(FileUtils.makePathAbsolute(imuIkTool.getMarkerDataFileName(),parentDir));
        imuIkTool.setCoordinateFileName(FileUtils.makePathAbsolute(imuIkTool.getCoordinateFileName(),parentDir)); */
        imuIkTool.set_orientations_file(FileUtils.makePathAbsolute(imuIkTool.get_orientations_file(), parentDir));
        String fullpath = imuIkTool.getResultsDir()+"/"+imuIkTool.getOutputMotionFileName();
        imuIkTool.setOutputMotionFileName(FileUtils.makePathAbsolute(fullpath, parentDir));
  }

   private void AbsoluteToRelativePaths(String parentFileName) {
      String parentDir = (new File(parentFileName)).getParent();
      imuIkTool.set_orientations_file(FileUtils.makePathRelative(sensorOrientationsFileName, parentDir));
      File outFile = new File(fullOutputFileName);
      if (outFile.getParent()!=null)
        imuIkTool.setResultsDir(FileUtils.makePathRelative(outFile.getParent(), parentDir));
      imuIkTool.setOutputMotionFileName(outFile.getName());
   }

   public boolean loadSettings(String fileName) {
      // TODO: set current working directory before trying to read it?
      IMUInverseKinematicsTool newIKTool = new IMUInverseKinematicsTool(fileName);

      imuIkTool = newIKTool;
      if (newIKTool.get_output_motion_file().isEmpty() && imuIkTool.get_orientations_file()!=null)
          newIKTool.setOutputMotionFileName("ik_"+imuIkTool.get_orientations_file().replace(".sto", ".mot"));
      fullOutputFileName = newIKTool.get_output_motion_file();
      sensorOrientationsFileName = imuIkTool.get_orientations_file();
      relativeToAbsolutePaths(fileName);
      fullOutputFileName = newIKTool.get_output_motion_file();
      sensorOrientationsFileName = imuIkTool.get_orientations_file();
      setTimeRange(new double[]{imuIkTool.getStartTime(), imuIkTool.getEndTime()});      
      //ikCommonModel.fromIKTool(imuIkTool);
      for (int i=0; i<3; i++) 
          rotations.set(i, Math.toDegrees(imuIkTool.get_sensor_to_opensim_rotations().get(i)));
      orientation_weightset = imuIkTool.get_orientation_weights();
      int sz = orientation_weightset.getSize();
      if (!sensorOrientationsFileName.isEmpty() && sz==0){
          populateOrientationWeights();
      }
      setModified(Operation.AllDataChanged);
      return true;
   }

   public boolean saveSettings(String fileName) {
      String fullFilename = FileUtils.addExtensionIfNeeded(fileName, ".xml");
      updateIKTool();
      AbsoluteToRelativePaths(fullFilename);
      imuIkTool.print(fullFilename);
      relativeToAbsolutePaths(fullFilename);
      return true;
   }
   
   void cleanup() {
      if (isExecuting()){
         cleanupAfterExecuting = true;
      }
      else{
         deleteObservers();
         //ikCommonModel=null;
         imuIkTool = null;
         System.gc();
      }
   }

    /**
     * @return the sensorOrientationsFileName
     */
    public String getSensorOrientationsFileName() {
        return sensorOrientationsFileName;
    }

    /**
     * @param sensorOrientationsFileName the sensorOrientationsFileName to set
     */
    public void setSensorOrientationsFileName(String sensorOrientationsFileName) {
        this.sensorOrientationsFileName = sensorOrientationsFileName;
    }

    /**
     * @return the rotations
     */
    public Vec3 getRotations() {
        return rotations;
    }

    /**
     * @param rotations the rotations to set
     */
    public void setRotations(Vec3 rotations) {
        this.rotations = new Vec3(rotations);
    }

    /**
     * @return the orientation_weightset
     */
    public OrientationWeightSet getOrientation_weightset() {
        if (orientation_weightset==null){
            populateOrientationWeights();
        }
        return orientation_weightset;
    }

    /**
     * @param orientation_weightset the orientation_weightset to set
     */
    public void setOrientation_weightset(OrientationWeightSet orientation_weightset) {
        this.orientation_weightset = new OrientationWeightSet(orientation_weightset);
    }
}
