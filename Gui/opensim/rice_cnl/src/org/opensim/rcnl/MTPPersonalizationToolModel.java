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
import org.opensim.modeling.PropertyBoolList;
import org.opensim.modeling.PropertyIntList;
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
    private double accuracy=1e-5;
    private String modelName;
    private Model model;
    private PropertyStringList propInputModelFileString;
    private PropertyStringList propInputOsimxFileString;
    private PropertyStringList propInputDataDirString;
    private PropertyStringList propOutputResultDirString;
    
    private PropertyStringList propCoordinateListString;
    // Muscle groups
    private PropertyStringList propActivationMGListString;
    private PropertyStringList propNormalizedFLMGListString;
    private PropertyStringList propMissingEMGMGListString;
    private PropertyStringList propCollectedEMGMGListString;
    
    private PropertyStringList propPassiveDataDirString;
    private OpenSimObject muscleTendonLengthInitializationAsObject;
    private PropertyBoolList propInitializationEnabled;
    private OpenSimObject mTPSynergyExtrapolationAsObject;
    private PropertyBoolList propSynergyEnabled;
    private PropertyIntList propertyNumSynergies;
    
    public MTPPersonalizationToolModel(Model model) {
        // TODO in case plugin is not preloaded, guard against null return or exception thown
        toolAsObject = OpenSimObject.newInstanceOfType("MuscleTendonPersonalizationTool");
        this.model = model;
        modelName = model.getName();
        AbstractProperty propInputModelFile = toolAsObject.updPropertyByName("input_model_file");
        propInputModelFileString = PropertyStringList.getAs(propInputModelFile);
        if (propInputModelFileString.size()==0 || propInputModelFileString.getValue(0).isEmpty()){
             String proposedName = model.getInputFileName();
             propInputModelFileString.setValue(0, proposedName);
        }
        connectPropertiesToClassMembers();

        
    }

    private void connectPropertiesToClassMembers() {
        AbstractProperty propInputOsimxFile = toolAsObject.updPropertyByName("input_osimx_file");
        propInputOsimxFileString = PropertyStringList.getAs(propInputOsimxFile);
        
        AbstractProperty propInputDataDirFile = toolAsObject.updPropertyByName("data_directory");
        propInputDataDirString = PropertyStringList.getAs(propInputDataDirFile);
        
        propOutputResultDirString = PropertyStringList.getAs(toolAsObject.updPropertyByName("results_directory"));
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
        // MuscleTendonLengthInitialization
        muscleTendonLengthInitializationAsObject = toolAsObject.updPropertyByName("MuscleTendonLengthInitialization").getValueAsObject();
        propPassiveDataDirString = PropertyStringList.updAs(muscleTendonLengthInitializationAsObject.updPropertyByName("passive_data_input_directory"));
        propInitializationEnabled = PropertyBoolList.updAs(muscleTendonLengthInitializationAsObject.updPropertyByName("is_enabled"));
        
        mTPSynergyExtrapolationAsObject = toolAsObject.updPropertyByName("MTPSynergyExtrapolation").getValueAsObject();
        propSynergyEnabled = PropertyBoolList.updAs(mTPSynergyExtrapolationAsObject.updPropertyByName("is_enabled"));
        propertyNumSynergies = PropertyIntList.updAs(mTPSynergyExtrapolationAsObject.updPropertyByName("number_of_synergies"));
    }
    public MTPPersonalizationToolModel(Model model, String fileXml) {
        // TODO in case plugin is not preloaded, guard against null return or exception thown
        toolAsObject = OpenSimObject.makeObjectFromFile(fileXml);
        this.model = model;
        modelName = model.getName();
        connectPropertiesToClassMembers();
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

    /**
     * @return the toolAsObject
     */
    public OpenSimObject getToolAsObject() {
        return toolAsObject;
    }
    String getInputOsimxFile() {
        if (propInputOsimxFileString.size()==1)
            return propInputOsimxFileString.getValue(0);
        return "";
    }
    void setInputOsimxFile(String newFileName) {
        propInputOsimxFileString.setValue(newFileName);
    }
    /**
     * @param toolAsObject the toolAsObject to set
     */
    public void setToolAsObject(OpenSimObject toolAsObject) {
        this.toolAsObject = toolAsObject;
    }

    void setDataDir(String fileName) {
        propInputDataDirString.setValue(fileName);
    }
    String getDataDir() {
        if (propInputDataDirString.size()==1)
            return propInputDataDirString.getValue();
        return "";
    }

    String getOutputResultDir() {
        if (propOutputResultDirString.size()==1)
            return propOutputResultDirString.getValue();
        return "";
    }

    void setOutputResultDir(String fileName) {
        propOutputResultDirString.setValue(fileName);
    }

    String getPassiveDataDir() {
        if (propPassiveDataDirString.size()==1)
            return propPassiveDataDirString.getValue();
        return "";
    }

    void setPassiveDataDir(String fileName) {
        propPassiveDataDirString.setValue(fileName);
    }
    
    void setEnableInitialization(boolean newValue) {
        propInitializationEnabled.setValue(newValue);
    }
    boolean getEnableInitialization() {
        return propInitializationEnabled.getValue();
    }

    void setEnableSynergies(boolean selected) {
        propSynergyEnabled.setValue(selected);
    }

    boolean getEnableSynergies() {
        return propSynergyEnabled.getValue();
    }
    void setNumSynergies(int newNumber) {
        propertyNumSynergies.setValue(newNumber);
    }
    int getNumSynergies() {
        return propertyNumSynergies.getValue();
    }
}
