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
import org.opensim.modeling.IMUPlacer;
//import org.opensim.modeling.InterruptingIntegCallback;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimContext;
import org.opensim.modeling.StdVectorString;
import org.opensim.modeling.Storage;
import org.opensim.modeling.TimeSeriesTableQuaternion;
import org.opensim.modeling.Vec3;
import org.opensim.view.motions.MotionsDB;
import org.opensim.swingui.SwingWorker;
import org.opensim.tracking.tools.SimulationDB;
import org.opensim.utils.FileUtils;
import org.opensim.view.pub.OpenSimDB;

//==================================================================
// IKToolModel
//==================================================================
// Only deals with a single (the first) IKTrial in the IKTrialSet of IKTool
public class IMUCalibrateModel extends Observable implements Observer {

    /**
     * @return the sensorDataLabels
     */
    public StdVectorString getSensorDataLabels() {
        return sensorDataLabels;
    }

    /**
     * @return the sensorData
     */
    public TimeSeriesTableQuaternion getSensorData() {
        if (sensorData == null){
            sensorData = new TimeSeriesTableQuaternion(sensorOrientationsFileName);
            sensorDataLabels = sensorData.getColumnLabels();
        }
        return sensorData;
    }
    
    void setSensorDataFileName(String fileName) {
        if (!new File(fileName).exists()){
            sensorData = null;
            return;
        }
        if (fileName != sensorOrientationsFileName){
            sensorOrientationsFileName = fileName;
            sensorData = new TimeSeriesTableQuaternion(sensorOrientationsFileName);
            sensorDataLabels = sensorData.getColumnLabels();
            //if (sensorDataLabels.size()>0)
            //    imuLabel = sensorDataLabels.get(0);
            setModified(Operation.AllDataChanged);
        }
    }

    void setIMULabel(String newLabel) {
        imuLabel = newLabel;
        setModified(Operation.AllDataChanged);
    }

    void setIMUAxis(String newAxis) {
        imuAxis = newAxis;
        setModified(Operation.AllDataChanged);
    }
   
   //========================================================================
   // IMUPlacerToolWorker
   //========================================================================
   class IMUPlacerToolWorker extends SwingWorker {
      private ProgressHandle progressHandle = null;
      boolean result = false;
      boolean promptToKeepPartialResult = true;
      boolean cleanup=true;
      Model modelCopy = new Model(getOriginalModel());
      final OpenSimContext context=OpenSimDB.getInstance().getContext(getOriginalModel());
      Model calibratedModel = null;
      
      IMUPlacerToolWorker() throws Exception {
         // Give the thread a nudge so that we're not much slower than command line'
         setPriority(Thread.MAX_PRIORITY);
         
         modelCopy.initSystem();
         updateIMUPlacerTool();

         // Operate on a copy of the model -- this way if users play with parameters in the GUI it won't affect the model we're actually computing on
         imuPlacerTool.setModel(modelCopy);
         // Make no motion be currently selected (so model doesn't have extraneous ground forces/experimental markers from
         // another motion show up on it)
         MotionsDB.getInstance().clearCurrent();

         // Initialize progress bar
         progressHandle = ProgressHandleFactory.createHandle("Executing calibration...",
                              new Cancellable() {
                                 public boolean cancel() {
                                    SimulationDB.getInstance().fireToolFinish();
                                    return true;
                                 }
                              });

         setExecuting(true);
         SimulationDB.getInstance().fireToolStart();

      }

      public Object construct() {
         try {
           imuPlacerTool.run();
         }
         catch(Exception ex) {
            progressHandle.finish();
            SimulationDB.getInstance().fireToolFinish();
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message("Model Calibration did not complete.\n"+ex.getMessage()+"\n", NotifyDescriptor.ERROR_MESSAGE));
            worker=null;
            cleanup=false;
            
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

            if (result) {
                resetModified();
            }
            calibratedModel = new Model(imuPlacerTool.getCalibratedModel());
            calibratedModel.setName(getOriginalModel().getName()+"_calibrated");
            String proposedFilename = getOriginalModel().getInputFileName().replace(".osim", "_calibrated.osim");
            calibratedModel.setInputFileName(proposedFilename);
            calibratedModel.setOriginalModelPathFromModel(getOriginalModel()); // important to keep track of the original path so bone loading works
            OpenSimDB.getInstance().replaceModel(null, calibratedModel, null);
            boolean addMotion = true;
            if (!result) {
                boolean havePartialResult = false;//OpenSim23 imuPlacerTool.getOutputStorage()!=null && imuPlacerTool.getOutputStorage().getSize()>0;
                if (havePartialResult && promptToKeepPartialResult) {
                    Object answer = DialogDisplayer.getDefault().notify(new NotifyDescriptor.Confirmation("Inverse kinematics did not complete.  Keep partial result?", NotifyDescriptor.YES_NO_OPTION));
                    if (answer == NotifyDescriptor.NO_OPTION) {
                        addMotion = false;
                    }
                } else {
                    addMotion = false;
                }
            }
            setExecuting(false);

            modelCopy = null;
            worker = null;
        }
    }
   private IMUPlacerToolWorker worker = null;
   //========================================================================
   // END IMUPlacerToolWorker
   //========================================================================

   public enum Operation { AllDataChanged, IKTrialNameChanged, IKTaskSetChanged, ExecutionStateChanged };

