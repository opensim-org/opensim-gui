/* -------------------------------------------------------------------------- *
 * OpenSim: ExcitationEditorJPanel.java                                       *
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
 * ExcitationEditorJPanel.java
 *
 * Created on January 29, 2008, 1:43 PM
 */

package org.opensim.view.excitationEditor;

import java.awt.Color;
import java.awt.Frame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeriesCollection;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.opensim.modeling.ArrayStr;
import org.opensim.modeling.Control;
import org.opensim.modeling.ControlLinear;
import org.opensim.modeling.ControlLinearNode;
import org.opensim.modeling.ControlSet;
import org.opensim.modeling.Function;
import org.opensim.modeling.PiecewiseLinearFunction;
import org.opensim.modeling.SetControlNodes;
import org.opensim.modeling.PiecewiseConstantFunction;
import org.opensim.modeling.XYFunctionInterface;
import org.opensim.utils.DialogUtils;
import org.opensim.utils.ErrorDialog;
import org.opensim.utils.FileUtils;
import org.opensim.utils.OpenSimDialog;
import org.opensim.view.excitationEditor.ExcitationRenderer.ExcitationFillMode;
import org.opensim.view.functionEditor.FunctionPanel;
import org.opensim.view.functionEditor.FunctionPlot;
import org.opensim.view.functionEditor.FunctionXYSeries;

/**
 *
 * @author  Ayman
 */
public class ExcitationEditorJPanel extends javax.swing.JPanel implements TreeSelectionListener {
    
    private ControlSet controlSet;
    //ControlSet backupControlSet;
    JFrame frame;
    DefaultTreeModel treeModel;
    Vector<TreePath> selectedPathsVector = new Vector<TreePath>(4);   // Cache used to accumulate user selection of the tree
    Vector<TreePath> selectedColumnsVector = new Vector<TreePath>(4);   // Cache used to accumulate user selection of the tree
    boolean somethingSelected=false;
    private ExcitationsGridJPanel excitationGridPanel = new ExcitationsGridJPanel();
    static int MAX_EXCITATIONS_PER_COLUMN=4;
    /** Creates new form ExcitationEditorJPanel */
    
