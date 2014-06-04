/*
 * ModelComDisplayer.java
 *
 * Created on July 7, 2011, 1:59 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.view;

import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimContext;
import org.opensim.view.pub.GeometryFileLocator;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;
import vtk.vtkActor;
import vtk.vtkPolyDataMapper;
import vtk.vtkProp3D;
import vtk.vtkSphereSource;

/**
 *
 * @author Ayman
 */
public class ModelComDisplayer{
    
    vtkActor centerOfMassActor = new vtkActor();
    Model model;
    /** Creates a new instance of ModelComDisplayer */
    public ModelComDisplayer(Model model) {
       this.model = model;

        String comboneFile = GeometryFileLocator.getInstance().getFullname("", "com.vtp", false);
        if (comboneFile==null)
            comboneFile = GeometryFileLocator.getInstance().getFullname("", "sphere.vtp", false);
        if (comboneFile==null) return;
        GeometryFactory.populatePolyDatarFromFile(comboneFile, centerOfMassActor);

       centerOfMassActor.GetProperty().SetColor(0.0, 1.0, 0.0); // Green COM for now, 3X marker size
       centerOfMassActor.SetScale(ViewDB.getInstance().getMarkerDisplayRadius()*3);
    }

    vtkProp3D getVtkActor() {
        return centerOfMassActor;
    }

    void updateCOMLocation() {
        OpenSimContext context=OpenSimDB.getInstance().getContext(model);
        double[] com = new double[3];
        context.getCenterOfMassInGround(com);
        centerOfMassActor.SetPosition(com);
    }

    public void setModified() {
        centerOfMassActor.Modified();
    }

    public void updateGeometry() {
        updateCOMLocation();
    }
    
}
