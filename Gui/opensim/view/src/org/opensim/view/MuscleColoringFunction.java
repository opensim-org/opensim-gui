/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.view;

import org.opensim.modeling.Muscle;

/**
 *
 * @author ayman
 */
public abstract class MuscleColoringFunction {

    public MuscleColoringFunction() {
    }

    public abstract double getColor(Muscle msl);
    
}
