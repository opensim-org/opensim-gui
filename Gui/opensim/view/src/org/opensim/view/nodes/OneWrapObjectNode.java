package org.opensim.view.nodes;

import java.awt.Image;
import java.net.URL;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.opensim.modeling.Body;
import org.opensim.modeling.WrapObject;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.WrapObjectSet;

/** Node class to wrap WrapObject objects */
public class OneWrapObjectNode extends OpenSimObjectNode{
   private static ResourceBundle bundle = NbBundle.getBundle(OneBodyNode.class);
   public OneWrapObjectNode(OpenSimObject b) {
      super(b);
      setChildren(Children.LEAF);
      setShortDescription(bundle.getString("HINT_WrapObjectNode"));
      addDisplayOption(displayOption.Showable);
      addDisplayOption(displayOption.Isolatable);
      addDisplayOption(displayOption.Colorable);
      
   }

    public Node cloneNode() {
        return new OneWrapObjectNode(getOpenSimObject());
    }
    /**
     * Icon for the body node 
     **/
    public Image getIcon(int i) {
        URL imageURL = this.getClass().getResource("/org/opensim/view/nodes/icons/wrapNode.png");
        if (imageURL != null) {
            return new ImageIcon(imageURL, "Wrap Object").getImage();
        } else {
            return null;
        }
    }

}
