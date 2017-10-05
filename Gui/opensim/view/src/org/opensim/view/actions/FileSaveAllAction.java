/* -------------------------------------------------------------------------- *
 * OpenSim: FileSaveAllAction.java                                            *
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

package org.opensim.view.actions;

import java.beans.PropertyVetoException;
import java.util.ArrayList;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.nodes.OneModelNode;
import org.opensim.view.nodes.ModelCloseSelectedAction;
import org.opensim.view.nodes.ModelSaveSelectedAction;
import org.opensim.view.pub.OpenSimDB;

public final class FileSaveAllAction extends CallableSystemAction {

   public void performAction() {
         Node root = ExplorerTopComponent.findInstance().getExplorerManager().getRootContext();
         if (root == null) return; // No models
         Children ch=root.getChildren();
         int numChildren = ch.getNodesCount();
         if (numChildren==0) return;
         ArrayList<Node> modelsToSave = new ArrayList<Node>();
         for(int i=0; i < numChildren; i++){
             if (ch.getNodeAt(i) instanceof OneModelNode){
                OneModelNode modelNode = (OneModelNode) ch.getNodeAt(i);
                modelsToSave.add(modelNode);
            }
         }
         if (modelsToSave.size()==0) return;
         Node[] modelNodes = new Node[modelsToSave.size()];
         for(int j=0; j<modelsToSave.size(); j++){
             modelNodes[j] = modelsToSave.get(j);
         }
        try {
            ExplorerTopComponent.findInstance().getExplorerManager().setSelectedNodes(modelNodes);
            ModelSaveSelectedAction saveSelectedAction = (ModelSaveSelectedAction) ModelCloseSelectedAction.findObject(
                     (Class)Class.forName("org.opensim.view.nodes.ModelSaveSelectedAction"), true);
            saveSelectedAction.performAction();
            ExplorerTopComponent.findInstance().getExplorerManager().setSelectedNodes(new Node[]{});
        } catch (ClassNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (PropertyVetoException ex) {
            Exceptions.printStackTrace(ex);
        }
   }
   
   public String getName() {
      return NbBundle.getMessage(FileSaveAllAction.class, "CTL_FileSaveAllAction");
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
       return OpenSimDB.getInstance().getCurrentModel()!=null;
   }
   
}
