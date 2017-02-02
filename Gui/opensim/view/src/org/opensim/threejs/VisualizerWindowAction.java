/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.threejs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.eclipse.jetty.JettyMain;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.opensim.swingui.SwingWorker;
import org.opensim.utils.BrowserLauncher;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;

@ActionID(
        category = "Edit",
        id = "org.opensim.threejs.VisualizerWindow"
)
@ActionRegistration(
        displayName = "#CTL_VisualizerWindow"
)
@ActionReference(path = "Menu/Window", position = 3333)
@Messages("CTL_VisualizerWindow=Visualizer")
public final class VisualizerWindowAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
            // launch server and open page in fixed location to be used as a view
        
            ExportSceneToThreeJsAction.exportAllModelsToJson(JettyMain.getServerRootDir()+"/threejs/editor/model.json");
            ViewDB.getInstance().startVisualizationServer();
            BrowserLauncher.openURL("http://localhost:"+JettyMain.getServerPort()+JettyMain.getPathToStartPage()+"index.html");
            ViewDB.getInstance().setCurrentJson();
    }
}
