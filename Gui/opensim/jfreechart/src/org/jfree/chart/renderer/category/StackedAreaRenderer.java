/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2005, by Object Refinery Limited and Contributors.
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
 * ------------------------
 * StackedAreaRenderer.java
 * ------------------------
 * (C) Copyright 2002-2006, by Dan Rivett (d.rivett@ukonline.co.uk) and 
 *                          Contributors.
 *
 * Original Author:  Dan Rivett (adapted from AreaCategoryItemRenderer);
 * Contributor(s):   Jon Iles;
 *                   David Gilbert (for Object Refinery Limited);
 *                   Christian W. Zuckschwerdt;
 *
 * $Id: StackedAreaRenderer.java,v 1.6.2.3 2006/10/11 16:25:50 mungady Exp $
 *
 * Changes:
 * --------
 * 20-Sep-2002 : Version 1, contributed by Dan Rivett;
 * 24-Oct-2002 : Amendments for changes in CategoryDataset interface and 
 *               CategoryToolTipGenerator interface (DG);
 * 01-Nov-2002 : Added tooltips (DG);
 * 06-Nov-2002 : Renamed drawCategoryItem() --> drawItem() and now using axis 
 *               for category spacing. Renamed StackedAreaCategoryItemRenderer 
 *               --> StackedAreaRenderer (DG);
 * 26-Nov-2002 : Switched CategoryDataset --> TableDataset (DG);
 * 26-Nov-2002 : Replaced isStacked() method with getRangeType() method (DG);
 * 17-Jan-2003 : Moved plot classes to a separate package (DG);
 * 25-Mar-2003 : Implemented Serializable (DG);
 * 13-May-2003 : Modified to take into account the plot orientation (DG);
 * 30-Jul-2003 : Modified entity constructor (CZ);
 * 07-Oct-2003 : Added renderer state (DG);
 * 29-Apr-2004 : Added getRangeExtent() override (DG);
 * 05-Nov-2004 : Modified drawItem() signature (DG);
 * 07-Jan-2005 : Renamed getRangeExtent() --> findRangeBounds() (DG);
 * ------------- JFREECHART 1.0.x ---------------------------------------------
 * 11-Oct-2006 : Added support for rendering data values as percentages,
 *               and added a second pass for drawing item labels (DG);
 * 
 */

package org.jfree.chart.renderer.category;

import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.DataUtilities;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PublicCloneable;

/**
 * A renderer that draws stacked area charts for a 
 * {@link org.jfree.chart.plot.CategoryPlot}.
 */
