/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.1.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 *  RowVectors are much less common than Vectors. However, if a Simmatrix user <br>
 *  wants one, this is the class intended to appear in user code. It can be a <br>
 *  fixed-size view of someone else's data, or can be a resizable data owner <br>
 *  itself, although of course it will always have just one row.
 */
public class RowVectorMat33 extends RowVectorBaseMat33 {
  private transient long swigCPtr;

  public RowVectorMat33(long cPtr, boolean cMemoryOwn) {
    super(opensimSimbodyJNI.RowVectorMat33_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(RowVectorMat33 obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  public static long swigRelease(RowVectorMat33 obj) {
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
        opensimSimbodyJNI.delete_RowVectorMat33(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public RowVectorMat33() {
    this(opensimSimbodyJNI.new_RowVectorMat33__SWIG_0(), true);
  }

  public RowVectorMat33(RowVectorMat33 src) {
    this(opensimSimbodyJNI.new_RowVectorMat33__SWIG_1(RowVectorMat33.getCPtr(src), src), true);
  }

  public RowVectorMat33(RowVectorBaseMat33 src) {
    this(opensimSimbodyJNI.new_RowVectorMat33__SWIG_2(RowVectorBaseMat33.getCPtr(src), src), true);
  }

  public RowVectorMat33(int n) {
    this(opensimSimbodyJNI.new_RowVectorMat33__SWIG_3(n), true);
  }

  public RowVectorMat33(int n, Mat33 cppInitialValues) {
    this(opensimSimbodyJNI.new_RowVectorMat33__SWIG_4(n, Mat33.getCPtr(cppInitialValues), cppInitialValues), true);
  }

}
