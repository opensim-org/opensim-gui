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
