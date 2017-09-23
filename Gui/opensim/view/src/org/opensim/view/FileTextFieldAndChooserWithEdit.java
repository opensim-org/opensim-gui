/* -------------------------------------------------------------------------- *
 * OpenSim: FileTextFieldAndChooserWithEdit.java                              *
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

import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.opensim.modeling.OpenSimObject;
import org.opensim.swingui.FileTextFieldAndChooser;
import org.opensim.view.editors.ObjectEditDialogMaker;

public class FileTextFieldAndChooserWithEdit extends FileTextFieldAndChooser {

   JButton editButton = new JButton();

   public FileTextFieldAndChooserWithEdit() {
      this(null);
   }

   public FileTextFieldAndChooserWithEdit(ActionListener al) {
      editButton = new javax.swing.JButton();
      editButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/swingui/editor.gif")));
      editButton.setToolTipText("Edit file contents");
      editButton.setMaximumSize(new java.awt.Dimension(30, 19));
      editButton.setMinimumSize(new java.awt.Dimension(30, 19));
      editButton.setPreferredSize(new java.awt.Dimension(30, 19));
      if (al==null){
          editButton.addActionListener(new java.awt.event.ActionListener() {
             public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
             }
          });          
      }
      else
        editButton.addActionListener(al);

      add(editButton);
   }
   
   public void setFileName(String name, boolean triggerEvent) {
      super.setFileName(name, triggerEvent);
      boolean exists = (new File(name)).exists();
      editButton.setEnabled(isEnabled() && exists);
   }

   private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {
      String originalFileName = getFileName();
      if(ObjectEditDialogMaker.editFile(originalFileName)) {
         // We want our tools to reload the data... if we just fire another state change notice most of them won't do anything
         // because they'll see that the filename hasn't changed... but we can force them to re-update with the following hack:
         setFileName("");
         setFileName(originalFileName);
      }
   }
}
