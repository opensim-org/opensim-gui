/*
 * JavaMotionDisplayerCallback.java
 *
 * Created on April 4, 2007, 8:35 AM
 *
 * Copyright (c)  2006, Stanford University and Ayman Habib. All rights reserved.
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

package org.opensim.view.motions;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.prefs.Preferences;
import javax.swing.SwingUtilities;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.awt.StatusDisplayer;
import org.opensim.modeling.*;
import org.opensim.utils.TheApp;
import org.opensim.view.MuscleColorByActivationStorage;
import org.opensim.view.MuscleColoringFunction;
import org.opensim.view.SingleModelVisuals;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;

/**
 *
 * @author Ayman Habib
 */
public class JavaMotionDisplayerCallback extends AnalysisWrapperWithTimer {
   private Storage storage = null;
   MotionDisplayer motionDisplayer = null;
   ProgressHandle progressHandle = null;

   boolean progressUsingTime = true;
   double startTime = 0.;
   double endTime = 1.;
   static final double progressTimeResolution = 1e2;
   int startStep = 0;
   int endStep = 1;
   int lastProgressStep = -1;
   double lastRenderTime = -1e10;
   double minRenderTimeInterval = -1;
   double startIKTime = getCurrentRealTime();
   double stopIKTime = 0;
   double startDisplayTime = 0;
   double stopDisplayTime = 0;
   double minSimTime = -1;
   double currentSimTime = 0;
   OpenSimContext context = null;
   OpenSimContext simulationContext = null; // Corresponding to actual model being run
   Model modelForDisplay=null;
   boolean ownsStorage=false;
   int numStates=0;
   ArrayStr stateLabels=null;
   private double[] statesBuffer;
   private boolean displayTimeProgress=false;
   private boolean coordinatesOnly=false;
   private boolean staticOptimization = false;
   private Storage activationStorage=null;
   HashMap<Integer, Integer> mapActivationIndex2State = new HashMap<Integer, Integer>(10);
   //private int stepNumbIntegerer=0;
   
   // Creates a new instance of JavaMotionDisplayerCallback 
   public JavaMotionDisplayerCallback(Model aModel, Storage aStorage, ProgressHandle progressHandle, boolean coordinatesOnly) {
      super(aModel);
      modelForDisplay=aModel;
      context = OpenSimDB.getInstance().getContext(aModel);
      this.coordinatesOnly = coordinatesOnly;
      if(aStorage!=null) {
         this.storage = aStorage;
      }
      else 
            createResultStorage();
      
      motionDisplayer = new MotionDisplayer(getStorage(), getModelForDisplay());
      this.progressHandle = progressHandle;
      setRefreshRateInMillis(getRefreshRatePreference());
   }

    private void createResultStorage() {
        storage = new Storage();
        getStorage().setName("Results");
        stateLabels = new ArrayStr();
        if (isCoordinatesOnly()){
            get_model().getCoordinateSet().getNames(stateLabels);
        }
        else {
        stateLabels = getModelForDisplay().getStateVariableNames();
            
        }
        stateLabels.insert(0, "time");
        getStorage().setColumnLabels(stateLabels);
        numStates = isCoordinatesOnly()?getModelForDisplay().getNumCoordinates(): getModelForDisplay().getNumStateVariables();
        statesBuffer = new double[numStates];
        ownsStorage=true;
    }
   
   public JavaMotionDisplayerCallback(Model aModel, Model aModelForDisplay, Storage aStorage, ProgressHandle progressHandle, boolean staticOptimization) {
      super(aModel);
      modelForDisplay=aModelForDisplay;
      context = OpenSimDB.getInstance().getContext(aModelForDisplay);
      this.staticOptimization = staticOptimization;
      if(aStorage!=null) {
         this.storage = aStorage;
      }
      else
          createResultStorage();
      motionDisplayer = new MotionDisplayer(getStorage(), getModelForDisplay());
      this.progressHandle = progressHandle;
      setRefreshRateInMillis(getRefreshRatePreference());
   }

