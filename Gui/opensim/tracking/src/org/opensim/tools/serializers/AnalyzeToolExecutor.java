/* -------------------------------------------------------------------------- *
 * OpenSim: AnalyzeToolExecutor.java                                          *
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
 * IKToolExecutor.java
 *
 * Created on August 11, 2010, 12:38 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.tools.serializers;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import org.opensim.modeling.Model;
import org.opensim.tracking.AnalyzeAndForwardToolPanel;
import org.opensim.tracking.AnalyzeToolModel;
import org.opensim.tracking.ResultDisplayerInterface;

/**
 *
 * @author ayman
 */
public class AnalyzeToolExecutor extends ToolExecutor implements Observer {
    
    AnalyzeToolModel toolModel;
                
    /** Creates a new instance of AnalyzeToolExecutor */
    public AnalyzeToolExecutor(Model model, String setupFile, ResultDisplayerInterface displayer) throws IOException {
        super(model, setupFile);
        toolModel = new AnalyzeToolModel(model,  AnalyzeAndForwardToolPanel.Mode.Analyze);
        toolModel.addResultDisplayer(displayer);
        //toolModel.loadSettings(setupFile);
        toolModel.addObserver(this);
    }

    public void execute() {
        toolModel.loadSettings(setupFile);
        toolModel.execute();
    }

    public void cancel() {
        toolModel.cancel();
    }

    public void update(Observable o, Object arg) {
       if (arg==AnalyzeToolModel.Operation.ExecutionStateChanged && !toolModel.isExecuting()){
         setChanged();
         notifyObservers(Operation.ExecutionFinished);
       }       
    }
    
}
