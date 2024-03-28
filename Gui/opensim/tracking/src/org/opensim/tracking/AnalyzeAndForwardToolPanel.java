/* -------------------------------------------------------------------------- *
 * OpenSim: AnalyzeAndForwardToolPanel.java                                   *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib, Christopher Dembia, Kevin Xu                       *
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
 * AnalyzeAndForwardToolPanel.java
 *
 * Created on July 24, 2007, 7:37 PM
 */

package org.opensim.tracking;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.SpinnerModel;
import org.jdesktop.layout.GroupLayout;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.opensim.modeling.Analysis;
import org.opensim.modeling.BodySet;
import org.opensim.modeling.ControlSet;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.Storage;
import org.opensim.view.motions.MotionsDB;
import org.opensim.swingui.FileTextFieldAndChooser;
import org.opensim.utils.BrowserLauncher;
import org.opensim.utils.ErrorDialog;
import org.opensim.utils.FileUtils;
import org.opensim.utils.TheApp;
import org.opensim.view.FileTextFieldAndChooserWithEdit;
import org.opensim.view.ModelEvent;
import org.opensim.view.excitationEditor.ExcitationEditorJFrame;
import org.opensim.view.pub.OpenSimDB;

/**
 *
 * @author  erang
 */
public class AnalyzeAndForwardToolPanel extends BaseToolPanel implements Observer {

   //private JCheckBox rraPanelCheckBox = new JCheckBox(new EnableReduceResidualsAction());
   class EnableReduceResidualsAction extends AbstractAction {
      public EnableReduceResidualsAction() { super("Reduce Residuals"); }
      public void actionPerformed(ActionEvent evt) { rraToolModel().setAdjustModelToReduceResidualsEnabled(((JCheckBox)evt.getSource()).isSelected()); }
   }

   public enum Mode { ForwardDynamics, InverseDynamics, CMC, RRA, Analyze, StaticOptimization };
   private Mode mode;
   String modeName;
   String helpUrl;
  
   AbstractToolModelWithExternalLoads toolModel = null;
   ActuatorsAndExternalLoadsPanel actuatorsAndExternalLoadsPanel = null;
   AnalysisSetPanel analysisSetPanel = null;

