/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.9
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class ModOpAddReserves extends ModelOperator {
  private transient long swigCPtr;

  public ModOpAddReserves(long cPtr, boolean cMemoryOwn) {
    super(opensimActuatorsAnalysesToolsJNI.ModOpAddReserves_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(ModOpAddReserves obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimActuatorsAnalysesToolsJNI.delete_ModOpAddReserves(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static ModOpAddReserves safeDownCast(OpenSimObject obj) {
    long cPtr = opensimActuatorsAnalysesToolsJNI.ModOpAddReserves_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new ModOpAddReserves(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimActuatorsAnalysesToolsJNI.ModOpAddReserves_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimActuatorsAnalysesToolsJNI.ModOpAddReserves_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimActuatorsAnalysesToolsJNI.ModOpAddReserves_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new ModOpAddReserves(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimActuatorsAnalysesToolsJNI.ModOpAddReserves_getConcreteClassName(swigCPtr, this);
  }

  public ModOpAddReserves() {
    this(opensimActuatorsAnalysesToolsJNI.new_ModOpAddReserves__SWIG_0(), true);
  }

  public ModOpAddReserves(double optimalForce) {
    this(opensimActuatorsAnalysesToolsJNI.new_ModOpAddReserves__SWIG_1(optimalForce), true);
  }

  public ModOpAddReserves(double optimalForce, double bound) {
    this(opensimActuatorsAnalysesToolsJNI.new_ModOpAddReserves__SWIG_2(optimalForce, bound), true);
  }

  public ModOpAddReserves(double optimalForce, double bounds, boolean skipCoordsWithActu) {
    this(opensimActuatorsAnalysesToolsJNI.new_ModOpAddReserves__SWIG_3(optimalForce, bounds, skipCoordsWithActu), true);
  }

  public void operate(Model model, String arg1) {
    opensimActuatorsAnalysesToolsJNI.ModOpAddReserves_operate(swigCPtr, this, Model.getCPtr(model), model, arg1);
  }

}
