/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.1.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 *  Add residual actuators to the model using<br>
 * ModelFactory::createResidualActuators. 
 */
public class ModOpAddResiduals extends ModelOperator {
  private transient long swigCPtr;

  public ModOpAddResiduals(long cPtr, boolean cMemoryOwn) {
    super(opensimActuatorsAnalysesToolsJNI.ModOpAddResiduals_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(ModOpAddResiduals obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  public static long swigRelease(ModOpAddResiduals obj) {
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
        opensimActuatorsAnalysesToolsJNI.delete_ModOpAddResiduals(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static ModOpAddResiduals safeDownCast(OpenSimObject obj) {
    long cPtr = opensimActuatorsAnalysesToolsJNI.ModOpAddResiduals_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new ModOpAddResiduals(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimActuatorsAnalysesToolsJNI.ModOpAddResiduals_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimActuatorsAnalysesToolsJNI.ModOpAddResiduals_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimActuatorsAnalysesToolsJNI.ModOpAddResiduals_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new ModOpAddResiduals(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimActuatorsAnalysesToolsJNI.ModOpAddResiduals_getConcreteClassName(swigCPtr, this);
  }

  public ModOpAddResiduals() {
    this(opensimActuatorsAnalysesToolsJNI.new_ModOpAddResiduals__SWIG_0(), true);
  }

  public ModOpAddResiduals(double rotOptimalForce, double transOptimalForce) {
    this(opensimActuatorsAnalysesToolsJNI.new_ModOpAddResiduals__SWIG_1(rotOptimalForce, transOptimalForce), true);
  }

  public ModOpAddResiduals(double rotOptimalForce, double transOptimalForce, double bound) {
    this(opensimActuatorsAnalysesToolsJNI.new_ModOpAddResiduals__SWIG_2(rotOptimalForce, transOptimalForce, bound), true);
  }

  public ModOpAddResiduals(double rotOptimalForce, double transOptimalForce, double bounds, boolean skipCoordsWithActu) {
    this(opensimActuatorsAnalysesToolsJNI.new_ModOpAddResiduals__SWIG_3(rotOptimalForce, transOptimalForce, bounds, skipCoordsWithActu), true);
  }

  public void operate(Model model, String arg1) {
    opensimActuatorsAnalysesToolsJNI.ModOpAddResiduals_operate(swigCPtr, this, Model.getCPtr(model), model, arg1);
  }

}
