/*
 * LabOutput.java
 *
 * Created on August 19, 2010, 6:56 PM
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
public abstract class LabOutput implements Externalizable {
    
    /** Creates a new instance of LabOutput */
    public LabOutput() {
    }

    public abstract String getQuantitySpecfication();

    public abstract void setQuantitySpecfication(String quantityDescriptor);

    public abstract void writeExternal(ObjectOutput out) throws IOException;

    public abstract void readExternal(ObjectInput in) throws IOException, ClassNotFoundException;
    
}
