/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.opensim.view.nodes;

import java.beans.PropertyEditorSupport;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.Action;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import static org.openide.nodes.Sheet.createExpertSet;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.opensim.modeling.AbstractConnector;
import org.opensim.modeling.AbstractProperty;
import org.opensim.modeling.Body;
import org.opensim.modeling.Component;
import org.opensim.modeling.OpenSimObject;

/**
 *
 * @author Ayman
 */
public class OneComponentNode extends OpenSimObjectNode {
    protected static ResourceBundle bundle = NbBundle.getBundle(OpenSimObjectNode.class);
    protected Component comp;

    public OneComponentNode(OpenSimObject obj) {
        super(obj);
        comp = Component.safeDownCast(obj);
    }

    public Action[] getActions(boolean b) {
        return super.getActions(b);
    }

    @Override
    public Sheet createSheet() {
        Sheet parentSheet =  super.createSheet(); 
        if (comp.getNumConnectors() >0){
            Sheet.Set connectorSheet = createExpertSet();
            connectorSheet.setDisplayName("Connectors");
            parentSheet.put(connectorSheet);
            for (int i=0; i< comp.getNumConnectors();i++ ){
                AbstractConnector ac = comp.getConnector(i);
                createConnectorProperty(ac, connectorSheet);
            }
        }
        return parentSheet;
    }
    
    private void createConnectorProperty(AbstractConnector connector, Sheet.Set sheetSet) {
        try {
            String connecteeType = connector.getConnectedToTypeName();
            String connectionName = connector.getName();
            PropertySupport.Reflection nextNodeProp = 
                    new PropertySupport.Reflection(new ConnectionEditor(connector, this),
                    String.class,
                    "getConnectedToName",
                    "setConnectedToName");
            nextNodeProp.setValue("canEditAsText", Boolean.TRUE);
            nextNodeProp.setValue("suppressCustomEditor", Boolean.TRUE);
            nextNodeProp.setName(connecteeType + ":" + connectionName);
            PropertyEditorSupport editor = EditorRegistry.getEditor(connecteeType);
            if (editor != null)
                nextNodeProp.setPropertyEditorClass(editor.getClass());
            sheetSet.put(nextNodeProp);
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * @return the comp
     */
    public Component getComp() {
        return comp;
    }
    
}
