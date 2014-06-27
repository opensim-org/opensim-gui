package org.opensim.view.nodes;

import java.util.ResourceBundle;
import org.openide.nodes.Children;
import org.openide.util.NbBundle;
import org.opensim.modeling.OpenSimObject;

/** Node class to wrap generic ModelComponent objects */
public class OneComponentNode extends OpenSimObjectNode{
   private static ResourceBundle bundle = NbBundle.getBundle(OneComponentNode.class);
   
   public OneComponentNode(OpenSimObject b) {
      super(b);
      setShortDescription(bundle.getString("HINT_ComponentNode"));
      setChildren(Children.LEAF);      
   }
 
}
