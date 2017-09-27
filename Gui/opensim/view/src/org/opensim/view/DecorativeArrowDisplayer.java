/* -------------------------------------------------------------------------- *
 * OpenSim: DecorativeArrowDisplayer.java                                     *
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
import vtk.vtkArrowSource;
import vtk.vtkPlane;
import vtk.vtkPolyData;
import vtk.vtkTransform;
import vtk.vtkTransformPolyDataFilter;

public class DecorativeArrowDisplayer extends DecorativeGeometryDisplayer {
    private DecorativeArrow ag;
    vtkArrowSource arrow = new vtkArrowSource();
    vtkTransformPolyDataFilter xformOriginDirsFilter = new vtkTransformPolyDataFilter();
    vtkTransform xformOriginDirs = new vtkTransform();
    //protected OpenSimObject obj;
    /** 
     * Displayer for Wrap Geometry
     * @param ag
     * @param object 
     */
    DecorativeArrowDisplayer(DecorativeArrow ag) {
        this.ag = ag.clone();
        //if (ag.hasUserRef()) setObj(ag.getUserRefAsObject());
     }

    /**
     * Convert DecorativeGeometry object passed in to the corresponding vtk polyhedral representation.
     * Transform is passed in as well since the way it applies to PolyData depends on source
     */
    private vtkPolyData getPolyData(DecorativeArrow ag) {
        //System.out.println("Processing arrow (r, l)"+params[0]+","+params[1]);
        arrow.SetShaftRadius(ag.getLineThickness());
        //arrow.SetHeight(ag.getHeight());
        Vec3 o = ag.getStartPoint();
        Vec3 d = ag.getEndPoint();
        Vec3 diff = new Vec3(d.get(0)-o.get(0), d.get(1)-o.get(1), d.get(2)-o.get(2));
        UnitVec3 dir = new UnitVec3(diff);
        //arrow.SetCenter(o.get(0), o.get(1), o.get(2));
        //arrow.SetDirection(d.get(0), d.get(1), d.get(2));
        //arrow.CappingOff();
        arrow.SetShaftResolution(32);
        arrow.SetTipResolution(32);
        arrow.SetShaftRadius(ag.getLineThickness());
        // Transform vtk Arrow (X-axis aligned at origin) to match dir
        xformOriginDirs.Translate(o.get(0), o.get(1), o.get(2));
        double cosTheta = dir.get(0);
        double sinTheta = Math.sqrt(1 - cosTheta*cosTheta);
        xformOriginDirs.GetMatrix().SetElement(0, 0, cosTheta);
        xformOriginDirs.GetMatrix().SetElement(0, 1, -sinTheta);
        xformOriginDirs.GetMatrix().SetElement(1, 0, sinTheta);
        xformOriginDirs.GetMatrix().SetElement(1, 1, cosTheta);
        xformOriginDirsFilter.SetTransform(xformOriginDirs);
        xformOriginDirsFilter.SetInputConnection(arrow.GetOutputPort());
        vtkPolyData full = xformOriginDirsFilter.GetOutput();
        return full;
    }

    /**
     * Based on the array of quadrants, clip the wrap-object brick/ellipsoid 
     *
    public static void setQuadrants(final boolean quadrants[], final vtkArrowSource brick) {
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
    * Clip poly data of Arrow, torus to proper half per passed in quadrants array
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
        DecorativeArrow arg0 = (DecorativeArrow)arg;
        ag.setStartPoint(arg0.getStartPoint());
        ag.setEndPoint(arg0.getEndPoint());
        ag.setTipLength(arg0.getTipLength());
        //ag.setBaseRadius(arg0.getBaseRadius());
        updateDisplayFromDecorativeGeometry();
    }
}
