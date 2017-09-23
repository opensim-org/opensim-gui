/* -------------------------------------------------------------------------- *
 * OpenSim: ObjectPropertiesEditor.java                                       *
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
