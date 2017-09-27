/* -------------------------------------------------------------------------- *
 * OpenSim: MotionReclassifyAction.java                                       *
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
package org.opensim.view.experimentaldata;

/**
 *
 * @author  ayman
 */
import java.awt.Dialog;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.motions.OneMotionNode;

public final class MotionReclassifyAction extends CallableSystemAction {
    
    public void performAction() {
        // TODO implement action body
        // Get experimental data object from selected nodes and invoke ".hide on them"
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        if (selected.length!=1 || (! (selected[0] instanceof OneMotionNode))) return;
        final AnnotatedMotion amot = (AnnotatedMotion) ((OneMotionNode)(selected[0])).getMotion();
        final ClassifyDataJPanel dataPanel= new ClassifyDataJPanel();
        dataPanel.setAmotion(amot);
        final DialogDescriptor dlg = new DialogDescriptor(dataPanel, "Inspect Experimental Data");
        dlg.setModal(false);
        Dialog wDlg = DialogDisplayer.getDefault().createDialog(dlg);
        wDlg.setVisible(true);
        wDlg.addWindowListener(new WindowAdapter(){
              public void windowClosing(WindowEvent event) {
                Object userInput = dlg.getValue();
                if (((Integer)userInput).compareTo((Integer)DialogDescriptor.CANCEL_OPTION)==0){
                    dataPanel.resetTransforms();
                }
                else
                    amot.setCurrentRotations(dataPanel.getRotations());
                //amot.applyTransform(dataPanel.getLastTranform());
                super.windowClosing(event);
              }
            public void windowClosed(WindowEvent e) {
                Object userInput = dlg.getValue();
                if (((Integer)userInput).compareTo((Integer)DialogDescriptor.CANCEL_OPTION)==0){
                    dataPanel.resetTransforms();
                }
                else
                    amot.setCurrentRotations(dataPanel.getRotations());
                //amot.applyTransform(dataPanel.getLastTranform());
                super.windowClosed(e);
            }
        });
    }
        
    public String getName() {
        return NbBundle.getMessage(MotionReclassifyAction.class, "CTL_MotionReclassifyAction");
    }
    
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }
    
}
