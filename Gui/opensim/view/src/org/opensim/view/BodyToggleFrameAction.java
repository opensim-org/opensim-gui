package org.opensim.view;

import java.awt.event.ActionEvent;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.BooleanStateAction;
import org.opensim.modeling.Body;
import org.opensim.view.nodes.OneBodyNode;
import org.opensim.view.pub.ViewDB;
import vtk.vtkProp3D;

public final class BodyToggleFrameAction extends BooleanStateAction {
    
    public BodyToggleFrameAction(){
    }
    
    public void performAction() {
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        // TODO implement action body
        OneBodyNode dNode = (OneBodyNode)selected[0];
        Body b = Body.safeDownCast(dNode.getOpenSimObject());
        //System.out.println("Setting showAxis to "+getBooleanState());
        vtkProp3D visuals=ViewDB.getInstance().getModelVisuals(b.getModel()).getVtkRepForObject(b);
        if (visuals instanceof BodyDisplayer){
            BodyDisplayer rep = (BodyDisplayer) visuals;
            boolean newState = getBooleanState();
            rep.setShowAxes(getBooleanState());
        }
    }
    
    public String getName() {
        return NbBundle.getMessage(BodyToggleFrameAction.class, "CTL_BodyToggleFrameAction");
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
        //if(selected.length!=1) return false; // only if a single item is selected
        // Action shouldn't be available otherwise
        if (selected[0] instanceof OneBodyNode){
            OneBodyNode dNode = (OneBodyNode)selected[0];
            Body b = Body.safeDownCast(dNode.getOpenSimObject());
            setBooleanState(b.getDisplayer().getShowAxes());
            return true;
        }
        return false;
    }

    public void actionPerformed(ActionEvent actionEvent) {
        //super.actionPerformed(actionEvent);
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        // TODO implement action body
        boolean newState = !getBooleanState();
        for(int i=0;i<selected.length;i++){
            Node selectedNode = selected[i];
            if (selectedNode instanceof OneBodyNode){
                OneBodyNode dNode = (OneBodyNode)selectedNode;
                Body b = Body.safeDownCast(dNode.getOpenSimObject());
                //System.out.println("Setting showAxis to "+getBooleanState());
                vtkProp3D visuals=ViewDB.getInstance().getModelVisuals(b.getModel()).getVtkRepForObject(b);
                if (visuals instanceof BodyDisplayer){
                    BodyDisplayer rep = (BodyDisplayer) visuals;
                    rep.setShowAxes(newState);
                }
            }
        }
        setBooleanState(newState);
        ViewDB.getInstance().renderAll();
    }
    
}
