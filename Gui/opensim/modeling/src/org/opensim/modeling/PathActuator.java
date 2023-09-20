/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.1.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 * This is the base class for actuators that apply controllable tension along <br>
 * a path. %PathActuator has no states; the control is simply the tension to be<br>
 * applied along a path (i.e. tensionable rope).<br>
 * <br>
 * @author Ajay Seth
 */
public class PathActuator extends ScalarActuator {
  private transient long swigCPtr;

  public PathActuator(long cPtr, boolean cMemoryOwn) {
    super(opensimSimulationJNI.PathActuator_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(PathActuator obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  public static long swigRelease(PathActuator obj) {
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
        opensimSimulationJNI.delete_PathActuator(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static PathActuator safeDownCast(OpenSimObject obj) {
    long cPtr = opensimSimulationJNI.PathActuator_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new PathActuator(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimSimulationJNI.PathActuator_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimSimulationJNI.PathActuator_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimSimulationJNI.PathActuator_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new PathActuator(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimSimulationJNI.PathActuator_getConcreteClassName(swigCPtr, this);
  }

  public void copyProperty_path(PathActuator source) {
    opensimSimulationJNI.PathActuator_copyProperty_path(swigCPtr, this, PathActuator.getCPtr(source), source);
  }

  public AbstractPath get_path(int i) {
    return new AbstractPath(opensimSimulationJNI.PathActuator_get_path__SWIG_0(swigCPtr, this, i), false);
  }

  public AbstractPath upd_path(int i) {
    return new AbstractPath(opensimSimulationJNI.PathActuator_upd_path__SWIG_0(swigCPtr, this, i), false);
  }

  public void set_path(int i, AbstractPath value) {
    opensimSimulationJNI.PathActuator_set_path__SWIG_0(swigCPtr, this, i, AbstractPath.getCPtr(value), value);
  }

  public int append_path(AbstractPath value) {
    return opensimSimulationJNI.PathActuator_append_path(swigCPtr, this, AbstractPath.getCPtr(value), value);
  }

  public void constructProperty_path(AbstractPath initValue) {
    opensimSimulationJNI.PathActuator_constructProperty_path(swigCPtr, this, AbstractPath.getCPtr(initValue), initValue);
  }

  public AbstractPath get_path() {
    return new AbstractPath(opensimSimulationJNI.PathActuator_get_path__SWIG_1(swigCPtr, this), false);
  }

  public AbstractPath upd_path() {
    return new AbstractPath(opensimSimulationJNI.PathActuator_upd_path__SWIG_1(swigCPtr, this), false);
  }

  public void set_path(AbstractPath value) {
    opensimSimulationJNI.PathActuator_set_path__SWIG_1(swigCPtr, this, AbstractPath.getCPtr(value), value);
  }

  public void copyProperty_optimal_force(PathActuator source) {
    opensimSimulationJNI.PathActuator_copyProperty_optimal_force(swigCPtr, this, PathActuator.getCPtr(source), source);
  }

  public double get_optimal_force(int i) {
    return opensimSimulationJNI.PathActuator_get_optimal_force__SWIG_0(swigCPtr, this, i);
  }

  public SWIGTYPE_p_double upd_optimal_force(int i) {
    return new SWIGTYPE_p_double(opensimSimulationJNI.PathActuator_upd_optimal_force__SWIG_0(swigCPtr, this, i), false);
  }

  public void set_optimal_force(int i, double value) {
    opensimSimulationJNI.PathActuator_set_optimal_force__SWIG_0(swigCPtr, this, i, value);
  }

  public int append_optimal_force(double value) {
    return opensimSimulationJNI.PathActuator_append_optimal_force(swigCPtr, this, value);
  }

  public void constructProperty_optimal_force(double initValue) {
    opensimSimulationJNI.PathActuator_constructProperty_optimal_force(swigCPtr, this, initValue);
  }

  public double get_optimal_force() {
    return opensimSimulationJNI.PathActuator_get_optimal_force__SWIG_1(swigCPtr, this);
  }

  public SWIGTYPE_p_double upd_optimal_force() {
    return new SWIGTYPE_p_double(opensimSimulationJNI.PathActuator_upd_optimal_force__SWIG_1(swigCPtr, this), false);
  }

  public void set_optimal_force(double value) {
    opensimSimulationJNI.PathActuator_set_optimal_force__SWIG_1(swigCPtr, this, value);
  }

  public void set_has_output_tension(boolean value) {
    opensimSimulationJNI.PathActuator__has_output_tension_set(swigCPtr, this, value);
  }

  public boolean get_has_output_tension() {
    return opensimSimulationJNI.PathActuator__has_output_tension_get(swigCPtr, this);
  }

  public PathActuator() {
    this(opensimSimulationJNI.new_PathActuator(), true);
  }

  public AbstractPath updPath() {
    return new AbstractPath(opensimSimulationJNI.PathActuator_updPath(swigCPtr, this), false);
  }

  public AbstractPath getPath() {
    return new AbstractPath(opensimSimulationJNI.PathActuator_getPath(swigCPtr, this), false);
  }

  public GeometryPath updGeometryPath() {
    return new GeometryPath(opensimSimulationJNI.PathActuator_updGeometryPath(swigCPtr, this), false);
  }

  public GeometryPath getGeometryPath() {
    return new GeometryPath(opensimSimulationJNI.PathActuator_getGeometryPath(swigCPtr, this), false);
  }

  public boolean hasVisualPath() {
    return opensimSimulationJNI.PathActuator_hasVisualPath(swigCPtr, this);
  }

  public void setOptimalForce(double aOptimalForce) {
    opensimSimulationJNI.PathActuator_setOptimalForce(swigCPtr, this, aOptimalForce);
  }

  public double getOptimalForce() {
    return opensimSimulationJNI.PathActuator_getOptimalForce(swigCPtr, this);
  }

  public double getLength(State s) {
    return opensimSimulationJNI.PathActuator_getLength(swigCPtr, this, State.getCPtr(s), s);
  }

  public double getLengtheningSpeed(State s) {
    return opensimSimulationJNI.PathActuator_getLengtheningSpeed(swigCPtr, this, State.getCPtr(s), s);
  }

  public double getPower(State s) {
    return opensimSimulationJNI.PathActuator_getPower(swigCPtr, this, State.getCPtr(s), s);
  }

  public double getStress(State s) {
    return opensimSimulationJNI.PathActuator_getStress(swigCPtr, this, State.getCPtr(s), s);
  }

  /**
   *  Note: This function does not maintain the State and so should be used<br>
   * only before a valid State is created.<br>
   * Note: Only valid if the `path` owned by this PathActuator supports<br>
   * PathPoint%s (e.g., GeometryPath). 
   */
  public void addNewPathPoint(String proposedName, PhysicalFrame aBody, Vec3 aPositionOnBody) {
    opensimSimulationJNI.PathActuator_addNewPathPoint(swigCPtr, this, proposedName, PhysicalFrame.getCPtr(aBody), aBody, Vec3.getCPtr(aPositionOnBody), aPositionOnBody);
  }

  public void computeForce(State state, VectorOfSpatialVec bodyForces, Vector mobilityForces) {
    opensimSimulationJNI.PathActuator_computeForce(swigCPtr, this, State.getCPtr(state), state, VectorOfSpatialVec.getCPtr(bodyForces), bodyForces, Vector.getCPtr(mobilityForces), mobilityForces);
  }

  public double computeActuation(State s) {
    return opensimSimulationJNI.PathActuator_computeActuation(swigCPtr, this, State.getCPtr(s), s);
  }

  public double computeMomentArm(State s, Coordinate aCoord) {
    return opensimSimulationJNI.PathActuator_computeMomentArm(swigCPtr, this, State.getCPtr(s), s, Coordinate.getCPtr(aCoord), aCoord);
  }

}
