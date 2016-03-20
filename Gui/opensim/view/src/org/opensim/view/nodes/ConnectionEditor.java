package org.opensim.view.nodes;


import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.openide.util.Exceptions;
import org.opensim.modeling.*;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.SingleModelGuiElements;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;


/**
 *
 * @author ayman
 * Copyright (c)  2005-2012, Stanford University, Ayman Habib, Kevin Xu
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
 *  PropertyEditorAdaptor is an adaptor class to implement the expected signatures for methods to be used
 * by built in property editors. The signatures are along the form:
 * setValueX( X valueToSet)
 * X getValueX()
 * 
 * These signature patterns originate in JavaBeans
 * The actual functionality is implemented by the native side class PropertyHelper that can leverage RTTI
 * @author ayman
 */
public class ConnectionEditor {

    OpenSimObject obj; // Object being edited or selected in navigator window
    AbstractConnector connector; // Property being edited
    OpenSimContext context = null; // Context object needed to recreate the system as needed, cached for speed
    Model model; // model to which obj belongs
    OpenSimObjectNode node;

    Model clonedModel;
    State clonedState;
    
    public ConnectionEditor(AbstractConnector conn, OpenSimObjectNode ownerNode) {
        this.connector = conn;
        this.model = ownerNode.getModelForNode();
        this.context = OpenSimDB.getInstance().getContext(model);
        this.obj = ownerNode.getOpenSimObject();
        this.node = ownerNode;
    }
    
    public void handleConnectionChangeCommon() {
        if (Geometry.safeDownCast(obj)!= null){
            Component mc = Component.safeDownCast(obj);
            ViewDB.getInstance().getModelVisuals(model).upateDisplay(mc);  
            ViewDB.repaintAll();
        }
        else 
            ViewDB.getInstance().updateModelDisplay(model);
        if (node!= null) node.refreshNode();
        SingleModelGuiElements guiElem = ViewDB.getInstance().getModelGuiElements(model);
        guiElem.setUnsavedChangesFlag(true);
    }

    // String Properties
    public String getConnectedToName() {
        return connector.getConnecteeName();
    }

    public void setConnectedToName(String v) {
        setConnectedToNameAndPropagateChange(v, true);
    }

    private void setConnectedToNameAndPropagateChange(String v, boolean supportUndo) {
        String oldValue = getConnectedToName();
        handleConnectionChange(oldValue, v, supportUndo);
    }

    private void handleConnectionChange(final String oldValue, final String v, boolean supportUndo) {
        context.cacheModelAndState();
        connector.setConnecteeName(v);
        try {
            context.restoreStateFromCachedModel();
         } catch (IOException iae) {
            try {
                 new JOptionPane(iae.getMessage(), 
				JOptionPane.ERROR_MESSAGE).createDialog(null, "Error").setVisible(true);
                
                connector.setConnecteeName(oldValue);
                context.restoreStateFromCachedModel();  
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
        }
        }
        handleConnectionChangeCommon();
        
        if (supportUndo) {
            AbstractUndoableEdit auEdit = new AbstractUndoableEdit() {

                @Override
                public void undo() throws CannotUndoException {
                    super.undo();
                    setConnectedToNameAndPropagateChange(oldValue, false);
                }

                @Override
                public String getUndoPresentationName() {
                    return "Undo "+connector.getName()+" change";
                }

                @Override
                public void redo() throws CannotRedoException {
                    super.redo();
                    setConnectedToNameAndPropagateChange(v, true);
                }
                 @Override
                public String getRedoPresentationName() {
                    return "Redo "+connector.getName()+" change";
                }
           };
            ExplorerTopComponent.addUndoableEdit(auEdit);
        }
    }

            
}
