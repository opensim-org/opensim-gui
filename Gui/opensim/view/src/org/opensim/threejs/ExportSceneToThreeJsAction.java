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
@Messages("CTL_ExportSceneToThreeJsAction=Export model to json")
public final class ExportSceneToThreeJsAction implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        
       Model model = OpenSimDB.getInstance().getCurrentModel();
       String fileName = FileUtils.getInstance().browseForFilenameToSave(
                    FileUtils.getFileFilter(".json", "Threejs scene file"), 
                true, model.getInputFileName().replace(".osim", ".json"));

        exportCurrentModelToJson(fileName, model);
    }

    public static VisualizationJson exportAllModelsToJson(String fileName) {
        BufferedWriter out = null;
        VisualizationJson vizJson = null;
        try {
            JSONObject jsonTop = ViewDB.getInstance().getJsondb();
            int numModels = OpenSimDB.getInstance().getNumModels();
            // Create Json rep for model
            for (int i=0; i<numModels; i++){
                vizJson = new VisualizationJson(jsonTop, 
                        OpenSimDB.getInstance().getModelByIndex(i));
            }
            StringWriter outString = new JSONWriter();
            jsonTop.writeJSONString(outString);
            String jsonText = outString.toString();
            out = new BufferedWriter(new FileWriter(fileName, false));
            out.write(jsonText);
            out.flush();
            out.close();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
        }
        return vizJson;
    }
        
    public static VisualizationJson exportCurrentModelToJson(String fileName, Model model) {
        BufferedWriter out = null;
        VisualizationJson vizJson = null;
        try {

            // Create Json rep for model
            vizJson = new VisualizationJson(ViewDB.getInstance().getJsondb(), model);
            JSONObject jsonTop = vizJson.getJson();
            StringWriter outString = new JSONWriter();
            jsonTop.writeJSONString(outString);
            String jsonText = outString.toString();
            out = new BufferedWriter(new FileWriter(fileName, false));
            out.write(jsonText);
            out.flush();
            out.close();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
        }
        return vizJson;
    }

}
