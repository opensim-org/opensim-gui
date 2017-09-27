/* -------------------------------------------------------------------------- *
 * OpenSim: IKTasksTableModel.java                                            *
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

package org.opensim.tracking;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import org.opensim.tracking.IKTasksModelEvent;

abstract class IKTasksCell {
   IKTasksModel tasks;
   int index;
   public IKTasksCell(IKTasksModel tasks, int index) { this.tasks = tasks; this.index = index; }
   public abstract boolean renderValidity();
}

class IKTasksNameCell extends IKTasksCell {
   public IKTasksNameCell(IKTasksModel tasks, int index) { super(tasks, index); }
   public boolean renderValidity() { return false; }
}

class IKTasksValueCell extends IKTasksCell {
   public IKTasksValueCell(IKTasksModel tasks, int index) { super(tasks, index); }
   public boolean renderValidity() { return true; }
}

class IKTasksWeightCell extends IKTasksCell {
   public IKTasksWeightCell(IKTasksModel tasks, int index) { super(tasks, index); }
   public boolean renderValidity() { return false; }
}

class IKTasksCellRenderer extends DefaultTableCellRenderer {
   protected static Color invalidColor = new Color(255,102,102);
   protected Font regularFont = new Font("Tahoma", Font.PLAIN, 11);
   protected Font boldFont = new Font("Tahoma", Font.BOLD, 11);

   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      IKTasksCell obj = (IKTasksCell)value;
      // Reset bg/fg colors before calling super.getTableCellRendererComponent()
      setBackground(null);
      setForeground(null);
      Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      comp.setEnabled(obj.tasks.getEnabled(obj.index));
      // Override bg/fg colors for invalid entry
      if(obj.renderValidity() && !obj.tasks.isValid(obj.index)) {
         if(isSelected) comp.setForeground(invalidColor);
         else comp.setBackground(invalidColor);
      }
      return comp;
   }
}

class IKTasksNameCellRenderer extends IKTasksCellRenderer {
   public void setValue(Object value) {
      IKTasksNameCell obj = (IKTasksNameCell)value;
      super.setValue(obj.tasks.getName(obj.index));
   }
}

class IKTasksValueCellRenderer extends IKTasksCellRenderer {
   public void setValue(Object value) {
      IKTasksValueCell obj = (IKTasksValueCell)value;
      super.setFont(regularFont); // reset
      switch(obj.tasks.getValueType(obj.index)) {
         case FromFile: super.setValue(obj.tasks.isValidValue(obj.index) ?  IKTasksTableModel.FromFileStr : IKTasksTableModel.InvalidFromFileStr); break;
         case DefaultValue: super.setValue(((Double)obj.tasks.getDefaultValue(obj.index))); break;
         case ManualValue: super.setValue((Double)obj.tasks.getValue(obj.index)); super.setFont(boldFont); break;
      }
      super.setHorizontalAlignment(SwingConstants.TRAILING);
   }
}

class IKTasksWeightCellRenderer extends IKTasksCellRenderer {
   public void setValue(Object value) {
      IKTasksWeightCell obj = (IKTasksWeightCell)value;
      if(obj.tasks.isLocked(obj.index)) super.setValue(IKTasksTableModel.LockedStr);
      else super.setValue((Double)obj.tasks.getWeight(obj.index));
      super.setHorizontalAlignment(SwingConstants.TRAILING);
   }
}

public class IKTasksTableModel extends AbstractTableModel implements Observer {
   private String[] columnNames = new String[]{"Enabled", "Name", "Value", "Weight"};
   private IKTasksModel tasks;

   public final static String LockedStr = "Locked";
   public final static String FromFileStr = "From File";
   public final static String InvalidFromFileStr = "From File -- NOT FOUND!";

   IKTasksTableModel(IKTasksModel tasks, String type) {
      this.tasks = tasks;
      columnNames[1] = type + " Name";
      tasks.addObserver(this);
   }

   //------------------------------------------------------------------------
   // Listen to events from the IKTasksModel
   //------------------------------------------------------------------------
   public void update(Observable observable, Object obj) {
      IKTasksModelEvent ev = (IKTasksModelEvent)obj;
      switch(ev.op) {
         case AllChanged: 
            fireTableDataChanged();
            break;
         case TaskChanged:
            fireTableRowsUpdated(ev.index,ev.index);
            break;
      }
   }

   //------------------------------------------------------------------------
   // Helper methods
   //------------------------------------------------------------------------
   public IKTasksModel getIKTasksModel() { return tasks; }

   public boolean isSameEnabled(int rows[]) {
      for(int row : rows) if(tasks.getEnabled(row)!=tasks.getEnabled(rows[0])) return false;
      return true;
   }
   public void setEnabled(int rows[], boolean enabled) {
      for(int row : rows) tasks.setEnabled(row, enabled);
   }

   public boolean isSameWeight(int rows[]) {
      for(int row : rows) if(tasks.isLocked(row)!=tasks.isLocked(rows[0]) || (!tasks.isLocked(row) && tasks.getWeight(row)!=tasks.getWeight(rows[0]))) return false;
      return true;
   }
   public void setWeight(int rows[], double weight) {
      for(int row : rows) tasks.setWeight(row, weight);
   }

   public boolean isSameValueType(int rows[]) {
      for(int row : rows) if(tasks.getValueType(row)!=tasks.getValueType(rows[0])) return false;
      return true;
   }
   public boolean isSameDefaultValue(int rows[]) {
      for(int row : rows) if(tasks.getDefaultValue(row)!=tasks.getDefaultValue(rows[0])) return false;
      return true;
   }
   public boolean isSameManualValue(int rows[]) {
      for(int row : rows) if(tasks.getManualValue(row)!=tasks.getManualValue(rows[0])) return false;
      return true;
   }
   public void setManualValue(int rows[], double value) {
      for(int row : rows) tasks.setManualValue(row, value);
   }
   public void setValueType(int rows[], IKTasksModel.ValueType valueType) {
      for(int row : rows) tasks.setValueType(row, valueType);
   }

   //------------------------------------------------------------------------
   // AbstractTableModel methods
   //------------------------------------------------------------------------
   public int getColumnCount() {
      return columnNames.length;
   }

   public int getRowCount() {
      return tasks.size();
   }

   public String getColumnName(int col) {
      return columnNames[col];
   }

   public Object getValueAt(int row, int col) {
      if(col==0) return tasks.getEnabled(row);
      else if(col==1) return new IKTasksNameCell(tasks, row);
      else if(col==2) return new IKTasksValueCell(tasks, row);
      else if(col==3) return new IKTasksWeightCell(tasks, row);
      else return null;
   }

   public Class getColumnClass(int col) {
      if(col==0) return Boolean.class;
      else if(col==1) return IKTasksNameCell.class;
      else if(col==2) return IKTasksValueCell.class;
      else if(col==3) return IKTasksWeightCell.class;
      else return null;
   }

   public boolean isCellEditable(int row, int col) {
      return col==0; // Only enabled column is editable
   }

   public void setValueAt(Object value, int row, int col) {
      assert(col==0);
      tasks.setEnabled(row, ((Boolean)value).booleanValue());
      //fireTableCellUpdated(row, col); // will fire an event when update() gets called due to a change in IKTasksModel
   }
}
