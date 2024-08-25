/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.opensim.rcnl;

import org.opensim.modeling.AbstractProperty;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimObject;
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
    
    public CostTermModel(OpenSimObject rCNLCostTerm, TreatmentOptimizationToolModel.Mode mode){
        this.model = OpenSimDB.getInstance().getCurrentModel();
        this.costTerm = rCNLCostTerm;
        this.mode = mode;
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
        return null;
    }
}
