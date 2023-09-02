/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 *  An Input&lt;Y&gt; must be connected by an Output&lt;Y&gt; 
 */
public class InputVec3 extends AbstractInput {
  private transient long swigCPtr;

  public InputVec3(long cPtr, boolean cMemoryOwn) {
    super(opensimCommonJNI.InputVec3_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(InputVec3 obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  @SuppressWarnings("deprecation")
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

  /**
   *  Connect this Input to the provided (Abstract)Output. 
   */
  public void connect(AbstractOutput output, String alias) {
    opensimCommonJNI.InputVec3_connect__SWIG_0(swigCPtr, this, AbstractOutput.getCPtr(output), output, alias);
  }

  /**
   *  Connect this Input to the provided (Abstract)Output. 
   */
  public void connect(AbstractOutput output) {
    opensimCommonJNI.InputVec3_connect__SWIG_1(swigCPtr, this, AbstractOutput.getCPtr(output), output);
  }

  public void connect(AbstractChannel channel, String alias) {
    opensimCommonJNI.InputVec3_connect__SWIG_2(swigCPtr, this, AbstractChannel.getCPtr(channel), channel, alias);
  }

  public void connect(AbstractChannel channel) {
    opensimCommonJNI.InputVec3_connect__SWIG_3(swigCPtr, this, AbstractChannel.getCPtr(channel), channel);
  }

  /**
   *  Connect this Input given a root Component to search for<br>
   *     the Output according to the connectee path of this Input  
   */
  public void finalizeConnection(Component root) {
    opensimCommonJNI.InputVec3_finalizeConnection(swigCPtr, this, Component.getCPtr(root), root);
  }

  public void disconnect() {
    opensimCommonJNI.InputVec3_disconnect(swigCPtr, this);
  }

  public boolean isConnected() {
    return opensimCommonJNI.InputVec3_isConnected(swigCPtr, this);
  }

  /**
   *  Get the value of this Input when it is connected. Redirects to connected<br>
   *     Output&lt;T&gt;'s getValue() with minimal overhead. This method can be used only<br>
   *     for non-list Input(s). For list Input(s), use the other overload.         
   */
  public Vec3 getValue(State state) {
    return new Vec3(opensimCommonJNI.InputVec3_getValue__SWIG_0(swigCPtr, this, State.getCPtr(state), state), false);
  }

  /**
   * Get the value of this Input when it is connected. Redirects to connected<br>
   *     Output&lt;T&gt;'s getValue() with minimal overhead. Specify the index of the <br>
   *     Channel whose value is desired.                                           
   */
  public Vec3 getValue(State state, long index) {
    return new Vec3(opensimCommonJNI.InputVec3_getValue__SWIG_1(swigCPtr, this, State.getCPtr(state), state, index), false);
  }

  /**
   *  Get the Channel associated with this Input. This method can only be<br>
   *     used for non-list Input(s). For list Input(s), use the other overload.    
   */
  public SWIGTYPE_p_OpenSim__OutputT_SimTK__VecT_3_double_1_t_t__Channel getChannel() {
    return new SWIGTYPE_p_OpenSim__OutputT_SimTK__VecT_3_double_1_t_t__Channel(opensimCommonJNI.InputVec3_getChannel__SWIG_0(swigCPtr, this), false);
  }

  /**
   *  Get the Channel associated with this Input. Specify the index of the<br>
   *     channel desired.                                                          
   */
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

  /**
   *  Access the values of all the channels connected to this Input as a <br>
   *     SimTK::Vector_&lt;T&gt;. The elements are in the same order as the channels.
   */
  public VectorVec3 getVector(State state) {
    return new VectorVec3(opensimCommonJNI.InputVec3_getVector(swigCPtr, this, State.getCPtr(state), state), true);
  }

  /**
   *  Get const access to the channels connected to this input.<br>
   *         You can use this to iterate through the channels.<br>
   *         {@code 
          for (const auto& chan : getChannels()) {
              std::cout << chan.getValue(state) << std::endl;
          }
          }
   */
  public SWIGTYPE_p_std__vectorT_SimTK__ReferencePtrT_OpenSim__OutputT_SimTK__VecT_3_t_t__Channel_const_t_t getChannels() {
    return new SWIGTYPE_p_std__vectorT_SimTK__ReferencePtrT_OpenSim__OutputT_SimTK__VecT_3_t_t__Channel_const_t_t(opensimCommonJNI.InputVec3_getChannels(swigCPtr, this), false);
  }

  /**
   *  Return the typename of the Output value, T, that satisfies<br>
   *         this Input&lt;T&gt;. No reason to return Output&lt;T&gt; since it is a<br>
   *         given that only an Output can satisfy an Input. 
   */
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

  /**
   *  For use in python/java/MATLAB bindings. 
   */
  public static InputVec3 safeDownCast(AbstractInput base) {
    long cPtr = opensimCommonJNI.InputVec3_safeDownCast(AbstractInput.getCPtr(base), base);
    return (cPtr == 0) ? null : new InputVec3(cPtr, false);
  }

}
