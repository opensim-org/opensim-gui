/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.view.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.nodes.OneModelNode;
import org.opensim.view.nodes.ModelCloseSelectedAction;

@ActionID(category = "File",
id = "org.opensim.view.actions.FileCloseAllAction")
@ActionRegistration(displayName = "#CTL_FileCloseAllAction")
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 1381)
})
@Messages("CTL_FileCloseAllAction=Close All")
public final class FileCloseAllAction implements ActionListener {

    public void actionPerformed(ActionEvent e) {
        // TODO implement action body
         Node root = ExplorerTopComponent.findInstance().getExplorerManager().getRootContext();
         if (root == null) return; // No models
         Children ch=root.getChildren();
         int numChildren = ch.getNodesCount();
         if (numChildren==0) return;
         ArrayList<Node> modelsToClose = new ArrayList<Node>();
         for(int i=0; i < numChildren; i++){
             if (ch.getNodeAt(i) instanceof OneModelNode){
                OneModelNode modelNode = (OneModelNode) ch.getNodeAt(i);
                modelsToClose.add(modelNode);
            }
         }
         if (modelsToClose.size()==0) return;
         Node[] modelNodes = new Node[modelsToClose.size()];
         for(int j=0; j<modelsToClose.size(); j++){
             modelNodes[j] = modelsToClose.get(j);
         }
        try {
            ExplorerTopComponent.findInstance().getExplorerManager().setSelectedNodes(modelNodes);
            ModelCloseSelectedAction closeSelectedAction = (ModelCloseSelectedAction) ModelCloseSelectedAction.findObject(
                     (Class)Class.forName("org.opensim.view.nodes.ModelCloseSelectedAction"), true);
            closeSelectedAction.performAction();
        } catch (ClassNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (PropertyVetoException ex) {
            Exceptions.printStackTrace(ex);
        }
   }
}
