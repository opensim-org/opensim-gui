/* -------------------------------------------------------------------------- *
 * OpenSim: EditOutputTextPanel.java                                          *
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
 * AddInputSliderPanel.java
 *
 * Created on September 7, 2010, 12:59 AM
 */

package org.opensim.k12utils;

import org.opensim.k12.*;

/**
 *
 * @author  ayman
 */
public class EditOutputTextPanel extends javax.swing.JPanel {
    
    private LabOutput outputDescription;
    private LabOutputTextToPanel outputDescription1= new LabOutputTextToPanel(); //mode=0
    private LabOutputTextToWindow outputDescription2 = new LabOutputTextToWindow(); //mode=1
    private LabOutputTextToObject outputDescription3= new LabOutputTextToObject(); //mode=2
    private LabOutputPlot outputDescription4 = new LabOutputPlot(); //mode=3
    private int mode=0;
    
    /**
     * Creates new form AddInputSliderPanel
     */
    public EditOutputTextPanel(LabOutput outputToEdit) {
        outputDescription = outputToEdit;
        initComponents();
        setMode(outputToEdit);
        updateAvailability();
        updateTextFields();
    }

    public LabOutput getOutputDescription() {
        switch(mode){
            case 0:
            outputDescription1.setHtmlTemplate(htmlOutputTextField.getText());
            outputDescription1.setQuantitySpecfication(quantitySpecificationTextField.getText());
            break;
        case 1:
            outputDescription2.setTextTemplate(htmlOutputTextField.getText());
            outputDescription2.setQuantitySpecfication(quantitySpecificationTextField.getText());
            outputDescription2.setLocation((String)cornerSpecComboBox.getSelectedItem());
            break;
        case 2:
            outputDescription3.setTextTemplate(htmlOutputTextField.getText());
            outputDescription3.setQuantitySpecfication(quantitySpecificationTextField.getText());
            outputDescription3.setOpenSimType(objectTypeTextField.getText());
            outputDescription3.setObjectName(objectNameTextField.getText());
            break;
        case 3:
            outputDescription4.setQuantitySpecfication(quantitySpecificationTextField.getText());
            outputDescription4.setPlotTitle(plotTitleTextField.getText());
            outputDescription4.setXAxisTitle(XTitleTextField.getText());
            outputDescription4.setYAxisTitle(YTitleTextField.getText());
            break;
        }
        return outputDescription;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        outputChannelButtonGroup = new javax.swing.ButtonGroup();
        outputLabel = new javax.swing.JLabel();
        htmlOutputTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        quantitySpecificationTextField = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        radioButtonPanel = new javax.swing.JRadioButton();
        radioButtonAnnotation = new javax.swing.JRadioButton();
        radioButtonAnnotationObject = new javax.swing.JRadioButton();
        radioButtonPlot = new javax.swing.JRadioButton();
        jLabel4 = new javax.swing.JLabel();
        cornerSpecComboBox = new javax.swing.JComboBox();
        objectTypeTextField = new javax.swing.JTextField();
        objectNameTextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        plotTitleTextField = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        XTitleTextField = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        YTitleTextField = new javax.swing.JTextField();

        outputLabel.setText("Html Output");

        jLabel2.setText("Quantity");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel3.setText("Output to:");

        outputChannelButtonGroup.add(radioButtonPanel);
        radioButtonPanel.setSelected(true);
        radioButtonPanel.setText("Text Panel");
        radioButtonPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        radioButtonPanel.setMargin(new java.awt.Insets(0, 0, 0, 0));
        radioButtonPanel.setOpaque(false);
        radioButtonPanel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonPanelActionPerformed(evt);
            }
        });

        outputChannelButtonGroup.add(radioButtonAnnotation);
        radioButtonAnnotation.setText("3D View Window, anchor to corner");
        radioButtonAnnotation.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        radioButtonAnnotation.setMargin(new java.awt.Insets(0, 0, 0, 0));
        radioButtonAnnotation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonAnnotationActionPerformed(evt);
            }
        });

        outputChannelButtonGroup.add(radioButtonAnnotationObject);
        radioButtonAnnotationObject.setText("3D View Window, anchor to Object");
        radioButtonAnnotationObject.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        radioButtonAnnotationObject.setMargin(new java.awt.Insets(0, 0, 0, 0));
        radioButtonAnnotationObject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonAnnotationObjectActionPerformed(evt);
            }
        });

        outputChannelButtonGroup.add(radioButtonPlot);
        radioButtonPlot.setText("Separate Plot Window");
        radioButtonPlot.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        radioButtonPlot.setMargin(new java.awt.Insets(0, 0, 0, 0));
        radioButtonPlot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonPlotActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel3)
                    .add(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(radioButtonAnnotation))
                    .add(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(radioButtonAnnotationObject))
                    .add(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(radioButtonPlot))
                    .add(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(radioButtonPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 77, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(54, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jLabel3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(radioButtonPanel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(radioButtonAnnotation)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(radioButtonAnnotationObject)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(radioButtonPlot)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel4.setText("Location");

        cornerSpecComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "LowerLeft", "LowerRight", "UpperRight", "UpperLeft" }));

        jLabel1.setText("Object Type");

        jLabel5.setText("Object Name");

        jLabel6.setText("Plot Title");

        jLabel7.setText("X-Title");

        XTitleTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                XTitleTextFieldActionPerformed(evt);
            }
        });

        jLabel8.setText("Y-title");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(outputLabel)
                    .add(jLabel2)
                    .add(jLabel4))
                .add(8, 8, 8)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, htmlOutputTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, quantitySpecificationTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
                    .add(cornerSpecComboBox, 0, 167, Short.MAX_VALUE))
                .addContainerGap())
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(jLabel5)
                    .add(jLabel6)
                    .add(jLabel7)
                    .add(jLabel8))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(objectNameTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
                    .add(objectTypeTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
                    .add(plotTitleTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
                    .add(XTitleTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
                    .add(YTitleTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(15, 15, 15)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(outputLabel)
                    .add(htmlOutputTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(quantitySpecificationTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cornerSpecComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel4))
                .add(20, 20, 20)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(objectTypeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(objectNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel5))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel6)
                    .add(plotTitleTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel7)
                    .add(XTitleTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel8)
                    .add(YTitleTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void XTitleTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_XTitleTextFieldActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_XTitleTextFieldActionPerformed

    private void radioButtonPlotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonPlotActionPerformed
        mode=3;
        outputDescription = outputDescription4;
        updateAvailability();
// TODO add your handling code here:
    }//GEN-LAST:event_radioButtonPlotActionPerformed

    private void radioButtonAnnotationObjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonAnnotationObjectActionPerformed
        mode=2;
        outputDescription = outputDescription3;
        updateAvailability();
// TODO add your handling code here:
    }//GEN-LAST:event_radioButtonAnnotationObjectActionPerformed

    private void radioButtonAnnotationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonAnnotationActionPerformed
        mode=1;
        outputDescription = outputDescription2;
        updateAvailability();
// TODO add your handling code here:
    }//GEN-LAST:event_radioButtonAnnotationActionPerformed

    private void radioButtonPanelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonPanelActionPerformed
// TODO add your handling code here:
        mode=0;
        outputDescription = outputDescription1;
        updateAvailability();
    }//GEN-LAST:event_radioButtonPanelActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField XTitleTextField;
    private javax.swing.JTextField YTitleTextField;
    private javax.swing.JComboBox cornerSpecComboBox;
    private javax.swing.JTextField htmlOutputTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField objectNameTextField;
    private javax.swing.JTextField objectTypeTextField;
    private javax.swing.ButtonGroup outputChannelButtonGroup;
    private javax.swing.JLabel outputLabel;
    private javax.swing.JTextField plotTitleTextField;
    private javax.swing.JTextField quantitySpecificationTextField;
    private javax.swing.JRadioButton radioButtonAnnotation;
    private javax.swing.JRadioButton radioButtonAnnotationObject;
    private javax.swing.JRadioButton radioButtonPanel;
    private javax.swing.JRadioButton radioButtonPlot;
    // End of variables declaration//GEN-END:variables

    void updateAvailability() {
        cornerSpecComboBox.setEnabled(mode==1);
        htmlOutputTextField.setEnabled(mode!=3);
        outputLabel.setText((mode==0)?"Html template":"Text template");
        objectTypeTextField.setEnabled(mode==2);
        objectNameTextField.setEnabled(mode==2);
        plotTitleTextField.setEnabled(mode==3);
        XTitleTextField.setEnabled(mode==3);
        YTitleTextField.setEnabled(mode==3);
    }

    private void setMode(LabOutput outputToEdit) {
        if (outputToEdit instanceof LabOutputTextToPanel){ 
            mode = 0; outputDescription1 = (LabOutputTextToPanel) outputToEdit;
            radioButtonPanel.setSelected(true);
        }
        else if (outputToEdit instanceof LabOutputTextToWindow) {
            mode = 1;  outputDescription2 = (LabOutputTextToWindow) outputToEdit;
            radioButtonAnnotation.setSelected(true);
        }
        else if (outputToEdit instanceof LabOutputTextToObject){ 
            mode = 2;  outputDescription3 = (LabOutputTextToObject) outputToEdit;
            radioButtonAnnotationObject.setSelected(true);
        }
        else {
            mode = 3;  outputDescription4 = (LabOutputPlot) outputToEdit;
            radioButtonPlot.setSelected(true);
        }
        
    }

    private void updateTextFields() {
        quantitySpecificationTextField.setText(outputDescription.getQuantitySpecfication());
        // Mode specific
        switch(mode){
            case 0:
                htmlOutputTextField.setText(outputDescription1.getHtmlTemplate());
                break;
            case 1:
                htmlOutputTextField.setText(outputDescription2.getTextTemplate());
                cornerSpecComboBox.setSelectedItem(outputDescription2.getLocation());
                break;
            case 2:
                htmlOutputTextField.setText(outputDescription3.getTextTemplate());
                objectTypeTextField.setText(outputDescription3.getOpenSimType());
                objectNameTextField.setText(outputDescription3.getObjectName());
                break;
            case 3:
                plotTitleTextField.setText(outputDescription4.getPlotTitle());
                XTitleTextField.setText(outputDescription4.getXAxisTitle());
                YTitleTextField.setText(outputDescription4.getYAxisTitle());
                break;
        }
    }
}
