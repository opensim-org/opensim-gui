/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.view;

import java.awt.Color;
import org.opensim.modeling.*;
import org.opensim.modeling.DecorativeGeometry.Representation;
import vtk.*;

/**
 *
 * @author ayman
 * 
 * This class serves as base class to all "Displayer" classes that put DecorativeGeometry on the screen.
 * Common behavior (e.g. Show, Hide, Color, Opacity, etc. should be implemnted here rather in concrete classes
 * 
 */
public abstract class DecorativeGeometryDisplayer extends vtkActor {
    private OpenSimObject obj=null;
    vtkPolyDataMapper mapper = new vtkPolyDataMapper();

    public DecorativeGeometryDisplayer() {
     }

    /**
     * Apply user display preference (None, wireframe, shading)
     */
    protected void applyDisplayPrefs(OpenSimObject object) {
        AbstractProperty ap = getObj().getPropertyByName("display_preference");
        if (ap == null) return;
        int prefInt = PropertyHelper.getValueInt(ap);
        Geometry.Representation pref = Geometry.Representation.swigToEnum(prefInt);
        // Show vs. HideDisplayGeometry
        if (pref == Geometry.Representation.Hide) {
            SetVisibility(0);
            return;
        }
        SetVisibility(1);
        if (pref == Geometry.Representation.DrawWireframe) {
            GetProperty().SetRepresentationToWireframe();
        } else {
            GetProperty().SetRepresentationToSurface();
            if (pref == Geometry.Representation.DrawSurface) {
                GetProperty().SetInterpolationToFlat();
            } else {
                GetProperty().SetInterpolationToGouraud();
            }
        }
    }

    abstract void updateDisplayFromDecorativeGeometry();

    /**
     * @return the obj
     */
    public OpenSimObject getObj() {
        return obj;
    }

    abstract DecorativeGeometry getDecorativeGeometry();
    
    private void setColorFromDecorativeGeometry(DecorativeGeometry cs) {
        final Vec3 color = cs.getColor();
        GetProperty().SetColor(color.get(0), color.get(1), color.get(2));
    }

    private void setScaleFromDecorativeGeometry(DecorativeGeometry cs) {
        Vec3 scales = cs.getScaleFactors();
        //System.out.println("Set scale to:"+scales.toString());

        if (scales.get(0)>0.0)
            SetScale(scales.get(0), scales.get(1), scales.get(2));
    }

    private void setTransformFromDecorativeGeometry(DecorativeGeometry cs) {
        Transform xform = cs.getTransform();
        Rotation rot = xform.R();
        Vec3 threeAngles = rot.convertRotationToBodyFixedXYZ();
        SetOrientation(0, 0, 0);
        //System.out.println("Set rotation to:"+rot.toString());
        if (!threeAngles.isNumericallyEqual(0.)){
            RotateX(threeAngles.get(0)*180.0/Math.PI);
            RotateY(threeAngles.get(1)*180.0/Math.PI);
            RotateZ(threeAngles.get(2)*180.0/Math.PI);
        }
        Vec3 trans = xform.p();
        //System.out.println("Set translation to:"+trans.toString());
        SetPosition(trans.get(0), trans.get(1), trans.get(2));
    }

    protected void setXformAndAttributesFromDecorativeGeometry(DecorativeGeometry cs) {
        setTransformFromDecorativeGeometry(cs);
        setScaleFromDecorativeGeometry(cs);
        setColorFromDecorativeGeometry(cs);
        setRepresentationFromDecorativeGeometry(cs);
        setOpacityFromDecorativeGeometry(cs);
    }

    protected void createAndConnectMapper(vtkPolyData polyData) {
        //updatePropertiesForPolyData(polyData);
        mapper.SetInput(polyData);
        SetMapper(mapper);
    }

    private void setRepresentationFromDecorativeGeometry(DecorativeGeometry cs) {
        Representation representation = cs.getRepresentation();
        if (representation==Representation.DrawSurface)
            GetProperty().SetRepresentationToSurface();
        else if (representation==Representation.DrawWireframe)
            GetProperty().SetRepresentationToWireframe();
        else if (representation==Representation.DrawPoints)
            GetProperty().SetRepresentationToPoints();
        else if (representation==Representation.DrawDefault)
            GetProperty().SetRepresentationToSurface();            
    }

    private void setOpacityFromDecorativeGeometry(DecorativeGeometry cs) {
        if (cs.getOpacity()>=0. && cs.getOpacity() <= 1.0)
            GetProperty().SetOpacity(cs.getOpacity());
    }

    abstract vtkActor getVisuals();

    void updateDecorativeGeometryFromObject() {
        // if Object has properties for transform, color, pref, scale then update DG
        Geometry dg = Geometry.safeDownCast(getObj());
        if (dg == null){
            ModelComponent mc = ModelComponent.safeDownCast(getObj());
        }

    }

    void copyAttributesFromDecorativeGeometry(DecorativeGeometry arg0) {
        setXformAndAttributesFromDecorativeGeometry(arg0);
    }

    abstract int getBodyId();

    /**
     * @param obj the obj to set
     */
    public void setObj(OpenSimObject obj) {
        this.obj = obj;
    }
}
