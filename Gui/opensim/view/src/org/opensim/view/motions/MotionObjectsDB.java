/* -------------------------------------------------------------------------- *
 * OpenSim: MotionObjectsDB.java                                              *
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
 *
 * MotionObjectsDB
 * Author(s): Ayman Habib
 */
package org.opensim.view.motions;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import org.opensim.view.pub.ViewDB;
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
      if (ViewDB.isVtkGraphicsAvailable()){
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
