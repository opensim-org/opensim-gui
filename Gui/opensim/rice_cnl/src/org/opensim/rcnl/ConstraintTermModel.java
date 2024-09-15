/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.opensim.rcnl;

import org.opensim.modeling.AbstractProperty;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.PropertyHelper;
import org.opensim.modeling.PropertyStringList;
import org.opensim.view.pub.OpenSimDB;

/**
 *
 * @author ayman
 */
public class ConstraintTermModel {
    
    Model model;
    OpenSimObject constraintTerm;
    private TreatmentOptimizationToolModel.Mode mode;
    private int termIndex = 0;
    private AbstractProperty maxErrorProp;
    private AbstractProperty minErrorProp;
    
    public ConstraintTermModel(OpenSimObject rCNLConstraintTerm, TreatmentOptimizationToolModel.Mode mode){
        this.model = OpenSimDB.getInstance().getCurrentModel();
        this.constraintTerm = rCNLConstraintTerm;
        this.mode = mode;
        maxErrorProp = rCNLConstraintTerm.updPropertyByName("max_error");
        minErrorProp = rCNLConstraintTerm.updPropertyByName("min_error");
    }

    void setTypeIndex(int selectedIndex) {
        termIndex = selectedIndex; 
    }

    public Model getModel() {
        return model;
    }
    
    public String getType() {
        return RCNLConstraintTermsInfo.getConstraintTermTypes(mode)[termIndex];
    }
    
    public String getComponentType() {
        return RCNLConstraintTermsInfo.getConstraintTermQuantityTypes(mode)[termIndex];
    }

    PropertyStringList getPropertyComponentList() {
        switch(getComponentType()){
            case "coordinate":
                return PropertyStringList.updAs(constraintTerm.updPropertyByName("coordinate_list"));
            case "load":
                return PropertyStringList.updAs(constraintTerm.updPropertyByName("load_list"));
            case "force":
                return PropertyStringList.updAs(constraintTerm.updPropertyByName("force_list"));
            case "moment":
                return PropertyStringList.updAs(constraintTerm.updPropertyByName("moment_list"));
            case "muscle":
                return PropertyStringList.updAs(constraintTerm.updPropertyByName("muscle_list"));
            case "synergy_group":
                return PropertyStringList.updAs(constraintTerm.updPropertyByName("synergy_group_list"));
        }
        return PropertyStringList.updAs(constraintTerm.updPropertyByName("coordinate_list"));
    }
    public double getMaxError() {
          return PropertyHelper.getValueDouble(maxErrorProp);
    }
    public void setMaxError(double newMaxErr) {
        PropertyHelper.setValueDouble(newMaxErr, maxErrorProp);
    }
    public double getMinError() {
        return PropertyHelper.getValueDouble(minErrorProp);
    }
    public void setMinError(double newMinError) {
        PropertyHelper.setValueDouble(newMinError, minErrorProp);
    }

}
