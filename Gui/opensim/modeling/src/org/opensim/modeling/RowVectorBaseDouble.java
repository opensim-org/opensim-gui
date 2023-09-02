/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 *  This is a dataless rehash of the MatrixBase class to specialize it for RowVectors.<br>
 *  This mostly entails overriding a few of the methods. Note that all the MatrixBase<br>
 *  operations remain available if you static_cast&lt;&gt; this up to a MatrixBase.
 */
public class RowVectorBaseDouble extends MatrixBaseDouble {
  private transient long swigCPtr;

  public RowVectorBaseDouble(long cPtr, boolean cMemoryOwn) {
    super(opensimSimbodyJNI.RowVectorBaseDouble_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(RowVectorBaseDouble obj) {
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
        opensimSimbodyJNI.delete_RowVectorBaseDouble(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

    @Override
    public double[][] getAsMat() {
        double[][] ret = new double[1][size()];
        for (int i = 0; i < size(); ++i) { ret[0][i] = get(i); }
        return ret;
    }

  /**
   *  These constructors create new RowVectorBase objects which own their<br>
   *  own data and are (at least by default) resizable. The resulting matrices<br>
   *  are 1 x n with the number of rows locked at 1. If there is any data<br>
   *  allocated but not explicitly initialized, that data will be uninitialized<br>
   *  garbage in Release builds but will be initialized to NaN (at a performance<br>
   *  cost) in Debug builds.<br>
   *  <br>
   *  Default constructor makes a 1x0 matrix locked at 1 row; you can<br>
   *  provide an initial allocation if you want.
   */
  public RowVectorBaseDouble(int n) {
    this(opensimSimbodyJNI.new_RowVectorBaseDouble__SWIG_0(n), true);
  }

  /**
   *  These constructors create new RowVectorBase objects which own their<br>
   *  own data and are (at least by default) resizable. The resulting matrices<br>
   *  are 1 x n with the number of rows locked at 1. If there is any data<br>
   *  allocated but not explicitly initialized, that data will be uninitialized<br>
   *  garbage in Release builds but will be initialized to NaN (at a performance<br>
   *  cost) in Debug builds.<br>
   *  <br>
   *  Default constructor makes a 1x0 matrix locked at 1 row; you can<br>
   *  provide an initial allocation if you want.
   */
  public RowVectorBaseDouble() {
    this(opensimSimbodyJNI.new_RowVectorBaseDouble__SWIG_1(), true);
  }

  /**
   *  Copy constructor is a deep copy (not appropriate for views!). That<br>
   *  means it creates a new, densely packed vector whose elements are<br>
   *  initialized from the source object.    
   */
  public RowVectorBaseDouble(RowVectorBaseDouble source) {
    this(opensimSimbodyJNI.new_RowVectorBaseDouble__SWIG_2(RowVectorBaseDouble.getCPtr(source), source), true);
  }

  /**
   *  Construct an owner row vector of length n, with each element initialized to<br>
   *  the given value.
   */
  public RowVectorBaseDouble(int n, double initialValue) {
    this(opensimSimbodyJNI.new_RowVectorBaseDouble__SWIG_3(n, initialValue), true);
  }

  /**
   *  Construct an owner vector of length n, with the elements initialized sequentially<br>
   *  from a C++ array of elements which is assumed to be of length n. Note that we<br>
   *  are expecting C++ packing; don't use this to initialize one Simmatrix vector<br>
   *  from another because Simmatrix may pack its elements more densely than C++.
   */
  public RowVectorBaseDouble(int n, SWIGTYPE_p_double cppInitialValues) {
    this(opensimSimbodyJNI.new_RowVectorBaseDouble__SWIG_4(n, SWIGTYPE_p_double.getCPtr(cppInitialValues)), true);
  }

  public int size() {
    return opensimSimbodyJNI.RowVectorBaseDouble_size(swigCPtr, this);
  }

  public int nrow() {
    return opensimSimbodyJNI.RowVectorBaseDouble_nrow(swigCPtr, this);
  }

  public int ncol() {
    return opensimSimbodyJNI.RowVectorBaseDouble_ncol(swigCPtr, this);
  }

  public SWIGTYPE_p_ptrdiff_t nelt() {
    return new SWIGTYPE_p_ptrdiff_t(opensimSimbodyJNI.RowVectorBaseDouble_nelt(swigCPtr, this), true);
  }

  public RowVectorView index(SWIGTYPE_p_Array_T_int_t indices) {
    return new RowVectorView(opensimSimbodyJNI.RowVectorBaseDouble_index(swigCPtr, this, SWIGTYPE_p_Array_T_int_t.getCPtr(indices)), true);
  }

  public RowVectorView updIndex(SWIGTYPE_p_Array_T_int_t indices) {
    return new RowVectorView(opensimSimbodyJNI.RowVectorBaseDouble_updIndex(swigCPtr, this, SWIGTYPE_p_Array_T_int_t.getCPtr(indices)), true);
  }

  public RowVectorBaseDouble resize(int n) {
    return new RowVectorBaseDouble(opensimSimbodyJNI.RowVectorBaseDouble_resize(swigCPtr, this, n), false);
  }

  public RowVectorBaseDouble resizeKeep(int n) {
    return new RowVectorBaseDouble(opensimSimbodyJNI.RowVectorBaseDouble_resizeKeep(swigCPtr, this, n), false);
  }

  public void clear() {
    opensimSimbodyJNI.RowVectorBaseDouble_clear(swigCPtr, this);
  }

  public double sum() {
    return opensimSimbodyJNI.RowVectorBaseDouble_sum(swigCPtr, this);
  }

  public SWIGTYPE_p_VectorIteratorT_double_SimTK__RowVectorBaseT_double_t_t begin() {
    return new SWIGTYPE_p_VectorIteratorT_double_SimTK__RowVectorBaseT_double_t_t(opensimSimbodyJNI.RowVectorBaseDouble_begin(swigCPtr, this), true);
  }

  public SWIGTYPE_p_VectorIteratorT_double_SimTK__RowVectorBaseT_double_t_t end() {
    return new SWIGTYPE_p_VectorIteratorT_double_SimTK__RowVectorBaseT_double_t_t(opensimSimbodyJNI.RowVectorBaseDouble_end(swigCPtr, this), true);
  }

  public double get(int i) {
    return opensimSimbodyJNI.RowVectorBaseDouble_get(swigCPtr, this, i);
  }

  public void set(int i, double value) {
    opensimSimbodyJNI.RowVectorBaseDouble_set(swigCPtr, this, i, value);
  }

  public double __getitem__(int i) {
    return opensimSimbodyJNI.RowVectorBaseDouble___getitem__(swigCPtr, this, i);
  }

  public void __setitem__(int i, double value) {
    opensimSimbodyJNI.RowVectorBaseDouble___setitem__(swigCPtr, this, i, value);
  }

}
