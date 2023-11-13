/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.rcnl;

import java.awt.Dialog;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.Exceptions;
import org.opensim.modeling.AbstractProperty;
import org.opensim.modeling.ArrayStr;
import org.opensim.modeling.BodyIterator;
import org.opensim.modeling.BodyList;
import org.opensim.modeling.FrameList;
import org.opensim.modeling.MarkerSet;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.PropertyBoolList;
import org.opensim.modeling.PropertyDoubleList;
import org.opensim.modeling.PropertyHelper;
import org.opensim.modeling.PropertyObjectList;
import org.opensim.modeling.PropertyStringList;
import org.opensim.view.pub.OpenSimDB;

/**
 *
 * @author Ayman-NMBL
 */
public class EditGCPSurfaceJPanel extends javax.swing.JPanel {

    private OpenSimObject gcpContactSurface;
    private NumberFormat numFormat = NumberFormat.getInstance();
    private PropertyBoolList enabledProp;
    private PropertyBoolList leftFootProp;
    private PropertyDoubleList startTimeProp;
    private PropertyDoubleList endTimeProp;
    private PropertyDoubleList beltSpeedProp;
    private PropertyStringList hindfootBodyProp, toeMarkerProp, medialMarkerProp, lateralMarkerProp, heelMarkerProp, midfootMarkerProp ;
    private Model model;

    /**
     * Creates new form EditJointTaskJPanel
     */
    public EditGCPSurfaceJPanel() {
        initComponents();
    }

    EditGCPSurfaceJPanel(OpenSimObject gcpContactSurface) {
        this.gcpContactSurface = gcpContactSurface;
         populatePropertiesFromObject();
         initComponents();
         jGCPSurfaceNameTextField.setText(gcpContactSurface.getName());
         jEnabledCheckBox.setSelected(enabledProp.getValue());
         jLeftFootCheckBox.setSelected(leftFootProp.getValue());
         jStartTimeTextField.setText(String.valueOf(startTimeProp.getValue()));
         jEndTimeTextField.setText(String.valueOf(endTimeProp.getValue()));
         jBeltSpeedTextField.setText(String.valueOf(beltSpeedProp.getValue()));
         model = OpenSimDB.getInstance().getCurrentModel();
         populateComboBoxes();
         toeMarkersComboBox.setSelectedItem(toeMarkerProp.getValue());
         medialMarkersComboBox.setSelectedItem(medialMarkerProp.getValue());
         lateralMarkersComboBox.setSelectedItem(lateralMarkerProp.getValue());
         heelMarkersComboBox.setSelectedItem(heelMarkerProp.getValue());
         midfootMarkersComboBox.setSelectedItem(midfootMarkerProp.getValue());
         HindfootBodiesComboBox.setSelectedItem(hindfootBodyProp.getValue());
    }

    void populatePropertiesFromObject() {
        enabledProp = PropertyBoolList.getAs(gcpContactSurface.getPropertyByName("is_enabled"));
        leftFootProp = PropertyBoolList.getAs(gcpContactSurface.getPropertyByName("is_left_foot"));
        startTimeProp = PropertyDoubleList.getAs(gcpContactSurface.getPropertyByName("start_time"));
        endTimeProp = PropertyDoubleList.getAs(gcpContactSurface.getPropertyByName("end_time"));
        beltSpeedProp = PropertyDoubleList.getAs(gcpContactSurface.getPropertyByName("belt_speed"));
        hindfootBodyProp = PropertyStringList.getAs(gcpContactSurface.getPropertyByName("hindfoot_body"));

        toeMarkerProp = PropertyStringList.getAs(gcpContactSurface.getPropertyByName("toe_marker"));
        medialMarkerProp = PropertyStringList.getAs(gcpContactSurface.getPropertyByName("medial_marker"));
        lateralMarkerProp = PropertyStringList.getAs(gcpContactSurface.getPropertyByName("lateral_marker"));
        heelMarkerProp = PropertyStringList.getAs(gcpContactSurface.getPropertyByName("heel_marker"));
        midfootMarkerProp = PropertyStringList.getAs(gcpContactSurface.getPropertyByName("midfoot_superior_marker"));
        
    }
    
