package org.opensim.view.nodes;

import org.openide.util.HelpCtx;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.view.pub.ViewDB;


class ViewMakeNewAction extends CallableSystemAction {

    public void performAction() {
        ViewDB.getInstance().addViewWindow();
    }

    public String getName() {
        return "Add window";
    }

    public HelpCtx getHelpCtx() {
        return null;
    }

    public boolean asynchronus() {
        return false;
    }
}