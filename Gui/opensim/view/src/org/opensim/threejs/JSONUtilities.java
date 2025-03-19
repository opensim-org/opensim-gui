/* -------------------------------------------------------------------------- *
 * OpenSim: JSONUtilities.java                                                *
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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.UUID;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.opensim.modeling.Rotation;
import org.opensim.modeling.Transform;
import org.opensim.modeling.Vec3;

/**
 *
 * @author Ayman
 */
public class JSONUtilities {

    static public JSONObject createTopLevelJson() {
        JSONObject topLevelJson = new JSONObject();
        topLevelJson.put("geometries", new JSONArray());
        topLevelJson.put("materials", new JSONArray());
        /*
        JSONObject models_json = new JSONObject();
        models_json.put("uuid", UUID.randomUUID().toString());
        models_json.put("type", "Group");
        models_json.put("name", "Models");
        //System.out.println(model_json.toJSONString());
        JSONArray models_json_arr = new JSONArray();
        models_json.put("children", models_json_arr);
        topLevelJson.put("object", models_json);
        */
        return topLevelJson;
    }
    
    public static String mapColorToRGBA(Vec3 color) {
        int r = (int) (color.get(0) * 255);
        int g = (int) (color.get(1) * 255);
        int b = (int) (color.get(2) * 255);
        long colorAsInt = r << 16 | g << 8 | b;
        return String.valueOf(colorAsInt);
    }
     public static String mapColorToHex(Vec3 color) {
        int r = (int) (color.get(0) * 255);
        int g = (int) (color.get(1) * 255);
        int b = (int) (color.get(2) * 255);
        long colorAsInt = r << 16 | g << 8 | b;
        return String.format("#%06X", (0xFFFFFF & colorAsInt));
    }
   
    static String stringifyVec3(Vec3 vec3) {
        String rawPrintString = vec3.toString();
        return rawPrintString.substring(1);
    }
  
    static String stringifyTransform(Transform xform) {
        String rawPrintString = xform.R().toString();
        return rawPrintString.substring(1);
    }

    static public JSONArray createMatrixFromTransform(Transform xform, Vec3 scaleFactors, double scale) {
        double retTransform[] = new double[]{1, 0, 0, 0, 0, 1, 0 , 0, 0, 0, 1, 0, 0 , 0, 0, 1};
        Rotation r = xform.R();
        Vec3 p = xform.p();
        for (int i=0; i<3; i++){
            for (int j=0; j<3; j++){
                retTransform[i+4*j] = r.asMat33().get(i, j);
                retTransform[i+4*j] *= scaleFactors.get(j);
            }
            retTransform[12+i] = p.get(i)*scale;
        }
        JSONArray ret = new JSONArray();
        for (int i=0; i<16; i++)
            ret.add(retTransform[i]);
        return ret;
    }
    // Method to remove Json of Model from Scene on Model closing
    public static void removeModelJson(JSONObject jsondb, UUID modelUUID) {
        // Find models node and remove modelUUID from list of children
        JSONObject models_json = ((JSONObject) jsondb.get("object"));
        JSONArray models_children = (JSONArray) models_json.get("children");

        for (int index = 0; index < models_children.size(); index++){
            JSONObject nextModelJson = (JSONObject) models_children.get(index);
            String nextModelUUID =  (String) nextModelJson.get("uuid");
            if (nextModelUUID.equals(modelUUID.toString())){
                models_children.remove(index);
                break;
            }
        }
    }

    public static void writeJsonFile(JSONObject jsonTop, String fileName) throws IOException {
        BufferedWriter out;
        StringWriter outString = new JSONWriter();
        jsonTop.writeJSONString(outString);
        out = new BufferedWriter(new FileWriter(fileName, false));
        out.write(outString.toString());
        out.flush();
        out.close();
    }
    
    public static JSONObject createUserDataObject(String osimType, boolean isDraggable) {
        JSONObject userDataJSON = new JSONObject();
        userDataJSON.put("opensimType", osimType);
        userDataJSON.put("draggable", isDraggable);
        return userDataJSON;
    }

}
