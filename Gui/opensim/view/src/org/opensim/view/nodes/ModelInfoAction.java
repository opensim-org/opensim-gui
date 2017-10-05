/* -------------------------------------------------------------------------- *
 * OpenSim: ModelInfoAction.java                                              *
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

import javax.swing.JButton;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.modeling.Model;
import org.opensim.view.ModelInfoJPanel;


public final class ModelInfoAction extends CallableSystemAction {
    
    public void performAction() {
        ModelInfoJPanel infoPanel=new ModelInfoJPanel();
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        // Action shouldn't be available otherwise'
        OneModelNode modelNode = (OneModelNode) selected[0];
        Model mdl = modelNode.getModel();
        infoPanel.setModelName(mdl.getName());
        infoPanel.setModelFile(mdl.getInputFileName());
        infoPanel.setDynamicsEngineName(mdl.getSimbodyEngine().getConcreteClassName());
        infoPanel.setAuthors(
                mdl.getCredits());
        infoPanel.setReferences(mdl.getPublications());
        DialogDescriptor dlg = new DialogDescriptor(infoPanel, "Model Info.");
        dlg.setOptions(new Object[]{new JButton("Close")});
        dlg.setClosingOptions(null);
        DialogDisplayer.getDefault().notify(dlg);
    }
    
    public String getName() {
        return NbBundle.getMessage(ModelInfoAction.class, "CTL_ModelInfoAction");
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
      return selected.length==1;
    }
}
