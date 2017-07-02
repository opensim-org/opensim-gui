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
import java.util.ArrayList;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Cancellable;
import org.opensim.modeling.Analysis;
import org.opensim.modeling.AnalyzeTool;
import org.opensim.modeling.InterruptCallback;
import org.opensim.modeling.Model;
import org.opensim.modeling.Storage;
import org.opensim.modeling.InverseDynamics;
import org.opensim.modeling.OpenSimContext;
import org.opensim.modeling.StaticOptimization;
import org.opensim.swingui.SwingWorker;
import org.opensim.tracking.tools.SimulationDB;
import org.opensim.utils.ErrorDialog;
import org.opensim.utils.FileUtils;
import org.opensim.view.MuscleColorByActivationStorage;
import org.opensim.view.MuscleColoringFunction;
import org.opensim.view.motions.JavaMotionDisplayerCallback;
import org.opensim.view.motions.MotionDisplayer;
import org.opensim.view.motions.MotionsDB;
import org.opensim.view.pub.OpenSimDB;

public class AnalyzeToolModel extends AbstractToolModelWithExternalLoads {
   //========================================================================
   // AnalyzeToolWorker
   //========================================================================
   class AnalyzeToolWorker extends SwingWorker {
      private ProgressHandle progressHandle = null;
      private JavaMotionDisplayerCallback animationCallback = null;
      private InterruptCallback interruptingCallback = null;
      boolean result = false;
      boolean promptToKeepPartialResult = true;
      private OpenSimContext context;
      
      AnalyzeToolWorker() throws IOException {
          
         updateTool();

         // Make no motion be currently selected (so model doesn't have extraneous ground forces/experimental markers from
         // another motion show up on it)
         MotionsDB.getInstance().clearCurrent();

         // Re-initialize our copy of the model
         Model workersModel = Model.safeDownCast(getOriginalModel().clone());
         workersModel.setName("workerModel");
         String tempFileName=getOriginalModel().getInputFileName();
         //int loc = tempFileName.lastIndexOf(".");
         workersModel.setInputFileName(tempFileName);

 
         // Update actuator set and contact force set based on settings in the tool, then call setup() and setModel()
         // setModel() will call addAnalysisSetToModel
         tool.updateModelForces(workersModel, "");
         workersModel.initSystem();
         tool.setModel(workersModel);
         tool.setToolOwnsModel(false);
         context = new OpenSimContext(workersModel.initSystem(), workersModel); // Has side effect of calling setup
        
         workersModel.setInputFileName("");
         
         if(getInputSource()==InputSource.Motion && getInputMotion()!=null)
            context.setStatesFromMotion(analyzeTool(), getInputMotion(),false); // false == motion is in radians
         else
            context.loadStatesFromFile(analyzeTool());
         //opensim20 analyzeTool().loadControlsFromFile();
         //opensim20 analyzeTool().loadPseudoStatesFromFile();

         // We don't need to add model to the 3D view... just using it to dump analyses result files
         setModel(workersModel);
         workersModel.initSystem();
       
         // Initialize progress bar, given we know the number of frames to process
         double ti = getInitialTime();
         double tf = getFinalTime();
         progressHandle = ProgressHandleFactory.createHandle("Executing analyses...",
                              new Cancellable() {
                                 public boolean cancel() {
                                    interrupt(false);
                                    SimulationDB.getInstance().fireToolFinish();
                                    return true;
                                 }
                              });

         // Animation callback will update the display during forward
         animationCallback = new JavaMotionDisplayerCallback(getModel(), getOriginalModel(), null, progressHandle, staticOptimizationMode);
         getModel().addAnalysis(animationCallback);
         animationCallback.setStepInterval(1);
         animationCallback.setMinRenderTimeInterval(0.1); // to avoid rendering really frequently which can slow down our execution
         animationCallback.startProgressUsingTime(ti,tf);
         animationCallback.setDisplayTimeProgress(true);
         
         // Do this manouver (there's gotta be a nicer way) to create the object so that C++ owns it and not Java (since 
         // removeIntegCallback in finished() will cause the C++-side callback to be deleted, and if Java owned this object
         // it would then later try to delete it yet again)
         interruptingCallback = new InterruptCallback(getModel());
         getModel().addAnalysis(interruptingCallback);
         addResultDisplayers(getModel());   // Create all analyses that need to be created on analysis model
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
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message("Tool execution failed. Check Messages window for details."));
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