    public ExcitationEditorJPanel(JFrame owner, ControlSet controls) {
        frame = owner;
        DefaultMutableTreeNode root=new DefaultMutableTreeNode("Excitation columns");
        treeModel = new DefaultTreeModel(root);
        initComponents();
        root.setUserObject(getExcitationGridPanel());
        jExcitationsTree.addTreeSelectionListener(this);
        jMoveUpButton.setEnabled(false);
        jMoveDownButton.setEnabled(false);
        jDeleteButton.setEnabled(false);
        jValueToFormattedTextField.getInputMap().put(KeyStroke.getKeyStroke(
              KeyEvent.VK_ENTER, 0),
              "check");
        jValueToFormattedTextField.getActionMap().put("check", new handleReturnAction());   
        // Drag and Drop support
        jExcitationsTree.setDragEnabled(true);
        MoveExcitationHandler th = new MoveExcitationHandler(getExcitationGridPanel());
        jExcitationsTree.setTransferHandler(th);
        jExcitationsTree.setDropTarget(new TreeDropTarget(th));

        jExcitationsTree.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0),
                            "handleDelete");
        jExcitationsTree.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0),
                            "handleDelete");
        jExcitationsTree.getActionMap().put("handleDelete", new handleDelete());
        ShowExcBaseShapeCheckBox2.setSelected(true);
        ShowMaxBaseShapeCheckBox2.setSelected(true);
        ShowMinBaseShapeCheckBox2.setSelected(true);
        MinMaxShadingCheckBox2.setSelected(true);
        if (controls!=null){
            populate(controls, true);
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jTabbedPane1 = new javax.swing.JTabbedPane();
        displayControlPanel = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        MinMaxShadingCheckBox1 = new javax.swing.JCheckBox();
        jPanel6 = new javax.swing.JPanel();
        ShowExcBaseShapeCheckBox1 = new JCheckBox("Control", true);
        ShowMinBaseShapeCheckBox1 = new JCheckBox("Minimum", true);
        ShowMaxBaseShapeCheckBox1 = new JCheckBox("Maximum", true);
        jPanel4 = new javax.swing.JPanel();
        ExportExcitationsButton = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jButton8 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jApplyValueToSelectedButton = new javax.swing.JButton();
        jSplitPane1 = new javax.swing.JSplitPane();
        jLeftPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jExcitationsTree = new javax.swing.JTree();
        jLayoutControlPanel = new javax.swing.JPanel();
        jMoveUpButton = new javax.swing.JButton();
        jMoveDownButton = new javax.swing.JButton();
        jDeleteButton = new javax.swing.JButton();
        jRightPanel = new javax.swing.JPanel();
        ControlPanel = new javax.swing.JPanel();
        jSeparator2 = new javax.swing.JSeparator();
        jBackupAllButton = new javax.swing.JButton();
        jRestoreAllButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jRemoveNodesButton = new javax.swing.JButton();
        jValueToFormattedTextField = new javax.swing.JFormattedTextField();
        jSetValueSelectedPointsButton = new javax.swing.JButton();
        jExScrollPane = new javax.swing.JScrollPane();
        displayControlPanel1 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        MinMaxShadingCheckBox2 = new javax.swing.JCheckBox();
        jPanel9 = new javax.swing.JPanel();
        ShowExcBaseShapeCheckBox2 = new JCheckBox("Control", true);
        ShowMinBaseShapeCheckBox2 = new JCheckBox("Minimum", true);
        ShowMaxBaseShapeCheckBox2 = new JCheckBox("Maximum", true);

        jTabbedPane1.setName("Edit");
        displayControlPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Display Preferences"));
        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Shading Options"));
        MinMaxShadingCheckBox1.setText("MinMax");
        MinMaxShadingCheckBox1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        MinMaxShadingCheckBox1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        MinMaxShadingCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MinMaxShadingCheckBoxActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel7Layout = new org.jdesktop.layout.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(22, Short.MAX_VALUE)
                .add(MinMaxShadingCheckBox1)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .add(MinMaxShadingCheckBox1)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Curve Shapes"));
        ShowExcBaseShapeCheckBox1.setText("Control");
        ShowExcBaseShapeCheckBox1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        ShowExcBaseShapeCheckBox1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        ShowExcBaseShapeCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ShowExcBaseShapeCheckBoxActionPerformed(evt);
            }
        });

        ShowMinBaseShapeCheckBox1.setText("Minimum");
        ShowMinBaseShapeCheckBox1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        ShowMinBaseShapeCheckBox1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        ShowMinBaseShapeCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ShowMinBaseShapeCheckBoxActionPerformed(evt);
            }
        });

        ShowMaxBaseShapeCheckBox1.setText("Maximum");
        ShowMaxBaseShapeCheckBox1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        ShowMaxBaseShapeCheckBox1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        ShowMaxBaseShapeCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ShowMaxBaseShapeCheckBoxActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(ShowExcBaseShapeCheckBox1)
                    .add(ShowMinBaseShapeCheckBox1)
                    .add(ShowMaxBaseShapeCheckBox1))
                .addContainerGap(35, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .add(ShowExcBaseShapeCheckBox1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ShowMinBaseShapeCheckBox1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ShowMaxBaseShapeCheckBox1))
        );

        org.jdesktop.layout.GroupLayout displayControlPanelLayout = new org.jdesktop.layout.GroupLayout(displayControlPanel);
        displayControlPanel.setLayout(displayControlPanelLayout);
        displayControlPanelLayout.setHorizontalGroup(
            displayControlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(displayControlPanelLayout.createSequentialGroup()
                .add(jPanel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        displayControlPanelLayout.setVerticalGroup(
            displayControlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(jPanel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "With Selected Excitations"));
        ExportExcitationsButton.setText("Export...");
        ExportExcitationsButton.setToolTipText("Save selected controls to a separate file");
        ExportExcitationsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExportExcitationsButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .add(ExportExcitationsButton)
                .addContainerGap(174, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(ExportExcitationsButton)
        );
        jButton5.setText("Apply");
        jLabel1.setText("Set to");
        jButton8.setText("Advanced...");

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 636, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(200, 200, 200)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(372, Short.MAX_VALUE))
        );
        jApplyValueToSelectedButton.setText("Apply");
        jApplyValueToSelectedButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jApplyValueToSelectedButtonActionPerformed(evt);
            }
        });

        setName("Excitation Editor");
        jSplitPane1.setDividerSize(7);
        jSplitPane1.setOneTouchExpandable(true);
        jLeftPanel.setLayout(new java.awt.BorderLayout());

        jExcitationsTree.setDragEnabled(true);
        jExcitationsTree.setModel(treeModel);
        jExcitationsTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jExcitationsTreeMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jExcitationsTreeMouseReleased(evt);
            }
        });

        jScrollPane1.setViewportView(jExcitationsTree);

        jLeftPanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jMoveUpButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/icons/upArrow.png")));
        jMoveUpButton.setBorderPainted(false);
        jMoveUpButton.setContentAreaFilled(false);
        jMoveUpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMoveUpButtonActionPerformed(evt);
            }
        });

        jMoveDownButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/icons/downArrow.png")));
        jMoveDownButton.setBorderPainted(false);
        jMoveDownButton.setContentAreaFilled(false);
        jMoveDownButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMoveDownButtonActionPerformed(evt);
            }
        });

        jDeleteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/icons/delete.png")));
        jDeleteButton.setBorderPainted(false);
        jDeleteButton.setContentAreaFilled(false);
        jDeleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jDeleteButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jLayoutControlPanelLayout = new org.jdesktop.layout.GroupLayout(jLayoutControlPanel);
        jLayoutControlPanel.setLayout(jLayoutControlPanelLayout);
        jLayoutControlPanelLayout.setHorizontalGroup(
            jLayoutControlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jLayoutControlPanelLayout.createSequentialGroup()
                .add(27, 27, 27)
                .add(jMoveUpButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jMoveDownButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jDeleteButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jLayoutControlPanelLayout.setVerticalGroup(
            jLayoutControlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jLayoutControlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(jDeleteButton)
                .add(jMoveDownButton)
                .add(jMoveUpButton))
        );
        jLeftPanel.add(jLayoutControlPanel, java.awt.BorderLayout.SOUTH);

        jSplitPane1.setLeftComponent(jLeftPanel);

        jRightPanel.setLayout(new java.awt.BorderLayout());

        jBackupAllButton.setText("Backup");
        jBackupAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBackupAllButtonActionPerformed(evt);
            }
        });

        jRestoreAllButton.setText("Restore");
        jRestoreAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRestoreAllButtonActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Operations on Selected Points"));
        jLabel2.setText("Set selected points to");

        jRemoveNodesButton.setText("Remove selected points ");
        jRemoveNodesButton.setToolTipText("Remove selected control points from their respective curves");
        jRemoveNodesButton.setActionCommand("Remove Selected Nodes");
        jRemoveNodesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRemoveNodesButtonActionPerformed(evt);
            }
        });

        jValueToFormattedTextField.setToolTipText("Specify fixed value to set Y value for selected control points");
        jValueToFormattedTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jValueToFormattedTextFieldActionPerformed(evt);
            }
        });
        jValueToFormattedTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jValueToFormattedTextFieldFocusLost(evt);
            }
        });

        jSetValueSelectedPointsButton.setText("OK");
        jSetValueSelectedPointsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSetValueSelectedPointsButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 104, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jValueToFormattedTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 94, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jSetValueSelectedPointsButton))
                    .add(jRemoveNodesButton)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jValueToFormattedTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jSetValueSelectedPointsButton)
                    .add(jLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jRemoveNodesButton)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        jExScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jExScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jExScrollPane.setAlignmentY(0.0F);

        displayControlPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Display Preferences"));
        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Shading Options"));
        MinMaxShadingCheckBox2.setText("MinMax");
        MinMaxShadingCheckBox2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        MinMaxShadingCheckBox2.setMargin(new java.awt.Insets(0, 0, 0, 0));
        MinMaxShadingCheckBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MinMaxShadingCheckBoxActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel8Layout = new org.jdesktop.layout.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .add(MinMaxShadingCheckBox2)
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel8Layout.createSequentialGroup()
                .add(MinMaxShadingCheckBox2)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Curve Shapes"));
        ShowExcBaseShapeCheckBox2.setText("Control");
        ShowExcBaseShapeCheckBox2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        ShowExcBaseShapeCheckBox2.setMargin(new java.awt.Insets(0, 0, 0, 0));
        ShowExcBaseShapeCheckBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ShowExcBaseShapeCheckBoxActionPerformed(evt);
            }
        });

        ShowMinBaseShapeCheckBox2.setText("Minimum");
        ShowMinBaseShapeCheckBox2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        ShowMinBaseShapeCheckBox2.setMargin(new java.awt.Insets(0, 0, 0, 0));
        ShowMinBaseShapeCheckBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ShowMinBaseShapeCheckBoxActionPerformed(evt);
            }
        });

        ShowMaxBaseShapeCheckBox2.setText("Maximum");
        ShowMaxBaseShapeCheckBox2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        ShowMaxBaseShapeCheckBox2.setMargin(new java.awt.Insets(0, 0, 0, 0));
        ShowMaxBaseShapeCheckBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ShowMaxBaseShapeCheckBoxActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel9Layout = new org.jdesktop.layout.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(ShowExcBaseShapeCheckBox2)
                    .add(ShowMinBaseShapeCheckBox2)
                    .add(ShowMaxBaseShapeCheckBox2))
                .addContainerGap(35, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .add(ShowExcBaseShapeCheckBox2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ShowMinBaseShapeCheckBox2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ShowMaxBaseShapeCheckBox2))
        );

        org.jdesktop.layout.GroupLayout displayControlPanel1Layout = new org.jdesktop.layout.GroupLayout(displayControlPanel1);
        displayControlPanel1.setLayout(displayControlPanel1Layout);
        displayControlPanel1Layout.setHorizontalGroup(
            displayControlPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(displayControlPanel1Layout.createSequentialGroup()
                .add(jPanel9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        displayControlPanel1Layout.setVerticalGroup(
            displayControlPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(displayControlPanel1Layout.createSequentialGroup()
                .add(displayControlPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout ControlPanelLayout = new org.jdesktop.layout.GroupLayout(ControlPanel);
        ControlPanel.setLayout(ControlPanelLayout);
        ControlPanelLayout.setHorizontalGroup(
            ControlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(ControlPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jBackupAllButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 374, Short.MAX_VALUE)
                .add(jRestoreAllButton)
                .addContainerGap())
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jSeparator2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 532, Short.MAX_VALUE)
            .add(ControlPanelLayout.createSequentialGroup()
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(displayControlPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 231, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(8, 8, 8))
            .add(jExScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 532, Short.MAX_VALUE)
        );
        ControlPanelLayout.setVerticalGroup(
            ControlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, ControlPanelLayout.createSequentialGroup()
                .add(jExScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ControlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, displayControlPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ControlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jBackupAllButton)
                    .add(jRestoreAllButton))
                .addContainerGap())
        );
        jRightPanel.add(ControlPanel, java.awt.BorderLayout.CENTER);

        jSplitPane1.setRightComponent(jRightPanel);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 644, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 539, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jSetValueSelectedPointsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSetValueSelectedPointsButtonActionPerformed
// TODO add your handling code here:
        new handleReturnAction().actionPerformed(null);
    }//GEN-LAST:event_jSetValueSelectedPointsButtonActionPerformed

    private void jValueToFormattedTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jValueToFormattedTextFieldFocusLost
// TODO add your handling code here:
        new handleReturnAction().actionPerformed(null);
    }//GEN-LAST:event_jValueToFormattedTextFieldFocusLost

    private void jValueToFormattedTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jValueToFormattedTextFieldActionPerformed
        new handleReturnAction().actionPerformed(null);
// TODO add your handling code here:
    }//GEN-LAST:event_jValueToFormattedTextFieldActionPerformed

   private void MinMaxShadingCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MinMaxShadingCheckBoxActionPerformed
      excitationGridPanel.toggleMinMaxShading(((JCheckBox)evt.getSource()).isSelected());
   }//GEN-LAST:event_MinMaxShadingCheckBoxActionPerformed

    private void ShowMaxBaseShapeCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ShowMaxBaseShapeCheckBoxActionPerformed
