/* -------------------------------------------------------------------------- *
 * OpenSim: DecorativeGeometryImplementationJS.java                           *
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

import java.util.UUID;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.opensim.modeling.DecorativeArrow;
import org.opensim.modeling.DecorativeBrick;
import org.opensim.modeling.DecorativeCircle;
import org.opensim.modeling.DecorativeCone;
import org.opensim.modeling.DecorativeCylinder;
import org.opensim.modeling.DecorativeEllipsoid;
import org.opensim.modeling.DecorativeFrame;
import org.opensim.modeling.DecorativeGeometry;
import org.opensim.modeling.DecorativeGeometryImplementation;
import org.opensim.modeling.DecorativeLine;
import org.opensim.modeling.DecorativeMesh;
import org.opensim.modeling.DecorativeMeshFile;
import org.opensim.modeling.DecorativePoint;
import org.opensim.modeling.DecorativeSphere;
import org.opensim.modeling.DecorativeText;
import org.opensim.modeling.DecorativeTorus;
import org.opensim.modeling.PolygonalMesh;
import org.opensim.modeling.Vec3;
import org.opensim.view.pub.GeometryFileLocator;


/**
 *
 * @author Ayman
 */ 
public class DecorativeGeometryImplementationJS extends DecorativeGeometryImplementation {

    /**
     * @return the supported
     */
    public boolean isSupported() {
        return supported;
    }
    private JSONArray jsonArr;
    private JSONArray json_materials;
    private UUID geomID;
    private UUID mat_uuid;
    private boolean reuse_material = false;
    private double visualizerScaleFactor = 100;
    boolean updateMode = false;
    private JSONObject last_json = null;
    private String quadrants = "";
    boolean debug = false;
    // Flag indicating whether specific DecorativeGeometry subtypes is directly supported
    // Point, Line, Circle and Frame are not suuported at this level
    private boolean supported = true; 
    
    public DecorativeGeometryImplementationJS(JSONArray jsonArr, JSONArray jsonArrMaterials, double scale) {
        this.jsonArr = jsonArr;
        this.json_materials = jsonArrMaterials;
        this.visualizerScaleFactor = scale;
        if (debug)
            System.out.println("Looking for Geometry files in :"+GeometryFileLocator.geometryInstallationDirectory);
    }
    
    @Override
    public void implementConeGeometry(DecorativeCone arg0) {
        JSONObject dg_json = createJsonForDecorativeCone(arg0);
        jsonArr.add(dg_json);     
        createMaterialJson(arg0, true);
    }

    public JSONObject createJsonForDecorativeCone(DecorativeCone arg0) {
        JSONObject dg_json = new JSONObject();
        dg_json.put("uuid", geomID.toString());
        dg_json.put("type", "CylinderGeometry");
        dg_json.put("radiusTop", 0.);
        dg_json.put("radiusBottom", arg0.getBaseRadius()*visualizerScaleFactor);
        dg_json.put("height", arg0.getHeight()*visualizerScaleFactor);
        dg_json.put("radialSegments", 32);
        dg_json.put("heightSegments", 1);
        return dg_json;
    }

    @Override
    public void implementArrowGeometry(DecorativeArrow arg0) {
        //super.implementArrowGeometry(arg0); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void implementTorusGeometry(DecorativeTorus arg0) {
        /*
            "type": "TorusGeometry",
            "radius": 100,
            "tube": 40,
            "radialSegments": 8,
            "tubularSegments": 6,
            "arc": 2*Math.PI07179586 */
        JSONObject dg_json = new JSONObject();
        dg_json.put("uuid", geomID.toString());
        dg_json.put("type", "TorusGeometry");
	dg_json.put("radius", arg0.getTorusRadius()*visualizerScaleFactor);
	dg_json.put("tube", arg0.getTubeRadius()*visualizerScaleFactor);
	dg_json.put("radialSegments", 32);
	dg_json.put("tubularSegments", 24);
        jsonArr.add(dg_json);  
        createMaterialJson(arg0, true);
     }

