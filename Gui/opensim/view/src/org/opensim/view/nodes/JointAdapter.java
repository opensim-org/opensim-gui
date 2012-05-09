/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.view.nodes;

import java.io.IOException;
import java.text.ParseException;
import java.util.Vector;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.openide.util.Exceptions;
import org.opensim.modeling.ArrayDouble;
import org.opensim.modeling.Joint;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimContext;
import org.opensim.modeling.OpenSimObject;
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

public class JointAdapter  {
    Joint joint;
    Model model;
    OpenSimContext context;
    
    public JointAdapter(Joint obj) {
        joint = obj;
        model = obj.getBody().getModel();
        context = OpenSimDB.getInstance().getContext(model);
    }

    /**
     * @return the bodyName
     */
    public String getBodyName() {
        return joint.getBody().getName();
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
        joint.setBody(model.getBodySet().get(bodyName));
        context.recreateSystemAfterSystemExistsKeepStage();
        updateDisplay();
        if (enableUndo){
            AbstractUndoableEdit auEdit = new AbstractUndoableEdit(){
               public void undo() throws CannotUndoException {
                   super.undo();
                   setBodyName(oldName, false);
               }
               public void redo() throws CannotRedoException {
                   super.redo();
                   setBodyName(bodyName, false);
               }
            };
            ExplorerTopComponent.addUndoableEdit(auEdit);
        }
    }
    /**
     * @return the offset
     */
    public String getLocationString() {
        double[] offset= {0., 0., 0.};
        joint.getLocationInChild(offset);
        ArrayDouble ret = new ArrayDouble();
        ret.setValues(offset, 3);
        return ret.toString();
    }

    /**
     * @param offset the offset to set
     */
    public void setLocationString(String offsetString) {
       ArrayDouble d = new ArrayDouble();
        //try {
            d.fromString(offsetString);
        //} catch (ParseException ex) {
        //    Exceptions.printStackTrace(ex);
        //    return;
        //}
       setLocation(d, true);
    }
    private void setLocation(final ArrayDouble newOffset, boolean enableUndo) {
        double[] rOffset = new double[]{0., 0., 0.};
        joint.getLocationInChild(rOffset);
        final ArrayDouble oldOffset = new ArrayDouble();
        oldOffset.setValues(rOffset, 3);
        joint.setLocationInChild(newOffset.getAsVec3());
        context.recreateSystemAfterSystemExistsKeepStage();
        updateDisplay(); 
        if (enableUndo){
             AbstractUndoableEdit auEdit = new AbstractUndoableEdit(){
               public void undo() throws CannotUndoException {
                   super.undo();
                   setLocation(oldOffset, false);
               }
               public void redo() throws CannotRedoException {
                   super.redo();
                   setLocation(newOffset, false);
               }
            };
            ExplorerTopComponent.addUndoableEdit(auEdit);            
        }
    }

    private void updateDisplay() {
        // tell the ViewDB to redraw the model
        ViewDB.getInstance().updateModelDisplay(model);
        ExplorerTopComponent.getDefault().requestActive();
    }

}
