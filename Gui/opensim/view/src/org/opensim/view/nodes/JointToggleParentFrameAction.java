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

public final class JointToggleParentFrameAction extends BooleanStateAction {
    
    public JointToggleParentFrameAction() {        
    }
    
    public void actionPerformed(ActionEvent actionEvent) {
        // TODO implement action body
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        // TODO implement action body
        for(int i=0;i<selected.length;i++){
            Node selectedNode = selected[i];
            if (selectedNode instanceof OneJointNode){
                OpenSimObject object=((OneJointNode) selectedNode).getOpenSimObject();
                Joint jnt = Joint.safeDownCast(object);
                Body b = jnt.getParentBody();
                vtkProp3D visuals=ViewDB.getInstance().getModelVisuals(b.getModel()).getVtkRepForObject(b);
                if (visuals instanceof BodyDisplayer){
                    BodyDisplayer rep = (BodyDisplayer) visuals;
                    boolean newState = !getBooleanState();
                    Body cb = jnt.getBody();
                    rep.setShowJointPFrame(cb, newState);
                    setBooleanState(newState);
                }
            }
        }
        ViewDB.renderAll();
   }
    
    public String getName() {
        return NbBundle.getMessage(JointToggleParentFrameAction.class, "CTL_JointToggleParentFrameAction");
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
            Body pb = jnt.getParentBody();
            Body b = jnt.getBody();
            vtkProp3D visuals=ViewDB.getInstance().getModelVisuals(pb.getModel()).getVtkRepForObject(pb);
            if (visuals instanceof BodyDisplayer){
              BodyDisplayer rep = (BodyDisplayer) visuals;
              setBooleanState(rep.isShowJointPFrame(b));
            }
            return true;
        }
        return false;
    }
    
}
