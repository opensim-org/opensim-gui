/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class ModelComponentSetJoints extends SetJoints {
  private transient long swigCPtr;

  public ModelComponentSetJoints(long cPtr, boolean cMemoryOwn) {
    super(opensimSimulationJNI.ModelComponentSetJoints_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(ModelComponentSetJoints obj) {
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
        opensimSimulationJNI.delete_ModelComponentSetJoints(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static ModelComponentSetJoints safeDownCast(OpenSimObject obj) {
    long cPtr = opensimSimulationJNI.ModelComponentSetJoints_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new ModelComponentSetJoints(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimSimulationJNI.ModelComponentSetJoints_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimSimulationJNI.ModelComponentSetJoints_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimSimulationJNI.ModelComponentSetJoints_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new ModelComponentSetJoints(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimSimulationJNI.ModelComponentSetJoints_getConcreteClassName(swigCPtr, this);
  }

  public void extendFinalizeFromProperties() {
    opensimSimulationJNI.ModelComponentSetJoints_extendFinalizeFromProperties(swigCPtr, this);
  }

  public ModelComponentSetJoints() {
    this(opensimSimulationJNI.new_ModelComponentSetJoints(), true);
  }

}
