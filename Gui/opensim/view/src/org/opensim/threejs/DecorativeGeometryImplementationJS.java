/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.opensim.threejs;

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
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.Vec3;

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
        Map dg_json = new LinkedHashMap();
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
        Map dg_json = new LinkedHashMap();
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
        //json.put("name", arg0.getUserRefAsObject());
        //super.implementFrameGeometry(arg0); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void implementEllipsoidGeometry(DecorativeEllipsoid arg0) {
        //super.implementEllipsoidGeometry(arg0); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void implementSphereGeometry(DecorativeSphere arg0) {
        Map dg_json = new LinkedHashMap();
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
        Map dg_json = new LinkedHashMap();
        dg_json.put("uuid", geomID.toString());
        dg_json.put("type", "CircleGeometry");
	dg_json.put("radius", arg0.getRadius()*visualizerScaleFactor);
	dg_json.put("segments", 32);
        jsonArr.add(dg_json); 
    }

    @Override
    public void implementCylinderGeometry(DecorativeCylinder arg0) {
        Map dg_json = new LinkedHashMap();
        dg_json.put("uuid", geomID.toString());
        dg_json.put("type", "CylinderGeometry");
	dg_json.put("radiusTop", arg0.getRadius()*visualizerScaleFactor);
	dg_json.put("radiusBottom", arg0.getRadius()*visualizerScaleFactor);
	dg_json.put("height", arg0.getHalfHeight()*2*visualizerScaleFactor);
	dg_json.put("radialSegments", 32);
	dg_json.put("heightSegments", 1);
        jsonArr.add(dg_json);        
    }

    private void setTransformAndScale(Map dg_json, DecorativeGeometry arg0) {
        dg_json.put("position", JSONUtilities.stringifyVec3(arg0.getTransform().p()));
        dg_json.put("rotation", JSONUtilities.stringifyVec3(arg0.getTransform().R().convertRotationToBodyFixedXYZ()));
        dg_json.put("scale", JSONUtilities.stringifyVec3(arg0.getScaleFactors()));
    }

    @Override
    public void implementBrickGeometry(DecorativeBrick arg0) {
        Map dg_json = new LinkedHashMap();
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
