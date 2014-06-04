package org.opensim.view.experimentaldata;

/**
 *
 * @author  ayman
 * Copyright (c)  2009, Stanford University and Ayman Habib. All rights reserved.
 * Use of the OpenSim software in source form is permitted provided that the following
 * conditions are met:
 * 	1. The software is used only for non-commercial research and education. It may not
 *     be used in relation to any commercial activity.
 * 	2. The software is not distributed or redistributed.  Software distribution is allowed
 *     only through https://simtk.org/home/opensim.
 * 	3. Use of the OpenSim software or derivatives must be acknowledged in all publications,
 *      presentations, or documents describing work in which OpenSim or derivatives are used.
 * 	4. Credits to developers may not be removed from executables
 *     created from modifications of the source.
 * 	5. Modifications of source code must retain the above copyright notice, this list of
 *     conditions and the following disclaimer.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 *  SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 *  TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 *  HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR BUSINESS INTERRUPTION) OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 *  WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
import org.opensim.modeling.Model;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.motions.OneMotionNode;

public final class MotionEditMotionObjectsAction extends CallableSystemAction {
    
    public void performAction() {
        // TODO implement action body
        // Get experimental data object from selected nodes and invoke ".hide on them"
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        if (selected.length!=1) return;
        OneMotionNode motionNode = (OneMotionNode)(selected[0]);
        Model model = motionNode.getModelForNode();
        final AnnotatedMotion amot = (AnnotatedMotion) (motionNode).getMotion();
        EditMotionObjectsPanel dataPanel= new EditMotionObjectsPanel(amot, model);
        final DialogDescriptor dlg = new DialogDescriptor(dataPanel, "Inspect Motion Data");
        dlg.setOptions(new Object[]{DialogDescriptor.CLOSED_OPTION});
        dlg.setModal(false);
        Dialog wDlg = DialogDisplayer.getDefault().createDialog(dlg);
        wDlg.setVisible(true);
        wDlg.addWindowListener(new WindowAdapter(){
              public void windowClosing(WindowEvent event) {
                Object userInput = dlg.getValue();
                if (((Integer)userInput).compareTo((Integer)DialogDescriptor.CLOSED_OPTION)==0){
                    amot.updateMotionDisplayer();
                }
                //amot.applyTransform(dataPanel.getLastTranform());
                super.windowClosing(event);
              }
            public void windowClosed(WindowEvent e) {
                Object userInput = dlg.getValue();
                if (((Integer)userInput).compareTo((Integer)DialogDescriptor.CLOSED_OPTION)==0){
                    amot.updateMotionDisplayer();
                }
                //amot.applyTransform(dataPanel.getLastTranform());
                super.windowClosed(e);
            }
        });
    }
        
    public String getName() {
        return NbBundle.getMessage(MotionEditMotionObjectsAction.class, "CTL_MotionEditMotionObjectsAction");
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