// TODO add your handling code here:
        excitationGridPanel.showBaseShape(2, ((JCheckBox)evt.getSource()).isSelected());
    }//GEN-LAST:event_ShowMaxBaseShapeCheckBoxActionPerformed

    private void ShowMinBaseShapeCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ShowMinBaseShapeCheckBoxActionPerformed
        excitationGridPanel.showBaseShape(1, ((JCheckBox)evt.getSource()).isSelected());
// TODO add your handling code here:
    }//GEN-LAST:event_ShowMinBaseShapeCheckBoxActionPerformed

    private void ShowExcBaseShapeCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ShowExcBaseShapeCheckBoxActionPerformed
        excitationGridPanel.showBaseShape(0, ((JCheckBox)evt.getSource()).isSelected());
// TODO add your handling code here:
    }//GEN-LAST:event_ShowExcBaseShapeCheckBoxActionPerformed

    private void jRestoreAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRestoreAllButtonActionPerformed
// TODO add your handling code here:
        excitationGridPanel.restore();
    }//GEN-LAST:event_jRestoreAllButtonActionPerformed

    private void jBackupAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBackupAllButtonActionPerformed
// TODO add your handling code here:
        // Delegate the call to individual Panels
        excitationGridPanel.backup();
    }//GEN-LAST:event_jBackupAllButtonActionPerformed

    private void ExportExcitationsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExportExcitationsButtonActionPerformed
// TODO add your handling code here:
        if (somethingSelected){
            // make a new controlSet and add selected excitations to it.
            String fileName = FileUtils.getInstance().browseForFilenameToSave(FileUtils.getFileFilter(".xml", "Save excitations to file"), true, "controls_1.xml");
            if(fileName != null){
                ControlSet newControlSet = new ControlSet();
                for(int i=0; i< selectedPathsVector.size(); i++){
                    TreePath nextPath=selectedPathsVector.get(i);
                    TreeNode lastNode=(TreeNode)nextPath.getLastPathComponent();
                    Object userObject =  ((DefaultMutableTreeNode)lastNode).getUserObject();
                    if (userObject instanceof ExcitationObject){
                        ExcitationObject eo = (ExcitationObject)userObject;
                        ExcitationRenderer renderer = (ExcitationRenderer) eo.getPlotPanel().getChart().getXYPlot().getRenderer(0);
                        newControlSet.cloneAndAppend(renderer.getControl());
                    }
                }
                newControlSet.print(fileName);
            }
        }
    }//GEN-LAST:event_ExportExcitationsButtonActionPerformed

    private void jRemoveNodesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRemoveNodesButtonActionPerformed
// TODO add your handling code here:
        getExcitationGridPanel().removeSelectedNodes();
    }//GEN-LAST:event_jRemoveNodesButtonActionPerformed

    private void jExcitationsTreeMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jExcitationsTreeMouseReleased
