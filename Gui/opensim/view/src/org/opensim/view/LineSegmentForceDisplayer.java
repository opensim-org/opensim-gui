/*
 * LineSegmentForceDisplayer.java
 *
 * Created on July 7, 2011, 2:19 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.view;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;
import org.opensim.modeling.Force;
import org.opensim.modeling.GeometryPath;
import org.opensim.modeling.OpenSimContext;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.PathPoint;
import org.opensim.modeling.PathPointSet;
import org.opensim.modeling.VisibleObject;
import org.opensim.view.pub.OpenSimDB;
import vtk.vtkMatrix4x4;

/**
 *
 * @author Ayman
 */
public class LineSegmentForceDisplayer {
    
    protected Hashtable<PathPoint, Integer> mapPathPointToGlyphId = new Hashtable<PathPoint, Integer>(10);
    protected Vector<Integer> pointGlyphIds = new Vector<Integer>(10);
    protected OpenSimvtkGlyphCloud pointsRep;
    protected Vector<Integer> segmentGlyphIds = new Vector<Integer>(10);
    protected OpenSimvtkOrientedGlyphCloud segmentsRep;
    protected GeometryPath geomPath;
    protected OpenSimContext openSimContext;
    protected Force forceAlongPath;
    /** Creates a new instance of LineSegmentForceDisplayer */
    public LineSegmentForceDisplayer(Force forceAlongPath,  OpenSimvtkGlyphCloud pointsRep, OpenSimvtkOrientedGlyphCloud segmentsRep) throws IOException {
        OpenSimObject pathObject =  forceAlongPath.getPropertyByName("GeometryPath").getValueAsObject();
        this.geomPath = GeometryPath.safeDownCast(pathObject);
        this.pointsRep = pointsRep;
        this.segmentsRep = segmentsRep;
        this.forceAlongPath = forceAlongPath;
        openSimContext = OpenSimDB.getInstance().getContext(geomPath.getModel());
    } 



    /**
     * Get the transform that takes a unit cylinder aligned with Y axis to a cylnder connecting 2 points
     */
    vtkMatrix4x4 retTransform = new vtkMatrix4x4();
    double[]     retTransformVector = new double[16];
    double[] newX = new double[3];
    double[] oldXCrossNewY = new double[3]; // NewZ


    protected void freeGlyphIds()
    {
        //System.out.println("LineSegmentMuscleDisplay ("+act.getName()+"): freeGlyphIds");
        for (int i = 0; i<pointGlyphIds.size(); i++)pointsRep.remove(pointGlyphIds.get(i));
        pointGlyphIds.clear();
        for (int i = 0; i<segmentGlyphIds.size(); i++)segmentsRep.remove(segmentGlyphIds.get(i));
        segmentGlyphIds.clear();
        mapPathPointToGlyphId.clear();
    }

    protected double[] getCylinderTransform(double[] normalizedAxis, double[] origin) {
        
        // yaxis is the unit vector
        for (int i = 0; i < 3; i++){
            retTransformVector[i*4+ 1] = normalizedAxis[i];
        }
        oldXCrossNewY[0] = 0.0;
        oldXCrossNewY[1] = -normalizedAxis[2];
        oldXCrossNewY[2] = normalizedAxis[1];
         
        double newZLength = normalizeAndGetLength(oldXCrossNewY);
        if (newZLength<1e-5){
            // use oldZ instead of oldX
            oldXCrossNewY[0] = normalizedAxis[1];
            oldXCrossNewY[1] = -normalizedAxis[0];
            oldXCrossNewY[2] = 0.0;
        }
        for (int i = 0; i < 3; i++){
            retTransformVector[i*4+ 2] = oldXCrossNewY[i];
        }
        newX[0] = normalizedAxis[1] * oldXCrossNewY[2] - normalizedAxis[2] * oldXCrossNewY[1];
        newX[1] = normalizedAxis[2] * oldXCrossNewY[0] - normalizedAxis[0] * oldXCrossNewY[2];
        newX[2] = normalizedAxis[0] * oldXCrossNewY[1] - normalizedAxis[1] * oldXCrossNewY[0];
        normalizeAndGetLength(newX);
        for (int i = 0; i < 3; i++){
            retTransformVector[i*4] = newX[i];
            retTransformVector[i*4+ 3] = origin[i];
        }
        for (int i = 12; i<15; i++)
            retTransformVector[i] = 0.0;
        retTransformVector[15] = 1.0;
        //retTransform.DeepCopy(retTransformVector);
        return retTransformVector;
    }

    /**
     * Normalize a vector and return its length
     */
    protected double normalizeAndGetLength(double[] vector3)
    {
        double length = Math.sqrt(vector3[0]*vector3[0]+
                                  vector3[1]*vector3[1]+
                                  vector3[2]*vector3[2]);
        // normalize
        for (int d = 0; d <3; d++)
            vector3[d]=vector3[d]/length;
        return length;
    }

    void addGeometry() {
        openSimContext.updateDisplayer(forceAlongPath);
        updateGlyphIds();
        updateGeometry(false);
    }

