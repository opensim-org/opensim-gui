/* -------------------------------------------------------------------------- *
 * OpenSim: DisablablModelComponentNode.java                                  *
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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.view.nodes;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import org.opensim.modeling.AbstractProperty;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.PropertyHelper;

/**
 *
 * @author Ayman
 */
public abstract class DisablablModelComponentNode extends OneModelComponentNode implements DisableableObject {
    protected boolean enabled = true;

    public DisablablModelComponentNode(OpenSimObject obj) {
        super(obj);
        updateEnabledFlag();
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        //OpenSimDB.getInstance().disableConstraint(getOpenSimObject(), enabled);
        this.enabled = enabled;
        if (!enabled) {
            setIconBaseWithExtension("/org/opensim/view/nodes/icons/disabledNode.png");
        } 
    }

    private void updateEnabledFlag() {
        OpenSimObject c = getOpenSimObject();
        AbstractProperty ap = c.getPropertyByName(getDisablePropertyName());
        enabled = PropertyHelper.getValueBool(ap);
    }

    @Override
    public void refreshNode() {
        super.refreshNode();
        updateEnabledFlag();
        setEnabled(enabled);
    }

    @Override
    // return diabled icon if enabled is true else null
    public Image getIcon(int i) {
        URL imageURL;
        if (!enabled){
            imageURL = this.getClass().getResource("icons/disabledNode.png");
        
            if (imageURL != null) { 
                return new ImageIcon(imageURL, "Controller").getImage();
            } else {
                return null;
            }
        }
        return null;
    }

    @Override
    public void updateSelfFromObject() {
        super.updateSelfFromObject();
        refreshNode();
    }
    
}
