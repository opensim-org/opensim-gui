/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.9
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class DataTableQuaternion extends AbstractDataTable {
  private transient long swigCPtr;
  private boolean swigCMemOwnDerived;

  protected DataTableQuaternion(long cPtr, boolean cMemoryOwn) {
    super(opensimCommonJNI.DataTableQuaternion_SWIGSmartPtrUpcast(cPtr), true);
    swigCMemOwnDerived = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(DataTableQuaternion obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwnDerived) {
        swigCMemOwnDerived = false;
        opensimCommonJNI.delete_DataTableQuaternion(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public DataTableQuaternion() {
    this(opensimCommonJNI.new_DataTableQuaternion__SWIG_0(), true);
  }

  public DataTableQuaternion(DataTableQuaternion arg0) {
    this(opensimCommonJNI.new_DataTableQuaternion__SWIG_1(DataTableQuaternion.getCPtr(arg0), arg0), true);
  }

  public DataTableQuaternion(String filename, String tablename) {
    this(opensimCommonJNI.new_DataTableQuaternion__SWIG_2(filename, tablename), true);
  }

  public DataTable flatten() {
    return new DataTable(opensimCommonJNI.DataTableQuaternion_flatten__SWIG_0(swigCPtr, this), true);
  }

  public DataTable flatten(StdVectorString suffixes) {
    return new DataTable(opensimCommonJNI.DataTableQuaternion_flatten__SWIG_1(swigCPtr, this, StdVectorString.getCPtr(suffixes), suffixes), true);
  }

  public long numComponentsPerElement() {
    return opensimCommonJNI.DataTableQuaternion_numComponentsPerElement(swigCPtr, this);
  }

  public void appendRow(double indRow, SWIGTYPE_p_std__initializer_listT_SimTK__Quaternion_T_double_t_t container) {
    opensimCommonJNI.DataTableQuaternion_appendRow__SWIG_1(swigCPtr, this, indRow, SWIGTYPE_p_std__initializer_listT_SimTK__Quaternion_T_double_t_t.getCPtr(container));
  }

  public void appendRow(double indRow, RowVectorQuaternion depRow) {
    opensimCommonJNI.DataTableQuaternion_appendRow__SWIG_3(swigCPtr, this, indRow, RowVectorQuaternion.getCPtr(depRow), depRow);
  }

  public void appendRow(double indRow, RowVectorViewQuaternion depRow) {
    opensimCommonJNI.DataTableQuaternion_appendRow__SWIG_4(swigCPtr, this, indRow, RowVectorViewQuaternion.getCPtr(depRow), depRow);
  }

  public RowVectorViewQuaternion getRowAtIndex(long index) {
    return new RowVectorViewQuaternion(opensimCommonJNI.DataTableQuaternion_getRowAtIndex(swigCPtr, this, index), true);
  }

  public RowVectorViewQuaternion getRow(double ind) {
    return new RowVectorViewQuaternion(opensimCommonJNI.DataTableQuaternion_getRow(swigCPtr, this, ind), true);
  }

  public RowVectorViewQuaternion updRowAtIndex(long index) {
    return new RowVectorViewQuaternion(opensimCommonJNI.DataTableQuaternion_updRowAtIndex(swigCPtr, this, index), true);
  }

  public RowVectorViewQuaternion updRow(double ind) {
    return new RowVectorViewQuaternion(opensimCommonJNI.DataTableQuaternion_updRow(swigCPtr, this, ind), true);
  }

  public void setRowAtIndex(long index, RowVectorViewQuaternion depRow) {
    opensimCommonJNI.DataTableQuaternion_setRowAtIndex__SWIG_0(swigCPtr, this, index, RowVectorViewQuaternion.getCPtr(depRow), depRow);
  }

  public void setRowAtIndex(long index, RowVectorQuaternion depRow) {
    opensimCommonJNI.DataTableQuaternion_setRowAtIndex__SWIG_1(swigCPtr, this, index, RowVectorQuaternion.getCPtr(depRow), depRow);
  }

  public void setRow(double ind, RowVectorViewQuaternion depRow) {
    opensimCommonJNI.DataTableQuaternion_setRow__SWIG_0(swigCPtr, this, ind, RowVectorViewQuaternion.getCPtr(depRow), depRow);
  }

  public void setRow(double ind, RowVectorQuaternion depRow) {
    opensimCommonJNI.DataTableQuaternion_setRow__SWIG_1(swigCPtr, this, ind, RowVectorQuaternion.getCPtr(depRow), depRow);
  }

  public void removeRowAtIndex(long index) {
    opensimCommonJNI.DataTableQuaternion_removeRowAtIndex(swigCPtr, this, index);
  }

  public void removeRow(double ind) {
    opensimCommonJNI.DataTableQuaternion_removeRow(swigCPtr, this, ind);
  }

  public StdVectorDouble getIndependentColumn() {
    return new StdVectorDouble(opensimCommonJNI.DataTableQuaternion_getIndependentColumn(swigCPtr, this), false);
  }

  public void appendColumn(String columnLabel, SWIGTYPE_p_std__initializer_listT_SimTK__Quaternion_T_double_t_t container) {
    opensimCommonJNI.DataTableQuaternion_appendColumn__SWIG_1(swigCPtr, this, columnLabel, SWIGTYPE_p_std__initializer_listT_SimTK__Quaternion_T_double_t_t.getCPtr(container));
  }

  public void appendColumn(String columnLabel, VectorQuaternion depCol) {
    opensimCommonJNI.DataTableQuaternion_appendColumn__SWIG_3(swigCPtr, this, columnLabel, VectorQuaternion.getCPtr(depCol), depCol);
  }

  public void appendColumn(String columnLabel, SWIGTYPE_p_SimTK__VectorView_T_SimTK__Quaternion_T_double_t_t depCol) {
    opensimCommonJNI.DataTableQuaternion_appendColumn__SWIG_4(swigCPtr, this, columnLabel, SWIGTYPE_p_SimTK__VectorView_T_SimTK__Quaternion_T_double_t_t.getCPtr(depCol));
  }

  public void removeColumnAtIndex(long index) {
    opensimCommonJNI.DataTableQuaternion_removeColumnAtIndex(swigCPtr, this, index);
  }

  public void removeColumn(String columnLabel) {
    opensimCommonJNI.DataTableQuaternion_removeColumn(swigCPtr, this, columnLabel);
  }

  public SWIGTYPE_p_SimTK__VectorView_T_SimTK__Quaternion_T_double_t_t getDependentColumnAtIndex(long index) {
    return new SWIGTYPE_p_SimTK__VectorView_T_SimTK__Quaternion_T_double_t_t(opensimCommonJNI.DataTableQuaternion_getDependentColumnAtIndex(swigCPtr, this, index), true);
  }

  public SWIGTYPE_p_SimTK__VectorView_T_SimTK__Quaternion_T_double_t_t getDependentColumn(String columnLabel) {
    return new SWIGTYPE_p_SimTK__VectorView_T_SimTK__Quaternion_T_double_t_t(opensimCommonJNI.DataTableQuaternion_getDependentColumn(swigCPtr, this, columnLabel), true);
  }

  public SWIGTYPE_p_SimTK__VectorView_T_SimTK__Quaternion_T_double_t_t updDependentColumnAtIndex(long index) {
    return new SWIGTYPE_p_SimTK__VectorView_T_SimTK__Quaternion_T_double_t_t(opensimCommonJNI.DataTableQuaternion_updDependentColumnAtIndex(swigCPtr, this, index), true);
  }

  public SWIGTYPE_p_SimTK__VectorView_T_SimTK__Quaternion_T_double_t_t updDependentColumn(String columnLabel) {
    return new SWIGTYPE_p_SimTK__VectorView_T_SimTK__Quaternion_T_double_t_t(opensimCommonJNI.DataTableQuaternion_updDependentColumn(swigCPtr, this, columnLabel), true);
  }

  public void setIndependentValueAtIndex(long rowIndex, double value) {
    opensimCommonJNI.DataTableQuaternion_setIndependentValueAtIndex(swigCPtr, this, rowIndex, value);
  }

  public SWIGTYPE_p_SimTK__MatrixView_T_SimTK__Quaternion_T_double_t_t getMatrix() {
    return new SWIGTYPE_p_SimTK__MatrixView_T_SimTK__Quaternion_T_double_t_t(opensimCommonJNI.DataTableQuaternion_getMatrix(swigCPtr, this), false);
  }

  public SWIGTYPE_p_SimTK__MatrixView_T_SimTK__Quaternion_T_double_t_t getMatrixBlock(long rowStart, long columnStart, long numRows, long numColumns) {
    return new SWIGTYPE_p_SimTK__MatrixView_T_SimTK__Quaternion_T_double_t_t(opensimCommonJNI.DataTableQuaternion_getMatrixBlock(swigCPtr, this, rowStart, columnStart, numRows, numColumns), true);
  }

  public SWIGTYPE_p_SimTK__MatrixView_T_SimTK__Quaternion_T_double_t_t updMatrix() {
    return new SWIGTYPE_p_SimTK__MatrixView_T_SimTK__Quaternion_T_double_t_t(opensimCommonJNI.DataTableQuaternion_updMatrix(swigCPtr, this), false);
  }

  public SWIGTYPE_p_SimTK__MatrixView_T_SimTK__Quaternion_T_double_t_t updMatrixBlock(long rowStart, long columnStart, long numRows, long numColumns) {
    return new SWIGTYPE_p_SimTK__MatrixView_T_SimTK__Quaternion_T_double_t_t(opensimCommonJNI.DataTableQuaternion_updMatrixBlock(swigCPtr, this, rowStart, columnStart, numRows, numColumns), true);
  }

  public String toString(StdVectorInt rows, StdVectorString columnLabels, boolean withMetaData, long splitSize, long maxWidth, long precision) {
    return opensimCommonJNI.DataTableQuaternion_toString__SWIG_0(swigCPtr, this, StdVectorInt.getCPtr(rows), rows, StdVectorString.getCPtr(columnLabels), columnLabels, withMetaData, splitSize, maxWidth, precision);
  }

  public String toString(StdVectorInt rows, StdVectorString columnLabels, boolean withMetaData, long splitSize, long maxWidth) {
    return opensimCommonJNI.DataTableQuaternion_toString__SWIG_1(swigCPtr, this, StdVectorInt.getCPtr(rows), rows, StdVectorString.getCPtr(columnLabels), columnLabels, withMetaData, splitSize, maxWidth);
  }

  public String toString(StdVectorInt rows, StdVectorString columnLabels, boolean withMetaData, long splitSize) {
    return opensimCommonJNI.DataTableQuaternion_toString__SWIG_2(swigCPtr, this, StdVectorInt.getCPtr(rows), rows, StdVectorString.getCPtr(columnLabels), columnLabels, withMetaData, splitSize);
  }

  public String toString(StdVectorInt rows, StdVectorString columnLabels, boolean withMetaData) {
    return opensimCommonJNI.DataTableQuaternion_toString__SWIG_3(swigCPtr, this, StdVectorInt.getCPtr(rows), rows, StdVectorString.getCPtr(columnLabels), columnLabels, withMetaData);
  }

  public String toString(StdVectorInt rows, StdVectorString columnLabels) {
    return opensimCommonJNI.DataTableQuaternion_toString__SWIG_4(swigCPtr, this, StdVectorInt.getCPtr(rows), rows, StdVectorString.getCPtr(columnLabels), columnLabels);
  }

  public String toString(StdVectorInt rows) {
    return opensimCommonJNI.DataTableQuaternion_toString__SWIG_5(swigCPtr, this, StdVectorInt.getCPtr(rows), rows);
  }

  public String toString() {
    return opensimCommonJNI.DataTableQuaternion_toString__SWIG_6(swigCPtr, this);
  }

  public DataTableQuaternion clone() {
    long cPtr = opensimCommonJNI.DataTableQuaternion_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new DataTableQuaternion(cPtr, true);
  }

}
