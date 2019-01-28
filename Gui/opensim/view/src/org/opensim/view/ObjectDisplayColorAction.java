/* -------------------------------------------------------------------------- *
 * OpenSim: ObjectDisplayColorAction.java                                     *
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

import java.awt.Color;
import java.util.Vector;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;
import org.opensim.modeling.OpenSimObject;
import org.opensim.view.nodes.OneBodyNode;
import org.opensim.view.nodes.OneComponentNode;
import org.opensim.view.nodes.OpenSimObjectNode;
import org.opensim.view.nodes.OpenSimObjectNode.displayOption;
import org.opensim.view.nodes.PropertyEditorAdaptor;
import org.opensim.view.pub.ViewDB;

public final class ObjectDisplayColorAction extends ObjectAppearanceChangeAction {
   
   //--------------------------------------------------------------------------
    public void performAction() 
    { 
      JColorChooser objectColorChooser = new JColorChooser();
      Color newColor = objectColorChooser.showDialog( (JFrame)WindowManager.getDefault().getMainWindow(), "Select new color", Color.WHITE );  
      if (newColor != null) {
        Vector<OneComponentNode> nodes = collectAffectedComponentNodes();
        ViewDB.getInstance().setApplyAppearanceChange(false);
        ObjectDisplayColorAction.ChangeUserSelectedNodesColor(nodes, newColor ); 
        ViewDB.getInstance().setApplyAppearanceChange(true);
      }
    }
   
    //--------------------------------------------------------------------------
    public static void ChangeUserSelectedNodesColor(Vector<OneComponentNode> nodes, 
            Color newColor) {
        // Cycle through ComponentNode(s) and apply color
        float[] newColorComponentsAsFloatArray = newColor.getRGBComponents(null);
        double[] newColorComponentsAsDoubleArray = {newColorComponentsAsFloatArray[0], newColorComponentsAsFloatArray[1], newColorComponentsAsFloatArray[2]};
        for (OneComponentNode nextNode:nodes){
            ObjectDisplayColorAction.applyOperationToNode(nextNode, newColorComponentsAsDoubleArray);
        }
        
    }


    //--------------------------------------------------------------------------
    public static void applyOperationToNode(final OneComponentNode objectNode, double[] newColorComponents) {
        boolean hasColor = (objectNode instanceof ColorableInterface);
        if (hasColor) {
            //PropertyEditorAdaptor pea = new PropertyEditorAdaptor("color", objectNode);
            Color newColor = new Color((float) newColorComponents[0],
                    (float) newColorComponents[1], (float) newColorComponents[2]);
            ((ColorableInterface) objectNode).setColor(newColor);
        }
        objectNode.refreshNode();

    }

    // Make it available only if selected objects have representation and belong to same model
    public boolean isEnabled() {
       // The "hide" option is enabled unless every selected node is hidden.
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        boolean isColorable=true;
        for(int i=0; i<selected.length && isColorable; i++){
            isColorable = (selected[i] instanceof OpenSimObjectNode);
            if (isColorable){
                OpenSimObjectNode objectNode = (OpenSimObjectNode) selected[i];
                isColorable=objectNode.getValidDisplayOptions().contains(displayOption.Colorable);
            }
        }
        return isColorable;
   }
   
   public String getName() {
      return NbBundle.getMessage(ObjectDisplayColorAction.class, "CTL_ObjectDisplayColorAction");
   }
   
}
