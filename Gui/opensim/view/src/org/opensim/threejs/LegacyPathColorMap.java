/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.threejs;

import org.opensim.modeling.GeometryPath;
import org.opensim.modeling.State;
import org.opensim.modeling.Vec3;
import org.opensim.view.MuscleColorByActivationStorage;

/**
 *
 * @author Ayman-NMBL
 * 
 * Default ColorMap for earlier versions of OpenSim red-to-blue 0-1
 *  RGB components scales linearly
 */
public class LegacyPathColorMap implements PathColorMap {

    @Override
    public Vec3 getColor(GeometryPath path, State state, double activation) {
        if (activation > 0.){
            return new Vec3(activation, 0, 1-activation);
        }
        else if (activation == MuscleColorByActivationStorage.UNUSED_MSL){ // unused show as outside red-blue spectrum
            return new Vec3(0.8, 0.8, 0.8);
            
        }
        return path.getColor(state);
    }
    
}
