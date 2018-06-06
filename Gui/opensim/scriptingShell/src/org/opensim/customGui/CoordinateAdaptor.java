/* -------------------------------------------------------------------------- *
 * OpenSim: CoordinateAdaptor.java                                            *
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
 * CoordinateAdaptor.java
 *
 * Created on August 8, 2010, 7:23 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.customGui;

import java.util.Vector;
import org.opensim.modeling.Coordinate;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimContext;
import org.opensim.modeling.OpenSimObject;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.swingui.DblBoundedRangeModel;
import org.opensim.view.ObjectsChangedEvent;
import org.opensim.view.pub.ViewDB;
/**
 *
 * @author ayman
 */
public class CoordinateAdaptor extends DblBoundedRangeModel {
    Coordinate coord;
    OpenSimContext context;
    private double value;

    public CoordinateAdaptor(Coordinate coord) {
        setCoordinate(coord);
    }
    public void setCoordinate(Coordinate coord) {
        this.coord = coord;
        context = OpenSimDB.getInstance().getContext(coord.getModel());
        //double v, double e, double minimum, double maximum, int p
        doSetRangeProps(context.getValue(coord), 0., coord.getRangeMin(), coord.getRangeMax(), 3);        
    }

    public double getDoubleValue() {
        return context.getValue(coord);
    }

    public void setDoubleValue(double value) {
        this.value = value;
        context.setValue(coord, value, true);
        coord.setDefaultValue(value);
         // Use renderAll rather than repaintAll for greater responsiveness in 3d viewer
         Model mdl=OpenSimDB.getInstance().getCurrentModel();
         ViewDB.getInstance().updateModelDisplayNoRepaint(mdl, false, true);
         ViewDB.getInstance().renderAll();
         Vector<OpenSimObject> objs = new Vector<OpenSimObject>(1);
         objs.add(coord);
         ObjectsChangedEvent evnt = new ObjectsChangedEvent(this, mdl, objs);
         OpenSimDB.getInstance().setChanged();
         OpenSimDB.getInstance().notifyObservers(evnt);
    }
    
}
