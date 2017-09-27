/* -------------------------------------------------------------------------- *
 * OpenSim: OtherForcesNode.java                                              *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib, Peter Loan                                         *
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
 * OtherForcesNode
 * Author(s): Peter Loan
 */
package org.opensim.view.nodes;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import java.util.ResourceBundle;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.opensim.modeling.Actuator;
import org.opensim.modeling.Force;
import org.opensim.modeling.ForceSet;
import org.opensim.modeling.Muscle;

/**
 *
 * @author Peter Loan
 *
 * Forces node (under Actuators) in Navigator view
 */
public class OtherForcesNode extends OpenSimObjectSetNode {

   private static ResourceBundle bundle = NbBundle.getBundle(OtherForcesNode.class);
   /**
    * Creates a new instance of OtherForcesNode
    */
   public OtherForcesNode(ForceSet as) {
      super(as);
      setDisplayName(NbBundle.getMessage(OtherForcesNode.class, "CTL_OtherForces"));
      Children children = getChildren();

         // Add all of the forces that are not Muscles, Actuators, or ContactForces directly under the Actuators node.
         for (int forceNum=0; forceNum < as.getSize(); forceNum++ ) {
            Force nextForce = as.get(forceNum);
            Muscle msl = Muscle.safeDownCast(nextForce);
            if (msl != null)
               continue;
            if (nextForce.getConcreteClassName().equalsIgnoreCase("ElasticFoundationForce") ||
              nextForce.getConcreteClassName().equalsIgnoreCase("HuntCrossleyForce"))
               continue;
            Actuator act = Actuator.safeDownCast(nextForce);
            if (act != null)
               continue;
            OneForceNode node = new OneForceNode(nextForce);
            Node[] arrNodes = new Node[1];
            arrNodes[0] = node;
            children.add(arrNodes);
         }

      if (getChildren().getNodesCount() == 0)
         setChildren(children.LEAF);
   }

    public Image getIcon(int i) {
      URL imageURL=null;
      try {
         imageURL = Class.forName("org.opensim.view.nodes.OpenSimNode").getResource("/org/opensim/view/nodes/icons/forceNode.png");
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
         imageURL = Class.forName("org.opensim.view.nodes.OpenSimNode").getResource("/org/opensim/view/nodes/icons/forceNode.png");
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
       return NbBundle.getMessage(OtherForcesNode.class, "CTL_OtherForces");
   }
}
