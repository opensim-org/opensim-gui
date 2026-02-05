/* -------------------------------------------------------------------------- *
 * OpenSim: AnimationJson.java                                       *
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

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.Vector;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.opensim.modeling.ArrayDouble;
import org.opensim.modeling.Frame;
import org.opensim.modeling.Model;
import org.opensim.modeling.PhysicalFrame;
import org.opensim.modeling.Quaternion;
import org.opensim.modeling.Rotation;
import org.opensim.modeling.State;
import org.opensim.modeling.StatesTrajectory;
import org.opensim.modeling.Storage;
import org.opensim.modeling.Transform;
import org.opensim.modeling.Vec3;
import org.opensim.view.experimentaldata.AnnotatedMotion;
import org.opensim.view.experimentaldata.ExperimentalDataObject;


/**
 *
 * @author Ayman
 */
public class AnimationJson extends JSONObject {
    public AnimationJson() {
        
    }
    public AnimationJson(AnnotatedMotion mot, Vector<ExperimentalDataObject> objects) {
        put("name", mot.getName());
        JSONArray animationsTracks = new JSONArray();
        put("tracks", animationsTracks);
        double[] times = new double[mot.getSize()];
        ArrayDouble timeArray = new ArrayDouble();
        mot.getTimeColumn(timeArray);
        for(int i=0; i< mot.getSize(); i++){
          times[i]=timeArray.get(i);
        }
        
        put("duration", times[times.length-1]);
        put("uuid", UUID.randomUUID().toString());
        // Create a track for time, tracks[objIndex], with name expObj.position
        JSONArray timesJson = JSONUtilities.createFromArrayDouble(times);
        for (ExperimentalDataObject expObj: objects){
            JSONArray objTracks = expObj.createAnimationTracks(mot);
            for (int tr=0; tr < objTracks.size(); tr++){
                JSONObject nextTrack = (JSONObject) objTracks.get(tr);
                nextTrack.put("times", timesJson);
                animationsTracks.add(nextTrack);
            }
        }
    }

    public AnimationJson(Storage mot, ModelVisualizationJson modelVisJson) {
        put("name", mot.getName());
        JSONArray animationsTracks = new JSONArray();
        put("tracks", animationsTracks);
        // Convert mot into a StateTrajectory so that we can get states one at a time;
        Model model = modelVisJson.getModel();
        StatesTrajectory trajectory = StatesTrajectory.createFromStatesStorage(model, mot);


        HashMap<Integer, PhysicalFrame> mapIndexToFrame = modelVisJson.getMapBodyIndicesToFrames();
        int numFrames = mapIndexToFrame.size()-1; // Exclude ground as not moving
        Frame[] frames = new Frame[numFrames];
        for (int iFrame=0; iFrame< numFrames; iFrame++){
            frames[iFrame]=mapIndexToFrame.get(iFrame+1);
        }        
        double[] times = new double[mot.getSize()];
        double[][] translationData = new double[numFrames][mot.getSize()*3];
        double[][] rotationData = new double[numFrames][mot.getSize()*4];
        for (int iState=0; iState < times.length; iState++){
            State nextState = trajectory.get(iState);
            times[iState] = nextState.getTime();
            model.realizeDynamics(nextState);
            for (int iFrame=0; iFrame< numFrames; iFrame++){
                // Get transform for Frame iFrame, convert into pos, quaternion then append to tracks.
                Transform xform = frames[iFrame].getTransformInGround(nextState);
                Vec3 translation = xform.T();
                for (int c=0; c<3; c++) 
                    translationData[iFrame][iState*3+c] = translation.get(c);
                Rotation rot = xform.R();
                Quaternion quat = rot.convertRotationToQuaternion();
                rotationData[iFrame][iState*4+3]=quat.get(0);
                for (int c=0; c<3; c++) 
                    rotationData[iFrame][iState*4+c] = quat.get(c+1);
            }
        }
        put("duration", times[times.length-1]);
        put("uuid", UUID.randomUUID().toString());
        // Create a track for time, translationData, rotationData
        for (int iFrame=0; iFrame< numFrames; iFrame++){
            JSONObject positionTrack = new JSONObject();
            String frameName = frames[iFrame].getName();
            positionTrack.put("name", frameName+".position");
            positionTrack.put("type", "vector");
            positionTrack.put("times", JSONUtilities.createFromArrayDouble(times));
            positionTrack.put("values", JSONUtilities.createFromArrayDouble(translationData[iFrame]));
            //animationTrack.put("interpolation", "Linear");
            animationsTracks.add(positionTrack);
            
            JSONObject orientationTrack = new JSONObject();
            orientationTrack.put("name", frameName+".quaternion");
            orientationTrack.put("type", "quaternion");
            orientationTrack.put("times", JSONUtilities.createFromArrayDouble(times));
            orientationTrack.put("values", JSONUtilities.createFromArrayDouble(rotationData[iFrame]));
            //animationTrack.put("interpolation", "Linear");
            animationsTracks.add(orientationTrack);
        }
    }
}
