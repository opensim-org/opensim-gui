/*
 * DynamicPropertyAdaptor.java
 *
 * Created on August 8, 2010, 7:23 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.k12;

import java.util.Vector;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimContext;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.AbstractProperty;
import org.opensim.modeling.PropertyHelper;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.swingui.DblBoundedRangeModel;
import org.opensim.view.ObjectsChangedEvent;
import org.opensim.view.pub.ViewDB;
/**
 *
 * @author ayman
 */
public class DynamicPropertyAdaptor extends DblBoundedRangeModel {
    OpenSimObject dObject;
    OpenSimContext context;
    AbstractProperty dProperty;
    private double value;

    public DynamicPropertyAdaptor(OpenSimObject object, Model model, AbstractProperty prop, double min, double max) {
        setRangeForObjectPropertyDbl(object, model, prop, min, max);
    }

    public DynamicPropertyAdaptor(OpenSimObject object, Model model, AbstractProperty prop) {
        setRangeForObjectPropertyDbl(object, model, prop);
    }
    private void setRangeForObjectPropertyDbl(OpenSimObject object, Model model, AbstractProperty prop, 
            double min, double max) {
        this.dObject = object;
        this.dProperty=prop;
        context = OpenSimDB.getInstance().getContext(model);
        //double v, double e, double minimum, double maximum, int p
        if (!Double.isNaN(min) && !Double.isNaN(max))
            doSetRangeProps(PropertyHelper.getValueDouble(prop), 0., min, max, 3); 
        else
            doSetRangeProps(PropertyHelper.getValueDouble(prop), 0., PropertyHelper.getValueDouble(prop)/2., PropertyHelper.getValueDouble(prop)*2.0, 3); 
    }
    private void setRangeForObjectPropertyDbl(OpenSimObject object, Model model, AbstractProperty prop) {
        setRangeForObjectPropertyDbl(object, model, prop, 
                PropertyHelper.getValueDouble(prop)/2., 
                PropertyHelper.getValueDouble(prop)*2.0);
    }

    public double getDoubleValue() {
        return PropertyHelper.getValueDouble(dProperty);
    }

    public void setDoubleValue(double value) {
        this.value = value;
        PropertyHelper.setValueDouble(value, dProperty);
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
    
}
