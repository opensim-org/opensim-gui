/* -------------------------------------------------------------------------- *
 * OpenSim: MarkerAdapter.java                                                *
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

import java.util.Vector;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.opensim.modeling.AbstractProperty;
import org.opensim.modeling.ArrayDouble;
import org.opensim.modeling.Marker;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimContext;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.Vec3;
import org.opensim.threejs.ModelVisualizationJson;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.ObjectsRenamedEvent;
import org.opensim.view.SingleModelVisuals;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;

/**
 *
 * @author Ayman
 */

public class MarkerAdapter  {
    Marker marker;
    Model model;
    OpenSimContext context;
    
    public MarkerAdapter(Marker obj) {
        marker = obj;
        model = obj.getModel();
        context = OpenSimDB.getInstance().getContext(model);
    }

    /**
     * @return the bodyName
     */
    public String getBodyName() {
        return marker.getParentFrameName();
    }

    /**
     * @param bodyName the bodyName to set
     */
    public void setBodyName(String bodyName) {
        setBodyName(bodyName, true);
    }
    
    private void setBodyName(final String bodyName, boolean enableUndo) {
        final String oldName = getBodyName();
        if (bodyName.equals(oldName)) return; // Nothing to do
        //marker.setParentFrameName(bodyName); 
        // The following line calls setParentFrame
        context.setBody(marker, model.getBodySet().get(bodyName), true);
        updateDisplay();
        if (enableUndo){
            AbstractUndoableEdit auEdit = new AbstractUndoableEdit(){
               public void undo() throws CannotUndoException {
                   super.undo();
                   setBodyName(oldName, false);
               }
               public void redo() throws CannotRedoException {
                   super.redo();
                   setBodyName(bodyName, true);
               }
            };
            ExplorerTopComponent.addUndoableEdit(auEdit);
        }
    }

    private void updateDisplay() {
        // tell the ViewDB to redraw the model
        ViewDB.getInstance().setObjectTranslationInParent(model, marker, marker.get_location());
    }

    /**
     * @return the offset
     */
    public String getOffsetString() {
        AbstractProperty offset= marker.getPropertyByName("location");
        return offset.toString();
    }

    /**
     * @param offset the offset to set
     */
    public void setOffsetString(String offsetString) {
       ArrayDouble d = new ArrayDouble();
        //try {
            d.fromString(offsetString);
        //} catch (ParseException ex) {
        //    Exceptions.printStackTrace(ex);
        //    return;
        //}
       setOffset(d, true);
    }
    private void setOffset(final ArrayDouble newOffset, boolean enableUndo) {
        Vec3 rOffest = marker.get_location();
        final ArrayDouble oldOffset = new ArrayDouble(3);
        for(int i=0; i<3; i++) oldOffset.set(i, rOffest.get(i));
         marker.set_location(newOffset.getAsVec3());
        updateDisplay(); 
        if (enableUndo){
             AbstractUndoableEdit auEdit = new AbstractUndoableEdit(){
               public void undo() throws CannotUndoException {
                   super.undo();
                   setOffset(oldOffset, false);
               }
               public void redo() throws CannotRedoException {
                   super.redo();
                   setOffset(newOffset, true);
               }
            };
            ExplorerTopComponent.addUndoableEdit(auEdit);            
        }
    }
    
    public void setName(String newName){
       setName(newName, true);
    }
    public String getName() {
        return marker.getName();
    }
    
    private void setName(final String newName, boolean enableUndo) {
        final String oldName = getName();
        if (newName.equals(oldName)) return; // Nothing to do
        marker.setName(newName);
        
        
        ExplorerTopComponent.getDefault().requestActive();
        if (enableUndo){
            AbstractUndoableEdit auEdit = new AbstractUndoableEdit(){
               public void undo() throws CannotUndoException {
                   super.undo();
                   setName(oldName, false);
               }
               public void redo() throws CannotRedoException {
                   super.redo();
                   setName(newName, true);
               }
            };
            ExplorerTopComponent.addUndoableEdit(auEdit);
        }
        OpenSimDB.getInstance().getModelGuiElements(model).updateMarkerNames();
        Vector<OpenSimObject> objs = new Vector<OpenSimObject>(1);
        objs.add(marker);
        ObjectsRenamedEvent evnt = new ObjectsRenamedEvent(this, model, objs);
        OpenSimDB.getInstance().setChanged();
        OpenSimDB.getInstance().notifyObservers(evnt);
    }

}
