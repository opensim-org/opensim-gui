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
import org.opensim.modeling.AbstractProperty;
import org.opensim.modeling.Constraint;
import org.opensim.modeling.OpenSimContext;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.PropertyHelper;
import org.opensim.view.ObjectDisplayMenuAction;
import org.opensim.view.pub.OpenSimDB;

/** Node class to wrap AbstractMarker objects */
public class OneConstraintNode extends OpenSimObjectNode  implements DisableableObject {
   private static ResourceBundle bundle = NbBundle.getBundle(OneConstraintNode.class);
   private boolean disabled=false;
   
   public OneConstraintNode(OpenSimObject b) {
      super(b);
      setShortDescription(bundle.getString("HINT_ConstraintNode"));
      setChildren(Children.LEAF);      
      updateDisabledFlag();
   }

    public Node cloneNode() {
        return new OneConstraintNode(getOpenSimObject());
    }
    /**
     * Icon for the constraint node 
     **/
    public Image getIcon(int i) {
        URL imageURL;
        if (disabled)
            imageURL = this.getClass().getResource("icons/disabledNode.png");
        else
            imageURL = this.getClass().getResource("icons/constraintNode.png");
        if (imageURL != null) { 
            return new ImageIcon(imageURL, "Constraint").getImage();
        } else {
            return null;
        }
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        //OpenSimDB.getInstance().disableConstraint(getOpenSimObject(), disabled);
        this.disabled = disabled;
        if (disabled)
            setIconBaseWithExtension("/org/opensim/view/nodes/icons/disabledNode.png");
        else
            setIconBaseWithExtension("/org/opensim/view/nodes/icons/constraintNode.png");
        //refreshNode();
    }

    private void updateDisabledFlag() {
        Constraint c = Constraint.safeDownCast(getOpenSimObject());
        AbstractProperty ap = c.getPropertyByName("isDisabled");
        disabled = PropertyHelper.getValueBool(ap);
    }

    public Action[] getActions(boolean b) {
        Action[] superActions = (Action[]) super.getActions(b);        
        // Arrays are fixed size, onvert to a List
        List<Action> actions = java.util.Arrays.asList(superActions);
        // Create new Array of proper size
        Action[] retActions = new Action[actions.size()+1];
        actions.toArray(retActions);
        if (disabled){  // take out display menu ObjectDisplayMenuAction
            for (int i=0; i< retActions.length; i++){
                if (retActions[i] instanceof ObjectDisplayMenuAction){
                    retActions[i] = null; 
                    break;
                }
            }
        }
        try {
            ToggleEnabledStateAction act =(ToggleEnabledStateAction) ToggleEnabledStateAction.findObject(
                    (Class)Class.forName("org.opensim.view.nodes.ToggleEnabledStateAction"), true);
            retActions[actions.size()]=act;
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return retActions;
    }

    @Override
    public void refreshNode() {
        super.refreshNode();
        updateDisabledFlag();
        setDisabled(disabled);
    }
}
