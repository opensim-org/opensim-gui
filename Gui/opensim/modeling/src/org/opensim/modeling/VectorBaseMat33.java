/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 *  This is a dataless rehash of the MatrixBase class to specialize it for Vectors.<br>
 *  This mostly entails overriding a few of the methods. Note that all the MatrixBase<br>
 *  operations remain available if you static_cast&lt;&gt; this up to a MatrixBase.
 */
public class VectorBaseMat33 extends MatrixBaseMat33 {
  private transient long swigCPtr;

  public VectorBaseMat33(long cPtr, boolean cMemoryOwn) {
    super(opensimSimbodyJNI.VectorBaseMat33_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(VectorBaseMat33 obj) {
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
        opensimSimbodyJNI.delete_VectorBaseMat33(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  /**
   *  These constructors create new VectorBase objects which own their<br>
   *  own data and are (at least by default) resizable. The resulting matrices<br>
   *  are m X 1 with the number of columns locked at 1. If there is any data<br>
   *  allocated but not explicitly initialized, that data will be uninitialized<br>
   *  garbage in Release builds but will be initialized to NaN (at a performance<br>
   *  cost) in Debug builds.<br>
   *  <br>
   *  Default constructor makes a 0x1 matrix locked at 1 column; you can<br>
   *  provide an initial allocation if you want.
   */
  public VectorBaseMat33(int m) {
    this(opensimSimbodyJNI.new_VectorBaseMat33__SWIG_0(m), true);
  }

  /**
   *  These constructors create new VectorBase objects which own their<br>
   *  own data and are (at least by default) resizable. The resulting matrices<br>
   *  are m X 1 with the number of columns locked at 1. If there is any data<br>
   *  allocated but not explicitly initialized, that data will be uninitialized<br>
   *  garbage in Release builds but will be initialized to NaN (at a performance<br>
   *  cost) in Debug builds.<br>
   *  <br>
   *  Default constructor makes a 0x1 matrix locked at 1 column; you can<br>
   *  provide an initial allocation if you want.
   */
  public VectorBaseMat33() {
    this(opensimSimbodyJNI.new_VectorBaseMat33__SWIG_1(), true);
  }

  /**
   *  Copy constructor is a deep copy (not appropriate for views!). That<br>
   *  means it creates a new, densely packed vector whose elements are<br>
   *  initialized from the source object.
   */
  public VectorBaseMat33(VectorBaseMat33 source) {
    this(opensimSimbodyJNI.new_VectorBaseMat33__SWIG_2(VectorBaseMat33.getCPtr(source), source), true);
  }

  public int size() {
    return opensimSimbodyJNI.VectorBaseMat33_size(swigCPtr, this);
  }

  public int nrow() {
    return opensimSimbodyJNI.VectorBaseMat33_nrow(swigCPtr, this);
  }

  public int ncol() {
    return opensimSimbodyJNI.VectorBaseMat33_ncol(swigCPtr, this);
  }

  public VectorBaseMat33 resize(int m) {
    return new VectorBaseMat33(opensimSimbodyJNI.VectorBaseMat33_resize(swigCPtr, this, m), false);
  }

  public VectorBaseMat33 resizeKeep(int m) {
    return new VectorBaseMat33(opensimSimbodyJNI.VectorBaseMat33_resizeKeep(swigCPtr, this, m), false);
  }

  public void clear() {
    opensimSimbodyJNI.VectorBaseMat33_clear(swigCPtr, this);
  }

  public Mat33 sum() {
    return new Mat33(opensimSimbodyJNI.VectorBaseMat33_sum(swigCPtr, this), true);
  }

}
