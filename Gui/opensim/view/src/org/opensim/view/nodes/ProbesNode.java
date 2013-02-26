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
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.Probe;
import org.opensim.modeling.ProbeSet;

/**
 * Node class to wrap Model's collection of probes
 */
public class ProbesNode extends OpenSimObjectSetNode {
    private static ResourceBundle bundle = NbBundle.getBundle(ProbesNode.class);

    public ProbesNode(ProbeSet probeSet) {
        super(probeSet);
        setDisplayName(NbBundle.getMessage(ProbesNode.class, "CTL_Probes"));

        for (int index=0; index < probeSet.getSize(); index++ ){

            Probe probe = probeSet.get(index);
            Children children = getChildren();

            OneProbeNode node = new OneProbeNode(probe);
            Node[] arrNodes = new Node[1];
            arrNodes[0] = node;
            children.add(arrNodes);
        }
       //if (getChildren().getNodesCount()==0) setChildren(Children.LEAF);
      //addDisplayOption(displayOption.Isolatable);
      //addDisplayOption(displayOption.Showable);
    }

    /**
     * Display name 
     */
    public String getHtmlDisplayName() {
        return "Probes";
    }
      public Image getIcon(int i) {
      URL imageURL=null;
      try {
         imageURL = Class.forName("org.opensim.view.nodes.OpenSimNode").getResource("icons/probe_multiple.png");
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
         imageURL = Class.forName("org.opensim.view.nodes.OpenSimNode").getResource("/org/opensim/view/nodes/icons/probe_multiple.png");
      } catch (ClassNotFoundException ex) {
         ex.printStackTrace();
      }
      if (imageURL != null) {
         return new ImageIcon(imageURL, "").getImage();
      } else {
         return null;
      }
   }
   
    public Action[] getActions(boolean b) {
        // Get actions from parent (generic object menu for review, display)
        Action[] superActions = (Action[]) super.getActions(b);        
        // Arrays are fixed size, onvert to a List
        List<Action> actions = java.util.Arrays.asList(superActions);
        // Create new Array of proper size
        Action[] retActions = new Action[actions.size()+1];
        actions.toArray(retActions);
        // append new command to the end of the list of actions
        retActions[actions.size()] = new NewProbeAction();
        return retActions;
    }

    String makeUniqueName(String baseName) {
        OpenSimObject probeSetObj = this.getOpenSimObject();
        ProbeSet probeSet = ProbeSet.safeDownCast(probeSetObj);
        boolean validName = false;
        String newName = "";
        for (int i = 0; !(validName); i++) {
            newName = baseName + "_" + i;
            validName = !(probeSet.contains(newName)); // name is being used by a current marker
        }
         return newName;
    }
    
} // class ProbesNode
