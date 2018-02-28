/* -------------------------------------------------------------------------- *
 * OpenSim: ModelForExperimentalData.java                                     *
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
 * 
 * ModelForExperimentalData.java
 *
 * Created on Feb 23, 09
 *
 */

package org.opensim.view.experimentaldata;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
import org.opensim.modeling.*;
import org.opensim.utils.TheApp;

/**
 *
 * @author ayman
 *
 * A fake model to be used for data import. 
 * has 0 states.
 * potentially one body (ground)
 * empty or a frame visuals
 */
public class ModelForExperimentalData extends Model{
    
    private ArrayList<ExperimentalMarker> experimentalMarkers = new ArrayList<ExperimentalMarker>();
    private ArrayList<MotionObjectPointForce> experimentalForces = new ArrayList<MotionObjectPointForce>();
    private Ground    ground;
    SimbodyEngine dEngine;
    private AnnotatedMotion motionData;
    /**
     * Creates a new instance of ModelForExperimentalData
     */
    public ModelForExperimentalData(int i, AnnotatedMotion motionData) {
        super();
        setName("ExperimentalData_"+i);
        this.motionData=motionData;
        //setup();
        dEngine = this.getSimbodyEngine();
        ground = this.get_ground();
        this.get_ModelVisualPreferences().get_ModelDisplayHints().set_show_frames(true);
        ground.get_frame_geometry().get_Appearance().set_visible(true);
        // blank filename to make sure it doesn't get overwritten
        this.setInputFileName("");
       
   }
    
    public void addMotionObjects(Vector<ExperimentalDataObject> motionObjects)
    {
        for (int i=0; i<motionObjects.size(); i++){
            ExperimentalDataObject nextMotionObject = motionObjects.get(i);
            if (nextMotionObject instanceof ExperimentalMarker)
                this.experimentalMarkers.add((ExperimentalMarker)nextMotionObject);
            else if (nextMotionObject instanceof MotionObjectPointForce)
                this.experimentalForces.add((MotionObjectPointForce)nextMotionObject);
        }
    }

    public AnnotatedMotion getMotionData() {
        return motionData;
    }

    private void setMotionData(AnnotatedMotion motionData) {
        this.motionData = motionData;
    }

    public Ground getGround() {
        return ground;
    }

    public SimbodyEngine getSimbodyEngine() {
        return dEngine;
    }

    /**
     * @return the experimentalMarkers
     */
    public ArrayList<ExperimentalMarker> getExperimentalMarkers() {
        return experimentalMarkers;
    }

    /**
     * @return the experimentalForces
     */
    public ArrayList<MotionObjectPointForce> getExperimentalForces() {
        return experimentalForces;
    }
}
