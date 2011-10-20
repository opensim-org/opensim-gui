/*
 * TwoBodyForceDisplayer.java
 *
 * Created on July 7, 2011, 2:19 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.view;

import java.util.Enumeration;
import java.util.Hashtable;
import org.opensim.modeling.DisplayGeometry.DisplayPreference;
import org.opensim.modeling.Force;
import org.opensim.modeling.Geometry;
import org.opensim.modeling.LineGeometry;
import org.opensim.modeling.OpenSimContext;
import org.opensim.modeling.OpenSimObject;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;
import vtk.vtkActor;
import vtk.vtkAppendPolyData;
import vtk.vtkAssembly;
import vtk.vtkLineSource;
import vtk.vtkPolyData;
import vtk.vtkPolyDataMapper;
import vtk.vtkTubeFilter;

/**
 *
 * @author Ayman
 */
public class TwoBodyForceDisplayer implements ObjectDisplayerInterface {
    
    protected OpenSimContext openSimContext;
    protected vtkAssembly modelAssembly;
    protected Force force;
    private vtkActor actor;
    vtkLineSource lineSource;
    private vtkTubeFilter dFilter;
    private vtkPolyData baseShapePolyData=null;
    private double baseSize=0.1;
    private boolean visible=true;
    private Hashtable<LineGeometry, vtkLineSource> mapGeometryToDisplayGeometry = 
            new Hashtable<LineGeometry, vtkLineSource>();

    /** Creates a new instance of TwoBodyForceDisplayer */
    public TwoBodyForceDisplayer(Force force, vtkAssembly modelAssembly){
        this.force = force;
        this.modelAssembly = modelAssembly;
        openSimContext = OpenSimDB.getInstance().getContext(force.getModel());
        openSimContext.updateDisplayer(force);
        int numGeometryPieces = force.getDisplayer().countGeometry();
        // Create Actor for fObject in ground frame then add it
        if (numGeometryPieces > 0){
            actor = new vtkActor();
            vtkAppendPolyData forcePolyData = new vtkAppendPolyData();
            dFilter = new vtkTubeFilter();
            dFilter.SetRadius(baseSize);
            dFilter.SetNumberOfSides(8);
            for(int i=0; i<numGeometryPieces; i++){
                Geometry g=force.getDisplayer().getGeometry(i);
                LineGeometry ag = LineGeometry.dynamic_cast(g);
                if (ag != null){               
                    lineSource = new vtkLineSource();
                    double[] p1 = new double[3];
                    double[] p2 = new double[3];
                    ag.getPoints(p1, p2);
                    lineSource.SetPoint1(p1);
                    lineSource.SetPoint2(p2);
                    if (baseShapePolyData==null) baseShapePolyData=lineSource.GetOutput();
                    dFilter.SetInput(baseShapePolyData);                    
                    forcePolyData.SetInputConnection(dFilter.GetOutputPort());
                    mapGeometryToDisplayGeometry.put(ag, lineSource);
                }
            }
            vtkPolyDataMapper forceMapper = new vtkPolyDataMapper();
            forceMapper.SetInput(forcePolyData.GetOutput());
            actor.SetMapper(forceMapper);
            visible = (force.getDisplayer().getDisplayPreference()!=DisplayPreference.None);
            if (visible)
                modelAssembly.AddPart(actor);

        }
   } 

    void addGeometry() {
        openSimContext.updateDisplayer(force);
    }

    public vtkActor getActor() {
        return actor;
    }

    public void setModified() {
        actor.Modified();
        modelAssembly.Modified();
    }

    public void updateGeometry() {
     if(force.getDisplayer()==null) return;
     if (force.getDisplayer().getDisplayPreference()==DisplayPreference.None){
         modelAssembly.RemovePart(actor);
          setModified();
          visible = false;
          return;
     }
     visible = true;
     modelAssembly.AddPart(actor);
     Enumeration<LineGeometry> dispIter = mapGeometryToDisplayGeometry.keys();
      while(dispIter.hasMoreElements()){
         LineGeometry ag = dispIter.nextElement();
         double[] p1 = new double[3];
         double[] p2 = new double[3];
         ag.getPoints(p1, p2);
         vtkLineSource lineSource = mapGeometryToDisplayGeometry.get(ag);
         lineSource.SetPoint1(p1);
         lineSource.SetPoint2(p2);
      }
      setModified();
    }

    public OpenSimObject getOpenSimObject() {
        return force;
    }

    public vtkPolyData getBaseShapePolyData() {
        return baseShapePolyData;
    }

    public void setBaseShapePolyData(vtkPolyData baseShapePolyData) {
        this.baseShapePolyData = baseShapePolyData;
    }

    public double getBaseSize() {
        return baseSize;
    }

    public void setBaseSize(double baseSize) {
        this.baseSize = baseSize;
    }


}
