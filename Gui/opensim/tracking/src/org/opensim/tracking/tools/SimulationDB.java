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
    
    // The setChanged() protected method must overridden to make it public
    public synchronized void setChanged() {
        super.setChanged();
    }
    
    public static synchronized SimulationDB getInstance() {
        if (instance == null) {
             instance = new SimulationDB();
             
        }
        return instance;
    }

    public void startSimulation(ForwardToolModel toolModel) {
        forwardToolModel = toolModel;
        running = true;
        setChanged();
        notifyObservers();
    }
    public void stopSimulation() {
        if (forwardToolModel!=null){
            forwardToolModel.interrupt(true);
            forwardToolModel=null;
        }
        running = false;
        setChanged();
        notifyObservers();
    }

    /**
     * @return the running
     */
    public boolean isRunning() {
        return running;
    }

    public void finishSimulation() {
        forwardToolModel=null;
        running = false;
        setChanged();
        notifyObservers();
    }
    
    
}
