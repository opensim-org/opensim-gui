/*
 * LabParametersNode.java
 *
 * Created on August 6, 2010, 2:48 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.customGui;

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
