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

import java.awt.Color;
import java.util.HashMap;
import java.util.Stack;
import org.opensim.modeling.OpenSimObject;
import org.opensim.view.motions.MotionObjectsDB;
import vtk.vtkActor;
import vtk.vtkColorTransferFunction;
import vtk.vtkDataArray;
import vtk.vtkFloatArray;
import vtk.vtkGlyph3D;
import vtk.vtkIdList;
import vtk.vtkPoints;
import vtk.vtkPolyData;
import vtk.vtkPolyDataMapper;
import vtk.vtkProperty;
import vtk.vtkSphereSource;

/**
 *
 * @author Ayman Habib. An object representing a cloud of visual objects used to display markers, muscle points 
 * efficiently.
 * For markers and muscle points
 */
public class OpenSimvtkGlyphCloud {    // Assume same shape
    
    private vtkPoints           pointCloud = new vtkPoints(); // object centers
    private vtkPolyData         pointPolyData = new vtkPolyData();
    private vtkPolyData         shape=new vtkSphereSource().GetOutput();
    private vtkActor            actor = new vtkActor();
    private vtkPolyDataMapper   mapper = new vtkPolyDataMapper();
    private vtkGlyph3D          glyph= new vtkGlyph3D();
    private vtkFloatArray       lineNormals = null;
    // vectorData is used primarily to "hide" objects, by setting the data to 0,0,0
    // a muscle point is hidden otherwise 1,1,1 except for forces that are really scaled uniformely by mag
    private vtkFloatArray       vectorData = new vtkFloatArray();
    private vtkFloatArray       scalarData = null;
    private vtkColorTransferFunction  lookupTable = null;
    private HashMap<OpenSimObject,Integer> mapObjectIdsToPointIds = new HashMap<OpenSimObject,Integer>(100); // maps to -1 for invalid objects
    private HashMap<Integer,OpenSimObject> mapPointIdsToObjectIds = new HashMap<Integer,OpenSimObject>(100);

    private Stack<Integer> freeList = new Stack<Integer>();
    private String name;
    private String shapeName;
    /**
    * Creates a new instance of OpenSimvtkGlyphCloud.
    * if createScalars is true then obbjects are colored based on scalarValues
    * otherwise it uses Vector magnitude.
    */
    public OpenSimvtkGlyphCloud(boolean createNormals) {
        pointPolyData.SetPoints(pointCloud);
        // Always use vector data because that's how we scale things to make them disappear
        vectorData.SetNumberOfTuples(1);
        vectorData.SetNumberOfComponents(3);
        pointPolyData.GetPointData().SetVectors(vectorData);
        if (createNormals) {
           lineNormals = new vtkFloatArray();
           lineNormals.SetNumberOfTuples(1);
           lineNormals.SetNumberOfComponents(3);
           pointPolyData.GetPointData().SetNormals(lineNormals);
        }
        glyph.GeneratePointIdsOn();
        glyph.SetScaleModeToDataScalingOff(); // So hide/show behavior is disabled by default
    }
    
    private void setShape(vtkPolyData rep) {
        shape = rep;
    }
    public void setShapeName(String name) {
            shapeName = name; // Cache shape name for later editing
            setShape(MotionObjectsDB.getInstance().getShape(name));
            setModified();
        
    }
    public void setDisplayProperties(vtkProperty prop) {
        actor.SetProperty(prop);
    }

   // Single color (doesn't support showing selected/unselected)
   public void setColor(double[] color) {
      actor.GetProperty().SetColor(color);
      setModified();
   }

