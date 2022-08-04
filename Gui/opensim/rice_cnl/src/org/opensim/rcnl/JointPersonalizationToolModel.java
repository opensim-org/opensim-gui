/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.rcnl;

import org.opensim.modeling.AbstractProperty;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimObject;

/**
 *
 * @author Ayman-NMBL
 */
public class JointPersonalizationToolModel {

    private OpenSimObject toolAsObject;
    private String resultsDir = ".";
    private String inputDir = ".";
    private String outputModelFile = "";
    private OpenSimObject jointPersonalizationTaskListAsObject;
    private double accuracy=1e-5;
    private double diff_min_change= 1e-5;
    private double optimalityTolerance = 1e-10;
    private double functionTolerance = 1e-13;
    private int maxFunctionEvaluations = 100;
    private String modelName;
    private Model model;
    public JointPersonalizationToolModel(Model model) {
        // TODO in case plugin is not preloaded, guard against null return or exception thown
        toolAsObject = OpenSimObject.newInstanceOfType("JointModelPersonalizationTool");
        this.model = model;
        modelName = model.getName();
    }


    /**
     * @return the modelName
     */
    public String getModelName() {
        return modelName;
    }
    String getOutputModelFile() {
        return model.getInputFileName().replace(".osim", "_perjoint.osim");
    }

    /**
     * @return the toolAsObject
     */
    public OpenSimObject getToolAsObject() {
        return toolAsObject;
    }

    /**
     * @param toolAsObject the toolAsObject to set
     */
    public void setToolAsObject(OpenSimObject toolAsObject) {
        this.toolAsObject = toolAsObject;
    }
    
    public OpenSimObject getJointTaskListAsObject() {
        System.out.println(toolAsObject.dump());
        AbstractProperty ap = toolAsObject.getPropertyByName("JointPersonalizationTaskList");
        System.out.println(ap.getTypeName());
        return ap.getValueAsObject();
    }
    
}
