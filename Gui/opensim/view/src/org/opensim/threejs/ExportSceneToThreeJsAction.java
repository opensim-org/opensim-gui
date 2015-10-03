/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.threejs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.opensim.modeling.ArrayDecorativeGeometry;
import org.opensim.modeling.BodiesList;
import org.opensim.modeling.BodyIterator;
import org.opensim.modeling.Component;
import org.opensim.modeling.ComponentIterator;
import org.opensim.modeling.ComponentsList;
import org.opensim.modeling.DecorativeGeometry;
import org.opensim.modeling.Frame;
import org.opensim.modeling.FrameIterator;
import org.opensim.modeling.FramesList;
import org.opensim.modeling.Model;
import org.opensim.modeling.ModelDisplayHints;
import org.opensim.modeling.PhysicalFrame;
import org.opensim.modeling.State;
import org.opensim.modeling.Transform;
import org.opensim.view.BodyDisplayer;
import org.opensim.view.pub.OpenSimDB;

@ActionID(
        category = "File",
        id = "org.opensim.threejs.ExportSceneToThreeJsAction"
)
@ActionRegistration(
        displayName = "#CTL_ExportSceneToThreeJsAction"
)
@ActionReference(path = "Menu/File", position = 1429)
@Messages("CTL_ExportSceneToThreeJsAction=Export to Browser")
public final class ExportSceneToThreeJsAction implements ActionListener {

    private HashMap<Integer, JSONObject> mapBodyIndicesToGroups = new HashMap<Integer, JSONObject>();
    private HashMap<Integer, PhysicalFrame> mapBodyIndicesToFrames = new HashMap<Integer, PhysicalFrame>();
    private State state;
    private double visScaleFactor = 1000.0;
    
    @Override
    public void actionPerformed(ActionEvent e) {
        BufferedWriter out = null;
        try {
            //HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
            //server.createContext("/scene", new MyHandler());
            //server.setExecutor(null); // creates a default executor
            //server.start();
            //BrowserLauncher.openURL("http://localhost:8000/scene/v0.html");
            out = new BufferedWriter(new FileWriter("C:\\Dev\\opensim3js\\scene.js", false));
            // TODO implement action body
            Model model = OpenSimDB.getInstance().getCurrentModel();
            state = model.getWorkingState();
            ModelDisplayHints mdh = model.getDisplayHints();
            ComponentsList mcList = model.getComponentsList();
            ComponentIterator mcIter = mcList.begin();
            // read template that includes default materials and place holders for "geometries", "materials"
            JSONObject jsonTop = loadTemplateJSON();
            if (jsonTop == null){
                // Throw exception, missing resource
            }
            BodiesList bodies = model.getBodiesList();
            BodyIterator body = bodies.begin();
            mapBodyIndicesToFrames.put(0, model.getGround());
            while (!body.equals(bodies.end())) {
                int id = body.getMobilizedBodyIndex();
                // Body group
                //JSONObject json_mobod = createGroupForMoBody(id, physicalFrame);
                //mapBodyIndicesToGroups.put(id, json_mobod);
                mapBodyIndicesToFrames.put(id, PhysicalFrame.safeDownCast(body.__deref__()));
                System.out.println("id="+id+" body ="+body.getName());
                body.next();
            }
            
            // Get reference to geometries, materials and object/children
            JSONArray json_geometries = (JSONArray) jsonTop.get("geometries");
            HashMap<String, UUID> mapGeometryToUUID = new HashMap<String, UUID>() {};
            
            JSONArray json_materials = (JSONArray) jsonTop.get("materials");
            HashMap<String, UUID> mapMaterialToUUID = new HashMap<String, UUID>() {};

            JSONObject sceneObject = (JSONObject) jsonTop.get("object");
            JSONArray json_scene_objects = (JSONArray) sceneObject.get("children");
            DecorativeGeometryImplementationJS dgimp = new DecorativeGeometryImplementationJS(json_geometries, visScaleFactor);
            while (!mcIter.equals(mcList.end())) {
                //System.out.println("Object:Type,Name:"+ mcIter.getConcreteClassName()+","+mcIter.getName());
                Component comp = mcIter.__deref__();
                System.out.println("Comp:"+comp.getConcreteClassName()+"ID="+comp.getPathName());
                ArrayDecorativeGeometry adg = new ArrayDecorativeGeometry();
                comp.generateDecorations(true, mdh, model.getWorkingState(), adg);
                //comp.generateDecorations(false, mdh, model.getWorkingState(), adg);
                if (adg.size() > 0) {  // Component has some geometry
                    DecorativeGeometry dg;
                    for (int idx = 0; idx < adg.size(); idx++) {
                        dg = adg.getElt(idx);
                        String geomId =comp.getPathName().concat(String.valueOf(dg.getIndexOnBody()));
                         // pad to 4 hex digits
                        UUID uuid = UUID.randomUUID();
                        mapGeometryToUUID.put(geomId, uuid);
                        //System.out.println("hexGeom uuid:"+uuid+" type="+dg.toString());                       
                        dgimp.setGeomID(uuid);
                        dg.implementGeometry(dgimp);
                        // add entry for corresponding Material here
                        UUID uuid_mat = UUID.randomUUID();
                        mapMaterialToUUID.put(geomId, uuid_mat);
                        addMaterialJsonForGeometry(uuid_mat, dg, json_materials);
                        addSceneJsonObject(model, dg, geomId, uuid, uuid_mat, json_scene_objects);
                    }
                }
                mcIter.next();
            }
            //JSONArray json_scene = new JSONArray();
            // Create top level node for one group at ground body
            //createDefaultScene(json_scene, json_geometries, json_materials);
            StringWriter outString = new JSONWriter();
            jsonTop.writeJSONString(outString);
            String jsonText = outString.toString();
            //System.out.print(jsonText);
            //System.out.println("---------------");
            System.out.print(jsonText);
            out.write(jsonText);
            out.flush();
            out.close();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
        }
    }

