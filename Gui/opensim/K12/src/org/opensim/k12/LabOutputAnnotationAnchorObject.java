/* -------------------------------------------------------------------------- *
 * OpenSim: LabOutputAnnotationAnchorObject.java                              *
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
 * LabOutputAnnotationAnchorObject.java
 *
 * Created on August 31, 2010, 6:26 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.k12;

import java.io.IOException;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimObject;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;
import vtk.vtkActor2D;
import vtk.vtkCaptionActor2D;
import vtk.vtkTextProperty;

/**
 *
 * @author Ayman
 */
public class LabOutputAnnotationAnchorObject extends LabOutputAnnotation {
    
    vtkCaptionActor2D caption=null;
    OpenSimObject dObject;
    double[] offset = new double[3];
    int fontSize =12;
    /** Creates a new instance of LabOutputAnnotationAnchorObject */
    public LabOutputAnnotationAnchorObject(LabOutputTextToObject labOutputTextToObject) throws IOException {
        super(labOutputTextToObject);
        Model mdl=OpenSimDB.getInstance().getCurrentModel();
        dObject = mdl.getObjectByTypeAndName(labOutputTextToObject.getOpenSimType(), labOutputTextToObject.getObjectName());
        //offset = labOutputTextToObject.getOffset();
        fontSize = labOutputTextToObject.getFontSize();
        if (fontSize==0) fontSize=12;
    }

    void updateText(final String newText) {
        caption.SetCaption(newText);
    }

    vtkActor2D getAnnotationActor() {
        if (caption== null){
            caption = new vtkCaptionActor2D();
            caption.GetTextActor().ScaledTextOff();
            caption.GetCaptionTextProperty().SetFontSize(fontSize);
            //caption.GetCaptionTextProperty().SetColor(0.0, 1.0, 0.);
            caption.BorderOff();
            ViewDB.getInstance().addObjectAnnotationToViews(caption, dObject);
        }
        return caption;
    }

    public void cleanup() {
        ViewDB.getInstance().removeObjectAnnotationFromViews(caption);
    }
    
}
