/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class SimTKArrayString {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected SimTKArrayString(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(SimTKArrayString obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimSimbodyJNI.delete_SimTKArrayString(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public SimTKArrayString() {
    this(opensimSimbodyJNI.new_SimTKArrayString__SWIG_0(), true);
  }

  public SimTKArrayString(long n) {
    this(opensimSimbodyJNI.new_SimTKArrayString__SWIG_1(n), true);
  }

  public SimTKArrayString(long n, String initVal) {
    this(opensimSimbodyJNI.new_SimTKArrayString__SWIG_2(n, initVal), true);
  }

  public SimTKArrayString(SimTKArrayString src) {
    this(opensimSimbodyJNI.new_SimTKArrayString__SWIG_3(SimTKArrayString.getCPtr(src), src), true);
  }

  public SimTKArrayString(SWIGTYPE_p_std__string first, SWIGTYPE_p_std__string last1, DontCopy arg2) {
    this(opensimSimbodyJNI.new_SimTKArrayString__SWIG_4(SWIGTYPE_p_std__string.getCPtr(first), SWIGTYPE_p_std__string.getCPtr(last1), DontCopy.getCPtr(arg2), arg2), true);
  }

  public SimTKArrayString deallocate() {
    return new SimTKArrayString(opensimSimbodyJNI.SimTKArrayString_deallocate(swigCPtr, this), false);
  }

  public void assign(long n, String fillValue) {
    opensimSimbodyJNI.SimTKArrayString_assign(swigCPtr, this, n, fillValue);
  }

  public void fill(String fillValue) {
    opensimSimbodyJNI.SimTKArrayString_fill(swigCPtr, this, fillValue);
  }

  public void swap(SimTKArrayString other) {
    opensimSimbodyJNI.SimTKArrayString_swap(swigCPtr, this, SimTKArrayString.getCPtr(other), other);
  }

  public SimTKArrayString adoptData(SWIGTYPE_p_std__string newData, long dataSize, long dataCapacity) {
    return new SimTKArrayString(opensimSimbodyJNI.SimTKArrayString_adoptData__SWIG_0(swigCPtr, this, SWIGTYPE_p_std__string.getCPtr(newData), dataSize, dataCapacity), false);
  }

  public SimTKArrayString adoptData(SWIGTYPE_p_std__string newData, long dataSize) {
    return new SimTKArrayString(opensimSimbodyJNI.SimTKArrayString_adoptData__SWIG_1(swigCPtr, this, SWIGTYPE_p_std__string.getCPtr(newData), dataSize), false);
  }

  public SimTKArrayString shareData(SWIGTYPE_p_std__string newData, long dataSize) {
    return new SimTKArrayString(opensimSimbodyJNI.SimTKArrayString_shareData__SWIG_0(swigCPtr, this, SWIGTYPE_p_std__string.getCPtr(newData), dataSize), false);
  }

  public SimTKArrayString shareData(SWIGTYPE_p_std__string first, SWIGTYPE_p_std__string last1) {
    return new SimTKArrayString(opensimSimbodyJNI.SimTKArrayString_shareData__SWIG_1(swigCPtr, this, SWIGTYPE_p_std__string.getCPtr(first), SWIGTYPE_p_std__string.getCPtr(last1)), false);
  }

  public long size() {
    return opensimSimbodyJNI.SimTKArrayString_size(swigCPtr, this);
  }

  public long max_size() {
    return opensimSimbodyJNI.SimTKArrayString_max_size(swigCPtr, this);
  }

  public boolean empty() {
    return opensimSimbodyJNI.SimTKArrayString_empty(swigCPtr, this);
  }

  public long capacity() {
    return opensimSimbodyJNI.SimTKArrayString_capacity(swigCPtr, this);
  }

  public void resize(long n) {
    opensimSimbodyJNI.SimTKArrayString_resize__SWIG_0(swigCPtr, this, n);
  }

  public void resize(long n, String initVal) {
    opensimSimbodyJNI.SimTKArrayString_resize__SWIG_1(swigCPtr, this, n, initVal);
  }

  public void reserve(long n) {
    opensimSimbodyJNI.SimTKArrayString_reserve(swigCPtr, this, n);
  }

  public void shrink_to_fit() {
    opensimSimbodyJNI.SimTKArrayString_shrink_to_fit(swigCPtr, this);
  }

  public long allocated() {
    return opensimSimbodyJNI.SimTKArrayString_allocated(swigCPtr, this);
  }

  public boolean isOwner() {
    return opensimSimbodyJNI.SimTKArrayString_isOwner(swigCPtr, this);
  }

  public SWIGTYPE_p_std__string cbegin() {
    long cPtr = opensimSimbodyJNI.SimTKArrayString_cbegin(swigCPtr, this);
    return (cPtr == 0) ? null : new SWIGTYPE_p_std__string(cPtr, false);
  }

  public SWIGTYPE_p_std__string begin() {
    long cPtr = opensimSimbodyJNI.SimTKArrayString_begin__SWIG_0(swigCPtr, this);
    return (cPtr == 0) ? null : new SWIGTYPE_p_std__string(cPtr, false);
  }

  public SWIGTYPE_p_std__string cend() {
    long cPtr = opensimSimbodyJNI.SimTKArrayString_cend(swigCPtr, this);
    return (cPtr == 0) ? null : new SWIGTYPE_p_std__string(cPtr, false);
  }

  public SWIGTYPE_p_std__string end() {
    long cPtr = opensimSimbodyJNI.SimTKArrayString_end__SWIG_0(swigCPtr, this);
    return (cPtr == 0) ? null : new SWIGTYPE_p_std__string(cPtr, false);
  }

  public SWIGTYPE_p_std__reverse_iteratorT_std__string_const_p_t crbegin() {
    return new SWIGTYPE_p_std__reverse_iteratorT_std__string_const_p_t(opensimSimbodyJNI.SimTKArrayString_crbegin(swigCPtr, this), true);
  }

  public SWIGTYPE_p_std__reverse_iteratorT_std__string_const_p_t rbegin() {
    return new SWIGTYPE_p_std__reverse_iteratorT_std__string_const_p_t(opensimSimbodyJNI.SimTKArrayString_rbegin__SWIG_0(swigCPtr, this), true);
  }

  public SWIGTYPE_p_std__reverse_iteratorT_std__string_const_p_t crend() {
    return new SWIGTYPE_p_std__reverse_iteratorT_std__string_const_p_t(opensimSimbodyJNI.SimTKArrayString_crend(swigCPtr, this), true);
  }

  public SWIGTYPE_p_std__reverse_iteratorT_std__string_const_p_t rend() {
    return new SWIGTYPE_p_std__reverse_iteratorT_std__string_const_p_t(opensimSimbodyJNI.SimTKArrayString_rend__SWIG_0(swigCPtr, this), true);
  }

  public SWIGTYPE_p_std__string cdata() {
    long cPtr = opensimSimbodyJNI.SimTKArrayString_cdata(swigCPtr, this);
    return (cPtr == 0) ? null : new SWIGTYPE_p_std__string(cPtr, false);
  }

  public SWIGTYPE_p_std__string data() {
    long cPtr = opensimSimbodyJNI.SimTKArrayString_data__SWIG_0(swigCPtr, this);
    return (cPtr == 0) ? null : new SWIGTYPE_p_std__string(cPtr, false);
  }

  public String at(long i) {
    return opensimSimbodyJNI.SimTKArrayString_at__SWIG_0(swigCPtr, this, i);
  }

  public String getElt(long i) {
    return opensimSimbodyJNI.SimTKArrayString_getElt(swigCPtr, this, i);
  }

  public SWIGTYPE_p_std__string updElt(long i) {
    return new SWIGTYPE_p_std__string(opensimSimbodyJNI.SimTKArrayString_updElt(swigCPtr, this, i), false);
  }

  public String front() {
    return opensimSimbodyJNI.SimTKArrayString_front__SWIG_0(swigCPtr, this);
  }

  public String back() {
    return opensimSimbodyJNI.SimTKArrayString_back__SWIG_0(swigCPtr, this);
  }

  public void push_back(String value) {
    opensimSimbodyJNI.SimTKArrayString_push_back__SWIG_0(swigCPtr, this, value);
  }

  public void push_back() {
    opensimSimbodyJNI.SimTKArrayString_push_back__SWIG_1(swigCPtr, this);
  }

  public SWIGTYPE_p_std__string raw_push_back() {
    long cPtr = opensimSimbodyJNI.SimTKArrayString_raw_push_back(swigCPtr, this);
    return (cPtr == 0) ? null : new SWIGTYPE_p_std__string(cPtr, false);
  }

  public void pop_back() {
    opensimSimbodyJNI.SimTKArrayString_pop_back(swigCPtr, this);
  }

  public SWIGTYPE_p_std__string erase(SWIGTYPE_p_std__string first, SWIGTYPE_p_std__string last1) {
    long cPtr = opensimSimbodyJNI.SimTKArrayString_erase__SWIG_0(swigCPtr, this, SWIGTYPE_p_std__string.getCPtr(first), SWIGTYPE_p_std__string.getCPtr(last1));
    return (cPtr == 0) ? null : new SWIGTYPE_p_std__string(cPtr, false);
  }

  public SWIGTYPE_p_std__string erase(SWIGTYPE_p_std__string p) {
    long cPtr = opensimSimbodyJNI.SimTKArrayString_erase__SWIG_1(swigCPtr, this, SWIGTYPE_p_std__string.getCPtr(p));
    return (cPtr == 0) ? null : new SWIGTYPE_p_std__string(cPtr, false);
  }

  public SWIGTYPE_p_std__string eraseFast(SWIGTYPE_p_std__string p) {
    long cPtr = opensimSimbodyJNI.SimTKArrayString_eraseFast(swigCPtr, this, SWIGTYPE_p_std__string.getCPtr(p));
    return (cPtr == 0) ? null : new SWIGTYPE_p_std__string(cPtr, false);
  }

  public void clear() {
    opensimSimbodyJNI.SimTKArrayString_clear(swigCPtr, this);
  }

  public SWIGTYPE_p_std__string insert(SWIGTYPE_p_std__string p, long n, String value) {
    long cPtr = opensimSimbodyJNI.SimTKArrayString_insert__SWIG_0(swigCPtr, this, SWIGTYPE_p_std__string.getCPtr(p), n, value);
    return (cPtr == 0) ? null : new SWIGTYPE_p_std__string(cPtr, false);
  }

  public SWIGTYPE_p_std__string insert(SWIGTYPE_p_std__string p, String value) {
    long cPtr = opensimSimbodyJNI.SimTKArrayString_insert__SWIG_1(swigCPtr, this, SWIGTYPE_p_std__string.getCPtr(p), value);
    return (cPtr == 0) ? null : new SWIGTYPE_p_std__string(cPtr, false);
  }

}
