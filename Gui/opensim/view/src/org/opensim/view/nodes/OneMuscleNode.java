/* -------------------------------------------------------------------------- *
 * OpenSim: OneMuscleNode.java                                                *
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
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.opensim.modeling.Marker;
import org.opensim.modeling.Muscle;
import org.opensim.modeling.OpenSimObject;
import org.opensim.view.editors.BodyNameEditor;
import org.opensim.view.nodes.OpenSimObjectNode.displayOption;

/**
 *
 * @author Ayman Habib
 */
public class OneMuscleNode extends OneActuatorNode {
    
    private static ResourceBundle bundle = NbBundle.getBundle(OneMuscleNode.class);
    /** Creates a new instance of OneMuscleNode */
    public OneMuscleNode(Muscle actuator) {
        super(actuator);
        setShortDescription(bundle.getString("HINT_MuscleNode"));
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
   public Image getOpenedIcon(int i) {
        return getIcon(i);
    }
    /** override createSheet to remove optimal_force property
    * 
    * @return 
    */
    @Override
    public Sheet createSheet() {
        Sheet sheet;

        sheet = super.createSheet();
        Sheet.Set set = sheet.get(Sheet.PROPERTIES);
        set.remove("optimal_force");
        return sheet;
    }
    public Action[] getActions(boolean b) {
        Action[] superActions = (Action[]) super.getActions(b);        
        // Arrays are fixed size, onvert to a List
        
        List<Action> actions = java.util.Arrays.asList(superActions);
        // Create new Array of proper size
        Action[] retActions = new Action[actions.size()+1];
        actions.toArray(retActions);
        try {
            // append new command to the end of the list of actions
            retActions[actions.size()] = (GeometryPathTogglePointsAction) GeometryPathTogglePointsAction.findObject(
                     (Class)Class.forName("org.opensim.view.nodes.GeometryPathTogglePointsAction"), true);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return retActions;
    }
    // Default action by double click now toggles pathpoints
    @Override
    public Action getPreferredAction() {
         Action[] actions = getActions(false);
        return actions[actions.length-1];
    }
}
