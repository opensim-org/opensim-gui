/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class DecorativePoint extends DecorativeGeometry {
  private transient long swigCPtr;

  protected DecorativePoint(long cPtr, boolean cMemoryOwn) {
    super(opensimSimbodyJNI.DecorativePoint_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected static long getCPtr(DecorativePoint obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimSimbodyJNI.delete_DecorativePoint(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public DecorativePoint(Vec3 p) {
    this(opensimSimbodyJNI.new_DecorativePoint__SWIG_0(Vec3.getCPtr(p), p), true);
  }

  public DecorativePoint() {
    this(opensimSimbodyJNI.new_DecorativePoint__SWIG_1(), true);
  }

  public DecorativePoint setPoint(Vec3 p) {
    return new DecorativePoint(opensimSimbodyJNI.DecorativePoint_setPoint(swigCPtr, this, Vec3.getCPtr(p), p), false);
  }

  public Vec3 getPoint() {
    return new Vec3(opensimSimbodyJNI.DecorativePoint_getPoint(swigCPtr, this), false);
  }

  public DecorativePoint setBodyId(int b) {
    return new DecorativePoint(opensimSimbodyJNI.DecorativePoint_setBodyId(swigCPtr, this, b), false);
  }

  public DecorativePoint setIndexOnBody(int x) {
    return new DecorativePoint(opensimSimbodyJNI.DecorativePoint_setIndexOnBody(swigCPtr, this, x), false);
  }

  public DecorativePoint setUserRef(SWIGTYPE_p_void p) {
    return new DecorativePoint(opensimSimbodyJNI.DecorativePoint_setUserRef(swigCPtr, this, SWIGTYPE_p_void.getCPtr(p)), false);
  }

  public DecorativePoint setTransform(Transform X_BD) {
    return new DecorativePoint(opensimSimbodyJNI.DecorativePoint_setTransform(swigCPtr, this, Transform.getCPtr(X_BD), X_BD), false);
  }

  public DecorativePoint setResolution(double r) {
    return new DecorativePoint(opensimSimbodyJNI.DecorativePoint_setResolution(swigCPtr, this, r), false);
  }

  public DecorativePoint setScaleFactors(Vec3 s) {
    return new DecorativePoint(opensimSimbodyJNI.DecorativePoint_setScaleFactors(swigCPtr, this, Vec3.getCPtr(s), s), false);
  }

  public DecorativePoint setColor(Vec3 rgb) {
    return new DecorativePoint(opensimSimbodyJNI.DecorativePoint_setColor(swigCPtr, this, Vec3.getCPtr(rgb), rgb), false);
  }

  public DecorativePoint setOpacity(double o) {
    return new DecorativePoint(opensimSimbodyJNI.DecorativePoint_setOpacity(swigCPtr, this, o), false);
  }

  public DecorativePoint setLineThickness(double t) {
    return new DecorativePoint(opensimSimbodyJNI.DecorativePoint_setLineThickness(swigCPtr, this, t), false);
  }

  public DecorativePoint setRepresentation(DecorativeGeometry.Representation r) {
    return new DecorativePoint(opensimSimbodyJNI.DecorativePoint_setRepresentation(swigCPtr, this, r.swigValue()), false);
  }

}
