/* -------------------------------------------------------------------------- *
 * OpenSim: FunctionRenderer.java                                             *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib, Peter Loan                                         *
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
 * FunctionRenderer.java
 *
 * Created on October 25, 2007, 10:52 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.view.functionEditor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import org.jfree.chart.ChartColor;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer.State;
import org.jfree.data.xy.XYDataset;
import org.jfree.text.TextUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;
import org.jfree.util.PaintList;
import org.jfree.util.PublicCloneable;
import org.jfree.util.ShapeUtilities;
import org.opensim.modeling.ArrayXYPoint;
import org.opensim.modeling.Units;
import org.opensim.modeling.XYFunctionInterface;

/**
 *
 * @author Peter Loan
 */
public class FunctionRenderer extends XYLineAndShapeRendererWithHighlight
        implements XYItemRenderer,
        Cloneable,
        PublicCloneable,
        Serializable {
   
   protected ArrayList<XYFunctionInterface> functionList = new ArrayList<XYFunctionInterface>(0);
   static Paint[] defaultColors = ChartColor.createDefaultPaintArray();
   static Paint[] reservedColors = new Paint[]{Color.BLACK, Color.BLUE, Color.RED};
   /** 
    * For each control point in each function, the shape fill color,
    * which is either the highlight color (yellow) or the default fill
    * color.
    **/
   private ArrayList<PaintList> shapeFillPaintList = new  ArrayList<PaintList>(0);
   /** For each function, the color of the function lines and shape outlines. */
   private PaintList functionPaintList = new  PaintList();
   /** For each function, the shape fill color for unselected control points. */
   private PaintList functionDefaultFillPaintList = new  PaintList();
   /** For each function, the shape fill color for selected control points. */
   private PaintList functionHighlightFillPaintList = new  PaintList();
   
   protected Units XUnits;         // units of array of X values
   protected Units XDisplayUnits;  // units for displaying X values to user
   protected Units YUnits;         // units of array of Y values
   protected Units YDisplayUnits;  // units for displaying Y values to user
   
   /** the string to display in the lower left corner of the plot area */
   private String interiorTitle = null;
   private Font interiorTitleFont = new Font("SansSerif", Font.BOLD, 12);
   private Paint interiorTitlePaint = Color.RED;
   
   /** Creates a FunctionRenderer for a single function. */
   public FunctionRenderer(XYFunctionInterface theFunction) {
      addFunction(theFunction);
      XUnits = new Units(Units.UnitType.Radians);
      XDisplayUnits = new Units(Units.UnitType.Degrees);
      YUnits = new Units(Units.UnitType.Meters);
      YDisplayUnits = new Units(Units.UnitType.Meters);
   }
   /**
    * Separate function for potentially adding more than one function by the Excitation editor e.g. min/max
    */
   public void addFunction(final XYFunctionInterface theFunction) {
       addFunction(theFunction, true);
   }
   public void addFunction(final XYFunctionInterface theFunction, boolean baseShapeVisible) {
      functionList.add(theFunction);
      int index = functionList.size()-1;
      shapeFillPaintList.add(new PaintList());
      Paint seriesColor = (index >= 3)? defaultColors[index]:reservedColors[index];
      setFunctionPaint(index, seriesColor);
      functionDefaultFillPaintList.setPaint(index, Color.GREEN);
      functionHighlightFillPaintList.setPaint(index, Color.BLACK);
      for (int i=0; i<theFunction.getNumberOfPoints(); i++)
         shapeFillPaintList.get(index).setPaint(i, Color.GREEN);
     Shape circle = new Ellipse2D.Float(-3, -3, 6, 6);
     setSeriesShape(index, circle);// all series
     setSeriesShapesVisible(index, baseShapeVisible);
     setSeriesStroke(index, new BasicStroke(1.5f));
     setSeriesOutlineStroke(index, new BasicStroke(1.0f));
     setFunctionDefaultFillPaint(index, Color.WHITE);
     setFunctionHighlightFillPaint(index, Color.BLUE);
   }

   public void drawFunctions(Graphics2D g2,
           XYItemRendererState state,
           Rectangle2D dataArea,
           XYPlot plot,
           ValueAxis domainAxis,
           ValueAxis rangeAxis,
           XYDataset dataset,
           CrosshairState crosshairState) {

      RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
      RectangleEdge yAxisLocation = plot.getRangeAxisEdge();

      // Draw the lines (series are drawn in reverse order)
      int seriesCount = dataset.getSeriesCount();
      for (int series = seriesCount - 1; series >= 0; series--) {
         int itemCount = dataset.getItemCount(series);
         State s = (State) state;
         s.seriesPath.reset();
         s.setLastPointGood(false);
         for (int item = 0; item < itemCount; item++) {
            ArrayXYPoint xyPts = functionList.get(series).renderAsLineSegments(item);
            if (xyPts != null) {
               for (int i = 0; i < xyPts.getSize(); i++) {
                  double datax = xyPts.get(i).get_x() * XUnits.convertTo(XDisplayUnits);
                  double datay = xyPts.get(i).get_y() * YUnits.convertTo(YDisplayUnits);
                  double screenx = domainAxis.valueToJava2D(datax, dataArea, xAxisLocation);
                  double screeny = rangeAxis.valueToJava2D(datay, dataArea, yAxisLocation);
                  if (!Double.isNaN(screenx) && !Double.isNaN(screeny)) {
                     if (s.isLastPointGood() == false) {
                        s.seriesPath.moveTo((float)screenx, (float)screeny);
                        s.setLastPointGood(true);
                     } else {
                        s.seriesPath.lineTo((float)screenx, (float)screeny);
                     }
                  }
               }
               functionList.get(series).deleteXYPointArray(xyPts);
            } else {
               s.setLastPointGood(false);
            }
         }
         g2.setStroke(getItemStroke(series, 0));
         g2.setPaint(getFunctionPaint(series));
         g2.draw(s.seriesPath);
      }

      // Draw the circles for the control points
      for (int series = seriesCount - 1; series >= 0; series--) {
         if (this.getSeriesShapesVisible(series)) {
            int itemCount = dataset.getItemCount(series);
            for (int item = 0; item < itemCount; item++) {
               double datax = dataset.getXValue(series, item);
               double datay = dataset.getYValue(series, item);
               double screenx = domainAxis.valueToJava2D(datax, dataArea, xAxisLocation);
               double screeny = rangeAxis.valueToJava2D(datay, dataArea, yAxisLocation);
               Shape shape = getItemShape(series, item);
               shape = ShapeUtilities.createTranslatedShape(shape, screenx, screeny);
               if (shape.intersects(dataArea)) {
                  g2.setPaint(getItemFillPaint(series, item));
                  g2.fill(shape);
                  g2.setPaint(getItemPaint(series, item));
                  g2.setStroke(getItemOutlineStroke(series, item));
                  g2.draw(shape);
               }
            }
         }
      }

      // Draw the interior title
      drawInteriorTitle(g2, plot, domainAxis, rangeAxis, dataArea);
   }

   private void drawInteriorTitle(Graphics2D g2, XYPlot plot, ValueAxis domainAxis,
           ValueAxis rangeAxis, Rectangle2D dataArea) {
      RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
      RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
      
      if (this.interiorTitle != null) {
         g2.setFont(this.interiorTitleFont);
         g2.setPaint(this.interiorTitlePaint);
         double textX = domainAxis.valueToJava2D(domainAxis.getUpperBound(), dataArea, xAxisLocation) - 2;
         double textY = rangeAxis.valueToJava2D(rangeAxis.getUpperBound(), dataArea, yAxisLocation) + 2;
         TextUtilities.drawAlignedString(this.interiorTitle, g2, (float)textX, (float)textY, TextAnchor.TOP_RIGHT);
      }
   }

   /**
    * Function colors
    * Each function (series) has its own color, which is used for the
    * function lines and the outlines of the node circles. The insides
    * of the circles are white for all nodes in all series. When a node
    * is selected, its fill color is yellow but its outline is still
    * the series color.
    */
   public void highlightNode(int function, int point) {
      if (function < functionList.size())
         shapeFillPaintList.get(function).setPaint(point, functionHighlightFillPaintList.getPaint(function));
   }

   public void unhighlightNode(int function, int point) {
      if (function < functionList.size())
         shapeFillPaintList.get(function).setPaint(point, functionDefaultFillPaintList.getPaint(function));
   }

   public Paint getNodePaint(int function, int point) {
      if (function < functionList.size())
         return shapeFillPaintList.get(function).getPaint(point);
      else
         return Color.BLACK;
   }

   // The item paint is used for the outlines of the control
   // point shapes.
   public Paint getItemPaint(int series, int item) {
      return functionPaintList.getPaint(series);
   }
   
   public Paint getItemFillPaint(int series, int item) {
      return getNodePaint(series, item);
   }
   
   public void setFunctionDefaultFillPaint(int function, Paint paint) {
      if (function < functionList.size()) {
         Paint oldPaint = functionDefaultFillPaintList.getPaint(function);
         functionDefaultFillPaintList.setPaint(function, paint);
         // Update the individual point's fill colors if they were equal to the old default color
         for (int i=0; i<functionList.get(function).getNumberOfPoints(); i++) {
            if (getNodePaint(function, i) == oldPaint)
               unhighlightNode(function, i);
         }
      }
   }
   
   public Paint getFunctionDefaultFillPaint(int function) {
      if (function < functionList.size())
         return functionDefaultFillPaintList.getPaint(function);
      else
         return Color.BLACK;
   }
   
   public void setFunctionHighlightFillPaint(int function, Paint paint) {
      if (function < functionList.size()) {
         Paint oldPaint = functionHighlightFillPaintList.getPaint(function);
         functionHighlightFillPaintList.setPaint(function, paint);
         // Update the individual point's fill colors if they were equal to the old highlight color
         for (int i=0; i<functionList.get(function).getNumberOfPoints(); i++) {
            if (getNodePaint(function, i) == oldPaint)
               highlightNode(function, i);
         }
      }
   }
   
   public Paint getFunctionHighlightFillPaint(int function) {
      if (function < functionList.size())
         return functionHighlightFillPaintList.getPaint(function);
      else
         return Color.BLACK;
   }
   
   public void setFunctionPaint(int function, Paint paint) {
      functionPaintList.setPaint(function, paint);
   }
   
   public Paint getFunctionPaint(int function) {
      if (function < functionList.size())
         return functionPaintList.getPaint(function);
      else
         return Color.BLACK;
   }
   
   public void setXUnits(Units XUnits) {
      this.XUnits = XUnits;
   }

   public Units getXUnits() {
      return this.XUnits;
   }

   public void setXDisplayUnits(Units XDisplayUnits) {
      this.XDisplayUnits = XDisplayUnits;
   }
   
   public Units getXDisplayUnits() {
      return this.XDisplayUnits;
   }

   public void setYUnits(Units YUnits) {
      this.YUnits = YUnits;
   }
   
   public void setYDisplayUnits(Units YDisplayUnits) {
      this.YDisplayUnits = YDisplayUnits;
   }
   
   public void setInteriorTitle(String title) {
      this.interiorTitle = title;
   }
   
   public String getInteriorTitle() {
      return this.interiorTitle;
   }
   
   public void setInteriorTitlePaint(Paint paint) {
      this.interiorTitlePaint = paint;
   }
   
   public void setInteriorTitleFont(Font font) {
      this.interiorTitleFont = font;
   }
   
}
