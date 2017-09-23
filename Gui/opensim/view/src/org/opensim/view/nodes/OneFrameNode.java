/* -------------------------------------------------------------------------- *
 * OpenSim: OneFrameNode.java                                                 *
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

import java.awt.Image;
import java.net.URL;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.opensim.modeling.Frame;
import org.opensim.modeling.Geometry;

/**
 *
 * @author Ayman
 */
class OneFrameNode extends OneModelComponentNode {
   private static ResourceBundle bundle = NbBundle.getBundle(OneFrameNode.class);
   Frame frame;
    public OneFrameNode(Frame comp) {
        super(comp);
        frame = comp;
        setShortDescription(bundle.getString("HINT_FrameNode"));
        createGeometryNodes();
    }
   
    protected final void createGeometryNodes() {
        
        int geomSize = frame.getPropertyByName("attached_geometry").size();
        // Create node for geometry
        Children children = getChildren();
        for (int g = 0; g < geomSize; g++) {
            Geometry oneG = frame.get_attached_geometry(g);
            OneGeometryNode node = new OneGeometryNode(oneG);
            Node[] arrNodes = new Node[1];
            arrNodes[0] = node;
            children.add(arrNodes);
        }
    }
    
   @Override
    public Node cloneNode() {
        return new OneFrameNode((Frame)getOpenSimObject());
    }
    /**
     * Icon for the body node 
     **/
   @Override
    public Image getIcon(int i) {
        URL imageURL = this.getClass().getResource("/org/opensim/view/nodes/icons/body.png");
        if (imageURL != null) {
            return new ImageIcon(imageURL, "Frame").getImage();
        } else {
            return null;
        }
    }

   @Override
    public Image getOpenedIcon(int i) {
        return getIcon(i);
    }
    
}
