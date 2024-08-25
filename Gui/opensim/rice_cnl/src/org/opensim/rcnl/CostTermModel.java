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
    private PropertyStringList propComponentListString;
    private AbstractProperty compListProp;
    
    public CostTermModel(OpenSimObject rCNLCostTerm, TreatmentOptimizationToolModel.Mode mode){
        this.model = OpenSimDB.getInstance().getCurrentModel();
        this.costTerm = rCNLCostTerm;
        this.mode = mode;
        compListProp = costTerm.updPropertyByName("coordinate_list");
        propComponentListString = PropertyStringList.updAs(compListProp);
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
                return propComponentListString;
        }
        return null;
    }
}
