/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.1.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 * This abstract class represents a force applied to bodies or generalized <br>
 * coordinates during a simulation. Each subclass represents a different type <br>
 * of force. The actual force computation is done by a SimTK::Force, which is <br>
 * created by extendAddToSystem().<br>
 * <br>
 * @author Peter Eastman<br>
 * @author Ajay Seth
 */
public class Force extends ModelComponent {
  private transient long swigCPtr;

  public Force(long cPtr, boolean cMemoryOwn) {
    super(opensimSimulationJNI.Force_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(Force obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  public static long swigRelease(Force obj) {
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
        opensimSimulationJNI.delete_Force(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static Force safeDownCast(OpenSimObject obj) {
    long cPtr = opensimSimulationJNI.Force_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new Force(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimSimulationJNI.Force_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimSimulationJNI.Force_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimSimulationJNI.Force_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new Force(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimSimulationJNI.Force_getConcreteClassName(swigCPtr, this);
  }

  public void copyProperty_appliesForce(Force source) {
    opensimSimulationJNI.Force_copyProperty_appliesForce(swigCPtr, this, Force.getCPtr(source), source);
  }

  public boolean get_appliesForce(int i) {
    return opensimSimulationJNI.Force_get_appliesForce__SWIG_0(swigCPtr, this, i);
  }

  public SWIGTYPE_p_bool upd_appliesForce(int i) {
    return new SWIGTYPE_p_bool(opensimSimulationJNI.Force_upd_appliesForce__SWIG_0(swigCPtr, this, i), false);
  }

  public void set_appliesForce(int i, boolean value) {
    opensimSimulationJNI.Force_set_appliesForce__SWIG_0(swigCPtr, this, i, value);
  }

  public int append_appliesForce(boolean value) {
    return opensimSimulationJNI.Force_append_appliesForce(swigCPtr, this, value);
  }

  public void constructProperty_appliesForce(boolean initValue) {
    opensimSimulationJNI.Force_constructProperty_appliesForce(swigCPtr, this, initValue);
  }

  public boolean get_appliesForce() {
    return opensimSimulationJNI.Force_get_appliesForce__SWIG_1(swigCPtr, this);
  }

  public SWIGTYPE_p_bool upd_appliesForce() {
    return new SWIGTYPE_p_bool(opensimSimulationJNI.Force_upd_appliesForce__SWIG_1(swigCPtr, this), false);
  }

  public void set_appliesForce(boolean value) {
    opensimSimulationJNI.Force_set_appliesForce__SWIG_1(swigCPtr, this, value);
  }

  public void set_has_output_potential_energy(boolean value) {
    opensimSimulationJNI.Force__has_output_potential_energy_set(swigCPtr, this, value);
  }

  public boolean get_has_output_potential_energy() {
    return opensimSimulationJNI.Force__has_output_potential_energy_get(swigCPtr, this);
  }

  /**
   * Tell SimBody to parallelize this force. Should be <br>
   * set to true for any forces that will take time to <br>
   * complete their calcForce method. Note that all forces<br>
   * that set this flag to false will be put in series on a<br>
   * thread that is running in parallel with other forces<br>
   * that marked this flag as true.
   */
  public boolean shouldBeParallelized() {
    return opensimSimulationJNI.Force_shouldBeParallelized(swigCPtr, this);
  }

  /**
   *  Return if the Force is applied (or enabled) or not.                   
   */
  public boolean appliesForce(State s) {
    return opensimSimulationJNI.Force_appliesForce(swigCPtr, this, State.getCPtr(s), s);
  }

  /**
   *  %Set whether or not the Force is applied.                             
   */
  public void setAppliesForce(State s, boolean applyForce) {
    opensimSimulationJNI.Force_setAppliesForce(swigCPtr, this, State.getCPtr(s), s, applyForce);
  }

  /**
   * Methods to query a Force for the value actually applied during <br>
   * simulation. The names of the quantities (column labels) is returned by <br>
   * this first function getRecordLabels().
   */
  public ArrayStr getRecordLabels() {
    return new ArrayStr(opensimSimulationJNI.Force_getRecordLabels(swigCPtr, this), true);
  }

  /**
   * Given SimTK::State object extract all the values necessary to report <br>
   * forces, application location frame, etc. used in conjunction with <br>
   * getRecordLabels and should return same size Array.
   */
  public ArrayDouble getRecordValues(State state) {
    return new ArrayDouble(opensimSimulationJNI.Force_getRecordValues(swigCPtr, this, State.getCPtr(state), state), true);
  }

  /**
   *  Return a flag indicating whether the Force is applied along a path that<br>
   * can be visualized. If you override this method to return true for a <br>
   * specific subclass, it must also implement the getPath() method. 
   */
  public boolean hasVisualPath() {
    return opensimSimulationJNI.Force_hasVisualPath(swigCPtr, this);
  }

}
