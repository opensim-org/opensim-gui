/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class MarkerPairSet extends SetMarkerPairs {
  private transient long swigCPtr;

  protected MarkerPairSet(long cPtr, boolean cMemoryOwn) {
    super(opensimActuatorsAnalysesToolsJNI.MarkerPairSet_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected static long getCPtr(MarkerPairSet obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimActuatorsAnalysesToolsJNI.delete_MarkerPairSet(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static MarkerPairSet safeDownCast(OpenSimObject obj) {
    long cPtr = opensimActuatorsAnalysesToolsJNI.MarkerPairSet_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new MarkerPairSet(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimActuatorsAnalysesToolsJNI.MarkerPairSet_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimActuatorsAnalysesToolsJNI.MarkerPairSet_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimActuatorsAnalysesToolsJNI.MarkerPairSet_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new MarkerPairSet(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimActuatorsAnalysesToolsJNI.MarkerPairSet_getConcreteClassName(swigCPtr, this);
  }

  public MarkerPairSet() {
    this(opensimActuatorsAnalysesToolsJNI.new_MarkerPairSet__SWIG_0(), true);
  }

  public MarkerPairSet(MarkerPairSet aSimmMarkerPairSet) {
    this(opensimActuatorsAnalysesToolsJNI.new_MarkerPairSet__SWIG_1(MarkerPairSet.getCPtr(aSimmMarkerPairSet), aSimmMarkerPairSet), true);
  }

}
