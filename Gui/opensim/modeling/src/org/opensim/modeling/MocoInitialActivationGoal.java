/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.1.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 *  For all muscles with activation dynamics, the initial activation and initial<br>
 * excitation should be the same.<br>
 * Without this goal, muscle activation may undesirably start at its maximum<br>
 * possible value in inverse/tracking problems which penalize only excitations<br>
 * (such activation is "free").<br>
 * This is an endpoint constraint goal by default.<br>
 * Credit for using this goal to address excessive initial activation goes to<br>
 * Jessica Allen.<br>
 * 
 */
public class MocoInitialActivationGoal extends MocoGoal {
  private transient long swigCPtr;

  public MocoInitialActivationGoal(long cPtr, boolean cMemoryOwn) {
    super(opensimMocoJNI.MocoInitialActivationGoal_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(MocoInitialActivationGoal obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  public static long swigRelease(MocoInitialActivationGoal obj) {
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
        opensimMocoJNI.delete_MocoInitialActivationGoal(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static MocoInitialActivationGoal safeDownCast(OpenSimObject obj) {
    long cPtr = opensimMocoJNI.MocoInitialActivationGoal_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new MocoInitialActivationGoal(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimMocoJNI.MocoInitialActivationGoal_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimMocoJNI.MocoInitialActivationGoal_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimMocoJNI.MocoInitialActivationGoal_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new MocoInitialActivationGoal(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimMocoJNI.MocoInitialActivationGoal_getConcreteClassName(swigCPtr, this);
  }

  public MocoInitialActivationGoal() {
    this(opensimMocoJNI.new_MocoInitialActivationGoal__SWIG_0(), true);
  }

  public MocoInitialActivationGoal(String name) {
    this(opensimMocoJNI.new_MocoInitialActivationGoal__SWIG_1(name), true);
  }

}
