/*
 * OpenSimvtkGlyphCloud.java
 *
 * Created on January 25, 2007, 9:39 AM
 *
 * Copyright (c)  2006, Stanford University and Ayman Habib. All rights reserved.
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

import java.util.Stack;
import vtk.vtkActor;
import vtk.vtkColorTransferFunction;
import vtk.vtkCylinderSource;
import vtk.vtkFloatArray;
import vtk.vtkPoints;
import vtk.vtkPolyData;
import vtk.vtkPolyDataMapper;
import vtk.vtkProperty;
import vtk.vtkTensorGlyph;

/**
 *
 * @author Ayman Habib. An object representing a cloud of visual objects used to display markers, muscle points 
 * efficiently.
 */
public class OpenSimvtkOrientedGlyphCloud {    // Assume same shape
    
    private vtkPoints           pointCloud = new vtkPoints(); // object centers
    private vtkPolyData         pointPolyData = new vtkPolyData();
    private vtkPolyData         shape=new vtkCylinderSource().GetOutput();
    private vtkActor            actor = new vtkActor();
    private vtkPolyDataMapper   mapper = new vtkPolyDataMapper();
    private vtkTensorGlyph      glyph= new vtkTensorGlyph();
    private vtkFloatArray       tensorData = new vtkFloatArray();

    private vtkFloatArray       scalarData = null;
    private vtkColorTransferFunction  lookupTable = null;

    private Stack<Integer> freeList = new Stack<Integer>();
    
    /**
    * Creates a new instance of OpenSimvtkGlyphCloud
    */
    public OpenSimvtkOrientedGlyphCloud() {
        tensorData.SetNumberOfTuples(1);
        tensorData.SetNumberOfComponents(9);
        pointPolyData.SetPoints(pointCloud);
        pointPolyData.GetPointData().SetTensors(tensorData);
        glyph.ExtractEigenvaluesOff();
        glyph.ThreeGlyphsOff();
        glyph.SymmetricOff();
    }
    
    public void setShape(vtkPolyData rep) {
        shape = rep;
    }
    public void setDisplayProperties(vtkProperty prop) {
        actor.SetProperty(prop);
    }
    public void setColor(double[] color) {
        actor.GetProperty().SetColor(color);
    }
    public void setOpacity(double newOpacity) {
        actor.GetProperty().SetOpacity(newOpacity);
    }

   private void initializeLookupTableIfNecessary() {
      if(lookupTable==null) {
         scalarData = new vtkFloatArray();
         scalarData.SetNumberOfTuples(1);
         scalarData.SetNumberOfComponents(1);
         pointPolyData.GetPointData().SetScalars(scalarData);
         glyph.SetColorGlyphs(1);
         glyph.SetColorModeToScalars();
         lookupTable = new vtkColorTransferFunction();
         mapper.SetLookupTable(lookupTable);
      }
   }

   public void setColorRange(double[] color0, double[] color1) {
      initializeLookupTableIfNecessary();
      lookupTable.AddRGBPoint(0.0, color0[0], color0[1], color0[2]);
      lookupTable.AddRGBPoint(1.0, color1[0], color1[1], color1[2]);
   }
/*
   public void set3ColorRange(double[] color0, double[] color1, double[] color2) {
      initializeLookupTableIfNecessary();
      lookupTable.AddRGBPoint(0.0, color0[0], color0[1], color0[2]);
      lookupTable.AddRGBPoint(0.5, color1[0], color1[1], color1[2]);
      lookupTable.AddRGBPoint(1.0, color2[0], color2[1], color2[2]);
   }
   */
    public vtkActor getVtkActor() {
        glyph.SetSource(shape);
        glyph.SetInput(pointPolyData);
        mapper.SetInput(glyph.GetOutput());
        actor.SetMapper(mapper);
        
        return actor;
    }
    
    public void setLocation(int index, double x, double y, double z) {
        pointCloud.SetPoint(index, x, y, z);
    }

    public void setLocation(int index, double[] point) {
        pointCloud.SetPoint(index, point[0], point[1], point[2]);
    }
    
    synchronized public void setTensorDataAtLocation(int index, double xx, double xy, double xz,
            double yx, double yy, double yz,
            double zx, double zy, double zz) {
        tensorData.SetTuple9(index, xx, xy, xz, yx, yy, yz, zx, zy, zz);
    }
    
    synchronized public void setScalarDataAtLocation(int index, double value) {
       if(scalarData!=null) scalarData.SetTuple1(index, value);
    }

   public vtkFloatArray getTensorData() {
      return tensorData;
   }

   public void setTensorData(vtkFloatArray tensorData) {
      this.tensorData = tensorData;
   }

   public void setModified() {
      pointPolyData.Modified();	//Enough to mark the polyData object as modified to trigger re-execution of the pipeline.
   }

    public void setPickable(boolean pickable) {
       if (pickable) {
          actor.SetPickable(1);
       } else {
          actor.SetPickable(0);
       }
    }

   /////////////////////////////////////////////////////////////////////////////
   // Add/Remove locations
   /////////////////////////////////////////////////////////////////////////////

   public int addLocation() {
      return addLocation(0.,0.,0.);
   }

   public int addLocation(double[] newPoint) {
      return addLocation(newPoint[0],newPoint[1],newPoint[2]);
   }

   public int addLocation(double px, double py, double pz) {
      int idx;
      if(!freeList.empty()) { // reuse existing index that was removed earlier
         idx = freeList.pop().intValue();
         show(idx);
      } else {
         idx = pointCloud.InsertNextPoint(px, py, pz);
         tensorData.InsertTuple9(idx, 1., 0., 0., 0., 1., 0., 0., 0., 1.);
         if(scalarData!=null) scalarData.InsertTuple1(idx, 0.);
      }
      return idx;
   }

   /*
    * Remove the specified index from view. 
    * There should be a better way to do this but vtkPoints doesn't have an interface
    * to remove points AFAIK !!
    */
   void remove(int index) {
      freeList.push(index);
      hide(index);
   }

   /////////////////////////////////////////////////////////////////////////////
   // Show/Hide points
   /////////////////////////////////////////////////////////////////////////////
   
   public void show(int index) {
      tensorData.SetTuple9(index, 1., 0., 0., 0., 1., 0., 0., 0., 0.);
   }

   public void hide(int index) {
      tensorData.SetTuple9(index, 0., 0., 0., 0., 0., 0., 0., 0., 0.);
   }
}
