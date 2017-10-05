/* -------------------------------------------------------------------------- *
 * OpenSim: ScaleFactorsTableModel.java                                       *
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
import java.awt.Dimension;
import java.awt.Font;
import java.text.DecimalFormat;
import java.util.Observable;
import java.util.Observer;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import org.opensim.modeling.BodySet;

class BodyScaleFactorsSubCellRenderer extends DefaultTableCellRenderer {
   private ScaleToolModel scaleToolModel;
   private Border[] interiorBorders = new Border[] {
     BorderFactory.createMatteBorder(0,0,0,1,Color.lightGray),
     BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0,1,0,0), BorderFactory.createMatteBorder(0,0,0,1,Color.lightGray)),
     BorderFactory.createEmptyBorder(0,1,0,0)};
   private Border intendedBorder; // store the border the DefaultTableCellRenderer wanted to use, and use this for the surrounding panel instead.
   private DecimalFormat numberFormat = new DecimalFormat("0.000000");

   private static Color invalidColor = new Color(255,102,102);
   private Font regularFont = new Font("Tahoma", Font.PLAIN, 11);
   private Font boldFont = new Font("Tahoma", Font.BOLD, 11);

   BodyScaleFactorsSubCellRenderer(ScaleToolModel scaleToolModel) {
      this.scaleToolModel = scaleToolModel;
   }

   public Component getMeasurementSubCellRenderer(JTable table, BodyScaleFactors scaleFactors, boolean isSelected, boolean hasFocus, int row, int col, int i, int n) {
      String name;
      Font font = null;
      boolean valid = true;
      if(scaleFactors.useManualScale()) {
         name = "MANUAL SCALES";
         font = boldFont;
      } else {
         name = (scaleFactors.measurements[i]==-1) ? ScaleFactorsTableModel.unassignedMeasurement : scaleToolModel.getMeasurementName(scaleFactors.measurements[i]);
         font = regularFont;
         if(scaleFactors.measurements[i]!=-1 && scaleToolModel.getMeasurementValue(scaleFactors.measurements[i])==null) valid = false;
      }
      // Reset bg/fg colors before calling super.getTableCellRendererComponent()
      setBackground(null);
      setForeground(null);
      JLabel label = (JLabel)super.getTableCellRendererComponent(table,name,isSelected,hasFocus,row,col);
      intendedBorder = label.getBorder();
      if(n==3) label.setBorder(interiorBorders[i]);
      else label.setBorder(null);
      label.setHorizontalAlignment(SwingConstants.LEADING);
      label.setFont(font);
      // Override bg/fg colors for invalid measurment (most likely it refers to markers which are not available in the model)
      if(!valid) {
         if(isSelected) label.setForeground(invalidColor);
         else label.setBackground(invalidColor);
      }
      return label;
   }

   public Component getScaleFactorsSubCellRenderer(JTable table, BodyScaleFactors scaleFactors, boolean isSelected, boolean hasFocus, int row, int col, int i, int n) {
      String str;
      Font font = null;
      if(scaleFactors.useManualScale()) {
         str = numberFormat.format(scaleFactors.manualScales[i]);
         font = boldFont;
      } else {
         if(scaleFactors.measurements[i]==-1) str = "1.0";
         else {
            Double val = scaleToolModel.getMeasurementValue(scaleFactors.measurements[i]);
            if(val==null) str = "?"; else str = numberFormat.format(val);
         }
         font = regularFont;
      }
      setBackground(null);
      setForeground(null);
      JLabel label = (JLabel)super.getTableCellRendererComponent(table,str,isSelected,hasFocus,row,col);
      intendedBorder = label.getBorder();
      if(n==3) label.setBorder(interiorBorders[i]);
      else label.setBorder(null);
      label.setHorizontalAlignment(SwingConstants.TRAILING);
      label.setFont(font);
      return label;
   }

   public Border getIntendedBorder() { return intendedBorder; }
}

class BodyScaleFactorsCellRenderer implements TableCellRenderer {
   private BodyScaleFactorsSubCellRenderer[] renderers;
   
   public BodyScaleFactorsCellRenderer(ScaleToolModel scaleToolModel) {
      renderers = new BodyScaleFactorsSubCellRenderer[]{new BodyScaleFactorsSubCellRenderer(scaleToolModel), new BodyScaleFactorsSubCellRenderer(scaleToolModel), new BodyScaleFactorsSubCellRenderer(scaleToolModel)};
      for(int i=0; i<3; i++) {
         renderers[i].setMaximumSize(new Dimension(99999,99999));
         renderers[i].setMinimumSize(new Dimension(1,1));
         renderers[i].setPreferredSize(new Dimension(60,20));
      }
   }

   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
      BodyScaleFactors scaleFactors = (BodyScaleFactors)value;
      JPanel panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
      if(col==1) { // Measurement column
         int n = (scaleFactors.uniformMeasurements() || scaleFactors.useManualScale()) ? 1 : 3;
         for(int i=0; i<n; i++) 
            panel.add(renderers[i].getMeasurementSubCellRenderer(table, scaleFactors, isSelected, hasFocus, row, col, i, n));
      } else { // Scale factors column
         int n = scaleFactors.useManualScale() ? (scaleFactors.uniformManualScales() ? 1 : 3) : (scaleFactors.uniformMeasurements() ? 1 : 3);
         for(int i=0; i<n; i++) 
            panel.add(renderers[i].getScaleFactorsSubCellRenderer(table, scaleFactors, isSelected, hasFocus, row, col, i, n));
      }
      panel.setBorder(renderers[0].getIntendedBorder());
      panel.setBackground(panel.getComponent(0).getBackground());
      return panel;
   }
}

public class ScaleFactorsTableModel extends AbstractTableModel implements Observer {
   private String[] columnNames = new String[]{"Body Name", "Measurement(s) Used", "Applied Scale Factor(s)"};

   private ScaleToolModel scaleToolModel;
   private BodySet bodySet;

   public static final String unassignedMeasurement = "Unassigned";

   ScaleFactorsTableModel(ScaleToolModel scaleToolModel) {
      this.scaleToolModel = scaleToolModel;
      bodySet = scaleToolModel.getUnscaledModel().getBodySet();
      scaleToolModel.addObserver(this);
   }

   public void update(Observable observable, Object obj) {
      ScaleToolModel.Operation op = (ScaleToolModel.Operation)obj;
      fireTableRowsUpdated(0,bodySet.getSize()-1); // better than fireTableDataChanged() because it doesn't lose the selection
   }

   //////////////////////////////////////////////////////////////////////////
   // Utilities
   //////////////////////////////////////////////////////////////////////////

   public int getMeasurement(int i, int axis) {
      return scaleToolModel.getBodySetScaleFactors().get(i).measurements[axis];
   }

   public double getManualScale(int i, int axis) {
      return scaleToolModel.getBodySetScaleFactors().get(i).manualScales[axis];
   }

   public boolean getUseManualScale(int i) {
      return scaleToolModel.getBodySetScaleFactors().get(i).useManualScale();
   }

   public boolean isSameMeasurement(int rows[], int axis) {
      if(rows.length==0) return true;
      int measurement = getMeasurement(rows[0], axis);
      for(int row : rows) if(getMeasurement(row, axis)!=measurement) return false;
      return true;
   }

   public boolean isSameManualScale(int rows[], int axis) {
      if(rows.length==0) return true;
      double scale = getManualScale(rows[0], axis);
      for(int row : rows) if(getManualScale(row, axis)!=scale) return false;
      return true;
   }

   public boolean isSameUseManualScale(int rows[]) {
      if(rows.length==0) return true;
      boolean use = getUseManualScale(rows[0]);
      for(int row : rows) if(getUseManualScale(row)!=use) return false;
      return true;
   }

   public void copyMeasurementValueToManualScales(int rows[]) {
      for(int row : rows) {
         BodyScaleFactors scaleFactors = scaleToolModel.getBodySetScaleFactors().get(row);
         for(int axis=0; axis<3; axis++) {
            if(scaleFactors.measurements[axis]>=0) {
               Double val = scaleToolModel.getMeasurementValue(scaleFactors.measurements[axis]);
               if(val!=null) scaleFactors.manualScales[axis] = val.doubleValue();
            }
         }
      }
   }

   //////////////////////////////////////////////////////////////////////////
   // AbstractTableModel methods
   //////////////////////////////////////////////////////////////////////////
   public int getColumnCount() {
      return columnNames.length;
   }

   public int getRowCount() {
      return bodySet.getSize();
   }

   public String getColumnName(int col) {
      return columnNames[col];
   }

   public Object getValueAt(int row, int col) {
      if(col==0) return bodySet.get(row).getName();
      else if(col==1) return scaleToolModel.getBodySetScaleFactors().get(row);
      else if(col==2) return scaleToolModel.getBodySetScaleFactors().get(row);
      else return null;
   }

   public Class getColumnClass(int col) {
      if(col==0) return String.class;
      else if(col==1) return BodyScaleFactors.class;
      else if(col==2) return BodyScaleFactors.class;
      else return null;
   }

   public boolean isCellEditable(int row, int col) {
      return false;
   }
}

