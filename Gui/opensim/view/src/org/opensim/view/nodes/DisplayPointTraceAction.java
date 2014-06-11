package org.opensim.view.nodes;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.modeling.OpenSimObject;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.experimentaldata.ExperimentalDataVisuals;
import org.opensim.view.experimentaldata.ModelForExperimentalData;
import org.opensim.view.pub.ViewDB;

public final class DisplayPointTraceAction extends CallableSystemAction {

    public boolean isEnabled() {
       // The "show" option is enabled unless every selected node is shown.
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        boolean enabled=true;
        for (Node node:selected){
       }
        return enabled;
    }
    public void performAction() {
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        
        for (Node node:selected){
            ModelForExperimentalData mdl = (ModelForExperimentalData) ((OpenSimObjectNode)node).getModelForNode();
            ExperimentalDataVisuals vis=(ExperimentalDataVisuals) ViewDB.getInstance().getModelVisuals(mdl);
            vis.toggleTraceDisplay(((OpenSimObjectNode)node).getOpenSimObject());
        }
    }

    public String getName() {
        return(NbBundle.getMessage(DisplayPointTraceAction.class, "CTL_DisplayPointTraceAction"));
    }

    public boolean asynchronus() {
        return false;
    }
   protected void initialize() {
      super.initialize();
      // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
      putValue("noIconInMenu", Boolean.TRUE);
   }
   
   public HelpCtx getHelpCtx() {
      return HelpCtx.DEFAULT_HELP;
   }
   

}
