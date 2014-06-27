/*
 *
 * PlotTreeModel
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
