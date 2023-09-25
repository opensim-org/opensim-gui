/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.rcnl;

import javax.swing.table.AbstractTableModel;
import org.opensim.modeling.Model;
import org.opensim.modeling.PropertyStringList;

/**
 *
 * @author Ayman-NMBL
 */
public class MuscleGroupTableModel  extends AbstractTableModel{

    PropertyStringList muscleGroupProperty;
    Model model;
    
    public MuscleGroupTableModel(PropertyStringList muscleGroupProperty, Model model){
        this.muscleGroupProperty = muscleGroupProperty;
        this.model = model;
    }
    @Override
    public int getRowCount() {
        return model.getMuscles().getNumGroups(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getColumnCount() {
        return 2; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getValueAt(int i, int i1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
