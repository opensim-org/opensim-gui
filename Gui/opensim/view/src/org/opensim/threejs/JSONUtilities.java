/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.opensim.threejs;

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
        JSONObject models_json = new JSONObject();
        models_json.put("uuid", UUID.randomUUID().toString());
        models_json.put("type", "Group");
        models_json.put("name", "Models");
        //System.out.println(model_json.toJSONString());
        JSONArray models_json_arr = new JSONArray();
        models_json.put("children", models_json_arr);
        topLevelJson.put("object", models_json);
        return topLevelJson;
    }
    
    static String mapColorToRGBA(Vec3 color) {
        int r = (int) (color.get(0) * 255);
        int g = (int) (color.get(1) * 255);
        int b = (int) (color.get(2) * 255);
        long colorAsInt = r << 16 | g << 8 | b;
        return String.valueOf(colorAsInt);
    }
    
    static String stringifyVec3(Vec3 vec3) {
        String rawPrintString = vec3.toString();
        return rawPrintString.substring(1);
    }
  
    static String stringifyTransform(Transform xform) {
        String rawPrintString = xform.R().toString();
        return rawPrintString.substring(1);
    }

    static JSONArray createMatrixFromTransform(Transform xform, Vec3 scaleFactors, double scale) {
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

}
