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

import java.util.Vector;
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
import org.opensim.view.nodes.OneComponentNode;
import org.opensim.view.nodes.OneGeometryNode;
import org.opensim.view.nodes.OneModelNode;
import org.opensim.view.nodes.OneWrapObjectNode;
import org.opensim.view.nodes.OpenSimObjectNode;
import org.opensim.view.nodes.PropertyEditorAdaptor;
import org.opensim.view.pub.ViewDB;

public abstract class ObjectDisplayShowHideBaseAction extends ObjectAppearanceChangeAction {
  
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
            //if( ViewDB.getInstance().getDisplayStatus(((OneModelNode)selected[i]).getModel()) != show )
               return true; // Too muuch communication with visualizer to decide if shown or not
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
      ViewDB.getInstance().setApplyAppearanceChange(false);
      Vector<OneComponentNode> nodes = collectAffectedComponentNodes();
      System.out.println("Num nodes="+nodes.size());
      for (OneComponentNode nextNode:nodes){
            this.applyOperationToNode( nextNode );
      }
      ViewDB.getInstance().setApplyAppearanceChange(true);
      ViewDB.getInstance().repaintAll();
   }

    //-------------------------------------------------------------------------
    private void applyOperationToNode( final OneComponentNode objectNode ) 
    {
        OpenSimObject obj = objectNode.getOpenSimObject();
        if (objectNode instanceof ColorableInterface){
            ((ColorableInterface)objectNode).setVisible(show);
            return;
        }
        boolean hasAppearanceProperty = obj.hasProperty("Appearance");
        
        if (hasAppearanceProperty){
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
    }
    
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
    }
    

}
