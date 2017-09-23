/* -------------------------------------------------------------------------- *
 * OpenSim: NameFilterTableModel.java                                         *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib                                                     *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may    *
 * not use this file except in compliance with the License. You may obtain a  *
 * copy of the License at http://www.apache.org/licenses/LICENSE-2.0          *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 * -------------------------------------------------------------------------- */
/*
 *
 * PlotterQuantityNameFilterTableModel
 * Author(s): Ayman Habib
 */
package org.opensim.view.excitationEditor;

import java.util.Vector;
import java.util.regex.Pattern;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Ayman
 */
public class NameFilterTableModel extends AbstractTableModel {
   /** Creates a new instance of PlotterQuantityNameFilterTableModel */
   FilterableInterface source;
   String[] tableColumnNames= new String[2];
   String[] availableQuantities;
   Vector<Integer> shownQuantities=new Vector<Integer>(50);
   boolean[] selected;
   
   public NameFilterTableModel(FilterableInterface source, String[] columnNames) {
      System.arraycopy(columnNames, 0, tableColumnNames, 0, 2);
      availableQuantities = source.getAllQuantities();
      showAll();
      source.clearSelectionStatus();
      selected = source.getSelectionStatus();
      select(".*", false);
   }

   public NameFilterTableModel(String[] quantities, String[] columnNames) {
      System.arraycopy(columnNames, 0, tableColumnNames, 0, 2);
      availableQuantities = new String[quantities.length];
      System.arraycopy(quantities, 0, availableQuantities, 0, quantities.length);
      source = new FilterableStringArray(quantities);
      showAll();
      source.clearSelectionStatus();
      selected = source.getSelectionStatus();
      select(".*", false);
   }
    private void showAll() {
        shownQuantities.clear();
        for(int i=0;i<availableQuantities.length;i++)
            shownQuantities.add(i);
    }
      
   public int getColumnCount() {
      return tableColumnNames.length;
   }
   
   public int getRowCount() {
      return shownQuantities.size();
   }
   
   public String getColumnName(int col) {
      return tableColumnNames[col];
   }
   
   public Object getValueAt(int row, int col) {
      if (col==0)
         return availableQuantities[shownQuantities.get(row)];
      else
         return selected[shownQuantities.get(row)];
   }
  /*
   * JTable uses this method to determine the default renderer/
   * editor for each cell.  If we didn't implement this method,
   * then the last column would contain text ("true"/"false"),
   * rather than a check box.
   */
  public Class getColumnClass(int c) {
      return getValueAt(0, c).getClass();
  }

   void applyFilter(String regex) {
      selected = source.filterByRegularExprssion(regex);
   }

    public void setValueAt(Object aValue, int row, int col) {
        selected[shownQuantities.get(row)] = (Boolean)aValue;
        fireTableCellUpdated(row, col);
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return (columnIndex==1);
    }

    int getNumSelected() {
        int numSelected=0;
        for(int i=0;i<selected.length; i++)
            numSelected += (selected[i]?1:0);
        return numSelected;
    }
    int getNumShownAndSelected() {
        int numShownAndSelected=0;
        for(int i=0;i<shownQuantities.size(); i++)
            numShownAndSelected += (selected[shownQuantities.get(i)]?1:0);
        return numShownAndSelected;
    }

    String getSelectedAsString() {
        String selectedString="";
        boolean first=true;
        for(int i=0;i<selected.length; i++){
            if (selected[i]){
                if (first){
                    selectedString += availableQuantities[i];
                    first=false;
                }
                else
                    selectedString += ", "+availableQuantities[i];;
            }
        }
        return selectedString;
    }

    void markSelectedNames(Vector<String> names) {
        for(int n=0; n<names.size();n++){
            String name = names.get(n);
            boolean found=false;
            for(int i=0; i<availableQuantities.length && !found; i++){
                if (availableQuantities[i].compareTo(name)==0)
                    selected[i]=true;
            }
        }
        fireTableCellUpdated(0, 0);

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

    void select(String pattern, boolean b) {
      Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
      for(int i=0; i<shownQuantities.size() ;i++){
        if (p.matcher(availableQuantities[i]).matches())
            selected[i]=b;
      }
      fireTableDataChanged();
    }

    void selectShown(boolean b) {
      for(int i=0; i<shownQuantities.size() ;i++){
            selected[shownQuantities.get(i)]=b;
      }
      fireTableDataChanged();
    }

    String[] getSelected()
    {
        String[] sel = new String[getNumSelected()];
        int j=0;
        for(int i=0;i<selected.length; i++){
            if (selected[i]){
                sel[j]=availableQuantities[i];
                j++;
            }
        }
        return sel;
    }
}
