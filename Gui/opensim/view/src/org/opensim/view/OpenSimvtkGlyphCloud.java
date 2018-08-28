/* -------------------------------------------------------------------------- *
 * OpenSim: OpenSimvtkGlyphCloud.java                                         *
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
 * OpenSimvtkGlyphCloud.java
 *
 * Created on January 25, 2007, 9:39 AM
 *
 */

package org.opensim.view;

import java.awt.Color;
import java.util.HashMap;
import java.util.Stack;
import org.opensim.modeling.OpenSimObject;
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
        return false;
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
