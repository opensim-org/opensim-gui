/* -------------------------------------------------------------------------- *
 * OpenSim: CameraEditorPanel.java                                            *
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

package org.opensim.view;

import java.util.Observable;
import java.util.Observer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import org.opensim.utils.FileUtils;
import org.opensim.view.pub.ViewDB;

//===========================================================================
// cameraKeyFramesTableModel
//===========================================================================
class CameraKeyFramesTableModel extends AbstractTableModel implements Observer {
   private String[] columnNames = new String[]{"time", "configuration"};
   private Camera camera = null;

   public CameraKeyFramesTableModel() {
      CameraDB.getInstance().addObserver(this);
   }

   public void setCamera(Camera camera) { 
      this.camera = camera;
      fireTableDataChanged();
   }

   public void update(Observable observable, Object obj) {
      if(!observable.equals(CameraDB.getInstance())) return;
      CameraEvent evt=(CameraEvent)obj;
      switch(evt.getOperation()) {
         case AllDataChanged:
         case CameraKeyFrameAdded:
         case CameraKeyFrameRemoved:
            fireTableDataChanged();
            break;
         case CameraKeyFrameModified:
            fireTableRowsUpdated(0,getRowCount());
            break;
         default:
            break;
      }
   }

   public int getColumnCount() { return columnNames.length; }
   public int getRowCount() { return camera!=null ? camera.getNumKeyFrames() : 0; }
   public String getColumnName(int col) { return columnNames[col]; }

   public Class getColumnClass(int col) {
      if(col==0) return Double.class;
      else if(col==1) return String.class;
      else return String.class;
   }

   public Object getValueAt(int row, int col) {
      if(col==0) return (Double)camera.getKeyFrameTime(row);
      else if(col==1) return camera.getKeyFrameConfiguration(row).toString();
      else return null;
   }

   public boolean isCellEditable(int row, int col) {
      return (col==0);
   }


   public void setValueAt(Object value, int row, int col) {
      if(col==0) camera.setKeyFrameTime(row, ((Double)value).doubleValue());
   }
}

//===========================================================================
// CamerasListTableModel
//===========================================================================
class CamerasListTableModel extends AbstractTableModel implements Observer {
   public CamerasListTableModel() {
      CameraDB.getInstance().addObserver(this);
   }

   public void update(Observable observable, Object obj) {
      if(!observable.equals(CameraDB.getInstance())) return;
      CameraEvent evt=(CameraEvent)obj;
      switch(evt.getOperation()) {
         case AllDataChanged:
         case CameraAdded:
         case CameraRemoved:
            fireTableDataChanged();
            break;
         case CameraRenamed:
            fireTableRowsUpdated(0,getRowCount());
            break;
         default:
            break;
      }
   }

   public int getColumnCount() { return 1; }
   public int getRowCount() { return CameraDB.getInstance().getNumCameras(); }
   public String getColumnName(int col) { return null; }
   
   public Class getColumnClass(int col) { return String.class; }
   public Object getValueAt(int row, int col) {
      return CameraDB.getInstance().getCamera(row).getName();
   }
   public boolean isCellEditable(int row, int col) { return false; }
   public void setValueAt(Object value, int row, int col) { }
}

//===========================================================================
// CameraEditorPanel
//===========================================================================
public class CameraEditorPanel extends javax.swing.JPanel implements Observer {
   CamerasListTableModel camerasListTableModel = new CamerasListTableModel();
   CameraKeyFramesTableModel cameraKeyFramesTableModel = new CameraKeyFramesTableModel();
    
   public CameraEditorPanel() {
      initComponents();
      CameraDB.getInstance().addObserver(this);
      camerasListTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
      camerasListTable.setTableHeader(null);
      camerasListTable.setModel(camerasListTableModel);
      camerasListTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      camerasListTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent event) {
            if(event.getValueIsAdjusting()) return;
            int row = camerasListTable.getSelectedRow();
            if(row==-1) cameraKeyFramesTableModel.setCamera(null);
            else cameraKeyFramesTableModel.setCamera(CameraDB.getInstance().getCamera(row));
         }
      });

      cameraKeyFramesTableModel.setCamera(null);
      cameraKeyFramesTable.setModel(cameraKeyFramesTableModel);
      cameraKeyFramesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      cameraKeyFramesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent event) {
            if(event.getValueIsAdjusting()) return;
         }
      });

      int timeColumnWidth = 50;
      cameraKeyFramesTable.getColumnModel().getColumn(0).setPreferredWidth(timeColumnWidth);
      cameraKeyFramesTable.getColumnModel().getColumn(0).setMinWidth(timeColumnWidth);
      cameraKeyFramesTable.getColumnModel().getColumn(0).setMaxWidth(timeColumnWidth);
   }

   public void update(Observable observable, Object obj) {
      updatePanel();
   }

   private void updatePanel() {

   }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
   // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
   private void initComponents() {
      jPanel1 = new javax.swing.JPanel();
      loadButton = new javax.swing.JButton();
      saveButton = new javax.swing.JButton();
      addCameraButton = new javax.swing.JButton();
      removeCameraButton = new javax.swing.JButton();
      jScrollPane3 = new javax.swing.JScrollPane();
      camerasListTable = new javax.swing.JTable();
      jPanel2 = new javax.swing.JPanel();
      addKeyButton = new javax.swing.JButton();
      setFromViewButton = new javax.swing.JButton();
      jScrollPane2 = new javax.swing.JScrollPane();
      cameraKeyFramesTable = new javax.swing.JTable();
      applyToViewButton = new javax.swing.JButton();
      removeKeyButton = new javax.swing.JButton();

      jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Cameras"));
      loadButton.setText("Load");
      loadButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            loadButtonActionPerformed(evt);
         }
      });

      saveButton.setText("Save");
      saveButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            saveButtonActionPerformed(evt);
         }
      });

      addCameraButton.setText("Add");
      addCameraButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            addCameraButtonActionPerformed(evt);
         }
      });

      removeCameraButton.setText("Del");
      removeCameraButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            removeCameraButtonActionPerformed(evt);
         }
      });

      camerasListTable.setModel(new javax.swing.table.DefaultTableModel(
         new Object [][] {
            {null},
            {null},
            {null},
            {null}
         },
         new String [] {
            "Title 1"
         }
      ));
      jScrollPane3.setViewportView(camerasListTable);

      org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
      jPanel1.setLayout(jPanel1Layout);
      jPanel1Layout.setHorizontalGroup(
         jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
         .add(jPanel1Layout.createSequentialGroup()
            .addContainerGap()
            .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
               .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane3, 0, 0, Short.MAX_VALUE)
               .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1Layout.createSequentialGroup()
                  .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                     .add(org.jdesktop.layout.GroupLayout.LEADING, addCameraButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                     .add(org.jdesktop.layout.GroupLayout.LEADING, loadButton))
                  .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                  .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                     .add(saveButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                     .add(removeCameraButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 57, Short.MAX_VALUE))))
            .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
      );

      jPanel1Layout.linkSize(new java.awt.Component[] {addCameraButton, loadButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

      jPanel1Layout.setVerticalGroup(
         jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
         .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
            .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
               .add(addCameraButton)
               .add(removeCameraButton))
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
               .add(loadButton)
               .add(saveButton))
            .addContainerGap())
      );

      jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Camera Details"));
      addKeyButton.setText("Add");
      addKeyButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            addKeyButtonActionPerformed(evt);
         }
      });

      setFromViewButton.setText("From Current View");
      setFromViewButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            setFromViewButtonActionPerformed(evt);
         }
      });

      cameraKeyFramesTable.setModel(new javax.swing.table.DefaultTableModel(
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
      jScrollPane2.setViewportView(cameraKeyFramesTable);

      applyToViewButton.setText("Set Current View");
      applyToViewButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            applyToViewButtonActionPerformed(evt);
         }
      });

      removeKeyButton.setText("Del");
      removeKeyButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            removeKeyButtonActionPerformed(evt);
         }
      });

      org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
      jPanel2.setLayout(jPanel2Layout);
      jPanel2Layout.setHorizontalGroup(
         jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
         .add(jPanel2Layout.createSequentialGroup()
            .addContainerGap()
            .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
               .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE)
               .add(jPanel2Layout.createSequentialGroup()
                  .add(addKeyButton)
                  .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                  .add(removeKeyButton)
                  .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                  .add(setFromViewButton)
                  .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                  .add(applyToViewButton)))
            .addContainerGap())
      );

      jPanel2Layout.linkSize(new java.awt.Component[] {addKeyButton, removeKeyButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

      jPanel2Layout.linkSize(new java.awt.Component[] {applyToViewButton, setFromViewButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

      jPanel2Layout.setVerticalGroup(
         jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
         .add(jPanel2Layout.createSequentialGroup()
            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
               .add(addKeyButton)
               .add(applyToViewButton)
               .add(setFromViewButton)
               .add(removeKeyButton))
            .addContainerGap())
      );

      org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(
         layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
         .add(layout.createSequentialGroup()
            .addContainerGap()
            .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addContainerGap())
      );
      layout.setVerticalGroup(
         layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
         .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
            .addContainerGap()
            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
               .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
               .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addContainerGap())
      );
   }// </editor-fold>//GEN-END:initComponents

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
      // TODO: file chooser
      String fileName = FileUtils.getInstance().browseForFilenameToSave(FileUtils.getFileFilter(".cam", "Save camera dolly settings file"), true, "dollyFile.cam");
      if(fileName != null){
        CameraDB.getInstance().saveCameras(fileName);
      }
    }//GEN-LAST:event_saveButtonActionPerformed

    private void loadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadButtonActionPerformed
      // TODO: file chooser
      String fileName = FileUtils.getInstance().browseForFilename(FileUtils.getFileFilter(".cam", "Load camera dolly settings file"));
       if(fileName != null){
        CameraDB.getInstance().loadCameras(fileName);
      }
    }//GEN-LAST:event_loadButtonActionPerformed

    private void removeCameraButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeCameraButtonActionPerformed
// TODO add your handling code here:
       int i = camerasListTable.getSelectedRow();
       if(i >= 0){
         CameraDB.instance.removeCamera(i);
       }
    }//GEN-LAST:event_removeCameraButtonActionPerformed

    private void addCameraButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addCameraButtonActionPerformed
       int nc = CameraDB.getInstance().getNumCameras()+1;
       String cameraName = "Camera ";
       cameraName = cameraName.concat(String.valueOf(nc));
       CameraDB.getInstance().createCamera(cameraName);
    }//GEN-LAST:event_addCameraButtonActionPerformed

    private void setFromViewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setFromViewButtonActionPerformed
      if(camerasListTable.getSelectedRow()>=0) {
         Camera camera = CameraDB.getInstance().getCamera(camerasListTable.getSelectedRow());
         if(camera==null) return;
         if(cameraKeyFramesTable.getSelectedRow()>=0) {
            int keyFrame = cameraKeyFramesTable.getSelectedRow();
            ModelWindowVTKTopComponent component = ViewDB.getInstance().getCurrentModelWindow();
            Camera.Configuration config = Camera.Configuration.getFromView(component.getCanvas());
            camera.setKeyFrameConfiguration(keyFrame, config);
         }
      }
    }//GEN-LAST:event_setFromViewButtonActionPerformed

    private void addKeyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addKeyButtonActionPerformed
      if(camerasListTable.getSelectedRow()>=0) {
         Camera camera = CameraDB.getInstance().getCamera(camerasListTable.getSelectedRow());
         if(camera==null) return;
         ModelWindowVTKTopComponent component = ViewDB.getInstance().getCurrentModelWindow();
         double time = component.getCanvas().getCurrentTime(); // TODO: need a better way to do this
         Camera.Configuration config = Camera.Configuration.getFromView(component.getCanvas());
         camera.addKeyFrame(time, config);
      }
    }//GEN-LAST:event_addKeyButtonActionPerformed
    
    private void removeKeyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeKeyButtonActionPerformed
      if(camerasListTable.getSelectedRow()>=0) {
         Camera camera = CameraDB.getInstance().getCamera(camerasListTable.getSelectedRow());
         if(camera==null) return;
         if(cameraKeyFramesTable.getSelectedRow()>=0) {
            int keyFrame = cameraKeyFramesTable.getSelectedRow();
            camera.removeKeyFrame(keyFrame);
         }
      }
    }//GEN-LAST:event_removeKeyButtonActionPerformed

    private void applyToViewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyToViewButtonActionPerformed
      if(camerasListTable.getSelectedRow()>=0) {
         Camera camera = CameraDB.getInstance().getCamera(camerasListTable.getSelectedRow());
         if(camera==null) return;
         if(cameraKeyFramesTable.getSelectedRow()>=0) {
            int keyFrame = cameraKeyFramesTable.getSelectedRow();
            ModelWindowVTKTopComponent component = ViewDB.getInstance().getCurrentModelWindow();
            component.getCanvas().applyCameraConfiguration(camera.getKeyFrameConfiguration(keyFrame), true);
         }
      }
    }//GEN-LAST:event_applyToViewButtonActionPerformed
    
   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JButton addCameraButton;
   private javax.swing.JButton addKeyButton;
   private javax.swing.JButton applyToViewButton;
   private javax.swing.JTable cameraKeyFramesTable;
   private javax.swing.JTable camerasListTable;
   private javax.swing.JPanel jPanel1;
   private javax.swing.JPanel jPanel2;
   private javax.swing.JScrollPane jScrollPane2;
   private javax.swing.JScrollPane jScrollPane3;
   private javax.swing.JButton loadButton;
   private javax.swing.JButton removeCameraButton;
   private javax.swing.JButton removeKeyButton;
   private javax.swing.JButton saveButton;
   private javax.swing.JButton setFromViewButton;
   // End of variables declaration//GEN-END:variables
    
}
