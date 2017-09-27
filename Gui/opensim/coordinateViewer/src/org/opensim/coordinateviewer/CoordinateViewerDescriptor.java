/* -------------------------------------------------------------------------- *
 * OpenSim: CoordinateViewerDescriptor.java                                   *
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
 * CoordinateViewerDescriptor.java
 *
 * Created on May 9, 2008, 11:42 AM
 *
 */

package org.opensim.coordinateviewer;

import java.util.ArrayList;
import org.opensim.modeling.Model;
import org.opensim.view.ModelPose;
import org.opensim.view.pub.OpenSimDB;

/**
 *
 * @author Ayman
 */
public class CoordinateViewerDescriptor {
    private ArrayList<ModelPose> poses = new  ArrayList<ModelPose>(5);
    /** Creates a new instance of CoordinateViewerDescriptor */
    public CoordinateViewerDescriptor(OpenSimDB opensimDB) {
        Object[] models = opensimDB.getAllModels();
        for (int i=0; i< models.length; i++){
            Model mdl = (Model) models[i];
            getPoses().add(i, new ModelPose(mdl.getCoordinateSet(), mdl.getName()));
        }
    }

    public CoordinateViewerDescriptor() {
    }
    
    public ArrayList<ModelPose> getPoses() {
        return poses;
    }

    public void setPoses(ArrayList<ModelPose> poses) {
        this.poses = poses;
    }
}
