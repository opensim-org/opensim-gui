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
import java.net.HttpURLConnection;
import java.net.URL;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.utils.BrowserLauncher;
import org.opensim.utils.TheApp;

public final class OpenDoxygenAction extends CallableSystemAction {
    
    public void performAction() {
        String basePath = TheApp.getInstallDir();
        String doxygenPath = "https://simtk.org/api_docs/opensim/api_docs30/";
        System.out.println("PATH: " + doxygenPath);

        // If issues with online Doxygen then open local Doxygen
        try {
            URL url = new URL(doxygenPath);
            HttpURLConnection urlConnect = (HttpURLConnection)url.openConnection();
            Object objData = urlConnect.getContent();
            if(objData == null) {
                doxygenPath = basePath + File.separator + "sdk" + File.separator + "doc" + File.separator + "OpenSimAPI.html";     
            }
        } catch (Exception e) {
            doxygenPath = basePath + File.separator + "sdk" + File.separator + "doc" + File.separator + "OpenSimAPI.html";          
        }

        
        BrowserLauncher.openURL(doxygenPath);
        
    }
    
    public String getName() {
        return NbBundle.getMessage(OpenDoxygenAction.class, "CTL_OpenDoxygen");
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
