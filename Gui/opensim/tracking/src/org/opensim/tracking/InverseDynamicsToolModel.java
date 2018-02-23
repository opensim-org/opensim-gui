/* -------------------------------------------------------------------------- *
 * OpenSim: InverseDynamicsToolModel.java                                     *
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
import java.util.ArrayList;
import java.util.prefs.Preferences;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Cancellable;
import org.opensim.modeling.AbstractTool;
import org.opensim.modeling.Analysis;
import org.opensim.modeling.ArrayStr;
import org.opensim.modeling.ExternalLoads;
import org.opensim.modeling.InverseDynamicsTool;
import org.opensim.modeling.InterruptCallback;
import org.opensim.modeling.Model;
import org.opensim.modeling.Storage;
import org.opensim.modeling.OpenSimContext;
import org.opensim.modeling.Tool;
import org.opensim.swingui.SwingWorker;
import org.opensim.utils.ErrorDialog;
import org.opensim.utils.FileUtils;
import org.opensim.utils.TheApp;
import org.opensim.view.motions.JavaMotionDisplayerCallback;
import org.opensim.view.motions.MotionsDB;

public class InverseDynamicsToolModel extends AbstractToolModelWithExternalLoads {
   //========================================================================
   // InverseDynamicsToolWorker
   //========================================================================
   class InverseDynamicsToolWorker extends SwingWorker {
      private ProgressHandle progressHandle = null;
      private JavaMotionDisplayerCallback animationCallback = null;
      private InterruptCallback interruptingCallback = null;
      boolean result = false;
      boolean promptToKeepPartialResult = true;
      private OpenSimContext context;
      
      InverseDynamicsToolWorker() throws IOException {
          
         updateTool();

         // Make no motion be currently selected (so model doesn't have extraneous ground forces/experimental markers from
         // another motion show up on it)
         MotionsDB.getInstance().clearCurrent();

         // Re-initialize our copy of the model
         Model workersModel = new Model(getOriginalModel());
         //workersModel.setName("workerModel");
         String tempFileName=getOriginalModel().getInputFileName();
         //int loc = tempFileName.lastIndexOf(".");
         workersModel.setInputFileName(tempFileName);

 
         // Update actuator set and contact force set based on settings in the tool, then call setup() and setModel()
         // setModel() will call addAnalysisSetToModel
         ArrayStr excludedGroups = new ArrayStr();
         excludedGroups.append("Muscles");
         idTool.setExcludedForces(excludedGroups);
         idTool.setModel(workersModel);
         context = new OpenSimContext(workersModel.initSystem(), workersModel); // Has side effect of calling setup
        
         workersModel.setInputFileName("");
         
         if(getInputSource()==InputSource.Motion && getInputMotion()!=null)
            idTool.setCoordinateValues(getInputMotion());
         // We don't need to add model to the 3D view... just using it to dump analyses result files
         setModel(workersModel);
          //OpenSim23 workersModel.initSystem();
       
         // Initialize progress bar, given we know the number of frames to process
         double ti = getInitialTime();
         double tf = getFinalTime();
         progressHandle = ProgressHandleFactory.createHandle("Executing Inverse Dynamics...",
                              new Cancellable() {
                                 public boolean cancel() {
                                    interrupt(false);
                                    return true;
                                 }
                              });

         // Animation callback will update the display during forward
         animationCallback = new JavaMotionDisplayerCallback(getModel(), getOriginalModel(), null, progressHandle, false);
         getModel().addAnalysis(animationCallback);
         animationCallback.setStepInterval(1);
         animationCallback.setMinRenderTimeInterval(0.1); // to avoid rendering really frequently which can slow down our execution
         animationCallback.startProgressUsingTime(ti,tf);

         // Do this manouver (there's gotta be a nicer way) to create the object so that C++ owns it and not Java (since 
         // removeIntegCallback in finished() will cause the C++-side callback to be deleted, and if Java owned this object
         // it would then later try to delete it yet again)
         interruptingCallback = new InterruptCallback(getModel());
         getModel().addAnalysis(interruptingCallback);
         addResultDisplayers(getModel());   // Create all analyses that need to be created on analysis model
         setExecuting(true);
      }

      public void interrupt(boolean promptToKeepPartialResult) {
         this.promptToKeepPartialResult = promptToKeepPartialResult;
         if(interruptingCallback!=null) interruptingCallback.interrupt();
      }

      public Object construct() {
         try {
            result = idTool.run();
         } catch (IOException ex) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message("Tool execution failed. Check Messages window for details."));
            ex.printStackTrace();
         }

         return this;
      }

      public void finished() {
         boolean processResults = result;
         if(!result) { // TODO: prompt to keep partial results?
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                    "Tool execution failed or canceled by user.  Output files were not written."));
         }

         progressHandle.finish();

         // Clean up motion displayer (this is necessary!)
         animationCallback.cleanupMotionDisplayer();
         Storage motion=null;
         if (getInputSource()==InputSource.Motion){
            motion = getInputMotion();
         }
         else { // Assume Coordinates file
            try {
                motion = new Storage(InverseDynamicsTool().getCoordinatesFileName());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
         }
         if (motion!=null){
            // Create a new States storage and load it in GUI 
            // this should make playback faster since no need to assemble
            Storage idmotion = new Storage(512, "IDResults"); // Java-side copy
            if (motion.isInDegrees()){
                getOriginalModel().getSimbodyEngine().convertDegreesToRadians(motion);
            }
            getOriginalModel().formStateStorage(
                    motion,
                    idmotion, false);
            updateMotion(idmotion); // replaces current motion

         }
         getModel().removeAnalysis(animationCallback, false);
         getModel().removeAnalysis(interruptingCallback, false);
         interruptingCallback = null;

         if(result) resetModified();

         setExecuting(false);

         worker = null;
      }

      private void updateMotion(Storage newMotion) {
          if(motion!=null) {
              MotionsDB.getInstance().closeMotion(getOriginalModel(), motion, false, false);
          }
          motion = newMotion;
          motion.crop(InverseDynamicsTool().getStartTime(), InverseDynamicsTool().getEndTime());
          if(motion!=null) {
              MotionsDB.getInstance().addMotion(getOriginalModel(), motion, null);
              MotionsDB.getInstance().setCurrentTime(motion.getLastTime());
              
          }
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
   private InverseDynamicsToolWorker worker = null;
   //========================================================================
   // END InverseDynamicsToolWorker
   //========================================================================
   private Storage motion = null;

   private boolean inverseDynamicsMode = false;

   enum InputSource { Motion, States, Coordinates, Unspecified };
   InputSource inputSource = InputSource.Unspecified;
   private Storage inputMotion = null;
   private boolean loadSpeeds = false;
   private String toolName;
   private InverseDynamicsTool idTool = new InverseDynamicsTool();
   public InverseDynamicsToolModel(Model model) throws IOException {
      super(model);

      toolName = "Inverse Dynamics Tool";

      //OpenSim23 InverseDynamicsTool dTool = new InverseDynamicsTool();
      idTool.setModel(model);
      //OpenSim23 setTool(dTool);

      // By default, set prefix of output to be subject name
      InverseDynamicsTool().setName(model.getName());

      setDefaultResultsDirectory(model);

      adjustToolForMode();
      updateFromTool();

      determineDefaultInputSource();
   }

   InverseDynamicsTool InverseDynamicsTool() { return idTool; }

   //------------------------------------------------------------------------
   // Utilities for inverse dynamics specific analyze tool
   //------------------------------------------------------------------------

   protected void adjustToolForMode() {
        ArrayStr excludedGroups = new ArrayStr();
        excludedGroups.append("Muscles");
        InverseDynamicsTool().setExcludedForces(excludedGroups);    
  }

   //------------------------------------------------------------------------
   // Default input source
   //------------------------------------------------------------------------

   protected void determineDefaultInputSource() {
      inputSource = InputSource.Unspecified;
      MotionsDB mdb = MotionsDB.getInstance();
      ArrayList<Storage> motions = mdb.getModelMotions(getOriginalModel());
      if(motions!=null && motions.size()>0) {
         // First check if one of the current motions is of this model
         for(int i=0; i<mdb.getNumCurrentMotions(); i++)
            if(mdb.getCurrentMotion(i).model==getOriginalModel()) {
               inputMotion = mdb.getCurrentMotion(i).motion;
               inputSource = InputSource.Motion;
               InverseDynamicsTool().setStartTime(inputMotion.getFirstTime());
               InverseDynamicsTool().setEndTime(inputMotion.getLastTime());
               setModified(AbstractToolModel.Operation.InputDataChanged);
               return;
            }
         // If not, then pick the first of this model's motions
         inputMotion = motions.get(0);
         inputSource = InputSource.Motion;
      }
      else   
      inputSource = InputSource.Coordinates;
   }

   //------------------------------------------------------------------------
   // Get/Set Values
   //------------------------------------------------------------------------

   public InputSource getInputSource() { return inputSource; }
   public void setInputSource(InputSource inputSource) { 
      if(this.inputSource != inputSource) {
         this.inputSource = inputSource;
         setModified(AbstractToolModel.Operation.InputDataChanged);
      }
   }

   public Storage getInputMotion() { return inputMotion; }
   public void setInputMotion(Storage inputMotion) { 
      if(this.inputMotion != inputMotion) {
         this.inputMotion = inputMotion;
         setModified(AbstractToolModel.Operation.InputDataChanged);
      }
   }

   public String getStatesFileName() { return ""; }
   void setStatesFileName(String speedsFileName) {
      if(!getStatesFileName().equals(speedsFileName)) {
        // InverseDynamicsTool().setStatesFileName(speedsFileName);
         setModified(AbstractToolModel.Operation.InputDataChanged);
      }
   }
   public boolean getStatesValid() { return (new File(getStatesFileName()).exists()); }

   public String getCoordinatesFileName() { return InverseDynamicsTool().getCoordinatesFileName(); }
   void setCoordinatesFileName(String coordinatesFileName) {
      if(!getCoordinatesFileName().equals(coordinatesFileName)) {
         InverseDynamicsTool().setCoordinatesFileName(coordinatesFileName);
         setInputSource(InputSource.Coordinates);
         setModified(AbstractToolModel.Operation.InputDataChanged);
      }
   }
   public boolean getCoordinatesValid() { return (new File(getCoordinatesFileName()).exists()); }

   public boolean getFilterCoordinates() { return getLowpassCutoffFrequency() > 0; }
   
   void setLowpassCutoffFrequency(double d) {
       InverseDynamicsTool().setLowpassCutoffFrequency(d);
   }

   double getLowpassCutoffFrequency() {
       double f = InverseDynamicsTool().getLowpassCutoffFrequency();
        return InverseDynamicsTool().getLowpassCutoffFrequency();
   }

   void setFilterCoordinates(boolean b) {
       if (b)
            InverseDynamicsTool().setLowpassCutoffFrequency(6);
       else
            InverseDynamicsTool().setLowpassCutoffFrequency(-1);
   }
   
   // TODO: implement
   public double[] getAvailableTimeRange() { 
      double range[] = null;
      if(getInputSource()==InputSource.Motion && getInputMotion()!=null) 
         range = new double[]{getInputMotion().getFirstTime(), getInputMotion().getLastTime()};
      return range;
   }

   //------------------------------------------------------------------------
   // External loads get/set (don't need to call setModified since AbstractToolModel does that)
   //------------------------------------------------------------------------
   public String getExternalLoadsFileName() { return idTool.getExternalLoadsFileName(); }
   protected void setExternalLoadsFileNameInternal(String fileName) {  idTool.setExternalLoadsFileName(fileName);}

   public String getExternalLoadsModelKinematicsFileName() { return idTool.getExternalLoads().getExternalLoadsModelKinematicsFileName(); }
   protected void setExternalLoadsModelKinematicsFileNameInternal(String fileName) { idTool.getExternalLoads().setExternalLoadsModelKinematicsFileName(fileName); }

   public double getLowpassCutoffFrequencyForLoadKinematics() { return idTool.getExternalLoads().getLowpassCutoffFrequencyForLoadKinematics(); }
   protected void setLowpassCutoffFrequencyForLoadKinematicsInternal(double cutoffFrequency) { idTool.getExternalLoads().setLowpassCutoffFrequencyForLoadKinematics(cutoffFrequency); }

   //------------------------------------------------------------------------
   // Utilities for running/canceling tool
   //------------------------------------------------------------------------

   public void execute() {
      if(isModified() && worker==null) {
         try {
            worker = new InverseDynamicsToolWorker();
         } catch (IOException ex) {
            setExecuting(false);
            ErrorDialog.displayIOExceptionDialog(toolName+" Error", "Tool initialization failed.", ex);
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
   }

   //------------------------------------------------------------------------
   // Validation
   //------------------------------------------------------------------------

   private boolean isValidInput() {
      return (getInputSource()==InputSource.Motion && getInputMotion()!=null) ||
             (getInputSource()==InputSource.States && getStatesValid()) ||
             (getInputSource()==InputSource.Coordinates && getCoordinatesValid()); // TODO check SpeedsValid once we re-enable speeds
   }

   public boolean isValidated() {
      return super.isValidated() && isValidInput();
   }

   //------------------------------------------------------------------------
   // Load/Save Settings
   //------------------------------------------------------------------------

   protected void updateFromTool() {
      super.updateFromTool();

      if(!FileUtils.effectivelyNull(getStatesFileName())) inputSource = InputSource.States;
      else if(!FileUtils.effectivelyNull(getCoordinatesFileName())) inputSource = InputSource.Coordinates;
      else inputSource = InputSource.Unspecified;
      // update filtering options here
       if(inputSource == InputSource.Coordinates) setCoordinatesFileName(InverseDynamicsTool().getCoordinatesFileName());
      
   }

   protected void updateTool() {
      super.updateTool();

      // The C++ code determines whether we're using states or coordinates as input based on whether the file names are nonempty
      if(inputSource != InputSource.Coordinates) {
         InverseDynamicsTool().setCoordinatesFileName("");
      } 
      if(inputSource != InputSource.States) {
        //InverseDynamicsTool().setCoordinatesFileName("");
      }
      setModified(AbstractToolModel.Operation.AllDataChanged);
      
   }

   protected void relativeToAbsolutePaths(String parentFileName) {
      //OpenSim23 super.relativeToAbsolutePaths(parentFileName);

      String parentDir = (new File(parentFileName)).getParent();

      //OpenSim23 InverseDynamicsTool().setStatesFileName(FileUtils.makePathAbsolute(InverseDynamicsTool().getStatesFileName(), parentDir));
      InverseDynamicsTool().setCoordinatesFileName(FileUtils.makePathAbsolute(InverseDynamicsTool().getCoordinatesFileName(), parentDir));

      InverseDynamicsTool().setExternalLoadsFileName(FileUtils.makePathAbsolute(InverseDynamicsTool().getExternalLoadsFileName(), parentDir));
      InverseDynamicsTool().getExternalLoads().setExternalLoadsModelKinematicsFileName(FileUtils.makePathAbsolute(InverseDynamicsTool().getExternalLoads().getExternalLoadsModelKinematicsFileName(), parentDir));
   }

   protected void AbsoluteToRelativePaths(String parentFileName) {
       //OpenSim23 super.AbsoluteToRelativePaths(parentFileName);

      String parentDir = (new File(parentFileName)).getParent();

      //OpenSim23 InverseDynamicsTool().setStatesFileName(FileUtils.makePathRelative(InverseDynamicsTool().getStatesFileName(), parentDir));
      InverseDynamicsTool().setCoordinatesFileName(FileUtils.makePathRelative(InverseDynamicsTool().getCoordinatesFileName(), parentDir));

      InverseDynamicsTool().setExternalLoadsFileName(FileUtils.makePathRelative(InverseDynamicsTool().getExternalLoadsFileName(), parentDir));
      InverseDynamicsTool().getExternalLoads().setExternalLoadsModelKinematicsFileName(FileUtils.makePathRelative(InverseDynamicsTool().getExternalLoads().getExternalLoadsModelKinematicsFileName(), parentDir));
   }
   
   public boolean loadSettings(String fileName) {
      // TODO: set current working directory before trying to read it?
      InverseDynamicsTool newInverseDynamicsTool = null;
      try {
         // TODO: pass it our model instead
         newInverseDynamicsTool = new InverseDynamicsTool(fileName, false);
      } catch (IOException ex) {
         ErrorDialog.displayIOExceptionDialog("Error loading file","Could not load "+fileName,ex);
         return false;
      }
      idTool = newInverseDynamicsTool;
      //setTool(newInverseDynamicsTool);
      relativeToAbsolutePaths(fileName);
      adjustToolForMode();
      updateFromTool();
      setModified(AbstractToolModel.Operation.AllDataChanged);
      return true;
   }

   public boolean saveSettings(String fileName) {
      String fullFilename = FileUtils.addExtensionIfNeeded(fileName, ".xml");
      updateTool();
      AbsoluteToRelativePaths(fullFilename);
      InverseDynamicsTool().print(fullFilename);
      relativeToAbsolutePaths(fullFilename);
      return true;
   }
   protected void setDefaultResultsDirectory(Model model) {
      // Try to come up with a reasonable output directory
      if(!model.getInputFileName().equals("")) idTool.setResultsDir((new File(model.getInputFileName())).getParent());
      else idTool.setResultsDir(Preferences.userNodeForPackage(TheApp.class).get("WorkDirectory", ""));
   }

    public InverseDynamicsTool getIdTool() {
        return idTool;
    }
   public boolean getReplaceForceSet() { return false; }
   public void setReplaceForceSet(boolean replace) { 
     
   }
   public ArrayStr getForceSetFiles() { return new ArrayStr(); }
   //------------------------------------------------------------------------
   // Time range settings
   //------------------------------------------------------------------------
   public double getInitialTime() { return idTool.getStartTime(); }
   public void setInitialTime(double time) {
      if(getInitialTime() != time) {
         idTool.setStartTime(time);
         setModified(Operation.TimeRangeChanged);
      }
   }

   public double getFinalTime() { return idTool.getEndTime(); }
   public void setFinalTime(double time) {
      if(getFinalTime() != time) {
         idTool.setEndTime(time);
         setModified(Operation.TimeRangeChanged);
      }
   }
   public String getOutputPrefix() { return ""; } // It's the name of the tool
   public String getResultsDirectory() { return idTool.getResultsDir(); }
   public void setResultsDirectory(String directory) {
      if(!getResultsDirectory().equals(directory)) {
         idTool.setResultsDir(directory);
         setModified(Operation.OutputDataChanged);
      }
   }

    public ExternalLoads getExternalLoads() {
        String extLoadsFile = idTool.getExternalLoadsFileName();
        idTool.createExternalLoads(extLoadsFile, getOriginalModel());
        return idTool.getExternalLoads();
    }

}