   private boolean internalTrigger = false;
   private NumberFormat numFormat = NumberFormat.getInstance();

   
   /** Creates new form AnalyzeAndForwardToolPanel */
   public AnalyzeAndForwardToolPanel(Model model, Mode mode) throws IOException {
      this.mode = mode;

      switch(mode) {
         case ForwardDynamics: 
             modeName = "forward dynamics tool"; 
             toolModel = new ForwardToolModel(model);  

             helpUrl = "53089629/Forward+Dynamics";
             break;
         case InverseDynamics: 
             modeName = "inverse dynamics tool"; 
             toolModel = new AnalyzeToolModel(model, mode); 
            
             helpUrl = "53090074/Inverse+Dynamics";
             break;
         case CMC: 
             modeName = "CMC tool"; 
             toolModel = new CMCToolModel(model); 
            
             helpUrl = "53089719/Computed+Muscle+Control";
            break;
         case RRA: 
             modeName = "RRA tool"; 
             toolModel = new RRAToolModel(model); 
             
             helpUrl = "53089669/Residual+Reduction+Algorithm";
             break;
         case Analyze: 
             modeName = "analyze tool"; 
             toolModel = new AnalyzeToolModel(model, mode); 
             
             helpUrl = "53089589/Analyses";
             break;
         case StaticOptimization:  
             modeName = "static optimization tool"; 
             toolModel = new AnalyzeToolModel(model, mode); 
            
             helpUrl = "53090088/Static+Optimization";
             break;
      }
                         
      helpButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
          String path = "https://opensimconfluence.atlassian.net/wiki/spaces/OpenSim40/pages/" + helpUrl;
          BrowserLauncher.openURL(path);
      }
      });
             
      if (numFormat instanceof DecimalFormat) {
        ((DecimalFormat) numFormat).applyPattern("###0.############");
      }

      initComponents();
      bindPropertiesToComponents();

      // Add checkbox titled borders to RRA panel
      //rraPanelCheckBox.setForeground(new Color(0,70,213));
      //rraPanel.setBorder(new ComponentTitledBorder(rraPanelCheckBox, rraPanel, BorderFactory.createEtchedBorder()));

      // File chooser settings
      outputDirectory.setIncludeOpenButton(true);
      outputDirectory.setDirectoriesOnly(true);
      outputDirectory.setSaveMode(true);
      outputDirectory.setCheckIfFileExists(false);
      outputDirectory.setDialogTitle("Choose Output Directory");

      setSettingsFileDescription("Settings file for "+modeName);

      analyzeInputPanel.setVisible(mode==Mode.Analyze);
      inverseInputPanel.setVisible(mode==Mode.InverseDynamics||mode==Mode.StaticOptimization);
      forwardInputPanel.setVisible(mode==Mode.ForwardDynamics);
      cmcInputPanel.setVisible(mode==Mode.CMC||mode==Mode.RRA);
      staticOptimizationPanel.setVisible(mode==Mode.StaticOptimization);
      
      rraPanel.setVisible(mode==Mode.RRA);
      activeAnalysesPanel.setVisible(mode==Mode.Analyze);

      plotMetricsPanel.setVisible(false);

      // disable for now
      jLabel7.setVisible(false);
      jLabel8.setVisible(false);
      
      availableInitialTime.setVisible(false);
      availableFinalTime.setVisible(false);
      if (mode != Mode.CMC){
        jLabel23.setVisible(false);
        cmcTimeWindow.setVisible(false);
	plotMetricsPanel.setVisible(false); // Show plots only for RRA, CMC for now as the more time consuming steps
      }
      if(mode==Mode.Analyze) {
         // Set file filters for analyze tool inputs
         statesFileName.setExtensionsAndDescription(".sto", "States data for "+modeName);
         coordinatesFileName.setExtensionsAndDescription(".mot,.sto", "Motion data for "+modeName);
         analyzeControlsFileName.setExtensionsAndDescription(".xml,.sto", "Controls input data for "+modeName);
         analyzeControlsFileName.setTreatEmptyStringAsValid(false);
         //speedsFileName.setExtensionsAndDescription(".mot,.sto", "Speeds data for "+modeName);
      } else if(mode==Mode.InverseDynamics || mode==Mode.StaticOptimization) {
         // Set file filters for inverse dynamics tool inputs
         statesFileName1.setExtensionsAndDescription(".sto", "States data for "+modeName);
         coordinatesFileName1.setExtensionsAndDescription(".mot,.sto", "Motion data for "+modeName);
      } else if(mode==Mode.ForwardDynamics) {
         // Set file filters for forward tool inputs
         controlsFileName.setExtensionsAndDescription(".xml,.sto", "Controls input data for "+modeName);
         controlsFileName.setTreatEmptyStringAsValid(true);
         initialStatesFileName.setExtensionsAndDescription(".sto", "Initial states data for "+modeName);
         initialStatesFileName.setTreatEmptyStringAsValid(true);
      } else if(mode==Mode.CMC||mode==Mode.RRA) {
         cmcDesiredKinematicsFileName.setExtensionsAndDescription(".mot,.sto", "Desired kinematics for "+modeName);
         cmcTaskSetFileName.setExtensionsAndDescription(".xml", "Task Set");
         //cmcTaskSetFileName.setIncludeEditButton(true);
         cmcConstraintsFileName.setExtensionsAndDescription(".xml", "Constraints");
         cmcConstraintsFileName.setTreatEmptyStringAsValid(false);
         //cmcConstraintsFileName.setIncludeEditButton(true);
         if (mode == Mode.RRA){
         rraOutputModelFileName.setExtensionsAndDescription(".osim", "Adjusted OpenSim model");
         rraOutputModelFileName.setSaveMode(true);
         rraOutputModelFileName.setAssociatedCheckBox(adjustModelCheckBox);
             rraOutputModelFileName.setCheckIfFileExists(false);
      }
      }

      // Actuators & External Loads tab
      actuatorsAndExternalLoadsPanel = new ActuatorsAndExternalLoadsPanel(toolModel, toolModel.getOriginalModel(), 
              mode!=Mode.InverseDynamics);
      if (mode==Mode.RRA) actuatorsAndExternalLoadsPanel.setReplaceOnlyMode();
      jTabbedPane1.addTab((mode==Mode.InverseDynamics) ? "External Loads" : "Actuators and External Loads", actuatorsAndExternalLoadsPanel);

      // Analysis Set tab
      if(mode==Mode.Analyze || mode==Mode.ForwardDynamics) {
         analysisSetPanel = new AnalysisSetPanel(toolModel);
         jTabbedPane1.addTab("Analyses", analysisSetPanel);
      }

      // Integrator settings for forward dynamics
      if(mode==Mode.ForwardDynamics || mode==Mode.CMC || mode==Mode.RRA) {
         jTabbedPane1.addTab("Integrator Settings", advancedSettingsPanel);
      }

      // Re-layout panels after we've removed various parts...
      ((GroupLayout)mainSettingsPanel.getLayout()).layoutContainer(mainSettingsPanel);

      updateStaticFields();
      updateFromModel();

      toolModel.addObserver(this);
   }
   // This method is used to bind comments on properties to tooltips in GUI
   private void bindPropertiesToComponents() {
      if(mode==Mode.Analyze) {
         //opensim20 ToolCommon.bindProperty(toolModel.getTool(), "controls_file", analyzeControlsFileName);
         ToolCommon.bindProperty(toolModel.getTool(), "states_file", statesFileName);
         ToolCommon.bindProperty(toolModel.getTool(), "coordinates_file", coordinatesFileName);
         ToolCommon.bindProperty(toolModel.getTool(), "lowpass_cutoff_frequency_for_coordinates", cutoffFrequency);
         ToolCommon.bindProperty(toolModel.getTool(), "solve_for_equilibrium_for_auxiliary_states", analyzeSolveForEquilibriumCheckBox);
      } else if(mode==Mode.InverseDynamics|| mode==Mode.StaticOptimization) {
         ToolCommon.bindProperty(toolModel.getTool(), "states_file", statesFileName);
         ToolCommon.bindProperty(toolModel.getTool(), "coordinates_file", coordinatesFileName);
         ToolCommon.bindProperty(toolModel.getTool(), "lowpass_cutoff_frequency_for_coordinates", cutoffFrequency);
      } else if(mode==Mode.ForwardDynamics) {
         //opensim20 ToolCommon.bindProperty(toolModel.getTool(), "controls_file", controlsFileName);
         ToolCommon.bindProperty(toolModel.getTool(), "states_file", initialStatesFileName);
      } else if (mode==Mode.CMC){ //CMC
         ToolCommon.bindProperty(toolModel.getTool(), "desired_kinematics_file", cmcDesiredKinematicsFileName);
         ToolCommon.bindProperty(toolModel.getTool(), "lowpass_cutoff_frequency", cmcCutoffFrequency);
         ToolCommon.bindProperty(toolModel.getTool(), "task_set_file", cmcTaskSetFileName);
         ToolCommon.bindProperty(toolModel.getTool(), "constraints_file", cmcConstraintsFileName);
         ToolCommon.bindProperty(toolModel.getTool(), "cmc_time_window", cmcTimeWindow);
         ToolCommon.bindProperty(toolModel.getTool(), "desired_kinematics_file", cmcDesiredKinematicsFileName);
      } else { //RRA
         ToolCommon.bindProperty(toolModel.getTool(), "task_set_file", cmcTaskSetFileName);
         ToolCommon.bindProperty(toolModel.getTool(), "constraints_file", cmcConstraintsFileName);
         ToolCommon.bindProperty(toolModel.getTool(), "output_model_file", rraOutputModelFileName);
         ToolCommon.bindProperty(toolModel.getTool(), "adjusted_com_body", rraAdjustedBodyComboBox);
         //ToolCommon.bindProperty(toolModel.getTool(), "cmc_time_window", cmcTimeWindow);
         ToolCommon.bindProperty(toolModel.getTool(), "desired_kinematics_file", cmcDesiredKinematicsFileName);
         ToolCommon.bindProperty(toolModel.getTool(), "lowpass_cutoff_frequency", cmcCutoffFrequency);
      }

       if (mode==Mode.StaticOptimization){
         ToolCommon.bindProperty(toolModel.getTool(), "solve_for_equilibrium_for_auxiliary_states", analyzeSolveForEquilibriumCheckBox);
         ToolCommon.bindProperty(toolModel.getTool(), "lowpass_cutoff_frequency_for_coordinates", cutoffFrequency1);
         ToolCommon.bindProperty(toolModel.getTool(), "states_file", statesFileName1);
         ToolCommon.bindProperty(toolModel.getTool(), "coordinates_file", coordinatesFileName1);
    //ToolCommon.bindProperty(toolModel.getTool(), "activation_exponent", staticOptActivationExponentTextField);
       }
       else
            ToolCommon.bindProperty(toolModel.getTool(), "solve_for_equilibrium_for_auxiliary_states", solveForEquilibriumCheckBox);
      ToolCommon.bindProperty(toolModel.getTool(), "maximum_number_of_integrator_steps", maximumNumberOfSteps);
      ToolCommon.bindProperty(toolModel.getTool(), "maximum_integrator_step_size", maxDT);
      ToolCommon.bindProperty(toolModel.getTool(), "minimum_integrator_step_size", minDT);
      ToolCommon.bindProperty(toolModel.getTool(), "integrator_error_tolerance", errorTolerance);
 
      ToolCommon.bindProperty(toolModel.getTool(), "initial_time", initialTime);
      ToolCommon.bindProperty(toolModel.getTool(), "final_time", finalTime);
      ToolCommon.bindProperty(toolModel.getTool(), "results_directory", outputDirectory);
      ToolCommon.bindProperty(toolModel.getTool(), "output_precision", outputPrecision);
   }

   private AnalyzeToolModel analyzeToolModel() { return (AnalyzeToolModel)toolModel; }
   private ForwardToolModel forwardToolModel() { return (ForwardToolModel)toolModel; } 
   private CMCToolModel cmcToolModel() { return (CMCToolModel)toolModel; } 
   private RRAToolModel rraToolModel() { return (RRAToolModel)toolModel; } 
   
   public void update(Observable observable, Object obj) {
        if (observable instanceof OpenSimDB){
           if (obj instanceof ModelEvent) {
                if (OpenSimDB.getInstance().hasModel(toolModel.getOriginalModel()))
                    return;
                else {
                    toolModel.deleteObserver(this);
                    OpenSimDB.getInstance().deleteObserver(this);
                    NotifyDescriptor.Message dlg =
                          new NotifyDescriptor.Message("Model used by the tool is being closed. Closing tool.");
                    DialogDisplayer.getDefault().notify(dlg);
                    this.close();
                    return;
                }        
           }
           return;
       }
      if(observable == toolModel && obj == AbstractToolModel.Operation.ExecutionStateChanged)

      //if(observable == toolModel && (obj == AbstractToolModel.Operation.ExecutionStateChanged ||
      //        obj == AbstractToolModel.Operation.InputDataChanged))
         updateDialogButtons();
      else
         updateFromModel(); 
   }

   private void setEnabled(JPanel panel, boolean enabled) {
      for(Component comp : panel.getComponents()) {
         comp.setEnabled(enabled);
         if(comp instanceof JPanel) setEnabled((JPanel)comp, enabled);
      }
   }

   public void updateStaticFields() {
      modelName.setText(toolModel.getOriginalModel().getName());
   }

   public void updateFromModel() {
      internalTrigger = true;

      // Start off with everything enabled
      setEnabled(mainSettingsPanel, true);
      setEnabled(advancedSettingsPanel, true);

      if(mode==Mode.Analyze) 
         updateAnalyzeToolSpecificFields(analyzeToolModel());
      else if(mode==Mode.InverseDynamics || mode==Mode.StaticOptimization) 
         updateInverseToolSpecificFields(analyzeToolModel());
      else if(mode==Mode.ForwardDynamics)
         updateForwardToolSpecificFields(forwardToolModel());
      else if(mode==Mode.RRA)
         updateRRAToolSpecificFields(rraToolModel());
      else if(mode==Mode.CMC)
         updateCMCToolSpecificFields(cmcToolModel());

      // Time
      double[] range = toolModel.getAvailableTimeRange();
      if(range!=null) {
         availableInitialTime.setText(numFormat.format(range[0]));
         availableFinalTime.setText(numFormat.format(range[1]));
      } else {
         availableInitialTime.setText("");
         availableFinalTime.setText("");
      }
      double initTime = toolModel.getInitialTime();
      initialTime.setText(numFormat.format(initTime));
      double finTime = toolModel.getFinalTime();
      finalTime.setText(numFormat.format(finTime));
      
      // Analysis set summary
      String str = "";
      for(int i=0; i<toolModel.getAnalysisSet().getSize(); i++){
          Analysis an = toolModel.getAnalysisSet().get(i);
          str += (i>0 ? ", " : "") + an.getConcreteClassName();
          toolModel.setAnalysisTimeFromTool(an);
      }
      activeAnalyses.setText(str);

      // Output
      outputName.setText(toolModel.getOutputPrefix());
      outputDirectory.setFileName(toolModel.getResultsDirectory(),false);
      outputPrecision.setText(numFormat.format(toolModel.getOutputPrecision()));

      // Actuators & external loads
      actuatorsAndExternalLoadsPanel.updatePanel();

      updateDialogButtons();

      internalTrigger = false;
   }

   public void updateDialogButtons() {
      updateApplyButton(!toolModel.isExecuting() && toolModel.isModified() && toolModel.isValidated());
   }

   //---------------------------------------------------------------------
   // Fields for analyze tool
   //---------------------------------------------------------------------
   public void updateAnalyzeToolSpecificFields(AnalyzeToolModel toolModel) {
      // Controls
      analyzeControlsCheckBox.setSelected(toolModel.getControlsEnabled());
      if(!analyzeControlsCheckBox.isSelected()) analyzeControlsFileName.setEnabled(false);
      analyzeControlsFileName.setFileName(toolModel.getControlsFileName(),false);

      if(toolModel.getInputSource()==AnalyzeToolModel.InputSource.States) buttonGroup1.setSelected(statesRadioButton.getModel(),true);
      else if(toolModel.getInputSource()==AnalyzeToolModel.InputSource.Coordinates) {
         buttonGroup1.setSelected(motionRadioButton.getModel(),true);
         buttonGroup2.setSelected(fromFileMotionRadioButton.getModel(),true);
      } else if(toolModel.getInputSource()==AnalyzeToolModel.InputSource.Motion) {
         buttonGroup1.setSelected(motionRadioButton.getModel(),true);
         buttonGroup2.setSelected(loadedMotionRadioButton.getModel(),true);
      } else {
         buttonGroup1.setSelected(unspecifiedRadioButton.getModel(),true);
      }

      // Motion selections
      ArrayList<Storage> motions = MotionsDB.getInstance().getModelMotions(toolModel.getOriginalModel());
      motionsComboBox.removeAllItems();
      if(motions!=null) for(int i=0; i<motions.size(); i++) motionsComboBox.addItem(motions.get(i).getName());

      if(motions==null || motions.size()==0) loadedMotionRadioButton.setEnabled(false);
      else if(toolModel.getInputMotion()==null) motionsComboBox.setSelectedIndex(-1);
      else motionsComboBox.setSelectedIndex(motions.indexOf(toolModel.getInputMotion()));

      if(!buttonGroup1.isSelected(statesRadioButton.getModel())) statesFileName.setEnabled(false);
      if(!buttonGroup1.isSelected(motionRadioButton.getModel())) {
         fromFileMotionRadioButton.setEnabled(false);
         loadedMotionRadioButton.setEnabled(false);
         filterCoordinatesCheckBox.setEnabled(false);
         cutoffFrequency.setEnabled(false);
      }
      if(!buttonGroup1.isSelected(motionRadioButton.getModel()) || !buttonGroup2.isSelected(fromFileMotionRadioButton.getModel()))
         coordinatesFileName.setEnabled(false);
      if(!buttonGroup1.isSelected(motionRadioButton.getModel()) || !buttonGroup2.isSelected(loadedMotionRadioButton.getModel()))
         motionsComboBox.setEnabled(false);

      statesFileName.setFileName(toolModel.getStatesFileName(),false);
      coordinatesFileName.setFileName(toolModel.getCoordinatesFileName(),false);

      // Filter
      filterCoordinatesCheckBox.setSelected(toolModel.getFilterCoordinates());
      if(!filterCoordinatesCheckBox.isSelected()) {
         cutoffFrequency.setText("");
         cutoffFrequency.setEnabled(false);
      } else {
         cutoffFrequency.setText(numFormat.format(toolModel.getLowpassCutoffFrequency()));
      }

      // Speeds
      //speedsCheckBox.setSelected(toolModel.getLoadSpeeds());
      //if(!speedsCheckBox.isSelected()) speedsFileName.setEnabled(false);
      //speedsFileName.setFileName(toolModel.getSpeedsFileName(),false);

      analyzeSolveForEquilibriumCheckBox.setSelected(toolModel.getSolveForEquilibrium());
   }
   
   //---------------------------------------------------------------------
   // Fields for inverse dynamics tool
   //---------------------------------------------------------------------
   public void updateInverseToolSpecificFields(AnalyzeToolModel toolModel) {

      if(toolModel.getInputSource()==AnalyzeToolModel.InputSource.States) buttonGroup3.setSelected(statesRadioButton1.getModel(),true);
      else if(toolModel.getInputSource()==AnalyzeToolModel.InputSource.Coordinates) {
         buttonGroup3.setSelected(motionRadioButton1.getModel(),true);
         buttonGroup4.setSelected(fromFileMotionRadioButton1.getModel(),true);
         
      } else if(toolModel.getInputSource()==AnalyzeToolModel.InputSource.Motion) {
         buttonGroup3.setSelected(motionRadioButton1.getModel(),true);
         buttonGroup4.setSelected(loadedMotionRadioButton1.getModel(),true);
      } else {
         buttonGroup3.setSelected(unspecifiedRadioButton.getModel(),true);
      }
            // StaticOptimization?
      if (mode==Mode.StaticOptimization){
         double exponent = toolModel.getActivationExponent();
         staticOptActivationExponentTextField.setText(String.valueOf(exponent));
         useForceLengthStaticOptCheckBox.setSelected(toolModel.getUseMusclePhysiology());
         int step = toolModel.getAnalysisStepInterval();
         StepIntervalSpinner.setValue(step);
      }

     // Motion selections
      ArrayList<Storage> motions = MotionsDB.getInstance().getModelMotions(toolModel.getOriginalModel());
      motionsComboBox1.removeAllItems();
      if(motions!=null) for(int i=0; i<motions.size(); i++) motionsComboBox1.addItem(motions.get(i).getName());

      if(motions==null || motions.size()==0) loadedMotionRadioButton1.setEnabled(false);
      else if(toolModel.getInputMotion()==null) motionsComboBox1.setSelectedIndex(-1);
      else motionsComboBox1.setSelectedIndex(motions.indexOf(toolModel.getInputMotion()));

      if(!buttonGroup3.isSelected(statesRadioButton1.getModel())) statesFileName1.setEnabled(false);
      if(!buttonGroup3.isSelected(motionRadioButton1.getModel())) {
         fromFileMotionRadioButton1.setEnabled(false);
         loadedMotionRadioButton1.setEnabled(false);
         filterCoordinatesCheckBox1.setEnabled(false);
         cutoffFrequency1.setEnabled(false);
         HzJLabel.setEnabled(false);
      }
      if(!buttonGroup3.isSelected(motionRadioButton1.getModel()) || !buttonGroup4.isSelected(fromFileMotionRadioButton1.getModel()))
         coordinatesFileName1.setEnabled(false);
      if(!buttonGroup3.isSelected(motionRadioButton1.getModel()) || !buttonGroup4.isSelected(loadedMotionRadioButton1.getModel()))
         motionsComboBox1.setEnabled(false);
      
      if(!buttonGroup3.isSelected(motionRadioButton1.getModel()) && !buttonGroup3.isSelected(statesRadioButton1.getModel())) {
          motionRadioButton1.setSelected(true);
          fromFileMotionRadioButton1.setEnabled(true);
          coordinatesFileName1.setEnabled(true);
          filterCoordinatesCheckBox1.setEnabled(true);
      }
      
      statesFileName1.setFileName(toolModel.getStatesFileName(),false);
      coordinatesFileName1.setFileName(toolModel.getCoordinatesFileName(),false);

      // Filter
      filterCoordinatesCheckBox1.setSelected(toolModel.getFilterCoordinates());
      if(!filterCoordinatesCheckBox1.isSelected()) {
         cutoffFrequency1.setText("");
         cutoffFrequency1.setEnabled(false);
         HzJLabel.setEnabled(false);
      } else {
         cutoffFrequency1.setText(numFormat.format(toolModel.getLowpassCutoffFrequency()));
      }
   }

   //---------------------------------------------------------------------
   // Fields for forward dynamics tool
   //---------------------------------------------------------------------
   public void updateForwardToolSpecificFields(ForwardToolModel toolModel) {
      controlsFileName.setFileName(toolModel.getControlsFileName(),false);
      initialStatesFileName.setFileName(toolModel.getInitialStatesFileName(),false);

      solveForEquilibriumCheckBox.setSelected(toolModel.getSolveForEquilibrium());
      updateIntegratorSettings(toolModel);
   }

   //---------------------------------------------------------------------
   // Fields for CMC tool
   //---------------------------------------------------------------------
   public void updateCMCToolSpecificFields(CMCToolModel toolModel) {
      cmcDesiredKinematicsFileName.setFileName(toolModel.getDesiredKinematicsFileName(),false);
      cmcTaskSetFileName.setFileName(toolModel.getTaskSetFileName(),false);
      cmcConstraintsFileName.setFileName(toolModel.getConstraintsFileName(),false);

      cmcFilterKinematicsCheckBox.setSelected(toolModel.getFilterKinematics());
      if(!cmcFilterKinematicsCheckBox.isSelected()) {
         cmcCutoffFrequency.setText("");
         cmcCutoffFrequency.setEnabled(false);
      } else {
         cmcCutoffFrequency.setText(numFormat.format(toolModel.getLowpassCutoffFrequency()));
      }

      cmcConstraintsCheckBox.setSelected(toolModel.getConstraintsEnabled());
      if(!cmcConstraintsCheckBox.isSelected()) cmcConstraintsFileName.setEnabled(false);
      SelectCmcTargetCheckBox.setSelected(!toolModel.getUseFastTarget());
      //----------------------------------------------------------------------
      // RRA Panel
      //----------------------------------------------------------------------
      rraOutputModelFileName.setEnabled(adjustModelCheckBox.isSelected());
      rraAdjustedBodyComboBox.setEnabled(adjustModelCheckBox.isSelected());
      
      updateIntegratorSettings(toolModel);
      cmcTimeWindow.setText(numFormat.format(toolModel.getCmcTimeWindow()));
   }
   //---------------------------------------------------------------------
   // Fields for RRA tool
   //---------------------------------------------------------------------
    private void updateRRAToolSpecificFields(RRAToolModel toolModel) {
      adjustModelCheckBox.setSelected(toolModel.getAdjustModelToReduceResidualsEnabled());      
      rraOutputModelFileName.setFileName(toolModel.getOutputModelFileName(),false);
      rraAdjustedBodyComboBox.removeAllItems();
      if(toolModel.getOriginalModel()!=null) {
         BodySet bodySet = toolModel.getOriginalModel().getBodySet();
         if(bodySet!=null) {
            for(int i=0; i<bodySet.getSize(); i++) rraAdjustedBodyComboBox.addItem(bodySet.get(i).getName());
            String selectedBody = toolModel.getAdjustedCOMBody();
            int index = bodySet.getIndex(selectedBody);
            rraAdjustedBodyComboBox.setSelectedIndex(index);
         }
      }
      cmcDesiredKinematicsFileName.setFileName(toolModel.getDesiredKinematicsFileName(),false);
      cmcTaskSetFileName.setFileName(toolModel.getTaskSetFileName(),false);
      cmcConstraintsFileName.setVisible(false);

      cmcFilterKinematicsCheckBox.setSelected(toolModel.getFilterKinematics());
      if(!cmcFilterKinematicsCheckBox.isSelected()) {
         cmcCutoffFrequency.setText("");
         cmcCutoffFrequency.setEnabled(false);
      } else {
         cmcCutoffFrequency.setText(numFormat.format(toolModel.getLowpassCutoffFrequency()));
      }

      cmcConstraintsCheckBox.setVisible(false);
      //if(!cmcConstraintsCheckBox.isSelected()) cmcConstraintsFileName.setEnabled(false);

      updateIntegratorSettings(toolModel);
    }

   //---------------------------------------------------------------------
   // Integrator settings fields
   //---------------------------------------------------------------------
   public void updateIntegratorSettings(AbstractToolModel toolModel) {
      maximumNumberOfSteps.setText(numFormat.format(toolModel.getMaximumNumberOfSteps()));
      maxDT.setText(numFormat.format(toolModel.getMaxDT()));
      minDT.setText(numFormat.format(toolModel.getMinDT()));
      errorTolerance.setText(numFormat.format(toolModel.getErrorTolerance()));
      //useSpecifiedDtActionPerformed(null);
   }

   //------------------------------------------------------------------------
   // Overrides from BaseToolPanel
   //------------------------------------------------------------------------

   public void loadSettings(String fileName) { toolModel.loadSettings(fileName); }
   public void saveSettings(String fileName) { toolModel.saveSettings(fileName); }

   public void pressedCancel() {
      toolModel.cancel();
   }

   public void pressedClose() {
   }

   public void pressedApply() {
      toolModel.execute();
      updateDialogButtons();
   }
   
   /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        SelectCmcTargetCheckBox = new javax.swing.JCheckBox();
        advancedSettingsPanel = new javax.swing.JPanel();
        integratorSettingsPanel = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        maximumNumberOfSteps = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        maxDT = new javax.swing.JTextField();
        errorTolerance = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        minDT = new javax.swing.JTextField();
        buttonGroup1 = new javax.swing.ButtonGroup();
        unspecifiedRadioButton = new javax.swing.JRadioButton();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        buttonGroup4 = new javax.swing.ButtonGroup();
        plotMetricsPanel = new javax.swing.JPanel();
        plotMetricsCheckBox = new javax.swing.JCheckBox();
        reuseSelectedQuantitiesCheckBox = new javax.swing.JCheckBox();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        mainSettingsPanel = new javax.swing.JPanel();
        analyzeInputPanel = new javax.swing.JPanel();
        motionsComboBox = new javax.swing.JComboBox();
        statesRadioButton = new javax.swing.JRadioButton();
        motionRadioButton = new javax.swing.JRadioButton();
        statesFileName = new org.opensim.swingui.FileTextFieldAndChooser();
        coordinatesFileName = new org.opensim.swingui.FileTextFieldAndChooser();
        filterCoordinatesCheckBox = new javax.swing.JCheckBox();
        cutoffFrequency = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        analyzeControlsCheckBox = new javax.swing.JCheckBox();
        analyzeControlsFileName = new org.opensim.swingui.FileTextFieldAndChooser();
        fromFileMotionRadioButton = new javax.swing.JRadioButton();
        loadedMotionRadioButton = new javax.swing.JRadioButton();
        analyzeSolveForEquilibriumCheckBox = new javax.swing.JCheckBox();
        editAnalyzeExcitationsButton = new javax.swing.JButton();
        outputPanel = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        outputPrecision = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        outputDirectory = new org.opensim.swingui.FileTextFieldAndChooser();
        jLabel6 = new javax.swing.JLabel();
        outputName = new javax.swing.JTextField();
        availableFinalTime = new javax.swing.JTextField();
        timePanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        initialTime = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        finalTime = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        availableInitialTime = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        cmcTimeWindow = new javax.swing.JTextField();
        modelInfoPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        modelName = new javax.swing.JTextField();
        activeAnalysesPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        activeAnalyses = new javax.swing.JTextField();
        editAnalysesButton = new javax.swing.JButton();
        forwardInputPanel = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        controlsFileName = new org.opensim.swingui.FileTextFieldAndChooser();
        jLabel12 = new javax.swing.JLabel();
        initialStatesFileName = new org.opensim.swingui.FileTextFieldAndChooser();
        solveForEquilibriumCheckBox = new javax.swing.JCheckBox();
        editForwardExcitationsButton = new javax.swing.JButton();
        cmcInputPanel = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        cmcDesiredKinematicsFileName = new org.opensim.swingui.FileTextFieldAndChooser();
        cmcFilterKinematicsCheckBox = new javax.swing.JCheckBox();
        cmcCutoffFrequency = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        cmcTaskSetFileName = new FileTextFieldAndChooserWithEdit();
        cmcConstraintsFileName = new FileTextFieldAndChooserWithEdit(new java.awt.event.ActionListener(){
            public void actionPerformed(java.awt.event.ActionEvent evt){
                editExcitationsFile(cmcConstraintsFileName);
            }});
            jLabel22 = new javax.swing.JLabel();
            cmcConstraintsCheckBox = new javax.swing.JCheckBox();
            rraPanel = new javax.swing.JPanel();
            jLabel21 = new javax.swing.JLabel();
            rraAdjustedBodyComboBox = new javax.swing.JComboBox();
            rraOutputModelFileName = new org.opensim.swingui.FileTextFieldAndChooser();
            adjustModelCheckBox =  new JCheckBox(new EnableReduceResidualsAction());
            inverseInputPanel = new javax.swing.JPanel();
            motionsComboBox1 = new javax.swing.JComboBox();
            statesRadioButton1 = new javax.swing.JRadioButton();
            motionRadioButton1 = new javax.swing.JRadioButton();
            statesFileName1 = new org.opensim.swingui.FileTextFieldAndChooser();
            coordinatesFileName1 = new org.opensim.swingui.FileTextFieldAndChooser();
            filterCoordinatesCheckBox1 = new javax.swing.JCheckBox();
            cutoffFrequency1 = new javax.swing.JTextField();
            HzJLabel = new javax.swing.JLabel();
            fromFileMotionRadioButton1 = new javax.swing.JRadioButton();
            loadedMotionRadioButton1 = new javax.swing.JRadioButton();
            staticOptimizationPanel = new javax.swing.JPanel();
            jPanel1 = new javax.swing.JPanel();
            jLabel14 = new javax.swing.JLabel();
            StepIntervalSpinner = new javax.swing.JSpinner();
            jLabel18 = new javax.swing.JLabel();
            jPanel2 = new javax.swing.JPanel();
            jLabel24 = new javax.swing.JLabel();
            staticOptActivationExponentTextField = new javax.swing.JTextField();
            useForceLengthStaticOptCheckBox = new javax.swing.JCheckBox();

            SelectCmcTargetCheckBox.setText("Adjust kinematics");
            SelectCmcTargetCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
            SelectCmcTargetCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
            SelectCmcTargetCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    SelectCmcTargetCheckBoxActionPerformed(evt);
                }
            });

            integratorSettingsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Integrator Settings"));

            jLabel15.setText("Integrator error tolerance");

            jLabel16.setText("Maximum number of steps");

            maximumNumberOfSteps.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
            maximumNumberOfSteps.setText("jTextField5");
            maximumNumberOfSteps.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    maximumNumberOfStepsActionPerformed(evt);
                }
            });
            maximumNumberOfSteps.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent evt) {
                    maximumNumberOfStepsFocusLost(evt);
                }
            });

            jLabel17.setText("Maximum step size");

            maxDT.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
            maxDT.setText("jTextField6");
            maxDT.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    maxDTActionPerformed(evt);
                }
            });
            maxDT.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent evt) {
                    maxDTFocusLost(evt);
                }
            });

            errorTolerance.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
            errorTolerance.setText("jTextField7");
            errorTolerance.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    errorToleranceActionPerformed(evt);
                }
            });
            errorTolerance.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent evt) {
                    errorToleranceFocusLost(evt);
                }
            });

            jLabel13.setText("Minimum step size");

            minDT.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
            minDT.setText("jTextField1");
            minDT.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    minDTActionPerformed(evt);
                }
            });
            minDT.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent evt) {
                    minDTFocusLost(evt);
                }
            });

            org.jdesktop.layout.GroupLayout integratorSettingsPanelLayout = new org.jdesktop.layout.GroupLayout(integratorSettingsPanel);
            integratorSettingsPanel.setLayout(integratorSettingsPanelLayout);
            integratorSettingsPanelLayout.setHorizontalGroup(
                integratorSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, integratorSettingsPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .add(integratorSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jLabel16)
                        .add(jLabel17)
                        .add(jLabel15, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jLabel13))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(integratorSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, minDT, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 6, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, errorTolerance, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, maxDT, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, maximumNumberOfSteps, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                    .add(216, 216, 216))
            );
            integratorSettingsPanelLayout.setVerticalGroup(
                integratorSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(integratorSettingsPanelLayout.createSequentialGroup()
                    .add(0, 0, 0)
                    .add(integratorSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel16)
                        .add(maximumNumberOfSteps, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(integratorSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel17)
                        .add(maxDT, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(integratorSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(minDT, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jLabel13))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(integratorSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(errorTolerance, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jLabel15))
                    .addContainerGap())
            );

            org.jdesktop.layout.GroupLayout advancedSettingsPanelLayout = new org.jdesktop.layout.GroupLayout(advancedSettingsPanel);
            advancedSettingsPanel.setLayout(advancedSettingsPanelLayout);
            advancedSettingsPanelLayout.setHorizontalGroup(
                advancedSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(advancedSettingsPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .add(integratorSettingsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap())
            );
            advancedSettingsPanelLayout.setVerticalGroup(
                advancedSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(advancedSettingsPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .add(integratorSettingsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 208, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(0, 0, 0))
            );

            buttonGroup1.add(unspecifiedRadioButton);
            unspecifiedRadioButton.setText("jRadioButton1");
            unspecifiedRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
            unspecifiedRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

            plotMetricsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Plot"));

            plotMetricsCheckBox.setText("Plot quantities while running ");
            plotMetricsCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
            plotMetricsCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
            plotMetricsCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    plotMetricsCheckBoxActionPerformed(evt);
                }
            });

            reuseSelectedQuantitiesCheckBox.setText("Use quantities from previous tool invocation (otherwise you'll be prompted)");
            reuseSelectedQuantitiesCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
            reuseSelectedQuantitiesCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
            reuseSelectedQuantitiesCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    reuseSelectedQuantitiesCheckBoxActionPerformed(evt);
                }
            });

            org.jdesktop.layout.GroupLayout plotMetricsPanelLayout = new org.jdesktop.layout.GroupLayout(plotMetricsPanel);
            plotMetricsPanel.setLayout(plotMetricsPanelLayout);
            plotMetricsPanelLayout.setHorizontalGroup(
                plotMetricsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(plotMetricsPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .add(plotMetricsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(plotMetricsCheckBox)
                        .add(reuseSelectedQuantitiesCheckBox))
                    .addContainerGap(50, Short.MAX_VALUE))
            );
            plotMetricsPanelLayout.setVerticalGroup(
                plotMetricsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(plotMetricsPanelLayout.createSequentialGroup()
                    .add(plotMetricsCheckBox)
                    .add(14, 14, 14)
                    .add(reuseSelectedQuantitiesCheckBox))
            );

            analyzeInputPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Input"));

            motionsComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
            motionsComboBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    motionsComboBoxActionPerformed(evt);
                }
            });

            buttonGroup1.add(statesRadioButton);
            statesRadioButton.setText("States");
            statesRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
            statesRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
            statesRadioButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    inputSourceRadioButtonActionPerformed(evt);
                }
            });

            buttonGroup1.add(motionRadioButton);
            motionRadioButton.setText("Motion");
            motionRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
            motionRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
            motionRadioButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    inputSourceRadioButtonActionPerformed(evt);
                }
            });

            statesFileName.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent evt) {
                    statesFileNameStateChanged(evt);
                }
            });

            coordinatesFileName.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent evt) {
                    coordinatesFileNameStateChanged(evt);
                }
            });

            filterCoordinatesCheckBox.setText("Filter coordinates");
            filterCoordinatesCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
            filterCoordinatesCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
            filterCoordinatesCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    filterCoordinatesCheckBoxActionPerformed(evt);
                }
            });

            cutoffFrequency.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
            cutoffFrequency.setText("jTextField1");
            cutoffFrequency.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    cutoffFrequencyActionPerformed(evt);
                }
            });
            cutoffFrequency.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent evt) {
                    cutoffFrequencyFocusLost(evt);
                }
            });

            jLabel1.setText("Hz");

            analyzeControlsCheckBox.setText("Controls");
            analyzeControlsCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
            analyzeControlsCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
            analyzeControlsCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    analyzeControlsCheckBoxActionPerformed(evt);
                }
            });

            analyzeControlsFileName.setToolTipText("xml file containing controls ");
            analyzeControlsFileName.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent evt) {
                    analyzeControlsFileNameStateChanged(evt);
                }
            });

            buttonGroup2.add(fromFileMotionRadioButton);
            fromFileMotionRadioButton.setSelected(true);
            fromFileMotionRadioButton.setText("From file");
            fromFileMotionRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
            fromFileMotionRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
            fromFileMotionRadioButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    inputSourceRadioButtonActionPerformed(evt);
                }
            });

            buttonGroup2.add(loadedMotionRadioButton);
            loadedMotionRadioButton.setText("Loaded motion");
            loadedMotionRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
            loadedMotionRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
            loadedMotionRadioButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    inputSourceRadioButtonActionPerformed(evt);
                }
            });

            analyzeSolveForEquilibriumCheckBox.setText("Solve for equilibrium for actuator states");
            analyzeSolveForEquilibriumCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
            analyzeSolveForEquilibriumCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
            analyzeSolveForEquilibriumCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    analyzeSolveForEquilibriumCheckBoxActionPerformed(evt);
                }
            });

            editAnalyzeExcitationsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/swingui/editor.gif"))); // NOI18N
            editAnalyzeExcitationsButton.setPreferredSize(new java.awt.Dimension(49, 20));
            editAnalyzeExcitationsButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    editAnalyzeExcitationsButtonActionPerformed(evt);
                }
            });

            org.jdesktop.layout.GroupLayout analyzeInputPanelLayout = new org.jdesktop.layout.GroupLayout(analyzeInputPanel);
            analyzeInputPanel.setLayout(analyzeInputPanelLayout);
            analyzeInputPanelLayout.setHorizontalGroup(
                analyzeInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(analyzeInputPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .add(analyzeInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(analyzeInputPanelLayout.createSequentialGroup()
                            .add(analyzeInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(statesRadioButton)
                                .add(motionRadioButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .add(14, 14, 14)
                            .add(analyzeInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(statesFileName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(analyzeInputPanelLayout.createSequentialGroup()
                                    .add(analyzeInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(filterCoordinatesCheckBox)
                                        .add(loadedMotionRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 101, Short.MAX_VALUE)
                                        .add(fromFileMotionRadioButton))
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                    .add(analyzeInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(motionsComboBox, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .add(coordinatesFileName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .add(cutoffFrequency))))
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(jLabel1))
                        .add(analyzeSolveForEquilibriumCheckBox)
                        .add(analyzeInputPanelLayout.createSequentialGroup()
                            .add(analyzeControlsCheckBox)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(analyzeControlsFileName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(0, 0, 0)
                            .add(editAnalyzeExcitationsButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 33, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap())
            );

            analyzeInputPanelLayout.linkSize(new java.awt.Component[] {motionRadioButton, statesRadioButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

            analyzeInputPanelLayout.linkSize(new java.awt.Component[] {filterCoordinatesCheckBox, fromFileMotionRadioButton, loadedMotionRadioButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

            analyzeInputPanelLayout.setVerticalGroup(
                analyzeInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(analyzeInputPanelLayout.createSequentialGroup()
                    .add(analyzeInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(analyzeControlsCheckBox)
                        .add(analyzeInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                            .add(editAnalyzeExcitationsButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(analyzeControlsFileName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(22, 22, 22)
                    .add(analyzeInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(statesRadioButton)
                        .add(statesFileName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(analyzeInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(analyzeInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(fromFileMotionRadioButton)
                            .add(motionRadioButton))
                        .add(coordinatesFileName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(analyzeInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(motionsComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(loadedMotionRadioButton))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(analyzeInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(filterCoordinatesCheckBox)
                        .add(analyzeInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(cutoffFrequency, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel1)))
                    .add(18, 18, 18)
                    .add(analyzeSolveForEquilibriumCheckBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );

            outputPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Output"));

            jLabel10.setText("Precision");

            outputPrecision.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
            outputPrecision.setText("jTextField1");
            outputPrecision.setMinimumSize(new java.awt.Dimension(40, 20));
            outputPrecision.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    outputPrecisionActionPerformed(evt);
                }
            });
            outputPrecision.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent evt) {
                    outputPrecisionFocusLost(evt);
                }
            });

            jLabel11.setText("Directory");

            outputDirectory.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent evt) {
                    outputDirectoryStateChanged(evt);
                }
            });

            jLabel6.setText("Prefix");

            outputName.setText("jTextField1");
            outputName.setToolTipText("Prefix to be attached to output file names");
            outputName.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    outputNameActionPerformed(evt);
                }
            });
            outputName.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent evt) {
                    outputNameFocusLost(evt);
                }
            });

            availableFinalTime.setEditable(false);
            availableFinalTime.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
            availableFinalTime.setText("jTextField2");

            org.jdesktop.layout.GroupLayout outputPanelLayout = new org.jdesktop.layout.GroupLayout(outputPanel);
            outputPanel.setLayout(outputPanelLayout);
            outputPanelLayout.setHorizontalGroup(
                outputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(outputPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .add(outputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(outputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jLabel6)
                            .add(jLabel11))
                        .add(jLabel10))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(outputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(outputPrecision, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 64, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(outputPanelLayout.createSequentialGroup()
                            .add(outputName)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(availableFinalTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(outputDirectory, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap())
            );
            outputPanelLayout.setVerticalGroup(
                outputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(outputPanelLayout.createSequentialGroup()
                    .add(outputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(outputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel6)
                            .add(outputName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(outputPanelLayout.createSequentialGroup()
                            .addContainerGap()
                            .add(availableFinalTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(outputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(jLabel11)
                        .add(outputDirectory, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(outputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel10)
                        .add(outputPrecision, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );

            timePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Time"));

            jLabel4.setText("Time range to process");

            initialTime.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
            initialTime.setText("jTextField2");
            initialTime.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    initialTimeActionPerformed(evt);
                }
            });
            initialTime.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent evt) {
                    initialTimeFocusLost(evt);
                }
            });

            jLabel5.setText("to");

            finalTime.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
            finalTime.setText("jTextField1");
            finalTime.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    finalTimeActionPerformed(evt);
                }
            });
            finalTime.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent evt) {
                    finalTimeFocusLost(evt);
                }
            });

            jLabel7.setText("Available time range from input data");

            availableInitialTime.setEditable(false);
            availableInitialTime.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
            availableInitialTime.setText("jTextField1");

            jLabel8.setText("to");

            jLabel23.setText("CMC look-ahead window");

            cmcTimeWindow.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
            cmcTimeWindow.setText("jTextField1");
            cmcTimeWindow.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    cmcTimeWindowActionPerformed(evt);
                }
            });
            cmcTimeWindow.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent evt) {
                    cmcTimeWindowFocusLost(evt);
                }
            });

            org.jdesktop.layout.GroupLayout timePanelLayout = new org.jdesktop.layout.GroupLayout(timePanel);
            timePanel.setLayout(timePanelLayout);
            timePanelLayout.setHorizontalGroup(
                timePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(timePanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .add(timePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel7)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, timePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel23)
                            .add(jLabel4)))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(timePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(timePanelLayout.createSequentialGroup()
                            .add(availableInitialTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(jLabel8))
                        .add(timePanelLayout.createSequentialGroup()
                            .add(timePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, cmcTimeWindow)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, initialTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 64, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(jLabel5)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(finalTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 64, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            timePanelLayout.setVerticalGroup(
                timePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(timePanelLayout.createSequentialGroup()
                    .add(timePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel7)
                        .add(availableInitialTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jLabel8))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(timePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel4)
                        .add(initialTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jLabel5)
                        .add(finalTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(timePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel23)
                        .add(cmcTimeWindow, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );

            modelInfoPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Current Model"));

            jLabel2.setText("Name");

            modelName.setEditable(false);
            modelName.setText("jTextField1");
            modelName.setToolTipText("Current Model in GUI");

            org.jdesktop.layout.GroupLayout modelInfoPanelLayout = new org.jdesktop.layout.GroupLayout(modelInfoPanel);
            modelInfoPanel.setLayout(modelInfoPanelLayout);
            modelInfoPanelLayout.setHorizontalGroup(
                modelInfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(modelInfoPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .add(jLabel2)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(modelName)
                    .addContainerGap())
            );
            modelInfoPanelLayout.setVerticalGroup(
                modelInfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(modelInfoPanelLayout.createSequentialGroup()
                    .add(6, 6, 6)
                    .add(modelInfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(modelName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jLabel2))
                    .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );

            activeAnalysesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Analysis Set"));

            jLabel3.setText("Active analyses");

            activeAnalyses.setEditable(false);
            activeAnalyses.setText("jTextField2");

            editAnalysesButton.setText("Edit");
            editAnalysesButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    editAnalysesButtonActionPerformed(evt);
                }
            });

            org.jdesktop.layout.GroupLayout activeAnalysesPanelLayout = new org.jdesktop.layout.GroupLayout(activeAnalysesPanel);
            activeAnalysesPanel.setLayout(activeAnalysesPanelLayout);
            activeAnalysesPanelLayout.setHorizontalGroup(
                activeAnalysesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(activeAnalysesPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .add(jLabel3)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(activeAnalyses)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(editAnalysesButton))
            );
            activeAnalysesPanelLayout.setVerticalGroup(
                activeAnalysesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(activeAnalysesPanelLayout.createSequentialGroup()
                    .add(activeAnalysesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel3)
                        .add(editAnalysesButton)
                        .add(activeAnalyses, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );

            forwardInputPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Input"));

            jLabel9.setText("Controls");

            controlsFileName.setToolTipText("xml file containing controls ");
            controlsFileName.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent evt) {
                    controlsFileNameStateChanged(evt);
                }
            });

            jLabel12.setText("Initial State");

            initialStatesFileName.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent evt) {
                    initialStatesFileNameStateChanged(evt);
                }
            });

            solveForEquilibriumCheckBox.setText("Solve for equilibrium for actuator states");
            solveForEquilibriumCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
            solveForEquilibriumCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
            solveForEquilibriumCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    solveForEquilibriumCheckBoxActionPerformed(evt);
                }
            });

            editForwardExcitationsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/swingui/editor.gif"))); // NOI18N
            editForwardExcitationsButton.setPreferredSize(new java.awt.Dimension(49, 20));
            editForwardExcitationsButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    editForwardExcitationsButtonActionPerformed(evt);
                }
            });

            org.jdesktop.layout.GroupLayout forwardInputPanelLayout = new org.jdesktop.layout.GroupLayout(forwardInputPanel);
            forwardInputPanel.setLayout(forwardInputPanelLayout);
            forwardInputPanelLayout.setHorizontalGroup(
                forwardInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(forwardInputPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .add(forwardInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(forwardInputPanelLayout.createSequentialGroup()
                            .add(19, 19, 19)
                            .add(forwardInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                .add(jLabel9)
                                .add(jLabel12))
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(forwardInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(initialStatesFileName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(controlsFileName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .add(0, 0, 0)
                            .add(editForwardExcitationsButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 33, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(solveForEquilibriumCheckBox))
                    .addContainerGap())
            );
            forwardInputPanelLayout.setVerticalGroup(
                forwardInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(forwardInputPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .add(forwardInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                        .add(editForwardExcitationsButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(controlsFileName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(forwardInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(jLabel12)
                        .add(initialStatesFileName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(solveForEquilibriumCheckBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap())
                .add(forwardInputPanelLayout.createSequentialGroup()
                    .add(18, 18, 18)
                    .add(jLabel9)
                    .add(79, 79, 79))
            );

            cmcInputPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Input"));

            jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
            jLabel19.setText("Desired kinematics");

            cmcDesiredKinematicsFileName.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent evt) {
                    cmcDesiredKinematicsFileNameStateChanged(evt);
                }
            });

            cmcFilterKinematicsCheckBox.setText("Filter kinematics");
            cmcFilterKinematicsCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
            cmcFilterKinematicsCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
            cmcFilterKinematicsCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    cmcFilterKinematicsCheckBoxActionPerformed(evt);
                }
            });

            cmcCutoffFrequency.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
            cmcCutoffFrequency.setText("jTextField1");
            cmcCutoffFrequency.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    cmcCutoffFrequencyActionPerformed(evt);
                }
            });
            cmcCutoffFrequency.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent evt) {
                    cmcCutoffFrequencyFocusLost(evt);
                }
            });

            jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
            jLabel20.setText("Tracking tasks");

            cmcTaskSetFileName.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent evt) {
                    cmcTaskSetFileNameStateChanged(evt);
                }
            });

            cmcConstraintsFileName.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent evt) {
                    cmcConstraintsFileNameStateChanged(evt);
                }
            });

            jLabel22.setText("Hz");

            cmcConstraintsCheckBox.setText("Actuator constraints");
            cmcConstraintsCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
            cmcConstraintsCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
            cmcConstraintsCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    cmcConstraintsCheckBoxActionPerformed(evt);
                }
            });

            org.jdesktop.layout.GroupLayout cmcInputPanelLayout = new org.jdesktop.layout.GroupLayout(cmcInputPanel);
            cmcInputPanel.setLayout(cmcInputPanelLayout);
            cmcInputPanelLayout.setHorizontalGroup(
                cmcInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(cmcInputPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .add(cmcInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(jLabel20)
                        .add(cmcConstraintsCheckBox)
                        .add(jLabel19))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(cmcInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(cmcInputPanelLayout.createSequentialGroup()
                            .add(cmcFilterKinematicsCheckBox)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(cmcCutoffFrequency))
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, cmcDesiredKinematicsFileName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(cmcTaskSetFileName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(cmcConstraintsFileName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(jLabel22)
                    .addContainerGap())
            );
            cmcInputPanelLayout.setVerticalGroup(
                cmcInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(cmcInputPanelLayout.createSequentialGroup()
                    .add(cmcInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(cmcDesiredKinematicsFileName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jLabel19))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(cmcInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel22)
                        .add(cmcFilterKinematicsCheckBox)
                        .add(cmcCutoffFrequency, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(cmcInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                        .add(jLabel20)
                        .add(cmcTaskSetFileName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(cmcInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                        .add(cmcConstraintsCheckBox)
                        .add(cmcConstraintsFileName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );

            rraPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Reduce Residuals"));

            jLabel21.setText("Body COM to adjust");

            rraAdjustedBodyComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
            rraAdjustedBodyComboBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    rraAdjustedBodyComboBoxActionPerformed(evt);
                }
            });

            rraOutputModelFileName.setIncludeOpenButton(false);
            rraOutputModelFileName.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent evt) {
                    rraOutputModelFileNameStateChanged(evt);
                }
            });

            adjustModelCheckBox.setSelected(true);
            adjustModelCheckBox.setText("Adjust model");
            adjustModelCheckBox.setToolTipText("Modify model to reduce residuals");
            adjustModelCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
            adjustModelCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
            adjustModelCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    adjustModelCheckBoxActionPerformed(evt);
                }
            });

            org.jdesktop.layout.GroupLayout rraPanelLayout = new org.jdesktop.layout.GroupLayout(rraPanel);
            rraPanel.setLayout(rraPanelLayout);
            rraPanelLayout.setHorizontalGroup(
                rraPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(rraPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .add(rraPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jLabel21)
                        .add(adjustModelCheckBox))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(rraPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(rraOutputModelFileName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(rraAdjustedBodyComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 154, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap())
            );
            rraPanelLayout.setVerticalGroup(
                rraPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(rraPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .add(rraPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(adjustModelCheckBox)
                        .add(rraOutputModelFileName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(rraPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel21)
                        .add(rraAdjustedBodyComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );

            inverseInputPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Input"));

            motionsComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
            motionsComboBox1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    motionsComboBox1ActionPerformed(evt);
                }
            });

            buttonGroup3.add(statesRadioButton1);
            statesRadioButton1.setText("States");
            statesRadioButton1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
            statesRadioButton1.setMargin(new java.awt.Insets(0, 0, 0, 0));
            statesRadioButton1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    inputSourceRadioButtonActionPerformed1(evt);
                }
            });

            buttonGroup3.add(motionRadioButton1);
            motionRadioButton1.setText("Motion");
            motionRadioButton1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
            motionRadioButton1.setMargin(new java.awt.Insets(0, 0, 0, 0));
            motionRadioButton1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    inputSourceRadioButtonActionPerformed1(evt);
                }
            });

            statesFileName1.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent evt) {
                    statesFileName1StateChanged(evt);
                }
            });

            coordinatesFileName1.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent evt) {
                    coordinatesFileName1StateChanged(evt);
                }
            });

            filterCoordinatesCheckBox1.setText("Filter coordinates");
            filterCoordinatesCheckBox1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
            filterCoordinatesCheckBox1.setMargin(new java.awt.Insets(0, 0, 0, 0));
            filterCoordinatesCheckBox1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    filterCoordinatesCheckBox1ActionPerformed(evt);
                }
            });

            cutoffFrequency1.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
            cutoffFrequency1.setText("jTextField1");
            cutoffFrequency1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    cutoffFrequency1ActionPerformed(evt);
                }
            });
            cutoffFrequency1.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent evt) {
                    cutoffFrequency1FocusLost(evt);
                }
            });

            HzJLabel.setText("Hz");

            buttonGroup4.add(fromFileMotionRadioButton1);
            fromFileMotionRadioButton1.setSelected(true);
            fromFileMotionRadioButton1.setText("From file");
            fromFileMotionRadioButton1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
            fromFileMotionRadioButton1.setMargin(new java.awt.Insets(0, 0, 0, 0));
            fromFileMotionRadioButton1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    inputSourceRadioButtonActionPerformed1(evt);
                }
            });

            buttonGroup4.add(loadedMotionRadioButton1);
            loadedMotionRadioButton1.setText("Loaded motion");
            loadedMotionRadioButton1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
            loadedMotionRadioButton1.setMargin(new java.awt.Insets(0, 0, 0, 0));
            loadedMotionRadioButton1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    inputSourceRadioButtonActionPerformed1(evt);
                }
            });

            org.jdesktop.layout.GroupLayout inverseInputPanelLayout = new org.jdesktop.layout.GroupLayout(inverseInputPanel);
            inverseInputPanel.setLayout(inverseInputPanelLayout);
            inverseInputPanelLayout.setHorizontalGroup(
                inverseInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(inverseInputPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .add(inverseInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                        .add(statesRadioButton1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(motionRadioButton1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(inverseInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(inverseInputPanelLayout.createSequentialGroup()
                            .add(inverseInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(fromFileMotionRadioButton1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 82, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(inverseInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, loadedMotionRadioButton1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, filterCoordinatesCheckBox1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(inverseInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(org.jdesktop.layout.GroupLayout.TRAILING, motionsComboBox1, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(org.jdesktop.layout.GroupLayout.TRAILING, coordinatesFileName1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(org.jdesktop.layout.GroupLayout.TRAILING, cutoffFrequency1)))
                        .add(statesFileName1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(HzJLabel)
                    .addContainerGap())
            );
            inverseInputPanelLayout.setVerticalGroup(
                inverseInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(inverseInputPanelLayout.createSequentialGroup()
                    .add(0, 0, 0)
                    .add(inverseInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(statesRadioButton1)
                        .add(statesFileName1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(inverseInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(inverseInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(motionRadioButton1)
                            .add(fromFileMotionRadioButton1))
                        .add(coordinatesFileName1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(inverseInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(motionsComboBox1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(loadedMotionRadioButton1))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(inverseInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(HzJLabel)
                        .add(filterCoordinatesCheckBox1)
                        .add(cutoffFrequency1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );

            staticOptimizationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Static Optimization"));

            jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Step Interval"));

            jLabel14.setText("Analyze every ");

            StepIntervalSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, 100, 1));
            StepIntervalSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent evt) {
                    StepIntervalSpinnerStateChanged(evt);
                }
            });
            StepIntervalSpinner.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent evt) {
                    StepIntervalSpinnerFocusLost(evt);
                }
            });

            jLabel18.setText("step(s)");

            org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
            jPanel1.setLayout(jPanel1Layout);
            jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jPanel1Layout.createSequentialGroup()
                    .add(jLabel14)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(StepIntervalSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 39, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(jLabel18)
                    .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel14)
                        .add(StepIntervalSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jLabel18))
                    .addContainerGap(34, Short.MAX_VALUE))
            );

            jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Objective Function"));

            jLabel24.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
            jLabel24.setText("Sum of (muscle activation) ^");

            staticOptActivationExponentTextField.setText("2");
            staticOptActivationExponentTextField.setToolTipText("power to raise muscle activation while solving");
            staticOptActivationExponentTextField.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    staticOptActivationExponentTextFieldActionPerformed(evt);
                }
            });
            staticOptActivationExponentTextField.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent evt) {
                    staticOptActivationExponentTextFieldFocusLost(evt);
                }
            });

            useForceLengthStaticOptCheckBox.setText("Use muscle force-length-velocity relation");
            useForceLengthStaticOptCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
            useForceLengthStaticOptCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
            useForceLengthStaticOptCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    useForceLengthStaticOptCheckBoxActionPerformed(evt);
                }
            });
            useForceLengthStaticOptCheckBox.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent evt) {
                    useForceLengthStaticOptCheckBoxFocusLost(evt);
                }
            });

            org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
            jPanel2.setLayout(jPanel2Layout);
            jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel2Layout.createSequentialGroup()
                            .add(jLabel24)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(staticOptActivationExponentTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 42, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(useForceLengthStaticOptCheckBox))
                    .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
            jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabel24)
                        .add(staticOptActivationExponentTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(useForceLengthStaticOptCheckBox)
                    .add(36, 36, 36))
            );

            org.jdesktop.layout.GroupLayout staticOptimizationPanelLayout = new org.jdesktop.layout.GroupLayout(staticOptimizationPanel);
            staticOptimizationPanel.setLayout(staticOptimizationPanelLayout);
            staticOptimizationPanelLayout.setHorizontalGroup(
                staticOptimizationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(org.jdesktop.layout.GroupLayout.TRAILING, staticOptimizationPanelLayout.createSequentialGroup()
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap())
            );
            staticOptimizationPanelLayout.setVerticalGroup(
                staticOptimizationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(staticOptimizationPanelLayout.createSequentialGroup()
                    .add(staticOptimizationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2, 0, 122, Short.MAX_VALUE))
                    .addContainerGap())
            );

            org.jdesktop.layout.GroupLayout mainSettingsPanelLayout = new org.jdesktop.layout.GroupLayout(mainSettingsPanel);
            mainSettingsPanel.setLayout(mainSettingsPanelLayout);
            mainSettingsPanelLayout.setHorizontalGroup(
                mainSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(mainSettingsPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .add(mainSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(modelInfoPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(analyzeInputPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(forwardInputPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, inverseInputPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(cmcInputPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(staticOptimizationPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(rraPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(timePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(activeAnalysesPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(outputPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap())
            );
            mainSettingsPanelLayout.setVerticalGroup(
                mainSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(mainSettingsPanelLayout.createSequentialGroup()
                    .add(modelInfoPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(analyzeInputPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(forwardInputPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(inverseInputPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(cmcInputPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(staticOptimizationPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(rraPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(timePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(activeAnalysesPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(outputPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap())
            );

            jTabbedPane1.addTab("Main Settings", mainSettingsPanel);

            org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
            this.setLayout(layout);
            layout.setHorizontalGroup(
                layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(layout.createSequentialGroup()
                    .add(jTabbedPane1)
                    .add(0, 0, Short.MAX_VALUE))
            );
            layout.setVerticalGroup(
                layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jTabbedPane1)
            );
        }// </editor-fold>//GEN-END:initComponents

    private void useForceLengthStaticOptCheckBoxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_useForceLengthStaticOptCheckBoxFocusLost
       if(!evt.isTemporary()) useForceLengthStaticOptCheckBoxActionPerformed(null);
// TODO add your handling code here:
    }//GEN-LAST:event_useForceLengthStaticOptCheckBoxFocusLost

    private void useForceLengthStaticOptCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useForceLengthStaticOptCheckBoxActionPerformed
          ((AnalyzeToolModel)toolModel).setUseMusclePhysiology(useForceLengthStaticOptCheckBox.isSelected());
    }//GEN-LAST:event_useForceLengthStaticOptCheckBoxActionPerformed

    private void staticOptActivationExponentTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_staticOptActivationExponentTextFieldFocusLost
// TODO add your handling code here:
       if(!evt.isTemporary()) staticOptActivationExponentTextFieldActionPerformed(null);
    }//GEN-LAST:event_staticOptActivationExponentTextFieldFocusLost

    private void staticOptActivationExponentTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_staticOptActivationExponentTextFieldActionPerformed
      try {
         ((AnalyzeToolModel)toolModel).setActivationExponent(numFormat.parse(staticOptActivationExponentTextField.getText()).doubleValue());
      } catch (ParseException ex) {
         staticOptActivationExponentTextField.setText(numFormat.format(((AnalyzeToolModel)toolModel).getActivationExponent()));
      }
// TODO add your handling code here:
    }//GEN-LAST:event_staticOptActivationExponentTextFieldActionPerformed

    private void plotMetricsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plotMetricsCheckBoxActionPerformed
// TODO add your handling code here:
        cmcToolModel().setPlotMetrics(plotMetricsCheckBox.isSelected());
    }//GEN-LAST:event_plotMetricsCheckBoxActionPerformed
    private void reuseSelectedQuantitiesCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reuseSelectedQuantitiesCheckBoxActionPerformed
// TODO add your handling code here:
        cmcToolModel().setReuseSelectedMetrics(reuseSelectedQuantitiesCheckBox.isSelected());
    }//GEN-LAST:event_reuseSelectedQuantitiesCheckBoxActionPerformed

    private void cmcTimeWindowFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmcTimeWindowFocusLost
       if(!evt.isTemporary()) cmcTimeWindowActionPerformed(null);
// TODO add your handling code here:
    }//GEN-LAST:event_cmcTimeWindowFocusLost

    private void cmcTimeWindowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmcTimeWindowActionPerformed
// TODO add your handling code here:
      try {
         cmcToolModel().setCmcTimeWindow(numFormat.parse(cmcTimeWindow.getText()).doubleValue());
      } catch (ParseException ex) {
         cmcTimeWindow.setText(numFormat.format(cmcToolModel().getCmcTimeWindow()));
      }  
    }//GEN-LAST:event_cmcTimeWindowActionPerformed

    private void SelectCmcTargetCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SelectCmcTargetCheckBoxActionPerformed
// TODO add your handling code here:
        cmcToolModel().setUseFastTarget(!SelectCmcTargetCheckBox.isSelected());
    }//GEN-LAST:event_SelectCmcTargetCheckBoxActionPerformed

    private void editAnalyzeExcitationsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editAnalyzeExcitationsButtonActionPerformed
// TODO add your handling code here:
        editExcitationsFile(analyzeControlsFileName);
    }//GEN-LAST:event_editAnalyzeExcitationsButtonActionPerformed

    private void editForwardExcitationsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editForwardExcitationsButtonActionPerformed
// TODO add your handling code here:
        editExcitationsFile(controlsFileName);
    }//GEN-LAST:event_editForwardExcitationsButtonActionPerformed
    
    private void editExcitationsFile(FileTextFieldAndChooser aControlsFileName){
        if (!aControlsFileName.getFileIsValid()) return;
        String controlsFile=aControlsFileName.getFileName();
        if(controlsFile!=null) {
            ControlSet cs = null;
            if (controlsFile.endsWith(".sto")){
                try {
                    cs = new ControlSet(new Storage(controlsFile));
                } catch (IOException ex) {
                    ErrorDialog.displayExceptionDialog(ex);
                }
            }
            else {
                OpenSimObject objGeneric = OpenSimObject.makeObjectFromFile(controlsFile);
                if (objGeneric==null || !objGeneric.getConcreteClassName().equalsIgnoreCase("ControlSet")){
                    DialogDisplayer.getDefault().notify(
                            new NotifyDescriptor.Message("Could not construct excitations from the specified file."));
                    return;
                }
                cs = new ControlSet(controlsFile);
            }
            if (cs !=null){
                new ExcitationEditorJFrame(cs).setVisible(true);
            }
        }
    }
   //------------------------------------------------------------------------
   // Inverse tool input settings
   //------------------------------------------------------------------------
    
    private void motionsComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_motionsComboBox1ActionPerformed
      if(internalTrigger) return;
      int index = motionsComboBox1.getSelectedIndex();
      ArrayList<Storage> motions = MotionsDB.getInstance().getModelMotions(toolModel.getOriginalModel());
      if(motions!=null && 0<=index && index<motions.size()) analyzeToolModel().setInputMotion(motions.get(index));
      else analyzeToolModel().setInputMotion(null);
    }//GEN-LAST:event_motionsComboBox1ActionPerformed

    private void cutoffFrequency1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cutoffFrequency1FocusLost
      if(!evt.isTemporary()) cutoffFrequency1ActionPerformed(null);
    }//GEN-LAST:event_cutoffFrequency1FocusLost

    private void cutoffFrequency1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cutoffFrequency1ActionPerformed
      try {
         analyzeToolModel().setLowpassCutoffFrequency(numFormat.parse(cutoffFrequency1.getText()).doubleValue());
      } catch (ParseException ex) {
         cutoffFrequency1.setText(numFormat.format(analyzeToolModel().getLowpassCutoffFrequency()));
      }
    }//GEN-LAST:event_cutoffFrequency1ActionPerformed

    private void filterCoordinatesCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterCoordinatesCheckBox1ActionPerformed
      analyzeToolModel().setFilterCoordinates(filterCoordinatesCheckBox1.isSelected());
    }//GEN-LAST:event_filterCoordinatesCheckBox1ActionPerformed

    private void coordinatesFileName1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_coordinatesFileName1StateChanged
      analyzeToolModel().setCoordinatesFileName(coordinatesFileName1.getFileName());
    }//GEN-LAST:event_coordinatesFileName1StateChanged

    private void statesFileName1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_statesFileName1StateChanged
      analyzeToolModel().setStatesFileName(statesFileName1.getFileName());
    }//GEN-LAST:event_statesFileName1StateChanged

    private void inputSourceRadioButtonActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputSourceRadioButtonActionPerformed1
      if(statesRadioButton1.isSelected()) analyzeToolModel().setInputSource(AnalyzeToolModel.InputSource.States);
      else if(motionRadioButton1.isSelected()) {
         if(fromFileMotionRadioButton1.isSelected()) analyzeToolModel().setInputSource(AnalyzeToolModel.InputSource.Coordinates);
         else if(loadedMotionRadioButton1.isSelected()) analyzeToolModel().setInputSource(AnalyzeToolModel.InputSource.Motion);
         else analyzeToolModel().setInputSource(AnalyzeToolModel.InputSource.Unspecified);
      }
      else analyzeToolModel().setInputSource(AnalyzeToolModel.InputSource.Unspecified);
    }//GEN-LAST:event_inputSourceRadioButtonActionPerformed1

   //------------------------------------------------------------------------
   // RRA Settings Panel
   //------------------------------------------------------------------------

   private void rraAdjustedBodyComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rraAdjustedBodyComboBoxActionPerformed
      if(!internalTrigger) {
         if(rraAdjustedBodyComboBox.getSelectedItem()==null) rraToolModel().setAdjustedCOMBody("");
         else rraToolModel().setAdjustedCOMBody((String)rraAdjustedBodyComboBox.getSelectedItem());
      }
   }//GEN-LAST:event_rraAdjustedBodyComboBoxActionPerformed

   private void rraOutputModelFileNameStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_rraOutputModelFileNameStateChanged
       String fullFilename = FileUtils.addExtensionIfNeeded(rraOutputModelFileName.getFileName(), ".osim");
       rraToolModel().setOutputModelFileName(fullFilename);
   }//GEN-LAST:event_rraOutputModelFileNameStateChanged
    
   private void adjustModelCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_adjustModelCheckBoxActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_adjustModelCheckBoxActionPerformed

   //------------------------------------------------------------------------
   // Forward tool input settings
   //------------------------------------------------------------------------

   private void initialStatesFileNameStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_initialStatesFileNameStateChanged
      forwardToolModel().setInitialStatesFileName(initialStatesFileName.getFileName());
   }//GEN-LAST:event_initialStatesFileNameStateChanged

   private void controlsFileNameStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_controlsFileNameStateChanged
      forwardToolModel().setControlsFileName(controlsFileName.getFileName());
   }//GEN-LAST:event_controlsFileNameStateChanged

   private void solveForEquilibriumCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_solveForEquilibriumCheckBoxActionPerformed
      forwardToolModel().setSolveForEquilibrium(solveForEquilibriumCheckBox.isSelected());
   }//GEN-LAST:event_solveForEquilibriumCheckBoxActionPerformed

   //------------------------------------------------------------------------
   // Analyze tool input settings
   //------------------------------------------------------------------------

   private void analyzeControlsFileNameStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_analyzeControlsFileNameStateChanged
      analyzeToolModel().setControlsFileName(analyzeControlsFileName.getFileName());
   }//GEN-LAST:event_analyzeControlsFileNameStateChanged

   private void analyzeControlsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_analyzeControlsCheckBoxActionPerformed
      analyzeToolModel().setControlsEnabled(analyzeControlsCheckBox.isSelected());
   }//GEN-LAST:event_analyzeControlsCheckBoxActionPerformed

   private void inputSourceRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputSourceRadioButtonActionPerformed
      if(statesRadioButton.isSelected()) analyzeToolModel().setInputSource(AnalyzeToolModel.InputSource.States);
      else if(motionRadioButton.isSelected()) {
         if(fromFileMotionRadioButton.isSelected()) analyzeToolModel().setInputSource(AnalyzeToolModel.InputSource.Coordinates);
         else if(loadedMotionRadioButton.isSelected()) analyzeToolModel().setInputSource(AnalyzeToolModel.InputSource.Motion);
         else analyzeToolModel().setInputSource(AnalyzeToolModel.InputSource.Unspecified);
      }
      else analyzeToolModel().setInputSource(AnalyzeToolModel.InputSource.Unspecified);
   }//GEN-LAST:event_inputSourceRadioButtonActionPerformed

   private void motionsComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_motionsComboBoxActionPerformed
      if(internalTrigger) return;
      int index = motionsComboBox.getSelectedIndex();
      ArrayList<Storage> motions = MotionsDB.getInstance().getModelMotions(toolModel.getOriginalModel());
      if(motions!=null && 0<=index && index<motions.size()) analyzeToolModel().setInputMotion(motions.get(index));
      else analyzeToolModel().setInputMotion(null);
   }//GEN-LAST:event_motionsComboBoxActionPerformed

   private void coordinatesFileNameStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_coordinatesFileNameStateChanged
      analyzeToolModel().setCoordinatesFileName(coordinatesFileName.getFileName());
   }//GEN-LAST:event_coordinatesFileNameStateChanged

   private void statesFileNameStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_statesFileNameStateChanged
      analyzeToolModel().setStatesFileName(statesFileName.getFileName());
   }//GEN-LAST:event_statesFileNameStateChanged

   private void filterCoordinatesCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterCoordinatesCheckBoxActionPerformed
      analyzeToolModel().setFilterCoordinates(filterCoordinatesCheckBox.isSelected());
   }//GEN-LAST:event_filterCoordinatesCheckBoxActionPerformed

   private void cutoffFrequencyFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cutoffFrequencyFocusLost
      if(!evt.isTemporary()) cutoffFrequencyActionPerformed(null);
   }//GEN-LAST:event_cutoffFrequencyFocusLost

   private void cutoffFrequencyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cutoffFrequencyActionPerformed
      try {
         analyzeToolModel().setLowpassCutoffFrequency(numFormat.parse(cutoffFrequency.getText()).doubleValue());
      } catch (ParseException ex) {
         cutoffFrequency.setText(numFormat.format(analyzeToolModel().getLowpassCutoffFrequency()));
      }
   }//GEN-LAST:event_cutoffFrequencyActionPerformed

   private void analyzeSolveForEquilibriumCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_analyzeSolveForEquilibriumCheckBoxActionPerformed
      analyzeToolModel().setSolveForEquilibrium(analyzeSolveForEquilibriumCheckBox.isSelected());
   }//GEN-LAST:event_analyzeSolveForEquilibriumCheckBoxActionPerformed

   //------------------------------------------------------------------------
   // Analyze section
   //------------------------------------------------------------------------

   private void editAnalysesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editAnalysesButtonActionPerformed
      jTabbedPane1.setSelectedComponent(analysisSetPanel);
   }//GEN-LAST:event_editAnalysesButtonActionPerformed

   private void initialTimeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_initialTimeFocusLost
      if(!evt.isTemporary()) initialTimeActionPerformed(null);
   }//GEN-LAST:event_initialTimeFocusLost

   private void initialTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_initialTimeActionPerformed
      try {
         toolModel.setInitialTime(numFormat.parse(initialTime.getText()).doubleValue());
      } catch (ParseException ex) {
         initialTime.setText(numFormat.format(toolModel.getInitialTime()));
      }
   }//GEN-LAST:event_initialTimeActionPerformed

   private void finalTimeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_finalTimeFocusLost
      if(!evt.isTemporary()) finalTimeActionPerformed(null);
   }//GEN-LAST:event_finalTimeFocusLost

   private void finalTimeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_finalTimeActionPerformed
      try {
         toolModel.setFinalTime(numFormat.parse(finalTime.getText()).doubleValue());
      } catch (ParseException ex) {
         finalTime.setText(numFormat.format(toolModel.getFinalTime()));
      }
   }//GEN-LAST:event_finalTimeActionPerformed

   private void setCmcTimeWindow(java.awt.event.ActionEvent evt) {                                            
      try {
         ((CMCToolModel)toolModel).setCmcTimeWindow(numFormat.parse(cmcTimeWindow.getText()).doubleValue());
      } catch (ParseException ex) {
         cmcTimeWindow.setText(numFormat.format(((CMCToolModel)toolModel).getCmcTimeWindow()));
      }
   }                                           

   //------------------------------------------------------------------------
   // Output settings
   //------------------------------------------------------------------------

   private void outputDirectoryStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_outputDirectoryStateChanged
      toolModel.setResultsDirectory(outputDirectory.getFileName());
   }//GEN-LAST:event_outputDirectoryStateChanged

   private void outputPrecisionFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_outputPrecisionFocusLost
      if(!evt.isTemporary()) outputPrecisionActionPerformed(null);
   }//GEN-LAST:event_outputPrecisionFocusLost

   private void outputPrecisionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outputPrecisionActionPerformed
      try {
         toolModel.setOutputPrecision(numFormat.parse(outputPrecision.getText()).intValue());
      } catch (ParseException ex) {
         outputPrecision.setText(numFormat.format(toolModel.getOutputPrecision()));
      }
   }//GEN-LAST:event_outputPrecisionActionPerformed

   private void outputNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_outputNameFocusLost
      if(!evt.isTemporary()) outputNameActionPerformed(null);
   }//GEN-LAST:event_outputNameFocusLost

   private void outputNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outputNameActionPerformed
      toolModel.setOutputPrefix(outputName.getText());
   }//GEN-LAST:event_outputNameActionPerformed

