/* -------------------------------------------------------------------------- *
 * OpenSim: ToolFactory.java                                                  *
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
