/* -------------------------------------------------------------------------- *
 * OpenSim: FileLoadMotionMenuAction.java                                     *
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
/*
 * FileLoadMotionAction.java
 *
 * Created on April 4, 2007, 8:35 AM
 *
 */
package org.opensim.view.motions;

import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.utils.FileUtils;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.nodes.OneModelNode;
import org.opensim.view.pub.OpenSimDB;

public final class FileLoadMotionMenuAction extends CallableSystemAction {
   
   public void performAction() {
      // TODO implement action body
      // Browse for a SIMM motion file
        String fileName = FileUtils.getInstance().browseForFilename(".mot,.sto", "Motion or storage file");
        if (fileName != null){
           // Load file and associate it to a model
           // File is loaded into a SimmMotionData object first then associated with a particular
           // model. This's done in OpenSimDB so that created SimmMotionData is not garbage collected early.
           // also because OpenSimDB has access to all models and as such can decide if it makes sense 
           // to associate the loaded motion with a prticular model.
            loadMotion(fileName);
        }
        
   }
   
   public String getName() {
      return NbBundle.getMessage(FileLoadMotionMenuAction.class, "CTL_FileLoadMotionAction");
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
   
   public boolean isEnabled() {
       return (OpenSimDB.getInstance().getCurrentModel()!=null);
   }

    public void loadMotion(String fileName) {
           MotionsDB.getInstance().loadMotionFile(fileName, true);
    }
   
   
}
