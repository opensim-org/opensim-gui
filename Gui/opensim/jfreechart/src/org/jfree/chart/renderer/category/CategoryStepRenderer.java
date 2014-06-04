/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2006, by Object Refinery Limited and Contributors.
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
 * CategoryStepRenderer.java
 * -------------------------
 *
 * (C) Copyright 2004-2006, by Brian Cole and Contributors.
 *
 * Original Author:  Brian Cole;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: CategoryStepRenderer.java,v 1.5.2.2 2006/12/01 13:57:09 mungady Exp $
 *
 * Changes
 * -------
 * 21-Apr-2004 : Version 1, contributed by Brian Cole (DG);
 * 22-Apr-2004 : Fixed Checkstyle complaints (DG);
 * 05-Nov-2004 : Modified drawItem() signature (DG);
 * 08-Mar-2005 : Added equals() method (DG);
 * ------------- JFREECHART 1.0.x ---------------------------------------------
 * 30-Nov-2006 : Added checks for series visibility (DG);
 * 
 */

package org.jfree.chart.renderer.category;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.util.PublicCloneable;

/**
 * A "step" renderer similar to {@link XYStepRenderer} but
 * that can be used with the {@link CategoryPlot} class.
 */
public class CategoryStepRenderer extends AbstractCategoryItemRenderer
                                  implements Cloneable, PublicCloneable, 
                                             Serializable {

    /** For serialization. */
    private static final long serialVersionUID = -5121079703118261470L;
    
    /** The stagger width. */
    public static final int STAGGER_WIDTH = 5; // could make this configurable
  
    /** 
     * A flag that controls whether or not the steps for multiple series are 
     * staggered. 
     */
    private boolean stagger = false;
    
    /** A working line - need to remove this. */
    private transient Line2D line = new Line2D.Double(0.0, 0.0, 0.0, 0.0);

    /** 
     * Creates a new renderer (stagger defaults to <code>false</code>).
     */
    public CategoryStepRenderer() {
        this(false);
    }
    
    /**
     * Creates a new renderer.
     *  
     * @param stagger  should the horizontal part of the step be staggered by 
     *                 series? 
     */
    public CategoryStepRenderer(boolean stagger) {
        this.stagger = stagger;
    }
  
    /**
     * Returns the flag that controls whether the series steps are staggered.
     * 
     * @return A boolean.
     */
    public boolean getStagger() {
        return this.stagger;
    }
    
    /**
     * Sets the flag that controls whether or not the series steps are 
     * staggered and sends a {@link RendererChangeEvent} to all registered
     * listeners.
     * 
     * @param shouldStagger  a boolean.
     */
    public void setStagger(boolean shouldStagger) {
        this.stagger = shouldStagger;
        notifyListeners(new RendererChangeEvent(this));
    }
   
    /**
     * Draws the line.
     * 
     * @param g2  the graphics device.
     * @param orientation  the plot orientation.
     * @param x0  the x-coordinate for the start of the line.
     * @param y0  the y-coordinate for the start of the line.
     * @param x1  the x-coordinate for the end of the line.
     * @param y1  the y-coordinate for the end of the line.
     */
    protected void drawLine(Graphics2D g2, PlotOrientation orientation,
                            double x0, double y0, double x1, double y1) {
     
        if (orientation == PlotOrientation.VERTICAL) {
            this.line.setLine(x0, y0, x1, y1);
            g2.draw(this.line);
        }
        else if (orientation == PlotOrientation.HORIZONTAL) {
            this.line.setLine(y0, x0, y1, x1); // switch x and y
            g2.draw(this.line);
        }
        // else unknown orientation (complain?)
    }

    /**
     * Draw a single data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the area in which the data is drawn.
     * @param plot  the plot.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
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

        // do nothing if item is not visible
        if (!getItemVisible(row, column)) {
            return;   
        }

        Number value = dataset.getValue(row, column);
        if (value == null) {
            return;
        }
        PlotOrientation orientation = plot.getOrientation();

        // current data point...
        double x1s = domainAxis.getCategoryStart(
            column, getColumnCount(), dataArea, plot.getDomainAxisEdge()
        );
        double x1 = domainAxis.getCategoryMiddle(
            column, getColumnCount(), dataArea, plot.getDomainAxisEdge()
        );
        double x1e = 2 * x1 - x1s; // or: x1s + 2*(x1-x1s)
        double y1 = rangeAxis.valueToJava2D(
            value.doubleValue(), dataArea, plot.getRangeAxisEdge()
        );
        g2.setPaint(getItemPaint(row, column));
        g2.setStroke(getItemStroke(row, column));

        if (column != 0) {
            Number previousValue = dataset.getValue(row, column - 1);
            if (previousValue != null) {
                // previous data point...
                double previous = previousValue.doubleValue();
                double x0s = domainAxis.getCategoryStart(
                    column - 1, getColumnCount(), dataArea, 
                    plot.getDomainAxisEdge()
                );
                double x0 = domainAxis.getCategoryMiddle(
                    column - 1, getColumnCount(), dataArea, 
                    plot.getDomainAxisEdge()
                );
                double x0e = 2 * x0 - x0s; // or: x0s + 2*(x0-x0s)
                double y0 = rangeAxis.valueToJava2D(
                    previous, dataArea, plot.getRangeAxisEdge()
                );
                if (getStagger()) {
                    int xStagger = row * STAGGER_WIDTH;
                    if (xStagger > (x1s - x0e)) {
                        xStagger = (int) (x1s - x0e);
                    }
                    x1s = x0e + xStagger;
                }
                drawLine(g2, orientation, x0e, y0, x1s, y0); 
                    // extend x0's flat bar

                drawLine(g2, orientation, x1s, y0, x1s, y1); // upright bar
           }
       }
       drawLine(g2, orientation, x1s, y1, x1e, y1); // x1's flat bar

       // draw the item labels if there are any...
       if (isItemLabelVisible(row, column)) {
            drawItemLabel(
                g2, orientation, dataset, row, column, x1, y1, 
                (value.doubleValue() < 0.0)
            );
       }
    /* This is how LineAndShapeRenderer.drawItem() handles tips and URLs, but
       I omit it due to time pressure. It shouldn't be hard to put back
 in.

       // collect entity and tool tip information...
       if (state.getInfo() != null) {
           EntityCollection entities =
 state.getInfo().getOwner().getEntityCollection();
           if (entities != null && shape != null) {
               String tip = null;
               CategoryItemLabelGenerator generator =
 getItemLabelGenerator(row, column);
               if (generator != null) {
                   tip = generator.generateToolTip(dataset, row, column);
               }
               String url = null;
               if (getItemURLGenerator(row, column) != null)                    
               url = getItemURLGenerator(row, column).generateURL(dataset, row, 
                 column);
               }
               CategoryItemEntity entity = new CategoryItemEntity(
                   shape, tip, url, dataset, row,
 dataset.getColumnKey(column), column);
               entities.addEntity(entity);
           }
       }
    */

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
        if (!(obj instanceof CategoryStepRenderer)) {
            return false;   
        }
        if (!super.equals(obj)) {
            return false;   
        }
        CategoryStepRenderer that = (CategoryStepRenderer) obj;
        if (this.stagger != that.stagger) {
            return false;   
        }
        return true;
    }

}
