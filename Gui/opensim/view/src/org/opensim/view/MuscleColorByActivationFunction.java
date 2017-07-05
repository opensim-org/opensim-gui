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
public class MuscleColorByActivationFunction extends MuscleColoringFunction {
    final static double activationColorTau = 5;
    final static double activationColorFactor = 1/(1-Math.exp(-activationColorTau));
    OpenSimContext dContext;
    MuscleColorByActivationFunction(OpenSimContext context){
        dContext = context;
    }
    @Override
    public double getColor(Muscle msl){
        double color = dContext.getActivation(msl);
        // Apply this transfer function to get better results from the color map
        color = activationColorFactor * (1-Math.exp(-activationColorTau*color));
        //dContext.getStateVariable(msl, "activation");
        return color;
    }
}
