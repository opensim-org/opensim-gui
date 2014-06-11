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
