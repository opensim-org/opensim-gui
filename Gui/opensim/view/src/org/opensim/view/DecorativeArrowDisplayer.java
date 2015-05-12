/*
 * Copyright (c)  2005-2008, Stanford University
 * Use of the OpenSim software in source form is permitted provided that the following
 * conditions are met:
 * 	1. The software is used only for non-commercial research and education. It may not
 *     be used in relation to any commercial activity.
 * 	2. The software is not distributed or redistributed.  Software distribution is allowed 
 *     only through https://simtk.org/home/opensim.
 * 	3. Use of the OpenSim software or derivatives must be acknowledged in all publications,
 *      presentations, or documents describing work in which OpenSim or derivatives are used.
 * 	4. Credits to developers may not be removed from executables
 *     created from modifications of the source.
 * 	5. Modifications of source code must retain the above copyright notice, this list of
 *     conditions and the following disclaimer. 
 * 
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 *  SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 *  TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 *  HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR BUSINESS INTERRUPTION) OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 *  WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
