/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 * A class to represent a Cone geometry.
 */
public class Cone extends AnalyticGeometry {
  private transient long swigCPtr;

  public Cone(long cPtr, boolean cMemoryOwn) {
    super(opensimSimulationJNI.Cone_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(Cone obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  @SuppressWarnings("deprecation")
  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimSimulationJNI.delete_Cone(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static Cone safeDownCast(OpenSimObject obj) {
    long cPtr = opensimSimulationJNI.Cone_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new Cone(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimSimulationJNI.Cone_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimSimulationJNI.Cone_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimSimulationJNI.Cone_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new Cone(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimSimulationJNI.Cone_getConcreteClassName(swigCPtr, this);
  }

  public void copyProperty_origin(Cone source) {
    opensimSimulationJNI.Cone_copyProperty_origin(swigCPtr, this, Cone.getCPtr(source), source);
  }

  public Vec3 get_origin(int i) {
    return new Vec3(opensimSimulationJNI.Cone_get_origin__SWIG_0(swigCPtr, this, i), false);
  }

  public Vec3 upd_origin(int i) {
    return new Vec3(opensimSimulationJNI.Cone_upd_origin__SWIG_0(swigCPtr, this, i), false);
  }

  public void set_origin(int i, Vec3 value) {
    opensimSimulationJNI.Cone_set_origin__SWIG_0(swigCPtr, this, i, Vec3.getCPtr(value), value);
  }

  public int append_origin(Vec3 value) {
    return opensimSimulationJNI.Cone_append_origin(swigCPtr, this, Vec3.getCPtr(value), value);
  }

  public void constructProperty_origin(Vec3 initValue) {
    opensimSimulationJNI.Cone_constructProperty_origin(swigCPtr, this, Vec3.getCPtr(initValue), initValue);
  }

  public Vec3 get_origin() {
    return new Vec3(opensimSimulationJNI.Cone_get_origin__SWIG_1(swigCPtr, this), false);
  }

  public Vec3 upd_origin() {
    return new Vec3(opensimSimulationJNI.Cone_upd_origin__SWIG_1(swigCPtr, this), false);
  }

  public void set_origin(Vec3 value) {
    opensimSimulationJNI.Cone_set_origin__SWIG_1(swigCPtr, this, Vec3.getCPtr(value), value);
  }

  public void copyProperty_direction(Cone source) {
    opensimSimulationJNI.Cone_copyProperty_direction(swigCPtr, this, Cone.getCPtr(source), source);
  }

  public Vec3 get_direction(int i) {
    return new Vec3(opensimSimulationJNI.Cone_get_direction__SWIG_0(swigCPtr, this, i), false);
  }

  public Vec3 upd_direction(int i) {
    return new Vec3(opensimSimulationJNI.Cone_upd_direction__SWIG_0(swigCPtr, this, i), false);
  }

  public void set_direction(int i, Vec3 value) {
    opensimSimulationJNI.Cone_set_direction__SWIG_0(swigCPtr, this, i, Vec3.getCPtr(value), value);
  }

  public int append_direction(Vec3 value) {
    return opensimSimulationJNI.Cone_append_direction(swigCPtr, this, Vec3.getCPtr(value), value);
  }

  public void constructProperty_direction(Vec3 initValue) {
    opensimSimulationJNI.Cone_constructProperty_direction(swigCPtr, this, Vec3.getCPtr(initValue), initValue);
  }

  public Vec3 get_direction() {
    return new Vec3(opensimSimulationJNI.Cone_get_direction__SWIG_1(swigCPtr, this), false);
  }

  public Vec3 upd_direction() {
    return new Vec3(opensimSimulationJNI.Cone_upd_direction__SWIG_1(swigCPtr, this), false);
  }

  public void set_direction(Vec3 value) {
    opensimSimulationJNI.Cone_set_direction__SWIG_1(swigCPtr, this, Vec3.getCPtr(value), value);
  }

  public void copyProperty_base_radius(Cone source) {
    opensimSimulationJNI.Cone_copyProperty_base_radius(swigCPtr, this, Cone.getCPtr(source), source);
  }

  public double get_base_radius(int i) {
    return opensimSimulationJNI.Cone_get_base_radius__SWIG_0(swigCPtr, this, i);
  }

  public SWIGTYPE_p_double upd_base_radius(int i) {
    return new SWIGTYPE_p_double(opensimSimulationJNI.Cone_upd_base_radius__SWIG_0(swigCPtr, this, i), false);
  }

  public void set_base_radius(int i, double value) {
    opensimSimulationJNI.Cone_set_base_radius__SWIG_0(swigCPtr, this, i, value);
  }

  public int append_base_radius(double value) {
    return opensimSimulationJNI.Cone_append_base_radius(swigCPtr, this, value);
  }

  public void constructProperty_base_radius(double initValue) {
    opensimSimulationJNI.Cone_constructProperty_base_radius(swigCPtr, this, initValue);
  }

  public double get_base_radius() {
    return opensimSimulationJNI.Cone_get_base_radius__SWIG_1(swigCPtr, this);
  }

  public SWIGTYPE_p_double upd_base_radius() {
    return new SWIGTYPE_p_double(opensimSimulationJNI.Cone_upd_base_radius__SWIG_1(swigCPtr, this), false);
  }

  public void set_base_radius(double value) {
    opensimSimulationJNI.Cone_set_base_radius__SWIG_1(swigCPtr, this, value);
  }

  public void copyProperty_height(Cone source) {
    opensimSimulationJNI.Cone_copyProperty_height(swigCPtr, this, Cone.getCPtr(source), source);
  }

  public double get_height(int i) {
    return opensimSimulationJNI.Cone_get_height__SWIG_0(swigCPtr, this, i);
  }

  public SWIGTYPE_p_double upd_height(int i) {
    return new SWIGTYPE_p_double(opensimSimulationJNI.Cone_upd_height__SWIG_0(swigCPtr, this, i), false);
  }

  public void set_height(int i, double value) {
    opensimSimulationJNI.Cone_set_height__SWIG_0(swigCPtr, this, i, value);
  }

  public int append_height(double value) {
    return opensimSimulationJNI.Cone_append_height(swigCPtr, this, value);
  }

  public void constructProperty_height(double initValue) {
    opensimSimulationJNI.Cone_constructProperty_height(swigCPtr, this, initValue);
  }

  public double get_height() {
    return opensimSimulationJNI.Cone_get_height__SWIG_1(swigCPtr, this);
  }

  public SWIGTYPE_p_double upd_height() {
    return new SWIGTYPE_p_double(opensimSimulationJNI.Cone_upd_height__SWIG_1(swigCPtr, this), false);
  }

  public void set_height(double value) {
    opensimSimulationJNI.Cone_set_height__SWIG_1(swigCPtr, this, value);
  }

  /**
   *  Default constructor
   */
  public Cone() {
    this(opensimSimulationJNI.new_Cone__SWIG_0(), true);
  }

  /**
   *  Convenience constructor that takes radius and half-height
   */
  public Cone(Vec3 o, UnitVec3 dir, double height, double base) {
    this(opensimSimulationJNI.new_Cone__SWIG_1(Vec3.getCPtr(o), o, UnitVec3.getCPtr(dir), dir, height, base), true);
  }

}
