/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.9
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class TimeSeriesTableVec6 extends DataTableVec6 {
  private transient long swigCPtr;
  private boolean swigCMemOwnDerived;

  protected TimeSeriesTableVec6(long cPtr, boolean cMemoryOwn) {
    super(opensimCommonJNI.TimeSeriesTableVec6_SWIGSmartPtrUpcast(cPtr), true);
    swigCMemOwnDerived = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(TimeSeriesTableVec6 obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwnDerived) {
        swigCMemOwnDerived = false;
        opensimCommonJNI.delete_TimeSeriesTableVec6(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public TimeSeriesTableVec6() {
    this(opensimCommonJNI.new_TimeSeriesTableVec6__SWIG_0(), true);
  }

  public TimeSeriesTableVec6(TimeSeriesTableVec6 arg0) {
    this(opensimCommonJNI.new_TimeSeriesTableVec6__SWIG_1(TimeSeriesTableVec6.getCPtr(arg0), arg0), true);
  }

  public TimeSeriesTableVec6(StdVectorDouble indVec, MatrixVec6 depData, StdVectorString labels) {
    this(opensimCommonJNI.new_TimeSeriesTableVec6__SWIG_2(StdVectorDouble.getCPtr(indVec), indVec, MatrixVec6.getCPtr(depData), depData, StdVectorString.getCPtr(labels), labels), true);
  }

  public TimeSeriesTableVec6(StdVectorDouble indVec) {
    this(opensimCommonJNI.new_TimeSeriesTableVec6__SWIG_3(StdVectorDouble.getCPtr(indVec), indVec), true);
  }

  public TimeSeriesTableVec6(DataTableVec6 datatable) {
    this(opensimCommonJNI.new_TimeSeriesTableVec6__SWIG_4(DataTableVec6.getCPtr(datatable), datatable), true);
  }

  public TimeSeriesTableVec6(String filename) {
    this(opensimCommonJNI.new_TimeSeriesTableVec6__SWIG_5(filename), true);
  }

  public TimeSeriesTableVec6(String filename, String tablename) {
    this(opensimCommonJNI.new_TimeSeriesTableVec6__SWIG_6(filename, tablename), true);
  }

  public long getNearestRowIndexForTime(double time, boolean restrictToTimeRange) {
    return opensimCommonJNI.TimeSeriesTableVec6_getNearestRowIndexForTime__SWIG_0(swigCPtr, this, time, restrictToTimeRange);
  }

  public long getNearestRowIndexForTime(double time) {
    return opensimCommonJNI.TimeSeriesTableVec6_getNearestRowIndexForTime__SWIG_1(swigCPtr, this, time);
  }

  public RowVectorViewVec6 getNearestRow(double time, boolean restrictToTimeRange) {
    return new RowVectorViewVec6(opensimCommonJNI.TimeSeriesTableVec6_getNearestRow__SWIG_0(swigCPtr, this, time, restrictToTimeRange), true);
  }

  public RowVectorViewVec6 getNearestRow(double time) {
    return new RowVectorViewVec6(opensimCommonJNI.TimeSeriesTableVec6_getNearestRow__SWIG_1(swigCPtr, this, time), true);
  }

  public RowVectorViewVec6 updNearestRow(double time, boolean restrictToTimeRange) {
    return new RowVectorViewVec6(opensimCommonJNI.TimeSeriesTableVec6_updNearestRow__SWIG_0(swigCPtr, this, time, restrictToTimeRange), true);
  }

  public RowVectorViewVec6 updNearestRow(double time) {
    return new RowVectorViewVec6(opensimCommonJNI.TimeSeriesTableVec6_updNearestRow__SWIG_1(swigCPtr, this, time), true);
  }

  public RowVectorVec6 averageRow(double beginTime, double endTime) {
    return new RowVectorVec6(opensimCommonJNI.TimeSeriesTableVec6_averageRow(swigCPtr, this, beginTime, endTime), true);
  }

  public TimeSeriesTableVec6 clone() {
    long cPtr = opensimCommonJNI.TimeSeriesTableVec6_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new TimeSeriesTableVec6(cPtr, true);
  }

  public TimeSeriesTable flatten() {
    return new TimeSeriesTable(opensimCommonJNI.TimeSeriesTableVec6_flatten__SWIG_0(swigCPtr, this), true);
  }

  public TimeSeriesTable flatten(StdVectorString suffixes) {
    return new TimeSeriesTable(opensimCommonJNI.TimeSeriesTableVec6_flatten__SWIG_1(swigCPtr, this, StdVectorString.getCPtr(suffixes), suffixes), true);
  }

}
