/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 * An abstract base class for specifying a task objective for<br>
 * a dynamic simulation.  This class supports joint, point, and orientation<br>
 * task objectives.  Specific implementations for these kinds of control<br>
 * tasks should inherit from this class.<br>
 * <br>
 * @author Frank C. Anderson<br>
 * @version 1.0
 */
public class CMC_Task extends TrackingTask {
  private transient long swigCPtr;

  public CMC_Task(long cPtr, boolean cMemoryOwn) {
    super(opensimActuatorsAnalysesToolsJNI.CMC_Task_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(CMC_Task obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  @SuppressWarnings("deprecation")
  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimActuatorsAnalysesToolsJNI.delete_CMC_Task(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static CMC_Task safeDownCast(OpenSimObject obj) {
    long cPtr = opensimActuatorsAnalysesToolsJNI.CMC_Task_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new CMC_Task(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimActuatorsAnalysesToolsJNI.CMC_Task_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimActuatorsAnalysesToolsJNI.CMC_Task_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimActuatorsAnalysesToolsJNI.CMC_Task_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new CMC_Task(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimActuatorsAnalysesToolsJNI.CMC_Task_getConcreteClassName(swigCPtr, this);
  }

  public void setWRTBodyName(String aBodyName) {
    opensimActuatorsAnalysesToolsJNI.CMC_Task_setWRTBodyName(swigCPtr, this, aBodyName);
  }

  public String getWRTBodyName() {
    return opensimActuatorsAnalysesToolsJNI.CMC_Task_getWRTBodyName(swigCPtr, this);
  }

  public void setExpressBodyName(String aBodyName) {
    opensimActuatorsAnalysesToolsJNI.CMC_Task_setExpressBodyName(swigCPtr, this, aBodyName);
  }

  public String getExpressBodyName() {
    return opensimActuatorsAnalysesToolsJNI.CMC_Task_getExpressBodyName(swigCPtr, this);
  }

  public void setActive(boolean a0, boolean a1, boolean a2) {
    opensimActuatorsAnalysesToolsJNI.CMC_Task_setActive__SWIG_0(swigCPtr, this, a0, a1, a2);
  }

  public void setActive(boolean a0, boolean a1) {
    opensimActuatorsAnalysesToolsJNI.CMC_Task_setActive__SWIG_1(swigCPtr, this, a0, a1);
  }

  public void setActive(boolean a0) {
    opensimActuatorsAnalysesToolsJNI.CMC_Task_setActive__SWIG_2(swigCPtr, this, a0);
  }

  public boolean getActive(int aWhich) {
    return opensimActuatorsAnalysesToolsJNI.CMC_Task_getActive(swigCPtr, this, aWhich);
  }

  public void setWeight(double aW0, double aW1, double aW2) {
    opensimActuatorsAnalysesToolsJNI.CMC_Task_setWeight__SWIG_0(swigCPtr, this, aW0, aW1, aW2);
  }

  public void setWeight(double aW0, double aW1) {
    opensimActuatorsAnalysesToolsJNI.CMC_Task_setWeight__SWIG_1(swigCPtr, this, aW0, aW1);
  }

  public void setWeight(double aW0) {
    opensimActuatorsAnalysesToolsJNI.CMC_Task_setWeight__SWIG_2(swigCPtr, this, aW0);
  }

  public double getWeight(int aWhich) {
    return opensimActuatorsAnalysesToolsJNI.CMC_Task_getWeight(swigCPtr, this, aWhich);
  }

  public void setKP(double aK0, double aK1, double aK2) {
    opensimActuatorsAnalysesToolsJNI.CMC_Task_setKP__SWIG_0(swigCPtr, this, aK0, aK1, aK2);
  }

  public void setKP(double aK0, double aK1) {
    opensimActuatorsAnalysesToolsJNI.CMC_Task_setKP__SWIG_1(swigCPtr, this, aK0, aK1);
  }

  public void setKP(double aK0) {
    opensimActuatorsAnalysesToolsJNI.CMC_Task_setKP__SWIG_2(swigCPtr, this, aK0);
  }

  public double getKP(int aWhich) {
    return opensimActuatorsAnalysesToolsJNI.CMC_Task_getKP(swigCPtr, this, aWhich);
  }

  public void setKV(double aK0, double aK1, double aK2) {
    opensimActuatorsAnalysesToolsJNI.CMC_Task_setKV__SWIG_0(swigCPtr, this, aK0, aK1, aK2);
  }

  public void setKV(double aK0, double aK1) {
    opensimActuatorsAnalysesToolsJNI.CMC_Task_setKV__SWIG_1(swigCPtr, this, aK0, aK1);
  }

  public void setKV(double aK0) {
    opensimActuatorsAnalysesToolsJNI.CMC_Task_setKV__SWIG_2(swigCPtr, this, aK0);
  }

  public double getKV(int aWhich) {
    return opensimActuatorsAnalysesToolsJNI.CMC_Task_getKV(swigCPtr, this, aWhich);
  }

  public void setKA(double aK0, double aK1, double aK2) {
    opensimActuatorsAnalysesToolsJNI.CMC_Task_setKA__SWIG_0(swigCPtr, this, aK0, aK1, aK2);
  }

  public void setKA(double aK0, double aK1) {
    opensimActuatorsAnalysesToolsJNI.CMC_Task_setKA__SWIG_1(swigCPtr, this, aK0, aK1);
  }

  public void setKA(double aK0) {
    opensimActuatorsAnalysesToolsJNI.CMC_Task_setKA__SWIG_2(swigCPtr, this, aK0);
  }

  public double getKA(int aWhich) {
    return opensimActuatorsAnalysesToolsJNI.CMC_Task_getKA(swigCPtr, this, aWhich);
  }

  public void setDirection_0(Vec3 aR) {
    opensimActuatorsAnalysesToolsJNI.CMC_Task_setDirection_0(swigCPtr, this, Vec3.getCPtr(aR), aR);
  }

  public void getDirection_0(Vec3 rR) {
    opensimActuatorsAnalysesToolsJNI.CMC_Task_getDirection_0(swigCPtr, this, Vec3.getCPtr(rR), rR);
  }

  public void setDirection_1(Vec3 aR) {
    opensimActuatorsAnalysesToolsJNI.CMC_Task_setDirection_1(swigCPtr, this, Vec3.getCPtr(aR), aR);
  }

  public void getDirection_1(Vec3 rR) {
    opensimActuatorsAnalysesToolsJNI.CMC_Task_getDirection_1(swigCPtr, this, Vec3.getCPtr(rR), rR);
  }

  public void setDirection_2(Vec3 aR) {
    opensimActuatorsAnalysesToolsJNI.CMC_Task_setDirection_2(swigCPtr, this, Vec3.getCPtr(aR), aR);
  }

  public void getDirection_2(Vec3 rR) {
    opensimActuatorsAnalysesToolsJNI.CMC_Task_getDirection_2(swigCPtr, this, Vec3.getCPtr(rR), rR);
  }

  public Function getTaskFunction(int aWhich) {
    long cPtr = opensimActuatorsAnalysesToolsJNI.CMC_Task_getTaskFunction(swigCPtr, this, aWhich);
    return (cPtr == 0) ? null : new Function(cPtr, false);
  }

  public void setTaskFunctionsForVelocity(Function aF0, Function aF1, Function aF2) {
    opensimActuatorsAnalysesToolsJNI.CMC_Task_setTaskFunctionsForVelocity__SWIG_0(swigCPtr, this, Function.getCPtr(aF0), aF0, Function.getCPtr(aF1), aF1, Function.getCPtr(aF2), aF2);
  }

  public void setTaskFunctionsForVelocity(Function aF0, Function aF1) {
    opensimActuatorsAnalysesToolsJNI.CMC_Task_setTaskFunctionsForVelocity__SWIG_1(swigCPtr, this, Function.getCPtr(aF0), aF0, Function.getCPtr(aF1), aF1);
  }

  public void setTaskFunctionsForVelocity(Function aF0) {
    opensimActuatorsAnalysesToolsJNI.CMC_Task_setTaskFunctionsForVelocity__SWIG_2(swigCPtr, this, Function.getCPtr(aF0), aF0);
  }

  public Function getTaskFunctionForVelocity(int aWhich) {
    long cPtr = opensimActuatorsAnalysesToolsJNI.CMC_Task_getTaskFunctionForVelocity(swigCPtr, this, aWhich);
    return (cPtr == 0) ? null : new Function(cPtr, false);
  }

  public void setTaskFunctionsForAcceleration(Function aF0, Function aF1, Function aF2) {
    opensimActuatorsAnalysesToolsJNI.CMC_Task_setTaskFunctionsForAcceleration__SWIG_0(swigCPtr, this, Function.getCPtr(aF0), aF0, Function.getCPtr(aF1), aF1, Function.getCPtr(aF2), aF2);
  }

  public void setTaskFunctionsForAcceleration(Function aF0, Function aF1) {
    opensimActuatorsAnalysesToolsJNI.CMC_Task_setTaskFunctionsForAcceleration__SWIG_1(swigCPtr, this, Function.getCPtr(aF0), aF0, Function.getCPtr(aF1), aF1);
  }

  public void setTaskFunctionsForAcceleration(Function aF0) {
    opensimActuatorsAnalysesToolsJNI.CMC_Task_setTaskFunctionsForAcceleration__SWIG_2(swigCPtr, this, Function.getCPtr(aF0), aF0);
  }

  public Function getTaskFunctionForAcceleration(int aWhich) {
    long cPtr = opensimActuatorsAnalysesToolsJNI.CMC_Task_getTaskFunctionForAcceleration(swigCPtr, this, aWhich);
    return (cPtr == 0) ? null : new Function(cPtr, false);
  }

  public double getTaskPosition(int aWhich, double aT) {
    return opensimActuatorsAnalysesToolsJNI.CMC_Task_getTaskPosition(swigCPtr, this, aWhich, aT);
  }

  public double getTaskVelocity(int aWhich, double aT) {
    return opensimActuatorsAnalysesToolsJNI.CMC_Task_getTaskVelocity(swigCPtr, this, aWhich, aT);
  }

  public double getTaskAcceleration(int aWhich, double aT) {
    return opensimActuatorsAnalysesToolsJNI.CMC_Task_getTaskAcceleration(swigCPtr, this, aWhich, aT);
  }

  public void setPositionErrorLast(double aE0, double aE1, double aE2) {
    opensimActuatorsAnalysesToolsJNI.CMC_Task_setPositionErrorLast__SWIG_0(swigCPtr, this, aE0, aE1, aE2);
  }

  public void setPositionErrorLast(double aE0, double aE1) {
    opensimActuatorsAnalysesToolsJNI.CMC_Task_setPositionErrorLast__SWIG_1(swigCPtr, this, aE0, aE1);
  }

  public void setPositionErrorLast(double aE0) {
    opensimActuatorsAnalysesToolsJNI.CMC_Task_setPositionErrorLast__SWIG_2(swigCPtr, this, aE0);
  }

  public double getPositionErrorLast(int aWhich) {
    return opensimActuatorsAnalysesToolsJNI.CMC_Task_getPositionErrorLast(swigCPtr, this, aWhich);
  }

  public void setVelocityErrorLast(double aE0, double aE1, double aE2) {
    opensimActuatorsAnalysesToolsJNI.CMC_Task_setVelocityErrorLast__SWIG_0(swigCPtr, this, aE0, aE1, aE2);
  }

  public void setVelocityErrorLast(double aE0, double aE1) {
    opensimActuatorsAnalysesToolsJNI.CMC_Task_setVelocityErrorLast__SWIG_1(swigCPtr, this, aE0, aE1);
  }

  public void setVelocityErrorLast(double aE0) {
    opensimActuatorsAnalysesToolsJNI.CMC_Task_setVelocityErrorLast__SWIG_2(swigCPtr, this, aE0);
  }

  public double getVelocityErrorLast(int aWhich) {
    return opensimActuatorsAnalysesToolsJNI.CMC_Task_getVelocityErrorLast(swigCPtr, this, aWhich);
  }

  public double getPositionError(int aWhich) {
    return opensimActuatorsAnalysesToolsJNI.CMC_Task_getPositionError(swigCPtr, this, aWhich);
  }

  public double getVelocityError(int aWhich) {
    return opensimActuatorsAnalysesToolsJNI.CMC_Task_getVelocityError(swigCPtr, this, aWhich);
  }

  public double getDesiredAcceleration(int aWhich) {
    return opensimActuatorsAnalysesToolsJNI.CMC_Task_getDesiredAcceleration(swigCPtr, this, aWhich);
  }

  public double getAcceleration(int aWhich) {
    return opensimActuatorsAnalysesToolsJNI.CMC_Task_getAcceleration(swigCPtr, this, aWhich);
  }

  public void computeErrors(State s, double aT) {
    opensimActuatorsAnalysesToolsJNI.CMC_Task_computeErrors(swigCPtr, this, State.getCPtr(s), s, aT);
  }

  public void computeDesiredAccelerations(State s, double aT) {
    opensimActuatorsAnalysesToolsJNI.CMC_Task_computeDesiredAccelerations__SWIG_0(swigCPtr, this, State.getCPtr(s), s, aT);
  }

  public void computeDesiredAccelerations(State s, double aTI, double aTF) {
    opensimActuatorsAnalysesToolsJNI.CMC_Task_computeDesiredAccelerations__SWIG_1(swigCPtr, this, State.getCPtr(s), s, aTI, aTF);
  }

  public void computeAccelerations(State s) {
    opensimActuatorsAnalysesToolsJNI.CMC_Task_computeAccelerations(swigCPtr, this, State.getCPtr(s), s);
  }

  public void computeJacobian() {
    opensimActuatorsAnalysesToolsJNI.CMC_Task_computeJacobian(swigCPtr, this);
  }

  public void computeEffectiveMassMatrix() {
    opensimActuatorsAnalysesToolsJNI.CMC_Task_computeEffectiveMassMatrix(swigCPtr, this);
  }

}
