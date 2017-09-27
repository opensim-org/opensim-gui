/* -------------------------------------------------------------------------- *
 * OpenSim: PlotTreeModel.java                                                *
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
 * PlotTreeModel
 * Author(s): Ayman Habib
 */
package org.opensim.plotter;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author Ayman, Class backing up the tree of plots that shows in the plotter dialog
 * with the following Hierarchy
 * -Plots
 *    - Plot
 *       -Curve
 */
public class PlotTreeModel extends DefaultTreeModel {
   
   Plot  currentPlot=null;
   
   /** Creates a new instance of PlotTreeModel */
   public PlotTreeModel() {
      super(new DefaultMutableTreeNode("Plots") , false);
   }
   
   public int getNumberOfPlots()
   {
      return ((DefaultMutableTreeNode)getRoot()).getChildCount();
   }
   
   public Plot getPlot(int index)
   {
      PlotNode fnode = (PlotNode)((DefaultMutableTreeNode)getRoot()).getChildAt(index);
      return (Plot)(fnode.getUserObject());
   }
   public int addPlot(Plot newPlot)
   {
      int pos = getNumberOfPlots();
      // Create a node for the figure
      PlotNode figNode = new PlotNode(newPlot);
      insertNodeInto(figNode, (DefaultMutableTreeNode)getRoot(), pos);
      currentPlot = newPlot;
      return pos;
   }
   /**
    * Get the node that represents a figure
    */
   public PlotNode findPlotNode(Plot fig)
   {
      int nf=getNumberOfPlots();
      for(int i= 0; i<nf; i++){
         PlotNode figNode = (PlotNode) ((DefaultMutableTreeNode)getRoot()).getChildAt(i);
         if (((Plot)figNode.getUserObject()).equals(fig))
            return (PlotNode)figNode;
      }
      return null;
   }
   /**
    * Get the node for the figure that contains the passed in plotCurve
    */
   public PlotNode findPlotNode(PlotCurve cv)
   {
      return (PlotNode) findCurveNode(cv).getParent();
   }
   
   public PlotCurveNode findCurveNode(PlotCurve cv)
   {
      int nf=getNumberOfPlots();
      for(int i= 0; i<nf; i++){
         PlotNode figNode = (PlotNode) ((DefaultMutableTreeNode)getRoot()).getChildAt(i);
         int figNodeChildrenCount = figNode.getChildCount();
         for(int c=0;c<figNodeChildrenCount;c++){
            PlotCurveNode cvNode = (PlotCurveNode) (figNode.getChildAt(c));
            if (cvNode.getUserObject() instanceof PlotCurve){
               if (cvNode.getUserObject().equals(cv))
                  return cvNode;
            }
         }
      }
      return null;

   }
   /**
    * Add a Plot to the current Plot
    */
   public void addPlotCurveToTree(PlotCurve crv)
   {
      // Find node for figure
      int nf=getNumberOfPlots();
      for(int i= 0; i<nf; i++){
         Plot fig = getPlot(i);
         PlotNode figNode = findPlotNode(fig);
         if (fig.equals(currentPlot)){
            // Create node for PlotCurve
            PlotCurveNode cnode = new PlotCurveNode(crv);
            insertNodeInto(cnode, figNode, figNode.getChildCount());
         }
      }
   }
   
}
