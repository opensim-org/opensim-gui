/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.1.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 *  Prescribe motion to Coordinate%s in a model by providing a table containing<br>
 *  time series data of Coordinate values. Any columns in the provided table<br>
 *  (e.g., "/jointset/ankle_r/ankle_angle_r/value") that do not match a valid<br>
 *  path to a Joint Coordinate value in the model will be ignored. A GCVSpline<br>
 *  function is created for each column of Coordinate values and this function<br>
 *  is assigned to the `prescribed_function` property for the matching Coordinate.<br>
 *  In addition, the `prescribed` property for each matching Coordinate is set<br>
 *  to "true".
 */
public class ModOpPrescribeCoordinateValues extends ModelOperator {
  private transient long swigCPtr;

  public ModOpPrescribeCoordinateValues(long cPtr, boolean cMemoryOwn) {
    super(opensimActuatorsAnalysesToolsJNI.ModOpPrescribeCoordinateValues_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(ModOpPrescribeCoordinateValues obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  public static long swigRelease(ModOpPrescribeCoordinateValues obj) {
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
        opensimActuatorsAnalysesToolsJNI.delete_ModOpPrescribeCoordinateValues(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static ModOpPrescribeCoordinateValues safeDownCast(OpenSimObject obj) {
    long cPtr = opensimActuatorsAnalysesToolsJNI.ModOpPrescribeCoordinateValues_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new ModOpPrescribeCoordinateValues(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimActuatorsAnalysesToolsJNI.ModOpPrescribeCoordinateValues_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimActuatorsAnalysesToolsJNI.ModOpPrescribeCoordinateValues_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimActuatorsAnalysesToolsJNI.ModOpPrescribeCoordinateValues_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new ModOpPrescribeCoordinateValues(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimActuatorsAnalysesToolsJNI.ModOpPrescribeCoordinateValues_getConcreteClassName(swigCPtr, this);
  }

  public ModOpPrescribeCoordinateValues(TableProcessor table) {
    this(opensimActuatorsAnalysesToolsJNI.new_ModOpPrescribeCoordinateValues(TableProcessor.getCPtr(table), table), true);
  }

  public void operate(Model model, String arg1) {
    opensimActuatorsAnalysesToolsJNI.ModOpPrescribeCoordinateValues_operate(swigCPtr, this, Model.getCPtr(model), model, arg1);
  }

}
