/*
 * DynamicPropertyComponentAdaptor.java
 *
 * Created on August 8, 2010, 7:23 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.k12;

import java.util.Vector;
import org.opensim.modeling.ArrayDouble;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimContext;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.Property;
import org.opensim.swingui.DblBoundedRangeModel;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.ObjectsChangedEvent;
import org.opensim.view.pub.ViewDB;
/**
 *
 * @author ayman
 */
public class DynamicPropertyComponentAdaptor extends DblBoundedRangeModel {
    OpenSimObject dObject;
    OpenSimContext context;
    Property dProperty;
    private double value;
    int index=-1;
    
    public DynamicPropertyComponentAdaptor(OpenSimObject object, Model model, Property prop, int index, double min, double max) {
        setRangeForObjectPropertyDbl(object, model, prop, index, min, max);
    }

    public DynamicPropertyComponentAdaptor(OpenSimObject object, Model model, Property prop, int index) {
        setRangeForObjectPropertyDbl(object, model, prop, index);
    }
    private void setRangeForObjectPropertyDbl(OpenSimObject object, Model model, Property prop, int index, 
            double min, double max) {
        this.dObject = object;
        this.dProperty=prop;
        this.index = index;
        context = OpenSimDB.getInstance().getContext(model);
        //double v, double e, double minimum, double maximum, int p
        double v = getComponentValue(prop, index);
        if (!Double.isNaN(min) && !Double.isNaN(max))
            doSetRangeProps(v, 0., min, max, 3);
        else
            doSetRangeProps(v, 0., v/2., v*2.0, 3);
    }
    private void setRangeForObjectPropertyDbl(OpenSimObject object, Model model, Property prop, int index) {
        double v = getComponentValue(prop, index);
        setRangeForObjectPropertyDbl(object, model, prop, index, v/2., v*2.0);
    }

    public double getDoubleValue() {
        return getComponentValue(dProperty, index);
    }

    public void setDoubleValue(double value) {
        this.value = value;
        setComponentValue(dProperty, index, value);
        context.realizePosition();
         // Use renderAll rather than repaintAll for greater responsiveness in 3d viewer
         Model mdl=OpenSimDB.getInstance().getCurrentModel();
         ViewDB.getInstance().updateModelDisplayNoRepaint(mdl);
         ViewDB.getInstance().renderAll();
         Vector<OpenSimObject> objs = new Vector<OpenSimObject>(1);
         objs.add(dObject);
         ObjectsChangedEvent evnt = new ObjectsChangedEvent(this, mdl, objs);
         OpenSimDB.getInstance().setChanged();
         OpenSimDB.getInstance().notifyObservers(evnt);
    }

    private double getComponentValue(Property prop, int index) {
        if (prop.getType()==prop.getType().DblArray){
            assert(prop.getValueDblArray().getSize()>=(index-1));
            return prop.getValueDblArray().getitem(index);
        }
        else if (prop.getType()==prop.getType().DblVec){
            assert(index <= prop.getValueDblArray().getSize()-1);
            String string = prop.toString();
            String[] valueStrings = string.substring(1, string.length()-1).split(" ");
            double[] dValues = new double[3];
            for(int i=0; i<3; i++){
                dValues[i]=Double.valueOf(valueStrings[i]);
            }

            return dValues[index];
        }
        else 
            return 0;
    }

    private void setComponentValue(Property prop, int index, double value) {
        if (prop.getType()==prop.getType().DblArray){
            assert(prop.getValueDblArray().getSize()>=(index-1));
            ArrayDouble current=prop.getValueDblArray();
            current.setitem(index, value);
            prop.setValue(current);
        }
        else if (prop.getType()==prop.getType().DblVec){
            assert(index <= 2);
            String string = prop.toString();
            String[] valueStrings = string.substring(1, string.length()-1).split(" ");
            double[] dValues = new double[3];
            for(int i=0; i<3; i++){
                dValues[i]=Double.valueOf(valueStrings[i]);
            }
            dValues[index]=value;
            prop.setValue(3, dValues);
        }
        else 
            throw new UnsupportedOperationException("Not yet implemented");
    }
    
}
