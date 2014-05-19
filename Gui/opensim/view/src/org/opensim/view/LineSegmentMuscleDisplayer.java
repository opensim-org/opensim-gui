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

import java.util.Hashtable;
import java.util.Vector;
import org.opensim.modeling.PathActuator;
import org.opensim.modeling.PathPoint;
import org.opensim.modeling.PathPointSet;
import org.opensim.modeling.OpenSimContext;
import org.opensim.modeling.VisibleObject;
import org.opensim.modeling.DisplayGeometry.DisplayPreference;
import org.opensim.modeling.GeometryPath;
import org.opensim.modeling.OpenSimObject;
import org.opensim.view.pub.OpenSimDB;
import vtk.vtkMatrix4x4;

public class LineSegmentMuscleDisplayer {

   private PathActuator act;
   private OpenSimContext openSimContext;

   private OpenSimvtkGlyphCloud musclePointsRep;
   private OpenSimvtkOrientedGlyphCloud muscleSegmentsRep;

   Hashtable<PathPoint,Integer> mapPathPointToGlyphId = new Hashtable<PathPoint,Integer>(10);
   Vector<Integer> musclePointGlyphIds = new Vector<Integer>(10);
   Vector<Integer> muscleSegmentGlyphIds = new Vector<Integer>(10);
   protected GeometryPath geomPath;
   private MuscleColoringFunction muscleColoringFunction; 
   
   public LineSegmentMuscleDisplayer(PathActuator act, OpenSimvtkGlyphCloud musclePointsRep, OpenSimvtkOrientedGlyphCloud muscleSegmentsRep)
    {
        OpenSimObject pathObject;
        pathObject = act.getGeometryPath();
        this.geomPath = GeometryPath.safeDownCast(pathObject);
        this.act = act;
        this.musclePointsRep = musclePointsRep;
        this.muscleSegmentsRep = muscleSegmentsRep;
        openSimContext = OpenSimDB.getInstance().getContext(act.getModel());
        muscleColoringFunction = new MuscleNoColoringFunction(openSimContext);
        
    }

   public void setApplyColoringFunction(MuscleColoringFunction colorFunction) {
      setMuscleColoringFunction(colorFunction);
      updateGeometry(true);
   }

   private void freeGlyphIds()
   {
      //System.out.println("LineSegmentMuscleDisplay ("+act.getName()+"): freeGlyphIds");
      for(int i=0; i<musclePointGlyphIds.size(); i++) musclePointsRep.remove(musclePointGlyphIds.get(i));
      musclePointGlyphIds.clear();
      for(int i=0; i<muscleSegmentGlyphIds.size(); i++) muscleSegmentsRep.remove(muscleSegmentGlyphIds.get(i));
      muscleSegmentGlyphIds.clear();
      mapPathPointToGlyphId.clear();
   }

   // TODO: deal with remapping selections?
   private void updateGlyphIds()
   {
      int oldPointsSize = musclePointGlyphIds.size();
      int oldSegmentsSize = muscleSegmentGlyphIds.size();

      int newPointsSize;
      int newSegmentsSize;

      VisibleObject actuatorDisplayer = act.getDisplayer();
      if (actuatorDisplayer == null) {
         newPointsSize = 0;
         newSegmentsSize = 0;
      } else {
         newPointsSize = act.getGeometryPath().getPathPointSet().getSize();
         newSegmentsSize = actuatorDisplayer.countGeometry();
      }

      // allocate glyph id's
      if(newPointsSize > oldPointsSize) {
         //System.out.println("LineSegmentMuscleDisplay ("+act.getName()+"): updateGlyphIds - points "+oldPointsSize+"->"+newPointsSize);

         // for muscle points, we assume the visible points are fixed attachments or moving via points, and we assume
         // the number of these doesn't change between typical updates... so we allocate the exact number of requested points
         musclePointGlyphIds.setSize(newPointsSize);
         for(int i=oldPointsSize; i<newPointsSize; i++) {
            int index = musclePointsRep.addLocation();
            musclePointGlyphIds.set(i, index);
            musclePointsRep.setVectorDataAtLocation(index,1,1,1);
         }
      }

      // update pointers
      PathPointSet as = act.getGeometryPath().getPathPointSet();
      mapPathPointToGlyphId.clear();
      for(int i=0; i<newPointsSize; i++) {
         musclePointsRep.setObjectAtPointId(musclePointGlyphIds.get(i), as.get(i));
         mapPathPointToGlyphId.put(as.get(i), musclePointGlyphIds.get(i));
         //System.out.println("MAP "+as.get(i)+" -- "+MusclePoint.getCPtr(as.get(i))+" to index "+musclePointGlyphIds.get(i));
         //System.out.println("VERIFY: "+musclePointsRep.getPointId(as.get(i)));
      }
      as=null; // Make sure we don't keep stale pointers'
      // if we've shrunk in size, remove references to non-existent objects from stale id's
      for(int i=newPointsSize; i<musclePointGlyphIds.size(); i++)
         musclePointsRep.setObjectAtPointId(musclePointGlyphIds.get(i), null);

      if(newSegmentsSize > oldSegmentsSize) {
         //System.out.println("LineSegmentMuscleDisplay ("+act.getName()+"): updateGlyphIds - segments "+oldSegmentsSize+"->"+newSegmentsSize);

         // for segments, due to wrapping, the number may change frequently, so we overallocate the glyph id's to
         // avoid having to frequently reset the glyphs
         int capacity = (oldSegmentsSize > 0) ? oldSegmentsSize : 1;
         while(capacity < newSegmentsSize) capacity *= 2;
         newSegmentsSize = capacity;
         muscleSegmentGlyphIds.setSize(newSegmentsSize);
         for(int i=oldSegmentsSize; i<newSegmentsSize; i++) {
            int index = muscleSegmentsRep.addLocation();
            muscleSegmentGlyphIds.set(i, index);
         }
      }
   }

