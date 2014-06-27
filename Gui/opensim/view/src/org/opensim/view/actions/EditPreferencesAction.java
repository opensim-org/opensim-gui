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
package org.opensim.view.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.prefs.BackingStoreException;
import javax.help.CSH;
import javax.help.CSH.DisplayHelpFromSource;
import javax.swing.JButton;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.utils.BrowserLauncher;
import org.opensim.utils.TheApp;

public final class EditPreferencesAction extends CallableSystemAction {

    final JButton helpBtn = new JButton ("Help");

    public EditPreferencesAction () {
    }
   
   public void performAction() {
      EditPreferencesJPanel prefsPanel;
      Object [] options =  {  NotifyDescriptor.OK_OPTION,
                                NotifyDescriptor.CANCEL_OPTION,
                                helpBtn};
      try {
         prefsPanel = new EditPreferencesJPanel();
         //DialogDescriptor prefsDialog = new DialogDescriptor(prefsPanel, "Preferences");
         DialogDescriptor prefsDialog = new DialogDescriptor(prefsPanel,
                                            "Preferences",
                                            true,
                                            options,
                                            NotifyDescriptor.OK_OPTION,
                                            DialogDescriptor.DEFAULT_ALIGN,
                                            null,
                                            new ActionListener() {

                                                @Override
                                                public void actionPerformed(ActionEvent ae) {
                                                    if(ae.getSource().equals(helpBtn)) {
                                                        String path = BrowserLauncher.isConnected() ? "http://simtk-confluence.stanford.edu:8080/display/OpenSim30/User+Preferences" : TheApp.getUsersGuideDir() + "User+Preferences.html";
                                                        BrowserLauncher.openURL(path);
                                                    }
                                                }
                                            });
         setEnabled(false);
         DialogDisplayer.getDefault().createDialog(prefsDialog).setVisible(true);
         setEnabled(true);

        //when JDialog is closed, do something
        Object userInput = prefsDialog.getValue();
        if (userInput == NotifyDescriptor.OK_OPTION)
            prefsPanel.apply();
        return;
      } catch (BackingStoreException ex) {
         ex.printStackTrace();
      }
   }
   
   public String getName() {
      return NbBundle.getMessage(EditPreferencesAction.class, "CTL_EditPreferencesAction");
   }
   
   protected void initialize() {
      super.initialize();
      // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
      putValue("noIconInMenu", Boolean.TRUE);
   }
   
   public HelpCtx getHelpCtx() {
      return HelpCtx.DEFAULT_HELP;
   }
   
   protected boolean asynchronous() {
      return false;
   }
}
