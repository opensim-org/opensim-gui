package org.opensim.view.nodes;

import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.modeling.Model;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;

public final class ModelMakeCurrentAction extends CallableSystemAction {
    
    public void performAction() {
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        // Action shouldn't be available otherwise'
        OneModelNode modelNode = (OneModelNode) selected[0];
        Model mdl = modelNode.getModel();
        OpenSimDB.getInstance().setCurrentModel(mdl);
    }
    
    public String getName() {
        return NbBundle.getMessage(ModelMakeCurrentAction.class, "CTL_ModelMakeCurrentAction");
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
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        if(selected.length!=1) return false; // only if a single item is selected
        // Action shouldn't be available otherwise'
        OneModelNode modelNode = (OneModelNode) selected[0];
        Model mdl = modelNode.getModel();
        return OpenSimDB.getInstance().getCurrentModel()!=mdl;
    }
    
}
