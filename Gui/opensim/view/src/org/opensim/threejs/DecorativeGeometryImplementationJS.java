/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.opensim.threejs;

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

/**
 *
 * @author Ayman
 */ 
public class DecorativeGeometryImplementationJS extends DecorativeGeometryImplementation {
    JSONArray jsona = new JSONArray();
    
    public void initJSON() {
        jsona = new JSONArray();
    }
    @Override
    public void implementConeGeometry(DecorativeCone arg0) {
        super.implementConeGeometry(arg0); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void implementArrowGeometry(DecorativeArrow arg0) {
        super.implementArrowGeometry(arg0); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void implementTorusGeometry(DecorativeTorus arg0) {
        super.implementTorusGeometry(arg0); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void implementMeshFileGeometry(DecorativeMeshFile arg0) {
        super.implementMeshFileGeometry(arg0); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void implementMeshGeometry(DecorativeMesh arg0) {
        super.implementMeshGeometry(arg0); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void implementTextGeometry(DecorativeText arg0) {
        super.implementTextGeometry(arg0); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void implementFrameGeometry(DecorativeFrame arg0) {
        super.implementFrameGeometry(arg0); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void implementEllipsoidGeometry(DecorativeEllipsoid arg0) {
        super.implementEllipsoidGeometry(arg0); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void implementSphereGeometry(DecorativeSphere arg0) {
        super.implementSphereGeometry(arg0); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void implementCircleGeometry(DecorativeCircle arg0) {
        super.implementCircleGeometry(arg0); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void implementCylinderGeometry(DecorativeCylinder arg0) {
        super.implementCylinderGeometry(arg0); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void implementBrickGeometry(DecorativeBrick arg0) {
        super.implementBrickGeometry(arg0); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void implementLineGeometry(DecorativeLine arg0) {
        super.implementLineGeometry(arg0); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void implementPointGeometry(DecorativePoint arg0) {
        super.implementPointGeometry(arg0); //To change body of generated methods, choose Tools | Templates.
    }
    
}
