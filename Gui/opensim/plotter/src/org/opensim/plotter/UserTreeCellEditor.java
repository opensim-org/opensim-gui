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
 * Copyright (c)  2005-2012, Stanford University, Ayman Habib
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
       }
       else if (userObject instanceof Plot) {
           topItem = (Plot) node.getUserObject();
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
 