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
import org.opensim.modeling.DecorativeGeometry;

/**
 *
 * @author ayman
 */
public interface ColorableInterface {
    
    public Boolean getVisible();
    public void setVisible(Boolean newValue);
    
    Color getColor();
    void setColor(Color newColor);

    int getDisplayPreference();
    void setDisplayPreference(int newPref);
    
    public double getOpacity();
    public void setOpacity(double opacity);
}