         progressHandle.finish();

         // Clean up motion displayer (this is necessary!)
         animationCallback.cleanupMotionDisplayer();

         Storage motion = null;
         if(analyzeTool().getStatesStorage()!=null) {
               motion = new Storage(analyzeTool().getStatesStorage());
               //motion.resampleLinear(0.001);
         }
         updateMotion(motion); // replaces current motion
         if (staticOptimizationMode){ // Color by activations from SO
             // Color by Activation from SO output
            StaticOptimization soA = StaticOptimization.safeDownCast(getModel().getAnalysisSet().get("StaticOptimization"));
            Storage storage = soA.getActivationStorage();
            MotionDisplayer motionDisplayer = new MotionDisplayer(storage, getOriginalModel());
            MuscleColoringFunction mcbya = new MuscleColorByActivationStorage(
            OpenSimDB.getInstance().getContext(getOriginalModel()), storage);
            motionDisplayer.setMuscleColoringFunction(mcbya);
         } 
         getModel().removeAnalysis(animationCallback, false);
         getModel().removeAnalysis(interruptingCallback, false);
         interruptingCallback = null;

         if(result) resetModified();

         setExecuting(false);
         SimulationDB.getInstance().fireToolFinish();

         worker = null;
      }

      private void updateMotion(Storage newMotion) {
          if(motion!=null) {
              MotionsDB.getInstance().closeMotion(getOriginalModel(), motion, false, false);
          }
          motion = newMotion;
          motion.crop(analyzeTool().getStartTime(), analyzeTool().getFinalTime());
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
   private AnalyzeToolWorker worker = null;
   //========================================================================
   // END AnalyzeToolWorker
   //========================================================================
   private Storage motion = null;

   private boolean staticOptimizationMode = false;

   enum InputSource { Motion, States, Coordinates, Unspecified };
   private InputSource inputSource = InputSource.Unspecified;
   private Storage inputMotion = null;
   private boolean loadSpeeds = false;
   private boolean controlsEnabled = false;
   private String toolName;
   private double activationExponent=0.0;
   
   public AnalyzeToolModel(Model model, AnalyzeAndForwardToolPanel.Mode dMode) throws IOException {
      super(model);

      this.staticOptimizationMode = (dMode==AnalyzeAndForwardToolPanel.Mode.StaticOptimization);
      if (staticOptimizationMode) toolName = "Static Optimization Tool";
      else toolName = "Analyze Tool";

      // In inverse dynamisc mode, we know for sure we'll need a real dynamics engine, so check this up front
      if(staticOptimizationMode && model.getSimbodyEngine().getConcreteClassName().equals("SimmKinematicsEngine"))
         throw new IOException(toolName+" requires a model with SdfastEngine or SimbodyEngine; SimmKinematicsEngine does not support dynamics.");

      AnalyzeTool dTool = new AnalyzeTool();
      dTool.setModel(model);
      setTool(dTool);

      // By default, set prefix of output to be subject name
      analyzeTool().setName(model.getName());

      setDefaultResultsDirectory(model);

      adjustToolForMode();
      updateFromTool();

      determineDefaultInputSource();
   }

   AnalyzeTool analyzeTool() { return (AnalyzeTool)tool; }

   //------------------------------------------------------------------------
   // Utilities for inverse dynamics specific analyze tool
   //------------------------------------------------------------------------

   protected void adjustToolForMode() {
      if(!staticOptimizationMode) return;
      // Check if have non-inverse dynamics analyses, or multiple inverse dynamics analyses
      boolean foundOtherAnalysis = false;
      boolean advancedSettings = false;
      int numFoundAnalyses = 0;
      InverseDynamics inverseDynamicsAnalysis = null;
      int numStaticOptimizationAnalyses = 0;
      StaticOptimization staticOptimizationAnalysis = null;
      if (false){
        // Since we're not using the model's actuator set, clear the actuator set related fields
        analyzeTool().setReplaceForceSet(true);
        analyzeTool().getForceSetFiles().setSize(0);
        // Mode is either InverseDynamics or StaticOptimization
          for(int i=analyzeTool().getAnalysisSet().getSize()-1; i>=0; i--) {
              Analysis analysis = analyzeTool().getAnalysisSet().get(i);
              //System.out.println(" PROCESSING ANALYSIS "+analysis.getType()+","+analysis.getName());
              if(InverseDynamics.safeDownCast(analysis)==null) {
                  foundOtherAnalysis = true;
                  analyzeTool().getAnalysisSet().remove(i);
              } else{
                  numFoundAnalyses++;
                  if(numFoundAnalyses==1) {
                      inverseDynamicsAnalysis = InverseDynamics.safeDownCast(analysis);
                      if(inverseDynamicsAnalysis.getUseModelForceSet() || !inverseDynamicsAnalysis.getOn())
                          advancedSettings = true;
                  } else {
                      analyzeTool().getAnalysisSet().remove(i);
                  }
              }
          }
          if(inverseDynamicsAnalysis==null) {
              inverseDynamicsAnalysis = InverseDynamics.safeDownCast(new InverseDynamics().clone());
              setAnalysisTimeFromTool(inverseDynamicsAnalysis);
              //analyzeTool().addAnalysis(inverseDynamicsAnalysis);
          }
          inverseDynamicsAnalysis.setOn(true);
          inverseDynamicsAnalysis.setUseModelForceSet(false);
          
      }
      else {    // StaticOptimization assumed
          // Mode is StaticOptimization
          for(int i=analyzeTool().getAnalysisSet().getSize()-1; i>=0; i--) {
              Analysis analysis = analyzeTool().getAnalysisSet().get(i);
              //System.out.println(" PROCESSING ANALYSIS "+analysis.getType()+","+analysis.getName());
              if(StaticOptimization.safeDownCast(analysis)==null) {
                  foundOtherAnalysis = true;
                  analyzeTool().getAnalysisSet().remove(i);
              } else{
                  numFoundAnalyses++;
                  if(numFoundAnalyses==1) {
                      staticOptimizationAnalysis = StaticOptimization.safeDownCast(analysis);
                      //if(staticOptimizationAnalysis.getUseModelActuatorSet() || !staticOptimizationAnalysis.getOn())
                       //   advancedSettings = true;
                  } else {
                      analyzeTool().getAnalysisSet().remove(i);
                  }
              }
          }
          if(staticOptimizationAnalysis==null) {
              staticOptimizationAnalysis = new StaticOptimization(); 
              analyzeTool().getAnalysisSet().setMemoryOwner(false);
              analyzeTool().getAnalysisSet().adoptAndAppend(staticOptimizationAnalysis);
          }
          staticOptimizationAnalysis.setOn(true);
          staticOptimizationAnalysis.setUseModelForceSet(true);
          analyzeTool().setReplaceForceSet(false);
      }
      if(foundOtherAnalysis || advancedSettings || numFoundAnalyses>1) {
          String message = "";
          if(foundOtherAnalysis) message = "Settings file contained analyses other than requested.  The tool will ignore these.\n";
          if(numFoundAnalyses>1) message += "More than one analysis was found.  Extras will be ignored.\n";
          if(advancedSettings) message += "Settings file contained an analysis with advanced settings which will be ignored by the tool.\n";
          message += "Please use the analyze tool if you wish to handle different analysis types and advanced analysis settings.\n";
          DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(message, NotifyDescriptor.WARNING_MESSAGE));
      }
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
               updateToolTimeRange(inputMotion);
               return;
            }
         // If not, then pick the first of this model's motions
         inputMotion = motions.get(0);
         inputSource = InputSource.Motion;
      }
      if (staticOptimizationMode){  // Surgically change inputSource based on fields
          inputSource = InputSource.Coordinates;
      }
   }

    private void updateToolTimeRange(Storage storage) {
        analyzeTool().setStartTime(storage.getFirstTime());
        analyzeTool().setFinalTime(storage.getLastTime());
        setModified(AbstractToolModel.Operation.TimeRangeChanged);
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

   public String getControlsFileName() { return analyzeTool().getControlsFileName(); }
   void setControlsFileName(String speedsFileName) {
      if(!getControlsFileName().equals(speedsFileName)) {
         analyzeTool().setControlsFileName(speedsFileName);
         setModified(AbstractToolModel.Operation.InputDataChanged);
      }
   }
   public boolean getControlsValid() { return !getControlsEnabled() || (new File(getControlsFileName()).exists()); }
   public boolean getControlsEnabled() { return controlsEnabled; }
   public void setControlsEnabled(boolean enabled) {
      if(controlsEnabled != enabled) {
         controlsEnabled = enabled;
         setModified(AbstractToolModel.Operation.InputDataChanged);
      }
   }

   public String getStatesFileName() { return analyzeTool().getStatesFileName(); }
   void setStatesFileName(String speedsFileName) {
      if(!getStatesFileName().equals(speedsFileName)) {
         analyzeTool().setStatesFileName(speedsFileName);
         setModified(AbstractToolModel.Operation.InputDataChanged);
      }
   }
   public boolean getStatesValid() { return (new File(getStatesFileName()).exists()); }

  /*opensim20

   public boolean needPseudoStates() { return getOriginalModel().getNumPseudoStates()>0; }
   public String getPseudoStatesFileName() { return analyzeTool().getPseudoStatesFileName(); }
   void setPseudoStatesFileName(String speedsFileName) {
      if(!getPseudoStatesFileName().equals(speedsFileName)) {
         analyzeTool().setPseudoStatesFileName(speedsFileName);
         setModified(AbstractToolModel.Operation.InputDataChanged);
      }
   }
*/
   public String getCoordinatesFileName() { return analyzeTool().getCoordinatesFileName(); }
   void setCoordinatesFileName(String coordinatesFileName) {
      if(!getCoordinatesFileName().equals(coordinatesFileName)) {
         analyzeTool().setCoordinatesFileName(coordinatesFileName);
         setInputSource(InputSource.Coordinates);
         setModified(AbstractToolModel.Operation.InputDataChanged);
         try {
            Storage coords = new Storage(coordinatesFileName);
            updateToolTimeRange(coords);
         } catch (IOException ex) {
            ex.printStackTrace();
      }
   }
   }
   public boolean getCoordinatesValid() { return (new File(getCoordinatesFileName()).exists()); }

   public String getSpeedsFileName() { return analyzeTool().getSpeedsFileName(); }
   void setSpeedsFileName(String speedsFileName) {
      if(!getSpeedsFileName().equals(speedsFileName)) {
         analyzeTool().setSpeedsFileName(speedsFileName);
         setModified(AbstractToolModel.Operation.InputDataChanged);
      }
   }
   public boolean getSpeedsValid() { return !getLoadSpeeds() || (new File(getSpeedsFileName()).exists()); }
   public boolean getLoadSpeeds() { return loadSpeeds; }
   public void setLoadSpeeds(boolean loadSpeeds) { 
      if(this.loadSpeeds != loadSpeeds) {
         this.loadSpeeds = loadSpeeds;
         setModified(AbstractToolModel.Operation.InputDataChanged);
      }
   }

   public double getLowpassCutoffFrequency() { return analyzeTool().getLowpassCutoffFrequency(); }
   public void setLowpassCutoffFrequency(double frequency) {
      if(getLowpassCutoffFrequency() != frequency) {
         analyzeTool().setLowpassCutoffFrequency(frequency);
         setModified(AbstractToolModel.Operation.InputDataChanged);
      }
   }

   public boolean getFilterCoordinates() { return getLowpassCutoffFrequency() > 0; }
   public void setFilterCoordinates(boolean filterCoordinates) {
      if(getFilterCoordinates() != filterCoordinates) {
         if(filterCoordinates) analyzeTool().setLowpassCutoffFrequency(6);
         else analyzeTool().setLowpassCutoffFrequency(-1);
         setModified(AbstractToolModel.Operation.InputDataChanged);
      }
   }

   public double getActivationExponent() { 
      Analysis an= analyzeTool().getAnalysisSet().get("StaticOptimization");
      return  StaticOptimization.safeDownCast(an).getActivationExponent();
   }
   void setActivationExponent(double p) {
      if(!(getActivationExponent()==p)) {
         Analysis an= analyzeTool().getAnalysisSet().get("StaticOptimization");
         StaticOptimization.safeDownCast(an).setActivationExponent(p);
         setModified(AbstractToolModel.Operation.InputDataChanged);
      }
   }
   
   public int getAnalysisStepInterval() { 
      Analysis an= analyzeTool().getAnalysisSet().get("StaticOptimization");
      return  StaticOptimization.safeDownCast(an).getStepInterval();
   }
   
   void setAnalysisStepInterval(int p) {
         Analysis an= analyzeTool().getAnalysisSet().get("StaticOptimization");
         StaticOptimization.safeDownCast(an).setStepInterval(p);
         setModified(AbstractToolModel.Operation.InputDataChanged);
   }
   
   public boolean getUseMusclePhysiology() {
      Analysis an= analyzeTool().getAnalysisSet().get("StaticOptimization");
      return  StaticOptimization.safeDownCast(an).getUseMusclePhysiology();
   }
   public void setUseMusclePhysiology(boolean useIt) {
      Analysis an= analyzeTool().getAnalysisSet().get("StaticOptimization");
      boolean oldValue = StaticOptimization.safeDownCast(an).getUseMusclePhysiology();
      StaticOptimization.safeDownCast(an).setUseMusclePhysiology(useIt);
      analyzeTool().setSolveForEquilibrium(useIt);
      if (oldValue != useIt)
        setModified(AbstractToolModel.Operation.InputDataChanged);

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
   public String getExternalLoadsFileName() { return analyzeTool().getExternalLoadsFileName(); }
   protected void setExternalLoadsFileNameInternal(String fileName) { analyzeTool().setExternalLoadsFileName(fileName); }

   public String getExternalLoadsModelKinematicsFileName() { return analyzeTool().getExternalLoads().getExternalLoadsModelKinematicsFileName(); }
   protected void setExternalLoadsModelKinematicsFileNameInternal(String fileName) { analyzeTool().getExternalLoads().setExternalLoadsModelKinematicsFileName(fileName); }

   public double getLowpassCutoffFrequencyForLoadKinematics() { return analyzeTool().getExternalLoads().getLowpassCutoffFrequencyForLoadKinematics(); }
   protected void setLowpassCutoffFrequencyForLoadKinematicsInternal(double cutoffFrequency) { analyzeTool().getExternalLoads().setLowpassCutoffFrequencyForLoadKinematics(cutoffFrequency); }

   //------------------------------------------------------------------------
   // Utilities for running/canceling tool
   //------------------------------------------------------------------------

   public void execute() {
      if(isModified() && worker==null) {
         try {
            worker = new AnalyzeToolWorker();
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
      return super.isValidated() && isValidInput() && getControlsValid() && getAnalysisSet().getSize()>0;
   }

   //------------------------------------------------------------------------
   // Load/Save Settings
   //------------------------------------------------------------------------

   protected void updateFromTool() {
      super.updateFromTool();

      if(!FileUtils.effectivelyNull(getStatesFileName())) inputSource = InputSource.States;
      else if(!FileUtils.effectivelyNull(getCoordinatesFileName())) inputSource = InputSource.Coordinates;
      else inputSource = InputSource.Unspecified;

      loadSpeeds = (inputSource == InputSource.Coordinates && !FileUtils.effectivelyNull(getSpeedsFileName()));

      controlsEnabled = !FileUtils.effectivelyNull(getControlsFileName());
      
      if (staticOptimizationMode){
          activationExponent = getActivationExponent();
      }
   }

   protected void updateTool() {
      super.updateTool();

      // The C++ code determines whether we're using states or coordinates as input based on whether the file names are nonempty
      if(inputSource != InputSource.Coordinates) {
         analyzeTool().setCoordinatesFileName("");
      } 
      if(inputSource != InputSource.Coordinates || !getLoadSpeeds()) {
         analyzeTool().setSpeedsFileName("");
      }
      if(inputSource != InputSource.States) {
         analyzeTool().setStatesFileName("");
      }
      setModified(AbstractToolModel.Operation.AllDataChanged);
      
   }

   protected void relativeToAbsolutePaths(String parentFileName) {
      super.relativeToAbsolutePaths(parentFileName);

      String parentDir = (new File(parentFileName)).getParent();

      analyzeTool().setControlsFileName(FileUtils.makePathAbsolute(analyzeTool().getControlsFileName(), parentDir));
      analyzeTool().setStatesFileName(FileUtils.makePathAbsolute(analyzeTool().getStatesFileName(), parentDir));
      //analyzeTool().setPseudoStatesFileName(FileUtils.makePathAbsolute(analyzeTool().getPseudoStatesFileName(), parentDir));
      analyzeTool().setCoordinatesFileName(FileUtils.makePathAbsolute(analyzeTool().getCoordinatesFileName(), parentDir));
      analyzeTool().setSpeedsFileName(FileUtils.makePathAbsolute(analyzeTool().getSpeedsFileName(), parentDir));

      analyzeTool().setExternalLoadsFileName(FileUtils.makePathAbsolute(analyzeTool().getExternalLoadsFileName(), parentDir));
      //analyzeTool().setExternalLoadsModelKinematicsFileName(FileUtils.makePathAbsolute(analyzeTool().getExternalLoadsModelKinematicsFileName(), parentDir));
   }

   protected void AbsoluteToRelativePaths(String parentFileName) {
      super.AbsoluteToRelativePaths(parentFileName);

      String parentDir = (new File(parentFileName)).getParent();

      analyzeTool().setControlsFileName(FileUtils.makePathRelative(analyzeTool().getControlsFileName(), parentDir));
      analyzeTool().setStatesFileName(FileUtils.makePathRelative(analyzeTool().getStatesFileName(), parentDir));
 //     analyzeTool().setPseudoStatesFileName(FileUtils.makePathRelative(analyzeTool().getPseudoStatesFileName(), parentDir));
      analyzeTool().setCoordinatesFileName(FileUtils.makePathRelative(analyzeTool().getCoordinatesFileName(), parentDir));
      analyzeTool().setSpeedsFileName(FileUtils.makePathRelative(analyzeTool().getSpeedsFileName(), parentDir));

      analyzeTool().setExternalLoadsFileName(FileUtils.makePathRelative(analyzeTool().getExternalLoadsFileName(), parentDir));
      //OpenSim23 analyzeTool().setExternalLoadsModelKinematicsFileName(FileUtils.makePathRelative(analyzeTool().getExternalLoadsModelKinematicsFileName(), parentDir));
   }
   
   public boolean loadSettings(String fileName) {
      // TODO: set current working directory before trying to read it?
      AnalyzeTool newAnalyzeTool = null;
      try {
         // TODO: pass it our model instead
         newAnalyzeTool = new AnalyzeTool(fileName, false);
      } catch (IOException ex) {
         ErrorDialog.displayIOExceptionDialog("Error loading file","Could not load "+fileName,ex);
         return false;
      }

      setTool(newAnalyzeTool);
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
      analyzeTool().print(fullFilename);
      relativeToAbsolutePaths(fullFilename);
      return true;
   }
}

