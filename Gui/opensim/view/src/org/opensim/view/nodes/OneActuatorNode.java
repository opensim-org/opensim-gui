/* -------------------------------------------------------------------------- *
 * OpenSim: OneActuatorNode.java                                              *
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
 * OneActuatorNode
 * Author(s): Ayman Habib
 */
package org.opensim.view.nodes;

import java.awt.Image;
import java.net.URL;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import org.openide.util.NbBundle;
import org.openide.nodes.Children;
import org.opensim.modeling.OpenSimObject;

/**
 *
 * @author Ayman Habib
 */
public class OneActuatorNode extends OneForceNode{
    
    private static ResourceBundle bundle = NbBundle.getBundle(OneActuatorNode.class);
    /**
    * Creates a new instance of OneActuatorNode
    */
    public OneActuatorNode(OpenSimObject actuator) {
        super(actuator);
        setShortDescription(bundle.getString("HINT_ActuatorNode"));

    }
    public Image getIcon(int i) {
        URL imageURL;
        if (!enabled)
            imageURL = this.getClass().getResource("icons/disabledNode.png");
        else
            imageURL = this.getClass().getResource("icons/muscleNode.png");
        if (imageURL != null) {
            return new ImageIcon(imageURL, "Actuator").getImage();
        } else {
            return null;
        }
    }
}
