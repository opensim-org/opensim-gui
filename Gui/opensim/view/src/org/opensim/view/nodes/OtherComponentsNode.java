package org.opensim.view.nodes;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import java.util.ResourceBundle;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.opensim.modeling.ComponentSet;
import org.opensim.modeling.ModelComponent;


/**
 * Node class to wrap Model's collection of misc. components
 */
public class OtherComponentsNode extends OpenSimObjectSetNode {
    private static ResourceBundle bundle = NbBundle.getBundle(OtherComponentsNode.class);

    public OtherComponentsNode(ComponentSet componentSet) {
        super(componentSet);
        setDisplayName(NbBundle.getMessage(OtherComponentsNode.class, "CTL_ModelComponents"));

        for (int index=0; index < componentSet.getSize(); index++ ){

            ModelComponent component = componentSet.get(index);
            Children children = getChildren();

            OneOtherModelComponentNode node = new OneOtherModelComponentNode(component);
            Node[] arrNodes = new Node[1];
            arrNodes[0] = node;
            children.add(arrNodes);
        }
        if (getChildren().getNodesCount()==0) setChildren(Children.LEAF);
      //addDisplayOption(displayOption.Isolatable);
      //addDisplayOption(displayOption.Showable);
    }
   
    /**
     * Display name 
     */
    public String getHtmlDisplayName() {
        return "Other Components";
    }

} // class MarkersNode
