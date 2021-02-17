/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.9
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class OrientationWeightSet extends SetOientationWeights {
  private transient long swigCPtr;

  public OrientationWeightSet(long cPtr, boolean cMemoryOwn) {
    super(opensimActuatorsAnalysesToolsJNI.OrientationWeightSet_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(OrientationWeightSet obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimActuatorsAnalysesToolsJNI.delete_OrientationWeightSet(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static OrientationWeightSet safeDownCast(OpenSimObject obj) {
    long cPtr = opensimActuatorsAnalysesToolsJNI.OrientationWeightSet_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new OrientationWeightSet(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimActuatorsAnalysesToolsJNI.OrientationWeightSet_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimActuatorsAnalysesToolsJNI.OrientationWeightSet_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimActuatorsAnalysesToolsJNI.OrientationWeightSet_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new OrientationWeightSet(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimActuatorsAnalysesToolsJNI.OrientationWeightSet_getConcreteClassName(swigCPtr, this);
  }

  public OrientationWeightSet() {
    this(opensimActuatorsAnalysesToolsJNI.new_OrientationWeightSet__SWIG_0(), true);
  }

  public OrientationWeightSet(OrientationWeightSet arg0) {
    this(opensimActuatorsAnalysesToolsJNI.new_OrientationWeightSet__SWIG_1(OrientationWeightSet.getCPtr(arg0), arg0), true);
  }

}
