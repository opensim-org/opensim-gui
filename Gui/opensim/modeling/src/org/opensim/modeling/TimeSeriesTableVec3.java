/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class TimeSeriesTableVec3 extends DataTableVec3 {
  private transient long swigCPtr;
  private transient boolean swigCMemOwnDerived;

  protected TimeSeriesTableVec3(long cPtr, boolean cMemoryOwn) {
    super(opensimCommonJNI.TimeSeriesTableVec3_SWIGSmartPtrUpcast(cPtr), true);
    swigCMemOwnDerived = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(TimeSeriesTableVec3 obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwnDerived) {
        swigCMemOwnDerived = false;
        opensimCommonJNI.delete_TimeSeriesTableVec3(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public TimeSeriesTableVec3() {
    this(opensimCommonJNI.new_TimeSeriesTableVec3__SWIG_0(), true);
  }

  public TimeSeriesTableVec3(TimeSeriesTableVec3 arg0) {
    this(opensimCommonJNI.new_TimeSeriesTableVec3__SWIG_1(TimeSeriesTableVec3.getCPtr(arg0), arg0), true);
  }

  public TimeSeriesTableVec3(StdVectorDouble indVec, MatrixVec3 depData, StdVectorString labels) {
    this(opensimCommonJNI.new_TimeSeriesTableVec3__SWIG_2(StdVectorDouble.getCPtr(indVec), indVec, MatrixVec3.getCPtr(depData), depData, StdVectorString.getCPtr(labels), labels), true);
  }

  public TimeSeriesTableVec3(DataTableVec3 datatable) {
    this(opensimCommonJNI.new_TimeSeriesTableVec3__SWIG_3(DataTableVec3.getCPtr(datatable), datatable), true);
  }

  public TimeSeriesTableVec3(String filename) {
    this(opensimCommonJNI.new_TimeSeriesTableVec3__SWIG_4(filename), true);
  }

  public TimeSeriesTableVec3(String filename, String tablename) {
    this(opensimCommonJNI.new_TimeSeriesTableVec3__SWIG_5(filename, tablename), true);
  }

  public RowVectorViewVec3 getNearestRow(double time, boolean restrictToTimeRange) {
    return new RowVectorViewVec3(opensimCommonJNI.TimeSeriesTableVec3_getNearestRow__SWIG_0(swigCPtr, this, time, restrictToTimeRange), true);
  }

  public RowVectorViewVec3 getNearestRow(double time) {
    return new RowVectorViewVec3(opensimCommonJNI.TimeSeriesTableVec3_getNearestRow__SWIG_1(swigCPtr, this, time), true);
  }

  public RowVectorViewVec3 updNearestRow(double time, boolean restrictToTimeRange) {
    return new RowVectorViewVec3(opensimCommonJNI.TimeSeriesTableVec3_updNearestRow__SWIG_0(swigCPtr, this, time, restrictToTimeRange), true);
  }

  public RowVectorViewVec3 updNearestRow(double time) {
    return new RowVectorViewVec3(opensimCommonJNI.TimeSeriesTableVec3_updNearestRow__SWIG_1(swigCPtr, this, time), true);
  }

  public RowVectorOfVec3 averageRow(double beginTime, double endTime) {
    return new RowVectorOfVec3(opensimCommonJNI.TimeSeriesTableVec3_averageRow(swigCPtr, this, beginTime, endTime), true);
  }

  public TimeSeriesTableVec3 clone() {
    long cPtr = opensimCommonJNI.TimeSeriesTableVec3_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new TimeSeriesTableVec3(cPtr, true);
  }

  public TimeSeriesTable flatten() {
    return new TimeSeriesTable(opensimCommonJNI.TimeSeriesTableVec3_flatten__SWIG_0(swigCPtr, this), true);
  }

  public TimeSeriesTable flatten(StdVectorString suffixes) {
    return new TimeSeriesTable(opensimCommonJNI.TimeSeriesTableVec3_flatten__SWIG_1(swigCPtr, this, StdVectorString.getCPtr(suffixes), suffixes), true);
  }

}
