package org.opensim.view.nodes;

import javax.swing.JButton;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.modeling.Model;
import org.opensim.view.ModelInfoJPanel;


public final class ModelInfoAction extends CallableSystemAction {
    
    public void performAction() {
        ModelInfoJPanel infoPanel=new ModelInfoJPanel();
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        // Action shouldn't be available otherwise'
        OneModelNode modelNode = (OneModelNode) selected[0];
        Model mdl = modelNode.getModel();
        infoPanel.setModelName(mdl.getName());
        infoPanel.setModelFile(mdl.getInputFileName());
        infoPanel.setDynamicsEngineName(mdl.getSimbodyEngine().getConcreteClassName());
        infoPanel.setAuthors(
                mdl.getCredits());
        infoPanel.setReferences(mdl.getPublications());
        DialogDescriptor dlg = new DialogDescriptor(infoPanel, "Model Info.");
        dlg.setOptions(new Object[]{new JButton("Close")});
        dlg.setClosingOptions(null);
        DialogDisplayer.getDefault().notify(dlg);
    }
    
    public String getName() {
        return NbBundle.getMessage(ModelInfoAction.class, "CTL_ModelInfoAction");
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
      return selected.length==1;
    }
}
