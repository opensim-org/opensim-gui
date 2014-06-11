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
 * ---------------
 * ColorBlock.java
 * ---------------
 * (C) Copyright 2004, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: ColorBlock.java,v 1.4.2.1 2005/10/25 20:39:38 mungady Exp $
 *
 * Changes:
 * --------
 * 22-Oct-2004 : Version 1 (DG);
 * 20-Apr-2005 : Added new draw() method (DG);
 * 
 */

package org.jfree.chart.block;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;

/**
 * A block that is filled with a single color.
 */
public class ColorBlock extends AbstractBlock implements Block {

    private Paint paint;
    
    /**
     * Creates a new block.
     * 
     * @param paint  the paint.
     * @param width  the width.
     * @param height  the height.
     */
    public ColorBlock(Paint paint, double width, double height) {
        this.paint = paint;
        setWidth(width);
        setHeight(height);
    }

    /**
     * Draws the block.
     * 
     * @param g2  the graphics device.
     * @param area  the area.
     */
    public void draw(Graphics2D g2, Rectangle2D area) {
        Rectangle2D bounds = getBounds();
        g2.setPaint(this.paint);
        g2.fill(bounds);
    }
    
    /**
     * Draws the block within the specified area.
     * 
     * @param g2  the graphics device.
     * @param area  the area.
     * @param params  ignored (<code>null</code> permitted).
     * 
     * @return Always <code>null</code>.
     */
    public Object draw(Graphics2D g2, Rectangle2D area, Object params) {
        draw(g2, area);
        return null;
    }

}
