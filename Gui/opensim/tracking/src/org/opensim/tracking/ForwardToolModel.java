/* -------------------------------------------------------------------------- *
 * OpenSim: ForwardToolModel.java                                             *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib, Kevin Xu                                           *
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
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Cancellable;
import org.opensim.modeling.*;
import org.opensim.view.motions.MotionsDB;
import org.opensim.swingui.SwingWorker;
import org.opensim.tracking.tools.SimulationDB;
import org.opensim.utils.ErrorDialog;
import org.opensim.utils.FileUtils;
import org.opensim.view.motions.JavaMotionDisplayerCallback;
import org.opensim.view.pub.OpenSimDB;

public class ForwardToolModel extends AbstractToolModelWithExternalLoads {

   //========================================================================
   // ForwardToolWorker
   //========================================================================
   class ForwardToolWorker extends SwingWorker {
      private ProgressHandle progressHandle = null;
      private JavaMotionDisplayerCallback animationCallback = null;
      private InterruptCallback interruptingCallback = null;
      private Kinematics kinematicsAnalysis = null; // For creating a storage we'll use as a motion
      boolean result = false;
      boolean promptToKeepPartialResult = true;
     
      ForwardToolWorker() throws IOException {
         updateTool();

         // Make no motion be currently selected (so model doesn't have extraneous ground forces/experimental markers from
         // another motion show up on it)
         MotionsDB.getInstance().clearCurrent();

         // Re-initialize our copy of the model
         Model model = new Model(getOriginalModel());
         model.initSystem();
         OpenSimContext context = OpenSimDB.getInstance().getContext(getOriginalModel());
         // This line has the effect of copying the current state of the gui model
         // to the copy of the model used for forward simulation so that a 
         // simulation can "resume" from current state. If initial states are specified 
         // this state will not be used
         model.setPropertiesFromState(context.getCurrentStateRef());
         String tempFileName=getOriginalModel().getInputFileName();
         //int loc = tempFileName.lastIndexOf(".");
         model.setInputFileName(tempFileName);

         // Update actuator set and contact force set based on settings in the tool, then call setup() and setModel()
         // setModel() will call addAnalysisSetToModel
         tool.updateModelForces(model, "");
         //ModelPose currentPose = new ModelPose("current", getOriginalModel());
         //currentPose.useAsDefaultForModel(model);
         
         model.setInputFileName("");    // Will do this after initSystem so that contact geometry can be loaded properly
         
         tool.setModel(model);
         model.initSystem(); // call initSystem after tool.setModel since the call invalidates the system to add Analyses in 4.0
         // don't add the model... we'll run forward on the new model but will actually apply the resulting motions to the current model
         setModel(model);

         // set model in our tool
         // add analysis set to model
         // TODO: eventually we'll want to have the kinematics analysis store the motion for us...

         // Initialize progress bar, given we know the number of frames to process
         double ti = getInitialTime();
         double tf = getFinalTime();
         progressHandle = ProgressHandleFactory.createHandle("Forward integration...",
                              new Cancellable() {
                                 public boolean cancel() {
                                    interrupt(true);
                                    SimulationDB.getInstance().stopSimulation();
                                    return true;
                                 }
                              });
         //getOriginalModel().setName("Originial");
         //model.setName("daCopy");
         // Animation callback will update the display *of the original model* during forward
         animationCallback = new JavaMotionDisplayerCallback(model, getOriginalModel(), null, progressHandle, false);
         getModel().addAnalysis(animationCallback);
         animationCallback.setStepInterval(1);
         animationCallback.setMinRenderTimeInterval(0.1); // to avoid rendering really frequently which can slow down our execution
         animationCallback.setRenderMuscleActivations(true);
         animationCallback.startProgressUsingTime(ti,tf);
         animationCallback.setDisplayTimeProgress(true);

         // Do this manouver (there's gotta be a nicer way) to create the object so that C++ owns it and not Java (since 
         // removeIntegCallback in finished() will cause the C++-side callback to be deleted, and if Java owned this object
         // it would then later try to delete it yet again)
         interruptingCallback = new InterruptCallback(getModel());
         getModel().addAnalysis(interruptingCallback);
         addResultDisplayers(getModel());   // Create all analyses that need to be created on analysis model
         // Put Stoppable in lookup to disable run and enable stop
         setExecuting(true);
      }

      
      public void interrupt(boolean promptToKeepPartialResult) {
         this.promptToKeepPartialResult = promptToKeepPartialResult;
         if(interruptingCallback!=null) interruptingCallback.interrupt();
         
      }

      public Object construct() {
         try {
            result = tool.run();
         } catch (IOException ex) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message("Tool execution failed. Check Messages window for details.\n"+ex.getMessage()));
            ex.printStackTrace();
         }

         return this;
      }

      public void finished() {
         System.out.println("Finished running forward tool.");
         boolean processResults = result;
         if(!result && promptToKeepPartialResult) {
            Object answer = NotifyDescriptor.YES_OPTION;//DialogDisplayer.getDefault().notify(new NotifyDescriptor.Confirmation("Do you want to keep your forward simulation results?",NotifyDescriptor.YES_NO_OPTION));
            if(answer==NotifyDescriptor.YES_OPTION) processResults = true;
         }
         //animationCallback.getStateStorage().print("AccumulatedState.sto");
         // Clean up motion displayer (this is necessary!)
         // Do it before adding/removing motions in MotionsDB since here we disable muscle activation rendering, 
         // but we want added motions to be able to enable that
         animationCallback.cleanupMotionDisplayer();

         if(processResults) {
            Storage motion = animationCallback.getStateStorage();
            if(motion!=null) {
                motion = new Storage(motion);
                //motion.resampleLinear(0.001);
            }
            updateMotion(motion); // replaces current motion
         }

         //opensim20 getModel().removeAnalysis(kinematicsAnalysis);

         // TODO: move this to a worker thread so as to not freeze the GUI if writing takes a while?
         //opensim20 if(processResults) {
            //opensim20 forwardTool().printResults(); // CRASH HERE 0327
         //opensim20 }

         progressHandle.finish();

         getModel().removeAnalysis(animationCallback, false);
         getModel().removeAnalysis(interruptingCallback, false);
         interruptingCallback = null;

         if(result) resetModified();

         setExecuting(false);
         SimulationDB.getInstance().finishSimulation();
         worker = null;
      }
        private void addResultDisplayers(Model model) {
            for(ResultDisplayerInterface nextDisplayer:resultDisplayers){
                Analysis nextAnalysis = nextDisplayer.createAnalysis(model);
            }
        }
        
        private void removeResultDisplayers(Model model) {
            for(ResultDisplayerInterface nextDisplayer:resultDisplayers){
                nextDisplayer.removeAnalysis(model);
            }           
        }
   }
   private ForwardToolWorker worker = null;
   //========================================================================
   // END ForwardToolWorker
   //========================================================================

   private Storage motion = null;
   private double [] controlTimeRange = {Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY };
   private double [] statesTimeRange = {Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY };
   
   public ForwardToolModel(Model model) throws IOException {
      super(model);

      // Check that the model has a real dynamics engine
      //if(model.getSimbodyEngine().getType().equals("SimmKinematicsEngine"))
      //   throw new IOException("Forward dynamics tool requires a model with SdfastEngine or SimbodyEngine; SimmKinematicsEngine does not support dynamics.");
      ForwardTool dTool = new ForwardTool();
      OpenSimContext openSimContext = OpenSimDB.getInstance().getContext(model);
      //openSimContext.setDefaultsFromState();
      dTool.setModel(model);
      dTool.setToolOwnsModel(false);

      //openSimContext.setDefaultsFromState();
      setTool(dTool);

      // By default, set prefix of output to be subject name
      forwardTool().setName(model.getName());

      setDefaultResultsDirectory(model);

   }

   private void updateToolTimeRange() {
       
       double timeRange[] = getTimeRange(controlTimeRange, statesTimeRange);
       
       forwardTool().setStartTime(timeRange[0]);     
       forwardTool().setFinalTime(timeRange[1]);
       
       setModified(AbstractToolModel.Operation.TimeRangeChanged);
   }

   ForwardTool forwardTool() { return (ForwardTool)tool; }

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
         MotionsDB.getInstance().setMotionModified(motion, true);
         //MotionControlJPanel.getInstance().setUserTime(motion.getLastTime());
      }
   }

   private void updateControlTimeRange(double startTime, double endTime) {
       controlTimeRange[0] = startTime;
       controlTimeRange[1] = endTime;
       updateToolTimeRange();
   }
   
   private void updateStatesTimeRange(double startTime, double endTime) {
       statesTimeRange[0] = startTime;
       statesTimeRange[1] = endTime;
       updateToolTimeRange();
   }
    
   /**
    * Calculating the time range for FD from control and states files
    * If control file is specified, take control file's initial/final times.
    * If state file is specified, take state file's initial time and assign
    * the final value to 1 second after the initial time.
    * Default the time range is [0, 1]
    * @param controlTimeRange
    * @param statesTimeRange
    * @return 
    */
   private double[] getTimeRange(double[] controlTimeRange, double[] statesTimeRange) {
       
       // Default time range;
       double timeRange[] = {0, 1}; 
       
       if(controlTimeRange[0] == Double.NEGATIVE_INFINITY 
               && controlTimeRange[1] == Double.NEGATIVE_INFINITY ) {
           
           timeRange[0] = (statesTimeRange[0] > Double.NEGATIVE_INFINITY ) ? statesTimeRange[0] : 0;
           timeRange[1] = (statesTimeRange[0] > Double.NEGATIVE_INFINITY ) ? statesTimeRange[0] + 1 : 1;
       } else {
           timeRange[0] = controlTimeRange[0];
           timeRange[1] = controlTimeRange[1];
       }
        
       return timeRange;
   }
   //------------------------------------------------------------------------
   // Get/Set Values
   //------------------------------------------------------------------------

   public String getControlsFileName() { return forwardTool().getControlsFileName(); }
   public void setControlsFileName(String fileName) {
      if(!getControlsFileName().equals(fileName)) {
         forwardTool().setControlsFileName(fileName);
         setModified(AbstractToolModel.Operation.InputDataChanged);
        
         double startTime = 0, endTime = 1;
         if(fileName.endsWith(".xml")){
             
            ControlSet set = new ControlSet(fileName);
            if(set.getSize() > 0) {
            	Control c = set.get(0);
                if(c != null) {
                    ControlLinear cl = ControlLinear.safeDownCast(c);
                    startTime = cl.getFirstTime();
                    endTime = cl.getLastTime();
                }
            }
         } else if (fileName.endsWith(".sto")) {
            try {
                Storage s = new Storage(fileName);
                startTime = s.getFirstTime();
                endTime = s.getLastTime();
            } catch (IOException ex) {
                ErrorDialog.displayExceptionDialog(ex);
            }
         }
         updateControlTimeRange(startTime, endTime);
      }
   }
   public boolean getControlsValid() { 
       // If model has no controls then return true always otherwise check file exists
       return (/*getOriginalModel().getNumControls()==0|| new File(getControlsFileName()).exists()*/true); 
   }

   public String getInitialStatesFileName() { return forwardTool().getStatesFileName(); }
   public void setInitialStatesFileName(String fileName) {
      if(!getInitialStatesFileName().equals(fileName)) {
         forwardTool().setStatesFileName(fileName);
         setModified(AbstractToolModel.Operation.InputDataChanged);
         try {
            Storage s = new Storage(fileName);
            updateStatesTimeRange(s.getFirstTime(), s.getLastTime());
         } catch (IOException ex) {
             ErrorDialog.displayExceptionDialog(ex);
         }
      }
   }
   public boolean getInitialStatesValid() { return true; }//(new File(getInitialStatesFileName()).exists()); }

   // TODO: implement
   public double[] getAvailableTimeRange() { return null; }

   //------------------------------------------------------------------------
   // Integrator settings (continued from AbstractToolModel
   //------------------------------------------------------------------------
   public boolean getUseSpecifiedDt() { return forwardTool().getUseSpecifiedDt(); }
   public void setUseSpecifiedDt(boolean useSpecifiedDt) {
      if(getUseSpecifiedDt() != useSpecifiedDt) {
         forwardTool().setUseSpecifiedDt(useSpecifiedDt);
         setModified(AbstractToolModel.Operation.IntegratorSettingsChanged);
      }
   }

   //------------------------------------------------------------------------
   // External loads get/set (don't need to call setModified since AbstractToolModel does that)
   //------------------------------------------------------------------------
   public String getExternalLoadsFileName() { return forwardTool().getExternalLoadsFileName(); }
   protected void setExternalLoadsFileNameInternal(String fileName) { forwardTool().setExternalLoadsFileName(fileName); }

   public String getExternalLoadsModelKinematicsFileName() { return forwardTool().getExternalLoads().getExternalLoadsModelKinematicsFileName(); }
   protected void setExternalLoadsModelKinematicsFileNameInternal(String fileName) { forwardTool().getExternalLoads().setExternalLoadsModelKinematicsFileName(fileName); }

   public double getLowpassCutoffFrequencyForLoadKinematics() { return forwardTool().getExternalLoads().getLowpassCutoffFrequencyForLoadKinematics(); }
   protected void setLowpassCutoffFrequencyForLoadKinematicsInternal(double cutoffFrequency) { forwardTool().getExternalLoads().setLowpassCutoffFrequencyForLoadKinematics(cutoffFrequency); }

   //------------------------------------------------------------------------
   // Utilities for running/canceling tool
   //------------------------------------------------------------------------

   public void execute() {
      if(isModified() && worker==null) {
         try {
            worker = new ForwardToolWorker();
            SimulationDB.getInstance().startSimulation(this);
         } catch (IOException ex) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(ex.getMessage()));
            setExecuting(false);
            return;
         }
         worker.start();
      }
   }

   // TODO: may need to use locks and such to ensure that worker doesn't get set to null (by IKToolWorker.finished()) in between worker!=null check and worker.interrupt()
   // But I think both will typically run on the swing thread so probably safe
   public void interrupt(boolean promptToKeepPartialResult) {
      if(worker!=null) worker.interrupt(promptToKeepPartialResult);
   }

   public void cancel() {
      interrupt(false);
      updateMotion(null);
   }

   //------------------------------------------------------------------------
   // Validation
   //------------------------------------------------------------------------

   public boolean isValidated() {
      return super.isValidated() && getInitialStatesValid() && getControlsValid();
   }

   //------------------------------------------------------------------------
   // Load/Save Settings
   //------------------------------------------------------------------------

   protected void updateFromTool() {
      super.updateFromTool();
   }

   protected void updateTool() {
      super.updateTool();
      //forwardTool().setPrintResultFiles(false); // we'll manually write them out
   }

   protected void relativeToAbsolutePaths(String parentFileName) {
      super.relativeToAbsolutePaths(parentFileName);

      String parentDir = (new File(parentFileName)).getParent();

      forwardTool().setModelFilename(FileUtils.makePathAbsolute(forwardTool().getModelFilename(), parentDir));
      forwardTool().setControlsFileName(FileUtils.makePathAbsolute(forwardTool().getControlsFileName(), parentDir));
      forwardTool().setStatesFileName(FileUtils.makePathAbsolute(forwardTool().getStatesFileName(), parentDir));

      forwardTool().setExternalLoadsFileName(FileUtils.makePathAbsolute(forwardTool().getExternalLoadsFileName(), parentDir));
      forwardTool().getExternalLoads().setExternalLoadsModelKinematicsFileName(FileUtils.makePathAbsolute(forwardTool().getExternalLoads().getExternalLoadsModelKinematicsFileName(), parentDir));
   }

   protected void AbsoluteToRelativePaths(String parentFileName) {
      super.AbsoluteToRelativePaths(parentFileName);

      String parentDir = (new File(parentFileName)).getParent();

      forwardTool().setModelFilename(FileUtils.makePathRelative(forwardTool().getModelFilename(), parentDir));
      forwardTool().setControlsFileName(FileUtils.makePathRelative(forwardTool().getControlsFileName(), parentDir));
      forwardTool().setStatesFileName(FileUtils.makePathRelative(forwardTool().getStatesFileName(), parentDir));

      forwardTool().setExternalLoadsFileName(FileUtils.makePathRelative(forwardTool().getExternalLoadsFileName(), parentDir));
      forwardTool().getExternalLoads().setExternalLoadsModelKinematicsFileName(FileUtils.makePathRelative(forwardTool().getExternalLoads().getExternalLoadsModelKinematicsFileName(), parentDir));
   }

   public boolean loadSettings(String fileName) {
      // TODO: set current working directory before trying to read it?
      ForwardTool newForwardTool = null;
      try {
         // TODO: pass it our model instead
         newForwardTool = new ForwardTool(fileName, true, false);
         if (newForwardTool.getParsingLog().length()>0){
             // Corrective springs were replaced with CorrectiveController. 
             // Inform user and popu a dialog
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(newForwardTool.getParsingLog()));
             
         }
      } catch (IOException ex) {
         ErrorDialog.displayIOExceptionDialog("Error loading file","Could not load "+fileName,ex);
         return false;
      }

      setTool(newForwardTool);
      relativeToAbsolutePaths(fileName);
      updateFromTool();
      setModified(AbstractToolModel.Operation.AllDataChanged);
      return true;
   }

   public boolean saveSettings(String fileName) {
      String fullFilename = FileUtils.addExtensionIfNeeded(fileName, ".xml");
      updateTool();
      AbsoluteToRelativePaths(fullFilename);
      forwardTool().print(fullFilename);
      relativeToAbsolutePaths(fullFilename);
      return true;
   }
}
