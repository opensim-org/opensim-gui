/* -------------------------------------------------------------------------- *
 * OpenSim: ObjectNameEditor.java                                             *
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