    void populateComboBoxes() {
        BodyList bodies = model.getBodyList();
        BodyIterator body = bodies.begin();
        Vector<String> bNames = new Vector<String>();
        while (!body.equals(bodies.end())) {
            bNames.add(body.getName());
            body.next();
        }
        HindfootBodiesComboBox.setModel(new javax.swing.DefaultComboBoxModel(bNames));
        MarkerSet markers = model.getMarkerSet();
        ArrayStr mNames = new ArrayStr();
        markers.getNames(mNames);
        Vector<String> markerNames = new Vector<String>();
        for(int i=0; i<mNames.getSize(); i++){
            markerNames.add(mNames.get(i));
        }

        toeMarkersComboBox.setModel(new javax.swing.DefaultComboBoxModel(markerNames));
        medialMarkersComboBox.setModel(new javax.swing.DefaultComboBoxModel(markerNames));
        lateralMarkersComboBox.setModel(new javax.swing.DefaultComboBoxModel(markerNames));
        heelMarkersComboBox.setModel(new javax.swing.DefaultComboBoxModel(markerNames));
        midfootMarkersComboBox.setModel(new javax.swing.DefaultComboBoxModel(markerNames));
        
        // Make properties consistent with UI
        HindfootBodiesComboBoxActionPerformed(null);
        toeMarkersComboBoxActionPerformed(null);
        medialMarkersComboBoxActionPerformed(null);
        lateralMarkersComboBoxActionPerformed(null);
        heelMarkersComboBoxActionPerformed(null);
        midfootMarkersComboBoxActionPerformed(null);
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
        jGCPSurfaceNameTextField = new javax.swing.JTextField();
        jEnabledCheckBox = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jStartTimeTextField = new javax.swing.JTextField();
        jEndTimeTextField = new javax.swing.JTextField();
        jLeftFootCheckBox = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();
        jBeltSpeedTextField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jComboBoxFX3 = new javax.swing.JComboBox();
        jComboBoxFY = new javax.swing.JComboBox();
        jComboBoxFZ = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        jComboBoxFX4 = new javax.swing.JComboBox();
        jComboBoxFY1 = new javax.swing.JComboBox();
        jComboBoxFZ1 = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        jComboBoxFX5 = new javax.swing.JComboBox();
        jComboBoxFY2 = new javax.swing.JComboBox();
        jComboBoxFZ2 = new javax.swing.JComboBox();
        HindfootBodiesComboBox = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        toeMarkersComboBox = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        medialMarkersComboBox = new javax.swing.JComboBox();
        jLabel11 = new javax.swing.JLabel();
        lateralMarkersComboBox = new javax.swing.JComboBox();
        jLabel12 = new javax.swing.JLabel();
        heelMarkersComboBox = new javax.swing.JComboBox();
        jLabel13 = new javax.swing.JLabel();
        midfootMarkersComboBox = new javax.swing.JComboBox();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(EditGCPSurfaceJPanel.class, "EditGCPSurfaceJPanel.jLabel1.text")); // NOI18N

