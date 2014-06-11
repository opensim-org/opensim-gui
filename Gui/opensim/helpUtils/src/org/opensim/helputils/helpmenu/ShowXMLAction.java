package org.opensim.helputils.helpmenu;

import javax.swing.JFrame;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.openide.windows.WindowManager;
import org.opensim.helputils.ShowXMLRepJDialog;

public final class ShowXMLAction extends CallableSystemAction {
    
    public void performAction() {
        // TODO implement action body
        ShowXMLRepJDialog dlg = new ShowXMLRepJDialog((JFrame) WindowManager.getDefault().getMainWindow(), false);
        dlg.setVisible(true);
    }
    
    public String getName() {
        return NbBundle.getMessage(ShowXMLAction.class, "CTL_ShowXML");
    }
    
    @Override
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    @Override
    protected boolean asynchronous() {
        return false;
    }
    
}
