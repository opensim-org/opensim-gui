/* -------------------------------------------------------------------------- *
 * OpenSim: DecorativeFrameDisplayer.java                                     *
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

import org.opensim.modeling.DecorativeFrame;
import org.opensim.modeling.DecorativeGeometry;
import org.opensim.modeling.OpenSimObject;
import vtk.vtkActor;
import vtk.vtkAxes;
import vtk.vtkAxesActor;
import vtk.vtkPolyData;
import vtk.vtkTubeFilter;

/**
 *
 * @author Ayman
 */
class DecorativeFrameDisplayer extends DecorativeGeometryDisplayer {

    private final DecorativeFrame ag;
    vtkAxes frameSrc = new vtkAxes();
    private final vtkTubeFilter dFilter = new vtkTubeFilter();

    public DecorativeFrameDisplayer(DecorativeFrame arg0) {
        //super(object);
        this.ag = arg0.clone();
        //if (ag.hasUserRef()) setObj(ag.getUserRefAsObject());

    }

    private vtkPolyData getPolyData(DecorativeFrame ag) {
        frameSrc.SetScaleFactor(ag.getAxisLength());
        dFilter.SetInput(frameSrc.GetOutput());
        dFilter.SetRadius(ag.getLineThickness());
        dFilter.SetNumberOfSides(16);
        return dFilter.GetOutput();
    }
    
    @Override
    void updateDisplayFromDecorativeGeometry() {
        vtkPolyData polyData = getPolyData(ag);
        //updatePropertiesForPolyData(polyData);
        createAndConnectMapper(polyData);
        setXformAndAttributesFromDecorativeGeometry(ag);
    }

    @Override
    vtkActor getVisuals() {
        updateDisplayFromDecorativeGeometry();
        return this;
    }
 
    @Override
    int getBodyId() {
        return ag.getBodyId();
    }
    @Override
    int getIndexOnBody() {
        return ag.getIndexOnBody();
    }
    
    @Override
    DecorativeGeometry getDecorativeGeometry() {
        return ag;
    }   
    
    @Override
    void updateGeometry(DecorativeGeometry arg) {
        DecorativeFrame arg0 = (DecorativeFrame) arg;
        ag.setAxisLength(arg0.getAxisLength());
        ag.setLineThickness(arg0.getLineThickness());
        updateDisplayFromDecorativeGeometry();
    }

}
