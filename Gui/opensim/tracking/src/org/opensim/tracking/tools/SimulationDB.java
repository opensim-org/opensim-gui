/* -------------------------------------------------------------------------- *
 * OpenSim: SimulationDB.java                                                 *
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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.tracking.tools;

import java.util.Observable;
import org.opensim.tracking.ForwardToolModel;

/**
 *
 * @author Ayman
 */
public class SimulationDB extends Observable{
    
    static SimulationDB instance;


    ForwardToolModel forwardToolModel= null;
    private boolean running = false;
    
    private SimulationDB() {
        instance = this;
    }
    
    public static synchronized SimulationDB getInstance() {
        if (instance == null) {
             instance = new SimulationDB();
             
        }
        return instance;
    }

    public void fireToolFinish() {
        running = false;
        setChanged();
        notifyObservers();
    }

    public void startSimulation(ForwardToolModel toolModel) {
        forwardToolModel = toolModel;
        fireToolStart();
    }

    public void fireToolStart() {
        running = true;
        setChanged();
        notifyObservers();
    }
    public void stopSimulation() {
        if (forwardToolModel!=null){
            forwardToolModel.interrupt(true);
            forwardToolModel=null;
        }
        fireToolFinish();
    }

    /**
     * @return the running
     */
    public boolean isRunning() {
        return running;
    }

    public boolean isSimulating() {
        return running && forwardToolModel!=null;
    }
    public void finishSimulation() {
        forwardToolModel=null;
        fireToolFinish();
    }
    
    
}
