/* -------------------------------------------------------------------------- *
 * OpenSim: DeleteProbeAction.java                                            *
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
 * DeleteProbeAction.java
 *
 * Created on June 9, 2008, 3:42 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.opensim.view.nodes;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.util.Vector;
import javax.swing.AbstractAction;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.opensim.modeling.ArrayStr;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.Probe;
import org.opensim.view.ExplorerTopComponent;

/**
 *
 * @author Ayman Habib
 */
public class DeleteProbeAction extends AbstractAction {

   /** Creates a new instance of DeleteProbeAction */
   public DeleteProbeAction() {
      super(NbBundle.getMessage(DeleteProbeAction.class, "CTL_DeleteProbeAction"));
   }

   public void actionPerformed(ActionEvent evt) {
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        // One ProbesNode must have been selected otherwise bail
        for(int i=selected.length-1; i>= 0; i--){
            if (selected[i] instanceof OneProbeNode){
                OneProbeNode probeNode = (OneProbeNode)selected[i];
                Model model = probeNode.getModelForNode();
                Probe toDelete = Probe.safeDownCast(probeNode.getOpenSimObject());
                model.removeProbe(toDelete);
                probeNode.getParentNode().getChildren().remove(new Node[]{probeNode});
                PropertyEditorAdaptor pea = new PropertyEditorAdaptor(model);
                pea.handleModelChange();
            }
        }
        
    }

}
