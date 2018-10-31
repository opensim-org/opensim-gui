/* -------------------------------------------------------------------------- *
 * OpenSim: OneMarkerDeleteAction.java                                        *
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

package org.opensim.view.markerEditor;

import java.io.IOException;
import java.util.Vector;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.modeling.Component;
import org.opensim.modeling.Marker;
import org.opensim.modeling.MarkerSet;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimContext;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.PhysicalFrame;
import org.opensim.modeling.Vec3;
import org.opensim.utils.ErrorDialog;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.ObjectsDeletedEvent;
import org.opensim.view.SingleModelGuiElements;
import org.opensim.view.nodes.OneMarkerNode;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;

public final class OneMarkerDeleteAction extends CallableSystemAction {

   public String getName() {
      return NbBundle.getMessage(OneMarkerDeleteAction.class, "CTL_MarkerDeleteAction");
   }
   
   protected void initialize() {
      super.initialize();
      // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
      putValue("noIconInMenu", Boolean.TRUE);
   }
   
   public HelpCtx getHelpCtx() {
      return HelpCtx.DEFAULT_HELP;
   }
   
   protected boolean asynchronous() {
      return false;
   }
   
   public boolean isEnabled() {
       return OpenSimDB.getInstance().getCurrentModel()!=null;
   }

    public void performAction() {
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        // If any selected object is hidden (or any selected group is mixed), return false.
        Vector<OpenSimObject> markers = new Vector<OpenSimObject>();
        for (int i = 0; i < selected.length; i++) {
            OneMarkerNode objectNode = (OneMarkerNode) selected[i];
            Marker marker = Marker.safeDownCast(objectNode.getOpenSimObject());
            markers.add(marker);
        }
        deleteMarkers(markers, markers.size()==1);

    }
    static public void deleteMarkers(final Vector<OpenSimObject> markers, boolean supportUndo) {
        // This block of code should stay in sync with 
        // TestEditMarkers.java in opensim-core
        // Remove the marker from the model's marker set.
        Marker marker = Marker.safeDownCast(markers.get(0));
        final Model model = marker.getModel();
        MarkerSet markerset = model.getMarkerSet();
        final String saveMarkerName = marker.getName();
        final PhysicalFrame saveBody = marker.getParentFrame();
        final Vec3 saveMarkerOffset = new Vec3(marker.get_location());

        ObjectsDeletedEvent evnt = new ObjectsDeletedEvent(model, model, markers);
        OpenSimDB.getInstance().setChanged();
        OpenSimDB.getInstance().notifyObservers(evnt);
        if (supportUndo) {
            //System.out.println("name, body, offset="+saveMarkerName+" "+saveBodyName+" "+saveMarkerOffset);
            AbstractUndoableEdit auEdit = new AbstractUndoableEdit() {

                public void undo() throws CannotUndoException {
                    super.undo();
                    Marker newMarker = new Marker();
                    newMarker.setName(saveMarkerName);
                    newMarker.set_location(saveMarkerOffset);
                    newMarker.setParentFrame(saveBody);
                    OpenSimContext context = OpenSimDB.getInstance().getContext(model);
                    context.cacheModelAndState();
                    model.getMarkerSet().adoptAndAppend(newMarker);
                    try {
                        context.restoreStateFromCachedModel();
                    } catch (IOException ex) {
                        ErrorDialog.displayExceptionDialog(ex);
                    }
                    Vector<OpenSimObject> markers = new  Vector<OpenSimObject>();
                    markers.add(newMarker);
                    new NewMarkerAction().addMarkers(markers, false);
                }

                public void redo() throws CannotRedoException {
                    super.redo();
                    Marker toDelete = model.getMarkerSet().get(saveMarkerName);
                    Vector<OpenSimObject> markers = new Vector<OpenSimObject>();
                    markers.add(toDelete);
                    deleteMarkers(markers, true);
                }
            };
            ExplorerTopComponent.addUndoableEdit(auEdit);
        }
        // Use Editing call sequence since removing a marker requires 
        // recreation of tree traversal/initialization
        OpenSimContext context = OpenSimDB.getInstance().getContext(model);
        //context.cacheModelAndState();
        for (int i=0; i<markers.size(); i++)
            markerset.remove(Marker.safeDownCast(markers.get(i)));
        // invoke initSystem on the modified model so that traversal is updated and markers are 
        // removed from component tree
        context.recreateSystemAfterSystemExistsKeepStage();
        // Update the marker name list in the ViewDB.
        OpenSimDB.getInstance().getModelGuiElements(model).updateMarkerNames();
        SingleModelGuiElements guiElem = OpenSimDB.getInstance().getModelGuiElements(model);
        guiElem.setUnsavedChangesFlag(true);
    }
  
}
