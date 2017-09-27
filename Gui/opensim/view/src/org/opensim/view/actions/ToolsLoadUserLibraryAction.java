/* -------------------------------------------------------------------------- *
 * OpenSim: ToolsLoadUserLibraryAction.java                                   *
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
package org.opensim.view.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.view.pub.PluginsDB;

public final class ToolsLoadUserLibraryAction extends CallableSystemAction {
    
    public void performAction() {
        // TODO implement action body
        return ;
    }
    
    public String getName() {
        return NbBundle.getMessage(ToolsLoadUserLibraryAction.class, "CTL_LoadUserLibraryAction");
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
    
    public JMenuItem getMenuPresenter() {
      JMenu displayMenu = new JMenu("User Plugins");
      final boolean windows = System.getProperty("os.name").toLowerCase().contains("win");
      FileFilter fileFilter = new FileFilter() {
                public boolean accept(File file) {
                    // For now .dll for windows should generalize to other platforms based on OS property
                    String dynamicLibraryExtension = (windows)?".dll":".so";
                    return (!file.isDirectory()&& file.getName().endsWith(dynamicLibraryExtension));
                }
      };
      File rootExtensionsDirectory= new File("plugins");
      String fullPath = rootExtensionsDirectory.getAbsolutePath();
      System.out.println("Loading plugins from directory "+fullPath);
      File[] files = rootExtensionsDirectory.listFiles(fileFilter);
      if (files == null)  return displayMenu;
      
      for (int i=0; i<files.length; i++){
        final String filename=files[i].getName();
        final String fullFileName = files[i].getAbsolutePath();
        JMenuItem extItem = new JMenuItem(filename);
        extItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                String libName = filename.substring(0, filename.indexOf("."));
                LoadPluginJPanel loadPluginJPanel= new LoadPluginJPanel(libName);
                DialogDescriptor confirmDialog = 
                    new DialogDescriptor(loadPluginJPanel, 
                        "Load user plugin",
                        true,
                        new Object[]{DialogDescriptor.OK_OPTION},
                        DialogDescriptor.OK_OPTION,
                        1, null, null);
                try {
                    System.load(fullFileName);
                    
                    DialogDisplayer.getDefault().createDialog(confirmDialog).setVisible(true);
                    // Check if we need to always preload
                    if(loadPluginJPanel.isPreloadAlways()){
                        //System.out.println("Always preload "+filename);
                        PluginsDB.getInstance().addLibrary(fullFileName);
                    }
                } catch(UnsatisfiedLinkError er){
                    DialogDisplayer.getDefault().notify(
                            new NotifyDescriptor.Message("Error trying to load library "+libName+". Library not loaded."));
                }
            }});
          displayMenu.add(extItem);
        }
        return displayMenu;
    }
    
}
