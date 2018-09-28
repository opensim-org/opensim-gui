/* -------------------------------------------------------------------------- *
 * OpenSim: SelectedUserObject.java                                           *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib, Eran Guendelman                                    *
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
 * SelectedUSerObject
 * Author(s): Ayman Habib
 */
package org.opensim.view;

import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.Model;

/**
 *
 * @author Ayman Habib
 *
 */
public abstract class SelectedUserObject implements Selectable {

   OpenSimObject object;
   Model model;
   
   public SelectedUserObject(OpenSimObject object, Model model) { 
      this.object = object; 
      this.model = model;
   }
   
   public OpenSimObject getOpenSimObject() {
      return object;
   }

   public String getStatusText() {
      return object.getConcreteClassName() + ":" + object.getName();
   }

    public Model getOwnerModel() {
        return model;
    }

}
