/* -------------------------------------------------------------------------- *
 * OpenSim: TheApp.java                                                       *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib, Kevin Xu                                           *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may    *
 * not use this file except in compliance with the License. You may obtain a  *
 * copy of the License at http://www.apache.org/licenses/LICENSE-2.0          *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software       insta *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 * -------------------------------------------------------------------------- */
/*
 *
 * TheApp, a convenient class to 
 * Author(s): Ayman Habib
 */
package org.opensim.utils;

import java.awt.Dialog;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.CopyOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.JFrame;
import org.openide.LifecycleManager;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import java.nio.file.StandardCopyOption;
import java.util.prefs.Preferences;
import javax.swing.JPanel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.opensim.swingui.FileTextFieldAndChooser;
import org.opensim.logger.OpenSimLogger;
/**
 *
 * @author Ayman, a convenience class used for now as a place holder for common utilities/helper functions used
 *  by the rest of the application. 
 */
public final class TheApp {
    
    //static TheApp instance=null;
    private static JFrame appFrame;    // Application's frame, cached in for quick access
    private static Image appImage;
    private static String installDir=null;
    private static String OS = System.getProperty("os.name").toLowerCase();
    private static String userDir = System.getProperty("netbeans.user")+File.separatorChar;
    /** Creates a new instance of TheApp 
    protected TheApp() {
    }
    
    public static TheApp getInstance()
    {
        if (instance == null){
            instance = new TheApp();
        }
        return instance;
    }
    /**
     * Try loading a dynamic library, if isRequired is true then abort otherwise do nothing
     */
    public static void TryLoadLibrary(String libraryName, boolean isRequired)
    {
        try {
            System.loadLibrary(libraryName);
        }
        catch(UnsatisfiedLinkError e){
            if (isRequired)
                TheApp.exitApp("Required native code library failed to load. Check that the dynamic library "+libraryName+" is in your PATH");
        }
    }
    /**
     * Shut down the platform after displaying an error message
     **/
    public static void exitApp(String errorMessage)
    {
        ErrorDialog.showMessageDialog(errorMessage);
        LifecycleManager.getDefault().exit();
    }
    /**
     * cache the application's top frame for quick access when creating dialogs
     * throughout the application.
     */
    public static void setAppFrame(JFrame dAppFrame)
    {
        appFrame=dAppFrame;
    }
    /**
     * Retrieve the applicatio's top frame 
     */
    public static JFrame getAppFrame() {
        return appFrame;
    }

    public static Image getAppImage() {
        if (appImage==null)
            appImage=ImageUtilities.loadImage("org/opensim/helputils/helpmenu/images/frame48.gif");
        return appImage;
    }
    
