/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.1.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 *  This class is identical to a Vector_; it is used only to manage the C++ rules<br>
 *  for when copy constructors are called by introducing a separate type to<br>
 *  prevent certain allowed optimizations from occuring when we don't want them.<br>
 *  Despite the name, this may be an owner if a Vector_ is recast to a VectorView_.<br>
 *  However, there are no owner constructors for VectorView_. 
 */
public class VectorViewVec3 extends VectorBaseVec3 {
  private transient long swigCPtr;

  public VectorViewVec3(long cPtr, boolean cMemoryOwn) {
    super(opensimSimbodyJNI.VectorViewVec3_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(VectorViewVec3 obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  public static long swigRelease(VectorViewVec3 obj) {
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
        opensimSimbodyJNI.delete_VectorViewVec3(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public VectorViewVec3(VectorViewVec3 v) {
    this(opensimSimbodyJNI.new_VectorViewVec3(VectorViewVec3.getCPtr(v), v), true);
  }

  public VectorViewVec3 multiplyAssign(double t) {
    return new VectorViewVec3(opensimSimbodyJNI.VectorViewVec3_multiplyAssign(swigCPtr, this, t), true);
  }

}
