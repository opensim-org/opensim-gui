/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.1.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 *  This is the Matrix class intended to appear in user code. It can be a <br>
 *  fixed-size view of someone else's data, or can be a resizable data owner itself.
 */
public class MatrixMat33 extends MatrixBaseMat33 {
  private transient long swigCPtr;

  public MatrixMat33(long cPtr, boolean cMemoryOwn) {
    super(opensimSimbodyJNI.MatrixMat33_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(MatrixMat33 obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  public static long swigRelease(MatrixMat33 obj) {
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
        opensimSimbodyJNI.delete_MatrixMat33(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public MatrixMat33() {
    this(opensimSimbodyJNI.new_MatrixMat33__SWIG_0(), true);
  }

  public MatrixMat33(MatrixMat33 src) {
    this(opensimSimbodyJNI.new_MatrixMat33__SWIG_1(MatrixMat33.getCPtr(src), src), true);
  }

  public MatrixMat33(int m, int n) {
    this(opensimSimbodyJNI.new_MatrixMat33__SWIG_2(m, n), true);
  }

  public MatrixMat33(int m, int n, Mat33 initialValue) {
    this(opensimSimbodyJNI.new_MatrixMat33__SWIG_3(m, n, Mat33.getCPtr(initialValue), initialValue), true);
  }

  public String toString() {
    return opensimSimbodyJNI.MatrixMat33_toString(swigCPtr, this);
  }

  /**
   *  Variant of indexing operator that's scripting friendly to get entry (i, j) *
   */
  public Mat33 get(int i, int j) {
    return new Mat33(opensimSimbodyJNI.MatrixMat33_get(swigCPtr, this, i, j), false);
  }

  /**
   *  Variant of indexing operator that's scripting friendly to set entry (i, j) *
   */
  public void set(int i, int j, Mat33 value) {
    opensimSimbodyJNI.MatrixMat33_set(swigCPtr, this, i, j, Mat33.getCPtr(value), value);
  }

}
