package org.opensim.view.nodes;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import java.util.ResourceBundle;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
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
        if (getChildren().getNodesCount()==0) setChildren(Children.LEAF);
      //addDisplayOption(displayOption.Isolatable);
      //addDisplayOption(displayOption.Showable);
    }

    /**
     * Display name 
     */
    public String getHtmlDisplayName() {
        return "Probes";
    }

} // class ProbesNode
