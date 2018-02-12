/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class WrapSphereObst extends WrapObject {
  private transient long swigCPtr;

  protected WrapSphereObst(long cPtr, boolean cMemoryOwn) {
    super(opensimSimulationJNI.WrapSphereObst_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected static long getCPtr(WrapSphereObst obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimSimulationJNI.delete_WrapSphereObst(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static WrapSphereObst safeDownCast(OpenSimObject obj) {
    long cPtr = opensimSimulationJNI.WrapSphereObst_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new WrapSphereObst(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimSimulationJNI.WrapSphereObst_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimSimulationJNI.WrapSphereObst_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimSimulationJNI.WrapSphereObst_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new WrapSphereObst(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimSimulationJNI.WrapSphereObst_getConcreteClassName(swigCPtr, this);
  }

  public WrapSphereObst() {
    this(opensimSimulationJNI.new_WrapSphereObst__SWIG_0(), true);
  }

  public WrapSphereObst(WrapSphereObst aWrapSphereObst) {
    this(opensimSimulationJNI.new_WrapSphereObst__SWIG_1(WrapSphereObst.getCPtr(aWrapSphereObst), aWrapSphereObst), true);
  }

  public void copyData(WrapSphereObst aWrapSphereObst) {
    opensimSimulationJNI.WrapSphereObst_copyData(swigCPtr, this, WrapSphereObst.getCPtr(aWrapSphereObst), aWrapSphereObst);
  }

  public double getRadius() {
    return opensimSimulationJNI.WrapSphereObst_getRadius(swigCPtr, this);
  }

  public void setRadius(double aRadius) {
    opensimSimulationJNI.WrapSphereObst_setRadius(swigCPtr, this, aRadius);
  }

  public double getLength() {
    return opensimSimulationJNI.WrapSphereObst_getLength(swigCPtr, this);
  }

  public void setLength(double aLength) {
    opensimSimulationJNI.WrapSphereObst_setLength(swigCPtr, this, aLength);
  }

  public String getWrapTypeName() {
    return opensimSimulationJNI.WrapSphereObst_getWrapTypeName(swigCPtr, this);
  }

  public String getDimensionsString() {
    return opensimSimulationJNI.WrapSphereObst_getDimensionsString(swigCPtr, this);
  }

  public void connectToModelAndBody(Model aModel, PhysicalFrame aBody) {
    opensimSimulationJNI.WrapSphereObst_connectToModelAndBody(swigCPtr, this, Model.getCPtr(aModel), aModel, PhysicalFrame.getCPtr(aBody), aBody);
  }

}
