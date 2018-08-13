/* -------------------------------------------------------------------------- *
 * OpenSim: FileOpenOsimModelAction.java                                      *
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

package org.opensim.view;

import java.io.File;
import java.io.IOException;
import javax.swing.SwingUtilities;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.StatusDisplayer;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.logger.OpenSimLogger;
import org.opensim.modeling.Model;
import org.opensim.utils.ErrorDialog;
import org.opensim.utils.FileUtils;
import org.opensim.view.actions.MRUFilesOptions;
import org.opensim.view.pub.OpenSimDB;

public class FileOpenOsimModelAction extends CallableSystemAction {
    
    
    public void performAction() {
        // TODO implement action body
        // Browse for model file
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                String fileName = FileUtils.getInstance().browseForFilename(FileUtils.OpenSimModelFileFilter);
                if (fileName != null) {
                    ProgressHandle progressHandle = ProgressHandleFactory.createHandle("Loading model file " + fileName + "...");
                    try {
                        progressHandle.start();
                        loadModel(fileName);
                        progressHandle.finish();
                        StatusDisplayer.getDefault().setStatusText("");
                    } catch (IOException ex) {
                        progressHandle.finish();
                        ErrorDialog.displayIOExceptionDialog("OpenSim Model Loading Error",
                                "Could not construct a model from " + fileName + ". Possible reasons: syntax error or unsupported format.", ex);

                    }
                    finally{
                        progressHandle.finish();
                    }

                }
            }
        });
    }

     /**
     * A wrapper around loadModel that handles a Model rather than a filename
     * setup was invoked already on the model
     */
    public boolean loadModel(final Model aModel) throws IOException {
        return loadModel(aModel , false);
    }
    
    public boolean loadModel(final Model aModel, boolean loadInForground) throws IOException {
        boolean retValue = false;
        if (OpenSimDB.getInstance().hasModel(aModel)){   // If model is already loaded, complain and return.
            OpenSimLogger.logMessage("Model is already loaded\n", OpenSimLogger.ERROR);
            return retValue;            
           
        }
        // Make the window
        if (loadInForground){
            OpenSimDB.getInstance().addModel(aModel);
            return true;
        }
        else 
            SwingUtilities.invokeLater(new Runnable(){
            public void run() {
                    try {
                        OpenSimDB.getInstance().addModel(aModel);
                    } catch (IOException ex) {
                        ErrorDialog.displayExceptionDialog(ex);
                    }
            }});
        retValue = true;
        return retValue;        
    }
    /**
     * This is the function that does the real work of loading a model file into the GUI
     * @param fileName is the absolute path to the file to be used.
     * @returns true on success else failure
     */
      public boolean loadModel(final String fileName) throws IOException {
         MRUFilesOptions opts = MRUFilesOptions.getInstance();
         opts.addFile(new File(fileName).getAbsolutePath());
         return loadModel(fileName, false);
         
     }
     public boolean loadModel(final String fileName, boolean loadInForground) throws IOException {
        boolean retValue = false;
        Model aModel=null;
        aModel= new Model(fileName);
        if (aModel == null){
             OpenSimLogger.logMessage("Failed to construct model from file "+fileName+"\n", OpenSimLogger.ERROR);
            return retValue;
        }
        if (aModel.getValidationLog().length()>0){
            NotifyDescriptor.Message dlg =
                          new NotifyDescriptor.Message("The following Warnings/Errors were encountered while loading the model:\n"+aModel.getValidationLog(), NotifyDescriptor.WARNING_MESSAGE);
                  DialogDisplayer.getDefault().notify(dlg);
        }
        return loadModel(aModel, loadInForground);
    }

    public String getName() {
        return NbBundle.getMessage(FileOpenOsimModelAction.class, "CTL_OpenOsimModel");
    }
    
    protected String iconResource() {
        return null;
    }
    
    public HelpCtx getHelpCtx() {
        return new HelpCtx(FileOpenOsimModelAction.class);
    }
    
    protected boolean asynchronous() {
        return true;
    }
    
    void openModelFile(String string) throws IOException {
        loadModel(string);
    }

}
