/* -------------------------------------------------------------------------- *
 * OpenSim: ExperimentalObjectDisplayShowAction.java                          *
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
package org.opensim.view.experimentaldata;

import org.json.simple.JSONObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.threejs.ModelVisualizationJson;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.motions.MotionControlJPanel;
import org.opensim.view.pub.ViewDB;
import vtk.vtkCaptionActor2D;

public final class ExperimentalObjectDisplayShowAction extends CallableSystemAction {
    
    public void performAction() {
        // Get experimental data object from selected nodes and invoke ".hide on them"
      Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
      for(int i=0; i < selected.length; i++){
         if(selected[i] instanceof ExperimentalDataNode) {
             ExperimentalDataNode node= (ExperimentalDataNode) selected[i];
             ExperimentalDataObject obj=node.getDataObject();
             // Tell MotionDisplayer to hide it
             ModelVisualizationJson modelVis = ViewDB.getInstance().getModelVisualizationJson(node.getModelForNode());
             JSONObject command = modelVis.createSetVisibilityCommandForUUID(true, obj.getDataObjectUUID());
             ViewDB.getInstance().sendVisualizerCommand(command);
             obj.setDisplayed(true);
         }
      }
      MotionControlJPanel.getInstance().getMasterMotion().applyTime();
    }
    
    public String getName() {
        return NbBundle.getMessage(ExperimentalObjectDisplayHideAction.class, "CTL_ExperimentalObjectDisplayShowAction");
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
        // Should we call super.isEnabled() too?
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        for(int i=0; i < selected.length; i++){
            if(selected[i] instanceof ExperimentalDataNode) {
                ExperimentalDataNode node= (ExperimentalDataNode) selected[i];
                ExperimentalDataObject obj=node.getDataObject();
                if (!(obj.isDisplayed())) return true;
            }
        }
        return false;
    }
    
}
