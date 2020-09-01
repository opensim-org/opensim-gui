/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.9
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class IKMarkerTask extends IKTask {
  private transient long swigCPtr;

  public IKMarkerTask(long cPtr, boolean cMemoryOwn) {
    super(opensimActuatorsAnalysesToolsJNI.IKMarkerTask_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(IKMarkerTask obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimActuatorsAnalysesToolsJNI.delete_IKMarkerTask(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static IKMarkerTask safeDownCast(OpenSimObject obj) {
    long cPtr = opensimActuatorsAnalysesToolsJNI.IKMarkerTask_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new IKMarkerTask(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimActuatorsAnalysesToolsJNI.IKMarkerTask_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimActuatorsAnalysesToolsJNI.IKMarkerTask_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimActuatorsAnalysesToolsJNI.IKMarkerTask_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new IKMarkerTask(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimActuatorsAnalysesToolsJNI.IKMarkerTask_getConcreteClassName(swigCPtr, this);
  }

  public IKMarkerTask() {
    this(opensimActuatorsAnalysesToolsJNI.new_IKMarkerTask(), true);
  }

}