    private void addMaterialJsonForGeometry(UUID uuid_mat, DecorativeGeometry dg, JSONArray json_materials) {
        Map mat_json = new LinkedHashMap();
        mat_json.put("uuid", uuid_mat.toString());
        mat_json.put("type", "MeshPhongMaterial");
        mat_json.put("color", JSONUtilities.mapColorToRGBA(dg.getColor()));
        mat_json.put("shininess", 30);
        mat_json.put("emissive", 0);
        mat_json.put("specular", 1118481);
        double opacity = dg.getOpacity();
        if (opacity < .999){
            mat_json.put("opacity", opacity);  
            mat_json.put("transparent", true);  
        }
        json_materials.add(mat_json);
    }

    private JSONObject loadTemplateJSON() {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = null;
        
        try {
 
            Object obj = parser.parse(new FileReader(
                    "templateScene.json"));
 
            jsonObject = (JSONObject) obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
   }

    private void addSceneJsonObject(Model model, DecorativeGeometry dg, String geomName, UUID uuid, UUID uuid_mat, JSONArray scene_objects) {
        /*
            "uuid": "70FF93EA-A50B-4740-A27A-E4413101C2ED",
            "type": "Mesh",
            "name": "Cylinder 1",
            "matrix": [1,0,0,0,0,1,0,0,0,0,1,0,99.17096710205078,74.63541412353516,0,1],
            "geometry": "EB07513A-4426-450D-AAF8-45A7E8BA6551",
            "material": "B0D463A8-01CD-4BAC-9EB7-8D2E4F780372"
        */
        Map obj_json = new LinkedHashMap();
        obj_json.put("uuid", UUID.randomUUID().toString());
        obj_json.put("type", "Mesh");
        obj_json.put("name", geomName);
        //obj_json.put("matrix", JSONUtilities.stringifyTransform(computeGroundTransform(model, dg)));
        obj_json.put("geometry", uuid.toString());
        obj_json.put("material", uuid_mat.toString());
        int bod = dg.getBodyId();
        Transform relativeTransform = dg.getTransform();
        PhysicalFrame bodyFrame = mapBodyIndicesToFrames.get(bod);
        Transform xform = bodyFrame.getGroundTransform(state);
        Transform fullTransform = xform.compose(relativeTransform);
        obj_json.put("matrix", JSONUtilities.createMatrixFromTransform(fullTransform, visScaleFactor));
        scene_objects.add(obj_json);
    }
    
    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            //URI uri = t.getRequestURI();
            File file = new File("scene/v1.html").getCanonicalFile();
            if (!file.isFile()) {
                // Object does not exist or is not a file: reject with 404 error.
                String response = "404 (Not Found)\n";
                t.sendResponseHeaders(404, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                // Object exists and is a file: accept with response code 200.
                t.sendResponseHeaders(200, 0);
                t.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                OutputStream os = t.getResponseBody();
                FileInputStream fs = new FileInputStream(file);
                final byte[] buffer = new byte[0x10000];
                int count = 0;
                while ((count = fs.read(buffer)) >= 0) {
                    os.write(buffer, 0, count);
                }
                fs.close();
                os.close();
            }
        }
    }
}
