/*
 * Copyright (c)  2005-2008, Stanford University
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