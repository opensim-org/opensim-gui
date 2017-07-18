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
import org.opensim.view.base.ExecOpenSimProcess;
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
                        DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(ex.getMessage()));
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
