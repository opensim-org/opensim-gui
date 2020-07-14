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
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import org.opensim.modeling.Vector;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.modeling.Body;
import org.opensim.modeling.BodyOrSpaceType;
import org.opensim.modeling.CoordinateAxis;
import org.opensim.modeling.DataTable;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSenseUtilities;
import org.opensim.modeling.Rotation;
import org.opensim.modeling.RowVectorView;
import org.opensim.modeling.State;
import org.opensim.modeling.StateVector;
import org.opensim.modeling.StdVectorDouble;
import org.opensim.view.experimentaldata.ModelForExperimentalData;
import org.opensim.modeling.Storage;
import org.opensim.modeling.TimeSeriesTableQuaternion;
import org.opensim.modeling.Vec3;
import org.opensim.utils.ErrorDialog;
import org.opensim.utils.FileUtils;
import org.opensim.view.experimentaldata.AnnotatedMotion;
import org.opensim.view.experimentaldata.MotionObjectOrientation;
import static org.opensim.view.motions.SensorLayoutOptions.Layout.CircleYZ;
import static org.opensim.view.motions.SensorLayoutOptions.Layout.LineX;
import static org.opensim.view.motions.SensorLayoutOptions.Layout.LineY;
import static org.opensim.view.motions.SensorLayoutOptions.Layout.LineZ;
import static org.opensim.view.motions.SensorLayoutOptions.Layout.UseCurrentModelPosition;
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
                SensorLayoutOptions layoutOptions = new SensorLayoutOptions();
                JSensorLayoutPanel layoutPanel = new JSensorLayoutPanel(layoutOptions);
                final DialogDescriptor dlg = new DialogDescriptor(layoutPanel, "Sensor Data Options");
                dlg.setModal(true);
                Dialog wDlg = DialogDisplayer.getDefault().createDialog(dlg);
                wDlg.pack();
                wDlg.setVisible(true);
                Model currentModel = OpenSimDB.getInstance().getCurrentModel();
                if (currentModel == null && layoutOptions.getLayout()==UseCurrentModelPosition){
                    NotifyDescriptor.Message warnDlg =
                          new NotifyDescriptor.Message("No current model is available, assuming Even spacing along X axis:\n", NotifyDescriptor.WARNING_MESSAGE);
                  DialogDisplayer.getDefault().notify(dlg);
                  layoutOptions.setLayout(LineX);
                }
                // if no rotations are specified proceed, otherwise xform
                // rotations are space fixed and are in degrees, convert into a Rotation Matrix to apply
                double[] rotations = layoutOptions.getRotations();
                Rotation sensorToOpenSim = new
                Rotation(BodyOrSpaceType.SpaceRotationSequence,
                    rotations[0], CoordinateAxis.getCoordinateAxis(0),
                    rotations[1], CoordinateAxis.getCoordinateAxis(1),
                    rotations[2], CoordinateAxis.getCoordinateAxis(2));

                TimeSeriesTableQuaternion quatTable = new TimeSeriesTableQuaternion(fileName);
                OpenSenseUtilities.rotateOrientationTable(quatTable, sensorToOpenSim);
                DataTable flattened = quatTable.flatten();
                // replace rows
                StdVectorDouble times = quatTable.getIndependentColumn();
                newStorage.purge();
                for (int i=0; i< flattened.getNumRows(); i++){
                    RowVectorView rowVV = flattened.getRowAtIndex(i);
                    StateVector newRow = new StateVector();
                    Vector newVector = new Vector();
                    newVector.resize(rowVV.size());
                    for (int j=0; j<rowVV.size();j++)
                        newVector.set(j, rowVV.get(j));
                    newRow.setStates(times.get(i), newVector);
                    newStorage.append(newRow);
                }
                AnnotatedMotion amot = new AnnotatedMotion(newStorage);
                amot.setName(new File(fileName).getName());
                ModelForExperimentalData modelForDataImport = null;
                try {
                    modelForDataImport = new ModelForExperimentalData(nextNumber++, amot);
                    setSensorDisplayPositionsInModel(modelForDataImport, layoutOptions, amot);
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

    private void setSensorDisplayPositionsInModel(ModelForExperimentalData modelForDataImport, 
            SensorLayoutOptions layoutModel, AnnotatedMotion amot) {
        // Fish out orientations
        java.util.Vector<String> sensorNames = amot.getSensorNames();
        switch(layoutModel.getLayout()){
            case Origin:
                break;  // Nothing to add
            case LineX:
            case LineY:
            case LineZ:
            case CircleYZ:
                int index=0;
                int numSensors = sensorNames.size();
                for (String sensorName: sensorNames){
                    double pointX = 0.0;
                    double pointY = 0.0;
                    double pointZ = 0.0;
                    /*
                    // add a Body with a free Joint to model
                    Body nextBody = new Body(sensorName, 1, new Vec3(0., 0., 0.), new Inertia(1.));
                    modelForDataImport.addBody(nextBody);
                    FreeJoint nextJoint = new FreeJoint(sensorName, modelForDataImport.getGround(), nextBody);
                    modelForDataImport.addJoint(nextJoint);
                    */
                    if (layoutModel.getLayout() == LineX){
                        pointX = 0.25 * (index + 1);
                    }
                    if (layoutModel.getLayout() == LineY){
                        pointY = 0.25 * (index + 1);
                    }
                    if (layoutModel.getLayout() == LineZ){
                        pointZ = 0.25 * (index + 1);
                    }
                    if (layoutModel.getLayout() == CircleYZ){
                        pointY = sin((double)index / numSensors * PI);
                        pointZ = cos((double)index / numSensors * PI);
                    }
                    ((MotionObjectOrientation)amot.getClassified().elementAt(index)).setPoint(new double[]{pointX, pointY, pointZ});
                    
                    index++; 
                }
                break;
            case UseCurrentModelPosition:
                int sensorIndex=0;
                Model currentModel = OpenSimDB.getInstance().getCurrentModel();
                State currentModelState = OpenSimDB.getInstance().getContext(currentModel).getCurrentStateRef();
                String missingSensorsMessage = "The following sensors (names) could not be matched to model, will be displayed at Origin: ";
                boolean missingSensors = false;
                for (String sensorName: sensorNames){
                    double[] point = new double[]{0.0, 0., 0.};
                    Body sensorBody = null;
                    // find a frame with either sensor name or sensorName - "_imu"
                    if (currentModel.hasComponent("/bodyset/"+sensorName)){
                        sensorBody = currentModel.getBodySet().get(sensorName);
                    }
                    else {
                        if (sensorName.endsWith("_imu")){
                            String shortName = new String(sensorName).replace("_imu", "");
                            if (currentModel.hasComponent("/bodyset/"+shortName)){
                                sensorBody = currentModel.getBodySet().get(shortName);
                            }
                        }
                    }
                    if (sensorBody!=null){
                        Vec3 location = sensorBody.findStationLocationInGround(currentModelState, sensorBody.getMassCenter());
                        for (int i=0; i<3; i++) point[i]=location.get(i);
                        ((MotionObjectOrientation)amot.getClassified().elementAt(sensorIndex)).setPoint(point);

                    }
                    else {
                        missingSensorsMessage.concat(sensorName+" ");
                        missingSensors = true;
                    }
                    sensorIndex++;
                }
                if (missingSensors){
                  NotifyDescriptor.Message warnDlg =
                          new NotifyDescriptor.Message(missingSensorsMessage, NotifyDescriptor.WARNING_MESSAGE);
                  DialogDisplayer.getDefault().notify(warnDlg);
   
                }
                break;
            case AttachCurrentModel:
            default:
                throw new UnsupportedOperationException("Not supported yet.");
        }
    }    
}
