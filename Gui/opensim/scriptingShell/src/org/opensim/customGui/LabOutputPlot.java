/*
 * LabOutputPlot.java
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
public class LabOutputPlot extends LabOutput {
   private String plotTitle = "Joint torques vs. elbow flexion angle (case 1)";
   private String xAxisTitle = "Elbow flexion angle";
   private String yAxisTitle = "Joint torques torque";

   private String quantitySpecfication;
    
    /**
     * Creates a new instance of LabOutputPlot
     */
    public LabOutputPlot() {
    }

    public String getQuantitySpecfication() {
        return quantitySpecfication;
    }

    public void setQuantitySpecfication(String quantitySpecfication) {
        this.quantitySpecfication = quantitySpecfication;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    public String getPlotTitle() {
        return plotTitle;
    }

    public void setPlotTitle(String plotTitle) {
        this.plotTitle = plotTitle;
    }

    public String getXAxisTitle() {
        return xAxisTitle;
    }

    public void setXAxisTitle(String xAxisTitle) {
        this.xAxisTitle = xAxisTitle;
    }

    public String getYAxisTitle() {
        return yAxisTitle;
    }

    public void setYAxisTitle(String yAxisTitle) {
        this.yAxisTitle = yAxisTitle;
    }
    public String toString() {
        String retValue;
        
        retValue = "Output: Plot "+quantitySpecfication;
        return retValue;
    }
    
}
