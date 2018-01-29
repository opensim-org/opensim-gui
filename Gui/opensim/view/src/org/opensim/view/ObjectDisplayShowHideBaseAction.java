/* -------------------------------------------------------------------------- *
 * OpenSim: ObjectDisplayShowHideBaseAction.java                              *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib, Paul Mitiguy                                       *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may    *
 * not use this file except in compliance with the License. You may obtain a  *
 * copy of the License at http://www.apache.org/licenses/LICENSE-2.0          *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 * -------------------------------------------------------------------------- */

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
        else { // Non-persistent, just change visuals
            ViewDB.getInstance().toggleObjectDisplay(obj, show);
        }
    }
    
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
    }
    

}
