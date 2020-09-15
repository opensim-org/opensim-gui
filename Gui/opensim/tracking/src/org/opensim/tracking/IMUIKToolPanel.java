/* -------------------------------------------------------------------------- *
 * OpenSim: IMUIKToolPanel.java                                               *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2020 Stanford University and the Authors                *
 * Author(s): Ayman Habib                                          *
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
 * IKToolPanel.java
 *
 * Created on July 16, 2007, 5:17 PM
 */

package org.opensim.tracking;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Observable;
import java.util.Observer;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.opensim.modeling.InverseKinematicsTool;
import org.opensim.modeling.MarkerSet;
import org.opensim.modeling.Model;
import org.opensim.utils.BrowserLauncher;
import org.opensim.utils.TheApp;
import org.opensim.view.ModelEvent;
import org.opensim.view.pub.OpenSimDB;

public class IMUIKToolPanel extends BaseToolPanel implements Observer {
   private IKToolModel ikToolModel = null;
   private NumberFormat numFormat = NumberFormat.getInstance();

   /** Creates new form IKToolPanel */
   public IMUIKToolPanel(Model model) throws IOException {
      if(model==null) throw new IOException("IKToolPanel got null model");

      ikToolModel = new IKToolModel(model);

      if (numFormat instanceof DecimalFormat) {
        ((DecimalFormat) numFormat).applyPattern("###0.#########");
      }
      
      helpButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                BrowserLauncher.openURL("https://simtk-confluence.stanford.edu/display/OpenSim40/Inverse+Kinematics");
            }
      });

      initComponents();
      bindPropertiesToComponents();

      setSettingsFileDescription("IK tool settings file");

      jTabbedPane.addTab("Weights", new IKTaskSetPanel(ikToolModel.getIKCommonModel()));

      markerFileName.setExtensionsAndDescription(".trc", "IK trial marker data");
      coordinateFileName.setExtensionsAndDescription(".mot,.sto", "Coordinates of IK trial");
      outputMotionFilePath.setExtensionsAndDescription(".mot", "Result motion file for IK");
      outputMotionFilePath.setIncludeOpenButton(false);
      outputMotionFilePath.setDirectoriesOnly(false);
      outputMotionFilePath.setCheckIfFileExists(false);
      outputMotionFilePath.setSaveMode(true);
      updateModelDataFromModel();
      updateFromModel();

      ikToolModel.addObserver(this);
   }

   private void bindPropertiesToComponents() {
      InverseKinematicsTool ikTool = ikToolModel.getIKTool();
      ToolCommon.bindProperty(ikTool, "marker_file", markerFileName);
      ToolCommon.bindProperty(ikTool, "coordinate_file", coordinateFileName);
      ToolCommon.bindProperty(ikTool, "time_range", startTime);
      ToolCommon.bindProperty(ikTool, "time_range", endTime);
      ToolCommon.bindProperty(ikTool, "output_motion_file", outputMotionFilePath);
      
   }

   public void update(Observable observable, Object obj) {
      if (observable instanceof OpenSimDB){
           if (obj instanceof ModelEvent) {
                if (OpenSimDB.getInstance().hasModel(ikToolModel.getOriginalModel()))
                    return;
                else {
                    ikToolModel.deleteObserver(this);
                    NotifyDescriptor.Message dlg =
                          new NotifyDescriptor.Message("Model used by the tool is being closed. Closing tool.");
                    DialogDisplayer.getDefault().notify(dlg);
                    this.close();
                    return;
                }        
           }
           return;
       }
      if(observable == ikToolModel && obj == IKToolModel.Operation.ExecutionStateChanged) {
         // Just need to update the buttons
         updateDialogButtons();
      } else {
         updateFromModel();
      }
   }

   public void updateModelDataFromModel() {
      // Fill in model data -- only needs to be done once in beginning
      Model model = ikToolModel.getOriginalModel();
      modelNameTextField.setText(model.getName());
      modelNameTextField.setCaretPosition(0);
      MarkerSet markerSet = model.getMarkerSet();
      int numMarkers = markerSet.getSize();
      if(numMarkers > 0) markerSetInfoTextField.setText(numFormat.format(numMarkers)+" markers");
      else markerSetInfoTextField.setText("No markers");
   }

   public void updateFromModel() {
      // Static trial marker data
      markerFileName.setFileName(ikToolModel.getIKCommonModel().getMarkerDataFileName(),false);
      markerFileName.setFileIsValid(ikToolModel.getIKCommonModel().getMarkerDataValid());
      //OpenSim23 markerDataInfoPanel.update(ikToolModel.getIKCommonModel().getMarkerData());

      // Coordinate data
      coordinateFileName.setEnabled(ikToolModel.getIKCommonModel().getCoordinateDataEnabled());
      coordinateCheckBox.setSelected(ikToolModel.getIKCommonModel().getCoordinateDataEnabled());
      coordinateFileName.setFileName(ikToolModel.getIKCommonModel().getCoordinateDataFileName(),false);
      coordinateFileName.setFileIsValid(ikToolModel.getIKCommonModel().getCoordinateDataValid());

      
      // Time range
      double[] timeRange = ikToolModel.getIKCommonModel().getTimeRange();
      startTime.setText(numFormat.format(timeRange[0]));
      endTime.setText(numFormat.format(timeRange[1]));

      outputMotionFilePath.setFileName(ikToolModel.getIKTool().getOutputMotionFileName());
      //---------------------------------------------------------------------
      // Dialog buttons
      //---------------------------------------------------------------------
      updateDialogButtons();
   }


   public void updateDialogButtons() {
      updateApplyButton(!ikToolModel.isExecuting() && ikToolModel.isModified() && ikToolModel.isValid());
   }

   //------------------------------------------------------------------------
   // Overrides from BaseToolPanel
   //------------------------------------------------------------------------

   public void loadSettings(String fileName) { ikToolModel.loadSettings(fileName); }
   public void saveSettings(String fileName) { ikToolModel.saveSettings(fileName); }

   public void pressedCancel() {
      ikToolModel.cancel();
   }

   public void pressedClose() {
   }

   public void pressedApply() {
      ikToolModel.execute();
      updateDialogButtons();
   }

   /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        genericModelDataPanel = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        modelNameTextField = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        markerSetInfoTextField = new javax.swing.JTextField();
        jTabbedPane = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        markerPlacerPanel = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        endTime = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        startTime = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        markerFileName = new org.opensim.swingui.FileTextFieldAndChooser();
        jButton1 = new javax.swing.JButton();
        jCheckBox1 = new javax.swing.JCheckBox();
        outputPanel = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        outputMotionFilePath = new org.opensim.swingui.FileTextFieldAndChooser();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        XSpinner = new javax.swing.JSpinner();
        XSpinner1 = new javax.swing.JSpinner();
        XSpinner2 = new javax.swing.JSpinner();

        genericModelDataPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Current Model"));

        jLabel13.setText("Name");

        modelNameTextField.setEditable(false);
        modelNameTextField.setToolTipText("Current Model in GUI");
        modelNameTextField.setMinimumSize(new java.awt.Dimension(3, 20));

        jLabel16.setText("Marker set");

        markerSetInfoTextField.setEditable(false);
        markerSetInfoTextField.setMinimumSize(new java.awt.Dimension(3, 20));

        org.jdesktop.layout.GroupLayout genericModelDataPanelLayout = new org.jdesktop.layout.GroupLayout(genericModelDataPanel);
        genericModelDataPanel.setLayout(genericModelDataPanelLayout);
        genericModelDataPanelLayout.setHorizontalGroup(
            genericModelDataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(genericModelDataPanelLayout.createSequentialGroup()
                .add(genericModelDataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(genericModelDataPanelLayout.createSequentialGroup()
                        .add(34, 34, 34)
                        .add(jLabel13))
                    .add(genericModelDataPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(jLabel16)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(genericModelDataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(modelNameTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
                    .add(markerSetInfoTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE))
                .addContainerGap())
        );
        genericModelDataPanelLayout.setVerticalGroup(
            genericModelDataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(genericModelDataPanelLayout.createSequentialGroup()
                .add(genericModelDataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel13)
                    .add(modelNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(genericModelDataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(markerSetInfoTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel16))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        markerPlacerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "IK Trial"));

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel8.setText("Time range:");

        endTime.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        endTime.setMinimumSize(new java.awt.Dimension(1, 20));
        endTime.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                timeRangeFocusLost(evt);
            }
        });
        endTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timeRangeActionPerformed(evt);
            }
        });

        jLabel9.setText("to");

        startTime.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        startTime.setMinimumSize(new java.awt.Dimension(1, 20));
        startTime.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                timeRangeFocusLost(evt);
            }
        });
        startTime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timeRangeActionPerformed(evt);
            }
        });

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel12.setText("Sensor data file:");

        markerFileName.setMinimumSize(new java.awt.Dimension(3, 20));
        markerFileName.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                markerFileNameStateChanged(evt);
            }
        });

        jButton1.setText("Weights...");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jCheckBox1.setText("Report orientation errors");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
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
                        .add(markerPlacerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(jLabel8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jLabel12))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(markerPlacerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(markerFileName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(markerPlacerPanelLayout.createSequentialGroup()
                                .add(markerPlacerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(markerPlacerPanelLayout.createSequentialGroup()
                                        .add(0, 0, Short.MAX_VALUE)
                                        .add(jCheckBox1))
                                    .add(markerPlacerPanelLayout.createSequentialGroup()
                                        .add(startTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 114, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(18, 18, 18)
                                        .add(jLabel9)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(endTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 108, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .add(88, 88, 88))))
                    .add(markerPlacerPanelLayout.createSequentialGroup()
                        .add(jButton1)
                        .add(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        markerPlacerPanelLayout.linkSize(new java.awt.Component[] {jLabel12, jLabel8}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        markerPlacerPanelLayout.setVerticalGroup(
            markerPlacerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(markerPlacerPanelLayout.createSequentialGroup()
                .add(markerPlacerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(markerFileName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel12))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(markerPlacerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton1)
                    .add(jCheckBox1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(markerPlacerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(startTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel9)
                    .add(endTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel8))
                .add(13, 13, 13))
        );

        outputPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Output"));

        jLabel11.setText("Motion File");

        outputMotionFilePath.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                outputMotionFilePathStateChanged(evt);
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
                .add(outputMotionFilePath, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        outputPanelLayout.setVerticalGroup(
            outputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(outputPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(outputPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabel11)
                    .add(outputMotionFilePath, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Data Transformation"));

        jLabel1.setText("Space fixed Euler angle transform from sensor space to OpenSim");

        jLabel2.setText("Rotations X, Y, Z (degrees):");

        XSpinner.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        XSpinner.setModel(xSpinnerModel);
        XSpinner.setToolTipText("Rotation angle, 90 degree increments");
        XSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                XSpinnerStateChanged(evt);
            }
        });

        XSpinner1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        XSpinner1.setModel(xSpinnerModel);
        XSpinner1.setToolTipText("Rotation angle, 90 degree increments");
        XSpinner1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                XSpinner1StateChanged(evt);
            }
        });

        XSpinner2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        XSpinner2.setModel(xSpinnerModel);
        XSpinner2.setToolTipText("Rotation angle, 90 degree increments");
        XSpinner2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                XSpinner2StateChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(XSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 66, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(XSpinner1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 66, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(XSpinner2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 66, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(0, 21, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel2)
                    .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(XSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(XSpinner1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(XSpinner2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
        );

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(markerPlacerPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(outputPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(118, 118, 118)
                .add(markerPlacerPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(outputPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane.addTab("Settings", jPanel1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jTabbedPane))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jTabbedPane)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

   private void markerFileNameStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_markerFileNameStateChanged
      ikToolModel.getIKCommonModel().setMarkerDataFileName(markerFileName.getFileName());
   }//GEN-LAST:event_markerFileNameStateChanged

   private void timeRangeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_timeRangeFocusLost
      if(!evt.isTemporary()) timeRangeActionPerformed(null);
   }//GEN-LAST:event_timeRangeFocusLost

   private void timeRangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timeRangeActionPerformed
      try {
         double[] range = new double[]{numFormat.parse(startTime.getText()).doubleValue(), numFormat.parse(endTime.getText()).doubleValue()};
         ikToolModel.getIKCommonModel().setTimeRange(range);
      } catch (ParseException ex) { // To catch parsing problems (string -> double)
         Toolkit.getDefaultToolkit().beep();
         double[] timeRange = ikToolModel.getIKCommonModel().getTimeRange();
         startTime.setText(numFormat.format(timeRange[0]));
         endTime.setText(numFormat.format(timeRange[1]));
      }
   }//GEN-LAST:event_timeRangeActionPerformed

private void outputMotionFilePathStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_outputMotionFilePathStateChanged
      ikToolModel.getIKTool().setOutputMotionFileName(outputMotionFilePath.getFileName());
}//GEN-LAST:event_outputMotionFilePathStateChanged

    private void XSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_XSpinnerStateChanged
        double delta = getRotationAngleChange(evt);
        //updateTransform(delta, 0., 0.);
        updateTransform(xSpinnerModel.getLastValue(),
            ySpinnerModel.getLastValue(),
            zSpinnerModel.getLastValue());

        // TODO add your handling code here:
    }//GEN-LAST:event_XSpinnerStateChanged

    private void XSpinner1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_XSpinner1StateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_XSpinner1StateChanged

    private void XSpinner2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_XSpinner2StateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_XSpinner2StateChanged

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox1ActionPerformed
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSpinner XSpinner;
    private javax.swing.JSpinner XSpinner1;
    private javax.swing.JSpinner XSpinner2;
    private javax.swing.JTextField endTime;
    private javax.swing.JPanel genericModelDataPanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTabbedPane jTabbedPane;
    private org.opensim.swingui.FileTextFieldAndChooser markerFileName;
    private javax.swing.JPanel markerPlacerPanel;
    private javax.swing.JTextField markerSetInfoTextField;
    private javax.swing.JTextField modelNameTextField;
    private org.opensim.swingui.FileTextFieldAndChooser outputMotionFilePath;
    private javax.swing.JPanel outputPanel;
    private javax.swing.JTextField startTime;
    // End of variables declaration//GEN-END:variables
   
}
