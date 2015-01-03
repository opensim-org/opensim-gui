/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.view;

import org.opensim.modeling.DecorativeEllipsoid;
import org.opensim.modeling.DecorativeGeometry;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.Vec3;
import vtk.vtkActor;
import vtk.vtkClipPolyData;
import vtk.vtkPlane;
import vtk.vtkPolyData;
import vtk.vtkPolyDataMapper;
import vtk.vtkSphereSource;
import vtk.vtkTransform;
import vtk.vtkTransformPolyDataFilter;

/**
 *
 * @author Ayman
 */
class DecorativeEllipsoidDisplayer extends DecorativeGeometryDisplayer {

    private static int RESOLUTION_PHI = 32;
    private static int RESOLUTION_THETA = 32;
    private DecorativeEllipsoid ag;
    //protected OpenSimObject obj;

    /** 
     * Displayer for Wrap Geometry
     * @param ag
     * @param object 
     */
    DecorativeEllipsoidDisplayer(DecorativeEllipsoid ag) {
        this.ag = ag;
        //if (ag.hasUserRef()) setObj(ag.getUserRefAsObject());
     }

    /**
     * Convert DecorativeGeometry object passed in to the corresponding vtk polyhedral representation.
     * Transform is passed in as well since the way it applies to PolyData depends on source
     */
    private vtkPolyData getPolyData(DecorativeEllipsoid ag) {
        vtkSphereSource ellipsoid = new vtkSphereSource();
        ellipsoid.SetRadius(1.0);
        ellipsoid.SetPhiResolution(RESOLUTION_PHI);
        ellipsoid.SetThetaResolution(RESOLUTION_THETA);
        //System.out.println("Processing cyl (r, l)"+params[0]+","+params[1]);
        Vec3 radii = ag.getRadii();
        // Transform vtk ellipsoid (Y-axis aligned at origin) to match SIMM's along Z-axis at center
        vtkTransformPolyDataFilter xformOriginDirsFilter = new vtkTransformPolyDataFilter();
        vtkTransform xformOriginDirs = new vtkTransform();
        xformOriginDirs.Scale(radii.get(0), radii.get(1), radii.get(2));
        xformOriginDirsFilter.SetTransform(xformOriginDirs);
        xformOriginDirsFilter.SetInputConnection(ellipsoid.GetOutputPort());
        vtkPolyData full = xformOriginDirsFilter.GetOutput();
        return full;
    }

    /**
     * Based on the array of quadrants, clip the wrap-object Ellipsoid/ellipsoid 
     *
    public static void setQuadrants(final boolean quadrants[], final vtkEllipsoidSource Ellipsoid) {
    if (!quadrants[0]){ 
    ellipsoid.SetStartTheta(270.0);
    ellipsoid.SetEndTheta(90.0);
    }
    else if (!quadrants[1]){
    ellipsoid.SetStartTheta(90.0);
    ellipsoid.SetEndTheta(270.0);
    }
    else if (!quadrants[2]){
    ellipsoid.SetEndTheta(180.0);
    }
    else if (!quadrants[3]){  
    ellipsoid.SetStartTheta(180.0);
    }
    else if (!quadrants[4])   
    ellipsoid.SetEndPhi(90.0);
    else if (!quadrants[5])
    ellipsoid.SetStartPhi(90.0);
    }
     */
    /** 
     * Clip poly data of Ellipsoid, torus to proper half per passed in quadrants array
     * only x, y are considered here as they are supported by the kinematics engine
     */
    public static vtkPolyData clipPolyData(boolean[] quadrants, vtkPolyData full) {
        vtkPlane cutPlane = new vtkPlane();
        if (!quadrants[0]) {
            cutPlane.SetNormal(1.0, 0.0, 0.0);
        } else if (!quadrants[1]) {
            cutPlane.SetNormal(-1.0, 0.0, 0.0);
        } else if (!quadrants[2]) {
            cutPlane.SetNormal(0.0, 1.0, 0.0);
        } else if (!quadrants[3]) {
            cutPlane.SetNormal(0.0, -1.0, 0.0);
        } else // do nothing
        {
            return full;
        }
        vtkClipPolyData clipper = new vtkClipPolyData();
        clipper.SetClipFunction(cutPlane);
        clipper.SetInput(full);

        return clipper.GetOutput();
    }

    @Override
    void updateDisplayFromDecorativeGeometry() {
        vtkPolyData polyData = getPolyData(ag);
        //updatePropertiesForPolyData(polyData);
        createAndConnectMapper(polyData);
        //setXformAndAttributesFromDecorativeGeometry(ag);
    }

    @Override
    vtkActor computeVisuals() {
       updateDisplayFromDecorativeGeometry();
       return this;
    }

    int getBodyId() {
        return ag.getBodyId();
    }
    int getIndexOnBody() {
        return 0;//ag.getIndexOnBody();
    }
}
