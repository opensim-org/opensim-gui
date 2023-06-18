/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.1.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 *  A cone with origin point, direction, height and base radius. Note that<br>
 * the actual placement can be changed by the parent class transform &amp; scale;<br>
 * here we are just generating the initial cone in the geometry object's local frame.<br>
 * <br>
 * There is a default constructor for this object but it is not much<br>
 * use unless followed by point, direction, height and base-radius specifications. By default<br>
 * we produce a cone going from (0,0,0) in direction (1,1,1) of height 1 and base<br>
 * radius of 0.5 just so it will show up if you forget to set it to something meaningful.<br>
 * Having a default constructor allows us to have arrays of these objects. *
 */
public class DecorativeCone extends DecorativeGeometry {
  private transient long swigCPtr;

  public DecorativeCone(long cPtr, boolean cMemoryOwn) {
    super(opensimSimbodyJNI.DecorativeCone_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(DecorativeCone obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  public static long swigRelease(DecorativeCone obj) {
    long ptr = 0;
    if (obj != null) {
      if (!obj.swigCMemOwn)
        throw new RuntimeException("Cannot release ownership as memory is not owned");
      ptr = obj.swigCPtr;
      obj.swigCMemOwn = false;
      obj.delete();
    }
    return ptr;
  }

  @SuppressWarnings("deprecation")
  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimSimbodyJNI.delete_DecorativeCone(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public DecorativeCone(Vec3 p1, UnitVec3 dir, double height, double base) {
    this(opensimSimbodyJNI.new_DecorativeCone__SWIG_0(Vec3.getCPtr(p1), p1, UnitVec3.getCPtr(dir), dir, height, base), true);
  }

  public DecorativeCone(Vec3 p1, UnitVec3 dir, double height) {
    this(opensimSimbodyJNI.new_DecorativeCone__SWIG_1(Vec3.getCPtr(p1), p1, UnitVec3.getCPtr(dir), dir, height), true);
  }

  public DecorativeCone(Vec3 p1, UnitVec3 dir) {
    this(opensimSimbodyJNI.new_DecorativeCone__SWIG_2(Vec3.getCPtr(p1), p1, UnitVec3.getCPtr(dir), dir), true);
  }

  public DecorativeCone(Vec3 p1) {
    this(opensimSimbodyJNI.new_DecorativeCone__SWIG_3(Vec3.getCPtr(p1), p1), true);
  }

  public DecorativeCone() {
    this(opensimSimbodyJNI.new_DecorativeCone__SWIG_4(), true);
  }

  public Vec3 getOrigin() {
    return new Vec3(opensimSimbodyJNI.DecorativeCone_getOrigin(swigCPtr, this), false);
  }

  public UnitVec3 getDirection() {
    return new UnitVec3(opensimSimbodyJNI.DecorativeCone_getDirection(swigCPtr, this), false);
  }

  public double getHeight() {
    return opensimSimbodyJNI.DecorativeCone_getHeight(swigCPtr, this);
  }

  public double getBaseRadius() {
    return opensimSimbodyJNI.DecorativeCone_getBaseRadius(swigCPtr, this);
  }

  public DecorativeCone setOrigin(Vec3 origin) {
    return new DecorativeCone(opensimSimbodyJNI.DecorativeCone_setOrigin(swigCPtr, this, Vec3.getCPtr(origin), origin), false);
  }

  public DecorativeCone setDirection(UnitVec3 direction) {
    return new DecorativeCone(opensimSimbodyJNI.DecorativeCone_setDirection(swigCPtr, this, UnitVec3.getCPtr(direction), direction), false);
  }

  public DecorativeCone setHeight(double length) {
    return new DecorativeCone(opensimSimbodyJNI.DecorativeCone_setHeight(swigCPtr, this, length), false);
  }

  public DecorativeCone setBaseRadius(double base) {
    return new DecorativeCone(opensimSimbodyJNI.DecorativeCone_setBaseRadius(swigCPtr, this, base), false);
  }

  public DecorativeCone setBodyId(int b) {
    return new DecorativeCone(opensimSimbodyJNI.DecorativeCone_setBodyId(swigCPtr, this, b), false);
  }

  public DecorativeCone setIndexOnBody(int x) {
    return new DecorativeCone(opensimSimbodyJNI.DecorativeCone_setIndexOnBody(swigCPtr, this, x), false);
  }

  public DecorativeCone setUserRef(SWIGTYPE_p_void p) {
    return new DecorativeCone(opensimSimbodyJNI.DecorativeCone_setUserRef(swigCPtr, this, SWIGTYPE_p_void.getCPtr(p)), false);
  }

  public DecorativeCone setTransform(Transform X_BD) {
    return new DecorativeCone(opensimSimbodyJNI.DecorativeCone_setTransform(swigCPtr, this, Transform.getCPtr(X_BD), X_BD), false);
  }

  public DecorativeCone setResolution(double r) {
    return new DecorativeCone(opensimSimbodyJNI.DecorativeCone_setResolution(swigCPtr, this, r), false);
  }

  public DecorativeCone setScaleFactors(Vec3 s) {
    return new DecorativeCone(opensimSimbodyJNI.DecorativeCone_setScaleFactors(swigCPtr, this, Vec3.getCPtr(s), s), false);
  }

  public DecorativeCone setColor(Vec3 rgb) {
    return new DecorativeCone(opensimSimbodyJNI.DecorativeCone_setColor(swigCPtr, this, Vec3.getCPtr(rgb), rgb), false);
  }

  public DecorativeCone setOpacity(double o) {
    return new DecorativeCone(opensimSimbodyJNI.DecorativeCone_setOpacity(swigCPtr, this, o), false);
  }

  public DecorativeCone setLineThickness(double t) {
    return new DecorativeCone(opensimSimbodyJNI.DecorativeCone_setLineThickness(swigCPtr, this, t), false);
  }

  public DecorativeCone setRepresentation(DecorativeGeometry.Representation r) {
    return new DecorativeCone(opensimSimbodyJNI.DecorativeCone_setRepresentation(swigCPtr, this, r.swigValue()), false);
  }

  public DecorativeCone clone() {
    long cPtr = opensimSimbodyJNI.DecorativeCone_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new DecorativeCone(cPtr, false);
  }

}
