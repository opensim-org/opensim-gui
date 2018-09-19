/* -------------------------------------------------------------------------- *
 * OpenSim: EditExternalLoadsPanel.java                                       *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib, Tom Uchida                                         *
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
 *  EditPrescribedForceSetPanel.java
 *
 * Created on January 27, 2010, 8:59 AM
 */

package org.opensim.tracking;

import java.awt.Dialog;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Vector;
import java.util.prefs.Preferences;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;
import org.opensim.modeling.ArrayStr;
import org.opensim.modeling.ExternalLoads;
import org.opensim.modeling.ForceSet;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.ExternalForce;
import org.opensim.modeling.Storage;
import org.opensim.utils.ErrorDialog;
import org.opensim.utils.FileUtils;
import org.opensim.utils.TheApp;

/**
 *
 * @author  ayman
 */
public class EditExternalLoadsPanel extends javax.swing.JPanel 
                                         implements ListSelectionListener {
    
    private ExternalLoads dLoads;
    Storage externalLoadsStorage=null;
    Vector<ExternalForce> cachedForces = new Vector<ExternalForce>(4);
    private ForceListModel forceListModel;
    ForceSet dForceSet;
    private NumberFormat numFormat = NumberFormat.getInstance();
    String fullExternalLoadsFilename;
    File dataFile=null;
    Model modelLocalCopy;
    /**
     * Creates new form EditPrescribedForceSetPanel
     */
    public EditExternalLoadsPanel(Model model, String externalLoadsFilename) throws IOException {
        boolean createNewFile=false;
        if (externalLoadsFilename.equalsIgnoreCase("Unassigned") || !new File(externalLoadsFilename).exists()){
            // Query user to create new file
            createNewFile = true;
        }
        modelLocalCopy = new Model(model);
        if (externalLoadsFilename.equalsIgnoreCase("")|| externalLoadsFilename.equalsIgnoreCase("Unassigned") || createNewFile){
            // Create a new empty ExternalLoads file and use it for now
            String f = TheApp.getCurrentVersionPreferences().get("Internal.WorkDirectory", "");
            //String t1 = model.getInputFileName();  
            if (model.getInputFileName()!=null && new File(model.getInputFileName()).getParentFile()!=null)
                f = new File(model.getInputFileName()).getParentFile().getAbsolutePath();
            externalLoadsFilename = FileUtils.getNextAvailableName(f, "ExternalLoads.xml");
            ExternalLoads el = new ExternalLoads();
            el.setName("Ex1");
            el.print(externalLoadsFilename);
            el.delete();
            el=null;
        }
        dLoads = new ExternalLoads(modelLocalCopy, externalLoadsFilename);
        dLoads.setName("Ex2");
        modelLocalCopy.initSystem();
        fullExternalLoadsFilename = externalLoadsFilename;
        forceListModel = new ForceListModel(dLoads);
        initComponents();
        ToolCommon.bindProperty(dLoads, "datafile", externalLoadsDataFileName);
        ToolCommon.bindProperty(dLoads, "external_loads_model_kinematics_file", externalLoadsModelKinematicsFileName);
        ToolCommon.bindProperty(dLoads, "lowpass_cutoff_frequency_for_load_kinematics", cutoffFrequency);
        ToolCommon.bindProperty(dLoads, "lowpass_cutoff_frequency_for_load_kinematics", filterModelKinematics);

        filterModelKinematics.setSelected(dLoads.getLowpassCutoffFrequencyForLoadKinematics()>0);
        externalLoadsDataFileName.setExtensionsAndDescription(".sto,.mot", "Data file for external forces");
        externalLoadsModelKinematicsFileName.setExtensionsAndDescription(".sto,.mot", "Kinematics for external loads if transforming force application");
        String externalLoadsKinFile = dLoads.getExternalLoadsModelKinematicsFileName(); // Optional
        externalLoadsModelKinematicsFileName.setFileName(externalLoadsKinFile);
        cutoffFrequency.setText(String.valueOf(dLoads.getLowpassCutoffFrequencyForLoadKinematics()));
        String dataFileName = dLoads.getDataFileName(); // Mandatory
        File extForcesFile = new File(fullExternalLoadsFilename);
        if (extForcesFile.exists()){
            dataFile = new File(dataFileName);
            if (!dataFile.exists() || dataFile.isDirectory()){
                // Try full path
                String parentDir = extForcesFile.getParent();
                dataFileName = parentDir+File.separator+dataFileName;
                dataFile = new File(dataFileName);
                if (!dataFile.exists()){
                    // disable editing
                    
                }
            }
        }
        else {
            // We're dead. bail out. We shouldn't be here as the constructor should've aborted'
        }
        externalLoadsDataFileName.setFileName(dataFileName);
        if (dataFileName!="" && dataFileName !=null && new File(dataFileName).exists() && 
                new File(dataFileName).isFile()){
            try {
                externalLoadsStorage = new Storage(dataFileName);
            } catch (IOException ex) {
                ErrorDialog.displayExceptionDialog(ex);
            }
        }
        updateButtonAvailability();
        jForcesList.addListSelectionListener(this);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        EditOneForcePanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        BodiesComboBox = new javax.swing.JComboBox();
        PointIsGlobalCheckBox = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        ForceNameTextField = new javax.swing.JTextField();
        ForceIsGlobalCheckBox = new javax.swing.JCheckBox();
        ForceColumnsPanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jComboBoxFX = new javax.swing.JComboBox();
        jComboBoxFY = new javax.swing.JComboBox();
        jComboBoxFZ = new javax.swing.JComboBox();
        PointColumnsPanel = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jComboBoxPX = new javax.swing.JComboBox();
        jComboBoxPY = new javax.swing.JComboBox();
        jComboBoxPZ = new javax.swing.JComboBox();
        TorqueColumnLabels = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jComboBoxTX = new javax.swing.JComboBox();
        jComboBoxTY = new javax.swing.JComboBox();
        jComboBoxTZ = new javax.swing.JComboBox();
        externalLoadsDataFileName = new org.opensim.swingui.FileTextFieldAndChooser();
        jLabel8 = new javax.swing.JLabel();
        externalLoadsModelKinematicsFileName = new org.opensim.swingui.FileTextFieldAndChooser();
        cutoffFrequency = new javax.swing.JTextField();
        filterModelKinematics = new javax.swing.JCheckBox();
        jLabel9 = new javax.swing.JLabel();
        DatafileNameLabel = new javax.swing.JLabel();
        ForcesListManagerPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jForcesList = new javax.swing.JList();
        jButtonAdd = new javax.swing.JButton();
        jButtonDelete = new javax.swing.JButton();
        jButtonEdit = new javax.swing.JButton();

        jLabel5.setText("Applied to");

        BodiesComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        PointIsGlobalCheckBox.setText("Point is global");
        PointIsGlobalCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        PointIsGlobalCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jLabel3.setText("Force Name");

        ForceIsGlobalCheckBox.setText("Force is global");
        ForceIsGlobalCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        ForceIsGlobalCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jLabel4.setText("Force Columns");

        jComboBoxFX.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jComboBoxFY.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jComboBoxFZ.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        org.jdesktop.layout.GroupLayout ForceColumnsPanelLayout = new org.jdesktop.layout.GroupLayout(ForceColumnsPanel);
        ForceColumnsPanel.setLayout(ForceColumnsPanelLayout);
        ForceColumnsPanelLayout.setHorizontalGroup(
            ForceColumnsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(ForceColumnsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel4)
                .add(12, 12, 12)
                .add(jComboBoxFZ, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 89, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jComboBoxFY, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 92, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jComboBoxFX, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 125, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
        );
        ForceColumnsPanelLayout.setVerticalGroup(
            ForceColumnsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(ForceColumnsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(jLabel4)
                .add(jComboBoxFY, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(jComboBoxFZ, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(jComboBoxFX, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        jLabel6.setText("Point Columns");

        jComboBoxPX.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jComboBoxPY.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jComboBoxPZ.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        org.jdesktop.layout.GroupLayout PointColumnsPanelLayout = new org.jdesktop.layout.GroupLayout(PointColumnsPanel);
        PointColumnsPanel.setLayout(PointColumnsPanelLayout);
        PointColumnsPanelLayout.setHorizontalGroup(
            PointColumnsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(PointColumnsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel6)
                .add(13, 13, 13)
                .add(jComboBoxPZ, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 89, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jComboBoxPY, 0, 94, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jComboBoxPX, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 125, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        PointColumnsPanelLayout.setVerticalGroup(
            PointColumnsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(PointColumnsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(jLabel6)
                .add(jComboBoxPY, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(jComboBoxPX, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(jComboBoxPZ, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        jLabel7.setText("Torque Columns");

        jComboBoxTX.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jComboBoxTY.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jComboBoxTZ.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        org.jdesktop.layout.GroupLayout TorqueColumnLabelsLayout = new org.jdesktop.layout.GroupLayout(TorqueColumnLabels);
        TorqueColumnLabels.setLayout(TorqueColumnLabelsLayout);
        TorqueColumnLabelsLayout.setHorizontalGroup(
            TorqueColumnLabelsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(TorqueColumnLabelsLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel7)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jComboBoxTZ, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 89, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jComboBoxTY, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 92, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jComboBoxTX, 0, 128, Short.MAX_VALUE))
        );
        TorqueColumnLabelsLayout.setVerticalGroup(
            TorqueColumnLabelsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(TorqueColumnLabelsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(jComboBoxTZ, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(jLabel7)
                .add(jComboBoxTY, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(jComboBoxTX, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        org.jdesktop.layout.GroupLayout EditOneForcePanelLayout = new org.jdesktop.layout.GroupLayout(EditOneForcePanel);
        EditOneForcePanel.setLayout(EditOneForcePanelLayout);
        EditOneForcePanelLayout.setHorizontalGroup(
            EditOneForcePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(EditOneForcePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(EditOneForcePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(EditOneForcePanelLayout.createSequentialGroup()
                        .add(9, 9, 9)
                        .add(EditOneForcePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel5)
                            .add(jLabel3))
                        .add(25, 25, 25)
                        .add(EditOneForcePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(ForceNameTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
                            .add(BodiesComboBox, 0, 116, Short.MAX_VALUE)))
                    .add(EditOneForcePanelLayout.createSequentialGroup()
                        .add(PointIsGlobalCheckBox)
                        .add(39, 39, 39)
                        .add(ForceIsGlobalCheckBox)))
                .add(231, 231, 231))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, EditOneForcePanelLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(EditOneForcePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(EditOneForcePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, ForceColumnsPanel, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, PointColumnsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(TorqueColumnLabels, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        EditOneForcePanelLayout.setVerticalGroup(
            EditOneForcePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(EditOneForcePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(EditOneForcePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(ForceNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(EditOneForcePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(BodiesComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 12, Short.MAX_VALUE)
                .add(ForceColumnsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(PointColumnsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(TorqueColumnLabels, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(21, 21, 21)
                .add(EditOneForcePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(PointIsGlobalCheckBox)
                    .add(ForceIsGlobalCheckBox))
                .addContainerGap())
        );

        externalLoadsDataFileName.setToolTipText("File containing time history of forces");
        externalLoadsDataFileName.setCheckIfFileExists(true);
        externalLoadsDataFileName.setFileFilter(null);
        externalLoadsDataFileName.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                externalLoadsDataFileNameStateChanged(evt);
            }
        });

        jLabel8.setText("Kinematics for external loads");

        externalLoadsModelKinematicsFileName.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                externalLoadsModelKinematicsFileNameStateChanged(evt);
            }
        });

        cutoffFrequency.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        cutoffFrequency.setText("-1");
        cutoffFrequency.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cutoffFrequency.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cutoffFrequencyActionPerformed(evt);
            }
        });
        cutoffFrequency.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cutoffFrequencyFocusLost(evt);
            }
        });

        filterModelKinematics.setText("Filter kinematics");
        filterModelKinematics.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        filterModelKinematics.setMargin(new java.awt.Insets(0, 0, 0, 0));
        filterModelKinematics.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterModelKinematicsActionPerformed(evt);
            }
        });

        jLabel9.setText("Hz");

        DatafileNameLabel.setText("Force data file");

        ForcesListManagerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Specify Forces/Torques for model"));
        jForcesList.setModel(forceListModel);
        jScrollPane1.setViewportView(jForcesList);

        jButtonAdd.setText("Add...");
        jButtonAdd.setToolTipText("Add an external force to the tool");
        jButtonAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddActionPerformed(evt);
            }
        });

        jButtonDelete.setText("Delete");
        jButtonDelete.setToolTipText("Delete selected forces/torques");
        jButtonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteActionPerformed1(evt);
            }
        });

        jButtonEdit.setText("Edit...");
        jButtonEdit.setToolTipText("Edit selected force");
        jButtonEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEditActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout ForcesListManagerPanelLayout = new org.jdesktop.layout.GroupLayout(ForcesListManagerPanel);
        ForcesListManagerPanel.setLayout(ForcesListManagerPanelLayout);
        ForcesListManagerPanelLayout.setHorizontalGroup(
            ForcesListManagerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, ForcesListManagerPanelLayout.createSequentialGroup()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ForcesListManagerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(jButtonDelete, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jButtonEdit, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jButtonAdd, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        ForcesListManagerPanelLayout.setVerticalGroup(
            ForcesListManagerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(ForcesListManagerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jButtonAdd)
                .add(21, 21, 21)
                .add(jButtonEdit)
                .add(17, 17, 17)
                .add(jButtonDelete)
                .add(24, 24, 24))
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(DatafileNameLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(externalLoadsDataFileName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)
                .addContainerGap())
            .add(ForcesListManagerPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                .add(filterModelKinematics)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cutoffFrequency))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel8))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(externalLoadsModelKinematicsFileName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
                    .add(jLabel9))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(externalLoadsDataFileName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(DatafileNameLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel8)
                    .add(externalLoadsModelKinematicsFileName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(filterModelKinematics)
                    .add(jLabel9)
                    .add(cutoffFrequency, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ForcesListManagerPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cutoffFrequencyFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cutoffFrequencyFocusLost
        if(!evt.isTemporary()) cutoffFrequencyActionPerformed(null);
    }//GEN-LAST:event_cutoffFrequencyFocusLost

    private void cutoffFrequencyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cutoffFrequencyActionPerformed
        try {
            dLoads.setLowpassCutoffFrequencyForLoadKinematics(numFormat.parse(cutoffFrequency.getText()).doubleValue());
        } catch (ParseException ex) {
            Toolkit.getDefaultToolkit().beep();
            cutoffFrequency.setText(numFormat.format(dLoads.getLowpassCutoffFrequencyForLoadKinematics()));
        }
    }//GEN-LAST:event_cutoffFrequencyActionPerformed

    private void filterModelKinematicsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterModelKinematicsActionPerformed
     boolean selected=filterModelKinematics.isSelected();
     if (selected) cutoffFrequency.setText("6.0"); else cutoffFrequency.setText("-1.0");
        try {
            dLoads.setLowpassCutoffFrequencyForLoadKinematics(numFormat.parse(cutoffFrequency.getText()).doubleValue());
        } catch (ParseException ex) {
            Toolkit.getDefaultToolkit().beep();
            cutoffFrequency.setText(numFormat.format(dLoads.getLowpassCutoffFrequencyForLoadKinematics()));
        }
    }//GEN-LAST:event_filterModelKinematicsActionPerformed

    private void externalLoadsModelKinematicsFileNameStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_externalLoadsModelKinematicsFileNameStateChanged
        dLoads.setExternalLoadsModelKinematicsFileName(externalLoadsModelKinematicsFileName.getFileName());
    }//GEN-LAST:event_externalLoadsModelKinematicsFileNameStateChanged

    private void jButtonDeleteActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteActionPerformed1
        int[] sels = jForcesList.getSelectedIndices();
        Object pfo=null;
        for(int i=sels.length;i>=1;i--){
           forceListModel.remove(sels[i-1]);
           dLoads.remove(sels[i-1]);
        }
 // TODO add your handling code here:
        
    }//GEN-LAST:event_jButtonDeleteActionPerformed1

    private void jButtonEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEditActionPerformed
        int[] sels = jForcesList.getSelectedIndices();
        Object pfo=null;
        if (sels.length==1){
            pfo = forceListModel.get(sels[0]);
        }
        ExternalForce pf = ExternalForce.safeDownCast((OpenSimObject) pfo);
        ExternalForce pfCopy = ExternalForce.safeDownCast(pf.clone());
        EditOneForceJPanel eofPanel = new EditOneForceJPanel(pf, externalLoadsStorage, dLoads);
        DialogDescriptor dlg = new DialogDescriptor(eofPanel, "Create/Edit ExternalForce");
        eofPanel.setDDialog(dlg);
        DialogDisplayer.getDefault().createDialog(dlg).setVisible(true);
        Object userInput = dlg.getValue();
         if (((Integer)userInput).compareTo((Integer)DialogDescriptor.OK_OPTION)!=0){
             forceListModel.set(sels[0], pfCopy);
         }
    }//GEN-LAST:event_jButtonEditActionPerformed

    private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddActionPerformed
        String initialIdentifier = findViableIdentifier();
        ExternalForce pf = new ExternalForce(externalLoadsStorage, 
                 initialIdentifier, initialIdentifier, "", "", "ground", "ground");
         modelLocalCopy.addForce(pf);
         //pf.setName(dTool.getNextAvailableForceName("ExternalForce"));
         pf.setAppliedToBodyName("ground");
        try {
            modelLocalCopy.initSystem();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
         EditOneForceJPanel eofPanel = new EditOneForceJPanel(pf, externalLoadsStorage,dLoads);
         DialogDescriptor dlg = new DialogDescriptor(eofPanel, "Create/Edit ExternalForce");
         eofPanel.setDDialog(dlg);
         Dialog d=DialogDisplayer.getDefault().createDialog(dlg);
         d.setVisible(true);
         Object userInput = dlg.getValue();
         if (((Integer)userInput).compareTo((Integer)DialogDescriptor.OK_OPTION)==0){
             forceListModel.add(forceListModel.getSize(), pf);
             String usrObjBodyName=pf.getAppliedToBodyName();                         
             dLoads.adoptAndAppend(pf);
             dLoads.setMemoryOwner(false);
             cachedForces.add(pf);
         }
    }//GEN-LAST:event_jButtonAddActionPerformed

    private void handleDatafileSelectionChange() {
        if (externalLoadsDataFileName.getFileIsValid()){
            //dTool.getExternalForceSet().
         String dataFile = externalLoadsDataFileName.getFileName();
         if (dataFile!="" && dataFile !=null && new File(dataFile).exists() &&
                 new File(dataFile).isFile()){
             try {
                 
                 externalLoadsStorage = new Storage(dataFile);   
                  boolean isUnique = verifyUniqueLabels(externalLoadsStorage);
                  externalLoadsStorage.makeStorageLabelsUnique();
                 if (!isUnique){
                     String newFilename = FileUtils.getInstance().browseForFilenameToSave(".sto,.mot", "Save file with unique labels as", true, dataFile);
                     if (newFilename==null) {
                         externalLoadsDataFileName.setFileName("");
                         return;
                     }
                     externalLoadsStorage = new Storage(dataFile); // Read full file not just headers
                     externalLoadsStorage.makeStorageLabelsUnique();
                     dataFile = newFilename;
                     externalLoadsStorage.print(newFilename);
                     externalLoadsDataFileName.setFileName(newFilename);
                }
             } catch (IOException ex) {
                 ErrorDialog.displayExceptionDialog(ex);
             }
         }
         dLoads.setDataFileName(externalLoadsDataFileName.getFileName());
         updateButtonAvailability();
        }
    }

    private void externalLoadsDataFileNameStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_externalLoadsDataFileNameStateChanged
        handleDatafileSelectionChange();
    }//GEN-LAST:event_externalLoadsDataFileNameStateChanged
    
    private void updateButtonAvailability() {
       int[] sels = jForcesList.getSelectedIndices();
       jButtonEdit.setEnabled(sels.length==1);
       jButtonDelete.setEnabled(sels.length>=1);
       // Allow Add only if a data file is available
       jButtonAdd.setEnabled (externalLoadsStorage!=null);
    }

    public void valueChanged(ListSelectionEvent e) {
        updateButtonAvailability();
    }

    public ForceListModel getForceListModel() {
        return forceListModel;
    }

    private boolean verifyUniqueLabels(Storage aStore) {
	ArrayStr lbls = aStore.getColumnLabels();
	boolean isUnique = true;
        String offending="";
	for(int i=0; i< lbls.getSize() && isUnique; i++){
		isUnique= (lbls.findIndex(lbls.getitem(i))==i);
                offending =lbls.getitem(i);
	}
        if (!isUnique){
                  NotifyDescriptor.Message dlg =
                          new NotifyDescriptor.Message("Column labels in specified data file are not unique (e.g. "+
                          offending+"). Appending a prefix to resolve ambiguity..");
                  DialogDisplayer.getDefault().notify(dlg);            
        }
	return isUnique;
    }    

    public ExternalLoads getExternalLoads() {
        return dLoads;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox BodiesComboBox;
    private javax.swing.JLabel DatafileNameLabel;
    private javax.swing.JPanel EditOneForcePanel;
    private javax.swing.JPanel ForceColumnsPanel;
    private javax.swing.JCheckBox ForceIsGlobalCheckBox;
    private javax.swing.JTextField ForceNameTextField;
    private javax.swing.JPanel ForcesListManagerPanel;
    private javax.swing.JPanel PointColumnsPanel;
    private javax.swing.JCheckBox PointIsGlobalCheckBox;
    private javax.swing.JPanel TorqueColumnLabels;
    private javax.swing.JTextField cutoffFrequency;
    private org.opensim.swingui.FileTextFieldAndChooser externalLoadsDataFileName;
    private org.opensim.swingui.FileTextFieldAndChooser externalLoadsModelKinematicsFileName;
    private javax.swing.JCheckBox filterModelKinematics;
    private javax.swing.JButton jButtonAdd;
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JButton jButtonEdit;
    private javax.swing.JComboBox jComboBoxFX;
    private javax.swing.JComboBox jComboBoxFY;
    private javax.swing.JComboBox jComboBoxFZ;
    private javax.swing.JComboBox jComboBoxPX;
    private javax.swing.JComboBox jComboBoxPY;
    private javax.swing.JComboBox jComboBoxPZ;
    private javax.swing.JComboBox jComboBoxTX;
    private javax.swing.JComboBox jComboBoxTY;
    private javax.swing.JComboBox jComboBoxTZ;
    private javax.swing.JList jForcesList;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
    String findViableIdentifier(){
        ArrayStr lbls = externalLoadsStorage.getColumnLabels();
        String candidate = "";
        boolean found = false;
        for(int i=0; i< lbls.getSize()-2 && !found; i++){
            candidate = EditOneForceJPanel.makeIdentifier(lbls.get(i), lbls.get(i+1), lbls.get(i+2));
            found = (candidate.length()!=0);
        }
        return candidate;
    }
}
