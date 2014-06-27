package org.opensim.tracking.tools;

import java.io.IOException;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.modeling.Model;
import org.opensim.tracking.AnalyzeAndForwardToolPanel;
import org.opensim.tracking.BaseToolPanel;
import org.opensim.utils.ErrorDialog;
import org.opensim.view.pub.OpenSimDB;

public final class StaticOptimizationAction extends CallableSystemAction {
    
    public void performAction() {
        // TODO implement action body
        Model model = OpenSimDB.getInstance().getCurrentModel();
        if(model==null) return;
        
        try {
            final AnalyzeAndForwardToolPanel panel = new AnalyzeAndForwardToolPanel(model,AnalyzeAndForwardToolPanel.Mode.StaticOptimization);
            BaseToolPanel.openToolDialog(panel, "Static Optimization Tool");
        } catch (IOException ex) {
            ErrorDialog.displayIOExceptionDialog("Static Optimization Tool Error","Error while initializing static optimization tool",ex);
        }
    }
    
    public String getName() {
        return NbBundle.getMessage(StaticOptimizationAction.class, "CTL_StaticOptimizationAction");
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
    
    public boolean isEnabled() {
        return (OpenSimDB.getInstance().getCurrentModel()!=null &&
                OpenSimDB.getInstance().getCurrentModel().getForceSet().getSize()!=0);
    }
}
