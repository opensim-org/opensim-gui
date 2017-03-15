/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.threejs;

import java.util.UUID;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.opensim.modeling.AbstractProperty;
import org.opensim.modeling.PropertyHelper;
import org.opensim.modeling.Vec3;

/**
 *
 * @author Ayman
 */
public class CommandComposerThreejs {

    static public JSONObject createSetVisibleCommandJson(boolean newValue, UUID uuid) {
        JSONObject commandJson = new JSONObject();
        commandJson.put("type", "SetValueCommand");
        commandJson.put("name", "SetVisible");
        commandJson.put("attributeName", "visible");
        commandJson.put("newValue", newValue);
        commandJson.put("objectUuid", uuid.toString());
        return commandJson;
    }

    static public JSONObject createTranslateObjectCommandJson(Vec3 newValue, UUID objectuuid) {
         JSONObject commandJson = new JSONObject();
        commandJson.put("type", "SetPositionCommand");
        commandJson.put("name", "Set Position");        // Avoid leading ~ of Vec3.toString
        JSONArray jsonVec3 = new JSONArray();
        jsonVec3.add(newValue.get(0));
        jsonVec3.add(newValue.get(1));
        jsonVec3.add(newValue.get(2));
        JSONArray jsonVec3Old = new JSONArray();
        jsonVec3Old.add(0.); jsonVec3Old.add(0.); jsonVec3Old.add(0.);
        commandJson.put("oldPosition", jsonVec3Old);
        commandJson.put("newPosition", jsonVec3);
        commandJson.put("objectUuid", objectuuid.toString());
        return commandJson;
    }

    static JSONObject createAppearanceChangeJson(AbstractProperty prop, UUID objectUuid) {
        // Map prop.name to threejs command as follows
         if (prop.getName().equalsIgnoreCase("visible")){
             boolean newValue = PropertyHelper.getValueBool(prop);
             return createSetVisibleCommandJson(newValue, objectUuid);
         }
         JSONObject commandJson = new JSONObject();
         return commandJson;
    }
    
}
