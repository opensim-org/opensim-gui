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
import org.opensim.tracking.AnalyzeAndForwardToolPanel;
import org.opensim.tracking.AnalyzeToolModel;
import org.opensim.tracking.ResultDisplayerInterface;

/**
 *
 * @author ayman
 */
public class AnalyzeToolExecutor extends ToolExecutor implements Observer {
    
    AnalyzeToolModel toolModel;
                
    /** Creates a new instance of AnalyzeToolExecutor */
    public AnalyzeToolExecutor(Model model, String setupFile, ResultDisplayerInterface displayer) throws IOException {
        super(model, setupFile);
        toolModel = new AnalyzeToolModel(model,  AnalyzeAndForwardToolPanel.Mode.Analyze);
        toolModel.addResultDisplayer(displayer);
        //toolModel.loadSettings(setupFile);
        toolModel.addObserver(this);
    }

    public void execute() {
        toolModel.loadSettings(setupFile);
        toolModel.execute();
    }

    public void cancel() {
        toolModel.cancel();
    }

    public void update(Observable o, Object arg) {
       if (arg==AnalyzeToolModel.Operation.ExecutionStateChanged && !toolModel.isExecuting()){
         setChanged();
         notifyObservers(Operation.ExecutionFinished);
       }       
    }
    
}