   private IMUPlacer imuPlacerTool = null;
   private Model originalModel = null;
   private boolean modifiedSinceLastExecute = true;
   private Storage motion = null;
   private boolean executing = false;
   private String trialName = "Calibration Pose";
   private boolean cleanupAfterExecuting = false;  // Keep track if cleaning up needs to be done on execution finish vs. dialog close
   private String sensorOrientationsFileName = "";
   private String fullOutputFileName = "";
   private TimeSeriesTableQuaternion sensorData = null;
   private StdVectorString sensorDataLabels = null;
   private Vec3 rotations = new Vec3(0);
   private String imuLabel="";
   private String imuAxis=" "; // default selection in dialog/panel
   
   public IMUCalibrateModel(Model originalModel) throws IOException {
      // Store original model
      this.originalModel = originalModel;

      // Create IMUPlacer tool
      imuPlacerTool = new IMUPlacer();
      //addTrialIfNecessary();
   }

   public Model getOriginalModel() { return originalModel; }
   public IMUPlacer getIMUPlacerTool() { return imuPlacerTool; }
   
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

   private void updateIMUPlacerTool() {
       // Copy values from self to imuPlacerTool
       Vec3 rotationsInRadians = new Vec3(rotations).scalarTimesEq(Math.toRadians(1.0));
       imuPlacerTool.set_sensor_to_opensim_rotations(rotationsInRadians);
       imuPlacerTool.set_orientation_file_for_calibration(sensorOrientationsFileName);
       imuPlacerTool.set_base_imu_label(imuLabel.trim());
       imuPlacerTool.set_base_heading_axis(imuAxis.trim());
   }

   public void execute() {  
      if(isModified() && worker==null) {
         try {
            worker = new IMUPlacerToolWorker();
            SimulationDB.getInstance().fireToolStart();
            worker.start();
         } catch (Exception ex) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(ex.getMessage(), NotifyDescriptor.ERROR_MESSAGE));
            worker = null;
         }
      }
   }


   public void cancel() {
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
      return sensorData!=null;
   }

   //------------------------------------------------------------------------
   // Load/Save Settings
   //------------------------------------------------------------------------
   
   private void relativeToAbsolutePaths(String parentFileName) {
      String parentDir = (new File(parentFileName)).getParent();
      /*
        imuPlacerTool.setMarkerDataFileName(FileUtils.makePathAbsolute(imuPlacerTool.getMarkerDataFileName(),parentDir));
        imuPlacerTool.setCoordinateFileName(FileUtils.makePathAbsolute(imuPlacerTool.getCoordinateFileName(),parentDir)); */
        setSensorDataFileName(FileUtils.makePathAbsolute(imuPlacerTool.get_orientation_file_for_calibration(), parentDir));
  }

   private void AbsoluteToRelativePaths(String parentFileName) {
      String parentDir = (new File(parentFileName)).getParent();
      imuPlacerTool.set_orientation_file_for_calibration(FileUtils.makePathRelative(sensorOrientationsFileName, parentDir));
   }

   public boolean loadSettings(String fileName) {
      // TODO: set current working directory before trying to read it?
      IMUPlacer newIMUPlacer = new IMUPlacer(fileName);

      imuPlacerTool = newIMUPlacer;
      setSensorOrientationsFileName(imuPlacerTool.get_orientation_file_for_calibration());
      relativeToAbsolutePaths(fileName);
      //ikCommonModel.fromIKTool(imuPlacerTool);
      for (int i=0; i<3; i++) 
          rotations.set(i, Math.toDegrees(imuPlacerTool.get_sensor_to_opensim_rotations().get(i)));
      imuLabel = newIMUPlacer.get_base_imu_label();
      imuAxis = newIMUPlacer.get_base_heading_axis();
      setModified(Operation.AllDataChanged);
      return true;
   }

   public boolean saveSettings(String fileName) {
      String fullFilename = FileUtils.addExtensionIfNeeded(fileName, ".xml");
      /*
      XMLExternalFileChooserHelper helper = new XMLExternalFileChooserHelper(fullFilename);
      helper.addObject(imuPlacerTool.getIKTaskSet(), "IK Task Set");
      if(!helper.promptUser()) return false;*/
      //imuIkTool.getIKTaskSet().setInlined(true);
      updateIMUPlacerTool();
      AbsoluteToRelativePaths(fullFilename);
      imuPlacerTool.print(fullFilename);
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
         imuPlacerTool = null;
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
    public void setSensorOrientationsFileName(String fileName) {
       if (fileName != sensorOrientationsFileName){
            sensorOrientationsFileName = fileName;
            if (new File(fileName).exists()){
                sensorData = new TimeSeriesTableQuaternion(sensorOrientationsFileName);
                sensorDataLabels = sensorData.getColumnLabels();
            }
       }
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
     * @return the imuLabel
     */
    public String getImuLabel() {
        return imuLabel;
    }

    /**
     * @param imuLabel the imuLabel to set
     */
    public void setImuLabel(String imuLabel) {
        this.imuLabel = imuLabel;
    }

    /**
     * @return the imuAxis
     */
    public String getImuAxis() {
        return imuAxis;
    }

    /**
     * @param imuAxis the imuAxis to set
     */
    public void setImuAxis(String imuAxis) {
        this.imuAxis = imuAxis;
    }

}
