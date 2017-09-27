/* -------------------------------------------------------------------------- *
 * OpenSim: OpenSimEvent.java                                                 *
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
 * OpenSimEvent.java
 *
 * Created on July 18, 2007, 1:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.view;

import java.util.EventObject;
import java.util.Vector;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimObject;

/**
 *
 * @author Peter Loan
 */
public class OpenSimEvent extends EventObject {
   /* 'objects' holds the vector of objects that this event is for. */
   private Vector<OpenSimObject> objects;
   /* 'model' is the model that owns the objects, but can be null
    * if the objects are not from a model or from multiple models.
    */
   private Model model;

   /** Creates a new instance of OpenSimEvent */
   public OpenSimEvent(Object source, Model theModel, Vector<OpenSimObject> theObjects) {
      super(source);
      model = theModel;
      if (theObjects != null && theObjects.size() > 0) {
         objects = new Vector<OpenSimObject>(theObjects.size());
         objects.addAll(theObjects);
      }
   }

   public Model getModel() {
       return model;
   }

   public Vector<OpenSimObject> getObjects() {
      return objects;
   }
}
