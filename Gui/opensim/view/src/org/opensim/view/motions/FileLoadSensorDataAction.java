/* -------------------------------------------------------------------------- *
 * OpenSim: FileLoadSensorDataAction.java                                     *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2020 Stanford University and the Authors                *
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
package org.opensim.view.motions;

/**
 *
 * @author  ayman
 */
import java.awt.Dialog;
import java.io.File;
import java.io.IOException;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.modeling.DataTable;
import org.opensim.modeling.MarkerData;
import org.opensim.view.experimentaldata.ModelForExperimentalData;
import org.opensim.modeling.Storage;
import org.opensim.modeling.TimeSeriesTableQuaternion;
import org.opensim.modeling.Units;
import org.opensim.utils.ErrorDialog;
import org.opensim.utils.FileUtils;
import org.opensim.view.experimentaldata.AnnotatedMotion;
import org.opensim.view.pub.OpenSimDB;

public final class FileLoadSensorDataAction extends CallableSystemAction {
    static int nextNumber=0;
    public void performAction() {
        // TODO implement action body
        String fileName = FileUtils.getInstance().browseForFilename(".sto", "Sensor data file");
        if (fileName != null) {
            if (fileName.toLowerCase().endsWith(".mot") || fileName.toLowerCase().endsWith(".sto")) {
                Storage newStorage = null;
                try {
                    newStorage = new Storage(fileName);
                } catch (IOException ex) {
                    System.out.println("Failed to construct storage from " + fileName + ". Previewing is aborted.");
                    ex.printStackTrace();
                }
                // Query user for layout:
                // 0: line
                // 1: circle
                // 2: coincident with current model/pose
                // 3: coincident with current model segments
                SensorLayoutOptions layoutModel = new SensorLayoutOptions();
                JSensorLayoutPanel layoutPanel = new JSensorLayoutPanel(layoutModel);
                final DialogDescriptor dlg = new DialogDescriptor(layoutPanel, "Sensor Data Options");
                dlg.setModal(true);
                Dialog wDlg = DialogDisplayer.getDefault().createDialog(dlg);
                wDlg.pack();
                wDlg.setVisible(true);
                AnnotatedMotion amot = new AnnotatedMotion(newStorage);

                amot.setName(new File(fileName).getName());
                ModelForExperimentalData modelForDataImport = null;
                try {
                    modelForDataImport = new ModelForExperimentalData(nextNumber++, amot);
                    addSensorBodiesToModel(modelForDataImport, layoutModel);
                    OpenSimDB.getInstance().addModel(modelForDataImport);
                } catch (IOException ex) {
                    ErrorDialog.displayExceptionDialog(ex);
                    return;
                }
                amot.setModel(modelForDataImport);
                MotionsDB.getInstance().addMotion(modelForDataImport, amot, null);
                MotionsDB.getInstance().saveStorageFileName(amot, fileName);
            }
        }
    }
    
    public String getName() {
        return NbBundle.getMessage(FileLoadSensorDataAction.class, "CTL_FileLoadSensorDataAction");
    }
    
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }

    private void addSensorBodiesToModel(ModelForExperimentalData modelForDataImport, SensorLayoutOptions layoutModel) {
        switch(layoutModel.getLayout()){
            case Origin:
                break;  // Nothing to add
            default:
                throw new UnsupportedOperationException("Not supported yet.");
        }
    }    
}
