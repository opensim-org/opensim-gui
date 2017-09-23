/* -------------------------------------------------------------------------- *
 * OpenSim: LabParametersNode.java                                            *
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
 * LabParametersNode.java
 *
 * Created on August 6, 2010, 2:48 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.k12;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;

/**
 *
 * @author ayman
 */
public class LabParametersNode  implements Externalizable {
    
    private ArrayList<LabParameter> parameters = new ArrayList<LabParameter>(5);
    /** Creates a new instance of LabParametersNode */
    public LabParametersNode() {
        
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    public ArrayList<LabParameter> getParameters() {
        return parameters;
    }

    public void setParameters(ArrayList<LabParameter> parameters) {
        this.parameters = parameters;
    }
    public void addParameter(LabParameter aParameter) {
        parameters.add(aParameter);
    }
/*
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();
        set.setDisplayName("Parameters");
        Model mdl=OpenSimDB.getInstance().getCurrentModel();
        for(int i=0; i< parameters.size(); i++){
            LabParameter l = parameters.get(i);
            OpenSimObject obj = mdl.getObjectByTypeAndName(l.getOpenSimType(), l.getObjectName());
            if (obj instanceof Coordinate){
                Coordinate coord = Coordinate.safeDownCast(obj);
                CoordinateAdaptor coordW = new CoordinateAdaptor(coord);
                PropertySupport.Reflection coordinateNodeProp;
                try {
                    coordinateNodeProp = new PropertySupport.Reflection(coordW, double.class, "getValue", "setValue");
                    coordinateNodeProp.setPropertyEditorClass(CoordinatePropertyEditor.class);
                    coordinateNodeProp.setName(l.getPropertyDisplayName());
                    coordinateNodeProp.setValue("Coordinate", obj);
                    set.put(coordinateNodeProp);    
                    continue;
                } catch (NoSuchMethodException ex) {
                    ex.printStackTrace();
                }
            }
            org.opensim.modeling.PropertySet ps= obj.getPropertySet();
            try {
                org.opensim.modeling.Property prop = ps.get(l.getPropertyName());
                PropertySupport.Reflection nextNodeProp;
                nextNodeProp = new PropertySupport.Reflection(prop, double.class, "getValueDbl", "setValue");
                nextNodeProp.setName(l.getPropertyDisplayName());
                set.put(nextNodeProp);
                //
                PropertySupport.Reflection anotherNodeProp = new PropertySupport.Reflection(prop, double.class, "getValueDbl", "setValue");
                anotherNodeProp.setPropertyEditorClass(SliderPropertyEditor.class);
                anotherNodeProp.setName(l.getPropertyDisplayName()+"_Slider");
                anotherNodeProp.setValue("Min", 0);
                anotherNodeProp.setValue("Max", 100);
                set.put(anotherNodeProp);   
                
                
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (NoSuchMethodException ex) {
                ex.printStackTrace();
            }
        }
        sheet.put(set);
        return sheet;
    }

    void buildSheet() {
        createSheet();

    }
*/

    public void removeParameter(LabParameter p) {
        parameters.remove(p);
    }
}
