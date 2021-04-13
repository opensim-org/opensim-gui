/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class MocoFrameDistanceConstraintPair extends OpenSimObject {
  private transient long swigCPtr;

  public MocoFrameDistanceConstraintPair(long cPtr, boolean cMemoryOwn) {
    super(opensimMocoJNI.MocoFrameDistanceConstraintPair_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(MocoFrameDistanceConstraintPair obj) {
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
        opensimMocoJNI.delete_MocoFrameDistanceConstraintPair(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static MocoFrameDistanceConstraintPair safeDownCast(OpenSimObject obj) {
    long cPtr = opensimMocoJNI.MocoFrameDistanceConstraintPair_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new MocoFrameDistanceConstraintPair(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimMocoJNI.MocoFrameDistanceConstraintPair_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimMocoJNI.MocoFrameDistanceConstraintPair_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimMocoJNI.MocoFrameDistanceConstraintPair_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new MocoFrameDistanceConstraintPair(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimMocoJNI.MocoFrameDistanceConstraintPair_getConcreteClassName(swigCPtr, this);
  }

  public void copyProperty_frame1_path(MocoFrameDistanceConstraintPair source) {
    opensimMocoJNI.MocoFrameDistanceConstraintPair_copyProperty_frame1_path(swigCPtr, this, MocoFrameDistanceConstraintPair.getCPtr(source), source);
  }

  public String get_frame1_path(int i) {
    return opensimMocoJNI.MocoFrameDistanceConstraintPair_get_frame1_path__SWIG_0(swigCPtr, this, i);
  }

  public SWIGTYPE_p_std__string upd_frame1_path(int i) {
    return new SWIGTYPE_p_std__string(opensimMocoJNI.MocoFrameDistanceConstraintPair_upd_frame1_path__SWIG_0(swigCPtr, this, i), false);
  }

  public void set_frame1_path(int i, String value) {
    opensimMocoJNI.MocoFrameDistanceConstraintPair_set_frame1_path__SWIG_0(swigCPtr, this, i, value);
  }

  public int append_frame1_path(String value) {
    return opensimMocoJNI.MocoFrameDistanceConstraintPair_append_frame1_path(swigCPtr, this, value);
  }

  public void constructProperty_frame1_path(String initValue) {
    opensimMocoJNI.MocoFrameDistanceConstraintPair_constructProperty_frame1_path(swigCPtr, this, initValue);
  }

  public String get_frame1_path() {
    return opensimMocoJNI.MocoFrameDistanceConstraintPair_get_frame1_path__SWIG_1(swigCPtr, this);
  }

  public SWIGTYPE_p_std__string upd_frame1_path() {
    return new SWIGTYPE_p_std__string(opensimMocoJNI.MocoFrameDistanceConstraintPair_upd_frame1_path__SWIG_1(swigCPtr, this), false);
  }

  public void set_frame1_path(String value) {
    opensimMocoJNI.MocoFrameDistanceConstraintPair_set_frame1_path__SWIG_1(swigCPtr, this, value);
  }

  public void copyProperty_frame2_path(MocoFrameDistanceConstraintPair source) {
    opensimMocoJNI.MocoFrameDistanceConstraintPair_copyProperty_frame2_path(swigCPtr, this, MocoFrameDistanceConstraintPair.getCPtr(source), source);
  }

  public String get_frame2_path(int i) {
    return opensimMocoJNI.MocoFrameDistanceConstraintPair_get_frame2_path__SWIG_0(swigCPtr, this, i);
  }

  public SWIGTYPE_p_std__string upd_frame2_path(int i) {
    return new SWIGTYPE_p_std__string(opensimMocoJNI.MocoFrameDistanceConstraintPair_upd_frame2_path__SWIG_0(swigCPtr, this, i), false);
  }

  public void set_frame2_path(int i, String value) {
    opensimMocoJNI.MocoFrameDistanceConstraintPair_set_frame2_path__SWIG_0(swigCPtr, this, i, value);
  }

  public int append_frame2_path(String value) {
    return opensimMocoJNI.MocoFrameDistanceConstraintPair_append_frame2_path(swigCPtr, this, value);
  }

  public void constructProperty_frame2_path(String initValue) {
    opensimMocoJNI.MocoFrameDistanceConstraintPair_constructProperty_frame2_path(swigCPtr, this, initValue);
  }

  public String get_frame2_path() {
    return opensimMocoJNI.MocoFrameDistanceConstraintPair_get_frame2_path__SWIG_1(swigCPtr, this);
  }

  public SWIGTYPE_p_std__string upd_frame2_path() {
    return new SWIGTYPE_p_std__string(opensimMocoJNI.MocoFrameDistanceConstraintPair_upd_frame2_path__SWIG_1(swigCPtr, this), false);
  }

  public void set_frame2_path(String value) {
    opensimMocoJNI.MocoFrameDistanceConstraintPair_set_frame2_path__SWIG_1(swigCPtr, this, value);
  }

  public void copyProperty_minimum_distance(MocoFrameDistanceConstraintPair source) {
    opensimMocoJNI.MocoFrameDistanceConstraintPair_copyProperty_minimum_distance(swigCPtr, this, MocoFrameDistanceConstraintPair.getCPtr(source), source);
  }

  public double get_minimum_distance(int i) {
    return opensimMocoJNI.MocoFrameDistanceConstraintPair_get_minimum_distance__SWIG_0(swigCPtr, this, i);
  }

  public SWIGTYPE_p_double upd_minimum_distance(int i) {
    return new SWIGTYPE_p_double(opensimMocoJNI.MocoFrameDistanceConstraintPair_upd_minimum_distance__SWIG_0(swigCPtr, this, i), false);
  }

  public void set_minimum_distance(int i, double value) {
    opensimMocoJNI.MocoFrameDistanceConstraintPair_set_minimum_distance__SWIG_0(swigCPtr, this, i, value);
  }

  public int append_minimum_distance(double value) {
    return opensimMocoJNI.MocoFrameDistanceConstraintPair_append_minimum_distance(swigCPtr, this, value);
  }

  public void constructProperty_minimum_distance(double initValue) {
    opensimMocoJNI.MocoFrameDistanceConstraintPair_constructProperty_minimum_distance(swigCPtr, this, initValue);
  }

  public double get_minimum_distance() {
    return opensimMocoJNI.MocoFrameDistanceConstraintPair_get_minimum_distance__SWIG_1(swigCPtr, this);
  }

  public SWIGTYPE_p_double upd_minimum_distance() {
    return new SWIGTYPE_p_double(opensimMocoJNI.MocoFrameDistanceConstraintPair_upd_minimum_distance__SWIG_1(swigCPtr, this), false);
  }

  public void set_minimum_distance(double value) {
    opensimMocoJNI.MocoFrameDistanceConstraintPair_set_minimum_distance__SWIG_1(swigCPtr, this, value);
  }

  public void copyProperty_maximum_distance(MocoFrameDistanceConstraintPair source) {
    opensimMocoJNI.MocoFrameDistanceConstraintPair_copyProperty_maximum_distance(swigCPtr, this, MocoFrameDistanceConstraintPair.getCPtr(source), source);
  }

  public double get_maximum_distance(int i) {
    return opensimMocoJNI.MocoFrameDistanceConstraintPair_get_maximum_distance__SWIG_0(swigCPtr, this, i);
  }

  public SWIGTYPE_p_double upd_maximum_distance(int i) {
    return new SWIGTYPE_p_double(opensimMocoJNI.MocoFrameDistanceConstraintPair_upd_maximum_distance__SWIG_0(swigCPtr, this, i), false);
  }

  public void set_maximum_distance(int i, double value) {
    opensimMocoJNI.MocoFrameDistanceConstraintPair_set_maximum_distance__SWIG_0(swigCPtr, this, i, value);
  }

  public int append_maximum_distance(double value) {
    return opensimMocoJNI.MocoFrameDistanceConstraintPair_append_maximum_distance(swigCPtr, this, value);
  }

  public void constructProperty_maximum_distance(double initValue) {
    opensimMocoJNI.MocoFrameDistanceConstraintPair_constructProperty_maximum_distance(swigCPtr, this, initValue);
  }

  public double get_maximum_distance() {
    return opensimMocoJNI.MocoFrameDistanceConstraintPair_get_maximum_distance__SWIG_1(swigCPtr, this);
  }

  public SWIGTYPE_p_double upd_maximum_distance() {
    return new SWIGTYPE_p_double(opensimMocoJNI.MocoFrameDistanceConstraintPair_upd_maximum_distance__SWIG_1(swigCPtr, this), false);
  }

  public void set_maximum_distance(double value) {
    opensimMocoJNI.MocoFrameDistanceConstraintPair_set_maximum_distance__SWIG_1(swigCPtr, this, value);
  }

  public MocoFrameDistanceConstraintPair() {
    this(opensimMocoJNI.new_MocoFrameDistanceConstraintPair__SWIG_0(), true);
  }

  public MocoFrameDistanceConstraintPair(String firstFramePath, String secondFramePath, double minimum_distance, double maximum_distance) {
    this(opensimMocoJNI.new_MocoFrameDistanceConstraintPair__SWIG_1(firstFramePath, secondFramePath, minimum_distance, maximum_distance), true);
  }

}
