/* -------------------------------------------------------------------------- *
 * OpenSim: ScaleToolPanel.java                                               *
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
 * ScaleToolPanel.java
 *
 * Created on July 3, 2007, 4:51 PM
 */

package org.opensim.tracking;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Observable;
import java.util.Observer;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.opensim.modeling.MarkerSet;
import org.opensim.modeling.Model;
import org.opensim.swingui.ComponentTitledBorder;
import org.opensim.utils.BrowserLauncher;
import org.opensim.utils.TheApp;
import org.opensim.view.ModelEvent;
import org.opensim.view.pub.OpenSimDB;

public class ScaleToolPanel extends BaseToolPanel implements Observer {
  
   private ScaleToolModel scaleToolModel = null;

   private JCheckBox modelScalerPanelCheckBox = new JCheckBox(new EnableModelScalerAction());
   private JCheckBox markerPlacerPanelCheckBox = new JCheckBox(new EnableMarkerPlacerAction());
   private MeasurementSetPanel measurementSetPanel;
   private Dialog measurementSetDialog;
   private NumberFormat numFormat = NumberFormat.getInstance();

   class EnableModelScalerAction extends AbstractAction {
      public EnableModelScalerAction() { super("Scale Model"); }
      public void actionPerformed(ActionEvent evt) { scaleToolModel.setModelScalerEnabled(((JCheckBox)evt.getSource()).isSelected()); }
   }

   class EnableMarkerPlacerAction extends AbstractAction {
      public EnableMarkerPlacerAction() { super("Adjust Model Markers"); }
      public void actionPerformed(ActionEvent evt) { 
         scaleToolModel.setMarkerPlacerEnabled(((JCheckBox)evt.getSource()).isSelected()); 
      }
   }

