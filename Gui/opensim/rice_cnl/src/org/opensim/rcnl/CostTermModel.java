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
public class CostTermModel {
    
    Model model;
    OpenSimObject costTerm;
    private TreatmentOptimizationToolModel.Mode mode;
    private int termIndex = 0;
    private AbstractProperty errorProp;
    private AbstractProperty errorCenterProp;
    
  
    
    public CostTermModel(OpenSimObject rCNLCostTerm, TreatmentOptimizationToolModel.Mode mode){
        this.model = OpenSimDB.getInstance().getCurrentModel();
        this.costTerm = rCNLCostTerm;
        this.mode = mode;
        errorProp = costTerm.updPropertyByName("max_allowable_error");
        errorCenterProp = costTerm.updPropertyByName("error_center");
    }

    void setTypeIndex(int selectedIndex) {
        termIndex = selectedIndex; 
    }

    public Model getModel() {
        return model;
    }
    
    public String getType() {
        return RCNLCostTermsInfo.getCostTermTypes(mode)[termIndex];
    }
    
    public String getComponentType() {
        return RCNLCostTermsInfo.getCostTermQuantityTypes(mode)[termIndex];
    }

    PropertyStringList getPropertyComponentList() {
        switch(getComponentType()){
            case "coordinate":
                return PropertyStringList.updAs(costTerm.updPropertyByName("coordinate_list"));
            case "marker":
                return PropertyStringList.updAs(costTerm.updPropertyByName("marker_list"));
            case "muscle":
                return PropertyStringList.updAs(costTerm.updPropertyByName("muscle_list"));
        }
        return PropertyStringList.updAs(costTerm.updPropertyByName("coordinate_list"));
    }
    
    public double getMaxAllowableError() {
          return PropertyHelper.getValueDouble(errorProp);
    }
    public void setMaxAllowableError(double newMaxErr) {
        PropertyHelper.setValueDouble(newMaxErr, errorProp);
    }
    public double getErrorCenter() {
        return PropertyHelper.getValueDouble(errorCenterProp);
    }
    public void setErrorCenter(double newErrorCenter) {
        PropertyHelper.setValueDouble(newErrorCenter, errorCenterProp);
    }
}
