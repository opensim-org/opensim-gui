/* -------------------------------------------------------------------------- *
 * OpenSim: NewProbeAction.java                                               *
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
 * NewMarkerAction.java
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
public class NewProbeAction extends AbstractAction {

   /** Creates a new instance of NewProbeAction */
   public NewProbeAction() {
      super(NbBundle.getMessage(NewProbeAction.class, "CTL_NewProbeAction"));
   }

   public void actionPerformed(ActionEvent evt) {
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        // One ProbesNode must have been selected otherwise bail
        if (selected.length != 1 || !(selected[0] instanceof ProbesNode)) {
            return;
        }
        ProbesNode probesNode = (ProbesNode) selected[0];
        String newName = probesNode.makeUniqueName("Probe");
        addNewObjectToSet(newName, probesNode);
    }

    protected void addNewObjectToSet(String newName, ProbesNode probesNode) {
        ArrayStr registeredTypes = new ArrayStr();
        Vector<String> validTypes = new Vector<String>();
        OpenSimObject.getRegisteredTypenames(registeredTypes);
        for(int i=0; i< registeredTypes.getSize(); i++){
            String nextType = registeredTypes.getitem(i);
            if (Probe.safeDownCast(OpenSimObject.newInstanceOfType(nextType))!=null){
                //System.out.println("Available type:"+nextType);
                validTypes.add(nextType);
            }
        }
        CreateObjectJPanel createPanel = new CreateObjectJPanel(validTypes, newName);
        DialogDescriptor dd = new DialogDescriptor(createPanel, "Create New Probe");
        Dialog dlg = DialogDisplayer.getDefault().createDialog(dd);
        dlg.setVisible(true);
        Object userInput = dd.getValue();
        if (((Integer)userInput).compareTo((Integer)DialogDescriptor.OK_OPTION)==0){
            // get user specified name and type and create instance, add it to the model
            String userSpecifiedName = createPanel.getUserSpecifiedName();
            String userSelectedType = createPanel.getUserSelectedType();
            Probe newProbe = Probe.safeDownCast(OpenSimObject.newInstanceOfType(userSelectedType));
            newProbe.setName(userSpecifiedName);
            Model model = probesNode.getModelForNode();
            model.addProbe(newProbe);
            OneProbeNode node=new OneProbeNode(newProbe);
            Node[] arrNodes = new Node[1];
            arrNodes[0] = node;
            probesNode.getChildren().add(arrNodes);
        }
    }
}