   // In seconds
   public void setMinRenderTimeInterval(double interval) { minRenderTimeInterval = interval; }

   public void setRenderMuscleActivations(boolean render) {
      if(getModelForDisplayCompatibleStates()) {
        SingleModelVisuals vis = ViewDB.getInstance().getModelVisuals(getModelForDisplay());
        if(vis!=null) vis.setApplyMuscleColors(render);
   }
   }

   public void startProgressUsingTime(double startTime, double endTime) {
      progressUsingTime = true;
      this.startTime = startTime;
      this.endTime = endTime;
      if(progressHandle!=null) progressHandle.start((int)((endTime-startTime)*progressTimeResolution));
   }

   public void startProgressUsingSteps(int startStep, int endStep) {
      progressUsingTime = false;
      this.startStep = startStep;
      this.endStep = endStep;
      if(progressHandle!=null) progressHandle.start(endStep-startStep+1);
   }
   
   private double getCurrentRealTime() {
      return 1e-9*System.nanoTime();
   }

   public void updateDisplaySynchronously() {
      try {
         SwingUtilities.invokeAndWait(new Runnable(){
            public void run() {
               double currentRealTime = getCurrentRealTime();
               if(minRenderTimeInterval<=0 || currentRealTime-lastRenderTime>minRenderTimeInterval) {
                  if(motionDisplayer!=null && getStorage().getSize()>0) 
                      motionDisplayer.applyFrameToModel(getStorage().getSize()-1); 
                  
                  ViewDB.getInstance().updateModelDisplay(getModelForDisplay());  // Faster? than the next few indented lines
                    //ViewDB.getInstance().updateModelDisplayNoRepaint(getModelForDisplay());
                    ////ViewDB.getInstance().renderAll(); // Render now (if want to do it later, use repaintAll()) -- may slow things down too much
                    //ViewDB.getInstance().repaintAll();
                  lastRenderTime = currentRealTime; 
                  //System.out.println("REPAINTED");
               }
              }});                      
      } 
      catch (InterruptedException ex) {
         ex.printStackTrace();
      } catch (InvocationTargetException ex) {
         ex.printStackTrace();
      }
   }
   
   public void processStep(State s, int stepNumber) {
      if(!getOn()) return;
      if (!proceed(stepNumber)) return;
      super.step(s, stepNumber);
      if(progressHandle!=null) {
          if (!progressUsingTime) progressHandle.progress(stepNumber-startStep);
          else {
         int progressStep = (int)((getSimulationTime()-startTime)*progressTimeResolution);
         if(progressStep > lastProgressStep) { // make sure we only advance progress (else an exception is thrown)
            String msg = String.format("Forward Simulation, t=%.4f", getSimulationTime());
            if (displayTimeProgress) {
                progressHandle.setDisplayName(msg);
            }
            progressHandle.progress(progressStep);
            //StatusDisplayer.getDefault().setStatusText(msg);
            lastProgressStep = progressStep;
         }
      }
      }
      currentSimTime = getSimulationTime();   
      context.getCurrentStateRef().setTime(currentSimTime);
      if (ownsStorage) {    // Callback is the one accumulating results //ASSERTS downlstream 0327
          // Need to make sure nextResult is not Freed by gc
          StateVector nextResult = new StateVector();
          if (isCoordinatesOnly()){
              CoordinateSet cs = get_model().getCoordinateSet();
              nextResult.setTime(currentSimTime);
              for(int i=0; i< cs.getSize(); i++){
                  nextResult.getData().append(cs.get(i).getValue(s));
              }
              getStorage().append(nextResult);
          }
          else {
          super.getStates(statesBuffer);
              /*
              if (staticOptimization){
                  StateVector sv = activationStorage.getLastStateVector();
                  ArrayDouble actData = sv.getData();
                  for (Integer actKey:mapActivationIndex2State.keySet()){
                      int stateIdx = mapActivationIndex2State.get(actKey);
                      statesBuffer[stateIdx] = actData.get(actKey.intValue());
                  }
              }*/
          //System.out.println("Simulation time="+currentSimTime+" state[0]="+statesBuffer[0]);
          nextResult.setStates(currentSimTime, numStates, statesBuffer);
          }
          getStorage().append(nextResult);
      }
      if (!isInitialized()){
         initializeTimer();
      }
      if(isUpdateDisplay()) {
          stopIKTime = getCurrentRealTime(); // Stop timing of ik computations
          startDisplayTime = getCurrentRealTime(); // Start timing of display update
          updateDisplaySynchronously();
          
          stopDisplayTime = getCurrentRealTime();  // Stop timing of display update
          minSimTime = currentSimTime+(stopDisplayTime-startDisplayTime)+(stopIKTime-startIKTime);  // Set minimum simulation time for next display update 
          
          //System.out.println("minSimTime = "+currentSimTime+" + "+(stopDisplayTime-startDisplayTime)+" + "+(stopIKTime-startIKTime)+" = "+minSimTime);
          //System.out.println("Updating Display");
          startIKTime = getCurrentRealTime(); // Start timing of ik computations
          setUpdateDisplay(false);
      }
      stepNumber++;
   }
   
