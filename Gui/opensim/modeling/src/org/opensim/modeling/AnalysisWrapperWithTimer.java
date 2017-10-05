/* -------------------------------------------------------------------------- *
 * OpenSim: AnalysisWrapperWithTimer.java                                     *
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
 * AnalysisWrapperWithTimer.java
 *
 * Created on September 3, 2010, 3:50 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.modeling;

import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Ayman
 */
public class AnalysisWrapperWithTimer extends AnalysisWrapper {
    private Timer aTimer;
    private long refreshRateInMillis=1000L;
    private TimerTask theTask;
    private double simulationTime=0.0;
    /** Creates a new instance of AnalysisWrapperWithTimer */
    public AnalysisWrapperWithTimer() {
    }

    public AnalysisWrapperWithTimer(Model model) {
        super(model);
    }
    
    public int step(State s, int stepNumber) {
        int retValue=0;
        simulationTime = s.getTime();
       
        //retValue = super.step(s, stepNumber);
        return retValue;
    }

    public int begin(State s) {
        int retValue;
        simulationTime = s.getTime();
        retValue = super.begin(s);
        createTimer();
        return retValue;
    }

    protected void createTimer() {
        aTimer = new Timer();
    }

    public int end(State s) {
        int retValue=0;
        
        //retValue = super.end(s);
        if (aTimer!=null){
            aTimer.cancel();
            aTimer=null;
        }
        return retValue;
    }

    public long getRefreshRateInMillis() {
        return refreshRateInMillis;
    }

    public void setRefreshRateInMillis(long refreshRateInMillis) {
        this.refreshRateInMillis = refreshRateInMillis;
    }

    public void setTask(TimerTask task) {
        theTask = task;
        createTimer();
        aTimer.scheduleAtFixedRate(theTask, 0L, refreshRateInMillis);
    }

    public Timer getTimer() {
        return aTimer;
    }

    private boolean initialized = false;

    private boolean updateDisplay = false;

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public boolean isUpdateDisplay() {
        return updateDisplay;
    }

    public void setUpdateDisplay(boolean updateDisplay) {
        this.updateDisplay = updateDisplay;
    }

    protected void initializeTimer() {
        setTask(new TimerTask() {

            public void run() {
                updateDisplay = true;
            }
        });
        setInitialized(true);
    }
    
    public double getSimulationTime() {
        return simulationTime;
    }
}
