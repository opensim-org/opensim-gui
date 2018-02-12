/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class InputVec3 extends AbstractInput {
  private transient long swigCPtr;

  protected InputVec3(long cPtr, boolean cMemoryOwn) {
    super(opensimCommonJNI.InputVec3_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected static long getCPtr(InputVec3 obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimCommonJNI.delete_InputVec3(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public AbstractSocket clone() {
    long cPtr = opensimCommonJNI.InputVec3_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new InputVec3(cPtr, true);
  }

  public void connect(AbstractOutput output, String alias) {
    opensimCommonJNI.InputVec3_connect__SWIG_0(swigCPtr, this, AbstractOutput.getCPtr(output), output, alias);
  }

  public void connect(AbstractOutput output) {
    opensimCommonJNI.InputVec3_connect__SWIG_1(swigCPtr, this, AbstractOutput.getCPtr(output), output);
  }

  public void connect(AbstractChannel channel, String alias) {
    opensimCommonJNI.InputVec3_connect__SWIG_2(swigCPtr, this, AbstractChannel.getCPtr(channel), channel, alias);
  }

  public void connect(AbstractChannel channel) {
    opensimCommonJNI.InputVec3_connect__SWIG_3(swigCPtr, this, AbstractChannel.getCPtr(channel), channel);
  }

  public void findAndConnect(Component root) {
    opensimCommonJNI.InputVec3_findAndConnect(swigCPtr, this, Component.getCPtr(root), root);
  }

  public void disconnect() {
    opensimCommonJNI.InputVec3_disconnect(swigCPtr, this);
  }

  public boolean isConnected() {
    return opensimCommonJNI.InputVec3_isConnected(swigCPtr, this);
  }

  public Vec3 getValue(State state) {
    return new Vec3(opensimCommonJNI.InputVec3_getValue__SWIG_0(swigCPtr, this, State.getCPtr(state), state), false);
  }

  public Vec3 getValue(State state, long index) {
    return new Vec3(opensimCommonJNI.InputVec3_getValue__SWIG_1(swigCPtr, this, State.getCPtr(state), state, index), false);
  }

  public SWIGTYPE_p_OpenSim__OutputT_SimTK__VecT_3_double_1_t_t__Channel getChannel() {
    return new SWIGTYPE_p_OpenSim__OutputT_SimTK__VecT_3_double_1_t_t__Channel(opensimCommonJNI.InputVec3_getChannel__SWIG_0(swigCPtr, this), false);
  }

  public SWIGTYPE_p_OpenSim__OutputT_SimTK__VecT_3_double_1_t_t__Channel getChannel(long index) {
    return new SWIGTYPE_p_OpenSim__OutputT_SimTK__VecT_3_double_1_t_t__Channel(opensimCommonJNI.InputVec3_getChannel__SWIG_1(swigCPtr, this, index), false);
  }

  public String getAlias() {
    return opensimCommonJNI.InputVec3_getAlias__SWIG_0(swigCPtr, this);
  }

  public String getAlias(long index) {
    return opensimCommonJNI.InputVec3_getAlias__SWIG_1(swigCPtr, this, index);
  }

  public void setAlias(String alias) {
    opensimCommonJNI.InputVec3_setAlias__SWIG_0(swigCPtr, this, alias);
  }

  public void setAlias(long index, String alias) {
    opensimCommonJNI.InputVec3_setAlias__SWIG_1(swigCPtr, this, index, alias);
  }

  public String getLabel() {
    return opensimCommonJNI.InputVec3_getLabel__SWIG_0(swigCPtr, this);
  }

  public String getLabel(long index) {
    return opensimCommonJNI.InputVec3_getLabel__SWIG_1(swigCPtr, this, index);
  }

  public VectorOfVec3 getVector(State state) {
    return new VectorOfVec3(opensimCommonJNI.InputVec3_getVector(swigCPtr, this, State.getCPtr(state), state), true);
  }

  public SWIGTYPE_p_std__vectorT_SimTK__ReferencePtrT_OpenSim__OutputT_SimTK__VecT_3_t_t__Channel_const_t_t getChannels() {
    return new SWIGTYPE_p_std__vectorT_SimTK__ReferencePtrT_OpenSim__OutputT_SimTK__VecT_3_t_t__Channel_const_t_t(opensimCommonJNI.InputVec3_getChannels(swigCPtr, this), false);
  }

  public String getConnecteeTypeName() {
    return opensimCommonJNI.InputVec3_getConnecteeTypeName(swigCPtr, this);
  }

  public static boolean isA(AbstractInput p) {
    return opensimCommonJNI.InputVec3_isA(AbstractInput.getCPtr(p), p);
  }

  public static InputVec3 downcast(AbstractInput p) {
    return new InputVec3(opensimCommonJNI.InputVec3_downcast(AbstractInput.getCPtr(p), p), false);
  }

  public static InputVec3 updDowncast(AbstractInput p) {
    return new InputVec3(opensimCommonJNI.InputVec3_updDowncast(AbstractInput.getCPtr(p), p), false);
  }

  public static InputVec3 safeDownCast(AbstractInput base) {
    long cPtr = opensimCommonJNI.InputVec3_safeDownCast(AbstractInput.getCPtr(base), base);
    return (cPtr == 0) ? null : new InputVec3(cPtr, false);
  }

}
