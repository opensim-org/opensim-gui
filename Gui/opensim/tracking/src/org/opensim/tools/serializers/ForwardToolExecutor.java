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
