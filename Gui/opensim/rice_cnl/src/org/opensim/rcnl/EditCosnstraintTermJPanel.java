/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.rcnl;

import java.text.NumberFormat;
import java.text.ParseException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.Exceptions;
import org.opensim.modeling.AbstractProperty;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.PropertyHelper;
import org.opensim.modeling.PropertyStringList;

/**
 *
 * @author Ayman-NMBL
 */
public class EditCosnstraintTermJPanel extends javax.swing.JPanel {

    private OpenSimObject constraintTerm2Edit;
    private boolean initializing=false;
    private TreatmentOptimizationToolModel.Mode mode;
    private String[] componentTypes;
    String componentType;
    private String[] availableComponentNames;
    private ConstraintTermModel constraintTermModel;
    private NumberFormat numFormat = NumberFormat.getInstance();
    private String trackedDataDir, initialGuessDir, osimxFile;

    /**
     * Creates new form EditJointTaskJPanel
     */
    public EditCosnstraintTermJPanel() {
        initComponents();
    }

    EditCosnstraintTermJPanel(OpenSimObject constraintTerm,
                                TreatmentOptimizationToolModel.Mode mode,
                                String trackedDataDir, String initialGuessDir, String osimxFile) {
        constraintTerm2Edit = constraintTerm;
        initializing = true;
        this.mode = mode;
        this.trackedDataDir = trackedDataDir;
        this.initialGuessDir = initialGuessDir;
        this.osimxFile = osimxFile;
        constraintTermModel = new ConstraintTermModel(constraintTerm2Edit, mode);
        AbstractProperty typeProp = constraintTerm2Edit.getPropertyByName("type");
        String saveType = PropertyHelper.getValueString(typeProp);
        initComponents(); 
        jConstraintTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(RCNLConstraintTermsInfo.getConstraintTermTypes(mode)));
        componentTypes = RCNLConstraintTermsInfo.getConstraintTermQuantityTypes(mode);

