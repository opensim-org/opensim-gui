/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.1.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 * A class for storing an array of values of type T.  The capacity of the class<br>
 * grows as needed.  To use this template for a class of type T, class T should<br>
 * implement the following methods:  default constructor, copy constructor,<br>
 * assignment operator (=), equality operator (==), and less than<br>
 * operator (&lt;).
 */
public class ArrayVec3 {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  public ArrayVec3(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  public static long getCPtr(ArrayVec3 obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  public static long swigRelease(ArrayVec3 obj) {
    long ptr = 0;
    if (obj != null) {
      if (!obj.swigCMemOwn)
        throw new RuntimeException("Cannot release ownership as memory is not owned");
      ptr = obj.swigCPtr;
      obj.swigCMemOwn = false;
      obj.delete();
    }
    return ptr;
  }

  @SuppressWarnings("deprecation")
  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimCommonJNI.delete_ArrayVec3(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public ArrayVec3(ArrayVec3 arg0) {
    this(opensimCommonJNI.new_ArrayVec3__SWIG_0(ArrayVec3.getCPtr(arg0), arg0), true);
  }

  public ArrayVec3(Vec3 aDefaultValue, int aSize, int aCapacity) {
    this(opensimCommonJNI.new_ArrayVec3__SWIG_2(Vec3.getCPtr(aDefaultValue), aDefaultValue, aSize, aCapacity), true);
  }

  public ArrayVec3(Vec3 aDefaultValue, int aSize) {
    this(opensimCommonJNI.new_ArrayVec3__SWIG_3(Vec3.getCPtr(aDefaultValue), aDefaultValue, aSize), true);
  }

  public ArrayVec3(Vec3 aDefaultValue) {
    this(opensimCommonJNI.new_ArrayVec3__SWIG_4(Vec3.getCPtr(aDefaultValue), aDefaultValue), true);
  }

  public ArrayVec3() {
    this(opensimCommonJNI.new_ArrayVec3__SWIG_5(), true);
  }

  public boolean arrayEquals(ArrayVec3 aArray) {
    return opensimCommonJNI.ArrayVec3_arrayEquals(swigCPtr, this, ArrayVec3.getCPtr(aArray), aArray);
  }

  /**
   * Trim the capacity of this array so that it is one larger than the size<br>
   * of this array.  This is useful for reducing the amount of memory used<br>
   * by this array.  This capacity is kept at one larger than the size so<br>
   * that, for example, an array of characters can be treated as a NULL<br>
   * terminated string.
   */
  public void trim() {
    opensimCommonJNI.ArrayVec3_trim(swigCPtr, this);
  }

  /**
   * Set the size of the array.  This method can be used to either increase<br>
   * or decrease the size of the array.  If this size of the array is<br>
   * increased, the new elements are initialized to the default value<br>
   * that was specified at the time of construction.<br>
   * <br>
   * Note that the size of an array is different than its capacity.  The size<br>
   * indicates how many valid elements are stored in an array.  The capacity<br>
   * indicates how much the size of the array can be increased without<br>
   * allocated more memory.  At all times size &lt;= capacity.<br>
   * <br>
   * @param aSize Desired size of the array.  The size must be greater than<br>
   * or equal to zero.
   */
  public boolean setSize(int aSize) {
    return opensimCommonJNI.ArrayVec3_setSize(swigCPtr, this, aSize);
  }

  /**
   * Get the size of the array.<br>
   * <br>
   * @return Size of the array.
   */
  public int getSize() {
    return opensimCommonJNI.ArrayVec3_getSize(swigCPtr, this);
  }

  /**
   *  Alternate name for getSize(). *
   */
  public int size() {
    return opensimCommonJNI.ArrayVec3_size(swigCPtr, this);
  }

  /**
   * Append a value onto the array.<br>
   * <br>
   * @param aValue Value to be appended.<br>
   * @return New size of the array, or, equivalently, the index to the new<br>
   * first empty element of the array.
   */
  public int append(Vec3 aValue) {
    return opensimCommonJNI.ArrayVec3_append__SWIG_0(swigCPtr, this, Vec3.getCPtr(aValue), aValue);
  }

  /**
   * Append an array of values.<br>
   * <br>
   * @param aArray Array of values to append.<br>
   * @return New size of the array, or, equivalently, the index to the new<br>
   * first empty element of the array.
   */
  public int append(ArrayVec3 aArray) {
    return opensimCommonJNI.ArrayVec3_append__SWIG_1(swigCPtr, this, ArrayVec3.getCPtr(aArray), aArray);
  }

  /**
   * Insert a value into the array at a specified index.<br>
   * <br>
   * This method is relatively computationally costly since many of the array<br>
   * elements may need to be shifted.<br>
   * <br>
   * @param aValue Value to be inserted.<br>
   * @param aIndex Index at which to insert the new value.  All current elements<br>
   * from aIndex to the end of the array are shifted one place in the direction<br>
   * of the end of the array.  If the specified index is greater than the<br>
   * current size of the array, the size of the array is increased to aIndex+1<br>
   * and the intervening new elements are initialized to the default value that<br>
   * was specified at the time of construction.<br>
   * @return Size of the array after the insertion.
   */
  public int insert(int aIndex, Vec3 aValue) {
    return opensimCommonJNI.ArrayVec3_insert(swigCPtr, this, aIndex, Vec3.getCPtr(aValue), aValue);
  }

  /**
   * Remove a value from the array at a specified index.<br>
   * <br>
   * This method is relatively computationally costly since many of the array<br>
   * elements may need to be shifted.<br>
   * <br>
   * @param aIndex Index of the value to remove.  All elements from aIndex to<br>
   * the end of the array are shifted one place toward the beginning of<br>
   * the array.  If aIndex is less than 0 or greater than or equal to the<br>
   * current size of the array, no element is removed.<br>
   * @return Size of the array after the removal.
   */
  public int remove(int aIndex) {
    return opensimCommonJNI.ArrayVec3_remove(swigCPtr, this, aIndex);
  }

  /**
   * Set the value at a specified index.<br>
   * <br>
   * @param aIndex Index of the array element to be set.  It is permissible<br>
   * for aIndex to be past the current end of the array- the capacity will<br>
   * be increased if necessary.  Values between the current end of the array<br>
   * and aIndex are not initialized.<br>
   * @param aValue Value.
   */
  public void set(int aIndex, Vec3 aValue) {
    opensimCommonJNI.ArrayVec3_set(swigCPtr, this, aIndex, Vec3.getCPtr(aValue), aValue);
  }

  /**
   * Get a const reference to the value at a specified array index.<br>
   * <br>
   * If the index is negative or passed the end of the array, an exception<br>
   * is thrown.<br>
   * <br>
   * For faster execution, the array elements can be accessed through the<br>
   * overloaded operator[], which does no bounds checking.<br>
   * <br>
   * @param aIndex Index of the desired array element.<br>
   * @return const reference to the array element.<br>
   * @throws Exception if (aIndex&lt;0)||(aIndex&gt;=_size).<br>
   * @see operator[].
   */
  public Vec3 get(int aIndex) {
    return new Vec3(opensimCommonJNI.ArrayVec3_get(swigCPtr, this, aIndex), false);
  }

  public Vec3 getitem(int index) {
    return new Vec3(opensimCommonJNI.ArrayVec3_getitem(swigCPtr, this, index), true);
  }

  public void setitem(int index, Vec3 val) {
    opensimCommonJNI.ArrayVec3_setitem(swigCPtr, this, index, Vec3.getCPtr(val), val);
  }

  /**
   * Get the last value in the array.<br>
   * <br>
   * @return Last value in the array.<br>
   * @throws Exception if the array is empty.
   */
  public Vec3 getLast() {
    return new Vec3(opensimCommonJNI.ArrayVec3_getLast(swigCPtr, this), false);
  }

  /**
   * Linear search for an element matching a given value.<br>
   * <br>
   * @param aValue Value to which the array elements are compared.<br>
   * @return Index of the array element matching aValue. If there is more than<br>
   * one such elements with the same value the index of the first of these elements<br>
   * is returned.  If no match is found, -1 is returned.
   */
  public int findIndex(Vec3 aValue) {
    return opensimCommonJNI.ArrayVec3_findIndex(swigCPtr, this, Vec3.getCPtr(aValue), aValue);
  }

  /**
   * Linear search in reverse for an element matching a given value.<br>
   * <br>
   * @param aValue Value to which the array elements are compared.<br>
   * @return Index of the array element matching aValue. If there is more than<br>
   * one such elements with the same value the index of the last of these elements<br>
   * is returned.  If no match is found, -1 is returned.
   */
  public int rfindIndex(Vec3 aValue) {
    return opensimCommonJNI.ArrayVec3_rfindIndex(swigCPtr, this, Vec3.getCPtr(aValue), aValue);
  }

  /**
   * Search for the largest value in the array that is less than or<br>
   * equal to a specified value.  If there is more than one element with this<br>
   * largest value, the index of the first of these elements can optionally be<br>
   * found, but this can be up to twice as costly.<br>
   * <br>
   * This method assumes that the array element values monotonically<br>
   * increase as the array index increases.  Note that monotonically<br>
   * increase means never decrease, so it is permissible for elements to<br>
   * <br>
   * A binary search is performed (i.e., the array is repeatedly subdivided<br>
   * into two bins one of which must contain the specified until the<br>
   * appropriate element is identified), so the performance of this method<br>
   * is approximately ln(n), where n is the size of the array.<br>
   * <br>
   * @param aValue Value to which the array elements are compared.<br>
   * @param aFindFirst DEPRECATED: this is now ALWAYS `true` - regardless of<br>
   * what you are calling it with. This makes the behavior predictable on all<br>
   * platforms.<br>
   * <br>
   * OLD BEHAVIOR: If true, find the first element that satisfies the search.<br>
   * OLD BEHAVIOR: If false, the index of any element that satisfies the<br>
   * search can be returned. Which index will be returned depends on the<br>
   * length of the array and is therefore somewhat arbitrary.<br>
   * OLD BEHAVIOR: By default, this flag is false (now: it is always true)<br>
   * @param aLo Lowest array index to consider in the search.<br>
   * @param aHi Highest array index to consider in the search.<br>
   * @return Index of the array element that has the largest value that is less<br>
   * than or equal to aValue. If an error is encountered (e.g., the array<br>
   * is empty), or if the array contains no element that is less than or<br>
   * equal to aValue, -1 is returned.
   */
  public int searchBinary(Vec3 aValue, boolean aFindFirst, int aLo, int aHi) {
    return opensimCommonJNI.ArrayVec3_searchBinary__SWIG_0(swigCPtr, this, Vec3.getCPtr(aValue), aValue, aFindFirst, aLo, aHi);
  }

  /**
   * Search for the largest value in the array that is less than or<br>
   * equal to a specified value.  If there is more than one element with this<br>
   * largest value, the index of the first of these elements can optionally be<br>
   * found, but this can be up to twice as costly.<br>
   * <br>
   * This method assumes that the array element values monotonically<br>
   * increase as the array index increases.  Note that monotonically<br>
   * increase means never decrease, so it is permissible for elements to<br>
   * <br>
   * A binary search is performed (i.e., the array is repeatedly subdivided<br>
   * into two bins one of which must contain the specified until the<br>
   * appropriate element is identified), so the performance of this method<br>
   * is approximately ln(n), where n is the size of the array.<br>
   * <br>
   * @param aValue Value to which the array elements are compared.<br>
   * @param aFindFirst DEPRECATED: this is now ALWAYS `true` - regardless of<br>
   * what you are calling it with. This makes the behavior predictable on all<br>
   * platforms.<br>
   * <br>
   * OLD BEHAVIOR: If true, find the first element that satisfies the search.<br>
   * OLD BEHAVIOR: If false, the index of any element that satisfies the<br>
   * search can be returned. Which index will be returned depends on the<br>
   * length of the array and is therefore somewhat arbitrary.<br>
   * OLD BEHAVIOR: By default, this flag is false (now: it is always true)<br>
   * @param aLo Lowest array index to consider in the search.<br>
   * <br>
   * @return Index of the array element that has the largest value that is less<br>
   * than or equal to aValue. If an error is encountered (e.g., the array<br>
   * is empty), or if the array contains no element that is less than or<br>
   * equal to aValue, -1 is returned.
   */
  public int searchBinary(Vec3 aValue, boolean aFindFirst, int aLo) {
    return opensimCommonJNI.ArrayVec3_searchBinary__SWIG_1(swigCPtr, this, Vec3.getCPtr(aValue), aValue, aFindFirst, aLo);
  }

  /**
   * Search for the largest value in the array that is less than or<br>
   * equal to a specified value.  If there is more than one element with this<br>
   * largest value, the index of the first of these elements can optionally be<br>
   * found, but this can be up to twice as costly.<br>
   * <br>
   * This method assumes that the array element values monotonically<br>
   * increase as the array index increases.  Note that monotonically<br>
   * increase means never decrease, so it is permissible for elements to<br>
   * <br>
   * A binary search is performed (i.e., the array is repeatedly subdivided<br>
   * into two bins one of which must contain the specified until the<br>
   * appropriate element is identified), so the performance of this method<br>
   * is approximately ln(n), where n is the size of the array.<br>
   * <br>
   * @param aValue Value to which the array elements are compared.<br>
   * @param aFindFirst DEPRECATED: this is now ALWAYS `true` - regardless of<br>
   * what you are calling it with. This makes the behavior predictable on all<br>
   * platforms.<br>
   * <br>
   * OLD BEHAVIOR: If true, find the first element that satisfies the search.<br>
   * OLD BEHAVIOR: If false, the index of any element that satisfies the<br>
   * search can be returned. Which index will be returned depends on the<br>
   * length of the array and is therefore somewhat arbitrary.<br>
   * OLD BEHAVIOR: By default, this flag is false (now: it is always true)<br>
   * <br>
   * <br>
   * @return Index of the array element that has the largest value that is less<br>
   * than or equal to aValue. If an error is encountered (e.g., the array<br>
   * is empty), or if the array contains no element that is less than or<br>
   * equal to aValue, -1 is returned.
   */
  public int searchBinary(Vec3 aValue, boolean aFindFirst) {
    return opensimCommonJNI.ArrayVec3_searchBinary__SWIG_2(swigCPtr, this, Vec3.getCPtr(aValue), aValue, aFindFirst);
  }

  /**
   * Search for the largest value in the array that is less than or<br>
   * equal to a specified value.  If there is more than one element with this<br>
   * largest value, the index of the first of these elements can optionally be<br>
   * found, but this can be up to twice as costly.<br>
   * <br>
   * This method assumes that the array element values monotonically<br>
   * increase as the array index increases.  Note that monotonically<br>
   * increase means never decrease, so it is permissible for elements to<br>
   * <br>
   * A binary search is performed (i.e., the array is repeatedly subdivided<br>
   * into two bins one of which must contain the specified until the<br>
   * appropriate element is identified), so the performance of this method<br>
   * is approximately ln(n), where n is the size of the array.<br>
   * <br>
   * @param aValue Value to which the array elements are compared.<br>
   * <br>
   * <br>
   * OLD BEHAVIOR: If true, find the first element that satisfies the search.<br>
   * OLD BEHAVIOR: If false, the index of any element that satisfies the<br>
   * search can be returned. Which index will be returned depends on the<br>
   * length of the array and is therefore somewhat arbitrary.<br>
   * OLD BEHAVIOR: By default, this flag is false (now: it is always true)<br>
   * <br>
   * <br>
   * @return Index of the array element that has the largest value that is less<br>
   * than or equal to aValue. If an error is encountered (e.g., the array<br>
   * is empty), or if the array contains no element that is less than or<br>
   * equal to aValue, -1 is returned.
   */
  public int searchBinary(Vec3 aValue) {
    return opensimCommonJNI.ArrayVec3_searchBinary__SWIG_3(swigCPtr, this, Vec3.getCPtr(aValue), aValue);
  }

}
