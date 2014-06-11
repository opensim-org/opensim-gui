/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.view;

import org.opensim.modeling.*;
import vtk.*;

/**
 *
 * @author ayman
 */
public abstract class ObjectDisplayer extends vtkActor {
    private OpenSimObject obj;

    public ObjectDisplayer(OpenSimObject object) {
        obj = object;
    }

    /**
     * Apply user display preference (None, wireframe, shading)
     */
    protected void applyDisplayPrefs(OpenSimObject object) {
        AbstractProperty ap = obj.getPropertyByName("display_preference");
        if (ap == null) return;
        int prefInt = PropertyHelper.getValueInt(ap);
        DisplayGeometry.DisplayPreference pref = DisplayGeometry.DisplayPreference.swigToEnum(prefInt);
        // Show vs. HideDisplayGeometry
        if (pref == DisplayGeometry.DisplayPreference.None) {
            SetVisibility(0);
            return;
        }
        SetVisibility(1);
        if (pref == DisplayGeometry.DisplayPreference.WireFrame) {
            GetProperty().SetRepresentationToWireframe();
        } else {
            GetProperty().SetRepresentationToSurface();
            if (pref == DisplayGeometry.DisplayPreference.FlatShaded) {
                GetProperty().SetInterpolationToFlat();
            } else {
                GetProperty().SetInterpolationToGouraud();
            }
        }
    }

    abstract void updateFromProperties();

    protected void updatePropertiesForPolyData(vtkPolyData polyData) {
        addPolyDataToActorApplyProperties(polyData, obj.getDisplayer());
        // Color/shading
        if (obj.hasProperty("color")) {
            AbstractProperty ap = obj.getPropertyByName("color");
            double[] dColor = new double[]{PropertyHelper.getValueDouble(ap, 0), PropertyHelper.getValueDouble(ap, 1), PropertyHelper.getValueDouble(ap, 2)};
            GetProperty().SetColor(dColor);
        } else {
            GetProperty().SetColor(new double[]{0.0, 1.0, 1.0});
        }
        applyDisplayPrefs(obj);
        Modified();
    }

    protected void addPolyDataToActorApplyProperties(final vtkPolyData meshPoly, final VisibleObject visibleObject) {
        // Move rep to proper location
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
        SetMapper(mapper);
    }

    /**
     * @return the obj
     */
    public OpenSimObject getObj() {
        return obj;
    }
    
}
