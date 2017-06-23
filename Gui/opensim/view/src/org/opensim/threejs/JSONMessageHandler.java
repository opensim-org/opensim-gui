/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.threejs;

import javax.swing.SwingUtilities;
import org.json.simple.JSONObject;
import org.opensim.modeling.AbstractProperty;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimObject;
import org.opensim.view.nodes.PropertyEditorAdaptor;
import org.opensim.view.pub.ViewDB;

/**
 *
 * @author Ayman
 */
public class JSONMessageHandler {
    public static void handleJSON(final Model model, final OpenSimObject opensimObj, final JSONObject jsonObject){
        final String eventType = (String) jsonObject.get("event");
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {
                if (eventType.equals("select")){
                    ViewDB.getInstance().setSelectedObject(opensimObj);
                }
                else if (eventType.equals("transform-changed")){
                    // Find property and modify
                    // From Visualizer side, we always get "position", "rotation", "scale"
                    // The corresponding Properties are adhoc and vary as follows:
                    if (opensimObj != null && opensimObj.hasProperty("location")){
                        final AbstractProperty ap = opensimObj.getPropertyByName("location");
                        final PropertyEditorAdaptor pea = new PropertyEditorAdaptor(model, opensimObj, ap, null);
                        String locationString = (String) jsonObject.get("location");
                        pea.setValueVec3FromString(convertLocationStringToPropertyFormat(jsonObject));
                        // Tell the world that objects have moved
                        ViewDB.getInstance().objectMoved(model, opensimObj);
                    }
                }
                else if (eventType.equals("geometry-changed")){
                    // update Preoperties from Json
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
 
}
