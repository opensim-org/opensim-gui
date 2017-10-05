/* -------------------------------------------------------------------------- *
 * OpenSim: JavaPlottingCallback.java                                         *
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
 * JavaIKAnimationCallback.java
 *
 * Created on April 25, 2007, 5:48 PM
 *
 */

package org.opensim.tracking;

import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.opensim.modeling.AnalysisWrapperWithTimer;
import org.opensim.modeling.ArrayStr;
import org.opensim.modeling.CMCTool;
import org.opensim.modeling.Model;
import org.opensim.modeling.State;
import org.opensim.modeling.Storage;
import org.opensim.plotter.JPlotterPanel;
import org.opensim.plotter.PlotCurve;

/**
 *
 * @author Ayman Habib.
 *
 * An acallback that can be can be invoked while running RRA or CMC
 * It should figure out the mode from the tool which mode is it in and based on that
 * - In RRA1 should display the residuals
 * - In CMC should probably show pre and post tracking quantities
 */
public class JavaPlottingCallback extends AnalysisWrapperWithTimer{
    
    CMCTool cmcRraTool;
    JPlotterPanel plotter;
    PlotCurve[] cvs;
    Storage s;
    int timeIndex=-1;
    int lastIndex;
    int errorsIndexInStorage=-1;
    private String[] qtyNames=null;
    int[] qtyIndices=null;   // Keep indices to qtys for quick access in step
    //OpenSimContext context=null;
    
    /** Creates a new instance of JavaPlottingCallback */
    public JavaPlottingCallback(Model model, Model aModelForDisplay) {
        super(model);
        //context = OpenSimDB.getInstance().getContext(aModelForDisplay);
    }
    
  public int step(State s, int i) {
      super.step(s, i);
      processStep(getSimulationTime());
      return 0;
   }
   
   private void processStep(final double aT) {
       // begin should have been called first but actully it is not!
       synchronized(this){ // Make sure nothing happens to this object until we're done.
           if (!isInitialized()){
               setupPlotter();
               initializeTimer();
           }
           if (isUpdateDisplay()){
               // update Plotter if it's up'
               timeIndex = s.findIndex(aT);
               System.out.println("JavaPlottingCallback:time="+aT+" index="+timeIndex);
               if (timeIndex>=0 && lastIndex!=timeIndex){
                   for(int i=0; i<getQtyNames().length; i++){
                       double value = s.getStateVector(timeIndex).getData().getitem(qtyIndices[i]);
                       cvs[i].addDataPoint((double)aT, value);
                   }
                   lastIndex=timeIndex;
               }
           }
       }
   }

 public int begin(State s) {
        int retValue=0;
        
        //retValue = super.begin(aStep, aDT, aT, aX, aY);
        setupPlotter();

        return retValue;
    }

    private void setupPlotter() {
        // Launch plotter 
        s = findForceStorage(cmcRraTool);
        if (s == null)
            return;
        
        System.out.println("=====");
        ArrayStr residuals = s.getColumnLabels();
        for(int i=0; i< residuals.getSize(); i++)
            System.out.println(residuals.getitem(i));
        System.out.println("=====");
        // Create plotter dialog and display s, column for markerError
        plotter = new JPlotterPanel();
        plotter.setName("Forces");
        DialogDescriptor dlg = new DialogDescriptor(plotter,"Plotter Dialog");
        dlg.setModal(false);
        DialogDisplayer.getDefault().createDialog(dlg).setVisible(true);
        dlg.setTitle("Live Plot: Forces");
        cvs = new PlotCurve[getQtyNames().length];
        
        for(int i=0; i<getQtyNames().length; i++){
                cvs[i]=plotter.showAnalysisCurveAgainstTime(get_model(), s, "Residual Forces", 
                         getQtyNames()[i], getQtyNames()[i], "xlabel-to-fill", "y-label-to-fill"
                         );
            qtyIndices[i]=s.getStateIndex(getQtyNames()[i]);
        }
       
    }

    protected void finalize() {
        super.finalize();
    }


    void setTool(CMCTool cMCTool) {
        cmcRraTool = cMCTool;
    }

    private Storage findForceStorage(CMCTool cmcRraTool) {
        Storage residualsStorage= cmcRraTool.getForceStorage();
        return residualsStorage;
    }

    public String[] getQtyNames() {
        return qtyNames;
    }

    public void setQtyNames(String[] qtyNames) {
        this.qtyNames = qtyNames;
        qtyIndices = new int[getQtyNames().length];
    }
    
}
