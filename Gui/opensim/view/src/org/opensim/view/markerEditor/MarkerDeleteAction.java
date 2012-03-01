package org.opensim.view.markerEditor;

import java.awt.event.ActionEvent;
import java.util.Vector;
import javax.swing.AbstractAction;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.opensim.modeling.Marker;
import org.opensim.modeling.MarkerSet;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimObject;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.ObjectsDeletedEvent;
import org.opensim.view.nodes.OneMarkerNode;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;

/**
 * Action which shows MarkerEditor component.
 */
public class MarkerDeleteAction extends AbstractAction {
   
   public MarkerDeleteAction() {
      super(NbBundle.getMessage(MarkerDeleteAction.class, "CTL_MarkerDeleteAction"));
   }
   
    public void actionPerformed(ActionEvent evt) {

        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        // If any selected object is hidden (or any selected group is mixed), return false.
        for (int i = 0; i < selected.length; i++) {
            OneMarkerNode objectNode = (OneMarkerNode) selected[i];
            Marker marker = Marker.safeDownCast(objectNode.getOpenSimObject());
            // Delete the marker's visuals.
            marker.removeSelfFromDisplay();

            // Remove the marker from the model's marker set.
            Model model = marker.getBody().getModel();
            MarkerSet markerset = model.getMarkerSet();
            markerset.remove(marker);

            // Update the marker name list in the ViewDB.
            ViewDB.getInstance().getModelGuiElements(model).updateMarkerNames();

            if (true) {
                // Generate an event so everyone can update, including the marker editor.
                Vector<OpenSimObject> objs = new Vector<OpenSimObject>(1);
                objs.add(marker);
                ObjectsDeletedEvent evnt = new ObjectsDeletedEvent(this, model, objs);
                OpenSimDB.getInstance().setChanged();
                OpenSimDB.getInstance().notifyObservers(evnt);
            }
        }

    }
}
