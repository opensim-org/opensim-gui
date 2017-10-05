/* -------------------------------------------------------------------------- *
 * OpenSim: AnalysisSetPanel.java                                             *
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
 * AnalysisSetPanel.java
 *
 * Created on July 30, 2007, 2:08 PM
 */

package org.opensim.tracking;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import org.opensim.modeling.Analysis;
import org.opensim.modeling.AnalysisSet;
import org.opensim.view.editors.ObjectEditDialogMaker;

//===========================================================================
// AnalysisSetTableModel
//===========================================================================
class AnalysisSetTableModel extends AbstractTableModel implements Observer {
   private String[] columnNames = new String[]{"Enabled", "Type", "Name"};
   private AbstractToolModel toolModel = null;

   AnalysisSetTableModel(AbstractToolModel toolModel) {
      this.toolModel = toolModel;
      toolModel.addObserver(this);
   }

   public void update(Observable observable, Object obj) {
      AbstractToolModel.Operation op = (AbstractToolModel.Operation)obj;
      switch(op) {
         case AllDataChanged: 
         case AnalysisAddedOrRemoved:
            fireTableDataChanged();
            break;
         case AnalysisDataChanged:
            fireTableRowsUpdated(0,getRowCount());
            break;
      }
   }

   public int getColumnCount() { return columnNames.length; }
   public int getRowCount() { return toolModel.getAnalysisSet().getSize(); }
   public String getColumnName(int col) { return columnNames[col]; }

   public Object getValueAt(int row, int col) {
      if(col==0) return toolModel.getAnalysisSet().get(row).getOn();
      else if(col==1) return toolModel.getAnalysisSet().get(row).getConcreteClassName();
      else if(col==2) return toolModel.getAnalysisSet().get(row).getName();
      else return null;
   }

   public Class getColumnClass(int col) {
      if(col==0) return Boolean.class;
      else if(col==1) return String.class;
      else if(col==2) return String.class;
      else return null;
   }

   public boolean isCellEditable(int row, int col) {
      return col==0 || col==2; // Enabled and name fields are editable
   }

   public void setValueAt(Object value, int row, int col) {
      if(col==0) { toolModel.getAnalysisSet().get(row).setOn(((Boolean)value).booleanValue()); }
      else if(col==2) { toolModel.getAnalysisSet().get(row).setName((String)value); }
      toolModel.analysisModified(row);
   }
}

//===========================================================================
// AnalysisSetPanel
//===========================================================================
public class AnalysisSetPanel extends javax.swing.JPanel implements Observer {
   private final static int enabledColumnWidth = 60;
   private AnalysisSet availableAnalyses = new AnalysisSet();
   private int currentSelectedRow = -1;
   private AbstractToolModel toolModel = null;

   class AddAnalysisAction extends AbstractAction {
      private Analysis analysis;
      public AddAnalysisAction(Analysis analysis) {
         super(analysis.getConcreteClassName());
         this.analysis = analysis;
      }
      public void actionPerformed(ActionEvent evt) {
         toolModel.addCopyOfAnalysis(analysis);
      }
   }
   /** Creates new form AnalysisSetPanel */
   public AnalysisSetPanel(AbstractToolModel toolModel) {
      this.toolModel = toolModel;

      initComponents();
      analysisSetTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
      // Create popup menu for addButton (all available analyses)
      AnalysisSet.getAvailableAnalyses(availableAnalyses);
      addButton.addMouseListener(new MouseAdapter() {
         public void mousePressed(MouseEvent evt) {
            JPopupMenu popup = new JPopupMenu();
            for(int i=0; i<availableAnalyses.getSize(); i++)
               popup.add(new JMenuItem(new AddAnalysisAction(availableAnalyses.get(i))));
            popup.show(evt.getComponent(),evt.getX(),evt.getY());
      }});

      // Initialize table
      analysisSetTable.setModel(new AnalysisSetTableModel(toolModel));
      analysisSetTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      analysisSetTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent event) {
            if(event.getValueIsAdjusting()) return;
            tableSelectionChanged();
         }
      });
      // Adjust width of "Enabled" column
      analysisSetTable.getColumnModel().getColumn(0).setPreferredWidth(enabledColumnWidth);
      analysisSetTable.getColumnModel().getColumn(0).setMinWidth(enabledColumnWidth);
      analysisSetTable.getColumnModel().getColumn(0).setMaxWidth(enabledColumnWidth);

      updatePanel();

      toolModel.addObserver(this);
   }

   public void update(Observable observable, Object obj) {
      updatePanel();
   }

   private void tableSelectionChanged() {
      currentSelectedRow = analysisSetTable.getSelectedRow();
      updatePanel();
   }

   private void updatePanel() {
      if(currentSelectedRow>=0) {
         editButton.setEnabled(true);
         deleteButton.setEnabled(true);
      } else {
         editButton.setEnabled(false);
         deleteButton.setEnabled(false);
      }
   }
   
   /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
   // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
   private void initComponents() {
      jScrollPane1 = new javax.swing.JScrollPane();
      analysisSetTable = new javax.swing.JTable();
      deleteButton = new javax.swing.JButton();
      addButton = new javax.swing.JButton();
      editButton = new javax.swing.JButton();

      analysisSetTable.setModel(new javax.swing.table.DefaultTableModel(
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
      jScrollPane1.setViewportView(analysisSetTable);

      deleteButton.setText("Delete");
      deleteButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            deleteButtonActionPerformed(evt);
         }
      });

      addButton.setText("Add >");

      editButton.setText("Edit");
      editButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            editButtonActionPerformed(evt);
         }
      });

      org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(
         layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
         .add(layout.createSequentialGroup()
            .addContainerGap()
            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
               .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 559, Short.MAX_VALUE)
               .add(layout.createSequentialGroup()
                  .add(addButton)
                  .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                  .add(editButton)
                  .add(5, 5, 5)
                  .add(deleteButton)))
            .addContainerGap())
      );

      layout.linkSize(new java.awt.Component[] {addButton, deleteButton, editButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

      layout.setVerticalGroup(
         layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
         .add(layout.createSequentialGroup()
            .addContainerGap()
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
               .add(addButton)
               .add(deleteButton)
               .add(editButton))
            .addContainerGap())
      );
   }// </editor-fold>//GEN-END:initComponents

   private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
      if(currentSelectedRow>=0) {
         Analysis analysis = toolModel.getAnalysisSet().get(currentSelectedRow);
         Analysis analysisCopy = Analysis.safeDownCast(analysis.clone()); // C++-side copy
         ObjectEditDialogMaker editorDialog = new ObjectEditDialogMaker(analysisCopy, true, "OK");
         if(editorDialog.process()) {
            toolModel.replaceAnalysis(currentSelectedRow, analysisCopy);
         } else {
            // Do nothing if edit was canceled
         }
      }
   }//GEN-LAST:event_editButtonActionPerformed

   private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
      if(currentSelectedRow>=0) toolModel.removeAnalysis(currentSelectedRow);
   }//GEN-LAST:event_deleteButtonActionPerformed
   
   
   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JButton addButton;
   private javax.swing.JTable analysisSetTable;
   private javax.swing.JButton deleteButton;
   private javax.swing.JButton editButton;
   private javax.swing.JScrollPane jScrollPane1;
   // End of variables declaration//GEN-END:variables
   
}
