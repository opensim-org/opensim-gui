/* -------------------------------------------------------------------------- *
 * OpenSim: MusclesNode.java                                                  *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib, Peter Loan, Jeff Reinbolt                          *
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
 * MusclesNode
 * Author(s): Peter Loan & Jeff Reinbolt
 */
package org.opensim.view.nodes;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import java.util.ResourceBundle;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.opensim.modeling.ArrayConstObjPtr;
import org.opensim.modeling.Muscle;
import org.opensim.modeling.ForceSet;
import org.opensim.modeling.ArrayObjPtr;
import org.opensim.modeling.ObjectGroup;
import org.opensim.view.nodes.OpenSimObjectNode.displayOption;

/**
 *
 * @author Peter Loan & Jeff Reinbolt
 *
 * Muscles node (under Actuators) in Navigator view
 */
public class MusclesNode extends OpenSimObjectSetNode {
   
   private static ResourceBundle bundle = NbBundle.getBundle(MusclesNode.class);
   /**
    * Creates a new instance of MusclesNode
    */
   public MusclesNode(ForceSet as) {
      super(as);
      setDisplayName(NbBundle.getMessage(MusclesNode.class, "CTL_Muscles"));
      Children children = getChildren();
      int numMuscleGroups = countMuscleGroups(as);
      int numMuscles = countMuscles(as);
      if (numMuscleGroups == 0) {
         // There are no Muscle groups, so just add all of the
         // muscles directly under the Muscles node.
         for (int actuatorNum=0; actuatorNum < as.getSize(); actuatorNum++ ) {
            Muscle muscle = Muscle.safeDownCast(as.get(actuatorNum));
            if (muscle != null) {
               OneMuscleNode node = new OneMuscleNode(muscle);
               Node[] arrNodes = new Node[1];
               arrNodes[0] = node;
               children.add(arrNodes);
            }
         }
      } else {
         /* If you're going to add an "all" group, make it the first group.
          * So first see if it's necessary, then add it. Then add the other
          * groups.
          */
         boolean userDefinedAllGroup = false;
         // See if "all" group is already defined.
         for (int i = 0; i < as.getNumGroups(); i++) {
            ObjectGroup grp = as.getGroup(i);
            ArrayConstObjPtr apo = grp.getMembers();
            if (apo.getSize() > 0) {
               Muscle muscle = Muscle.safeDownCast(apo.getitem(0));
               // If the first member of the group is an Muscle, then
               // consider this group to be an Muscle group.
               if (muscle != null && grp.getName().equals("all")) {
                  userDefinedAllGroup = true;
                  break;
               }
            }
         }
         // Now make the "all" group, if necessary.
         if (userDefinedAllGroup == false) {
            ObjectGroup allGroup = new ObjectGroup();
            allGroup.setName("all");
            for (int actuatorNum=0; actuatorNum < as.getSize(); actuatorNum++ ) {
               Muscle muscle = Muscle.safeDownCast(as.get(actuatorNum));
               if (muscle != null) {
                  allGroup.add(muscle);
               }
            }
            children.add(new Node[] {new ActuatorGroupNode(allGroup)});
         }
         // Now add the user-defined groups.
         for (int i = 0; i < as.getNumGroups(); i++) {
            ObjectGroup grp = as.getGroup(i);
            ArrayConstObjPtr apo = grp.getMembers();
            if (apo.getSize() > 0) {
               Muscle muscle = Muscle.safeDownCast(apo.getitem(0));
               // If the first member of the group is an Muscle, then
               // consider this group to be an Muscle group.
               if (muscle != null)
                  children.add(new Node[] {new ActuatorGroupNode(grp)});
            }
         }
      }
      if (getChildren().getNodesCount() == 0)
         setChildren(children.LEAF);
      addDisplayOption(displayOption.Showable);
   }
   
   public Image getIcon(int i) {
      URL imageURL=null;
      try {
         imageURL = Class.forName("org.opensim.view.nodes.OpenSimNode").getResource("/org/opensim/view/nodes/icons/musclesNode.png");
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
       return NbBundle.getMessage(MusclesNode.class, "CTL_Muscles");
   }

   private int countMuscleGroups(ForceSet as) {
      int count = 0;
      for (int i = 0; i < as.getNumGroups(); i++) {
         ObjectGroup grp = as.getGroup(i);
         ArrayConstObjPtr apo = grp.getMembers();
         if (apo.getSize()==0) continue;  // Gaurd against empty groups
         Muscle muscle = Muscle.safeDownCast(apo.getitem(0)); 
         // If the first member of the group is an Muscle, then
         // consider this group to be an Muscle group.
         if (muscle != null)
            count++;
      }
      return count;
   }

   private int countMuscles(ForceSet as) {
      int count = 0;
      for (int i = 0; i < as.getSize(); i++) {
         Muscle muscle = Muscle.safeDownCast(as.get(i));
         if (muscle != null)
            count++;
      }
      return count;
   }
}
