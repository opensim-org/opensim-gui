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

import org.opensim.modeling.*;
import vtk.vtkActor;
import vtk.vtkClipPolyData;
import vtk.vtkCubeSource;
import vtk.vtkPlane;
import vtk.vtkPolyData;

public class DecorativeBrickDisplayer extends DecorativeGeometryDisplayer {
    private DecorativeBrick ag;
    //protected OpenSimObject obj;
    /** 
     * @param ag
     * @param object 
     */
    DecorativeBrickDisplayer(DecorativeBrick ag) {
        this.ag = ag;
        //if (ag.hasUserRef()) setObj(ag.getUserRefAsObject());

     }

    /**
     * Convert DecorativeGeometry object passed in to the corresponding vtk polyhedral representation.
     * Transform is passed in as well since the way it applies to PolyData depends on source
     */
    private vtkPolyData getPolyData(DecorativeBrick ag) {
        vtkCubeSource brick = new vtkCubeSource();
        //System.out.println("Processing cyl (r, l)"+params[0]+","+params[1]);
        Vec3 halfLengths = ag.getHalfLengths();
        brick.SetXLength(halfLengths.get(0)*2);
        brick.SetYLength(halfLengths.get(1)*2);
        brick.SetZLength(halfLengths.get(2)*2);
        
        return brick.GetOutput();
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
        //setXformAndAttributesFromDecorativeGeometry(ag);

    }

    @Override
    vtkActor computeVisuals() {
       updateDisplayFromDecorativeGeometry();
       return this;
    }

    int getBodyId() {
        return ag.getBodyId();
    }
    int getIndexOnBody() {
        return 0;//ag.getIndexOnBody();
    }
}