    public static Image getApplicationIcon() {
       return ImageUtilities.loadImage("/org/opensim/utils/frame.gif");
    }
    /**
     * get the top directory where the OpenSim application is installed based on OPENSIM_HOME
     * @return installDir
     */
    public static String getInstallDir() {
        if (installDir == null){ try {
            // Test cross platform and cross platform with Spaces in path names etc.
            URI jarfile = TheApp.class.getProtectionDomain().getCodeSource().getLocation().toURI();
             // Remove the jar: prefix
            String schemePart = jarfile.getSchemeSpecificPart();
            // Remove trailing !/
            schemePart = schemePart.substring(0, schemePart.length()-2);
            String parentDir = "";
            if (OS.indexOf("win") >= 0){
                parentDir = schemePart.substring(6);
            }
            else {
                Path jarFilePath = Paths.get(schemePart);
                Path parentPath = jarFilePath.getParent();
                parentDir = parentPath.toString().substring(5);
            }
            boolean buildEnvironment = parentDir.lastIndexOf("cluster")!=-1;
            if (buildEnvironment){
                int lastIndex = parentDir.lastIndexOf("cluster")-6;
                installDir = parentDir.substring(0, lastIndex);
            }
            else {
                // go up the tree twice
                installDir = new File(parentDir).getParent();
                installDir = new File(installDir).getParent();
            }
            // Strip leading "file:/"
            System.out.println("new OPENSIM_HOME ="+installDir);
            } catch (URISyntaxException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return installDir;
    }
    /**
     * Get full path name to the directory containing the local users guide html pages
     * @return path or null if OPENSIM_HOME is not set;
     */
    public static String getUsersGuideDir() {
        return getInstallDir() + File.separator + "sdk" + File.separator + "doc" + File.separator + "UsersGuide" + File.separator;
    }
    /**
     * Get full path name to the file containing logo to be displayed as watermark
     * @return path or null if OPENSIM_HOME is not set;
     */
    public static String getApplicationLogoFileName() {
        String installDir = TheApp.getInstallDir();
        if (installDir==null) return null;
        String fullLogoFileName = installDir+File.separatorChar+"Geometry"+File.separatorChar+"OpenSimLogoSmall.PNG";
        return fullLogoFileName;
    }
    /**
     * Get default location to search for geometry in a new installation. This is shown in GUI under 
     * Preferences -> Geometry Path and can be edited later
     * @return $installDir/Geometry, this is platform dependent due to different layout
     */
    public static String getDefaultGeometrySearchPath() {
        return getCrossPlatformInstallDir()+File.separatorChar+"Geometry"+File.separatorChar;
    }
    /**
     * Get User Directory, this is usually with writable permissions and is
     * Platform dependent.
     */
    public static String getUserDir() {
        return userDir;
    }
    /**
     * Get Resources Directory where Models, Code has been installed. It will be updated by the installer 
     * when new Resources are installed.
     * @return 
     */
    public static String getResourcesDir() {
        return Preferences.userNodeForPackage(TheApp.class).get("OpenSimResourcesDir", null);
    }
    public static String installResources() {
        // Popup a directory browser dialog prompting for install location of Models, Scripts
        String userHome = System.getProperty("user.home")+File.separator+"Documents"+File.separator+"OpenSim"+
                        File.separator+System.getProperty ("netbeans.buildnumber");
        FileUtils.getInstance().setWorkingDirectoryPreference(userHome);
        String userSelection = null;
        boolean exists = false;
        javax.swing.JScrollPane jScrollPane1;
        javax.swing.JTextArea jTextArea1 = new javax.swing.JTextArea();

        jTextArea1.setColumns(20);
        jTextArea1.setFont(jTextArea1.getFont());
        jTextArea1.setRows(4);
        jTextArea1.setWrapStyleWord(true);
        jTextArea1.setEnabled(false);
        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new javax.swing.BoxLayout(containerPanel, javax.swing.BoxLayout.Y_AXIS));
        jTextArea1.setText("Please choose a location for the OpenSim resources (models and examples). \nApproximately 60 MB will be copied. You should have permissions to write to this folder.\nTo reinstall resources at any time, type \"installResources()\" in the ScriptingShell Window.\nResources can be uninstalled by manually deleting the resources folder.");
        FileTextFieldAndChooser destDirectoryPanel = new org.opensim.swingui.FileTextFieldAndChooser();
        destDirectoryPanel.setDirectoriesOnly(true);
        destDirectoryPanel.setCheckIfFileExists(false);
        destDirectoryPanel.setFileName(userHome, false);
        destDirectoryPanel.isSaveMode();
        destDirectoryPanel.setTreatEmptyStringAsValid(false);
        containerPanel.add(jTextArea1);
        containerPanel.add(destDirectoryPanel);
        containerPanel.validate();
        containerPanel.setPreferredSize(containerPanel.getPreferredSize());
        DialogDescriptor dd = new DialogDescriptor(containerPanel, "Folder to install models and scripts:");
        // Create a Dialog to contain the chooser
        Dialog dlg = DialogDisplayer.getDefault().createDialog(dd);
        dlg.setModal(true);
        dlg.setResizable(false);
        do {
            dlg.setVisible(true);
            Object userInput = dd.getValue();
            if (((Integer)userInput).compareTo((Integer)DialogDescriptor.CANCEL_OPTION)==0){
                return "";
            }
            userSelection = destDirectoryPanel.getFileName();
            exists = new File(userSelection).exists();
            if (exists){
                // Show info warning to redo
                NotifyDescriptor.Message errorDlg =
                          new NotifyDescriptor.Message("Specified directory already exists, please remove or rename before proceeding.");
                  DialogDisplayer.getDefault().notify(errorDlg);
            }
            else if (userSelection==null){
                 NotifyDescriptor.Message errorDlg =
                          new NotifyDescriptor.Message("No directory was specified, please fix before proceeding.");
                  DialogDisplayer.getDefault().notify(errorDlg);
               exists = true;
                // show error message and redo
            }
        } while (exists);
        
        if (userSelection != null){
            FileUtils.getInstance().setWorkingDirectoryPreference(userSelection);
            String src = getCrossPlatformInstallDir() + File.separator + "Resources.zip";
            String dest = userSelection;
            OpenSimLogger.logMessage("\nCopying resources to "+dest+"... ", OpenSimLogger.INFO);
            Path srcPath = Paths.get(src);
            Path destPath = Paths.get(dest);
            boolean success=true;
            if (!destPath.toFile().exists())
                success = destPath.toFile().mkdirs();
            if (success){
                try {
                    FileUtils.unzip(srcPath, destPath);
                } catch (IOException ex) {
                    ErrorDialog.displayExceptionDialog(ex);
                    System.out.println(srcPath.toAbsolutePath()+" couldn't be copied..Skipping");
                }
            }
            OpenSimLogger.logMessage("Done.", OpenSimLogger.INFO);
            return userSelection;
        }
        else
            return null;
    }
	// Due to platform differences, this call returns top level install folder
	// cross platform.
    public static String getCrossPlatformInstallDir() {
        if (OS.indexOf("win") >= 0){
         return (getInstallDir()+File.separatorChar+".."+File.separatorChar);
        }
        else { //OSX
          return getInstallDir();     
        }
    }
}
