/* -------------------------------------------------------------------------- *
 * OpenSim: IKTaskSetPanel.java                                               *
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

import java.awt.Component;
import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

/**
 *
 * @author  erang
 */
public class IKTaskSetPanel extends javax.swing.JPanel implements ListSelectionListener, TableModelListener {
   private final static int enabledColumnWidth = 60;

   IKCommonModel ikCommonModel;

   IKTasksTableModel ikMarkerTasksTableModel;
   IKTasksTableModel ikCoordinateTasksTableModel;

   JTable activeTable = null; // the one which was the source of the last selection event (the one whose data is displayed in the panel's fields)
   private int[] selectedRows = new int[]{}; // Selected rows of active table
   private NumberFormat numFormat = NumberFormat.getInstance();

   /** Creates new form IKCoordinateTaskPanel */
   public IKTaskSetPanel(IKCommonModel ikCommonModel) {
      this.ikCommonModel = ikCommonModel;

      ikMarkerTasksTableModel = new IKTasksTableModel(ikCommonModel.getIKMarkerTasksModel(), "Marker");
      ikCoordinateTasksTableModel = new IKTasksTableModel(ikCommonModel.getIKCoordinateTasksModel(), "Coordinate");

      if (numFormat instanceof DecimalFormat) {
        ((DecimalFormat) numFormat).applyPattern("###0.#########");
      }

      initComponents();

      ikMarkerTasksTable.setModel(ikMarkerTasksTableModel);
      ikCoordinateTasksTable.setModel(ikCoordinateTasksTableModel);

      for(JTable table : new JTable[]{ikMarkerTasksTable, ikCoordinateTasksTable}) {
         table.getSelectionModel().addListSelectionListener(this);
         table.getModel().addTableModelListener(this);

         table.setDefaultRenderer(IKTasksNameCell.class, new IKTasksNameCellRenderer());
         table.setDefaultRenderer(IKTasksValueCell.class, new IKTasksValueCellRenderer());
         table.setDefaultRenderer(IKTasksWeightCell.class, new IKTasksWeightCellRenderer());

         table.getColumnModel().getColumn(0).setPreferredWidth(enabledColumnWidth);
         table.getColumnModel().getColumn(0).setMinWidth(enabledColumnWidth);
         table.getColumnModel().getColumn(0).setMaxWidth(enabledColumnWidth);
         table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
      }

      updatePanel();
   }

   private IKTasksTableModel activeTableModel() { return activeTable!=null ? (IKTasksTableModel)activeTable.getModel() : null; }
   private IKTasksModel activeModel() { return activeTableModel()!=null ? activeTableModel().getIKTasksModel() : null; }

   //------------------------------------------------------------------------
   // Table data and selection handlers
   //------------------------------------------------------------------------
   public void tableChanged(TableModelEvent evt) {
      updatePanel();
   }

   // Use these rather than directly calling jTable.getSelectedRows() so that if 
   // changing selected rows causes a focus lost event on a text field the updated text
   // field value applies to the right (old) row(s) selected
   private int[] getSelectedRows() { return selectedRows; }
   private int getSelectedRowCount() { return selectedRows.length; }

   public void valueChanged(ListSelectionEvent event) {
      if(event.getValueIsAdjusting()) return;

      // Update activeTable
      JTable otherTable = null;
      if(event.getSource()==ikMarkerTasksTable.getSelectionModel()) { activeTable = ikMarkerTasksTable; otherTable = ikCoordinateTasksTable; }
      else if(event.getSource()==ikCoordinateTasksTable.getSelectionModel()) { activeTable = ikCoordinateTasksTable; otherTable = ikMarkerTasksTable; }
      // Make sure only one table is selected at a time
      if(otherTable.getSelectedRowCount()>0) {
         otherTable.getSelectionModel().removeListSelectionListener(this);
         otherTable.getSelectionModel().clearSelection();
         otherTable.getSelectionModel().addListSelectionListener(this);
      }

      // Update copy of selected rows
      selectedRows = activeTable.getSelectedRows().clone();

      // TODO: if pending changes, might want to apply them to old selection
      updatePanel();
   }

