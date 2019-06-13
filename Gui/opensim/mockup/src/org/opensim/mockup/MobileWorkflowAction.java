/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.mockup;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Tools",
        id = "org.opensim.mockup.MobileWorkflowAction"
)
@ActionRegistration(
        displayName = "#CTL_MobileWorkflowAction"
)
@ActionReference(path = "Menu/Tools", position = -55)
@Messages("CTL_MobileWorkflowAction=Mobile Workflow ...")
public final class MobileWorkflowAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO implement action body
        new MobileRehabWizardWizardAction().actionPerformed(e);
    }
}
