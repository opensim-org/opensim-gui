/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.rcnl;

import java.util.Vector;
import java.util.regex.Pattern;
import javax.swing.table.AbstractTableModel;
import org.opensim.modeling.ArrayBool;
import org.opensim.modeling.ArrayStr;
import org.opensim.modeling.Model;
import org.opensim.modeling.PropertyStringList;

/**
 *
 * @author Ayman-NMBL
 */
public class MuscleGroupTableModel  extends AbstractTableModel{

    PropertyStringList muscleGroupProperty;
    Model model;
    String[] tableColumnNames= {"Groups", "Selected"};
    ArrayStr groupNames = new ArrayStr();
    ArrayBool selected = new ArrayBool();
    String[] availableQuantities;
    Vector<Integer> shownQuantities=new Vector<Integer>(50);

    public MuscleGroupTableModel(PropertyStringList muscleGroupProperty, Model model){
        this.muscleGroupProperty = muscleGroupProperty;
        this.model = model;
        model.getForceSet().getGroupNames(groupNames);
        for (int i=0; i < groupNames.getSize(); i++){
            setValueAt(groupNames.get(i), i, 0);
            setValueAt(Boolean.FALSE, i, 0);
            selected.append(Boolean.FALSE);
        }
        availableQuantities = new String[groupNames.getSize()];
        groupNames.toVector().copyInto(availableQuantities);
        showAll();
        // Now select entries based on passed in coordinateListProperty
        for (int p=0; p < muscleGroupProperty.size(); p++){
            int cIndex = groupNames.findIndex(muscleGroupProperty.getValue(p));
            setValueAt(Boolean.TRUE, cIndex, 1);
            selected.set(cIndex, true);
        }
        showAll();
    }
    
    private void showAll() {
        shownQuantities.clear();
        for(int i=0;i<availableQuantities.length;i++)
            shownQuantities.add(i);
    }
    @Override
    public int getRowCount() {
        return shownQuantities.size(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getColumnCount() {
        return 2; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getValueAt(int row, int col) {
      if (col==0)
         return availableQuantities[shownQuantities.get(row)];
      else
         return selected.get(shownQuantities.get(row));  
    }
       
   public String getColumnName(int col) {
      return tableColumnNames[col];
   }
   
    public void setValueAt(Object aValue, int row, int col) {
        if (col ==1) {
            selected.set(shownQuantities.get(row), (Boolean)aValue);
            fireTableCellUpdated(row, col);
        }
    }

   public boolean isCellEditable(int rowIndex, int columnIndex) {
       return (columnIndex==1);
   }
  /*
   * JTable uses this method to determine the default renderer/
   * editor for each cell.  If we didn't implement this method,
   * then the last column would contain text ("true"/"false"),
   * rather than a check box.
   */
    public Class getColumnClass(int column) {
        return (column==0)? String.class: Boolean.class;
    }
    
    public void populateMuscleGroupProperty() {
        muscleGroupProperty.clear();
        // Now select entries based on passed in coordinateListProperty
        for (int p=0; p < selected.getSize(); p++){
            if (selected.get(p))
               muscleGroupProperty.appendValue(groupNames.get(p));
        }
    }

    void restrictNamesBy(String pattern){
      Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
      shownQuantities.clear();
      for(int i=0; i<availableQuantities.length ;i++){
        //System.out.println("Match ["+availableQuantities[i]+"] against pattern "+pattern+" returns "+p.matcher(availableQuantities[i]).matches());
        if (p.matcher(availableQuantities[i]).matches())
            shownQuantities.add(i);
      }
      fireTableDataChanged();
        
    }
}
