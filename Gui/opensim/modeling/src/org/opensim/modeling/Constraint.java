/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.9
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class Constraint extends ModelComponent {
  private long swigCPtr;

  public Constraint(long cPtr, boolean cMemoryOwn) {
    super(opensimModelJNI.Constraint_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(Constraint obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimModelJNI.delete_Constraint(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static Constraint safeDownCast(OpenSimObject obj) {
    long cPtr = opensimModelJNI.Constraint_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new Constraint(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimModelJNI.Constraint_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimModelJNI.Constraint_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimModelJNI.Constraint_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new Constraint(cPtr, false);
  }

  public String getConcreteClassName() {
    return opensimModelJNI.Constraint_getConcreteClassName(swigCPtr, this);
  }

  public void setPropertyIndex_isDisabled(SWIGTYPE_p_PropertyIndex value) {
    opensimModelJNI.Constraint_PropertyIndex_isDisabled_set(swigCPtr, this, SWIGTYPE_p_PropertyIndex.getCPtr(value));
  }

  public SWIGTYPE_p_PropertyIndex getPropertyIndex_isDisabled() {
    return new SWIGTYPE_p_PropertyIndex(opensimModelJNI.Constraint_PropertyIndex_isDisabled_get(swigCPtr, this), true);
  }

  public void copyProperty_isDisabled(Constraint source) {
    opensimModelJNI.Constraint_copyProperty_isDisabled(swigCPtr, this, Constraint.getCPtr(source), source);
  }

  public SWIGTYPE_p_OpenSim__PropertyT_bool_t getProperty_isDisabled() {
    return new SWIGTYPE_p_OpenSim__PropertyT_bool_t(opensimModelJNI.Constraint_getProperty_isDisabled(swigCPtr, this), false);
  }

  public SWIGTYPE_p_OpenSim__PropertyT_bool_t updProperty_isDisabled() {
    return new SWIGTYPE_p_OpenSim__PropertyT_bool_t(opensimModelJNI.Constraint_updProperty_isDisabled(swigCPtr, this), false);
  }

  public boolean get_isDisabled(int i) {
    return opensimModelJNI.Constraint_get_isDisabled__SWIG_0(swigCPtr, this, i);
  }

  public SWIGTYPE_p_bool upd_isDisabled(int i) {
    return new SWIGTYPE_p_bool(opensimModelJNI.Constraint_upd_isDisabled__SWIG_0(swigCPtr, this, i), false);
  }

  public void set_isDisabled(int i, boolean value) {
    opensimModelJNI.Constraint_set_isDisabled__SWIG_0(swigCPtr, this, i, value);
  }

  public int append_isDisabled(boolean value) {
    return opensimModelJNI.Constraint_append_isDisabled(swigCPtr, this, value);
  }

  public void constructProperty_isDisabled(boolean initValue) {
    opensimModelJNI.Constraint_constructProperty_isDisabled(swigCPtr, this, initValue);
  }

  public boolean get_isDisabled() {
    return opensimModelJNI.Constraint_get_isDisabled__SWIG_1(swigCPtr, this);
  }

  public SWIGTYPE_p_bool upd_isDisabled() {
    return new SWIGTYPE_p_bool(opensimModelJNI.Constraint_upd_isDisabled__SWIG_1(swigCPtr, this), false);
  }

  public void set_isDisabled(boolean value) {
    opensimModelJNI.Constraint_set_isDisabled__SWIG_1(swigCPtr, this, value);
  }

  public void updateFromConstraint(State s, Constraint aConstraint) {
    opensimModelJNI.Constraint_updateFromConstraint(swigCPtr, this, State.getCPtr(s), s, Constraint.getCPtr(aConstraint), aConstraint);
  }

  public boolean isDisabled(State s) {
    return opensimModelJNI.Constraint_isDisabled(swigCPtr, this, State.getCPtr(s), s);
  }

  public boolean setDisabled(State s, boolean isDisabled) {
    return opensimModelJNI.Constraint_setDisabled(swigCPtr, this, State.getCPtr(s), s, isDisabled);
  }

  public void calcConstraintForces(State s, VectorOfSpatialVec bodyForcesInAncestor, Vector mobilityForces) {
    opensimModelJNI.Constraint_calcConstraintForces(swigCPtr, this, State.getCPtr(s), s, VectorOfSpatialVec.getCPtr(bodyForcesInAncestor), bodyForcesInAncestor, Vector.getCPtr(mobilityForces), mobilityForces);
  }

  public ArrayStr getRecordLabels() {
    return new ArrayStr(opensimModelJNI.Constraint_getRecordLabels(swigCPtr, this), true);
  }

  public ArrayDouble getRecordValues(State state) {
    return new ArrayDouble(opensimModelJNI.Constraint_getRecordValues(swigCPtr, this, State.getCPtr(state), state), true);
  }

  public void scale(ScaleSet aScaleSet) {
    opensimModelJNI.Constraint_scale(swigCPtr, this, ScaleSet.getCPtr(aScaleSet), aScaleSet);
  }

  public void setContactPointForInducedAccelerations(State s, Vec3 point) {
    opensimModelJNI.Constraint_setContactPointForInducedAccelerations(swigCPtr, this, State.getCPtr(s), s, Vec3.getCPtr(point), point);
  }

}
