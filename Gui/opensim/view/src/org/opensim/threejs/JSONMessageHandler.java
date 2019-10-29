/* -------------------------------------------------------------------------- *
 * OpenSim: JSONMessageHandler.java                                           *
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

import java.util.Formatter;
import java.util.UUID;
import javax.swing.SwingUtilities;
import org.json.simple.JSONObject;
import org.opensim.modeling.AbstractProperty;
import org.opensim.modeling.Marker;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.PathPoint;
import org.opensim.modeling.Vec3;
import org.opensim.view.experimentaldata.MotionObjectOrientation;
import org.opensim.view.nodes.MarkerAdapter;
import org.opensim.view.nodes.PathPointAdapter;
import org.opensim.view.nodes.PropertyEditorAdaptor;
import org.opensim.view.pub.ViewDB;

/**
 *
 * @author Ayman
 */
public class JSONMessageHandler {
    static Boolean debug = false;
    public static void handleJSON(final Model model, final OpenSimObject opensimObj, final JSONObject jsonObject){
        if (debug)
            System.out.println("Received Message "+jsonObject.toString());
        final String eventType = (String) jsonObject.get("event");
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {
                if (eventType.equals("select")){
                    ViewDB.getInstance().setSelectedObject(opensimObj);
                }
                else if (eventType.equals("translate")){
                    // Find property and modify
                    UUID objUuid = UUID.fromString((String) jsonObject.get("uuid"));
                    OpenSimObject opensimObj = ViewDB.getInstance().getObjectFromUUID(objUuid);
                    // From Visualizer side, we always get "position", "rotation", "scale"
                    // The corresponding Properties are adhoc and vary as follows:
                    if (PathPoint.safeDownCast(opensimObj)!=null){
                        PathPoint ppt = PathPoint.safeDownCast(opensimObj);
                        PathPointAdapter pptAdapter = new PathPointAdapter(ppt);
                        JSONObject locationJson = (JSONObject) jsonObject.get("location");
                        Vec3 locationVec3 = convertJsonXYZToVec3(locationJson);
                        pptAdapter.setLocation(locationVec3, true);
                        return;
                    }
                    if (Marker.safeDownCast(opensimObj)!=null){
                        Marker mkr = Marker.safeDownCast(opensimObj);
                        MarkerAdapter markerAdapter = new MarkerAdapter(mkr);
                        JSONObject locationJson = (JSONObject) jsonObject.get("location");
                        Vec3 locationVec3 = convertJsonXYZToVec3(locationJson);
                        markerAdapter.setLocation(locationVec3, true);
                        return;
                    }
                    if (opensimObj != null && opensimObj instanceof MotionObjectOrientation){
                        MotionObjectOrientation morientObj = (MotionObjectOrientation) opensimObj;
                        JSONObject locationJson = (JSONObject) jsonObject.get("location");
                        Vec3 locationVec3 = convertJsonXYZToVec3(locationJson);
                        StringBuilder sbuf = new StringBuilder();
                        Formatter fmt = new Formatter(sbuf);
                        fmt.format("(%f %f %f)", locationVec3.get(0), locationVec3.get(1), locationVec3.get(2));
                        morientObj.setPointFromString(fmt.toString());
                        return;
                    }
                    if (opensimObj != null && opensimObj.hasProperty("location")){
                        final AbstractProperty ap = opensimObj.getPropertyByName("location");
                        final PropertyEditorAdaptor pea = new PropertyEditorAdaptor(model, opensimObj, ap, null);
                        JSONObject locationJson = (JSONObject) jsonObject.get("location");
                        Vec3 locationVec3 = convertJsonXYZToVec3(locationJson);
                        // Convert from ground frame to object's frame
                        pea.setValueVec3(locationVec3, true);
                        // Tell the world that objects have moved
                        ViewDB.getInstance().objectMoved(model, opensimObj);
                    }
                }
            }
       
         });
        
    }
    static void setTransformPropertiesFromJSON(final OpenSimObject opensimObj, JSONObject jsonObject){
        
    }
    static void setGeometryPropertiesFromJSON(final OpenSimObject opensimObj, JSONObject jsonObject){
        
    }
    static void setAppearancePropertiesFromJSON(OpenSimObject opensimObj, JSONObject jsonObject){
        
    }
    static String convertLocationStringToPropertyFormat(JSONObject jsonObject) {
          JSONObject positionObj = (JSONObject) jsonObject.get("position");
          String returnString = "";
          double x, y, z;
          double relativeScale = ModelVisualizationJson.getVisScaleFactor();
          Object xObj = positionObj.get("x");
          double xDouble, yDouble, zDouble;
          if (xObj instanceof Long){
              xDouble = (Double)(((Long)xObj)/relativeScale);
              Object yObj = positionObj.get("y");
              yDouble = (Double)(((Long)yObj)/relativeScale);
              Object zObj = positionObj.get("z");
              zDouble = (Double)(((Long)zObj)/relativeScale);
          }
          else {
              xDouble = (Double)xObj/relativeScale;
              Object yObj = positionObj.get("y");
              yDouble = (Double)yObj/relativeScale;
              Object zObj = positionObj.get("z");
              zDouble = (Double)zObj/relativeScale;
        }
          returnString = returnString.concat(String.valueOf(xDouble));
          returnString = returnString.concat(" ");
          returnString = returnString.concat(String.valueOf(yDouble));
          returnString = returnString.concat(" ");
          returnString = returnString.concat(String.valueOf(zDouble));
          return returnString;
    }
    static public double convertObjectFromJsonToDouble(Object obj) {
        double value = 0.0;
        if (obj instanceof Long){
            Long l = (Long) obj;
            value = l;
        }
        if (obj instanceof Double){
            value = (Double) obj;
        }
        return value;
    }
    static public Vec3 convertJsonXYZToVec3(JSONObject offsetObj) {
        double relativeScale = ModelVisualizationJson.getVisScaleFactor();
        Object xString = offsetObj.get("x");
        double xValue = JSONMessageHandler.convertObjectFromJsonToDouble(xString)/relativeScale;
        Object yString = offsetObj.get("y");
        double yValue = JSONMessageHandler.convertObjectFromJsonToDouble(yString)/relativeScale;
        Object zString = offsetObj.get("z");
        double zValue = JSONMessageHandler.convertObjectFromJsonToDouble(zString)/relativeScale;
        Vec3 offsetAsVec3 = new Vec3(xValue, yValue, zValue);
        return offsetAsVec3;
    }

}
