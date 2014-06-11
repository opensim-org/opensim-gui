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
