/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.9
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class ModOpAddExternalLoads extends ModelOperator {
  private transient long swigCPtr;

  public ModOpAddExternalLoads(long cPtr, boolean cMemoryOwn) {
    super(opensimActuatorsAnalysesToolsJNI.ModOpAddExternalLoads_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(ModOpAddExternalLoads obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimActuatorsAnalysesToolsJNI.delete_ModOpAddExternalLoads(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static ModOpAddExternalLoads safeDownCast(OpenSimObject obj) {
    long cPtr = opensimActuatorsAnalysesToolsJNI.ModOpAddExternalLoads_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new ModOpAddExternalLoads(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimActuatorsAnalysesToolsJNI.ModOpAddExternalLoads_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimActuatorsAnalysesToolsJNI.ModOpAddExternalLoads_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimActuatorsAnalysesToolsJNI.ModOpAddExternalLoads_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new ModOpAddExternalLoads(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimActuatorsAnalysesToolsJNI.ModOpAddExternalLoads_getConcreteClassName(swigCPtr, this);
  }

  public ModOpAddExternalLoads() {
    this(opensimActuatorsAnalysesToolsJNI.new_ModOpAddExternalLoads__SWIG_0(), true);
  }

  public ModOpAddExternalLoads(String filepath) {
    this(opensimActuatorsAnalysesToolsJNI.new_ModOpAddExternalLoads__SWIG_1(filepath), true);
  }

  public void operate(Model model, String relativeToDirectory) {
    opensimActuatorsAnalysesToolsJNI.ModOpAddExternalLoads_operate(swigCPtr, this, Model.getCPtr(model), model, relativeToDirectory);
  }

}
