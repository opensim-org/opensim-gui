/* -------------------------------------------------------------------------- *
 * OpenSim: DisplayPreferenceEditor.java                                      *
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
 *
 *
 * DisplayPreferenceEditor.java
 *
 * Created on September 26, 2010, 2:12 AM
 *
 */

package org.opensim.view.editors;

import java.awt.Component;
import java.awt.event.ActionListener;
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
import org.opensim.modeling.DecorativeGeometry;

/**
 *
 * @author ayman
 */
public class DisplayPreferenceEditor extends PropertyEditorSupport 
       implements ExPropertyEditor, InplaceEditor.Factory
{
    ///Vec3 value = null;
    /**
     * Creates a new instance of DisplayPreferenceEditor
     */
    public DisplayPreferenceEditor() {
    }

    public void setAsText(String text) throws IllegalArgumentException {
       ///this.value = Vec3.parse(text);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        super.addPropertyChangeListener(listener);
    }

    public void setValue(Object value) {
        super.setValue(value);
    }

    public Object getValue() {

        return super.getValue();//null
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
            ///ed.setValue(value);
        }
        return ed;
    }
    
    private static class Inplace implements InplaceEditor {
    
        private final JComboBox picker = new JComboBox();
        private PropertyEditor editor = null;
        private DecorativeGeometry.Representation[] options = new DecorativeGeometry.Representation[]{
            DecorativeGeometry.Representation.Hide, 
            DecorativeGeometry.Representation.DrawPoints,
            DecorativeGeometry.Representation.DrawWireframe,
            DecorativeGeometry.Representation.DrawSurface,
            DecorativeGeometry.Representation.DrawDefault};
        
        public void connect(PropertyEditor propertyEditor, PropertyEnv env) {
            editor = propertyEditor;
            picker.setModel(new DefaultComboBoxModel(options));
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
            return picker.getSelectedItem();//picker.getVec3();
        }

        public void setValue(Object object) {
            picker.setSelectedItem(object);
        }

        public boolean supportsTextEntry() {
            return true;
        }

        public void reset() {
            DecorativeGeometry.Representation d = (DecorativeGeometry.Representation) editor.getValue();
            if (d != null) {
                //picker.setVec3(d);
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
