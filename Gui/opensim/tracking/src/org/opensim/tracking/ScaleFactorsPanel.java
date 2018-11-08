/* -------------------------------------------------------------------------- *
 * OpenSim: ScaleFactorsPanel.java                                            *
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
 * IKMarkerTaskPanel.java
 *
 * Created on July 4, 2007, 1:39 PM
 */

package org.opensim.tracking;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.opensim.modeling.MeasurementSet;
import org.opensim.modeling.Model;

/**
 *
 * @author  erang
 */
public class ScaleFactorsPanel extends javax.swing.JPanel implements Observer {

   private ScaleToolModel scaleToolModel;
   private ScaleFactorsTableModel tableModel = null;
   private JComboBox[] measurementXYZ;
   private JTextField[] manualScaleXYZ;
   private boolean internalChange = false;
   private Dialog measurementSetDialog;
   private int[] selectedRows = new int[]{};
   private NumberFormat numFormat = NumberFormat.getInstance();

   /** Creates new form ScaleFactorsPanel */
   public ScaleFactorsPanel(ScaleToolModel scaleToolModel, Dialog measurementSetDialog) {
      this.scaleToolModel = scaleToolModel;
      this.measurementSetDialog = measurementSetDialog;

      if (numFormat instanceof DecimalFormat) {
        ((DecimalFormat) numFormat).applyPattern("###0.#########");
      }

      initComponents();
      jTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

      measurementXYZ = new JComboBox[]{measurementX, measurementY, measurementZ};
      manualScaleXYZ = new JTextField[]{manualScaleX, manualScaleY, manualScaleZ};

      Model model = scaleToolModel.getUnscaledModel();
      tableModel = new ScaleFactorsTableModel(scaleToolModel);
      jTable.setModel(tableModel);
      jTable.setDefaultRenderer(BodyScaleFactors.class, new BodyScaleFactorsCellRenderer(scaleToolModel));
      jTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent event) {
            if(event.getValueIsAdjusting()) return;
            tableSelectionChanged();
         }
      });

      scaleToolModel.addObserver(this);

      measurementSetChanged();
      updatePanel();
   }

   // Use these rather than directly calling jTable.getSelectedRows() so that if 
   // changing selected rows causes a focus lost event on a text field the updated text
   // field value applies to the right (old) row(s) selected
   private int[] getSelectedRows() { return selectedRows; }
   private int getSelectedRowCount() { return selectedRows.length; }

   public void update(Observable observable, Object obj) {
      ScaleToolModel.Operation op = (ScaleToolModel.Operation)obj;
      if(op==ScaleToolModel.Operation.MeasurementSetChanged || op==ScaleToolModel.Operation.AllDataChanged) 
         measurementSetChanged();
      updatePanel();
   }

   private void tableSelectionChanged() {
      selectedRows = jTable.getSelectedRows().clone();
      updatePanel();
   }

   private void updatePanel() {
      // Base enabled settings (everything on or off depending on whether we have any selection)
      // Everything in this panel except for the scroll pane containing the table and the edit measurement set button should be disabled if there is no selection
      boolean enabled = (getSelectedRowCount() > 0);
      for(Component comp : getComponents()) 
         if(comp!=jScrollPane1 && comp!=editMeasurementSetButton) comp.setEnabled(enabled);

      updateRadioButtons();
      updateMeasurementSelections();
      updateManualScaleFields();
      updateUniformIndicators(true,true);
   }

   private void measurementSetChanged() {
      // Update combo boxes
      MeasurementSet measurementSet = scaleToolModel.getScaleTool().getModelScaler().getMeasurementSet();
      internalChange = true;
      for(JComboBox combo : measurementXYZ) {
         combo.removeAllItems();
         combo.addItem(ScaleFactorsTableModel.unassignedMeasurement);
         for(int i=0; i<measurementSet.getSize(); i++) combo.addItem(measurementSet.get(i).getName());
      }
      internalChange = false;
      updateMeasurementSelections();
   }
   private void updateMeasurementSelections() {
      internalChange = true;
      if(getSelectedRowCount()==0) {
         for(JComboBox combo : measurementXYZ) combo.setSelectedIndex(-1);
         measurementUniformCheckBox.setSelected(false);
      } else {
         for(int i=0; i<3; i++) {
            if(tableModel.isSameMeasurement(getSelectedRows(),i)) {
               int measurement = tableModel.getMeasurement(getSelectedRows()[0],i);
               // Measurement should be a valid index, unless there's been an underlying change to the measurement set and
               // the panel is updating before measurementSetChanged is called (which can happen) -- in this case we
               // set the selected index to -1, and assume that eventually the panel will re-update with the right selection.
               if(measurement+1 < measurementXYZ[i].getItemCount()) measurementXYZ[i].setSelectedIndex(measurement+1);
               else measurementXYZ[i].setSelectedIndex(-1);
            } else measurementXYZ[i].setSelectedIndex(-1);
         }
         boolean uniform = measurementXYZ[0].getSelectedIndex()>=0 &&
                           measurementXYZ[0].getSelectedIndex()==measurementXYZ[1].getSelectedIndex() && 
                           measurementXYZ[0].getSelectedIndex()==measurementXYZ[2].getSelectedIndex();
         measurementUniformCheckBox.setSelected(uniform);
      }
      internalChange = false;
   }
   private void updateManualScaleFields() {
      internalChange = true;
      if(getSelectedRowCount()==0) {
         for(JTextField field : manualScaleXYZ) field.setText("");
         manualScaleUniformCheckBox.setSelected(false);
      } else {
         for(int i=0; i<3; i++) {
            if(tableModel.isSameManualScale(getSelectedRows(),i)) {
               double manualScale = tableModel.getManualScale(getSelectedRows()[0],i);
               manualScaleXYZ[i].setText(numFormat.format(manualScale));
            } else manualScaleXYZ[i].setText("");
         }
         boolean uniform = manualScaleXYZ[0].getText().equals(manualScaleXYZ[1].getText()) && 
                           manualScaleXYZ[0].getText().equals(manualScaleXYZ[2].getText());
         manualScaleUniformCheckBox.setSelected(uniform);
      }
      internalChange = false;
   }
   private void updateRadioButtons() {
      if(getSelectedRowCount()==0) {
         buttonGroup1.setSelected(useNoneRadioButton.getModel(),true);
      } else {
         if(tableModel.isSameUseManualScale(getSelectedRows())) {
            if(tableModel.getUseManualScale(getSelectedRows()[0]))
               buttonGroup1.setSelected(useManualScaleRadioButton.getModel(),true);
            else
               buttonGroup1.setSelected(useMeasurementRadioButton.getModel(),true);
         } else {
            buttonGroup1.setSelected(useNoneRadioButton.getModel(),true);
         }
      }
      // update enabled
      if(buttonGroup1.getSelection()!=useManualScaleRadioButton.getModel()) {
         for(Component comp : new Component[]{manualScaleX,manualScaleY,manualScaleZ,manualScaleUniformCheckBox,resetToMeasurementButton})
            comp.setEnabled(false);
      }
      if(buttonGroup1.getSelection()!=useMeasurementRadioButton.getModel()) {
         for(Component comp : new Component[]{measurementX,measurementY,measurementZ,measurementUniformCheckBox})
            comp.setEnabled(false);
      }
   }
   private void updateUniformIndicators(boolean measurement, boolean manualScale) {
      // Uniform measurement
      if(measurement) {
         if(measurementUniformCheckBox.isSelected()) {
            equalsLabel1.setForeground(Color.black);
            equalsLabel2.setForeground(Color.black);
         } else {
            equalsLabel1.setForeground(equalsLabel1.getBackground());
            equalsLabel2.setForeground(equalsLabel2.getBackground());
         }
      }
      // Uniform manual scales
      if(manualScale) {
         if(manualScaleUniformCheckBox.isSelected()) {
            equalsLabel3.setForeground(Color.black);
            equalsLabel4.setForeground(Color.black);
         } else {
            equalsLabel3.setForeground(equalsLabel3.getBackground());
            equalsLabel4.setForeground(equalsLabel4.getBackground());
         }
      }
   }

   //////////////////////////////////////////////////////////////////////////
   // GUI stuff
   //////////////////////////////////////////////////////////////////////////
   
   /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        useNoneRadioButton = new javax.swing.JRadioButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable = new javax.swing.JTable();
        measurementUniformCheckBox = new javax.swing.JCheckBox();
        measurementX = new javax.swing.JComboBox();
        measurementY = new javax.swing.JComboBox();
        measurementZ = new javax.swing.JComboBox();
        equalsLabel1 = new javax.swing.JLabel();
        equalsLabel2 = new javax.swing.JLabel();
        editMeasurementSetButton = new javax.swing.JButton();
        manualScaleX = new javax.swing.JTextField();
        manualScaleY = new javax.swing.JTextField();
        manualScaleZ = new javax.swing.JTextField();
        equalsLabel3 = new javax.swing.JLabel();
        equalsLabel4 = new javax.swing.JLabel();
        manualScaleUniformCheckBox = new javax.swing.JCheckBox();
        useMeasurementRadioButton = new javax.swing.JRadioButton();
        useManualScaleRadioButton = new javax.swing.JRadioButton();
        resetToMeasurementButton = new javax.swing.JButton();

        buttonGroup1.add(useNoneRadioButton);
        useNoneRadioButton.setSelected(true);
        useNoneRadioButton.setText("jRadioButton1");
        useNoneRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        useNoneRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null}
            },
            new String [] {
                "Title 1"
            }
        ));
        jScrollPane1.setViewportView(jTable);

        measurementUniformCheckBox.setText("Uniform");
        measurementUniformCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        measurementUniformCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        measurementUniformCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                measurementUniformCheckBoxActionPerformed(evt);
            }
        });

        measurementX.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        measurementX.setToolTipText("X axis measurement");
        measurementX.setMinimumSize(new java.awt.Dimension(6, 20));
        measurementX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                measurementXActionPerformed(evt);
            }
        });

        measurementY.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        measurementY.setToolTipText("Y axis measurement");
        measurementY.setMinimumSize(new java.awt.Dimension(6, 20));
        measurementY.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                measurementYActionPerformed(evt);
            }
        });

        measurementZ.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        measurementZ.setToolTipText("Z axis measurement");
        measurementZ.setMinimumSize(new java.awt.Dimension(6, 20));
        measurementZ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                measurementZActionPerformed(evt);
            }
        });

        equalsLabel1.setText("=");

        equalsLabel2.setText("=");

        editMeasurementSetButton.setText("Edit Measurement Set");
        editMeasurementSetButton.setMinimumSize(new java.awt.Dimension(6, 23));
        editMeasurementSetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editMeasurementSetButtonActionPerformed(evt);
            }
        });

        manualScaleX.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        manualScaleX.setToolTipText("X axis scale factor");
        manualScaleX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manualScaleXActionPerformed(evt);
            }
        });
        manualScaleX.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                manualScaleXFocusLost(evt);
            }
        });

        manualScaleY.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        manualScaleY.setToolTipText("Y axis scale factor");
        manualScaleY.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manualScaleYActionPerformed(evt);
            }
        });
        manualScaleY.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                manualScaleYFocusLost(evt);
            }
        });

        manualScaleZ.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        manualScaleZ.setToolTipText("Z axis scale factor");
        manualScaleZ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manualScaleZActionPerformed(evt);
            }
        });
        manualScaleZ.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                manualScaleZFocusLost(evt);
            }
        });

        equalsLabel3.setText("=");

        equalsLabel4.setText("=");

        manualScaleUniformCheckBox.setText("Uniform");
        manualScaleUniformCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        manualScaleUniformCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        manualScaleUniformCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manualScaleUniformCheckBoxActionPerformed(evt);
            }
        });

        buttonGroup1.add(useMeasurementRadioButton);
        useMeasurementRadioButton.setText("Use measurements");
        useMeasurementRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        useMeasurementRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        useMeasurementRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useMeasurementRadioButtonActionPerformed(evt);
            }
        });

        buttonGroup1.add(useManualScaleRadioButton);
        useManualScaleRadioButton.setText("Use manual scales");
        useManualScaleRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        useManualScaleRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        useManualScaleRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useManualScaleRadioButtonActionPerformed(evt);
            }
        });

        resetToMeasurementButton.setText("Reset to Measurement");
        resetToMeasurementButton.setMinimumSize(new java.awt.Dimension(6, 23));
        resetToMeasurementButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetToMeasurementButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 802, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(useManualScaleRadioButton)
                            .add(useMeasurementRadioButton))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(manualScaleX, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                            .add(measurementX, 0, 149, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(equalsLabel1)
                            .add(equalsLabel3))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(measurementY, 0, 149, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(equalsLabel2)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(measurementZ, 0, 149, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(measurementUniformCheckBox))
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .add(manualScaleY, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(equalsLabel4)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(manualScaleZ, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(manualScaleUniformCheckBox)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(editMeasurementSetButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                            .add(resetToMeasurementButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {editMeasurementSetButton, resetToMeasurementButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(measurementUniformCheckBox)
                    .add(measurementZ, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(measurementY, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(equalsLabel1)
                    .add(equalsLabel2)
                    .add(measurementX, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(useMeasurementRadioButton)
                    .add(editMeasurementSetButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(manualScaleUniformCheckBox)
                    .add(equalsLabel4)
                    .add(manualScaleY, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(equalsLabel3)
                    .add(manualScaleX, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(useManualScaleRadioButton)
                    .add(resetToMeasurementButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(manualScaleZ, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

   //------------------------------------------------------------------------
   // Radio buttons
   //------------------------------------------------------------------------

   private void useManualScaleRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useManualScaleRadioButtonActionPerformed
      for(int row : getSelectedRows())
         scaleToolModel.getBodySetScaleFactors().get(row).setUseManualScale(true);
      scaleToolModel.bodySetScaleFactorsModified();
   }//GEN-LAST:event_useManualScaleRadioButtonActionPerformed

   private void useMeasurementRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useMeasurementRadioButtonActionPerformed
      for(int row : getSelectedRows())
         scaleToolModel.getBodySetScaleFactors().get(row).setUseManualScale(false);
      scaleToolModel.bodySetScaleFactorsModified();
   }//GEN-LAST:event_useMeasurementRadioButtonActionPerformed

   //------------------------------------------------------------------------
   // Manual scale
   //------------------------------------------------------------------------

   private void manualScaleUniformCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manualScaleUniformCheckBoxActionPerformed
      if(manualScaleUniformCheckBox.isSelected()) {
         synchronizeManualScales(0); // TODO: print warning if this is a lossy operation
         scaleToolModel.bodySetScaleFactorsModified();
      }
      updateUniformIndicators(false,true);
   }//GEN-LAST:event_manualScaleUniformCheckBoxActionPerformed

   private void setManualScalesFromTextField(int axis) {
      try {
         double scale = Double.valueOf(manualScaleXYZ[axis].getText());
         for(int row : getSelectedRows())
            scaleToolModel.getBodySetScaleFactors().get(row).manualScales[axis] = scale;
      } catch (NumberFormatException ex) {
         // If not a valid double, skip it
      }
   }

   private void synchronizeManualScales(int fromAxis) {
      internalChange=true;
      for(int i=0; i<3; i++) if(i!=fromAxis) manualScaleXYZ[i].setText(manualScaleXYZ[fromAxis].getText());
      internalChange=false;
      for(int axis=0; axis<3; axis++) setManualScalesFromTextField(axis);
   }

   private void manualScaleZFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_manualScaleZFocusLost
      if(!evt.isTemporary()) manualScaleXYZActionPerformed(2);
   }//GEN-LAST:event_manualScaleZFocusLost

   private void manualScaleZActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manualScaleZActionPerformed
      manualScaleXYZActionPerformed(2);
   }//GEN-LAST:event_manualScaleZActionPerformed

   private void manualScaleYFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_manualScaleYFocusLost
      if(!evt.isTemporary()) manualScaleXYZActionPerformed(1);
   }//GEN-LAST:event_manualScaleYFocusLost

   private void manualScaleYActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manualScaleYActionPerformed
      manualScaleXYZActionPerformed(1);
   }//GEN-LAST:event_manualScaleYActionPerformed

   private void manualScaleXFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_manualScaleXFocusLost
      if(!evt.isTemporary()) manualScaleXYZActionPerformed(0);
   }//GEN-LAST:event_manualScaleXFocusLost

   private void manualScaleXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manualScaleXActionPerformed
      manualScaleXYZActionPerformed(0);
   }//GEN-LAST:event_manualScaleXActionPerformed

   private void manualScaleXYZActionPerformed(int axis) {
      if(manualScaleUniformCheckBox.isSelected()) synchronizeManualScales(axis);
      else setManualScalesFromTextField(axis);
      scaleToolModel.bodySetScaleFactorsModified();
   }

   private void resetToMeasurementButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetToMeasurementButtonActionPerformed
      tableModel.copyMeasurementValueToManualScales(getSelectedRows());
      scaleToolModel.bodySetScaleFactorsModified();
   }//GEN-LAST:event_resetToMeasurementButtonActionPerformed

   //------------------------------------------------------------------------
   // Measurements
   //------------------------------------------------------------------------

   private void measurementUniformCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_measurementUniformCheckBoxActionPerformed
      if(measurementUniformCheckBox.isSelected()) {
         synchronizeMeasurements(0); // TODO: print warning if this is a lossy operation
         scaleToolModel.bodySetScaleFactorsModified();
      }
      updateUniformIndicators(true,false);
   }//GEN-LAST:event_measurementUniformCheckBoxActionPerformed

   private void setMeasurementsFromComboBox(int axis) {
      int measurement = measurementXYZ[axis].getSelectedIndex()-1;
      for(int row : getSelectedRows())
         scaleToolModel.getBodySetScaleFactors().get(row).measurements[axis] = measurement;
   }

   private void synchronizeMeasurements(int fromAxis) {
      internalChange=true;
      for(int i=0; i<3; i++) if(i!=fromAxis) measurementXYZ[i].setSelectedIndex(measurementXYZ[fromAxis].getSelectedIndex());
      internalChange=false;
      for(int axis=0; axis<3; axis++) setMeasurementsFromComboBox(axis);
   }

   private void measurementZActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_measurementZActionPerformed
      if(!internalChange) measurementXYZActionPerformed(2);
   }//GEN-LAST:event_measurementZActionPerformed

   private void measurementYActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_measurementYActionPerformed
      if(!internalChange) measurementXYZActionPerformed(1);
   }//GEN-LAST:event_measurementYActionPerformed

   private void measurementXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_measurementXActionPerformed
      if(!internalChange) measurementXYZActionPerformed(0);
   }//GEN-LAST:event_measurementXActionPerformed
   
   private void measurementXYZActionPerformed(int axis) {
      if(measurementUniformCheckBox.isSelected()) synchronizeMeasurements(axis);
      else setMeasurementsFromComboBox(axis);
      scaleToolModel.bodySetScaleFactorsModified();
   }
   
   private void editMeasurementSetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editMeasurementSetButtonActionPerformed
      measurementSetDialog.setVisible(true);
   }//GEN-LAST:event_editMeasurementSetButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton editMeasurementSetButton;
    private javax.swing.JLabel equalsLabel1;
    private javax.swing.JLabel equalsLabel2;
    private javax.swing.JLabel equalsLabel3;
    private javax.swing.JLabel equalsLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable;
    private javax.swing.JCheckBox manualScaleUniformCheckBox;
    private javax.swing.JTextField manualScaleX;
    private javax.swing.JTextField manualScaleY;
    private javax.swing.JTextField manualScaleZ;
    private javax.swing.JCheckBox measurementUniformCheckBox;
    private javax.swing.JComboBox measurementX;
    private javax.swing.JComboBox measurementY;
    private javax.swing.JComboBox measurementZ;
    private javax.swing.JButton resetToMeasurementButton;
    private javax.swing.JRadioButton useManualScaleRadioButton;
    private javax.swing.JRadioButton useMeasurementRadioButton;
    private javax.swing.JRadioButton useNoneRadioButton;
    // End of variables declaration//GEN-END:variables
}
