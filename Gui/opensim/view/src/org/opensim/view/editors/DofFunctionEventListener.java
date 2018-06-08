/* -------------------------------------------------------------------------- *
 * OpenSim: DofFunctionEventListener.java                                     *
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
 * DofFunctionEventListener.java
 *
 * Created on January 10, 2008, 2:16 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.view.editors;

import org.opensim.modeling.Coordinate;
import org.opensim.modeling.TransformAxis;
import org.opensim.modeling.Function;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimContext;
import org.opensim.modeling.OpenSimObject;
import org.opensim.view.SingleModelGuiElements;
import org.opensim.view.functionEditor.FunctionEvent;
import org.opensim.view.functionEditor.FunctionEventListener;
import org.opensim.view.functionEditor.FunctionModifiedEvent;
import org.opensim.view.functionEditor.FunctionReplacedEvent;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;

/**
 *
 * @author Peter Loan
 */
public class DofFunctionEventListener implements FunctionEventListener {

   /**
    * Creates a new instance of DofFunctionEventListener
    */
   public DofFunctionEventListener() {
   }

   public void handleFunctionEvent(FunctionEvent event) {
      OpenSimObject object = event.getObject();
      TransformAxis dof = TransformAxis.safeDownCast(object);

      if (dof != null) {
         Model model = OpenSimDB.getInstance().getCurrentModel();
         SingleModelGuiElements guiElem = OpenSimDB.getInstance().getModelGuiElements(model);
         OpenSimContext openSimContext = OpenSimDB.getInstance().getContext(model);

         if (event instanceof FunctionReplacedEvent) {
            FunctionReplacedEvent fre = (FunctionReplacedEvent) event;
            Function oldFunction = fre.getFunction();
            Function newFunction = fre.getReplacementFunction();
            if (Function.getCPtr(oldFunction) != Function.getCPtr(newFunction)) {
               openSimContext.replaceTransformAxisFunction(dof, newFunction);
               ViewDB.getInstance().updateModelDisplayNoRepaint(model, false, true);
               ViewDB.getInstance().renderAll();
               guiElem.setUnsavedChangesFlag(true);
            }
         } else if (event instanceof FunctionModifiedEvent) {
             
            if (dof.getCoordinateNamesInArray().getSize() > 0) {
               //TODO: not a good way to force a joint recalculation!!
               // TODO: for now, deal only with the first coordinate.
               String coordName = dof.getCoordinateNamesInArray().getitem(0);
               Coordinate coord = dof.getJoint().get_coordinates(0);
               openSimContext.setValue(coord, openSimContext.getValue(coord));
               ViewDB.getInstance().updateModelDisplayNoRepaint(model, false, true);
               ViewDB.getInstance().renderAll();
               guiElem.setUnsavedChangesFlag(true);
            }
         }
      }
   }
}
