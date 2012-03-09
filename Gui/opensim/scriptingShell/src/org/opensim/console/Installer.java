/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.console;

import java.util.prefs.Preferences;
import org.openide.modules.ModuleInstall;
import org.openide.util.NbBundle;
import org.opensim.utils.TheApp;

public class Installer extends ModuleInstall {

    @Override
    public void restored() {
        // TODO
        restorePrefs();
    }
    /**
     * restorePrefs is primarily used for the first time around where there are no pref values
     * stored in the backing file/registry. It sets values in the backing store based on the resource/Bundle files
     * built into the application */
    private void restorePrefs()
    {
         String defaultScriptsPath = NbBundle.getMessage(JConsole.class,"CTL_ScriptsPath");
         String saved=Preferences.userNodeForPackage(TheApp.class).get("Scripts Path", defaultScriptsPath);
         Preferences.userNodeForPackage(TheApp.class).put("Scripts Path", saved);

    }
}
