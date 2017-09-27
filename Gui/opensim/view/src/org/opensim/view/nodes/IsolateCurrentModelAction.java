/* -------------------------------------------------------------------------- *
 * OpenSim: IsolateCurrentModelAction.java                                    *
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
package org.opensim.view.nodes;

import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.modeling.Model;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.pub.ViewDB;

public final class IsolateCurrentModelAction extends CallableSystemAction {
   
    public boolean isEnabled() {
       // The "show" option is enabled unless every selected node is shown.
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        return  ((selected.length == 1) &&
           (selected[0] instanceof OneModelNode));
    }
    
   public void performAction() {
      // TODO implement action body
      Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
      // Action shouldn't be available otherwise
      if ((selected.length != 1) ||
         (!(selected[0] instanceof OneModelNode))) return;
      OneModelNode modelNode = (OneModelNode) selected[0];
      Model mdl = modelNode.getModel();
      ViewDB.getInstance().isolateModel(mdl);
   }
  
   public String getName() {
      return NbBundle.getMessage(IsolateCurrentModelAction.class, "CTL_IsolateCurrentModelAction");
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
