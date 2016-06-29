/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.opensim.view.nodes;

import java.awt.Image;
import java.net.URL;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.opensim.modeling.Frame;
import org.opensim.modeling.Geometry;

/**
 *
 * @author Ayman
 */
class OneFrameNode extends OneModelComponentNode {
   private static ResourceBundle bundle = NbBundle.getBundle(OneFrameNode.class);
   Frame frame;
    public OneFrameNode(Frame comp) {
        super(comp);
        frame = comp;
        setShortDescription(bundle.getString("HINT_FrameNode"));
        createGeometryNodes();
    }
   
    protected final void createGeometryNodes() {
        
        int geomSize = frame.getPropertyByName("attached_geometry").size();
        // Create node for geometry
        Children children = getChildren();
        for (int g = 0; g < geomSize; g++) {
            Geometry oneG = frame.get_attached_geometry(g);
            OneGeometryNode node = new OneGeometryNode(oneG);
            Node[] arrNodes = new Node[1];
            arrNodes[0] = node;
            children.add(arrNodes);
        }
    }
    
   @Override
    public Node cloneNode() {
        return new OneFrameNode((Frame)getOpenSimObject());
    }
    /**
     * Icon for the body node 
     **/
   @Override
    public Image getIcon(int i) {
        URL imageURL = this.getClass().getResource("/org/opensim/view/nodes/icons/body.png");
        if (imageURL != null) {
            return new ImageIcon(imageURL, "Frame").getImage();
        } else {
            return null;
        }
    }

   @Override
    public Image getOpenedIcon(int i) {
        return getIcon(i);
    }
    
}
