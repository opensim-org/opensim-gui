/* -------------------------------------------------------------------------- *
 * OpenSim: ObjectAddGeometryMenuAction.java                                  *
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

import java.awt.event.ActionEvent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.actions.CallableSystemAction;
import org.openide.util.actions.Presenter;
import org.opensim.view.nodes.OneComponentNode;

public final class ObjectAddGeometryMenuAction extends CallableSystemAction implements Presenter.Popup {
    
    public boolean isEnabled() {
        return true;
    }

    public void performAction() {
    }
    
    public String getName() {
        return "Add Geometry";
    }
        
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }

   public void actionPerformed(ActionEvent e) {
   }

   public JMenuItem getPopupPresenter() {
      JMenu geometryMenu = new JMenu("Add Geometry");
      // Check that selected objects support the operations
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        // If any selected object is hidden (or any selected group is mixed), return false.
        for(int i=0; i < selected.length; i++){
           if (! (selected[i] instanceof OneComponentNode))
              continue;
            OneComponentNode objectNode = (OneComponentNode) selected[i];

        }

      try {      
            geometryMenu.add(new JMenuItem(
                 (ObjectAddFrameAction) ObjectAddFrameAction.findObject(
                 (Class)Class.forName("org.opensim.view.ObjectAddFrameAction"), true)));
            geometryMenu.add(new JMenuItem(
                 (ObjectAddSphereAction) ObjectAddSphereAction.findObject(
                 (Class)Class.forName("org.opensim.view.ObjectAddSphereAction"), true)));
       } catch (ClassNotFoundException ex) {
         ex.printStackTrace();
      }     
         
      return geometryMenu;
   }
    
}
