/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.9
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class MocoKinematicConstraint {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  public MocoKinematicConstraint(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  public static long getCPtr(MocoKinematicConstraint obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimMocoJNI.delete_MocoKinematicConstraint(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public SWIGTYPE_p_MocoConstraintInfo getConstraintInfo() {
    return new SWIGTYPE_p_MocoConstraintInfo(opensimMocoJNI.MocoKinematicConstraint_getConstraintInfo(swigCPtr, this), false);
  }

  public void setConstraintInfo(SWIGTYPE_p_MocoConstraintInfo cInfo) {
    opensimMocoJNI.MocoKinematicConstraint_setConstraintInfo(swigCPtr, this, SWIGTYPE_p_MocoConstraintInfo.getCPtr(cInfo));
  }

  public SWIGTYPE_p_SimTK__ConstraintIndex getSimbodyConstraintIndex() {
    return new SWIGTYPE_p_SimTK__ConstraintIndex(opensimMocoJNI.MocoKinematicConstraint_getSimbodyConstraintIndex(swigCPtr, this), true);
  }

  public int getNumPositionEquations() {
    return opensimMocoJNI.MocoKinematicConstraint_getNumPositionEquations(swigCPtr, this);
  }

  public int getNumVelocityEquations() {
    return opensimMocoJNI.MocoKinematicConstraint_getNumVelocityEquations(swigCPtr, this);
  }

  public int getNumAccelerationEquations() {
    return opensimMocoJNI.MocoKinematicConstraint_getNumAccelerationEquations(swigCPtr, this);
  }

  public SWIGTYPE_p_std__vectorT_KinematicLevel_t getKinematicLevels() {
    return new SWIGTYPE_p_std__vectorT_KinematicLevel_t(opensimMocoJNI.MocoKinematicConstraint_getKinematicLevels(swigCPtr, this), true);
  }

  public void calcKinematicConstraintErrors(Model model, State state, Vector errors) {
    opensimMocoJNI.MocoKinematicConstraint_calcKinematicConstraintErrors(swigCPtr, this, Model.getCPtr(model), model, State.getCPtr(state), state, Vector.getCPtr(errors), errors);
  }

}