    @Override
    public void implementMeshFileGeometry(DecorativeMeshFile arg0) {
        //super.implementMeshFileGeometry(arg0); //To change body of generated methods, choose Tools | Templates.
        String meshFile = arg0.getMeshFile();
        String fullFileName = GeometryFileLocator.getInstance().getFullname("",meshFile, false);
        if (debug)
            System.out.println("Processing file"+fullFileName);
        if (fullFileName==null) return;
        //System.out.println("...Found");
        String filenameLower = fullFileName.toLowerCase();
        if (filenameLower.endsWith(".vtp") || filenameLower.endsWith(".stl") || filenameLower.endsWith(".obj")){
            // Create json for vtp
            PolygonalMesh mesh = new PolygonalMesh();
            mesh.loadFile(fullFileName);
            createJsonForMesh(mesh, arg0);
        }
    }

    private void createJsonForMesh(PolygonalMesh mesh, DecorativeGeometry arg0) {
        JSONObject dg_json = new JSONObject();
        dg_json.put("uuid", geomID.toString());
        dg_json.put("type", "BufferGeometry");
        JSONObject attributes_json = new JSONObject();
        JSONObject pos_json = new JSONObject();
        pos_json.put("itemSize", 3);
        pos_json.put("type", "Float32Array");
        JSONObject normals_json = new JSONObject();
        normals_json.put("itemSize", 3);
        normals_json.put("type", "Float32Array");
        
        int nv = mesh.getNumVertices();
        
        int nf = mesh.getNumFaces();
        Vec3[] normals = new Vec3[nv];
        
        for (int f=0; f < nf; f++){
            // get first three face indices, form normal from cross product
            //System.out.println("f="+f);
            Vec3[] verts = new Vec3[3];
            for (int i=0; i<3; i++){
                verts[i] = mesh.getVertexPosition(mesh.getFaceVertex(f, i));
            }
            Vec3 side1 = new Vec3();
            Vec3 side2 = new Vec3();
            for (int i=0; i<3; i++){
                side1.set(i, verts[1].get(i) - verts[0].get(i));
                side2.set(i, verts[2].get(i) - verts[0].get(i));
            }
            Vec3 cross = new Vec3(side1.get(1)*side2.get(2)-side1.get(2)*side2.get(1),
                    side1.get(2)*side2.get(0)-side1.get(0)*side2.get(2),
                    side1.get(0)*side2.get(1)-side1.get(1)*side2.get(0));
            double norm = Math.sqrt(cross.get(0)*cross.get(0)+
                    cross.get(1)*cross.get(1)+cross.get(2)*cross.get(2));
            for (int i=0; i<3; i++) cross.set(i, cross.get(i)/norm);
            int nvf = mesh.getNumVerticesForFace(f);
            for (int i=0; i<nvf; i++){
                int vindex = mesh.getFaceVertex(f, i);
                //System.out.print("f-v:"+vindex);
                normals[vindex]= cross;
            }
            //System.out.println("");
        }
        // Build JSONArrays for attributes, normal
        JSONArray pos_array = new JSONArray();
        JSONArray normals_array = new JSONArray();
        JSONObject data_json = new JSONObject();
        for (int v=0; v < nv; v++){
            Vec3 vec3 = mesh.getVertexPosition(v);
            for (int coord=0; coord <3; coord++){
                pos_array.add(vec3.get(coord)*visualizerScaleFactor);
            }
            //System.out.println("v="+v+" normal =:"+normals[v].toString());
            if (normals[v]!=null){
                normals_array.add(normals[v].get(0));
                normals_array.add(normals[v].get(1));
                normals_array.add(normals[v].get(2));
            }
            else {
                normals_array.add(0.0);
                normals_array.add(0.0);
                normals_array.add(0.0);
            }
        }
        pos_json.put("array", pos_array);
        normals_json.put("array", normals_array);
        attributes_json.put("position", pos_json);
        attributes_json.put("normal", normals_json);
        // Now the index to pass connectivity
        /*
        "index": {
        "itemSize": 3,
        "type" : "Uint16Array",
        "array": [3,0,1,3,2,0]
        }
        */
        JSONObject index_json = new JSONObject();
        JSONArray index_array = new JSONArray();
        for (int f=0; f < nf; f++){
            int numVerts = mesh.getNumVerticesForFace(f);
            for (int vi=0; vi <numVerts-2; vi++){
                index_array.add(mesh.getFaceVertex(f, 0));
                index_array.add(mesh.getFaceVertex(f, vi+1));
                index_array.add(mesh.getFaceVertex(f, vi+2));
            }
        }
        index_json.put("array", index_array);
        index_json.put("itemSize", 3);
        index_json.put("type", "Uint16Array");
        data_json.put("index", index_json);
        
        // Now attributes
        data_json.put("attributes", attributes_json);
        dg_json.put("data", data_json);
        jsonArr.add(dg_json);
        createMaterialJson(arg0, true);
    }

