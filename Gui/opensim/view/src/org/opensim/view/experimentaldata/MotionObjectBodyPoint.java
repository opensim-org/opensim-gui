/* -------------------------------------------------------------------------- *
 * OpenSim: MotionObjectBodyPoint.java                                        *
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

import java.util.ArrayList;
import java.util.UUID;
import org.json.simple.JSONObject;
import org.opensim.modeling.Sphere;
import org.opensim.view.motions.MotionDisplayer;

/**
 *
 * @author ayman
 */

/*
 * A class representing a point fixed to a body read from a data/motion file. 
 */
public class MotionObjectBodyPoint extends ExperimentalDataObject {
    protected double[] point = new double[]{0, 0, 0};
    Sphere sphere = new Sphere(.01);
    public MotionObjectBodyPoint(ExperimentalDataItemType objectType, String baseName, int index) {
        super(objectType, baseName, index);
        setPointIdentifier(baseName);
        //FIXME DEVWEEK sphere.setFrameName("ground");
        // Will get point and set it as Sphere.center, this's a hack
    }
    protected String pointExpressedInBody = "ground";
    private String pointIdentifier="";
    /**
     * Get the value of pointExpressedInBody
     *
     * @return the value of pointExpressedInBody
     */
    public String getPointExpressedInBody() {
        return pointExpressedInBody;
    }

    /**
     * Set the value of pointExpressedInBody
     *
     * @param pointExpressedInBody new value of pointExpressedInBody
     */
    public void setPointExpressedInBody(String bodyName) {
         this.pointExpressedInBody = bodyName;
    }


    public void setPointIdentifier(String makeIdentifier) {
        pointIdentifier = makeIdentifier;
    }

    public String getPointIdentifier() {
        return pointIdentifier;
    }


    /**
     * @return the point
     */
    public double[] getPoint() {
        return point;
    }

    /**
     * @param point the point to set
     */
    public void setPoint(double[] point) {
        this.point = point;
    }

    boolean appliesForce() {
        return false;
    }
}
