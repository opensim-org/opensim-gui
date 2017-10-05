/* -------------------------------------------------------------------------- *
 * OpenSim: AllForcesNode.java                                                *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib, Jeff Reinbolt                                      *
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
 * AllForcesNode
 * Author(s): Ayman Habib & Jeff Reinbolt
 */
package org.opensim.view.nodes;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import java.util.ResourceBundle;
import javax.swing.Action;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.opensim.modeling.ForceSet;

/**
 *
 * @author Ayman Habib & Jeff Reinbolt
 *
 * Top level Actuators node in Navigator view
 */
public class AllForcesNode extends OpenSimObjectSetNode {

    private static ResourceBundle bundle = NbBundle.getBundle(AllForcesNode.class);

    /**
     * Creates a new instance of AllForcesNode
     */
    public AllForcesNode(ForceSet as) {
        super(as);
        setDisplayName(NbBundle.getMessage(AllForcesNode.class, "CTL_AllForces"));
        /*for (int i=0; i<as.getSize(); i++) {
        System.out.println(as.get(i).getType());
        }*/
        getChildren().add(new Node[]{new MusclesNode(as)});
        getChildren().add(new Node[]{new ActuatorsNode(as)});
        getChildren().add(new Node[]{new ContactForcesNode(as)});
        getChildren().add(new Node[]{new OtherForcesNode(as)});
        //getChildren().add(new Node[] {new ForcesNode(as)});
        //getChildren().add(new Node[] {new TorquesNode(as)});
        //getChildren().add(new Node[] {new GeneralizedForcesNode(as)});
    }

    /**
     * Display name
     */
    public String getHtmlDisplayName() {

        return NbBundle.getMessage(AllForcesNode.class, "CTL_AllForces");
    }

    public Image getIcon(int i) {
        URL imageURL = null;
        try {
            imageURL = Class.forName("org.opensim.view.nodes.OpenSimNode").getResource("/org/opensim/view/nodes/icons/actuatorsNode.png");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        if (imageURL != null) {
            return new ImageIcon(imageURL, "").getImage();
        } else {
            return null;
        }
    }

    public Image getOpenedIcon(int i) {
        URL imageURL = null;
        try {
            imageURL = Class.forName("org.opensim.view.nodes.OpenSimNode").getResource("/org/opensim/view/nodes/icons/actuatorsNode.png");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        if (imageURL != null) {
            return new ImageIcon(imageURL, "").getImage();
        } else {
            return null;
        }
    }
}
