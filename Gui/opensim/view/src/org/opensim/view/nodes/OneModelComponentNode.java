package org.opensim.view.nodes;

import java.util.ResourceBundle;
import org.openide.nodes.Children;
import org.openide.util.NbBundle;
import org.opensim.modeling.OpenSimObject;

/** Node class to wrap generic ModelComponent objects */
public class OneModelComponentNode extends OpenSimObjectNode{
   private static ResourceBundle bundle = NbBundle.getBundle(OneModelComponentNode.class);
   
   public OneModelComponentNode(OpenSimObject b) {
      super(b);
      setShortDescription(bundle.getString("CTL_ModelComponents"));
      setChildren(Children.LEAF);      
   }
 
}
