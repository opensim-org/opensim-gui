/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2007, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, 
 * USA.  
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * -------------------------
 * XYDifferenceRenderer.java
 * -------------------------
 * (C) Copyright 2003-2007, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Christian W. Zuckschwerdt;
 *
 * $Id: XYDifferenceRenderer.java,v 1.12.2.9 2007/02/06 16:29:11 mungady Exp $
 *
 * Changes:
 * --------
 * 30-Apr-2003 : Version 1 (DG);
 * 30-Jul-2003 : Modified entity constructor (CZ);
 * 20-Aug-2003 : Implemented Cloneable and PublicCloneable (DG);
 * 16-Sep-2003 : Changed ChartRenderingInfo --> PlotRenderingInfo (DG);
 * 09-Feb-2004 : Updated to support horizontal plot orientation (DG);
 * 10-Feb-2004 : Added default constructor, setter methods and updated 
 *               Javadocs (DG);
 * 25-Feb-2004 : Replaced CrosshairInfo with CrosshairState (DG);
 * 30-Mar-2004 : Fixed bug in getNegativePaint() method (DG);
 * 15-Jul-2004 : Switched getX() with getXValue() and getY() with 
 *               getYValue() (DG);
 * 25-Aug-2004 : Fixed a bug preventing the use of crosshairs (DG);
 * 11-Nov-2004 : Now uses ShapeUtilities to translate shapes (DG);
 * 19-Jan-2005 : Now accesses only primitive values from dataset (DG);
 * 22-Feb-2005 : Override getLegendItem(int, int) to return "line" items (DG);
 * 13-Apr-2005 : Fixed shape positioning bug (id = 1182062) (DG);
 * 20-Apr-2005 : Use generators for legend tooltips and URLs (DG);
 * 04-May-2005 : Override equals() method, renamed get/setPlotShapes() -->
 *               get/setShapesVisible (DG);
 * 09-Jun-2005 : Updated equals() to handle GradientPaint (DG);
 * 16-Jun-2005 : Fix bug (1221021) affecting stroke used for each series (DG);
 * ------------- JFREECHART 1.0.x ---------------------------------------------
 * 24-Jan-2007 : Added flag to allow rounding of x-coordinates, and fixed
 *               bug in clone() (DG);
 * 05-Feb-2007 : Added an extra call to updateCrosshairValues() in 
 *               drawItemPass1(), to fix bug 1564967 (DG);
 * 06-Feb-2007 : Fixed bug 1086307, crosshairs with multiple axes (DG);
 *
 */

package org.jfree.chart.renderer.xy;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PaintUtilities;
import org.jfree.util.PublicCloneable;
import org.jfree.util.ShapeUtilities;

/**
 * A renderer for an {@link XYPlot} that highlights the differences between two
 * series.  The renderer expects a dataset that:
 * <ul>
 * <li>has exactly two series;</li>
 * <li>each series has the same x-values;</li>
 * <li>no <code>null</code> values;
 * </ul>
 */
