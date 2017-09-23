/* -------------------------------------------------------------------------- *
 * OpenSim: IKToolModel.java                                                  *
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
import org.opensim.modeling.InverseKinematicsTool;
import org.opensim.modeling.InterruptCallback;
//import org.opensim.modeling.InterruptingIntegCallback;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimContext;
import org.opensim.modeling.Storage;
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
public class IKToolModel extends Observable implements Observer {
   //========================================================================
   // IKToolWorker
   //========================================================================
   class IKToolWorker extends SwingWorker {
      private ProgressHandle progressHandle = null;
      private JavaMotionDisplayerCallback animationCallback = null;
      private InterruptCallback interruptingCallback = null;
      boolean result = false;
      boolean promptToKeepPartialResult = true;
      boolean cleanup=true;
      //private Model modelCopy = null;
      final OpenSimContext context=OpenSimDB.getInstance().getContext(getOriginalModel());
      
      IKToolWorker() throws Exception {
         // Give the thread a nudge so that we're not much slower than command line'
         setPriority(Thread.MAX_PRIORITY);
         
         updateIKTool();

         // Operate on a copy of the model -- this way if users play with parameters in the GUI it won't affect the model we're actually computing on
         ikTool.setModel(getOriginalModel());

         // Make no motion be currently selected (so model doesn't have extraneous ground forces/experimental markers from
         // another motion show up on it)
         MotionsDB.getInstance().clearCurrent();

         // Initialize progress bar, given we know the number of frames to process
         double startTime = ikTool.getStartTime();
         double endTime = ikTool.getEndTime();
         progressHandle = ProgressHandleFactory.createHandle("Executing inverse kinematics...",
                              new Cancellable() {
                                 public boolean cancel() {
                                    interrupt(true);
                                    SimulationDB.getInstance().fireToolFinish();
                                    return true;
                                 }
                              });

         // Animation callback will update the display during IK solve
         animationCallback = new JavaMotionDisplayerCallback(getOriginalModel(),null/* ikTool.getOutputStorage()*/, progressHandle, true);
         //OpenSim20 animationCallback.setRenderMuscleActivations(false);
         //OpenSim20 animationCallback.setModelForDisplaySetConfiguration(false);
         getOriginalModel().addAnalysis(animationCallback);
         animationCallback.setStepInterval(1);
         animationCallback.startProgressUsingTime(startTime, endTime);

         // Do this manouver (there's gotta be a nicer way) to create the object so that C++ owns it and not Java (since 
         // removeIntegCallback in finished() will cause the C++-side callback to be deleted, and if Java owned this object
         // it would then later try to delete it yet again)
         interruptingCallback = new InterruptCallback(getOriginalModel());
         getOriginalModel().addAnalysis(interruptingCallback);
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
           ikTool.run();
         }
         catch(Exception ex) {
            progressHandle.finish();
            SimulationDB.getInstance().fireToolFinish();
            worker=null;
            cleanup=false;
            getOriginalModel().removeAnalysis(interruptingCallback, false);
            
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

            getOriginalModel().removeAnalysis(animationCallback, false);
            getOriginalModel().removeAnalysis(interruptingCallback, false);
            interruptingCallback = null;

            if (result) {
                resetModified();
            }

            boolean addMotion = true;
            if (!result) {
                boolean havePartialResult = false;//OpenSim23 ikTool.getOutputStorage()!=null && ikTool.getOutputStorage().getSize()>0;
                if (havePartialResult && promptToKeepPartialResult) {
                    Object answer = DialogDisplayer.getDefault().notify(new NotifyDescriptor.Confirmation("Inverse kinematics did not complete.  Keep partial result?", NotifyDescriptor.YES_NO_OPTION));
                    if (answer == NotifyDescriptor.NO_OPTION) {
                        addMotion = false;
                    }
                } else {
                    addMotion = false;
                }
            }
            // Create a new States storage and load it in GUI 
            // this should make playback faster since no need to assemble
            Storage ikmotion = new Storage(512, "IKResults"); // Java-side copy
            getOriginalModel().formStateStorage(
                    animationCallback.getStorage(),
                    ikmotion, false);
            updateMotion(ikmotion);

            setExecuting(false);

            //modelCopy = null;
            worker = null;
        }
    }
   private IKToolWorker worker = null;
   //========================================================================
   // END IKToolWorker
   //========================================================================

   public enum Operation { AllDataChanged, IKTrialNameChanged, IKTaskSetChanged, ExecutionStateChanged };

   private InverseKinematicsTool ikTool = null;
   private Model originalModel = null;
   private boolean modifiedSinceLastExecute = true;
   private IKCommonModel ikCommonModel;
   private Storage motion = null;
   private boolean executing = false;
   private String trialName = "ik trial";
   private boolean cleanupAfterExecuting = false;  // Keep track if cleaning up needs to be done on execution finish vs. dialog close

   public IKToolModel(Model originalModel) throws IOException {
      // Store original model
      this.originalModel = originalModel;

      // Create IK tool
      ikTool = new InverseKinematicsTool();
      //addTrialIfNecessary();

      ikCommonModel = new IKCommonModel(originalModel);
      ikCommonModel.addObserver(this);
   }

   public Model getOriginalModel() { return originalModel; }
   public InverseKinematicsTool getIKTool() { return ikTool; }
   public IKCommonModel getIKCommonModel() { return ikCommonModel; }
   
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
      ikCommonModel.toInverseKinematicsTool(ikTool);
      //OpenSim23 ikTool.setPrintResultFiles(false);
   }

   public void execute() {  
      if(isModified() && worker==null) {
         try {
            worker = new IKToolWorker();
            SimulationDB.getInstance().fireToolStart();
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
      if(observable==ikCommonModel) setModified(Operation.IKTaskSetChanged);
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
      return ikCommonModel.isValid();
   }

   //------------------------------------------------------------------------
   // Load/Save Settings
   //------------------------------------------------------------------------
   
   private void relativeToAbsolutePaths(String parentFileName) {
      String parentDir = (new File(parentFileName)).getParent();
        ikTool.setMarkerDataFileName(FileUtils.makePathAbsolute(ikTool.getMarkerDataFileName(),parentDir));
        ikTool.setCoordinateFileName(FileUtils.makePathAbsolute(ikTool.getCoordinateFileName(),parentDir));
        ikTool.setOutputMotionFileName(FileUtils.makePathAbsolute(ikTool.getOutputMotionFileName(), parentDir));
  }

   private void AbsoluteToRelativePaths(String parentFileName) {
      String parentDir = (new File(parentFileName)).getParent();
      ikTool.setMarkerDataFileName(FileUtils.makePathRelative(ikTool.getMarkerDataFileName(),parentDir));
      ikTool.setCoordinateFileName(FileUtils.makePathRelative(ikTool.getCoordinateFileName(),parentDir));
      ikTool.setOutputMotionFileName(FileUtils.makePathRelative(ikTool.getOutputMotionFileName(), parentDir));
   }

   public boolean loadSettings(String fileName) {
      // TODO: set current working directory before trying to read it?
      InverseKinematicsTool newIKTool = null;
      try {
         newIKTool = new InverseKinematicsTool(fileName, false);
      } catch (IOException ex) {
         ErrorDialog.displayIOExceptionDialog("Error loading file","Could not load "+fileName,ex);
         return false;
      }
      ikTool = newIKTool;
      relativeToAbsolutePaths(fileName);

      ikCommonModel.fromIKTool(ikTool);

      setModified(Operation.AllDataChanged);
      return true;
   }

   public boolean saveSettings(String fileName) {
      String fullFilename = FileUtils.addExtensionIfNeeded(fileName, ".xml");
      /*
      XMLExternalFileChooserHelper helper = new XMLExternalFileChooserHelper(fullFilename);
      helper.addObject(ikTool.getIKTaskSet(), "IK Task Set");
      if(!helper.promptUser()) return false;*/
      ikTool.getIKTaskSet().setInlined(true);
      updateIKTool();
      AbsoluteToRelativePaths(fullFilename);
      ikTool.print(fullFilename);
      relativeToAbsolutePaths(fullFilename);
      return true;
   }
   
   void cleanup() {
      if (isExecuting()){
         cleanupAfterExecuting = true;
      }
      else{
         ikCommonModel.deleteObservers();
         ikCommonModel=null;
         ikTool = null;
         System.gc();
      }
   }
}
