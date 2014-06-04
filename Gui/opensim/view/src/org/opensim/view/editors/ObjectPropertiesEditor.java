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
package org.opensim.view.editors;

import java.io.File;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.modeling.OpenSimObject;
import org.opensim.utils.FileUtils;

public final class ObjectPropertiesEditor extends CallableSystemAction {
    
    public void performAction() {
      String fileName = FileUtils.getInstance().browseForFilename(".osim,.xml", "OpenSim model or XML file");
      if(fileName!=null) {
         OpenSimObject obj = OpenSimObject.makeObjectFromFile(fileName);
         if (obj != null){
            ObjectEditDialogMaker dlg = new ObjectEditDialogMaker(obj, true, "Save...");
            boolean confirm = dlg.process();
            if (confirm){
                // get Extension of original file
                File origFile =  new File(fileName);
                String fullName =origFile.getAbsolutePath();
                String folderName = origFile.getParent();
                String ext = fullName.substring(fullName.lastIndexOf('.'));
                //browseForFilenameToSave(String extensions, String description, boolean promptIfReplacing, String currentFilename)
                String newFilename = FileUtils.getInstance().browseForFilenameToSave(ext, "Save to file name", true, fileName);
                obj.print(newFilename);
            }
         } else {
            DialogDisplayer.getDefault().notify(
               new NotifyDescriptor.Message("Could not construct an object from the specified file."));
         }
      }
    }
    
    public String getName() {
        return NbBundle.getMessage(ObjectPropertiesEditor.class, "CTL_ObjectPropertiesEditor");
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
