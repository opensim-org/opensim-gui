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

public final class UsersGuideAction extends CallableSystemAction {
    
    public void performAction() {
        String basePath = TheApp.getInstallDir();
        String usersGuidePath = "http://simtk-confluence.stanford.edu:8080/display/OpenSim/User%27s+Guide"; 
        System.out.println("PATH: " + usersGuidePath);

        // If issues with online user's guide then open local file
        try {
            URL url = new URL(usersGuidePath);
            HttpURLConnection urlConnect = (HttpURLConnection)url.openConnection();
            Object objData = urlConnect.getContent();
            if(objData == null) {
                usersGuidePath = basePath + File.separator + "sdk" + File.separator + "doc" + File.separator + "UsersGuide.pdf";  
            }            
        } catch (Exception e) {
            usersGuidePath = basePath + File.separator + "sdk" + File.separator + "doc" + File.separator + "UsersGuide.pdf";            
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
