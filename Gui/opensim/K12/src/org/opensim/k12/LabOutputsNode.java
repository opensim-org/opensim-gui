/*
 * LabOutputsNode.java
 *
 * Created on August 6, 2010, 2:48 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.k12;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;

/**
 *
 * @author ayman
 */
public class LabOutputsNode  implements Externalizable {
    
    private ArrayList<LabOutput> outputs = new ArrayList<LabOutput>(5);
    private long refreshRate=100L;
    
    /** Creates a new instance of LabOutputsNode */
    public LabOutputsNode() {
        
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    public ArrayList<LabOutput> getOutputs() {
        return outputs;
    }

    public void setOutputs(ArrayList<LabOutput> outputs) {
        this.outputs = outputs;
    }
    public void addOutput(LabOutput aOutput) {
        outputs.add(aOutput);
    }

    public long getRefreshRate() {
        return refreshRate;
    }

    public void setRefreshRate(long refreshRate) {
        this.refreshRate = refreshRate;
    }

}
