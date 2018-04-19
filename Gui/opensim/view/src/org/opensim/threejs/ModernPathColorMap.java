/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.threejs;

import org.opensim.modeling.GeometryPath;
import org.opensim.modeling.State;
import org.opensim.modeling.Vec3;

/**
 *
 * @author Ayman-NMBL
 * 
 * Default ColorMap for earlier versions of OpenSim red-to-blue 0-1
 *  RGB components scales linearly
 */
public class ModernPathColorMap implements PathColorMap {

    @Override
    public Vec3 getColor(GeometryPath path, State state) {
        Vec3 activationBasedColor = path.getColor(state);
        double redness = activationBasedColor.get(0);
        Vec3 mappedColor = new Vec3(0.6435 + 0.07588*redness,
            0.7009 - 0.6396*redness,
            0.8554 - 0.6891*redness);
        return mappedColor;
    }
    
}
