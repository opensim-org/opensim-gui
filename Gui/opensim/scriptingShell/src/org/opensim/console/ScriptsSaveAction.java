/* -------------------------------------------------------------------------- *
 * OpenSim: ScriptsSaveAction.java                                            *
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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.console;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.cookies.SaveCookie;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;



@ActionID(category = "Edit",
id = "org.opensim.console.ScriptsSaveAction")
@ActionRegistration(
displayName = "#CTL_SaveCurrentScriptAction")
@ActionReferences({
    @ActionReference(path = "Menu/Scripts", position = 400)
})
@Messages("CTL_SaveCurrentScriptAction=Save Current Script")
public final class ScriptsSaveAction implements ActionListener {

    private final DataObject context;


    public ScriptsSaveAction(DataObject context) {
        this.context = context;
    }

    public void actionPerformed(ActionEvent ev) {
        // TODO use context
        SaveCookie sc = context.getCookie(SaveCookie.class);
        if (sc != null) {
            try {
                sc.save();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        
    }

    public String getName() {
        return "Save";
    }

}
