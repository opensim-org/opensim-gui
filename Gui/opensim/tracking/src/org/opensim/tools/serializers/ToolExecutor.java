/* -------------------------------------------------------------------------- *
 * OpenSim: ToolExecutor.java                                                 *
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
 * ToolExecutor.java
 *
 * Created on August 11, 2010, 12:32 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.tools.serializers;

import java.util.Observable;
import java.util.Observer;
import org.opensim.modeling.Model;

/**
 *
 * @author ayman
 */
public abstract class ToolExecutor extends Observable // Clients who want to track Tool execution
        implements Observer{ // Observes toolModel's and map to common events'
    
    protected String setupFile;
    protected Model model;
    
    public enum Operation { ExecutionStarted, ExecutionInterrupted, ExecutionFinished, ExecutionNotStarted };

    public ToolExecutor(Model model, String setupFile) {
        this.setupFile = setupFile;
        this.model = model;
    }
    abstract public void execute();
    abstract public void cancel();
}