   private void initializeLookupTableIfNecessary() {
      if(lookupTable==null) {
         scalarData = new vtkFloatArray();
         scalarData.SetNumberOfTuples(1);
         scalarData.SetNumberOfComponents(1);
         pointPolyData.GetPointData().SetScalars(scalarData);
         glyph.SetColorModeToColorByScalar();
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
   public void setSelectedColor(double[] color) {
      initializeLookupTableIfNecessary();
      lookupTable.AddRGBPoint(-1.0, color[0], color[1], color[2]);
   }

   public void setSelected(int index, boolean selected) {
      if(scalarData!=null && lookupTable!=null) scalarData.SetTuple1(index, selected ? -1.0 : 0.0);
   }

   public boolean getSelected(int index) {
      return scalarData!=null && lookupTable!=null && scalarData.GetTuple1(index)==-1.0;
   }

    public void setOpacity(double newOpacity) {
        actor.GetProperty().SetOpacity(newOpacity);
    }
    
    public vtkActor getVtkActor() {
        glyph.SetSource(shape);
        glyph.SetInput(pointPolyData);
        mapper.SetInput(glyph.GetOutput());
        actor.SetMapper(mapper);
        return actor;
    }

    public void setPickable(boolean pickable) {
       if (pickable) {
          actor.SetPickable(1);
       } else {
          actor.SetPickable(0);
       }
    }

    public void setLocation(int index, double x, double y, double z) {
        pointCloud.SetPoint(index, x, y, z);
    }

    public void setLocation(int index, double[] point) {
        pointCloud.SetPoint(index, point[0], point[1], point[2]);
    }

    public void getLocation(int index, double[] point) {
       pointCloud.GetPoint(index, point);
    }

    public void setNormalAtLocation(int index, double x, double y, double z) {
        if (lineNormals != null) lineNormals.SetTuple3(index, x, y, z);
    }
    
    public void setVectorDataAtLocation(int index, double x, double y, double z) {
        vectorData.SetTuple3(index, x, y, z);
    }

    public void setScalarDataAtLocation(int index, double value) {
       if(scalarData!=null) scalarData.SetTuple1(index, value);
    }
   
    // Used e.g. for the ground reaction forces, it uses the normals to orient the GRF "arrow", and the normal magnitude is used to scale the GRF "arrow"
    public void orientByNormalAndScaleByVector() {
      glyph.SetVectorModeToUseNormal(); 
      glyph.SetScaleModeToScaleByVector(); // scales by vector *magnitude*
    }

    public void setScaleFactor(double d) {
        glyph.SetScaleFactor(d);
        setModified();
    }
   
    public double getScaleFactor() {
        return glyph.GetScaleFactor();
    }
   public void setModified() {
      pointPolyData.Modified();
      pointCloud.Modified();
   }

   // Used to enable hide/show behavior
   public void scaleByVectorComponents() {
      glyph.SetScaleModeToScaleByVectorComponents();
   }

   /////////////////////////////////////////////////////////////////////////////
   // Add/Remove locations
   /////////////////////////////////////////////////////////////////////////////

   // A point associated with an object
   public int addLocation(OpenSimObject obj) {
      int idx = addLocation();
      mapObjectIdsToPointIds.put(obj, idx);
      mapPointIdsToObjectIds.put(idx, obj);
      return idx;
   }

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
         vectorData.InsertTuple3(idx, 1., 1., 1.);
         if (lineNormals != null) lineNormals.InsertTuple3(idx, 0., 0., 0.);
         if (scalarData != null) scalarData.InsertTuple1(idx, 0.);
      }
      return idx;
   }

   // This works as long as you've associated scaling with the vector channel
   void remove(int index) {
      freeList.push(index);
      hide(index);
   }

   /////////////////////////////////////////////////////////////////////////////
   // Show/Hide points
   /////////////////////////////////////////////////////////////////////////////
   
   public void show(int index) {
      if (lineNormals!= null) 
          setNormalAtLocation(index, 1., 1., 1.);
      setVectorDataAtLocation(index, 1., 1., 1.);
   }

   public void hide(int index) {
       if (lineNormals!= null) 
          setNormalAtLocation(index, 0., 0., 0.);
        setVectorDataAtLocation(index, 0., 0., 0.);
   }

   /////////////////////////////////////////////////////////////////////////////
   // Mapping between picked id's, point id's, and objects
   /////////////////////////////////////////////////////////////////////////////

    public OpenSimObject getPickedObject(int pickedId) {
       glyph.GetOutput().BuildCells();
       vtkIdList ids = new vtkIdList();
       glyph.GetOutput().GetCellPoints(pickedId, ids);
        vtkDataArray inputIds = 
            glyph.GetOutput().GetPointData().GetArray("InputPointIds");
        int inputId = (int)inputIds.GetTuple1(ids.GetId(0));
        //System.out.println("GlyphCloud: pickedId="+pickedId+"  inputId="+inputId+"  ids="+ids);
        //for(int i=0;i<(int)ids.GetNumberOfIds();i++) {
           //System.out.println("["+i+"] = "+(int)ids.GetId(i)+" --> "+(int)inputIds.GetTuple1(ids.GetId(i)));
        //}
        return mapPointIdsToObjectIds.get(inputId);
    }
 
    public int getPointId(OpenSimObject object) {
       return mapObjectIdsToPointIds.get(object);
   }

   public void setObjectAtPointId(int id, OpenSimObject obj) {
      // To be safe, get rid of any stale mappings
      if(obj!=null) {
         Integer oldId = mapObjectIdsToPointIds.get(obj);
         if(oldId!=null && (int)oldId!=id) mapPointIdsToObjectIds.put(oldId,null);
         mapObjectIdsToPointIds.put(obj,id);
      }

      OpenSimObject oldObj = mapPointIdsToObjectIds.get(id);
      if(oldObj!=null && !oldObj.equals(obj)) mapObjectIdsToPointIds.put(oldObj,-1);
      mapPointIdsToObjectIds.put(id,obj);
   }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the lookupTable
     */
    public vtkColorTransferFunction getLookupTable() {
        return lookupTable;
    }

    /**
     * @return the shapeName
     */
    public String getShapeName() {
        return shapeName;
    }
    
    public boolean hasShapeName(String newName){
        return (MotionObjectsDB.getInstance().getShape(name)!=null);
    }

    /**
     * @return the color
     */
    public Color getColor() {
        double[] color = new double[]{1.0, 1.0, 1.0};
        this.getLookupTable().GetColor(0.0, color);
        return new Color((float) color[0], (float) color[1], (float) color[2]);
    }

    /**
     * @param color the color to set
     */
    public void setColor(Color color) {
        // Convert to RGB values and call setColorRange
        float[] colorAsFloats = new float[]{1.0F, 1.0F, 1.0F};
        color.getRGBColorComponents(colorAsFloats);
        double[] colorAsDoubles = new double[]{colorAsFloats[0], colorAsFloats[1], colorAsFloats[2]};
        this.setColorRange(colorAsDoubles, colorAsDoubles);
        setModified();
    }
}
