/*
 * DblBoundedRangeModel.java
 *
 * Created on August 8, 2010, 9:41 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.swingui;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import javax.swing.DefaultBoundedRangeModel;

/**
 *
 * @author ayman
 */
public class DblBoundedRangeModel extends DefaultBoundedRangeModel implements Externalizable {
    /** The lower bound */
    private double dblMinimum;
    /** The upper bound */
    private double dblMaximum;
    /** The extent. */
    private double dblExtent;
    /**
     * The factor used to convert between our actual double and the integer underneath it
     */
    private int    doubleToIntFactor;
    /** Number of decimal places to keep */
    private int    numDecimals;
    
    /** Default constructor.  Initializes the model with a min of 0,
     * max of 1, value and extent of 0, and a numDecimals of 3
     * decimal points. */
    public DblBoundedRangeModel() {
        this(0.0, 0.0, 0.0, 1.0, 3);
    }
    
    /** Constructor that takes all parameters except numDecimals, which defaults to 3 decimal places. */
    public DblBoundedRangeModel(double v, double e, double minimum,
            double maximum) {
        this(v, e, minimum, maximum, 3);
        
    }
    
    /** Constructor for which all values have been provided by the user. */
    public DblBoundedRangeModel(double v, double e, double minimum,
            double maximum, int numDecimals) {
        doSetRangeProps(v, e, minimum, maximum, numDecimals);
    }
    
    /** Changes the Set Range Properties.  This involves ensuring that all ranges are valid,
     * and that maximum >= minimum, minimum <= maximum, and minimum <= value <= (maximum - extent) */
    public void doSetRangeProps(double v, double e, double minimum, double maximum, int p) {
        double dblValue;
        
        if ( minimum > maximum ) {
            double swap = maximum;
            maximum = minimum;
            minimum = swap;
        }
        if ( (v + e) > maximum ) {
            v = maximum - e;
        }
        if ( v < minimum ) {
            v = minimum;
        }
        
        numDecimals = p;
        doubleToIntFactor = new Double(Math.pow(10, p)).intValue();
        
        dblMinimum = minimum;
        setMinimum((int)(dblMinimum * doubleToIntFactor));
        dblMaximum = maximum;
        setMaximum((int)(dblMaximum * doubleToIntFactor));
        dblValue   = v;
        setValue((int)(dblValue * doubleToIntFactor));
        dblExtent  = e;
        setExtent((int)(dblExtent  * doubleToIntFactor));
    }
    
    /** Changes the Set Range Properties, and fires events to alert listeners of the change. */
    public void setRangeProperties(double v, double e, double minimum, double maximum, int numDecimals, boolean newValueIsAdjusting) {
        doSetRangeProps(v, e, minimum, maximum, numDecimals);
        setValueIsAdjusting(newValueIsAdjusting);
        fireStateChanged();
    }
    
    /** Returns the current value as a double. */
    public double getDoubleValue() {
        return (double)getValue() / (double)doubleToIntFactor;
    }
    
    /** Set the value from a double. */
    public void setDoubleValue(double v) {
        if ( (v + dblExtent) > dblMaximum ) {
            v = dblMaximum - dblExtent;
        }
        if ( v < dblMinimum ) {
            v = dblMinimum;
        }
        double dblValue = v;
        setValue((int)(dblValue * doubleToIntFactor));
    }
    
    public int getConversion() {
        return doubleToIntFactor;
    }
    
    double getDoubleMin() {
        return dblMinimum;
    }
    
    double getDoubleMax() {
        return dblMaximum;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    public double getDblMinimum() {
        return dblMinimum;
    }

    public void setDblMinimum(double dblMinimum) {
        this.dblMinimum = dblMinimum;
    }

    public void setDblMaximum(double dblMaximum) {
        this.dblMaximum = dblMaximum;
    }

    public double getDblExtent() {
        return dblExtent;
    }

    public int getNumDecimals() {
        return numDecimals;
    }
}

