/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.4
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class SetPathPoint extends OpenSimObject {
  private long swigCPtr;

  public SetPathPoint(long cPtr, boolean cMemoryOwn) {
    super(opensimModelJNI.SetPathPoint_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(SetPathPoint obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimModelJNI.delete_SetPathPoint(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static SetPathPoint safeDownCast(OpenSimObject obj) {
    long cPtr = opensimModelJNI.SetPathPoint_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new SetPathPoint(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimModelJNI.SetPathPoint_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimModelJNI.SetPathPoint_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimModelJNI.SetPathPoint_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new SetPathPoint(cPtr, false);
  }

  public String getConcreteClassName() {
    return opensimModelJNI.SetPathPoint_getConcreteClassName(swigCPtr, this);
  }

  public SetPathPoint() {
    this(opensimModelJNI.new_SetPathPoint__SWIG_0(), true);
  }

  public SetPathPoint(String aFileName, boolean aUpdateFromXMLNode) {
    this(opensimModelJNI.new_SetPathPoint__SWIG_1(aFileName, aUpdateFromXMLNode), true);
  }

  public SetPathPoint(String aFileName) {
    this(opensimModelJNI.new_SetPathPoint__SWIG_2(aFileName), true);
  }

  public SetPathPoint(SetPathPoint aSet) {
    this(opensimModelJNI.new_SetPathPoint__SWIG_3(SetPathPoint.getCPtr(aSet), aSet), true);
  }

  public void setupGroups() {
    opensimModelJNI.SetPathPoint_setupGroups(swigCPtr, this);
  }

  public void setMemoryOwner(boolean aTrueFalse) {
    opensimModelJNI.SetPathPoint_setMemoryOwner(swigCPtr, this, aTrueFalse);
  }

  public boolean getMemoryOwner() {
    return opensimModelJNI.SetPathPoint_getMemoryOwner(swigCPtr, this);
  }

  public boolean computeNewCapacity(int aMinCapacity, SWIGTYPE_p_int rNewCapacity) {
    return opensimModelJNI.SetPathPoint_computeNewCapacity(swigCPtr, this, aMinCapacity, SWIGTYPE_p_int.getCPtr(rNewCapacity));
  }

  public boolean ensureCapacity(int aCapacity) {
    return opensimModelJNI.SetPathPoint_ensureCapacity(swigCPtr, this, aCapacity);
  }

  public void trim() {
    opensimModelJNI.SetPathPoint_trim(swigCPtr, this);
  }

  public int getCapacity() {
    return opensimModelJNI.SetPathPoint_getCapacity(swigCPtr, this);
  }

  public void setCapacityIncrement(int aIncrement) {
    opensimModelJNI.SetPathPoint_setCapacityIncrement(swigCPtr, this, aIncrement);
  }

  public int getCapacityIncrement() {
    return opensimModelJNI.SetPathPoint_getCapacityIncrement(swigCPtr, this);
  }

  public boolean setSize(int aSize) {
    return opensimModelJNI.SetPathPoint_setSize(swigCPtr, this, aSize);
  }

  public int getSize() {
    return opensimModelJNI.SetPathPoint_getSize(swigCPtr, this);
  }

  public int getIndex(PathPoint aObject, int aStartIndex) {
    return opensimModelJNI.SetPathPoint_getIndex__SWIG_0(swigCPtr, this, PathPoint.getCPtr(aObject), aObject, aStartIndex);
  }

  public int getIndex(PathPoint aObject) {
    return opensimModelJNI.SetPathPoint_getIndex__SWIG_1(swigCPtr, this, PathPoint.getCPtr(aObject), aObject);
  }

  public int getIndex(String aName, int aStartIndex) {
    return opensimModelJNI.SetPathPoint_getIndex__SWIG_2(swigCPtr, this, aName, aStartIndex);
  }

  public int getIndex(String aName) {
    return opensimModelJNI.SetPathPoint_getIndex__SWIG_3(swigCPtr, this, aName);
  }

  public void getGroupNamesContaining(String aObjectName, ArrayStr rGroupNames) {
    opensimModelJNI.SetPathPoint_getGroupNamesContaining(swigCPtr, this, aObjectName, ArrayStr.getCPtr(rGroupNames), rGroupNames);
  }

  public boolean append(PathPoint aObject) {
    return opensimModelJNI.SetPathPoint_append(swigCPtr, this, PathPoint.getCPtr(aObject), aObject);
  }

  public boolean cloneAndAppend(PathPoint aObject) {
    return opensimModelJNI.SetPathPoint_cloneAndAppend(swigCPtr, this, PathPoint.getCPtr(aObject), aObject);
  }

  public boolean insert(int aIndex, PathPoint aObject) {
    return opensimModelJNI.SetPathPoint_insert(swigCPtr, this, aIndex, PathPoint.getCPtr(aObject), aObject);
  }

  public boolean remove(int aIndex) {
    return opensimModelJNI.SetPathPoint_remove__SWIG_0(swigCPtr, this, aIndex);
  }

  public boolean remove(PathPoint aObject) {
    return opensimModelJNI.SetPathPoint_remove__SWIG_1(swigCPtr, this, PathPoint.getCPtr(aObject), aObject);
  }

  public void clearAndDestroy() {
    opensimModelJNI.SetPathPoint_clearAndDestroy(swigCPtr, this);
  }

  public boolean set(int aIndex, PathPoint aObject, boolean preserveGroups) {
    return opensimModelJNI.SetPathPoint_set__SWIG_0(swigCPtr, this, aIndex, PathPoint.getCPtr(aObject), aObject, preserveGroups);
  }

  public boolean set(int aIndex, PathPoint aObject) {
    return opensimModelJNI.SetPathPoint_set__SWIG_1(swigCPtr, this, aIndex, PathPoint.getCPtr(aObject), aObject);
  }

  public PathPoint get(int aIndex) {
    return new PathPoint(opensimModelJNI.SetPathPoint_get__SWIG_0(swigCPtr, this, aIndex), false);
  }

  public PathPoint get(String aName) {
    return new PathPoint(opensimModelJNI.SetPathPoint_get__SWIG_1(swigCPtr, this, aName), false);
  }

  public boolean contains(String aName) {
    return opensimModelJNI.SetPathPoint_contains(swigCPtr, this, aName);
  }

  public void getNames(ArrayStr rNames) {
    opensimModelJNI.SetPathPoint_getNames(swigCPtr, this, ArrayStr.getCPtr(rNames), rNames);
  }

  public PathPoint getLast() {
    long cPtr = opensimModelJNI.SetPathPoint_getLast(swigCPtr, this);
    return (cPtr == 0) ? null : new PathPoint(cPtr, false);
  }

  public int searchBinary(PathPoint aObject, boolean aFindFirst, int aLo, int aHi) {
    return opensimModelJNI.SetPathPoint_searchBinary__SWIG_0(swigCPtr, this, PathPoint.getCPtr(aObject), aObject, aFindFirst, aLo, aHi);
  }

  public int searchBinary(PathPoint aObject, boolean aFindFirst, int aLo) {
    return opensimModelJNI.SetPathPoint_searchBinary__SWIG_1(swigCPtr, this, PathPoint.getCPtr(aObject), aObject, aFindFirst, aLo);
  }

  public int searchBinary(PathPoint aObject, boolean aFindFirst) {
    return opensimModelJNI.SetPathPoint_searchBinary__SWIG_2(swigCPtr, this, PathPoint.getCPtr(aObject), aObject, aFindFirst);
  }

  public int searchBinary(PathPoint aObject) {
    return opensimModelJNI.SetPathPoint_searchBinary__SWIG_3(swigCPtr, this, PathPoint.getCPtr(aObject), aObject);
  }

  public int getNumGroups() {
    return opensimModelJNI.SetPathPoint_getNumGroups(swigCPtr, this);
  }

  public void addGroup(String aGroupName) {
    opensimModelJNI.SetPathPoint_addGroup(swigCPtr, this, aGroupName);
  }

  public void removeGroup(String aGroupName) {
    opensimModelJNI.SetPathPoint_removeGroup(swigCPtr, this, aGroupName);
  }

  public void renameGroup(String oldGroupName, String newGroupName) {
    opensimModelJNI.SetPathPoint_renameGroup(swigCPtr, this, oldGroupName, newGroupName);
  }

  public void addObjectToGroup(String aGroupName, String aObjectName) {
    opensimModelJNI.SetPathPoint_addObjectToGroup(swigCPtr, this, aGroupName, aObjectName);
  }

  public void getGroupNames(ArrayStr rGroupNames) {
    opensimModelJNI.SetPathPoint_getGroupNames(swigCPtr, this, ArrayStr.getCPtr(rGroupNames), rGroupNames);
  }

  public ObjectGroup getGroup(String aGroupName) {
    long cPtr = opensimModelJNI.SetPathPoint_getGroup__SWIG_0(swigCPtr, this, aGroupName);
    return (cPtr == 0) ? null : new ObjectGroup(cPtr, false);
  }

  public ObjectGroup getGroup(int aIndex) {
    long cPtr = opensimModelJNI.SetPathPoint_getGroup__SWIG_1(swigCPtr, this, aIndex);
    return (cPtr == 0) ? null : new ObjectGroup(cPtr, false);
  }

}
