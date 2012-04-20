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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.prefs.Preferences;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.utils.TheApp;
import org.python.util.PythonInterpreter;

public final class ToolsRunScriptAction extends CallableSystemAction {

    public void performAction() {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    protected boolean asynchronous() {
        return false;
    }

    public String getName() {
        return "Scripts";
    }

   
   protected void initialize() {
      super.initialize();
      // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
      putValue("noIconInMenu", Boolean.TRUE);
   }
    
    public HelpCtx getHelpCtx() {
         return HelpCtx.DEFAULT_HELP;
    }

    public JMenuItem getMenuPresenter() {
        JMenu scriptsMenu = new JMenu("Scripts");
        FileFilter fileFilter = new FileFilter() {

            public boolean accept(File file) {
                return (!file.isDirectory() && file.getName().endsWith(".py"));
            }
        };
        final String ScriptsRootDirectory = Preferences.userNodeForPackage(TheApp.class).get("Scripts Path", "Scripts");
        File rootHelpDirectory = new File(ScriptsRootDirectory);
        String fullPath = rootHelpDirectory.getAbsolutePath();
        File[] files = rootHelpDirectory.listFiles(fileFilter);
        if (files == null) {
            
            return scriptsMenu;
        }

        for (int i = 0; i < files.length; i++) {
            final String fileName = files[i].getName();
            JMenuItem nextItem = new JMenuItem(fileName);
            nextItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    PythonInterpreter interp = ScriptingShellTopComponent.getDefault().getInterp();
                    interp.execfile(ScriptsRootDirectory+"/"+fileName);
                    File scriptFile= new File(ScriptsRootDirectory+"/"+fileName);
                    String fileContents = getContents(scriptFile);
                    ScriptingShellTopComponent.getDefault().logMessage(fileContents);
                    ScriptingShellTopComponent.getDefault().logMessage("Finished executing script file "+fileName);
                }
            });
            
            scriptsMenu.add(nextItem);
        }

        return scriptsMenu;
    }
 
    public String getContents(File aFile) {
        StringBuilder contents = new StringBuilder();

        try {
            //use buffering, reading one line at a time
            //FileReader always assumes default encoding is OK!
            BufferedReader input = new BufferedReader(new FileReader(aFile));
            try {
                String line = null; //not declared within while loop
        		/*
                 * readLine is a bit quirky :
                 * it returns the content of a line MINUS the newline.
                 * it returns null only for the END of the stream.
                 * it returns an empty String if two newlines appear in a row.
                 */
                while ((line = input.readLine()) != null) {
                    contents.append(line);
                    contents.append(System.getProperty("line.separator"));
                }
            } finally {
                input.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return contents.toString();
    }
}
