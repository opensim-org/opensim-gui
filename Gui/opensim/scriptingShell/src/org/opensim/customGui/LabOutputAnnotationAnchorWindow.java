/*
 * LabOutputAnnotationAnchorWindow.java
 *
 * Created on August 31, 2010, 6:26 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.customGui;

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
