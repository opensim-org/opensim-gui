/* -------------------------------------------------------------------------- *
 * OpenSim: InverseDynamicsToolPanel.java                                     *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib, Kevin Xu                                           *
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
 * InverseDynamicsToolPanel.java
 *
 * Created on Feb 24, 2011, 7:37 PM
 */

package org.opensim.tracking;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JPanel;
import org.jdesktop.layout.GroupLayout;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.opensim.modeling.ControlSet;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.Storage;
import org.opensim.view.motions.MotionsDB;
import org.opensim.swingui.FileTextFieldAndChooser;
import org.opensim.tracking.InverseDynamicsToolModel.InputSource;
import org.opensim.utils.BrowserLauncher;
import org.opensim.utils.ErrorDialog;
import org.opensim.utils.TheApp;
import org.opensim.view.ModelEvent;
import org.opensim.view.excitationEditor.ExcitationEditorJFrame;
import org.opensim.view.pub.OpenSimDB;

/**
 *
 * @author  erang
 */
public class InverseDynamicsToolPanel extends BaseToolPanel implements Observer {

   InverseDynamicsToolModel toolModel = null;
   ActuatorsAndExternalLoadsPanel actuatorsAndExternalLoadsPanel = null;
   String modeName = "Inverse Dynamics";

   private boolean internalTrigger = false;
   private NumberFormat numFormat = NumberFormat.getInstance();

