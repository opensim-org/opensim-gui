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
 * FunctionPanel.java
 *
 * Created on November 15, 2007, 10:11 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.view.functionEditor;

import java.awt.AWTEvent;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.EventListenerList;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.event.AxisChangeListener;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;
import org.opensim.modeling.ArrayInt;

/**
 *
 * @author Peter Loan
 */
public class FunctionPanel extends ChartPanel
                        implements KeyListener {

   private boolean picking = false;
   private boolean dragging = false;
   /** Crosshairs are displayed while dragging points or when
    * the crosshairs checkbox is selected.
    */
   private boolean showCrosshairs = false;
   private boolean mandatoryCrosshairs = false;
   private boolean domainAxisAutoRange = true;
   private boolean rangeAxisAutoRange = true;
   FunctionNode dragNode = null;
   int dragScreenXOld = -1, dragScreenYOld = -1;
   int lastMouseX = -1, lastMouseY = -1;
   double dragDataXOld = -99.9, dragDataYOld = -99.9;
   int lastLeftButtonClickCount = 0;
   FunctionNode lastLeftButtonClickNode = null;
   protected FunctionNode rightClickNode = null;
   private int rightClickX = -1;
   private int rightClickY = -1;
   private Point boxSelectPoint = null;
   private transient Rectangle2D boxSelectRectangle = null;
   protected ArrayList<FunctionNode> selectedNodes = new ArrayList<FunctionNode>(0);
   private ArrayList<FunctionNode> oldBoxSelectNodes = null;
   private final java.awt.Color boxSelectColor = java.awt.Color.green; // to get purple select box
   private XYLineAndShapeRendererWithHighlight renderer;
   protected JPopupMenu addNodePopUpMenu;
   private JPopupMenu nodePopUpMenu;
   private XYTextAnnotation crosshairAnnotation = null;
   public static final String DUPLICATE_NODE_COMMAND = "DUPLICATE_NODE";
   public static final String DELETE_NODE_COMMAND = "DELETE_NODE";
   public static final String ADD_NODE_COMMAND = "ADD_NODE";
   protected EventListenerList functionPanelListeners;
/*
   class EnforceExcitationRange implements AxisChangeListener {
      public void axisChanged(AxisChangeEvent event) {
         if (zero_to_one_checkbox is checked) {
            Axis axis = event.getAxis();
            if (axis instanceof ValueAxis) {
               ValueAxis va = (ValueAxis)axis;
               if (va.isAutoRange()) {
                  va.setAutoRange(false);
                  va.setRangeWithMargins(0.0, 1.0);
               }
            }
         }
      }
   }
*/
   /** Creates a new instance of FunctionPanel */
   public FunctionPanel(JFreeChart chart) {
      super(chart);
      //this.setFocusable(true);
      this.functionPanelListeners = new EventListenerList();
      this.renderer = (XYLineAndShapeRendererWithHighlight) chart.getXYPlot().getRenderer();
      this.enableEvents(AWTEvent.INPUT_METHOD_EVENT_MASK);
      this.enableEvents(AWTEvent.KEY_EVENT_MASK);
      this.addKeyListener(this);
      this.nodePopUpMenu = createNodePopupMenu();
      this.addNodePopUpMenu = createAddNodePopupMenu();
      this.crosshairAnnotation = new XYTextAnnotation("", 0, 0);
      this.crosshairAnnotation.setTextAnchor(TextAnchor.BOTTOM_LEFT);
      // Make sure the X and Y ranges are not zero, which messes up the display
      chart.getXYPlot().getDomainAxis().setAutoRangeMinimumSize(0.000001);
      chart.getXYPlot().getRangeAxis().setAutoRangeMinimumSize(0.000001);
      //chart.getXYPlot().getRangeAxis().setAutoRange(false);
      //chart.getXYPlot().getRangeAxis().setRangeWithMargins(0.0, 1.0);
      //chart.getXYPlot().getRangeAxis().addChangeListener(new EnforceExcitationRange());
   }

   public void updateCursorLocation(MouseEvent e) {
      lastMouseX = e.getX();
      lastMouseY = e.getY();
   }

   public XYLineAndShapeRendererWithHighlight getRenderer() {
      return this.renderer;
   }

   public void mouseEntered(MouseEvent e) {
      e.getComponent().requestFocusInWindow();
      updateCursorLocation(e);
      updateCrosshairs(e.getX(), e.getY());
   }

   public void mouseExited(MouseEvent e) {
      updateCursorLocation(e);
      updateCrosshairs(-1, -1);
      picking = false;
      endBoxSelect();
      super.mouseExited(e);
   }

   public void mousePressed(MouseEvent e) {
      this.requestFocusInWindow();
      updateCursorLocation(e);
      FunctionNode leftClickNode = null;
      int keyMods = e.getModifiers();
      if ((keyMods & InputEvent.BUTTON1_MASK) > 0) {
         leftClickNode = findNodeAt(e.getX(), e.getY());

         // Some code to handle double clicking on an object, but which does so in a way that the sequence
         // CTRL-Click and Click does not count as a double click.  This avoids
         // treating as double click the case where the user selects an object
         // (CTRL-Click) and quickly starts dragging (Click & Drag) 
         if (leftClickNode != null && picking == false) {
            if (e.getClickCount() == lastLeftButtonClickCount+1 && leftClickNode == lastLeftButtonClickNode) {
               //handleDoubleClick(leftClickNode);
               return; 
            } else {
               lastLeftButtonClickCount = e.getClickCount();
               lastLeftButtonClickNode = leftClickNode;
            } 
         } else {
            lastLeftButtonClickCount = -1;
            lastLeftButtonClickNode = null;
         }
      }

      if ((keyMods & InputEvent.BUTTON3_MASK) > 0) {
         rightClickNode = findNodeAt(e.getX(), e.getY());
         rightClickX = e.getX();
         rightClickY = e.getY();
      } else if (picking == true && (keyMods & InputEvent.BUTTON1_MASK) > 0) {
         if (leftClickNode == null) {
            // Picking mode is on, but the user clicked away from a control point.
            // So clear the selections (unless Shift is pressed) and prepare for a box select.
            if ((keyMods & InputEvent.SHIFT_MASK) <= 0)
               clearSelectedNodes();
            startBoxSelect(e);
         } else {
            if ((keyMods & InputEvent.SHIFT_MASK) > 0) {
               toggleSelectedNode(leftClickNode.series, leftClickNode.node);
            } else {
               replaceSelectedNode(leftClickNode.series, leftClickNode.node);
            }
         }
      } else if ((leftClickNode != null) && listContainsNode(leftClickNode, selectedNodes) == true) {
         XYPlot xyPlot = getChart().getXYPlot();
         dragNode = leftClickNode;
         dragScreenXOld = e.getX();
         dragScreenYOld = e.getY();
         RectangleEdge xAxisLocation = xyPlot.getDomainAxisEdge();
         RectangleEdge yAxisLocation = xyPlot.getRangeAxisEdge();
         Rectangle2D dataArea = getScreenDataArea();
         dragDataXOld = xyPlot.getDomainAxis().java2DToValue((double)e.getX(), dataArea, xAxisLocation);
         dragDataYOld = xyPlot.getRangeAxis().java2DToValue((double)e.getY(), dataArea, yAxisLocation);
         setDragging(true);
         // During dragging, the crosshairs lock onto the center of the dragNode
         double crosshairX = xyPlot.getDataset().getXValue(dragNode.series, dragNode.node);
         double crosshairY = xyPlot.getDataset().getYValue(dragNode.series, dragNode.node);
         double crosshairScreenX = xyPlot.getDomainAxis().valueToJava2D(crosshairX, dataArea, xAxisLocation);
         double crosshairScreenY = xyPlot.getRangeAxis().valueToJava2D(crosshairY, dataArea, yAxisLocation);
         updateCrosshairs((int)crosshairScreenX, (int)crosshairScreenY);
         picking = false;
      } else {
         super.mousePressed(e);
      }
   }

   public void mouseDragged(MouseEvent e) {
      updateCursorLocation(e);
      int keyMods = e.getModifiers();
      if (picking == true && (keyMods & InputEvent.BUTTON1_MASK) > 0) {
         // do nothing; you're still in picking mode
         dragScreenXOld = e.getX();
         dragScreenYOld = e.getY();
         updateCrosshairs(e.getX(), e.getY());
         doBoxSelect(e);
      } else if (getDragging() == true && (keyMods & InputEvent.BUTTON1_MASK) > 0) {
         if (e.getX() != dragScreenXOld || e.getY() != dragScreenYOld) {
            if (dragNode != null) {
               XYPlot xyPlot = getChart().getXYPlot();
               RectangleEdge xAxisLocation = xyPlot.getDomainAxisEdge();
               RectangleEdge yAxisLocation = xyPlot.getRangeAxisEdge();
               Rectangle2D dataArea = getScreenDataArea();
               double dragDataXNew = xyPlot.getDomainAxis().java2DToValue((double)e.getX(), dataArea, xAxisLocation);
               double dragDataYNew = xyPlot.getRangeAxis().java2DToValue((double)e.getY(), dataArea, yAxisLocation);
               if (dragDataXNew > xyPlot.getDomainAxis().getUpperBound())
                  dragDataXNew = xyPlot.getDomainAxis().getUpperBound();
               else if (dragDataXNew < xyPlot.getDomainAxis().getLowerBound())
                  dragDataXNew = xyPlot.getDomainAxis().getLowerBound();
               if (dragDataYNew > xyPlot.getRangeAxis().getUpperBound())
                  dragDataYNew = xyPlot.getRangeAxis().getUpperBound();
               else if (dragDataYNew < xyPlot.getRangeAxis().getLowerBound())
                  dragDataYNew = xyPlot.getRangeAxis().getLowerBound();
               // the amount to drag the objects is the distance between dragPtOld and dragPtNew
               double dragVector[] = new double[2];
               dragVector[0] = dragDataXNew - dragDataXOld;
               dragVector[1] = dragDataYNew - dragDataYOld;
               // drag the selected objects
               dragSelectedNodes(dragNode.series, dragNode.node, dragVector);
               // store the new point as the old, for use next time
               dragDataXOld = dragDataXNew;
               dragDataYOld = dragDataYNew;
               // During dragging, the crosshairs lock onto the center of the dragNode
               double crosshairX = xyPlot.getDataset().getXValue(dragNode.series, dragNode.node);
               double crosshairY = xyPlot.getDataset().getYValue(dragNode.series, dragNode.node);
               double crosshairScreenX = xyPlot.getDomainAxis().valueToJava2D(crosshairX, dataArea, xAxisLocation);
               double crosshairScreenY = xyPlot.getRangeAxis().valueToJava2D(crosshairY, dataArea, yAxisLocation);
               updateCrosshairs((int)crosshairScreenX, (int)crosshairScreenY);
            }
         }
         dragScreenXOld = e.getX();
         dragScreenYOld = e.getY();
      } else {
         super.mouseDragged(e);
      }
   }

   public void mouseMoved(MouseEvent e) {
      updateCursorLocation(e);
      if (showCrosshairs == true) {
         updateCrosshairs(e.getX(), e.getY());
      } else {
         super.mouseMoved(e);
      }
   }

   public void mouseReleased(MouseEvent e) {
      updateCursorLocation(e);
      int keyMods = e.getModifiers();
      if (picking == true && (keyMods & InputEvent.BUTTON1_MASK) > 0) {
         endBoxSelect();
      } else if (getDragging() == true && (keyMods & InputEvent.BUTTON1_MASK) > 0) {
         dragNode = null;
         dragScreenXOld = -1;
         dragScreenYOld = -1;
         dragDataXOld = -99.9;
         dragDataYOld = -99.9;
         setDragging(false);
      } else if ((keyMods & InputEvent.BUTTON3_MASK) > 0 && e.isPopupTrigger()) {
         if (rightClickNode != null && this.nodePopUpMenu != null)
            this.nodePopUpMenu.show(this, e.getX(), e.getY());
         else if (this.addNodePopUpMenu != null)
            this.addNodePopUpMenu.show(this, e.getX(), e.getY());
      } else {
         super.mouseReleased(e);
      }
   }

   private void startBoxSelect(MouseEvent e) {
      if (this.boxSelectRectangle == null) {
         Rectangle2D screenDataArea = getScreenDataArea(e.getX(), e.getY());
         if (screenDataArea != null) {
            this.boxSelectPoint = getPointInRectangle(e.getX(), e.getY(), screenDataArea);
         }
         else {
            this.boxSelectPoint = null;
         }
      }
   }

   private void doBoxSelect(MouseEvent e) {
      // if no initial boxSelect point was set, ignore dragging...
      if (this.boxSelectPoint == null)
         return;

      Graphics2D g2 = (Graphics2D) getGraphics();

      // Use XOR to erase the old rectangle, if any.
      g2.setXORMode(boxSelectColor);
      if (this.boxSelectRectangle != null)
         g2.draw(this.boxSelectRectangle);
      // Save the current paint color. You need to restore it
      // after highlighting the control points in order for
      // the XOR drawing of the box to work properly.
      Paint savedPaint = g2.getPaint();
      g2.setPaintMode();

      Rectangle2D scaledDataArea = getScreenDataArea(
         (int) this.boxSelectPoint.getX(), (int) this.boxSelectPoint.getY());
      // Box can be dragged in any direction, so compute proper min and max
      // given direction of drag and bounds of data area.
      double xmin=0, xmax=0, ymin=0, ymax=0;
      if (e.getX() < this.boxSelectPoint.getX()) {
         xmin = Math.max(e.getX(), scaledDataArea.getMinX());
         xmax = this.boxSelectPoint.getX();
      } else {
         xmin = this.boxSelectPoint.getX();
         xmax = Math.min(e.getX(), scaledDataArea.getMaxX());
      }
      if (e.getY() < this.boxSelectPoint.getY()) {
         ymin = Math.max(e.getY(), scaledDataArea.getMinY());
         ymax = this.boxSelectPoint.getY();
      } else {
         ymin = this.boxSelectPoint.getY();
         ymax = Math.min(e.getY(), scaledDataArea.getMaxY());
      }
      this.boxSelectRectangle = new Rectangle2D.Double(xmin, ymin, xmax - xmin, ymax - ymin);

      XYPlot xyPlot = getChart().getXYPlot();
      ArrayList<FunctionNode> newBoxSelectNodes = getBoxSelectNodes(this.boxSelectRectangle);
      // For the nodes that were picked up with the latest box resizing, toggle their select state
      for (int i=0; i<newBoxSelectNodes.size(); i++) {
         if (oldBoxSelectNodes == null || listContainsNode(newBoxSelectNodes.get(i), oldBoxSelectNodes) == false) {
            toggleSelectedNode(newBoxSelectNodes.get(i).series, newBoxSelectNodes.get(i).node);
            // Draw the control point with the new color
            xyPlot.getRenderer().drawItem(g2, null, getScreenDataArea(), null, xyPlot,
               xyPlot.getDomainAxis(), xyPlot.getRangeAxis(), xyPlot.getDataset(), newBoxSelectNodes.get(i).series,
               newBoxSelectNodes.get(i).node, null, 1);
         }
      }
      // For the nodes that dropped out with the latest box resizing, toggle their select state
      if (oldBoxSelectNodes != null) {
         for (int i=0; i<oldBoxSelectNodes.size(); i++) {
            if (listContainsNode(oldBoxSelectNodes.get(i), newBoxSelectNodes) == false) {
               toggleSelectedNode(oldBoxSelectNodes.get(i).series, oldBoxSelectNodes.get(i).node);
               // Draw the control point with the new color
               xyPlot.getRenderer().drawItem(g2, null, getScreenDataArea(), null, xyPlot,
                  xyPlot.getDomainAxis(), xyPlot.getRangeAxis(), xyPlot.getDataset(), oldBoxSelectNodes.get(i).series,
                  oldBoxSelectNodes.get(i).node, null, 1);
            }
         }
      }
      oldBoxSelectNodes = newBoxSelectNodes;

      // Use XOR to draw the new rectangle.
      g2.setPaint(savedPaint);
      g2.setXORMode(boxSelectColor);
      if (this.boxSelectRectangle != null)
         g2.draw(this.boxSelectRectangle);

      g2.dispose();
   }

   private void endBoxSelect() {
      // use XOR to erase the last zoom rectangle.
      if (this.boxSelectRectangle != null) {
         Graphics2D g2 = (Graphics2D) getGraphics();
         g2.setXORMode(boxSelectColor);
         g2.draw(this.boxSelectRectangle);
         this.boxSelectPoint = null;
         this.boxSelectRectangle = null;
         this.oldBoxSelectNodes = null;
      }
   }

   private ArrayList<FunctionNode> getBoxSelectNodes(Rectangle2D box) {
      ArrayList<FunctionNode> nodes = new ArrayList<FunctionNode>(0);
      XYPlot xyPlot = getChart().getXYPlot();
      XYDataset xyDataset = xyPlot.getDataset();
      // Compute dataBox (data coordinates, X right, Y up)
      // from box (screen coordinates, X right, Y down)
      RectangleEdge xAxisLocation = xyPlot.getDomainAxisEdge();
      RectangleEdge yAxisLocation = xyPlot.getRangeAxisEdge();
      Rectangle2D dataArea = getScreenDataArea();
      double dataXMin = xyPlot.getDomainAxis().java2DToValue(box.getMinX(), dataArea, xAxisLocation);
      double dataXMax = xyPlot.getDomainAxis().java2DToValue(box.getMaxX(), dataArea, xAxisLocation);
      double dataYMin = xyPlot.getRangeAxis().java2DToValue(box.getMaxY(), dataArea, yAxisLocation);
      double dataYMax = xyPlot.getRangeAxis().java2DToValue(box.getMinY(), dataArea, yAxisLocation);
      Rectangle2D dataBox = new Rectangle2D.Double(dataXMin, dataYMin, dataXMax - dataXMin, dataYMax - dataYMin);
      for (int i=0; i<xyDataset.getSeriesCount(); i++) {
         if (renderer.getSeriesShapesVisible(i)) {
            for (int j=0; j<xyDataset.getItemCount(i); j++) {
               double x = xyDataset.getXValue(i, j);
               double y = xyDataset.getYValue(i, j);
               if (dataBox.contains(x, y) == true)
                  nodes.add(new FunctionNode(i, j));
            }
         }
      }
      return nodes;
   }

   /* This method is in ChartPanel, but is private. */
   private Point getPointInRectangle(int x, int y, Rectangle2D area) {
       x = (int) Math.max(Math.ceil(area.getMinX()), Math.min(x, 
               Math.floor(area.getMaxX())));   
       y = (int) Math.max(Math.ceil(area.getMinY()), Math.min(y, 
               Math.floor(area.getMaxY())));
       return new Point(x, y);
   }

   public void updateCrosshairs(int screenX, int screenY) {
      XYPlot xyPlot = getChart().getXYPlot();
      if (showCrosshairs == true) {
         RectangleEdge xAxisLocation = xyPlot.getDomainAxisEdge();
         RectangleEdge yAxisLocation = xyPlot.getRangeAxisEdge();
         Rectangle2D dataArea = getScreenDataArea();
         double crosshairX = xyPlot.getDomainAxis().java2DToValue((double)screenX, dataArea, xAxisLocation);
         double crosshairY = xyPlot.getRangeAxis().java2DToValue((double)screenY, dataArea, yAxisLocation);
         if (crosshairX < xyPlot.getDomainAxis().getLowerBound() ||
             crosshairX > xyPlot.getDomainAxis().getUpperBound() ||
             crosshairY < xyPlot.getRangeAxis().getLowerBound() ||
             crosshairY > xyPlot.getRangeAxis().getUpperBound()) {
            xyPlot.setDomainCrosshairVisible(false);
            xyPlot.setRangeCrosshairVisible(false);
            crosshairAnnotation.setText("");
         } else {
            xyPlot.setDomainCrosshairVisible(true);
            xyPlot.setRangeCrosshairVisible(true);
            /** The xyPlot's crosshairs are updated with the screen coordinates of the XY location.
             * The annotation's text and location is updated with the data coordinates.
             * The format used for displaying the XY data coordinates is taken from the
             * format used for the X and Y axis tick labels. This format is not normally
             * accessible here, but a method was added to NumberTickUnit to provide it.
             * If this turns out to be problematic, the format could always be changed
             * to use a fixed number of significant digits.
             */
            NumberAxis dna = (NumberAxis)xyPlot.getDomainAxis();
            NumberFormat dnf = (NumberFormat)dna.getTickUnit().getFormatter().clone();
            dnf.setMaximumFractionDigits(dnf.getMaximumFractionDigits()+3);
            String xString = dnf.format(crosshairX);
            NumberAxis rna = (NumberAxis)xyPlot.getRangeAxis();
            NumberFormat rnf = (NumberFormat)rna.getTickUnit().getFormatter().clone();
            rnf.setMaximumFractionDigits(rnf.getMaximumFractionDigits()+3);
            String yString = rnf.format(crosshairY);
            crosshairAnnotation.setText("(" + xString + ", " + yString + ")");
            crosshairAnnotation.setX(crosshairX);
            crosshairAnnotation.setY(crosshairY);
            xyPlot.setDomainCrosshairValue(crosshairX);
            xyPlot.setRangeCrosshairValue(crosshairY);
            // JPL 11/19/09: the chart's plotInfo does not appear to be updated when the
            // FunctionPanel is resized. So the following call to handleClick will pass in
            // the wrong plot area for calculating crosshair coordinates. So instead, set
            // the crosshair directly with the two lines above this comment.
            //xyPlot.handleClick(screenX, screenY, this.getChartRenderingInfo().getPlotInfo());
         }
      }
   }

   public void setDragging(boolean state) {
      XYPlot xyPlot = getChart().getXYPlot();
      dragging = state;
      if (state == true) {
         // Save the autoRange state, then set it to false for dragging.
         domainAxisAutoRange = xyPlot.getDomainAxis().isAutoRange();
         rangeAxisAutoRange = xyPlot.getRangeAxis().isAutoRange();
         xyPlot.getDomainAxis().setAutoRange(false);
         xyPlot.getRangeAxis().setAutoRange(false);
      } else {
         // Restore the autoRange state
         xyPlot.getDomainAxis().setAutoRange(domainAxisAutoRange);
         xyPlot.getRangeAxis().setAutoRange(rangeAxisAutoRange);
      }
      if (mandatoryCrosshairs == false)
         setCrosshairsState(state);
   }

   public boolean getDragging() {
      return this.dragging;
   }

   public void setMandatoryCrosshairs(boolean state) {
      mandatoryCrosshairs = state;
      setCrosshairsState(state);
      if (state == true)
         updateCrosshairs(-1, -1);
   }

   public void setCrosshairsState(boolean state) {
      XYPlot xyPlot = getChart().getXYPlot();
      // With two ways to turn on/off crosshairs, it's possible
      // to call this method twice in a row with state=true.
      // But you want to add the annotation only once.
      if (state == true && showCrosshairs == false) {
         crosshairAnnotation.setText("");
         xyPlot.addAnnotation(crosshairAnnotation);
      } else if (state == false) {
         xyPlot.removeAnnotation(crosshairAnnotation);
      }
      if (state == false) {
         xyPlot.setDomainCrosshairVisible(false);
         xyPlot.setRangeCrosshairVisible(false);
      }
      showCrosshairs = state;
   }

   public void keyPressed(KeyEvent e) {
      if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
         picking = true;
      } else if (e.getKeyCode() == KeyEvent.VK_I) {
         zoomPlot(lastMouseX, lastMouseY, true);
      } else if (e.getKeyCode() == KeyEvent.VK_O) {
         zoomPlot(lastMouseX, lastMouseY, false);
      } else if (e.getKeyCode() == KeyEvent.VK_L || e.getKeyCode() == KeyEvent.VK_R ||
                 e.getKeyCode() == KeyEvent.VK_U || e.getKeyCode() == KeyEvent.VK_D) {
         panPlot(e.getKeyCode());
      } else if (e.getKeyCode() == KeyEvent.VK_DELETE) {
         deleteSelectedNodes();
      }
   }

   public void keyTyped(KeyEvent e) {
   }

   private void zoomPlot(int screenX, int screenY, boolean zoomIn) {
      XYPlot xyPlot = getChart().getXYPlot();
      RectangleEdge xAxisLocation = xyPlot.getDomainAxisEdge();
      RectangleEdge yAxisLocation = xyPlot.getRangeAxisEdge();
      Rectangle2D dataArea = getScreenDataArea();
      double XOrthoChange = -(xyPlot.getDomainAxis().getUpperBound() - xyPlot.getDomainAxis().getLowerBound()) * 0.02;
      double YOrthoChange = -(xyPlot.getRangeAxis().getUpperBound() - xyPlot.getRangeAxis().getLowerBound()) * 0.02;
      if (zoomIn == false) {
         XOrthoChange = -XOrthoChange;
         YOrthoChange = -YOrthoChange;
      }
      double XPercent = (screenX - (dataArea.getMinX() + dataArea.getMaxX()) * 0.5) / ((dataArea.getMaxX() - dataArea.getMinX()) * 0.5);
      double YPercent = -(screenY - (dataArea.getMinY() + dataArea.getMaxY()) * 0.5) / ((dataArea.getMaxY() - dataArea.getMinY()) * 0.5);
      double newMinX = xyPlot.getDomainAxis().getLowerBound() - XOrthoChange * (XPercent + 1.0) * 0.5;
      double newMaxX = xyPlot.getDomainAxis().getUpperBound() - XOrthoChange * (XPercent - 1.0) * 0.5;
      double newMinY = xyPlot.getRangeAxis().getLowerBound() - YOrthoChange * (YPercent + 1.0) * 0.5;
      double newMaxY = xyPlot.getRangeAxis().getUpperBound() - YOrthoChange * (YPercent - 1.0) * 0.5;
      xyPlot.getDomainAxis().setLowerBound(newMinX);
      xyPlot.getDomainAxis().setUpperBound(newMaxX);
      xyPlot.getRangeAxis().setLowerBound(newMinY);
      xyPlot.getRangeAxis().setUpperBound(newMaxY);
   }

   private void panPlot(int keyCode) {
      XYPlot xyPlot = getChart().getXYPlot();
      RectangleEdge xAxisLocation = xyPlot.getDomainAxisEdge();
      RectangleEdge yAxisLocation = xyPlot.getRangeAxisEdge();
      Rectangle2D dataArea = getScreenDataArea();
      if (keyCode == KeyEvent.VK_U) {
         double YOrthoChange = -0.008 * (xyPlot.getRangeAxis().getUpperBound() - xyPlot.getRangeAxis().getLowerBound());
         double newMinY = xyPlot.getRangeAxis().getLowerBound() + YOrthoChange;
         double newMaxY = xyPlot.getRangeAxis().getUpperBound() + YOrthoChange;
         xyPlot.getRangeAxis().setLowerBound(newMinY);
         xyPlot.getRangeAxis().setUpperBound(newMaxY);
      } else if (keyCode == KeyEvent.VK_D) {
         double YOrthoChange = 0.008 * (xyPlot.getRangeAxis().getUpperBound() - xyPlot.getRangeAxis().getLowerBound());
         double newMinY = xyPlot.getRangeAxis().getLowerBound() + YOrthoChange;
         double newMaxY = xyPlot.getRangeAxis().getUpperBound() + YOrthoChange;
         xyPlot.getRangeAxis().setLowerBound(newMinY);
         xyPlot.getRangeAxis().setUpperBound(newMaxY);
      } else if (keyCode == KeyEvent.VK_R) {
         double XOrthoChange = -0.008 * (xyPlot.getDomainAxis().getUpperBound() - xyPlot.getDomainAxis().getLowerBound());
         double newMinX = xyPlot.getDomainAxis().getLowerBound() + XOrthoChange;
         double newMaxX = xyPlot.getDomainAxis().getUpperBound() + XOrthoChange;
         xyPlot.getDomainAxis().setLowerBound(newMinX);
         xyPlot.getDomainAxis().setUpperBound(newMaxX);
      } else if (keyCode == KeyEvent.VK_L) {
         double XOrthoChange = 0.008 * (xyPlot.getDomainAxis().getUpperBound() - xyPlot.getDomainAxis().getLowerBound());
         double newMinX = xyPlot.getDomainAxis().getLowerBound() + XOrthoChange;
         double newMaxX = xyPlot.getDomainAxis().getUpperBound() + XOrthoChange;
         xyPlot.getDomainAxis().setLowerBound(newMinX);
         xyPlot.getDomainAxis().setUpperBound(newMaxX);
      }
   }

   public void keyReleased(KeyEvent e) {
      if (e.getKeyCode() == KeyEvent.VK_CONTROL)
      {
         if (picking == true)
            endBoxSelect();
         picking = false;
      }
      else if (e.getKeyCode() == KeyEvent.VK_F1) {
         //mandatoryCrosshairs = false;
         //setCrosshairsState(false);
      }
   }

   private FunctionNode findNodeAt(int x, int y) {
      XYPlot xyPlot = getChart().getXYPlot();
      XYDataset xyDataset = xyPlot.getDataset();
      RectangleEdge xAxisLocation = xyPlot.getDomainAxisEdge();
      RectangleEdge yAxisLocation = xyPlot.getRangeAxisEdge();
      Rectangle2D dataArea = getScreenDataArea();
      // Loop through the nodes from last to first, so if the circles
      // for two or more nodes overlap, you get the one drawn on top.
      for (int i=xyDataset.getSeriesCount()-1; i>=0; i--) {
         if (renderer.getSeriesShapesVisible(i)) {
            for (int j=xyDataset.getItemCount(i)-1; j>=0; j--) {
               double sx = xyPlot.getDomainAxis().valueToJava2D(xyDataset.getXValue(i, j), dataArea, xAxisLocation);
               double sy = xyPlot.getRangeAxis().valueToJava2D(xyDataset.getYValue(i, j), dataArea, yAxisLocation);
               double distance = Math.sqrt((sx-x)*(sx-x) + (sy-y)*(sy-y));
               if (distance < 6.0) {
                  return new FunctionNode(i, j);
               }
            }
         }
      }
      return null;
   }

   protected JPopupMenu createNodePopupMenu() {

       JPopupMenu menu = new JPopupMenu("Node Commands");

       JMenuItem duplicateNodeItem = new JMenuItem("Duplicate control point");
       duplicateNodeItem.setActionCommand(DUPLICATE_NODE_COMMAND);
       duplicateNodeItem.addActionListener(this);
       menu.add(duplicateNodeItem);

       JMenuItem deleteNodeItem = new JMenuItem("Delete control point");
       deleteNodeItem.setActionCommand(DELETE_NODE_COMMAND);
       deleteNodeItem.addActionListener(this);
       menu.add(deleteNodeItem);

       return menu;
   }

   protected JPopupMenu createAddNodePopupMenu() {

       JPopupMenu menu = new JPopupMenu("Node Commands");

       JMenuItem addNodeItem = new JMenuItem("Add control point");
       addNodeItem.setActionCommand(ADD_NODE_COMMAND);
       addNodeItem.addActionListener(this);
       menu.add(addNodeItem);

       return menu;
   }

   public void actionPerformed(ActionEvent event) {
      String command = event.getActionCommand();

      if (command.equals(DUPLICATE_NODE_COMMAND)) {
         duplicateNode(rightClickNode.series, rightClickNode.node);
         rightClickNode = null;
      }
      else if (command.equals(DELETE_NODE_COMMAND)) {
         deleteNode(rightClickNode.series, rightClickNode.node);
         rightClickNode = null;
      }
      else if (command.equals(ADD_NODE_COMMAND)) {
         // TODO: maybe show a pop-up to let user select the series?
         addNode(0, rightClickX, rightClickY);
         rightClickNode = null;
      }
   }
 
   public ArrayList<FunctionNode> getSelectedNodes() {
      return selectedNodes;
   }

   public boolean isNodeSelected(int series, int node) {
      if (findNodeInList(series, node, selectedNodes) >= 0)
         return true;
      return false;
   }

   public void clearSelectedNodes() {   // Made public so that after nodes are removed nothing remains selected -Ayman
      // Unhighlight the selected nodes.
      for (int i = 0; i < selectedNodes.size(); i++)
         renderer.unhighlightNode(selectedNodes.get(i).series, selectedNodes.get(i).node);

      // Clear the selected nodes.
      selectedNodes.clear();

      this.repaint();

      // Now notify all listeners about the change.
      Object[] listeners = this.functionPanelListeners.getListenerList();
      for (int i = listeners.length - 2; i >= 0; i -= 2) {
         if (listeners[i] == FunctionPanelListener.class) {
            ((FunctionPanelListener) listeners[i + 1]).clearSelectedNodes();
         }
      }
   }

   public void toggleSelectedNode(int series, int node) {
      // Look for the node in the selected list.
      int nodeIndex = findNodeInList(series, node, selectedNodes);
      // If the node is already in the list, remove it
      if (nodeIndex >= 0) {
         selectedNodes.remove(nodeIndex);
         renderer.unhighlightNode(series, node);
      } else {
         // If the node is not already in the list, add it
         FunctionNode selectedNode = new FunctionNode(series, node);
         selectedNodes.add(selectedNode);
         renderer.highlightNode(series, node);
      }

      // Now notify all listeners about the change.
      Object[] listeners = this.functionPanelListeners.getListenerList();
      for (int i = listeners.length - 2; i >= 0; i -= 2) {
         if (listeners[i] == FunctionPanelListener.class) {
            ((FunctionPanelListener) listeners[i + 1]).toggleSelectedNode(series, node);
         }
      }
   }

   public void replaceSelectedNode(int series, int node) {
      // If the node is already in the list of selected ones, do nothing (a la Illustrator)
      // If the node is not already in the list, make this node the only selected one
      if (findNodeInList(series, node, selectedNodes) < 0)
      {
         // Unhighlight and clear the selected nodes. Do not call clearSelectedNodes
         // because this will notify all listeners unnecessarily.
         for (int i = 0; i < selectedNodes.size(); i++)
            renderer.unhighlightNode(selectedNodes.get(i).series, selectedNodes.get(i).node);
         selectedNodes.clear();
         // Set the selected node to this one.
         if (series >= 0 && node >= 0) {
            selectedNodes.add(new FunctionNode(series, node));
            renderer.highlightNode(series, node);
         }
      }

      // Now notify all listeners about the change.
      Object[] listeners = this.functionPanelListeners.getListenerList();
      for (int i = listeners.length - 2; i >= 0; i -= 2) {
         if (listeners[i] == FunctionPanelListener.class) {
            ((FunctionPanelListener) listeners[i + 1]).replaceSelectedNode(series, node);
         }
      }
   }

   protected int addNode(int series, int screenX, int screenY) {
      XYPlot xyPlot = getChart().getXYPlot();
      RectangleEdge xAxisLocation = xyPlot.getDomainAxisEdge();
      RectangleEdge yAxisLocation = xyPlot.getRangeAxisEdge();
      Rectangle2D dataArea = getScreenDataArea();
      double newNodeX = xyPlot.getDomainAxis().java2DToValue(screenX, dataArea, xAxisLocation);
      double newNodeY = xyPlot.getRangeAxis().java2DToValue(screenY, dataArea, yAxisLocation);

      // Notify all listeners about the change.
      Object[] listeners = this.functionPanelListeners.getListenerList();
      for (int i = listeners.length - 2; i >= 0; i -= 2) {
         if (listeners[i] == FunctionPanelListener.class) {
            ((FunctionPanelListener) listeners[i + 1]).addNode(series, newNodeX, newNodeY);
         }
      }

      // Now add the point to the series, first figuring out what its index will be.
      XYSeriesCollection seriesCollection = (XYSeriesCollection)xyPlot.getDataset();
      XYSeries dSeries = seriesCollection.getSeries(series);
      int index = dSeries.getItemCount();
      for (int i=0; i<dSeries.getItemCount(); i++) {
         if (dSeries.getDataItem(i).getX().doubleValue() > newNodeX) {
            index = i;
            break;
         }
      }
      dSeries.add(newNodeX, newNodeY);

      updateSelectedNodesAfterAddition(series, index);
      return index;
   }
 
   public void deleteNode(int series, int node) {
      // Notify all listeners about the change. There should be only
      // one listener, so when one returns success, remove the node from
      // the series, adjust the list of selected nodes, and return.
      Object[] listeners = this.functionPanelListeners.getListenerList();
      for (int i = listeners.length - 2; i >= 0; i -= 2) {
         if (listeners[i] == FunctionPanelListener.class) {
            boolean success = ((FunctionPanelListener) listeners[i + 1]).deleteNode(series, node);
            if (success) {
               XYSeriesCollection seriesCollection = (XYSeriesCollection)getChart().getXYPlot().getDataset();
               seriesCollection.getSeries(series).remove(node);
               seriesCollection.getSeries(series).fireSeriesChanged();
               updateSelectedNodesAfterDeletion(series, node);
               return;
            }
         }
      }
   }

   public void deleteSelectedNodes() {
      XYSeriesCollection seriesCollection = (XYSeriesCollection)getChart().getXYPlot().getDataset();
      for (int series=0; series < seriesCollection.getSeriesCount(); series++) {
         // If the series' shapes (circles) are turned off, don't allow deletion.
         if (!renderer.getSeriesShapesVisible(series))
            continue;
         Vector<Integer> sortedIndices = new Vector<Integer>(selectedNodes.size());
         for (int i=0; i<selectedNodes.size(); i++) {
            int index = selectedNodes.get(i).node;
            if (selectedNodes.get(i).series == series)
               sortedIndices.add(new Integer(index));
         }
         Collections.sort(sortedIndices);
         // Make an array of ints holding the indices sorted from highest to lowest
         ArrayInt sortedNodes  = new ArrayInt(0, 0, sortedIndices.size());
         for (int i=sortedIndices.size()-1; i>=0; i--)
            sortedNodes.append(sortedIndices.get(i));

         // Notify all listeners about the change. There should be only
         // one listener, so when one returns success, remove the nodes from
         // the series, clear the selected node list, and go on to the next series.
         Object[] listeners = this.functionPanelListeners.getListenerList();
         for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == FunctionPanelListener.class) {
               boolean success = ((FunctionPanelListener) listeners[i + 1]).deleteNodes(series, sortedNodes);
               if (success) {
                  for (int j = 0; j < sortedNodes.getSize(); j++) {
                     int index = sortedNodes.getitem(j);
                     renderer.unhighlightNode(series, index);
                     seriesCollection.getSeries(series).remove(index);
                     seriesCollection.getSeries(series).fireSeriesChanged();
                     updateSelectedNodesAfterDeletion(series, index);
                  }
                  break;
               }
            }
         }
      }
      //this.repaint();
   }

   public void updateSelectedNodesAfterDeletion(int series, int node) {
      XYSeriesCollection seriesCollection = (XYSeriesCollection)getChart().getXYPlot().getDataset();
      XYSeries dSeries = seriesCollection.getSeries(series);

      // Unhighlight all nodes in the series that are after the deleted one.
      for (int i=node; i<dSeries.getItemCount(); i++) {
         renderer.unhighlightNode(series, i);
      }

      // If the deleted node was selected, remove it from selectedNodes.
      // For all nodes in the series after the deleted one, decrement the node number.
      for (int i=selectedNodes.size()-1; i>=0; i--) {
         if (selectedNodes.get(i).series == series && selectedNodes.get(i).node >= node) {
            if (selectedNodes.get(i).node == node) {
               selectedNodes.remove(i);
            } else {
               selectedNodes.get(i).node--;
            }
         }
      }

      // For each selected node in the series after the deleted one, highlight it.
      for (int i=0; i<selectedNodes.size(); i++) {
         if (selectedNodes.get(i).series == series && selectedNodes.get(i).node >= node) {
            renderer.highlightNode(series, selectedNodes.get(i).node);
         }
      }
   }

   public void updateSelectedNodesAfterAddition(int series, int node) {
      XYSeriesCollection seriesCollection = (XYSeriesCollection)getChart().getXYPlot().getDataset();
      XYSeries dSeries = seriesCollection.getSeries(series);

      // Unhighlight all nodes in the series that are after the added one.
      for (int i=node; i<dSeries.getItemCount(); i++) {
         renderer.unhighlightNode(series, i);
      }

      // For all nodes in the series after the added one, increment the node number.
      for (int i=0; i<selectedNodes.size(); i++) {
         if (selectedNodes.get(i).series == series && selectedNodes.get(i).node >= node) {
            selectedNodes.get(i).node++;
         }
      }

      // For each selected node in the series after the added one, highlight it.
      for (int i=0; i<selectedNodes.size(); i++) {
         if (selectedNodes.get(i).series == series && selectedNodes.get(i).node > node) {
            renderer.highlightNode(series, selectedNodes.get(i).node);
         }
      }
   }

   public void duplicateNode(int series, int node) {
      // TODO: it would be really slick to adjust the list of
      // selected nodes to account for the duplicated one, but you
      // don't know if the duplicateNode will be successful
      // (e.g., some functions may not allow adding nodes).
      // So instead just clear the list of selected nodes.
      clearSelectedNodes();

      // Now notify all listeners about the change.
      Object[] listeners = this.functionPanelListeners.getListenerList();
      for (int i = listeners.length - 2; i >= 0; i -= 2) {
         if (listeners[i] == FunctionPanelListener.class) {
            ((FunctionPanelListener) listeners[i + 1]).duplicateNode(series, node);
         }
      }
   }

   public void dragSelectedNodes(int series, int node, double dragVector[]) {
      // Now notify all listeners about the change.
      Object[] listeners = this.functionPanelListeners.getListenerList();
      for (int i = listeners.length - 2; i >= 0; i -= 2) {
         if (listeners[i] == FunctionPanelListener.class) {
            ((FunctionPanelListener) listeners[i + 1]).dragSelectedNodes(series, node, dragVector);
         }
      }
   }

   private int findNodeInList(int series, int node, ArrayList<FunctionNode> nodeList) {
      if (nodeList == null)
         return -1;
      for (int i = 0; i < nodeList.size(); i++)
         if (nodeList.get(i).series == series && nodeList.get(i).node == node)
            return i;
      return -1;
   }

   private boolean listContainsNode(FunctionNode node, ArrayList<FunctionNode> nodeList) {
      if (nodeList == null)
         return false;
      for (int i = 0; i < nodeList.size(); i++) {
         if (nodeList.get(i).series == node.series && nodeList.get(i).node == node.node)
            return true;
      }
      return false;
   }

   public void addFunctionPanelListener(FunctionPanelListener listener) {
       if (listener == null) {
           throw new IllegalArgumentException("Null 'listener' argument.");
       }
       this.functionPanelListeners.add(FunctionPanelListener.class, listener);
   }

   public void removeFunctionPanelListener(FunctionPanelListener listener) {
       this.functionPanelListeners.remove(FunctionPanelListener.class, listener);
   }

    public JPopupMenu getNodePopUpMenu() {
        return nodePopUpMenu;
    }

    public void setNodePopUpMenu(JPopupMenu nodePopUpMenu) {
        this.nodePopUpMenu = nodePopUpMenu;
    }

    public static JFreeChart createFunctionChart(String title,
                                                 String xAxisLabel,
                                                 String yAxisLabel,
                                                 XYDataset dataset,
                                                 boolean legend,
                                                 boolean tooltips) {

        NumberAxis xAxis = new NumberAxis(xAxisLabel);
        xAxis.setAutoRangeIncludesZero(false);
        NumberAxis yAxis = new NumberAxis(yAxisLabel);
        XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
        FunctionPlot plot = new FunctionPlot(dataset, xAxis, yAxis, renderer);
        plot.setOrientation(PlotOrientation.VERTICAL);
        if (tooltips) {
            renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
        }

        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);

        return chart;
    }

    public int getRightClickX() {
        return rightClickX;
    }

    public int getRightClickY() {
        return rightClickY;
    }
}
