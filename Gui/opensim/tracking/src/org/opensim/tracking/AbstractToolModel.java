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

   protected Model originalModel = originalModel = null;
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
      Analysis analysisCopy = Analysis.safeDownCast(analysis.clone()); // C++-side copy
      analysisCopy.setName(analysis.getConcreteClassName()); // Change name...  otherwise name will be "default" since currently the analyses we're making copies of come from the registered object table
      getAnalysisSet().adoptAndAppend(analysisCopy);
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
      else tool.setResultsDir(Preferences.userNodeForPackage(TheApp.class).get("WorkDirectory", ""));
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

