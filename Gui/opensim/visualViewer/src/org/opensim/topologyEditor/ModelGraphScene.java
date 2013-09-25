/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.topologyEditor;

import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.border.Border;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.graph.GraphScene;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;

/**
 *
 * @author Ayman after code from JavaOne demo
 * 
 * The model of  a graph representing the topology of an OpenSim model.
 */
public class ModelGraphScene extends GraphScene.StringGraph {

    private static final Border BORDER_4 = BorderFactory.createLineBorder (4);
    private static final Border BORDER_0 = BorderFactory.createEmptyBorder();

    /**
     * @return the BORDER_0
     */
    public static Border getBORDER_0() {
        return BORDER_0;
    }

    private LayerWidget mainLayer;
    private LayerWidget connectionLayer;

    private WidgetAction moveAction = ActionFactory.createMoveAction ();

    public ModelGraphScene() {
        mainLayer = new LayerWidget (this);
        addChild(mainLayer);

        connectionLayer = new LayerWidget (this);
        addChild(connectionLayer);
    }

    protected Widget attachNodeWidget (String node) {
        LabelWidget label = new LabelWidget (this, node);
        label.setBorder (BORDER_4);
        label.getActions ().addAction (moveAction);
        mainLayer.addChild (label);
        return label;
    }

    protected Widget attachEdgeWidget (String edge) {
        ConnectionWidget connection = new ConnectionWidget (this);
        connection.setTargetAnchorShape (AnchorShape.TRIANGLE_FILLED);
        connectionLayer.addChild (connection);
        return connection;
    }

    protected void attachEdgeSourceAnchor (String edge, String oldSourceNode, String sourceNode) {
        Widget w = sourceNode != null ? findWidget (sourceNode) : null;
        ((ConnectionWidget) findWidget (edge)).setSourceAnchor (AnchorFactory.createRectangularAnchor (w));
    }

    protected void attachEdgeTargetAnchor (String edge, String oldTargetNode, String targetNode) {
        Widget w = targetNode != null ? findWidget (targetNode) : null;
        ((ConnectionWidget) findWidget (edge)).setTargetAnchor (AnchorFactory.createRectangularAnchor (w));
    }

}