   /** Creates new form AnalyzeAndForwardToolPanel */
   public InverseDynamicsToolPanel(Model model) throws IOException {
      toolModel = new InverseDynamicsToolModel(model);
      if (numFormat instanceof DecimalFormat) {
        ((DecimalFormat) numFormat).applyPattern("#,##0.############");
      }

      initComponents();

      helpButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                String path = BrowserLauncher.isConnected() ? "http://simtk-confluence.stanford.edu/display/OpenSim40/Inverse+Dynamics" : TheApp.getUsersGuideDir() + "Inverse+Dynamics.html"; 
                BrowserLauncher.openURL(path);
            }
      });
      bindPropertiesToComponents();

      // Add checkbox titled borders to RRA panel
      //rraPanelCheckBox.setForeground(new Color(0,70,213));
      //rraPanel.setBorder(new ComponentTitledBorder(rraPanelCheckBox, rraPanel, BorderFactory.createEtchedBorder()));

      // File chooser settings
      outputDirectory.setIncludeOpenButton(true);
      outputDirectory.setDirectoriesOnly(true);
      outputDirectory.setCheckIfFileExists(false);

      setSettingsFileDescription("Settings file for "+modeName);

      // disable for now
      plotMetricsPanel.setVisible(false); // Show plots only for RRA, CMC for now as the more time consuming steps


      // Set file filters for inverse dynamics tool inputs
      //statesFileName1.setExtensionsAndDescription(".sto", "States data for "+modeName);
      coordinatesFileName1.setExtensionsAndDescription(".mot,.sto", "Motion data for "+modeName);

      // Actuators & External Loads tab
      actuatorsAndExternalLoadsPanel = new ActuatorsAndExternalLoadsPanel(toolModel, toolModel.getOriginalModel(),
              false);
      jTabbedPane1.addTab( "External Loads", actuatorsAndExternalLoadsPanel);
      // Re-layout panels after we've removed various parts...
      ((GroupLayout)mainSettingsPanel.getLayout()).layoutContainer(mainSettingsPanel);

      updateStaticFields();
      updateFromModel();

      toolModel.addObserver(this);
   }

   private void bindPropertiesToComponents() {
//          ToolCommon.bindProperty(toolModel.getTool(), "states_file", statesFileName);
      InverseDynamicsToolModel idModel = (InverseDynamicsToolModel)toolModel;

      ToolCommon.bindProperty(idModel.InverseDynamicsTool(), "coordinates_file", motionsComboBox1);
      ToolCommon.bindProperty(idModel.InverseDynamicsTool(), "coordinates_file", coordinatesFileName1);
      ToolCommon.bindProperty(idModel.InverseDynamicsTool(), "lowpass_cutoff_frequency_for_coordinates", cutoffFrequency1);
      ToolCommon.bindProperty(idModel.InverseDynamicsTool(), "time_range", initialTime);
      ToolCommon.bindProperty(idModel.InverseDynamicsTool(), "time_range", finalTime);
      ToolCommon.bindProperty(idModel.InverseDynamicsTool(), "results_directory", outputDirectory);
    }

   public void update(Observable observable, Object obj) {
      if (observable instanceof OpenSimDB){
           if (obj instanceof ModelEvent) {
                if (OpenSimDB.getInstance().hasModel(toolModel.getOriginalModel()))
                    return;
                else {
                    toolModel.deleteObserver(this);
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
      //modelName.setText(toolModel.getOriginalModel().getName());
   }

   public void updateFromModel() {
      internalTrigger = true;

      // Start off with everything enabled
      setEnabled(mainSettingsPanel, true);
      updateInverseToolSpecificFields(toolModel);
      // Time
      double initTime = toolModel.getInitialTime();
      initialTime.setText(numFormat.format(initTime));
      double finTime = toolModel.getFinalTime();
      finalTime.setText(numFormat.format(finTime));

      // Output
      outputName.setText(toolModel.getOutputPrefix());
      outputDirectory.setFileName(toolModel.getResultsDirectory(),false);
      //outputPrecision.setText(numFormat.format(toolModel.getOutputPrecision()));

      // Actuators & external loads
      actuatorsAndExternalLoadsPanel.updatePanel();

      updateDialogButtons();

      internalTrigger = false;
   }

   public void updateDialogButtons() {
      updateApplyButton(!toolModel.isExecuting() && toolModel.isModified() && toolModel.isValidated());
   }

   //---------------------------------------------------------------------
   // Fields for inverse dynamics tool
   //---------------------------------------------------------------------
   public void updateInverseToolSpecificFields(InverseDynamicsToolModel toolModel) {

      /*if(toolModel.getInputSource()==InverseDynamicsToolModel.InputSource.States) buttonGroup3.setSelected(statesRadioButton1.getModel(),true);
      else */if(toolModel.getInputSource()==InverseDynamicsToolModel.InputSource.Coordinates) {
         buttonGroup3.setSelected(motionRadioButton1.getModel(),true);
         buttonGroup4.setSelected(fromFileMotionRadioButton1.getModel(),true);

      } else if(toolModel.getInputSource()==InverseDynamicsToolModel.InputSource.Motion) {
         buttonGroup3.setSelected(motionRadioButton1.getModel(),true);
         buttonGroup4.setSelected(loadedMotionRadioButton1.getModel(),true);
      } else {
         buttonGroup3.setSelected(unspecifiedRadioButton.getModel(),true);
      }

     // Motion selections
      ArrayList<Storage> motions = MotionsDB.getInstance().getModelMotions(toolModel.getOriginalModel());
      motionsComboBox1.removeAllItems();
      if(motions!=null) for(int i=0; i<motions.size(); i++) motionsComboBox1.addItem(motions.get(i).getName());

      if(motions==null || motions.size()==0) loadedMotionRadioButton1.setEnabled(false);

      else if(toolModel.getInputMotion()==null) motionsComboBox1.setSelectedIndex(-1);
      else motionsComboBox1.setSelectedIndex(motions.indexOf(toolModel.getInputMotion()));

      //if(!buttonGroup3.isSelected(statesRadioButton1.getModel())) statesFileName1.setEnabled(false);
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

      if(!buttonGroup3.isSelected(motionRadioButton1.getModel()) ) {
          motionRadioButton1.setSelected(true);
          fromFileMotionRadioButton1.setEnabled(true);
          coordinatesFileName1.setEnabled(true);
          filterCoordinatesCheckBox1.setEnabled(true);
      }

      //statesFileName1.setFileName(toolModel.getStatesFileName(),false);
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

        outputName = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        buttonGroup1 = new javax.swing.ButtonGroup();
        unspecifiedRadioButton = new javax.swing.JRadioButton();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        buttonGroup4 = new javax.swing.ButtonGroup();
        HzJLabel = new javax.swing.JLabel();
        plotMetricsPanel = new javax.swing.JPanel();
        plotMetricsCheckBox = new javax.swing.JCheckBox();
        reuseSelectedQuantitiesCheckBox = new javax.swing.JCheckBox();
        motionRadioButton1 = new javax.swing.JRadioButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        mainSettingsPanel = new javax.swing.JPanel();
        outputPanel = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        outputDirectory = new org.opensim.swingui.FileTextFieldAndChooser();
        timePanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        initialTime = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        finalTime = new javax.swing.JTextField();
        inverseInputPanel = new javax.swing.JPanel();
        motionsComboBox1 = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        coordinatesFileName1 = new org.opensim.swingui.FileTextFieldAndChooser();
        fromFileMotionRadioButton1 = new javax.swing.JRadioButton();
        loadedMotionRadioButton1 = new javax.swing.JRadioButton();
        filterCoordinatesCheckBox1 = new javax.swing.JCheckBox();
        cutoffFrequency1 = new javax.swing.JTextField();

        outputName.setText("jTextField1");
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

        jLabel6.setText("Prefix");

        buttonGroup1.add(unspecifiedRadioButton);
        unspecifiedRadioButton.setText("jRadioButton1");
        unspecifiedRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        unspecifiedRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        HzJLabel.setText("Hz");

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
                .addContainerGap(38, Short.MAX_VALUE))
        );
        plotMetricsPanelLayout.setVerticalGroup(
            plotMetricsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(plotMetricsPanelLayout.createSequentialGroup()
                .add(plotMetricsCheckBox)
                .add(14, 14, 14)
                .add(reuseSelectedQuantitiesCheckBox))
        );

        buttonGroup3.add(motionRadioButton1);
        motionRadioButton1.setSelected(true);
        motionRadioButton1.setText("Motion");
        motionRadioButton1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        motionRadioButton1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        motionRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputSourceRadioButtonActionPerformed1(evt);
            }
        });

        outputPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Output"));

        jLabel11.setText("Directory");

        outputDirectory.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                outputDirectoryStateChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout outputPanelLayout = new org.jdesktop.layout.GroupLayout(outputPanel);
        outputPanel.setLayout(outputPanelLayout);
        outputPanelLayout.setHorizontalGroup(
            outputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(outputPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel11)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(outputDirectory, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE)
                .addContainerGap())
        );
        outputPanelLayout.setVerticalGroup(
            outputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(outputPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(outputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabel11)
                    .add(outputDirectory, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
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

        org.jdesktop.layout.GroupLayout timePanelLayout = new org.jdesktop.layout.GroupLayout(timePanel);
        timePanel.setLayout(timePanelLayout);
        timePanelLayout.setHorizontalGroup(
            timePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(timePanelLayout.createSequentialGroup()
                .add(71, 71, 71)
                .add(jLabel4)
                .add(15, 15, 15)
                .add(initialTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 64, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel5)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(finalTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 64, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(84, Short.MAX_VALUE))
        );

        timePanelLayout.linkSize(new java.awt.Component[] {finalTime, initialTime}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        timePanelLayout.setVerticalGroup(
            timePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(timePanelLayout.createSequentialGroup()
                .add(timePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(initialTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel5)
                    .add(finalTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        inverseInputPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Input"));

        motionsComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                motionsComboBox1ActionPerformed(evt);
            }
        });

        jLabel1.setText("Hz");

        coordinatesFileName1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                coordinatesFileName1StateChanged(evt);
            }
        });

        buttonGroup4.add(fromFileMotionRadioButton1);
        fromFileMotionRadioButton1.setText("From file");
        fromFileMotionRadioButton1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        fromFileMotionRadioButton1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        fromFileMotionRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputSourceRadioButtonActionPerformed1(evt);
            }
        });

        buttonGroup4.add(loadedMotionRadioButton1);
        loadedMotionRadioButton1.setSelected(true);
        loadedMotionRadioButton1.setText("Loaded motion");
        loadedMotionRadioButton1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        loadedMotionRadioButton1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        loadedMotionRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputSourceRadioButtonActionPerformed1(evt);
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

        org.jdesktop.layout.GroupLayout inverseInputPanelLayout = new org.jdesktop.layout.GroupLayout(inverseInputPanel);
        inverseInputPanel.setLayout(inverseInputPanelLayout);
        inverseInputPanelLayout.setHorizontalGroup(
            inverseInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(inverseInputPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(inverseInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(inverseInputPanelLayout.createSequentialGroup()
                        .add(inverseInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(fromFileMotionRadioButton1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 82, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(loadedMotionRadioButton1))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(inverseInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, motionsComboBox1, 0, 285, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, coordinatesFileName1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE)))
                    .add(inverseInputPanelLayout.createSequentialGroup()
                        .add(filterCoordinatesCheckBox1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cutoffFrequency1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 32, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel1)))
                .add(34, 34, 34))
        );
        inverseInputPanelLayout.setVerticalGroup(
            inverseInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(inverseInputPanelLayout.createSequentialGroup()
                .add(inverseInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(fromFileMotionRadioButton1)
                    .add(coordinatesFileName1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(inverseInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(motionsComboBox1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(loadedMotionRadioButton1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(inverseInputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(filterCoordinatesCheckBox1)
                    .add(cutoffFrequency1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel1)))
        );

        org.jdesktop.layout.GroupLayout mainSettingsPanelLayout = new org.jdesktop.layout.GroupLayout(mainSettingsPanel);
        mainSettingsPanel.setLayout(mainSettingsPanelLayout);
        mainSettingsPanelLayout.setHorizontalGroup(
            mainSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, mainSettingsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(mainSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, outputPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(inverseInputPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, timePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        mainSettingsPanelLayout.setVerticalGroup(
            mainSettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainSettingsPanelLayout.createSequentialGroup()
                .add(inverseInputPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(timePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addContainerGap()
                .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

private void plotMetricsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plotMetricsCheckBoxActionPerformed
// TODO add your handling code here:
    //cmcToolModel().setPlotMetrics(plotMetricsCheckBox.isSelected());
}//GEN-LAST:event_plotMetricsCheckBoxActionPerformed

private void reuseSelectedQuantitiesCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reuseSelectedQuantitiesCheckBoxActionPerformed
// TODO add your handling code here:
    //cmcToolModel().setReuseSelectedMetrics(reuseSelectedQuantitiesCheckBox.isSelected());
}//GEN-LAST:event_reuseSelectedQuantitiesCheckBoxActionPerformed

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
      if(motions!=null && 0<=index && index<motions.size()) toolModel.setInputMotion(motions.get(index));
      else toolModel.setInputMotion(null);
    }//GEN-LAST:event_motionsComboBox1ActionPerformed

    private void cutoffFrequency1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cutoffFrequency1FocusLost
      if(!evt.isTemporary()) cutoffFrequency1ActionPerformed(null);
    }//GEN-LAST:event_cutoffFrequency1FocusLost

    private void cutoffFrequency1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cutoffFrequency1ActionPerformed
      try {
         toolModel.setLowpassCutoffFrequency(numFormat.parse(cutoffFrequency1.getText()).doubleValue());
      } catch (ParseException ex) {
         cutoffFrequency1.setText(numFormat.format(toolModel.getLowpassCutoffFrequency()));
      }
    }//GEN-LAST:event_cutoffFrequency1ActionPerformed

    private void filterCoordinatesCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterCoordinatesCheckBox1ActionPerformed
      boolean selected=filterCoordinatesCheckBox1.isSelected();
       toolModel.setFilterCoordinates(selected);
       cutoffFrequency1.setEnabled(selected);
       if (selected)
           cutoffFrequency1.setText("6");
       else
           cutoffFrequency1.setText("-1.");
    }//GEN-LAST:event_filterCoordinatesCheckBox1ActionPerformed

    private void coordinatesFileName1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_coordinatesFileName1StateChanged
       String fileName = coordinatesFileName1.getFileName();
       boolean invalidFile=(fileName==null || fileName.equals("") || fileName.equalsIgnoreCase("Unassigned"));
       if(!invalidFile) {
      toolModel.setCoordinatesFileName(coordinatesFileName1.getFileName());
             File f = new File(coordinatesFileName1.getFileName());
             if (f.exists()){
                try {
                    Storage st = new Storage(coordinatesFileName1.getFileName());
                    toolModel.setInitialTime(st.getFirstTime());
                    toolModel.setFinalTime(st.getLastTime());
                } catch (IOException ex) {
                    ErrorDialog.displayExceptionDialog(ex);
                }
             }
      }
    }//GEN-LAST:event_coordinatesFileName1StateChanged

    private void inputSourceRadioButtonActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputSourceRadioButtonActionPerformed1
    /*if(statesRadioButton1.isSelected()) toolModel.setInputSource(InverseDynamicsToolModel.InputSource.States);
      else*/
        if(motionRadioButton1.isSelected()) {
         if(fromFileMotionRadioButton1.isSelected()) toolModel.setInputSource(InputSource.Coordinates);
         else if(loadedMotionRadioButton1.isSelected()) toolModel.setInputSource(InputSource.Motion);
         else toolModel.setInputSource(InputSource.Unspecified);
      }
      else toolModel.setInputSource(InputSource.Unspecified);
    }//GEN-LAST:event_inputSourceRadioButtonActionPerformed1

   //------------------------------------------------------------------------
   // Analyze section
   //------------------------------------------------------------------------

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

   //------------------------------------------------------------------------
   // Output settings
   //------------------------------------------------------------------------

   private void outputDirectoryStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_outputDirectoryStateChanged
      toolModel.setResultsDirectory(outputDirectory.getFileName());
   }//GEN-LAST:event_outputDirectoryStateChanged

   private void outputNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_outputNameFocusLost
      if(!evt.isTemporary()) outputNameActionPerformed(null);
   }//GEN-LAST:event_outputNameFocusLost

   private void outputNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outputNameActionPerformed
      toolModel.setOutputPrefix(outputName.getText());
   }//GEN-LAST:event_outputNameActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel HzJLabel;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.ButtonGroup buttonGroup4;
    private org.opensim.swingui.FileTextFieldAndChooser coordinatesFileName1;
    private javax.swing.JTextField cutoffFrequency1;
    private javax.swing.JCheckBox filterCoordinatesCheckBox1;
    private javax.swing.JTextField finalTime;
    private javax.swing.JRadioButton fromFileMotionRadioButton1;
    private javax.swing.JTextField initialTime;
    private javax.swing.JPanel inverseInputPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JRadioButton loadedMotionRadioButton1;
    private javax.swing.JPanel mainSettingsPanel;
    private javax.swing.JRadioButton motionRadioButton1;
    private javax.swing.JComboBox motionsComboBox1;
    private org.opensim.swingui.FileTextFieldAndChooser outputDirectory;
    private javax.swing.JTextField outputName;
    private javax.swing.JPanel outputPanel;
    private javax.swing.JCheckBox plotMetricsCheckBox;
    private javax.swing.JPanel plotMetricsPanel;
    private javax.swing.JCheckBox reuseSelectedQuantitiesCheckBox;
    private javax.swing.JPanel timePanel;
    private javax.swing.JRadioButton unspecifiedRadioButton;
    // End of variables declaration//GEN-END:variables
   // Relinquish C++ resources by setting references to them to null
   public void cleanup()
   {

   }
}