   public void addGeometry()
   {
      openSimContext.updateDisplayer(act);
      updateGlyphIds();
      updateGeometry(false);
   }

   public void updateGeometry(boolean callUpdateDisplayer)
   {
       /*
      // Get attachments and connect them
      if(callUpdateDisplayer) openSimContext.updateDisplayer(act);
      VisibleObject actuatorDisplayer = act.getDisplayer();
      if (actuatorDisplayer == null) return;

      DisplayPreference dp = actuatorDisplayer.getDisplayPreference();

      // Account for possible increase in number of muscle segments
      updateGlyphIds();

      // Set all glyphs to hidden to begin with
      for(int i=0; i<musclePointGlyphIds.size(); i++) musclePointsRep.hide(musclePointGlyphIds.get(i));
      for(int i=0; i<muscleSegmentGlyphIds.size(); i++) muscleSegmentsRep.hide(muscleSegmentGlyphIds.get(i));

      if (dp == DisplayPreference.None || openSimContext.isDisabled(act)) return;

      double activation = 0;
      Muscle msl = Muscle.safeDownCast(act);
      if (msl!=null)
          activation = getMuscleColoringFunction().getColor(msl); 
      // A displayer is found, get geometry
      int geomSize = actuatorDisplayer.countGeometry();
      if (geomSize > 0) {
         double[] axis = new double[]{0.0, 0.0, 0.0};
         double[] center = new double[]{0.0, 0.0, 0.0};
         double[] position1 = new double[3];
         double[] position2 = new double[3];

         ArrayPathPoint path=openSimContext.getCurrentDisplayPath(geomPath);

         // Points are already in inertial frame
         for(int i=0; i<geomSize; i++) {
            Geometry geomEntry = actuatorDisplayer.getGeometry(i);
            LineGeometry geomLine = LineGeometry.dynamic_cast(geomEntry);
            geomLine.getPoints(position1, position2);
           
            // 
            // Draw cylinder for muscle segment
            //
            for(int d=0; d <3; d++){
               axis[d] = position1[d]-position2[d];
               center[d] = (position1[d]+position2[d])/2.0;
            }
            double length = normalizeAndGetLength(axis);
            double[] xform = getCylinderTransform(axis, center);
            int idx = muscleSegmentGlyphIds.get(i).intValue(); // we should have enough muscleSegmentGlyphIds allocated at this point sinec we had called updateGlyphIds()
            muscleSegmentsRep.show(idx);
            muscleSegmentsRep.setLocation(idx, center);
            //muscleSegmentsRep.getVtkActor().GetProperty().SetOpacity(10*activation);
                muscleSegmentsRep.setTensorDataAtLocation(idx,
                        xform[0], xform[4], xform[8],
                        length*xform[1], length*xform[5], length*xform[9],
                        xform[2], xform[6], xform[10]);
//Jeff                double scaledMuscleRadius = Math.pow((activation-0.5)*2.0, 2);
//Jeff             muscleSegmentsRep.setTensorDataAtLocation(idx, 
//Jeff                         scaledMuscleRadius*xform[0], scaledMuscleRadius*xform[4], scaledMuscleRadius*xform[8],
//Jeff                              length*xform[1], length*xform[5], length*xform[9],
//Jeff                         scaledMuscleRadius*xform[2], scaledMuscleRadius*xform[6], scaledMuscleRadius*xform[10]);
            muscleSegmentsRep.setScalarDataAtLocation(idx, activation);

            //
            // Draw non-wrap muscle points
            //
            PathPoint pt1 = path.getitem(i);
            if(PathWrapPoint.safeDownCast(pt1)==null) {
               Integer pointIdx = mapPathPointToGlyphId.get(pt1);
               if(pointIdx!=null) { // shouldn't be null!  but what should we do if it is??
                  idx = pointIdx.intValue();
                  musclePointsRep.show(idx);
                  musclePointsRep.setLocation(idx, position1);
                  //musclePointsRep.setOpacity(10.0*activation);
 //Jeff                      musclePointsRep.setVectorDataAtLocation(idx, scaledMuscleRadius,scaledMuscleRadius,scaledMuscleRadius);
                  if(!musclePointsRep.getSelected(idx)) musclePointsRep.setScalarDataAtLocation(idx, activation);
               }
            }
            if(i==geomSize-1) {
               // Draw last point if it's non-wrap
               PathPoint pt2 = path.getitem(i+1);
               if(PathWrapPoint.safeDownCast(pt2)==null) {
                  Integer pointIdx = mapPathPointToGlyphId.get(pt2);
                  if(pointIdx!=null) { // shouldn't be null!  but what should we do if it is??
                     idx = pointIdx.intValue();
                     musclePointsRep.show(idx);
                     musclePointsRep.setLocation(idx, position2);
                     //musclePointsRep.setOpacity(10.0*activation);
//Jeff                             musclePointsRep.setVectorDataAtLocation(idx, scaledMuscleRadius,scaledMuscleRadius,scaledMuscleRadius);
                     if(!musclePointsRep.getSelected(idx)) musclePointsRep.setScalarDataAtLocation(idx, activation);
                  }
               }
            }
         }
      }
        * */
      // We'll let SingleModelVisuals call setModified() as necessary so we avoid calling it repeatedly when unnecessary
      //muscleSegmentsRep.setModified();
      //musclePointsRep.setModified();
   }

