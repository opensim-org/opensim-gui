/* -------------------------------------------------------------------------- *
 * OpenSim: MotionObjectJointForce.java                                       *
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
package org.opensim.view.experimentaldata;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author ayman
 */
public class MotionObjectJointForce extends MotionObjectBodyPoint {

    protected String jointName;

    public MotionObjectJointForce(ExperimentalDataItemType objectType, String baseName, int index) {
        super(objectType, baseName, index);
    }

    /**
     * Get the value of jointName
     *
     * @return the value of jointName
     */
    public String getJointName() {
        return jointName;
    }

    /**
     * Set the value of jointName
     *
     * @param jointName new value of jointName
     */
    public void setJointName(String jointName) {
        this.jointName = jointName;
    }
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    void setForceExpressedInBodyName(String selected) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    void setForceIdentifier(String makeIdentifier) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    String getForceExpressedInBodyName() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    boolean appliesForce() {
        return true;
    }

    String getForceIdentifier() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