public class StackedAreaRenderer extends AreaRenderer 
                                 implements Cloneable, PublicCloneable, 
                                            Serializable {

    /** For serialization. */
    private static final long serialVersionUID = -3595635038460823663L;
     
    /** A flag that controls whether the areas display values or percentages. */
    private boolean renderAsPercentages;
    
    /**
     * Creates a new renderer.
     */
    public StackedAreaRenderer() {
        this(false);
    }
    
    /**
     * Creates a new renderer.
     * 
     * @param renderAsPercentages  a flag that controls whether the data values
     *                             are rendered as percentages.
     */
    public StackedAreaRenderer(boolean renderAsPercentages) {
        super();
        this.renderAsPercentages = renderAsPercentages;
    }

    /**
     * Returns <code>true</code> if the renderer displays each item value as
     * a percentage (so that the stacked areas add to 100%), and 
     * <code>false</code> otherwise.
     * 
     * @return A boolean.
     *
     * @since 1.0.3
     */
    public boolean getRenderAsPercentages() {
        return this.renderAsPercentages;   
    }
    
    /**
     * Sets the flag that controls whether the renderer displays each item
     * value as a percentage (so that the stacked areas add to 100%), and sends
     * a {@link RendererChangeEvent} to all registered listeners.
     * 
     * @param asPercentages  the flag.
     *
     * @since 1.0.3
     */
    public void setRenderAsPercentages(boolean asPercentages) {
        this.renderAsPercentages = asPercentages; 
        notifyListeners(new RendererChangeEvent(this));
    }
    
    /**
     * Returns the number of passes (<code>2</code>) required by this renderer. 
     * The first pass is used to draw the bars, the second pass is used to
     * draw the item labels (if visible).
     * 
     * @return The number of passes required by the renderer.
     */
    public int getPassCount() {
        return 2;
    }

    /**
     * Returns the range of values the renderer requires to display all the 
     * items from the specified dataset.
     * 
     * @param dataset  the dataset (<code>null</code> not permitted).
     * 
     * @return The range (or <code>null</code> if the dataset is empty).
     */
    public Range findRangeBounds(CategoryDataset dataset) {
        if (this.renderAsPercentages) {
            return new Range(0.0, 1.0);   
        }
        else {
            return DatasetUtilities.findStackedRangeBounds(dataset);
        }
    }

    /**
     * Draw a single data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the data plot area.
     * @param plot  the plot.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the data.
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     * @param pass  the pass index.
     */
    public void drawItem(Graphics2D g2,
                         CategoryItemRendererState state,
                         Rectangle2D dataArea,
                         CategoryPlot plot,
                         CategoryAxis domainAxis,
                         ValueAxis rangeAxis,
                         CategoryDataset dataset,
                         int row,
                         int column,
                         int pass) {

        // plot non-null values...
        Number dataValue = dataset.getValue(row, column);
        if (dataValue == null) {
            return;
        }

        double value = dataValue.doubleValue();
        double total = 0.0;  // only needed if calculating percentages
        if (this.renderAsPercentages) {
            total = DataUtilities.calculateColumnTotal(dataset, column);
            value = value / total;
        }

        // leave the y values (y1, y0) untranslated as it is going to be be 
        // stacked up later by previous series values, after this it will be 
        // translated.
        double xx1 = domainAxis.getCategoryMiddle(column, getColumnCount(), 
                dataArea, plot.getDomainAxisEdge());
        
        double previousHeightx1 = getPreviousHeight(dataset, row, column);
        double y1 = value + previousHeightx1;
        RectangleEdge location = plot.getRangeAxisEdge();
        double yy1 = rangeAxis.valueToJava2D(y1, dataArea, location);

        g2.setPaint(getItemPaint(row, column));
        g2.setStroke(getItemStroke(row, column));

        // in column zero, the only job to do is draw any visible item labels
        // and this is done in the second pass...
        if (column == 0) {
            if (pass == 1) {
                // draw item labels, if visible
                if (isItemLabelVisible(row, column)) {
                    drawItemLabel(g2, plot.getOrientation(), dataset, row, column, 
                            xx1, yy1, (y1 < 0.0));
                }    
            }
        }
        else {
            Number previousValue = dataset.getValue(row, column - 1);
            if (previousValue != null) {

                double xx0 = domainAxis.getCategoryMiddle(column - 1, 
                        getColumnCount(), dataArea, plot.getDomainAxisEdge());
                double y0 = previousValue.doubleValue();
                if (this.renderAsPercentages) {
                    total = DataUtilities.calculateColumnTotal(dataset, 
                            column - 1);
                    y0 = y0 / total;
                }
               

                // Get the previous height, but this will be different for both
                // y0 and y1 as the previous series values could differ.
                double previousHeightx0 = getPreviousHeight(dataset, row, 
                        column - 1);

                // Now stack the current y values on top of the previous values.
                y0 += previousHeightx0;

                // Now translate the previous heights
                double previousHeightxx0 = rangeAxis.valueToJava2D(
                        previousHeightx0, dataArea, location);
                double previousHeightxx1 = rangeAxis.valueToJava2D(
                        previousHeightx1, dataArea, location);

                // Now translate the current y values.
                double yy0 = rangeAxis.valueToJava2D(y0, dataArea, location);

                if (pass == 0) {
                    Polygon p = null;
                    PlotOrientation orientation = plot.getOrientation();
                    if (orientation == PlotOrientation.HORIZONTAL) {
                        p = new Polygon();
                        p.addPoint((int) yy0, (int) xx0);
                        p.addPoint((int) yy1, (int) xx1);
                        p.addPoint((int) previousHeightxx1, (int) xx1);
                        p.addPoint((int) previousHeightxx0, (int) xx0);
                    }
                    else if (orientation == PlotOrientation.VERTICAL) {
                        p = new Polygon();
                        p.addPoint((int) xx0, (int) yy0);
                        p.addPoint((int) xx1, (int) yy1);
                        p.addPoint((int) xx1, (int) previousHeightxx1);
                        p.addPoint((int) xx0, (int) previousHeightxx0);
                    }
                    g2.setPaint(getItemPaint(row, column));
                    g2.setStroke(getItemStroke(row, column));
                    g2.fill(p);
                }
                else {
                    if (isItemLabelVisible(row, column)) {
                        drawItemLabel(g2, plot.getOrientation(), dataset, row, 
                                column, xx1, yy1, (y1 < 0.0));
                    }  
                }
            }
            

        }
        

        // add an item entity, if this information is being collected
        EntityCollection entities = state.getEntityCollection();
        if (entities != null) {
            Shape shape = new Rectangle2D.Double(xx1 - 3.0, yy1 - 3.0, 6.0, 6.0);
            addItemEntity(entities, dataset, row, column, shape);
        }

    }

    /**
     * Calculates the stacked value of the all series up to, but not including 
     * <code>series</code> for the specified category, <code>category</code>.  
     * It returns 0.0 if <code>series</code> is the first series, i.e. 0.
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     * @param series  the series.
     * @param category  the category.
     *
     * @return double returns a cumulative value for all series' values up to 
     *         but excluding <code>series</code> for Object 
     *         <code>category</code>.
     */
    protected double getPreviousHeight(CategoryDataset dataset, 
                                       int series, int category) {

        double result = 0.0;
        Number n;
        double total = 0.0;
        if (this.renderAsPercentages) {
            total = DataUtilities.calculateColumnTotal(dataset, category);
        }
        for (int i = 0; i < series; i++) {
            n = dataset.getValue(i, category);
            if (n != null) {
                double v = n.doubleValue();
                if (this.renderAsPercentages) {
                    v = v / total;
                }
                result += v;
            }
        }
        return result;

    }

    /**
     * Checks this instance for equality with an arbitrary object.
     *
     * @param obj  the object (<code>null</code> not permitted).
     *
     * @return A boolean.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (! (obj instanceof StackedAreaRenderer)) {
            return false;
        }
        StackedAreaRenderer that = (StackedAreaRenderer) obj;
        if (this.renderAsPercentages != that.renderAsPercentages) {
            return false;
        }
        return super.equals(obj);
    }
}
