/* -------------------------------------------------------------------------- *
 * OpenSim: OneJointNode.java                                                 *
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
 *
 * OneMuscleNode
 * Author(s): Ayman Habib
 */
package org.opensim.view.nodes;

import java.awt.Image;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.opensim.modeling.Body;
import org.opensim.modeling.Component;
import org.opensim.modeling.ComponentIterator;
import org.opensim.modeling.ComponentsList;
import org.opensim.modeling.CustomJoint;
import org.opensim.modeling.Frame;
import org.opensim.modeling.Joint;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.SpatialTransform;
import org.opensim.modeling.TransformAxis;

/**
 *
 * @author Ayman Habib
 */
public class OneJointNode extends OneModelComponentNode {

    /**
     * Creates a new instance of OneMuscleNode
     */
    public OneJointNode(OpenSimObject jnt) {
        super(jnt);
        comp = Component.safeDownCast(jnt);
        Joint joint = Joint.safeDownCast(jnt);
        
        CustomJoint cj = CustomJoint.safeDownCast(joint);
        if (cj != null) {
            SpatialTransform spt = cj.getSpatialTransform();
            for (int i = 0; i < 6; i++) {
                TransformAxis ta = spt.getTransformAxis(i);
                getChildren().add(new Node[]{new OneDofNode(ta)});
            }
        } 
        OpenSimNodeHelper.createFrameNodes(getChildren(), comp);
        if (getChildren().getNodesCount()==0) {
            setChildren(Children.LEAF);
        }
    }

    public Image getIcon(int i) {
        URL imageURL = this.getClass().getResource("icons/jointNode.png");
        if (imageURL != null) {
            return new ImageIcon(imageURL, "Joint").getImage();
        } else {
            return null;
        }
    }

    public Action[] getActions(boolean b) {
        Action[] superActions = (Action[]) super.getActions(b);

        // Arrays are fixed size, onvert to a List
        
         List<Action> actions = Arrays.asList(superActions);
         // Create new Array of proper size
         Action[] retActions = new Action[actions.size() + 2];
         actions.toArray(retActions);
         try {
         // append new command to the end of the list of actions
         retActions[actions.size()] = (JointToggleParentFrameAction) JointToggleParentFrameAction.findObject((Class) Class.forName("org.opensim.view.nodes.JointToggleParentFrameAction"), true);
         retActions[actions.size()+1] = (JointToggleChildFrameAction) JointToggleChildFrameAction.findObject((Class) Class.forName("org.opensim.view.nodes.JointToggleChildFrameAction"), true);
         } catch (ClassNotFoundException ex) {
         ex.printStackTrace();
         }
        return retActions;
    }
}
