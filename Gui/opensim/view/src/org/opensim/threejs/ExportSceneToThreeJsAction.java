/* -------------------------------------------------------------------------- *
 * OpenSim: ExportSceneToThreeJsAction.java                                   *
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
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.threejs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import org.json.simple.JSONObject;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.opensim.modeling.Model;
import org.opensim.utils.ErrorDialog;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.utils.FileUtils;
import org.opensim.view.pub.ViewDB;

@ActionID(
        category = "File",
        id = "org.opensim.threejs.ExportSceneToThreeJsAction"
)
@ActionRegistration(
        displayName = "#CTL_ExportSceneToThreeJsAction"
)
@ActionReference(path = "Menu/File", position = 1429)
@Messages("CTL_ExportSceneToThreeJsAction=Export all models to json format")
public final class ExportSceneToThreeJsAction implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        
       Model model = OpenSimDB.getInstance().getCurrentModel();
       if (model == null) return; // Nothing to export
       String fileName = FileUtils.getInstance().browseForFilenameToSave(
                    FileUtils.getFileFilter(".json", "Threejs scene file"), 
                true, model.getInputFileName().replace(".osim", ".json"));

        exportAllModelsToJson(fileName);
    }

    public static ModelVisualizationJson exportAllModelsToJson(String fileName) {
        BufferedWriter out = null;
        ModelVisualizationJson vizJson = null;
        try {
            JSONObject jsonTop = ViewDB.getInstance().getJsondb();
            int numModels = OpenSimDB.getInstance().getNumModels();
            // Create Json rep for model
            for (int i=0; i<numModels; i++){
                Model model = OpenSimDB.getInstance().getModelByIndex(i);
                vizJson = new ModelVisualizationJson(jsonTop, model);
                ViewDB.getInstance().addModelVisuals(model, vizJson);
            }
            JSONUtilities.writeJsonFile(jsonTop, fileName);
        } catch (IOException ex) {
            ErrorDialog.displayExceptionDialog(ex);
        } finally {
        }
        return vizJson;
    }


}
