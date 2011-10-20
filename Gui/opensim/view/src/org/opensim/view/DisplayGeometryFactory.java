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
import org.opensim.modeling.AnalyticGeometry;
import org.opensim.modeling.DisplayGeometry;
import org.opensim.modeling.Geometry;
import org.opensim.modeling.PolyhedralGeometry;
import org.opensim.modeling.VisibleObject;
import org.opensim.utils.TheApp;
import org.opensim.view.pub.GeometryFileLocator;
import vtk.vtkActor;
import vtk.vtkOBJReader;
import vtk.vtkPolyData;
import vtk.vtkPolyDataMapper;
import vtk.vtkTransform;
import vtk.vtkTransformPolyDataFilter;

/**
 *
 * @author ayman
 */
public class DisplayGeometryFactory {

    static boolean displayContactGeometry = false;

    static void updateDisplayPreference() {
        String saved = Preferences.userNodeForPackage(TheApp.class).get("Display Contact Geometry", "Off");
        if (saved.equalsIgnoreCase("On"))
            displayContactGeometry = true;
        else
            displayContactGeometry = false;

    }
    public static vtkActor createGeometryDisplayer(VisibleObject visibleObject, String modelPath) {
        vtkActor attachmentRep = null;
        for(int gc=0; gc<visibleObject.countGeometry(); gc++){
            Geometry g = visibleObject.getGeometry(gc);
            AnalyticGeometry ag=null;
            ag = AnalyticGeometry.dynamic_cast(g);
            if (ag != null){
                vtkPolyData analyticPolyData = AnalyticGeometryDisplayer.getPolyData(ag);
                attachmentRep= createActorForPolyData(analyticPolyData, visibleObject);
                attachmentRep.GetProperty().SetRepresentationToWireframe();
                //attachmentRep.GetProperty().SetOpacity(0.3);
            } else {  // Contact geometry
                PolyhedralGeometry pg = PolyhedralGeometry.dynamic_cast(g);
                if (pg !=null){
                    String geometryFile = pg.getGeometryFilename();
                    String meshFile = GeometryFileLocator.getInstance().getFullname(modelPath,geometryFile, false);
                    if (meshFile==null || (!meshFile.endsWith(".obj")))
                        continue;
                    vtkOBJReader polyReader = new vtkOBJReader();
                    polyReader.SetFileName(meshFile);
                    vtkPolyData meshPoly = polyReader.GetOutput();
                    attachmentRep= createActorForPolyData(meshPoly, visibleObject);
                    // Always wireframe wrapping and contact geometry for now
                    attachmentRep.GetProperty().SetRepresentationToWireframe();
                    //attachmentRep.GetProperty().SetOpacity(0.3);
                }
            }
        } //for
        
        applyDisplayPrefs(visibleObject,attachmentRep);
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
                displayContactGeometry==false){
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

}
