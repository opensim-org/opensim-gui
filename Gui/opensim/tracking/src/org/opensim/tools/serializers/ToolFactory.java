/*
 * ToolFactory.java
 *
 * Created on August 11, 2010, 12:56 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.tools.serializers;

import java.io.IOException;
import org.opensim.modeling.Model;
import org.opensim.tracking.ResultDisplayerInterface;

/**
 *
 * @author ayman
 */
final public class ToolFactory {
    
    
    static public ToolExecutor createExecutor(Model model, String setupFile, String toolString, ResultDisplayerInterface displayer) throws IOException
    {
        if (toolString.equalsIgnoreCase("IKTool"))
            return new IKToolExecutor(model, setupFile, displayer);
        else if (toolString.equalsIgnoreCase("ForwardTool"))
            return new ForwardToolExecutor(model, setupFile, displayer);
        else if (toolString.equalsIgnoreCase("AnalyzeTool"))
            return new AnalyzeToolExecutor(model, setupFile, displayer);
        throw new IOException("Unrecognized tool specification in ToolExecutor.createExecutor");
    }
    
}
