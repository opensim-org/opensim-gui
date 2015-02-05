package org.opensim.view.nodes;

import java.awt.Image;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.opensim.modeling.Body;
import org.opensim.modeling.Geometry;
import org.opensim.modeling.WrapObject;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.WrapObjectSet;
import org.opensim.view.BodyToggleFrameAction;
import org.opensim.view.nodes.OpenSimObjectNode.displayOption;

/** Node class to wrap Body objects */
public class OneBodyNode extends OpenSimObjectNode{
   private static ResourceBundle bundle = NbBundle.getBundle(OneBodyNode.class);
   public OneBodyNode(OpenSimObject b) {
      super(b);
      setShortDescription(bundle.getString("HINT_BodyNode"));
      // Create children for wrap objects associated with body
      Body bdy = (Body) b;
      Children children = getChildren();
      createGeometryNodes(bdy, children);
	  // Create nodes for wrap objects      
      WrapObjectSet wrapObjects = bdy.getWrapObjectSet();
      for (int index=0; index < wrapObjects.getSize(); index++ ){
         WrapObject wrapObject = wrapObjects.get(index);
         OneWrapObjectNode node = new OneWrapObjectNode(wrapObject);
         Node[] arrNodes = new Node[1];
         arrNodes[0] = node;
         children.add(arrNodes);
         
      }

      if(children.getNodesCount()==0) setChildren(Children.LEAF);      
      addDisplayOption(displayOption.Colorable);
      addDisplayOption(displayOption.Isolatable);
      addDisplayOption(displayOption.Showable);
   }

    protected void createGeometryNodes(Body bdy, Children children) {
        int geomSize = bdy.getGeometrySize();
        // Create node for geometry
        for (int g = 0; g < geomSize; g++) {
            Geometry oneG = bdy.get_GeometrySet(g);
            
            OneGeometryNode node = new OneGeometryNode(oneG);
            Node[] arrNodes = new Node[1];
            arrNodes[0] = node;
            children.add(arrNodes);
        }
    }

   
   //----------------------------------------------------------------
   public static int  GetNumberOfWrapObjectsForBody( OpenSimObject openSimObject )           
   { 
      WrapObjectSet wrapObjectSet = OneBodyNode.GetWrapObjectSetOrNullForBody( openSimObject ); 
      return wrapObjectSet == null ? 0 : wrapObjectSet.getSize();
   }

   
   //----------------------------------------------------------------
   public static WrapObjectSet  GetWrapObjectSetOrNullForBody( OpenSimObject openSimObject )
   {
      if( openSimObject instanceof Body )
      {
         Body body = (Body)openSimObject;
	 WrapObjectSet wrapObjectSet = body.getWrapObjectSet();
         return wrapObjectSet;
      }
      return null;
   }

   
   @Override
    public Node cloneNode() {
        return new OneBodyNode(getOpenSimObject());
    }
    /**
     * Icon for the body node 
     **/
   @Override
    public Image getIcon(int i) {
        URL imageURL = this.getClass().getResource("/org/opensim/view/nodes/icons/bodyNode.png");
        if (imageURL != null) {
            return new ImageIcon(imageURL, "Body").getImage();
        } else {
            return null;
        }
    }

   @Override
    public Image getOpenedIcon(int i) {
        return getIcon(i);
    }

    public Action[] getActions(boolean b) {
        Action[] superActions = (Action[]) super.getActions(b);        
        // Arrays are fixed size, onvert to a List
        List<Action> actions = java.util.Arrays.asList(superActions);
        // Create new Array of proper size
        Action[] retActions = new Action[actions.size()+2];
        actions.toArray(retActions);
        try {
            // append new command to the end of the list of actions
            retActions[actions.size()] = (BodyToggleFrameAction) BodyToggleFrameAction.findObject(
                     (Class)Class.forName("org.opensim.view.BodyToggleFrameAction"), true);
            retActions[actions.size()+1] = (BodyToggleCOMAction) BodyToggleCOMAction.findObject(
                     (Class)Class.forName("org.opensim.view.nodes.BodyToggleCOMAction"), true);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return retActions;
    }

}
