/*
 * Selectable.java
 *
 * Created on April 9, 2009, 9:24 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.view;

import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimObject;
import vtk.vtkCaptionActor2D;

/**
 *
 * @author ayman
 */
public interface Selectable {
    double[] getBounds();

    OpenSimObject getOpenSimObject();

    Model getOwnerModel();

    String getStatusText();

    void markSelected(boolean highlight);

    void updateAnchor(vtkCaptionActor2D caption);
    
}
