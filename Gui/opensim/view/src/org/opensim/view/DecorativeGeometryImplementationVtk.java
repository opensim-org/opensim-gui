/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import org.opensim.modeling.ArrayDecorativeGeometry;
import org.opensim.modeling.Body;
import org.opensim.modeling.BodySet;
import org.opensim.modeling.DecorativeBrick;
import org.opensim.modeling.DecorativeCircle;
import org.opensim.modeling.DecorativeCylinder;
import org.opensim.modeling.DecorativeEllipsoid;
import org.opensim.modeling.DecorativeFrame;
import org.opensim.modeling.DecorativeGeometry;
import org.opensim.modeling.DecorativeGeometryImplementation;
import org.opensim.modeling.DecorativeLine;
import org.opensim.modeling.DecorativeMesh;
import org.opensim.modeling.DecorativePoint;
import org.opensim.modeling.DecorativeSphere;
import org.opensim.modeling.DecorativeText;
import org.opensim.modeling.Model;
import org.opensim.modeling.ModelComponent;
import org.opensim.modeling.ModelDisplayHints;
import org.opensim.modeling.OpenSimContext;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.State;
import org.opensim.modeling.Transform;
import org.opensim.modeling.Vec3;
import vtk.vtkActor;
import vtk.vtkAlgorithm;
import vtk.vtkAssembly;
import vtk.vtkAxes;
import vtk.vtkCubeSource;
import vtk.vtkCylinderSource;
import vtk.vtkLineSource;
import vtk.vtkPolyData;
import vtk.vtkPolyDataMapper;
import vtk.vtkProp3D;
import vtk.vtkSphereSource;
import vtk.vtkTransform;
import vtk.vtkTransformPolyDataFilter;

/**
 *
 * @author Ayman, implementation of Visualizer API that uses VTK objects
 */
public class DecorativeGeometryImplementationVtk extends DecorativeGeometryImplementation {
    private Model model;
    private vtkAssembly modelDisplayAsembly;
    private HashMap<OpenSimObject, vtkProp3D> mapObjects2Vtk; 
    private HashMap<vtkProp3D, OpenSimObject> mapVtk2Objects;
    private HashMap<OpenSimObject, HashMap<Integer, vtkAlgorithm>> variableGeometryMap; 
    private HashMap<OpenSimObject, HashMap<Integer, vtkProp3D>> variableGeometryPropsMap; 
    private ArrayList<OpenSimObject> objectsWithVarGeometry;
    private HashMap<Integer, Body> mapBodyIndexToBody;
    private boolean fixedGeometry = false;
    private double[] flattenedXform = new double[16];
    private Vec3 pt1, pt2;
    private OpenSimObject lastObj = null;
    private int numObjects = 0;
    vtkAlgorithm currentGeometryAlgorithm;
    vtkProp3D currentProp;
    
    DecorativeGeometryImplementationVtk(Model aModel, vtkAssembly modelDisplayAssembly,
            HashMap<OpenSimObject, vtkProp3D> mapObjects2Vtk, 
            HashMap<vtkProp3D, OpenSimObject> mapVtk2Objects) {
        model = aModel;
        this.mapObjects2Vtk = mapObjects2Vtk;
        this.mapVtk2Objects = mapVtk2Objects;
        this.modelDisplayAsembly = modelDisplayAssembly;
        this.fixedGeometry = true;
        populateMapBodyIndexToBody();
        variableGeometryMap = new HashMap<OpenSimObject, HashMap<Integer, vtkAlgorithm>>();
        variableGeometryPropsMap  = new HashMap<OpenSimObject, HashMap<Integer, vtkProp3D>>();
        objectsWithVarGeometry = new ArrayList<OpenSimObject>();
    }

    public void attachApperanceOfVtkObject(DecorativeGeometry arg0, vtkActor boneActor) {
        Vec3 rgb = arg0.getColor();
        boneActor.GetProperty().SetColor(rgb.get(0), rgb.get(1), rgb.get(2));
        boneActor.GetProperty().SetInterpolation(arg0.getRepresentation().swigValue());
        boneActor.GetProperty().SetOpacity(arg0.getOpacity());
        
    }

    public void attachTransformToVtkObject(Transform tr, vtkProp3D boneActor) {
        OpenSimContext.getTransformAsDouble16(tr, flattenedXform);
        boneActor.SetUserMatrix(SingleModelVisuals.convertTransformToVtkMatrix4x4(flattenedXform));
    }

    public void bindObjectToRepresentation(OpenSimObject obj, vtkActor boneActor) {
        mapObjects2Vtk.put(obj, boneActor);
        mapVtk2Objects.put(boneActor, obj);
    }

