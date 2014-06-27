/*
 *
 * PlotterQuantityNameFilterTableModel
 * Author(s): Ayman Habib
 * Copyright (c)  2005-2006, Stanford University, Ayman Habib
* Use of the OpenSim software in source form is permitted provided that the following
* conditions are met:
* 	1. The software is used only for non-commercial research and education. It may not
*     be used in relation to any commercial activity.
* 	2. The software is not distributed or redistributed.  Software distribution is allowed 
*     only through https://simtk.org/home/opensim.
* 	3. Use of the OpenSim software or derivatives must be acknowledged in all publications,
*      presentations, or documents describing work in which OpenSim or derivatives are used.
* 	4. Credits to developers may not be removed from executables
*     created from modifications of the source.
* 	5. Modifications of source code must retain the above copyright notice, this list of
*     conditions and the following disclaimer. 
* 
*  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
*  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
*  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
*  SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
*  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
*  TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
*  HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
*  OR BUSINESS INTERRUPTION) OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
*  WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.opensim.plotter;

import java.util.Vector;
import java.util.regex.Pattern;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Ayman
 */
public class QuantityNameFilterTableModel extends AbstractTableModel {
   /** Creates a new instance of PlotterQuantityNameFilterTableModel */
   PlotterSourceInterface source;
   String[] tableColumnNames= new String[2];
   String[] availableQuantities;
   Vector<Integer> shownQuantities=new Vector<Integer>(50);
   boolean[] selected;
   
   public QuantityNameFilterTableModel(PlotterSourceInterface source, String[] columnNames) {
      System.arraycopy(columnNames, 0, tableColumnNames, 0, 2);
      availableQuantities = source.getAllQuantities();
      showAll();
      source.clearSelectionStatus();
      selected = source.getSelectionStatus();
      select(".*", false);
   }

   public QuantityNameFilterTableModel(String[] quantities, String[] columnNames) {
      System.arraycopy(columnNames, 0, tableColumnNames, 0, 2);
      availableQuantities = new String[quantities.length];
      System.arraycopy(quantities, 0, availableQuantities, 0, quantities.length);
      source = new PlotterSourceStringArray(quantities);
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
