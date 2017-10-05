/* -------------------------------------------------------------------------- *
 * OpenSim: FileCloseAllAction.java                                           *
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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.view.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.nodes.OneModelNode;
import org.opensim.view.nodes.ModelCloseSelectedAction;

@ActionID(category = "File",
id = "org.opensim.view.actions.FileCloseAllAction")
@ActionRegistration(displayName = "#CTL_FileCloseAllAction")
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 1381)
})
@Messages("CTL_FileCloseAllAction=Close All")
public final class FileCloseAllAction implements ActionListener {

    public void actionPerformed(ActionEvent e) {
        // TODO implement action body
         Node root = ExplorerTopComponent.findInstance().getExplorerManager().getRootContext();
         if (root == null) return; // No models
         Children ch=root.getChildren();
         int numChildren = ch.getNodesCount();
         if (numChildren==0) return;
         ArrayList<Node> modelsToClose = new ArrayList<Node>();
         for(int i=0; i < numChildren; i++){
             if (ch.getNodeAt(i) instanceof OneModelNode){
                OneModelNode modelNode = (OneModelNode) ch.getNodeAt(i);
                modelsToClose.add(modelNode);
            }
         }
         if (modelsToClose.size()==0) return;
         Node[] modelNodes = new Node[modelsToClose.size()];
         for(int j=0; j<modelsToClose.size(); j++){
             modelNodes[j] = modelsToClose.get(j);
         }
        try {
            ExplorerTopComponent.findInstance().getExplorerManager().setSelectedNodes(modelNodes);
            ModelCloseSelectedAction closeSelectedAction = (ModelCloseSelectedAction) ModelCloseSelectedAction.findObject(
                     (Class)Class.forName("org.opensim.view.nodes.ModelCloseSelectedAction"), true);
            closeSelectedAction.performAction();
        } catch (ClassNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (PropertyVetoException ex) {
            Exceptions.printStackTrace(ex);
        }
   }
}