public class XYDifferenceRenderer extends AbstractXYItemRenderer 
                                  implements XYItemRenderer, 
                                             Cloneable,
                                             PublicCloneable,
                                             Serializable {

    /** For serialization. */
    private static final long serialVersionUID = -8447915602375584857L;
    
    /** The paint used to highlight positive differences (y(0) > y(1)). */
    private transient Paint positivePaint;

    /** The paint used to highlight negative differences (y(0) < y(1)). */
    private transient Paint negativePaint;

    /** Display shapes at each point? */
    private boolean shapesVisible;
    
    /** The shape to display in the legend item. */
    private transient Shape legendLine;

    /**
     * This flag controls whether or not the x-coordinates (in Java2D space) 
     * are rounded to integers.  When set to true, this can avoid the vertical
     * striping that anti-aliasing can generate.  However, the rounding may not
     * be appropriate for output in high resolution formats (for example, 
     * vector graphics formats such as SVG and PDF).
     * 
     * @since 1.0.4
     */
    private boolean roundXCoordinates;

    /**
     * Creates a new renderer with default attributes.
     */
    public XYDifferenceRenderer() {
        this(Color.green, Color.red, false);
    }
    
    /**
     * Creates a new renderer.
     *
     * @param positivePaint  the highlight color for positive differences 
     *                       (<code>null</code> not permitted).
     * @param negativePaint  the highlight color for negative differences 
     *                       (<code>null</code> not permitted).
     * @param shapes  draw shapes?
     */
    public XYDifferenceRenderer(Paint positivePaint, Paint negativePaint, 
                                boolean shapes) {
        if (positivePaint == null) {
            throw new IllegalArgumentException(
                    "Null 'positivePaint' argument.");
        }
        if (negativePaint == null) {
            throw new IllegalArgumentException(
                    "Null 'negativePaint' argument.");
        }
        this.positivePaint = positivePaint;
        this.negativePaint = negativePaint;
        this.shapesVisible = shapes;
        this.legendLine = new Line2D.Double(-7.0, 0.0, 7.0, 0.0);
        this.roundXCoordinates = false;
    }

    /**
     * Returns the paint used to highlight positive differences.
     *
     * @return The paint (never <code>null</code>).
     * 
     * @see #setPositivePaint(Paint)
     */
    public Paint getPositivePaint() {
        return this.positivePaint;
    }

    /**
     * Sets the paint used to highlight positive differences.
     * 
     * @param paint  the paint (<code>null</code> not permitted).
     * 
     * @see #getPositivePaint()
     */
    public void setPositivePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.positivePaint = paint;
        notifyListeners(new RendererChangeEvent(this));
    }

    /**
     * Returns the paint used to highlight negative differences.
     *
     * @return The paint (never <code>null</code>).
     * 
     * @see #setNegativePaint(Paint)
     */
    public Paint getNegativePaint() {
        return this.negativePaint;
    }
    
    /**
     * Sets the paint used to highlight negative differences.
     * 
     * @param paint  the paint (<code>null</code> not permitted).
     * 
     * @see #getNegativePaint()
     */
    public void setNegativePaint(Paint paint) {
        if (paint == null) {
            throw new IllegalArgumentException("Null 'paint' argument.");
        }
        this.negativePaint = paint;
        notifyListeners(new RendererChangeEvent(this));
    }

    /**
     * Returns a flag that controls whether or not shapes are drawn for each 
     * data value.
     * 
     * @return A boolean.
     * 
     * @see #setShapesVisible(boolean)
     */
    public boolean getShapesVisible() {
        return this.shapesVisible;
    }

    /**
     * Sets a flag that controls whether or not shapes are drawn for each 
     * data value.
     * 
     * @param flag  the flag.
     * 
     * @see #getShapesVisible()
     */
    public void setShapesVisible(boolean flag) {
        this.shapesVisible = flag;
        notifyListeners(new RendererChangeEvent(this));
    }
    
    /**
     * Returns the shape used to represent a line in the legend.
     * 
     * @return The legend line (never <code>null</code>).
     * 
     * @see #setLegendLine(Shape)
     */
    public Shape getLegendLine() {
        return this.legendLine;   
    }
    
    /**
     * Sets the shape used as a line in each legend item and sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param line  the line (<code>null</code> not permitted).
     * 
     * @see #getLegendLine()
     */
    public void setLegendLine(Shape line) {
        if (line == null) {
            throw new IllegalArgumentException("Null 'line' argument.");   
        }
        this.legendLine = line;
        notifyListeners(new RendererChangeEvent(this));
    }

    /**
     * Returns the flag that controls whether or not the x-coordinates (in
     * Java2D space) are rounded to integer values.
     * 
     * @return The flag.
     * 
     * @since 1.0.4
     * 
     * @see #setRoundXCoordinates(boolean)
     */
    public boolean getRoundXCoordinates() {
        return this.roundXCoordinates;
    }
    
    /**
     * Sets the flag that controls whether or not the x-coordinates (in 
     * Java2D space) are rounded to integer values, and sends a 
     * {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param round  the new flag value.
     * 
     * @since 1.0.4
     * 
     * @see #getRoundXCoordinates()
     */
    public void setRoundXCoordinates(boolean round) {
        this.roundXCoordinates = round;
        notifyListeners(new RendererChangeEvent(this));
    }

    /**
     * Initialises the renderer and returns a state object that should be 
     * passed to subsequent calls to the drawItem() method.  This method will 
     * be called before the first item is rendered, giving the renderer an 
     * opportunity to initialise any state information it wants to maintain.  
     * The renderer can do nothing if it chooses.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area inside the axes.
     * @param plot  the plot.
     * @param data  the data.
     * @param info  an optional info collection object to return data back to 
     *              the caller.
     *
     * @return A state object.
     */
    public XYItemRendererState initialise(Graphics2D g2,
                                          Rectangle2D dataArea,
                                          XYPlot plot,
                                          XYDataset data,
                                          PlotRenderingInfo info) {

        return super.initialise(g2, dataArea, plot, data, info);

    }

    /**
     * Returns <code>2</code>, the number of passes required by the renderer.  
     * The {@link XYPlot} will run through the dataset this number of times.
     * 
     * @return The number of passes required by the renderer.
     */
    public int getPassCount() {
        return 2;
    }
    
    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the area within which the data is being drawn.
     * @param info  collects information about the drawing.
     * @param plot  the plot (can be used to obtain standard color 
     *              information etc).
     * @param domainAxis  the domain (horizontal) axis.
     * @param rangeAxis  the range (vertical) axis.
     * @param dataset  the dataset.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairState  crosshair information for the plot 
     *                        (<code>null</code> permitted).
     * @param pass  the pass index.
     */
    public void drawItem(Graphics2D g2,
                         XYItemRendererState state,
                         Rectangle2D dataArea,
                         PlotRenderingInfo info,
                         XYPlot plot,
                         ValueAxis domainAxis,
                         ValueAxis rangeAxis,
                         XYDataset dataset,
                         int series,
                         int item,
                         CrosshairState crosshairState,
                         int pass) {

        if (pass == 0) {
            drawItemPass0(g2, dataArea, info, plot, domainAxis, rangeAxis, 
                    dataset, series, item, crosshairState);
        }
        else if (pass == 1) {
            drawItemPass1(g2, dataArea, info, plot, domainAxis, rangeAxis, 
                    dataset, series, item, crosshairState);
        }

    }

    /**
     * Draws the visual representation of a single data item, first pass.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area within which the data is being drawn.
     * @param info  collects information about the drawing.
     * @param plot  the plot (can be used to obtain standard color 
     *              information etc).
     * @param domainAxis  the domain (horizontal) axis.
     * @param rangeAxis  the range (vertical) axis.
     * @param dataset  the dataset.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairState  crosshair information for the plot 
     *                        (<code>null</code> permitted).
     */
    protected void drawItemPass0(Graphics2D g2,
                                 Rectangle2D dataArea,
                                 PlotRenderingInfo info,
                                 XYPlot plot,
                                 ValueAxis domainAxis,
                                 ValueAxis rangeAxis,
                                 XYDataset dataset,
                                 int series,
                                 int item,
                                 CrosshairState crosshairState) {

        if (series == 0) {

            PlotOrientation orientation = plot.getOrientation();
            RectangleEdge domainAxisLocation = plot.getDomainAxisEdge();
            RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();
            
            double y0 = dataset.getYValue(0, item);
            double x1 = dataset.getXValue(1, item);
            double y1 = dataset.getYValue(1, item);

            double transY0 = rangeAxis.valueToJava2D(y0, dataArea, 
                    rangeAxisLocation);
            double transX1 = domainAxis.valueToJava2D(x1, dataArea, 
                    domainAxisLocation);
            if (this.roundXCoordinates) {
                transX1 = Math.rint(transX1);
            }
            double transY1 = rangeAxis.valueToJava2D(y1, dataArea, 
                    rangeAxisLocation);

            if (item > 0) {
                double prevx0 = dataset.getXValue(0, item - 1);
                double prevy0 = dataset.getYValue(0, item - 1);
                double prevy1 = dataset.getYValue(1, item - 1);

                double prevtransX0 = domainAxis.valueToJava2D(prevx0, dataArea, 
                        domainAxisLocation);
                if (this.roundXCoordinates) {
                    prevtransX0 = Math.rint(prevtransX0);
                }
                double prevtransY0 = rangeAxis.valueToJava2D(prevy0, dataArea, 
                        rangeAxisLocation);
                double prevtransY1 = rangeAxis.valueToJava2D(prevy1, dataArea, 
                        rangeAxisLocation);

                Shape positive = getPositiveArea((float) prevtransX0, 
                        (float) prevtransY0, (float) prevtransY1,
                        (float) transX1, (float) transY0, (float) transY1,
                        orientation);
                if (positive != null) {
                    g2.setPaint(getPositivePaint());
                    g2.fill(positive);
                }

                Shape negative = getNegativeArea((float) prevtransX0, 
                        (float) prevtransY0, (float) prevtransY1,
                        (float) transX1, (float) transY0, (float) transY1,
                        orientation);

                if (negative != null) {
                    g2.setPaint(getNegativePaint());
                    g2.fill(negative);
                }
            }
        }

    }

    /**
     * Draws the visual representation of a single data item, second pass.  In 
     * the second pass, the renderer draws the lines and shapes for the 
     * individual points in the two series.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area within which the data is being drawn.
     * @param info  collects information about the drawing.
     * @param plot  the plot (can be used to obtain standard color information 
     *              etc).
     * @param domainAxis  the domain (horizontal) axis.
     * @param rangeAxis  the range (vertical) axis.
     * @param dataset  the dataset.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairState  crosshair information for the plot 
     *                        (<code>null</code> permitted).
     */
    protected void drawItemPass1(Graphics2D g2,
                                 Rectangle2D dataArea,
                                 PlotRenderingInfo info,
                                 XYPlot plot,
                                 ValueAxis domainAxis,
                                 ValueAxis rangeAxis,
                                 XYDataset dataset,
                                 int series,
                                 int item,
                                 CrosshairState crosshairState) {

        Shape entityArea = null;
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getOwner().getEntityCollection();
        }

        Paint seriesPaint = getItemPaint(series, item);
        Stroke seriesStroke = getItemStroke(series, item);
        g2.setPaint(seriesPaint);
        g2.setStroke(seriesStroke);

        if (series == 0) {

            PlotOrientation orientation = plot.getOrientation(); 
            RectangleEdge domainAxisLocation = plot.getDomainAxisEdge();
            RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();

            double x0 = dataset.getXValue(0, item);
            double y0 = dataset.getYValue(0, item);
            double x1 = dataset.getXValue(1, item);
            double y1 = dataset.getYValue(1, item);

            double transX0 = domainAxis.valueToJava2D(x0, dataArea, 
                    domainAxisLocation);
            double transY0 = rangeAxis.valueToJava2D(y0, dataArea, 
                    rangeAxisLocation);
            double transX1 = domainAxis.valueToJava2D(x1, dataArea, 
                    domainAxisLocation);
            double transY1 = rangeAxis.valueToJava2D(y1, dataArea, 
                    rangeAxisLocation);

            if (item > 0) {
                // get the previous data points...
                double prevx0 = dataset.getXValue(0, item - 1);
                double prevy0 = dataset.getYValue(0, item - 1);
                double prevx1 = dataset.getXValue(1, item - 1);
                double prevy1 = dataset.getYValue(1, item - 1);

                double prevtransX0 = domainAxis.valueToJava2D(prevx0, dataArea,
                        domainAxisLocation);
                double prevtransY0 = rangeAxis.valueToJava2D(prevy0, dataArea, 
                        rangeAxisLocation);
                double prevtransX1 = domainAxis.valueToJava2D(prevx1, dataArea, 
                        domainAxisLocation);
                double prevtransY1 = rangeAxis.valueToJava2D(prevy1, dataArea, 
                        rangeAxisLocation);

                Line2D line0 = null;
                Line2D line1 = null;
                if (orientation == PlotOrientation.HORIZONTAL) {
                    line0 = new Line2D.Double(transY0, transX0, prevtransY0, 
                            prevtransX0);
                    line1 = new Line2D.Double(transY1, transX1, prevtransY1, 
                            prevtransX1);
                }
                else if (orientation == PlotOrientation.VERTICAL) {
                    line0 = new Line2D.Double(transX0, transY0, prevtransX0, 
                            prevtransY0);
                    line1 = new Line2D.Double(transX1, transY1, prevtransX1, 
                            prevtransY1);
                }
                if (line0 != null && line0.intersects(dataArea)) {
                    g2.setPaint(getItemPaint(series, item));
                    g2.setStroke(getItemStroke(series, item));
                    g2.draw(line0);
                }
                if (line1 != null && line1.intersects(dataArea)) {
                    g2.setPaint(getItemPaint(1, item));
                    g2.setStroke(getItemStroke(1, item));
                    g2.draw(line1);
                }
            }

            if (getShapesVisible()) {
                Shape shape0 = getItemShape(series, item);
                if (orientation == PlotOrientation.HORIZONTAL) {
                    shape0 = ShapeUtilities.createTranslatedShape(shape0, 
                            transY0, transX0);
                }
                else {  // vertical
                    shape0 = ShapeUtilities.createTranslatedShape(shape0, 
                            transX0, transY0);
                }
                if (shape0.intersects(dataArea)) {
                    g2.setPaint(getItemPaint(series, item));
                    g2.fill(shape0);
                }
                entityArea = shape0;

                // add an entity for the item...
                if (entities != null) {
                    if (entityArea == null) {
                        entityArea = new Rectangle2D.Double(transX0 - 2, 
                                transY0 - 2, 4, 4);
                    }
                    String tip = null;
                    XYToolTipGenerator generator = getToolTipGenerator(series, 
                            item);
                    if (generator != null) {
                        tip = generator.generateToolTip(dataset, series, item);
                    }
                    String url = null;
                    if (getURLGenerator() != null) {
                        url = getURLGenerator().generateURL(dataset, series, 
                                item);
                    }
                    XYItemEntity entity = new XYItemEntity(entityArea, dataset, 
                            series, item, tip, url);
                    entities.add(entity);
                }

                Shape shape1 = getItemShape(series + 1, item);
                if (orientation == PlotOrientation.HORIZONTAL) {
                    shape1 = ShapeUtilities.createTranslatedShape(shape1, 
                            transY1, transX1);
                }
                else {  // vertical
                    shape1 = ShapeUtilities.createTranslatedShape(shape1, 
                            transX1, transY1);
                }
                if (shape1.intersects(dataArea)) {
                    g2.setPaint(getItemPaint(series + 1, item));
                    g2.fill(shape1);
                }
                entityArea = shape1;

                // add an entity for the item...
                if (entities != null) {
                    if (entityArea == null) {
                        entityArea = new Rectangle2D.Double(transX1 - 2, 
                                transY1 - 2, 4, 4);
                    }
                    String tip = null;
                    XYToolTipGenerator generator = getToolTipGenerator(series, 
                            item);
                    if (generator != null) {
                        tip = generator.generateToolTip(dataset, series + 1, 
                                item);
                    }
                    String url = null;
                    if (getURLGenerator() != null) {
                        url = getURLGenerator().generateURL(dataset, 
                                series + 1, item);
                    }
                    XYItemEntity entity = new XYItemEntity(entityArea, dataset, 
                            series + 1, item, tip, url);
                    entities.add(entity);
                }
            }
            
            int domainAxisIndex = plot.getDomainAxisIndex(domainAxis);
            int rangeAxisIndex = plot.getRangeAxisIndex(rangeAxis);
            updateCrosshairValues(crosshairState, x1, y1, domainAxisIndex, 
                    rangeAxisIndex, transX1, transY1, orientation);
            updateCrosshairValues(crosshairState, x0, y0, domainAxisIndex, 
                    rangeAxisIndex, transX0, transY0, orientation);
        }

    }

    /**
     * Returns the positive area for a crossover point.
     * 
     * @param x0  x coordinate.
     * @param y0A  y coordinate A.
     * @param y0B  y coordinate B.
     * @param x1  x coordinate.
     * @param y1A  y coordinate A.
     * @param y1B  y coordinate B.
     * @param orientation  the plot orientation.
     * 
     * @return The positive area.
     */
    protected Shape getPositiveArea(float x0, float y0A, float y0B, 
                                    float x1, float y1A, float y1B,
                                    PlotOrientation orientation) {

        Shape result = null;

        boolean startsNegative = (y0A >= y0B);  
        boolean endsNegative = (y1A >= y1B);
        if (orientation == PlotOrientation.HORIZONTAL) {
            startsNegative = (y0B >= y0A);
            endsNegative = (y1B >= y1A);
        }
        
        if (startsNegative) {  // starts negative
            if (endsNegative) {
                // all negative - return null
                result = null;
            }
            else {
                // changed from negative to positive
                float[] p = getIntersection(x0, y0A, x1, y1A, x0, y0B, x1, y1B);
                GeneralPath area = new GeneralPath();
                if (orientation == PlotOrientation.HORIZONTAL) {
                    area.moveTo(y1A, x1);
                    area.lineTo(p[1], p[0]);
                    area.lineTo(y1B, x1);
                    area.closePath();
                }
                else if (orientation == PlotOrientation.VERTICAL) {
                    area.moveTo(x1, y1A);
                    area.lineTo(p[0], p[1]);
                    area.lineTo(x1, y1B);
                    area.closePath();
                }
                result = area;
            }
        }
        else {  // starts positive
            if (endsNegative) {
                // changed from positive to negative
                float[] p = getIntersection(x0, y0A, x1, y1A, x0, y0B, x1, y1B);
                GeneralPath area = new GeneralPath();
                if (orientation == PlotOrientation.HORIZONTAL) {
                    area.moveTo(y0A, x0);
                    area.lineTo(p[1], p[0]);
                    area.lineTo(y0B, x0);
                    area.closePath();
                }
                else if (orientation == PlotOrientation.VERTICAL) {
                    area.moveTo(x0, y0A);
                    area.lineTo(p[0], p[1]);
                    area.lineTo(x0, y0B);
                    area.closePath();
                }
                result = area;

            }
            else {
                GeneralPath area = new GeneralPath();
                if (orientation == PlotOrientation.HORIZONTAL) {
                    area.moveTo(y0A, x0);
                    area.lineTo(y1A, x1);
                    area.lineTo(y1B, x1);
                    area.lineTo(y0B, x0);
                    area.closePath();
                }
                else if (orientation == PlotOrientation.VERTICAL) {
                    area.moveTo(x0, y0A);
                    area.lineTo(x1, y1A);
                    area.lineTo(x1, y1B);
                    area.lineTo(x0, y0B);
                    area.closePath();
                }
                result = area;
            }

        }

        return result;

    }

    /**
     * Returns the negative area for a cross-over section.
     * 
     * @param x0  x coordinate.
     * @param y0A  y coordinate A.
     * @param y0B  y coordinate B.
     * @param x1  x coordinate.
     * @param y1A  y coordinate A.
     * @param y1B  y coordinate B.
     * @param orientation  the plot orientation.
     * 
     * @return The negative area.
     */
    protected Shape getNegativeArea(float x0, float y0A, float y0B, 
                                    float x1, float y1A, float y1B,
                                    PlotOrientation orientation) {

        Shape result = null;

        boolean startsNegative = (y0A >= y0B);
        boolean endsNegative = (y1A >= y1B);
        if (orientation == PlotOrientation.HORIZONTAL) {
            startsNegative = (y0B >= y0A);
            endsNegative = (y1B >= y1A);
        }
        if (startsNegative) {  // starts negative
            if (endsNegative) {  // all negative
                GeneralPath area = new GeneralPath();
                if (orientation == PlotOrientation.HORIZONTAL) {
                    area.moveTo(y0A, x0);
                    area.lineTo(y1A, x1);
                    area.lineTo(y1B, x1);
                    area.lineTo(y0B, x0);
                    area.closePath();
                }
                else if (orientation == PlotOrientation.VERTICAL) {
                    area.moveTo(x0, y0A);
                    area.lineTo(x1, y1A);
                    area.lineTo(x1, y1B);
                    area.lineTo(x0, y0B);
                    area.closePath();
                }
                result = area;
            }
            else {  // changed from negative to positive
                float[] p = getIntersection(x0, y0A, x1, y1A, x0, y0B, x1, y1B);
                GeneralPath area = new GeneralPath();
                if (orientation == PlotOrientation.HORIZONTAL) {
                    area.moveTo(y0A, x0);
                    area.lineTo(p[1], p[0]);
                    area.lineTo(y0B, x0);
                    area.closePath();
                }
                else if (orientation == PlotOrientation.VERTICAL) {
                    area.moveTo(x0, y0A);
                    area.lineTo(p[0], p[1]);
                    area.lineTo(x0, y0B);
                    area.closePath();
                }
                result = area;
            }
        }
        else {
            if (endsNegative) {
                // changed from positive to negative
                float[] p = getIntersection(x0, y0A, x1, y1A, x0, y0B, x1, y1B);
                GeneralPath area = new GeneralPath();
                if (orientation == PlotOrientation.HORIZONTAL) {
                    area.moveTo(y1A, x1);
                    area.lineTo(p[1], p[0]);
                    area.lineTo(y1B, x1);
                    area.closePath();
                }
                else if (orientation == PlotOrientation.VERTICAL) {
                    area.moveTo(x1, y1A);
                    area.lineTo(p[0], p[1]);
                    area.lineTo(x1, y1B);
                    area.closePath();
                }
                result = area;
            }
            else {
                // all negative - return null
            }

        }

        return result;

    }

    /**
     * Returns the intersection point of two lines.
     * 
     * @param x1  x1
     * @param y1  y1
     * @param x2  x2
     * @param y2  y2
     * @param x3  x3
     * @param y3  y3
     * @param x4  x4
     * @param y4  y4
     * 
     * @return The intersection point.
     */
    private float[] getIntersection(float x1, float y1, float x2, float y2,
                                    float x3, float y3, float x4, float y4) {

        float n = (x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3);
        float d = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
        float u = n / d;

        float[] result = new float[2];
        result[0] = x1 + u * (x2 - x1);
        result[1] = y1 + u * (y2 - y1);
        return result;

    }
    
    /**
     * Returns a default legend item for the specified series.  Subclasses 
     * should override this method to generate customised items.
     *
     * @param datasetIndex  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     *
     * @return A legend item for the series.
     */
    public LegendItem getLegendItem(int datasetIndex, int series) {
        LegendItem result = null;
        XYPlot p = getPlot();
        if (p != null) {
            XYDataset dataset = p.getDataset(datasetIndex);
            if (dataset != null) {
                if (getItemVisible(series, 0)) {
                    String label = getLegendItemLabelGenerator().generateLabel(
                            dataset, series);
                    String description = label;
                    String toolTipText = null;
                    if (getLegendItemToolTipGenerator() != null) {
                        toolTipText 
                            = getLegendItemToolTipGenerator().generateLabel(
                                    dataset, series);
                    }
                    String urlText = null;
                    if (getLegendItemURLGenerator() != null) {
                        urlText = getLegendItemURLGenerator().generateLabel(
                                dataset, series);
                    }
                    Paint paint = getSeriesPaint(series);
                    Stroke stroke = getSeriesStroke(series);
                    // TODO:  the following hard-coded line needs generalising
                    Line2D line = new Line2D.Double(-7.0, 0.0, 7.0, 0.0);
                    result = new LegendItem(label, description, 
                            toolTipText, urlText, line, stroke, paint);
                }
            }

        }

        return result;

    }

    /**
     * Tests this renderer for equality with an arbitrary object.
     * 
     * @param obj  the object (<code>null</code> permitted).
     * 
     * @return A boolean.
     */    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;   
        }
        if (!(obj instanceof XYDifferenceRenderer)) {
            return false;   
        }
        if (!super.equals(obj)) {
            return false;   
        }
        XYDifferenceRenderer that = (XYDifferenceRenderer) obj;
        if (!PaintUtilities.equal(this.positivePaint, that.positivePaint)) {
            return false;   
        }
        if (!PaintUtilities.equal(this.negativePaint, that.negativePaint)) {
            return false;   
        }
        if (this.shapesVisible != that.shapesVisible) {
            return false;   
        }
        if (!ShapeUtilities.equal(this.legendLine, that.legendLine)) {
            return false;   
        }
        if (this.roundXCoordinates != that.roundXCoordinates) {
            return false;
        }
        return true;
    }
    
    /**
     * Returns a clone of the renderer.
     * 
     * @return A clone.
     * 
     * @throws CloneNotSupportedException  if the renderer cannot be cloned.
     */
    public Object clone() throws CloneNotSupportedException {
        XYDifferenceRenderer clone = (XYDifferenceRenderer) super.clone();
        clone.legendLine = ShapeUtilities.clone(this.legendLine);
        return clone;
    }

    /**
     * Provides serialization support.
     *
     * @param stream  the output stream.
     *
     * @throws IOException  if there is an I/O error.
     */
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writePaint(this.positivePaint, stream);
        SerialUtilities.writePaint(this.negativePaint, stream);
        SerialUtilities.writeShape(this.legendLine, stream);
    }

    /**
     * Provides serialization support.
     *
     * @param stream  the input stream.
     *
     * @throws IOException  if there is an I/O error.
     * @throws ClassNotFoundException  if there is a classpath problem.
     */
    private void readObject(ObjectInputStream stream) 
        throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.positivePaint = SerialUtilities.readPaint(stream);
        this.negativePaint = SerialUtilities.readPaint(stream);
        this.legendLine = SerialUtilities.readShape(stream);
    }

}
