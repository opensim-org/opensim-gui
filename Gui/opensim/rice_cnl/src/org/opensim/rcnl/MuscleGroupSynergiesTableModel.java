/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.rcnl;

import javax.swing.table.AbstractTableModel;
import org.opensim.modeling.ArrayBool;
import org.opensim.modeling.ArrayInt;
import org.opensim.modeling.ArrayStr;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.PropertyIntList;
import org.opensim.modeling.PropertyObjectList;
import org.opensim.modeling.PropertyStringList;

/**
 *
 * @author Ayman-NMBL
 */
public class MuscleGroupSynergiesTableModel  extends AbstractTableModel{

    PropertyObjectList muscleGroupSynergyListProperty;
    Model model;
    String[] tableColumnNames= {"Groups", "Synergies", "Selected"};
    ArrayStr groupNames = new ArrayStr();
    ArrayInt synergyCount = new ArrayInt();
    ArrayBool selected = new ArrayBool();
    
    public MuscleGroupSynergiesTableModel(PropertyObjectList muscleGroupSynergyListProperty, Model model){
        this.muscleGroupSynergyListProperty = muscleGroupSynergyListProperty;
        this.model = model;
        model.getForceSet().getGroupNames(groupNames);
        for (int i=0; i < groupNames.getSize(); i++){
            setValueAt(groupNames.get(i), i, 0);
            synergyCount.append(0);
            setValueAt(synergyCount.get(i), i, 1);
            setValueAt(Boolean.FALSE, i, 2);
            selected.append(Boolean.FALSE);
        }
        // Now select entries based on passed in coordinateListProperty
        for (int p=0; p < muscleGroupSynergyListProperty.size(); p++){
            OpenSimObject oneGroupSynergy = muscleGroupSynergyListProperty.getValue(p);
            PropertyStringList groupName = PropertyStringList.getAs(oneGroupSynergy.getPropertyByName("muscle_group_name"));
            String gName = groupName.getValue(0);
            PropertyIntList synergyCount = PropertyIntList.getAs(oneGroupSynergy.getPropertyByName("num_synergies"));
            int gIndex = groupNames.findIndex(gName);
            setValueAt(synergyCount.getValue(0), gIndex, 1);
            setValueAt(Boolean.TRUE, gIndex, 2);
            
        }
        
    }
    @Override
    public int getRowCount() {
        return model.getForceSet().getNumGroups(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getColumnCount() {
        return 3; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getValueAt(int row, int col) {
      if (col==0)
         return groupNames.get(row);
      else if (col==1)
          return synergyCount.get(row);
      else
         return selected.get(row);    
    }
       
   public String getColumnName(int col) {
      return tableColumnNames[col];
   }
   
    public void setValueAt(Object aValue, int row, int col) {
        if (col ==1) {
            synergyCount.set(row, (Integer)aValue);
            fireTableCellUpdated(row, col);
        } else if (col ==2) {
            selected.set(row, (Boolean)aValue);
            fireTableCellUpdated(row, col);
        }
    }

   public boolean isCellEditable(int rowIndex, int columnIndex) {
       return (columnIndex!=0);
   }
  /*
   * JTable uses this method to determine the default renderer/
   * editor for each cell.  If we didn't implement this method,
   * then the last column would contain text ("true"/"false"),
   * rather than a check box.
   */
    public Class getColumnClass(int column) {
        if (column==0)
            return String.class;
        if (column==1)
            return Integer.class;
        else  
            return Boolean.class;
    }
    
    public void populateMuscleGroupSynergiesProperty() {
        muscleGroupSynergyListProperty.clear();
        // Now select entries based on passed in coordinateListProperty
        for (int p=0; p < selected.getSize(); p++){
            if (selected.get(p)){
               // Create object of type RCNLSynergy, populate then append
               OpenSimObject newSynergy = OpenSimObject.newInstanceOfType("RCNLSynergy");
               PropertyStringList muscleGroups = PropertyStringList.getAs(newSynergy.getPropertyByName("muscle_group_name"));
               muscleGroups.setValue(0, groupNames.get(p));
               PropertyIntList synergies = PropertyIntList.getAs(newSynergy.getPropertyByName("num_synergies"));
               synergies.setValue(0, synergyCount.get(p));
               muscleGroupSynergyListProperty.appendValue(newSynergy);
            }
        }
    }
}
