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
public class GCPPersonalizationToolModel {

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
    private PropertyStringList propInputMotionFileString;
    private PropertyStringList propInputGRFFileString;
    
    private PropertyStringList propOutputResultDirString;
    
    private PropertyStringList propCoordinateListString;
    // Muscle groups
    private PropertyStringList propActivationMGListString;
    private PropertyStringList propNormalizedFLMGListString;
    
    private PropertyStringList propInputDirString;
    private OpenSimObject muscleTendonLengthInitializationAsObject;
    private PropertyBoolList propInitializationEnabled;
    private PropertyStringList propMTPDirString;
    private PropertyObjectList propSynergyList;
    
    public GCPPersonalizationToolModel(Model model) {
        // TODO in case plugin is not preloaded, guard against null return or exception thown
        toolAsObject = OpenSimObject.newInstanceOfType("GroundContactPersonalizationTool");
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
        
        propOutputResultDirString = PropertyStringList.getAs(toolAsObject.updPropertyByName("results_directory"));
        
        propInputMotionFileString = PropertyStringList.getAs(toolAsObject.updPropertyByName("input_motion_file"));
        
        propInputGRFFileString  = PropertyStringList.getAs(toolAsObject.updPropertyByName("input_grf_file"));
 
    }
    public GCPPersonalizationToolModel(Model model, String fileXml) {
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
        propInputDirString.setValue(fileName);
    }
    String getDataDir() {
        if (propInputDirString.size()==1)
            return propInputDirString.getValue();
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
    
    void setEnableInitialization(boolean newValue) {
        propInitializationEnabled.setValue(newValue);
    }
    boolean getEnableInitialization() {
        return propInitializationEnabled.getValue();
    }

    void setMTPDir(String fileName) {
        propMTPDirString.setValue(fileName);
    }
    String getMTPDir() {
        if (propMTPDirString.size()==1)
            return propMTPDirString.getValue();
        return "";
    }
    PropertyObjectList getSynergyList() {
       return propSynergyList;
    }
    String getSynergiesAsString() {
        String result = "";
        for (int i=0; i< propSynergyList.size(); i++){
            OpenSimObject nextSynergy = propSynergyList.getValue(i);
               PropertyStringList muscleGroups = PropertyStringList.getAs(nextSynergy.getPropertyByName("muscle_group_name"));
               PropertyIntList synergies = PropertyIntList.getAs(nextSynergy.getPropertyByName("num_synergies"));
            result = result.concat("("+muscleGroups.getValue(0)+","+synergies.getValue(0)+") ");
        }
        return result;
    }

    void setInputMotionFile(String newFileName) {
         propInputMotionFileString.setValue(newFileName);
    }

    String getInputMotionFile() {
        if (propInputMotionFileString.size()==1)
            return propInputMotionFileString.getValue(0);
        return "";
    }
    
    void setInputGRFFile(String newFileName) {
         propInputGRFFileString.setValue(newFileName);
    }
    
    String geInputGRFFile() {
        if (propInputGRFFileString.size()==1)
            return propInputGRFFileString.getValue(0);
        return "";
    }

}
