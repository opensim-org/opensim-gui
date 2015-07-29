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
import org.opensim.modeling.Geometry;
import org.opensim.modeling.ModelComponent;
import org.opensim.modeling.OpenSimObject;
import org.opensim.view.ObjectAddGeometryMenuAction;

/**
 *
 * @author Ayman
 */
public class OneModelComponentNode extends OneComponentNode {
    private ModelComponent modelComp;
    public OneModelComponentNode(OpenSimObject obj) {
        super(obj);
        modelComp = ModelComponent.safeDownCast(obj);
        createGeometryNodes();
    }

    protected final void createGeometryNodes() {
        int geomSize = modelComp.getNumGeometry();
        // Create node for geometry
        Children children = getChildren();
        for (int g = 0; g < geomSize; g++) {
            Geometry oneG = modelComp.get_geometry(g);
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
        
        // Arrays are fixed size, onvert to a List
        /*
        List<Action> actions = Arrays.asList(superActions);
        // Create new Array of proper size
        Action[] retActions = new Action[actions.size() + 1];
        actions.toArray(retActions);
        try {
            // append new command to the end of the list of actions
            retActions[actions.size()] = (ObjectAddGeometryMenuAction) ObjectAddGeometryMenuAction.findObject((Class) Class.forName("org.opensim.view.ObjectAddGeometryMenuAction"), true);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }*/
        return superActions;
    }

    /**
     * @return the modelComp
     */
    public ModelComponent getModelComp() {
        return modelComp;
    }
    
}
