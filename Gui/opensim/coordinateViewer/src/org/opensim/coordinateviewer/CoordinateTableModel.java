/* -------------------------------------------------------------------------- *
 * OpenSim: CoordinateTableModel.java                                         *
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
 * CoordinateTableModel
 * Author(s): Ayman Habib
 */
package org.opensim.coordinateviewer;

import javax.swing.table.AbstractTableModel;
import org.opensim.modeling.Coordinate;
import org.opensim.modeling.Model;
import org.opensim.modeling.CoordinateSet;
import org.opensim.modeling.OpenSimContext;
import org.opensim.view.pub.OpenSimDB;
/**
 *
 * @author Ayman
 */
public class CoordinateTableModel extends AbstractTableModel{
   
   Object[][] contents;
   String[] columnNames= new String[]{"Coordinate", "c", "l", "value"};
   int numCoords;
   private Model model;
   private CoordinateSet coords;
   private OpenSimContext openSimContext;

   /** Creates a new instance of CoordinateTableModel */
   public CoordinateTableModel(Model aModel) {
      model = aModel;
      if (model==null){
         reset();
         return;
      }
      openSimContext = OpenSimDB.getInstance().getContext(model);
      coords = model.getCoordinateSet();
      numCoords = coords.getSize();
      contents = new Object[numCoords][5];
      for(int i=0; i<numCoords; i++){
         Coordinate coord = coords.get(i);
         contents[i][0]=coord.getName();
         contents[i][1]=(openSimContext.getClamped(coord))?Boolean.TRUE:Boolean.FALSE; // Clamped
         contents[i][2]=(openSimContext.getLocked(coord))?Boolean.TRUE:Boolean.FALSE; // Locked
         contents[i][3]=openSimContext.getValue(coord); // Value
         
      }
   }

   public int getRowCount() {
      return numCoords;
   }

   public int getColumnCount() { // name, clamped, locked, text, slider
      return 4;
   }

   public Object getValueAt(int rowIndex, int columnIndex) {
      return contents[rowIndex][columnIndex];
   }

   public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
      super.setValueAt(aValue, rowIndex, columnIndex);
   }

   public String getColumnName(int column) {
      return columnNames[column];
   }

   public Class<?> getColumnClass(int columnIndex) {
      return getValueAt(0, columnIndex).getClass();
   }

   public boolean isCellEditable(int rowIndex, int columnIndex) {
      return true;
   }
   
   public void reset() {
      contents = new Object[0][4];
      numCoords=0;
   }

   public Model getModel() {
      return model;
   }

   Coordinate getCoordinate(int row) {
      return coords.get(row);
   }
   
}
