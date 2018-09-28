/* -------------------------------------------------------------------------- *
 * OpenSim: ViewDBDescriptor.java                                             *
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
 * ViewDBDescriptor.java
 *
 * Created on May 2, 2008, 9:55 AM
 *
 */

package org.opensim.view.pub;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Ayman
 *
 * This class contains a short, more "textual" representation of ViewDB for serialization on entry/exit.
 * If we want to serialize to an XML file this class should implement Externalizable instead and follow Beans convention.
 *
 * Want to serialize: Camera position for each 3DWiew window
 *                    Model display offset for each model on display
 */
public class ViewDBDescriptor implements Serializable {
    
    private ArrayList<String> viewNames=new ArrayList<String>(5);
    private ArrayList<double[]> cameraAttributes=new ArrayList<double[]>(5);
    private ArrayList<double[]> offsetsList=new ArrayList<double[]>(5);
    private static final long serialVersionUID = 1L;
    /** Creates a new instance of ViewDBDescriptor */
    public ViewDBDescriptor() {
    }
    
    public ViewDBDescriptor(ViewDB viewDB) {

    }

    public ArrayList<String> getViewNames() {
        return viewNames;
    }

    public void setViewNames(ArrayList<String> viewNames) {
        this.viewNames = viewNames;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
    class CameraAttributes {
        double[] attributes = new double[6];
    }

    public ArrayList<double[]> getCameraAttributes() {
        return cameraAttributes;
    }

    public void setCameraAttributes(ArrayList<double[]> cameraAttributes) {
        this.cameraAttributes = cameraAttributes;
    }

    public ArrayList<double[]> getOffsetsList() {
        return offsetsList;
    }

    public void setOffsetsList(ArrayList<double[]> offsetsList) {
        this.offsetsList = offsetsList;
    }

}
