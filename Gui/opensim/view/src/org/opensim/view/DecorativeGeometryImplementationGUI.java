/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.view;

import java.util.Hashtable;
import org.opensim.modeling.DecorativeBrick;
import org.opensim.modeling.DecorativeCircle;
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
import org.opensim.modeling.DisplayGeometry;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimObject;
import org.opensim.view.pub.GeometryFileLocator;
import vtk.vtkActor;
import vtk.vtkAssembly;
import vtk.vtkProp3D;

/**
 *
 * @author Ayman, a Java implementation of DecorativeGeometryImplementation to be used by GUI
 */
public class DecorativeGeometryImplementationGUI extends DecorativeGeometryImplementation {
  int unused =0;
  private vtkAssembly modelAssembly;
  Hashtable<OpenSimObject, vtkProp3D> mapObject2VtkObjects;
  private Model model;
  String modelFilePath;
  public DecorativeGeometryImplementationGUI() {
      // Default constructor
      unused = 1;
      
  }
    @Override
  public void implementPointGeometry(DecorativePoint arg0) {
    System.out.println("Type: DecorativePoint");
    //opensimModelJNI.DecorativeGeometryImplementation_implementPointGeometry(swigCPtr, this, DecorativePoint.getCPtr(arg0), arg0);
  }

    @Override
  public void implementLineGeometry(DecorativeLine arg0) {
    System.out.println("Type: DecorativeLine");
    //opensimModelJNI.DecorativeGeometryImplementation_implementLineGeometry(swigCPtr, this, DecorativeLine.getCPtr(arg0), arg0);
  }

  public void implementBrickGeometry(DecorativeBrick arg0) {
    System.out.println("Type: DecorativeBrick");
    //opensimModelJNI.DecorativeGeometryImplementation_implementBrickGeometry(swigCPtr, this, DecorativeBrick.getCPtr(arg0), arg0);
  }

  public void implementCylinderGeometry(DecorativeCylinder arg0) {
    System.out.println("Type: Cylinder(r, [center], bodyId)"+ 
            arg0.getRadius()+", ["+arg0.getTransform().T().toString()+"]"+arg0.getBodyId());
    //opensimModelJNI.DecorativeGeometryImplementation_implementCylinderGeometry(swigCPtr, this, DecorativeCylinder.getCPtr(arg0), arg0);
  }

  public void implementCircleGeometry(DecorativeCircle arg0) {
    System.out.println("Type: DecorativeCircle");
    //opensimModelJNI.DecorativeGeometryImplementation_implementCircleGeometry(swigCPtr, this, DecorativeCircle.getCPtr(arg0), arg0);
  }

  public void implementSphereGeometry(DecorativeSphere arg0) {
    System.out.println("Type: Sphere(r, [center], bodyId)"+ 
            arg0.getRadius()+", ["+arg0.getTransform().T().toString()+"]"+arg0.getBodyId());
    BodyDisplayer bd = (BodyDisplayer) mapObject2VtkObjects.get(model.getBodySet().get(arg0.getBodyId()));
    vtkActor sphereActor=new DecorativeSphereDisplayer(arg0, arg0.getUserRefAsObject());
    bd.AddPart(sphereActor);
  }

  public void implementEllipsoidGeometry(DecorativeEllipsoid arg0) {
    System.out.println("Type: DecorativeEllipsoid");
    //opensimModelJNI.DecorativeGeometryImplementation_implementEllipsoidGeometry(swigCPtr, this, DecorativeEllipsoid.getCPtr(arg0), arg0);
  }

  public void implementFrameGeometry(DecorativeFrame arg0) {
    System.out.println("Type: DecorativeFrame");
    //opensimModelJNI.DecorativeGeometryImplementation_implementFrameGeometry(swigCPtr, this, DecorativeFrame.getCPtr(arg0), arg0);
  }

  public void implementTextGeometry(DecorativeText arg0) {
    System.out.println("Type: DecorativeText");
    //opensimModelJNI.DecorativeGeometryImplementation_implementTextGeometry(swigCPtr, this, DecorativeText.getCPtr(arg0), arg0);
  }

  public void implementMeshGeometry(DecorativeMesh arg0) {
    System.out.println("Type: DecorativeMesh");
    //opensimModelJNI.DecorativeGeometryImplementation_implementMeshGeometry(swigCPtr, this, DecorativeMesh.getCPtr(arg0), arg0);
  }
  public void implementMeshFileGeometry(DecorativeMeshFile arg0) {
    System.out.println("Type: DecorativeMeshFile(file, [center], bodyId)"+ 
            arg0.getMeshFile()+", ["+arg0.getTransform().T().toString()+"]"+arg0.getBodyId());
    BodyDisplayer bd = (BodyDisplayer) mapObject2VtkObjects.get(model.getBodySet().get(arg0.getBodyId()));
    String fullFileName = GeometryFileLocator.getInstance().getFullname(modelFilePath,arg0.getMeshFile(), false);
    if (fullFileName==null) return;
    vtkActor boneActor=new DisplayGeometryDisplayer(DisplayGeometry.safeDownCast(arg0.getUserRefAsObject()), modelFilePath);
    bd.AddPart(boneActor);
    
  }

    /**
     * @return the modelAssembly
     */
    public vtkAssembly getModelAssembly() {
        return modelAssembly;
    }

    /**
     * @param modelAssembly the modelAssembly to set
     */
    public void setModelAssembly(vtkAssembly modelAssembly, 
            Hashtable<OpenSimObject, vtkProp3D> mapObject2VtkObjects,
            Model model) {
        this.modelAssembly = modelAssembly;
        this.mapObject2VtkObjects = mapObject2VtkObjects;
        this.model = model;
        this.modelFilePath=model.getFilePath();
    }
   
}