// TODO add your handling code here:
       if (evt.isPopupTrigger())
        invokeTreePopupIfNeeded(evt.getX(), evt.getY());
       //OpenSim23 if (evt.getClickCount()==2 && evt.getModifiers()==InputEvent.BUTTON1_MASK){    // innocent double click on a node. Toggle display
       //OpenSim23     toggleDisplay(evt.getX(), evt.getY());
       //OpenSim23 }

    }//GEN-LAST:event_jExcitationsTreeMouseReleased

    private void jExcitationsTreeMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jExcitationsTreeMousePressed
       if (evt.isPopupTrigger())
        invokeTreePopupIfNeeded(evt.getX(), evt.getY());
       
// TODO add your handling code here:
    }//GEN-LAST:event_jExcitationsTreeMousePressed

    private void jApplyValueToSelectedButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jApplyValueToSelectedButtonActionPerformed
// TODO add your handling code here:
        // Cycle thru all excitation panels and apply operation
        String valueString=jValueToFormattedTextField.getText();
        double valueDouble = Double.valueOf(valueString);
        getExcitationGridPanel().applyValueToSelectedNodes(valueDouble);
    }//GEN-LAST:event_jApplyValueToSelectedButtonActionPerformed

    private void jDeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDeleteButtonActionPerformed
// TODO add your handling code here:
         if (somethingSelected){
            // Cache nodes to delete separately since deletion of nodes causes the current array of selection to change on us
            Vector<TreeNode> pendingNodesVector=new Vector<TreeNode>(4);
            for(int i=0; i< selectedPathsVector.size(); i++){
                TreePath nextPath=selectedPathsVector.get(i);
                TreeNode lastNode=(TreeNode)nextPath.getLastPathComponent();
                ExcitationObject eo = (ExcitationObject) ((DefaultMutableTreeNode)lastNode).getUserObject();
                if (eo != null){
                    pendingNodesVector.add(lastNode);
                }
            }
             for(int i=0; i< pendingNodesVector.size(); i++){
                TreeNode lastNode=(TreeNode)pendingNodesVector.get(i);  
                int idx = treeModel.getIndexOfChild(lastNode.getParent(),lastNode);
                assert(idx!=-1);
                TreeNode parentNode = lastNode.getParent();
                int parentIndex=treeModel.getIndexOfChild(parentNode.getParent(),parentNode);
                assert(parentIndex!=-1);
                ExcitationObject eo = (ExcitationObject) ((DefaultMutableTreeNode)lastNode).getUserObject();
                if (eo != null){
                   getExcitationGridPanel().removePanel(parentIndex, eo.getPlotPanel());
                }
                treeModel.removeNodeFromParent((MutableTreeNode) lastNode);
            }
         }
         // Now the columns
         // Keep a copy since first deletion destroys selection
         Vector<TreeNode> pendingColumnsVector=new Vector<TreeNode>(4);
         for(int i=0; i< selectedColumnsVector.size(); i++){
            pendingColumnsVector.add((TreeNode)selectedColumnsVector.get(i).getLastPathComponent());
         }
         for(int i=0; i< pendingColumnsVector.size(); i++){
                DefaultMutableTreeNode lastNode=(DefaultMutableTreeNode)pendingColumnsVector.get(i);
                ExcitationColumnJPanel eCol = (ExcitationColumnJPanel) ((DefaultMutableTreeNode)lastNode).getUserObject();
                if (eCol != null){
                    treeModel.removeNodeFromParent(lastNode);
                    excitationGridPanel.removeColumn(eCol);
                    excitationGridPanel.repaint();  // Sounds wrong but have no other way to force refresh of display
                }
         }
         excitationGridPanel.validate();
         
    }//GEN-LAST:event_jDeleteButtonActionPerformed

    private void jMoveDownButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMoveDownButtonActionPerformed
        if (somethingSelected){
            // 
            Vector<TreeNode> pendingNodesVector=new Vector<TreeNode>(4);
            for(int i=0; i< selectedPathsVector.size(); i++){
                TreePath nextPath=selectedPathsVector.get(i);
                TreeNode lastNode=(TreeNode)nextPath.getLastPathComponent();
                ExcitationObject eo = (ExcitationObject) ((DefaultMutableTreeNode)lastNode).getUserObject();
                if (eo != null){
                    pendingNodesVector.add(lastNode);
                }
            }
            // For each column, find selected entries, sort top to bottom and move one step up
            DefaultMutableTreeNode root  = (DefaultMutableTreeNode) treeModel.getRoot();
            for(int i=0; i<root.getChildCount(); i++){
                // get Column's node
                DefaultMutableTreeNode columnNode = (DefaultMutableTreeNode) treeModel.getChild(root, i);
                ArrayList<Integer> selectedIdices = new ArrayList<Integer>();
                for(int j=0; j<columnNode.getChildCount(); j++){
                    DefaultMutableTreeNode exciationNode = (DefaultMutableTreeNode) treeModel.getChild(columnNode, j);
                    if (pendingNodesVector.contains(exciationNode)) 
                        selectedIdices.add(new Integer(j));
                }
                if (selectedIdices.size()==0)   // Nothing to move up
                    continue;
                Collections.sort(selectedIdices);
                // Skip contiguous list starting at last child;
                int lastAllowedNode = columnNode.getChildCount()-1;
                for(int k=selectedIdices.size()-1; k>=0; k--){
                    if (selectedIdices.get(k).intValue()==lastAllowedNode){
                        lastAllowedNode--;
                        continue;
                    }
                    // An index that can be moved up
                    int currentPosition = selectedIdices.get(k).intValue();
                    if (currentPosition==columnNode.getChildCount()-1) continue;
                    TreeNode nodeToMove =  columnNode.getChildAt(currentPosition+1);
                    treeModel.removeNodeFromParent((MutableTreeNode)nodeToMove);
                    treeModel.insertNodeInto((MutableTreeNode) nodeToMove, (MutableTreeNode) columnNode, currentPosition);
                    int idx = treeModel.getIndexOfChild(columnNode.getParent(),columnNode);
                    getExcitationGridPanel().exchangePlots(currentPosition, currentPosition+1, idx);
               }
            }
             for(int i=0; i<selectedPathsVector.size(); i++){
                TreePath nextPath=selectedPathsVector.get(i);
               // Keep selected in sync.
                jExcitationsTree.addSelectionPath(nextPath);
            }
            //frame.pack();
        }
// TODO add your handling code here:
    }//GEN-LAST:event_jMoveDownButtonActionPerformed

    private void jMoveUpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMoveUpButtonActionPerformed
