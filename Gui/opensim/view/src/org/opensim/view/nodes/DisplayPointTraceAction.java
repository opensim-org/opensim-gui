/* -------------------------------------------------------------------------- *
 * OpenSim: DisplayPointTraceAction.java                                      *
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

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.modeling.OpenSimObject;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.experimentaldata.ExperimentalDataVisuals;
import org.opensim.view.experimentaldata.ModelForExperimentalData;
import org.opensim.view.pub.ViewDB;

public final class DisplayPointTraceAction extends CallableSystemAction {

    public boolean isEnabled() {
       // The "show" option is enabled unless every selected node is shown.
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        boolean enabled=true;
        for (Node node:selected){
       }
        return enabled;
    }
    public void performAction() {
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        
        for (Node node:selected){
            ModelForExperimentalData mdl = (ModelForExperimentalData) ((OpenSimObjectNode)node).getModelForNode();
            ExperimentalDataVisuals vis=(ExperimentalDataVisuals) ViewDB.getInstance().getModelVisuals(mdl);
            vis.toggleTraceDisplay(((OpenSimObjectNode)node).getOpenSimObject());
        }
    }

    public String getName() {
        return(NbBundle.getMessage(DisplayPointTraceAction.class, "CTL_DisplayPointTraceAction"));
    }

    public boolean asynchronus() {
        return false;
    }
   protected void initialize() {
      super.initialize();
      // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
      putValue("noIconInMenu", Boolean.TRUE);
   }
   
   public HelpCtx getHelpCtx() {
      return HelpCtx.DEFAULT_HELP;
   }
   

}
