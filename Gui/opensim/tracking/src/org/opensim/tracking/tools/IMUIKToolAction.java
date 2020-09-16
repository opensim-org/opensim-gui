/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.tracking.tools;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.opensim.modeling.Model;
import org.opensim.tracking.BaseToolPanel;
import org.opensim.tracking.IMUIKToolPanel;
import org.opensim.utils.ErrorDialog;
import org.opensim.view.pub.OpenSimDB;

@ActionID(
        category = "Tools",
        id = "org.opensim.tracking.tools.IMUIKToolAction"
)
@ActionRegistration(
        displayName = "#CTL_IMUIKToolAction"
)
@ActionReference(path = "Menu/Tools", position = 200)
@Messages("CTL_IMUIKToolAction=Inverse Kinematics with Sensors")
public final class IMUIKToolAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO implement action body      
      Model model = OpenSimDB.getInstance().getCurrentModel();
      if(model==null) return;

      try {
         final IMUIKToolPanel panel = new IMUIKToolPanel(model);
         BaseToolPanel.openToolDialog(panel, "IMU Inverse Kinematics Tool");
      } catch (IOException ex) {
         ErrorDialog.displayIOExceptionDialog("Inverse Kinematics Tool Error","Error while initializing inverse kinematics tool",ex);
      }
        
    }
}
