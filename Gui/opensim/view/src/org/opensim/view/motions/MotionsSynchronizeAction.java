/* -------------------------------------------------------------------------- *
 * OpenSim: MotionsSynchronizeAction.java                                     *
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

import java.util.Hashtable;
import java.util.Vector;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.view.ExplorerTopComponent;

public final class MotionsSynchronizeAction extends CallableSystemAction {
    
    public boolean isEnabled() {
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        Hashtable <Node,Boolean> usedMotionNodes = new Hashtable<Node,Boolean>(selected.length);
        for(int i=0; i<selected.length; i++) {
           if(!(selected[i] instanceof OneMotionNode)) return false; // one of the nodes is not a OneMotionNode
           Node parent = selected[i].getParentNode();
           if(usedMotionNodes.containsKey(parent)) return false; // trying to pick multiple motions for the same model
           usedMotionNodes.put(parent, true);
        }
        return (selected.length>1);
    }
    /**
     * Action works by flushing all motions and then adding selected motions one at a time
     */
    public void performAction() {
       Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
       Vector<MotionsDB.ModelMotionPair> motions = new Vector<MotionsDB.ModelMotionPair>(selected.length);
       for(int i=0; i<selected.length; i++) {
            OneMotionNode node =((OneMotionNode)selected[i]);
            motions.add(new MotionsDB.ModelMotionPair(node.getModel(), node.getMotion()));
       }
       MotionsDB.getInstance().setCurrent(motions);
    }

    public String getName() {
        return NbBundle.getMessage(MotionsSynchronizeAction.class, "CTL_MotionsSynchronizeAction");
    }
    
    protected String iconResource() {
        return "org/opensim/view/camera.gif";
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }
    
}
