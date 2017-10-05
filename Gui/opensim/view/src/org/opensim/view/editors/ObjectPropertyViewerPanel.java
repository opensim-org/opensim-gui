/* -------------------------------------------------------------------------- *
 * OpenSim: ObjectPropertyViewerPanel.java                                    *
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

package org.opensim.view.editors;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import org.openide.util.Exceptions;
import org.opensim.modeling.OpenSimObject;
import org.opensim.swingui.JTableButtonMouseListener;
import org.opensim.swingui.JTableButtonRenderer;

class ObjectPropertyViewerTreeTable extends JTreeTable {
   private TreeTableModelAdapter adapter = null;
   private OpenSimObjectModel model = null;

   public ObjectPropertyViewerTreeTable(OpenSimObjectModel model) {
      super(model);
      this.model = model;
      this.adapter = (TreeTableModelAdapter)getModel();
   }

   public TableCellRenderer getCellRenderer(int row, int column) {
      TableColumn tableColumn = getColumnModel().getColumn(column);
      TableCellRenderer renderer = tableColumn.getCellRenderer();
      if (renderer == null) renderer = getDefaultRenderer(model.getCellClass(adapter.nodeForRow(row), convertColumnIndexToModel(column)));
      return renderer;
   }

   public TableCellEditor getCellEditor(int row, int column) {
      TableColumn tableColumn = getColumnModel().getColumn(column);
      TableCellEditor editor = tableColumn.getCellEditor();
      if (editor == null) editor = getDefaultEditor(model.getCellClass(adapter.nodeForRow(row), convertColumnIndexToModel(column)));
      return editor;
   }

   public String getToolTipText(MouseEvent e) {
      java.awt.Point p = e.getPoint();
      int row = rowAtPoint(p);
      int column = columnAtPoint(p);
      return model.getToolTipText(adapter.nodeForRow(row), convertColumnIndexToModel(column));
   }
}

// The default renderer for double values in the table sucks because it uses commas (e.g. 100,000.0) and also doesn't use scientific notation
// even for huge numbers... so I prefer using toString() which does a pretty good job of formatting doubles.
class MyNumberRenderer extends DefaultTableCellRenderer.UIResource {
   public MyNumberRenderer() { super(); setHorizontalAlignment(JLabel.RIGHT); } 
	public void setValue(Object value) { setText((value==null) ? "" : value.toString()); }
}

/**
 * Assembles the UI (a JTreeTable).
 * Based on example by Scott Violet, SUN Micro
 */
public class ObjectPropertyViewerPanel extends JPanel {
    private static final int controlsColumnWidth = 16;

    private OpenSimObjectModel model;
    protected JTreeTable         treeTable;
    protected OpenSimObject      object;
    protected boolean            editMode;

    public ObjectPropertyViewerPanel(OpenSimObject aObject, boolean allowModification) {
        this.object = aObject;
        editMode = allowModification;
        if (object == null)
            return;
        
        model = new OpenSimObjectModel(aObject, editMode);
        //treeTable = new JTreeTable(model);
        treeTable = new ObjectPropertyViewerTreeTable(model);

        // Create column model and assign renderer to show tooltip
        ColumnHeaderRenderer renderer = new ColumnHeaderRenderer();
        for (int i=0;i<model.getColumnCount();i++)
          treeTable.getColumnModel().getColumn(i).setHeaderRenderer(renderer);

        // Set column width for the controls column
        treeTable.getColumnModel().getColumn(1).setPreferredWidth(controlsColumnWidth);
        treeTable.getColumnModel().getColumn(1).setMinWidth(controlsColumnWidth);
        treeTable.getColumnModel().getColumn(1).setMaxWidth(controlsColumnWidth);

        // Set column width for the extra padding column
        treeTable.getColumnModel().getColumn(3).setPreferredWidth(controlsColumnWidth);
        treeTable.getColumnModel().getColumn(3).setMinWidth(controlsColumnWidth);
        treeTable.getColumnModel().getColumn(3).setMaxWidth(controlsColumnWidth);

        // Don't allow selection (no use for it here)
        treeTable.setRowSelectionAllowed(false);

        treeTable.setDefaultRenderer(JButton.class, new JTableButtonRenderer(treeTable.getDefaultRenderer(JButton.class)));
        treeTable.setDefaultRenderer(Double.class, new MyNumberRenderer());

        // To handle pressing the "controls" buttons
        treeTable.addMouseListener(new JTableButtonMouseListener(treeTable));

        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.add(new JScrollPane(treeTable));

        model.reloadChildren(model.getRoot());
    }

    /**
     * @return the model
     */
    public OpenSimObjectModel getModel() {
        return model;
    }

   // Handler for column header painting
   class ColumnHeaderRenderer extends JLabel implements TableCellRenderer {
      public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
         setToolTipText(getModel().getColumnHeaderToolTip(column));
         setText((value ==null) ? "" : value.toString());
         setBorder(UIManager.getBorder("TableHeader.cellBorder"));
         return this;
      }
   }
}
