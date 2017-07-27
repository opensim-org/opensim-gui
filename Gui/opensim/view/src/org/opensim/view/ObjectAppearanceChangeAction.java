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
import org.opensim.view.nodes.OpenSimObjectSetNode;
import org.openide.util.HelpCtx;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.view.nodes.ActuatorGroupNode;
/**
 *
 * @author Ayman
 */
public abstract class ObjectAppearanceChangeAction extends CallableSystemAction {
    
    protected Vector<OneComponentNode> collectAffectedComponentNodes() {
        Vector<OneComponentNode> objects = new Vector<OneComponentNode>();
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        for (int i = 0; i < selected.length; i++) {
            if (selected[i] instanceof OneComponentNode) {
                collectDescendentNodes((OneComponentNode) selected[i], objects);
            } else if (selected[i] instanceof OpenSimObjectSetNode) {
                // Get children nd descend if instanceof OneComponentNode
                Children ch = ((OpenSimObjectSetNode) selected[i]).getChildren();
                for (int chNum = 0; chNum < ch.getNodesCount(); chNum++) {
                    if (ch.getNodeAt(chNum) instanceof OneComponentNode) {
                        collectDescendentNodes((OneComponentNode) ch.getNodeAt(chNum), objects);
                    }
                }
            } else if (selected[i] instanceof ActuatorGroupNode) {
                Children ch = ((ActuatorGroupNode) selected[i]).getChildren();
                for (int chNum = 0; chNum < ch.getNodesCount(); chNum++) {
                    if (ch.getNodeAt(chNum) instanceof OneComponentNode) {
                        collectDescendentNodes((OneComponentNode) ch.getNodeAt(chNum), objects);
                    }
                }
            }
        }
        return objects;
    }

    // node could be a Group or a list of objects not backed by OpenSim objects
    protected void collectDescendentNodes(OneComponentNode node, Vector<OneComponentNode> descendents) {
        descendents.add(node);
        Children ch = node.getChildren();
        // process children
        for (Node childNode : ch.getNodes()) {
            if (childNode instanceof OneComponentNode) {
                collectDescendentNodes((OneComponentNode) childNode, descendents);
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
