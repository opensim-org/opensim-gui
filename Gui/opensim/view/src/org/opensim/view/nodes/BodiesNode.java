/* -------------------------------------------------------------------------- *
 * OpenSim: BodiesNode.java                                                   *
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
package org.opensim.view.nodes;

import java.awt.Image;
import java.net.URL;
import java.util.List;
import javax.swing.ImageIcon;
import java.util.ResourceBundle;
import javax.swing.Action;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.opensim.modeling.Body;
import org.opensim.modeling.OpenSimObjectSet;
import org.opensim.view.nodes.OpenSimObjectNode.displayOption;

/**
 * Node class to wrap Model's collection of SimmBodies
 */
public class BodiesNode extends OpenSimObjectSetNode {
    boolean topological=false;
    private static ResourceBundle bundle = NbBundle.getBundle(BodiesNode.class);

    public BodiesNode(OpenSimObjectSet bodySet) {
        super(bodySet);
        setDisplayName(NbBundle.getMessage(BodiesNode.class, "CTL_Bodies"));
        //Stack<OneBodyNode> stack = new Stack<OneBodyNode>();
        Children children = getChildren();
        for (int bodyNum=0; bodyNum < bodySet.getSize(); bodyNum++ ){

            Body body = Body.safeDownCast(bodySet.get(bodyNum));
            OneBodyNode node = new OneBodyNode(body);
            Node[] arrNodes = new Node[1];
            arrNodes[0] = node;
            children.add(arrNodes);
        }
      addDisplayOption(displayOption.Isolatable);
      addDisplayOption(displayOption.Showable);
      addDisplayOption(displayOption.Colorable);
    }

      public Image getIcon(int i) {
      URL imageURL=null;
      try {
         imageURL = Class.forName("org.opensim.view.nodes.OpenSimNode").getResource("/org/opensim/view/nodes/icons/bodiesNode.png");
      } catch (ClassNotFoundException ex) {
         ex.printStackTrace();
      }
      if (imageURL != null) {
         return new ImageIcon(imageURL, "").getImage();
      } else {
         return null;
      }
   }
      
    /**
     * Display name 
     */
    public String getHtmlDisplayName() {
        
        return "Bodies";
    }
    
    public Action[] getActions(boolean b) {
        Action[] superActions = (Action[]) super.getActions(b);        
        // Arrays are fixed size, onvert to a List
        List<Action> actions = java.util.Arrays.asList(superActions);
        // Create new Array of proper size
        Action[] retActions = new Action[actions.size()+1];
        actions.toArray(retActions);
        try {
            // append new command to the end of the list of actions
            retActions[actions.size()] = (BodyToggleCOMAction) BodyToggleCOMAction.findObject(
                     (Class)Class.forName("org.opensim.view.nodes.BodyToggleCOMAction"), true);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return retActions;
    }
    
} // class BodiesNode
