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
import java.util.LinkedHashMap;
import java.util.Map;
import org.json.simple.JSONArray;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.opensim.modeling.ArrayDecorativeGeometry;
import org.opensim.modeling.DecorativeGeometry;
import org.opensim.modeling.Model;
import org.opensim.modeling.ModelDisplayHints;
import org.opensim.modeling.Vec3;
import org.opensim.view.pub.OpenSimDB;

@ActionID(
        category = "File",
        id = "org.opensim.threejs.ExportSceneToThreeJsAction"
)
@ActionRegistration(
        displayName = "#CTL_ExportSceneToThreeJsAction"
)
@ActionReference(path = "Menu/File", position = 1429)
@Messages("CTL_ExportSceneToThreeJsAction=Export to Browser")
public final class ExportSceneToThreeJsAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        BufferedWriter out = null;
        try {
            // TODO implement action body
            Model model = OpenSimDB.getInstance().getCurrentModel();
            ModelDisplayHints mdh = model.getDisplayHints();
            ArrayDecorativeGeometry adg = new ArrayDecorativeGeometry();
            model.generateDecorations(true, mdh, model.getWorkingState(), adg);
            // This should be initialized from default visResources/mt_scene.js
            JSONArray jsona = new JSONArray();
            if (adg.size()>0){  // Component has some geometry
                DecorativeGeometry dg;
                for (int idx=0; idx <adg.size(); idx++){
                    dg = adg.getElt(idx);
                    Map dg_json=new LinkedHashMap();
                    /*
                                        "geometry" : "cubeNormals",
					"material" : "phong_red",
					"position" : [ 0, 0, 0 ],
					"rotation" : [ 0, -0.3, 0 ],
					"scale"	   : [ 1, 1, 1 ],
					"visible"  : true,
                    */
                    dg_json.put("geomerty","Geometry_"+String.valueOf(idx));
                    dg_json.put("position",new Vec3(dg.getTransform().p()));
                    dg_json.put("rotation",new Vec3(dg.getTransform().R().convertRotationToBodyFixedXYZ()));
                    dg_json.put("scale",dg.getScaleFactors());
                    jsona.add(dg_json);
                }
            }   
            out = new BufferedWriter(new FileWriter("C:/Demo40/test_2.json", true));
            jsona.writeJSONString(out);
            ArrayDecorativeGeometry avdg = new ArrayDecorativeGeometry();
            model.generateDecorations(false, mdh, model.getWorkingState(), avdg);
            if (avdg.size()>0){  // Component has some geometry
                DecorativeGeometry dg;
                for (int idx=0; idx <avdg.size(); idx++){
                    dg = adg.getElt(idx);
                    //dg.implementGeometry(dgi);
                }
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
