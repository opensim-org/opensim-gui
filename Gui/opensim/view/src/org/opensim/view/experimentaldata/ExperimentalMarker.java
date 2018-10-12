/* -------------------------------------------------------------------------- *
 * OpenSim: ExperimentalMarker.java                                           *
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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.opensim.modeling.ArrayDecorativeGeometry;
import org.opensim.modeling.ArrayDouble;
import org.opensim.modeling.DecorativeSphere;
import org.opensim.modeling.ModelDisplayHints;
import org.opensim.modeling.State;
import org.opensim.modeling.StateVector;
import org.opensim.modeling.Transform;
import org.opensim.modeling.Vec3;
import org.opensim.view.motions.MotionDisplayer;

/**
 *
 * @author ayman
 */

/*
 * An Object representing an Experimental Marker
 */
public class ExperimentalMarker extends MotionObjectBodyPoint {
 
    private String markerName;
    private double conversion = 1.0;
    public ExperimentalMarker(ExperimentalDataItemType objectType, String baseName, int index) {
        super(objectType, baseName, index);
    }
    
    public String getConcreteClassName() {
        return "Experimental Marker";
    }
    /**
     * @return the markerName
     */
    public String getMarkerName() {
        return markerName;
    }

    /**
     * @param markerName the markerName to set
     */
    public void setMarkerName(String markerName) {
        this.markerName = markerName;
    }


    public void generateDecorations(boolean fixed, ModelDisplayHints hints, State state, ArrayDecorativeGeometry appendToThis) {
        if (!fixed){
            Transform xform = new Transform();
            xform.setP(new Vec3(point[0], point[1], point[2]));
            appendToThis.push_back(new DecorativeSphere(0.007).setBodyId(0).setColor(new Vec3(0., 1., 1.0)).setOpacity(0.5).setIndexOnBody(getStartIndexInFileNotIncludingTime()).setTransform(xform));            
        }
    }

    @Override
    void updateDecorations(ArrayDouble interpolatedStates) {
        int idx = getStartIndexInFileNotIncludingTime();
        setPoint(new double[]{interpolatedStates.get(idx)/conversion, 
            interpolatedStates.get(idx+1)/conversion, 
            interpolatedStates.get(idx+2)/conversion});
    }
    
    // Create JSON object to represent ExperimentalMarker
    @Override
    public JSONObject createDecorationJson(ArrayList<UUID> comp_uuids, MotionDisplayer motionDisplayer) {
        // Create Object with proper name, add it to ground, update Map of Object to UUID
        JSONObject expMarker_json = new JSONObject();
        UUID mesh_uuid = UUID.randomUUID();
        expMarker_json.put("uuid", mesh_uuid.toString());
        expMarker_json.put("type", "Mesh");
        expMarker_json.put("opensimtype", "ExperimentalMarker");
        expMarker_json.put("name", getName());
        expMarker_json.put("geometry", motionDisplayer.getExperimentalMarkerGeometryJson().get("uuid"));
        expMarker_json.put("material", motionDisplayer.getExperimentalMarkerMaterialJson().get("uuid"));
        StateVector dataAtStartTime = motionDisplayer.getSimmMotionData().getStateVector(0);
        ArrayDouble interpolatedStates = dataAtStartTime.getData();
        int idx = getStartIndexInFileNotIncludingTime();
        // By construction ExperimentalMarkers come through AnnotatedMotion which handles unit conversion 
        // get unit conversion once and save locally, convert data on the fly
        // TODO: investigate conversion once on loading
        // TODO: Convert to use opensim-core libraries to do the reading/conversion
        AnnotatedMotion mot = (AnnotatedMotion)motionDisplayer.getSimmMotionData();
        conversion = mot.getUnitConversion();
        JSONArray pos = new JSONArray();
        for (int i = 0; i < 3; i++) {
            pos.add(interpolatedStates.get(idx+i)/conversion);
        }
        expMarker_json.put("position", pos);
        expMarker_json.put("castShadow", false);
        expMarker_json.put("userData", "NonEditable");
        
        comp_uuids.add(mesh_uuid);
        return expMarker_json;
    }

    
 }
