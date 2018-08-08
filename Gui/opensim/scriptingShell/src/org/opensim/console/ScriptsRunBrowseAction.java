/* -------------------------------------------------------------------------- *
 * OpenSim: ScriptsRunBrowseAction.java                                       *
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

import java.io.File;
import java.io.IOException;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.HelpCtx;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.utils.ErrorDialog;
import org.opensim.utils.FileUtils;

public final class ScriptsRunBrowseAction extends CallableSystemAction {

    public void performAction() {
        String scriptFilename = FileUtils.getInstance().browseForFilename(".py", "Files containing script to execute", true);
        if (scriptFilename != null) {
            MRUScriptsOptions.getInstance().addFile(scriptFilename);
            ScriptingShellTopComponent.getDefault().getConsole().executeFile(scriptFilename);
        }
    }

    protected boolean asynchronous() {
        return false;
    }

    public String getName() {
        return "Run...";
    }

   
   protected void initialize() {
      super.initialize();
      // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
      putValue("noIconInMenu", Boolean.TRUE);
   }
    
    public HelpCtx getHelpCtx() {
         return HelpCtx.DEFAULT_HELP;
    }
/*
    public JMenuItem getMenuPresenter() {
        JMenu scriptsMenu = new JMenu("Open");
        FileFilter fileFilter = new FileFilter() {

            public boolean accept(File file) {
                return (!file.isDirectory() && file.getName().endsWith(".py"));
            }
        };
        final String ScriptsRootDirectory = TheApp.getCurrentVersionPreferences().get("Scripts Path", "Scripts");
        File rootHelpDirectory = new File(ScriptsRootDirectory);
        final String fullPath = rootHelpDirectory.getAbsolutePath();
        File[] files = rootHelpDirectory.listFiles(fileFilter);
        if (files == null) {

            return scriptsMenu;
        }

        for (int i = 0; i < files.length; i++) {
            final String fileName = files[i].getName();
            JMenuItem nextItem = new JMenuItem(fileName);
            nextItem.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    openFile(fullPath + "/" + fileName);
                }
            });

            scriptsMenu.add(nextItem);
        }
        JMenuItem browseItem = new JMenuItem("Browse...");
        browseItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String dataFilename = FileUtils.getInstance().browseForFilename(".py", "Files containing script to execute", true);
                if (dataFilename != null) {
                    openFile(dataFilename);
                }
            }
        });
        scriptsMenu.add(browseItem);
        return scriptsMenu;
    }
*/
    /**
     * execute passed in scriptFilename in Scripting shell and echo contents
     * @param scriptFilename 
     */
    void openFile(String scriptFilename) {
        try {
            FileObject fObj = FileUtil.createData(new File(scriptFilename));
            DataObject dObj  = DataObject.find(fObj);
            OpenCookie open = dObj.getLookup().lookup(OpenCookie.class);
            open.open();
            MRUScriptsOptions.getInstance().addFile(scriptFilename);
        } catch (IOException ex) {
            ErrorDialog.displayExceptionDialog(ex);
        }
        
    }
}
