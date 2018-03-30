/* -------------------------------------------------------------------------- *
 * OpenSim: FileLoadDataAction.java                                           *
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
package org.opensim.view.motions;

/**
 *
 * @author  ayman
 */
import java.io.File;
import java.io.IOException;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.modeling.MarkerData;
import org.opensim.view.experimentaldata.ModelForExperimentalData;
import org.opensim.modeling.Storage;
import org.opensim.modeling.Units;
import org.opensim.utils.ErrorDialog;
import org.opensim.utils.FileUtils;
import org.opensim.view.experimentaldata.AnnotatedMotion;
import org.opensim.view.pub.OpenSimDB;

public final class FileLoadDataAction extends CallableSystemAction {
    static int nextNumber=0;
    public void performAction() {
        // TODO implement action body
        String fileName = FileUtils.getInstance().browseForFilename(".trc,.mot,.sto", "Experimental data file");
        if (fileName != null){
            if (fileName.toLowerCase().endsWith(".trc") || fileName.toLowerCase().endsWith(".sto")){
                MarkerData markerData;
                try {
                    markerData = new MarkerData(fileName);
                    Storage newStorage = new Storage();
                    markerData.makeRdStorage(newStorage);
                    AnnotatedMotion amot = new AnnotatedMotion(newStorage, markerData.getMarkerNames());
                    amot.setUnitConversion(1.0/(markerData.getUnits().convertTo(Units.UnitType.Meters)));
                    amot.setName(new File(fileName).getName());
                    amot.setDataRate(markerData.getDataRate());
                    amot.setCameraRate(markerData.getCameraRate());
                    // Add the visuals to support it
                    ModelForExperimentalData modelForDataImport = new ModelForExperimentalData(nextNumber++, amot);
                    modelForDataImport.initSystem();
                    modelForDataImport.addMotionObjects(amot.getClassified());
                    OpenSimDB.getInstance().addModel(modelForDataImport);
                    MotionsDB.getInstance().addMotion(modelForDataImport, amot, null);
                    MotionsDB.getInstance().saveStorageFileName(amot, fileName);
                } catch (IOException ex) {
                    NotifyDescriptor.Message dlg =
                          new NotifyDescriptor.Message("Couldn't load data and/or model for display.\n"+
                                "Possible reasons: data file has incorrect format or resource file _openSimlab.osim missing.");
                  DialogDisplayer.getDefault().notify(dlg);   
            return;

                }
            }
            else if (fileName.toLowerCase().endsWith(".mot")){
                    Storage newStorage=null;
                    try {
                        newStorage = new Storage(fileName);
                    } catch (IOException ex) {
                        System.out.println("Failed to construct storage from "+fileName+". Previewing is aborted.");
                        ex.printStackTrace();
                    }
                    AnnotatedMotion amot = new AnnotatedMotion(newStorage);
                    
                    amot.setName(new File(fileName).getName());
                    ModelForExperimentalData modelForDataImport=null;
                    try {
                        modelForDataImport = new ModelForExperimentalData(nextNumber++, amot);
                        OpenSimDB.getInstance().addModel(modelForDataImport);
                    } catch (IOException ex) {
                        ErrorDialog.displayExceptionDialog(ex);
                        return;
                    }
                    MotionsDB.getInstance().addMotion(modelForDataImport, amot, null);
                    MotionsDB.getInstance().saveStorageFileName(amot, fileName);
            }
        }
    }
    
    public String getName() {
        return NbBundle.getMessage(FileLoadDataAction.class, "CTL_FileLoadDataAction");
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
    
}
