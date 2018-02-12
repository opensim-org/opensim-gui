/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class DecorativeEllipsoid extends DecorativeGeometry {
  private transient long swigCPtr;

  protected DecorativeEllipsoid(long cPtr, boolean cMemoryOwn) {
    super(opensimSimbodyJNI.DecorativeEllipsoid_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected static long getCPtr(DecorativeEllipsoid obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimSimbodyJNI.delete_DecorativeEllipsoid(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public DecorativeEllipsoid(Vec3 radii) {
    this(opensimSimbodyJNI.new_DecorativeEllipsoid__SWIG_0(Vec3.getCPtr(radii), radii), true);
  }

  public DecorativeEllipsoid() {
    this(opensimSimbodyJNI.new_DecorativeEllipsoid__SWIG_1(), true);
  }

  public DecorativeEllipsoid setRadii(Vec3 arg0) {
    return new DecorativeEllipsoid(opensimSimbodyJNI.DecorativeEllipsoid_setRadii(swigCPtr, this, Vec3.getCPtr(arg0), arg0), false);
  }

  public Vec3 getRadii() {
    return new Vec3(opensimSimbodyJNI.DecorativeEllipsoid_getRadii(swigCPtr, this), false);
  }

  public DecorativeEllipsoid setBodyId(int b) {
    return new DecorativeEllipsoid(opensimSimbodyJNI.DecorativeEllipsoid_setBodyId(swigCPtr, this, b), false);
  }

  public DecorativeEllipsoid setIndexOnBody(int x) {
    return new DecorativeEllipsoid(opensimSimbodyJNI.DecorativeEllipsoid_setIndexOnBody(swigCPtr, this, x), false);
  }

  public DecorativeEllipsoid setUserRef(SWIGTYPE_p_void p) {
    return new DecorativeEllipsoid(opensimSimbodyJNI.DecorativeEllipsoid_setUserRef(swigCPtr, this, SWIGTYPE_p_void.getCPtr(p)), false);
  }

  public DecorativeEllipsoid setTransform(Transform X_BD) {
    return new DecorativeEllipsoid(opensimSimbodyJNI.DecorativeEllipsoid_setTransform(swigCPtr, this, Transform.getCPtr(X_BD), X_BD), false);
  }

  public DecorativeEllipsoid setResolution(double r) {
    return new DecorativeEllipsoid(opensimSimbodyJNI.DecorativeEllipsoid_setResolution(swigCPtr, this, r), false);
  }

  public DecorativeEllipsoid setScaleFactors(Vec3 s) {
    return new DecorativeEllipsoid(opensimSimbodyJNI.DecorativeEllipsoid_setScaleFactors(swigCPtr, this, Vec3.getCPtr(s), s), false);
  }

  public DecorativeEllipsoid setColor(Vec3 rgb) {
    return new DecorativeEllipsoid(opensimSimbodyJNI.DecorativeEllipsoid_setColor(swigCPtr, this, Vec3.getCPtr(rgb), rgb), false);
  }

  public DecorativeEllipsoid setOpacity(double o) {
    return new DecorativeEllipsoid(opensimSimbodyJNI.DecorativeEllipsoid_setOpacity(swigCPtr, this, o), false);
  }

  public DecorativeEllipsoid setLineThickness(double t) {
    return new DecorativeEllipsoid(opensimSimbodyJNI.DecorativeEllipsoid_setLineThickness(swigCPtr, this, t), false);
  }

  public DecorativeEllipsoid setRepresentation(DecorativeGeometry.Representation r) {
    return new DecorativeEllipsoid(opensimSimbodyJNI.DecorativeEllipsoid_setRepresentation(swigCPtr, this, r.swigValue()), false);
  }

  public DecorativeEllipsoid clone() {
    long cPtr = opensimSimbodyJNI.DecorativeEllipsoid_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new DecorativeEllipsoid(cPtr, false);
  }

}
