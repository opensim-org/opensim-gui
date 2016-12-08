/*
 * Copyright (c)  2005-2008, Stanford University
 * Use of the OpenSim software in source form is permitted provided that the following
 * conditions are met:
 * 	1. The software is used only for non-commercial research and education. It may not
 *     be used in relation to any commercial activity.
 * 	2. The software is not distributed or redistributed.  Software distribution is allowed 
 *     only through https://simtk.org/home/opensim.
 * 	3. Use of the OpenSim software or derivatives must be acknowledged in all publications,
 *      presentations, or documents describing work in which OpenSim or derivatives are used.
 * 	4. Credits to developers may not be removed from executables
 *     created from modifications of the source.
 * 	5. Modifications of source code must retain the above copyright notice, this list of
 *     conditions and the following disclaimer. 
 * 
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 *  SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 *  TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 *  HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR BUSINESS INTERRUPTION) OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 *  WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.opensim.view;

import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.modeling.AbstractProperty;
import org.opensim.modeling.Actuator;
import org.opensim.modeling.Appearance;
import org.opensim.modeling.Body;
import org.opensim.modeling.Joint;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.SurfaceProperties;
import org.opensim.view.nodes.OneModelNode;
import org.opensim.view.nodes.OneWrapObjectNode;
import org.opensim.view.nodes.OpenSimObjectNode;
import org.opensim.view.nodes.PropertyEditorAdaptor;
import org.opensim.view.pub.ViewDB;

public abstract class ObjectDisplayShowHideBaseAction extends CallableSystemAction {
  
   private boolean show;

   ObjectDisplayShowHideBaseAction(boolean show) {
      this.show = show;
   }

   //------------------------------------------------------------------
   public static Model  getModelForOpenSimObjectOrNull( OpenSimObject object ) 
   {
      Actuator act = Actuator.safeDownCast(object);
      if( act != null ) return act.getModel();
      Body body = Body.safeDownCast(object);
      if( body != null ) return body.getModel();
      Joint joint = Joint.safeDownCast( object );
      if( joint != null ) return joint.getModel();
      return null;
   }

   public boolean isEnabled() {
      // If show==true: The "show" option is enabled unless every selected node is shown.
      // If show==false: The "hide" option is enabled unless every selected node is hidden.
      Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
      for( int i=0; i < selected.length; i++ ) {
         if( selected[i] instanceof OneModelNode ) {
            if( ViewDB.getInstance().getDisplayStatus(((OneModelNode)selected[i]).getModel()) != show )
               return true;
         } else if ( selected[i] instanceof OpenSimObjectNode ) {
            OpenSimObjectNode objectNode = (OpenSimObjectNode) selected[i];
            if (objectNode.getChildren().getNodesCount()>0) // TODO: actually check children
               return true;
            int displayStatus = ViewDB.getInstance().getDisplayStatus(objectNode.getOpenSimObject());
            if ((show && displayStatus==0) || (!show && displayStatus==1) || displayStatus == 2)
               return true;
         }
      }
      return false;
   }

   public void performAction() {
      Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
      for(int i=0; i < selected.length; i++){
         if(selected[i] instanceof OneModelNode) {
            ViewDB.getInstance().toggleModelDisplay(((OneModelNode)selected[i]).getModel(), show);
         } else if(selected[i] instanceof OpenSimObjectNode) {
            this.applyOperationToNode( (OpenSimObjectNode)selected[i] );
         }
      }
      ViewDB.getInstance().repaintAll();
   }

    //-------------------------------------------------------------------------
    private void applyOperationToNode( final OpenSimObjectNode objectNode ) 
    {
        // Apply action recursively to children. 
        Children ch = objectNode.getChildren();
        if (ch.getNodesCount()>0){
            Node[] childNodes=ch.getNodes();
            for(int child=0; child < childNodes.length ; child++){
                if (!(childNodes[child] instanceof OpenSimObjectNode))
                    continue;
               OpenSimObjectNode childNode = (OpenSimObjectNode) childNodes[child];
               this.applyOperationToNode( childNode );
            }
        }
        //else
        OpenSimObject obj = objectNode.getOpenSimObject();
        boolean hasPreferenceProperty = obj.hasProperty("Appearance");
        
        if (hasPreferenceProperty){
            AbstractProperty apbn = obj.getPropertyByName("Appearance");
            boolean iso = apbn.isObjectProperty();
            //Model aModel, OpenSimObject obj, AbstractProperty prop, OpenSimObjectNode node
            OpenSimObject ap =  obj.getPropertyByName("Appearance").getValueAsObject();
            Appearance apObj = Appearance.safeDownCast(ap);
            SurfaceProperties surfApp = apObj.get_SurfaceProperties();
            PropertyEditorAdaptor pea = new PropertyEditorAdaptor(objectNode.getModelForNode(),
                    surfApp,
                    surfApp.getPropertyByName("representation"), objectNode);
            pea.setValueInt(show?3:0);
        }
        else {
            ViewDB.getInstance().toggleObjectsDisplay(obj, show);
            objectNode.refreshNode();
        }
    }
    
    //-------------------------------------------------------------------------
    public static void ApplyOperationToNodeWithShowHide( final OpenSimObjectNode objectNode, boolean showOrHide, boolean repaintAll, boolean skipWrappingSurfaces, boolean justWrappingSurfaces ) 
    {
        // Apply action recursively to children.
        Children children = objectNode.getChildren();
        if( children.getNodesCount() > 0 )
        {
            Node[] childrenNodes = children.getNodes();
            for( int i=0;  i < childrenNodes.length;  i++ )
            {
               Node childNodei = childrenNodes[i]; 
               if( !(childNodei instanceof OpenSimObjectNode) ) continue;
               if( skipWrappingSurfaces && childNodei instanceof OneWrapObjectNode ) continue;
               if( justWrappingSurfaces && childNodei instanceof OneWrapObjectNode ) continue;
               OpenSimObjectNode childNode = (OpenSimObjectNode)childNodei;
               ObjectDisplayShowHideBaseAction.ApplyOperationToNodeWithShowHide( childNode, showOrHide, false, skipWrappingSurfaces, justWrappingSurfaces );
            }
        }
        
        // After applying recursively, apply to openSimObjectNode and possibly repaint all.
        ObjectDisplayShowHideBaseAction.ShowOrHideOpenSimObject( objectNode.getOpenSimObject(), showOrHide, repaintAll );
    }
    
    
    //-------------------------------------------------------------------------
    private static void  ShowOrHideOpenSimObject( OpenSimObject openSimObject, boolean showIsTrueHideIsFalse, boolean repaintAll )
    {
       ViewDB.getInstance().toggleObjectsDisplay( openSimObject, showIsTrueHideIsFalse );
       if( repaintAll ) ViewDB.getInstance().repaintAll();    
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
