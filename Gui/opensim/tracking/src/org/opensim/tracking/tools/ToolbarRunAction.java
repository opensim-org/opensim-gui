/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.tracking.tools;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.opensim.modeling.Model;
import org.opensim.tracking.ForwardToolModel;
import org.opensim.view.experimentaldata.ModelForExperimentalData;
import org.opensim.view.pub.OpenSimDB;

@ActionID(category = "Edit",
id = "org.opensim.tracking.tools.ToolbarRunAction")
@ActionRegistration(iconBase = "org/opensim/tracking/tools/run.png",
displayName = "#CTL_ToolbarRunAction")
@ActionReferences({
    @ActionReference(path = "Toolbars/UndoRedo", position = 600),
    @ActionReference(path = "Shortcuts", name = "O-R")
})
@Messages("CTL_ToolbarRunAction=Run Forward Simulation")
public final class ToolbarRunAction implements ActionListener {

    ForwardToolModel toolModel=null;
    public void actionPerformed(ActionEvent e) {
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
        }
    }
}
