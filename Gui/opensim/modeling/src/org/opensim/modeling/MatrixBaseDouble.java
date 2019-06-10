/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.9
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class MatrixBaseDouble {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  public MatrixBaseDouble(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  public static long getCPtr(MatrixBaseDouble obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimSimbodyJNI.delete_MatrixBaseDouble(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public int nrow() {
    return opensimSimbodyJNI.MatrixBaseDouble_nrow(swigCPtr, this);
  }

  public int ncol() {
    return opensimSimbodyJNI.MatrixBaseDouble_ncol(swigCPtr, this);
  }

  public boolean isResizeable() {
    return opensimSimbodyJNI.MatrixBaseDouble_isResizeable(swigCPtr, this);
  }

  public MatrixBaseDouble() {
    this(opensimSimbodyJNI.new_MatrixBaseDouble__SWIG_0(), true);
  }

  public MatrixBaseDouble(int m, int n) {
    this(opensimSimbodyJNI.new_MatrixBaseDouble__SWIG_1(m, n), true);
  }

  public void clear() {
    opensimSimbodyJNI.MatrixBaseDouble_clear(swigCPtr, this);
  }

  public MatrixBaseDouble setTo(double t) {
    return new MatrixBaseDouble(opensimSimbodyJNI.MatrixBaseDouble_setTo(swigCPtr, this, t), false);
  }

  public MatrixBaseDouble setToNaN() {
    return new MatrixBaseDouble(opensimSimbodyJNI.MatrixBaseDouble_setToNaN(swigCPtr, this), false);
  }

  public MatrixBaseDouble setToZero() {
    return new MatrixBaseDouble(opensimSimbodyJNI.MatrixBaseDouble_setToZero(swigCPtr, this), false);
  }

  public double getElt(int i, int j) {
    return opensimSimbodyJNI.MatrixBaseDouble_getElt(swigCPtr, this, i, j);
  }

  public MatrixBaseDouble negateInPlace() {
    return new MatrixBaseDouble(opensimSimbodyJNI.MatrixBaseDouble_negateInPlace(swigCPtr, this), false);
  }

  public MatrixBaseDouble resize(int m, int n) {
    return new MatrixBaseDouble(opensimSimbodyJNI.MatrixBaseDouble_resize(swigCPtr, this, m, n), false);
  }

  public MatrixBaseDouble resizeKeep(int m, int n) {
    return new MatrixBaseDouble(opensimSimbodyJNI.MatrixBaseDouble_resizeKeep(swigCPtr, this, m, n), false);
  }

  public void lockShape() {
    opensimSimbodyJNI.MatrixBaseDouble_lockShape(swigCPtr, this);
  }

  public void unlockShape() {
    opensimSimbodyJNI.MatrixBaseDouble_unlockShape(swigCPtr, this);
  }

  public final static int NScalarsPerElement = opensimSimbodyJNI.MatrixBaseDouble_NScalarsPerElement_get();
  public final static int CppNScalarsPerElement = opensimSimbodyJNI.MatrixBaseDouble_CppNScalarsPerElement_get();

}
