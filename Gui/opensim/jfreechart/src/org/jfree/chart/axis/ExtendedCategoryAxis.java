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
 * -------------------------
 * ExtendedCategoryAxis.java
 * -------------------------
 * (C) Copyright 2003, 2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: ExtendedCategoryAxis.java,v 1.4.2.1 2005/10/25 20:37:34 mungady Exp $
 *
 * Changes
 * -------
 * 07-Nov-2003 : Version 1 (DG);
 * 07-Jan-2004 : Updated the createLabel() method (DG);
 * 29-Jan-2004 : Added paint attribute (DG);
 *
 */

package org.jfree.chart.axis;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.util.HashMap;
import java.util.Map;

import org.jfree.text.TextBlock;
import org.jfree.text.TextFragment;
import org.jfree.text.TextLine;
import org.jfree.ui.RectangleEdge;

/**
 * An extended version of the {@link CategoryAxis} class that supports 
 * sublabels on the axis.
 */
public class ExtendedCategoryAxis extends CategoryAxis {

    /** Storage for the sublabels. */
    private Map sublabels;
    
    /** The sublabel font. */
    private Font sublabelFont;
    
    /** The sublabel paint. */
    private Paint sublabelPaint;
    
    /**
     * Creates a new axis.
     * 
     * @param label  the axis label.
     */
    public ExtendedCategoryAxis(String label) {
        super(label);
        this.sublabels = new HashMap();
        this.sublabelFont = new Font("SansSerif", Font.PLAIN, 10);
        this.sublabelPaint = Color.black;
    }
    
    /**
     * Returns the font for the sublabels.
     * 
     * @return The font.
     */
    public Font getSubLabelFont() {
        return this.sublabelFont;
    }
    
    /**
     * Sets the font for the sublabels.
     * 
     * @param font  the font.
     */
    public void setSubLabelFont(Font font) {
        this.sublabelFont = font;
    }
    
    /**
     * Returns the paint for the sublabels.
     * 
     * @return The paint.
     */
    public Paint getSubLabelPaint() {
        return this.sublabelPaint;
    }
    
    /**
     * Sets the paint for the sublabels.
     * 
     * @param paint  the paint.
     */
    public void setSubLabelPaint(Paint paint) {
        this.sublabelPaint = paint;
    }
    
    /**
     * Adds a sublabel for a category.
     * 
     * @param category  the category.
     * @param label  the label.
     */
    public void addSubLabel(Comparable category, String label) {
        this.sublabels.put(category, label);
    }
    
    /**
     * Overrides the default behaviour by adding the sublabel to the text 
     * block that is used for the category label.
     * 
     * @param category  the category.
     * @param width  the width (not used yet).
     * @param edge  the location of the axis.
     * @param g2  the graphics device.
     * 
     * @return A label.
     */
    protected TextBlock createLabel(Comparable category, float width, 
                                    RectangleEdge edge, Graphics2D g2) {
        TextBlock label = super.createLabel(category, width, edge, g2);   
        String s = (String) this.sublabels.get(category);
        if (s != null) {
            if (edge == RectangleEdge.TOP || edge == RectangleEdge.BOTTOM) {
                TextLine line = new TextLine(
                    s, this.sublabelFont, this.sublabelPaint
                );
                label.addLine(line);
            }
            else if (edge == RectangleEdge.LEFT 
                    || edge == RectangleEdge.RIGHT) {
                TextLine line = label.getLastLine();
                if (line != null) {
                    line.addFragment(
                        new TextFragment(
                            "  " + s, this.sublabelFont, this.sublabelPaint
                        )
                    );
                }
            }
        }
        return label; 
    }
    
}
