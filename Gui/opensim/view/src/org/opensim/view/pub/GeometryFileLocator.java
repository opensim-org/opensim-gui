/* -------------------------------------------------------------------------- *
 * OpenSim: GeometryFileLocator.java                                          *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib                                                     *
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
/*
 *
 * GeometryFileLocator
 * Author(s): Ayman Habib
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
   
   static GeometryFileLocator locator=null;
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
        String userGeometryPath=TheApp.getCurrentVersionPreferences().get("Geometry Search Path", ".");
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
      String GeometryPath=TheApp.getCurrentVersionPreferences().get("Geometry Search Path", ".");
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
