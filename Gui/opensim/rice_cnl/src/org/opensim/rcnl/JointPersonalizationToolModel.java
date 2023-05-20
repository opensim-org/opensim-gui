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
public class JointPersonalizationToolModel {

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
    private  PropertyStringList propModelFileString;
    
    public JointPersonalizationToolModel(Model model) {
        // TODO in case plugin is not preloaded, guard against null return or exception thown
        toolAsObject = OpenSimObject.newInstanceOfType("JointModelPersonalizationTool");
        this.model = model;
        modelName = model.getName();
        AbstractProperty propModelFile = toolAsObject.updPropertyByName("output_model_file");
        propModelFileString = PropertyStringList.getAs(propModelFile);
        if (propModelFileString.size()==0 || propModelFileString.getValue(0).isEmpty()){
             String proposedName = model.getInputFileName().replace(".osim", "_JMP.osim");
             propModelFileString.setValue(0, proposedName);
        }
    }
    public JointPersonalizationToolModel(Model model, String fileXml) {
        // TODO in case plugin is not preloaded, guard against null return or exception thown
        toolAsObject = OpenSimObject.makeObjectFromFile(fileXml);
        this.model = model;
        modelName = model.getName();
        AbstractProperty prop = toolAsObject.updPropertyByName("output_model_file");
        propModelFileString = PropertyStringList.getAs(prop);
        if (propModelFileString.size()==0|| propModelFileString.getValue(0).isEmpty()){
             String proposedName = model.getInputFileName().replace(".osim", "_perjoint.osim");
             propModelFileString.setValue(0, proposedName);
        }
    }


    /**
     * @return the modelName
     */
    public String getModelName() {
        return modelName;
    }
    String getOutputModelFile() {
        return propModelFileString.getValue(0);
    }
    void setOutputModelFile(String newFileName) {
        propModelFileString.setValue(newFileName);
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
        Vector<OpenSimObject> tasks = new Vector<OpenSimObject>();
        AbstractProperty ap = toolAsObject.getPropertyByName("JMPTaskList");
        PropertyObjectList olist = PropertyObjectList.getAs(ap);
        for (int i=0; i< olist.size(); i++){
            OpenSimObject ithTask = olist.getValue(i);
            tasks.add(ithTask);
        }
        return olist;
    }
    
}
