/* -------------------------------------------------------------------------- *
 * OpenSim: PlotterSourceFile.java                                            *
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
 * PlotterSourceFile
 * Author(s): Ayman Habib
 */
package org.opensim.plotter;

import java.io.File;
import java.io.IOException;
import org.opensim.modeling.Storage;

/**
 *
 * @author Ayman. A class representing the File selection. The file is parsed
 * and its contents cached in this object for quick reference later.
 */
public class PlotterSourceFile extends PlotterSourceStorage {

   /** Creates a new instance of PlotterSourceFile */
   public PlotterSourceFile(String filename) throws IOException {
      super(new Storage(filename));
      String fullFileName = filename;
      displayName = new File(fullFileName).getName();
      //System.out.println("Created PlotterSourceFile DisplayName="+displayName);
   }
   
    public boolean convertAngularUnits() {
        return false;
    }
}
