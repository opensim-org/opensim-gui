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
