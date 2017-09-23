/* -------------------------------------------------------------------------- *
 * OpenSim: ToolCommon.java                                                   *
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

package org.opensim.tracking;

import javax.swing.JComponent;
import org.opensim.modeling.AbstractProperty;
import org.opensim.modeling.IO;
import org.opensim.modeling.OpenSimObject;

public class ToolCommon {

   public static final String MassFormatStr = "%.3f";
   public static final String TimeFormatStr = "%.3f";
   public static final String CoordinateFormatStr = "%.3f";
   public static final String WeightFormatStr = "%.3f";

    public static void bindProperty(OpenSimObject obj, String propertyName, JComponent comp) { 
       AbstractProperty prop = obj.getPropertyByName(propertyName);
      if(prop!=null) {
         String comment = prop.getComment();
         if(!comment.equals("")) {
            comp.setToolTipText("<html>"+IO.formatText(comment, "", 120, "<br>")+"<br><br>XML property: <b>"+obj.getConcreteClassName()+" > "+propertyName+"</b></html>");
         }
      } else {
         System.out.println("ToolCommon.bindProperty: Could not find property '"+propertyName+"' in object of type '"+obj.getConcreteClassName()+"'");
      }
   }
}
