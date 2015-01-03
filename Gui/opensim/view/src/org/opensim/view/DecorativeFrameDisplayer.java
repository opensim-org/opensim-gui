/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.view;

import org.opensim.modeling.DecorativeFrame;
import org.opensim.modeling.OpenSimObject;
import vtk.vtkActor;
import vtk.vtkAxes;
import vtk.vtkAxesActor;
import vtk.vtkPolyData;

/**
 *
 * @author Ayman
 */
class DecorativeFrameDisplayer extends DecorativeGeometryDisplayer {

    private DecorativeFrame ag;
    public DecorativeFrameDisplayer(DecorativeFrame arg0) {
        //super(object);
        this.ag = arg0;
        //if (ag.hasUserRef()) setObj(ag.getUserRefAsObject());

    }

    private vtkPolyData getPolyData(DecorativeFrame ag) {
        vtkAxes frameSrc = new vtkAxes();
        frameSrc.SetScaleFactor(ag.getAxisLength());
        return frameSrc.GetOutput();
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
