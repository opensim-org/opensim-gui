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
    private JSONArray jsonArr;
    private JSONArray json_materials;
    private UUID geomID;
    private UUID mat_uuid;
    private double visualizerScaleFactor = 100;
    boolean updateMode = false;
    private Map<String, Object> last_json = null;
    
    public DecorativeGeometryImplementationJS(JSONArray jsonArr, JSONArray jsonArrMaterials, double scale) {
        this.jsonArr = jsonArr;
        this.json_materials = jsonArrMaterials;
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
        createMaterialJson(arg0, true);
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
        createMaterialJson(arg0, true);
     }

    @Override
    public void implementMeshFileGeometry(DecorativeMeshFile arg0) {
        //super.implementMeshFileGeometry(arg0); //To change body of generated methods, choose Tools | Templates.
        String meshFile = arg0.getMeshFile();
        String fullFileName = GeometryFileLocator.getInstance().getFullname("",meshFile, false);
        //System.out.print("Processing file"+fullFileName);
        if (fullFileName==null) return;
        //System.out.println("...Found");
        String filenameLower = fullFileName.toLowerCase();
        if (filenameLower.endsWith(".vtp") || filenameLower.endsWith(".stl") || filenameLower.endsWith(".obj")){
            // Create json for vtp
            Map<String, Object> dg_json = new LinkedHashMap<String, Object>();
            dg_json.put("uuid", geomID.toString());
            dg_json.put("type", "BufferGeometry");
            Map<String, Object> attributes_json = new LinkedHashMap<String, Object>();
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
            createMaterialJson(arg0, true);
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
	dg_json.put("radius", .00005*visualizerScaleFactor);
	//dg_json.put("size", arg0.getAxisLength()*visualizerScaleFactor);
        jsonArr.add(dg_json);    
        createMaterialJson(arg0, false);
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
        createMaterialJson(arg0, true);
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
        createMaterialJson(arg0, false);
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
        createMaterialJson(arg0, true);
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
        createMaterialJson(arg0, true);
    }

    @Override
    public void implementLineGeometry(DecorativeLine arg0) {
        Map<String, Object> dg_json = new LinkedHashMap<String, Object>();
        dg_json.put("uuid", geomID.toString());
        dg_json.put("type", "OpenSim.PathGeometry");
        String colorString = JSONUtilities.mapColorToRGBA(arg0.getColor());
        dg_json.put("color", colorString);
        /*
	"data": {
        "indices": [0,1,2,...],
        "vertices": [50,50,50,...],
        "normals": [1,0,0,...],
        "uvs": [0,1,...]
        */
        Map<String, Object> data_json = new LinkedHashMap<String, Object>();
        JSONArray verts_array = createVertexArray(arg0);
        //data_json.put("vertices", verts_array);
        data_json.put("itemSize", 3);
        data_json.put("type", "Float32Array");
        data_json.put("array", verts_array);
        dg_json.put("positions", data_json);
        last_json = dg_json;
        if (!updateMode) {
            jsonArr.add(dg_json);        
            createMaterialJson(arg0, false);
        }
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

    private void createMaterialJson(DecorativeGeometry dg, boolean isSurface) {
        mat_uuid = UUID.randomUUID();
        addMaterialJsonForGeometry(mat_uuid, dg, isSurface);

    }

    private void addMaterialJsonForGeometry(UUID uuid_mat, DecorativeGeometry dg, boolean isSurface) {
        Map<String, Object> mat_json = new LinkedHashMap<String, Object>();
        mat_json.put("uuid", uuid_mat.toString());
        if (isSurface){
            mat_json.put("type", "MeshPhongMaterial");
            mat_json.put("shininess", 30);
            mat_json.put("emissive", JSONUtilities.mapColorToRGBA(new Vec3(0., 0., 0.)));
            mat_json.put("specular", JSONUtilities.mapColorToRGBA(new Vec3(0., 0., 0.)));
            mat_json.put("side", 2);
        }
        else {
            mat_json.put("type", "LineBasicMaterial");           
        }
        String colorString = JSONUtilities.mapColorToRGBA(dg.getColor());
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
    public Map<String, Object> getLast_json() {
        return last_json;
    }
}
