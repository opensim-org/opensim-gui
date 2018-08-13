/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.view;

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.prefs.Preferences;
import org.openide.windows.OnShowing;
import org.opensim.utils.ApplicationState;
import org.opensim.utils.ErrorDialog;
import org.opensim.utils.TheApp;
import org.opensim.view.motions.MotionsDB;
import org.opensim.view.motions.MotionsDBDescriptor;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.OpenSimDBDescriptor;
import org.opensim.view.pub.PluginsDB;
import org.opensim.view.pub.ViewDB;
import org.opensim.view.pub.ViewDBDescriptor;

/**
 *
 * @author Ayman
 */
@OnShowing
public class DeSerializeState implements Runnable {

    @Override
    public void run() {
        String saved = Preferences.userNodeForPackage(TheApp.class).get("Persist Models", "On");
        if (saved.equalsIgnoreCase("on")){
            /** Restore from file */            
            try {
                XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(
                        new FileInputStream(TheApp.getUserDir()+"AppState.xml")));
                ApplicationState readState= (ApplicationState)decoder.readObject();
                PluginsDB.getInstance().loadPlugins();
                OpenSimDB.getInstance().rebuild((OpenSimDBDescriptor) readState.getObject("OpenSimDB"));
                ViewDB.getInstance().rebuild((ViewDBDescriptor) readState.getObject("ViewDB"));
                decoder.close();
            } catch (FileNotFoundException ex) {
                // First time, no file yet
                ApplicationState as = ApplicationState.getInstance();
                as.addObject("OpenSimDB", new OpenSimDBDescriptor(OpenSimDB.getInstance()));
                as.addObject("ViewDB", new ViewDBDescriptor(ViewDB.getInstance()));
                as.addObject("PluginsDB", PluginsDB.getInstance());
                as.addObject("MotionsDB", new MotionsDBDescriptor(MotionsDB.getInstance()));
            } catch (IOException ex) {
                ErrorDialog.displayExceptionDialog(ex);
            }
        }
    }
    
}
