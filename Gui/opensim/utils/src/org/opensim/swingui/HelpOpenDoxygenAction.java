/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.swingui;

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

public final class HelpOpenDoxygenAction extends CallableSystemAction {
    
    public void performAction() {
        String basePath = TheApp.getInstallDir();
        String doxygenPath = basePath + File.separator + "sdk" + File.separator + "doc" + File.separator + "OpenSimAPI.html";
        System.out.println("PATH: " + doxygenPath);

        // If local file doesn't exist open Doxygen online
        if(! new File(doxygenPath).exists()) {
            doxygenPath = "https://simtk.org/api_docs/opensim/api_docs30/";
        }
        
        BrowserLauncher.openURL(doxygenPath);
        
    }
    
    public String getName() {
        return NbBundle.getMessage(HelpOpenOnlineDocsAction.class, "CTL_OpenDoxygen");
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
