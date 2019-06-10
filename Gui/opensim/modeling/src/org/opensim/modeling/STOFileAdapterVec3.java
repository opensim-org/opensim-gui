/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.9
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class STOFileAdapterVec3 {
  private transient long swigCPtr;
  private transient boolean swigCMemOwn;

  protected STOFileAdapterVec3(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(STOFileAdapterVec3 obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimCommonJNI.delete_STOFileAdapterVec3(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public STOFileAdapterVec3() {
    this(opensimCommonJNI.new_STOFileAdapterVec3__SWIG_0(), true);
  }

  public STOFileAdapterVec3(STOFileAdapterVec3 arg0) {
    this(opensimCommonJNI.new_STOFileAdapterVec3__SWIG_1(STOFileAdapterVec3.getCPtr(arg0), arg0), true);
  }

  public STOFileAdapterVec3 clone() {
    long cPtr = opensimCommonJNI.STOFileAdapterVec3_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new STOFileAdapterVec3(cPtr, true);
  }

  public static TimeSeriesTableVec3 readFile(String fileName) {
    return new TimeSeriesTableVec3(opensimCommonJNI.STOFileAdapterVec3_readFile(fileName), true);
  }

  public static void write(TimeSeriesTableVec3 table, String fileName) {
    opensimCommonJNI.STOFileAdapterVec3_write(TimeSeriesTableVec3.getCPtr(table), table, fileName);
  }

}
