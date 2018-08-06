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
import org.opensim.modeling.State;
import org.opensim.modeling.StateVector;
import org.opensim.modeling.Transform;
import org.opensim.modeling.UnitVec3;
import org.opensim.modeling.Vec3;
import org.opensim.threejs.JSONUtilities;
import org.opensim.threejs.ModelVisualizationJson;
import org.opensim.view.motions.MotionDisplayer;
import org.opensim.view.pub.OpenSimDB;

/**
 *
 * @author ayman
 */
public class MotionObjectPointForce extends MotionObjectBodyPoint {

    double[] offset = new double[]{0, 0, 0};
    String forceIdentifier="";
    String forceExpressedInBodyName = "ground";
    private String forceAppliedToBody = "ground";

    private boolean specifyPoint;
    private String forceComponent = "All";
    private String torqueIdentifier="";
    private boolean specifyTorque=false;
    private Vec3 direction = new Vec3();
    private Vec3 color = new Vec3();
    private double length= 1.0;
    
    public MotionObjectPointForce(ExperimentalDataItemType objectType, String baseName, int forceIndex) {
        super(objectType, baseName, forceIndex);
        setForceIdentifier(baseName);
   }

    MotionObjectPointForce(MotionObjectPointForce pf) {
        this(pf.getObjectType(), pf.getName(), pf.getStartIndexInFileNotIncludingTime());
        offset = pf.offset;
        forceIdentifier = pf.forceIdentifier;
        forceExpressedInBodyName = pf.forceExpressedInBodyName;
        forceAppliedToBody = pf.forceAppliedToBody;
        specifyPoint = pf.specifyPoint;
        forceComponent = pf.forceComponent;
        torqueIdentifier = pf.torqueIdentifier;
        specifyTorque = pf.specifyTorque;
    }

    void setForceExpressedInBodyName(String selected) {
        forceExpressedInBodyName = selected;
    }

    public void setForceIdentifier(String makeIdentifier) {
        forceIdentifier = makeIdentifier;
    }

    String getForceExpressedInBodyName() {
        return forceExpressedInBodyName;
    }

    boolean appliesForce() {
        return true;
    }

    public String getForceIdentifier() {
        return forceIdentifier;
    }

    
    boolean specifiesPoint() {
        return specifyPoint;
    }

    @Override
    public void setObjectType(ExperimentalDataItemType objectType) {
        super.setObjectType(objectType);
        if (objectType==ExperimentalDataItemType.BodyForceData)
            specifyPoint = false;
        else
            specifyPoint = true;
    }

    /**
     * @return the forceComponent
     */
    public String getForceComponent() {
        return forceComponent;
    }

    /**
     * @param forceComponent the forceComponent to set
     */
    public void setForceComponent(String forceComponent) {
        this.forceComponent = forceComponent;
    }

    /**
     * @return the forceAppliedToBody
     */
    public String getForceAppliedToBody() {
        return forceAppliedToBody;
    }

    /**
     * @param forceAppliedToBody the forceAppliedToBody to set
     */
    public void setForceAppliedToBody(String forceAppliedToBody) {
        this.forceAppliedToBody = forceAppliedToBody;
    }

    /**
     * @return the specifyTorque
     */
    public boolean isSpecifyTorque() {
        return specifyTorque;
    }

    /**
     * @param specifyTorque the specifyTorque to set
     */
    public void setSpecifyTorque(boolean specifyTorque) {
        this.specifyTorque = specifyTorque;
    }

    /**
     * @return the torqueIdentifier
     */
    public String getTorqueIdentifier() {
        return torqueIdentifier;
    }

    /**
     * @param torqueIdentifier the torqueIdentifier to set
     */
    public void setTorqueIdentifier(String torqueIdentifier) {
        this.torqueIdentifier = torqueIdentifier;
    }
    