       private void updateGlyphIds()
   {
      int oldPointsSize = pointGlyphIds.size();
      int oldSegmentsSize = segmentGlyphIds.size();

      int newPointsSize;
      int newSegmentsSize;

      VisibleObject actuatorDisplayer = forceAlongPath.getDisplayer();
      if (actuatorDisplayer == null) {
         newPointsSize = 0;
         newSegmentsSize = 0;
      } else {
         newPointsSize = geomPath.getPathPointSet().getSize();
         newSegmentsSize = actuatorDisplayer.countGeometry();
      }

      // allocate glyph id's
      if(newPointsSize > oldPointsSize) {
         //System.out.println("LineSegmentMuscleDisplay ("+act.getName()+"): updateGlyphIds - points "+oldPointsSize+"->"+newPointsSize);

         // for muscle points, we assume the visible points are fixed attachments or moving via points, and we assume
         // the number of these doesn't change between typical updates... so we allocate the exact number of requested points
         pointGlyphIds.setSize(newPointsSize);
         for(int i=oldPointsSize; i<newPointsSize; i++) {
            int index = pointsRep.addLocation();
            pointGlyphIds.set(i, index);
            pointsRep.setVectorDataAtLocation(index,1,1,1);
         }
      }

      // update pointers
      PathPointSet as = geomPath.getPathPointSet();
      mapPathPointToGlyphId.clear();
      for(int i=0; i<newPointsSize; i++) {
         pointsRep.setObjectAtPointId(pointGlyphIds.get(i), as.get(i));
         mapPathPointToGlyphId.put(as.get(i), pointGlyphIds.get(i));
         //System.out.println("MAP "+as.get(i)+" -- "+MusclePoint.getCPtr(as.get(i))+" to index "+pointGlyphIds.get(i));
         //System.out.println("VERIFY: "+pointsRep.getPointId(as.get(i)));
      }
      as=null; // Make sure we don't keep stale pointers'
      // if we've shrunk in size, remove references to non-existent objects from stale id's
      for(int i=newPointsSize; i<pointGlyphIds.size(); i++)
         pointsRep.setObjectAtPointId(pointGlyphIds.get(i), null);

      if(newSegmentsSize > oldSegmentsSize) {
         //System.out.println("LineSegmentMuscleDisplay ("+act.getName()+"): updateGlyphIds - segments "+oldSegmentsSize+"->"+newSegmentsSize);

         // for segments, due to wrapping, the number may change frequently, so we overallocate the glyph id's to
         // avoid having to frequently reset the glyphs
         int capacity = (oldSegmentsSize > 0) ? oldSegmentsSize : 1;
         while(capacity < newSegmentsSize) capacity *= 2;
         newSegmentsSize = capacity;
         segmentGlyphIds.setSize(newSegmentsSize);
         for(int i=oldSegmentsSize; i<newSegmentsSize; i++) {
            int index = segmentsRep.addLocation();
            segmentGlyphIds.set(i, index);
         }
      }
   }

   public void updateGeometry(boolean callUpdateDisplayer)
   {
       /*
      // Get attachments and connect them
      if(callUpdateDisplayer) openSimContext.updateDisplayer(forceAlongPath);
      VisibleObject actuatorDisplayer = forceAlongPath.getDisplayer();
      if (actuatorDisplayer == null) return;

      DisplayPreference dp = actuatorDisplayer.getDisplayPreference();

      // Account for possible increase in number of muscle segments
      updateGlyphIds();

      // Set all glyphs to hidden to begin with
      for(int i=0; i<pointGlyphIds.size(); i++) pointsRep.hide(pointGlyphIds.get(i));
      for(int i=0; i<segmentGlyphIds.size(); i++) segmentsRep.hide(segmentGlyphIds.get(i));

      if (dp == DisplayPreference.None || openSimContext.isDisabled(forceAlongPath)) return;

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
            int idx = segmentGlyphIds.get(i).intValue(); // we should have enough segmentGlyphIds allocated at this point sinec we had called updateGlyphIds()
            segmentsRep.show(idx);
            segmentsRep.setLocation(idx, center);
            segmentsRep.setTensorDataAtLocation(idx,
                    xform[0], xform[4], xform[8],
                    length*xform[1], length*xform[5], length*xform[9],
                    xform[2], xform[6], xform[10]);

            //
            // Draw non-wrap muscle points
            //
            PathPoint pt1 = path.getitem(i);
            if(PathWrapPoint.safeDownCast(pt1)==null) {
               Integer pointIdx = mapPathPointToGlyphId.get(pt1);
               if(pointIdx!=null) { // shouldn't be null!  but what should we do if it is??
                  idx = pointIdx.intValue();
                  pointsRep.show(idx);
                  pointsRep.setLocation(idx, position1);
               }
            }
            if(i==geomSize-1) {
               // Draw last point if it's non-wrap
               PathPoint pt2 = path.getitem(i+1);
               if(PathWrapPoint.safeDownCast(pt2)==null) {
                  Integer pointIdx = mapPathPointToGlyphId.get(pt2);
                  if(pointIdx!=null) { // shouldn't be null!  but what should we do if it is??
                     idx = pointIdx.intValue();
                     pointsRep.show(idx);
                     pointsRep.setLocation(idx, position2);
                   }
               }
            }
         }
      }*/
   }

}
