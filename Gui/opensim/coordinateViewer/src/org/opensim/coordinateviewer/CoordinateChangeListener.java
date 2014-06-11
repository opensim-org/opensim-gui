/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.coordinateviewer;

import org.opensim.modeling.Coordinate;

/**
 * Interface coordinate the coordinates (pun intended) when changes happen on the GUI.
 * @author Kevin Xu
 */
public interface CoordinateChangeListener {
    
    public void valueChanged(Coordinate coord, double newValue, boolean setText, boolean setSlider, boolean setCoordinate, boolean updateDisplay);
    
}
