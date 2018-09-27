/* -------------------------------------------------------------------------- *
 * OpenSim: MusclePointFunctionEventListener.java                             *
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
 * MusclePointFunctionEventListener.java
 *
 * Created on January 10, 2008, 4:51 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.view.editors;

import org.opensim.modeling.OpenSimContext;
import org.opensim.view.functionEditor.FunctionEvent;
import org.opensim.view.functionEditor.FunctionEventListener;
import org.opensim.view.functionEditor.FunctionModifiedEvent;
import org.opensim.view.functionEditor.FunctionReplacedEvent;
import org.opensim.modeling.Muscle;
import org.opensim.modeling.Function;
import org.opensim.modeling.GeometryPath;
import org.opensim.modeling.MovingPathPoint;
import org.opensim.modeling.OpenSimObject;
import org.opensim.view.pub.OpenSimDB;

/**
 *
 * @author Peter Loan
 */
public class MusclePointFunctionEventListener implements FunctionEventListener {
   
   /** Creates a new instance of MusclePointFunctionEventListener */
   public MusclePointFunctionEventListener() {
   }

   public void handleFunctionEvent(FunctionEvent event) {
      OpenSimObject object = event.getObject();

      MovingPathPoint mmp = MovingPathPoint.safeDownCast(object);
      OpenSimContext context=OpenSimDB.getInstance().getContext(mmp.getBody().getModel());
      if (mmp != null) {
         if (event instanceof FunctionReplacedEvent) {
            FunctionReplacedEvent fre = (FunctionReplacedEvent) event;
            Function oldFunction = fre.getFunction();
            Function newFunction = fre.getReplacementFunction();
            if (Function.getCPtr(oldFunction) != Function.getCPtr(newFunction)) {
               if (Function.getCPtr(oldFunction) == Function.getCPtr(mmp.get_x_location()))
                  context.setXFunction(mmp, newFunction);
               else if (Function.getCPtr(oldFunction) == Function.getCPtr(mmp.get_y_location()))
                  context.setYFunction(mmp, newFunction);
               else if (Function.getCPtr(oldFunction) == Function.getCPtr(mmp.get_z_location()))
                  context.setZFunction(mmp, newFunction);
            }
         } else if (event instanceof FunctionModifiedEvent) {
            FunctionModifiedEvent fme = (FunctionModifiedEvent) event;
            Function function = fme.getFunction();
            if (Function.getCPtr(function) == Function.getCPtr(mmp.get_x_location()))
               context.setXFunction(mmp, function);
            else if (Function.getCPtr(function) == Function.getCPtr(mmp.get_y_location()))
               context.setYFunction(mmp, function);
            else if (Function.getCPtr(function) == Function.getCPtr(mmp.get_z_location()))
               context.setZFunction(mmp, function);
         }
      }
   }
}
