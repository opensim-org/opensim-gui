/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.console;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.JOptionPane;
import org.openide.loaders.DataObject;

import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.cookies.SaveCookie;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.opensim.tracking.ForwardToolModel;

@ActionID(category = "Edit",
id = "org.opensim.console.ToolsRunCurrentScriptAction")
@ActionRegistration(iconBase = "org/opensim/console/run.png",
displayName = "#CTL_RunCurrentAction")
@ActionReferences({
    @ActionReference(path = "Menu/Tools", position = 925)
})
@Messages("CTL_RunCurrentAction=Run Current Script")
public final class ToolsRunCurrentScriptAction implements ActionListener {

    private final DataObject context;

    ForwardToolModel toolModel=null;

    public ToolsRunCurrentScriptAction(DataObject context) {
        this.context = context;
    }

    public void actionPerformed(ActionEvent ev) {
        // TODO use context
        String path = context.getPrimaryFile().getPath();
        SaveCookie sc = context.getCookie(SaveCookie.class);
        if (sc != null) {
            try {
                Object[] options = {"Yes", "No"};
                int answer = JOptionPane.showOptionDialog(null,
                        "The script has been modified, do you want to save it first?",
                        "Save Modified Script",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE,
                        null,
                        options,
                        options[0]);
                if (answer == 0) {
                    sc.save();
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        ScriptingShellTopComponent.getDefault().getConsole().executeFile(path);
        /*
        if (toolModel == null || !toolModel.isExecuting()) {
            try {
                Model currentModel = OpenSimDB.getInstance().getCurrentModel();
                if (currentModel == null || currentModel instanceof ModelForExperimentalData)
                    return;
                // TODO implement action body
                toolModel = new ForwardToolModel(currentModel);
                toolModel.setSolveForEquilibrium(true);
                toolModel.execute();
                // Change 
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        } else {
            if (toolModel != null) {
                toolModel.cancel();
            }
        }*/
    }
}
