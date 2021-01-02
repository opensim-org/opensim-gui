/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.9
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class MocoGoal extends OpenSimObject {
  private transient long swigCPtr;

  public MocoGoal(long cPtr, boolean cMemoryOwn) {
    super(opensimMocoJNI.MocoGoal_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(MocoGoal obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimMocoJNI.delete_MocoGoal(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static MocoGoal safeDownCast(OpenSimObject obj) {
    long cPtr = opensimMocoJNI.MocoGoal_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new MocoGoal(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimMocoJNI.MocoGoal_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimMocoJNI.MocoGoal_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimMocoJNI.MocoGoal_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new MocoGoal(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimMocoJNI.MocoGoal_getConcreteClassName(swigCPtr, this);
  }

  public void setEnabled(boolean enabled) {
    opensimMocoJNI.MocoGoal_setEnabled(swigCPtr, this, enabled);
  }

  public boolean getEnabled() {
    return opensimMocoJNI.MocoGoal_getEnabled(swigCPtr, this);
  }

  public void setWeight(double weight) {
    opensimMocoJNI.MocoGoal_setWeight(swigCPtr, this, weight);
  }

  public double getWeight() {
    return opensimMocoJNI.MocoGoal_getWeight(swigCPtr, this);
  }

  public void setMode(String mode) {
    opensimMocoJNI.MocoGoal_setMode(swigCPtr, this, mode);
  }

  public String getModeAsString() {
    return opensimMocoJNI.MocoGoal_getModeAsString(swigCPtr, this);
  }

  public MocoGoal.Mode getMode() {
    return MocoGoal.Mode.swigToEnum(opensimMocoJNI.MocoGoal_getMode(swigCPtr, this));
  }

  public boolean getModeIsCost() {
    return opensimMocoJNI.MocoGoal_getModeIsCost(swigCPtr, this);
  }

  public boolean getModeIsEndpointConstraint() {
    return opensimMocoJNI.MocoGoal_getModeIsEndpointConstraint(swigCPtr, this);
  }

  public MocoGoal.Mode getDefaultMode() {
    return MocoGoal.Mode.swigToEnum(opensimMocoJNI.MocoGoal_getDefaultMode(swigCPtr, this));
  }

  public boolean getSupportsEndpointConstraint() {
    return opensimMocoJNI.MocoGoal_getSupportsEndpointConstraint(swigCPtr, this);
  }

  public SWIGTYPE_p_MocoConstraintInfo getConstraintInfo() {
    return new SWIGTYPE_p_MocoConstraintInfo(opensimMocoJNI.MocoGoal_getConstraintInfo(swigCPtr, this), false);
  }

  public SWIGTYPE_p_MocoConstraintInfo updConstraintInfo() {
    return new SWIGTYPE_p_MocoConstraintInfo(opensimMocoJNI.MocoGoal_updConstraintInfo(swigCPtr, this), false);
  }

  public int getNumOutputs() {
    return opensimMocoJNI.MocoGoal_getNumOutputs(swigCPtr, this);
  }

  public int getNumIntegrals() {
    return opensimMocoJNI.MocoGoal_getNumIntegrals(swigCPtr, this);
  }

  public Stage getStageDependency() {
    return new Stage(opensimMocoJNI.MocoGoal_getStageDependency(swigCPtr, this), true);
  }

  public void initializeOnModel(Model model) {
    opensimMocoJNI.MocoGoal_initializeOnModel(swigCPtr, this, Model.getCPtr(model), model);
  }

  public void printDescription() {
    opensimMocoJNI.MocoGoal_printDescription(swigCPtr, this);
  }

  public final static class Mode {
    public final static MocoGoal.Mode Cost = new MocoGoal.Mode("Cost");
    public final static MocoGoal.Mode EndpointConstraint = new MocoGoal.Mode("EndpointConstraint");

    public final int swigValue() {
      return swigValue;
    }

    public String toString() {
      return swigName;
    }

    public static Mode swigToEnum(int swigValue) {
      if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
        return swigValues[swigValue];
      for (int i = 0; i < swigValues.length; i++)
        if (swigValues[i].swigValue == swigValue)
          return swigValues[i];
      throw new IllegalArgumentException("No enum " + Mode.class + " with value " + swigValue);
    }

    private Mode(String swigName) {
      this.swigName = swigName;
      this.swigValue = swigNext++;
    }

    private Mode(String swigName, int swigValue) {
      this.swigName = swigName;
      this.swigValue = swigValue;
      swigNext = swigValue+1;
    }

    private Mode(String swigName, Mode swigEnum) {
      this.swigName = swigName;
      this.swigValue = swigEnum.swigValue;
      swigNext = this.swigValue+1;
    }

    private static Mode[] swigValues = { Cost, EndpointConstraint };
    private static int swigNext = 0;
    private final int swigValue;
    private final String swigName;
  }

}