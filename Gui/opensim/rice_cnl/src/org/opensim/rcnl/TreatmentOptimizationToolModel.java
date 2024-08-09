/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.rcnl;

import org.opensim.modeling.AbstractProperty;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.PropertyObjectList;
import org.opensim.modeling.PropertyStringList;

/**
 *
 * @author Ayman-NMBL
 */
public class TreatmentOptimizationToolModel {

    private OpenSimObject toolAsObject;
    private String resultsDir = ".";
    private String inputDir = ".";
    private String outputModelFile = "";
    private OpenSimObject jointPersonalizationTaskListAsVector;
    private double accuracy=1e-5;
    private double diff_min_change= 1e-5;
    private double optimalityTolerance = 1e-10;
    private double functionTolerance = 1e-13;
    private int maxFunctionEvaluations = 100;
    private String modelName;
    private Model model;
    private  PropertyStringList propOutputModelFileString;
    private PropertyStringList propInputModelFileString;
    public enum Mode { TrackingOptimization, VerificationOptimization, DesignOptimization };
    public Mode mode;
    
    public TreatmentOptimizationToolModel(Model model) {
        // TODO in case plugin is not preloaded, guard against null return or exception thown
        toolAsObject = OpenSimObject.newInstanceOfType("JointModelPersonalizationTool");
        this.model = model;
        modelName = model.getName();
        AbstractProperty propOutputModelFile = toolAsObject.updPropertyByName("output_model_file");
        propOutputModelFileString = PropertyStringList.getAs(propOutputModelFile);
        if (propOutputModelFileString.size()==0 || propOutputModelFileString.getValue(0).isEmpty()){
             String proposedName = model.getInputFileName().replace(".osim", "_JMP.osim");
             propOutputModelFileString.setValue(0, proposedName);
        }
        AbstractProperty propInputModelFile = toolAsObject.updPropertyByName("input_model_file");
        propInputModelFileString = PropertyStringList.getAs(propInputModelFile);
        if (propInputModelFileString.size()==0 || propInputModelFileString.getValue(0).isEmpty()){
             String proposedName = model.getInputFileName();
             propInputModelFileString.setValue(0, proposedName);
        }
    }
    public TreatmentOptimizationToolModel(Model model, String fileXml) {
        // TODO in case plugin is not preloaded, guard against null return or exception thown
        toolAsObject = OpenSimObject.makeObjectFromFile(fileXml);
        this.model = model;
        modelName = model.getName();
        AbstractProperty prop = toolAsObject.updPropertyByName("output_model_file");
        propOutputModelFileString = PropertyStringList.getAs(prop);
        if (propOutputModelFileString.size()==0|| propOutputModelFileString.getValue(0).isEmpty()){
            String proposedName = model.getInputFileName().replace(".osim", "_perjoint.osim");
            propOutputModelFileString.setValue(0, proposedName);
        }
        prop = toolAsObject.updPropertyByName("input_model_file");
        propInputModelFileString = PropertyStringList.getAs(prop);
        if (propInputModelFileString.size()==0 || propInputModelFileString.getValue(0).isEmpty()){
            String proposedName = model.getInputFileName();
            propInputModelFileString.setValue(0, proposedName);
        }
    }


    /**
     * @return the modelName
     */
    public String getModelName() {
        return modelName;
    }
    public String getInputModelFile() {
        return propInputModelFileString.getValue(0);
    }
    String getOutputModelFile() {
        return propOutputModelFileString.getValue(0);
    }
    void setOutputModelFile(String newFileName) {
        propOutputModelFileString.setValue(newFileName);
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
    
    public PropertyObjectList getJointTaskListAsObjectList() {
        AbstractProperty ap = toolAsObject.getPropertyByName("JMPTaskList");
        PropertyObjectList olist = PropertyObjectList.getAs(ap);
        for (int i=0; i< olist.size(); i++){
            OpenSimObject ithTask = olist.getValue(i);
        }
        return olist;
    }
    
}
