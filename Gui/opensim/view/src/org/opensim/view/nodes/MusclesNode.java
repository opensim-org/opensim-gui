/*
 *
 * MusclesNode
 * Author(s): Peter Loan & Jeff Reinbolt
 * Copyright (c)  2007, Stanford University, Peter Loan & Jeff Reinbolt
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
package org.opensim.view.nodes;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import java.util.ResourceBundle;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.opensim.modeling.Muscle;
import org.opensim.modeling.ForceSet;
import org.opensim.modeling.ArrayConstObjPtr;
import org.opensim.modeling.ObjectGroup;
import org.opensim.modeling.SetActuators;
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
