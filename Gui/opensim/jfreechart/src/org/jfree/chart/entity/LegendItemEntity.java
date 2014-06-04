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
 * LegendItemEntity.java
 * ---------------------
 * (C) Copyright 2003-2005, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: LegendItemEntity.java,v 1.3.2.1 2005/10/25 20:41:59 mungady Exp $
 *
 * Changes:
 * --------
 * 05-Jun-2003 : Version 1 (DG);
 * 20-May-2004 : Added equals() method and implemented Cloneable and 
 *               Serializable (DG);
 *
 */

package org.jfree.chart.entity;

import java.awt.Shape;
import java.io.Serializable;

/**
 * An entity that represents an item within a legend.
 */
public class LegendItemEntity extends ChartEntity 
                              implements Cloneable, Serializable {

    /** For serialization. */
    private static final long serialVersionUID = -7435683933545666702L;
    
    /** The series index. */
    private int seriesIndex;

    /**
     * Creates a legend item entity.
     *
     * @param area  the area.
     */
    public LegendItemEntity(Shape area) {
        super(area);
    }

    /**
     * Returns the series index.
     *
     * @return The series index.
     */
    public int getSeriesIndex() {
        return this.seriesIndex;
    }

    /**
     * Sets the series index.
     *
     * @param index  the series index.
     */
    public void setSeriesIndex(int index) {
        this.seriesIndex = index;
    }
    
    /**
     * Tests this object for equality with an arbitrary object.
     * 
     * @param obj  the object (<code>null</code> permitted).
     * 
     * @return A boolean.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;   
        }
        if (obj instanceof LegendItemEntity && super.equals(obj)) {
            LegendItemEntity e = (LegendItemEntity) obj;
            if (this.seriesIndex != e.seriesIndex) {
                return false;   
            }
            return true;   
        }
        return false;
    }
    
    /**
     * Returns a clone of the entity.
     * 
     * @return A clone.
     * 
     * @throws CloneNotSupportedException if there is a problem cloning the 
     *         object.
     */
    public Object clone() throws CloneNotSupportedException {
        return super.clone();   
    }

}
