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
public class LabOutputText extends LabOutput {
    private String quantitySpecfication;
    private String textTemplate;
    /**
     * Creates a new instance of LabTextOutputToPanel
     */
    public LabOutputText() {
    }

    public String getQuantitySpecfication() {
        return quantitySpecfication;
    }

    public void setQuantitySpecfication(String quantityDescriptor) {
        this.quantitySpecfication = quantityDescriptor;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    public String getTextTemplate() {
        return textTemplate;
    }

    public void setTextTemplate(String textTemplate) {
        this.textTemplate = textTemplate;
    }
    public String toString() {
        String retValue;
        
        retValue = "Output: Panel. Quantity:"+quantitySpecfication;
        return retValue;
    }
    
}
