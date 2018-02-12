/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class InterruptCallback extends AnalysisWrapper {
  private transient long swigCPtr;

  protected InterruptCallback(long cPtr, boolean cMemoryOwn) {
    super(opensimActuatorsAnalysesToolsJNI.InterruptCallback_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected static long getCPtr(InterruptCallback obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimActuatorsAnalysesToolsJNI.delete_InterruptCallback(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public InterruptCallback(Model aModel) {
    this(opensimActuatorsAnalysesToolsJNI.new_InterruptCallback__SWIG_0(Model.getCPtr(aModel), aModel), true);
  }

  public InterruptCallback() {
    this(opensimActuatorsAnalysesToolsJNI.new_InterruptCallback__SWIG_1(), true);
  }

  public void interrupt() {
    opensimActuatorsAnalysesToolsJNI.InterruptCallback_interrupt(swigCPtr, this);
  }

  public int step(State s, int stepNumber) {
    return opensimActuatorsAnalysesToolsJNI.InterruptCallback_step(swigCPtr, this, State.getCPtr(s), s, stepNumber);
  }

}
