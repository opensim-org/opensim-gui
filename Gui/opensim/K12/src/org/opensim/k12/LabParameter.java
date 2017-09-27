/* -------------------------------------------------------------------------- *
 * OpenSim: LabParameter.java                                                 *
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
 * LabParameter.java
 *
 * Created on August 6, 2010, 2:42 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.k12;

/**
 *
 * @author ayman
 */
public class LabParameter {
    
    private String openSimType;
    private String objectName;
    private String propertyName;
    private String propertyDisplayName;
    private int propertyComponent=-1;
    private double rangeMin=Double.NaN;
    private double rangeMax=Double.NaN;
    private String userInterface="Slider";
    
    /** Creates a new instance of LabParameter */
    public LabParameter() {
    }

    public String getOpenSimType() {
        return openSimType;
    }

    public void setOpenSimType(String OpenSimType) {
        this.openSimType = OpenSimType;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String ObjectName) {
        this.objectName = ObjectName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String PropertyName) {
        this.propertyName = PropertyName;
    }

    public String getPropertyDisplayName() {
        return propertyDisplayName;
    }

    public void setPropertyDisplayName(String propertyDisplayName) {
        this.propertyDisplayName = propertyDisplayName;
    }

    public int getPropertyComponent() {
        return propertyComponent;
    }

    public void setPropertyComponent(int propertyComponent) {
        this.propertyComponent = propertyComponent;
    }

    public double getRangeMin() {
        return rangeMin;
    }

    public void setRangeMin(double rangeMin) {
        this.rangeMin = rangeMin;
    }

    public double getRangeMax() {
        return rangeMax;
    }

    public void setRangeMax(double rangeMax) {
        this.rangeMax = rangeMax;
    }

    public void setUserInterface(Object object) {
        // In the future this will allow for specifying different JComponents
        userInterface = (String) object;
    }

    public Object getUserInterface() {
        return userInterface;
    }

    public String toString() {
        String retValue;
        
        retValue = "Input:"+userInterface.toString()+" "+openSimType+"."+objectName+"."+propertyName;
        return retValue;
    }
    
}
