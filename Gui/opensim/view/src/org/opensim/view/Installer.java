/* -------------------------------------------------------------------------- *
 * OpenSim: Installer.java                                                    *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib, Christopher Dembia                                 *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may    *
 * not use this file except in compliance with the License. You may obtain a  *
 * copy of the License at http://www.apache.org/licenses/LICENSE-2.0          *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 * -------------------------------------------------------------------------- */

package org.opensim.view;

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.openide.modules.ModuleInstall;
import org.openide.util.Exceptions;
import org.openide.windows.WindowManager;
import org.opensim.logger.OpenSimLogger;
import org.opensim.utils.ApplicationState;
import org.opensim.utils.ErrorDialog;
import org.opensim.utils.TheApp;
import org.opensim.view.actions.ApplicationExit;
import org.opensim.view.editors.BodyNameEditor;
import org.opensim.view.editors.FrameNameEditor;
import static org.opensim.view.motions.MotionDisplayer.STRING_EXPMARKER_DEFAULT_RADIUS;
import org.opensim.view.motions.MotionsDB;
import org.opensim.view.motions.MotionsDBDescriptor;
import org.opensim.view.nodes.EditorRegistry;
import org.opensim.view.pub.GeometryFileLocator;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.OpenSimDBDescriptor;
import org.opensim.view.pub.OpenSimDragNDropHandler;
import org.opensim.view.pub.PluginsDB;
import org.opensim.view.pub.ViewDB;
import org.opensim.view.pub.ViewDBDescriptor;

/**
 * Manages a module's lifecycle. Remember that an installer is optional and
 * often not needed at all.
 */
public class Installer extends ModuleInstall {
    
   // Enable popups to display on top of heavy weight component/canvas
   static {
      JPopupMenu.setDefaultLightWeightPopupEnabled(false);
      javax.swing.ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
      javax.swing.ToolTipManager.sharedInstance().setDismissDelay(600000); // keep tooltips up for a minute!
   }

   // This function is called when File...Exit is chosen.
   public boolean closing() {
      return ApplicationExit.confirmExit();
   }

    public void restored() {
        super.restored();
        try {
             // Put your startup code here.
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
            if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
                // The native slider on macOS looks somewhat nice.
                UIManager.put("SliderUI", "org.opensim.view.OpenSimSliderUI");
            }
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
        /**
         * @todo open explorer window, Restore default directory and Bones directories, ..
         */
        restorePrefs();
        // Force creation of Model-database OpenSimDB 
        // and a View-database ViewDB
        // and register View as Observer of Model
        OpenSimDB.getInstance().addObserver(ViewDB.getInstance());
        
        String saved = TheApp.getCurrentVersionPreferences().get("Application: Restore Models on Startup", "Off");
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
        
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {
                        JFrame mainFrame = (JFrame) WindowManager.getDefault().getMainWindow();
                        mainFrame.setTransferHandler(new OpenSimDragNDropHandler());
                        mainFrame.getDropTarget().setActive(true);
             }
        });
       
        //
        //PropertyEditorManager.registerEditor(OpenSimObject.class, OpenSimObjectEditor.class);
        EditorRegistry.addEditor("Body", new BodyNameEditor());
        EditorRegistry.addEditor("PhysicalFrame", new FrameNameEditor());
        EditorRegistry.addEditor("Frame", new FrameNameEditor());
    }
    /**
     * restorePrefs is primarily used for the first time around where there are no pref values
     * stored in the backing file/registry. It sets values in the backing store based on the resource/Bundle files
     * built nito the application */
    private void restorePrefs()
    {
         boolean updateResources = false;
         String relaunch_check_filepath = TheApp.getUserDir()+"first_launch.text";
         boolean firstLaunch = !new File(relaunch_check_filepath).exists();
         if (firstLaunch){
            updateResources = true;
            SwingUtilities.invokeLater( new Runnable(){
                public void run() {
                 String userDir = TheApp.installResources();
                 TheApp.getCurrentVersionPreferences().put("Internal.OpenSimResourcesDir", userDir);
                 String defaultScriptsPath = userDir+"/Code/GUI/";
                 TheApp.getCurrentVersionPreferences().put("Paths: Scripts Path", defaultScriptsPath);
               }
            });
            try {
                 // First launch, copy resources to User selected folder and save that as OpenSimUserDir
                 new File(relaunch_check_filepath).createNewFile();
             } catch (IOException ex) {
                 // need to report 
                 Exceptions.printStackTrace(ex);
             }        
         }
         
         String defaultGeometryPath = TheApp.getDefaultGeometrySearchPath();
         String saved=TheApp.getCurrentVersionPreferences().get("Paths: Geometry Search Path", defaultGeometryPath);
         OpenSimLogger.logMessage("\nGeometrySeach path set to "+saved, OpenSimLogger.INFO);
         if (saved.isEmpty()||saved.equalsIgnoreCase("")){
             saved = defaultGeometryPath;
             TheApp.getCurrentVersionPreferences().put("Paths: Geometry Search Path", saved);
         }
         // If saved is not blank we assume user knows what s/he's doing and leave it alone. Fixes issue #1115
         // Push changes to API side
         GeometryFileLocator.updateGeometrySearchPathsFromPreferences();

         String muscleRadius = "8";
         saved = TheApp.getCurrentVersionPreferences().get("Visualizer: Muscle Display Radius (mm)", muscleRadius);
         TheApp.getCurrentVersionPreferences().put("Visualizer: Muscle Display Radius (mm)", saved);
         // The following value should stay the same as the value in MotionDisplayer constructor
         String experimentalMarkerDisplayRadiusStr=STRING_EXPMARKER_DEFAULT_RADIUS;
         saved=TheApp.getCurrentVersionPreferences().get("Visualizer: Experimental Marker Radius (mm)", experimentalMarkerDisplayRadiusStr);
         TheApp.getCurrentVersionPreferences().put("Visualizer: Experimental Marker Radius (mm)", saved);
         
         String persistModels = "Off";        
         saved = TheApp.getCurrentVersionPreferences().get("Application: Restore Models on Startup", persistModels);
         TheApp.getCurrentVersionPreferences().put("Application: Restore Models on Startup", saved);

         String refreshRateInMS = "100";        
         saved = TheApp.getCurrentVersionPreferences().get("Internal.Refresh Rate (ms)", refreshRateInMS);
         TheApp.getCurrentVersionPreferences().put("Internal.Refresh Rate (ms)", saved);
   }
}
