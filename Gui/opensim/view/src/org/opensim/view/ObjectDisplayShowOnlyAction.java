/* -------------------------------------------------------------------------- *
 * OpenSim: ObjectDisplayShowOnlyAction.java                                  *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib                                                     *
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

import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.view.nodes.OneModelNode;
import org.opensim.view.nodes.OpenSimObjectNode;
import org.opensim.view.nodes.OpenSimObjectSetNode;
import org.opensim.view.pub.ViewDB;

public final class ObjectDisplayShowOnlyAction extends CallableSystemAction {
   
   public boolean isEnabled() {
      Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
      if (selected.length==0) return false;
      Object  parent = selected[0].getParentNode();
      boolean sameParent=true;
      for(int i=0; i < selected.length && sameParent; i++){
          sameParent = (selected[i].getParentNode().equals(parent));
      }
      return sameParent;
   }
   
   public void performAction() {
      // TODO implement action body
      Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
      // The general scenario is to hide parent then show node and children
      // one exception is if the parent node is a model since hiding it in this case
      // makes it impossible to show selected node. This sitution is handled by overriding the behavior
      // in appropriate nodes.
      for (int i = 0; i < selected.length; i++) {
           if (!(selected[i] instanceof OpenSimObjectNode)) {
               continue;
           }

           OpenSimObjectNode objectNode = (OpenSimObjectNode) selected[i];
           Node parentNode = objectNode.getParentNode();
           /*
            // For Actuators we want to go up to the ActuatorSet node rather than the group node
            // A cleaner solution would be to make nodes hold pointer to which node would
            // show only be relative to.
            while (!(parentNode instanceof OpenSimObjectSetNode) && 
            !(parentNode instanceof OneModelNode))
            parentNode = (OpenSimObjectNode) (parentNode.getParentNode());
            */
           Children siblings = parentNode.getChildren();
           Node[] siblingNodes = siblings.getNodes();
           for (int j = 0; j < siblingNodes.length; j++) {
               Node nextSibiling = siblingNodes[j];
               if (nextSibiling == objectNode) {
                   continue;
               }
               OpenSimObjectNode n = (OpenSimObjectNode) siblingNodes[j];
               ViewDB.getInstance().toggleObjectsDisplay(n.getOpenSimObject(), false);
           }
       }
       for (int i = 0; i < selected.length; i++) {
           if (!(selected[i] instanceof OpenSimObjectNode)) {
               continue;
           }
           OpenSimObjectNode objectNode = (OpenSimObjectNode) selected[i];
           ViewDB.getInstance().toggleObjectDisplay(objectNode.getOpenSimObject(), true);
       }
       ViewDB.getInstance().repaintAll();
   }
   
   public String getName() {
      return NbBundle.getMessage(ObjectDisplayShowOnlyAction.class, "CTL_ObjectDisplayShowOnlyAction");
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
