/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.opensim.threejs;

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

    static JSONArray createMatrixFromTransform(Transform xform, double scale) {
        double retTransform[] = new double[]{1, 0, 0, 0, 0, 1, 0 , 0, 0, 0, 1, 0, 0 , 0, 0, 1};
        Rotation r = xform.R();
        Vec3 p = xform.p();
        for (int i=0; i<3; i++){
            for (int j=0; j<3; j++){
                retTransform[i+4*j] = r.asMat33().get(i, j);
            }
            retTransform[12+i] = p.get(i)*scale;
        }
        JSONArray ret = new JSONArray();
        for (int i=0; i<16; i++)
            ret.add(retTransform[i]);
        return ret;
    }

}
