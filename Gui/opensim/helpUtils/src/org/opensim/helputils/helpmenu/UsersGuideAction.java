/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.helputils.helpmenu;

/**
 *
 * @author Kevin Xu
 */
import java.io.File;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.utils.BrowserLauncher;
import org.opensim.utils.TheApp;

public final class UsersGuideAction extends CallableSystemAction {

    public void performAction() {
        String usersGuidePath = BrowserLauncher.isConnected() ? "http://simtk-confluence.stanford.edu:8080/display/OpenSim30/User%27s+Guide" : 
                TheApp.getUsersGuideDir() + "User%27s+Guide.html";            

        System.out.println("USERS GUIDE PATH: " + usersGuidePath);
        BrowserLauncher.openURL(usersGuidePath);
    }
    
    public String getName() {
        return NbBundle.getMessage(UsersGuideAction.class, "CTL_UsersGuide");
    }
    
    @Override
    protected void initialize() {
        super.initialize();
        putValue("noIconInMenu", Boolean.TRUE);
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
}
