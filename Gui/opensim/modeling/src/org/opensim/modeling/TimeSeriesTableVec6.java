/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 *  TimeSeriesTable_ is a DataTable_ where the independent column is time of <br>
 * type double. The time column is enforced to be strictly increasing.           
 */
public class TimeSeriesTableVec6 extends DataTableVec6 {
  private transient long swigCPtr;
  private transient boolean swigCMemOwnDerived;

  protected TimeSeriesTableVec6(long cPtr, boolean cMemoryOwn) {
    super(opensimCommonJNI.TimeSeriesTableVec6_SWIGSmartPtrUpcast(cPtr), true);
    swigCMemOwnDerived = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(TimeSeriesTableVec6 obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void swigSetCMemOwn(boolean own) {
    swigCMemOwnDerived = own;
    super.swigSetCMemOwn(own);
  }

  @SuppressWarnings("deprecation")
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

  /**
   *  Convenience constructor to efficiently populate a time series table<br>
   *     from available data. This is primarily useful for constructing with large<br>
   *     data read in from file without having to reallocate and copy memory.
   */
  public TimeSeriesTableVec6(StdVectorDouble indVec, MatrixVec6 depData, StdVectorString labels) {
    this(opensimCommonJNI.new_TimeSeriesTableVec6__SWIG_2(StdVectorDouble.getCPtr(indVec), indVec, MatrixVec6.getCPtr(depData), depData, StdVectorString.getCPtr(labels), labels), true);
  }

  /**
   *  Construct a table with only the independent (time) column and 0<br>
   *     dependent columns. This constructor is useful if you want to populate the<br>
   *     table by appending columns rather than by appending rows.                 
   */
  public TimeSeriesTableVec6(StdVectorDouble indVec) {
    this(opensimCommonJNI.new_TimeSeriesTableVec6__SWIG_3(StdVectorDouble.getCPtr(indVec), indVec), true);
  }

  /**
   *  Construct a TimeSeriesTable_ from a DataTable_.                       <br>
   * <br>
   *     @throws InvalidTable If the input table's independent column is not strictly<br>
   *                          increasing.                                          
   */
  public TimeSeriesTableVec6(DataTableVec6 datatable) {
    this(opensimCommonJNI.new_TimeSeriesTableVec6__SWIG_4(DataTableVec6.getCPtr(datatable), datatable), true);
  }

  /**
   *  Construct TimeSeriesTable_ from a file.<br>
   * <br>
   *     @param filename Name of the file.<br>
   * <br>
   *     @throws InvalidArgument If the input file contains more than one table.<br>
   *     @throws InvalidArgument If the input file contains a table that is not of<br>
   *                             this TimeSeriesTable_ type.                       
   */
  public TimeSeriesTableVec6(String filename) {
    this(opensimCommonJNI.new_TimeSeriesTableVec6__SWIG_5(filename), true);
  }

  /**
   *  Construct TimeSeriesTable_ from a file.<br>
   * <br>
   *     @param filename Name of the file.<br>
   *     @param tablename Name of the table in the file to construct this <br>
   *                      TimeSeriesTable_ from. For example, a c3d file contains <br>
   *                      tables named 'markers' and 'forces'.<br>
   * <br>
   *     @throws InvalidArgument If the input file contains more than one table and <br>
   *                             tablename was not specified.<br>
   *     @throws InvalidArgument If the input file contains a table that is not of<br>
   *                             this TimeSeriesTable_ type.                       
   */
  public TimeSeriesTableVec6(String filename, String tablename) {
    this(opensimCommonJNI.new_TimeSeriesTableVec6__SWIG_6(filename, tablename), true);
  }

  /**
   *  Get index of row whose time is nearest/closest to the given value.<br>
   * <br>
   *     @param time Value to search for.<br>
   *     @param restrictToTimeRange  When true -- Exception is thrown if the given<br>
   *                                 value is out-of-range of the time column. A value<br>
   *                                 within SimTK::SignifcantReal of a time column<br>
   *                                 bound is considered to be equal to the bound.<br>
   *                                 When false -- If the given value is less than or<br>
   *                                 equal to the first value in the time column, the<br>
   *                                 index returned is of the first row. If the given<br>
   *                                 value is greater than or equal to the last value<br>
   *                                 in the time column, the index of the last row is<br>
   *                                 returned. Defaults to 'true'.<br>
   * <br>
   *     @throws TimeOutOfRange If the given value is out-of-range of time column.<br>
   *     @throws EmptyTable If the table is empty.                                 
   */
  public long getNearestRowIndexForTime(double time, boolean restrictToTimeRange) {
    return opensimCommonJNI.TimeSeriesTableVec6_getNearestRowIndexForTime__SWIG_0(swigCPtr, this, time, restrictToTimeRange);
  }

  /**
   *  Get index of row whose time is nearest/closest to the given value.<br>
   * <br>
   *     @param time Value to search for.<br>
   *     <br>
   * <br>
   *     @throws TimeOutOfRange If the given value is out-of-range of time column.<br>
   *     @throws EmptyTable If the table is empty.                                 
   */
  public long getNearestRowIndexForTime(double time) {
    return opensimCommonJNI.TimeSeriesTableVec6_getNearestRowIndexForTime__SWIG_1(swigCPtr, this, time);
  }

  /**
   *  Get index of row whose time is first to be higher than the given value.<br>
   * <br>
   *      @param time Value to search for.
   */
  public long getRowIndexAfterTime(double time) {
    return opensimCommonJNI.TimeSeriesTableVec6_getRowIndexAfterTime(swigCPtr, this, time);
  }

  /**
   *  Get index of row whose time is the largest time less than the given value.<br>
   * <br>
   *      @param time Value to search for.
   */
  public long getRowIndexBeforeTime(double time) {
    return opensimCommonJNI.TimeSeriesTableVec6_getRowIndexBeforeTime(swigCPtr, this, time);
  }

  /**
   *  Get row whose time column is nearest/closest to the given value. <br>
   * <br>
   *     @param time Value to search for. <br>
   *     @param restrictToTimeRange When true -- Exception is thrown if the given <br>
   *                                value is out-of-range of the time column. <br>
   *                                When false -- If the given value is less than or <br>
   *                                equal to the first value in the time column, the<br>
   *                                row returned is the first row. If the given value<br>
   *                                is greater than or equal to the last value in the<br>
   *                                time column, the row returned is the last row. <br>
   *                                This operation only returns existing rows and <br>
   *                                does not perform any interpolation. Defaults to<br>
   *                                'true'.<br>
   * <br>
   *     @throws TimeOutOfRange If the given value is out-of-range of time column.<br>
   *     @throws EmptyTable If the table is empty.                                 
   */
  public RowVectorViewVec6 getNearestRow(double time, boolean restrictToTimeRange) {
    return new RowVectorViewVec6(opensimCommonJNI.TimeSeriesTableVec6_getNearestRow__SWIG_0(swigCPtr, this, time, restrictToTimeRange), true);
  }

  /**
   *  Get row whose time column is nearest/closest to the given value. <br>
   * <br>
   *     @param time Value to search for. <br>
   *     <br>
   * <br>
   *     @throws TimeOutOfRange If the given value is out-of-range of time column.<br>
   *     @throws EmptyTable If the table is empty.                                 
   */
  public RowVectorViewVec6 getNearestRow(double time) {
    return new RowVectorViewVec6(opensimCommonJNI.TimeSeriesTableVec6_getNearestRow__SWIG_1(swigCPtr, this, time), true);
  }

  /**
   *  Get writable reference to row whose time column is nearest/closest to <br>
   *     the given value. <br>
   * <br>
   *     @param time Value to search for. <br>
   *     @param restrictToTimeRange When true -- Exception is thrown if the given <br>
   *                                value is out-of-range of the time column. <br>
   *                                When false -- If the given value is less than or <br>
   *                                equal to the first value in the time column, the<br>
   *                                row returned is the first row. If the given value<br>
   *                                is greater than or equal to the last value in the<br>
   *                                time column, the row returned is the last row. <br>
   *                                This operation only returns existing rows and <br>
   *                                does not perform any interpolation. Defaults to<br>
   *                                'true'.<br>
   * <br>
   *     @throws TimeOutOfRange If the given value is out-of-range of time column.<br>
   *     @throws EmptyTable If the table is empty.                                 
   */
  public RowVectorViewVec6 updNearestRow(double time, boolean restrictToTimeRange) {
    return new RowVectorViewVec6(opensimCommonJNI.TimeSeriesTableVec6_updNearestRow__SWIG_0(swigCPtr, this, time, restrictToTimeRange), true);
  }

  /**
   *  Get writable reference to row whose time column is nearest/closest to <br>
   *     the given value. <br>
   * <br>
   *     @param time Value to search for. <br>
   *     <br>
   * <br>
   *     @throws TimeOutOfRange If the given value is out-of-range of time column.<br>
   *     @throws EmptyTable If the table is empty.                                 
   */
  public RowVectorViewVec6 updNearestRow(double time) {
    return new RowVectorViewVec6(opensimCommonJNI.TimeSeriesTableVec6_updNearestRow__SWIG_1(swigCPtr, this, time), true);
  }

  /**
   *  Compute the average row in the time range (inclusive) given. This<br>
   *     operation does not modify the table. It just computes and returns an average<br>
   *     row. <br>
   * <br>
   *     @throws InvalidTimeRange If beginTime is greater than or equal to endTime.<br>
   *     @throws TimeOutOfRange If beginTime or endTime is out of range of time <br>
   *                            column.                                            
   */
  public RowVectorVec6 averageRow(double beginTime, double endTime) {
    return new RowVectorVec6(opensimCommonJNI.TimeSeriesTableVec6_averageRow(swigCPtr, this, beginTime, endTime), true);
  }

  /**
   * Trim TimeSeriesTable to rows that have times that lies between <br>
   * newStartTime, newFinalTime. The trimming is done in place, no copy is made. <br>
   * Uses getRowIndexAfterTime to locate first row and<br>
   * getNearestRowIndexForTime method to locate last row.
   */
  public void trim(double newStartTime, double newFinalTime) {
    opensimCommonJNI.TimeSeriesTableVec6_trim(swigCPtr, this, newStartTime, newFinalTime);
  }

  /**
   * trim TimeSeriesTable, keeping rows at newStartTime to the end.
   */
  public void trimFrom(double newStartTime) {
    opensimCommonJNI.TimeSeriesTableVec6_trimFrom(swigCPtr, this, newStartTime);
  }

  /**
   * trim TimeSeriesTable, keeping rows up to newFinalTime
   */
  public void trimTo(double newFinalTime) {
    opensimCommonJNI.TimeSeriesTableVec6_trimTo(swigCPtr, this, newFinalTime);
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
