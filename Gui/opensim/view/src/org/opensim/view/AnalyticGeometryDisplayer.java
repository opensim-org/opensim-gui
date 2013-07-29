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
 */
package org.opensim.view;

import java.awt.Color;
import org.opensim.modeling.*;
import vtk.vtkActor;
import vtk.vtkClipPolyData;
import vtk.vtkCylinderSource;
import vtk.vtkParametricFunctionSource;
import vtk.vtkParametricTorus;
import vtk.vtkPlane;
import vtk.vtkPolyData;
import vtk.vtkPolyDataMapper;
import vtk.vtkSphereSource;
import vtk.vtkTransform;
import vtk.vtkTransformPolyDataFilter;

public class AnalyticGeometryDisplayer extends ObjectDisplayer {
    private static int RESOLUTION_PHI=32;
    private static int RESOLUTION_THETA=32;
    private static int CYL_RESOLUTION=32;
    private AnalyticGeometry ag;
    //protected OpenSimObject obj;
    /** 
     * Displayer for Wrap Geometry
     * @param ag
     * @param object 
     */
    AnalyticGeometryDisplayer(AnalyticGeometry ag, OpenSimObject object) {
        super(object);
        this.ag = ag;
        //this.obj = object;
        updateFromProperties();
     }

    /**
     * Convert AnalyticGeometry object passed in to the corresponding vtk polyhedral representation.
     * Transform is passed in as well since the way it applies to PolyData depends on source
     */
    public static vtkPolyData getPolyData(AnalyticGeometry ag) {
       Geometry.GeometryType analyticType = ag.getShape();
       boolean quadrants[] = new boolean[6];
       ag.getQuadrants(quadrants);
       double[] pos = new double[3];
       if (analyticType == Geometry.GeometryType.Sphere){
          vtkSphereSource sphere = new vtkSphereSource();
          AnalyticSphere typed = AnalyticSphere.dynamic_cast(ag);
          sphere.LatLongTessellationOn(); 
          sphere.SetPhiResolution(RESOLUTION_PHI);
          sphere.SetThetaResolution(RESOLUTION_THETA);
          sphere.SetRadius(typed.getRadius());
          if (ag.isPiece())
            setQuadrants(quadrants, sphere);
          return sphere.GetOutput();
       }
       else if (analyticType == Geometry.GeometryType.Ellipsoid){
          vtkSphereSource sphere = new vtkSphereSource();
          sphere.LatLongTessellationOn(); 
          sphere.SetPhiResolution(RESOLUTION_PHI);
          sphere.SetThetaResolution(RESOLUTION_THETA);
          sphere.SetRadius(1.0);
          if (ag.isPiece())
            setQuadrants(quadrants, sphere);
          
          AnalyticEllipsoid typed = AnalyticEllipsoid.dynamic_cast(ag);
          // Make a stretching transform to take the sphere into an ellipsoid
          vtkTransformPolyDataFilter stretch = new vtkTransformPolyDataFilter();
          vtkTransform stretchSphereToEllipsoid = new vtkTransform();
          double[] params = new double[]{1.0, 1.0, 1.0};
          typed.getEllipsoidParams(params);
          stretchSphereToEllipsoid.Scale(params[0], params[1], params[2]);
          stretch.SetTransform(stretchSphereToEllipsoid);
          stretch.SetInputConnection(sphere.GetOutputPort());
          return stretch.GetOutput();
       }
       else if (analyticType == Geometry.GeometryType.Cylinder){
          vtkCylinderSource cylinder = new vtkCylinderSource();
          cylinder.SetResolution(CYL_RESOLUTION);
          AnalyticCylinder typed = AnalyticCylinder.dynamic_cast(ag);
          double[] params = new double[]{1.0, 1.0};
          typed.getCylinderParams(params);
          //System.out.println("Processing cyl (r, l)"+params[0]+","+params[1]);
          cylinder.SetRadius(params[0]);
          cylinder.SetHeight(params[1]);
          // Transform vtk cylinder (Y-axis aligned at origin) to match SIMM's along Z-axis at center
          vtkTransformPolyDataFilter xformOriginDirsFilter = new vtkTransformPolyDataFilter();
          vtkTransform xformOriginDirs = new vtkTransform();
          xformOriginDirs.RotateX(90);
          xformOriginDirsFilter.SetTransform(xformOriginDirs);
          xformOriginDirsFilter.SetInputConnection(cylinder.GetOutputPort());
          vtkPolyData full = xformOriginDirsFilter.GetOutput();
          if (ag.isPiece())
            return clipPolyData(quadrants, full);
          else
             return full;
       }
       else if (analyticType == Geometry.GeometryType.Torus){
          vtkParametricTorus torus=new vtkParametricTorus();
          vtkParametricFunctionSource torusSource = new vtkParametricFunctionSource();
          torusSource.SetParametricFunction(torus);
          vtkPolyDataMapper torusMapper=new vtkPolyDataMapper();
          torusMapper.SetInputConnection(torusSource.GetOutputPort());

          AnalyticTorus typed = AnalyticTorus.dynamic_cast(ag);
          double[] params = new double[]{1.0, 1.0};
          typed.getTorusParams(params);
          //System.out.println("Processing torus (r1, r2)"+params[0]+","+params[1]);
          torus.SetRingRadius(params[1]+params[0]);
          torus.SetCrossSectionRadius(params[0]);
          vtkPolyData full = torusSource.GetOutput();
          if (ag.isPiece())
            return clipPolyData(quadrants, full);
          else
             return full;
       }
       return null;
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
    void updateFromProperties() {
        ContactSphere cs = ContactSphere.safeDownCast(getObj());
        if (cs != null){
            double newRadius = cs.getRadius();
            AnalyticSphere anSphere = AnalyticSphere.dynamic_cast(ag);
            anSphere.setSphereRadius(newRadius);
        }
        vtkPolyData polyData = getPolyData(ag);
        updatePropertiesForPolyData(polyData);
    }
}
