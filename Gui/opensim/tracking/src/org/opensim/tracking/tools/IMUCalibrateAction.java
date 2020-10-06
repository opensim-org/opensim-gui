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
import org.opensim.tracking.IMUCalibrationPanel;
import org.opensim.tracking.IMUIKToolPanel;
import org.opensim.utils.ErrorDialog;
import org.opensim.view.pub.OpenSimDB;

@ActionID(
        category = "Tools",
        id = "org.opensim.tracking.tools.IMUCalibrateAction"
)
@ActionRegistration(
        displayName = "#CTL_IMUCalibrateAction"
)
@ActionReference(path = "Menu/Tools", position = 250, separatorBefore = 225)
@Messages("CTL_IMUCalibrateAction=Calibrate...")
public final class IMUCalibrateAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO implement action body      
      Model model = OpenSimDB.getInstance().getCurrentModel();
      if(model==null) return;

      try {
         final IMUCalibrationPanel panel = new IMUCalibrationPanel(model);
         BaseToolPanel.openToolDialog(panel, "IMU Model Calibration Tool");
      } catch (IOException ex) {
         ErrorDialog.displayIOExceptionDialog("Inverse Kinematics Tool Error","Error while initializing inverse kinematics tool",ex);
      }

    }
}
