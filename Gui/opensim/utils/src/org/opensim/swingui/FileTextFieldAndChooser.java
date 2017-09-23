/* -------------------------------------------------------------------------- *
 * OpenSim: FileTextFieldAndChooser.java                                      *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib, Christopher Dembia, Kevin Xu                       *
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

package org.opensim.swingui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import org.opensim.utils.BrowserLauncher;
import org.opensim.utils.FileUtils;

public class FileTextFieldAndChooser extends javax.swing.JPanel implements ActionListener {

   private static Color validBackground = Color.white;
   private static Color invalidBackground = new Color(255,102,102);

   private FileFilter filter = null;
   private boolean directoriesOnly = false;

   private String lastFileName; // keep track of previous value to avoid firing change events when name stays the same
   private boolean fileIsValid = true;
   private boolean treatUnassignedAsEmptyString = true;
   private boolean treatEmptyStringAsValid = false;
   private boolean checkIfFileExists = true;

   private JCheckBox checkBox = null; // Optional associated check box which enables/disables the text field
   private boolean saveMode = false;   // Whether to bring the file chooser in Open or Save mode
   private Frame ownerFrame=null;

   public FileTextFieldAndChooser() {
      initComponents();
      lastFileName = fileNameTextField.getText();
   }

   public FileTextFieldAndChooser(Frame owner) {
      initComponents();
      lastFileName = fileNameTextField.getText();
      setOwnerFrame(owner);
   }

   public void setToolTipText(String text) {
      fileNameTextField.setToolTipText(text);
   }

   public void setCheckIfFileExists(boolean check) {
      checkIfFileExists = check;
   }

   public void setDirectoriesOnly(boolean directoriesOnly) {
      this.directoriesOnly = directoriesOnly;
   }

   public void setExtensionsAndDescription(String extensions, String description) {
      filter = FileUtils.getFileFilter(extensions, description);
   }

   public void setFileFilter(FileFilter filter) {
      this.filter = filter;
   }

   public void setIncludeOpenButton(boolean includeOpenButton) {
      if(includeOpenButton) {
         if(!isAncestorOf(openButton)) add(openButton);
      } else {
         if(isAncestorOf(openButton)) remove(openButton);
      }
   }

   public String getFileName() {
      return fileNameTextField.getText();
   }

   public void setFileName(String name, boolean triggerEvent) {
      if(name==null || (name.equals("Unassigned") && treatUnassignedAsEmptyString)) name = "";
      if(!name.equals(lastFileName)) {
         lastFileName = name;
         fileNameTextField.setText(name);
         boolean exists = (new File(name)).exists();
         setFileIsValid(checkIfFileExists ? exists : true);
         openButton.setEnabled(isEnabled() && exists); // don't enable open button if the component as a whole is disabled
         if(triggerEvent) fireStateChanged();
      }
   }

   public void setFileName(String name) {
      setFileName(name, true);
   }

   public boolean getFileIsValid() {
      return fileIsValid;
   }

   public void setFileIsValid(boolean valid) {
      fileIsValid = valid;
      updateTextFieldColor();
   }

   public void setEnabled(boolean enabled) {
      super.setEnabled(enabled);
      for(Component comp : getComponents()) comp.setEnabled(enabled);
      updateTextFieldColor();
   }

   public void setTreatEmptyStringAsValid(boolean asValid) {
      treatEmptyStringAsValid = asValid;
   }

   //------------------------------------------------------------------------
   // Associated check box
   //------------------------------------------------------------------------

   public void setAssociatedCheckBox(JCheckBox checkBox) {
      if(this.checkBox!=null) this.checkBox.removeActionListener(this);
      this.checkBox = checkBox;
      if(this.checkBox!=null) this.checkBox.addActionListener(this);
      setEnabled(this.checkBox.isSelected());
   }

   public void actionPerformed(ActionEvent evt) {
      if(evt.getSource()==checkBox && isEnabled() != checkBox.isSelected()) {
         setEnabled(checkBox.isSelected());
         fireStateChanged();
      }
   }

   //------------------------------------------------------------------------
   // ChangeListener code taken from AbstractButton.java
   //------------------------------------------------------------------------

   protected transient ChangeEvent changeEvent;
   public void addChangeListener(ChangeListener l) {
      listenerList.add(ChangeListener.class, l);
   }
   public void removeChangeListener(ChangeListener l) {
      listenerList.remove(ChangeListener.class, l);
   }
   public ChangeListener[] getChangeListeners() {
      return (ChangeListener[])(listenerList.getListeners(ChangeListener.class));
   }
   protected void fireStateChanged() {
      // Guaranteed to return a non-null array
      Object[] listeners = listenerList.getListenerList();
      // Process the listeners last to first, notifying
      // those that are interested in this event
      for (int i = listeners.length-2; i>=0; i-=2) {
         if (listeners[i]==ChangeListener.class) {
            // Lazily create the event:
            if (changeEvent == null)
               changeEvent = new ChangeEvent(this);
            ((ChangeListener)listeners[i+1]).stateChanged(changeEvent);
         }          
      }
   }   

   //------------------------------------------------------------------------
   // Utilities
   //------------------------------------------------------------------------

   private void updateTextFieldColor() {
      if(!isEnabled()) {
         fileNameTextField.setBackground(null);
         fileNameTextField.setEnabled(isEnabled());
      } else if(!fileIsValid || (!treatEmptyStringAsValid && fileNameTextField.getText().equals(""))) {
         fileNameTextField.setBackground(invalidBackground);
      } else {
         fileNameTextField.setBackground(validBackground);
      }
   }

   public boolean isSaveMode() {
      return saveMode;
   }

   public void setSaveMode(boolean saveMode) {
      this.saveMode = saveMode;
      if(saveMode) setCheckIfFileExists(false);
   }

    public Frame getOwnerFrame() {
        return ownerFrame;
    }

    public void setOwnerFrame(Frame ownerFrame) {
        this.ownerFrame = ownerFrame;
    }

   //------------------------------------------------------------------------
   // Event handling for the text field / button
   //------------------------------------------------------------------------

   private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
      String result =null;
     if (isSaveMode()){
         result = directoriesOnly ?
                      FileUtils.getInstance().browseForFolder(ownerFrame, "") :
                      FileUtils.getInstance().browseForFilenameToSave(filter, true, "", ownerFrame);
     }
     else
         result = directoriesOnly ?
                      FileUtils.getInstance().browseForFolder(ownerFrame, "") :
                      FileUtils.getInstance().browseForFilename(filter, ownerFrame);
      if(result != null) setFileName(result);
   }//GEN-LAST:event_browseButtonActionPerformed

   private void fileNameTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_fileNameTextFieldFocusLost
      if(!evt.isTemporary()) fileNameTextFieldActionPerformed(null);
   }//GEN-LAST:event_fileNameTextFieldFocusLost

   private void fileNameTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileNameTextFieldActionPerformed
      setFileName(fileNameTextField.getText());
   }//GEN-LAST:event_fileNameTextFieldActionPerformed
   
   /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        openButton = new javax.swing.JButton();
        fileNameTextField = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();

        openButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/swingui/folderOpen.png"))); // NOI18N
        openButton.setToolTipText("Open file/folder");
        openButton.setMaximumSize(new java.awt.Dimension(30, 19));
        openButton.setMinimumSize(new java.awt.Dimension(30, 19));
        openButton.setPreferredSize(new java.awt.Dimension(30, 19));
        openButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openButtonActionPerformed(evt);
            }
        });

        setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.LINE_AXIS));

        fileNameTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileNameTextFieldActionPerformed(evt);
            }
        });
        fileNameTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                fileNameTextFieldFocusLost(evt);
            }
        });
        add(fileNameTextField);

        browseButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/swingui/folderBrowse.png"))); // NOI18N
        browseButton.setToolTipText("Browse for file/folder");
        browseButton.setMaximumSize(new java.awt.Dimension(30, 19));
        browseButton.setMinimumSize(new java.awt.Dimension(30, 19));
        browseButton.setPreferredSize(new java.awt.Dimension(30, 19));
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });
        add(browseButton);
    }// </editor-fold>//GEN-END:initComponents

   private void openButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openButtonActionPerformed
      BrowserLauncher.openURL("file://"+getFileName());
   }//GEN-LAST:event_openButtonActionPerformed
   
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseButton;
    private javax.swing.JTextField fileNameTextField;
    private javax.swing.JButton openButton;
    // End of variables declaration//GEN-END:variables
   
}
