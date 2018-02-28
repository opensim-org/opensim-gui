/* -------------------------------------------------------------------------- *
 * OpenSim: ToolSerializer.java                                               *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib                                                     *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may    *
 * not use this file except in compliance with the License. You may obtain a  *
 * copy of the License at http://www.apache.org/licenses/LICENSE-2.0          *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 * -------------------------------------------------------------------------- */
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
import org.opensim.utils.ErrorDialog;

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
            ErrorDialog.displayExceptionDialog(ex);
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
