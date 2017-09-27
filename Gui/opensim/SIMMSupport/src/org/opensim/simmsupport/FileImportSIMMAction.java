/* -------------------------------------------------------------------------- *
 * OpenSim: FileImportSIMMAction.java                                         *
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

import java.io.File;
import java.io.IOException;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.logger.OpenSimLogger;
import org.opensim.view.base.ExecOpenSimProcess;
import org.opensim.view.FileOpenOsimModelAction;

public final class FileImportSIMMAction extends CallableSystemAction {
    
    public void performAction() {
        // TODO implement action body
        SimmToOpenSimOptionsJPanel importPanel = new SimmToOpenSimOptionsJPanel();
        DialogDescriptor dlg = new DialogDescriptor(importPanel, "Import SIMM Model");
        //dlg.setValid(false);
        DialogDisplayer.getDefault().createDialog(dlg).setVisible(true);
        Object userInput = dlg.getValue();
        
        if (((Integer)userInput).compareTo((Integer)DialogDescriptor.OK_OPTION)==0){
            String jntfileName = importPanel.getJointFilename();
            if (jntfileName==null || jntfileName.equalsIgnoreCase("") ){
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message("No valid .jnt file have been specified. Import aborted."));
                return;
            }
            String mslfileName = importPanel.getMslFilename();
            String osimfilename = importPanel.getOsimFilename();
            String markersFileName = importPanel.getMarkersFileName();
            importSIMMModel(jntfileName, mslfileName, osimfilename, markersFileName);
        }
    }
    
    public void importSIMMModel(String jntfileName, String mslfileName, String osimfilename, String markersFileName){

            String command="simmToOpenSim -j \""+jntfileName+"\"";
            if (mslfileName!=null && !mslfileName.equalsIgnoreCase(""))
                command += " -m \""+mslfileName+"\"";
            command += " -g Geometry";
            // simmToOpenSim is assumed in the Path, similar to other dlls we depend on.
            File f = new File(jntfileName);
            File jntFileDir = f.getParentFile();
            if (osimfilename==null || osimfilename.length()==0){
               DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message("No valid OpeSim file have been specified. Import aborted."));
               return;
            }
            String fullOsimFilename = jntFileDir.getAbsolutePath()+File.separator+osimfilename;
            File testExists = new File(fullOsimFilename);
            if (testExists.exists()){
               Object userAnswer = DialogDisplayer.getDefault().notify(
                        new NotifyDescriptor.Confirmation("File "+fullOsimFilename+" already exists, do you want to overwrite?.",NotifyDescriptor.YES_NO_OPTION));
               if(userAnswer==NotifyDescriptor.NO_OPTION) return;
            }
           
            command+=" -x \""+osimfilename+"\"";
            if (markersFileName!=null &&
                    !markersFileName.equalsIgnoreCase("")){
               //-ms markerset_out
               command+=" -ms \""+markersFileName+"\"";
            }
            OpenSimLogger.logMessage("Executing ["+command+"]\n", 0);
            boolean success = ExecOpenSimProcess.execute(command, new String[]{""}, jntFileDir );
            // if file was not generated warn and point to message area
            testExists = new File(fullOsimFilename);
            if (!testExists.exists()){
               DialogDisplayer.getDefault().notify(
                        new NotifyDescriptor.Message("Importing model failed, please check Message window for details."));
               success=false;
            }
            
            if (success){
                try {
                    // Display original model
                    ((FileOpenOsimModelAction) FileOpenOsimModelAction.findObject(
                            (Class)Class.forName("org.opensim.view.FileOpenOsimModelAction"))).loadModel(fullOsimFilename);
                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    DialogDisplayer.getDefault().notify(
                            new NotifyDescriptor.Message("Error opening converted model file "+fullOsimFilename));
                };
            }

    }
    public String getName() {
        return NbBundle.getMessage(FileImportSIMMAction.class, "CTL_FileImportSIMMAction");
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
