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
