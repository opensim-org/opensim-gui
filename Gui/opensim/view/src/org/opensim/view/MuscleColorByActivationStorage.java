/* -------------------------------------------------------------------------- *
 * OpenSim: MuscleColorByActivationStorage.java                               *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib                                                     *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may    *
 * not use this file except in compliance with the License. You may obtain a  *
 * copy of the License at http://www.apache.org/licenses/LICENSE-2.0          *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 * -------------------------------------------------------------------------- */
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
    public double getColor(Muscle msl){
        double displayTime = dContext.getTime();
        int aTimeIndex = activationStorage.findIndex(0, displayTime);
        StateVector actData = activationStorage.getStateVector(aTimeIndex);
        //System.out.println("coloring by data at time ="+actData.getTime()+" sim time = "+dContext.getTime());
        
        int idx = activationLabels.findIndex(msl.getName())-1;
        if (idx < 0) 
            return UNUSED_MSL;    // indicate no activation found
        double color = actData.getData().get(idx);
        // Apply this transfer function to get better results from the color map
        color = activationColorFactor * (1-Math.exp(-activationColorTau*color));
        //dContext.getStateVariable(msl, "activation");
        return color;
    }
    public static final double UNUSED_MSL = -2.0;
}
