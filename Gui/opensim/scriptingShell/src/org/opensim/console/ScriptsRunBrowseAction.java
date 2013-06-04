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
        final String ScriptsRootDirectory = Preferences.userNodeForPackage(TheApp.class).get("Scripts Path", "Scripts");
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
            Exceptions.printStackTrace(ex);
        }
        
    }
}