   public void updatePanel() {
      updateSelectionCheckBoxes();
      updateValueComponents();
      updateWeightTextField();
   }

   private void updateSelectionCheckBoxes() {
      enableSelectedCheckBox.setSelected(false);
      disableSelectedCheckBox.setSelected(false);
      enableSelectedCheckBox.setEnabled(false);
      disableSelectedCheckBox.setEnabled(false);
      if(activeTable!=null && getSelectedRowCount()>0) {
         boolean sameEnabled = activeTableModel().isSameEnabled(getSelectedRows());
         if(sameEnabled) {
            if(activeModel().getEnabled(getSelectedRows()[0])) { // all are enabled
               enableSelectedCheckBox.setSelected(true);
               disableSelectedCheckBox.setEnabled(true);
            } else {
               disableSelectedCheckBox.setSelected(true);
               enableSelectedCheckBox.setEnabled(true);
            }
         } else {
            enableSelectedCheckBox.setEnabled(true);
            disableSelectedCheckBox.setEnabled(true);
         }
      }
   }

   private void updateWeightTextField() {
      weightTextField.setText("");
      if(activeTable!=null && getSelectedRowCount() > 0) {
         boolean sameWeight = activeTableModel().isSameWeight(getSelectedRows());
         if(sameWeight) {
            if(activeModel().isLocked(getSelectedRows()[0])) {
               weightTextField.setText(IKTasksTableModel.LockedStr); // and keep disabled
               weightTextField.setEnabled(false);
            } else {
               weightTextField.setText(numFormat.format(activeModel().getWeight(getSelectedRows()[0])));
               weightTextField.setEnabled(true);
            }
         } else {
            weightTextField.setEnabled(true);
         }
      } else {
         weightTextField.setEnabled(false);
      }
   }

