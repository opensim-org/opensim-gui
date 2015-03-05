/*
 * ColorableInterface.java
 *
 * Created on July 27, 2010, 2:36 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.view;

import java.awt.Color;
import org.opensim.modeling.Geometry;
import org.opensim.modeling.Geometry.DisplayPreference;

/**
 *
 * @author ayman
 */
public interface ColorableInterface {
    Color getColor();

    void setColor(Color newColor);

    Geometry.DisplayPreference getDisplayPreference();

    void setDisplayPreference(Geometry.DisplayPreference newPref);
    
}
