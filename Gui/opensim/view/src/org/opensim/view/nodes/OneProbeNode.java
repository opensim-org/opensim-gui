package org.opensim.view.nodes;

import java.util.ResourceBundle;
import org.openide.nodes.Children;
import org.openide.util.NbBundle;
import org.opensim.modeling.OpenSimObject;


/** Node class to wrap Probe objects */
public class OneProbeNode extends OpenSimObjectNode{
   private static ResourceBundle bundle = NbBundle.getBundle(OneProbeNode.class);
   
   public OneProbeNode(OpenSimObject b) {
      super(b);
      setShortDescription(bundle.getString("HINT_ProbeNode"));
      setChildren(Children.LEAF);      
   }

}
