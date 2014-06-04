/*
 * ToolSerializer.java
 *
 * Created on July 29, 2010, 1:09 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.tools.serializers;

import java.io.IOException;
import java.io.Serializable;
import java.util.Observable;
import java.util.Observer;
import org.opensim.modeling.AbstractTool;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimObject;
import org.opensim.tracking.AbstractToolModel;
import org.opensim.tracking.AnalyzeAndForwardToolPanel;
import org.opensim.tracking.AnalyzeToolModel;
import org.opensim.tracking.ForwardToolModel;
import org.opensim.tracking.IKToolModel;

/**
 *
 * @author ayman
 */
public class ToolSerializer extends Observable implements Serializable{
    
    transient AbstractTool dTool;
    transient Model model;
    private String setupFile;
    transient AbstractToolModel tool1=null;
    transient IKToolModel tool2=null;
    /**
     * Creates a new instance of ToolSerializer
     */
    public ToolSerializer() {
    }

    public String getSetupFile() {
        return setupFile;
    }

    public void setSetupFile(String setupFile) {
        this.setupFile = setupFile;
    }
    
    public void executeTool() {
        OpenSimObject obj = OpenSimObject.makeObjectFromFile(setupFile);
        AbstractTool tool = AbstractTool.safeDownCast(obj);
        try {
            if (tool.getConcreteClassName().equalsIgnoreCase("IKTool")){
                IKToolModel toolModel = new IKToolModel(model);
                tool2 = toolModel;
                toolModel.loadSettings(setupFile);
                toolModel.execute();
            }
            else if (tool.getConcreteClassName().equalsIgnoreCase("ForwardTool")){
                ForwardToolModel toolModel = new ForwardToolModel(model);
                tool1 = toolModel;
                toolModel.loadSettings(setupFile);
                toolModel.execute();
            }
            else if (tool.getConcreteClassName().equalsIgnoreCase("AnalyzeTool")){ // Covers IVD, StaticOptimization and Analyze
                AnalyzeToolModel toolModel = new AnalyzeToolModel(model, AnalyzeAndForwardToolPanel.Mode.Analyze);
                tool1 = toolModel;
                toolModel.loadSettings(setupFile);
                toolModel.execute();                
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public void stopTool() {
        if (tool1 != null) 
            tool1.cancel();
        else if (tool2 != null)
            tool2.cancel();
        
    }
}
