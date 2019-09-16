/* -------------------------------------------------------------------------- *
 * OpenSim: EditPreferencesAction.java                                        *
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
                                                        BrowserLauncher.openURL("https://simtk-confluence.stanford.edu/display/OpenSim40/User+Preferences");
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
