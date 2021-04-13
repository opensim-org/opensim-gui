/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 *  This class is identical to a RowVector_; it is used only to manage the C++ <br>
 *  rules for when copy constructors are called by introducing a separate type to<br>
 *  prevent certain allowed optimizations from occuring when we don't want them.<br>
 *  Despite the name, this may be an owner if a RowVector_ is recast to a <br>
 *  RowVectorView_. However, there are no owner constructors for RowVectorView_. 
 */
public class RowVectorViewMat33 extends RowVectorBaseMat33 {
  private transient long swigCPtr;

  public RowVectorViewMat33(long cPtr, boolean cMemoryOwn) {
    super(opensimSimbodyJNI.RowVectorViewMat33_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(RowVectorViewMat33 obj) {
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
        opensimSimbodyJNI.delete_RowVectorViewMat33(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public RowVectorViewMat33(RowVectorViewMat33 r) {
    this(opensimSimbodyJNI.new_RowVectorViewMat33(RowVectorViewMat33.getCPtr(r), r), true);
  }

}
