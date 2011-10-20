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
 * -----------------------------
 * LegendItemBlockContainer.java
 * -----------------------------
 * (C) Copyright 2006, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: LegendItemBlockContainer.java,v 1.1.2.2 2006/10/06 15:46:34 mungady Exp $
 *
 * Changes
 * -------
 * 20-Jul-2006 : Version 1 (DG);
 * 06-Oct-2006 : Added tooltip and URL text fields (DG);
 * 
 */

package org.jfree.chart.title;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.block.Arrangement;
import org.jfree.chart.block.BlockContainer;
import org.jfree.chart.block.BlockResult;
import org.jfree.chart.block.EntityBlockParams;
import org.jfree.chart.block.EntityBlockResult;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.LegendItemEntity;
import org.jfree.chart.entity.StandardEntityCollection;

/**
 * A container that holds all the pieces of a single legend item.
 *
 * @since 1.0.2
 */
public class LegendItemBlockContainer extends BlockContainer {

    /** The dataset index. */
    private int dataset;
    
    /** The series index. */
    private int series;
    
    /** The tool tip text (can be <code>null</code>). */
    private String toolTipText;
    
    /** The URL text (can be <code>null</code>). */
    private String urlText;
    
    /**
     * Creates a new legend item block.
     * 
     * @param arrangement
     * @param dataset
     * @param series
     */
    public LegendItemBlockContainer(Arrangement arrangement, int dataset,
            int series) {
        super(arrangement);
        this.dataset = dataset;
        this.series = series;
    }
    
    /**
     * Returns the dataset index.
     * 
     * @return The dataset index.
     */
    public int getDatasetIndex() {
        return this.dataset;
    }
   
    /**
     * Returns the series index.
     * 
     * @return The series index.
     */
    public int getSeriesIndex() {
        return this.series;
    }
    
    /**
     * Returns the tool tip text.
     * 
     * @return The tool tip text (possibly <code>null</code>).
     * 
     * @since 1.0.3
     */
    public String getToolTipText() {
        return this.toolTipText;
    }
    
    /**
     * Sets the tool tip text.
     * 
     * @param text  the text (<code>null</code> permitted).
     * 
     * @since 1.0.3
     */
    public void setToolTipText(String text) {
        this.toolTipText = text;   
    }
    
    /**
     * Returns the URL text.
     * 
     * @return The URL text (possibly <code>null</code>).
     * 
     * @since 1.0.3
     */
    public String getURLText() {
        return this.urlText;
    }
    
    /**
     * Sets the URL text.
     * 
     * @param text  the text (<code>null</code> permitted).
     * 
     * @since 1.0.3
     */
    public void setURLText(String text) {
        this.urlText = text;   
    }
    
    /**
     * Draws the block within the specified area.
     * 
     * @param g2  the graphics device.
     * @param area  the area.
     * @param params  passed on to blocks within the container 
     *                (<code>null</code> permitted).
     * 
     * @return An instance of {@link EntityBlockResult}, or <code>null</code>.
     */
    public Object draw(Graphics2D g2, Rectangle2D area, Object params) {
        // draw the block without collecting entities
        super.draw(g2, area, null);
        EntityBlockParams ebp = null;
        BlockResult r = new BlockResult();
        if (params instanceof EntityBlockParams) {
            ebp = (EntityBlockParams) params;
            if (ebp.getGenerateEntities()) {
                EntityCollection ec = new StandardEntityCollection();
                LegendItemEntity entity = new LegendItemEntity(
                        (Shape) area.clone());
                entity.setSeriesIndex(this.series);
                entity.setToolTipText(getToolTipText());
                entity.setURLText(getURLText());
                ec.add(entity);
                r.setEntityCollection(ec);
            }
        }
        return r;
    }
}
