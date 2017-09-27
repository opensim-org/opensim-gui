/* -------------------------------------------------------------------------- *
 * OpenSim: DecorativeCylinderDisplayer.java                                  *
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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.view;

import org.opensim.modeling.*;
import vtk.vtkActor;
import vtk.vtkClipPolyData;
import vtk.vtkCylinderSource;
import vtk.vtkPlane;
import vtk.vtkPolyData;
import vtk.vtkTransform;
import vtk.vtkTransformPolyDataFilter;

public class DecorativeCylinderDisplayer extends DecorativeGeometryDisplayer {
    private DecorativeCylinder ag;
    vtkCylinderSource cyl = new vtkCylinderSource();
    //protected OpenSimObject obj;
    /** 
     * Displayer for Wrap Geometry
     * @param ag
     * @param object 
     */
    DecorativeCylinderDisplayer(DecorativeCylinder ag) {
        this.ag = ag.clone();
        //if (ag.hasUserRef()) setObj(ag.getUserRefAsObject());
     }

    /**
     * Convert DecorativeGeometry object passed in to the corresponding vtk polyhedral representation.
     * Transform is passed in as well since the way it applies to PolyData depends on source
     */
    private vtkPolyData getPolyData(DecorativeCylinder ag) {
        //System.out.println("Processing cyl (r, l)"+params[0]+","+params[1]);
        cyl.SetRadius(ag.getRadius());
        cyl.SetHeight(ag.getHalfHeight() * 2);
        cyl.SetResolution(32);
        // Transform vtk brick (Y-axis aligned at origin) to match SIMM's along Z-axis at center
        vtkTransformPolyDataFilter xformOriginDirsFilter = new vtkTransformPolyDataFilter();
        vtkTransform xformOriginDirs = new vtkTransform();
        xformOriginDirsFilter.SetTransform(xformOriginDirs);
        xformOriginDirsFilter.SetInputConnection(cyl.GetOutputPort());
        vtkPolyData full = xformOriginDirsFilter.GetOutput();
        return full;
    }

    /**
     * Based on the array of quadrants, clip the wrap-object brick/ellipsoid 
     *
    public static void setQuadrants(final boolean quadrants[], final vtkCylinderSource brick) {
      if (!quadrants[0]){ 
         brick.SetStartTheta(270.0);
         brick.SetEndTheta(90.0);
      }
      else if (!quadrants[1]){
         brick.SetStartTheta(90.0);
         brick.SetEndTheta(270.0);
      }
      else if (!quadrants[2]){
        brick.SetEndTheta(180.0);
      }
      else if (!quadrants[3]){  
         brick.SetStartTheta(180.0);
      }
      else if (!quadrants[4])   
        brick.SetEndPhi(90.0);
      else if (!quadrants[5])
         brick.SetStartPhi(90.0);
   }
*/
   /** 
    * Clip poly data of Cylinder, torus to proper half per passed in quadrants array
    * only x, y are considered here as they are supported by the kinematics engine
    */
   public static vtkPolyData clipPolyData(boolean[] quadrants, vtkPolyData full) {
      vtkPlane cutPlane = new vtkPlane();
      if (!quadrants[0])
         cutPlane.SetNormal(1.0, 0.0, 0.0);
      else if (!quadrants[1])
         cutPlane.SetNormal(-1.0, 0.0, 0.0);
      else if (!quadrants[2]) 
         cutPlane.SetNormal(0.0, 1.0, 0.0);
      else if (!quadrants[3])
         cutPlane.SetNormal(0.0, -1.0, 0.0);
      else  // do nothing
         return full;
      vtkClipPolyData clipper = new vtkClipPolyData();
      clipper.SetClipFunction(cutPlane);
      clipper.SetInput(full);
      
      return clipper.GetOutput();
   }

    @Override
    void updateDisplayFromDecorativeGeometry() {
        vtkPolyData polyData = getPolyData(ag);
        //updatePropertiesForPolyData(polyData);
        createAndConnectMapper(polyData);
        setXformAndAttributesFromDecorativeGeometry(ag);

    }

    @Override
    vtkActor getVisuals() {
       updateDisplayFromDecorativeGeometry();
       return this;
    }

    @Override
    int getBodyId() {
        return ag.getBodyId();
    }
    @Override
    int getIndexOnBody() {
        return ag.getIndexOnBody();
    }
    
    @Override
    DecorativeGeometry getDecorativeGeometry() {
        return ag;
    }

    @Override
    void updateGeometry(DecorativeGeometry arg) {
        DecorativeCylinder arg0 = (DecorativeCylinder)arg;
        ag.setRadius(arg0.getRadius());
        ag.setHalfHeight(arg0.getHalfHeight());
        updateDisplayFromDecorativeGeometry();
    }
}
