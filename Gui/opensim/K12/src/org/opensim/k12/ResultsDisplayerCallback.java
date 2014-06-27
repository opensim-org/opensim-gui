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

package org.opensim.k12;

import java.awt.Dialog;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.opensim.modeling.AnalysisWrapperWithTimer;
import org.opensim.modeling.Model;
import org.opensim.modeling.State;
import org.opensim.modeling.Storage;
import org.opensim.plotter.JPlotterPanel;
import org.opensim.plotter.PlotCurve;
import org.opensim.view.motions.JavaMotionDisplayerCallback;
import org.opensim.view.motions.MotionsDB;
import org.opensim.view.pub.OpenSimDB;

/**
 *
 * @author Ayman Habib.
 *
 * An acallback that can be can be invoked while running RRA or CMC
 * It should figure out the mode from the tool which mode is it in and based on that
 * - In RRA1 should display the residuals
 * - In CMC should probably show pre and post tracking quantities
 */
public class ResultsDisplayerCallback extends AnalysisWrapperWithTimer{
    
    //CMCTool cmcRraTool;
    private JPlotterPanel plotter;
    PlotCurve[] cvs;
    Storage s;
    int timeIndex=-1;
    int lastIndex;
    int errorsIndexInStorage=-1;
    private String[] qtyNames=null;
    int[] qtyIndices=null;   // Keep indices to qtys for quick access in step
    boolean plotterInitialized=false;
    private ArrayList<LabOutputInterface> outputs=new ArrayList<LabOutputInterface>();
   /** Creates a new instance of JavaPlottingCallback */
    public ResultsDisplayerCallback(Model model) {
        super(model);
        setRefreshRateInMillis(JavaMotionDisplayerCallback.getRefreshRatePreference());
    }
    
  public int step(State s, int i) {
      super.step(s, i);
      processStep(getSimulationTime());
      return 0;
   }
   
   private void processStep(final double aT) {
       // begin should have been called first but actully it is not!
       synchronized(this){ // Make sure nothing happens to this object until we're done.
           /*if (!plotterInitialized){
               setupPlotterIfNeeded();
           }
           if (plotterInitialized){
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
           }*/
           if (isUpdateDisplay()) {
               for(LabOutputInterface output:outputs)
                   output.updateDisplay(this.getSimulationTime());
           }
       }
   }

 public int begin(State s) {
        int retValue=0;
        
        //retValue = super.begin(aStep, aDT, aT, aX, aY);
        setupPlotterIfNeeded();
        setupTextOutput();
        setupGraphicalOutput();
        if (!isInitialized()){
            initializeTimer();
        }
        return retValue;
    }

    private void setupPlotterIfNeeded() {

        // Create plotter dialog and display s, column for markerError
        boolean plotSpecified = false;
        for(LabOutputInterface output:outputs){
            if (output instanceof PlotOutputPanel){
                plotSpecified = true; 
            }
            if (plotSpecified) break;
        }
        if (!plotSpecified) return;  
        plotter = new JPlotterPanel();
        plotter.setName("Results");
        plotter.collapseControlPanel();
        DialogDescriptor dlg = new DialogDescriptor(plotter,"Plotter Dialog");
        dlg.setModal(false);
        Dialog dlgWindow = DialogDisplayer.getDefault().createDialog(dlg);
        dlgWindow.setVisible(true);
        dlgWindow.addWindowListener(new WindowAdapter(){

         public void windowOpened(WindowEvent e) {
            MotionsDB.getInstance().addObserver(plotter);
            OpenSimDB.getInstance().addObserver(plotter);   // Make sure current model does not change under us
         }
         public void windowClosing(WindowEvent e) {
            MotionsDB.getInstance().deleteObserver(plotter);
            OpenSimDB.getInstance().deleteObserver(plotter); 
         }

         public void windowClosed(WindowEvent e) {
            MotionsDB.getInstance().deleteObserver(plotter);
            OpenSimDB.getInstance().deleteObserver(plotter); 
         }

    });

        /*dlg.setTitle("Live Plot: Forces");
        cvs = new PlotCurve[getQtyNames().length];
        
        for(int i=0; i<getQtyNames().length; i++){
                cvs[i]=plotter.showAnalysisCurveAgainstTime(get_model(), s, "Residual Forces", 
                         getQtyNames()[i], getQtyNames()[i], "xlabel-to-fill", "y-label-to-fill"
                         );
            qtyIndices[i]=s.getStateIndex(getQtyNames()[i]);
        }*/
        plotterInitialized=true;
       
    }

    protected void finalize() {
        //super.finalize();
    }
    
    private void setupTextOutput() {
        for(LabOutputInterface nextOuput:outputs)
            nextOuput.connectQuantityToSource(get_model());
    }

    private void setupGraphicalOutput() {
        //throw new UnsupportedOperationException("Not yet implemented");
    }
    public void addOutput(LabOutputInterface output){
       outputs.add(output);
    }   

    public int end(State s) {
        int retValue;
        
        //retValue = super.end(s);
        setUpdateDisplay(true);
        processStep(getSimulationTime());
        return 0;
    }

    public JPlotterPanel getPlotter() {
        return plotter;
    }
}
