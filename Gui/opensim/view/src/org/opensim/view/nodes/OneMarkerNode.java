package org.opensim.view.nodes;

import java.awt.Image;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.opensim.modeling.Marker;
import org.opensim.modeling.OpenSimObject;
import org.opensim.view.editors.BodyNameEditor;
import org.opensim.view.markerEditor.MarkerDeleteAction;
import org.opensim.view.markerEditor.MarkerEditorAction;
import org.opensim.view.nodes.OpenSimObjectNode.displayOption;

/** Node class to wrap AbstractMarker objects */
public class OneMarkerNode extends OpenSimObjectNode{
   private static ResourceBundle bundle = NbBundle.getBundle(OneMarkerNode.class);
   public OneMarkerNode(OpenSimObject b) {
      super(b);
      setShortDescription(bundle.getString("HINT_MarkerNode"));
      setChildren(Children.LEAF);      
      addDisplayOption(displayOption.Isolatable);
      addDisplayOption(displayOption.Showable);
   }

    public Node cloneNode() {
        return new OneMarkerNode(getOpenSimObject());
    }
    /**
     * Icon for the marker node 
     **/
    public Image getIcon(int i) {
        URL imageURL = this.getClass().getResource("/org/opensim/view/nodes/icons/markerNode.png");
        if (imageURL != null) {
            return new ImageIcon(imageURL, "Marker").getImage();
        } else {
            return null;
        }
    }

    public Image getOpenedIcon(int i) {
        return getIcon(i);
    }

    public Action[] getActions(boolean b) {
        // Get actions from parent (generic object menu for review, display)
        Action[] superActions = (Action[]) super.getActions(b);
        // Arrays are fixed size, onvert to a List
        List<Action> actions = java.util.Arrays.asList(superActions);
        // Create new Array of proper size
        Action[] retActions = new Action[actions.size()+2];
        actions.toArray(retActions);
        // append new command to the end of the list of actions
        retActions[actions.size()] = new MarkerEditorAction();
        retActions[actions.size()+1] = new MarkerDeleteAction();
        return retActions;
    }

    @Override
    public Sheet createSheet() {
        Sheet sheet;

        sheet = super.createSheet();
        Sheet.Set set = sheet.get("properties");
        // Add property for Location
        Marker obj = Marker.safeDownCast(getOpenSimObject());
        MarkerAdapter gMarker = new MarkerAdapter(obj);
        org.opensim.modeling.PropertySet ps = obj.getPropertySet();
        org.opensim.modeling.Property prop;
        try {
            set.remove("body");
            PropertySupport.Reflection nextNodeProp2;
            nextNodeProp2 = new PropertySupport.Reflection(gMarker, String.class, "getBodyName", "setBodyName");
            nextNodeProp2.setPropertyEditorClass(BodyNameEditor.class);
            nextNodeProp2.setName("body");
            set.put(nextNodeProp2);
            // USe custom name change
            set.remove("name");
            PropertySupport.Reflection nameNodeProp;
            nameNodeProp = new PropertySupport.Reflection(gMarker, String.class, "getName", "setName");
            ((Node.Property) nameNodeProp).setValue("oneline", Boolean.TRUE);
            ((Node.Property) nameNodeProp).setValue("suppressCustomEditor", Boolean.TRUE);
            nameNodeProp.setName("name");
            set.put(nameNodeProp);
            // customize offset
            set.remove("location");
            PropertySupport.Reflection locationNodeProp;
            locationNodeProp = new PropertySupport.Reflection(gMarker, String.class, "getOffsetString", "setOffsetString");
            ((Node.Property) locationNodeProp).setValue("oneline", Boolean.TRUE);
            ((Node.Property) locationNodeProp).setValue("suppressCustomEditor", Boolean.TRUE);
            locationNodeProp.setName("location");
            locationNodeProp.setShortDescription(gMarker.getPropertyComment("location"));
            set.put(locationNodeProp);
            
            Node.Property fixedProp = set.get("fixed");
            set.remove("fixed");
            Sheet.Set expertSet = sheet.get("expert");
            expertSet.put(fixedProp);
   
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }

        return sheet;
    }
}