// TODO add your handling code here:
        if (somethingSelected){
            // move node dn in parent unless last node.
            //System.out.println("Size="+selectedPathsVector.size());
            // package it as a Move down
            Vector<TreeNode> pendingNodesVector=new Vector<TreeNode>(4);
            for(int i=0; i< selectedPathsVector.size(); i++){
                TreePath nextPath=selectedPathsVector.get(i);
                TreeNode lastNode=(TreeNode)nextPath.getLastPathComponent();
                ExcitationObject eo = (ExcitationObject) ((DefaultMutableTreeNode)lastNode).getUserObject();
                if (eo != null){
                    pendingNodesVector.add(lastNode);
                }
            }
            // For each column, find selected entries, sort top to bottom and move one step up
            DefaultMutableTreeNode root  = (DefaultMutableTreeNode) treeModel.getRoot();
            for(int i=0; i<root.getChildCount(); i++){
                // get Column's node
                DefaultMutableTreeNode columnNode = (DefaultMutableTreeNode) treeModel.getChild(root, i);
                ArrayList<Integer> selectedIdices = new ArrayList<Integer>();
                for(int j=0; j<columnNode.getChildCount(); j++){
                    DefaultMutableTreeNode exciationNode = (DefaultMutableTreeNode) treeModel.getChild(columnNode, j);
                    if (pendingNodesVector.contains(exciationNode)) 
                        selectedIdices.add(new Integer(j));
                }
                if (selectedIdices.size()==0)   // Nothing to move up
                    continue;
                Collections.sort(selectedIdices);
                // Skip contiguous list starting at 0;
                for(int k=0; k<selectedIdices.size(); k++){
                    if (selectedIdices.get(k).intValue()==k)
                        continue;
                    // An index that can be moved up
                    int currentPosition = selectedIdices.get(k).intValue();
                    TreeNode nodeToMove =  columnNode.getChildAt(currentPosition-1);
                    treeModel.removeNodeFromParent((MutableTreeNode)nodeToMove);
                    treeModel.insertNodeInto((MutableTreeNode) nodeToMove, (MutableTreeNode) columnNode, currentPosition);
                    getExcitationGridPanel().exchangePlots(currentPosition, currentPosition-1, root.getIndex(columnNode));
               }
            }

             for(int i=0; i<selectedPathsVector.size(); i++){
                TreePath nextPath=selectedPathsVector.get(i);
               // Keep selected in sync.
                jExcitationsTree.addSelectionPath(nextPath);
            }
            //frame.pack();
        }
    }//GEN-LAST:event_jMoveUpButtonActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ControlPanel;
    private javax.swing.JButton ExportExcitationsButton;
    private javax.swing.JCheckBox MinMaxShadingCheckBox1;
    private javax.swing.JCheckBox MinMaxShadingCheckBox2;
    private javax.swing.JCheckBox ShowExcBaseShapeCheckBox1;
    private javax.swing.JCheckBox ShowExcBaseShapeCheckBox2;
    private javax.swing.JCheckBox ShowMaxBaseShapeCheckBox1;
    private javax.swing.JCheckBox ShowMaxBaseShapeCheckBox2;
    private javax.swing.JCheckBox ShowMinBaseShapeCheckBox1;
    private javax.swing.JCheckBox ShowMinBaseShapeCheckBox2;
    private javax.swing.JPanel displayControlPanel;
    private javax.swing.JPanel displayControlPanel1;
    private javax.swing.JButton jApplyValueToSelectedButton;
    private javax.swing.JButton jBackupAllButton;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jDeleteButton;
    private javax.swing.JScrollPane jExScrollPane;
    private javax.swing.JTree jExcitationsTree;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jLayoutControlPanel;
    private javax.swing.JPanel jLeftPanel;
    private javax.swing.JButton jMoveDownButton;
    private javax.swing.JButton jMoveUpButton;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JButton jRemoveNodesButton;
    private javax.swing.JButton jRestoreAllButton;
    private javax.swing.JPanel jRightPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JButton jSetValueSelectedPointsButton;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JFormattedTextField jValueToFormattedTextField;
    // End of variables declaration//GEN-END:variables

   /** 
     * Creates an overlaid chart.
     *
     * @return The chart.
     */
    private void createExcitationColumnPanel(int colIndex, String[] names) {

        DefaultMutableTreeNode columnNode = new DefaultMutableTreeNode("Column "+String.valueOf(colIndex));
        treeModel.insertNodeInto(columnNode, (DefaultMutableTreeNode) treeModel.getRoot(), colIndex);
        getExcitationGridPanel().addColumn("Column "+String.valueOf(colIndex));
        columnNode.setUserObject(getExcitationGridPanel().getExcitationColumn(colIndex));
        if (getControlSet()==null) return;  // There's no controls to use!'
        for(int i=0; i<names.length; i++){
            Control nextControl = null;
            if (getControlSet().contains(names[i]))
                nextControl = getControlSet().get(names[i]);
            else if (getControlSet().contains(getLongName(names[i])))
                nextControl = getControlSet().get(getLongName(names[i]));
             if (nextControl==null)
                 continue;
            addExcitation(columnNode, nextControl, colIndex);
        } 
        validate();
    }
    /**
     * The sole place to add excitations to the window. It handles both the panel and the tree
     * Also makes sure that preferences for base shap display are observed.
     */
    public void addExcitation(final DefaultMutableTreeNode columnNode, final Control nextControl, final int colIndex) {
        // Build an array of OpenSim::Functions for value, min, max
        Vector<XYFunctionInterface> functions = new Vector<XYFunctionInterface>(3);
        ControlLinear control = ControlLinear.safeDownCast(nextControl);
        // Create a panel to hold the control/min/max and return the array of underlying OpenSim::Functions
        ExcitationPanel nextExcitationPanel = createPanel(control, functions); 
        // Set some parameters of the renderer
        //.showBaseShape(0, ((JCheckBox)evt.getSource()).isSelected());
        nextExcitationPanel.showBaseShape(0, ShowExcBaseShapeCheckBox2.isSelected());
        nextExcitationPanel.showBaseShape(1, ShowMinBaseShapeCheckBox2.isSelected());
        nextExcitationPanel.showBaseShape(2, ShowMaxBaseShapeCheckBox2.isSelected());
        // commented out: sample usage of MIN_MAX_EXC shading mode
        //renderer.setFillMode(ExcitationFillMode.MIN_MAX_EXC);
        //renderer.setMaxFillPaint(new Color(150, 150, 250));
        //renderer.setExcFillPaint(new Color(150, 150, 150));
        //renderer.setMinFillPaint(new Color(250, 150, 150));
        ExcitationRenderer renderer = (ExcitationRenderer)nextExcitationPanel.getRenderer();
        if (MinMaxShadingCheckBox2.isSelected())
           renderer.setFillMode(ExcitationFillMode.MIN_MAX);
        else
           renderer.setFillMode(ExcitationFillMode.NONE);
        renderer.setMinMaxFillPaint(new Color(220, 220, 220));
        // Handle addition to the tree
        ExcitationObject excitationNode= new ExcitationObject(nextExcitationPanel, nextControl.getName());
        excitationNode.setUserObject(excitationNode);
        treeModel.insertNodeInto((MutableTreeNode)excitationNode, (MutableTreeNode)columnNode, columnNode.getChildCount());
        jExcitationsTree.scrollPathToVisible(new TreePath(excitationNode.getPath()));
        // Handle addition to the panel of excitations
        Vector<XYFunctionInterface> xyFunctions = new Vector<XYFunctionInterface>(3);
        xyFunctions.add(functions.get(0));
        xyFunctions.add(functions.get(1));
        xyFunctions.add(functions.get(2));
        getExcitationGridPanel().addExcitationPanel(colIndex, nextExcitationPanel, control, xyFunctions);
    }



    void populate(ControlSet obj, boolean showFilteringDialog) {
       setControlSet(obj);
      
       getExcitationGridPanel().setControlSet(obj);
       jExScrollPane.setViewportView(getExcitationGridPanel());
       
       int numColumns= ((DefaultMutableTreeNode)treeModel.getRoot()).getChildCount();
        ArrayStr names = new ArrayStr();
        obj.getNames(names);
        for(int i=0; i< names.getSize(); i++){
            names.setitem(i, getShortName(names.getitem(i)));
        }
       removeDisplayedItems((DefaultMutableTreeNode)treeModel.getRoot(), names);
       FilterableStringArray namesSource = new FilterableStringArray(names);
       NameFilterJPanel filterPanel = new NameFilterJPanel(namesSource, false);
       OpenSimDialog selectionDlg=DialogUtils.createDialogForPanelWithParent(frame, filterPanel, "Select Excitations");
       DialogUtils.addStandardButtons(selectionDlg);
       selectionDlg.setModal(true);
       selectionDlg.setVisible(true);

       if (selectionDlg.getDialogReturnValue()==selectionDlg.OK_OPTION) {
           String[] selNames = new String[filterPanel.getNumSelected()];
           System.arraycopy(filterPanel.getSelected(), 0, selNames, 0, filterPanel.getNumSelected());
           int numExcitationColumns = selNames.length/MAX_EXCITATIONS_PER_COLUMN;
           String[] currentColNames = new String[MAX_EXCITATIONS_PER_COLUMN];
           for(int col=0; col<numExcitationColumns; col++){
               System.arraycopy(filterPanel.getSelected(), col*MAX_EXCITATIONS_PER_COLUMN, currentColNames, 0, MAX_EXCITATIONS_PER_COLUMN);
               numColumns= ((DefaultMutableTreeNode)treeModel.getRoot()).getChildCount();
               createExcitationColumnPanel(numColumns, currentColNames);
           }
           numColumns= ((DefaultMutableTreeNode)treeModel.getRoot()).getChildCount();
           int remainingItemsCount = filterPanel.getSelected().length-numExcitationColumns*MAX_EXCITATIONS_PER_COLUMN;
           if (remainingItemsCount!=0){
               currentColNames = new String[remainingItemsCount];
               System.arraycopy(filterPanel.getSelected(), numExcitationColumns*MAX_EXCITATIONS_PER_COLUMN, 
                                currentColNames, 0, remainingItemsCount);
               createExcitationColumnPanel(numColumns, currentColNames);
           }
           
       }
       else
           return;

       frame.setExtendedState(Frame.MAXIMIZED_VERT);
       //frame.doLayout();
       frame.validate();
    }

    public void valueChanged(TreeSelectionEvent e) {
       TreePath[] selectedPaths = e.getPaths();
      somethingSelected=false;
      for(int i=0;i<selectedPaths.length;i++){
         ExcitationObject eo = null;
         ExcitationColumnJPanel eCol = null;
         TreeNode lastNode=(TreeNode)selectedPaths[i].getLastPathComponent();
         if (((DefaultMutableTreeNode)lastNode).getUserObject() instanceof ExcitationObject){
            eo = (ExcitationObject) ((DefaultMutableTreeNode)lastNode).getUserObject();
         }
         else if (((DefaultMutableTreeNode)lastNode).getUserObject() instanceof ExcitationColumnJPanel){
            eCol = (ExcitationColumnJPanel) ((DefaultMutableTreeNode)lastNode).getUserObject();
         }
         if (e.isAddedPath(i)){
             if (eo != null){
                selectedPathsVector.add(selectedPaths[i]);
                eo.markSelected(true);
                somethingSelected=true;
             }
             else if (eCol != null) {
                selectedColumnsVector.add(selectedPaths[i]);                 
             }
         } else {   //Removed
             if (eo != null){
                selectedPathsVector.remove(selectedPaths[i]);
                eo.markSelected(false);
             }
             else if (eCol != null) {
                selectedColumnsVector.remove(selectedPaths[i]);                 
             }
         }
      }
     // Enable buttons to move only if  (somethingSelected)
      jMoveUpButton.setEnabled(somethingSelected);
      jMoveDownButton.setEnabled(somethingSelected);
      jDeleteButton.setEnabled(somethingSelected);
      // Same for Export and Simpify
      //SimplfyExcitationsButton.setEnabled(somethingSelected);
      ExportExcitationsButton.setEnabled(somethingSelected);
      
   }
   
   static String filterExcitationName(String fullName)
   {
       String filtered = fullName;
       if (fullName.contains(".")){
           filtered = filtered.substring(0, fullName.indexOf('.')); 
       }
       return filtered;
   }
   
   static ExcitationPanel createPanel(ControlLinear excitation, Vector<XYFunctionInterface> functions)
   {
         ControlLinear cl = ControlLinear.safeDownCast(excitation);
         XYSeriesCollection seriesCollection = new XYSeriesCollection();
         
         FunctionXYSeries xySeries = new FunctionXYSeries("excitation");
         SetControlNodes cnodes = cl.getControlValues();
         XYFunctionInterface ctrlFunction = createFunctionFromControlLinear(xySeries, cnodes, !cl.getUseSteps());
         int np = ctrlFunction.getNumberOfPoints();
         functions.add(ctrlFunction);
         seriesCollection.addSeries(xySeries);
         
         FunctionXYSeries xySeriesMin = new FunctionXYSeries("min");
         SetControlNodes minNodes = cl.getControlMinValues();
         XYFunctionInterface minFunction = createFunctionFromControlLinear(xySeriesMin, minNodes, false);
         functions.add(minFunction);
         seriesCollection.addSeries(xySeriesMin);
        
         FunctionXYSeries xySeriesMax = new FunctionXYSeries("max");
         SetControlNodes maxNodes = cl.getControlMaxValues();
         XYFunctionInterface maxFunction = createFunctionFromControlLinear(xySeriesMax, maxNodes, false);
         functions.add(maxFunction);
         seriesCollection.addSeries(xySeriesMax);
         
         JFreeChart chart = FunctionPanel.createFunctionChart(
                    "", "", "", seriesCollection,
                    true, true);
         FunctionPlot xyPlot = (FunctionPlot)chart.getXYPlot();
         XYDataset xyDataset = xyPlot.getDataset();

         Vector<XYFunctionInterface> xyFunctions = new Vector<XYFunctionInterface>(functions.size());
         for (int i=0; i<functions.size(); i++)
             xyFunctions.add(functions.get(i));

         ExcitationRenderer renderer = new ExcitationRenderer(excitation, functions);
         ValueAxis va = xyPlot.getRangeAxis();
         if (va instanceof NumberAxis) {
            NumberAxis na = (NumberAxis) va;
            na.setAutoRangeIncludesZero(false);
            na.setAutoRangeStickyZero(false);
            xyPlot.setRangeAxis(na);
            na.setNumberFormatOverride(new DecimalFormat("0.0000"));
         }
         xyPlot.setRenderer(renderer);
         ExcitationPanel dPanel =  new ExcitationPanel(chart);
         
         return dPanel;
   }


    public static XYFunctionInterface createFunctionFromControlLinear(final FunctionXYSeries xySeries, final SetControlNodes cnodes, boolean useLinear) {
        Function ctrlFunction=null;
        XYFunctionInterface xyFunction=null;
        if (!useLinear){ // Step function
            ctrlFunction = new PiecewiseConstantFunction();
            xyFunction = new XYFunctionInterface(ctrlFunction, true);
            for (int i=0; i<cnodes.getSize(); i++) {
               ControlLinearNode clnode = cnodes.get(i);
               xyFunction.addPoint(clnode.getTime(), clnode.getValue());
               xySeries.add(clnode.getTime(), clnode.getValue());
            }
        }
        else { // Linear function
            ctrlFunction = new PiecewiseLinearFunction();
            xyFunction = new XYFunctionInterface(ctrlFunction, true);
            for (int i=0; i<cnodes.getSize(); i++) {
               ControlLinearNode clnode = cnodes.get(i);
               xyFunction.addPoint(clnode.getTime(), clnode.getValue());
               xySeries.add(clnode.getTime(), clnode.getValue());
            }
            
        }
        return xyFunction;
    }
   
   private void invokeTreePopupIfNeeded(int evtX, int evtY) {
      TreePath useClickedElement = jExcitationsTree.getPathForLocation(evtX, evtY);
      JPopupMenu contextMenu = new JPopupMenu();
      DefaultMutableTreeNode clickedNode=null;
      Object  clickedObject = null;
      if (useClickedElement==null){    // treat as root
          clickedNode=(DefaultMutableTreeNode)treeModel.getRoot();
          useClickedElement = new TreePath(clickedNode.getPath());
      }
      final TreePath clickedElement=useClickedElement;
      if (clickedElement.getLastPathComponent() instanceof DefaultMutableTreeNode){
          clickedNode = (DefaultMutableTreeNode)clickedElement.getLastPathComponent();
          clickedObject = clickedNode.getUserObject();
      }
      
      if (clickedNode.equals(treeModel.getRoot()) && getControlSet()!=null){
          // allow addition of a new column
            JMenuItem addColMenuItem = new JMenuItem("Add Column...");
            addColMenuItem.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    ArrayStr names = getUndisplayedExcitations();
                    FilterableStringArray namesSource = new FilterableStringArray(names);
                    NameFilterJPanel filterPanel = new NameFilterJPanel(namesSource, false);
                    OpenSimDialog selectionDlg=DialogUtils.createDialogForPanelWithParent(frame, filterPanel, "Select Excitations");
                    DialogUtils.addStandardButtons(selectionDlg);
                    selectionDlg.setModal(true);
                    selectionDlg.setVisible(true);
                    
                    int numColumns= ((DefaultMutableTreeNode)treeModel.getRoot()).getChildCount();
                    if (selectionDlg.getDialogReturnValue()==selectionDlg.OK_OPTION) {
                        String[] selNames = new String[filterPanel.getNumSelected()];
                        System.arraycopy(filterPanel.getSelected(), 0, selNames, 0, filterPanel.getNumSelected());
                        for (int i=0; i<filterPanel.getNumSelected(); i++)
                            selNames[i] = getShortName(selNames[i]);
                        createExcitationColumnPanel(numColumns, selNames);
                     }

                }

});
          contextMenu.add(addColMenuItem );
      }
      if (clickedObject instanceof ExcitationColumnJPanel){
          // Add item to change display name
          final ExcitationColumnJPanel columnPanel = (ExcitationColumnJPanel) clickedObject;
          JMenuItem renameMenuItem = new JMenuItem("Rename...");
            renameMenuItem.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                     NotifyDescriptor.InputLine dlg =
                            new NotifyDescriptor.InputLine("Current Name: "+columnPanel.toString(), "Rename Object");
                     if(DialogDisplayer.getDefault().notify(dlg)==NotifyDescriptor.OK_OPTION){
                        columnPanel.setLabel( dlg.getInputText());
                     }
                }
            });
          JMenuItem appendMenuItem = new JMenuItem("Append...");
            appendMenuItem.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    ArrayStr names = getUndisplayedExcitations();
                    FilterableStringArray namesSource = new FilterableStringArray(names);
                    NameFilterJPanel filterPanel = new NameFilterJPanel(namesSource, false);
                    OpenSimDialog selectionDlg=DialogUtils.createDialogForPanelWithParent(frame, filterPanel, "Select Excitations");
                    DialogUtils.addStandardButtons(selectionDlg);
                    selectionDlg.setModal(true);
                    selectionDlg.setVisible(true);
                    
                    int numColumns= ((DefaultMutableTreeNode)treeModel.getRoot()).getChildCount();
                    if (selectionDlg.getDialogReturnValue()==selectionDlg.OK_OPTION) {
                        String[] selNames = new String[filterPanel.getNumSelected()];
                        System.arraycopy(filterPanel.getSelected(), 0, selNames, 0, filterPanel.getNumSelected());
                        for (int i=0; i<filterPanel.getNumSelected(); i++)
                            selNames[i] = getShortName(selNames[i]);
                        // Add selected names to proper column Panel
                        DefaultMutableTreeNode clickedNode = (DefaultMutableTreeNode)clickedElement.getLastPathComponent();
                        int colIndex = treeModel.getIndexOfChild(clickedNode.getParent(),clickedNode);
                        //treeModel.insertNodeInto(clickedNode, (DefaultMutableTreeNode) treeModel.getRoot(), colIndex);
                        for(int i=0; i<selNames.length; i++){
                            Control nextControl = getControlSet().get(selNames[i]);
                            if (nextControl==null)
                                nextControl = getControlSet().get(getLongName(selNames[i]));
                            addExcitation(clickedNode, nextControl, colIndex);
                        } 
                        frame.validate();
                     }
                }
            });
            /* Removed explicit "Remove" since delete button will do.
          JMenuItem removeMenuItem = new JMenuItem("Remove");
            removeMenuItem.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    int numColumns= ((DefaultMutableTreeNode)treeModel.getRoot()).getChildCount();
                    DefaultMutableTreeNode clickedNode = (DefaultMutableTreeNode)clickedElement.getLastPathComponent();
                    ExcitationColumnJPanel columnPanel = (ExcitationColumnJPanel)clickedNode.getUserObject();
                    int indexToRemove = treeModel.getIndexOfChild(clickedNode.getParent(),clickedNode);
                    treeModel.removeNodeFromParent(clickedNode);
                    excitationGridPanel.removeColumn(columnPanel);
                    excitationGridPanel.repaint();  // Sounds wrong but have no other way to force refresh of display
                    frame.validate();
                }
            });
           */
           contextMenu.add(renameMenuItem);
           contextMenu.add(appendMenuItem);
           //contextMenu.add(removeMenuItem);
      }
      contextMenu.show(jExcitationsTree, evtX, evtY);
   }

    public static String getShortName(String string) {
        int idx= string.indexOf(".excitation");
        if (idx!=-1)
            return string.substring(0, idx);
        else
            return string;
    }

    public static String getLongName(String string) {
        return string+".excitation";
    }
    
   class handleDelete extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
             jDeleteButtonActionPerformed(null);
        }           
   }
   /**
    * Handler of return button in Value
    */
   class handleReturnAction extends AbstractAction {
       public void actionPerformed(ActionEvent e) {
        // Cycle thru all excitation panels and apply operation
        String valueString=jValueToFormattedTextField.getText();
        try {
            Double.parseDouble(valueString);
        } catch (NumberFormatException nfe) {
            return;
        }
        double valueDouble = Double.valueOf(valueString);
        getExcitationGridPanel().applyValueToSelectedNodes(valueDouble);

       }
   }

    public ControlSet getControlSet() {
        return controlSet;
    }

    public void setControlSet(ControlSet controlSet) {
        this.controlSet = controlSet;
    }

    private void removeDisplayedItems(DefaultMutableTreeNode treeNode, ArrayStr names) {
        int count = treeNode.getChildCount();
        for( int i=0;  i < count;  i++ ) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) treeNode.getChildAt(i);
            if ( child.isLeaf())
                names.remove(names.findIndex(child.toString()));
            else
                removeDisplayedItems( child, names );
        }
    }

    private ArrayStr getUndisplayedExcitations() {
        // Bring up the filter dialog populated by list of names from current control set
        ArrayStr names = new ArrayStr();
        getControlSet().getNames(names);
        for(int i=0; i< names.getSize(); i++){
            names.setitem(i, getShortName(names.getitem(i)));
        }
        removeDisplayedItems((DefaultMutableTreeNode)treeModel.getRoot(), names);
        return names;
    }

    private void toggleDisplay(int evtX, int evtY) {
      final TreePath clickedElement = jExcitationsTree.getPathForLocation(evtX, evtY);
      JPopupMenu contextMenu = new JPopupMenu();
      DefaultMutableTreeNode clickedNode=null;
      Object  clickedObject = null;
      if (clickedElement==null)
          return;
      if (clickedElement.getLastPathComponent() instanceof DefaultMutableTreeNode){
          clickedNode = (DefaultMutableTreeNode)clickedElement.getLastPathComponent();
          clickedObject = clickedNode.getUserObject();
      }
      
      if (clickedObject instanceof ExcitationObject){
          ExcitationColumnJPanel parentPanel = (ExcitationColumnJPanel)(((DefaultMutableTreeNode)clickedNode.getParent()).getUserObject());
          ExcitationPanel dPanel = ((ExcitationObject) clickedObject).getPlotPanel();
          parentPanel.toggle(dPanel);
      }
    }

    public ExcitationsGridJPanel getExcitationGridPanel() {
        return excitationGridPanel;
    }

    void clear() {
        DefaultMutableTreeNode root = ((DefaultMutableTreeNode)treeModel.getRoot());
        if (root==null) return;
        
        int numColumns= root.getChildCount();
        for (int i=numColumns-1; i>= 0; i--){
            DefaultMutableTreeNode columnNode = (DefaultMutableTreeNode)root.getChildAt(i);
            // Remove node
            treeModel.removeNodeFromParent(columnNode);
            // Remove Panel
             getExcitationGridPanel().removeColumn((ExcitationColumnJPanel)columnNode.getUserObject());
        }
    }

    void applyLayoutFromFile(String fileName) {
      try {
         InputStream inputStream = new FileInputStream(fileName);
         BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
         int numColumns = Integer.parseInt(reader.readLine());
         ExcitationsGridJPanel gridPanel = getExcitationGridPanel();
         if (getControlSet()==null) return; // Probably should warn
         // Remove existing columns if any
         clear();
         for(int i=0; i<numColumns; i++){
             // Parse column name and entry names
             String columnName = reader.readLine();
             int numEntries = Integer.parseInt(reader.readLine());
             String[] names = new String[numEntries];
             for(int j=0; j< numEntries; j++){
                 String nextName = reader.readLine();
                 names[j]=nextName;
             }
             createExcitationColumnPanel(i, names);
             getExcitationGridPanel().getColumn(i).setColumnNameLabelText(columnName);
         }
      } catch (IOException ex) {
          ErrorDialog.displayExceptionDialog(ex);
      }
    }

}