   private void updateValueComponents() {
      if(activeTable==null || getSelectedRowCount()==0) {
         fromFileTextField.setText("");
         defaultValueTextField.setText("");
         manualValueTextField.setText("");
         buttonGroup1.setSelected(noValueRadioButton.getModel(),true);
         for(Component comp : new Component[]{fromFileRadioButton, defaultValueRadioButton, manualValueRadioButton, fromFileTextField, defaultValueTextField, manualValueTextField})
            comp.setEnabled(false);
      } else {
         boolean sameValueType = activeTableModel().isSameValueType(getSelectedRows());
         boolean sameDefaultValue = activeTableModel().isSameDefaultValue(getSelectedRows());
         boolean sameManualValue = activeTableModel().isSameManualValue(getSelectedRows());

         Component enabledTextField = null;
         if(sameValueType) {
            switch(activeModel().getValueType(getSelectedRows()[0])) {
               case FromFile:
                  buttonGroup1.setSelected(fromFileRadioButton.getModel(),true); 
                  enabledTextField = fromFileTextField;
                  break;
               case DefaultValue: 
                  buttonGroup1.setSelected(defaultValueRadioButton.getModel(),true); 
                  enabledTextField = defaultValueTextField;
                  break;
               case ManualValue: 
                  buttonGroup1.setSelected(manualValueRadioButton.getModel(),true); 
                  enabledTextField = manualValueTextField;
                  break;
            }
         } else {
            buttonGroup1.setSelected(noValueRadioButton.getModel(),true);
         }
         for(Component comp : new Component[]{fromFileTextField, defaultValueTextField, manualValueTextField})
            comp.setEnabled(comp==enabledTextField);

         // TODO: update as file changes and depending on which table
         String fileName = (activeTable==ikMarkerTasksTable) ? ikCommonModel.getMarkerDataFileName() : ikCommonModel.getCoordinateDataFileName();
         fromFileTextField.setText((new File(fileName)).getName());

         if(activeTable==ikCoordinateTasksTable) {
            fromFileRadioButton.setEnabled(true);
            defaultValueRadioButton.setEnabled(true);
            manualValueRadioButton.setEnabled(true);

            if(sameDefaultValue) defaultValueTextField.setText(numFormat.format(activeModel().getDefaultValue(getSelectedRows()[0])));
            else defaultValueTextField.setText("Different");
            if(sameManualValue) manualValueTextField.setText(numFormat.format(activeModel().getManualValue(getSelectedRows()[0])));
            else manualValueTextField.setText("");
         } else {
            fromFileRadioButton.setEnabled(true);
            defaultValueRadioButton.setEnabled(false);
            manualValueRadioButton.setEnabled(false);

            defaultValueTextField.setText("");
            manualValueTextField.setText("");
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
        noValueRadioButton = new javax.swing.JRadioButton();
        jPanel1 = new javax.swing.JPanel();
        weightTextField = new javax.swing.JTextField();
        enableSelectedCheckBox = new javax.swing.JCheckBox();
        manualValueRadioButton = new javax.swing.JRadioButton();
        manualValueTextField = new javax.swing.JTextField();
        fromFileRadioButton = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        defaultValueRadioButton = new javax.swing.JRadioButton();
        defaultValueTextField = new javax.swing.JTextField();
        disableSelectedCheckBox = new javax.swing.JCheckBox();
        fromFileTextField = new javax.swing.JTextField();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        ikMarkerTasksTable = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        ikCoordinateTasksTable = new javax.swing.JTable();

        buttonGroup1.add(noValueRadioButton);
        noValueRadioButton.setText("jRadioButton1");
        noValueRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        noValueRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

        weightTextField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        weightTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                weightTextFieldActionPerformed(evt);
            }
        });
        weightTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                weightTextFieldFocusLost(evt);
            }
        });

        enableSelectedCheckBox.setText("Enable all selected");
        enableSelectedCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        enableSelectedCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        enableSelectedCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enableSelectedCheckBoxActionPerformed(evt);
            }
        });

        buttonGroup1.add(manualValueRadioButton);
        manualValueRadioButton.setText("Manual value");
        manualValueRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        manualValueRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        manualValueRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manualValueRadioButtonActionPerformed(evt);
            }
        });

        manualValueTextField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        manualValueTextField.setText("jTextField3");
        manualValueTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manualValueTextFieldActionPerformed(evt);
            }
        });
        manualValueTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                manualValueTextFieldFocusLost(evt);
            }
        });

        buttonGroup1.add(fromFileRadioButton);
        fromFileRadioButton.setText("From file");
        fromFileRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        fromFileRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        fromFileRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fromFileRadioButtonActionPerformed(evt);
            }
        });

        jLabel2.setText("Value");

        jLabel1.setText("Weight");

        buttonGroup1.add(defaultValueRadioButton);
        defaultValueRadioButton.setText("Default value");
        defaultValueRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        defaultValueRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        defaultValueRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                defaultValueRadioButtonActionPerformed(evt);
            }
        });

        defaultValueTextField.setEditable(false);
        defaultValueTextField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        defaultValueTextField.setText("jTextField2");

        disableSelectedCheckBox.setText("Disable all selected");
        disableSelectedCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        disableSelectedCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        disableSelectedCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                disableSelectedCheckBoxActionPerformed(evt);
            }
        });

        fromFileTextField.setEditable(false);
        fromFileTextField.setText("jTextField1");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(enableSelectedCheckBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 105, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(70, 70, 70)
                        .add(jLabel2))
                    .add(disableSelectedCheckBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 107, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(defaultValueRadioButton)
                    .add(manualValueRadioButton)
                    .add(fromFileRadioButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, fromFileTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 115, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(defaultValueTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(manualValueTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 126, Short.MAX_VALUE)
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(weightTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 105, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel1Layout.linkSize(new java.awt.Component[] {defaultValueTextField, fromFileTextField, manualValueTextField}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(weightTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jLabel1))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(fromFileTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(fromFileRadioButton)
                            .add(jLabel2)
                            .add(enableSelectedCheckBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(defaultValueRadioButton)
                            .add(defaultValueTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(disableSelectedCheckBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(manualValueTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(manualValueRadioButton))))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jSplitPane1.setDividerLocation(70);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        ikMarkerTasksTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(ikMarkerTasksTable);

        jSplitPane1.setLeftComponent(jScrollPane1);

        jScrollPane2.setViewportView(ikCoordinateTasksTable);

        jSplitPane1.setRightComponent(jScrollPane2);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 696, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

   private void manualValueRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manualValueRadioButtonActionPerformed
      activeTableModel().setValueType(getSelectedRows(), IKTasksModel.ValueType.ManualValue);
   }//GEN-LAST:event_manualValueRadioButtonActionPerformed

   private void defaultValueRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_defaultValueRadioButtonActionPerformed
      activeTableModel().setValueType(getSelectedRows(), IKTasksModel.ValueType.DefaultValue);
   }//GEN-LAST:event_defaultValueRadioButtonActionPerformed

   private void fromFileRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fromFileRadioButtonActionPerformed
      activeTableModel().setValueType(getSelectedRows(), IKTasksModel.ValueType.FromFile);
   }//GEN-LAST:event_fromFileRadioButtonActionPerformed

   private void setManualValueFromTextField() {
      try {
         double value = Double.valueOf(manualValueTextField.getText());
         activeTableModel().setManualValue(getSelectedRows(), value);
      } catch (NumberFormatException ex) {
      }
   }

   private void manualValueTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_manualValueTextFieldFocusLost
      if(!evt.isTemporary()) setManualValueFromTextField();
   }//GEN-LAST:event_manualValueTextFieldFocusLost

   private void manualValueTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manualValueTextFieldActionPerformed
      setManualValueFromTextField();
   }//GEN-LAST:event_manualValueTextFieldActionPerformed

   private void setWeightFromTextField() {
      try {
         double weight = Double.valueOf(weightTextField.getText());
         activeTableModel().setWeight(getSelectedRows(), weight);
      } catch (NumberFormatException ex) {
      }
   }

   private void weightTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_weightTextFieldFocusLost
      if(!evt.isTemporary()) setWeightFromTextField();
   }//GEN-LAST:event_weightTextFieldFocusLost

   private void weightTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_weightTextFieldActionPerformed
      setWeightFromTextField();
   }//GEN-LAST:event_weightTextFieldActionPerformed

   private void disableSelectedCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_disableSelectedCheckBoxActionPerformed
      activeTableModel().setEnabled(getSelectedRows(), false);
   }//GEN-LAST:event_disableSelectedCheckBoxActionPerformed

   private void enableSelectedCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enableSelectedCheckBoxActionPerformed
      activeTableModel().setEnabled(getSelectedRows(), true);
   }//GEN-LAST:event_enableSelectedCheckBoxActionPerformed
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JRadioButton defaultValueRadioButton;
    private javax.swing.JTextField defaultValueTextField;
    private javax.swing.JCheckBox disableSelectedCheckBox;
    private javax.swing.JCheckBox enableSelectedCheckBox;
    private javax.swing.JRadioButton fromFileRadioButton;
    private javax.swing.JTextField fromFileTextField;
    private javax.swing.JTable ikCoordinateTasksTable;
    private javax.swing.JTable ikMarkerTasksTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JRadioButton manualValueRadioButton;
    private javax.swing.JTextField manualValueTextField;
    private javax.swing.JRadioButton noValueRadioButton;
    private javax.swing.JTextField weightTextField;
    // End of variables declaration//GEN-END:variables
}
