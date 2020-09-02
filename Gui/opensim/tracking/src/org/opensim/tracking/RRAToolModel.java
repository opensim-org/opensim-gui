/* -------------------------------------------------------------------------- *
 * OpenSim: RRAToolModel.java                                                 *
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
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Cancellable;
import org.opensim.modeling.ExternalLoads;
import org.opensim.modeling.InterruptCallback;
import org.opensim.modeling.Kinematics;
import org.opensim.modeling.Model;
import org.opensim.modeling.RRATool;
import org.opensim.modeling.Storage;
import org.opensim.view.motions.JavaMotionDisplayerCallback;
import org.opensim.view.motions.MotionsDB;
import org.opensim.swingui.SwingWorker;
import org.opensim.tracking.tools.SimulationDB;
import org.opensim.utils.ErrorDialog;
import org.opensim.utils.FileUtils;
import org.opensim.view.excitationEditor.NameFilterJPanel;
import org.opensim.view.pub.OpenSimDB;

public class RRAToolModel extends TrackingToolModel {
   //========================================================================
   // RRAToolWorker
   //========================================================================
   class RRAToolWorker extends SwingWorker {
      private ProgressHandle progressHandle = null;
      private JavaMotionDisplayerCallback animationCallback = null;
      private InterruptCallback interruptingCallback = null;
      
      private Kinematics kinematicsAnalysis = null; // For creating a storage we'll use as a motion
      boolean result = false;
      boolean promptToKeepPartialResult = true;
      NameFilterJPanel filterPanel = null;
      
      RRAToolWorker() throws IOException {
         updateTool();

         // Make no motion be currently selected (so workersModel doesn't have extraneous ground forces/experimental markers from
         // another motion show up on it)
         MotionsDB.getInstance().clearCurrent();

         // CMC needs to remember the original actuator set, since it is replaced in updateModelActuatorsAndContactForces
         rraTool().setOriginalForceSet(getOriginalModel().getForceSet());

         // Re-initialize our copy of the workersModel
         Model workersModel = new Model(getOriginalModel());
         workersModel.updAnalysisSet().setSize(0);
         String tempFileName=getOriginalModel().getInputFileName();
         //int loc = tempFileName.lastIndexOf(".");
         workersModel.setInputFileName(tempFileName);

         // Update actuator set and contact force set based on settings in the tool, then call setup() and setModel()
         // setModel() will call addAnalysisSetToModel
         tool.updateModelForces(workersModel, "");
         workersModel.initSystem();
         //workersModel.setInputFileName("");    // Will do this after initSystem so that contact geometry can be loaded properly
         tool.setModel(workersModel);

         setModel(workersModel);

         // TODO: eventually we'll want to have the kinematics analysis store the motion for us...

         // Initialize progress bar, given we know the number of frames to process
         double ti = getInitialTime();
         double tf = getFinalTime();
         progressHandle = ProgressHandleFactory.createHandle("Reducing residuals...",
                              new Cancellable() {
                                 public boolean cancel() {
                                    interrupt(false);
                                    SimulationDB.getInstance().fireToolFinish();
                                       return true;
                                 }
                              });

         // Animation callback will update the display during forward
         animationCallback = new JavaMotionDisplayerCallback(getModel(), getOriginalModel(), null, progressHandle, false);
         
         getModel().addAnalysis(animationCallback);
         animationCallback.setStepInterval(1);
         animationCallback.setMinRenderTimeInterval(0.1); // to avoid rendering really frequently which can slow down our execution
         animationCallback.startProgressUsingTime(ti,tf);

         // Do this maneuver (there's gotta be a nicer way) to create the object so that C++ owns it and not Java (since 
         // removeIntegCallback in finished() will cause the C++-side callback to be deleted, and if Java owned this object
         // it would then later try to delete it yet again)
         interruptingCallback = new InterruptCallback(getModel());
         getModel().addAnalysis(interruptingCallback);

        // Kinematics analysis -- so that we can extract the resulting motion
         // NO LONGER NEEDED SINCE WE JUST GET THE STATES FROM THE INTEGRAND
         //kinematicsAnalysis = Kinematics.safeDownCast((new Kinematics()).copy());
         //kinematicsAnalysis.setRecordAccelerations(false);
         //kinematicsAnalysis.setInDegrees(false);
         //kinematicsAnalysis.setPrintResultFiles(false);
         //getModel().addAnalysis(kinematicsAnalysis);

         setExecuting(true);
         SimulationDB.getInstance().fireToolStart();
     }

      public void interrupt(boolean promptToKeepPartialResult) {
         this.promptToKeepPartialResult = promptToKeepPartialResult;
         if(interruptingCallback!=null) interruptingCallback.interrupt();
      }

      public Object construct() {
         try {
            SimulationDB.getInstance().fireToolStart();
            result = tool.run();
         } catch (IOException ex) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message("Tool execution failed. Check Messages window for details.\n"+ex.getMessage()));
            ex.printStackTrace();
            SimulationDB.getInstance().fireToolFinish();
         }

         return this;
      }

      public void finished() {
         boolean processResults = result;
         if(!result) { // TODO: prompt to keep partial results?
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                    "Tool execution failed or canceled by user.  Output files were not written."));
         }

         // Clean up motion displayer (this is necessary!)
         // Do it before adding/removing motions in MotionsDB since here we disable muscle activation rendering, 
         // but we want added motions to be able to enable that
         animationCallback.cleanupMotionDisplayer();

         // Add adjusted RRA model if we're in that mode
         if(processResults) {
            // Remove previous motion
            if(motion!=null) {
               if(reducedResidualsModel!=null) MotionsDB.getInstance().closeMotion(reducedResidualsModel, motion, false, false);
               else MotionsDB.getInstance().closeMotion(getOriginalModel(), motion, false, false);
               motion = null;
            }

            motion = animationCallback.getStateStorage();
            if(motion!=null) {
               motion = new Storage(motion);
               //motion.resampleLinear(0.001); // so that we don't get a crazy oversampled storage
            }

            if(getAdjustModelToReduceResidualsEnabled()) {
                if (new File(getOutputModelFileName()).exists()) {
               Model newReducedResidualsModel = null;
               try {
                  newReducedResidualsModel = new Model(getOutputModelFileName());
                  //newReducedResidualsModel.setup();
               } catch (IOException ex) {
                  newReducedResidualsModel = null;
                  ErrorDialog.displayExceptionDialog(ex);
               }
               //OpenSim20 added argument
               OpenSimDB.getInstance().replaceModel(reducedResidualsModel, newReducedResidualsModel, null);
               reducedResidualsModel = newReducedResidualsModel;
               MotionsDB.getInstance().addMotion(reducedResidualsModel, motion, null);
               }
            } else {
               if(reducedResidualsModel!=null) {
                  OpenSimDB.getInstance().removeModel(reducedResidualsModel);
                  reducedResidualsModel = null;
               }
               MotionsDB.getInstance().addMotion(getOriginalModel(), motion, null);
            }
         }

         // Remove the kinematics analysis before printing results, so its results won't be written to disk
         //getModel().removeAnalysis(kinematicsAnalysis);

         progressHandle.finish();

         getModel().removeAnalysis(animationCallback, false);
         getModel().removeAnalysis(interruptingCallback, false);
         interruptingCallback = null;

         if(result) resetModified();

         setExecuting(false);

         worker = null;
      }
   }
   private RRAToolWorker worker = null;
   //========================================================================
   // END RRAToolWorker
   //========================================================================

   private boolean constraintsEnabled = false;
   private Model reducedResidualsModel = null;
   private Storage motion = null;
   private boolean plotMatrics = false;
   private int plotUpdateRate = 0;
   private boolean reuseMetrics = false;
   private static String[] qtys2plot = null;
   
   public RRAToolModel(Model model) throws IOException {
      super(model);

      if(model.getSimbodyEngine().getConcreteClassName().equals("SimmKinematicsEngine"))
         throw new IOException("Computed muscle control tool requires a model with SdfastEngine or SimbodyEngine; SimmKinematicsEngine does not support dynamics.");

      setTool(new RRATool());

      // By default, set prefix of output to be subject name
      rraTool().setName(model.getName());
      rraTool().setModelFilename(model.getInputFileName());
      setAdjustModelToReduceResidualsEnabled(true);
      setDefaultResultsDirectory(model);

      updateFromTool();
   }

   RRATool rraTool() { return (RRATool)tool; }

   //------------------------------------------------------------------------
   // Get/set
   //------------------------------------------------------------------------

   public String getDesiredKinematicsFileName() { return rraTool().getDesiredKinematicsFileName(); }
   public void setDesiredKinematicsFileName(String fileName) {
      if(!getDesiredKinematicsFileName().equals(fileName)) {
         rraTool().setDesiredKinematicsFileName(fileName);
         setModified(AbstractToolModel.Operation.InputDataChanged);
         try {
            Storage coords = new Storage(fileName);
            updateToolTimeRange(coords);
         } catch (IOException ex) {
            ErrorDialog.displayExceptionDialog(ex);
      }
   }
   }
   
   public boolean getDesiredKinematicsValid() { return (new File(getDesiredKinematicsFileName()).exists()); }

   public String getTaskSetFileName() { 
       return rraTool().getTaskSetFileName(); 
   }
   public void setTaskSetFileName(String fileName) {
      if(!getTaskSetFileName().equals(fileName)) {
         rraTool().setTaskSetFileName(fileName);
         setModified(AbstractToolModel.Operation.InputDataChanged);
      }
   }
   public boolean getTaskSetValid() { return (new File(getTaskSetFileName()).exists()); }

   public String getConstraintsFileName() { return rraTool().getConstraintsFileName(); }
   public void setConstraintsFileName(String fileName) {
      if(!getConstraintsFileName().equals(fileName)) {
         rraTool().setConstraintsFileName(fileName);
         setModified(AbstractToolModel.Operation.InputDataChanged);
      }
   }
   public boolean getConstraintsValid() { return !getConstraintsEnabled() || (new File(getConstraintsFileName()).exists()); }

   public boolean getConstraintsEnabled() { return constraintsEnabled; }
   public void setConstraintsEnabled(boolean enabled) {
      if(getConstraintsEnabled() != enabled) {
         constraintsEnabled = enabled;
         setModified(AbstractToolModel.Operation.InputDataChanged);
      }
   }

   public double getLowpassCutoffFrequency() { return rraTool().getLowpassCutoffFrequency(); }
   public void setLowpassCutoffFrequency(double frequency) {
      if(getLowpassCutoffFrequency() != frequency) {
         rraTool().setLowpassCutoffFrequency(frequency);
         setModified(AbstractToolModel.Operation.InputDataChanged);
      }
   }

   public boolean getFilterKinematics() { return getLowpassCutoffFrequency() > 0; }
   public void setFilterKinematics(boolean filterKinematics) {
      if(getFilterKinematics() != filterKinematics) {
         if(filterKinematics) rraTool().setLowpassCutoffFrequency(6);
         else rraTool().setLowpassCutoffFrequency(-1);
         setModified(AbstractToolModel.Operation.InputDataChanged);
      }
   }

   // RRA settings
   //
   public String getOutputModelFileName() { return rraTool().getOutputModelFileName(); }
   public void setOutputModelFileName(String fileName) {
      if(!getOutputModelFileName().equals(fileName)) {
         rraTool().setOutputModelFileName(fileName);
         setModified(AbstractToolModel.Operation.InputDataChanged);
      }
   }

   public boolean getAdjustModelToReduceResidualsEnabled() { return rraTool().getAdjustCOMToReduceResiduals(); }
   public void setAdjustModelToReduceResidualsEnabled(boolean enabled) {
      if(getAdjustModelToReduceResidualsEnabled() != enabled) {
         rraTool().setAdjustCOMToReduceResiduals(enabled);
         setModified(AbstractToolModel.Operation.InputDataChanged);
      }
   }

   public String getAdjustedCOMBody() { return rraTool().getAdjustedCOMBody(); }
   public void setAdjustedCOMBody(String fileName) {
      if(!getAdjustedCOMBody().equals(fileName)) {
         rraTool().setAdjustedCOMBody(fileName);
         setModified(AbstractToolModel.Operation.InputDataChanged);
      }
   }
   public boolean getAdjustedCOMBodyValid() {
      return getOriginalModel().getBodySet().getIndex(getAdjustedCOMBody())>=0;
   }

   private boolean isRRAValid() {
      return !getAdjustModelToReduceResidualsEnabled() || (!FileUtils.effectivelyNull(getOutputModelFileName()) && getAdjustedCOMBodyValid());
   }

   public boolean getUseFastTarget() { 
       return false; 
   }
   public void setUseFastTarget(boolean enabled) {
   } 
 
   //------------------------------------------------------------------------
   // External loads get/set (don't need to call setModified since AbstractToolModel does that)
   //------------------------------------------------------------------------
   public String getExternalLoadsFileName() { return rraTool().getExternalLoadsFileName(); }
   protected void setExternalLoadsFileNameInternal(String fileName) { rraTool().setExternalLoadsFileName(fileName); }

   // TODO: implement
   public double[] getAvailableTimeRange() { return null; }

   //------------------------------------------------------------------------
   // Utilities for running/canceling tool
   //------------------------------------------------------------------------

   public void execute() {
      if(isModified() && worker==null) {
         try {
            worker = new RRAToolWorker();
         } catch (IOException ex) {
            setExecuting(false);
            ErrorDialog.displayIOExceptionDialog("RRA Tool Error", "Tool initialization failed.", ex);
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
      /* Cancel is same as close, no need to remove adjusted model if tool is done
      if(reducedResidualsModel!=null) {
         OpenSimDB.getInstance().removeModel(reducedResidualsModel);
         reducedResidualsModel = null;
      }*/
   }

   //------------------------------------------------------------------------
   // Validation
   //------------------------------------------------------------------------

   public boolean isValidated() {
      return super.isValidated() && getDesiredKinematicsValid() && getTaskSetValid() && getConstraintsValid() && isRRAValid();
   }

   //------------------------------------------------------------------------
   // Load/Save Settings
   //------------------------------------------------------------------------

   protected void updateFromTool() {
      super.updateFromTool();

      constraintsEnabled = !FileUtils.effectivelyNull(getConstraintsFileName());
      ExternalLoads lds = rraTool().getExternalLoads();
      
   }

   protected void updateTool() {
      super.updateTool();

      if(!constraintsEnabled) rraTool().setConstraintsFileName("");

      setModified(AbstractToolModel.Operation.AllDataChanged);
   }

   protected void relativeToAbsolutePaths(String parentFileName) {
      super.relativeToAbsolutePaths(parentFileName);

      String parentDir = (new File(parentFileName)).getParent();

      rraTool().setDesiredKinematicsFileName(FileUtils.makePathAbsolute(rraTool().getDesiredKinematicsFileName(), parentDir));
      rraTool().setConstraintsFileName(FileUtils.makePathAbsolute(rraTool().getConstraintsFileName(), parentDir));
      rraTool().setTaskSetFileName(FileUtils.makePathAbsolute(rraTool().getTaskSetFileName(), parentDir));
      //rraTool().setRRAControlsFileName(FileUtils.makePathAbsolute(rraTool().getRRAControlsFileName(), parentDir));
      rraTool().setOutputModelFileName(FileUtils.makePathAbsolute(rraTool().getOutputModelFileName(), parentDir));

      rraTool().setExternalLoadsFileName(FileUtils.makePathAbsolute(rraTool().getExternalLoadsFileName(), parentDir));
   }

   protected void AbsoluteToRelativePaths(String parentFileName) {
      super.AbsoluteToRelativePaths(parentFileName);
      String parentDir = (new File(parentFileName)).getParent();
      rraTool().setDesiredKinematicsFileName(FileUtils.makePathRelative(rraTool().getDesiredKinematicsFileName(), parentDir));
      rraTool().setConstraintsFileName(FileUtils.makePathRelative(rraTool().getConstraintsFileName(), parentDir));
      rraTool().setTaskSetFileName(FileUtils.makePathRelative(rraTool().getTaskSetFileName(), parentDir));
      //rraTool().setRRAControlsFileName(FileUtils.makePathRelative(rraTool().getRRAControlsFileName(), parentDir));
      rraTool().setOutputModelFileName(FileUtils.makePathRelative(rraTool().getOutputModelFileName(), parentDir));

      rraTool().setExternalLoadsFileName(FileUtils.makePathRelative(rraTool().getExternalLoadsFileName(), parentDir));
   }
   
   public boolean loadSettings(String fileName) {
      // TODO: set current working directory before trying to read it?
      RRATool newCMCTool = null;
      try {
         // TODO: pass it our model instead
         newCMCTool = new RRATool(fileName, false);
      } catch (IOException ex) {
         ErrorDialog.displayIOExceptionDialog("Error loading file","Could not load "+fileName,ex);
         return false;
      }

      setTool(newCMCTool);
      relativeToAbsolutePaths(fileName);
      updateFromTool();
      setModified(AbstractToolModel.Operation.AllDataChanged);
      return true;
   }

   public boolean saveSettings(String fileName) {
      String fullFilename = FileUtils.addExtensionIfNeeded(fileName, ".xml");
      updateTool();
      AbsoluteToRelativePaths(fullFilename);
      rraTool().print(fullFilename);
      relativeToAbsolutePaths(fullFilename);
      return true;
   }

    void setPlotMetrics(boolean b) {
        plotMatrics=b;
    }

    void setReuseSelectedMetrics(boolean b) {
        reuseMetrics=b;
    }
}

