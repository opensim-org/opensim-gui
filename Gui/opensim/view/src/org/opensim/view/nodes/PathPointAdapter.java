/* -------------------------------------------------------------------------- *
 * OpenSim: PathPointAdapter.java                                                *
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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.view.nodes;

import java.io.IOException;
import java.util.UUID;
import java.util.Vector;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.openide.util.Exceptions;
import org.opensim.modeling.AbstractProperty;
import org.opensim.modeling.ArrayDouble;
import org.opensim.modeling.GeometryPath;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimContext;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.PathPoint;
import org.opensim.modeling.Vec3;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.ObjectsRenamedEvent;
import org.opensim.view.SingleModelGuiElements;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;

/**
 *
 * @author Ayman
 */

public class PathPointAdapter  {
    PathPoint pathpoint;
    Model model;
    OpenSimContext context;
    
    public PathPointAdapter(PathPoint obj) {
        pathpoint = obj;
        model = obj.getModel();
        context = OpenSimDB.getInstance().getContext(model);
    }

    private void updateDisplay() {
        // tell the ViewDB to redraw the model
        UUID pathpoint_uuid = ViewDB.getInstance().getModelVisualizationJson(model).findUUIDForObject(pathpoint).get(0);
        ViewDB.getInstance().setObjectTranslationInParentByUuid(pathpoint_uuid, pathpoint.get_location());
    }

    public void setLocation(final Vec3 newLocation, boolean enableUndo) {
        GeometryPath currentPath = GeometryPath.safeDownCast(pathpoint.getOwner());
        boolean hasWrapping = currentPath.getWrapSet().getSize()>0;
        final Vec3 oldLocation = new Vec3(pathpoint.get_location());
        //System.out.println("oldLocation:"+oldLocation.get(1));
        context.cacheModelAndState();
        pathpoint.set_location(newLocation);
        try {
            context.restoreStateFromCachedModel();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        //System.out.println("newLocation:"+newLocation.get(1));
        if (!hasWrapping)
            updateDisplay(); 
        else{
            ViewDB.getInstance().updateModelDisplay(model);
        }
        if (enableUndo){
             AbstractUndoableEdit auEdit = new AbstractUndoableEdit(){
               public void undo() throws CannotUndoException {
                   super.undo();
                   setLocation(oldLocation, false);
                   //System.out.println("after undo oldLocation:"+oldLocation.get(1));
               }
               public void redo() throws CannotRedoException {
                   super.redo();
                   setLocation(newLocation, true);
               }
            };
            ExplorerTopComponent.addUndoableEdit(auEdit);
        }
        SingleModelGuiElements guiElem = OpenSimDB.getInstance().getModelGuiElements(model);
        guiElem.setUnsavedChangesFlag(true);
    }
    
}
