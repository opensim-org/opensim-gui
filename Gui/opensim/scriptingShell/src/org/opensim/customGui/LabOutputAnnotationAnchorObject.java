/*
 * LabOutputAnnotationAnchorObject.java
 *
 * Created on August 31, 2010, 6:26 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.customGui;

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
