/* -------------------------------------------------------------------------- *
 * OpenSim: ToolsOpenScriptAction.java                                        *
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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.console;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.prefs.Preferences;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.utils.FileUtils;
import org.opensim.utils.TheApp;

public final class ToolsOpenScriptAction extends CallableSystemAction {

    public void performAction() {
        String dataFilename = FileUtils.getInstance().browseForFilename(FileUtils.ScriptFileFilter, "Choose script (.py) to open");
        if (dataFilename != null) {
            openFile(dataFilename);
        }
    }

    protected boolean asynchronous() {
        return false;
    }

    public String getName() {
        return "Open...";
    }

   
   protected void initialize() {
      super.initialize();
      // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
      putValue("noIconInMenu", Boolean.TRUE);
   }
    
    public HelpCtx getHelpCtx() {
         return HelpCtx.DEFAULT_HELP;
    }
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
            Exceptions.printStackTrace(ex);
        }
        
    }
}
