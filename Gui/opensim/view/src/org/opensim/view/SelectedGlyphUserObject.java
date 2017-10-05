/* -------------------------------------------------------------------------- *
 * OpenSim: SelectedGlyphUserObject.java                                      *
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

import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimObject;
import org.opensim.view.pub.ViewDB;
import vtk.vtkCaptionActor2D;

/**
 *
 * @author Ayman Habib
 *
 */
public class SelectedGlyphUserObject extends SelectedUserObject {

   OpenSimvtkGlyphCloud cloud;
   
   
   public SelectedGlyphUserObject(OpenSimObject object, Model model, OpenSimvtkGlyphCloud cloud) { 
      super(object, model); 
      this.cloud = cloud;
   }

   public void markSelected(boolean highlight)
   {
         int id = cloud.getPointId(object);
         if(id>=0) { // just to be safe
            cloud.setSelected(id, highlight);
            cloud.setModified();
         }
   }

   public static double[] getGlyphPointBounds(OpenSimvtkGlyphCloud cloud, SingleModelVisuals visuals, OpenSimObject object) {
      double pointSize = 0.05;
      int id = cloud.getPointId(object);
      if(id<0) return null; // just to be safe
      double[] location = new double[3];
      cloud.getLocation(id, location);
      visuals.transformModelToWorldPoint(location); // Transform to world space
      return new double[]{location[0]-pointSize,location[0]+pointSize,
                          location[1]-pointSize,location[1]+pointSize,
                          location[2]-pointSize,location[2]+pointSize};
   }
   
   public double[] getBounds() {
       
       return SelectedObject.getGlyphPointBounds(cloud,
               ViewDB.getInstance().getModelVisuals(model), object);
   }

}
