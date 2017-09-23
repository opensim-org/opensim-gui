/* -------------------------------------------------------------------------- *
 * OpenSim: DragObjectsEvent.java                                             *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib, Peter Loan                                         *
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
 * DragObjectsEvent.java
 *
 * Created on April 12, 2007, 11:30 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.view;

import java.util.EventObject;
import org.opensim.modeling.OpenSimObject;

/**
 *
 * @author Peter Loan
 */
public class DragObjectsEvent extends EventObject {

   private double dragVector[] = null; 
    /** Creates a new instance of DragObjectsEvent
     *  dragVector is the vector in the world frame that the
     * objects should be dragged along.
     */
    public DragObjectsEvent(OpenSimObject source, double dragVector[]) {
       super(source);
       this.dragVector = new double[3];
       this.dragVector[0] = dragVector[0];
       this.dragVector[1] = dragVector[1];
       this.dragVector[2] = dragVector[2];
    }

    public OpenSimObject getObject()
    {
        return (OpenSimObject) source;
    }

    public String getName()
    {
        return ((OpenSimObject) source).getName();
    }

    public double[] getDragVector()
    {
        return dragVector;
    }

}
