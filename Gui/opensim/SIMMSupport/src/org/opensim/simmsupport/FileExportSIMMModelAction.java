/* -------------------------------------------------------------------------- *
 * OpenSim: FileExportSIMMModelAction.java                                    *
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

package org.opensim.simmsupport;

import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.StatusDisplayer;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.modeling.Model;
//import org.opensim.modeling.SimmFileWriter;
import org.opensim.modeling.SimmFileWriter;
import org.opensim.view.pub.OpenSimDB;

/**
 * A Class represnting the Action of exporting an OpenSim model to SIMM's jnt format.
 * The exported model is the "Current" model in the GUI as indicated by the explorer window.
 */
public final class FileExportSIMMModelAction extends CallableSystemAction {
    
    public void performAction() {
        Model mdl = OpenSimDB.getInstance().getCurrentModel();
        if (mdl != null){
            OpenSimToSIMMOptionsJPanel exportPanel = new OpenSimToSIMMOptionsJPanel();
            DialogDescriptor dlg = new DialogDescriptor(exportPanel, "Export SIMM Model");
            DialogDisplayer.getDefault().createDialog(dlg).setVisible(true);
            Object userInput = dlg.getValue();
            if (((Integer)userInput).compareTo((Integer)DialogDescriptor.OK_OPTION)==0){
                String jntfileName = exportPanel.getJointFilename();
                if (jntfileName==null)
                    return;
                // Make sure we have the right extension.
                if (!jntfileName.endsWith(".jnt"))
                    jntfileName = jntfileName+".jnt";
                SimmFileWriter modelWriter=new SimmFileWriter(mdl);
                modelWriter.writeJointFile(jntfileName);

                // Now the muscles
                String mslfileName = exportPanel.getMslFilename();
                if (mslfileName==null || mslfileName.equalsIgnoreCase(""))
                    return;
                // Make sure we have the right extension.
                if (!mslfileName.endsWith(".msl"))
                    mslfileName = mslfileName+".msl";
                modelWriter.writeMuscleFile(mslfileName);

                StatusDisplayer.getDefault().setStatusText("Exported SIMM jnt & muscle files for model " + mdl.getName() + ".");
            }
        }
    }
    
    public String getName() {
        return NbBundle.getMessage(FileExportSIMMModelAction.class, "CTL_ExportSIMMJntAction");
    }
    
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
        //setEnabled(false);
        //ViewDB.getInstance().registerModelCommand(this);
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }

    public boolean isEnabled() {
       return OpenSimDB.getInstance().getCurrentModel()!=null;
   }

    private void throwUnimplemented() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
