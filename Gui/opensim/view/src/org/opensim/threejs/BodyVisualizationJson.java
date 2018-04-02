/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.threejs;

import java.util.UUID;
import org.json.simple.JSONObject;
import org.opensim.modeling.Body;
import org.opensim.modeling.PhysicalFrame;
import org.opensim.modeling.Transform;
import org.opensim.modeling.Vec3;

/**
 *
 * @author Ayman-NMBL
 */
public class BodyVisualizationJson extends JSONObject{
    private final Vec3 vec3Unit = new Vec3(1.0, 1.0, 1.0);
    private boolean showCom = true;
    private Body body;
    private UUID comMaterialUUID, comObjectUUID, comGeometryUUID;
    
    
    BodyVisualizationJson(Body body, UUID uuid, ModelVisualizationJson modelJson){
        this.body = body;
        put("uuid", uuid.toString());
        put("type", "Group");
        put("opensimType", "Frame");
        put("userData", "NonEditable");
        put("name", body.getAbsolutePathString());
        Transform bodyXform = body.getTransformInGround(modelJson.getState());
        put("matrix", JSONUtilities.createMatrixFromTransform(bodyXform, vec3Unit, modelJson.getVisScaleFactor()));
    }

    public void addBodyCom(ModelVisualizationJson modelJson) {
        modelJson.addFrameJsonObject(body, createComGeometryJson(), createComMaterialJson(), 
                createBodyComObjectJson(modelJson));
    }
    JSONObject createComGeometryJson() {
        JSONObject geomJson = new JSONObject();
        UUID uuidForComGeometry = UUID.randomUUID();
        geomJson.put("uuid", uuidForComGeometry.toString());
        geomJson.put("type", "BoxGeometry");
        geomJson.put("width", 100);
        geomJson.put("height", 100);
        geomJson.put("depth", 100);
        comGeometryUUID = uuidForComGeometry;
        return geomJson;
    }     
    JSONObject createComMaterialJson() {
        JSONObject mat_json = new JSONObject();
        UUID mat_uuid = UUID.randomUUID();
        mat_json.put("uuid", mat_uuid.toString());
        mat_json.put("name", "ComMat");
        mat_json.put("type", "MeshBasicMaterial");
        String colorString = JSONUtilities.mapColorToRGBA(new Vec3(.0, 1.0, 0));
        mat_json.put("color", colorString);
        mat_json.put("side", 2);
        comMaterialUUID = mat_uuid;
        return mat_json;
    }
    
    JSONObject createBodyComObjectJson(ModelVisualizationJson modelJson) {
        JSONObject obj_json = new JSONObject();
        UUID uuidForComObject = UUID.randomUUID();
        comObjectUUID = uuidForComObject;
        obj_json.put("uuid", comObjectUUID.toString());
        obj_json.put("type", "Mesh");
        obj_json.put("name", "Com");
        obj_json.put("opensimType", "ModelCom");
        obj_json.put("geometry", comGeometryUUID.toString());
        obj_json.put("material", comMaterialUUID.toString());
        Transform localTransform = new Transform();
        Vec3 location = body.get_mass_center();
        localTransform.setP(location);
        obj_json.put("matrix", JSONUtilities.createMatrixFromTransform(localTransform, new Vec3(1.), 
                modelJson.getVisScaleFactor()));
        obj_json.put("castShadow", true);
        obj_json.put("visible", showCom);
        return obj_json;
    }
}   
