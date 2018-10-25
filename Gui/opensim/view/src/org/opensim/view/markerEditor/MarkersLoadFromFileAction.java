/* -------------------------------------------------------------------------- *
 * OpenSim: MarkersLoadFromFileAction.java                                    *
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
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.opensim.modeling.ArrayStr;
import org.opensim.modeling.Marker;
import org.opensim.modeling.MarkerSet;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimContext;
import org.opensim.modeling.OpenSimObject;
import org.opensim.utils.ErrorDialog;
import org.opensim.utils.FileUtils;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.nodes.MarkersNode;
import org.opensim.view.pub.OpenSimDB;

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
        // This block of code should stay in sync with 
        // TestEditMarkers.java in opensim-core
        MarkerSet newMarkerSet;
        try {
           newMarkerSet = new MarkerSet(fileName);
        } catch (IOException ex) {
           Exceptions.printStackTrace(ex);
           return;
       }
        //
        // Will make sure no marker in newMarkerSet is already in model since
        // updateMarkerSet will delete the old Marker causing GUI to hold to a stale 
        // pointer/reference.
        MarkerSet markersToAdd = new MarkerSet(); // for passing to model
        String errorMessages = "";
        boolean errorsToReport = false;

        for (int i=0; i<newMarkerSet.getSize(); i++){
             Marker newMarker = newMarkerSet.get(i);
             // Check name duplication
             if (model.getMarkerSet().getIndex(newMarker.getName())!= -1){
                 errorMessages = errorMessages.concat("Marker name "+newMarker.getName()+ " already in use. Ignoring..\n");
                 errorsToReport = true;
                 continue;
             }
             // This marker will be added
             markersToAdd.cloneAndAppend(newMarker);
        }
        // This is the meat of the functionality where passed in MarkerSet is "merged" with model's MarkerSet.
        model.updateMarkerSet(markersToAdd);
        OpenSimContext context = OpenSimDB.getInstance().getContext(model);
        // This will do the full recreation of System and save/restore of state so we don't lose configuration
        context.recreateSystemAfterSystemExistsKeepStage();

        // The remaining part is to update the GUI tree view and visualization to show the newly added Markers
        Vector<OpenSimObject> markersAdded = new Vector<OpenSimObject>(); // for GUI update
        for (int i=0; i < markersToAdd.getSize(); i++){
            markersAdded.add(model.getMarkerSet().get(markersToAdd.get(i).getName()));
        }
         new NewMarkerAction().addMarkers(markersAdded, true);
        if (errorsToReport){
            DialogDisplayer.getDefault().notify(
                    new NotifyDescriptor.Message("Following errors were encountered " + errorMessages));
        }
    }

}
