/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.opensim.view.nodes;

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
            String templatedType = connector.getConcreteClassName();
            String connecteeType = templatedType.substring(10, templatedType.length() - 1);
            String connectionName = connector.getName();
            PropertySupport.Reflection nextNodeProp = null;
            nextNodeProp = new PropertySupport.Reflection(connector,
                    String.class,
                    "getName",
                    null);
            nextNodeProp.setValue("canEditAsText", Boolean.TRUE);
            nextNodeProp.setValue("suppressCustomEditor", Boolean.TRUE);
            nextNodeProp.setName(connecteeType + ":" + connectionName);
            sheetSet.put(nextNodeProp);
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
}
