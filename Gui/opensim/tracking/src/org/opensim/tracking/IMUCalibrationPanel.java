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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Observable;
import java.util.Observer;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JSpinner;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.opensim.modeling.IMUPlacer;
import org.opensim.modeling.Model;
import org.opensim.modeling.StdVectorString;
import org.opensim.modeling.Vec3;
import org.opensim.swingui.RotationSpinnerListModel;
import org.opensim.utils.BrowserLauncher;
import org.opensim.view.ModelEvent;
import org.opensim.view.pub.OpenSimDB;

public class IMUCalibrationPanel extends BaseToolPanel implements Observer {
   private IMUCalibrateModel calibrationModel = null;
   private NumberFormat numFormat = NumberFormat.getInstance();
    RotationSpinnerListModel xSpinnerModel=new RotationSpinnerListModel(0., -270., 360., 90.);
    RotationSpinnerListModel ySpinnerModel=new RotationSpinnerListModel(0., -270., 360., 90.);
    RotationSpinnerListModel zSpinnerModel=new RotationSpinnerListModel(0., -270., 360., 90.);
    Vec3 rotationsInDegrees= new Vec3(0);

   /** Creates new form IKToolPanel */
   public IMUCalibrationPanel(Model model) throws IOException {
      if(model==null) throw new IOException("IMUCalibrationPanel got null model");

      calibrationModel = new IMUCalibrateModel(model);

      if (numFormat instanceof DecimalFormat) {
        ((DecimalFormat) numFormat).applyPattern("###0.#########");
      }
      
      helpButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                BrowserLauncher.openURL("https://simtk-confluence.stanford.edu/display/OpenSim40/IMU+Placer");
            }
      });

      initComponents();
      bindPropertiesToComponents();

      setSettingsFileDescription("Calibration settings file");

      sensorQFileName.setExtensionsAndDescription(".sto", "Calibration sensor data");
      updateFromModel();

      calibrationModel.addObserver(this);
   }

   private void bindPropertiesToComponents() {
       
      IMUPlacer imuPlacer = calibrationModel.getIMUPlacerTool();
      ToolCommon.bindProperty(imuPlacer, "orientation_file_for_calibration", sensorQFileName);
      ToolCommon.bindProperty(imuPlacer, "base_heading_axis", jComboBoxIMUAxis);
      ToolCommon.bindProperty(imuPlacer, "base_imu_label", jComboBoxIMULabel);
      
   }

   public void update(Observable observable, Object obj) {
      if (observable instanceof OpenSimDB){
           if (obj instanceof ModelEvent) {
                if (OpenSimDB.getInstance().hasModel(calibrationModel.getOriginalModel()))
                    return;
                else {
                    calibrationModel.deleteObserver(this);
                    NotifyDescriptor.Message dlg =
                          new NotifyDescriptor.Message("Model used by the tool is being closed. Closing tool.");
                    DialogDisplayer.getDefault().notify(dlg);
                    this.close();
                    return;
                }        
           }
           return;
       }
      if(observable == calibrationModel && obj == IKToolModel.Operation.ExecutionStateChanged) {
         // Just need to update the buttons
         updateDialogButtons();
      } else {
         updateFromModel();
      }
   }

   public void updateFromModel() {
      // sensor data
      sensorQFileName.setFileName(calibrationModel.getSensorOrientationsFileName(),false);
      updateIMULabelsComboBox(calibrationModel.getSensorDataLabels());
      // populate jComboBoxIMULabel based on file
      //sensorQFileName.setFileIsValid(calibrationModel.getMarkerDataValid());
      // Rotations
      Vec3 rotations=calibrationModel.getRotations();
      XSpinner.setValue(rotations.get(0));
      YSpinner.setValue(rotations.get(1));
      ZSpinner.setValue(rotations.get(2));
      jComboBoxIMUAxis.setSelectedItem(calibrationModel.getImuAxis());
      jComboBoxIMULabel.setSelectedItem(calibrationModel.getImuLabel());
      //---------------------------------------------------------------------
      // Dialog buttons
      //---------------------------------------------------------------------
      updateDialogButtons();
   }


   public void updateDialogButtons() {
      updateApplyButton(!calibrationModel.isExecuting() && calibrationModel.isModified() && calibrationModel.isValid());
   }

   //------------------------------------------------------------------------
   // Overrides from BaseToolPanel
   //------------------------------------------------------------------------

   public void loadSettings(String fileName) { 
       calibrationModel.loadSettings(fileName);
       ownerDialog.pack();
   }
   public void saveSettings(String fileName) { calibrationModel.saveSettings(fileName); }

   public void pressedCancel() {
      calibrationModel.cancel();
   }

   public void pressedClose() {
   }

   public void pressedApply() {
      calibrationModel.execute();
      updateDialogButtons();
   }

   /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jRadioButtonReplace = new javax.swing.JRadioButton();
        jRadioButtonNewModel = new javax.swing.JRadioButton();
        transformDataPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        XSpinner = new javax.swing.JSpinner();
        YSpinner = new javax.swing.JSpinner();
        ZSpinner = new javax.swing.JSpinner();
        jLabel6 = new javax.swing.JLabel();
        sensorQFileName = new org.opensim.swingui.FileTextFieldAndChooser();
        calibrationPanel = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jComboBoxIMUAxis = new javax.swing.JComboBox<>();
        jComboBoxIMULabel = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();

        buttonGroup1.add(jRadioButtonReplace);
        jRadioButtonReplace.setSelected(true);
        jRadioButtonReplace.setText("  Replace sensors on current model");
        jRadioButtonReplace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonReplaceActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButtonNewModel);
        jRadioButtonNewModel.setText("create new model");

        transformDataPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "IMU Orientation Data & Transformation"));

        jLabel1.setText("Space fixed Euler angles (XYZ order) from IMU space to OpenSim");

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

        jLabel6.setText("Orientation file at placement pose:");

        sensorQFileName.setMinimumSize(new java.awt.Dimension(3, 20));
        sensorQFileName.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sensorQFileNameStateChanged(evt);
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
            .add(transformDataPanelLayout.createSequentialGroup()
                .add(jLabel6)
                .add(1, 1, 1)
                .add(sensorQFileName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        transformDataPanelLayout.setVerticalGroup(
            transformDataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(transformDataPanelLayout.createSequentialGroup()
                .add(6, 6, 6)
                .add(transformDataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel6)
                    .add(sensorQFileName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel1)
                .add(3, 3, 3)
                .add(transformDataPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, XSpinner)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, YSpinner)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, ZSpinner))
                .addContainerGap())
        );

        calibrationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "IMU Placement/Heading Correction (Optional)"));

        jLabel7.setText("Align IMU (name, axis):");

        jComboBoxIMUAxis.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "   ", "x", "y", "z", "-x", "-y", "-z" }));
        jComboBoxIMUAxis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxIMUAxisActionPerformed(evt);
            }
        });

        jComboBoxIMULabel.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "          ", " " }));
        jComboBoxIMULabel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxIMULabelActionPerformed(evt);
            }
        });

        jLabel10.setText("with model's forward heading.  ");

        org.jdesktop.layout.GroupLayout calibrationPanelLayout = new org.jdesktop.layout.GroupLayout(calibrationPanel);
        calibrationPanel.setLayout(calibrationPanelLayout);
        calibrationPanelLayout.setHorizontalGroup(
            calibrationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(calibrationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel7)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jComboBoxIMULabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jComboBoxIMUAxis, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel10)
                .addContainerGap())
        );
        calibrationPanelLayout.setVerticalGroup(
            calibrationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(calibrationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(calibrationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jComboBoxIMUAxis, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jComboBoxIMULabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel10)
                    .add(jLabel7))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(transformDataPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(calibrationPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(12, 12, 12)
                .add(transformDataPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(calibrationPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jRadioButtonReplaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonReplaceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButtonReplaceActionPerformed

    private void jComboBoxIMULabelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxIMULabelActionPerformed
        // TODO add your handling code here:
        calibrationModel.setIMULabel((String)jComboBoxIMULabel.getSelectedItem());
    }//GEN-LAST:event_jComboBoxIMULabelActionPerformed

    private void jComboBoxIMUAxisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxIMUAxisActionPerformed
        // TODO add your handling code here:
        calibrationModel.setIMUAxis((String)jComboBoxIMUAxis.getSelectedItem());
    }//GEN-LAST:event_jComboBoxIMUAxisActionPerformed

    private void sensorQFileNameStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sensorQFileNameStateChanged
        // TODO add your handling code here:
        calibrationModel.setSensorDataFileName(sensorQFileName.getFileName());
        updateIMULabelsComboBox(calibrationModel.getSensorDataLabels());
    }//GEN-LAST:event_sensorQFileNameStateChanged

    private void ZSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_ZSpinnerStateChanged
        // TODO add your handling code here:
        double angle = getRotationAngle(evt);
        updateTransform(xSpinnerModel.getLastValue(), ySpinnerModel.getLastValue(), angle);
    }//GEN-LAST:event_ZSpinnerStateChanged

    private void YSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_YSpinnerStateChanged
        // TODO add your handling code here:
        double angle = getRotationAngle(evt);
        updateTransform(xSpinnerModel.getLastValue(), angle,
            zSpinnerModel.getLastValue());
    }//GEN-LAST:event_YSpinnerStateChanged

    private void XSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_XSpinnerStateChanged
        double angle = getRotationAngle(evt);
        updateTransform(angle, ySpinnerModel.getLastValue(),
            zSpinnerModel.getLastValue());

        // TODO add your handling code here:
    }//GEN-LAST:event_XSpinnerStateChanged
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSpinner XSpinner;
    private javax.swing.JSpinner YSpinner;
    private javax.swing.JSpinner ZSpinner;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel calibrationPanel;
    private javax.swing.JComboBox<String> jComboBoxIMUAxis;
    private javax.swing.JComboBox<String> jComboBoxIMULabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JRadioButton jRadioButtonNewModel;
    private javax.swing.JRadioButton jRadioButtonReplace;
    private org.opensim.swingui.FileTextFieldAndChooser sensorQFileName;
    private javax.swing.JPanel transformDataPanel;
    // End of variables declaration//GEN-END:variables
    private double getRotationAngle(final javax.swing.event.ChangeEvent evt) {
// TODO add your handling code here:
        RotationSpinnerListModel numberModel = (RotationSpinnerListModel)((JSpinner)evt.getSource()).getModel();
        double newValue = numberModel.getNumber().doubleValue();
        numberModel.setLastValue(newValue);
        return newValue;
    }

    private void updateTransform(double vx, double vy, double vz) {
        rotationsInDegrees.set(0, vx);
        rotationsInDegrees.set(1, vy);
        rotationsInDegrees.set(2, vz);
        calibrationModel.setRotations(rotationsInDegrees);
    }

    private void updateIMULabelsComboBox(StdVectorString labels) {
        if (labels==null)
            return;
        int numLabels = (int)labels.size();
        String[] colNames = new String[numLabels+1];
        colNames[0] = " ";
        for (int i=0; i< numLabels; i++ ){
            colNames[i+1] = labels.get(i);
        }
        jComboBoxIMULabel.setModel(new DefaultComboBoxModel<String>(colNames));
    }
    
}
