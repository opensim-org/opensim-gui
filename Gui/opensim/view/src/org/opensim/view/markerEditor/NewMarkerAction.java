/*
 * NewMarkerAction.java
 *
 * Created on June 9, 2008, 3:42 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.opensim.view.markerEditor;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.opensim.modeling.Body;
import org.opensim.modeling.Component;
import org.opensim.modeling.Marker;
import org.opensim.modeling.MarkerSet;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimContext;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.PhysicalFrame;
import org.opensim.modeling.Vec3;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.ObjectsAddedEvent;
import org.opensim.view.nodes.MarkersNode;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;

/**
 *
 * @author Ayman Habib
 */
public class NewMarkerAction extends AbstractAction {

   /** Creates a new instance of NewMarkerAction */
   public NewMarkerAction() {
      super(NbBundle.getMessage(NewMarkerAction.class, "CTL_NewMarkerAction"));
   }

   public void actionPerformed(ActionEvent evt) {
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        // One MarkersNode must have been selected otherwise bail
        if (selected.length != 1 || !(selected[0] instanceof MarkersNode)) {
            return;
   }
        MarkersNode markersNode = (MarkersNode) selected[0];
        Model model = markersNode.getModelForNode();
        Vec3 offset = new Vec3(0.11, 0.22, 0.33);
        MarkerSet markerset = model.getMarkerSet();
        Body body = model.getBodySet().get(0);
        if (body == null) {
            return;
        }
        String newMarkerName = makeUniqueMarkerName(markerset);
        Marker newMarker = new Marker();
        newMarker.setName(newMarkerName);
        newMarker.set_location(offset);
        newMarker.setParentFrame(body);
        OpenSimContext context = OpenSimDB.getInstance().getContext(model);
        context.cacheModelAndState();
        markerset.adoptAndAppend(newMarker);
        try {
           context.restoreStateFromCachedModel();
       } catch (IOException ex) {
           Exceptions.printStackTrace(ex);
       }
        addMarker(newMarker, true);
    }

    public void addMarker(final Marker marker, boolean supportUndo) {
        // Update the marker name list in the ViewDB.
        final String saveMarkerName = marker.getName();
        final String saveBodyName = marker.getFrameName();
        final Vec3 saveMarkerOffset = marker.get_location();
        final Model model = marker.getModel();
        OpenSimDB.getInstance().getModelGuiElements(model).updateMarkerNames();

        Vector<OpenSimObject> objs = new Vector<OpenSimObject>(1);
        objs.add(marker);
        ObjectsAddedEvent evnt = new ObjectsAddedEvent(this, model, objs);
        OpenSimDB.getInstance().setChanged();
        OpenSimDB.getInstance().notifyObservers(evnt);
        // undo support
        if (supportUndo) {
            AbstractUndoableEdit auEdit = new AbstractUndoableEdit() {

                public void undo() throws CannotUndoException {
                    super.undo();
                    Marker toDelete = model.getMarkerSet().get(saveMarkerName);
                    OneMarkerDeleteAction.deleteMarker(toDelete, false);
                }

                public void redo() throws CannotRedoException {
                    super.redo();
                    Marker newMarker = new Marker();
                    newMarker.setName(saveMarkerName);
                    newMarker.set_location(saveMarkerOffset);
                    Component physFrame = model.getComponent(saveBodyName);
                    newMarker.setParentFrame(PhysicalFrame.safeDownCast(physFrame));
                    addMarker(newMarker, true);
                }
                @Override
                public String getRedoPresentationName() {
                    return "Redo new marker";
                }
                @Override
                public String getUndoPresentationName() {
                    return "Undo new marker";
                }
            };
            ExplorerTopComponent.addUndoableEdit(auEdit);
        }
    }

    private String makeUniqueMarkerName(MarkerSet markerset) {
        String baseName = "NewMarker";
        String newMarkerName = baseName;

        for (int i = 0; i < markerset.getSize(); i++) {
            newMarkerName = baseName + "_" + i;
            if (markerset.contains(newMarkerName) == true) // name is being used by a current marker
            {
                continue;
            }
        }

        return newMarkerName;
    }
}
