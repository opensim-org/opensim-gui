/*
 * ObjectDisplayerInterface.java
 *
 * Created on July 27, 2011, 7:38 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.view;

import org.opensim.modeling.OpenSimObject;

/**
 *
 * @author Ayman
 */
public interface ObjectDisplayerInterface {
    void setModified();

    void updateGeometry();
    
    OpenSimObject getOpenSimObject();

    public void updateFromProperties();
    
}
