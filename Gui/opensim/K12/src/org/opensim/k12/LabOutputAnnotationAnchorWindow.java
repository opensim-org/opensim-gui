/* -------------------------------------------------------------------------- *
 * OpenSim: LabOutputAnnotationAnchorWindow.java                              *
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
 * LabOutputAnnotationAnchorWindow.java
 *
 * Created on August 31, 2010, 6:26 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.k12;

import org.opensim.view.pub.ViewDB;
import vtk.vtkActor2D;
import vtk.vtkCornerAnnotation;

/**
 *
 * @author Ayman
 */
public class LabOutputAnnotationAnchorWindow extends LabOutputAnnotation {
    
    vtkCornerAnnotation cornerAnnotation=null;
    int locationInt=0;
    /** Creates a new instance of LabOutputAnnotationAnchorWindow */
    public LabOutputAnnotationAnchorWindow(LabOutputTextToWindow labOutputTextToWindow) {
        super(labOutputTextToWindow);
        locationInt = mapLocationStringToInt(labOutputTextToWindow.getLocation());
    }

    void updateText(final String newText) {
        cornerAnnotation.ClearAllTexts();
        cornerAnnotation.SetText(locationInt, newText);
    }

    vtkActor2D getAnnotationActor() {
        if (cornerAnnotation== null){
            cornerAnnotation = new vtkCornerAnnotation();
            ViewDB.getInstance().addAnnotationToViews(cornerAnnotation);
        }
        return cornerAnnotation;
    }

    private int mapLocationStringToInt(String locationString) {
        if (locationString.equalsIgnoreCase("UpperLeft"))
            return 2;
        else if (locationString.equalsIgnoreCase("UpperRight"))
            return 3;
        else if (locationString.equalsIgnoreCase("LowerRight"))
            return 1;
        else // default is LowerLeft
            return 0;
    }

    public void cleanup() {
        ViewDB.getInstance().removeAnnotationFromViews(cornerAnnotation);
    }


    
}
