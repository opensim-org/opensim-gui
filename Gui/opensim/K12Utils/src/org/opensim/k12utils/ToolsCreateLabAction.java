package org.opensim.k12utils;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.utils.TheApp;

public final class ToolsCreateLabAction extends CallableSystemAction {
    
    public void performAction() {
        // TODO implement action body
        CreateLabDialog createLabDialog=new CreateLabDialog(TheApp.getAppFrame(), true);
        createLabDialog.setVisible(true);
    }
    
    public String getName() {
        return NbBundle.getMessage(ToolsCreateLabAction.class, "CTL_ToolsCreateLabAction");
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
