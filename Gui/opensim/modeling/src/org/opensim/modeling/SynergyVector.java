/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.1.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 * <br>
 * A vector that represents the control weights for a single synergy in a <br>
 * SynergyController. The size of the vector should be equal to the number of <br>
 * actuators connected to the controller.
 */
public class SynergyVector extends OpenSimObject {
  private transient long swigCPtr;

  public SynergyVector(long cPtr, boolean cMemoryOwn) {
    super(opensimSimulationJNI.SynergyVector_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(SynergyVector obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  public static long swigRelease(SynergyVector obj) {
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
        opensimSimulationJNI.delete_SynergyVector(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static SynergyVector safeDownCast(OpenSimObject obj) {
    long cPtr = opensimSimulationJNI.SynergyVector_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new SynergyVector(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimSimulationJNI.SynergyVector_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimSimulationJNI.SynergyVector_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimSimulationJNI.SynergyVector_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new SynergyVector(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimSimulationJNI.SynergyVector_getConcreteClassName(swigCPtr, this);
  }

  public void copyProperty_synergy_weights(SynergyVector source) {
    opensimSimulationJNI.SynergyVector_copyProperty_synergy_weights(swigCPtr, this, SynergyVector.getCPtr(source), source);
  }

  public Vector get_synergy_weights(int i) {
    return new Vector(opensimSimulationJNI.SynergyVector_get_synergy_weights__SWIG_0(swigCPtr, this, i), false);
  }

  public Vector upd_synergy_weights(int i) {
    return new Vector(opensimSimulationJNI.SynergyVector_upd_synergy_weights__SWIG_0(swigCPtr, this, i), false);
  }

  public void set_synergy_weights(int i, Vector value) {
    opensimSimulationJNI.SynergyVector_set_synergy_weights__SWIG_0(swigCPtr, this, i, Vector.getCPtr(value), value);
  }

  public int append_synergy_weights(Vector value) {
    return opensimSimulationJNI.SynergyVector_append_synergy_weights(swigCPtr, this, Vector.getCPtr(value), value);
  }

  public void constructProperty_synergy_weights(Vector initValue) {
    opensimSimulationJNI.SynergyVector_constructProperty_synergy_weights(swigCPtr, this, Vector.getCPtr(initValue), initValue);
  }

  public Vector get_synergy_weights() {
    return new Vector(opensimSimulationJNI.SynergyVector_get_synergy_weights__SWIG_1(swigCPtr, this), false);
  }

  public Vector upd_synergy_weights() {
    return new Vector(opensimSimulationJNI.SynergyVector_upd_synergy_weights__SWIG_1(swigCPtr, this), false);
  }

  public void set_synergy_weights(Vector value) {
    opensimSimulationJNI.SynergyVector_set_synergy_weights__SWIG_1(swigCPtr, this, Vector.getCPtr(value), value);
  }

  public SynergyVector() {
    this(opensimSimulationJNI.new_SynergyVector__SWIG_0(), true);
  }

  public SynergyVector(String name, Vector weights) {
    this(opensimSimulationJNI.new_SynergyVector__SWIG_1(name, Vector.getCPtr(weights), weights), true);
  }

}
