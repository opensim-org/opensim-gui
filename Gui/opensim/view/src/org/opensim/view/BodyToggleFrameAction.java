package org.opensim.view;

import java.awt.event.ActionEvent;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.BooleanStateAction;
import org.opensim.modeling.Body;
import org.opensim.modeling.OpenSimObject;
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
        boolean newState = super.getBooleanState();
        BodyToggleFrameAction.ShowAxesForBody( dNode.getOpenSimObject(), newState, false );
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
        if( selected[0] instanceof OneBodyNode ){
            OneBodyNode dNode = (OneBodyNode)selected[0];
            Body b = Body.safeDownCast( dNode.getOpenSimObject() );
            super.setBooleanState( b.getDisplayer().getShowAxes() );
            return true;
        }
        return false;
    }

    public void actionPerformed(ActionEvent actionEvent) {
        //super.actionPerformed(actionEvent);
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        // TODO implement action body
        boolean newState = !super.getBooleanState();
        for( int i=0;  i<selected.length;  i++ ){
            Node selectedNode = selected[i];
            if( selectedNode instanceof OneBodyNode )
                 BodyToggleFrameAction.ShowAxesForOneBodyNode( (OneBodyNode)selectedNode, newState, false );
        }
        super.setBooleanState( newState );
        ViewDB.ViewDBGetInstanceRenderAll();
    }
  
     
    //-------------------------------------------------------------------------
    public static boolean  IsShowAxesForBody( OpenSimObject openSimObjectAssociatedWithBody )
    {
       BodyDisplayer rep = BodyToggleFrameAction.GetBodyDisplayerForBody( openSimObjectAssociatedWithBody );  
       return rep==null ? false : rep.isShowAxes();
    }
    
    
    //----------------------------------------------------------------------------- 
    static public void  ShowAxesForOneBodyNode( OneBodyNode oneBodyNode, boolean showAxesTrueHideIsFalse, boolean renderAll )
    {
       if( oneBodyNode == null ) return; 
       BodyToggleFrameAction.ShowAxesForBody( oneBodyNode.getOpenSimObject(), showAxesTrueHideIsFalse, renderAll );
    }
    
    //-------------------------------------------------------------------------
    public static void  ShowAxesForBody( OpenSimObject openSimObjectAssociatedWithBody, boolean showAxesTrueHideIsFalse, boolean renderAll )
    {
       BodyDisplayer rep = BodyToggleFrameAction.GetBodyDisplayerForBody( openSimObjectAssociatedWithBody );
       if( rep != null )
       {
           rep.setShowAxes( showAxesTrueHideIsFalse );
           if( renderAll ) ViewDB.ViewDBGetInstanceRenderAll();
       }     
    }
    
    
    //-------------------------------------------------------------------------
    public static BodyDisplayer  GetBodyDisplayerForBody( OpenSimObject openSimObjectAssociatedWithBody )
    {
       Body b = Body.safeDownCast( openSimObjectAssociatedWithBody );
       SingleModelVisuals viz = ViewDB.getInstance().getModelVisuals( b.getModel() );
       if (viz == null) return null;
       vtkProp3D visuals = viz.getVtkRepForObject(b);
       return ( visuals instanceof BodyDisplayer ) ? (BodyDisplayer)visuals : null;
    }
    
}
