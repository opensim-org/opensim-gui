/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class DecorativeMesh extends DecorativeGeometry {
  private transient long swigCPtr;

  protected DecorativeMesh(long cPtr, boolean cMemoryOwn) {
    super(opensimSimbodyJNI.DecorativeMesh_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected static long getCPtr(DecorativeMesh obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimSimbodyJNI.delete_DecorativeMesh(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public DecorativeMesh(PolygonalMesh mesh) {
    this(opensimSimbodyJNI.new_DecorativeMesh(PolygonalMesh.getCPtr(mesh), mesh), true);
  }

  public PolygonalMesh getMesh() {
    return new PolygonalMesh(opensimSimbodyJNI.DecorativeMesh_getMesh(swigCPtr, this), false);
  }

  public DecorativeMesh setBodyId(int b) {
    return new DecorativeMesh(opensimSimbodyJNI.DecorativeMesh_setBodyId(swigCPtr, this, b), false);
  }

  public DecorativeMesh setIndexOnBody(int x) {
    return new DecorativeMesh(opensimSimbodyJNI.DecorativeMesh_setIndexOnBody(swigCPtr, this, x), false);
  }

  public DecorativeMesh setUserRef(SWIGTYPE_p_void p) {
    return new DecorativeMesh(opensimSimbodyJNI.DecorativeMesh_setUserRef(swigCPtr, this, SWIGTYPE_p_void.getCPtr(p)), false);
  }

  public DecorativeMesh setTransform(Transform X_BD) {
    return new DecorativeMesh(opensimSimbodyJNI.DecorativeMesh_setTransform(swigCPtr, this, Transform.getCPtr(X_BD), X_BD), false);
  }

  public DecorativeMesh setResolution(double r) {
    return new DecorativeMesh(opensimSimbodyJNI.DecorativeMesh_setResolution(swigCPtr, this, r), false);
  }

  public DecorativeMesh setScaleFactors(Vec3 s) {
    return new DecorativeMesh(opensimSimbodyJNI.DecorativeMesh_setScaleFactors(swigCPtr, this, Vec3.getCPtr(s), s), false);
  }

  public DecorativeMesh setColor(Vec3 rgb) {
    return new DecorativeMesh(opensimSimbodyJNI.DecorativeMesh_setColor(swigCPtr, this, Vec3.getCPtr(rgb), rgb), false);
  }

  public DecorativeMesh setOpacity(double o) {
    return new DecorativeMesh(opensimSimbodyJNI.DecorativeMesh_setOpacity(swigCPtr, this, o), false);
  }

  public DecorativeMesh setLineThickness(double t) {
    return new DecorativeMesh(opensimSimbodyJNI.DecorativeMesh_setLineThickness(swigCPtr, this, t), false);
  }

  public DecorativeMesh setRepresentation(DecorativeGeometry.Representation r) {
    return new DecorativeMesh(opensimSimbodyJNI.DecorativeMesh_setRepresentation(swigCPtr, this, r.swigValue()), false);
  }

}
