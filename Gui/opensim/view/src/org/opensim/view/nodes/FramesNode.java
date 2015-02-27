package org.opensim.view.nodes;

import java.awt.Image;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.opensim.modeling.BodiesList;
import org.opensim.modeling.Body;
import org.opensim.modeling.BodyIterator;
import org.opensim.modeling.Frame;
import org.opensim.modeling.FrameIterator;
import org.opensim.modeling.FramesList;
import org.opensim.modeling.Model;
import org.opensim.view.nodes.OpenSimObjectNode.displayOption;

/**
 * Node class to wrap Model's collection of SimmBodies
 */
public class FramesNode extends OpenSimNode {

    private static ResourceBundle bundle = NbBundle.getBundle(FramesNode.class);

    public FramesNode(Model model) {
        super();
        setDisplayName(NbBundle.getMessage(FramesNode.class, "CTL_Bodies"));
        //Stack<OneBodyNode> stack = new Stack<OneBodyNode>();
        Children children = getChildren();
        FramesList frames = model.getFramesList();
        FrameIterator frm = frames.begin();
        while (!frm.equals(frames.end())) {
            Frame deref = frm.__deref__();
            if (Body.safeDownCast(deref)==null){ // Exclude bodies
                OneFrameNode node = new OneFrameNode(deref);
                Node[] arrNodes = new Node[1];
                arrNodes[0] = node;
                children.add(arrNodes);
            }
            frm.next();
        }
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
        
        return "Frames";
    }
    
    public Action[] getActions(boolean b) {
        Action[] superActions = (Action[]) super.getActions(b);        
        // Arrays are fixed size, onvert to a List
        /*
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
        }*/
        return superActions;
    }
    
} // class BodiesNode
