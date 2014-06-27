package org.opensim.view.experimentaldata;

import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.motions.MotionControlJPanel;
import org.opensim.view.nodes.ConcreteModelNode;
import org.opensim.view.nodes.OpenSimObjectNode;
import org.opensim.view.pub.ViewDB;

public final class ExperimentalObjectDisplayShowOnlyAction extends CallableSystemAction {
    
    public void performAction() {
        // Get experimental data object from selected nodes and invoke ".hide on them"
      Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
      // Hide all siblings
      Node[] siblings = null;
      if (selected.length >=1 && selected[0] instanceof ExperimentalDataNode)
          siblings = selected[0].getParentNode().getChildren().getNodes();
      for(int i=0; i < siblings.length; i++){
         if(siblings[i] instanceof ExperimentalDataNode) {
             ExperimentalDataNode node= (ExperimentalDataNode) siblings[i];
             ExperimentalDataObject obj=node.getDataObject();
             // Tell MotionDisplayer to hide it
             obj.getMyGlyph().hide(obj.getGlyphIndex());
             obj.setDisplayed(false);
             obj.getMyGlyph().setModified();
         }
      }
      // No show selected ones
      for(int i=0; i < selected.length; i++){
         if(selected[i] instanceof ExperimentalDataNode) {
             ExperimentalDataNode node= (ExperimentalDataNode) selected[i];
             ExperimentalDataObject obj=node.getDataObject();
             // Tell MotionDisplayer to hide it
             obj.getMyGlyph().show(obj.getGlyphIndex());
             obj.setDisplayed(true);
             obj.getMyGlyph().setModified();
         }
      }
      MotionControlJPanel.getInstance().getMasterMotion().applyTime();
    }
    
    public String getName() {
        return NbBundle.getMessage(ExperimentalObjectDisplayShowOnlyAction.class, "CTL_ExperimentalObjectDisplayShowOnlyAction");
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
