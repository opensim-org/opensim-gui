/*
 *
 * CoordinateTableModel
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
