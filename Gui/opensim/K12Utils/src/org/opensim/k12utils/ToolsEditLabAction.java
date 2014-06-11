package org.opensim.k12utils;

import java.io.FileNotFoundException;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.utils.FileUtils;
import org.opensim.utils.TheApp;

public final class ToolsEditLabAction extends CallableSystemAction {
    
    public void performAction() {
        // Browse for an oscript file
        String fileName = FileUtils.getInstance().browseForFilename(".oscript", "Script file", true);
        // TODO implement action body
        if (fileName==null) return;
        EditLabDialog editLabDialog;
        try {
            editLabDialog = new EditLabDialog(fileName);
            editLabDialog.setVisible(true);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    
    public String getName() {
        return NbBundle.getMessage(ToolsEditLabAction.class, "CTL_ToolsEditLabAction");
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