   /** Creates new form ScaleToolPanel */
   public ScaleToolPanel(Model model) throws IOException {
      if(model==null) throw new IOException("ScaleToolPanel got null model");

      scaleToolModel = new ScaleToolModel(model);
      //scaleToolModel.loadSettings("C:\\eran\\dev\\simbios\\opensim\\Trunk\\OpenSim\\Examples\\Gait2354\\subject01_Setup_Scale.xml");

      if (numFormat instanceof DecimalFormat) {
        ((DecimalFormat) numFormat).applyPattern("#,##0.#########");
      }

      
      helpButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                BrowserLauncher.openURL("http://simtk-confluence.stanford.edu/display/OpenSim40/Scaling");
            }
      });
      
      initComponents();
      bindPropertiesToComponents();

      setSettingsFileDescription("Scale tool settings file");

      //---------------------------------------------------------------------
      // Measurement set editor dialog
      //---------------------------------------------------------------------
      measurementSetPanel = new MeasurementSetPanel(scaleToolModel);
      DialogDescriptor dlg = new DialogDescriptor(measurementSetPanel, "Measurement Set");
      dlg.setModal(false);
      dlg.setOptions(new Object[]{DialogDescriptor.OK_OPTION});
      measurementSetDialog = DialogDisplayer.getDefault().createDialog(dlg);

      jTabbedPane.addTab("Scale Factors", new ScaleFactorsPanel(scaleToolModel, measurementSetDialog));
      jTabbedPane.addTab("Static Pose Weights", new IKTaskSetPanel(scaleToolModel.getIKCommonModel()));      
      
      markerSetFileName.setExtensionsAndDescription(".xml", "MarkerSet XML file");
      measurementTrialFileName.setExtensionsAndDescription(".trc", "Measurement trial marker data");
      staticTrialFileName.setExtensionsAndDescription(".trc", "Static trial marker data");
      coordinateFileName.setExtensionsAndDescription(".mot", "Coordinates of static trial");

      // Add checkbox titled borders to model scaler and marker placer panels
      // TODO: ComponentTitledBorder example called setFocusPainted(false) on the checkboxes... do we need to do that?
      modelScalerPanelCheckBox.setForeground(new Color(0,70,213));
      markerPlacerPanelCheckBox.setForeground(new Color(0,70,213));
      modelScalerPanel.setBorder(new ComponentTitledBorder(modelScalerPanelCheckBox, modelScalerPanel, BorderFactory.createEtchedBorder()));
      markerPlacerPanel.setBorder(new ComponentTitledBorder(markerPlacerPanelCheckBox, markerPlacerPanel, BorderFactory.createEtchedBorder()));

      updateUnscaledModelDataFromModel();
      updateFromModel();

      scaleToolModel.addObserver(this);
   }

   private void bindPropertiesToComponents() {
      // Subject data
      ToolCommon.bindProperty(scaleToolModel.getScaleTool(), "mass", modelMassTextField);
      ToolCommon.bindProperty(scaleToolModel.getGenericModelMaker(), "marker_set_file", markerSetFileName);
      // Model scaler
      ToolCommon.bindProperty(scaleToolModel.getModelScaler(), "preserve_mass_distribution", preserveMassDistributionCheckBox);
      ToolCommon.bindProperty(scaleToolModel.getModelScaler(), "marker_file", measurementTrialFileName);
      ToolCommon.bindProperty(scaleToolModel.getModelScaler(), "time_range", measurementTrialStartTime);
      ToolCommon.bindProperty(scaleToolModel.getModelScaler(), "time_range", measurementTrialEndTime);
      // Maker placer
      ToolCommon.bindProperty(scaleToolModel.getMarkerPlacer(), "marker_file", staticTrialFileName);
      ToolCommon.bindProperty(scaleToolModel.getMarkerPlacer(), "time_range", staticTrialStartTime);
      ToolCommon.bindProperty(scaleToolModel.getMarkerPlacer(), "time_range", staticTrialEndTime);
      ToolCommon.bindProperty(scaleToolModel.getMarkerPlacer(), "coordinate_file", coordinateFileName);
   }

   public void update(Observable observable, Object obj) {
      if (observable instanceof OpenSimDB){
           if (obj instanceof ModelEvent) {
                if (OpenSimDB.getInstance().hasModel(scaleToolModel.getOriginalModel()))
                    return;
                else {
                    scaleToolModel.deleteObserver(this);
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
      if(observable == scaleToolModel && obj == ScaleToolModel.Operation.ExecutionStateChanged) {
         // Just need to update the buttons
         updateDialogButtons();
      } else {
         updateFromModel();
      }
   }

   public void updateUnscaledModelDataFromModel() {
      // Fill in unscaled model data -- only needs to be done once in beginning
      Model unscaledModel = scaleToolModel.getUnscaledModel();
      unscaledModelNameTextField.setText(unscaledModel.getName());
      unscaledModelNameTextField.setCaretPosition(0);
      unscaledModelMassTextField.setText(numFormat.format(scaleToolModel.getModelMass(unscaledModel)));
      unscaledModelMassTextField.setScrollOffset(0);
      // Assumes user has not had a chance to modify the marker set of the unscaled model yet...
      MarkerSet markerSet = unscaledModel.getMarkerSet();
      int numMarkers = markerSet.getSize();
      if(numMarkers > 0) unscaledMarkerSetInfoTextField.setText(numFormat.format(numMarkers)+" markers");
      else unscaledMarkerSetInfoTextField.setText("No markers");
   }

   public void updateFromModel() {
      //---------------------------------------------------------------------
      // Subject data
      //---------------------------------------------------------------------
      // Model name, mass
      modelNameTextField.setText(scaleToolModel.getName());
      modelMassTextField.setText(numFormat.format(scaleToolModel.getMass()));

      // Marker set
      Model unscaledModel = scaleToolModel.getUnscaledModel();
      MarkerSet markerSet = unscaledModel.getMarkerSet();
      int numMarkers = markerSet.getSize();
      if(numMarkers > 0) markerSetInfoTextField.setText(numFormat.format(numMarkers)+" markers");
      else markerSetInfoTextField.setText("No markers!");
      if(scaleToolModel.getUseExtraMarkerSet()) {
         loadMarkerSetCheckBox.setSelected(true);
         markerSetFileName.setEnabled(true);
         markerSetFileName.setFileName(scaleToolModel.getExtraMarkerSetFileName(),false);
         markerSetFileName.setFileIsValid(scaleToolModel.getExtraMarkerSetValid());
      } else {
         loadMarkerSetCheckBox.setSelected(false);
         markerSetFileName.setEnabled(false);
      }

      //---------------------------------------------------------------------
      // Model scaler
      //---------------------------------------------------------------------
      // Model scaler enabled
      boolean enabled = scaleToolModel.getModelScalerEnabled();
      modelScalerPanelCheckBox.setSelected(enabled);
      for(Component comp : modelScalerPanel.getComponents()) comp.setEnabled(enabled);

      // Preserve mass distribution
      preserveMassDistributionCheckBox.setSelected(scaleToolModel.getPreserveMassDistribution());

      // Measurement trial file and marker data
      measurementTrialCheckBox.setSelected(scaleToolModel.getMeasurementTrialEnabled());
      if(!scaleToolModel.getMeasurementTrialEnabled()) measurementTrialFileName.setEnabled(false);
      measurementTrialFileName.setFileName(scaleToolModel.getMeasurementTrialFileName(),false);
      measurementTrialFileName.setFileIsValid(scaleToolModel.getMeasurementTrialValid());
      measurementTrialInfoPanel.update(scaleToolModel.getMeasurementTrial());

      // Time range
      double[] timeRange = scaleToolModel.getMeasurementTrialTimeRange();
      measurementTrialStartTime.setText(numFormat.format(timeRange[0]));
      measurementTrialEndTime.setText(numFormat.format(timeRange[1]));
      if(!scaleToolModel.getMeasurementTrialEnabled() || !scaleToolModel.getMeasurementTrialValid()) {
         measurementTrialStartTime.setEnabled(false);
         measurementTrialEndTime.setEnabled(false);
      }

      //---------------------------------------------------------------------
      // Marker placer
      //---------------------------------------------------------------------
      enabled = scaleToolModel.getMarkerPlacerEnabled();
      markerPlacerPanelCheckBox.setSelected(enabled);
      for(Component comp : markerPlacerPanel.getComponents()) comp.setEnabled(enabled);

      // Static trial marker data
      staticTrialFileName.setFileName(scaleToolModel.getIKCommonModel().getMarkerDataFileName(),false);
      staticTrialFileName.setFileIsValid(scaleToolModel.getIKCommonModel().getMarkerDataValid());
      staticTrialInfoPanel.update(scaleToolModel.getIKCommonModel().getMarkerData());

      // Coordinate data
      if(!scaleToolModel.getIKCommonModel().getCoordinateDataEnabled()) coordinateFileName.setEnabled(false);
      coordinateCheckBox.setSelected(scaleToolModel.getIKCommonModel().getCoordinateDataEnabled());
      coordinateFileName.setFileName(scaleToolModel.getIKCommonModel().getCoordinateDataFileName(),false);
      coordinateFileName.setFileIsValid(scaleToolModel.getIKCommonModel().getCoordinateDataValid());

      // Time range
      timeRange = scaleToolModel.getIKCommonModel().getTimeRange();
      staticTrialStartTime.setText(numFormat.format(timeRange[0]));
      staticTrialEndTime.setText(numFormat.format(timeRange[1]));
      if(!scaleToolModel.getIKCommonModel().getMarkerDataValid()) {
         staticTrialStartTime.setEnabled(false);
         staticTrialEndTime.setEnabled(false);
      }

      previewStaticPoseCheckBox.setSelected(!scaleToolModel.getMoveModelMarkers());

      //---------------------------------------------------------------------
      // Dialog buttons
      //---------------------------------------------------------------------
      updateDialogButtons();
   }

   public void updateDialogButtons() {
      updateApplyButton(!scaleToolModel.isExecuting() && scaleToolModel.isModified() && scaleToolModel.isValid());
   }

   //------------------------------------------------------------------------
   // Overrides from BaseToolPanel
   //------------------------------------------------------------------------
   public void loadSettings(String fileName) { scaleToolModel.loadSettings(fileName); }
   public void saveSettings(String fileName) { scaleToolModel.saveSettings(fileName); }

   public void pressedCancel() {
      scaleToolModel.cancel();
      measurementSetDialog.dispose();
   }

   public void pressedClose() {
      measurementSetDialog.dispose();
   }

   public void pressedApply() {
      scaleToolModel.execute();
      updateDialogButtons();
   }
   
   /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPreviewRunButton = new javax.swing.JButton();
        jTabbedPane = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        markerPlacerPanel = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        staticTrialEndTime = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        staticTrialStartTime = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        staticTrialFileName = new org.opensim.swingui.FileTextFieldAndChooser();
        staticTrialInfoPanel = new org.opensim.tracking.MarkerDataInfoPanel();
        coordinateFileName = new org.opensim.swingui.FileTextFieldAndChooser();
        coordinateCheckBox = new javax.swing.JCheckBox();
        previewStaticPoseCheckBox = new javax.swing.JCheckBox();
        modelScalerPanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        measurementTrialStartTime = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        measurementTrialEndTime = new javax.swing.JTextField();
        preserveMassDistributionCheckBox = new javax.swing.JCheckBox();
        measurementTrialFileName = new org.opensim.swingui.FileTextFieldAndChooser();
        measurementTrialInfoPanel = new org.opensim.tracking.MarkerDataInfoPanel();
        measurementTrialCheckBox = new javax.swing.JCheckBox();
        subjectDataPanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        modelNameTextField = new javax.swing.JTextField();
        loadMarkerSetCheckBox = new javax.swing.JCheckBox();
        jLabel7 = new javax.swing.JLabel();
        markerSetInfoTextField = new javax.swing.JTextField();
        modelMassTextField = new javax.swing.JTextField();
        markerSetFileName = new org.opensim.swingui.FileTextFieldAndChooser();
        genericModelDataPanel = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        unscaledModelNameTextField = new javax.swing.JTextField();
        unscaledModelMassTextField = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        unscaledMarkerSetInfoTextField = new javax.swing.JTextField();

        jPreviewRunButton.setText("Preview");
        jPreviewRunButton.setToolTipText("Run scale tool in preview mode");
        jPreviewRunButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPreviewRunButtonActionPerformed(evt);
            }
        });

        markerPlacerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Adjust Model Markers"));

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel8.setText("Average markers between times");

        staticTrialEndTime.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        staticTrialEndTime.setMinimumSize(new java.awt.Dimension(1, 20));
        staticTrialEndTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                staticTrialTimeRangeActionPerformed(evt);
            }
        });
        staticTrialEndTime.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                staticTrialTimeRangeFocusLost(evt);
            }
        });

        jLabel9.setText("and");

        staticTrialStartTime.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        staticTrialStartTime.setMinimumSize(new java.awt.Dimension(1, 20));
        staticTrialStartTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                staticTrialTimeRangeActionPerformed(evt);
            }
        });
        staticTrialStartTime.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                staticTrialTimeRangeFocusLost(evt);
            }
        });

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel12.setText("Marker data for static pose");

        staticTrialFileName.setMinimumSize(new java.awt.Dimension(3, 20));
        staticTrialFileName.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                staticTrialFileNameStateChanged(evt);
            }
        });

        coordinateFileName.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                coordinateFileNameStateChanged(evt);
            }
        });

        coordinateCheckBox.setText("Coordinate data for static pose");
        coordinateCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        coordinateCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        coordinateCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                coordinateCheckBoxActionPerformed(evt);
            }
        });

        previewStaticPoseCheckBox.setText("Preview static pose (no marker movement)");
        previewStaticPoseCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        previewStaticPoseCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        previewStaticPoseCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previewStaticPoseCheckBoxActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout markerPlacerPanelLayout = new org.jdesktop.layout.GroupLayout(markerPlacerPanel);
        markerPlacerPanel.setLayout(markerPlacerPanelLayout);
        markerPlacerPanelLayout.setHorizontalGroup(
            markerPlacerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(markerPlacerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(markerPlacerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(markerPlacerPanelLayout.createSequentialGroup()
                        .add(markerPlacerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, coordinateCheckBox)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 163, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel12, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 155, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(markerPlacerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, markerPlacerPanelLayout.createSequentialGroup()
                                .add(staticTrialStartTime, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel9)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(staticTrialEndTime, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE))
                            .add(coordinateFileName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE)
                            .add(staticTrialFileName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(staticTrialInfoPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(markerPlacerPanelLayout.createSequentialGroup()
                        .add(previewStaticPoseCheckBox)
                        .addContainerGap(467, Short.MAX_VALUE))))
        );

        markerPlacerPanelLayout.linkSize(new java.awt.Component[] {coordinateCheckBox, jLabel12, jLabel8}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        markerPlacerPanelLayout.setVerticalGroup(
            markerPlacerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(markerPlacerPanelLayout.createSequentialGroup()
                .add(markerPlacerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(staticTrialInfoPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(markerPlacerPanelLayout.createSequentialGroup()
                        .add(markerPlacerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jLabel12)
                            .add(staticTrialFileName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(markerPlacerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(staticTrialStartTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel9)
                            .add(staticTrialEndTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel8))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(markerPlacerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(coordinateCheckBox)
                            .add(coordinateFileName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(previewStaticPoseCheckBox)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        modelScalerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Scale Model"));

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel5.setText("Average measurements between times");

        measurementTrialStartTime.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        measurementTrialStartTime.setMinimumSize(new java.awt.Dimension(1, 20));
        measurementTrialStartTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                measurementSetTimeRangeActionPerformed(evt);
            }
        });
        measurementTrialStartTime.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                measurementSetTimeRangeFocusLost(evt);
            }
        });

        jLabel6.setText("and");

        measurementTrialEndTime.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        measurementTrialEndTime.setMinimumSize(new java.awt.Dimension(1, 20));
        measurementTrialEndTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                measurementSetTimeRangeActionPerformed(evt);
            }
        });
        measurementTrialEndTime.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                measurementSetTimeRangeFocusLost(evt);
            }
        });

        preserveMassDistributionCheckBox.setText("Preserve mass distribution during scale");
        preserveMassDistributionCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        preserveMassDistributionCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        preserveMassDistributionCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                preserveMassDistributionCheckBoxActionPerformed(evt);
            }
        });

        measurementTrialFileName.setMinimumSize(new java.awt.Dimension(3, 20));
        measurementTrialFileName.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                measurementTrialFileNameStateChanged(evt);
            }
        });

        measurementTrialCheckBox.setText("Marker data for measurements");
        measurementTrialCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        measurementTrialCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        measurementTrialCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                measurementTrialCheckBoxActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout modelScalerPanelLayout = new org.jdesktop.layout.GroupLayout(modelScalerPanel);
        modelScalerPanel.setLayout(modelScalerPanelLayout);
        modelScalerPanelLayout.setHorizontalGroup(
            modelScalerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, modelScalerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(modelScalerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(preserveMassDistributionCheckBox)
                    .add(measurementTrialCheckBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 208, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, modelScalerPanelLayout.createSequentialGroup()
                        .add(jLabel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 204, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                .add(modelScalerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, modelScalerPanelLayout.createSequentialGroup()
                        .add(measurementTrialStartTime, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel6)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(measurementTrialEndTime, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE))
                    .add(measurementTrialFileName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(measurementTrialInfoPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        modelScalerPanelLayout.linkSize(new java.awt.Component[] {jLabel5, measurementTrialCheckBox, preserveMassDistributionCheckBox}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        modelScalerPanelLayout.setVerticalGroup(
            modelScalerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(modelScalerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(modelScalerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(modelScalerPanelLayout.createSequentialGroup()
                        .add(modelScalerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(modelScalerPanelLayout.createSequentialGroup()
                                .add(preserveMassDistributionCheckBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(measurementTrialCheckBox))
                            .add(measurementTrialFileName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(modelScalerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel5)
                            .add(measurementTrialEndTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel6)
                            .add(measurementTrialStartTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(measurementTrialInfoPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        subjectDataPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Subject Data"));

        jLabel4.setText("Model name");

        jLabel2.setText("Mass");

        jLabel1.setText("kg");

        modelNameTextField.setMinimumSize(new java.awt.Dimension(3, 20));
        modelNameTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modelNameTextFieldActionPerformed(evt);
            }
        });
        modelNameTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                modelNameTextFieldFocusLost(evt);
            }
        });

        loadMarkerSetCheckBox.setText("Add markers from file");
        loadMarkerSetCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        loadMarkerSetCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        loadMarkerSetCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadMarkerSetCheckBoxActionPerformed(evt);
            }
        });

        jLabel7.setText("Resulting marker set");

        markerSetInfoTextField.setEditable(false);
        markerSetInfoTextField.setMinimumSize(new java.awt.Dimension(3, 20));

        modelMassTextField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        modelMassTextField.setMinimumSize(new java.awt.Dimension(3, 20));
        modelMassTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modelMassTextFieldActionPerformed(evt);
            }
        });
        modelMassTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                modelMassTextFieldFocusLost(evt);
            }
        });

        markerSetFileName.setMinimumSize(new java.awt.Dimension(3, 20));
        markerSetFileName.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                markerSetFileNameStateChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout subjectDataPanelLayout = new org.jdesktop.layout.GroupLayout(subjectDataPanel);
        subjectDataPanel.setLayout(subjectDataPanelLayout);
        subjectDataPanelLayout.setHorizontalGroup(
            subjectDataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(subjectDataPanelLayout.createSequentialGroup()
                .add(subjectDataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(subjectDataPanelLayout.createSequentialGroup()
                        .add(72, 72, 72)
                        .add(subjectDataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jLabel2)
                            .add(jLabel4)))
                    .add(subjectDataPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(subjectDataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(subjectDataPanelLayout.createSequentialGroup()
                                .add(17, 17, 17)
                                .add(jLabel7))
                            .add(loadMarkerSetCheckBox))))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(subjectDataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(markerSetFileName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                    .add(modelNameTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                    .add(modelMassTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                    .add(markerSetInfoTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel1))
        );
        subjectDataPanelLayout.setVerticalGroup(
            subjectDataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(subjectDataPanelLayout.createSequentialGroup()
                .add(subjectDataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(modelNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(subjectDataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(jLabel1)
                    .add(modelMassTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(subjectDataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(loadMarkerSetCheckBox)
                    .add(markerSetFileName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(subjectDataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(markerSetInfoTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        genericModelDataPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Generic Model Data"));

        jLabel13.setText("Model name");

        unscaledModelNameTextField.setEditable(false);
        unscaledModelNameTextField.setMinimumSize(new java.awt.Dimension(3, 20));

        unscaledModelMassTextField.setEditable(false);
        unscaledModelMassTextField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        unscaledModelMassTextField.setMinimumSize(new java.awt.Dimension(3, 20));

        jLabel14.setText("Mass");

        jLabel15.setText("kg");

        jLabel16.setText("Marker set");

        unscaledMarkerSetInfoTextField.setEditable(false);
        unscaledMarkerSetInfoTextField.setMinimumSize(new java.awt.Dimension(3, 20));

        org.jdesktop.layout.GroupLayout genericModelDataPanelLayout = new org.jdesktop.layout.GroupLayout(genericModelDataPanel);
        genericModelDataPanel.setLayout(genericModelDataPanelLayout);
        genericModelDataPanelLayout.setHorizontalGroup(
            genericModelDataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(genericModelDataPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(genericModelDataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabel13)
                    .add(jLabel14)
                    .add(jLabel16))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(genericModelDataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(unscaledMarkerSetInfoTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
                    .add(unscaledModelNameTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
                    .add(unscaledModelMassTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel15))
        );
        genericModelDataPanelLayout.setVerticalGroup(
            genericModelDataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(genericModelDataPanelLayout.createSequentialGroup()
                .add(genericModelDataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel13)
                    .add(unscaledModelNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(genericModelDataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel14)
                    .add(jLabel15)
                    .add(unscaledModelMassTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(genericModelDataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel16)
                    .add(unscaledMarkerSetInfoTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, markerPlacerPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1Layout.createSequentialGroup()
                        .add(subjectDataPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(genericModelDataPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, modelScalerPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(genericModelDataPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(subjectDataPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(modelScalerPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(16, 16, 16)
                .add(markerPlacerPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                .add(23, 23, 23))
        );

        jTabbedPane.addTab("Settings", jPanel1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jTabbedPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 787, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jTabbedPane))
        );
    }// </editor-fold>//GEN-END:initComponents

   //------------------------------------------------------------------------
   // Subject data
   //------------------------------------------------------------------------
   private void modelNameTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_modelNameTextFieldFocusLost
      if(!evt.isTemporary()) modelNameTextFieldActionPerformed(null);
   }//GEN-LAST:event_modelNameTextFieldFocusLost

   private void modelNameTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modelNameTextFieldActionPerformed
      scaleToolModel.setName(modelNameTextField.getText());
   }//GEN-LAST:event_modelNameTextFieldActionPerformed
   
   private void modelMassTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_modelMassTextFieldFocusLost
      if(!evt.isTemporary()) modelMassTextFieldActionPerformed(null);
   }//GEN-LAST:event_modelMassTextFieldFocusLost

   private void modelMassTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modelMassTextFieldActionPerformed
      try {
         double value = numFormat.parse(modelMassTextField.getText()).doubleValue();
         scaleToolModel.setMass(value);
      } catch (ParseException ex) {
         Toolkit.getDefaultToolkit().beep();
         modelMassTextField.setText(numFormat.format(scaleToolModel.getMass()));
      }
   }//GEN-LAST:event_modelMassTextFieldActionPerformed

   private void loadMarkerSetCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadMarkerSetCheckBoxActionPerformed
      scaleToolModel.setUseExtraMarkerSet(loadMarkerSetCheckBox.isSelected());
   }//GEN-LAST:event_loadMarkerSetCheckBoxActionPerformed

   private void markerSetFileNameStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_markerSetFileNameStateChanged
      scaleToolModel.setExtraMarkerSetFileName(markerSetFileName.getFileName());
   }//GEN-LAST:event_markerSetFileNameStateChanged

   //------------------------------------------------------------------------
   // ModelScaler data
   //------------------------------------------------------------------------

   private void preserveMassDistributionCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_preserveMassDistributionCheckBoxActionPerformed
      scaleToolModel.setPreserveMassDistribution(((JCheckBox)evt.getSource()).isSelected());
   }//GEN-LAST:event_preserveMassDistributionCheckBoxActionPerformed

   private void measurementTrialCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_measurementTrialCheckBoxActionPerformed
      scaleToolModel.setMeasurementTrialEnabled(measurementTrialCheckBox.isSelected());
   }//GEN-LAST:event_measurementTrialCheckBoxActionPerformed

   private void measurementTrialFileNameStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_measurementTrialFileNameStateChanged
      scaleToolModel.setMeasurementTrialFileName(measurementTrialFileName.getFileName());
   }//GEN-LAST:event_measurementTrialFileNameStateChanged

   private void measurementSetTimeRangeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_measurementSetTimeRangeFocusLost
      if(!evt.isTemporary()) measurementSetTimeRangeActionPerformed(null);
   }//GEN-LAST:event_measurementSetTimeRangeFocusLost

   private void measurementSetTimeRangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_measurementSetTimeRangeActionPerformed
      try {
         double[] range = new double[]{numFormat.parse(measurementTrialStartTime.getText()).doubleValue(),
                                       numFormat.parse(measurementTrialEndTime.getText()).doubleValue()};
         scaleToolModel.setMeasurementTrialTimeRange(range);
      } catch (ParseException ex) { // To catch parsing problems (string -> double)
         Toolkit.getDefaultToolkit().beep();
         double[] timeRange = scaleToolModel.getMeasurementTrialTimeRange();
         measurementTrialStartTime.setText(numFormat.format(timeRange[0]));
         measurementTrialEndTime.setText(numFormat.format(timeRange[1]));
      }
   }//GEN-LAST:event_measurementSetTimeRangeActionPerformed
   
   //------------------------------------------------------------------------
   // MarkerPlacer data
   //------------------------------------------------------------------------

   private void staticTrialFileNameStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_staticTrialFileNameStateChanged
      scaleToolModel.getIKCommonModel().setMarkerDataFileName(staticTrialFileName.getFileName());
   }//GEN-LAST:event_staticTrialFileNameStateChanged

   private void coordinateCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_coordinateCheckBoxActionPerformed
      scaleToolModel.getIKCommonModel().setCoordinateDataEnabled(coordinateCheckBox.isSelected());
   }//GEN-LAST:event_coordinateCheckBoxActionPerformed

   private void coordinateFileNameStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_coordinateFileNameStateChanged
      scaleToolModel.getIKCommonModel().setCoordinateDataFileName(coordinateFileName.getFileName());
   }//GEN-LAST:event_coordinateFileNameStateChanged
   
   private void staticTrialTimeRangeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_staticTrialTimeRangeFocusLost
      if(!evt.isTemporary()) staticTrialTimeRangeActionPerformed(null);
   }//GEN-LAST:event_staticTrialTimeRangeFocusLost

   private void staticTrialTimeRangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_staticTrialTimeRangeActionPerformed
      try {
         double[] range = new double[]{numFormat.parse(staticTrialStartTime.getText()).doubleValue(),
                                       numFormat.parse(staticTrialEndTime.getText()).doubleValue()};
         scaleToolModel.getIKCommonModel().setTimeRange(range);
      } catch (ParseException ex) { // To catch parsing problems (string -> double)
         Toolkit.getDefaultToolkit().beep();
         double[] timeRange = scaleToolModel.getIKCommonModel().getTimeRange();
         staticTrialStartTime.setText(numFormat.format(timeRange[0]));
         staticTrialEndTime.setText(numFormat.format(timeRange[1]));
      }
   }//GEN-LAST:event_staticTrialTimeRangeActionPerformed

   private void previewStaticPoseCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previewStaticPoseCheckBoxActionPerformed
      scaleToolModel.setMoveModelMarkers(!previewStaticPoseCheckBox.isSelected());
   }//GEN-LAST:event_previewStaticPoseCheckBoxActionPerformed

    private void jPreviewRunButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPreviewRunButtonActionPerformed
        // TODO add your handling code here:
        scaleToolModel.setMoveModelMarkers(!previewStaticPoseCheckBox.isSelected());
        scaleToolModel.execute();
    }//GEN-LAST:event_jPreviewRunButtonActionPerformed

   //------------------------------------------------------------------------
   // Local variables
   //------------------------------------------------------------------------

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox coordinateCheckBox;
    private org.opensim.swingui.FileTextFieldAndChooser coordinateFileName;
    private javax.swing.JPanel genericModelDataPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton jPreviewRunButton;
    private javax.swing.JTabbedPane jTabbedPane;
    private javax.swing.JCheckBox loadMarkerSetCheckBox;
    private javax.swing.JPanel markerPlacerPanel;
    private org.opensim.swingui.FileTextFieldAndChooser markerSetFileName;
    private javax.swing.JTextField markerSetInfoTextField;
    private javax.swing.JCheckBox measurementTrialCheckBox;
    private javax.swing.JTextField measurementTrialEndTime;
    private org.opensim.swingui.FileTextFieldAndChooser measurementTrialFileName;
    private org.opensim.tracking.MarkerDataInfoPanel measurementTrialInfoPanel;
    private javax.swing.JTextField measurementTrialStartTime;
    private javax.swing.JTextField modelMassTextField;
    private javax.swing.JTextField modelNameTextField;
    private javax.swing.JPanel modelScalerPanel;
    private javax.swing.JCheckBox preserveMassDistributionCheckBox;
    private javax.swing.JCheckBox previewStaticPoseCheckBox;
    private javax.swing.JTextField staticTrialEndTime;
    private org.opensim.swingui.FileTextFieldAndChooser staticTrialFileName;
    private org.opensim.tracking.MarkerDataInfoPanel staticTrialInfoPanel;
    private javax.swing.JTextField staticTrialStartTime;
    private javax.swing.JPanel subjectDataPanel;
    private javax.swing.JTextField unscaledMarkerSetInfoTextField;
    private javax.swing.JTextField unscaledModelMassTextField;
    private javax.swing.JTextField unscaledModelNameTextField;
    // End of variables declaration//GEN-END:variables
   // Relinquish C++ resources by setting references to them to null
   public void cleanup()
   {
      scaleToolModel.cleanup();
   }
   
}
