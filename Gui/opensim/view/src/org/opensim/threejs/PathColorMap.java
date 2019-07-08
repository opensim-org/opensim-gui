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
 */
public interface PathColorMap {
    // Color is either extracted from state or computed from providedActivation
    // if providedActivation > 0.0 it takes precedence otherwise it's ignored
    abstract Vec3 getColor(GeometryPath path, State state, double provideActivation);
}