   public void cleanupMotionDisplayer() {
      setRenderMuscleActivations(false);
      if(motionDisplayer!=null) motionDisplayer.cleanupDisplay();
      ViewDB.getInstance().repaintAll();
      if (getTimer()!=null)
          getTimer().cancel();
   }

   protected void finalize() {
      super.finalize();
   }
   
    private Model getModelForDisplay() {
        return modelForDisplay;//get_model();
    }

    private boolean getModelForDisplayCompatibleStates() {
        return true;
    }

    public int step(State s, int stepNumber) {
        int retValue;
        retValue = super.step(s, stepNumber);
        processStep(s, stepNumber);
        /*
         * if (staticOptimization){
            int sz = activationStorage.getSize();
        }*/
        return retValue;
    }

    public int begin(State s) {
        int retValue=0;
        if (!isInitialized()){
            initializeTimer();
            
            if (staticOptimization){
                
                StaticOptimization staticOptimizationAnalysis = StaticOptimization.safeDownCast(get_model().getAnalysisSet().get("StaticOptimization"));
                activationStorage = staticOptimizationAnalysis.getActivationStorage();

                // Change coloring function for model
                MuscleColoringFunction mcbya = new MuscleColorByActivationStorage(context, activationStorage);
                motionDisplayer.setMuscleColoringFunction(mcbya);
                
            }
        }
        processStep(s, 0);
        return retValue;
    }

    private double getCurrentTime() {
        return context.getTime();
    }

    public Storage getStateStorage() {
        return getStorage();
    }

    static public long getRefreshRatePreference() {
         String refreshRateInMS = "100";        
         String saved = Preferences.userNodeForPackage(TheApp.class).get("Refresh Rate (ms.)", refreshRateInMS);
         Long savedLong = Long.parseLong(saved);
         return savedLong;
    }    

    public Storage getStorage() {
        return storage;
    }

    /**
     * @param displayTimeProgress the displayTimeProgress to set
     */
    public void setDisplayTimeProgress(boolean displayTimeProgress) {
        this.displayTimeProgress = displayTimeProgress;
    }

    /**
     * @return the coordinatesOnly
     */
    public boolean isCoordinatesOnly() {
        return coordinatesOnly;
    }

    /**
     * @param coordinatesOnly the coordinatesOnly to set
     */
    public void setCoordinatesOnly(boolean coordinatesOnly) {
        this.coordinatesOnly = coordinatesOnly;
    }

    /**
     * @return the activationStorage
     */
    public Storage getActivationStorage() {
        return activationStorage;
    }

    /**
     * @param activationStorage the activationStorage to set
     */
    public void setActivationStorage(Storage activationStorage) {
        this.activationStorage = activationStorage;
    }
}
