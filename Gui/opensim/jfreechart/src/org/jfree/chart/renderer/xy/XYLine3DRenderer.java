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
 * ---------------------
 * XYLine3DRenderer.java
 * ---------------------
 * (C) Copyright 2005, by Object Refinery Limited.
 *
 * Original Author:  Thomas Morgner;
 * Contributor(s):   David Gilbert (for Object Refinery Limited);
 *
 * $Id: XYLine3DRenderer.java,v 1.4.2.1 2005/10/25 20:56:21 mungady Exp $
 *
 * Changes
 * -------
 * 14-Jan-2005 : Added standard header (DG);
 */

package org.jfree.chart.renderer.xy;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.io.Serializable;

import org.jfree.chart.Effect3D;
import org.jfree.chart.event.RendererChangeEvent;

/**
 * A XYLineAndShapeRenderer that adds a shadow line to the graph
 * to emulate a 3D-effect.
 */
public class XYLine3DRenderer extends XYLineAndShapeRenderer 
                              implements Effect3D, Serializable {

    /** For serialization. */
    private static final long serialVersionUID = 588933208243446087L;
    
    /** The default x-offset for the 3D effect. */
    public static final double DEFAULT_X_OFFSET = 12.0;

    /** The default y-offset for the 3D effect. */
    public static final double DEFAULT_Y_OFFSET = 8.0;

    /** The default wall paint. */
    public static final Paint DEFAULT_WALL_PAINT = new Color(0xDD, 0xDD, 0xDD);

    /** The size of x-offset for the 3D effect. */
    private double xOffset;

    /** The size of y-offset for the 3D effect. */
    private double yOffset;

    /** The paint used to shade the left and lower 3D wall. */
    private transient Paint wallPaint;

    /**
     * Creates a new renderer.
     */
    public XYLine3DRenderer() {
        this.wallPaint = DEFAULT_WALL_PAINT;
        this.xOffset = DEFAULT_X_OFFSET;
        this.yOffset = DEFAULT_Y_OFFSET;
    }

    /**
     * Returns the x-offset for the 3D effect.
     *
     * @return The 3D effect.
     */
    public double getXOffset() {
        return this.xOffset;
    }

    /**
     * Returns the y-offset for the 3D effect.
     *
     * @return The 3D effect.
     */
    public double getYOffset() {
        return this.yOffset;
    }

    /**
     * Sets the x-offset and sends a {@link RendererChangeEvent} to all 
     * registered listeners.
     * 
     * @param xOffset  the x-offset.
     */
    public void setXOffset(double xOffset) {
        this.xOffset = xOffset;
        notifyListeners(new RendererChangeEvent(this));
    }

    /**
     * Sets the y-offset and sends a {@link RendererChangeEvent} to all 
     * registered listeners.
     * 
     * @param yOffset  the y-offset.
     */
    public void setYOffset(double yOffset) {
        this.yOffset = yOffset;
        notifyListeners(new RendererChangeEvent(this));
    }

    /**
     * Returns the paint used to highlight the left and bottom wall in the plot
     * background.
     *
     * @return The paint.
     */
    public Paint getWallPaint() {
        return this.wallPaint;
    }

    /**
     * Sets the paint used to hightlight the left and bottom walls in the plot 
     * background.
     *
     * @param paint  the paint.
     */
    public void setWallPaint(Paint paint) {
        this.wallPaint = paint;
        notifyListeners(new RendererChangeEvent(this));
    }

    /**
     * Returns the number of passes through the data that the renderer requires 
     * in order to draw the chart.  Most charts will require a single pass, 
     * but some require two passes.
     *
     * @return The pass count.
     */
    public int getPassCount() {
        return 3;
    }

    /**
     * Returns <code>true</code> if the specified pass involves drawing lines.
     * 
     * @param pass  the pass.
     * 
     * @return A boolean.
     */
    protected boolean isLinePass(int pass) {
        return pass == 0 || pass == 1;
    }

    /**
     * Returns <code>true</code> if the specified pass involves drawing items.
     * 
     * @param pass  the pass.
     * 
     * @return A boolean.
     */
    protected boolean isItemPass(int pass) {
        return pass == 2;
    }

    /**
     * Returns <code>true</code> if the specified pass involves drawing shadows.
     * 
     * @param pass  the pass.
     * 
     * @return A boolean.
     */
    protected boolean isShadowPass (int pass) {
        return pass == 0;
    }

    /**
     * Overrides the method in the subclass to draw a shadow in the first pass.
     * 
     * @param g2  the graphics device.
     * @param pass  the pass.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param shape  the shape.
     */
    protected void drawFirstPassShape(Graphics2D g2,
                                      int pass,
                                      int series,
                                      int item,
                                      Shape shape) {
        if (isShadowPass(pass)) {
            if (getWallPaint() != null) {
                g2.setStroke(getItemStroke(series, item));
                g2.setPaint(getWallPaint());
                g2.translate(getXOffset(), getYOffset());
                g2.draw(shape);
                g2.translate(-getXOffset(), -getYOffset());
            }
        }
        else {
            // now draw the real shape
            super.drawFirstPassShape(g2, pass, series, item, shape);
        }
    }

}
