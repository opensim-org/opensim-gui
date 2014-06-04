package org.opensim.coordinateviewer;

import javax.swing.SwingUtilities;
import org.openide.modules.ModuleInstall;
import org.opensim.utils.ApplicationState;
import org.opensim.view.pub.OpenSimDB;

/**
 * Manages a module's lifecycle. Remember that an installer is optional and
 * often not needed at all.
 */
public class Installer extends ModuleInstall {

    public void restored() {
        // By default, do nothing.
        // Put your startup code here.
        ApplicationState state = ApplicationState.getInstance();
        Object cv = state.getObject("CoordinateViewer");
        if (cv != null){
            final CoordinateViewerDescriptor mDesc = (CoordinateViewerDescriptor) cv;
            SwingUtilities.invokeLater(new Runnable(){ // Should change to WindowManager.getDefault().invokeWhenUIReady if/when we upgrade NB
                public void run(){
                    CoordinateViewerTopComponent.getDefault().rebuild(mDesc);
                }
            });
        }
    }

    public boolean closing() {
        
        ApplicationState state = ApplicationState.getInstance();
        state.addObject("CoordinateViewer", new CoordinateViewerDescriptor(OpenSimDB.getInstance()));
        return true;
    }
    
}
