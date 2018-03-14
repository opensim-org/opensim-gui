/* -------------------------------------------------------------------------- *
 * OpenSim: SelectedObject.java                                               *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib, Eran Guendelman                                    *
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
 * SelectedObject
 * Author(s): Eran Guendelman
 */
package org.opensim.view;

import org.opensim.modeling.Body;
import org.opensim.modeling.Component;
import org.opensim.modeling.GeometryPath;
import org.opensim.modeling.Marker;
import org.opensim.modeling.Model;
import org.opensim.modeling.ModelComponent;
import org.opensim.modeling.OpenSimContext;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.PathActuator;
import org.opensim.modeling.PathPoint;
import org.opensim.modeling.PhysicalFrame;
import org.opensim.modeling.WrapObject;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;
import vtk.vtkActor;
import vtk.vtkCaptionActor2D;
import vtk.vtkProp3D;
import vtk.vtkProp3DCollection;


/**
 *
 * @author Eran Guendelman
 *
 */
public class SelectedObject implements Selectable {
   public static final double defaultSelectedColor[] = {0.8, 0.8, 0.0};

   OpenSimObject object;

   public SelectedObject(OpenSimObject object) { 
      this.object = object; 
   }

   public OpenSimObject getOpenSimObject() {
      return object;
   }

   public String getStatusText() {
      return object.getConcreteClassName() + ":" + object.getName();
   }

   private Model getModel(ModelComponent mc) { return mc.getModel(); }
 
   public Model getOwnerModel()
   {
      ModelComponent mc = ModelComponent.safeDownCast(object);
      if (mc != null) return mc.getModel();
      Component comp = Component.safeDownCast(object);
      ModelComponent parent = ModelComponent.safeDownCast(comp.getOwner());
      while (parent == null){
          comp = comp.getOwner();
          parent = ModelComponent.safeDownCast(comp);
      }
      return parent.getModel();
   }

   public void markSelected(boolean highlight)
   {
      if (PathPoint.safeDownCast(object) != null) {
         PathPoint mp = PathPoint.safeDownCast(object);
         OpenSimObject owner = GeometryPath.safeDownCast(mp.getOwner()).getOwner();
         SingleModelVisuals visuals = ViewDB.getInstance().getModelVisuals(getModel(mp));
         /* FIX40 if keeping VTK
         OpenSimvtkGlyphCloud cloud = null;
         int id = cloud.getPointId(object);
         if(id>=0) { // just to be safe
            cloud.setSelected(id, highlight);
            visuals.updateObjectDisplay(owner); //TODO: perhaps overkill for getting musclepoint to update?
         }*/
      } else if (Marker.safeDownCast(object) != null) {
         Marker marker = Marker.safeDownCast(object);
         SingleModelVisuals visuals = ViewDB.getInstance().getModelVisuals(getModel(marker));
         visuals.highLightObject(object, highlight);
      } else if (Body.safeDownCast(object) != null ) {
          SingleModelVisuals viz = ViewDB.getInstance().getModelVisuals(this.getOwnerModel());
          viz.highLightObject(object, highlight);
      }
      else {
          vtkProp3D prop = ViewDB.getInstance().getVtkRepForObject(object);
          if (prop instanceof DecorativeGeometryDisplayer){
             DecorativeGeometryDisplayer asm = (DecorativeGeometryDisplayer) prop;
             if(highlight){
                 //b.getDisplayer().getVisibleProperties().setColor(currentColor);
                 ViewDB.getInstance().applyColor(defaultSelectedColor, asm, false);
             }
             else{
                asm.updateDisplayFromDecorativeGeometry();
             }
          }
          else if (prop instanceof DisplayGeometryDisplayer){
              DisplayGeometryDisplayer dgd = (DisplayGeometryDisplayer) prop;
              if(highlight){
                 //b.getDisplayer().getVisibleProperties().setColor(currentColor);
                 ViewDB.getInstance().applyColor(defaultSelectedColor, prop, false);
             }
             else{
                dgd.updateFromProperties();
             }
          }
      }
   }

   public static double[] getGlyphPointBounds(OpenSimvtkGlyphCloud cloud, SingleModelVisuals visuals, OpenSimObject object) {
      double pointSize = 0.05;
      int id = cloud.getPointId(object);
      if(id<0) return null; // just to be safe
      double[] location = new double[3];
      cloud.getLocation(id, location);
      visuals.transformModelToWorldPoint(location); // Transform to world space
      return new double[]{location[0]-pointSize,location[0]+pointSize,
                          location[1]-pointSize,location[1]+pointSize,
                          location[2]-pointSize,location[2]+pointSize};
   }

   public double[] getBounds()
   {
      
      double[] bounds = null;
      if (PathPoint.safeDownCast(object) != null) {
         PathPoint mp = PathPoint.safeDownCast(object);
         Model dModel = getModel(mp);
         OpenSimContext context = OpenSimDB.getInstance().getContext(dModel);
         if (!(context.isActivePathPoint(mp))) return null;
         SingleModelVisuals visuals = ViewDB.getInstance().getModelVisuals(dModel);
         // If muscle is now hidden return
         int displayStatus = ViewDB.getInstance().getDisplayStatus(GeometryPath.safeDownCast(mp.getOwner()).getOwner());
         if (displayStatus==0) return null;
         //bounds = getGlyphPointBounds(visuals.getMusclePointsRep(), visuals, object);
      } else if (Marker.safeDownCast(object) != null) {
         Marker marker = Marker.safeDownCast(object);
         SingleModelVisuals visuals = ViewDB.getInstance().getModelVisuals(getModel(marker));
         // Check if not visible, return
         int displayStatus = ViewDB.getInstance().getDisplayStatus(marker);
         if (displayStatus==0) return null;
         bounds = getGlyphPointBounds(visuals.getMarkersRep(), visuals, object);
         
      } else { // if (AbstractBody.safeDownCast(object) != null)
          // Check if object is visible 
         int displayStatus = ViewDB.getInstance().getDisplayStatus(object);
         if (displayStatus==0) return null;
         vtkProp3D asm = ViewDB.getInstance().getVtkRepForObject(object);
         if(asm!=null) {
            bounds = asm.GetBounds();
            Body dBody = null;
            if (Body.safeDownCast(object)!=null )
                dBody = Body.safeDownCast(object);
            else if (WrapObject.safeDownCast(object)!=null)
                dBody = Body.safeDownCast(WrapObject.safeDownCast(object).getFrame());
            if (dBody != null){
                SingleModelVisuals visuals = ViewDB.getInstance().getModelVisuals(getModel(dBody));
            if(bounds!=null && visuals!=null) visuals.transformModelToWorldBounds(bounds);
         }
      }
      }
      return bounds;
   }

    public void updateAnchor(vtkCaptionActor2D caption) {
             double[] bounds=getBounds();
             if (bounds == null){   // object currently invisible
                 caption.SetVisibility(0);
                 return;
}
             else
                 caption.SetVisibility(1);
             caption.SetAttachmentPoint(new double[]{
                 (bounds[0]+bounds[1])/2.0,
                 (bounds[2]+bounds[3])/2.0,
                 (bounds[4]+bounds[5])/2.0,
             });
    }
}
