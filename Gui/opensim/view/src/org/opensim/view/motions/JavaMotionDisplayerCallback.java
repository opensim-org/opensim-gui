/* -------------------------------------------------------------------------- *
 * OpenSim: JavaMotionDisplayerCallback.java                                  *
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
/*
 * JavaMotionDisplayerCallback.java
 *
 * Created on April 4, 2007, 8:35 AM
 *
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
   Kinematics kinReporter = null;
   StatesReporter statesReporter = null;
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
        
        
         if (isCoordinatesOnly()){
            kinReporter = new Kinematics(get_model());
            kinReporter.setInDegrees(false);
            kinReporter.setRecordAccelerations(false);
            kinReporter.begin(context.getCurrentStateRef());
            storage = kinReporter.getPositionStorage();
        }
        else {
            statesReporter = new StatesReporter(get_model());
            statesReporter.setInDegrees(false);
            statesReporter.begin(context.getCurrentStateRef());
            storage = statesReporter.getStatesStorage();
        }
        getStorage().setName("Results");
       // Create map int->int from Y vector to statesBuffer
        ownsStorage=true;
    }
   
   public JavaMotionDisplayerCallback(Model aModel, Model aModelForDisplay, Storage aStorage, ProgressHandle progressHandle, boolean staticOptimization) {
      super(aModel);
      modelForDisplay=aModelForDisplay;
      context = OpenSimDB.getInstance().getContext(aModelForDisplay);
      this.staticOptimization = staticOptimization;
      if (!staticOptimization)
        this.coordinatesOnly = true;
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
        if (getModelForDisplayCompatibleStates()) {
            if (ViewDB.isVtkGraphicsAvailable()) {
                SingleModelVisuals vis = ViewDB.getInstance().getModelVisuals(getModelForDisplay());
                if (vis != null) {
                    vis.setApplyMuscleColors(render);
                }
            }
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
                  
                  //ViewDB.getInstance().updateModelDisplay(getModelForDisplay());  // Faster? than the next few indented lines
                    ViewDB.getInstance().updateModelDisplayNoRepaint(getModelForDisplay(), true);
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
      if(progressHandle!=null) {
          if (!progressUsingTime) progressHandle.progress(stepNumber-startStep);
          else {
         int progressStep = (int)((getSimulationTime()-startTime)*progressTimeResolution);
         if(progressStep > lastProgressStep) { // make sure we only advance progress (else an exception is thrown)
            String msg = String.format("Executing tool, t=%.4f", getSimulationTime());
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
      
      super.step(s, stepNumber);
       //context.getCurrentStateRef().setTime(currentSimTime);
      if (kinReporter != null) {    // Callback is the one accumulating results 
          kinReporter.step(s, stepNumber);
      }
      else if (statesReporter!=null){
          statesReporter.step(s, stepNumber);
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
      //ViewDB.getInstance().repaintAll();
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
        int retValue=0;
        if (ownsStorage && stepNumber==0)
            storage.purge();
        //retValue = super.step(s, stepNumber);
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
        if (ownsStorage)
            storage.purge();
        processStep(s, 0);
        return retValue;
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
   @Override
    public int end(State s) {
        processStep(s, storage.getSize());
        return  super.end(s);
    }
}