    public void createMapperAndAdd(DecorativeGeometry arg0, vtkActor dActor, vtkPolyData vpd) {
        //super.implementMeshFileGeometry(arg0);
        Transform tr = arg0.getTransform();
        attachTransformToVtkObject(tr, dActor);
        attachApperanceOfVtkObject(arg0, dActor);

        vtkPolyDataMapper mapper = new vtkPolyDataMapper();
        mapper.SetInput(vpd);
        dActor.SetMapper(mapper);
        // Get vtkAssembly for body and attach Sphere
        Body b = mapBodyIndexToBody.get(new Integer(arg0.getBodyId()));
        if (b==null) return;
        int v = 0;
        ((vtkAssembly) mapObjects2Vtk.get(b)).AddPart(dActor);

    }

    @Override
    public void implementBrickGeometry(DecorativeBrick arg0) {
        
        Vec3 scales = arg0.getScaleFactors();
        vtkActor cubeActor = new vtkActor();
        vtkCubeSource cube = new vtkCubeSource();
        cube.SetXLength(scales.get(0));
        cube.SetYLength(scales.get(1));
        cube.SetZLength(scales.get(2));
        cube.SetCenter(0.0, 0.0, 0.0);
        vtkPolyData vpd = cube.GetOutput();
        createMapperAndAdd(arg0, cubeActor, vpd);
        if (arg0.hasUserRef()){ 
            OpenSimObject obj = arg0.getUserRefAsObject();
            //if (!isFixedGeometry()) processNewObjectVarGeometry(obj, cube, arg0.getIndexOnBody(), cubeActor);
            bindObjectToRepresentation(obj, cubeActor);            
        }

   }

    private void processNewObjectVarGeometry(OpenSimObject obj, vtk.vtkAlgorithm source, int idxOnBody, vtk.vtkActor actor) {
        if (lastObj == null || !obj.equals(lastObj)){
            numObjects++;
            System.out.println("New object with variable geometry: type"+obj.getConcreteClassName()
                    +":"+obj.getName()
                    +" count"+String.valueOf(numObjects));
            lastObj = obj;
            objectsWithVarGeometry.add(obj);
            HashMap<Integer, vtkAlgorithm> srcs = new HashMap<Integer, vtkAlgorithm>();
            srcs.put(idxOnBody, source);
            variableGeometryMap.put(obj, srcs);
            HashMap<Integer, vtkProp3D> props = new HashMap<Integer, vtkProp3D>();
            props.put(idxOnBody, actor);
            variableGeometryPropsMap.put(obj, props);

        }
        else if (variableGeometryMap.containsKey(obj)){
            HashMap<Integer, vtkAlgorithm> vtkSources = variableGeometryMap.get(obj);
            vtkSources.put(idxOnBody, source);
        }
    }

    @Override
    public void implementCircleGeometry(DecorativeCircle arg0) {
        System.out.println("implementCircleGeometry:");
    }

    @Override
    public void implementCylinderGeometry(DecorativeCylinder arg0) {
        double radius = arg0.getRadius();
        vtkActor cylActor = new vtkActor();
        vtkCylinderSource cylinder = new vtkCylinderSource();
        cylinder.SetResolution(32);
        cylinder.SetHeight(arg0.getHalfHeight() * 2);
        cylinder.SetRadius(radius);
        cylinder.SetCenter(0.0, 0.0, 0.0);
        vtkPolyData vpd = cylinder.GetOutput();
        createMapperAndAdd(arg0, cylActor, vpd);
        if (arg0.hasUserRef()){ 
            OpenSimObject obj = arg0.getUserRefAsObject();
            //if (!isFixedGeometry()) processNewObjectVarGeometry(obj, cylinder, arg0.getIndexOnBody(), cylActor);
            bindObjectToRepresentation(obj, cylActor);            
        }
    }

    @Override
    public void implementEllipsoidGeometry(DecorativeEllipsoid arg0) {
        //super.implementSphereGeometry(arg0);
        Vec3 radii = arg0.getRadii();
        vtkActor sphereActor = new vtkActor();
        vtkSphereSource sphere = new vtkSphereSource();
        sphere.LatLongTessellationOn();
        sphere.SetPhiResolution(32);
        sphere.SetThetaResolution(32);
        sphere.SetRadius(1.0);
        
        vtkTransformPolyDataFilter stretch = new vtkTransformPolyDataFilter();
          vtkTransform stretchSphereToEllipsoid = new vtkTransform();
          stretchSphereToEllipsoid.Scale(radii.get(0), radii.get(1), radii.get(2));
          stretch.SetTransform(stretchSphereToEllipsoid);
          stretch.SetInputConnection(sphere.GetOutputPort());
          vtkPolyData vpd = stretch.GetOutput();
        createMapperAndAdd(arg0, sphereActor, vpd);
        if (arg0.hasUserRef()){ 
            OpenSimObject obj = arg0.getUserRefAsObject();
            //if (!isFixedGeometry()) processNewObjectVarGeometry(obj, sphere, arg0.getIndexOnBody(), sphereActor);
            bindObjectToRepresentation(obj, sphereActor);
        }
        //System.out.println("implementEllipsoidGeometry:");
    }

