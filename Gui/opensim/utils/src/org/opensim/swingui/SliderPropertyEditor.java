/* -------------------------------------------------------------------------- *
 * OpenSim: SliderPropertyEditor.java                                         *
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
 * SliderPropertyEditor.java
 *
 * Created on July 6, 2010, 12:11 AM
 *
 *
 */

package org.opensim.utils;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.explorer.propertysheet.PropertyModel;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;

/**
 *
 * @author ayman. A Property Editor that shows as a Slider that goes from 0 to 1
 */
public class SliderPropertyEditor extends PropertyEditorSupport implements ExPropertyEditor, InplaceEditor.Factory{
    
    /** Creates a new instance of SliderPropertyEditor */
    public SliderPropertyEditor() {
    }

    public void attachEnv(PropertyEnv propertyEnv) {
        propertyEnv.registerInplaceEditorFactory(this);
        Node.Property dProperty=(Property) propertyEnv.getFeatureDescriptor();
        Object min = dProperty.getValue("Min");
        Object max = dProperty.getValue("Max");
    }

    private InplaceEditor ed = null;

    public InplaceEditor getInplaceEditor() {
        if (ed == null) {
            ed = new Inplace();
        }
        return ed;
    }    
    // Nested class for Inplace editor
   private static class Inplace implements InplaceEditor {
       private final JSlider slider = new JSlider();
       private PropertyEditor editor = null;
       
       public void connect(PropertyEditor propertyEditor, PropertyEnv propertyEnv) {
           editor = propertyEditor;
        }

        public JComponent getComponent() {
            return slider;
        }

        public void clear() {
        }

        public Object getValue() {
            return new Integer(slider.getValue()); //0-100
        }

        public void setValue(Object object) {
            if (object instanceof Integer){
                int value = ((Integer) object).intValue();
                slider.setValue(value);
            }
        }

        public boolean supportsTextEntry() {
            return false;
        }

        public void reset() {
        }

        public void addActionListener(ActionListener actionListener) {
        }

        public void removeActionListener(ActionListener actionListener) {
        }

        public KeyStroke[] getKeyStrokes() {
            return new KeyStroke[0];
        }

        public PropertyEditor getPropertyEditor() {
            return editor;
        }
        private PropertyModel model;
        public PropertyModel getPropertyModel() {
            return model;
        }

        public void setPropertyModel(PropertyModel propertyModel) {
        }

        public boolean isKnownComponent(Component component) {
            return true;
        }
   }
}
