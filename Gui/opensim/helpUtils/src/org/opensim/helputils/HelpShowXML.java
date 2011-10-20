package org.opensim.helputils;

import javax.swing.JFrame;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.openide.windows.WindowManager;

public final class HelpShowXML extends CallableSystemAction {
    
    public void performAction() {
        // TODO implement action body
        ShowXMLRepJDialog dlg = new ShowXMLRepJDialog((JFrame) WindowManager.getDefault().getMainWindow(), false);
        dlg.setVisible(true);
    }
    
    public String getName() {
        return NbBundle.getMessage(HelpShowXML.class, "CTL_HelpShowXML");
    }
    
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }
    
}
