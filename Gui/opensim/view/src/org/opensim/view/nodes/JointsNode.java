/* -------------------------------------------------------------------------- *
 * OpenSim: JointsNode.java                                                   *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib, Jeff Reinbolt                                      *
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
/*
 *
 * JointsNode
 * Author(s): Ayman Habib & Jeff Reinbolt
 */
package org.opensim.view.nodes;

import java.awt.Image;
import java.net.URL;
import javax.swing.Action;
import javax.swing.ImageIcon;
import java.util.ResourceBundle;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.nodes.Children;
import org.opensim.modeling.Joint;
import org.opensim.modeling.JointSet;

/**
 *
 * @author Ayman Habib & Jeff Reinbolt
 *
 * Top level Joints node in Navigator view
 */
public class JointsNode extends OpenSimObjectSetNode {
    
    private static ResourceBundle bundle = NbBundle.getBundle(JointsNode.class);
    
   /**
    * Creates a new instance of JointsNode
    */
   public JointsNode(JointSet js) {
      super(js);
      setDisplayName(NbBundle.getMessage(JointsNode.class, "CTL_Joints"));
      Children children = getChildren();
      for (int i=0; i < js.getSize(); i++ ) {
         children.add(new Node[] { new OneJointNode(js.get(i)) });
      }
      if (getChildren().getNodesCount()==0) setChildren(Children.LEAF);
   }
   
      public Image getIcon(int i) {
      URL imageURL=null;
      try {
         imageURL = Class.forName("org.opensim.view.nodes.OpenSimNode").getResource("/org/opensim/view/nodes/icons/jointsNode.png");
      } catch (ClassNotFoundException ex) {
         ex.printStackTrace();
      }
      if (imageURL != null) {
         return new ImageIcon(imageURL, "").getImage();
      } else {
         return null;
      }
   }
   
   public Image getOpenedIcon(int i) {
      URL imageURL=null;
      try {
         imageURL = Class.forName("org.opensim.view.nodes.OpenSimNode").getResource("/org/opensim/view/nodes/icons/jointsNode.png");
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
       return NbBundle.getMessage(JointsNode.class, "CTL_Joints"); }

}
