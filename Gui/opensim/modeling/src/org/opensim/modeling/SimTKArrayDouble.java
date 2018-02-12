/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class SimTKArrayDouble {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected SimTKArrayDouble(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(SimTKArrayDouble obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimSimbodyJNI.delete_SimTKArrayDouble(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public SimTKArrayDouble() {
    this(opensimSimbodyJNI.new_SimTKArrayDouble__SWIG_0(), true);
  }

  public SimTKArrayDouble(long n) {
    this(opensimSimbodyJNI.new_SimTKArrayDouble__SWIG_1(n), true);
  }

  public SimTKArrayDouble(long n, double initVal) {
    this(opensimSimbodyJNI.new_SimTKArrayDouble__SWIG_2(n, initVal), true);
  }

  public SimTKArrayDouble(SimTKArrayDouble src) {
    this(opensimSimbodyJNI.new_SimTKArrayDouble__SWIG_3(SimTKArrayDouble.getCPtr(src), src), true);
  }

  public SimTKArrayDouble(SWIGTYPE_p_double first, SWIGTYPE_p_double last1, DontCopy arg2) {
    this(opensimSimbodyJNI.new_SimTKArrayDouble__SWIG_4(SWIGTYPE_p_double.getCPtr(first), SWIGTYPE_p_double.getCPtr(last1), DontCopy.getCPtr(arg2), arg2), true);
  }

  public SimTKArrayDouble deallocate() {
    return new SimTKArrayDouble(opensimSimbodyJNI.SimTKArrayDouble_deallocate(swigCPtr, this), false);
  }

  public void assign(long n, double fillValue) {
    opensimSimbodyJNI.SimTKArrayDouble_assign(swigCPtr, this, n, fillValue);
  }

  public void fill(double fillValue) {
    opensimSimbodyJNI.SimTKArrayDouble_fill(swigCPtr, this, fillValue);
  }

  public void swap(SimTKArrayDouble other) {
    opensimSimbodyJNI.SimTKArrayDouble_swap(swigCPtr, this, SimTKArrayDouble.getCPtr(other), other);
  }

  public SimTKArrayDouble adoptData(SWIGTYPE_p_double newData, long dataSize, long dataCapacity) {
    return new SimTKArrayDouble(opensimSimbodyJNI.SimTKArrayDouble_adoptData__SWIG_0(swigCPtr, this, SWIGTYPE_p_double.getCPtr(newData), dataSize, dataCapacity), false);
  }

  public SimTKArrayDouble adoptData(SWIGTYPE_p_double newData, long dataSize) {
    return new SimTKArrayDouble(opensimSimbodyJNI.SimTKArrayDouble_adoptData__SWIG_1(swigCPtr, this, SWIGTYPE_p_double.getCPtr(newData), dataSize), false);
  }

  public SimTKArrayDouble shareData(SWIGTYPE_p_double newData, long dataSize) {
    return new SimTKArrayDouble(opensimSimbodyJNI.SimTKArrayDouble_shareData__SWIG_0(swigCPtr, this, SWIGTYPE_p_double.getCPtr(newData), dataSize), false);
  }

  public SimTKArrayDouble shareData(SWIGTYPE_p_double first, SWIGTYPE_p_double last1) {
    return new SimTKArrayDouble(opensimSimbodyJNI.SimTKArrayDouble_shareData__SWIG_1(swigCPtr, this, SWIGTYPE_p_double.getCPtr(first), SWIGTYPE_p_double.getCPtr(last1)), false);
  }

  public long size() {
    return opensimSimbodyJNI.SimTKArrayDouble_size(swigCPtr, this);
  }

  public long max_size() {
    return opensimSimbodyJNI.SimTKArrayDouble_max_size(swigCPtr, this);
  }

  public boolean empty() {
    return opensimSimbodyJNI.SimTKArrayDouble_empty(swigCPtr, this);
  }

  public long capacity() {
    return opensimSimbodyJNI.SimTKArrayDouble_capacity(swigCPtr, this);
  }

  public void resize(long n) {
    opensimSimbodyJNI.SimTKArrayDouble_resize__SWIG_0(swigCPtr, this, n);
  }

  public void resize(long n, double initVal) {
    opensimSimbodyJNI.SimTKArrayDouble_resize__SWIG_1(swigCPtr, this, n, initVal);
  }

  public void reserve(long n) {
    opensimSimbodyJNI.SimTKArrayDouble_reserve(swigCPtr, this, n);
  }

  public void shrink_to_fit() {
    opensimSimbodyJNI.SimTKArrayDouble_shrink_to_fit(swigCPtr, this);
  }

  public long allocated() {
    return opensimSimbodyJNI.SimTKArrayDouble_allocated(swigCPtr, this);
  }

  public boolean isOwner() {
    return opensimSimbodyJNI.SimTKArrayDouble_isOwner(swigCPtr, this);
  }

  public SWIGTYPE_p_double cbegin() {
    long cPtr = opensimSimbodyJNI.SimTKArrayDouble_cbegin(swigCPtr, this);
    return (cPtr == 0) ? null : new SWIGTYPE_p_double(cPtr, false);
  }

  public SWIGTYPE_p_double begin() {
    long cPtr = opensimSimbodyJNI.SimTKArrayDouble_begin__SWIG_0(swigCPtr, this);
    return (cPtr == 0) ? null : new SWIGTYPE_p_double(cPtr, false);
  }

  public SWIGTYPE_p_double cend() {
    long cPtr = opensimSimbodyJNI.SimTKArrayDouble_cend(swigCPtr, this);
    return (cPtr == 0) ? null : new SWIGTYPE_p_double(cPtr, false);
  }

  public SWIGTYPE_p_double end() {
    long cPtr = opensimSimbodyJNI.SimTKArrayDouble_end__SWIG_0(swigCPtr, this);
    return (cPtr == 0) ? null : new SWIGTYPE_p_double(cPtr, false);
  }

  public SWIGTYPE_p_std__reverse_iteratorT_double_const_p_t crbegin() {
    return new SWIGTYPE_p_std__reverse_iteratorT_double_const_p_t(opensimSimbodyJNI.SimTKArrayDouble_crbegin(swigCPtr, this), true);
  }

  public SWIGTYPE_p_std__reverse_iteratorT_double_const_p_t rbegin() {
    return new SWIGTYPE_p_std__reverse_iteratorT_double_const_p_t(opensimSimbodyJNI.SimTKArrayDouble_rbegin__SWIG_0(swigCPtr, this), true);
  }

  public SWIGTYPE_p_std__reverse_iteratorT_double_const_p_t crend() {
    return new SWIGTYPE_p_std__reverse_iteratorT_double_const_p_t(opensimSimbodyJNI.SimTKArrayDouble_crend(swigCPtr, this), true);
  }

  public SWIGTYPE_p_std__reverse_iteratorT_double_const_p_t rend() {
    return new SWIGTYPE_p_std__reverse_iteratorT_double_const_p_t(opensimSimbodyJNI.SimTKArrayDouble_rend__SWIG_0(swigCPtr, this), true);
  }

  public SWIGTYPE_p_double cdata() {
    long cPtr = opensimSimbodyJNI.SimTKArrayDouble_cdata(swigCPtr, this);
    return (cPtr == 0) ? null : new SWIGTYPE_p_double(cPtr, false);
  }

  public SWIGTYPE_p_double data() {
    long cPtr = opensimSimbodyJNI.SimTKArrayDouble_data__SWIG_0(swigCPtr, this);
    return (cPtr == 0) ? null : new SWIGTYPE_p_double(cPtr, false);
  }

  public double at(long i) {
    return opensimSimbodyJNI.SimTKArrayDouble_at__SWIG_0(swigCPtr, this, i);
  }

  public double getElt(long i) {
    return opensimSimbodyJNI.SimTKArrayDouble_getElt(swigCPtr, this, i);
  }

  public SWIGTYPE_p_double updElt(long i) {
    return new SWIGTYPE_p_double(opensimSimbodyJNI.SimTKArrayDouble_updElt(swigCPtr, this, i), false);
  }

  public double front() {
    return opensimSimbodyJNI.SimTKArrayDouble_front__SWIG_0(swigCPtr, this);
  }

  public double back() {
    return opensimSimbodyJNI.SimTKArrayDouble_back__SWIG_0(swigCPtr, this);
  }

  public void push_back(double value) {
    opensimSimbodyJNI.SimTKArrayDouble_push_back__SWIG_0(swigCPtr, this, value);
  }

  public void push_back() {
    opensimSimbodyJNI.SimTKArrayDouble_push_back__SWIG_1(swigCPtr, this);
  }

  public SWIGTYPE_p_double raw_push_back() {
    long cPtr = opensimSimbodyJNI.SimTKArrayDouble_raw_push_back(swigCPtr, this);
    return (cPtr == 0) ? null : new SWIGTYPE_p_double(cPtr, false);
  }

  public void pop_back() {
    opensimSimbodyJNI.SimTKArrayDouble_pop_back(swigCPtr, this);
  }

  public SWIGTYPE_p_double erase(SWIGTYPE_p_double first, SWIGTYPE_p_double last1) {
    long cPtr = opensimSimbodyJNI.SimTKArrayDouble_erase__SWIG_0(swigCPtr, this, SWIGTYPE_p_double.getCPtr(first), SWIGTYPE_p_double.getCPtr(last1));
    return (cPtr == 0) ? null : new SWIGTYPE_p_double(cPtr, false);
  }

  public SWIGTYPE_p_double erase(SWIGTYPE_p_double p) {
    long cPtr = opensimSimbodyJNI.SimTKArrayDouble_erase__SWIG_1(swigCPtr, this, SWIGTYPE_p_double.getCPtr(p));
    return (cPtr == 0) ? null : new SWIGTYPE_p_double(cPtr, false);
  }

  public SWIGTYPE_p_double eraseFast(SWIGTYPE_p_double p) {
    long cPtr = opensimSimbodyJNI.SimTKArrayDouble_eraseFast(swigCPtr, this, SWIGTYPE_p_double.getCPtr(p));
    return (cPtr == 0) ? null : new SWIGTYPE_p_double(cPtr, false);
  }

  public void clear() {
    opensimSimbodyJNI.SimTKArrayDouble_clear(swigCPtr, this);
  }

  public SWIGTYPE_p_double insert(SWIGTYPE_p_double p, long n, double value) {
    long cPtr = opensimSimbodyJNI.SimTKArrayDouble_insert__SWIG_0(swigCPtr, this, SWIGTYPE_p_double.getCPtr(p), n, value);
    return (cPtr == 0) ? null : new SWIGTYPE_p_double(cPtr, false);
  }

  public SWIGTYPE_p_double insert(SWIGTYPE_p_double p, double value) {
    long cPtr = opensimSimbodyJNI.SimTKArrayDouble_insert__SWIG_1(swigCPtr, this, SWIGTYPE_p_double.getCPtr(p), value);
    return (cPtr == 0) ? null : new SWIGTYPE_p_double(cPtr, false);
  }

}
