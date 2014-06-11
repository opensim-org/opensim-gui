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
         model.initSystem();
         model.setInputFileName("");    // Will do this after initSystem so that contact geometry can be loaded properly
         
         tool.setModel(model);

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
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message("Tool execution failed. Check Messages window for details."));
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
                motion.resampleLinear(0.001);
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
                ex.printStackTrace();
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
             ex.printStackTrace();
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
