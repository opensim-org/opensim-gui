/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.tracking.tools;

import java.io.IOException;
import org.openide.util.actions.CallableSystemAction;
import org.openide.util.NbBundle;
import org.openide.util.HelpCtx;
import org.opensim.modeling.Model;
import org.opensim.tracking.BaseToolPanel;
import org.opensim.tracking.IMUIKToolPanel;
import org.opensim.utils.ErrorDialog;
import org.opensim.view.pub.OpenSimDB;


public final class IMUIKToolAction extends CallableSystemAction {
   public void performAction() {
        // TODO implement action body      
        Model model = OpenSimDB.getInstance().getCurrentModel();
        if (model == null) {
            return;
        }

        try {
            final IMUIKToolPanel panel = new IMUIKToolPanel(model);
            BaseToolPanel.openToolDialog(panel, "IMU Inverse Kinematics Tool");
        } catch (IOException ex) {
            ErrorDialog.displayIOExceptionDialog("Inverse Kinematics Tool Error", "Error while initializing inverse kinematics tool", ex);
        }
    }
   public String getName() {
      return NbBundle.getMessage(IMUIKToolAction.class, "CTL_IMUIKToolAction");
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
      return (OpenSimDB.getInstance().getCurrentModel()!=null &&
              OpenSimDB.getInstance().getCurrentModel().getNumCoordinates()>=1);
   }

}
