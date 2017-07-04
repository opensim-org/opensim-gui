/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.view;

import org.opensim.modeling.Muscle;
import org.opensim.modeling.OpenSimContext;

/**
 *
 * @author ayman
 */
public class MuscleNoColoringFunction extends MuscleColoringFunction{
    final static double activationColorTau = 5;
    final static double activationColorFactor = 1/(1-Math.exp(-activationColorTau));
    OpenSimContext dContext;
    MuscleNoColoringFunction(OpenSimContext context){
        dContext = context;
    }
    @Override
    public double getColor(Muscle msl){
        double color = 1.0;
        return color;
    }
}
