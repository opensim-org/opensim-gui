/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 *  This class represents a small matrix whose size is known at compile time, <br>
 * containing elements of any Composite Numerical Type (CNT) and engineered to<br>
 * have no runtime overhead whatsoever. Memory layout defaults to packed,<br>
 * column ordered storage but can be specified to have any regular row and <br>
 * column spacing. A Mat object is itself a Composite Numerical Type and can thus<br>
 * be the element type for other matrix and vector types.<br>
 * <br>
 * <br>
 * <br>
 * @see Matrix_ for handling of large or variable-size matrices.@see SymMat, Vec, Row
 */
public class Mat33 {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  public Mat33(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  public static long getCPtr(Mat33 obj) {
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
        opensimSimbodyJNI.delete_Mat33(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  /**
   *  Return the total number of elements M*N contained in this Mat. *
   */
  public static int size() {
    return opensimSimbodyJNI.Mat33_size();
  }

  /**
   *  Return the number of rows in this Mat, echoing the value supplied<br>
   *     for the template paramter <i>M</i>. *
   */
  public static int nrow() {
    return opensimSimbodyJNI.Mat33_nrow();
  }

  /**
   *  Return the number of columns in this Mat, echoing the value supplied<br>
   *     for the template paramter <i>N</i>. *
   */
  public static int ncol() {
    return opensimSimbodyJNI.Mat33_ncol();
  }

  /**
   *  Default construction initializes to NaN when debugging but is left <br>
   *     uninitialized otherwise to ensure that there is no overhead. *
   */
  public Mat33() {
    this(opensimSimbodyJNI.new_Mat33__SWIG_0(), true);
  }

  /**
   *  Copy constructor copies only the elements that are present and does<br>
   *     not touch any unused memory space between them if they are not packed. *
   */
  public Mat33(Mat33 src) {
    this(opensimSimbodyJNI.new_Mat33__SWIG_1(Mat33.getCPtr(src), src), true);
  }

  /**
   *  Explicit construction from a single element <i>e</i> of this Mat's element<br>
   *     type E sets all the main diagonal elements to <i>e</i> but sets the rest of <br>
   *     the elements to zero. *
   */
  public Mat33(double e) {
    this(opensimSimbodyJNI.new_Mat33__SWIG_2(e), true);
  }

  /**
   *  Explicit construction from an int value means we convert the int into<br>
   *     an object of this Mat's element type E, and then apply the single-element<br>
   *     constructor above which sets the Mat to zero except for its main diagonal<br>
   *     elements which will all be set to the given value. To convert an int to<br>
   *     an element, we first turn it into the appropriate-precision floating point <br>
   *     number, and then call E's constructor that takes a single scalar. *
   */
  public Mat33(int i) {
    this(opensimSimbodyJNI.new_Mat33__SWIG_3(i), true);
  }

  public Mat33(double e0, double e1) {
    this(opensimSimbodyJNI.new_Mat33__SWIG_4(e0, e1), true);
  }

  public Mat33(double e0, double e1, double e2) {
    this(opensimSimbodyJNI.new_Mat33__SWIG_5(e0, e1, e2), true);
  }

  public Mat33(double e0, double e1, double e2, double e3) {
    this(opensimSimbodyJNI.new_Mat33__SWIG_6(e0, e1, e2, e3), true);
  }

  public Mat33(double e0, double e1, double e2, double e3, double e4) {
    this(opensimSimbodyJNI.new_Mat33__SWIG_7(e0, e1, e2, e3, e4), true);
  }

  public Mat33(double e0, double e1, double e2, double e3, double e4, double e5) {
    this(opensimSimbodyJNI.new_Mat33__SWIG_8(e0, e1, e2, e3, e4, e5), true);
  }

  public Mat33(double e0, double e1, double e2, double e3, double e4, double e5, double e6) {
    this(opensimSimbodyJNI.new_Mat33__SWIG_9(e0, e1, e2, e3, e4, e5, e6), true);
  }

  public Mat33(double e0, double e1, double e2, double e3, double e4, double e5, double e6, double e7) {
    this(opensimSimbodyJNI.new_Mat33__SWIG_10(e0, e1, e2, e3, e4, e5, e6, e7), true);
  }

  public Mat33(double e0, double e1, double e2, double e3, double e4, double e5, double e6, double e7, double e8) {
    this(opensimSimbodyJNI.new_Mat33__SWIG_11(e0, e1, e2, e3, e4, e5, e6, e7, e8), true);
  }

  public Mat33(double e0, double e1, double e2, double e3, double e4, double e5, double e6, double e7, double e8, double e9) {
    this(opensimSimbodyJNI.new_Mat33__SWIG_12(e0, e1, e2, e3, e4, e5, e6, e7, e8, e9), true);
  }

  public Mat33(double e0, double e1, double e2, double e3, double e4, double e5, double e6, double e7, double e8, double e9, double e10) {
    this(opensimSimbodyJNI.new_Mat33__SWIG_13(e0, e1, e2, e3, e4, e5, e6, e7, e8, e9, e10), true);
  }

  public Mat33(double e0, double e1, double e2, double e3, double e4, double e5, double e6, double e7, double e8, double e9, double e10, double e11) {
    this(opensimSimbodyJNI.new_Mat33__SWIG_14(e0, e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11), true);
  }

  public Mat33(double e0, double e1, double e2, double e3, double e4, double e5, double e6, double e7, double e8, double e9, double e10, double e11, double e12) {
    this(opensimSimbodyJNI.new_Mat33__SWIG_15(e0, e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11, e12), true);
  }

  public Mat33(double e0, double e1, double e2, double e3, double e4, double e5, double e6, double e7, double e8, double e9, double e10, double e11, double e12, double e13) {
    this(opensimSimbodyJNI.new_Mat33__SWIG_16(e0, e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11, e12, e13), true);
  }

  public Mat33(double e0, double e1, double e2, double e3, double e4, double e5, double e6, double e7, double e8, double e9, double e10, double e11, double e12, double e13, double e14) {
    this(opensimSimbodyJNI.new_Mat33__SWIG_17(e0, e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11, e12, e13, e14), true);
  }

  public Mat33(double e0, double e1, double e2, double e3, double e4, double e5, double e6, double e7, double e8, double e9, double e10, double e11, double e12, double e13, double e14, double e15) {
    this(opensimSimbodyJNI.new_Mat33__SWIG_18(e0, e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11, e12, e13, e14, e15), true);
  }

  public void setToNaN() {
    opensimSimbodyJNI.Mat33_setToNaN(swigCPtr, this);
  }

  public void setToZero() {
    opensimSimbodyJNI.Mat33_setToZero(swigCPtr, this);
  }

  /**
   *  Return true if any element of this Mat contains a NaN anywhere.
   */
  public boolean isNaN() {
    return opensimSimbodyJNI.Mat33_isNaN(swigCPtr, this);
  }

  /**
   *  Return true if any element of this Mat contains a +Inf<br>
   *  or -Inf somewhere but no element contains a NaN anywhere.
   */
  public boolean isInf() {
    return opensimSimbodyJNI.Mat33_isInf(swigCPtr, this);
  }

  /**
   *  Return true if no element contains an Infinity or a NaN.
   */
  public boolean isFinite() {
    return opensimSimbodyJNI.Mat33_isFinite(swigCPtr, this);
  }

  /**
   *  For approximate comparisions, the default tolerance to use for a matrix is<br>
   *  its shortest dimension times its elements' default tolerance.
   */
  public static double getDefaultTolerance() {
    return opensimSimbodyJNI.Mat33_getDefaultTolerance();
  }

  /**
   *  %Test whether this is numerically a "scalar" matrix, meaning that it is <br>
   *  a diagonal matrix in which each diagonal element is numerically equal to <br>
   *  the same scalar, using either a specified tolerance or the matrix's <br>
   *  default tolerance (which is always the same or looser than the default<br>
   *  tolerance for one of its elements).
   */
  public boolean isNumericallyEqual(double e, double tol) {
    return opensimSimbodyJNI.Mat33_isNumericallyEqual__SWIG_0(swigCPtr, this, e, tol);
  }

  /**
   *  %Test whether this is numerically a "scalar" matrix, meaning that it is <br>
   *  a diagonal matrix in which each diagonal element is numerically equal to <br>
   *  the same scalar, using either a specified tolerance or the matrix's <br>
   *  default tolerance (which is always the same or looser than the default<br>
   *  tolerance for one of its elements).
   */
  public boolean isNumericallyEqual(double e) {
    return opensimSimbodyJNI.Mat33_isNumericallyEqual__SWIG_1(swigCPtr, this, e);
  }

  /**
   *  A Matrix is symmetric (actually Hermitian) if it is square and each <br>
   *  element (i,j) is the Hermitian transpose of element (j,i). Here we<br>
   *  are testing for numerical symmetry, meaning that the symmetry condition<br>
   *  is satisified to within a tolerance (supplied or default). This is <br>
   *  a relatively expensive test since all elements must be examined but<br>
   *  can be very useful in Debug mode to check assumptions.<br>
   *  @see isExactlySymmetric() for a rarely-used exact equality test
   */
  public boolean isNumericallySymmetric(double tol) {
    return opensimSimbodyJNI.Mat33_isNumericallySymmetric__SWIG_0(swigCPtr, this, tol);
  }

  /**
   *  A Matrix is symmetric (actually Hermitian) if it is square and each <br>
   *  element (i,j) is the Hermitian transpose of element (j,i). Here we<br>
   *  are testing for numerical symmetry, meaning that the symmetry condition<br>
   *  is satisified to within a tolerance (supplied or default). This is <br>
   *  a relatively expensive test since all elements must be examined but<br>
   *  can be very useful in Debug mode to check assumptions.<br>
   *  @see isExactlySymmetric() for a rarely-used exact equality test
   */
  public boolean isNumericallySymmetric() {
    return opensimSimbodyJNI.Mat33_isNumericallySymmetric__SWIG_1(swigCPtr, this);
  }

  /**
   *  A Matrix is symmetric (actually Hermitian) if it is square and each <br>
   *  element (i,j) is the Hermitian (conjugate) transpose of element (j,i). This<br>
   *  method tests for exact (bitwise) equality and is too stringent for most <br>
   *  purposes; don't use it unless you know that the corresponding elements<br>
   *  should be bitwise conjugates, typically because you put them there directly.<br>
   *  @see isNumericallySymmetric() for a more useful method
   */
  public boolean isExactlySymmetric() {
    return opensimSimbodyJNI.Mat33_isExactlySymmetric(swigCPtr, this);
  }

  public String toString() {
    return opensimSimbodyJNI.Mat33_toString(swigCPtr, this);
  }

  /**
   *  Variant of indexing operator that's scripting friendly to get entry (i, j) *
   */
  public double get(int i, int j) {
    return opensimSimbodyJNI.Mat33_get(swigCPtr, this, i, j);
  }

  /**
   *  Variant of indexing operator that's scripting friendly to set entry (i, j) *
   */
  public void set(int i, int j, double value) {
    opensimSimbodyJNI.Mat33_set(swigCPtr, this, i, j, value);
  }

}
