package org.opensim.view.experimentaldata;

import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.motions.MotionControlJPanel;

public final class ExperimentalObjectDisplayHideAction extends CallableSystemAction {
    
    public void performAction() {
        // Get experimental data object from selected nodes and invoke ".hide on them"
      Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
      for(int i=0; i < selected.length; i++){
         if(selected[i] instanceof ExperimentalDataNode) {
             ExperimentalDataNode node= (ExperimentalDataNode) selected[i];
             ExperimentalDataObject obj=node.getDataObject();
             // Tell MotionDisplayer to hide it
             obj.getMyGlyph().hide(obj.getGlyphIndex());
             //System.out.println("Hiding index "+obj.getGlyphIndex()+" of object "+obj.toString());
             obj.setDisplayed(false);
             obj.getMyGlyph().setModified();
         }
      }
      MotionControlJPanel.getInstance().getMasterMotion().applyTime();
    }
    
    public String getName() {
        return NbBundle.getMessage(ExperimentalObjectDisplayHideAction.class, "CTL_ExperimentalObjectDisplayHideAction");
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
        // Should we call super.isEnabled() too?
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        for(int i=0; i < selected.length; i++){
            if(selected[i] instanceof ExperimentalDataNode) {
                ExperimentalDataNode node= (ExperimentalDataNode) selected[i];
                ExperimentalDataObject obj=node.getDataObject();
                if (obj.isDisplayed()) return true;
            }
        }
        return false;
    }
    
}
