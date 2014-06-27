package org.opensim.view.nodes;

import java.awt.event.ActionEvent;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.BooleanStateAction;
import org.opensim.view.ExplorerTopComponent;

public final class ToggleEnabledStateAction extends BooleanStateAction {
        
    public String getName() {
        return NbBundle.getMessage(ToggleEnabledStateAction.class, "CTL_ToggleEnabledStateAction");
    }
    
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }
    public boolean isEnabled() {
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        //if(selected.length!=1) return false; // only if a single item is selected
        // Action shouldn't be available otherwise
        if (selected[0] instanceof DisableableObject){
            DisableableObject dNode = (DisableableObject)selected[0];
            setBooleanState(!dNode.isDisabled());
            return true;
        }
        return false;
    }
    public void actionPerformed(ActionEvent actionEvent) {
        // TODO implement action body
        final Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        // TODO implement action body
        boolean oldState = getBooleanState();
        final boolean newState = !oldState;
        applyNewStateToNodes(selected, newState);
   }

    private void applyNewStateToNodes(final Node[] selected, final boolean newState) {
        for(int i=0;i<selected.length;i++){
            Node selectedNode = selected[i];
            if (selectedNode instanceof DisableableObject){
                DisableableObject object=(DisableableObject) selectedNode;
                object.setDisabled(!newState);
                OpenSimObjectNode oNode= (OpenSimObjectNode) selectedNode;
                PropertyEditorAdaptor pea = new PropertyEditorAdaptor("isDisabled", oNode);
                pea.setValueBool(!newState);
            }
        }
        setBooleanState(newState);
    }
    
}
