/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.threejs;

import org.opensim.modeling.AbstractOutput;
import org.opensim.modeling.Geometry;
import org.opensim.modeling.OutputVec3;
import org.opensim.modeling.Sphere;
import org.opensim.modeling.Transform;

/**
 *
 * @author Ayman-NMBL
 */
public class ComVisualizerAddOn implements VisualizerAddOn {
    ModelVisualizationJson modelJson;
    Geometry sphereGeometry = new Sphere(.1);
    @Override
    public void init(ModelVisualizationJson modelJson) {
        this.modelJson = modelJson;
        AbstractOutput output = modelJson.getModel().getOutput("com_position");
        OutputVec3 outputVec3 = OutputVec3.safeDownCast(output);
        Transform xform = new Transform(outputVec3.getValue(modelJson.getState()));
        

    }

    @Override
    public void updateVisuals() {
    
    }

    @Override
    public void cleanup() {
    
    }
    
}
