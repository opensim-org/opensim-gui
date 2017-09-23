/* -------------------------------------------------------------------------- *
 * OpenSim: ExcitationPanelListener.java                                      *
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
