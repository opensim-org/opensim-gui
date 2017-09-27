/* -------------------------------------------------------------------------- *
 * OpenSim: FunctionEvent.java                                                *
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
 * FunctionEvent.java
 *
 * Created on January 10, 2008, 4:51 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.view.functionEditor;

import java.util.EventObject;
import org.opensim.modeling.Function;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimObject;

/**
 * A change event that encapsulates information about a change to a function.
 */
public class FunctionEvent extends EventObject {
   
   private Model model;
   private OpenSimObject object;
   private Function function;

   public FunctionEvent(Model model, OpenSimObject object, Function function) {
      super(function);
      this.model = model;
      this.object = object;
      this.function = function;
   }
   
   public Model getModel() {
      return this.model;
   }
   
   public OpenSimObject getObject() {
      return this.object;
   }
   
   public Function getFunction() {
      return this.function;
   }
}
