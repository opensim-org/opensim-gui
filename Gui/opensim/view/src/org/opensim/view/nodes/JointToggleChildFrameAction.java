package org.opensim.view.nodes;

import java.awt.event.ActionEvent;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.BooleanStateAction;
import org.opensim.modeling.Body;
import org.opensim.modeling.Joint;
import org.opensim.modeling.OpenSimObject;
import org.opensim.view.BodyDisplayer;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.pub.ViewDB;
import vtk.vtkProp3D;

public final class JointToggleChildFrameAction extends BooleanStateAction {
    
    public JointToggleChildFrameAction() {        
    }
    
    public void actionPerformed(ActionEvent actionEvent) {
        // TODO implement action body
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        // TODO implement action body
        boolean newState = !getBooleanState();
        for(int i=0;i<selected.length;i++){
            Node selectedNode = selected[i];
            if (selectedNode instanceof OneJointNode){
                OpenSimObject object=((OneJointNode) selectedNode).getOpenSimObject();
                Joint jnt = Joint.safeDownCast(object);
                /*
                Body b = jnt.getBody();
                vtkProp3D visuals=ViewDB.getInstance().getModelVisuals(b.getModel()).getVtkRepForObject(b);
                if (visuals instanceof BodyDisplayer){
                    BodyDisplayer rep = (BodyDisplayer) visuals;
                    rep.setShowJointBFrame(newState);
                }
                */
            }
        }
         setBooleanState(newState);
        ViewDB.getInstance().renderAll();
   }
    
    public String getName() {
        return NbBundle.getMessage(JointToggleChildFrameAction.class, "CTL_JointToggleChildFrameAction");
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
        if (selected[0] instanceof OneJointNode){
            OneJointNode dNode = (OneJointNode)selected[0];
            Joint jnt = Joint.safeDownCast(dNode.getOpenSimObject());
            Body b = jnt.getChildBody();
            vtkProp3D visuals=ViewDB.getInstance().getModelVisuals(b.getModel()).getVtkRepForObject(b);
            if (visuals instanceof BodyDisplayer){
               BodyDisplayer rep = (BodyDisplayer) visuals;
               setBooleanState(rep.isShowJointBFrame());
            }
            return true;
        }
        return false;
    }
}