private void StepIntervalSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_StepIntervalSpinnerStateChanged
// TODO add your handling code here:
          SpinnerModel stepModel = StepIntervalSpinner.getModel();
          int newStepInterval = (Integer) stepModel.getValue();
          //System.out.println("New step = "+newStepInterval);
         ((AnalyzeToolModel)toolModel).setAnalysisStepInterval(newStepInterval);
    
}//GEN-LAST:event_StepIntervalSpinnerStateChanged

private void StepIntervalSpinnerFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_StepIntervalSpinnerFocusLost
// TODO add your handling code here:
    StepIntervalSpinnerStateChanged(null);
}//GEN-LAST:event_StepIntervalSpinnerFocusLost

   //------------------------------------------------------------------------
   // CMC Input Panel
   //------------------------------------------------------------------------

    private void cmcConstraintsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmcConstraintsCheckBoxActionPerformed
        ((TrackingToolModel)toolModel).setConstraintsEnabled(cmcConstraintsCheckBox.isSelected());
    }//GEN-LAST:event_cmcConstraintsCheckBoxActionPerformed

    private void cmcConstraintsFileNameStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cmcConstraintsFileNameStateChanged
        ((TrackingToolModel)toolModel).setConstraintsFileName(cmcConstraintsFileName.getFileName());
    }//GEN-LAST:event_cmcConstraintsFileNameStateChanged

    private void cmcTaskSetFileNameStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cmcTaskSetFileNameStateChanged
        ((TrackingToolModel)toolModel).setTaskSetFileName(cmcTaskSetFileName.getFileName());
    }//GEN-LAST:event_cmcTaskSetFileNameStateChanged

    private void cmcCutoffFrequencyFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmcCutoffFrequencyFocusLost
        if(!evt.isTemporary()) cmcCutoffFrequencyActionPerformed(null);
    }//GEN-LAST:event_cmcCutoffFrequencyFocusLost

    private void cmcCutoffFrequencyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmcCutoffFrequencyActionPerformed
        try {
            ((TrackingToolModel)toolModel).setLowpassCutoffFrequency(numFormat.parse(cmcCutoffFrequency.getText()).doubleValue());
        } catch (ParseException ex) {
            cmcCutoffFrequency.setText(numFormat.format( ((TrackingToolModel)toolModel).getLowpassCutoffFrequency()));
        }
    }//GEN-LAST:event_cmcCutoffFrequencyActionPerformed

    private void cmcFilterKinematicsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmcFilterKinematicsCheckBoxActionPerformed
        ((TrackingToolModel)toolModel).setFilterKinematics(cmcFilterKinematicsCheckBox.isSelected());
    }//GEN-LAST:event_cmcFilterKinematicsCheckBoxActionPerformed

    private void cmcDesiredKinematicsFileNameStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cmcDesiredKinematicsFileNameStateChanged
        ((TrackingToolModel)toolModel).setDesiredKinematicsFileName(cmcDesiredKinematicsFileName.getFileName());
    }//GEN-LAST:event_cmcDesiredKinematicsFileNameStateChanged

    private void minDTFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_minDTFocusLost
        if(!evt.isTemporary()) minDTActionPerformed(null);
        // TODO add your handling code here:
    }//GEN-LAST:event_minDTFocusLost

    private void minDTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minDTActionPerformed
        try {
            toolModel.setMinDT(numFormat.parse(minDT.getText()).doubleValue());
        } catch (ParseException ex) {
            minDT.setText(numFormat.format(toolModel.getMinDT()));
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_minDTActionPerformed

    private void errorToleranceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_errorToleranceFocusLost
        if(!evt.isTemporary()) errorToleranceActionPerformed(null);
    }//GEN-LAST:event_errorToleranceFocusLost

    private void errorToleranceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_errorToleranceActionPerformed
        try {
            toolModel.setErrorTolerance(numFormat.parse(errorTolerance.getText()).doubleValue());
        } catch (ParseException ex) {
            errorTolerance.setText(numFormat.format(toolModel.getErrorTolerance()));
        }
    }//GEN-LAST:event_errorToleranceActionPerformed

   //------------------------------------------------------------------------
   // Integrator settings (forward tool)
   //------------------------------------------------------------------------

    private void maxDTFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_maxDTFocusLost
        if(!evt.isTemporary()) maxDTActionPerformed(null);
    }//GEN-LAST:event_maxDTFocusLost

    private void maxDTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maxDTActionPerformed
        try {
            toolModel.setMaxDT(numFormat.parse(maxDT.getText()).doubleValue());
        } catch (ParseException ex) {
            maxDT.setText(numFormat.format(toolModel.getMaxDT()));
        }
    }//GEN-LAST:event_maxDTActionPerformed

    private void maximumNumberOfStepsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_maximumNumberOfStepsFocusLost
        if(!evt.isTemporary()) maximumNumberOfStepsActionPerformed(null);
    }//GEN-LAST:event_maximumNumberOfStepsFocusLost

    private void maximumNumberOfStepsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maximumNumberOfStepsActionPerformed
        try {
            toolModel.setMaximumNumberOfSteps(numFormat.parse(maximumNumberOfSteps.getText()).intValue());
        } catch (ParseException ex) {
            maximumNumberOfSteps.setText(numFormat.format(toolModel.getMaximumNumberOfSteps()));
        }
    }//GEN-LAST:event_maximumNumberOfStepsActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel HzJLabel;
    private javax.swing.JCheckBox SelectCmcTargetCheckBox;
    private javax.swing.JSpinner StepIntervalSpinner;
    private javax.swing.JTextField activeAnalyses;
    private javax.swing.JPanel activeAnalysesPanel;
    private javax.swing.JCheckBox adjustModelCheckBox;
    private javax.swing.JPanel advancedSettingsPanel;
    private javax.swing.JCheckBox analyzeControlsCheckBox;
    private org.opensim.swingui.FileTextFieldAndChooser analyzeControlsFileName;
    private javax.swing.JPanel analyzeInputPanel;
    private javax.swing.JCheckBox analyzeSolveForEquilibriumCheckBox;
    private javax.swing.JTextField availableFinalTime;
    private javax.swing.JTextField availableInitialTime;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.ButtonGroup buttonGroup4;
    private javax.swing.JCheckBox cmcConstraintsCheckBox;
    private org.opensim.swingui.FileTextFieldAndChooser cmcConstraintsFileName;
    private javax.swing.JTextField cmcCutoffFrequency;
    private org.opensim.swingui.FileTextFieldAndChooser cmcDesiredKinematicsFileName;
    private javax.swing.JCheckBox cmcFilterKinematicsCheckBox;
    private javax.swing.JPanel cmcInputPanel;
    private org.opensim.swingui.FileTextFieldAndChooser cmcTaskSetFileName;
    private javax.swing.JTextField cmcTimeWindow;
    private org.opensim.swingui.FileTextFieldAndChooser controlsFileName;
    private org.opensim.swingui.FileTextFieldAndChooser coordinatesFileName;
    private org.opensim.swingui.FileTextFieldAndChooser coordinatesFileName1;
    private javax.swing.JTextField cutoffFrequency;
    private javax.swing.JTextField cutoffFrequency1;
    private javax.swing.JButton editAnalysesButton;
    private javax.swing.JButton editAnalyzeExcitationsButton;
    private javax.swing.JButton editForwardExcitationsButton;
    private javax.swing.JTextField errorTolerance;
    private javax.swing.JCheckBox filterCoordinatesCheckBox;
    private javax.swing.JCheckBox filterCoordinatesCheckBox1;
    private javax.swing.JTextField finalTime;
    private javax.swing.JPanel forwardInputPanel;
    private javax.swing.JRadioButton fromFileMotionRadioButton;
    private javax.swing.JRadioButton fromFileMotionRadioButton1;
    private org.opensim.swingui.FileTextFieldAndChooser initialStatesFileName;
    private javax.swing.JTextField initialTime;
    private javax.swing.JPanel integratorSettingsPanel;
    private javax.swing.JPanel inverseInputPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JRadioButton loadedMotionRadioButton;
    private javax.swing.JRadioButton loadedMotionRadioButton1;
    private javax.swing.JPanel mainSettingsPanel;
    private javax.swing.JTextField maxDT;
    private javax.swing.JTextField maximumNumberOfSteps;
    private javax.swing.JTextField minDT;
    private javax.swing.JPanel modelInfoPanel;
    private javax.swing.JTextField modelName;
    private javax.swing.JRadioButton motionRadioButton;
    private javax.swing.JRadioButton motionRadioButton1;
    private javax.swing.JComboBox motionsComboBox;
    private javax.swing.JComboBox motionsComboBox1;
    private org.opensim.swingui.FileTextFieldAndChooser outputDirectory;
    private javax.swing.JTextField outputName;
    private javax.swing.JPanel outputPanel;
    private javax.swing.JTextField outputPrecision;
    private javax.swing.JCheckBox plotMetricsCheckBox;
    private javax.swing.JPanel plotMetricsPanel;
    private javax.swing.JCheckBox reuseSelectedQuantitiesCheckBox;
    private javax.swing.JComboBox rraAdjustedBodyComboBox;
    private org.opensim.swingui.FileTextFieldAndChooser rraOutputModelFileName;
    private javax.swing.JPanel rraPanel;
    private javax.swing.JCheckBox solveForEquilibriumCheckBox;
    private org.opensim.swingui.FileTextFieldAndChooser statesFileName;
    private org.opensim.swingui.FileTextFieldAndChooser statesFileName1;
    private javax.swing.JRadioButton statesRadioButton;
    private javax.swing.JRadioButton statesRadioButton1;
    private javax.swing.JTextField staticOptActivationExponentTextField;
    private javax.swing.JPanel staticOptimizationPanel;
    private javax.swing.JPanel timePanel;
    private javax.swing.JRadioButton unspecifiedRadioButton;
    private javax.swing.JCheckBox useForceLengthStaticOptCheckBox;
    // End of variables declaration//GEN-END:variables
   // Relinquish C++ resources by setting references to them to null
   public void cleanup()
   {
      
   }
}
