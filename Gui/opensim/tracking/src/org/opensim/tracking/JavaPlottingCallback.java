/*
 * JavaIKAnimationCallback.java
 *
 * Created on April 25, 2007, 5:48 PM
 *
 * Copyright (c)  2006, Stanford University and Ayman Habib. All rights reserved.
* Use of the OpenSim software in source form is permitted provided that the following
* conditions are met:
* 	1. The software is used only for non-commercial research and education. It may not
*     be used in relation to any commercial activity.
* 	2. The software is not distributed or redistributed.  Software distribution is allowed 
*     only through https://simtk.org/home/opensim.
* 	3. Use of the OpenSim software or derivatives must be acknowledged in all publications,
*      presentations, or documents describing work in which OpenSim or derivatives are used.
* 	4. Credits to developers may not be removed from executables
*     created from modifications of the source.
* 	5. Modifications of source code must retain the above copyright notice, this list of
*     conditions and the following disclaimer. 
* 
*  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
*  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
*  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
*  SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
*  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
*  TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
*  HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
*  OR BUSINESS INTERRUPTION) OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
*  WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
