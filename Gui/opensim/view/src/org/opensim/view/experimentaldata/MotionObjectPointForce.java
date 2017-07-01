/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.view.experimentaldata;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.opensim.modeling.ArrayDecorativeGeometry;
import org.opensim.modeling.ArrayDouble;
import org.opensim.modeling.DecorativeArrow;
import org.opensim.modeling.DecorativeSphere;
import org.opensim.modeling.ModelDisplayHints;
import org.opensim.modeling.State;
import org.opensim.modeling.Transform;
import org.opensim.modeling.Vec3;
import org.opensim.threejs.JSONUtilities;
import org.opensim.view.motions.MotionDisplayer;

/**
 *
 * @author ayman
 * Copyright (c)  2005-2012, Stanford University, Ayman Habib
 * Use of the OpenSim software in source form is permitted provided that the following
 * conditions are met:
 * 	1. The software is used only for non-commercial research and education. It may not
 *     be used in relation to any commercial activity.
 * 	2. The software is not distributed or redistributed.  Software distribution is allowed
 *     only through https://simtk.org/home/opensim.
 * 	3. Use of the OpenSim software or derivatives must be acknowledged in all publications,
 *      presentations, or documents describing work in which OpenSim or derivatives are used.
 * 	4. Credits to developers may not be removed from executables
 *     created from modifications of the source.
 * 	5. Modifications of source code must retain the above copyright notice, this list of
 *     conditions and the following disclaimer.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 *  SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 *  TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 *  HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR BUSINESS INTERRUPTION) OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 *  WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
    
    public void generateDecorations(boolean fixed, ModelDisplayHints hints, State state, ArrayDecorativeGeometry appendToThis) {
        if (!fixed){
            Transform xform = new Transform();
            
            xform.setP(new Vec3(point[0], point[1], point[2]));
            appendToThis.push_back(new DecorativeArrow(new Vec3(0, 0, 0)).setBodyId(0).setColor(new Vec3(0., 1., .0)).setOpacity(0.5).setIndexOnBody(getStartIndexInFileNotIncludingTime()).setTransform(xform));            
        }
    }

    @Override
    void updateDecorations(ArrayDouble interpolatedStates) {
        int idx = getStartIndexInFileNotIncludingTime();
        setPoint(new double[]{interpolatedStates.get(idx+3), interpolatedStates.get(idx+4), interpolatedStates.get(idx+5)});
        for (int i=0; i<3; i++) 
            getDirection().set(i, interpolatedStates.get(idx+i));
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
        JSONArray origin = new JSONArray();
        for (int i = 0; i < 3; i++) {
            origin.add(0.0);
        }
        expForce_json.put("origin", origin);
        expForce_json.put("length", 1.0);
        JSONArray dir = new JSONArray();
        for (int i = 0; i < 3; i++) {
            dir.add((i == 1) ? 1.0 : 0.0);
        }
        expForce_json.put("dir", dir);
        expForce_json.put("castShadow", false);
        expForce_json.put("userData", "NonEditable");
        expForce_json.put("color", JSONUtilities.mapColorToHex(motionDisplayer.getDefaultForceColorVec3()));
        expForce_json.put("matrix", JSONUtilities.createMatrixFromTransform(new Transform(), new Vec3(1.), 1.0));
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
