/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.9
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class MocoMarkerFinalGoal extends MocoGoal {
  private transient long swigCPtr;

  public MocoMarkerFinalGoal(long cPtr, boolean cMemoryOwn) {
    super(opensimMocoJNI.MocoMarkerFinalGoal_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(MocoMarkerFinalGoal obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimMocoJNI.delete_MocoMarkerFinalGoal(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static MocoMarkerFinalGoal safeDownCast(OpenSimObject obj) {
    long cPtr = opensimMocoJNI.MocoMarkerFinalGoal_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new MocoMarkerFinalGoal(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimMocoJNI.MocoMarkerFinalGoal_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimMocoJNI.MocoMarkerFinalGoal_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimMocoJNI.MocoMarkerFinalGoal_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new MocoMarkerFinalGoal(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimMocoJNI.MocoMarkerFinalGoal_getConcreteClassName(swigCPtr, this);
  }

  public MocoMarkerFinalGoal() {
    this(opensimMocoJNI.new_MocoMarkerFinalGoal__SWIG_0(), true);
  }

  public MocoMarkerFinalGoal(String name) {
    this(opensimMocoJNI.new_MocoMarkerFinalGoal__SWIG_1(name), true);
  }

  public MocoMarkerFinalGoal(String name, double weight) {
    this(opensimMocoJNI.new_MocoMarkerFinalGoal__SWIG_2(name, weight), true);
  }

  public void setPointName(String pointName) {
    opensimMocoJNI.MocoMarkerFinalGoal_setPointName(swigCPtr, this, pointName);
  }

  public void setReferenceLocation(Vec3 refLocationInGround) {
    opensimMocoJNI.MocoMarkerFinalGoal_setReferenceLocation(swigCPtr, this, Vec3.getCPtr(refLocationInGround), refLocationInGround);
  }

}
