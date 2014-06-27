/*
 * LabOutputTextToPanel.java
 *
 * Created on August 19, 2010, 6:31 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.k12;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 *
 * @author ayman
 */
public class LabOutputTextToPanel extends LabOutput {
    private String htmlTemplate;
    private String quantitySpecfication;
    
    /**
     * Creates a new instance of LabOutputTextToPanel
     */
    public LabOutputTextToPanel() {
    }

    public String getHtmlTemplate() {
        return htmlTemplate;
    }

    public void setHtmlTemplate(String htmlTemplate) {
        this.htmlTemplate = htmlTemplate;
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
    
    public String toString() {
        String retValue;
        
        retValue = "Output: Panel. Quantity:"+quantitySpecfication;
        return retValue;
    }
    
}
