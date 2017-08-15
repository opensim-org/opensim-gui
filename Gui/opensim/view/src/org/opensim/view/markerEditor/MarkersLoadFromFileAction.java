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
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.opensim.logger.OpenSimLogger;
import org.opensim.modeling.ArrayStr;
import org.opensim.modeling.Body;
import org.opensim.modeling.Component;
import org.opensim.modeling.Marker;
import org.opensim.modeling.MarkerSet;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimContext;
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
        MarkerSet modelMarkerSet = model.getMarkerSet();
        OpenSimContext context = OpenSimDB.getInstance().getContext(model);
        ArrayStr existingNames = new ArrayStr();
        modelMarkerSet.getMarkerNames(existingNames);
        String errorMessages = "";
        boolean errorsToReport = false;
        for (int i=0; i<newMarkerSet.getSize(); i++){
            context.cacheModelAndState();
             Marker newMarker = newMarkerSet.get(i);
             // Check name duplication
             if (existingNames.findIndex(newMarker.getName())!= -1){
                 errorMessages = errorMessages.concat("Marker name "+newMarker.getName()+ " already in use. Ignoring..\n");
                 errorsToReport = true;
                 continue;
             }
             // Ideally we use cloneAndAppend but the clone loses track of frame!
             modelMarkerSet.adoptAndAppend(newMarker);
            try {
                context.restoreStateFromCachedModel();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
             // Notify the world so that navigator and visualization update
             new NewMarkerAction().addMarker(newMarker, true);
             modelMarkerSet.getMarkerNames(existingNames);
        }
        // This hack is so that the adopted Marker isn't gc'ed
        newMarkerSet.setMemoryOwner(false);
        if (errorsToReport){
            DialogDisplayer.getDefault().notify(
                    new NotifyDescriptor.Message("Following errors were encountered " + errorMessages));
        }
    }

}