   public void removeGeometry()
   {
      freeGlyphIds();
      VisibleObject actuatorDisplayer = act.getDisplayer();
      if(actuatorDisplayer!=null) actuatorDisplayer.setDisplayPreference(DisplayPreference.None);
   }

    /**
     * Get the transform that takes a unit cylinder aligned with Y axis to a cylnder connecting 2 points
     */
    vtkMatrix4x4 retTransform = new vtkMatrix4x4();
    double[]     retTransformVector = new double[16];
    double[] newX = new double[3];
    double[] oldXCrossNewY = new double[3]; // NewZ
    double[] getCylinderTransform(double[] normalizedAxis, double[] origin) {
        //Assume normalizedAxis is already normalized
        //double length = normalizeAndGetLength(axis);
        
        // yaxis is the unit vector
        for (int i=0; i < 3; i++){
            retTransformVector[i*4+ 1]= normalizedAxis[i];
        }
        oldXCrossNewY[0] = 0.0;
        oldXCrossNewY[1] = -normalizedAxis[2];
        oldXCrossNewY[2] = normalizedAxis[1];
         
        double newZLength=normalizeAndGetLength(oldXCrossNewY);
        if (newZLength<1e-5){
            // use oldZ instead of oldX
            oldXCrossNewY[0]=normalizedAxis[1];
            oldXCrossNewY[1]=-normalizedAxis[0];
            oldXCrossNewY[2]=0.0;
        }
        for (int i=0; i < 3; i++){
            retTransformVector[i*4+ 2]= oldXCrossNewY[i];
        }
        newX[0] = normalizedAxis[1]*oldXCrossNewY[2]-normalizedAxis[2]*oldXCrossNewY[1];
        newX[1] = normalizedAxis[2]*oldXCrossNewY[0]-normalizedAxis[0]*oldXCrossNewY[2];
        newX[2] = normalizedAxis[0]*oldXCrossNewY[1]-normalizedAxis[1]*oldXCrossNewY[0];
        normalizeAndGetLength(newX);
       for (int i=0; i < 3; i++){
          retTransformVector[i*4]= newX[i];
          retTransformVector[i*4+ 3]= origin[i];
         }
        for(int i=12; i<15;i++)
            retTransformVector[i]=0.0;
        retTransformVector[15]=1.0;
        //retTransform.DeepCopy(retTransformVector);
        return retTransformVector;
    }
    /**
     * Normalize a vector and return its length
     */
    private double normalizeAndGetLength(double[] vector3)
    {
        double length = Math.sqrt(vector3[0]*vector3[0]+
                                  vector3[1]*vector3[1]+
                                  vector3[2]*vector3[2]);
        // normalize
        for(int d=0; d <3; d++)
            vector3[d]=vector3[d]/length;
        return length;
    }


    /**
     * @return the muscleColoringFunction
     */
    public MuscleColoringFunction getMuscleColoringFunction() {
        return muscleColoringFunction;
    }

    /**
     * @param muscleColoringFunction the muscleColoringFunction to set
     */
    public void setMuscleColoringFunction(MuscleColoringFunction muscleColoringFunction) {
        this.muscleColoringFunction = muscleColoringFunction;
    }
}
