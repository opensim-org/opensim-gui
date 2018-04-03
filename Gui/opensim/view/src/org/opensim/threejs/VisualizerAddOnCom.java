/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.threejs;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.opensim.modeling.AbstractOutput;
import org.opensim.modeling.Geometry;
import org.opensim.modeling.OutputVec3;
import org.opensim.modeling.Sphere;
import org.opensim.modeling.Transform;
import org.opensim.modeling.Vec3;

/**
 *
 * @author Ayman-NMBL
 * Visualizer System COM, wiring it to an output and updating
 * based on state. System COM is not a Component
 */
public class VisualizerAddOnCom implements VisualizerAddOn {

    private ModelVisualizationJson modelJson;
    private UUID geometryUUID;
    private UUID materialUUID;
    private UUID objectUUID;
    private Transform transform;
    private OutputVec3 outputVec3;
    
    @Override
    public void init(ModelVisualizationJson modelJson) {
        this.modelJson = modelJson;
        AbstractOutput output = modelJson.getModel().getOutput("com_position");
        outputVec3 = OutputVec3.safeDownCast(output);
        transform = new Transform(outputVec3.getValue(modelJson.getState()));
        modelJson.addFrameJsonObject(modelJson.getModel().getGround(), 
                createGeometryJson(), 
                createMaterialJson(), 
                createSceneObjectJson());

    }

    @Override
    public void updateVisuals(JSONArray frame_jsonArray) {
        JSONObject comUpdate = new JSONObject();
        comUpdate.put("uuid", objectUUID.toString());
        transform = new Transform(outputVec3.getValue(modelJson.getState()));
        comUpdate.put("matrix", JSONUtilities.createMatrixFromTransform(transform, new Vec3(1.), 
                modelJson.getVisScaleFactor()));
        frame_jsonArray.add(comUpdate);

    }

    @Override
    public void cleanup() {
    
    }
    
    JSONObject createGeometryJson() {
        JSONObject geomJson = new JSONObject();
        UUID uuidForComGeometry = UUID.randomUUID();
        geomJson.put("uuid", uuidForComGeometry.toString());
        geomJson.put("type", "SphereGeometry");
        geomJson.put("radius", 50);
        geometryUUID = uuidForComGeometry;
        return geomJson;
    }
    
    JSONObject createMaterialJson() {
        JSONObject mat_json = new JSONObject();
        UUID mat_uuid = UUID.randomUUID();
        mat_json.put("uuid", mat_uuid.toString());
        mat_json.put("name", "ComMat");
        mat_json.put("type", "MeshPhongMaterial");
        mat_json.put("shininess", 30);
        mat_json.put("transparent", true);
        String colorString = JSONUtilities.mapColorToRGBA(new Vec3(.0, 1.0, 0));
        mat_json.put("color", colorString);
        mat_json.put("side", 2);
        materialUUID = mat_uuid;
        return mat_json;
    }
    
    JSONObject createSceneObjectJson() {
        JSONObject obj_json = new JSONObject();
        UUID uuidForComObject = UUID.randomUUID();
        objectUUID = uuidForComObject;
        obj_json.put("uuid", objectUUID.toString());
        obj_json.put("type", "Mesh");
        obj_json.put("name", "Com");
        obj_json.put("opensimType", "ModelCom");
        obj_json.put("geometry", geometryUUID.toString());
        obj_json.put("material", materialUUID.toString());
        obj_json.put("matrix", JSONUtilities.createMatrixFromTransform(transform, new Vec3(1.), 
                modelJson.getVisScaleFactor()));
        obj_json.put("castShadow", true);
        obj_json.put("visible", modelJson.isShowCom());
        return obj_json;
    }
    /**
     * @return the objectUUID
     */
    public UUID getObjectUUID() {
        return objectUUID;
    }
}