    @Override
    public void implementMeshGeometry(DecorativeMesh arg0) {
        PolygonalMesh mesh = arg0.getMesh();
        createJsonForMesh(mesh, arg0);
    }

    @Override
    public void implementTextGeometry(DecorativeText arg0) {
        supported = false; // Not supporting text
        //super.implementTextGeometry(arg0); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void implementFrameGeometry(DecorativeFrame arg0) {
        JSONObject dg_json = new JSONObject();
        dg_json.put("uuid", geomID.toString());
        dg_json.put("type", "Frame");
        //dg_json.put("radius", .00005*visualizerScaleFactor);
        dg_json.put("size", arg0.getAxisLength()*visualizerScaleFactor);
        jsonArr.add(dg_json);    
        supported = false; // Frames ar enot meshes with material, instead they are sceneGraph objects with builtin colors
    }

    @Override
    public void implementEllipsoidGeometry(DecorativeEllipsoid arg0) {
        JSONObject dg_json = new JSONObject();
        Vec3 radii = arg0.getRadii();
        // This is a hack since threejs doesn't have a builtin Ellipsoid
        // Should be transparent to users unless they use non unit scalefactors 
        // on Ellipsoids
        arg0.setScaleFactors(radii);
        // Will make a Sphere with radius 1 and scale using the Transform
        dg_json.put("uuid", geomID.toString());
        dg_json.put("type", "SphereGeometry");
	dg_json.put("radius", visualizerScaleFactor);
	dg_json.put("widthSegments", 32);
	dg_json.put("heightSegments", 16);
        if (quadrants.equals("")){
           dg_json.put("phiStart", 0);
           dg_json.put("phiLength", 2*Math.PI);
           dg_json.put("thetaStart", 0);
           dg_json.put("thetaLength", Math.PI);
        }
        else{
            if (quadrants.contains("y")){ // untested
                dg_json.put("phiStart", 0);
                dg_json.put("phiLength", 2*Math.PI);
                dg_json.put("thetaLength", 0.5*Math.PI);
                if (quadrants.equalsIgnoreCase("-y"))
                    dg_json.put("thetaStart", 0.5*Math.PI);
                else
                    dg_json.put("thetaStart", 0);
            }
            else if (quadrants.contains("z")){ 
                dg_json.put("thetaLength", Math.PI);
                dg_json.put("thetaStart", 0);
                dg_json.put("phiLength", Math.PI);
                if (quadrants.equalsIgnoreCase("-z"))
                    dg_json.put("phiStart", 0.5*Math.PI);
                else
                    dg_json.put("phiStart", 0);
            }
            else { // Z
                
            }
        }

        jsonArr.add(dg_json);        
        createMaterialJson(arg0, true);
    }

    @Override
    public void implementSphereGeometry(DecorativeSphere arg0) {
        JSONObject dg_json = createJsonForDecorativeSphere(arg0);
        jsonArr.add(dg_json);        
        createMaterialJson(arg0, true);
    }

