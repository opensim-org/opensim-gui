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
 * ExcitationRenderer
 *
 * Created on October 25, 2007, 10:52 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.view.excitationEditor;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.util.Vector;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer.State;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;
import org.opensim.modeling.ArrayXYPoint;
import org.opensim.modeling.ControlLinear;
import org.opensim.modeling.Units;
import org.opensim.modeling.XYFunctionInterface;
import org.opensim.view.functionEditor.FunctionRenderer;

/**
 *
 * @author Ayman Habib 
 */
public class ExcitationRenderer extends FunctionRenderer
 {

   public enum ExcitationFillMode {NONE, MIN_MAX, MIN_MAX_EXC};

   private ControlLinear control;

   private ExcitationFillMode fillMode;
   private Paint maxFillPaint = null;
   private Paint minFillPaint = null;
   private Paint excFillPaint = null;
   private Paint minMaxFillPaint = null;

   /**
     * Creates a ExcitationRenderer for a single function.
     */
   public ExcitationRenderer(ControlLinear theControl, Vector<XYFunctionInterface> dFunctions) {
       super(dFunctions.get(0));
       control=theControl;
       setXUnits(new Units(Units.UnitType.Seconds));
       setXDisplayUnits(new Units(Units.UnitType.Seconds));
       setYUnits(new Units(Units.UnitType.Seconds));
       setYDisplayUnits(new Units(Units.UnitType.Seconds));
       setBaseShapesVisible(true);
       setBaseShapesFilled(true);
       setBaseSeriesVisibleInLegend(false);
       setDrawOutlines(true);
       setUseFillPaint(true);
       setInteriorTitle(control.getName().replace(".excitation",""));
       for (int i=1; i<dFunctions.size(); i++)
           addFunction(dFunctions.get(i));
   }

   public ControlLinear getControl() {
      return control;
   }

   void setControl(ControlLinear backup) {
      control=backup;       
   }

   void deleteSeriesPoint(int series, int index) {
      functionList.get(series).deletePoint(index);
   }

   void setSeriesPointXY(int series, int index, double newX, double newY) {
      functionList.get(series).setX(index, newX);
      functionList.get(series).setY(index, newY);
   }

   void replaceFunction(int series, XYFunctionInterface aFunction) {
      functionList.set(series, aFunction);
      // Replace the function in listener as well
   }

   public void setFillMode(ExcitationFillMode mode) {
      fillMode = mode;
      notifyListeners(new RendererChangeEvent(this));
   }

   public void setMaxFillPaint(Paint paint) {
      this.maxFillPaint = paint;
   }

   public void setMinFillPaint(Paint paint) {
      this.minFillPaint = paint;
   }

   public void setExcFillPaint(Paint paint) {
      this.excFillPaint = paint;
   }

   public void setMinMaxFillPaint(Paint paint) {
      this.minMaxFillPaint = paint;
   }

   public void drawFunctions(Graphics2D g2,
           XYItemRendererState state,
           Rectangle2D dataArea,
           XYPlot plot,
           ValueAxis domainAxis,
           ValueAxis rangeAxis,
           XYDataset dataset,
           CrosshairState crosshairState) {

      if (fillMode == ExcitationFillMode.MIN_MAX) {
         fillMinMax(g2, state, dataArea, plot, domainAxis, rangeAxis, dataset);
      } else if (fillMode == ExcitationFillMode.MIN_MAX_EXC) {
         fillMinMaxExc(g2, state, dataArea, plot, domainAxis, rangeAxis, dataset);
      }
      super.drawFunctions(g2, state, dataArea, plot, domainAxis, rangeAxis, dataset, crosshairState);
   }

   public void fillMinMax(Graphics2D g2,
           XYItemRendererState state,
           Rectangle2D dataArea,
           XYPlot plot,
           ValueAxis domainAxis,
           ValueAxis rangeAxis,
           XYDataset dataset) {

      int seriesCount = dataset.getSeriesCount();
      if (seriesCount < 3)
         return;

      double screenX=0.0, screenY=0.0;
      float minX1=0.0f, minX2=0.0f, minY1=0.0f, minY2=0.0f;
      float maxX1=0.0f, maxX2=-1.0f, maxY1=0.0f, maxY2=0.0f;
      RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
      RectangleEdge yAxisLocation = plot.getRangeAxisEdge();

      State s = (State) state;
      s.seriesPath.reset();
      s.setLastPointGood(false);

      // Go forwards through series 1
      for (int item = 0; item < dataset.getItemCount(1); item++) {
         ArrayXYPoint xyPts = functionList.get(1).renderAsLineSegments(item);
         if (xyPts != null) {
            for (int i = 0; i < xyPts.getSize(); i++) {
               double dataX = xyPts.get(i).get_x() * XUnits.convertTo(XDisplayUnits);
               double dataY = xyPts.get(i).get_y() * YUnits.convertTo(YDisplayUnits);
               screenX = domainAxis.valueToJava2D(dataX, dataArea, xAxisLocation);
               screenY = rangeAxis.valueToJava2D(dataY, dataArea, yAxisLocation);
               if (!Double.isNaN(screenX) && !Double.isNaN(screenY)) {
                  if (s.isLastPointGood() == false) {
                     s.seriesPath.moveTo((float)screenX, (float)screenY);
                     s.setLastPointGood(true);
                     // store first border point for later call to 'draw'
                     minX1 = (float)screenX;
                     minY1 = (float)screenY;
                  } else {
                     s.seriesPath.lineTo((float)screenX, (float)screenY);
                  }
               }
            }
            functionList.get(1).deleteXYPointArray(xyPts);
         }
      }
      // store second border point for later call to 'draw'
      minX2 = (float)screenX;
      minY2 = (float)screenY;

      // Go backwards through series 2
      for (int item = dataset.getItemCount(2) - 1; item >= 0; item--) {
         ArrayXYPoint xyPts = functionList.get(2).renderAsLineSegments(item);
         if (xyPts != null) {
            for (int i = xyPts.getSize() - 1; i >= 0; i--) {
               double dataX = xyPts.get(i).get_x() * XUnits.convertTo(XDisplayUnits);
               double dataY = xyPts.get(i).get_y() * YUnits.convertTo(YDisplayUnits);
               screenX = domainAxis.valueToJava2D(dataX, dataArea, xAxisLocation);
               screenY = rangeAxis.valueToJava2D(dataY, dataArea, yAxisLocation);
               if (!Double.isNaN(screenX) && !Double.isNaN(screenY)) {
                  if (maxX2 < 0.0) {
                     // store third border point for later call to 'draw'
                     maxX2 = (float)screenX;
                     maxY2 = (float)screenY;
                  }
                  if (s.isLastPointGood() == false) {
                     s.seriesPath.moveTo((float)screenX, (float)screenY);
                     s.setLastPointGood(true);
                  } else {
                     s.seriesPath.lineTo((float)screenX, (float)screenY);
                  }
               }
            }
            functionList.get(2).deleteXYPointArray(xyPts);
         }
      }
      // store fourth border point for later call to 'draw'
      maxX1 = (float)screenX;
      maxY1 = (float)screenY;

      g2.setPaint(minMaxFillPaint);
      g2.fill(s.seriesPath);

      // Draw a line connecting the first point of the min curve to the
      // first point of the max curve, so that the region looks more "filled-in".
      // Also draw a line connecting the last points.
      g2.setStroke(getItemStroke(2, 0));
      s.seriesPath.reset();
      s.setLastPointGood(false);
      s.seriesPath.moveTo(minX1, minY1);
      s.seriesPath.lineTo(maxX1, maxY1);
      g2.draw(s.seriesPath);
      s.seriesPath.reset();
      s.setLastPointGood(false);
      s.seriesPath.moveTo(minX2, minY2);
      s.seriesPath.lineTo(maxX2, maxY2);
      g2.draw(s.seriesPath);
   }

   public void fillMinMaxExc(Graphics2D g2,
           XYItemRendererState state,
           Rectangle2D dataArea,
           XYPlot plot,
           ValueAxis domainAxis,
           ValueAxis rangeAxis,
           XYDataset dataset) {

      if (dataset.getSeriesCount() < 3)
         return;

      if (maxFillPaint != null) {
         fillSeries(2, maxFillPaint, g2, state, dataArea, plot, domainAxis, rangeAxis, dataset);
      }

      if (excFillPaint != null) {
         fillSeries(0, excFillPaint, g2, state, dataArea, plot, domainAxis, rangeAxis, dataset);
      }

      if (minFillPaint != null) {
         fillSeries(1, minFillPaint, g2, state, dataArea, plot, domainAxis, rangeAxis, dataset);
      }
   }

   public void fillSeries(int series,
           Paint paint,
           Graphics2D g2,
           XYItemRendererState state,
           Rectangle2D dataArea,
           XYPlot plot,
           ValueAxis domainAxis,
           ValueAxis rangeAxis,
           XYDataset dataset) {

      int itemCount = dataset.getItemCount(series);
      if (itemCount > 1)
      {
         double screenX=0.0, screenY=0.0;
         float minX1=0.0f, minX2=0.0f, minY1=0.0f, minY2=0.0f;
         float maxX1=-1.0f, maxX2=0.0f, maxY1=0.0f, maxY2=0.0f;

         RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
         RectangleEdge yAxisLocation = plot.getRangeAxisEdge();

         State s = (State) state;
         s.seriesPath.reset();
         s.setLastPointGood(false);

         double dataX = dataset.getX(series, itemCount-1).doubleValue() * XUnits.convertTo(XDisplayUnits);
         double dataY = 0.0;
         screenX = domainAxis.valueToJava2D(dataX, dataArea, xAxisLocation);
         screenY = rangeAxis.valueToJava2D(dataY, dataArea, yAxisLocation);
         // store the first border point for later call to 'draw'
         minX2 = (float)screenX;
         minY2 = (float)screenY;
         s.seriesPath.moveTo((float)screenX, (float)screenY);
         s.setLastPointGood(true);
         dataX = dataset.getX(series, 0).doubleValue() * XUnits.convertTo(XDisplayUnits);
         screenX = domainAxis.valueToJava2D(dataX, dataArea, xAxisLocation);
         // store the second border point for later call to 'draw'
         minX1 = (float)screenX;
         minY1 = (float)screenY;
         s.seriesPath.lineTo((float)screenX, (float)screenY);
         for (int item = 0; item < itemCount-1; item++) {
            ArrayXYPoint xyPts = functionList.get(series).renderAsLineSegments(item);
            if (xyPts != null) {
               for (int i = 0; i < xyPts.getSize(); i++) {
                  dataX = xyPts.get(i).get_x() * XUnits.convertTo(XDisplayUnits);
                  dataY = xyPts.get(i).get_y() * YUnits.convertTo(YDisplayUnits);
                  screenX = domainAxis.valueToJava2D(dataX, dataArea, xAxisLocation);
                  screenY = rangeAxis.valueToJava2D(dataY, dataArea, yAxisLocation);
                  if (!Double.isNaN(screenX) && !Double.isNaN(screenY)) {
                     if (maxX1 < 0.0) {
                        // store the third border point for later call to 'draw'
                        maxX1 = (float)screenX;
                        maxY1 = (float)screenY;
                     }
                     s.seriesPath.lineTo((float)screenX, (float)screenY);
                  }
               }
               functionList.get(series).deleteXYPointArray(xyPts);
            }
         }
         // store the fourth border point for later call to 'draw'
         maxX2 = (float)screenX;
         maxY2 = (float)screenY;

         g2.setPaint(paint);
         g2.fill(s.seriesPath);

         // Draw a line connecting the first point of the min curve to the
         // first point of the max curve, so that the region looks more "filled-in".
         // Also draw a line connecting the last points.
         g2.setStroke(getItemStroke(series, 0));
         s.seriesPath.reset();
         s.setLastPointGood(false);
         s.seriesPath.moveTo(minX1, minY1);
         s.seriesPath.lineTo(maxX1, maxY1);
         g2.draw(s.seriesPath);
         s.seriesPath.reset();
         s.setLastPointGood(false);
         s.seriesPath.moveTo(minX2, minY2);
         s.seriesPath.lineTo(maxX2, maxY2);
         g2.draw(s.seriesPath);
      }
   }
}
