/* -------------------------------------------------------------------------- *
 * OpenSim: MotionsNode.java                                                  *
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
/*
 *
 * JointsNode
 * Author(s): Ayman Habib
 */
package org.opensim.view.motions;

import java.awt.Image;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.opensim.view.nodes.*;

/**
 *
 * @author Ayman Habib
 *
 * Top level motions node in Navigator view. One per model
 */
public class MotionsNode extends OpenSimNode implements Observer{
   
   private static ResourceBundle bundle = NbBundle.getBundle(MotionsNode.class);
   
   /** Creates a new instance of MotionsNode */
   public MotionsNode() {
      setDisplayName("Motions");
      setName("Motions"); // To be used by findNode();
   }
   
   /**
    * Icon for the node, same as OpenSimNode
    **/
   public Image getIcon(int i) {
      URL imageURL=null;
      try {
         imageURL = Class.forName("org.opensim.view.nodes.OpenSimNode").getResource("/org/opensim/view/nodes/icons/motionsNode.png");
      } catch (ClassNotFoundException ex) {
         ex.printStackTrace();
      }
      if (imageURL != null) {
         return new ImageIcon(imageURL, "").getImage();
      } else {
         return null;
      }
   }
   
   public void update(Observable o, Object arg) { // Should add child node for associated node
      if (o instanceof MotionsDB && arg instanceof MotionEvent) {
         // No matter what set the names of the naodes so that the explorer view is updated 
         // with what's current in bold'
         Node[] nodes = this.getChildren().getNodes();
         for(int i=0; i< nodes.length; i++){
            nodes[i].setName(nodes[i].getName());
         }
      }
   }

}
