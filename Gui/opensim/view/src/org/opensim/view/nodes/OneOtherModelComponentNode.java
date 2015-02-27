package org.opensim.view.nodes;

import java.util.ResourceBundle;
import org.openide.nodes.Children;
import org.openide.util.NbBundle;
import org.opensim.modeling.OpenSimObject;

/** Node class to wrap generic ModelComponent objects */
public class OneOtherModelComponentNode extends OneComponentNode{
   private static ResourceBundle bundle = NbBundle.getBundle(OneOtherModelComponentNode.class);
   
   public OneOtherModelComponentNode(OpenSimObject b) {
      super(b);
      setShortDescription(bundle.getString("CTL_ModelComponents"));
      setChildren(Children.LEAF);      
   }
 
}
