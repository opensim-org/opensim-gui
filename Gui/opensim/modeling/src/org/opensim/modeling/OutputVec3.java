/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class OutputVec3 extends AbstractOutput {
  private transient long swigCPtr;

  protected OutputVec3(long cPtr, boolean cMemoryOwn) {
    super(opensimCommonJNI.OutputVec3_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected static long getCPtr(OutputVec3 obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimCommonJNI.delete_OutputVec3(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public OutputVec3() {
    this(opensimCommonJNI.new_OutputVec3__SWIG_0(), true);
  }

  public OutputVec3(String name, SWIGTYPE_p_std__functionT_void_fOpenSim__Component_const_p_SimTK__State_const_R_std__string_const_R_SimTK__VecT_3_double_1_t_RF_t outputFunction, Stage dependsOnStage, boolean isList) {
    this(opensimCommonJNI.new_OutputVec3__SWIG_1(name, SWIGTYPE_p_std__functionT_void_fOpenSim__Component_const_p_SimTK__State_const_R_std__string_const_R_SimTK__VecT_3_double_1_t_RF_t.getCPtr(outputFunction), Stage.getCPtr(dependsOnStage), dependsOnStage, isList), true);
  }

  public OutputVec3(OutputVec3 source) {
    this(opensimCommonJNI.new_OutputVec3__SWIG_2(OutputVec3.getCPtr(source), source), true);
  }

  public boolean isCompatible(AbstractOutput o) {
    return opensimCommonJNI.OutputVec3_isCompatible(swigCPtr, this, AbstractOutput.getCPtr(o), o);
  }

  public void compatibleAssign(AbstractOutput o) {
    opensimCommonJNI.OutputVec3_compatibleAssign(swigCPtr, this, AbstractOutput.getCPtr(o), o);
  }

  public void clearChannels() {
    opensimCommonJNI.OutputVec3_clearChannels(swigCPtr, this);
  }

  public void addChannel(String channelName) {
    opensimCommonJNI.OutputVec3_addChannel(swigCPtr, this, channelName);
  }

  public AbstractChannel getChannel(String name) {
    return new AbstractChannel(opensimCommonJNI.OutputVec3_getChannel(swigCPtr, this, name), false);
  }

  public SWIGTYPE_p_std__mapT_std__string_OpenSim__OutputT_SimTK__VecT_3_double_1_t_t__Channel_t getChannels() {
    return new SWIGTYPE_p_std__mapT_std__string_OpenSim__OutputT_SimTK__VecT_3_double_1_t_t__Channel_t(opensimCommonJNI.OutputVec3_getChannels(swigCPtr, this), false);
  }

  public Vec3 getValue(State state) {
    return new Vec3(opensimCommonJNI.OutputVec3_getValue(swigCPtr, this, State.getCPtr(state), state), false);
  }

  public String getTypeName() {
    return opensimCommonJNI.OutputVec3_getTypeName(swigCPtr, this);
  }

  public String getValueAsString(State state) {
    return opensimCommonJNI.OutputVec3_getValueAsString(swigCPtr, this, State.getCPtr(state), state);
  }

  public AbstractOutput clone() {
    long cPtr = opensimCommonJNI.OutputVec3_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new OutputVec3(cPtr, true);
  }

  public static boolean isA(AbstractOutput p) {
    return opensimCommonJNI.OutputVec3_isA(AbstractOutput.getCPtr(p), p);
  }

  public static OutputVec3 downcast(AbstractOutput p) {
    return new OutputVec3(opensimCommonJNI.OutputVec3_downcast(AbstractOutput.getCPtr(p), p), false);
  }

  public static OutputVec3 updDowncast(AbstractOutput p) {
    return new OutputVec3(opensimCommonJNI.OutputVec3_updDowncast(AbstractOutput.getCPtr(p), p), false);
  }

  public static OutputVec3 safeDownCast(AbstractOutput parent) {
    long cPtr = opensimCommonJNI.OutputVec3_safeDownCast(AbstractOutput.getCPtr(parent), parent);
    return (cPtr == 0) ? null : new OutputVec3(cPtr, false);
  }

}
