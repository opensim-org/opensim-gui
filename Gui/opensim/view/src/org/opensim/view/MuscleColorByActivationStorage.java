/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.view;

import org.opensim.modeling.ArrayStr;
import org.opensim.modeling.Muscle;
import org.opensim.modeling.OpenSimContext;
import org.opensim.modeling.StateVector;
import org.opensim.modeling.Storage;

/**
 *
 * @author ayman
 */
public class MuscleColorByActivationStorage extends MuscleColoringFunction {
    final static double activationColorTau = 5;
    final static double activationColorFactor = 1/(1-Math.exp(-activationColorTau));
    Storage activationStorage;
    OpenSimContext dContext;
    ArrayStr activationLabels;
    public MuscleColorByActivationStorage(OpenSimContext context, Storage storage){
        dContext = context;
        activationStorage = storage;
        activationLabels = storage.getColumnLabels();
    }
    
    @Override
    double getColor(Muscle msl){
        double displayTime = dContext.getTime();
        int aTimeIndex = activationStorage.findIndex(0, displayTime);
        StateVector actData = activationStorage.getStateVector(aTimeIndex);
        //System.out.println("coloring by data at time ="+actData.getTime()+" sim time = "+dContext.getTime());
        
        int idx = activationLabels.findIndex(msl.getName())-1;
        if (idx <= 0) return 0;
        double color = actData.getData().get(idx);
        // Apply this transfer function to get better results from the color map
        color = activationColorFactor * (1-Math.exp(-activationColorTau*color));
        //dContext.getStateVariable(msl, "activation");
        return color;
    }
}
