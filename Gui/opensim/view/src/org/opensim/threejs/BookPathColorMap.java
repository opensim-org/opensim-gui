/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.threejs;

import java.util.prefs.Preferences;
import org.opensim.modeling.GeometryPath;
import org.opensim.modeling.State;
import org.opensim.modeling.Vec3;
import org.opensim.utils.TheApp;

/**
 *
 * @author Ayman-NMBL
 * 
 * Color map used to generate figures for the book
 */
public class BookPathColorMap implements PathColorMap {

    @Override
    public Vec3 getColor(GeometryPath path, State state, double activation) {
        //updateWeight ();

        Vec3 activationBasedColor = path.getColor(state);
        if (activation > 0){
            activationBasedColor = new Vec3(activation, 0, 1-activation);
        }
        double act = activationBasedColor.get(0);
        
        activationBasedColor.set(0,rChannel(act));
        activationBasedColor.set(1,gChannel(act));
        activationBasedColor.set(2,bChannel(act));
        return activationBasedColor;
    }
    double rChannel(double a){
        return (a <= 0.3) ? (-0.299*a + 0.6779) : (a*(-0.1525*a + 0.3655) + 0.4923);
    }
    double gChannel(double a){
        return (a*(a*(-0.6125*a + 0.9872) - 1.026) + 0.7824);
    }
    double bChannel(double a){
        return (a*(0.2436*a - 0.957) + 0.8963);
    }

}

