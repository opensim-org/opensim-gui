/* -------------------------------------------------------------------------- *
 * OpenSim: OtherComponentsNode.java                                          *
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

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import java.util.ResourceBundle;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.opensim.modeling.ComponentSet;
import org.opensim.modeling.ModelComponent;


/**
 * Node class to wrap Model's collection of misc. components
 */
public class OtherComponentsNode extends OpenSimObjectSetNode {
    private static ResourceBundle bundle = NbBundle.getBundle(OtherComponentsNode.class);

    public OtherComponentsNode(ComponentSet componentSet) {
        super(componentSet);
        setDisplayName(NbBundle.getMessage(OtherComponentsNode.class, "CTL_ModelComponents"));

        for (int index=0; index < componentSet.getSize(); index++ ){

            ModelComponent component = componentSet.get(index);
            Children children = getChildren();

            OneOtherModelComponentNode node = new OneOtherModelComponentNode(component);
            Node[] arrNodes = new Node[1];
            arrNodes[0] = node;
            children.add(arrNodes);
        }
        if (getChildren().getNodesCount()==0) setChildren(Children.LEAF);
      //addDisplayOption(displayOption.Isolatable);
      //addDisplayOption(displayOption.Showable);
    }
   
    /**
     * Display name 
     */
    public String getHtmlDisplayName() {
        return "Other Components";
    }

} // class MarkersNode
