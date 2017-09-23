/* -------------------------------------------------------------------------- *
 * OpenSim: OneModelComponentNode.java                                        *
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

package org.opensim.view.nodes;

import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.opensim.modeling.Geometry;
import org.opensim.modeling.ModelComponent;
import org.opensim.modeling.OpenSimObject;
import org.opensim.view.ObjectAddGeometryMenuAction;

/**
 *
 * @author Ayman
 */
public class OneModelComponentNode extends OneComponentNode {
    private ModelComponent modelComp;
    public OneModelComponentNode(OpenSimObject obj) {
        super(obj);
        modelComp = ModelComponent.safeDownCast(obj);
    }

    @Override
    public Node cloneNode() {
        return new OneModelComponentNode(getOpenSimObject());
    }

    public Action[] getActions(boolean b) {
        Action[] superActions = (Action[]) super.getActions(b);
        
        // Arrays are fixed size, convert to a List
        /*
        List<Action> actions = Arrays.asList(superActions);
        // Create new Array of proper size
        Action[] retActions = new Action[actions.size() + 1];
        actions.toArray(retActions);
        try {
            // append new command to the end of the list of actions
            retActions[actions.size()] = (ObjectAddGeometryMenuAction) ObjectAddGeometryMenuAction.findObject((Class) Class.forName("org.opensim.view.ObjectAddGeometryMenuAction"), true);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }*/
        return superActions;
    }

    /**
     * @return the modelComp
     */
    public ModelComponent getModelComp() {
        return modelComp;
    }
    
}
