package org.opensim.view.nodes;

import java.util.Vector;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimObject;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.ObjectsRenamedEvent;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;

/**
 *
 * @author ayman
 * Copyright (c)  2005-2012, Stanford University, Ayman Habib
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
 */

/**
 * A class to handle changes in Object name. This's handled separate from Property Editors because
 * - Name is not an OpenSimObject property,
 * - Name changes are propagated differently using an object renamed event.
 * 
 * @author ayman
 */
public class ObjectNameEditor {
    OpenSimObject obj;
    Model model;
    OpenSimObjectNode node;
    
    public ObjectNameEditor(OpenSimObject obj, Model model, OpenSimObjectNode aThis) {
        this.obj = obj;
        this.model = model;
        node = aThis;
    }
    public String getName() {
        return obj.getName();
    }
    
    public void setName(String aName){
        setName(aName, true);
    }
    private void setName(String aName, boolean supportUndo) {
        String oldName  = obj.getName();
        obj.setName(aName);
        handleNameChange(oldName, aName, supportUndo);
    }
    private void handleNameChange(final String oldValue, final String v, boolean supportUndo) {
        ViewDB.getInstance().updateModelDisplay(model);
        Vector<OpenSimObject> objs = new Vector<OpenSimObject>(1);
        objs.add(obj);
        ObjectsRenamedEvent evnt = new ObjectsRenamedEvent(this, model, objs);
        OpenSimDB.getInstance().setChanged();
        OpenSimDB.getInstance().notifyObservers(evnt);
        node.refreshNode();
        if (supportUndo) {
            AbstractUndoableEdit auEdit = new AbstractUndoableEdit() {

                @Override
                public void undo() throws CannotUndoException {
                    super.undo();
                    setName(oldValue, false);
                }

                @Override
                public void redo() throws CannotRedoException {
                    super.redo();
                    setName(v, true);
                }
                @Override
                public String getRedoPresentationName() {
                    return "Redo name change";
                }
                @Override
                public String getUndoPresentationName() {
                    return "Undo name change";
                }
            };
            ExplorerTopComponent.addUndoableEdit(auEdit);
        }
    }
}
