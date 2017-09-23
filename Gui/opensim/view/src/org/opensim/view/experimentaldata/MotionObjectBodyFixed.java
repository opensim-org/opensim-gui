/* -------------------------------------------------------------------------- *
 * OpenSim: MotionObjectBodyFixed.java                                        *
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
package org.opensim.view.experimentaldata;

import org.opensim.utils.Vec3;

/**
 *
 * @author ayman
 */
public class MotionObjectBodyFixed extends ExperimentalDataObject {

    public MotionObjectBodyFixed(ExperimentalDataItemType objectType, String baseName, int index) {
        super(objectType, baseName, index);
    }
    public static final String PROP_BODYNAME = "bodyName";
    protected String bodyName = "ground";
    protected Vec3 position = new Vec3(0, 0, 0);

    /**
     * Get the value of bodyName
     *
     * @return the value of bodyName
     */
    public String getBodyName() {
        return bodyName;
    }

    /**
     * Get the value of position
     *
     * @return the value of position
     */
    public Vec3 getPosition() {
        return position;
    }

    /**
     * Set the value of bodyName
     *
     * @param bodyName new value of bodyName
     */
    public void setBodyName(String bodyName) {
        String oldBodyName = this.bodyName;
        this.bodyName = bodyName;
    }

    /**
     * Set the value of position
     *
     * @param position new value of position
     */
    public void setPosition(Vec3 position) {
        this.position = position;
    }
    
}
