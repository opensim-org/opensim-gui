/* -------------------------------------------------------------------------- *
 * OpenSim: BaseToolPanel.java                                                *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib, Kevin Xu                                           *
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
 * ScaleToolPanel.java
 *
 * Created on July 3, 2007, 4:51 PM
 */

package org.opensim.tracking;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.Observer;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.opensim.utils.FileUtils;
import org.opensim.view.pub.OpenSimDB;

public abstract class BaseToolPanel extends JPanel implements ActionListener, Observer {
  
   private FileFilter settingsFilter = null;

   protected JButton loadSettingsButton = new JButton("Load...");
   protected JButton saveSettingsButton = new JButton("Save...");
   protected JButton applyButton = new JButton("Run");
   protected JButton okButton = new JButton("Close");
   //protected JButton cancelButton = new JButton("Cancel");
   protected JButton helpButton = new JButton("Help");
   protected Dialog ownerDialog = null;
   protected boolean cleanupAfterExecuting = false;  // Keep track if cleaning up needs to be done on execution finish vs. dialog close

   //------------------------------------------------------------------------
   // Load/Save Settings Actions
   //------------------------------------------------------------------------
   class LoadSettingsAction extends AbstractAction {
      public LoadSettingsAction() { super("Load Settings..."); }
      public void actionPerformed(ActionEvent evt) {
         String fileName = FileUtils.getInstance().browseForFilename(settingsFilter);
         if(fileName!=null) loadSettings(fileName);
      }
   }
   class SaveSettingsAction extends AbstractAction {
      public SaveSettingsAction() { super("Save Settings..."); }
      public void actionPerformed(ActionEvent evt) {
         String fileName = FileUtils.getInstance().browseForFilenameToSave(settingsFilter, true, "", null);
         if(fileName!=null) saveSettings(fileName);
      }
   }

   public BaseToolPanel() {
      /*loadSettingsButton.addMouseListener(new MouseAdapter() {
         public void mousePressed(MouseEvent evt) {
            JPopupMenu popup = new JPopupMenu();
            popup.add(new JMenuItem());
            popup.add(new JMenuItem(new SaveSettingsAction()));
            popup.show(evt.getComponent(),evt.getX(),evt.getY());
      }});*/
      loadSettingsButton.addActionListener(new LoadSettingsAction());
      saveSettingsButton.addActionListener(new SaveSettingsAction());
 
      //settingsButton.setToolTipText("Load or save tool settings to an XML file.");
      //applyButton.setToolTipText("Run tool.  Disabled while tool is running, and if settings are either invalid or unchanged since last run.");
      //okButton.setToolTipText("Close tool dialog while letting tool continue running and keeping results.");
      // TODO: tool tip for cancel button -- what's the easiest way to do this?
      OpenSimDB.getInstance().addObserver(this);
   }

   public void setSettingsFileDescription(String description) {
      settingsFilter = FileUtils.getFileFilter(".xml", description);
   }

   //------------------------------------------------------------------------
   // Override in derived classes
   //------------------------------------------------------------------------
   public void loadSettings(String fileName) {}
   public void saveSettings(String fileName) {}
   public void pressedCancel() {}
   public void pressedClose() {}
   public void pressedApply() {}

   //------------------------------------------------------------------------
   // Dialog Operations
   //------------------------------------------------------------------------
   public JButton[] getDialogOptions() {
      return new JButton[]{loadSettingsButton, saveSettingsButton, applyButton, okButton, helpButton};
   }

   public void setOwner(Dialog window) { ownerDialog = window; }

   public void updateApplyButton(boolean applyEnabled) {
      applyButton.setEnabled(applyEnabled);
   }

   public void actionPerformed(ActionEvent evt) {
      if(evt.getSource() == okButton) {
         pressedClose();
         ownerDialog.dispose();
      } else if(evt.getSource() == applyButton) {
         pressedApply();
      }
   }

   //------------------------------------------------------------------------
   // Helper function to create dialog
   //------------------------------------------------------------------------
   public static void openToolDialog(final BaseToolPanel panel, String name) {
      DialogDescriptor dlg = new DialogDescriptor(panel, name, false, panel);
      dlg.setOptions(panel.getDialogOptions());
      Dialog dialog = DialogDisplayer.getDefault().createDialog(dlg);
      panel.setOwner(dialog);
      dialog.setVisible(true);      
      dialog.requestFocus();      
      //JFrame toolFrame = DialogUtils.createFrameForPanel(panel, name);
      //DialogUtils.addButtons(toolFrame, (JButton[]) panel.getDialogOptions(), panel);
      //panel.setOwner(toolFrame);
      //toolFrame.setVisible(true);      
      dialog.requestFocus();
      dialog.addWindowListener(new WindowAdapter(){
         public void windowClosed(WindowEvent e) {
            super.windowClosed(e);
            panel.cleanup();
            OpenSimDB.getInstance().deleteObserver(panel);
         }

         public void windowClosing(WindowEvent e) {
            super.windowClosing(e);
            panel.cleanup();
            OpenSimDB.getInstance().deleteObserver(panel);
         }
      });
   }
   // Relinquish C++ resources by setting references to them to null
   public void cleanup()
   {
      // If tool is still running don't do any cleanup until the tool is done otherwise cleanup now
      // by freeing C++ allocated resources.
   }

   public void close()
   {
       pressedCancel();
       OpenSimDB.getInstance().deleteObserver(this);
       ownerDialog.dispose();
   }
}
