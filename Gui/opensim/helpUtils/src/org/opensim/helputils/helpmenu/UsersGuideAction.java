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
        String basePath = TheApp.getInstallDir();
        String usersGuidePath = basePath + File.separator + "sdk" + File.separator + "doc" + File.separator + "UsersGuide.pdf";
        System.out.println("PATH: " + usersGuidePath);

        // If local file doesn't exist open Doxygen online
        if(! new File(usersGuidePath).exists()) {
            usersGuidePath = "http://simtk-confluence.stanford.edu:8080/display/OpenSim/User%27s+Guide";
        }
        
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
