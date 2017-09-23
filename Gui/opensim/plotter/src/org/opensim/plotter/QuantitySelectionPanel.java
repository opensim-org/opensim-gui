/* -------------------------------------------------------------------------- *
 * OpenSim: QuantitySelectionPanel.java                                       *
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

package org.opensim.plotter;

import java.util.regex.Pattern;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.opensim.modeling.ArrayStr;
import org.opensim.modeling.Storage;



// Class for "Single" quantity selection.
class QuantitySelectionPanel extends JPanel
{
   private final JPlotterQuantitySelector jPlotterQuantitySelectorSingle;

   String[] selected;
   boolean isDomain;
   public QuantitySelectionPanel(JPlotterQuantitySelector jPlotterQuantitySelectorSingle, PlotterSourceInterface source, String filterRegex, boolean isDomain)
   {
      this.jPlotterQuantitySelectorSingle = jPlotterQuantitySelectorSingle;
      this.isDomain=isDomain;
      Storage nextStorage = source.getStorage();
      ArrayStr columnLabels = nextStorage.getColumnLabels();
      int numEntries = columnLabels.getSize();
      // make a JList embedded in a ScrollPane and add entries to it
      //final JPopupMenu p = new JPopupMenu();
      DefaultListModel listModel = new DefaultListModel();
      int k = 0;
      if (isDomain && source instanceof PlotterSourceMotion){
          listModel.add(k, source.getStorage().getName());
          k++;
      }
      for (int j = 0; j < columnLabels.getSize(); j++){
         final String columnName = columnLabels.getitem(j);
         if (Pattern.matches(filterRegex, columnName)){
              listModel.add(k, columnName);
              k++;
         }
      }
      final JList list = new JList(listModel);
      if (this.jPlotterQuantitySelectorSingle.isDomain)
         list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      else
         list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      
      list.addListSelectionListener(new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent e) { 
            Object obj = e.getSource();
            JList lsm = (JList)e.getSource();   // Documentation says it's ListSlectionModel!'

            int firstIndex = e.getFirstIndex();
            int lastIndex = e.getLastIndex();
            boolean isAdjusting = e.getValueIsAdjusting(); 
            if (lsm.isSelectionEmpty()) {
                  selected=null;
            } else {
               // Find out which indexes are selected.
               int[] allSelected = lsm.getSelectedIndices();
               selected = new String[allSelected.length];
               for (int i = 0; i < allSelected.length; i++) {
                  selected[i] = (String) lsm.getModel().getElementAt(allSelected[i]);
               }
            }
         }
        });   // SelectionListener
      list.setVisibleRowCount(10);
      final JScrollPane ext = new JScrollPane(list);
      this.add(ext);
   }

   public String[] getSelected() {
      if (selected != null && selected.length >0){
         return selected;
      }
      return null;
   }
}