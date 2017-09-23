/* -------------------------------------------------------------------------- *
 * OpenSim: ForwardToolExecutor.java                                          *
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
import org.opensim.modeling.Model;
import org.opensim.tools.serializers.ToolExecutor.Operation;
import org.opensim.tracking.ForwardToolModel;
import org.opensim.tracking.ResultDisplayerInterface;

/**
 *
 * @author ayman
 */
public class ForwardToolExecutor extends ToolExecutor  {
    
    ForwardToolModel toolModel;
                
    /** Creates a new instance of IKToolExecutor */
    public ForwardToolExecutor(Model model, String setupFile, ResultDisplayerInterface displayer) throws IOException {
        super(model, setupFile);
        toolModel = new ForwardToolModel(model);
        toolModel.addResultDisplayer(displayer);
        //toolModel.loadSettings(setupFile);
        toolModel.addObserver(this);
    }

    public void execute() {
        toolModel.loadSettings(setupFile);
        toolModel.execute();
    }

    public void cancel() {
        toolModel.interrupt(true);
    }

    public void update(Observable o, Object arg) {
          if (arg==ForwardToolModel.Operation.ExecutionStateChanged && !toolModel.isExecuting()){
             setChanged();
             notifyObservers(Operation.ExecutionFinished);
         }       
    }
    
}
