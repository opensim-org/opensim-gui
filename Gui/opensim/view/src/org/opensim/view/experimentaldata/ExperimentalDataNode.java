/* -------------------------------------------------------------------------- *
 * OpenSim: ExperimentalDataNode.java                                         *
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
 * ExperimentalDataNode.java
 *
 * Created on March 11, 2009, 2:25 PM
 *
 *
 *
 */

package org.opensim.view.experimentaldata;

import java.awt.Image;
import java.net.URL;
import java.util.ResourceBundle;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.opensim.view.actions.ObjectDisplaySelectAction;
import org.opensim.view.nodes.OpenSimNode;

/**
 *
 * @author ayman
 */
public class ExperimentalDataNode extends OpenSimNode{
    
    /** Creates a new instance of ExperimentalDataNode */
    public ExperimentalDataNode() {
    }

    protected static ResourceBundle bundle = NbBundle.getBundle(ExperimentalForceNode.class);

    protected AnnotatedMotion dMotion;
    protected ExperimentalDataObject dataObject;
    /**
     * Icon for the node, same as OpenSimNode
     **/
    public Image getIcon(int i) {
        URL imageURL = null;
        try {
            imageURL = Class.forName("org.opensim.view.nodes.OpenSimNode").getResource("/org/opensim/view/nodes/icons/motionsNode.png");
        }  catch (ClassNotFoundException ex) {
           ex.printStackTrace();
        }
        if (imageURL != null) {
            return new ImageIcon(imageURL, "").getImage();
        }  else {
           return null;
        }
    }

    
    public Image getOpenedIcon(int i) {
        URL imageURL = null;
        try {
            imageURL = Class.forName("org.opensim.view.nodes.OpenSimNode").getResource("/org/opensim/view/nodes/icons/motionsNode.png");
        }  catch (ClassNotFoundException ex) {
           ex.printStackTrace();
        }
        if (imageURL != null) {
            return new ImageIcon(imageURL, "").getImage();
        }  else {
           return null;
        }
    }
    public Action[] getActions(boolean b) {
        Action[] nodeActions=null;
            try {
                nodeActions = new Action[] {
                           (ExperimentalObjectDisplayShowAction) ExperimentalObjectDisplayShowAction.findObject(
                           (Class<org.opensim.view.experimentaldata.ExperimentalObjectDisplayShowAction>)Class.forName("org.opensim.view.experimentaldata.ExperimentalObjectDisplayShowAction"), 
                                   true),
                           (ExperimentalObjectDisplayShowOnlyAction) ExperimentalObjectDisplayShowOnlyAction.findObject(
                           (Class<org.opensim.view.experimentaldata.ExperimentalObjectDisplayShowOnlyAction>)Class.forName("org.opensim.view.experimentaldata.ExperimentalObjectDisplayShowOnlyAction"), 
                                   true),
                           (ExperimentalObjectDisplayHideAction) ExperimentalObjectDisplayHideAction.findObject(
                           (Class<org.opensim.view.experimentaldata.ExperimentalObjectDisplayHideAction>)Class.forName("org.opensim.view.experimentaldata.ExperimentalObjectDisplayHideAction"), 
                                   true),
                };
                
                
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        return nodeActions;
    }

    public ExperimentalDataObject getDataObject() {
        return dataObject;
    }

    public void setDataObject(ExperimentalDataObject dataObject) {
        this.dataObject = dataObject;
    }

    public AnnotatedMotion getDMotion() {
        return dMotion;
    }
    @Override
    public Action getPreferredAction() {
        try {
            return ((ObjectDisplaySelectAction) ObjectDisplaySelectAction.findObject( (Class)Class.forName("org.opensim.view.actions.ObjectDisplaySelectAction"), true));
        } catch (ClassNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }
}
