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
 * -----------------------
 * CategoryItemEntity.java
 * -----------------------
 * (C) Copyright 2002-2005, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Richard Atkinson;
 *                   Christian W. Zuckschwerdt;
 *
 * $Id: CategoryItemEntity.java,v 1.5.2.1 2005/10/25 20:41:59 mungady Exp $
 *
 * Changes:
 * --------
 * 23-May-2002 : Version 1 (DG);
 * 12-Jun-2002 : Added Javadoc comments (DG);
 * 26-Jun-2002 : Added getImageMapAreaTag() method (DG);
 * 05-Aug-2002 : Added new constructor to populate URLText
 *               Moved getImageMapAreaTag() to ChartEntity (superclass) (RA);
 * 03-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 30-Jul-2003 : Added CategoryDataset reference (CZ);
 * 20-May-2004 : Added equals() and clone() methods, and implemented 
 *               Serializable (DG);
 * 11-Jan-2005 : Removed deprecated code in preparation for 1.0.0 release (DG);
 *
 */

package org.jfree.chart.entity;

import java.awt.Shape;
import java.io.Serializable;

import org.jfree.data.category.CategoryDataset;
import org.jfree.util.ObjectUtilities;

/**
 * A chart entity that represents one item within a category plot.
 */
public class CategoryItemEntity extends ChartEntity 
                                implements Cloneable, Serializable {

    /** For serialization. */
    private static final long serialVersionUID = -8657249457902337349L;
    
    /** The dataset. */
    private transient CategoryDataset dataset;
    
    /** The series (zero-based index). */
    private int series;

    /** The category. */
    private Object category;

    /** The category index. */
    private int categoryIndex;

    /**
     * Creates a new category item entity.
     *
     * @param area  the area.
     * @param toolTipText  the tool tip text.
     * @param urlText  the URL text for HTML image maps.
     * @param dataset  the dataset.
     * @param series  the series (zero-based index).
     * @param category  the category.
     * @param categoryIndex  the category index.
     */
    public CategoryItemEntity(Shape area, String toolTipText, String urlText,
                              CategoryDataset dataset,
                              int series, Object category, int categoryIndex) {

        super(area, toolTipText, urlText);
        this.dataset = dataset;
        this.series = series;
        this.category = category;
        this.categoryIndex = categoryIndex;

    }

    /**
     * Returns the datset this entity refers to.
     *
     * @return The dataset (possibly <code>null</code>).
     */
    public CategoryDataset getDataset() {
        return this.dataset; 
    }

    /**
     * Sets the datset this entity refers to.
     *
     * @param dataset  the dataset (<code>null</code> permited).
     */
    public void setDataset(CategoryDataset dataset) {
        this.dataset = dataset;
    }

    /**
     * Returns the series index.
     *
     * @return The series index.
     */
    public int getSeries() {
        return this.series;
    }

    /**
     * Sets the series index.
     *
     * @param series  the series index (zero-based).
     */
    public void setSeries(int series) {
        this.series = series;
    }

    /**
     * Returns the category.
     *
     * @return The category (possibly <code>null</code>).
     */
    public Object getCategory() {
        return this.category;
    }

    /**
     * Sets the category.
     *
     * @param category  the category (<code>null</code> permitted).
     */
    public void setCategory(Object category) {
        this.category = category;
    }

    /**
     * Returns the category index.
     *
     * @return The index.
     */
    public int getCategoryIndex() {
        return this.categoryIndex;
    }

    /**
     * Sets the category index.
     *
     * @param index  the category index.
     */
    public void setCategoryIndex(int index) {
        this.categoryIndex = index;
    }

    /**
     * Returns a string representing this object (useful for debugging 
     * purposes).
     *
     * @return A string.
     */
    public String toString() {
        return "Category Item: series=" + this.series 
               + ", category=" + this.category.toString();
    }

    /**
     * Tests the entity for equality with an arbitrary object.
     * 
     * @param obj  the object (<code>null</code> permitted).
     * 
     * @return A boolean.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;      
        }
        if (obj instanceof CategoryItemEntity && super.equals(obj)) {
            CategoryItemEntity cie = (CategoryItemEntity) obj;
            if (this.categoryIndex != cie.categoryIndex) {
                return false;   
            }
            if (this.series != cie.series) {
                return false;   
            }
            if (!ObjectUtilities.equal(this.category, cie.category)) {
                return false;   
            }
            return true;
        }
        return false;
    }

}
