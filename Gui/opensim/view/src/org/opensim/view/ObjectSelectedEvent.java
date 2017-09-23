/* -------------------------------------------------------------------------- *
 * OpenSim: ObjectSelectedEvent.java                                          *
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
 * ObjectSelectedEvent.java
 *
 * Created on April 1, 2007, 09:20 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.view;

import java.util.EventObject;
import org.opensim.modeling.OpenSimObject;
import org.opensim.view.SelectedObject;

/**
 *
 * @author Peter Loan
 */
public class ObjectSelectedEvent extends EventObject {

   private Selectable selectedObject;
   private boolean selected = false;

    /** Creates a new instance of ObjectSelectedEvent
     *  state indicates whether the object was selected (true)
     *  or unselected (false).
     */
    public ObjectSelectedEvent(Object source, Selectable object, boolean state) {
       super(source);
       selectedObject = object;
       selected = state;
    }

    public SelectedObject getSelectedObject()
    {
        return (SelectedObject) selectedObject;
    }

    public String getName()
    {
        return getSelectedObject().getOpenSimObject().getName();
    }

    public boolean getState()
    {
        return selected;
    }

}
