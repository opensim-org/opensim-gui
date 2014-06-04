/*
 * LabOutputTextToObject.java
 *
 * Created on August 19, 2010, 6:31 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.k12;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 *
 * @author ayman
 */
public class LabOutputTextToObject extends LabOutputText {
    private String openSimType;
    private String objectName;
    //private double[] offset = new double[]{0., 0., 0.};
    private int fontSize=12;
    /**
     * Creates a new instance of LabOutputTextToObject
     */
    public LabOutputTextToObject() {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    public String getOpenSimType() {
        return openSimType;
    }

    public void setOpenSimType(String openSimType) {
        this.openSimType = openSimType;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }
/*
    public double[] getOffset() {
        return offset;
    }

    public void setOffset(double[] offset) {
        this.offset = offset;
    }
    */

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }
    
    public String toString() {
        String retValue;
        
        retValue = "Output: Anchored to Object"+openSimType+"."+objectName;
        return retValue;
    }
}