        jConstraintNameTextField.setText(constraintTerm2Edit.getName());
        jConstraintTypeComboBox.setSelectedItem(saveType);
        jConstraintTypeComboBoxActionPerformed(null);
        AbstractProperty enabledProp = constraintTerm2Edit.getPropertyByName("is_enabled");
        jEnabledCheckBox.setSelected(PropertyHelper.getValueBool(enabledProp));
        initializing = false;
        jTermComponentListTextArea.setText(constraintTermModel.getPropertyComponentList().toString());
        jMaxErrorTextField.setText(String.valueOf(constraintTermModel.getMaxError()));
        jMinErrorTextField.setText(String.valueOf(constraintTermModel.getMinError()));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jEnabledCheckBox = new javax.swing.JCheckBox();
        jComponentListPanel = new javax.swing.JPanel();
        editComonentListButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTermComponentListTextArea = new javax.swing.JTextArea();
        jLabel11 = new javax.swing.JLabel();
        jConstraintTypeComboBox = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jMinErrorTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jMaxErrorTextField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jConstraintNameTextField = new javax.swing.JTextField();
        jComponentTypeTextField = new javax.swing.JTextField();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(EditCosnstraintTermJPanel.class, "EditCosnstraintTermJPanel.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jEnabledCheckBox, org.openide.util.NbBundle.getMessage(EditCosnstraintTermJPanel.class, "EditCosnstraintTermJPanel.jEnabledCheckBox.text")); // NOI18N
        jEnabledCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jEnabledCheckBoxItemStateChanged(evt);
            }
        });
        jEnabledCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jEnabledCheckBoxActionPerformed(evt);
            }
        });

        jComponentListPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(EditCosnstraintTermJPanel.class, "EditCosnstraintTermJPanel.jComponentListPanel.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(editComonentListButton, org.openide.util.NbBundle.getMessage(EditCosnstraintTermJPanel.class, "EditCosnstraintTermJPanel.editComonentListButton.text")); // NOI18N
        editComonentListButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editComonentListButtonActionPerformed(evt);
            }
        });

        jTermComponentListTextArea.setColumns(20);
        jTermComponentListTextArea.setRows(5);
        jScrollPane2.setViewportView(jTermComponentListTextArea);

        javax.swing.GroupLayout jComponentListPanelLayout = new javax.swing.GroupLayout(jComponentListPanel);
        jComponentListPanel.setLayout(jComponentListPanelLayout);
        jComponentListPanelLayout.setHorizontalGroup(
            jComponentListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jComponentListPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 762, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(editComonentListButton)
                .addContainerGap())
        );
        jComponentListPanelLayout.setVerticalGroup(
            jComponentListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jComponentListPanelLayout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(editComonentListButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
        );

        org.openide.awt.Mnemonics.setLocalizedText(jLabel11, org.openide.util.NbBundle.getMessage(EditCosnstraintTermJPanel.class, "EditCosnstraintTermJPanel.jLabel11.text")); // NOI18N

        jConstraintTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "kinetic_consistency", "generalized_speed_tracking", "marker_position_tracking", "inverse_dynamics_load_tracking", "inverse_dynamics_load_minimization", "inverse_dynamics_slope_tracking", "joint_acceleration_minimization", "external_force_tracking", "external_moment_tracking", "muscle_activation_tracking" }));
        jConstraintTypeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jConstraintTypeComboBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(EditCosnstraintTermJPanel.class, "EditCosnstraintTermJPanel.jLabel2.text")); // NOI18N

        jMinErrorTextField.setText(org.openide.util.NbBundle.getMessage(EditCosnstraintTermJPanel.class, "EditCosnstraintTermJPanel.jMinErrorTextField.text")); // NOI18N
        jMinErrorTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jMinErrorTextFieldFocusLost(evt);
            }
        });
        jMinErrorTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMinErrorTextFieldActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(EditCosnstraintTermJPanel.class, "EditCosnstraintTermJPanel.jLabel3.text")); // NOI18N

        jMaxErrorTextField.setText(org.openide.util.NbBundle.getMessage(EditCosnstraintTermJPanel.class, "EditCosnstraintTermJPanel.jMaxErrorTextField.text")); // NOI18N
        jMaxErrorTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jMaxErrorTextFieldFocusLost(evt);
            }
        });
        jMaxErrorTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMaxErrorTextFieldActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(EditCosnstraintTermJPanel.class, "EditCosnstraintTermJPanel.jLabel4.text")); // NOI18N

        jConstraintNameTextField.setText(org.openide.util.NbBundle.getMessage(EditCosnstraintTermJPanel.class, "EditCosnstraintTermJPanel.jConstraintNameTextField.text")); // NOI18N
        jConstraintNameTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jConstraintNameTextFieldFocusLost(evt);
            }
        });
        jConstraintNameTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jConstraintNameTextFieldActionPerformed(evt);
            }
        });

        jComponentTypeTextField.setEditable(false);
        jComponentTypeTextField.setText(org.openide.util.NbBundle.getMessage(EditCosnstraintTermJPanel.class, "EditCosnstraintTermJPanel.jComponentTypeTextField.text")); // NOI18N
        jComponentTypeTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComponentTypeTextFieldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComponentTypeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jEnabledCheckBox)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jConstraintTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jConstraintNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 460, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jMaxErrorTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(128, 128, 128)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jMinErrorTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComponentListPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jConstraintNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jConstraintTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jEnabledCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jComponentTypeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jComponentListPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jMaxErrorTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jMinErrorTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jEnabledCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jEnabledCheckBoxItemStateChanged
        // TODO add your handling code here:
        AbstractProperty enabledProp = constraintTerm2Edit.getPropertyByName("is_enabled");
        PropertyHelper.setValueBool(evt.getStateChange()==1, enabledProp);
    }//GEN-LAST:event_jEnabledCheckBoxItemStateChanged

    private void jEnabledCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jEnabledCheckBoxActionPerformed
        // TODO add your handling code here:
        AbstractProperty enabledProp = constraintTerm2Edit.getPropertyByName("is_enabled");
        PropertyHelper.setValueBool(jEnabledCheckBox.isSelected(), enabledProp);

    }//GEN-LAST:event_jEnabledCheckBoxActionPerformed

    private void editComonentListButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editComonentListButtonActionPerformed
        // TODO add your handling code here:
        String[] names = RCNLCostTermsInfo.getAvailableNamesForComponentType(constraintTermModel.getComponentType(), constraintTermModel.getModel(), 
                this.trackedDataDir, this.initialGuessDir, this.osimxFile);
        PropertyStringList componentListProperty = constraintTermModel.getPropertyComponentList();
        ComponentTableModel ctm = new ComponentTableModel(componentListProperty, names);
        SelectQuantitiesFromListJPanel selectionPanel = new SelectQuantitiesFromListJPanel(ctm);
        DialogDescriptor dlg = new DialogDescriptor(selectionPanel,"Select Components");
        dlg.setModal(true);
        DialogDisplayer.getDefault().createDialog(dlg).setVisible(true);
        Object userInput = dlg.getValue();
        if (((Integer)userInput).compareTo((Integer)DialogDescriptor.OK_OPTION)==0){
            ctm.populateListProperty();
            jTermComponentListTextArea.setText(constraintTermModel.getPropertyComponentList().toString());
            //System.out.println(costTerm2Edit.dump());
        }
    }//GEN-LAST:event_editComonentListButtonActionPerformed

    private void jConstraintTypeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jConstraintTypeComboBoxActionPerformed
        int ndx = jConstraintTypeComboBox.getSelectedIndex();
        componentType = componentTypes[ndx];
        String oldType = jComponentTypeTextField.getText();
        jComponentTypeTextField.setText(componentType);
        if (!oldType.trim().equalsIgnoreCase("") && !oldType.equalsIgnoreCase(componentType)){ // type change so old values not applicable
            constraintTermModel.getPropertyComponentList().clear();
            jTermComponentListTextArea.setText(constraintTermModel.getPropertyComponentList().toString());
        }
        jComponentTypeTextField.setText(componentType);
        AbstractProperty typeProp = constraintTerm2Edit.getPropertyByName("type");
        PropertyHelper.setValueString(jConstraintTypeComboBox.getSelectedItem().toString(), typeProp);

        // based on componentType, populate underlying available componentList, if none then disable
        editComonentListButton.setEnabled(!componentType.equalsIgnoreCase("none"));
        constraintTermModel.setTypeIndex(jConstraintTypeComboBox.getSelectedIndex());        // TODO add your handling code here:
    }//GEN-LAST:event_jConstraintTypeComboBoxActionPerformed

    private void jComponentTypeTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComponentTypeTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComponentTypeTextFieldActionPerformed

    private void jConstraintNameTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jConstraintNameTextFieldActionPerformed
        // TODO add your handling code here:
        constraintTerm2Edit.setName(jConstraintNameTextField.getText());
    }//GEN-LAST:event_jConstraintNameTextFieldActionPerformed

    private void jConstraintNameTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jConstraintNameTextFieldFocusLost
        // TODO add your handling code here:
        jConstraintNameTextFieldActionPerformed(null);
    }//GEN-LAST:event_jConstraintNameTextFieldFocusLost

    private void jMaxErrorTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMaxErrorTextFieldActionPerformed
        // TODO add your handling code here:
        if(jMaxErrorTextField.getText().trim().length()>0) 
            try {
                constraintTermModel.setMaxError(numFormat.parse(jMaxErrorTextField.getText().trim()).doubleValue());
        } catch (ParseException ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_jMaxErrorTextFieldActionPerformed

    private void jMaxErrorTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jMaxErrorTextFieldFocusLost
        // TODO add your handling code here:
        jMaxErrorTextFieldActionPerformed(null);
    }//GEN-LAST:event_jMaxErrorTextFieldFocusLost

    private void jMinErrorTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMinErrorTextFieldActionPerformed
        // TODO add your handling code here:
        if(jMinErrorTextField.getText().trim().length()>0) 
            try {
                constraintTermModel.setMinError(numFormat.parse(jMinErrorTextField.getText().trim()).doubleValue());
        } catch (ParseException ex) {
            Exceptions.printStackTrace(ex);
        }

    }//GEN-LAST:event_jMinErrorTextFieldActionPerformed

    private void jMinErrorTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jMinErrorTextFieldFocusLost
        // TODO add your handling code here:
        jMinErrorTextFieldActionPerformed(null);
    }//GEN-LAST:event_jMinErrorTextFieldFocusLost


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton editComonentListButton;
    private javax.swing.JPanel jComponentListPanel;
    private javax.swing.JTextField jComponentTypeTextField;
    private javax.swing.JTextField jConstraintNameTextField;
    private javax.swing.JComboBox<String> jConstraintTypeComboBox;
    private javax.swing.JCheckBox jEnabledCheckBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTextField jMaxErrorTextField;
    private javax.swing.JTextField jMinErrorTextField;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTermComponentListTextArea;
    // End of variables declaration//GEN-END:variables
    private class ListSelectionHandler implements ListSelectionListener {

        public ListSelectionHandler() {
        }

        @Override
        public void valueChanged(ListSelectionEvent lse) {
            // Disable delete if nothing is selected
            // Enable edit if single selection

            
        }
    }
    
       private class List2SelectionHandler implements ListSelectionListener {

        public List2SelectionHandler() {
        }

        @Override
        public void valueChanged(ListSelectionEvent lse) {
            // Disable delete if nothing is selected
            // Enable edit if single selection
            
        }
    }

}
