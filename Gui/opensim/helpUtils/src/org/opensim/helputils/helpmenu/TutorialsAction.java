/* -------------------------------------------------------------------------- *
 * OpenSim: TutorialsAction.java                                              *
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
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 * -------------------------------------------------------------------------- */

package org.opensim.helputils.helpmenu;

import java.io.File;
import java.io.FileFilter;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

public final class TutorialsAction extends CallableSystemAction {

    public void performAction() {
        // TODO implement action body
    }

    public String getName() {
        return NbBundle.getMessage(TutorialsAction.class, "CTL_Tutorials");
    }

    @Override
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }

    @Override
    public JMenuItem getMenuPresenter() {
      JMenu displayMenu = new JMenu("Examples & Tutorials");
      FileFilter fileFilter = new FileFilter() {
                public boolean accept(File file) {
                    return (!file.isDirectory()&& file.getName().endsWith(".html"));
                }
      };

      // Add online examples
      displayMenu.add(new BrowserPageDisplayerAction("Online Examples", "https://simtk-confluence.stanford.edu/display/OpenSim/Examples+and+Tutorials"));

      // Adding tutorials
      displayMenu.add(new BrowserPageDisplayerAction("OpenSimTutorial1", "https://simtk-confluence.stanford.edu/display/OpenSim/Tutorial+1+-+Intro+to+Musculoskeletal+Modeling"));
      displayMenu.add(new BrowserPageDisplayerAction("OpenSimTutorial2", "https://simtk-confluence.stanford.edu/display/OpenSim/Tutorial+2+-+Simulation+and+Analysis+of+a+Tendon+Transfer+Surgery"));
      displayMenu.add(new BrowserPageDisplayerAction("OpenSimTutorial3", "https://simtk-confluence.stanford.edu/display/OpenSim/Tutorial+3+-+Scaling%2C+Inverse+Kinematics%2C+and+Inverse+Dynamics"));

      return displayMenu;
    }

}
