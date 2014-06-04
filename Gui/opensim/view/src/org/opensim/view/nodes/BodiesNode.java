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
import org.opensim.modeling.BodySet;
import org.opensim.view.BodyToggleFrameAction;
import org.opensim.view.nodes.OpenSimObjectNode.displayOption;

/**
 * Node class to wrap Model's collection of SimmBodies
 */
public class BodiesNode extends OpenSimObjectSetNode {
    boolean topological=false;
    private static ResourceBundle bundle = NbBundle.getBundle(BodiesNode.class);

    public BodiesNode(BodySet bodySet) {
        super(bodySet);
        setDisplayName(NbBundle.getMessage(BodiesNode.class, "CTL_Bodies"));
        //Stack<OneBodyNode> stack = new Stack<OneBodyNode>();

        for (int bodyNum=0; bodyNum < bodySet.getSize(); bodyNum++ ){

            Body body = bodySet.get(bodyNum);
            Children children = getChildren();

            if (topological){
                /*while (stack.size() > i.getNumAncestors())
                    stack.pop();

                if (stack.size() > 0)
                    children = stack.peek().getChildren();

                stack.push(new BodyNode(body));

                children.add(new Node[] { stack.peek() });*/
            }
            else {
                OneBodyNode node = new OneBodyNode(body);
                Node[] arrNodes = new Node[1];
                arrNodes[0] = node;
                children.add(arrNodes);
            }
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
