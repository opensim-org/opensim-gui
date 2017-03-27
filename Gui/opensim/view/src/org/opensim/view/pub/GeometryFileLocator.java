/*
 *
 * GeometryFileLocator
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
package org.opensim.view.pub;

import java.io.File;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.prefs.Preferences;
import org.opensim.logger.OpenSimLogger;
import org.opensim.modeling.ModelVisualizer;
import org.opensim.utils.*;

/**
 *
 * @author Ayman. Enforces the policy for locating geometry files aka bone files
 * The policy is to search:
 * - directory bones under modelFilePath.
 * - GeometryPath preference directory if set
 * - under installation
 */
public class GeometryFileLocator {
   
   static GeometryFileLocator locator;
   public static String geometryInstallationDirectory=TheApp.getDefaultGeometrySearchPath();
   /** Creates a new instance of GeometryFileLocator */
   public GeometryFileLocator() {
   }
   
   public static GeometryFileLocator getInstance(){
      if (locator==null){
         locator = new GeometryFileLocator();
         // Parse GeometryPath preference and send to API
         ModelVisualizer.addDirToGeometrySearchPaths(geometryInstallationDirectory);
         updateGeometrySearchPathsFromPreferences();
      }
      return locator;
   }

    public static void updateGeometrySearchPathsFromPreferences() {
        String userGeometryPath=Preferences.userNodeForPackage(TheApp.class).get("Geometry Path", ".");
        String dirs[] = userGeometryPath.split(File.pathSeparator);
        for (int i=0; i< dirs.length; i++)
            ModelVisualizer.addDirToGeometrySearchPaths(dirs[i]);
    }

   public String getFullname(String modelFilePath, String bareFileName, boolean debug) {
      String candidate=bareFileName;
      if (debug) OpenSimLogger.logMessage("Debug: Trying "+candidate+"\n", OpenSimLogger.INFO);
      if (isValidFile(candidate))
         return candidate;
      candidate = modelFilePath+File.separator+"Geometry"+File.separator+bareFileName;
      if (debug) OpenSimLogger.logMessage("Debug: Trying "+candidate+"\n", OpenSimLogger.INFO);
      if (isValidFile(candidate))
         return candidate;
      String GeometryPath=Preferences.userNodeForPackage(TheApp.class).get("Geometry Path", ".");
      if (GeometryPath!=null){
         // Split at ";" to get directoryList
         StringTokenizer tokenizer = new StringTokenizer(GeometryPath, File.pathSeparator);
         Vector<String> dirList = new Vector<String>(4);
         // Try directory list in order
         while(tokenizer.hasMoreElements()){
                String nextDir = tokenizer.nextToken();
                candidate = nextDir+File.separator+bareFileName;
               if (debug) OpenSimLogger.logMessage("Debug: Trying "+candidate+"\n", OpenSimLogger.INFO);
               if (isValidFile(candidate))
                  return candidate;
         }
      }
      // Either "GeometryPath" is unspecified or was searched and nothing was found, now try installation dir.
      candidate= TheApp.getDefaultGeometrySearchPath()+File.separator+bareFileName;
      if (debug) OpenSimLogger.logMessage("Debug: Trying "+candidate+"\n", OpenSimLogger.INFO);
      if (isValidFile(candidate))
         return candidate;
      if (debug) System.out.println("GeometryFileLocator: not found"+candidate);
      return null;
   }

    private boolean isValidFile(String candidate) {
        return new File(candidate).exists()&& new File(candidate).isFile();
    }
   
}
