/*
 * Copyright (c)  2005-2008, Stanford University
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
 *
 * PositionEditor.java
 *
 * Created on September 26, 2010, 2:12 AM
 *
 */

package org.opensim.view.editors;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.explorer.propertysheet.PropertyModel;
import org.opensim.modeling.Model;
import org.opensim.view.SingleModelGuiElements;
import org.opensim.view.nodes.PropertyEditorAdaptor;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;

/**
 *
 * @author ayman
 */
public class FrameNameEditor extends PropertyEditorSupport 
        implements ExPropertyEditor, InplaceEditor.Factory, PropertyChangeListener, ActionListener {
    /**
     * Creates a new instance of FrameNameEditor
     */
    public FrameNameEditor() {
        addPropertyChangeListener(this);
    }

    public void setAsText(String text) throws IllegalArgumentException {
        super.setAsText(text);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        super.addPropertyChangeListener(listener);
    }

    public void setValue(Object value) {
        super.setValue(value);
    }

    public Object getValue() {

        return super.getValue();
    }


    public String getAsText() {
        return getValue().toString();
    }

    public String getJavaInitializationString() {
        String retValue;
        
        retValue = super.getJavaInitializationString();
        return retValue;
    }

    public void attachEnv(PropertyEnv propertyEnv) {
        propertyEnv.registerInplaceEditorFactory(this);
    }

    private InplaceEditor ed = null;

    public InplaceEditor getInplaceEditor() {
        if (ed == null) {
            ed = new Inplace();
            
        }
        return ed;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        Model mdl = ViewDB.getCurrentModel();
        PropertyEditorAdaptor pea = new PropertyEditorAdaptor(mdl); // Need Model, Object, Property, Node
        pea.handleModelChange();
    }

    public void actionPerformed(ActionEvent e) {
    }
     
    private static class Inplace implements InplaceEditor {
    
        private final JComboBox picker = new JComboBox();
        private PropertyEditor editor = null;
        
        public void connect(PropertyEditor propertyEditor, PropertyEnv env) {
            editor = propertyEditor;
            SingleModelGuiElements modelGuiElems = OpenSimDB.getInstance().getModelGuiElements(ViewDB.getCurrentModel());
            picker.setModel(new DefaultComboBoxModel(modelGuiElems.getFrameNames()));
            reset();
        }

        public JComponent getComponent() {
            return picker;
        }

        public void clear() {
            //avoid memory leaks:
            editor = null;
            model = null;
        }

        public Object getValue() {
            return picker.getSelectedItem();
        }

        public void setValue(Object object) {
            picker.setSelectedItem (object);
        }

        public boolean supportsTextEntry() {
            return false;
        }

        public void reset() {
            String d = (String) editor.getValue();
            if (d != null) {
                picker.setSelectedItem(d);
            }
        }

        public KeyStroke[] getKeyStrokes() {
            return new KeyStroke[0];
        }

        public PropertyEditor getPropertyEditor() {
            return editor;
        }

        public PropertyModel getPropertyModel() {
            return model;
        }

        private PropertyModel model;
        public void setPropertyModel(PropertyModel propertyModel) {
            this.model = propertyModel;
        }

        public boolean isKnownComponent(Component component) {
            return component == picker || picker.isAncestorOf(component);
        }

        public void addActionListener(ActionListener actionListener) {
           //do nothing - not needed for this component
        }

        public void removeActionListener(ActionListener actionListener) {
           //do nothing - not needed for this component
        }
    }

}
