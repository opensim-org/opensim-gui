/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.rcnl;

import java.util.Vector;
import org.opensim.modeling.AbstractProperty;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.PropertyObjectList;
import org.opensim.modeling.PropertyStringList;

/**
 *
 * @author Ayman-NMBL
 */
public class MTPPersonalizationToolModel {

    /**
     * @return the propActivationMGListString
     */
    public PropertyStringList getPropActivationMGListString() {
        return propActivationMGListString;
    }

    /**
     * @return the propNormalizedFLMGListString
     */
    public PropertyStringList getPropNormalizedFLMGListString() {
        return propNormalizedFLMGListString;
    }

    /**
     * @return the propMissingEMGMGListString
     */
    public PropertyStringList getPropMissingEMGMGListString() {
        return propMissingEMGMGListString;
    }

    /**
     * @return the propCollectedEMGMGListString
     */
    public PropertyStringList getPropCollectedEMGMGListString() {
        return propCollectedEMGMGListString;
    }

    /**
     * @return the propCoordinateListString
     */
    public PropertyStringList getPropCoordinateListString() {
        return propCoordinateListString;
    }

    private OpenSimObject toolAsObject;
    private String resultsDir = ".";
    private String inputDir = ".";
    private String outputModelFile = "";
    private double accuracy=1e-5;
    private String modelName;
    private Model model;
    private  PropertyStringList propOutputModelFileString;
    private PropertyStringList propInputModelFileString;
    private PropertyStringList propCoordinateListString;
    // Muscle groups
    private PropertyStringList propActivationMGListString;
    private PropertyStringList propNormalizedFLMGListString;
    private PropertyStringList propMissingEMGMGListString;
    private PropertyStringList propCollectedEMGMGListString;
    
    public MTPPersonalizationToolModel(Model model) {
        // TODO in case plugin is not preloaded, guard against null return or exception thown
        toolAsObject = OpenSimObject.newInstanceOfType("MuscleTendonPersonalizationTool");
        this.model = model;
        modelName = model.getName();
        AbstractProperty propOutputModelFile = toolAsObject.updPropertyByName("output_model_file");
        propOutputModelFileString = PropertyStringList.getAs(propOutputModelFile);
        if (propOutputModelFileString.size()==0 || propOutputModelFileString.getValue(0).isEmpty()){
             String proposedName = model.getInputFileName().replace(".osim", "_MTP.osim");
             propOutputModelFileString.setValue(0, proposedName);
        }
        AbstractProperty propInputModelFile = toolAsObject.updPropertyByName("input_model_file");
        propInputModelFileString = PropertyStringList.getAs(propInputModelFile);
        if (propInputModelFileString.size()==0 || propInputModelFileString.getValue(0).isEmpty()){
             String proposedName = model.getInputFileName();
             propInputModelFileString.setValue(0, proposedName);
        }
        AbstractProperty coordListProp = toolAsObject.updPropertyByName("coordinate_list");
        propCoordinateListString = PropertyStringList.updAs(coordListProp);
        
        AbstractProperty activationMGListProp = toolAsObject.updPropertyByName("activation_muscle_groups");
        propActivationMGListString = PropertyStringList.updAs(activationMGListProp);
        AbstractProperty normalizedFLMGListProp = toolAsObject.updPropertyByName("normalized_fiber_length_muscle_groups");
        propNormalizedFLMGListString = PropertyStringList.updAs(normalizedFLMGListProp);
        AbstractProperty missingEMGMGListProp = toolAsObject.updPropertyByName("missing_emg_channel_muscle_groups");
        propMissingEMGMGListString = PropertyStringList.updAs(missingEMGMGListProp);
        AbstractProperty collectedEMGMGListProp = toolAsObject.updPropertyByName("collected_emg_channel_muscle_groups");
        propCollectedEMGMGListString = PropertyStringList.updAs(collectedEMGMGListProp);
        
    }
    public MTPPersonalizationToolModel(Model model, String fileXml) {
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
    
}