    @Override
    public void generateDecorations(boolean fixed, ModelDisplayHints hints, State state, ArrayDecorativeGeometry appendToThis) {
        if (!fixed){
            Transform xform = new Transform();
            
            xform.setP(new Vec3(point[0], point[1], point[2]));
            appendToThis.push_back(new DecorativeArrow(new Vec3(0, 0, 0)).setBodyId(0).setColor(color).setOpacity(0.5).setIndexOnBody(getStartIndexInFileNotIncludingTime()).setTransform(xform));            
        }
    }
   @Override
    void updateDecorations(ArrayDouble interpolatedStates) {
        int idx = getStartIndexInFileNotIncludingTime();
        // if Point not in ground, transform into Ground since Arrow is in Ground by default
        // and we don't want to change scene graph layout for easy book-keeping
        super.setPoint(new double[]{interpolatedStates.get(idx + 3), interpolatedStates.get(idx + 4), interpolatedStates.get(idx + 5)});
        double[] forceLocal = new double[]{interpolatedStates.get(idx), 
                              interpolatedStates.get(idx+1), 
                              interpolatedStates.get(idx+2)};
        if (!forceExpressedInBodyName.equalsIgnoreCase("ground")){
            Vec3 localDirection = new Vec3();
            for (int i=0; i<3; i++) 
                localDirection.set(i, forceLocal[i]);
            applyForceComponent(localDirection, interpolatedStates, idx);
            for (int i=0; i<3; i++) 
                forceLocal[i] = localDirection.get(i);
            // Convertback to double[]
            double[] forceGlobal = new double[3]; 
            Model model = getModel();
            OpenSimContext dContext= OpenSimDB.getInstance().getContext(model);
            Component c = model.getComponent(getForceExpressedInBodyName());
            PhysicalFrame f = PhysicalFrame.safeDownCast(c);
            dContext.transform(f, forceLocal, model.get_ground(), forceGlobal);
            for (int i=0; i<3; i++) 
                getDirection().set(i, forceGlobal[i]);
        }
        else
            applyForceComponent(getDirection(), interpolatedStates, idx);
    }

    private void applyForceComponent(Vec3 direction1, ArrayDouble interpolatedStates, int idx) {
        if (forceComponent.equalsIgnoreCase("all")) {
            for (int i = 0; i < 3; i++) {
                direction1.set(i, interpolatedStates.get(idx + i));
            }
        } else {
            String componentNames = "xyz";
            int componentIndex = componentNames.indexOf(forceComponent);
            for (int i = 0; i < 3; i++) {
                direction1.set(i, 0);
            }
            direction1.set(componentIndex, interpolatedStates.get(idx + componentIndex));
        }
    }
    // Create JSON object to represent ExperimentalForce
    @Override
    public JSONObject createDecorationJson(ArrayList<UUID> comp_uuids, MotionDisplayer motionDisplayer) {
        
        // Create Object with proper name, add it to ground, update Map of Object to UUID
        JSONObject expForce_json = new JSONObject();
        UUID forcrep_uuid = UUID.randomUUID(); //3f63, acf9
        expForce_json.put("uuid", forcrep_uuid.toString());
        expForce_json.put("type", "Arrow");
        expForce_json.put("opensimtype", "ExperimentalForce");
        expForce_json.put("name", getName());
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
        for (int i = 0; i < 3; i++) {
            origin.add(interpolatedStates.get(idx+3+i));
            point[i]=interpolatedStates.get(idx+3+i);
        }
        expForce_json.put("origin", origin);
        expForce_json.put("length", 1.0);
        JSONArray dir = new JSONArray();
        for (int i = 0; i < 3; i++) {
            dir.add(interpolatedStates.get(idx+i));
            direction.set(i, interpolatedStates.get(idx+i));
        }
        expForce_json.put("dir", dir);
        expForce_json.put("castShadow", false);
        expForce_json.put("userData", "NonEditable");
        expForce_json.put("color", JSONUtilities.mapColorToHex(motionDisplayer.getDefaultForceColorVec3()));
        Transform xform = new Transform();
        double length = Math.sqrt(Math.pow(direction.get(0),2)+
                Math.pow(direction.get(1),2)+Math.pow(direction.get(2),2))/1000;
        UnitVec3 dirNorm = new UnitVec3(direction);
        xform.setP(new Vec3(point[0], point[1], point[2]));
        for (int i=0; i<3; i++)
              xform.R().set(i, 1, dirNorm.get(i));
        expForce_json.put("matrix", JSONUtilities.createMatrixFromTransform(xform, new Vec3(1, length ,1), 
                ModelVisualizationJson.getVisScaleFactor()));
        comp_uuids.add(forcrep_uuid);
        return expForce_json;
    }

    /**
     * @return the direction
     */
    public Vec3 getDirection() {
        return direction;
    }

    /**
     * @return the color
     */
    public Vec3 getColor() {
        return color;
    }

    /**
     * @return the length
     */
    public double getLength() {
        return length;
    }

}
