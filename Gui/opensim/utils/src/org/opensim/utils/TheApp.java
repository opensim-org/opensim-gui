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
import javax.swing.JFrame;
import org.openide.DialogDisplayer;
import org.openide.LifecycleManager;
import org.openide.NotifyDescriptor;
import org.openide.util.Utilities;

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
        DialogDisplayer.getDefault().notify(
                new NotifyDescriptor.Message(errorMessage));
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
            appImage=Utilities.loadImage("org/opensim/view/images/frame48.gif");
        return appImage;
    }
    
    public static Image getApplicationIcon() {
       return Utilities.loadImage("/org/opensim/utils/frame.gif");
    }
}
