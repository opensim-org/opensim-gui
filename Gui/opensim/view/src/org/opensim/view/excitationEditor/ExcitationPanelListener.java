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
/*
 * ExcitationPanelListener.java
 *
 * Created on February 14, 2008, 10:21 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.view.excitationEditor;

import java.util.ArrayList;
import java.util.Vector;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.opensim.modeling.ArrayInt;
import org.opensim.modeling.ControlLinear;
import org.opensim.modeling.ControlLinearNode;
import org.opensim.modeling.Function;
import org.opensim.modeling.SetControlNodes;
import org.opensim.modeling.XYFunctionInterface;
import org.opensim.view.functionEditor.FunctionNode;
import org.opensim.view.functionEditor.FunctionPanel;
import org.opensim.view.functionEditor.FunctionPanelListener;
import org.opensim.view.functionEditor.FunctionXYSeries;

/**
 *
 * @author Ayman
 */
public class ExcitationPanelListener implements FunctionPanelListener{
    FunctionPanel functionPanel;
    ControlLinear control;
    // THE LIST OF DISPLAYED FUNCTIONS IS CACHED SO THAT INSERTION AND DELETION IS POSSIBLE
    // THE LIST NEEDS UPDATING WHENEVER ORIGINAL FUNCTION IS RECREATED.
    Vector<XYFunctionInterface> functions;//=new  Vector<Function>(3);
    //ControlLinearNode newControlNode;
    /**
     * Creates a new instance of ExcitationPanelListener
     */
    public ExcitationPanelListener(FunctionPanel functionPanel, ControlLinear excitation, Vector<XYFunctionInterface> functions) {
        this.functionPanel = functionPanel;
        this.control = excitation;
        this.functions=functions;
    }

   public void toggleSelectedNode(int series, int node) {
      //updateXYTextFields();
   }

   public void clearSelectedNodes() {
      //updateXYTextFields();
   }

   public void replaceSelectedNode(int series, int node) {
      //updateXYTextFields();
   }

   private void cropDragVector(double dragVector[]) {
      ExcitationRenderer renderer = (ExcitationRenderer) functionPanel.getRenderer();
      if (renderer == null)
         return;
      // Don't allow any dragged node to go past either of its neighbors in the X dimension.
      double minGap = 99999999.9;
      double gap = minGap;
      ArrayList<FunctionNode> selectedNodes = functionPanel.getSelectedNodes();
      if (dragVector[0] < 0.0) {  // dragging to the left
         for (int i=0; i<selectedNodes.size(); i++) {
            int index = selectedNodes.get(i).node;
            int series = selectedNodes.get(i).series;
            if (!renderer.getSeriesShapesVisible(series))   // series shapes are off, so this node can't move
               continue;
            if (index == 0) {  // there is no left neighbor, so the plot's left edge is the boundary
               ControlLinearNode cn= getControlNodeForSeries(series, index);
               gap = cn.getTime() - functionPanel.getChart().getXYPlot().getDomainAxis().getLowerBound() *
                  renderer.getXDisplayUnits().convertTo(renderer.getXUnits());
               // If the node is already outside the plot area, don't crop it against the plot edge
               if (gap < 0.0)
                  gap = minGap;
            } else if (!functionPanel.isNodeSelected(0, index-1)) {  // left neighbor is not selected, so it is the boundary
               ControlLinearNode cn= getControlNodeForSeries(series, index);
               ControlLinearNode cnMinus1= getControlNodeForSeries(series, index-1);
               gap = cn.getTime() - cnMinus1.getTime();
            } else {  // left neighbor is selected, so there is no boundary for this node
               continue;
            }
            if (gap < minGap)
               minGap = gap;
         }
         // minGap is the smallest [positive] distance between a dragged node and its
         // left neighbor (if unselected). dragVector[0] can't be a larger negative
         // number than this value.
         if (dragVector[0] < -minGap)
            dragVector[0] = -minGap;
      } else if (dragVector[0] > 0.0) {  // dragging to the right
         for (int i=0; i<selectedNodes.size(); i++) {
            int index = selectedNodes.get(i).node;
            int series = selectedNodes.get(i).series;
            if (!renderer.getSeriesShapesVisible(series))   // series shapes are off, so this node can't move
               continue;
            if (index == getControlNodeCountForSeries(selectedNodes.get(i).series)-1) {  // there is no right neighbor, so the plot's right edge is the boundary
               ControlLinearNode cn= getControlNodeForSeries(series, index);
               gap = functionPanel.getChart().getXYPlot().getDomainAxis().getUpperBound() *
                  renderer.getXDisplayUnits().convertTo(renderer.getXUnits()) - cn.getTime();
               // If the node is already outside the plot area, don't crop it against the plot edge
               if (gap < 0.0)
                  gap = minGap;
            } else if (!functionPanel.isNodeSelected(selectedNodes.get(i).series, index+1)) {  // right neighbor is not selected, so it is the boundary
               ControlLinearNode cn= getControlNodeForSeries(selectedNodes.get(i).series, index);
               ControlLinearNode cnPlus1= getControlNodeForSeries(selectedNodes.get(i).series, index+1);
               gap = cnPlus1.getTime() - cn.getTime();
            } else {  // right neighbor is selected, so there is no boundary for this node
               continue;
            }
            if (gap < minGap)
               minGap = gap;
         }
         // minGap is the smallest [positive] distance between a dragged node and its
         // right neighbor (if unselected). dragVector[0] can't be a larger positive
         // number than this value.
         if (dragVector[0] > minGap)
            dragVector[0] = minGap;
      }
   }

