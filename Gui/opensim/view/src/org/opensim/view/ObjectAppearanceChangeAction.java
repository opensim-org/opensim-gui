/* -------------------------------------------------------------------------- *
 * OpenSim: ObjectAppearanceChangeAction.java                                 *
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
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.view;

import java.util.Vector;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.opensim.view.nodes.OneComponentNode;
import org.openide.util.HelpCtx;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.modeling.OpenSimObject;
import org.opensim.view.nodes.OpenSimNode;
/**
 *
 * @author Ayman
 */
public abstract class ObjectAppearanceChangeAction extends CallableSystemAction {
    
    protected Vector<OneComponentNode> collectAffectedComponentNodes() {
        Vector<OneComponentNode> objects = new Vector<OneComponentNode>();
        Vector<OpenSimObject> opensimObjects = new Vector<OpenSimObject>();
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        for (int i = 0; i < selected.length; i++) {
            if (!selected[i].isLeaf()){
                Children ch = selected[i].getChildren();
                for (int chNum = 0; chNum < ch.getNodesCount(); chNum++) {
                    collectDescendentNodes((OpenSimNode) ch.getNodeAt(chNum), objects, opensimObjects);
                }
            }
            if (selected[i] instanceof OneComponentNode) {
                if (!(opensimObjects.contains(((OneComponentNode)selected[i]).getOpenSimObject()))){
                    objects.add((OneComponentNode)selected[i]);
                    opensimObjects.add(((OneComponentNode)selected[i]).getOpenSimObject());
                }
            } 
        }
        return objects;
    }

    // node could be a Group or a list of objects not backed by OpenSim objects
    protected void collectDescendentNodes(OpenSimNode node, Vector<OneComponentNode> descendents, Vector<OpenSimObject> osimObjects) {
        
        if (node.isLeaf() && node instanceof OneComponentNode){
            if(!(osimObjects.contains(((OneComponentNode)node).getOpenSimObject()))){
                descendents.add((OneComponentNode) node);
                osimObjects.add(((OneComponentNode)node).getOpenSimObject());
                return;
            }
        }
        Children ch = node.getChildren();
        // process children
        for (Node childNode : ch.getNodes()) {
            if (!childNode.isLeaf()){
                collectDescendentNodes((OpenSimNode) childNode, descendents, osimObjects);
            }
            else if (childNode instanceof OneComponentNode) {
                if(!(osimObjects.contains(((OneComponentNode)childNode).getOpenSimObject()))){
                    descendents.add((OneComponentNode) childNode);
                    osimObjects.add(((OneComponentNode)childNode).getOpenSimObject());
                }
            }
         }
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    protected boolean asynchronous() {
        return false;
    }
    
}
