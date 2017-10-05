/* -------------------------------------------------------------------------- *
 * OpenSim: XMLExternalFileChooser.java                                       *
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

package org.opensim.utils;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.opensim.swingui.FileTextFieldAndChooser;

public class XMLExternalFileChooser extends javax.swing.JPanel {

   public static class Item extends JPanel {
      private JCheckBox writeExternalCheckBox = new JCheckBox();
      private FileTextFieldAndChooser externalFileName = new FileTextFieldAndChooser();
      
      public Item(String name, String defaultExternalFileName) {
         setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
       
         Dimension checkBoxDim = new Dimension(200,20); 
         Dimension fileNameDim = new Dimension(300,20);

         writeExternalCheckBox.setMinimumSize(checkBoxDim);
         writeExternalCheckBox.setPreferredSize(checkBoxDim);
         writeExternalCheckBox.setMaximumSize(checkBoxDim);
         writeExternalCheckBox.setText(name);
         writeExternalCheckBox.setSelected(defaultExternalFileName!=null);
         add(writeExternalCheckBox);
       
         externalFileName.setMinimumSize(fileNameDim);
         externalFileName.setPreferredSize(fileNameDim);
         externalFileName.setMaximumSize(fileNameDim);

         externalFileName.setAssociatedCheckBox(writeExternalCheckBox);
         externalFileName.setTreatEmptyStringAsValid(false);
         externalFileName.setCheckIfFileExists(false);
         externalFileName.setFileName(defaultExternalFileName!=null ? defaultExternalFileName : "", false);

         // Update this component's valid flag whenever the filename field changes state
         externalFileName.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent evt) { putClientProperty("valid", (Boolean)isValidated()); }
         });

         add(externalFileName);
      }

      public String getName() { return writeExternalCheckBox.getText(); }
      public String getExternalFileName() { return writeExternalCheckBox.isSelected() ? externalFileName.getFileName() : null; }
      public boolean isValidated() {
          if (writeExternalCheckBox==null || externalFileName==null) return false;
          return !writeExternalCheckBox.isSelected() || !FileUtils.effectivelyNull(externalFileName.getFileName()); }
   }

   // If returns true then was successful (user pressed OK), and sets new values in externalFileNames
   // If returns then user pressed Cancel
   static public boolean promptUser(String mainSettingsFileName, String[] names, String[] externalFileNames) {
      assert(names.length == externalFileNames.length);
      XMLExternalFileChooser panel = new XMLExternalFileChooser(mainSettingsFileName);
      for(int i=0; i<names.length; i++) panel.addItem(names[i], externalFileNames[i]);
      final DialogDescriptor dlg = new DialogDescriptor(panel, "Choose external references in XML file");
      // Disable "OK" button of dialog if settings are not valid
      panel.addPropertyChangeListener("valid", new PropertyChangeListener() {
         public void propertyChange(PropertyChangeEvent evt) { dlg.setValid((Boolean)evt.getNewValue()); }
      });
      Object answer = DialogDisplayer.getDefault().notify(dlg);
      if(answer==NotifyDescriptor.OK_OPTION) for(int i=0; i<names.length; i++) externalFileNames[i] = panel.getItemExternalFileName(i);
      return answer==NotifyDescriptor.OK_OPTION;
   }

   public XMLExternalFileChooser(String mainSettingsFileName) {
      initComponents();
     
      if(mainSettingsFileName!=null)
         parentDirectoryTextField.setText((new File(mainSettingsFileName)).getParent());
      else
         parentDirectoryTextField.setText("Unknown");
   }

   public void addItem(String name, String defaultExternalFileName) {
      XMLExternalFileChooser.Item item = new XMLExternalFileChooser.Item(name, defaultExternalFileName);
      // Update valid flag for this whole component whenever an individual item's valid flag changes
      item.addPropertyChangeListener("valid", new PropertyChangeListener() {
         public void propertyChange(PropertyChangeEvent evt) { putClientProperty("valid", (Boolean)isValidated()); }
      });
      jPanel1.add(item);
   }

   public String getItemExternalFileName(int i) {
      String fileName=((XMLExternalFileChooser.Item)jPanel1.getComponent(i)).getExternalFileName();
      if (fileName == null) return null;
      return FileUtils.addExtensionIfNeeded(fileName,".xml");
   }

   public boolean isValidated() {
      if (jPanel1==null) return false;
      for(int i=0; i<jPanel1.getComponents().length; i++)
         if(!((XMLExternalFileChooser.Item)jPanel1.getComponent(i)).isValidated())
            return false;
      return true;
   }

   /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
   // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
   private void initComponents() {
      jTextArea1 = new javax.swing.JTextArea();
      parentDirectoryTextField = new javax.swing.JTextField();
      jPanel1 = new javax.swing.JPanel();

      jTextArea1.setColumns(20);
      jTextArea1.setEditable(false);
      jTextArea1.setFont(new java.awt.Font("Tahoma", 0, 11));
      jTextArea1.setLineWrap(true);
      jTextArea1.setRows(5);
      jTextArea1.setText("The settings below can be optionally written to external files, allowing multiple trials to share the same settings.  Select which of the following you would like written to external files, and specify their file names.\n\nRelative paths are allowed, and are relative to the directory of the main settings file:");
      jTextArea1.setWrapStyleWord(true);
      jTextArea1.setOpaque(false);

      parentDirectoryTextField.setEditable(false);
      parentDirectoryTextField.setText("jTextField1");

      jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS));

      jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

      org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(
         layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
         .add(layout.createSequentialGroup()
            .addContainerGap()
            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
               .add(jTextArea1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 500, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
               .add(parentDirectoryTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
               .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE))
            .addContainerGap())
      );
      layout.setVerticalGroup(
         layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
         .add(layout.createSequentialGroup()
            .addContainerGap()
            .add(jTextArea1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(parentDirectoryTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
            .addContainerGap())
      );
   }// </editor-fold>//GEN-END:initComponents
   
   
   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JPanel jPanel1;
   private javax.swing.JTextArea jTextArea1;
   private javax.swing.JTextField parentDirectoryTextField;
   // End of variables declaration//GEN-END:variables
   
}
