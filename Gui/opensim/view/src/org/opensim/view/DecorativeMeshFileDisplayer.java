/* -------------------------------------------------------------------------- *
 * OpenSim: DecorativeMeshFileDisplayer.java                                  *
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

package org.opensim.view;

import java.awt.Color;
import org.opensim.modeling.*;
import org.opensim.view.pub.GeometryFileLocator;
import vtk.vtkActor;
import vtk.vtkPolyData;

public class DecorativeMeshFileDisplayer extends DecorativeGeometryDisplayer {
    private DecorativeMeshFile ag;
    private String modelFilePath;
    protected OpenSimObject obj;
    /** 
     * Displayer for Wrap Geometry
     * @param ag
     * @param object 
     */
    DecorativeMeshFileDisplayer(DecorativeMeshFile ag, 
            String modelFilePath) {
        this.ag = ag.clone();
        this.modelFilePath = modelFilePath;
        //if (ag.hasUserRef()) setObj(ag.getUserRefAsObject());
     }

    /**
     * Convert DecorativeGeometry object passed in to the corresponding vtk polyhedral representation.
     * Transform is passed in as well since the way it applies to PolyData depends on source
     */
    private vtkPolyData getPolyData(DecorativeMeshFile ag) {
        String boneFile = GeometryFileLocator.getInstance().getFullname(modelFilePath,ag.getMeshFile(), false);
        if (boneFile==null) return null;
        return GeometryFactory.populatePolyDataFromFile(boneFile, this);
    }

    @Override
    void updateDisplayFromDecorativeGeometry() {
         setXformAndAttributesFromDecorativeGeometry(ag);
    }

    private void createDisplayFromDecorativeGeometry() {
        vtkPolyData polyData = getPolyData(ag);
        //updatePropertiesForPolyData(polyData);
        createAndConnectMapper(polyData);
    }

    
    public void setColorGUI(Color newColor) {
        float[] floats = new float[3];
        newColor.getRGBColorComponents(floats);
        // Push change to object

    }

    public Color getColor() {
        Vec3 clr = ag.getColor();
        return new Color((float)clr.get(0), (float)clr.get(1), (float)clr.get(2));
    }

    @Override
    vtkActor getVisuals() {
        createDisplayFromDecorativeGeometry();
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
        DecorativeMeshFile arg0 = (DecorativeMeshFile) arg;
        if (!arg0.getMeshFile().equalsIgnoreCase(ag.getMeshFile())){
            ag = arg0.clone();
            createDisplayFromDecorativeGeometry();
        }
    }
}
