package org.opensim.view.nodes;

import java.util.ArrayList;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.modeling.Model;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.actions.FileCloseAction;
import org.opensim.view.pub.OpenSimDB;

public final class ModelCloseSelectedAction extends CallableSystemAction {
   
    public void performAction() {
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        // Action shouldn't be available otherwise'
        // Cycle thru selected models and cache referenes since closing a model changes selected nodes
        ArrayList<Model> modelsToClose = new ArrayList<Model>();
        for(int i=0; i<selected.length; i++){
            if (selected[i] instanceof ConcreteModelNode){
                ConcreteModelNode modelNode = (ConcreteModelNode) selected[i];
                Model mdl = modelNode.getModel();
                modelsToClose.add(mdl);
            }
        }
        OpenSimDB.setCurrentCloseModelDefaultAction(OpenSimDB.CloseModelDefaultAction.PROMPT);
        boolean proceedWithClosing=true;
        for(int i=0; i<modelsToClose.size() && proceedWithClosing; i++){
            // Piggyback on common code in FileCloseAction
            proceedWithClosing = FileCloseAction.closeModel(modelsToClose.get(i), i==0 && modelsToClose.size()>1);
        }
    }
   
   public String getName() {
      return NbBundle.getMessage(ModelCloseSelectedAction.class, "CTL_ModelCloseSelectedAction");
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
      return selected.length>=1;
   }
}
