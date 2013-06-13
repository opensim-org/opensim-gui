/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.console;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.cookies.SaveCookie;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;



@ActionID(category = "Edit",
id = "org.opensim.console.ScriptsSaveAction")
@ActionRegistration(
displayName = "#CTL_SaveCurrentScriptAction")
@ActionReferences({
    @ActionReference(path = "Menu/Scripts", position = 400)
})
@Messages("CTL_SaveCurrentScriptAction=Save Current Script")
public final class ScriptsSaveAction implements ActionListener {

    private final DataObject context;


    public ScriptsSaveAction(DataObject context) {
        this.context = context;
    }

    public void actionPerformed(ActionEvent ev) {
        // TODO use context
        SaveCookie sc = context.getCookie(SaveCookie.class);
        if (sc != null) {
            try {
                sc.save();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        
    }

    public String getName() {
        return "Save";
    }

}
