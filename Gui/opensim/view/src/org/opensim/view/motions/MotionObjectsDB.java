/*
 *
 * MotionObjectsDB
 * Author(s): Ayman Habib
 * Copyright (c)  2005-2006, Stanford University, Ayman Habib
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
package org.opensim.view.motions;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import vtk.vtkArrowSource;
import vtk.vtkCubeSource;
import vtk.vtkPolyData;
import vtk.vtkSphereSource;
import vtk.vtkTransform;
import vtk.vtkTransformPolyDataFilter;

/**
 *
 * @author Ayman. Collection of built in motion objects with room for growth through 
 * registering new object names, types.
 */
public class MotionObjectsDB {
   
   /** Creates a new instance of MotionObjectsDB */
   static MotionObjectsDB instance;
   // Map model to an ArrayList of Motions linked with it
   static Hashtable<String, vtkPolyData> motionObjectsMap =
           new Hashtable<String, vtkPolyData>(10);
   
   /** Creates a new instance of MotionsDB */
   private MotionObjectsDB() {
      // default motion objects
      // ball
      motionObjectsMap.put("ball", createBall());
      // marker
      motionObjectsMap.put("marker", createMarker());
      // musclePoint
      motionObjectsMap.put("musclepoint", createMusclePoint());
      // force
      motionObjectsMap.put("arrow", createArrow());
      // another marker
      motionObjectsMap.put("box", creatCube());
      // reverse arrow
      motionObjectsMap.put("arrow_in", createArrowIn());
      
   }
   
   public static synchronized MotionObjectsDB getInstance() {
      if (instance == null) {
         instance = new MotionObjectsDB();
         
      }
      return instance;
   }

   String[] getAvailableNames() {
      Set<String> keys=motionObjectsMap.keySet();
      String[] names = new String[keys.size()];
      Iterator<String> keyIterator = keys.iterator();
      int i=0;
      while(keyIterator.hasNext())
         names[i++]=keyIterator.next();
      
      return names;
   }
   
   void addNewMotionObject(String newObjectName, vtkPolyData newObjectRep) {
      motionObjectsMap.put(newObjectName, newObjectRep);
   }

   private vtkPolyData createBall() {
      vtkSphereSource ball = new vtkSphereSource();
      ball.SetRadius(1.0);
      ball.SetCenter(0., 0., 0.);
      ball.SetThetaResolution(32);
      ball.SetPhiResolution(32);
      return ball.GetOutput();
   }

   private vtkPolyData createMarker() {
      vtkSphereSource marker=new vtkSphereSource();
      marker.SetRadius(.01);
      marker.SetCenter(0., 0., 0.);
      return marker.GetOutput();
   }
   
   private vtkPolyData createMusclePoint() {
      vtkSphereSource mp=new vtkSphereSource();
      mp.SetRadius(1.); // will be scaled by preference
      mp.SetCenter(0., 0., 0.);
      mp.SetThetaResolution(32);
      mp.SetPhiResolution(32);
      return mp.GetOutput();
   }

   private vtkPolyData createArrow() {
      vtkArrowSource force=new vtkArrowSource();
      force.SetShaftRadius(0.02);
      force.SetTipLength(0.2);
      return force.GetOutput();
   }

   private vtkPolyData creatCube() {
      vtkCubeSource marker=new vtkCubeSource();
      marker.SetXLength(.01);
      marker.SetYLength(.01);
      marker.SetZLength(.01);
      marker.SetCenter(0., 0., 0.);
      return marker.GetOutput();
   }

   public vtkPolyData getShape(String shapeName) {
        return motionObjectsMap.get(shapeName);
    }

    private vtkPolyData createArrowIn() {
      vtkArrowSource arr=new vtkArrowSource();
      arr.SetShaftRadius(0.02);
      arr.SetTipLength(0.2);
      vtkTransformPolyDataFilter flipFilter = new vtkTransformPolyDataFilter();
      vtkTransform flipTransform = new vtkTransform();
      //flipTransform.RotateZ(180);
      flipTransform.Translate(-1.0, 0., 0.);
      flipFilter.SetTransform(flipTransform);
      flipFilter.SetInputConnection(arr.GetOutputPort());
      return flipFilter.GetOutput();
   }
   
}
