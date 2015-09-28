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
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.json.simple.JSONArray;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.opensim.modeling.Appearance;
import org.opensim.modeling.ArrayDecorativeGeometry;
import org.opensim.modeling.Component;
import org.opensim.modeling.ComponentIterator;
import org.opensim.modeling.ComponentsList;
import org.opensim.modeling.DecorativeGeometry;
import org.opensim.modeling.Model;
import org.opensim.modeling.ModelDisplayHints;
import org.opensim.modeling.Vec3;
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

    @Override
    public void actionPerformed(ActionEvent e) {
        BufferedWriter out = null;
        try {
            //HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
            //server.createContext("/scene", new MyHandler());
            //server.setExecutor(null); // creates a default executor
            //server.start();
            //BrowserLauncher.openURL("http://localhost:8000/scene/v0.html");
            out = new BufferedWriter(new FileWriter("C:\\Dev\\gui\\opensim-gui\\Gui\\opensim\\scene\\scene.json", false));
            // TODO implement action body
            Model model = OpenSimDB.getInstance().getCurrentModel();
            ModelDisplayHints mdh = model.getDisplayHints();
            ComponentsList mcList = model.getComponentsList();
            ComponentIterator mcIter = mcList.begin();
            // This should be initialized from default visResources/mt_scene.js
            JSONArray json_geometries = new JSONArray();
            HashMap<String, UUID> mapGeometryToUUID = new HashMap<String, UUID>() {};
            
            JSONArray json_materials = new JSONArray();
            HashMap<String, UUID> mapMaterialToUUID = new HashMap<String, UUID>() {};

            DecorativeGeometryImplementationJS dgimp = new DecorativeGeometryImplementationJS(json_geometries);
            while (!mcIter.equals(mcList.end())) {
                //System.out.println("Object:Type,Name:"+ mcIter.getConcreteClassName()+","+mcIter.getName());
                Component comp = mcIter.__deref__();
                System.out.println("Comp:"+comp.getConcreteClassName()+"ID="+comp.getPathName());
                ArrayDecorativeGeometry adg = new ArrayDecorativeGeometry();
                comp.generateDecorations(true, mdh, model.getWorkingState(), adg);
                comp.generateDecorations(false, mdh, model.getWorkingState(), adg);
                if (adg.size() > 0) {  // Component has some geometry
                    DecorativeGeometry dg;
                    for (int idx = 0; idx < adg.size(); idx++) {
                        dg = adg.getElt(idx);
                        String geomId =comp.getPathName().concat(String.valueOf(dg.getIndexOnBody()));
                         // pad to 4 hex digits
                        UUID uuid = UUID.randomUUID();
                        mapGeometryToUUID.put(geomId, uuid);
                        System.out.println("hexGeom uuid:"+uuid+" type="+dg.toString());                       
                        dgimp.setGeomID(uuid);
                        dg.implementGeometry(dgimp);
                        // add entry for corresponding Material here
                        UUID uuid_mat = UUID.randomUUID();
                        mapMaterialToUUID.put(geomId, uuid_mat);
                        addMaterialJsonForGeometry(uuid_mat, dg, json_materials);
                    }
                }
                mcIter.next();
            }
            JSONArray json_scene = new JSONArray();
            // Create top level node for one group at ground frame
            createDefaultScene(json_scene, json_geometries, json_materials);
            
            out.flush();
            out.close();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
        }
    }

    private void addMaterialJsonForGeometry(UUID uuid_mat, DecorativeGeometry dg, JSONArray json_materials) {
        Map mat_json = new LinkedHashMap();
        mat_json.put("uuid", uuid_mat);
        /* "type": "MeshPhongMaterial",
			"color": 16711680,
			"emissive": 8453888,
			"specular": 16777215,
			"shininess": 30,
			"vertexColors": 1,
			"shading": 1,
			"transparent": true */
        mat_json.put("type", "MeshPhongMaterial");
        mat_json.put("color", mapColorToHex(dg.getColor()));
        mat_json.put("transparent", dg.getOpacity());        
        json_materials.add(mat_json);
    }

    private String mapColorToHex(Vec3 color) {
        int r = (int) (color.get(0)*256);
        int g = (int) (color.get(1)*256);
        int b = (int) (color.get(2)*256);
        String hexColor = String.format("#%02x%02x%02x", r, g,b);
        return hexColor;
    }

    private void createDefaultScene(JSONArray json_scene, JSONArray json_geometries, JSONArray json_materials) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