   public void dragSelectedNodes(int series, int node, double dragVector[]) {
      ExcitationRenderer renderer = (ExcitationRenderer) functionPanel.getRenderer();
      if (renderer == null)
         return;
      cropDragVector(dragVector);
      // Now move all the control points by dragVector.
      ArrayList<FunctionNode> selectedNodes = functionPanel.getSelectedNodes();
      // Before we commit to move we need to make sure that "ALL" nodes will not go outside range 
      // NOT-ONLY the node selected to drag
      double signalMin=renderer.getControl().getDefaultParameterMin();
      double signalMax=renderer.getControl().getDefaultParameterMax();

      for (int i=0; i<selectedNodes.size(); i++) {
         int selIndex = selectedNodes.get(i).node;
         int selSeries = selectedNodes.get(i).series;
         if (renderer.getSeriesShapesVisible(selSeries)) {
            ControlLinearNode controlNode=getControlNodeForSeries(selSeries, selIndex);
            double newX = controlNode.getTime() + dragVector[0];
            double newY = controlNode.getValue() + dragVector[1];
            if (newY>signalMax) newY=signalMax;
            if (newY<signalMin) newY=signalMin;
            
            controlNode.setTime(newX);
            controlNode.setValue(newY);
            XYSeriesCollection seriesCollection = (XYSeriesCollection) functionPanel.getChart().getXYPlot().getDataset();
            ((FunctionXYSeries)seriesCollection.getSeries(selSeries)).updateByIndex(selIndex, newX, newY);
            // Update the function so that when render as line segment is called it's uptodate'
            functions.get(selSeries).setX(selIndex, newX);
            functions.get(selSeries).setY(selIndex, newY);
            seriesCollection.getSeries(selSeries).fireSeriesChanged();
         }
      }
      ((ExcitationPanel)functionPanel).setChanged(true);
   }

   public void duplicateNode(int series, int node) {
      if (control != null && node >= 0 && node < getControlNodeCountForSeries(series)) {
         // Make a new point that is offset slightly in the X direction from
         // the point to be duplicated.
         ControlLinearNode controlNode=getControlNodeForSeries(series, node);
         
         double newX = controlNode.getTime() + 0.00001;
         double newY = controlNode.getValue();
         // function, control, series
         functions.get(series).addPoint(newX, newY);
         ControlLinearNode newControlNode = new ControlLinearNode(); // Early garbage collection suspect!
         newControlNode.setTime(newX);
         newControlNode.setValue(newY);
         //getSelectedControlNodes(series).insert(node+1, newControlNode);
         if (series==0)
            control.insertNewValueNode(node+1, newControlNode);
         else if (series==1)
            control.insertNewMinNode(node+1, newControlNode);
         else
            control.insertNewMaxNode(node+1, newControlNode);
             
         XYSeriesCollection seriesCollection = (XYSeriesCollection) functionPanel.getChart().getXYPlot().getDataset();
         XYSeries dSeries=seriesCollection.getSeries(series);
         dSeries.add(newX, newY);
         
        ((ExcitationPanel)functionPanel).setChanged(true);
      }
   }

   public boolean deleteNode(int series, int node) {
      if (control != null && node >= 0 && node < control.getControlValues().getSize()) {
         // Remove the node from the function and the control.
         // Return 'true' so the ExcitationPanel will remove it from the series.
         if (functions.get(series).deletePoint(node)) {
            getControlNodes(series).remove(node);
            ((ExcitationPanel)functionPanel).setChanged(true);
            return true;
         }
      }
      return false;
   }

   public boolean deleteNodes(int series, ArrayInt nodes) {
      if (control != null) {
         // Remove the nodes from the function and the control.
         // Return 'true' so the ExcitationPanel will remove them from the series.
         if (functions.get(series).deletePoints(nodes)) {
            for (int i=0; i<nodes.getSize(); i++) {
               int index = nodes.getitem(i);
               getControlNodes(series).remove(index);
            }
            ((ExcitationPanel)functionPanel).setChanged(true);
            return true;
         }
      }
      return false;
   }

   public void addNode(int series, double x, double y) {
      if (control != null) {
         int idx = functions.get(series).addPoint(x, y);
         // Now the control
         ControlLinearNode node = new ControlLinearNode();
         node.setTime(x);
         node.setValue(y);
         if (series==0)
            control.insertNewValueNode(idx, node);
         else if (series==1)
             control.insertNewMinNode(idx, node);
         else if (series==2)
             control.insertNewMaxNode(idx, node);
         
        ((ExcitationPanel)functionPanel).setChanged(true);
      }
   }

    public void addFunction(XYFunctionInterface aFunction)
    {
        functions.add(aFunction);
    }
    
    public void removeFunction(XYFunctionInterface aFunction)
    {
        functions.remove(aFunction);
    }
    
    public void replaceFunction(XYFunctionInterface aFunction, int index)
    {
        functions.setElementAt(aFunction, index);
    }
    private ControlLinearNode getControlNodeForSeries(int series, int index)
    {
       SetControlNodes foo = this.getControlNodes(0);
       int num = foo.getSize();
       return getControlNodes(series).get(index);
    }

    private int getControlNodeCountForSeries(int series) {
        return getControlNodes(series).getSize();
    }
    private SetControlNodes getControlNodes(int series)
    {
         if (series==0)
             return control.getControlValues();
         else if (series==1)
            return control.getControlMinValues();
         else
            return control.getControlMaxValues();
        
    }
    
    void setControl(ControlLinear backup) {
        control=backup;
    } 
}
