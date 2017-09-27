/* -------------------------------------------------------------------------- *
 * OpenSim: ModelVisualsVtk.java                                              *
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
package org.opensim.view.pub;

import org.opensim.modeling.Actuator;
import org.opensim.modeling.Marker;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimObject;
import org.opensim.view.MarkersDisplayer;
import org.opensim.view.OpenSimvtkGlyphCloud;
import vtk.vtkActor;
import vtk.vtkAssembly;
import vtk.vtkAssemblyPath;
import vtk.vtkLinearTransform;
import vtk.vtkProp3D;

/**
 *
 * @author Ayman
 * 
 * Inteface to separate the construction of visuals from later tasks (picking/hiding etc.)
 * Eventua;lly this should be more abstract than vtk to allow for a different visualizer
 * 
 */
public interface ModelVisualsVtk {

    void addUserObject(vtkActor userObj);

    void cleanup();

    /**
     * @return the bounds
     */
    double[] getBounds();

    double[] getBoundsBodiesOnly();

    /**
     * return a reference to the vtkAssembly representing the model
     */
    vtkAssembly getModelDisplayAssembly();

    vtkLinearTransform getModelDisplayTransform();

    /**
     * find the Object for passed in vtkProp3D
     */
    OpenSimObject getObjectForVtkRep(vtkProp3D prop);

    /**
     * Since there's no global model opacity, we'll take the opacity of the first object
     * we find and use it as value for the Opacity of he model. This has the advantage that
     * if Opacities are the same for all objects then the behavior is as expected.
     */
    double getOpacity();

    /**
     * Find the vtkProp3D for the passed in object
     */
    vtkProp3D getVtkRepForObject(OpenSimObject obj);

    OpenSimObject pickObject(vtkAssemblyPath asmPath);

    void removeGeometry(OpenSimObject object);

    void removeUserObject(vtkActor userObj);

    void setOpacity(double opacity);

    void transformModelToWorldBounds(double[] bounds);

    /**
     * Compute bounding box for model as this can be useful for initial placement
     * This is supposed to be a ballpark rather than an accurate estimate so that minor changes to
     * model do not cause overlap, but the bboox is not intended to be kept up-to-date
     * unused and turned out to be very slow for some reason
     * private void computeModelBoundingbox() {
     * modelDisplayAssembly.GetBounds(bounds);
     * }
     */
    void transformModelToWorldPoint(double[] point);

    /**
     * updateModelDisplay with new transforms cached in animationCallback.
     * This method must be optimized since it's invoked during live animation
     * of simulations and/or analyses (ala IK).
     */
    void updateModelDisplay(Model model);

    void updateObjectDisplay(OpenSimObject specificObject);

    public void setPickable(boolean b);

    public boolean isVisible();

    public void setVisible(boolean b);

    public void setShowCOM(boolean newState);

    public boolean isShowCOM();

    public void setApplyMuscleColors(boolean render);
    
}
