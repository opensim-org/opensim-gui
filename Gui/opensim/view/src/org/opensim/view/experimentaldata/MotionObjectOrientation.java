/* -------------------------------------------------------------------------- *
 * OpenSim: MotionObjectPointForce.java                                       *
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
package org.opensim.view.experimentaldata;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.UUID;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.opensim.modeling.ArrayDecorativeGeometry;
import org.opensim.modeling.ArrayDouble;
import org.opensim.modeling.Component;
import org.opensim.modeling.DecorativeArrow;
import org.opensim.modeling.Model;
import org.opensim.modeling.ModelDisplayHints;
import org.opensim.modeling.OpenSimContext;
import org.opensim.modeling.PhysicalFrame;
import org.opensim.modeling.Quaternion;
import org.opensim.modeling.Rotation;
import org.opensim.modeling.State;
import org.opensim.modeling.StateVector;
import org.opensim.modeling.Transform;
import org.opensim.modeling.UnitVec3;
import org.opensim.modeling.Vec3;
import org.opensim.threejs.JSONUtilities;
import org.opensim.threejs.ModelVisualizationJson;
import org.opensim.view.motions.MotionDisplayer;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;

/**
 *
 * @author ayman
 */
public class MotionObjectOrientation extends MotionObjectBodyPoint {

    UUID imurep_uuid;
    /**
     * @return the offset
     */
    public String getPointAsString() {

        StringBuilder sbuf = new StringBuilder();
        Formatter fmt = new Formatter(sbuf);
        fmt.format("(%f %f %f)", point[0], point[1], point[2]);
        return sbuf.toString();
    }

    /**
     * @param offset the offset to set
     */
    public void setPointFromString(String offsetString) {
         ArrayDouble d = new ArrayDouble();
         d.fromString(offsetString);
         for (int i=0; i<3; i++)
            point[i] = d.get(i);
         // Update visualization
         Vec3 offsetAsVec3 = new Vec3(point[0], point[1], point[2]);
         ViewDB.getInstance().setObjectTranslationInParentByUuid(imurep_uuid, offsetAsVec3);
         
    }

    private Quaternion quaternion;
    private Vec3 color = new Vec3();
    
    public MotionObjectOrientation(ExperimentalDataItemType objectType, String baseName, int startIndex) {
        super(objectType, baseName, startIndex);
   }

    MotionObjectOrientation(MotionObjectOrientation pf) {
        this(pf.getObjectType(), pf.getName(), pf.getStartIndexInFileNotIncludingTime());
    }
    public String getConcreteClassName() {
        return "Sensor";
    }
    /**
     * @return the quaternion
     */
    public Quaternion getQuaternion() {
        return quaternion;
    }

   @Override
    void updateDecorations(ArrayDouble interpolatedStates) {
        int idx = getStartIndexInFileNotIncludingTime();
        // if Point not in ground, transform into Ground since Arrow is in Ground by default
        // and we don't want to change scene graph layout for easy book-keeping
        //super.setPoint(getOffset());
        quaternion = new Quaternion(interpolatedStates.get(idx), 
                              interpolatedStates.get(idx+1), 
                              interpolatedStates.get(idx+2),
                interpolatedStates.get(idx+3));

    }

    // Create JSON object to represent IMU Sensor
    @Override
    public JSONObject createDecorationJson(ArrayList<UUID> comp_uuids, MotionDisplayer motionDisplayer) {
        // Create Object with proper name, add it to ground, update Map of Object to UUID
        JSONObject expSensor_json = new JSONObject();
        imurep_uuid = UUID.randomUUID(); 
        expSensor_json.put("uuid", imurep_uuid.toString());
        expSensor_json.put("type", "Frame");
        expSensor_json.put("size", ModelVisualizationJson.getVisScaleFactor());
        expSensor_json.put("opensimtype", "ExperimentalSensor");
        expSensor_json.put("name", getName());
        //dir -- direction from origin. Must be a unit vector. 
        //origin -- Point at which the arrow starts.
        //length -- length of the arrow. Default is 1.
        //hex -- hexadecimal value to define color. Default is 0xffff00.
        //headLength -- The length of the head of the arrow. Default is 0.2 * length.
        //headWidth -- The length of the width of the arrow. Default is 0.2 * headLength.
        StateVector dataAtStartTime = motionDisplayer.getSimmMotionData().getStateVector(0);
        ArrayDouble interpolatedStates = dataAtStartTime.getData();
        int idx = getStartIndexInFileNotIncludingTime();
        JSONArray origin = new JSONArray();
        for (int i=0; i<3; i++) origin.add(i, getPoint()[i]);
        expSensor_json.put("origin", origin);
        expSensor_json.put("castShadow", false);
        // Allow Graphical representation to be dragged
        //expSensor_json.put("userData", "NonEditable");
        push_rotation_to_matrix(interpolatedStates, idx, expSensor_json);
        comp_uuids.add(imurep_uuid);
        ((JSONArray)motionDisplayer.getModelVisJson().getModelGroundJson().get("children")).add(expSensor_json);
        return expSensor_json;
    }

    private void push_rotation_to_matrix(ArrayDouble interpolatedStates, int idx, JSONObject expSensor_json) {
        Transform xform = new Transform();
        quaternion = new Quaternion(interpolatedStates.get(idx),
                interpolatedStates.get(idx+1), interpolatedStates.get(idx+2),
                interpolatedStates.get(idx+3));
        Rotation rot = new Rotation(quaternion);
        Vec3 p =new Vec3(point[0], point[1], point[2]);
        xform.set(rot, p);
        expSensor_json.put("matrix", JSONUtilities.createMatrixFromTransform(xform, new Vec3(1, 1 ,1), 
                ModelVisualizationJson.getVisScaleFactor()));
    }
}
