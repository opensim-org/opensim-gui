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
        id = "org.opensim.rcnl.TrackingOptimizationAction"
)
@ActionRegistration(
        displayName = "#CTL_TrackingOptimizationAction"
)
@Messages("CTL_TrackingOptimizationAction=Tracking Optimization")
public final class TrackingOptimizationAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO implement action body
       Model model = OpenSimDB.getInstance().getCurrentModel();
      if(model==null){
          NotifyDescriptor.Message dlg =
                          new NotifyDescriptor.Message("Error while initializing Tracking Optimization tool. No current model to personalize");
                  DialogDisplayer.getDefault().notify(dlg);
          return;
      }

      try {
         final TrackingOptimizationJPanel jpPanel = new TrackingOptimizationJPanel(model);
         BaseToolPanel.openToolDialog(jpPanel, "Tracking Optimization Tool");
      } catch (IOException ex) {
         ErrorDialog.displayIOExceptionDialog("Tracking Optimization Error",
                    "Error while initializing Tracking Optimization tool",ex);
      }

        ;
    }
}