        jGCPSurfaceNameTextField.setText(org.openide.util.NbBundle.getMessage(EditGCPSurfaceJPanel.class, "EditGCPSurfaceJPanel.jGCPSurfaceNameTextField.text")); // NOI18N
        jGCPSurfaceNameTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jGCPSurfaceNameTextFieldFocusLost(evt);
            }
        });
        jGCPSurfaceNameTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jGCPSurfaceNameTextFieldActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jEnabledCheckBox, org.openide.util.NbBundle.getMessage(EditGCPSurfaceJPanel.class, "EditGCPSurfaceJPanel.jEnabledCheckBox.text")); // NOI18N
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

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(EditGCPSurfaceJPanel.class, "EditGCPSurfaceJPanel.jLabel3.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(EditGCPSurfaceJPanel.class, "EditGCPSurfaceJPanel.jLabel4.text")); // NOI18N

        jStartTimeTextField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jStartTimeTextField.setText(org.openide.util.NbBundle.getMessage(EditGCPSurfaceJPanel.class, "EditGCPSurfaceJPanel.jStartTimeTextField.text")); // NOI18N
        jStartTimeTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jStartTimeTextFieldFocusLost(evt);
            }
        });
        jStartTimeTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jStartTimeTextFieldActionPerformed(evt);
            }
        });

        jEndTimeTextField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jEndTimeTextField.setText(org.openide.util.NbBundle.getMessage(EditGCPSurfaceJPanel.class, "EditGCPSurfaceJPanel.jEndTimeTextField.text")); // NOI18N
        jEndTimeTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jEndTimeTextFieldFocusLost(evt);
            }
        });
        jEndTimeTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jEndTimeTextFieldActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLeftFootCheckBox, org.openide.util.NbBundle.getMessage(EditGCPSurfaceJPanel.class, "EditGCPSurfaceJPanel.jLeftFootCheckBox.text")); // NOI18N
        jLeftFootCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jLeftFootCheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(EditGCPSurfaceJPanel.class, "EditGCPSurfaceJPanel.jLabel2.text")); // NOI18N

        jBeltSpeedTextField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jBeltSpeedTextField.setText(org.openide.util.NbBundle.getMessage(EditGCPSurfaceJPanel.class, "EditGCPSurfaceJPanel.jBeltSpeedTextField.text")); // NOI18N
        jBeltSpeedTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jBeltSpeedTextFieldFocusLost(evt);
            }
        });
        jBeltSpeedTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBeltSpeedTextFieldActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(EditGCPSurfaceJPanel.class, "EditGCPSurfaceJPanel.jLabel5.text")); // NOI18N

        jComboBoxFX3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ground_force_px", "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxFX3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxFX3ActionPerformed(evt);
            }
        });

        jComboBoxFY.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ground_force_py", "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxFY.setEnabled(false);
        jComboBoxFY.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxFYupdateForceFromPanel(evt);
            }
        });

        jComboBoxFZ.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ground_force_pz", "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxFZ.setEnabled(false);
        jComboBoxFZ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxFZupdateForceFromPanel(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(EditGCPSurfaceJPanel.class, "EditGCPSurfaceJPanel.jLabel6.text")); // NOI18N

        jComboBoxFX4.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ground_force_px", "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxFX4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxFX4ActionPerformed(evt);
            }
        });

        jComboBoxFY1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ground_force_py", "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxFY1.setEnabled(false);
        jComboBoxFY1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxFY1updateForceFromPanel(evt);
            }
        });

        jComboBoxFZ1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ground_force_pz", "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxFZ1.setEnabled(false);
        jComboBoxFZ1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxFZ1updateForceFromPanel(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel7, org.openide.util.NbBundle.getMessage(EditGCPSurfaceJPanel.class, "EditGCPSurfaceJPanel.jLabel7.text")); // NOI18N

        jComboBoxFX5.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ground_force_px", "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxFX5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxFX5ActionPerformed(evt);
            }
        });

        jComboBoxFY2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ground_force_py", "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxFY2.setEnabled(false);
        jComboBoxFY2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxFY2updateForceFromPanel(evt);
            }
        });

        jComboBoxFZ2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ground_force_pz", "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxFZ2.setEnabled(false);
        jComboBoxFZ2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxFZ2updateForceFromPanel(evt);
            }
        });

        HindfootBodiesComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        HindfootBodiesComboBox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                HindfootBodiesComboBoxFocusLost(evt);
            }
        });
        HindfootBodiesComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                HindfootBodiesComboBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel8, org.openide.util.NbBundle.getMessage(EditGCPSurfaceJPanel.class, "EditGCPSurfaceJPanel.jLabel8.text")); // NOI18N

        toeMarkersComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        toeMarkersComboBox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                toeMarkersComboBoxFocusLost(evt);
            }
        });
        toeMarkersComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toeMarkersComboBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel9, org.openide.util.NbBundle.getMessage(EditGCPSurfaceJPanel.class, "EditGCPSurfaceJPanel.jLabel9.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel10, org.openide.util.NbBundle.getMessage(EditGCPSurfaceJPanel.class, "EditGCPSurfaceJPanel.jLabel10.text")); // NOI18N

        medialMarkersComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        medialMarkersComboBox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                medialMarkersComboBoxFocusLost(evt);
            }
        });
        medialMarkersComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                medialMarkersComboBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel11, org.openide.util.NbBundle.getMessage(EditGCPSurfaceJPanel.class, "EditGCPSurfaceJPanel.jLabel11.text")); // NOI18N

        lateralMarkersComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        lateralMarkersComboBox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                lateralMarkersComboBoxFocusLost(evt);
            }
        });
        lateralMarkersComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lateralMarkersComboBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel12, org.openide.util.NbBundle.getMessage(EditGCPSurfaceJPanel.class, "EditGCPSurfaceJPanel.jLabel12.text")); // NOI18N

        heelMarkersComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        heelMarkersComboBox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                heelMarkersComboBoxFocusLost(evt);
            }
        });
        heelMarkersComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                heelMarkersComboBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel13, org.openide.util.NbBundle.getMessage(EditGCPSurfaceJPanel.class, "EditGCPSurfaceJPanel.jLabel13.text")); // NOI18N

        midfootMarkersComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        midfootMarkersComboBox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                midfootMarkersComboBoxFocusLost(evt);
            }
        });
        midfootMarkersComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                midfootMarkersComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jEnabledCheckBox)
                    .addComponent(jLeftFootCheckBox)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10)
                    .addComponent(jLabel12))
                .addGap(9, 9, 9)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBoxFX5, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(heelMarkersComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(medialMarkersComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(toeMarkersComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(HindfootBodiesComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addComponent(jLabel13))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lateralMarkersComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(midfootMarkersComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jGCPSurfaceNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jStartTimeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(jEndTimeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jBeltSpeedTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jComboBoxFX3, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxFY, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxFZ, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jComboBoxFY2, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jComboBoxFX4, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBoxFY1, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBoxFZ1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBoxFZ2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(0, 22, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jGCPSurfaceNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jEnabledCheckBox)
                .addGap(9, 9, 9)
                .addComponent(jLeftFootCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jStartTimeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jEndTimeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jBeltSpeedTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jComboBoxFZ, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jComboBoxFY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jComboBoxFX3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jComboBoxFZ1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jComboBoxFY1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jComboBoxFX4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jComboBoxFX5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jComboBoxFY2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jComboBoxFZ2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(HindfootBodiesComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(toeMarkersComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(medialMarkersComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(lateralMarkersComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(heelMarkersComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(midfootMarkersComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jGCPSurfaceNameTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jGCPSurfaceNameTextFieldActionPerformed
        // TODO add your handling code here:
        gcpContactSurface.setName(jGCPSurfaceNameTextField.getText());
    }//GEN-LAST:event_jGCPSurfaceNameTextFieldActionPerformed

    private void jStartTimeTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jStartTimeTextFieldActionPerformed
        // TODO add your handling code here:
        jStartTimeTextFieldFocusLost(null);
    }//GEN-LAST:event_jStartTimeTextFieldActionPerformed

    private void jEnabledCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jEnabledCheckBoxItemStateChanged
        // TODO add your handling code here:
        enabledProp.setValue(jEnabledCheckBox.isSelected());
    }//GEN-LAST:event_jEnabledCheckBoxItemStateChanged

    private void jGCPSurfaceNameTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jGCPSurfaceNameTextFieldFocusLost
        // TODO add your handling code here:
         gcpContactSurface.setName(jGCPSurfaceNameTextField.getText());
    }//GEN-LAST:event_jGCPSurfaceNameTextFieldFocusLost

    private void jStartTimeTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jStartTimeTextFieldFocusLost
        try {
            if(jStartTimeTextField.getText().trim().length()>0)
                startTimeProp.setValue(0,  numFormat.parse(jStartTimeTextField.getText().trim()).doubleValue());
        } catch (ParseException ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_jStartTimeTextFieldFocusLost

    private void jEndTimeTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jEndTimeTextFieldActionPerformed
        // TODO add your handling code here:
        try {
            // TODO add your handling code here:();
            double endTime=endTimeProp.getValue(0);
            if (jEndTimeTextField.getText().trim().length()>0)
                endTime = numFormat.parse(jEndTimeTextField.getText().trim()).doubleValue();
            endTimeProp.setValue(0, endTime);
        } catch (ParseException ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_jEndTimeTextFieldActionPerformed

    private void jEndTimeTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jEndTimeTextFieldFocusLost
        // TODO add your handling code here:
        jEndTimeTextFieldActionPerformed(null);
    }//GEN-LAST:event_jEndTimeTextFieldFocusLost

    private void jEnabledCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jEnabledCheckBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jEnabledCheckBoxActionPerformed

    private void jBeltSpeedTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jBeltSpeedTextFieldFocusLost
        // TODO add your handling code here:
        jBeltSpeedTextFieldActionPerformed(null);
    }//GEN-LAST:event_jBeltSpeedTextFieldFocusLost

    private void jBeltSpeedTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBeltSpeedTextFieldActionPerformed
        try {
            double beltSpeed=beltSpeedProp.getValue();
            // TODO add your handling code here:();
            if (jBeltSpeedTextField.getText().trim().length()>0)
                beltSpeed = numFormat.parse(jBeltSpeedTextField.getText().trim()).doubleValue();
            beltSpeedProp.setValue(0, beltSpeed);
        } catch (ParseException ex) {
            Exceptions.printStackTrace(ex);
        }        // TODO add your handling code here:
    }//GEN-LAST:event_jBeltSpeedTextFieldActionPerformed

    private void jComboBoxFX3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxFX3ActionPerformed
        // TODO add your handling code here:
        // Populate next two dropdown from the following 2 columns

    }//GEN-LAST:event_jComboBoxFX3ActionPerformed

    private void jComboBoxFYupdateForceFromPanel(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxFYupdateForceFromPanel
        // TODO add your handling code here:
        //updateForceFromPanel();

    }//GEN-LAST:event_jComboBoxFYupdateForceFromPanel

    private void jComboBoxFZupdateForceFromPanel(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxFZupdateForceFromPanel
        // TODO add your handling code here:
        //updateForceFromPanel();
    }//GEN-LAST:event_jComboBoxFZupdateForceFromPanel

    private void jComboBoxFX4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxFX4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxFX4ActionPerformed

    private void jComboBoxFY1updateForceFromPanel(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxFY1updateForceFromPanel
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxFY1updateForceFromPanel

    private void jComboBoxFZ1updateForceFromPanel(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxFZ1updateForceFromPanel
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxFZ1updateForceFromPanel

    private void jComboBoxFX5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxFX5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxFX5ActionPerformed

    private void jComboBoxFY2updateForceFromPanel(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxFY2updateForceFromPanel
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxFY2updateForceFromPanel

    private void jComboBoxFZ2updateForceFromPanel(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxFZ2updateForceFromPanel
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxFZ2updateForceFromPanel

    private void HindfootBodiesComboBoxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_HindfootBodiesComboBoxFocusLost
        HindfootBodiesComboBoxActionPerformed(null);
    }//GEN-LAST:event_HindfootBodiesComboBoxFocusLost

    private void HindfootBodiesComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_HindfootBodiesComboBoxActionPerformed
       hindfootBodyProp.setValue((String) HindfootBodiesComboBox.getSelectedItem());
    }//GEN-LAST:event_HindfootBodiesComboBoxActionPerformed

    private void toeMarkersComboBoxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_toeMarkersComboBoxFocusLost
        // TODO add your handling code here:
        toeMarkersComboBoxActionPerformed(null);
    }//GEN-LAST:event_toeMarkersComboBoxFocusLost

    private void toeMarkersComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toeMarkersComboBoxActionPerformed
        // TODO add your handling code here:
        toeMarkerProp.setValue((String) toeMarkersComboBox.getSelectedItem());
    }//GEN-LAST:event_toeMarkersComboBoxActionPerformed

    private void medialMarkersComboBoxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_medialMarkersComboBoxFocusLost
        // TODO add your handling code here:
        medialMarkersComboBoxActionPerformed(null);
    }//GEN-LAST:event_medialMarkersComboBoxFocusLost

    private void medialMarkersComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_medialMarkersComboBoxActionPerformed
        // TODO add your handling code here:
        medialMarkerProp.setValue((String) medialMarkersComboBox.getSelectedItem());
    }//GEN-LAST:event_medialMarkersComboBoxActionPerformed

    private void lateralMarkersComboBoxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_lateralMarkersComboBoxFocusLost
        // TODO add your handling code here:
        lateralMarkersComboBoxActionPerformed(null);
    }//GEN-LAST:event_lateralMarkersComboBoxFocusLost

    private void lateralMarkersComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lateralMarkersComboBoxActionPerformed
        // TODO add your handling code here:
        lateralMarkerProp.setValue((String) lateralMarkersComboBox.getSelectedItem());
    }//GEN-LAST:event_lateralMarkersComboBoxActionPerformed

    private void heelMarkersComboBoxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_heelMarkersComboBoxFocusLost
        // TODO add your handling code here:
        heelMarkersComboBoxActionPerformed(null);
    }//GEN-LAST:event_heelMarkersComboBoxFocusLost

    private void heelMarkersComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_heelMarkersComboBoxActionPerformed
        // TODO add your handling code here:
        heelMarkerProp.setValue((String) heelMarkersComboBox.getSelectedItem());
    }//GEN-LAST:event_heelMarkersComboBoxActionPerformed

    private void midfootMarkersComboBoxFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_midfootMarkersComboBoxFocusLost
        // TODO add your handling code here:
        midfootMarkersComboBoxActionPerformed(null);
    }//GEN-LAST:event_midfootMarkersComboBoxFocusLost

    private void midfootMarkersComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_midfootMarkersComboBoxActionPerformed
        // TODO add your handling code here:
        midfootMarkerProp.setValue((String) midfootMarkersComboBox.getSelectedItem());
    }//GEN-LAST:event_midfootMarkersComboBoxActionPerformed

    private void jLeftFootCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jLeftFootCheckBoxActionPerformed
        // TODO add your handling code here:
        leftFootProp.setValue(jLeftFootCheckBox.isSelected());
    }//GEN-LAST:event_jLeftFootCheckBoxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox HindfootBodiesComboBox;
    private javax.swing.JComboBox heelMarkersComboBox;
    private javax.swing.JTextField jBeltSpeedTextField;
    private javax.swing.JComboBox jComboBoxFX3;
    private javax.swing.JComboBox jComboBoxFX4;
    private javax.swing.JComboBox jComboBoxFX5;
    private javax.swing.JComboBox jComboBoxFY;
    private javax.swing.JComboBox jComboBoxFY1;
    private javax.swing.JComboBox jComboBoxFY2;
    private javax.swing.JComboBox jComboBoxFZ;
    private javax.swing.JComboBox jComboBoxFZ1;
    private javax.swing.JComboBox jComboBoxFZ2;
    private javax.swing.JCheckBox jEnabledCheckBox;
    private javax.swing.JTextField jEndTimeTextField;
    private javax.swing.JTextField jGCPSurfaceNameTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JCheckBox jLeftFootCheckBox;
    private javax.swing.JTextField jStartTimeTextField;
    private javax.swing.JComboBox lateralMarkersComboBox;
    private javax.swing.JComboBox medialMarkersComboBox;
    private javax.swing.JComboBox midfootMarkersComboBox;
    private javax.swing.JComboBox toeMarkersComboBox;
    // End of variables declaration//GEN-END:variables


}
