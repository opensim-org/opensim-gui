/* -------------------------------------------------------------------------- *
 * OpenSim: AbstractToolModel.java                                            *
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
import java.util.Observable;
import java.util.Vector;
import java.util.prefs.Preferences;
import org.opensim.modeling.AbstractTool;
import org.opensim.modeling.Analysis;
import org.opensim.modeling.AnalysisSet;
import org.opensim.modeling.ArrayStr;
import org.opensim.modeling.ExternalLoads;
import org.opensim.modeling.Model;
import org.opensim.modeling.Storage;
import org.opensim.tracking.AbstractToolModel.Operation;
import org.opensim.utils.FileUtils;
import org.opensim.utils.TheApp;

public abstract class AbstractToolModel extends Observable {

   public enum Operation { AllDataChanged, InputDataChanged, OutputDataChanged, TimeRangeChanged, AnalysisDataChanged, AnalysisAddedOrRemoved, ActuatorsDataChanged, ExternalLoadsDataChanged, IntegratorSettingsChanged, ExecutionStateChanged };

   private boolean modifiedSinceLastExecute = true;
   private boolean executing = false;

   protected Model originalModel = null;
   protected Model model = null;
   protected AbstractTool tool = null;
   protected Vector<ResultDisplayerInterface> resultDisplayers=new Vector<ResultDisplayerInterface>();
   
   public AbstractToolModel(Model originalModel) {
      this.originalModel = originalModel;
   }

   public void setTool(AbstractTool tool) { this.tool = tool; }
   public AbstractTool getTool() { return tool; }

   //------------------------------------------------------------------------
   // Get/Set Values
   //------------------------------------------------------------------------

   public Model getOriginalModel() { return originalModel; }

   public Model getModel() { return model; }
   protected void setModel(Model model) { 
       this.model = model; 
       // Include model's file name in xml rep.'
       if (model.getInputFileName()!="" && tool != null)
           tool.setModelFilename(model.getInputFileName());
   }

   //------------------------------------------------------------------------
   // Actuators
   //------------------------------------------------------------------------
   public boolean getReplaceForceSet() { return tool.getReplaceForceSet(); }
   public void setReplaceForceSet(boolean replace) { 
      if(getReplaceForceSet() != replace) {
         tool.setReplaceForceSet(replace);
         setModified(Operation.ActuatorsDataChanged);
      }
   }

   public ArrayStr getForceSetFiles() { return tool.getForceSetFiles(); }
   public void setForceSetFiles(ArrayStr files) {
      if(!getForceSetFiles().arrayEquals(files)) {
         tool.setForceSetFiles(files);
         setModified(Operation.ActuatorsDataChanged);
      }
   }

   //------------------------------------------------------------------------
   // Analyses
   //------------------------------------------------------------------------
   public AnalysisSet getAnalysisSet() { return tool.getAnalysisSet(); }
   public void addCopyOfAnalysis(Analysis analysis) {
      getAnalysisSet().cloneAndAppend(analysis); // All cloning memory management on Cpp side
      Analysis an = getAnalysisSet().get(getAnalysisSet().getSize()-1);
      an.setName(analysis.getConcreteClassName());
      setModified(Operation.AnalysisAddedOrRemoved);
   }
   public void replaceAnalysis(int i, Analysis analysis) {
      getAnalysisSet().set(i, analysis);
      setModified(Operation.AnalysisDataChanged);
   }
   public void removeAnalysis(int i) {
      getAnalysisSet().remove(i); 
      setModified(Operation.AnalysisAddedOrRemoved);
   }
   // Rely on users to call this since we don't have accessors for all analysis properties
   public void analysisModified(int i) { setModified(Operation.AnalysisDataChanged); }

   //------------------------------------------------------------------------
   // Time range settings
   //------------------------------------------------------------------------
   public double getInitialTime() { return tool.getInitialTime(); }
   public void setInitialTime(double time) {
      if(getInitialTime() != time) {
         tool.setInitialTime(time);
         setModified(Operation.TimeRangeChanged);
      }
   }

   public double getFinalTime() { return tool.getFinalTime(); }
   public void setFinalTime(double time) {
      if(getFinalTime() != time) {
         tool.setFinalTime(time);
         setModified(Operation.TimeRangeChanged);
      }
   }

   //------------------------------------------------------------------------
   // Ouptut settings
   //------------------------------------------------------------------------
   public int getOutputPrecision() { return tool.getOutputPrecision(); }
   public void setOutputPrecision(int precision) {
      if(getOutputPrecision() != precision) {
         tool.setOutputPrecision(precision);
         setModified(Operation.OutputDataChanged);
      }
   }

   public String getOutputPrefix() { return tool.getName(); } // It's the name of the tool
   public void setOutputPrefix(String name) {
      if(!getOutputPrefix().equals(name)) {
         tool.setName(name);
         setModified(Operation.OutputDataChanged);
      }
   }

   public String getResultsDirectory() { return tool.getResultsDir(); }
   public void setResultsDirectory(String directory) {
      if(!getResultsDirectory().equals(directory)) {
         tool.setResultsDir(directory);
         setModified(Operation.OutputDataChanged);
      }
   }
   protected void setDefaultResultsDirectory(Model model) {
      // Try to come up with a reasonable output directory
      if(!model.getInputFileName().equals("")){
          String parentDir = (new File(model.getInputFileName())).getParent();
          if (parentDir != null)
            tool.setResultsDir(parentDir);
          else
            tool.setResultsDir(".");
      }
      else tool.setResultsDir(TheApp.getCurrentVersionPreferences().get("Internal.WorkDirectory", ""));
   }
   
   //------------------------------------------------------------------------
   // Integrator settings
   //------------------------------------------------------------------------
   public int getMaximumNumberOfSteps() { return tool.getMaximumNumberOfSteps(); }
   public void setMaximumNumberOfSteps(int maxSteps) {
      if(getMaximumNumberOfSteps() != maxSteps) {
         tool.setMaximumNumberOfSteps(maxSteps);
         setModified(Operation.IntegratorSettingsChanged);
      }
   }
   public double getMaxDT() { return tool.getMaxDT(); }
   public void setMaxDT(double maxDT) {
      if(getMaxDT() != maxDT) {
         tool.setMaxDT(maxDT);
         setModified(Operation.IntegratorSettingsChanged);
      }
   }
   public double getMinDT() { return tool.getMinDT(); }
   public void setMinDT(double minDT) {
      if(getMinDT() != minDT) {
         tool.setMinDT(minDT);
         setModified(Operation.IntegratorSettingsChanged);
      }
   }
   public double getErrorTolerance() { return tool.getErrorTolerance(); }
   public void setErrorTolerance(double tolerance) {
      if(getErrorTolerance() != tolerance) {
         tool.setErrorTolerance(tolerance);
         setModified(Operation.IntegratorSettingsChanged);
      }
   }


   //------------------------------------------------------------------------
   // Other settings
   //------------------------------------------------------------------------
   public boolean getSolveForEquilibrium() { return tool.getSolveForEquilibrium(); }
   public void setSolveForEquilibrium(boolean solve) {
      if(getSolveForEquilibrium() != solve) {
         tool.setSolveForEquilibrium(solve);
         setModified(Operation.InputDataChanged);
      }
   }

   //------------------------------------------------------------------------
   // Functions to override
   //------------------------------------------------------------------------

   public abstract double[] getAvailableTimeRange();

   public abstract boolean isValidated();

   public abstract void execute();
   public abstract void cancel();

   public abstract boolean loadSettings(String fileName);
   public abstract boolean saveSettings(String fileName);

   //------------------------------------------------------------------------
   // Execution status
   //------------------------------------------------------------------------

   protected void setExecuting(boolean executing) {
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

   protected void setModified(Object change) {
      modifiedSinceLastExecute = true;
      setChanged(); // need to call this before calling notifyObservers
      notifyObservers(change);
   }
   protected void resetModified() {
      modifiedSinceLastExecute = false;
   }
   public boolean isModified() {
      return modifiedSinceLastExecute;
   }

   //------------------------------------------------------------------------
   // Sync model to/from tool
   //------------------------------------------------------------------------

   protected void relativeToAbsolutePaths(String parentFileName) {
      String parentDir = (new File(parentFileName)).getParent();

      tool.setModelFilename(FileUtils.makePathAbsolute(tool.getModelFilename(), parentDir));
      tool.setResultsDir(FileUtils.makePathAbsolute(tool.getResultsDir(), parentDir));

      ArrayStr actuatorSetFiles = getForceSetFiles();
      for(int i=0; i<actuatorSetFiles.getSize(); i++)
         actuatorSetFiles.set(i, FileUtils.makePathAbsolute(actuatorSetFiles.getitem(i), parentDir));
   }

   protected void AbsoluteToRelativePaths(String parentFileName) {
      String parentDir = (new File(parentFileName)).getParent();
      
      tool.setModelFilename(FileUtils.makePathRelative(tool.getModelFilename(), parentDir));
      tool.setResultsDir(FileUtils.makePathRelative(tool.getResultsDir(), parentDir));

      ArrayStr actuatorSetFiles = getForceSetFiles();
      for(int i=0; i<actuatorSetFiles.getSize(); i++)
         actuatorSetFiles.set(i, FileUtils.makePathRelative(actuatorSetFiles.getitem(i), parentDir));
   }
   protected void updateFromTool() {

   }

   protected void updateTool() {
      //tool.setAllPropertiesUseDefault(false); // To make sure we serialize all properties even after we've changed their values
   }


    public void setAnalysisTimeFromTool(final Analysis aAnalysis) {
        aAnalysis.setStartTime(tool.getInitialTime());
        aAnalysis.setEndTime(tool.getFinalTime());
    }

}

abstract class AbstractToolModelWithExternalLoads extends AbstractToolModel {
   private boolean externalLoadsEnabled = false;

   public AbstractToolModelWithExternalLoads(Model model) { super(model); }

   public boolean getExternalLoadsEnabled() { return externalLoadsEnabled; }
   public void setExternalLoadsEnabled(boolean enabled) {
        if(getExternalLoadsEnabled() != enabled) {
            externalLoadsEnabled = enabled;
            if (!enabled) setExternalLoadsFileName("");
            setModified(AbstractToolModel.Operation.ExternalLoadsDataChanged);
        }
    }

    abstract String getExternalLoadsFileName();
    protected abstract void setExternalLoadsFileNameInternal(String fileName);
    public void setExternalLoadsFileName(String fileName) {
        if(!getExternalLoadsFileName().equals(fileName)) {
          setExternalLoadsFileNameInternal(fileName);
          setModified(AbstractToolModel.Operation.ExternalLoadsDataChanged);
        }
    }
    
    public abstract String getExternalLoadsModelKinematicsFileName();
    protected abstract void setExternalLoadsModelKinematicsFileNameInternal(String fileName);
    public void setExternalLoadsModelKinematicsFileName(String fileName) {
       if(!getExternalLoadsModelKinematicsFileName().equals(fileName)) {
          setExternalLoadsModelKinematicsFileNameInternal(fileName);
          setModified(AbstractToolModel.Operation.ExternalLoadsDataChanged);
       }
    }

   public abstract double getLowpassCutoffFrequencyForLoadKinematics();
   protected abstract void setLowpassCutoffFrequencyForLoadKinematicsInternal(double cutoffFrequency);
   
    public void setLowpassCutoffFrequencyForLoadKinematics(double cutoffFrequency) {
       if(getLowpassCutoffFrequencyForLoadKinematics() != cutoffFrequency) {
          setLowpassCutoffFrequencyForLoadKinematicsInternal(cutoffFrequency);
          setModified(AbstractToolModel.Operation.ExternalLoadsDataChanged);
       }
    }

    public boolean getFilterLoadKinematics() { return getLowpassCutoffFrequencyForLoadKinematics() > 0; }
    public void setFilterLoadKinematics(boolean filterLoadKinematics) {
       if(getFilterLoadKinematics() != filterLoadKinematics) {
          if(filterLoadKinematics) setLowpassCutoffFrequencyForLoadKinematicsInternal(6);
          else setLowpassCutoffFrequencyForLoadKinematicsInternal(-1);
          setModified(AbstractToolModel.Operation.ExternalLoadsDataChanged);
       }
    }

   protected void updateFromTool() {
      super.updateFromTool();
      externalLoadsEnabled = !FileUtils.effectivelyNull(getExternalLoadsFileName());
   }

   protected void updateTool() {
      super.updateTool();
      //OpenSim23 if(!externalLoadsEnabled) setExternalLoadsFileNameInternal("");
   }

   public boolean isValidated() {
      return !getExternalLoadsEnabled() || (new File(getExternalLoadsFileName()).exists());
   }

   protected double[] intersectTimeRanges(double[] range1, double[] range2) {
      return new double[]{(range1[0]>range2[0])?range1[0]:range2[0], (range1[1]<range2[1])?range1[1]:range2[1]};
   }
   
   public void addResultDisplayer(ResultDisplayerInterface displayer) {
       resultDisplayers.add(displayer);
   }
   public boolean hasResultsDisplayer(ResultDisplayerInterface displayer) {
       return resultDisplayers.contains(displayer);
   }

    void setLowpassCutoffFrequency(double d) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    double getLowpassCutoffFrequency() {
        return 0;
    }

    void setFilterCoordinates(boolean b) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    void setStatesFileName(String string) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    void setCoordinatesFileName(String string) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public ExternalLoads getExternalLoads() {
        return getTool().getExternalLoads();
    }
}
/**
 * Common abstract class to be shared by CMCToolModel RRAToolModel
 */
abstract class TrackingToolModel extends AbstractToolModelWithExternalLoads {
    public TrackingToolModel(Model model) { super(model); }

    abstract void setFilterKinematics(boolean b);

    abstract void setDesiredKinematicsFileName(String string) ;

    abstract void setConstraintsEnabled(boolean b) ;

    abstract void setConstraintsFileName(String string) ;

    abstract void setTaskSetFileName(String string) ;


    protected void updateToolTimeRange(Storage storage) {
        getTool().setStartTime(storage.getFirstTime());
        getTool().setFinalTime(storage.getLastTime());
        setModified(Operation.TimeRangeChanged);
    }

}