    @Override
    public void implementFrameGeometry(DecorativeFrame arg0) {
        double axisLength = arg0.getAxisLength();
        vtkActor axesActor = new vtkActor();
        vtkAxes axes = new vtkAxes();
        axes.SetOrigin(0.0, 0.0, 0.0);
        axes.SetScaleFactor(1.0);
        vtkPolyData vpd = axes.GetOutput();
        //super.implementMeshFileGeometry(arg0);
        Vec3 rgb = arg0.getColor();
        axesActor.GetProperty().SetColor(rgb.get(0), rgb.get(1), rgb.get(2));
        axesActor.GetProperty().SetInterpolation(arg0.getRepresentation().swigValue());

        vtkPolyDataMapper mapper = new vtkPolyDataMapper();
        mapper.SetInput(vpd);
        axesActor.SetMapper(mapper);
        // Get vtkAssembly for body and attach Sphere
        Body b = mapBodyIndexToBody.get(new Integer(arg0.getBodyId()));
        ((vtkAssembly) mapObjects2Vtk.get(b)).AddPart(axesActor);
        Transform tr = arg0.getTransform();
        attachTransformToVtkObject(tr, axesActor);
        System.out.println("frame:" + axisLength + "tr" + tr.T().get(0));
    }
    /*
    @Override
    public void implementMeshFileGeometry(DecorativeMeshFile arg0) {
        String meshFile = arg0.getMeshFile();
        String fullFileName = GeometryFileLocator.getInstance().getFullname("",meshFile, false);
        if (fullFileName==null) return;
        vtkActor boneActor=new vtkActor();
        GeometryFactory.populateActorFromFile(fullFileName, boneActor);
        // Get body for mesh
        Body b = mapBodyIndexToBody.get(new Integer(arg0.getBodyId()));
        ((vtkAssembly)mapObjects2Vtk.get(b)).AddPart(boneActor);
        //super.implementMeshFileGeometry(arg0);
        Transform tr = arg0.getTransform();
        attachTransformToVtkObject(tr, boneActor);
        attachApperanceOfVtkObject(arg0, boneActor);

        if (arg0.hasUserRef()){ 
            //System.out.println("User ref for meshFile "+meshFile+" specified.");
            OpenSimObject obj = arg0.getUserRefAsObject();
            bindObjectToRepresentation(obj, boneActor);
        }
    }
     * */
    @Override
    public void implementMeshGeometry(DecorativeMesh arg0) {
        System.out.println("implementMeshGeometry:");
    }

    @Override
    public void implementPointGeometry(DecorativePoint arg0) {
        System.out.println("implementPointGeometry:"+arg0.getPoint());
    }

    @Override
    public void implementSphereGeometry(DecorativeSphere arg0) {
        //super.implementSphereGeometry(arg0);
        double radius = arg0.getRadius();
        vtkActor sphereActor = new vtkActor();
        vtkSphereSource sphere = new vtkSphereSource();
        sphere.LatLongTessellationOn();
        sphere.SetPhiResolution(32);
        sphere.SetThetaResolution(32);
        sphere.SetRadius(radius);
        vtkPolyData vpd = sphere.GetOutput();
        createMapperAndAdd(arg0, sphereActor, vpd);
        if (arg0.hasUserRef()){ 
            OpenSimObject obj = arg0.getUserRefAsObject();
            //processNewObjectVarGeometry(obj); will skip muscle points as they could be sprinkled between lines
            bindObjectToRepresentation(obj, sphereActor);
        }

    }

    @Override
    public void implementTextGeometry(DecorativeText arg0) {
        System.out.println("implementTextGeometry:"+arg0.getText());
    }

