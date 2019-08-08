/* -------------------------------------------------------------------------- *
 * OpenSim: ToolbarRunForwardAction.java                                      *
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

package org.opensim.tracking.tools;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import javax.swing.ImageIcon;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.modeling.ForwardTool;
import org.opensim.modeling.Model;
import org.opensim.tracking.ForwardToolModel;
import org.opensim.utils.ErrorDialog;
import org.opensim.view.experimentaldata.ModelForExperimentalData;
import org.opensim.view.pub.OpenSimDB;

public final class ToolbarRunForwardAction extends CallableSystemAction implements Observer {
   
    private boolean enabled = true;
    private double finalTime = 5.0;
    
    public ToolbarRunForwardAction() {
        SimulationDB.getInstance().addObserver(this);
        OpenSimDB.getInstance().addObserver(this);
    }
   public void performAction() {
       ForwardToolModel toolModel=null;
            try {
                Model currentModel = OpenSimDB.getInstance().getCurrentModel();
                if (currentModel == null || currentModel instanceof ModelForExperimentalData)
                    return;
                // TODO implement action body
                toolModel = new ForwardToolModel(currentModel);
                toolModel.setFinalTime(getFinalTime());
                toolModel.setSolveForEquilibrium(true);
                ((ForwardTool) toolModel.getTool()).setPrintResultFiles(false);
                toolModel.execute();
                
                // Change 
            } catch (IOException ex) {
                ErrorDialog.displayExceptionDialog(ex);
            }   
   }
   
   public String getName() {
      return NbBundle.getMessage(ToolbarRunForwardAction.class, "CTL_ToolbarRunAction");
   }
   
   protected void initialize() {
      super.initialize();
      // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
      setIcon(new ImageIcon(getClass().getResource("/org/opensim/tracking/tools/run.png")));
   }
   
   public HelpCtx getHelpCtx() {
      return HelpCtx.DEFAULT_HELP;
   }
   
   protected boolean asynchronous() {
      return false;
   }
   
   public boolean isEnabled() {
      boolean toolEnabled = checkToolStatus();
      return enabled && toolEnabled;
   }

    private boolean checkToolStatus() {
        ForwardToolAction fdAction;
        boolean toolEnabled=true;
        try {
            fdAction = (ForwardToolAction) ForwardToolAction.findObject((Class)Class.forName("org.opensim.tracking.tools.ForwardToolAction"), true);
            toolEnabled = fdAction.isEnabled();
        } catch (ClassNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
        return toolEnabled;
    }
    public void update(Observable o, Object o1) {
        if (o instanceof SimulationDB){
            SimulationDB sdb = (SimulationDB) o;
            if (sdb.isRunning()){
                //System.out.println("Will disable Run");
                enabled = false;
                setEnabled(enabled);
            }
            else {
                //System.out.println("Will enable Run");
                enabled = true;
                setEnabled(enabled);
            }
        }
        else if (o instanceof OpenSimDB){
            enabled = checkToolStatus();
            setEnabled(enabled);
    }
    }

    /**
     * @return the finalTime
     */
    public double getFinalTime() {
        return finalTime;
    }

    /**
     * @param finalTime the finalTime to set
     */
    public void setFinalTime(double finalTime) {
        this.finalTime = finalTime;
    }
}
