/* -------------------------------------------------------------------------- *
 * OpenSim: DecorativeGeometryDisplayer.java                                  *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib                                                     *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may    *
 * not use this file except in compliance with the License. You may obtain a  *
 * copy of the License at http://www.apache.org/licenses/LICENSE-2.0          *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 * -------------------------------------------------------------------------- */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.view;

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
        DecorativeGeometry.Representation pref = DecorativeGeometry.Representation.swigToEnum(prefInt);
        // Show vs. HideDisplayGeometry
        if (pref == DecorativeGeometry.Representation.Hide) {
            SetVisibility(0);
            return;
        }
        SetVisibility(1);
        if (pref == DecorativeGeometry.Representation.DrawWireframe) {
            GetProperty().SetRepresentationToWireframe();
        } else {
            GetProperty().SetRepresentationToSurface();
            if (pref == DecorativeGeometry.Representation.DrawSurface) {
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
        if (representation==Representation.Hide){
            SetVisibility(0);
            return;
        }
        SetVisibility(1);
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
            Component mc = Component.safeDownCast(getObj());
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

    abstract int getIndexOnBody();

    abstract void updateGeometry(DecorativeGeometry arg);
    
}
