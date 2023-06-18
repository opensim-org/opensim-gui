/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.1.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 * A class for recording the kinematics of the bodies<br>
 * of a model during a simulation.<br>
 * <br>
 * @author Frank C. Anderson<br>
 * @version 1.0
 */
public class BodyKinematics extends Analysis {
  private transient long swigCPtr;

  public BodyKinematics(long cPtr, boolean cMemoryOwn) {
    super(opensimActuatorsAnalysesToolsJNI.BodyKinematics_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(BodyKinematics obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  public static long swigRelease(BodyKinematics obj) {
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
        opensimActuatorsAnalysesToolsJNI.delete_BodyKinematics(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static BodyKinematics safeDownCast(OpenSimObject obj) {
    long cPtr = opensimActuatorsAnalysesToolsJNI.BodyKinematics_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new BodyKinematics(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimActuatorsAnalysesToolsJNI.BodyKinematics_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimActuatorsAnalysesToolsJNI.BodyKinematics_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimActuatorsAnalysesToolsJNI.BodyKinematics_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new BodyKinematics(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimActuatorsAnalysesToolsJNI.BodyKinematics_getConcreteClassName(swigCPtr, this);
  }

  public BodyKinematics(Model aModel, boolean aInDegrees) {
    this(opensimActuatorsAnalysesToolsJNI.new_BodyKinematics__SWIG_0(Model.getCPtr(aModel), aModel, aInDegrees), true);
  }

  public BodyKinematics(Model aModel) {
    this(opensimActuatorsAnalysesToolsJNI.new_BodyKinematics__SWIG_1(Model.getCPtr(aModel), aModel), true);
  }

  public BodyKinematics() {
    this(opensimActuatorsAnalysesToolsJNI.new_BodyKinematics__SWIG_2(), true);
  }

  public BodyKinematics(String aFileName) {
    this(opensimActuatorsAnalysesToolsJNI.new_BodyKinematics__SWIG_3(aFileName), true);
  }

  public BodyKinematics(BodyKinematics aObject) {
    this(opensimActuatorsAnalysesToolsJNI.new_BodyKinematics__SWIG_4(BodyKinematics.getCPtr(aObject), aObject), true);
  }

  public void setStorageCapacityIncrements(int aIncrement) {
    opensimActuatorsAnalysesToolsJNI.BodyKinematics_setStorageCapacityIncrements(swigCPtr, this, aIncrement);
  }

  public Storage getAccelerationStorage() {
    long cPtr = opensimActuatorsAnalysesToolsJNI.BodyKinematics_getAccelerationStorage(swigCPtr, this);
    return (cPtr == 0) ? null : new Storage(cPtr, false);
  }

  public Storage getVelocityStorage() {
    long cPtr = opensimActuatorsAnalysesToolsJNI.BodyKinematics_getVelocityStorage(swigCPtr, this);
    return (cPtr == 0) ? null : new Storage(cPtr, false);
  }

  public Storage getPositionStorage() {
    long cPtr = opensimActuatorsAnalysesToolsJNI.BodyKinematics_getPositionStorage(swigCPtr, this);
    return (cPtr == 0) ? null : new Storage(cPtr, false);
  }

  public void setExpressResultsInLocalFrame(boolean aTrueFalse) {
    opensimActuatorsAnalysesToolsJNI.BodyKinematics_setExpressResultsInLocalFrame(swigCPtr, this, aTrueFalse);
  }

  public boolean getExpressResultsInLocalFrame() {
    return opensimActuatorsAnalysesToolsJNI.BodyKinematics_getExpressResultsInLocalFrame(swigCPtr, this);
  }

  public void setRecordCenterOfMass(boolean aTrueFalse) {
    opensimActuatorsAnalysesToolsJNI.BodyKinematics_setRecordCenterOfMass(swigCPtr, this, aTrueFalse);
  }

  public void setBodiesToRecord(ArrayStr listOfBodies) {
    opensimActuatorsAnalysesToolsJNI.BodyKinematics_setBodiesToRecord(swigCPtr, this, ArrayStr.getCPtr(listOfBodies), listOfBodies);
  }

  public void setModel(Model aModel) {
    opensimActuatorsAnalysesToolsJNI.BodyKinematics_setModel(swigCPtr, this, Model.getCPtr(aModel), aModel);
  }

  public int begin(State s) {
    return opensimActuatorsAnalysesToolsJNI.BodyKinematics_begin(swigCPtr, this, State.getCPtr(s), s);
  }

  public int step(State s, int setNumber) {
    return opensimActuatorsAnalysesToolsJNI.BodyKinematics_step(swigCPtr, this, State.getCPtr(s), s, setNumber);
  }

  public int end(State s) {
    return opensimActuatorsAnalysesToolsJNI.BodyKinematics_end(swigCPtr, this, State.getCPtr(s), s);
  }

  public int printResults(String aBaseName, String aDir, double aDT, String aExtension) {
    return opensimActuatorsAnalysesToolsJNI.BodyKinematics_printResults__SWIG_0(swigCPtr, this, aBaseName, aDir, aDT, aExtension);
  }

  public int printResults(String aBaseName, String aDir, double aDT) {
    return opensimActuatorsAnalysesToolsJNI.BodyKinematics_printResults__SWIG_1(swigCPtr, this, aBaseName, aDir, aDT);
  }

  public int printResults(String aBaseName, String aDir) {
    return opensimActuatorsAnalysesToolsJNI.BodyKinematics_printResults__SWIG_2(swigCPtr, this, aBaseName, aDir);
  }

  public int printResults(String aBaseName) {
    return opensimActuatorsAnalysesToolsJNI.BodyKinematics_printResults__SWIG_3(swigCPtr, this, aBaseName);
  }

}
