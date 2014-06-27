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
    /** Creates a new instance of AnalysisWrapperWithTimer */
    public AnalysisWrapperWithTimer() {
    }

    public AnalysisWrapperWithTimer(Model model) {
        super(model);
    }
    
    public int step(State s, int stepNumber) {
        int retValue;
        
        retValue = super.step(s, stepNumber);
        return retValue;
    }

    public int begin(State s) {
        int retValue;
        
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
    
}
