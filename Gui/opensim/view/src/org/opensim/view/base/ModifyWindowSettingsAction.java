/* -------------------------------------------------------------------------- *
 * OpenSim: ModifyWindowSettingsAction.java                                   *
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

package org.opensim.view.base;

import java.awt.Color;
import java.util.prefs.Preferences;
import javax.swing.JColorChooser;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.utils.TheApp;
import org.opensim.view.OpenSimCanvas;
import org.opensim.view.pub.ViewDB;

public final class ModifyWindowSettingsAction extends CallableSystemAction {
        
    public void performAction() {
        // TODO implement action body
        JColorChooser backgroundColorChooser = new JColorChooser();
        OpenSimCanvas dCanvas = ViewDB.getInstance().getCurrentModelWindow().getCanvas();
        Color newColor = backgroundColorChooser.showDialog(dCanvas, "Select new background color", dCanvas.getBackground());
        if (newColor != null){
             float[] colorComponents = newColor.getRGBComponents(null);
             dCanvas.GetRenderer().SetBackground(colorComponents[0], colorComponents[1], colorComponents[2]);
             String defaultBackgroundColor=String.valueOf(colorComponents[0])+", "+
                     String.valueOf(colorComponents[1])+", "+
                     String.valueOf(colorComponents[2]);
            Preferences.userNodeForPackage(TheApp.class).put("BackgroundColor", defaultBackgroundColor);

             dCanvas.repaint();
        }
    }
    
    public String getName() {
        return NbBundle.getMessage(ModifyWindowSettingsAction.class, "CTL_ModifyWindowSettingsAction");
    }
    
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }
}
