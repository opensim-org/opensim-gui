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
