/* -------------------------------------------------------------------------- *
 * OpenSim: SelectedObject.java                                               *
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
 * SelectedObject
 * Author(s): Eran Guendelman
 */
package org.opensim.view;

import org.opensim.modeling.Body;
import org.opensim.modeling.Component;
import org.opensim.modeling.GeometryPath;
import org.opensim.modeling.Marker;
import org.opensim.modeling.Model;
import org.opensim.modeling.ModelComponent;
import org.opensim.modeling.OpenSimContext;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.PathActuator;
import org.opensim.modeling.PathPoint;
import org.opensim.modeling.PhysicalFrame;
import org.opensim.modeling.WrapObject;
import org.opensim.view.experimentaldata.ExperimentalDataObject;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;
import vtk.vtkActor;
import vtk.vtkCaptionActor2D;
import vtk.vtkProp3D;
import vtk.vtkProp3DCollection;


/**
 *
 * @author Eran Guendelman
 *
 */
public class SelectedObject implements Selectable {
   public static final double defaultSelectedColor[] = {0.8, 0.8, 0.0};

   OpenSimObject object;

   public SelectedObject(OpenSimObject object) { 
      this.object = object; 
   }

   public OpenSimObject getOpenSimObject() {
      return object;
   }

   public String getStatusText() {
      return object.getConcreteClassName() + ":" + object.getName();
   }

   private Model getModel(ModelComponent mc) { return mc.getModel(); }
 
   public Model getOwnerModel()
   {
      // Handle ExperimentalDataObjects first as they handle their getModel thier own way
      if (object instanceof ExperimentalDataObject)
          return (((ExperimentalDataObject)object).getModel());
      
      ModelComponent mc = ModelComponent.safeDownCast(object);
      if (mc != null) return mc.getModel();
      Component comp = Component.safeDownCast(object);
      if (comp == null)
          return null;
      ModelComponent parent = ModelComponent.safeDownCast(comp.getOwner());
      while (parent == null){
          comp = comp.getOwner();
          parent = ModelComponent.safeDownCast(comp);
      }
      return parent.getModel();
   }
}