    public JSONObject createJsonForDecorativeSphere(DecorativeSphere arg0) {
        JSONObject dg_json = new JSONObject();
        dg_json.put("uuid", geomID.toString());
        dg_json.put("type", "SphereGeometry");
        dg_json.put("radius", arg0.getRadius()*visualizerScaleFactor);
        dg_json.put("widthSegments", 32);
        dg_json.put("heightSegments", 16);
        if (quadrants.equals("")||quadrants.equalsIgnoreCase("all")){
            dg_json.put("phiStart", 0);
            dg_json.put("phiLength", 2*Math.PI);
            dg_json.put("thetaStart", 0);
            dg_json.put("thetaLength", Math.PI);
        }
        else{
            if (quadrants.contains("x")){
                dg_json.put("thetaStart", 0);
                dg_json.put("thetaLength", Math.PI);
                dg_json.put("phiLength", Math.PI);
                if (quadrants.equalsIgnoreCase("-x"))
                    dg_json.put("phiStart", -0.5*Math.PI);
                else
                    dg_json.put("phiStart", 0.5*Math.PI);
            }
            else if (quadrants.contains("y")){ 
                dg_json.put("thetaStart", -0.5*Math.PI);
                dg_json.put("thetaLength", Math.PI);
                dg_json.put("phiLength", Math.PI);
                if (quadrants.equalsIgnoreCase("-y"))
                    dg_json.put("phiStart", 0.5*Math.PI);
                else
                    dg_json.put("phiStart", -0.5*Math.PI);
            }
        }
        return dg_json;
    }

    @Override
    public void implementCircleGeometry(DecorativeCircle arg0) {
        /* "uuid": "B3E01AF2-C356-4018-A7EA-CA17C38CD8E0",
            "type": "CircleGeometry",
            "radius": 20,
            "segments": 32 */
        supported = false; // unused so impossible to test, may need draw a torus instead
        return;
        /*
        JSONObject dg_json = new JSONObject();
        dg_json.put("uuid", geomID.toString());
        dg_json.put("type", "CircleGeometry");
	dg_json.put("radius", arg0.getRadius()*visualizerScaleFactor);
	dg_json.put("segments", 32);
        jsonArr.add(dg_json); 
        createMaterialJson(arg0, false); */
    }

    @Override
    public void implementCylinderGeometry(DecorativeCylinder arg0) {
        JSONObject dg_json = createJsonForDecorativeCylinder(arg0);
        jsonArr.add(dg_json);        
        createMaterialJson(arg0, true);
    }

    public JSONObject createJsonForDecorativeCylinder(DecorativeCylinder arg0) {
        JSONObject dg_json = new JSONObject();
        dg_json.put("uuid", geomID.toString());
        dg_json.put("type", "CylinderGeometry");
        dg_json.put("radiusTop", arg0.getRadius()*visualizerScaleFactor);
        dg_json.put("radiusBottom", arg0.getRadius()*visualizerScaleFactor);
        dg_json.put("height", arg0.getHalfHeight()*2*visualizerScaleFactor);
        dg_json.put("radialSegments", 32);
        dg_json.put("heightSegments", 1);
        if (!quadrants.equals("") && !quadrants.equalsIgnoreCase("all")){
            dg_json.put("thetaLength", Math.PI);
            if (quadrants.equalsIgnoreCase("-y"))
                dg_json.put("thetaStart", -0.5*Math.PI);
            else if (quadrants.equalsIgnoreCase("y"))
                dg_json.put("thetaStart", 0.5*Math.PI);
            else if (quadrants.equalsIgnoreCase("x"))
                dg_json.put("thetaStart", 0);
            else if (quadrants.equalsIgnoreCase("-x"))
                dg_json.put("thetaStart", Math.PI);
        }
        return dg_json;
    }

    @Override
    public void implementBrickGeometry(DecorativeBrick arg0) {
        JSONObject dg_json = createJsonForDecorativeBrick(arg0);
        jsonArr.add(dg_json);        
        createMaterialJson(arg0, true);
    }

    public JSONObject createJsonForDecorativeBrick(DecorativeBrick arg0) {
        JSONObject dg_json = new JSONObject();
        dg_json.put("uuid", geomID.toString());
        dg_json.put("type", "BoxGeometry");
        dg_json.put("width", arg0.getHalfLengths().get(0)*2*visualizerScaleFactor);
        dg_json.put("height", arg0.getHalfLengths().get(1)*2*visualizerScaleFactor);
        dg_json.put("depth", arg0.getHalfLengths().get(2)*2*visualizerScaleFactor);
        dg_json.put("radialSegments", 1);
        dg_json.put("heightSegments", 1);
        return dg_json;
    }

