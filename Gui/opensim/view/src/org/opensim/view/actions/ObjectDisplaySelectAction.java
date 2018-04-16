/* -------------------------------------------------------------------------- *
 * OpenSim: ObjectDisplaySelectAction.java                                    *
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

import javax.swing.SwingUtilities;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.experimentaldata.ExperimentalDataNode;
import org.opensim.view.nodes.OpenSimObjectNode;
import org.opensim.view.pub.ViewDB;

public final class ObjectDisplaySelectAction extends CallableSystemAction  {

   public ObjectDisplaySelectAction() {
   }

   public String getName() {
      return NbBundle.getMessage(ObjectDisplaySelectAction.class, "CTL_SelectObjectAction");
   }

    @Override
    public void performAction() {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
                // The general scenario is to hide parent then show node and children
                // one exception is if the parent node is a model since hiding it in this case
                // makes it impossible to show selected node. This sitution is handled by overriding the behavior
                // in appropriate nodes.
                for (int i = 0; i < selected.length; i++) {
                    
                    if (selected[i] instanceof OpenSimObjectNode){ 
                        OpenSimObjectNode osNode = (OpenSimObjectNode) selected[i];
                        ViewDB.getInstance().setSelectedObject(osNode.getOpenSimObject());
                    }
                    else if (selected[i] instanceof ExperimentalDataNode){
                        ExperimentalDataNode expNode = (ExperimentalDataNode) selected[i];
                        ViewDB.getInstance().setSelectedObject(expNode.getDataObject());
                    }
                 }
            }
        });
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false; //To change body of generated methods, choose Tools | Templates.
    }

}
