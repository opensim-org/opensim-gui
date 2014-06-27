package org.opensim.view.nodes;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import java.util.ResourceBundle;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.opensim.modeling.Constraint;
import org.opensim.modeling.ConstraintSet;

/**
 * Node class to wrap Model's collection of constraints
 */
public class ConstraintsNode extends OpenSimObjectSetNode {
    private static ResourceBundle bundle = NbBundle.getBundle(ConstraintsNode.class);

    public ConstraintsNode(ConstraintSet constraintSet) {
        super(constraintSet);
        setDisplayName(NbBundle.getMessage(ConstraintsNode.class, "CTL_Constraints"));

        for (int index=0; index < constraintSet.getSize(); index++ ){

            Constraint constraint = constraintSet.get(index);
            Children children = getChildren();

            OneConstraintNode node = new OneConstraintNode(constraint);
            Node[] arrNodes = new Node[1];
            arrNodes[0] = node;
            children.add(arrNodes);
        }
        if (getChildren().getNodesCount()==0) setChildren(Children.LEAF);
      //addDisplayOption(displayOption.Isolatable);
      //addDisplayOption(displayOption.Showable);
    }

      public Image getIcon(int i) {
      URL imageURL=null;
      try {
         imageURL = Class.forName("org.opensim.view.nodes.OpenSimNode").getResource("icons/constraintsNode.png");
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
         imageURL = Class.forName("org.opensim.view.nodes.OpenSimNode").getResource("/org/opensim/view/nodes/icons/constraintsNode.png");
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
        return "Constraints";
    }

} // class MarkersNode
