/* -------------------------------------------------------------------------- *
 * OpenSim: FileSaveAsModelAction.java                                        *
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
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.StatusDisplayer;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.modeling.Model;
import org.opensim.utils.FileUtils;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;

public final class FileSaveAsModelAction extends CallableSystemAction {
  
   public static void saveModel(Model model, String fileName) {
      StatusDisplayer.getDefault().setStatusText("Saving model...");
      OpenSimDB.getInstance().saveModel(model, fileName);
      StatusDisplayer.getDefault().setStatusText("Saved model "+model.getName()+" to "+fileName);
   }

   public static boolean saveAsModel(Model model) {
      String fileName = FileUtils.getInstance().browseForFilenameToSave(FileUtils.OpenSimModelFileFilter, true, model.getInputFileName());
      if(fileName!=null) {
         // If no extension was specified, append ".osim""
         String fullFilename = FileUtils.addExtensionIfNeeded(fileName,".osim");
         saveModel(model, fullFilename);
         return true;
      } else return false;
   }

   public void performAction() {
      Model mdl = ViewDB.getInstance().getCurrentModel();
      if (mdl != null) saveAsModel(mdl);
   }
   
   public String getName() {
      return NbBundle.getMessage(FileSaveAsModelAction.class, "CTL_FileSaveAsModelAction");
   }
   
   protected void initialize() {
      super.initialize();
      // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
      putValue("noIconInMenu", Boolean.TRUE);
      //setEnabled(false);
      //ViewDB.getInstance().registerModelCommand(this);
   }
   
   public HelpCtx getHelpCtx() {
      return HelpCtx.DEFAULT_HELP;
   }
   
   protected boolean asynchronous() {
      return false;
   }
   
   public boolean isEnabled() {
      return ViewDB.getInstance().getCurrentModel()!=null;
   }
}
