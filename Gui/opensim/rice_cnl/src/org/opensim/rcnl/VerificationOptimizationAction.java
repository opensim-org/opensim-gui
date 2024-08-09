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
        id = "org.opensim.rcnl.VerificationOptimizationAction"
)
@ActionRegistration(
        displayName = "#CTL_VerificationOptimizationAction"
)
@Messages("CTL_VerificationOptimizationAction=Verification Optimization")
public final class VerificationOptimizationAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO implement action body
       Model model = OpenSimDB.getInstance().getCurrentModel();
      if(model==null){
          NotifyDescriptor.Message dlg =
                          new NotifyDescriptor.Message("Error while initializing Verification Optimization tool. No current model to personalize");
                  DialogDisplayer.getDefault().notify(dlg);
          return;
      }

      try {
         final TreatmentOptimizationJPanel jpPanel = new TreatmentOptimizationJPanel(model, TreatmentOptimizationToolModel.Mode.VerificationOptimization);
         BaseToolPanel.openToolDialog(jpPanel, "Verification Optimization Tool");
      } catch (IOException ex) {
         ErrorDialog.displayIOExceptionDialog("Verification Optimization Error",
                    "Error while initializing Verification Optimization tool",ex);
      }

        ;
    }
}
