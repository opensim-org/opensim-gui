/* -------------------------------------------------------------------------- *
 * OpenSim: UserTreeCellEditor.java                                           *
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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.plotter;

import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author ayman
 */
public class UserTreeCellEditor extends DefaultTreeCellEditor {
    
   public UserTreeCellEditor(JTree tree, DefaultTreeCellRenderer renderer) {
      super(tree, renderer);
   }
   public Object getCellEditorValue() {
     Object returnValue = null;
     Object value = super.getCellEditorValue();
     if(item == null && topItem == null) {
       returnValue = value;
     } 
     else if (item != null){
        item.getCurveSeries().fireSeriesChanged();
        item.setLegend((String)value);
       returnValue = item;
     }
     else {
         topItem.setTitle((String)value);
         returnValue = topItem;
     }
     return returnValue;
   }
   public Component getTreeCellEditorComponent(JTree tree, Object value,
     boolean isSelected, boolean expanded, boolean leaf, int row) {
     if(value instanceof DefaultMutableTreeNode) {
       DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
       Object userObject = node.getUserObject();
       
       if(userObject instanceof PlotCurve) {
         item = (PlotCurve) node.getUserObject();
         topItem = null;
       }
       else if (userObject instanceof Plot) {
           topItem = (Plot) node.getUserObject();
           item = null;
       }
     }
     return super.getTreeCellEditorComponent(
     tree, value, isSelected, expanded, leaf, row);
   }
   private PlotCurve item;
   private Plot topItem;
}

/*
 * if (node instanceof PlotNode){ // Chart properties
            dPlot = ((Plot)node.getUserObject());
         } else if (node instanceof PlotCurveNode){
            cv = ((PlotCurve)node.getUserObject());
         }
 * */
 