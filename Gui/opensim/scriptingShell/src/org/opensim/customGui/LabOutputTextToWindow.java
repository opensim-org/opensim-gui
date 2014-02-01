/*
 * LabTextOutputToPanel.java
 *
 * Created on August 19, 2010, 6:31 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.customGui;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 *
 * @author ayman
 */
public class LabOutputTextToWindow extends LabOutputText {
    private String location="LowerLeft";
    /**
     * Creates a new instance of LabTextOutputToPanel
     */
    public LabOutputTextToWindow() {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    
    public String toString() {
        String retValue;
        
        retValue = "Output: Anchor to 3D Window. Quantity:"+this.getQuantitySpecfication();
        return retValue;
    }
    
}
