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
        id = "org.opensim.rcnl.DesignOptimizationAction"
)
@ActionRegistration(
        displayName = "#CTL_DesignOptimizationAction"
)
@Messages("CTL_DesignOptimizationAction=Design Optimization")
public final class DesignOptimizationAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO implement action body
       Model model = OpenSimDB.getInstance().getCurrentModel();
      if(model==null){
          NotifyDescriptor.Message dlg =
                          new NotifyDescriptor.Message("Error while initializing Design Optimization tool. No current model to personalize");
                  DialogDisplayer.getDefault().notify(dlg);
          return;
      }

      try {
         final TreatmentOptimizationJPanel jpPanel = new TreatmentOptimizationJPanel(model, TreatmentOptimizationToolModel.Mode.DesignOptimization);
         BaseToolPanel.openToolDialog(jpPanel, "Design Optimization Tool");
      } catch (IOException ex) {
         ErrorDialog.displayIOExceptionDialog("Design Optimization Error",
                    "Error while initializing Design Optimization tool",ex);
      }

        ;
    }
}
