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
import org.opensim.logger.OpenSimLogger;
import org.opensim.modeling.Body;
import org.opensim.modeling.Marker;
import org.opensim.modeling.MarkerSet;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.PhysicalFrame;
import org.opensim.modeling.Vec3;
import org.opensim.utils.FileUtils;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.ObjectsAddedEvent;
import org.opensim.view.nodes.MarkersNode;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;

/**
 *
 * @author Ayman Habib
 */
public class MarkersLoadFromFileAction extends AbstractAction {

   /** Creates a new instance of NewMarkerAction */
   public MarkersLoadFromFileAction() {
      super(NbBundle.getMessage(MarkersLoadFromFileAction.class, "CTL_ImportMarkersAction"));
   }

   public void actionPerformed(ActionEvent evt) {
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        // One MarkersNode must have been selected otherwise bail
        if (selected.length != 1 || !(selected[0] instanceof MarkersNode)) 
            return;
   
        MarkersNode markersNode = (MarkersNode) selected[0];
        Model model = markersNode.getModelForNode();
        MarkerSet markerset = model.getMarkerSet();
        // Browse for file for MarkerSet
        String fileName = FileUtils.getInstance().browseForFilename(".xml", "XML file containing markers");
        if (fileName==null) return;
        MarkerSet newMarkerSet;
        try {
            newMarkerSet = new MarkerSet(model, fileName);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            return;
        }
        for (int i=0; i<newMarkerSet.getSize(); i++){
            Marker m = newMarkerSet.get(i);
            Vec3 offset = m.get_location();
            String newMarkerFrameName = m.getFrameName();
            PhysicalFrame physFrame = PhysicalFrame.safeDownCast(model.getComponent(newMarkerFrameName));
            if (physFrame != null){
                Marker newMarker = markerset.addMarker(m.getName(), offset, physFrame);
                if (newMarker!=null)
                    addMarker(newMarker, true);
                else
                    OpenSimLogger.logMessage("Marker: "+m.getName()+" already exists in model and will be ignored\n", OpenSimLogger.INFO);
           }
            else
               OpenSimLogger.logMessage("Marker: "+m.getName()+" refers to unknown Body "+newMarkerFrameName
                       +" and will be ignored\n", OpenSimLogger.INFO);

        }
         
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
                    Marker newMarker = model.getMarkerSet().addMarker(saveMarkerName, saveMarkerOffset, model.getBodySet().get(saveBodyName));
                    addMarker(newMarker, true);
                }
            };
            ExplorerTopComponent.addUndoableEdit(auEdit);
        }
    }

    private String makeUniqueMarkerName(MarkerSet markerset) {
        String baseName = "NewMarker";
        String newMarkerName = baseName;
        Marker existingMarker = null;

        for (int i = 0; i < markerset.getSize(); i++) {
            newMarkerName = baseName + "_" + i;
            if (markerset.contains(newMarkerName) == true) // name is being used by a current marker
            {
                continue;
            }
            else
                break;
        }

        return newMarkerName;
    }
}