    @Override
    public void implementLineGeometry(DecorativeLine arg0) {
        //Not supported for now to prevent models with custom DecorativeFrames from failing to visualize
        supported = false; // May need to draw as cylinder 3D
        /*
        JSONObject dg_json = new JSONObject();
        dg_json.put("uuid", geomID.toString());
        dg_json.put("type", "BufferGeometry");

        JSONObject index_json = new JSONObject();
        index_json.put("itemSize", 3);
        index_json.put("type", "Uint16Array");
        JSONObject data_json = new JSONObject();
        JSONArray verts_array = createVertexArray(arg0);
        data_json.put("vertices", verts_array);
        data_json.put("itemSize", 3);
        data_json.put("type", "Float32Array");
        data_json.put("array", verts_array);
        data_json.put("index", index_json);
        
        dg_json.put("data", data_json);
        jsonArr.add(dg_json);      
        createMaterialJson(arg0, false);*/
    }

    private JSONArray createVertexArray(DecorativeLine arg0) {
        JSONArray verts_array = new JSONArray();
        verts_array.add(arg0.getPoint1().get(0)*visualizerScaleFactor);
        verts_array.add(arg0.getPoint1().get(1)*visualizerScaleFactor);
        verts_array.add(arg0.getPoint1().get(2)*visualizerScaleFactor);
        verts_array.add(arg0.getPoint2().get(0)*visualizerScaleFactor);
        verts_array.add(arg0.getPoint2().get(1)*visualizerScaleFactor);
        verts_array.add(arg0.getPoint2().get(2)*visualizerScaleFactor);
        return verts_array;
    }

    @Override
    public void implementPointGeometry(DecorativePoint arg0) {
        //super.implementPointGeometry(arg0); //To change body of generated methods, choose Tools | Templates.
        supported = false; // May need to draw as a Sphere to be visible
    }
    /**
     * @return the jsonArr
     */
    public JSONArray getJsonArr() {
        return jsonArr;
    }

    void setGeomID(UUID hexGeom) {
        geomID = hexGeom;
        supported = true;
    }

    /**
     * @return the visualizerScaleFactor
     */
    public double getVisualizerScaleFactor() {
        return visualizerScaleFactor;
    }

    /**
     * @param visualizerScaleFactor the visualizerScaleFactor to set
     */
    public void setVisualizerScaleFactor(double visualizerScaleFactor) {
        this.visualizerScaleFactor = visualizerScaleFactor;
    }

    private void createMaterialJson(DecorativeGeometry dg, boolean isSurface) {
        if (!reuse_material){
            mat_uuid = UUID.randomUUID();
            addMaterialJsonForGeometry(mat_uuid, dg, isSurface);
        }

    }

    private void addMaterialJsonForGeometry(UUID uuid_mat, DecorativeGeometry dg, boolean isSurface) {
        JSONObject mat_json = new JSONObject();
        mat_json.put("uuid", uuid_mat.toString());
        String colorString = JSONUtilities.mapColorToRGBA(dg.getColor());
        if (isSurface){
            mat_json.put("type", "MeshStandardMaterial");
            mat_json.put("transparent", false);
            mat_json.put("metalness", 0);
            mat_json.put("roughness", 1.0);
            mat_json.put("side", 2);
            mat_json.put("wireframe", dg.getRepresentation() == DecorativeGeometry.Representation.DrawWireframe);     
        }
        else {
            mat_json.put("type", "LineBasicMaterial");           
        }
        mat_json.put("color", colorString);

        double opacity = dg.getOpacity();
        if (Math.abs(opacity) < 0.999) {
            mat_json.put("opacity", opacity);
            mat_json.put("transparent", true);
        }
        json_materials.add(mat_json);
    }

    /**
     * @return the mat_uuid
     */
    public UUID getMat_uuid() {
        return mat_uuid;
    }

    void updateGeometry(DecorativeGeometry dg, UUID current_uuid) {
        geomID = current_uuid;
        updateMode = true;
        dg.implementGeometry(this);
        updateMode = false;
    }

    /**
     * @return the last_json
     */
    public JSONObject getLast_json() {
        return last_json;
    }

    void setQuadrants(String _quadrant) {
        quadrants = _quadrant;
    }

    void useMaterial(UUID matuuid) {
        if (matuuid == null){
            reuse_material = false;
        }
        else {
            reuse_material = true;
            mat_uuid = matuuid;
        }
    }

    JSONObject getGeometryJson() {
       return (JSONObject) jsonArr.get(jsonArr.size()-1);
    }
}
