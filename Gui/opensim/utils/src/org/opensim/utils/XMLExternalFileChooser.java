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
 */
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
