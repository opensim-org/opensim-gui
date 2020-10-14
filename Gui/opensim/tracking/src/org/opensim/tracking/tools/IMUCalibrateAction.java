/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.tracking.tools;

import org.openide.util.actions.CallableSystemAction;
import org.openide.util.NbBundle;
import org.openide.util.HelpCtx;
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
/*
@ActionID(
        category = "Tools",
        id = "org.opensim.tracking.tools.IMUCalibrateAction"
)
@ActionRegistration(
        displayName = "#CTL_IMUCalibrateAction"
)
@ActionReference(path = "Menu/Tools", position = 190, separatorBefore = 150)
@Messages("CTL_IMUCalibrateAction=IMU Calibrate...")
*/
public final class IMUCalibrateAction  extends CallableSystemAction {

    @Override
    public void performAction() {
        // TODO implement action body      
      Model model = OpenSimDB.getInstance().getCurrentModel();
      if(model==null) return;

      try {
         final IMUCalibrationPanel panel = new IMUCalibrationPanel(model);
         BaseToolPanel.openToolDialog(panel, "IMU Model Calibration Tool");
      } catch (IOException ex) {
         ErrorDialog.displayIOExceptionDialog("IMU Model Calibration Tool Error","Error while initializing IMUCalibrateAction",ex);
      }
    }
       public String getName() {
      return NbBundle.getMessage(IMUCalibrateAction.class, "CTL_IMUCalibrateAction");
   }
   
   protected void initialize() {
      super.initialize();
      // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
      putValue("noIconInMenu", Boolean.TRUE);
   }
   
   protected boolean asynchronous() {
      return false;
   }
   public HelpCtx getHelpCtx() {
      return HelpCtx.DEFAULT_HELP;
   }
   public boolean isEnabled() {
      return (OpenSimDB.getInstance().getCurrentModel()!=null);
   }

}
