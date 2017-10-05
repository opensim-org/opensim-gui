/* -------------------------------------------------------------------------- *
 * OpenSim: NewMarkerAction.java                                              *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib                                                     *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may    *
 * not use this file except in compliance with the License. You may obtain a  *
 * copy of the License at http://www.apache.org/licenses/LICENSE-2.0          *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 * -------------------------------------------------------------------------- */
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
        Vector<OpenSimObject> markers = new  Vector<OpenSimObject>();
        markers.add(newMarker);
        addMarkers(markers, true);
    }

    public void addMarkers(final Vector<OpenSimObject> markers, boolean supportUndo) {

        // This block of code should stay in sync with 
        // TestEditMarkers.java in opensim-core
        final Marker marker = Marker.safeDownCast(markers.get(0));
        final String saveMarkerName = marker.getName();
        final String saveBodyName = marker.getParentFrameName();
        final Vec3 saveMarkerOffset = marker.get_location();
        final Model model = marker.getModel();
         // Update the marker name list in the ViewDB.
        OpenSimDB.getInstance().getModelGuiElements(model).updateMarkerNames();

        ObjectsAddedEvent evnt = new ObjectsAddedEvent(this, model, markers);
        OpenSimDB.getInstance().setChanged();
        OpenSimDB.getInstance().notifyObservers(evnt);
        // undo support
        if (supportUndo) {
            AbstractUndoableEdit auEdit = new AbstractUndoableEdit() {

                public void undo() throws CannotUndoException {
                    super.undo();
                    Marker toDelete = model.getMarkerSet().get(saveMarkerName);
                    Vector<OpenSimObject> markers = new Vector<OpenSimObject>();
                    markers.add(toDelete);
                    OneMarkerDeleteAction.deleteMarkers(markers, false);
                }

                public void redo() throws CannotRedoException {
                    super.redo();
                    Marker newMarker = new Marker();
                    newMarker.setName(saveMarkerName);
                    newMarker.set_location(saveMarkerOffset);
                    Component physFrame = model.getComponent(saveBodyName);
                    newMarker.setParentFrame(PhysicalFrame.safeDownCast(physFrame));
                    Vector<OpenSimObject> markers = new  Vector<OpenSimObject>();
                    markers.add(newMarker);
                    addMarkers(markers, true);
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
