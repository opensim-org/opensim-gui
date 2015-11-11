/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.opensim.threejs;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.json.simple.JSONArray;
import org.opensim.modeling.DecorativeArrow;
import org.opensim.modeling.DecorativeBrick;
import org.opensim.modeling.DecorativeCircle;
import org.opensim.modeling.DecorativeCone;
import org.opensim.modeling.DecorativeCylinder;
import org.opensim.modeling.DecorativeEllipsoid;
import org.opensim.modeling.DecorativeFrame;
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
    private JSONArray jsonArr;
    private UUID geomID;
    private double visualizerScaleFactor = 100;
    public DecorativeGeometryImplementationJS(JSONArray jsonArr, double scale) {
        this.jsonArr = jsonArr;
        this.visualizerScaleFactor = scale;
    }
    
    @Override
    public void implementConeGeometry(DecorativeCone arg0) {
        Map<String, Object> dg_json = new LinkedHashMap<String, Object>();
        dg_json.put("uuid", geomID.toString());
        dg_json.put("type", "CylinderGeometry");
	dg_json.put("radiusTop", 0.);
	dg_json.put("radiusBottom", arg0.getBaseRadius()*visualizerScaleFactor);
	dg_json.put("widthSegments", 32);
	dg_json.put("heightSegments", 1);
        jsonArr.add(dg_json);     
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
            "arc": 6.283185307179586 */
        Map<String, Object> dg_json = new LinkedHashMap<String, Object>();
        dg_json.put("uuid", geomID.toString());
        dg_json.put("type", "TorusGeometry");
	dg_json.put("radius", arg0.getTorusRadius()*visualizerScaleFactor);
	dg_json.put("tube", arg0.getTubeRadius()*visualizerScaleFactor);
	dg_json.put("radialSegments", 32);
	dg_json.put("tubularSegments", 24);
        jsonArr.add(dg_json);        
     }

    @Override
    public void implementMeshFileGeometry(DecorativeMeshFile arg0) {
        //super.implementMeshFileGeometry(arg0); //To change body of generated methods, choose Tools | Templates.
        String meshFile = arg0.getMeshFile();
        String fullFileName = GeometryFileLocator.getInstance().getFullname("",meshFile, false);
        System.out.print("Processing file"+fullFileName);
        if (fullFileName==null) return;
        System.out.println("...Found");
        String filenameLower = fullFileName.toLowerCase();
        if (filenameLower.endsWith(".vtp") || filenameLower.endsWith(".stl") || filenameLower.endsWith(".obj")){
            // Create json for vtp
            Map<String, Object> dg_json = new LinkedHashMap<String, Object>();
            dg_json.put("uuid", geomID.toString());
            dg_json.put("type", "BufferGeometry");
            Map<String, Object> attributes_json = new LinkedHashMap<String, Object>();
            // Make position json entry
            /*
                    "position": {
                        "itemSize": 3,
                        "type": "Float32Array",
                        "array": [0,0,0,100,0,0,100,100,0]
                    }
            */
            Map<String, Object> pos_json = new LinkedHashMap<String, Object>();
            pos_json.put("itemSize", 3);
            pos_json.put("type", "Float32Array");
            Map<String, Object> normals_json = new LinkedHashMap<String, Object>();
            normals_json.put("itemSize", 3);
            normals_json.put("type", "Float32Array");
            
            PolygonalMesh mesh = new PolygonalMesh();
            mesh.loadFile(fullFileName);
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
                                    side1.get(0)*side2.get(2)-side1.get(2)*side2.get(0),
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
            Map<String, Object> data_json = new LinkedHashMap<String, Object>();
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
            Map<String, Object> index_json = new LinkedHashMap<String, Object>();
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
            attributes_json.put("index", index_json);
            
            // Now attributes
            data_json.put("attributes", attributes_json);
            dg_json.put("data", data_json);
            jsonArr.add(dg_json);
        }
    }

    @Override
    public void implementMeshGeometry(DecorativeMesh arg0) {
        //super.implementMeshGeometry(arg0); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void implementTextGeometry(DecorativeText arg0) {
        //super.implementTextGeometry(arg0); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void implementFrameGeometry(DecorativeFrame arg0) {
        Map<String, Object> dg_json = new LinkedHashMap<String, Object>();
        dg_json.put("uuid", geomID.toString());
        dg_json.put("type", "SphereGeometry");
	dg_json.put("radius", .01*visualizerScaleFactor);
	dg_json.put("widthSegments", 32);
	dg_json.put("heightSegments", 16);
	dg_json.put("phiStart", 0);
	dg_json.put("phiLength", 6.28);
	dg_json.put("thetaStart", 0);
	dg_json.put("thetaLength", 3.14);
        jsonArr.add(dg_json);    
    }

    @Override
    public void implementEllipsoidGeometry(DecorativeEllipsoid arg0) {
        //super.implementEllipsoidGeometry(arg0); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void implementSphereGeometry(DecorativeSphere arg0) {
        Map<String, Object> dg_json = new LinkedHashMap<String, Object>();
        dg_json.put("uuid", geomID.toString());
        dg_json.put("type", "SphereGeometry");
	dg_json.put("radius", arg0.getRadius()*visualizerScaleFactor);
	dg_json.put("widthSegments", 32);
	dg_json.put("heightSegments", 16);
	dg_json.put("phiStart", 0);
	dg_json.put("phiLength", 6.28);
	dg_json.put("thetaStart", 0);
	dg_json.put("thetaLength", 3.14);
        jsonArr.add(dg_json);        
    }

    @Override
    public void implementCircleGeometry(DecorativeCircle arg0) {
        /* "uuid": "B3E01AF2-C356-4018-A7EA-CA17C38CD8E0",
            "type": "CircleGeometry",
            "radius": 20,
            "segments": 32 */
        Map<String, Object> dg_json = new LinkedHashMap<String, Object>();
        dg_json.put("uuid", geomID.toString());
        dg_json.put("type", "CircleGeometry");
	dg_json.put("radius", arg0.getRadius()*visualizerScaleFactor);
	dg_json.put("segments", 32);
        jsonArr.add(dg_json); 
    }

    @Override
    public void implementCylinderGeometry(DecorativeCylinder arg0) {
        Map<String, Object> dg_json = new LinkedHashMap<String, Object>();
        dg_json.put("uuid", geomID.toString());
        dg_json.put("type", "CylinderGeometry");
	dg_json.put("radiusTop", arg0.getRadius()*visualizerScaleFactor);
	dg_json.put("radiusBottom", arg0.getRadius()*visualizerScaleFactor);
	dg_json.put("height", arg0.getHalfHeight()*2*visualizerScaleFactor);
	dg_json.put("radialSegments", 32);
	dg_json.put("heightSegments", 1);
        jsonArr.add(dg_json);        
    }

    @Override
    public void implementBrickGeometry(DecorativeBrick arg0) {
        Map<String, Object> dg_json = new LinkedHashMap<String, Object>();
        dg_json.put("uuid", geomID.toString());
        dg_json.put("type", "BoxGeometry");
	dg_json.put("width", arg0.getHalfLengths().get(0)*visualizerScaleFactor);
	dg_json.put("height", arg0.getHalfLengths().get(1)*visualizerScaleFactor);
	dg_json.put("depth", arg0.getHalfLengths().get(2)*visualizerScaleFactor);
	dg_json.put("radialSegments", 1);
	dg_json.put("heightSegments", 1);
        jsonArr.add(dg_json);        
    }

    @Override
    public void implementLineGeometry(DecorativeLine arg0) {
        //super.implementLineGeometry(arg0); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void implementPointGeometry(DecorativePoint arg0) {
        //super.implementPointGeometry(arg0); //To change body of generated methods, choose Tools | Templates.
    }
    /**
     * @return the jsonArr
     */
    public JSONArray getJsonArr() {
        return jsonArr;
    }

    void setGeomID(UUID hexGeom) {
        geomID = hexGeom;
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
}
