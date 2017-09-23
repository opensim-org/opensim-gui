/* -------------------------------------------------------------------------- *
 * OpenSim: GeometryFactory.java                                              *
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
 * GeometryFactory.java
 *
 * Created on July 22, 2010, 11:55 AM
 * Author(s): Ayman Habib
 *
 */

package org.opensim.view;

import vtk.vtkActor;
import vtk.vtkOBJReader;
import vtk.vtkPolyData;
import vtk.vtkPolyDataMapper;
import vtk.vtkSTLReader;
import vtk.vtkXMLPolyDataReader;

/**
 *
 * @author ayman
 */
public class GeometryFactory {

    static vtkPolyData populatePolyDataFromFile(String boneFile, vtkActor boneActor) {
        vtkPolyData poly=null;
          if (boneFile.toLowerCase().endsWith(".vtp")){
              vtkXMLPolyDataReader polyReader = new vtkXMLPolyDataReader();
              polyReader.SetFileName(boneFile);
              poly = polyReader.GetOutput();
              polyReader.GetOutput().ReleaseDataFlagOn();
              //attachPolyDataToActor(poly, boneActor);
          }
          else if (boneFile.toLowerCase().endsWith(".stl")){
              vtkSTLReader polyReader = new vtkSTLReader();
              polyReader.SetFileName(boneFile);
              poly = polyReader.GetOutput();
              polyReader.GetOutput().ReleaseDataFlagOn();
              //attachPolyDataToActor(poly, boneActor);
          }
          else if (boneFile.toLowerCase().endsWith(".obj")){
              vtkOBJReader polyReader = new vtkOBJReader();
              polyReader.SetFileName(boneFile);
              poly = polyReader.GetOutput();
              polyReader.GetOutput().ReleaseDataFlagOn();
              //attachPolyDataToActor(poly, boneActor);
           }
          else
              System.out.println("Unexpected extension for geometry file"+boneFile);
          return poly;
    }
    
}
