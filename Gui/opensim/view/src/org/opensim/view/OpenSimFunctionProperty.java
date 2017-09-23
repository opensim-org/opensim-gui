/* -------------------------------------------------------------------------- *
 * OpenSim: OpenSimFunctionProperty.java                                      *
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
package org.opensim.view;

import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;
import org.openide.nodes.PropertySupport;
import org.opensim.modeling.AbstractProperty;
import org.opensim.modeling.Function;
import org.opensim.modeling.OpenSimObject;
import org.opensim.view.nodes.OpenSimFunctionEditor;
import org.opensim.view.nodes.OpenSimObjectNode;

/**
 *
 * @author ayman
 */
public class OpenSimFunctionProperty extends OpenSimBaseObjectProperty  {

    Function objectToEdit;

    public OpenSimFunctionProperty(AbstractProperty ap, OpenSimObjectNode parentNode){
        super(ap.getName(), ap.getName(), ap.getComment());
        OpenSimObject dObj = ap.isOptionalProperty()?ap.getValueAsObject(0): ap.getValueAsObject();
        objectToEdit = Function.safeDownCast(dObj);
        this.ap = ap;
        this.parentNode = parentNode;

    }
    
    @Override
    public OpenSimObject getValue() {
        return objectToEdit;
    }

    @Override
    public void setValue(OpenSimObject value)  {
        objectToEdit = Function.safeDownCast(value);
    }

    @Override
    public PropertyEditor getPropertyEditor() {
        return new OpenSimFunctionEditor(ap, objectToEdit, parentNode);
    }

    @Override
    public void restoreDefaultValue() throws IllegalAccessException, InvocationTargetException {
        super.restoreDefaultValue();
    }


}

