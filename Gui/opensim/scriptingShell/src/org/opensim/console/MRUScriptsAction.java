/* -------------------------------------------------------------------------- *
 * OpenSim: MRUScriptsAction.java                                             *
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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.console;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.awt.DynamicMenuContent;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.CallableSystemAction;

@ActionID(category = "Edit",
id = "org.opensim.console.MRUScriptsAction")
@ActionRegistration(displayName = "#CTL_MRUScriptsAction")
@ActionReferences({
    @ActionReference(path = "Scripts", position = 1362, separatorBefore = 1356, separatorAfter = 1368)
})
@Messages("CTL_MRUFilesAction=Recent Scripts")
public final class MRUScriptsAction extends CallableSystemAction {
 
    /** {@inheritDoc}
     * do nothing
     */
    public void performAction() {
        // do nothing
    }
 
    /** {@inheritDoc} */
    public String getName() {
        return NbBundle.getMessage(MRUScriptsAction.class, "CTL_MRUScriptsAction");
    }
 
 
    /** {@inheritDoc} */
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
 
    /** {@inheritDoc} */
    protected boolean asynchronous() {
        return false;
    }
 
    /** {@inheritDoc}
     * Overide to provide SubMenu for MRUFiles (Most Recently Used Files)
     */
    public JMenuItem getMenuPresenter() {
        JMenu menu = new MRUFilesMenu(getName());
        return menu;
    }
 
 
 
    class MRUFilesMenu extends JMenu implements DynamicMenuContent {
 
        public MRUFilesMenu(String s) {
            super(s);
 
            MRUScriptsOptions opts = MRUScriptsOptions.getInstance();
            opts.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    if (!evt.getPropertyName().equals(MRUScriptsOptions.MRU_SCRIPT_LIST_PROPERTY)) {
                       return;
                    }
                    updateMenu();
                }
            });
 
            updateMenu();
        }
 
        public JComponent[] getMenuPresenters() {
            return new JComponent[] {this};
        }
 
        public JComponent[] synchMenuPresenters(JComponent[] items) {
            return getMenuPresenters();
        }
 
        private void updateMenu() {
            removeAll();
            MRUScriptsOptions opts = MRUScriptsOptions.getInstance();
            List<String> list = opts.getMRUFileList();
            for (String name : list) {
                Action action = createAction(name);
                action.putValue(Action.NAME,name);
                JMenuItem menuItem = new JMenuItem(action);
                add(menuItem);
            }
        }
 
 
        private Action createAction(String actionCommand) {
            Action action = new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    menuItemActionPerformed(e);
                }
            };
 
            action.putValue(Action.ACTION_COMMAND_KEY, actionCommand);
            return action;
        }
 
        private void menuItemActionPerformed(ActionEvent evt) {
            String command = evt.getActionCommand();
            File file = new File(command);
 
            try {
                DataObject data = DataObject.find(FileUtil.toFileObject(file));
                OpenCookie cookie = data.getCookie(OpenCookie.class);
                cookie.open();
            } catch (OutOfMemoryError ex) {               
            } catch (Exception ex) {
            }
        }
    }
}