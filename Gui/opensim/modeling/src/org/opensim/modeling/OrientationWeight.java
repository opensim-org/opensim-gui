/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.1.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class OrientationWeight extends OpenSimObject {
  private transient long swigCPtr;

  public OrientationWeight(long cPtr, boolean cMemoryOwn) {
    super(opensimSimulationJNI.OrientationWeight_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(OrientationWeight obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  public static long swigRelease(OrientationWeight obj) {
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
        opensimSimulationJNI.delete_OrientationWeight(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static OrientationWeight safeDownCast(OpenSimObject obj) {
    long cPtr = opensimSimulationJNI.OrientationWeight_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new OrientationWeight(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimSimulationJNI.OrientationWeight_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimSimulationJNI.OrientationWeight_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimSimulationJNI.OrientationWeight_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new OrientationWeight(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimSimulationJNI.OrientationWeight_getConcreteClassName(swigCPtr, this);
  }

  public OrientationWeight() {
    this(opensimSimulationJNI.new_OrientationWeight__SWIG_0(), true);
  }

  public OrientationWeight(String name, double weight) {
    this(opensimSimulationJNI.new_OrientationWeight__SWIG_1(name, weight), true);
  }

  public void setWeight(double weight) {
    opensimSimulationJNI.OrientationWeight_setWeight(swigCPtr, this, weight);
  }

  public double getWeight() {
    return opensimSimulationJNI.OrientationWeight_getWeight(swigCPtr, this);
  }

}
