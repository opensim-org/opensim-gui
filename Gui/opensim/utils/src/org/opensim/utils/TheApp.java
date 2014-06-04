/*
 *
 * TheApp, a convenient class to 
 * Author(s): Ayman Habib
 * Copyright (c)  2005-2006, Stanford University, Ayman Habib
* Use of the OpenSim software in source form is permitted provided that the following
* conditions are met:
* 	1. The software is used only for non-commercial research and education. It may not
*     be used in relation to any commercial activity.
* 	2. The software is not distributed or redistributed.  Software distribution is allowed 
*     only through https://simtk.org/home/opensim.
* 	3. Use of the OpenSim software or derivatives must be acknowledged in all publications,
*      presentations, or documents describing work in which OpenSim or derivatives are used.
* 	4. Credits to developers may not be removed from executables
*     created from modifications of the source.
* 	5. Modifications of source code must retain the above copyright notice, this list of
*     conditions and the following disclaimer. 
* 
*  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
*  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
*  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
*  SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
*  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
*  TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
*  HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
*  OR BUSINESS INTERRUPTION) OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
*  WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.opensim.utils;

import java.awt.Image;
import java.io.File;
import java.util.Map;
import javax.swing.JFrame;
import org.openide.DialogDisplayer;
import org.openide.LifecycleManager;
import org.openide.NotifyDescriptor;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Ayman, a convenience class used for now as a place holder for common utilities/helper functions used
 *  by the rest of the application. 
 */
public final class TheApp {
    
    //static TheApp instance=null;
    private static JFrame appFrame;    // Application's frame, cached in for quick access
    private static Image appImage;
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
        Map<String, String> env = System.getenv();
        String installDir = env.get("OPENSIM_HOME");
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
     * @return $installDir/Geometry
     */
    public static String getDefaultGeometrySearchPath() {
        return getInstallDir()+File.separatorChar+"Geometry";
    }
}
