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
 * ---------------------------
 * TimeSeriesURLGenerator.java
 * ---------------------------
 * (C) Copyright 2002-2006, by Richard Atkinson and Contributors.
 *
 * Original Author:  Richard Atkinson;
 * Contributors:     David Gilbert (for Object Refinery Limited);
 *
 * $Id: TimeSeriesURLGenerator.java,v 1.5.2.2 2006/07/06 09:42:12 mungady Exp $
 *
 * Changes:
 * --------
 * 29-Aug-2002 : Initial version (RA);
 * 09-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 23-Mar-2003 : Implemented Serializable (DG);
 * 15-Jul-2004 : Switched getX() with getXValue() and getY() with 
 *               getYValue() (DG);
 * 13-Jan-2005 : Modified for XHTML 1.0 compliance (DG);
 * ------------- JFREECHART 1.0.0 ---------------------------------------------
 * 06-Jul-2006 : Swap call to dataset's getX() --> getXValue() (DG);
 * 
 */

package org.jfree.chart.urls;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

import org.jfree.data.xy.XYDataset;

/**
 * A URL generator.
 */
public class TimeSeriesURLGenerator implements XYURLGenerator, Serializable {

    /** For serialization. */
    private static final long serialVersionUID = -9122773175671182445L;    
    
    /** A formatter for the date. */
    private DateFormat dateFormat = DateFormat.getInstance();
    
    /** Prefix to the URL */
    private String prefix = "index.html";

    /** Name to use to identify the series */
    private String seriesParameterName = "series";

    /** Name to use to identify the item */
    private String itemParameterName = "item";

    /**
     * Blank constructor
     */
    public TimeSeriesURLGenerator() {
        super();
    }

    /**
     * Construct TimeSeriesURLGenerator overriding defaults
     *
     * @param dDateFormat  a formatter for the date.
     * @param sPrefix  the prefix of the URL.
     * @param sSeriesParameterName  the name of the series parameter in the URL.
     * @param sItemParameterName  the name of the item parameter in the URL.
     */
    public TimeSeriesURLGenerator(DateFormat dDateFormat, String sPrefix,
                                  String sSeriesParameterName, 
                                  String sItemParameterName) {

        this.dateFormat = dDateFormat;
        this.prefix = sPrefix;
        this.seriesParameterName = sSeriesParameterName;
        this.itemParameterName = sItemParameterName;

    }

    /**
     * Generates a URL for a particular item within a series.
     *
     * @param dataset  the dataset.
     * @param series  the series number (zero-based index).
     * @param item  the item number (zero-based index).
     *
     * @return The generated URL.
     */
    public String generateURL(XYDataset dataset, int series, int item) {
        String result = this.prefix;
        boolean firstParameter = result.indexOf("?") == -1;
        Comparable seriesKey = dataset.getSeriesKey(series);
        if (seriesKey != null) {
            result += firstParameter ? "?" : "&amp;";
            result += this.seriesParameterName + "=" + seriesKey.toString();
            firstParameter = false;
        }

        long x = (long) dataset.getXValue(series, item);
        String xValue = this.dateFormat.format(new Date(x));
        result += firstParameter ? "?" : "&amp;";
        result += this.itemParameterName + "=" + xValue;

        return result;
    }


}
