/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.9
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class STOFileAdapterUnitVec3 {
  private transient long swigCPtr;
  private transient boolean swigCMemOwn;

  protected STOFileAdapterUnitVec3(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(STOFileAdapterUnitVec3 obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimCommonJNI.delete_STOFileAdapterUnitVec3(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public STOFileAdapterUnitVec3() {
    this(opensimCommonJNI.new_STOFileAdapterUnitVec3__SWIG_0(), true);
  }

  public STOFileAdapterUnitVec3(STOFileAdapterUnitVec3 arg0) {
    this(opensimCommonJNI.new_STOFileAdapterUnitVec3__SWIG_1(STOFileAdapterUnitVec3.getCPtr(arg0), arg0), true);
  }

  public STOFileAdapterUnitVec3 clone() {
    long cPtr = opensimCommonJNI.STOFileAdapterUnitVec3_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new STOFileAdapterUnitVec3(cPtr, true);
  }

  public static TimeSeriesTableUnitVec3 readFile(String fileName) {
    return new TimeSeriesTableUnitVec3(opensimCommonJNI.STOFileAdapterUnitVec3_readFile(fileName), true);
  }

  public static void write(TimeSeriesTableUnitVec3 table, String fileName) {
    opensimCommonJNI.STOFileAdapterUnitVec3_write(TimeSeriesTableUnitVec3.getCPtr(table), table, fileName);
  }

}
