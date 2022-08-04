/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.rcnl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.opensim.utils.ErrorDialog;
import org.opensim.modeling.Model;
import org.opensim.view.pub.OpenSimDB;

@ActionID(
        category = "Edit",
        id = "org.opensim.rcnl.JointPersonalizationAction"
)
@ActionRegistration(
        displayName = "#CTL_JointPersonalizationAction"
)
@Messages("CTL_JointPersonalizationAction=Personalize Model Joints")
public final class JointPersonalizationAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO implement action body
       Model model = OpenSimDB.getInstance().getCurrentModel();
      if(model==null){
          NotifyDescriptor.Message dlg =
                          new NotifyDescriptor.Message("Error while initializing Joint Model Personalization tool. No current model to personalize");
                  DialogDisplayer.getDefault().notify(dlg);
          return;
      }

      try {
         final JointPersonalizationJPanel jpPanel = new JointPersonalizationJPanel(model);
         BaseToolPanel.openToolDialog(jpPanel, "Joint Model Personalization Tool");
      } catch (IOException ex) {
         ErrorDialog.displayIOExceptionDialog("Joint Model Personalization Error",
                    "Error while initializing Joint Model Personalization tool",ex);
      }

        ;
    }
}
