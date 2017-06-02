/*
 *
 * ActuatorGroupNode
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
import org.opensim.modeling.ArrayConstObjPtr;
import org.opensim.modeling.Muscle;
import org.opensim.modeling.ObjectGroup;

/**
 *
 * @author Peter Loan & Jeff Reinbolt
 *
 * Top level Actuators node in Navigator view
 */
public class ActuatorGroupNode extends OpenSimObjectNode {
   
   private static ResourceBundle bundle = NbBundle.getBundle(ActuatorGroupNode.class);
   /**
    * Creates a new instance of ActuatorGroupNode
    */
   public ActuatorGroupNode(ObjectGroup group) {
      super(group);
      setDisplayName(group.getName());
      setShortDescription(bundle.getString("HINT_ActuatorGroupNode"));
      Children children = getChildren();
      ArrayConstObjPtr members = group.getMembers();
      for (int i = 0; i < members.getSize(); i++ ) {
         Node[] arrNodes = new Node[1];
         Muscle msl = Muscle.safeDownCast(members.getitem(i));
         if (msl != null) {
            OneMuscleNode node = new OneMuscleNode(msl);
            arrNodes[0] = node;
         } else {
            OneActuatorNode node = new OneActuatorNode(members.getitem(i));
            arrNodes[0] = node;
         }
         children.add(arrNodes);
      }
      if (getChildren().getNodesCount()==0) setChildren(Children.LEAF);
      addDisplayOption(displayOption.Showable);
      addDisplayOption(displayOption.Isolatable);
      addDisplayOption(displayOption.Colorable);
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
   
   public Image getOpenedIcon(int i) {
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
      return getOpenSimObject().getName() ;
   }
}
