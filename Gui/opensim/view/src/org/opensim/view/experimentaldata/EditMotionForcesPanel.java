/*
* Copyright (c)  2005-2008, Stanford University
 * Use of the OpenSim software in source form is permitted provided that the following
 * conditions are met:
 * 	1. The software is used only for non-commercial research and education. It may not
 *     be used in relation to any commercial activity.
 * 	2. The software is not distributed or redistributed.  Software distribution is allowed 
 *     only through https://simtk.org/home/opensim.
 * 	3. Use of the OpenSim software or derivatives must be acknowledged in all publications,
 *      presentations, or documents describing work in which OpenSim or derivatives are used.
 * 	4. Credits to developers may not be removed from executables
 *     created from modifications of the source.
 * 	5. Modifications of source code must retain the above copyright notice, this list of
 *     conditions and the following disclaimer. 
 * 
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 *  SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 *  TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 *  HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR BUSINESS INTERRUPTION) OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 *  WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  EditPrescribedForceSetPanel.java
 *
 * Created on January 27, 2010, 8:59 AM
 */

package org.opensim.view.experimentaldata;

import java.awt.Dialog;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Vector;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.opensim.modeling.ArrayStr;
import org.opensim.modeling.ExternalLoads;
import org.opensim.modeling.ForceSet;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.ExternalForce;
import org.opensim.modeling.Storage;
import org.opensim.utils.FileUtils;

/**
 *
 * @author  ayman
 */
public class EditMotionForcesPanel extends javax.swing.JPanel 
                                         implements ListSelectionListener {
    
    private ExternalLoads dLoads;
    Storage externalLoadsStorage=null;
    Vector<ExternalForce> cachedForces = new Vector<ExternalForce>(4);
    private ForceListModel forceListModel;
    ForceSet dForceSet;
    private NumberFormat numFormat = NumberFormat.getInstance();
    String fullExternalLoadsFilename;
    File dataFile=null;
    /**
     * Creates new form EditPrescribedForceSetPanel
     */
    public EditMotionForcesPanel(Model model, String externalLoadsFilename) throws IOException {
        boolean createNewFile=false;
        if (!new File(externalLoadsFilename).exists()){
            // Query user to create new file
            createNewFile = true;
        }
        if (externalLoadsFilename.equalsIgnoreCase("")|| externalLoadsFilename.equalsIgnoreCase("Unassigned") || createNewFile){
            // Create a new empty ExternalLoads file and use it for now
            String f = new File(model.getInputFileName()).getParentFile().getAbsolutePath();
            externalLoadsFilename = FileUtils.getNextAvailableName(f, "ExternalLoads.xml");
            ExternalLoads el = new ExternalLoads();
            el.setName("Ex1");
            el.print(externalLoadsFilename);
            el.delete();
            el=null;
        }
            dLoads = new ExternalLoads(model, externalLoadsFilename);
        dLoads.setName("Ex2");
        fullExternalLoadsFilename = externalLoadsFilename;
        forceListModel = new ForceListModel(dLoads);
        initComponents();
        String dataFileName = dLoads.getDataFileName(); // Mandatory
        File extForcesFile = new File(fullExternalLoadsFilename);
        if (extForcesFile.exists()){
            dataFile = new File(dataFileName);
            if (!dataFile.exists()){
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
         if (dataFileName!="" && dataFileName !=null && new File(dataFileName).exists()){
            try {
                externalLoadsStorage = new Storage(dataFileName, true);
            } catch (IOException ex) {
                ex.printStackTrace();
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
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
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

        ForcesListManagerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Specify Forces"));

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
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE)
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
                .add(jButtonDelete))
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 185, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(ForcesListManagerPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(ForcesListManagerPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

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
        ExternalForce pfCopy = new ExternalForce(pf);
        EditOneMotionForcePanel eofPanel = new EditOneMotionForcePanel(pf, externalLoadsStorage, dLoads);
        DialogDescriptor dlg = new DialogDescriptor(eofPanel, "Create/Edit ExternalForce");
        eofPanel.setDDialog(dlg);
        DialogDisplayer.getDefault().createDialog(dlg).setVisible(true);
        Object userInput = dlg.getValue();
         if (((Integer)userInput).compareTo((Integer)DialogDescriptor.OK_OPTION)!=0){
             forceListModel.set(sels[0], pfCopy);
         }
    }//GEN-LAST:event_jButtonEditActionPerformed

    private void jButtonAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddActionPerformed
         ExternalForce pf = new ExternalForce();
         //pf.setName(dTool.getNextAvailableForceName("ExternalForce"));
         pf.setAppliedToBodyName("ground");
         EditOneMotionForcePanel eofPanel = new EditOneMotionForcePanel(pf, externalLoadsStorage,dLoads);
         DialogDescriptor dlg = new DialogDescriptor(eofPanel, "Create/Edit ExternalForce");
         eofPanel.setDDialog(dlg);
         Dialog d=DialogDisplayer.getDefault().createDialog(dlg);
         d.setVisible(true);
         Object userInput = dlg.getValue();
         if (((Integer)userInput).compareTo((Integer)DialogDescriptor.OK_OPTION)==0){
             forceListModel.add(forceListModel.getSize(), pf);
             String usrObjBodyName=pf.getAppliedToBodyName();                         
             dLoads.append(pf);
             dLoads.setMemoryOwner(false);
             cachedForces.add(pf);
         }
    }//GEN-LAST:event_jButtonAddActionPerformed
   
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
    private javax.swing.JPanel EditOneForcePanel;
    private javax.swing.JPanel ForceColumnsPanel;
    private javax.swing.JCheckBox ForceIsGlobalCheckBox;
    private javax.swing.JTextField ForceNameTextField;
    private javax.swing.JPanel ForcesListManagerPanel;
    private javax.swing.JPanel PointColumnsPanel;
    private javax.swing.JCheckBox PointIsGlobalCheckBox;
    private javax.swing.JPanel TorqueColumnLabels;
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
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
    
}
