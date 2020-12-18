/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.9
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class MocoAccelerationTrackingGoal extends MocoGoal {
  private transient long swigCPtr;

  public MocoAccelerationTrackingGoal(long cPtr, boolean cMemoryOwn) {
    super(opensimMocoJNI.MocoAccelerationTrackingGoal_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(MocoAccelerationTrackingGoal obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimMocoJNI.delete_MocoAccelerationTrackingGoal(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static MocoAccelerationTrackingGoal safeDownCast(OpenSimObject obj) {
    long cPtr = opensimMocoJNI.MocoAccelerationTrackingGoal_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new MocoAccelerationTrackingGoal(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimMocoJNI.MocoAccelerationTrackingGoal_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimMocoJNI.MocoAccelerationTrackingGoal_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimMocoJNI.MocoAccelerationTrackingGoal_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new MocoAccelerationTrackingGoal(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimMocoJNI.MocoAccelerationTrackingGoal_getConcreteClassName(swigCPtr, this);
  }

  public MocoAccelerationTrackingGoal() {
    this(opensimMocoJNI.new_MocoAccelerationTrackingGoal__SWIG_0(), true);
  }

  public MocoAccelerationTrackingGoal(String name) {
    this(opensimMocoJNI.new_MocoAccelerationTrackingGoal__SWIG_1(name), true);
  }

  public MocoAccelerationTrackingGoal(String name, double weight) {
    this(opensimMocoJNI.new_MocoAccelerationTrackingGoal__SWIG_2(name, weight), true);
  }

  public void setAccelerationReferenceFile(String filepath) {
    opensimMocoJNI.MocoAccelerationTrackingGoal_setAccelerationReferenceFile(swigCPtr, this, filepath);
  }

  public void setAccelerationReference(TimeSeriesTableVec3 ref) {
    opensimMocoJNI.MocoAccelerationTrackingGoal_setAccelerationReference(swigCPtr, this, TimeSeriesTableVec3.getCPtr(ref), ref);
  }

  public void setFramePaths(StdVectorString paths) {
    opensimMocoJNI.MocoAccelerationTrackingGoal_setFramePaths(swigCPtr, this, StdVectorString.getCPtr(paths), paths);
  }

  public void setWeightForFrame(String frameName, double weight) {
    opensimMocoJNI.MocoAccelerationTrackingGoal_setWeightForFrame(swigCPtr, this, frameName, weight);
  }

  public void setWeightSet(MocoWeightSet weightSet) {
    opensimMocoJNI.MocoAccelerationTrackingGoal_setWeightSet(swigCPtr, this, MocoWeightSet.getCPtr(weightSet), weightSet);
  }

  public String getAccelerationReferenceFile() {
    return opensimMocoJNI.MocoAccelerationTrackingGoal_getAccelerationReferenceFile(swigCPtr, this);
  }

}
