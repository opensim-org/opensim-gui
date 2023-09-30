/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.rcnl;

import javax.swing.table.AbstractTableModel;
import org.opensim.modeling.ArrayBool;
import org.opensim.modeling.ArrayStr;
import org.opensim.modeling.Model;
import org.opensim.modeling.PropertyStringList;

/**
 *
 * @author Ayman-NMBL
 */
public class CoordinateTableModel  extends AbstractTableModel{

    PropertyStringList coordinateListProperty;
    Model model;
    String[] tableColumnNames= {"Coordiates", "Selected"};
    ArrayStr coordinateNames = new ArrayStr();
    ArrayBool selected = new ArrayBool();
       
    public CoordinateTableModel(PropertyStringList coordinateListProperty, Model mdl){
        this.coordinateListProperty = coordinateListProperty;
        this.model = mdl;
        model.getCoordinateSet().getNames(coordinateNames);
        for (int i=0; i < coordinateNames.getSize(); i++){
            setValueAt(coordinateNames.get(i), i, 0);
            setValueAt(Boolean.FALSE, i, 0);
            selected.append(Boolean.FALSE);
        }
        // Now select entries based on passed in coordinateListProperty
        for (int p=0; p < coordinateListProperty.size(); p++){
            int cIndex = model.getCoordinateSet().getIndex(coordinateListProperty.getValue(p));
            setValueAt(Boolean.TRUE, cIndex, 1);
        }
        
    }
    @Override
    public int getRowCount() {
        return coordinateNames.getSize(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getColumnCount() {
        return 2; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getValueAt(int row, int col) {
      if (col==0)
         return coordinateNames.get(row);
      else
         return selected.get(row);    
    }
       
   public String getColumnName(int col) {
      return tableColumnNames[col];
   }
   
    public void setValueAt(Object aValue, int row, int col) {
        if (col ==1) {
            selected.set(row, (Boolean)aValue);
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
    // Translate checkboxes in selected array into coordinateListProperty
    public void populateCoordinateListProperty() {
        coordinateListProperty.clear();
        // Now select entries based on passed in coordinateListProperty
        for (int p=0; p < selected.getSize(); p++){
            if (selected.get(p))
               coordinateListProperty.appendValue(coordinateNames.get(p));
        }
    }
}
