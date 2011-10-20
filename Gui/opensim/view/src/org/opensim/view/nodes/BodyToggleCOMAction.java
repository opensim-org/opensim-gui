package org.opensim.view.nodes;

import java.awt.event.ActionEvent;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.BooleanStateAction;
import org.opensim.modeling.Body;
import org.opensim.modeling.Joint;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimObject;
import org.opensim.view.BodyDisplayer;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.pub.ViewDB;
import vtk.vtkProp3D;

public final class BodyToggleCOMAction extends BooleanStateAction {
    
    public BodyToggleCOMAction() {        
    }
    
    public void actionPerformed(ActionEvent actionEvent) {
        // TODO implement action body
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        // TODO implement action body
        boolean newState = !getBooleanState();
        for(int i=0;i<selected.length;i++){
            Node selectedNode = selected[i];
            if (selectedNode instanceof OneBodyNode){
                OpenSimObject object=((OneBodyNode) selectedNode).getOpenSimObject();
                Body b =  Body.safeDownCast(object);
                vtkProp3D visuals=ViewDB.getInstance().getModelVisuals(b.getModel()).getVtkRepForObject(b);
                if (visuals instanceof BodyDisplayer){
                    BodyDisplayer rep = (BodyDisplayer) visuals;
                    rep.setShowCOM(newState);
                }
            }
            else if (selectedNode instanceof BodiesNode){
                Model model=((BodiesNode)selectedNode).getModelForNode();
                ViewDB.getInstance().getModelVisuals(model).setShowCOM(newState);
            }
        }
         setBooleanState(newState);
        ViewDB.getInstance().renderAll();
   }
    
    public String getName() {
        return NbBundle.getMessage(BodyToggleCOMAction.class, "CTL_BodyToggleCOMAction");
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
            vtkProp3D visuals=ViewDB.getInstance().getModelVisuals(b.getModel()).getVtkRepForObject(b);
            if (visuals instanceof BodyDisplayer){
               BodyDisplayer rep = (BodyDisplayer) visuals;
               setBooleanState(rep.isShowCOM());
            }
            return true;
        }
        else if (selected[0] instanceof BodiesNode){
                Model model=((BodiesNode)selected[0]).getModelForNode();
                setBooleanState(ViewDB.getInstance().getModelVisuals(model).isShowCOM());
                return true;
        }
        return false;
    }
}
