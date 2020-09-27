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
import java.util.Vector;
import javax.swing.JSpinner;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.opensim.modeling.IMUInverseKinematicsTool;
import org.opensim.modeling.Model;
import org.opensim.modeling.OrientationWeight;
import org.opensim.modeling.OrientationWeightSet;
import org.opensim.modeling.StdVectorString;
import org.opensim.modeling.Vec3;
import org.opensim.swingui.RotationSpinnerListModel;
import org.opensim.utils.BrowserLauncher;
import org.opensim.view.ModelEvent;
import org.opensim.view.ModelPose;
import org.opensim.view.pub.OpenSimDB;

public class IMUIKToolPanel extends BaseToolPanel implements Observer {
   private IMUIKToolModel ikToolModel = null;
   private NumberFormat numFormat = NumberFormat.getInstance();
    RotationSpinnerListModel xSpinnerModel=new RotationSpinnerListModel(0., -270., 360., 90.);
    RotationSpinnerListModel ySpinnerModel=new RotationSpinnerListModel(0., -270., 360., 90.);
    RotationSpinnerListModel zSpinnerModel=new RotationSpinnerListModel(0., -270., 360., 90.);
    Vec3 rotationsInDegrees= new Vec3(0);

   /** Creates new form IKToolPanel */
   public IMUIKToolPanel(Model model) throws IOException {
      if(model==null) throw new IOException("IKToolPanel got null model");

      ikToolModel = new IMUIKToolModel(model);

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

      sensorQFileName.setExtensionsAndDescription(".sto", "IK trial sensor data");
      outputMotionFilePath.setExtensionsAndDescription(".mot", "Result motion file for IK");
      outputMotionFilePath.setIncludeOpenButton(false);
      outputMotionFilePath.setDirectoriesOnly(false);
      outputMotionFilePath.setCheckIfFileExists(false);
      outputMotionFilePath.setSaveMode(true);
      updateFromModel();

      ikToolModel.addObserver(this);
   }

