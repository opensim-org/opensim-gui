/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 *  AbstractDataTable is the base-class of all DataTable_(templated) allowing <br>
 * storage of DataTable_ templated on different types to be stored in a container <br>
 * like std::vector. DataTable_ represents a matrix and an additional column. The <br>
 * columns of the matrix are dependent columns. The additional column is the<br>
 * independent column. All dependent columns and the independent column can have<br>
 * metadata. AbstractDataTable offers:<br>
 * - Interface to access metadata of independent column and dependent columns.<br>
 * - A heterogeneous container to store metadata associated with the DataTable_ in<br>
 *   the form of key-value pairs where key is of type std::string and value can be<br>
 *   of any type.<br>
 * <br>
 * This class is abstract and cannot be used directly. Create instances of <br>
 * DataTable_ instead. See DataTable_ for details on usage.                     
 */
public class AbstractDataTable {
  private transient long swigCPtr;
  private transient boolean swigCMemOwn;

  protected AbstractDataTable(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(AbstractDataTable obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void swigSetCMemOwn(boolean own) {
    swigCMemOwn = own;
  }

  @SuppressWarnings("deprecation")
  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimCommonJNI.delete_AbstractDataTable(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  /**
   *  Get number of components per element of the DataTable. See documentation<br>
   *     for DataTable on possible return values.                                  
   */
  public long numComponentsPerElement() {
    return opensimCommonJNI.AbstractDataTable_numComponentsPerElement(swigCPtr, this);
  }

  /**
   *  Get number of rows.                                                   
   */
  public long getNumRows() {
    return opensimCommonJNI.AbstractDataTable_getNumRows(swigCPtr, this);
  }

  /**
   *  Get number of dependent columns.                                      
   */
  public long getNumColumns() {
    return opensimCommonJNI.AbstractDataTable_getNumColumns(swigCPtr, this);
  }

  /**
   *  Whether or not table metadata for the given key exists.               
   */
  public boolean hasTableMetaDataKey(String key) {
    return opensimCommonJNI.AbstractDataTable_hasTableMetaDataKey(swigCPtr, this, key);
  }

  /**
   *  Get table metadata for a given key as a string.<br>
   * <br>
   *     @throws KeyNotFound If the key provided is not found in table metadata.   
   */
  public String getTableMetaDataAsString(String key) {
    return opensimCommonJNI.AbstractDataTable_getTableMetaDataAsString(swigCPtr, this, key);
  }

  /**
   *  Remove key-value pair associated with the given key from table <br>
   *     metadata.                                                                 
   */
  public void removeTableMetaDataKey(String key) {
    opensimCommonJNI.AbstractDataTable_removeTableMetaDataKey(swigCPtr, this, key);
  }

  /**
   *  Get table metadata keys.                                              
   */
  public StdVectorString getTableMetaDataKeys() {
    return new StdVectorString(opensimCommonJNI.AbstractDataTable_getTableMetaDataKeys(swigCPtr, this), true);
  }

  /**
   *  Remove key-value pair associated with the given key from dependents<br>
   *     metadata.                                                                 
   */
  public void removeDependentsMetaDataForKey(String key) {
    opensimCommonJNI.AbstractDataTable_removeDependentsMetaDataForKey(swigCPtr, this, key);
  }

  /**
   *  End of MetaData accessors/mutators.<br>
   *  Following functions operate on column labels of dependent columns only<br>
   *  excluding the independent column.<br>
   *  Following are examples on using setColumnLabels(). If you have a <br>
   *  sequence of strings, you can pretty much call setColumnLabels() on it.<br>
   *  {@code 
  Simplest way to set column labels is to provide them directly to
  setColumnLabels.
   table.setColumnLabels({"col1", "col2", "col3"});
   }<br>
   *  {@code 
  if you have a sequence container like std::vector or std::list of 
  std::string holding column labels, pass the container directly to
  setColumnLabels.
   std::list<std::string> columnLabels{"col1", "col2", "col3"};
   table.setColumnLabels(columnLabels);
   }<br>
   *  {@code 
  If you have a sequence container like std::vector or std::list of 
  std::string holding column labels but you want to use only a subset
  of them to set column labels of the table, use iterators like below.
   std::vector<std::string> columnLabels{"col-not-used1", 
                                         "col1", "col2", "col3", 
                                         "col-not-used2"};
   table.setColumnLabels(columnLabels.begin() + 1, 
                         columnLabels.end() - 1);
   }<br>
   *  Does the table have non-zero number of column labels.                 
   */
  public boolean hasColumnLabels() {
    return opensimCommonJNI.AbstractDataTable_hasColumnLabels(swigCPtr, this);
  }

  /**
   *  Get column labels.                                                    <br>
   * <br>
   *     @throws NoColumnLabels If column labels have not be set for the table.    
   */
  public StdVectorString getColumnLabels() {
    return new StdVectorString(opensimCommonJNI.AbstractDataTable_getColumnLabels(swigCPtr, this), true);
  }

  /**
   *  Get column label of a given column.                                   <br>
   * <br>
   *     @throws ColumnIndexOutOfRange If columnIndex is out of range of number of<br>
   *                                   columns.                                    <br>
   *     @throws NoColumnLabels If column labels have not be set for the table.    
   */
  public String getColumnLabel(long columnIndex) {
    return opensimCommonJNI.AbstractDataTable_getColumnLabel(swigCPtr, this, columnIndex);
  }

  /**
   *  %Set the label for a column.                                          <br>
   * <br>
   *     @throws NoColumnLabels If table has no column labels.<br>
   *     @throws ColumnIndexOutOfRange If columnIndex is out of range for number of<br>
   *                                   columns in the table.                       
   */
  public void setColumnLabel(long columnIndex, String columnLabel) {
    opensimCommonJNI.AbstractDataTable_setColumnLabel(swigCPtr, this, columnIndex, columnLabel);
  }

  /**
   *  Get index of a column label.                                          <br>
   * <br>
   *     @throws NoColumnLabels If table has no column labels.<br>
   *     @throws KeyNotFound If columnLabel is not found to be label for any column.
   */
  public long getColumnIndex(String columnLabel) {
    return opensimCommonJNI.AbstractDataTable_getColumnIndex(swigCPtr, this, columnLabel);
  }

  /**
   *  Check if the table has a column with the given label.<br>
   * <br>
   *     @throws NoColumnLabels If table has no column labels.                     
   */
  public boolean hasColumn(String columnLabel) {
    return opensimCommonJNI.AbstractDataTable_hasColumn__SWIG_0(swigCPtr, this, columnLabel);
  }

  /**
   *  End of Column-labels related accessors/mutators. Check if the table has a column with the given index.                 
   */
  public boolean hasColumn(long columnIndex) {
    return opensimCommonJNI.AbstractDataTable_hasColumn__SWIG_1(swigCPtr, this, columnIndex);
  }

  public void setColumnLabels(StdVectorString columnLabels) {
    opensimCommonJNI.AbstractDataTable_setColumnLabels(swigCPtr, this, StdVectorString.getCPtr(columnLabels), columnLabels);
  }

  public void addTableMetaDataString(String key, String value) {
    opensimCommonJNI.AbstractDataTable_addTableMetaDataString(swigCPtr, this, key, value);
  }

  public StdVectorMatrix getTableMetaDataVectorMatrix(String key) {
    return new StdVectorMatrix(opensimCommonJNI.AbstractDataTable_getTableMetaDataVectorMatrix(swigCPtr, this, key), true);
  }

  public StdVectorUnsigned getTableMetaDataVectorUnsigned(String key) {
    return new StdVectorUnsigned(opensimCommonJNI.AbstractDataTable_getTableMetaDataVectorUnsigned(swigCPtr, this, key), true);
  }

  public String getTableMetaDataString(String key) {
    return opensimCommonJNI.AbstractDataTable_getTableMetaDataString(swigCPtr, this, key);
  }

  public StdVectorString getDependentsMetaDataString(String key) {
    return new StdVectorString(opensimCommonJNI.AbstractDataTable_getDependentsMetaDataString(swigCPtr, this, key), true);
  }

}
