/* -------------------------------------------------------------------------- *
 * OpenSim: MotionCustomColorByAction.java                                    *
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

package org.opensim.view.motions;

import java.io.IOException;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.modeling.Model;
import org.opensim.modeling.Storage;
import org.opensim.utils.FileUtils;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.MuscleColorByActivationStorage;
import org.opensim.view.MuscleColoringFunction;
import org.opensim.view.pub.OpenSimDB;

public final class MotionCustomColorByAction extends CallableSystemAction {

   public void performAction() {
      Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
      if (selected.length == 1 && (selected[0] instanceof OneMotionNode)) {
         OneMotionNode node = (OneMotionNode)selected[0];
         String fileName = FileUtils.getInstance().browseForFilename(".sto", "Storage file", ExplorerTopComponent.findInstance());
         if (fileName != null) {
            Storage motion = node.getMotion();
            Storage storage = null;
            try {
               if (fileName.toLowerCase().endsWith(".sto"))
                storage = new Storage(fileName);
               
            } catch (IOException ex) {
               ex.printStackTrace();
               DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message("Could not read motion file " + fileName));
               return;
            }
            Model mdl = node.getModelForNode();
            MotionDisplayer motionDisplayer = MotionsDB.getInstance().getDisplayerForMotion(motion);
            MuscleColoringFunction mcbya = new MuscleColorByActivationStorage(
                    OpenSimDB.getInstance().getContext(mdl), storage);
            motionDisplayer.setMuscleColoringFunction(mcbya);
         }
      }
   }

   public String getName() {
      return NbBundle.getMessage(MotionCustomColorByAction.class, "CTL_MotionCustomColorByAction");
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
      Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
      for(int i=0; i<selected.length; i++) if(!(selected[i] instanceof OneMotionNode)) return false; // one of the nodes is not a OneMotionNode
      return (selected.length==1);
   }

}
