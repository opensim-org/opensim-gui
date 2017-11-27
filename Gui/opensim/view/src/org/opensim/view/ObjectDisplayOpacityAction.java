/* -------------------------------------------------------------------------- *
 * OpenSim: ObjectDisplayOpacityAction.java                                   *
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

import java.util.Vector;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.opensim.view.nodes.OneComponentNode;
import org.opensim.view.nodes.OpenSimObjectNode;
import org.opensim.view.pub.ViewDB;

public final class ObjectDisplayOpacityAction extends ObjectAppearanceChangeAction {

   public void performAction() {
      Vector<OneComponentNode> objects = collectAffectedComponentNodes();
      ViewDB.getInstance().setApplyAppearanceChange(false);
      ObjectDisplayOpacityPanel.showDialog(objects);
      ViewDB.getInstance().setApplyAppearanceChange(true);
   }

   
   public String getName() {
      return NbBundle.getMessage(ObjectDisplayOpacityAction.class, "CTL_ObjectDisplayOpacityAction");
   }
   
   protected void initialize() {
      super.initialize();
      // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
      putValue("noIconInMenu", Boolean.TRUE);
   }
 
    // Make it available only if selected objects have representation and belong to same model
    public boolean isEnabled() {
        // The "hide" option is enabled unless every selected node is hidden.
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        boolean isColorable = true;
        for (int i = 0; i < selected.length && isColorable; i++) {
            isColorable = (selected[i] instanceof OpenSimObjectNode);
            if (isColorable) {
                OpenSimObjectNode objectNode = (OpenSimObjectNode) selected[i];
                isColorable = objectNode.getValidDisplayOptions().contains(OpenSimObjectNode.displayOption.Colorable);
            }
        }
        return isColorable;
    }
    
    public static void ChangeUserSelectedNodesOpacity(Vector<OneComponentNode> nodes, double newOpacity) {
        for (OneComponentNode nextNode:nodes){
            if (nextNode.isLeaf())
                ObjectDisplayOpacityAction.applyOperationToNode(nextNode, newOpacity);
            else {
                Vector<OneComponentNode> descendents = ObjectDisplayColorAction.collectAffectedComponentNodesFromSelection(new Node[]{nextNode});
                for (OneComponentNode desc:descendents)
                    ObjectDisplayOpacityAction.applyOperationToNode(desc, newOpacity);
            }
        }
        ViewDB.getInstance().repaintAll();
     }
    private static void applyOperationToNode(final OneComponentNode objectNode, double newOpacity) {
        boolean hasColor = (objectNode instanceof ColorableInterface);
        if (hasColor) {
            ((ColorableInterface) objectNode).setOpacity(newOpacity);
        }
        objectNode.refreshNode();
    }
    
    
   
  
}
