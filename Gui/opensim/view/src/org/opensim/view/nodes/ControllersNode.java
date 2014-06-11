package org.opensim.view.nodes;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import java.util.ResourceBundle;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.opensim.modeling.Controller;
import org.opensim.modeling.ControllerSet;

/**
 * Node class to wrap Model's collection of Controllers
 */
public class ControllersNode extends OpenSimObjectSetNode {
    private static ResourceBundle bundle = NbBundle.getBundle(ControllersNode.class);

    public ControllersNode(ControllerSet ControllerSet) {
        super(ControllerSet);
        setDisplayName(NbBundle.getMessage(ControllersNode.class, "CTL_Controllers"));

        for (int index=0; index < ControllerSet.getSize(); index++ ){

            Controller Controller = ControllerSet.get(index);
            Children children = getChildren();

            OneControllerNode node = new OneControllerNode(Controller);
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
        return "Controllers";
    }

} // class ControllersNode
