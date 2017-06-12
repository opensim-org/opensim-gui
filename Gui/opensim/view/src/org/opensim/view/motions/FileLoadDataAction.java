package org.opensim.view.motions;

/**
 *
 * @author  ayman
 * Copyright (c)  2009, Stanford University and Ayman Habib. All rights reserved.
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
import org.opensim.utils.FileUtils;
import org.opensim.view.experimentaldata.AnnotatedMotion;
import org.opensim.view.pub.OpenSimDB;

public final class FileLoadDataAction extends CallableSystemAction {
    static int nextNumber=0;
    public void performAction() {
        // TODO implement action body
        String fileName = FileUtils.getInstance().browseForFilename(".trc,.mot,.sto", "Experimental data file");
        if (fileName != null){
            if (fileName.endsWith(".trc") || fileName.endsWith(".sto")){
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
                    modelForDataImport.addMarkers(amot.getClassified());
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
            else if (fileName.endsWith(".mot")){
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
                        ex.printStackTrace();
                        System.out.println("Missing resource file Models/Internal/_openSimlab.osim. Previewing is aborted.");
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
