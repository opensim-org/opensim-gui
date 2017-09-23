/* -------------------------------------------------------------------------- *
 * OpenSim: ObjectDisplayChangeRepAction.java                                 *
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

package org.opensim.view;

import java.util.Vector;
import org.openide.util.HelpCtx;
import org.opensim.view.nodes.OneComponentNode;
import org.opensim.view.nodes.OpenSimObjectNode;
import org.opensim.view.pub.ViewDB;

public class ObjectDisplayChangeRepAction extends ObjectAppearanceChangeAction {
    
    public boolean isEnabled() {
        return true;
    }

    public void performAction() {
       // unimplemneted by design
      throw new UnsupportedOperationException("Method should never be called!");
    }
    /**
     * A variation of performAction that takes the desired representation and applies it to the model
     */
     public void performAction(int newRep, int newShading) {
        ViewDB.getInstance().setApplyAppearanceChange(false);
        Vector<OneComponentNode> nodes = collectAffectedComponentNodes();
        for(int i=0; i < nodes.size(); i++){
            OpenSimObjectNode objectNode = (OpenSimObjectNode) nodes.get(i);
            if (objectNode instanceof ColorableInterface) {
                ((ColorableInterface)objectNode).setDisplayPreference(newRep);
            }
        }
        ViewDB.getInstance().setApplyAppearanceChange(true);
   }
  
    public String getName() {
        return "unused";
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }
    
}
