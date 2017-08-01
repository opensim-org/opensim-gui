/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.view;

import java.util.Vector;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.opensim.view.nodes.OneComponentNode;
import org.openide.util.HelpCtx;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.modeling.OpenSimObject;
import org.opensim.view.nodes.OpenSimNode;
/**
 *
 * @author Ayman
 */
public abstract class ObjectAppearanceChangeAction extends CallableSystemAction {
    
    protected Vector<OneComponentNode> collectAffectedComponentNodes() {
        Vector<OneComponentNode> objects = new Vector<OneComponentNode>();
        Vector<OpenSimObject> opensimObjects = new Vector<OpenSimObject>();
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        for (int i = 0; i < selected.length; i++) {
            if (!selected[i].isLeaf()){
                Children ch = selected[i].getChildren();
                for (int chNum = 0; chNum < ch.getNodesCount(); chNum++) {
                    collectDescendentNodes((OpenSimNode) ch.getNodeAt(chNum), objects, opensimObjects);
                }
            }
            if (selected[i] instanceof OneComponentNode) {
                if (!(opensimObjects.contains(((OneComponentNode)selected[i]).getOpenSimObject()))){
                    objects.add((OneComponentNode)selected[i]);
                    opensimObjects.add(((OneComponentNode)selected[i]).getOpenSimObject());
                }
            } 
        }
        return objects;
    }

    // node could be a Group or a list of objects not backed by OpenSim objects
    protected void collectDescendentNodes(OpenSimNode node, Vector<OneComponentNode> descendents, Vector<OpenSimObject> osimObjects) {
        
        if (node.isLeaf() && node instanceof OneComponentNode){
            if(!(osimObjects.contains(((OneComponentNode)node).getOpenSimObject()))){
                descendents.add((OneComponentNode) node);
                osimObjects.add(((OneComponentNode)node).getOpenSimObject());
                return;
            }
        }
        Children ch = node.getChildren();
        // process children
        for (Node childNode : ch.getNodes()) {
            if (!childNode.isLeaf()){
                collectDescendentNodes((OpenSimNode) childNode, descendents, osimObjects);
            }
            else if (childNode instanceof OneComponentNode) {
                if(!(osimObjects.contains(((OneComponentNode)childNode).getOpenSimObject()))){
                    descendents.add((OneComponentNode) childNode);
                    osimObjects.add(((OneComponentNode)childNode).getOpenSimObject());
                }
            }
         }
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    protected boolean asynchronous() {
        return false;
    }
    
}
