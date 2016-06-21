package org.opensim.view.markerEditor;

import java.awt.event.ActionEvent;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.opensim.modeling.Marker;
import org.opensim.modeling.MarkerSet;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.Vec3;
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
            deleteMarker(marker, true);
        }

    }

    public void deleteMarker(final Marker marker, boolean supportUndo) {
        // Delete the marker's visuals.
        final String saveMarkerName = marker.getName();
        final String saveBodyName = marker.getFrameName();
        final Vec3 saveMarkerOffset = marker.get_location();
        
        //marker.removeSelfFromDisplay();

        // Remove the marker from the model's marker set.
        final Model model = marker.getModel();
        MarkerSet markerset = model.getMarkerSet();
        markerset.remove(marker);

        // Update the marker name list in the ViewDB.
        OpenSimDB.getInstance().getModelGuiElements(model).updateMarkerNames();

        // Generate an event so everyone can update, including the marker editor.
        Vector<OpenSimObject> objs = new Vector<OpenSimObject>(1);
        objs.add(marker);
        ObjectsDeletedEvent evnt = new ObjectsDeletedEvent(this, model, objs);
        OpenSimDB.getInstance().setChanged();
        OpenSimDB.getInstance().notifyObservers(evnt);
        if (supportUndo) {
            System.out.println("name, body, offset="+saveMarkerName+" "+saveBodyName+" "+saveMarkerOffset);
            AbstractUndoableEdit auEdit = new AbstractUndoableEdit() {

                public void undo() throws CannotUndoException {
                    super.undo();
                    Marker newMarker = model.getMarkerSet().addMarker(saveMarkerName, saveMarkerOffset, model.getBodySet().get(saveBodyName));
                    new NewMarkerAction().addMarker(newMarker, false);
                }

                public void redo() throws CannotRedoException {
                    super.redo();
                    Marker toDelete = model.getMarkerSet().get(saveMarkerName);
                    deleteMarker(toDelete, true);
                }
            };
            ExplorerTopComponent.addUndoableEdit(auEdit);
        }
    }
}
