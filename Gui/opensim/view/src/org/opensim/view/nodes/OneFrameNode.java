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

/**
 *
 * @author Ayman
 */
class OneFrameNode extends OneModelComponentNode {
   private static ResourceBundle bundle = NbBundle.getBundle(OneFrameNode.class);
    public OneFrameNode(Frame frame) {
        super(frame);
        setShortDescription(bundle.getString("HINT_FrameNode"));
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
