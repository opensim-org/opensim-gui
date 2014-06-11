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
