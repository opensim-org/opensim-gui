/*
 * DisplayGeometryFactory.java
 *
 * Created on July 27, 2010, 9:47 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.view;

import java.util.prefs.Preferences;
import org.opensim.modeling.*;
import org.opensim.utils.TheApp;
import org.opensim.view.pub.GeometryFileLocator;
import vtk.vtkActor;
import vtk.vtkOBJReader;
import vtk.vtkPolyData;
import vtk.vtkPolyDataMapper;
import vtk.vtkProp3D;
import vtk.vtkTransform;
import vtk.vtkTransformPolyDataFilter;

/**
 *
 * @author ayman
 */
public class DisplayGeometryFactory {

    private static boolean displayContactGeometry = false;

    static void updateDisplayPreference() {
        String saved = Preferences.userNodeForPackage(TheApp.class).get("Display Contact Geometry", "Off");
        if (saved.equalsIgnoreCase("On"))
            displayContactGeometry = true;
        else
            displayContactGeometry = false;

    }
    public static vtkActor createGeometryDisplayer(OpenSimObject object, String modelPath) {
        vtkActor attachmentRep = null;
        VisibleObject visibleObject = object.getDisplayer();
        int numGeometryPieces = visibleObject.countGeometry();
        //assert(numGeometryPieces<= 1);
        if (numGeometryPieces>0) {
        for(int gc=0; gc< numGeometryPieces; gc++){
            Geometry g = visibleObject.getGeometry(gc);
            AnalyticGeometry ag=null;
            ag = AnalyticGeometry.dynamic_cast(g);
            if (ag != null){
                attachmentRep= new AnalyticGeometryDisplayer(ag, object);
                
            } else {  // Contact geometry
                 PolyhedralGeometry pg = PolyhedralGeometry.dynamic_cast(g);
                if (pg !=null){
                    attachmentRep = new PolyhedralGeometryDisplayer(pg, object, modelPath);
                 }
            }
        } //for
        }
        return attachmentRep;
    }
    
    private static vtkActor createActorForPolyData(final vtkPolyData meshPoly, final VisibleObject visibleObject) {
        // Move rep to proper location
        vtkActor attachmentRep=new vtkActor();
        vtkTransformPolyDataFilter mover = new vtkTransformPolyDataFilter();
        vtkTransform moverTransform = new vtkTransform();
        double[] matRows = new double[16];
        visibleObject.getTransformAsDouble16(matRows);
        moverTransform.SetMatrix(SingleModelVisuals.convertTransformToVtkMatrix4x4(matRows));
        mover.SetInput(meshPoly);
        mover.SetTransform(moverTransform);
        
        // Mapper
        vtkPolyDataMapper mapper = new vtkPolyDataMapper();
        mapper.SetInput(mover.GetOutput());
        attachmentRep.SetMapper(mapper);
        
        // Color/shading
        attachmentRep.GetProperty().SetColor(new double[]{0., 1., 1.});
        return attachmentRep;
    }
    
   /**
     * Apply user display preference (None, wireframe, shading)
     */
    private static void applyDisplayPrefs(VisibleObject objectDisplayer, vtkActor objectRep) {

        if (objectRep==null) return;
        // Show vs. Hide
        if (objectDisplayer.getDisplayPreference() == DisplayGeometry.DisplayPreference.None ||
                isDisplayContactGeometry()==false){
            objectRep.SetVisibility(0);
            return;
        }
        objectRep.SetVisibility(1);
        if (objectDisplayer.getDisplayPreference().swigValue()==DisplayGeometry.DisplayPreference.WireFrame.swigValue())
            objectRep.GetProperty().SetRepresentationToWireframe();
        else {

            if (objectDisplayer.getDisplayPreference() == DisplayGeometry.DisplayPreference.FlatShaded)
                objectRep.GetProperty().SetInterpolationToFlat();
            else
                objectRep.GetProperty().SetRepresentationToSurface();
        }
    }

    static void updateFromProperties(vtkProp3D prop3D, VisibleObject visibleObject) {
        prop3D.Modified();
    }

    /**
     * @return the displayContactGeometry
     */
    public static boolean isDisplayContactGeometry() {
        return displayContactGeometry;
    }
}
