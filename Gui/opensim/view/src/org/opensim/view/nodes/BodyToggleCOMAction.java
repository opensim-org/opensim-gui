package org.opensim.view.nodes;

import java.awt.event.ActionEvent;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.BooleanStateAction;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimObject;
import org.opensim.view.BodyDisplayer;
import org.opensim.view.BodyToggleFrameAction;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.pub.ViewDB;

public final class BodyToggleCOMAction extends BooleanStateAction {
    
    public BodyToggleCOMAction() {        
    }
    
    public void actionPerformed(ActionEvent actionEvent) {
        // TODO implement action body
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        // TODO implement action body
        boolean newState = !super.getBooleanState();
        for( int i=0; i<selected.length; i++ ){
            Node selectedNode = selected[i];
            if( selectedNode instanceof OneBodyNode )
                this.ShowCMForOneBodyNode( (OneBodyNode)selectedNode, newState, false );
            else if( selectedNode instanceof BodiesNode ){
                Model model=((BodiesNode)selectedNode).getModelForNode();
                ViewDB.getInstance().getModelVisuals(model).setShowCOM(newState);
            }
        }
        super.setBooleanState( newState );
        ViewDB.renderAll();
   }
    
   //-------------------------------------------------------------------------
   public static boolean  IsShowCMForBody( OpenSimObject openSimObjectAssociatedWithBody )
   {
      BodyDisplayer rep = BodyToggleFrameAction.GetBodyDisplayerForBody( openSimObjectAssociatedWithBody );  
      return rep==null ? false : rep.isShowCOM();
   }
    
    
   //----------------------------------------------------------------------------- 
   static public void  ShowCMForOneBodyNode( OneBodyNode oneBodyNode, boolean showCMIsTrueHideIsFalse, boolean renderAll )
   {
      if( oneBodyNode == null ) return; 
      BodyToggleCOMAction.ShowCMForBody( oneBodyNode.getOpenSimObject(), showCMIsTrueHideIsFalse, renderAll );
   }
   
   
   //-------------------------------------------------------------------------
   public static void  ShowCMForBody( OpenSimObject openSimObjectAssociatedWithBody, boolean showCMIsTrueHideIsFalse, boolean renderAll )
   {
      BodyDisplayer rep = BodyToggleFrameAction.GetBodyDisplayerForBody( openSimObjectAssociatedWithBody );
      if( rep != null )
      {
          rep.setShowCOM( showCMIsTrueHideIsFalse ); 
          if( renderAll ) ViewDB.renderAll();
      }     
   }
    
    
    public String getName() {
        return NbBundle.getMessage(BodyToggleCOMAction.class, "CTL_BodyToggleCOMAction");
    }
    
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
        super.putValue("noIconInMenu", Boolean.TRUE);
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
            BodyDisplayer rep = BodyToggleFrameAction.GetBodyDisplayerForBody( dNode.getOpenSimObject() );
            if( rep != null ) super.setBooleanState( rep.isShowCOM() );
            return true;
        }
        else if (selected[0] instanceof BodiesNode){
                Model model=((BodiesNode)selected[0]).getModelForNode();
                super.setBooleanState( ViewDB.getInstance().getModelVisuals(model).isShowCOM() );
                return true;
        }
        return false;
    }
    

}