    @Override
    public void implementLineGeometry(DecorativeLine arg0) {
        vtkLineSource line;
        boolean reuseLine = false;
        if (currentGeometryAlgorithm instanceof vtkLineSource) {
            line = (vtkLineSource) currentGeometryAlgorithm;
            reuseLine = true;
        } else {
            line = new vtkLineSource();
        }
        pt1 = arg0.getPoint1();
        pt2 = arg0.getPoint2();
        line.SetPoint1(pt1.get(0), pt1.get(1), pt1.get(2));
        line.SetPoint2(pt2.get(0), pt2.get(1), pt2.get(2));
        if (!reuseLine) {
            vtkPolyData vpd = line.GetOutput();
            //super.implementMeshFileGeometry(arg0);
            //Transform tr = arg0.getTransform();
            //attachTransformToVtkObject(tr, lineActor);
            Vec3 rgb = arg0.getColor();
            vtkActor lineActor = new vtkActor();
            lineActor.GetProperty().SetColor(rgb.get(0), rgb.get(1), rgb.get(2));

            vtkPolyDataMapper mapper = new vtkPolyDataMapper();
            mapper.SetInput(vpd);
            lineActor.SetMapper(mapper);
            Body b = mapBodyIndexToBody.get(arg0.getBodyId());
            vtkAssembly bodyAssembly = (vtkAssembly) mapObjects2Vtk.get(b);
            bodyAssembly.AddPart(lineActor);

            if (arg0.hasUserRef()) {
                OpenSimObject obj = arg0.getUserRefAsObject();
                //int idx = arg0.getIndexOnBody();
                //arg0.setIndexOnBody(0);
                if (!isFixedGeometry()) {
                    //processNewObjectVarGeometry(obj, line, arg0.getIndexOnBody(), lineActor);
                }
                bindObjectToRepresentation(obj, lineActor);
            }
        } else {
            line.Modified();
            Vec3 rgb = arg0.getColor();
            vtkActor act = (vtkActor)currentProp;
            if (act!=null){
                act.GetProperty().SetColor(rgb.get(0), rgb.get(1), rgb.get(2));
                act.Modified();
            }
        }

    }

    public void updateVariableGeometry(ModelDisplayHints mdh, Model model) {
        // Geometry in ground frame need updating (e.g. Muscles, GRF. etc)
        
        // For each Object in the list call its generateDecorations and update accordingly
        Iterator<OpenSimObject> iterator = objectsWithVarGeometry.iterator();
        while (iterator.hasNext()){
            updateObjectDisplay(iterator.next(), mdh, model.updWorkingState());
        }        
        modelDisplayAsembly.Modified();
    }

    private void populateMapBodyIndexToBody() {
       BodySet bset = model.getBodySet();
       mapBodyIndexToBody = new HashMap<Integer, Body>();
       for (int i=0;i<bset.getSize();i++){
           Body b = bset.get(i);
           mapBodyIndexToBody.put(b.getIndex(), b);
           vtkAssembly bodyAssembly = new vtkAssembly();
           mapObjects2Vtk.put(b, bodyAssembly);
           mapVtk2Objects.put(bodyAssembly, b);
           modelDisplayAsembly.AddPart(bodyAssembly);
       }
        updateBodyTransforms();
    }

    public void updateBodyTransforms() {
        State s = model.getWorkingState();
        for (Body b:mapBodyIndexToBody.values()){
            vtkAssembly bodyAssembly = (vtkAssembly) mapObjects2Vtk.get(b);
            Transform bXform = model.getSimbodyEngine().getTransform(s, b);
            attachTransformToVtkObject(bXform, bodyAssembly);
        }
    }


    /**
     * @return the fixedGeometry
     */
    public boolean isFixedGeometry() {
        return fixedGeometry;
    }

    /**
     * @param fixedGeometry the fixedGeometry to set
     */
    public void setFixedGeometry(boolean fixedGeometry) {
        if (fixedGeometry != this.fixedGeometry) {
            System.out.println("Toggle fixed geometry flag");
            numObjects = 0;
        }
        this.fixedGeometry = fixedGeometry;
    }
    
    public void printSummaries() {
        Set<OpenSimObject> keys = mapObjects2Vtk.keySet();
        System.out.println("KEYS");
        for(OpenSimObject key:keys){
            System.out.println(key.getConcreteClassName()+":"+key.getName());
        }

    }

    void updateObjectDisplay(OpenSimObject specificObject, ModelDisplayHints mdh, State s) {
        /*
        System.out.append("update object of type:"+specificObject.getConcreteClassName());
        ModelComponent comp = ModelComponent.safeDownCast(specificObject);
        if (comp == null) return;
        HashMap<Integer, vtkAlgorithm> vtkSources = variableGeometryMap.get(specificObject);
        HashMap<Integer, vtkProp3D> vtkProps = variableGeometryPropsMap.get(specificObject);
        ArrayDecorativeGeometry adg = new ArrayDecorativeGeometry();
        /////comp.generateDecorations(false, mdh, s, adg);
        for(int i=0; i<adg.size(); i++){
            int idOnBody = adg.getElt(i).getIndexOnBody();
            if (idOnBody==-1) continue;
            currentGeometryAlgorithm = vtkSources.get(idOnBody);
            currentProp = vtkProps.get(idOnBody);
            adg.getElt(i).implementGeometry(this);
        }
        adg.delete();
         * 
         */
    }
}
