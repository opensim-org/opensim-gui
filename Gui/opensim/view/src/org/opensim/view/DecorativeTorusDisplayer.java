/* -------------------------------------------------------------------------- *
 * OpenSim: DecorativeTorusDisplayer.java                                     *
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

package org.opensim.view;

import org.opensim.modeling.*;
import vtk.vtkActor;
import vtk.vtkClipPolyData;
import vtk.vtkPlane;
import vtk.vtkPolyData;
import vtk.vtkSphereSource;

public class DecorativeTorusDisplayer extends DecorativeGeometryDisplayer {
    private static int RESOLUTION_PHI=32;
    private static int RESOLUTION_THETA=32;
    DecorativeTorus ag;
    vtkSphereSource sphere = new vtkSphereSource();
    //protected OpenSimObject obj;
    /** 
     * Displayer for Wrap Geometry
     * @param ag
     * @param object 
     */
    DecorativeTorusDisplayer(DecorativeTorus ag) {
        this.ag = ag.clone();
        //if (ag.hasUserRef()) setObj(ag.getUserRefAsObject());
     }

    /**
     * Convert DecorativeGeometry object passed in to the corresponding vtk polyhedral representation.
     * Transform is passed in as well since the way it applies to PolyData depends on source
     */
    public vtkPolyData getPolyData() {
        
        //Geometry.GeometryType analyticType = ag.
        boolean quadrants[] = new boolean[6];
        //ag.getQuadrants(quadrants);
        double[] pos = new double[3];
        sphere.LatLongTessellationOn();
        sphere.SetPhiResolution(RESOLUTION_PHI);
        sphere.SetThetaResolution(RESOLUTION_THETA);
        //sphere.SetRadius(ag.getRadius());
        
        return sphere.GetOutput();
    }

    /**
     * Based on the array of quadrants, clip the wrap-object sphere/ellipsoid 
     */
    public static void setQuadrants(final boolean quadrants[], final vtkSphereSource sphere) {
      if (!quadrants[0]){ 
         sphere.SetStartTheta(270.0);
         sphere.SetEndTheta(90.0);
      }
      else if (!quadrants[1]){
         sphere.SetStartTheta(90.0);
         sphere.SetEndTheta(270.0);
      }
      else if (!quadrants[2]){
        sphere.SetEndTheta(180.0);
      }
      else if (!quadrants[3]){  
         sphere.SetStartTheta(180.0);
      }
      else if (!quadrants[4])   
        sphere.SetEndPhi(90.0);
      else if (!quadrants[5])
         sphere.SetStartPhi(90.0);
   }

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
        vtkPolyData polyData = getPolyData();
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
        DecorativeTorus arg0 = (DecorativeTorus) arg;
        //ag.setRadius(arg0.getRadius());
        updateDisplayFromDecorativeGeometry();
    }
}
