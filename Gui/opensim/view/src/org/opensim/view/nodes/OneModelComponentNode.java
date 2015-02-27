/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.opensim.view.nodes;

import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.opensim.modeling.Body;
import org.opensim.modeling.Geometry;
import org.opensim.modeling.ModelComponent;
import org.opensim.modeling.OpenSimObject;
import org.opensim.view.BodyToggleFrameAction;

/**
 *
 * @author Ayman
 */
public class OneModelComponentNode extends OneComponentNode {
    protected ModelComponent modelComp;
    public OneModelComponentNode(OpenSimObject obj) {
        super(obj);
        modelComp = ModelComponent.safeDownCast(obj);
        createGeometryNodes();
    }

    protected void createGeometryNodes() {
        int geomSize = modelComp.getGeometrySize();
        // Create node for geometry
        Children children = getChildren();
        for (int g = 0; g < geomSize; g++) {
            Geometry oneG = modelComp.get_GeometrySet(g);
            OneGeometryNode node = new OneGeometryNode(oneG);
            Node[] arrNodes = new Node[1];
            arrNodes[0] = node;
            children.add(arrNodes);
        }
    }

    @Override
    public Node cloneNode() {
        return new OneModelComponentNode(getOpenSimObject());
    }

    public Action[] getActions(boolean b) {
        Action[] superActions = (Action[]) super.getActions(b);
        /*
        // Arrays are fixed size, onvert to a List
        List<Action> actions = Arrays.asList(superActions);
        // Create new Array of proper size
        Action[] retActions = new Action[actions.size() + 2];
        actions.toArray(retActions);
        try {
            // append new command to the end of the list of actions
            retActions[actions.size()] = (BodyToggleFrameAction) BodyToggleFrameAction.findObject((Class) Class.forName("org.opensim.view.BodyToggleFrameAction"), true);
            retActions[actions.size() + 1] = (BodyToggleCOMAction) BodyToggleCOMAction.findObject((Class) Class.forName("org.opensim.view.nodes.BodyToggleCOMAction"), true);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }*/
        return superActions;
    }
    
}
