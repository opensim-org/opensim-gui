/*
 * Copyright (c)  2005-2008, Stanford University
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
