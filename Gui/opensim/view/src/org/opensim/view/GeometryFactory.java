/*
 * GeometryFactory.java
 *
 * Created on July 22, 2010, 11:55 AM
 * Author(s): Ayman Habib
 *
 * Copyright (c)  2005-2010, Stanford University, Ayman Habib
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

    static void populateActorFromFile(String boneFile, vtkActor boneActor) {
        vtkPolyData poly=null;
          if (boneFile.toLowerCase().endsWith(".vtp")){
              vtkXMLPolyDataReader polyReader = new vtkXMLPolyDataReader();
              polyReader.SetFileName(boneFile);
              poly = polyReader.GetOutput();
              polyReader.GetOutput().ReleaseDataFlagOn();
              attachPolyDataToActor(poly, boneActor);
          }
          else if (boneFile.toLowerCase().endsWith(".stl")){
              vtkSTLReader polyReader = new vtkSTLReader();
              polyReader.SetFileName(boneFile);
              poly = polyReader.GetOutput();
              polyReader.GetOutput().ReleaseDataFlagOn();
              attachPolyDataToActor(poly, boneActor);
          }
          else if (boneFile.toLowerCase().endsWith(".obj")){
              vtkOBJReader polyReader = new vtkOBJReader();
              polyReader.SetFileName(boneFile);
              poly = polyReader.GetOutput();
              polyReader.GetOutput().ReleaseDataFlagOn();
              attachPolyDataToActor(poly, boneActor);
           }
          else
              System.out.println("Unexpected extension for geometry file"+boneFile);
    }

    private static void attachPolyDataToActor(vtkPolyData polyData, vtkActor boneActor) {
        vtkPolyDataMapper boneMapper = new vtkPolyDataMapper();
        boneActor.SetMapper(boneMapper);
        // Create polyData and append it to one common polyData object
        boneMapper.SetInput(polyData);
    }
    
}
