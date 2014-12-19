/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.view.nodes;

import java.util.Vector;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.opensim.modeling.ArrayDouble;
import org.opensim.modeling.Marker;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimContext;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.Vec3;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.ObjectsRenamedEvent;
import org.opensim.view.SingleModelVisuals;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;

/**
 * Copyright (c)  2005-2012, Stanford University and Ayman Habib
 * Use of the OpenSim software in source form is permitted provided that the following
 * conditions are met:
 * 	1. The software is used only for non-commercial research and education. It may not
 *     be used in relation to any commercial activity.
 * 	2. The software is not distributed or redistributed.  Software distribution is allowed 
 *     only through https://simtk.org/home/opensim.
 * 	3. Use of the OpenSim software or derivatives must be acknowledged in all publications,
 *      presentations, or documents describing work in which OpenSim or derivatives are used.
 * 	4. Credits to developers may not be removed from executables
 *     created from modifications of the source.
 * 	5. Modifications of source code must retain the above copyright notice, this list of
 *     conditions and the following disclaimer. 
 * 
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 *  SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 *  TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 *  HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR BUSINESS INTERRUPTION) OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 *  WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
        return marker.getFrameName();
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
        marker.setFrameName(bodyName);
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
        SingleModelVisuals vis = ViewDB.getInstance().getModelVisuals(model);
        vis.getMarkersRep().updateMarkerGeometry(marker);
        ViewDB.getInstance().repaintAll();
        ExplorerTopComponent.getDefault().requestActive();
    }

    /**
     * @return the offset
     */
    public String getOffsetString() {
        Vec3 offset= marker.get_location();
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
        ViewDB.getInstance().getModelGuiElements(model).updateMarkerNames();
        Vector<OpenSimObject> objs = new Vector<OpenSimObject>(1);
        objs.add(marker);
        ObjectsRenamedEvent evnt = new ObjectsRenamedEvent(this, model, objs);
        OpenSimDB.getInstance().setChanged();
        OpenSimDB.getInstance().notifyObservers(evnt);
    }

}
