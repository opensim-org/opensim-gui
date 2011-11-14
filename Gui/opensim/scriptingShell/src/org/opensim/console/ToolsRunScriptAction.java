/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.console;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.openide.util.HelpCtx;
import org.openide.util.actions.CallableSystemAction;
import org.python.core.Py;
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
        JMenu displayMenu = new JMenu("Scripts");
        FileFilter fileFilter = new FileFilter() {

            public boolean accept(File file) {
                return (!file.isDirectory() && file.getName().endsWith(".py"));
            }
        };
        final String ScriptsRootDirectory = "Scripts";
        File rootHelpDirectory = new File(ScriptsRootDirectory);
        String fullPath = rootHelpDirectory.getAbsolutePath();
        File[] files = rootHelpDirectory.listFiles(fileFilter);
        if (files == null) {
            return displayMenu;
        }

        for (int i = 0; i < files.length; i++) {
            final String fileName = files[i].getName();
            JMenuItem nextItem = new JMenuItem(fileName);
            nextItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {

                    PythonInterpreter interp = new PythonInterpreter();

                    Py.getSystemState().setClassLoader(
                            this.getClass().getClassLoader());
                    interp.exec("import sys");
                    interp.execfile(ScriptsRootDirectory+"/"+fileName);
                }
            });
            
            displayMenu.add(nextItem);
        }

        return displayMenu;
    }
}
