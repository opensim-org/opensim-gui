/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class PathPointSet extends SetPathPoint {
  private transient long swigCPtr;

  protected PathPointSet(long cPtr, boolean cMemoryOwn) {
    super(opensimSimulationJNI.PathPointSet_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected static long getCPtr(PathPointSet obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimSimulationJNI.delete_PathPointSet(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static PathPointSet safeDownCast(OpenSimObject obj) {
    long cPtr = opensimSimulationJNI.PathPointSet_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new PathPointSet(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimSimulationJNI.PathPointSet_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimSimulationJNI.PathPointSet_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimSimulationJNI.PathPointSet_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new PathPointSet(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimSimulationJNI.PathPointSet_getConcreteClassName(swigCPtr, this);
  }

  public PathPointSet() {
    this(opensimSimulationJNI.new_PathPointSet__SWIG_0(), true);
  }

  public PathPointSet(PathPointSet aPathPointSet) {
    this(opensimSimulationJNI.new_PathPointSet__SWIG_1(PathPointSet.getCPtr(aPathPointSet), aPathPointSet), true);
  }

}
