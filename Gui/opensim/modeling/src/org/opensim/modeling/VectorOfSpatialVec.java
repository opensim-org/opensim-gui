/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 *  This is the Vector class intended to appear in user code. It can be a <br>
 *  fixed-size view of someone else's data, or can be a resizable data owner <br>
 *  itself, although of course it will always have just one column.
 */
public class VectorOfSpatialVec {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  public VectorOfSpatialVec(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  public static long getCPtr(VectorOfSpatialVec obj) {
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
        opensimSimbodyJNI.delete_VectorOfSpatialVec(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public VectorOfSpatialVec() {
    this(opensimSimbodyJNI.new_VectorOfSpatialVec__SWIG_0(), true);
  }

  public VectorOfSpatialVec(VectorOfSpatialVec src) {
    this(opensimSimbodyJNI.new_VectorOfSpatialVec__SWIG_1(VectorOfSpatialVec.getCPtr(src), src), true);
  }

  public VectorOfSpatialVec(int m, SpatialVec initialValue) {
    this(opensimSimbodyJNI.new_VectorOfSpatialVec__SWIG_2(m, SpatialVec.getCPtr(initialValue), initialValue), true);
  }

  public String toString() {
    return opensimSimbodyJNI.VectorOfSpatialVec_toString(swigCPtr, this);
  }

  /**
   *  Variant of operator[] that's scripting friendly to get ith entry *
   */
  public SpatialVec get(int i) {
    return new SpatialVec(opensimSimbodyJNI.VectorOfSpatialVec_get(swigCPtr, this, i), false);
  }

  /**
   *  Variant of operator[] that's scripting friendly to set ith entry *
   */
  public void set(int i, SpatialVec value) {
    opensimSimbodyJNI.VectorOfSpatialVec_set(swigCPtr, this, i, SpatialVec.getCPtr(value), value);
  }

}