   private void bindPropertiesToComponents() {
       
      IMUInverseKinematicsTool ikTool = ikToolModel.getIKTool();
      //ToolCommon.bindProperty(ikTool, "marker_file", sensorQFileName);
      //ToolCommon.bindProperty(ikTool, "coordinate_file", coordinateFileName);
      ToolCommon.bindProperty(ikTool, "time_range", startTime);
      ToolCommon.bindProperty(ikTool, "time_range", endTime);
      //ToolCommon.bindProperty(ikTool, "output_motion_file", outputMotionFilePath);
      
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

   public void updateFromModel() {
      // sensor data
      sensorQFileName.setFileName(ikToolModel.getSensorOrientationsFileName(),false);
      //sensorQFileName.setFileIsValid(ikToolModel.getMarkerDataValid());
      // Rotations
      Vec3 rotations=ikToolModel.getRotations();
      XSpinner.setValue(rotations.get(0));
      YSpinner.setValue(rotations.get(1));
      ZSpinner.setValue(rotations.get(2));
      
      // Time range
      double[] timeRange = ikToolModel.getTimeRange();
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

        calibrationPanel = new javax.swing.JPanel();
        calibrationFileName = new org.opensim.swingui.FileTextFieldAndChooser();
        jLabel3 = new javax.swing.JLabel();
        baseIMUComboBox = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        directionComboBox = new javax.swing.JComboBox<>();
        sensorIKPanel = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        endTime = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        startTime = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        sensorQFileName = new org.opensim.swingui.FileTextFieldAndChooser();
        jWeightsButton = new javax.swing.JButton();
        jReportErrorsCheckBox = new javax.swing.JCheckBox();
        outputPanel = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        outputMotionFilePath = new org.opensim.swingui.FileTextFieldAndChooser();
        transformDataPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        XSpinner = new javax.swing.JSpinner();
        YSpinner = new javax.swing.JSpinner();
        ZSpinner = new javax.swing.JSpinner();

        calibrationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Calibration"));

        calibrationFileName.setMinimumSize(new java.awt.Dimension(3, 20));
        calibrationFileName.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                calibrationFileNameStateChanged(evt);
            }
        });

        jLabel3.setText("Sensor data file:");

        baseIMUComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "pelvis_imu", "torso_imu", " " }));

        jLabel4.setText("Sensor label:");

        jLabel5.setText("Corresponding heading axis:");

        directionComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "+X", "+Y", "+Z", "-X", "-Y", "-Z" }));

        org.jdesktop.layout.GroupLayout calibrationPanelLayout = new org.jdesktop.layout.GroupLayout(calibrationPanel);
        calibrationPanel.setLayout(calibrationPanelLayout);
        calibrationPanelLayout.setHorizontalGroup(
            calibrationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(calibrationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(calibrationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, calibrationPanelLayout.createSequentialGroup()
                        .add(jLabel3)
                        .add(1, 1, 1)
                        .add(calibrationFileName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(calibrationPanelLayout.createSequentialGroup()
                        .add(jLabel4)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(baseIMUComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(jLabel5)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(directionComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        calibrationPanelLayout.setVerticalGroup(
            calibrationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(calibrationPanelLayout.createSequentialGroup()
                .add(calibrationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(calibrationFileName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(calibrationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(baseIMUComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel4)
                    .add(jLabel5)
                    .add(directionComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(0, 0, Short.MAX_VALUE))
        );

        sensorIKPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "IK Trial"));

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
        jLabel12.setText("Sensor orientation file (quaternions):");

        sensorQFileName.setMinimumSize(new java.awt.Dimension(3, 20));
        sensorQFileName.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sensorQFileNameStateChanged(evt);
            }
        });

        jWeightsButton.setText("Weights...");
        jWeightsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jWeightsButtonActionPerformed(evt);
            }
        });

        jReportErrorsCheckBox.setText("Report orientation errors");
        jReportErrorsCheckBox.setBorder(null);
        jReportErrorsCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jReportErrorsCheckBoxActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout sensorIKPanelLayout = new org.jdesktop.layout.GroupLayout(sensorIKPanel);
        sensorIKPanel.setLayout(sensorIKPanelLayout);
        sensorIKPanelLayout.setHorizontalGroup(
            sensorIKPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(sensorIKPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(sensorIKPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(sensorIKPanelLayout.createSequentialGroup()
                        .add(sensorIKPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(jLabel8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jLabel12))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(sensorIKPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(sensorQFileName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(sensorIKPanelLayout.createSequentialGroup()
                                .add(sensorIKPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(sensorIKPanelLayout.createSequentialGroup()
                                        .add(0, 0, Short.MAX_VALUE)
                                        .add(jReportErrorsCheckBox))
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, sensorIKPanelLayout.createSequentialGroup()
                                        .add(startTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 114, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                        .add(jLabel9)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                        .add(endTime, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 108, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(0, 0, Short.MAX_VALUE)))
                                .add(88, 88, 88))))
                    .add(sensorIKPanelLayout.createSequentialGroup()
                        .add(jWeightsButton)
                        .add(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        sensorIKPanelLayout.linkSize(new java.awt.Component[] {jLabel12, jLabel8}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        sensorIKPanelLayout.setVerticalGroup(
            sensorIKPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(sensorIKPanelLayout.createSequentialGroup()
                .add(sensorIKPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(sensorQFileName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel12))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(sensorIKPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jWeightsButton)
                    .add(jReportErrorsCheckBox))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(sensorIKPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
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

        transformDataPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Data Transformation"));

        jLabel1.setText("Space fixed Euler angle transform from sensor space to OpenSim");

        jLabel2.setText("Rotations X, Y, Z (degrees):");

        XSpinner.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        XSpinner.setModel(xSpinnerModel);
        XSpinner.setToolTipText("Rotation angle, 90 degree increments");
        XSpinner.setAlignmentX(0.0F);
        XSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                XSpinnerStateChanged(evt);
            }
        });

        YSpinner.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        YSpinner.setModel(ySpinnerModel);
        YSpinner.setToolTipText("Rotation angle, 90 degree increments");
        YSpinner.setAlignmentX(0.0F);
        YSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                YSpinnerStateChanged(evt);
            }
        });

        ZSpinner.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        ZSpinner.setModel(zSpinnerModel);
        ZSpinner.setToolTipText("Rotation angle, 90 degree increments");
        ZSpinner.setAlignmentX(0.0F);
        ZSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                ZSpinnerStateChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout transformDataPanelLayout = new org.jdesktop.layout.GroupLayout(transformDataPanel);
        transformDataPanel.setLayout(transformDataPanelLayout);
        transformDataPanelLayout.setHorizontalGroup(
            transformDataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(transformDataPanelLayout.createSequentialGroup()
                .add(transformDataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(transformDataPanelLayout.createSequentialGroup()
                        .add(jLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(XSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 66, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(YSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 66, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(ZSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 66, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(0, 0, Short.MAX_VALUE))
        );
        transformDataPanelLayout.setVerticalGroup(
            transformDataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(transformDataPanelLayout.createSequentialGroup()
                .add(9, 9, 9)
                .add(jLabel1)
                .add(3, 3, 3)
                .add(transformDataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, XSpinner)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, YSpinner)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, ZSpinner))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(0, 0, 0)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, sensorIKPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, outputPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, transformDataPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .add(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(0, 0, 0)
                .add(transformDataPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(sensorIKPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(outputPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

   private void sensorQFileNameStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sensorQFileNameStateChanged
      ikToolModel.setSensorDataFileName(sensorQFileName.getFileName());
   }//GEN-LAST:event_sensorQFileNameStateChanged

   private void timeRangeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_timeRangeFocusLost
      if(!evt.isTemporary()) timeRangeActionPerformed(null);
   }//GEN-LAST:event_timeRangeFocusLost

   private void timeRangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timeRangeActionPerformed
      try {
         double[] range = new double[]{numFormat.parse(startTime.getText()).doubleValue(), numFormat.parse(endTime.getText()).doubleValue()};
         ikToolModel.setTimeRange(range);
      } catch (ParseException ex) { // To catch parsing problems (string -> double)
         Toolkit.getDefaultToolkit().beep();
         double[] timeRange = ikToolModel.getTimeRange();
         startTime.setText(numFormat.format(timeRange[0]));
         endTime.setText(numFormat.format(timeRange[1]));
      }
   }//GEN-LAST:event_timeRangeActionPerformed

private void outputMotionFilePathStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_outputMotionFilePathStateChanged
      ikToolModel.getIKTool().setOutputMotionFileName(outputMotionFilePath.getFileName());
}//GEN-LAST:event_outputMotionFilePathStateChanged

    private void jWeightsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jWeightsButtonActionPerformed
        // TODO add your handling code here:
        OrientationWeightSet currentWeights = ikToolModel.getOrientation_weightset();
        OrientationWeightSet saveWeights = OrientationWeightSet.safeDownCast(currentWeights.clone());
        OrientationWeightsJPanel weightsPanel = new OrientationWeightsJPanel(currentWeights);
        Object [] options =  {  NotifyDescriptor.OK_OPTION,
                                NotifyDescriptor.CANCEL_OPTION};
        DialogDescriptor weightsDialog = new DialogDescriptor(weightsPanel,
                                            "Sensor Weights");
        DialogDisplayer.getDefault().createDialog(weightsDialog).setVisible(true);
        Object userInput = weightsDialog.getValue();
        if (((Integer)userInput).compareTo((Integer)DialogDescriptor.OK_OPTION)!=0){
            // Uer cancelled
            currentWeights = OrientationWeightSet.safeDownCast(saveWeights.clone());
            ikToolModel.setOrientation_weightset(currentWeights);
        }
    }//GEN-LAST:event_jWeightsButtonActionPerformed

    private void jReportErrorsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jReportErrorsCheckBoxActionPerformed
        // TODO add your handling code here:
        ikToolModel.setReportErrors(jReportErrorsCheckBox.isSelected());
    }//GEN-LAST:event_jReportErrorsCheckBoxActionPerformed

    private void calibrationFileNameStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_calibrationFileNameStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_calibrationFileNameStateChanged

    private void ZSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ZSpinnerStateChanged
        // TODO add your handling code here:
        double delta = getRotationAngleChange(evt);
        updateTransform(xSpinnerModel.getLastValue(), ySpinnerModel.getLastValue(), delta);
    }//GEN-LAST:event_ZSpinnerStateChanged

    private void YSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_YSpinnerStateChanged
        // TODO add your handling code here:
        double delta = getRotationAngleChange(evt);
        updateTransform(xSpinnerModel.getLastValue(), delta,
            zSpinnerModel.getLastValue());
    }//GEN-LAST:event_YSpinnerStateChanged

    private void XSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_XSpinnerStateChanged
        double delta = getRotationAngleChange(evt);
        updateTransform(delta, ySpinnerModel.getLastValue(),
            zSpinnerModel.getLastValue());

        // TODO add your handling code here:
    }//GEN-LAST:event_XSpinnerStateChanged
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSpinner XSpinner;
    private javax.swing.JSpinner YSpinner;
    private javax.swing.JSpinner ZSpinner;
    private javax.swing.JComboBox<String> baseIMUComboBox;
    private org.opensim.swingui.FileTextFieldAndChooser calibrationFileName;
    private javax.swing.JPanel calibrationPanel;
    private javax.swing.JComboBox<String> directionComboBox;
    private javax.swing.JTextField endTime;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JCheckBox jReportErrorsCheckBox;
    private javax.swing.JButton jWeightsButton;
    private org.opensim.swingui.FileTextFieldAndChooser outputMotionFilePath;
    private javax.swing.JPanel outputPanel;
    private javax.swing.JPanel sensorIKPanel;
    private org.opensim.swingui.FileTextFieldAndChooser sensorQFileName;
    private javax.swing.JTextField startTime;
    private javax.swing.JPanel transformDataPanel;
    // End of variables declaration//GEN-END:variables
    private double getRotationAngleChange(final javax.swing.event.ChangeEvent evt) {
// TODO add your handling code here:
        RotationSpinnerListModel numberModel = (RotationSpinnerListModel)((JSpinner)evt.getSource()).getModel();
        double newValue = numberModel.getNumber().doubleValue();
        double delta = newValue-numberModel.getLastValue();
        numberModel.setLastValue(newValue);
        return delta;
    }

    private void updateTransform(double vx, double vy, double vz) {
        rotationsInDegrees.set(0, vx);
        rotationsInDegrees.set(1, vy);
        rotationsInDegrees.set(2, vz);
        ikToolModel.setRotations(rotationsInDegrees);
    }
    
    private void populateOrientationWeights(){

    }
}
