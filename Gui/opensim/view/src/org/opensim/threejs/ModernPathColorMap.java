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
 * Default ColorMap for earlier versions of OpenSim red-to-blue 0-1
 *  RGB components scales linearly
 */
public class ModernPathColorMap implements PathColorMap {
    double weight = 0.0;
    
    void updateWeight (){
        String weightString="0.";
        String saved=Preferences.userNodeForPackage(TheApp.class).get("ColorMap Weight", weightString);
        Preferences.userNodeForPackage(TheApp.class).put("ColorMap Weight", saved);
        weight = Double.parseDouble(saved);
    }

    @Override
    public Vec3 getColor(GeometryPath path, State state) {
        updateWeight ();
        Vec3 activationBasedColor = path.getColor(state);
        double redness = activationBasedColor.get(0);
        Vec3 mappedColor = new Vec3(0.6435 + 0.07588*redness,
            0.7009 - 0.6396*redness,
            0.8554 - 0.6891*redness);
        Vec3 rawColor = new Vec3(redness, 0, 1-redness);
        Vec3 weightedColor = new Vec3();
        for (int i=0; i<3; i++)
            weightedColor.set(i, weight*rawColor.get(i)+(1 - weight)*mappedColor.get(i));
        return weightedColor;
    }
    
}
