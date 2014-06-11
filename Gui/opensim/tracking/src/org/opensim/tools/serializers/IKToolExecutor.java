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
import org.opensim.tools.serializers.ToolExecutor.Operation;
import org.opensim.tracking.IKToolModel;
import org.opensim.tracking.ResultDisplayerInterface;

/**
 *
 * @author ayman
 */
public class IKToolExecutor extends ToolExecutor implements Observer {
    
    IKToolModel toolModel;
    /** Creates a new instance of IKToolExecutor */
    public IKToolExecutor(Model model, String setupFile, ResultDisplayerInterface displayer) throws IOException {
        super(model, setupFile);
        toolModel = new IKToolModel(model);
        //toolModel.loadSettings(setupFile);
        toolModel.addObserver(this);
    }

    public void execute() {
        toolModel.loadSettings(setupFile);
        toolModel.execute();    // runs in background
     }

    public void cancel() {
        toolModel.cancel();
         setChanged();
         notifyObservers(Operation.ExecutionInterrupted);
    }

    public void update(Observable o, Object arg) {
         if (arg==IKToolModel.Operation.ExecutionStateChanged && !toolModel.isExecuting()){
             setChanged();
             notifyObservers(Operation.ExecutionFinished);
         }
    }
    
}
