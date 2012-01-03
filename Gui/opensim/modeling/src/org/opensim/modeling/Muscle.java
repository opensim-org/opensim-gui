/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.4
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class Muscle extends PathActuator {
  private long swigCPtr;

  public Muscle(long cPtr, boolean cMemoryOwn) {
    super(opensimModelJNI.Muscle_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(Muscle obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimModelJNI.delete_Muscle(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public OpenSimObject copy() {
    long cPtr = opensimModelJNI.Muscle_copy__SWIG_0(swigCPtr, this);
    return (cPtr == 0) ? null : new OpenSimObject(cPtr, false);
  }

  public void copyData(Muscle aMuscle) {
    opensimModelJNI.Muscle_copyData(swigCPtr, this, Muscle.getCPtr(aMuscle), aMuscle);
  }

  public void updateFromXMLNode(SWIGTYPE_p_SimTK__Xml__Element aNode, int versionNumber) {
    opensimModelJNI.Muscle_updateFromXMLNode__SWIG_0(swigCPtr, this, SWIGTYPE_p_SimTK__Xml__Element.getCPtr(aNode), versionNumber);
  }

  public void updateFromXMLNode(SWIGTYPE_p_SimTK__Xml__Element aNode) {
    opensimModelJNI.Muscle_updateFromXMLNode__SWIG_1(swigCPtr, this, SWIGTYPE_p_SimTK__Xml__Element.getCPtr(aNode));
  }

  public double getMaxIsometricForce() {
    return opensimModelJNI.Muscle_getMaxIsometricForce(swigCPtr, this);
  }

  public double getOptimalFiberLength() {
    return opensimModelJNI.Muscle_getOptimalFiberLength(swigCPtr, this);
  }

  public double getTendonSlackLength() {
    return opensimModelJNI.Muscle_getTendonSlackLength(swigCPtr, this);
  }

  public double getPennationAngleAtOptimalFiberLength() {
    return opensimModelJNI.Muscle_getPennationAngleAtOptimalFiberLength(swigCPtr, this);
  }

  public double getMaxContractionVelocity() {
    return opensimModelJNI.Muscle_getMaxContractionVelocity(swigCPtr, this);
  }

  public void setMaxIsometricForce(double aMaxIsometricForce) {
    opensimModelJNI.Muscle_setMaxIsometricForce(swigCPtr, this, aMaxIsometricForce);
  }

  public void setOptimalFiberLength(double aOptimalFiberLength) {
    opensimModelJNI.Muscle_setOptimalFiberLength(swigCPtr, this, aOptimalFiberLength);
  }

  public void setTendonSlackLength(double aTendonSlackLength) {
    opensimModelJNI.Muscle_setTendonSlackLength(swigCPtr, this, aTendonSlackLength);
  }

  public void setPennationAngleAtOptimalFiberLength(double aPennationAngle) {
    opensimModelJNI.Muscle_setPennationAngleAtOptimalFiberLength(swigCPtr, this, aPennationAngle);
  }

  public void setMaxContractionVelocity(double aMaxContractionVelocity) {
    opensimModelJNI.Muscle_setMaxContractionVelocity(swigCPtr, this, aMaxContractionVelocity);
  }

  public double getPennationAngle(SWIGTYPE_p_SimTK__State s) {
    return opensimModelJNI.Muscle_getPennationAngle(swigCPtr, this, SWIGTYPE_p_SimTK__State.getCPtr(s));
  }

  public double getTendonLength(SWIGTYPE_p_SimTK__State s) {
    return opensimModelJNI.Muscle_getTendonLength(swigCPtr, this, SWIGTYPE_p_SimTK__State.getCPtr(s));
  }

  public double getFiberLength(SWIGTYPE_p_SimTK__State s) {
    return opensimModelJNI.Muscle_getFiberLength(swigCPtr, this, SWIGTYPE_p_SimTK__State.getCPtr(s));
  }

  public double getNormalizedFiberLength(SWIGTYPE_p_SimTK__State s) {
    return opensimModelJNI.Muscle_getNormalizedFiberLength(swigCPtr, this, SWIGTYPE_p_SimTK__State.getCPtr(s));
  }

  public double getFiberLengthAlongTendon(SWIGTYPE_p_SimTK__State s) {
    return opensimModelJNI.Muscle_getFiberLengthAlongTendon(swigCPtr, this, SWIGTYPE_p_SimTK__State.getCPtr(s));
  }

  public double getFiberForce(SWIGTYPE_p_SimTK__State s) {
    return opensimModelJNI.Muscle_getFiberForce(swigCPtr, this, SWIGTYPE_p_SimTK__State.getCPtr(s));
  }

  public double getActiveFiberForce(SWIGTYPE_p_SimTK__State s) {
    return opensimModelJNI.Muscle_getActiveFiberForce(swigCPtr, this, SWIGTYPE_p_SimTK__State.getCPtr(s));
  }

  public double getPassiveFiberForce(SWIGTYPE_p_SimTK__State s) {
    return opensimModelJNI.Muscle_getPassiveFiberForce(swigCPtr, this, SWIGTYPE_p_SimTK__State.getCPtr(s));
  }

  public double getActiveFiberForceAlongTendon(SWIGTYPE_p_SimTK__State s) {
    return opensimModelJNI.Muscle_getActiveFiberForceAlongTendon(swigCPtr, this, SWIGTYPE_p_SimTK__State.getCPtr(s));
  }

  public double getPassiveFiberForceAlongTendon(SWIGTYPE_p_SimTK__State s) {
    return opensimModelJNI.Muscle_getPassiveFiberForceAlongTendon(swigCPtr, this, SWIGTYPE_p_SimTK__State.getCPtr(s));
  }

  public double getTendonForce(SWIGTYPE_p_SimTK__State s) {
    return opensimModelJNI.Muscle_getTendonForce(swigCPtr, this, SWIGTYPE_p_SimTK__State.getCPtr(s));
  }

  public double getActivation(SWIGTYPE_p_SimTK__State s) {
    return opensimModelJNI.Muscle_getActivation(swigCPtr, this, SWIGTYPE_p_SimTK__State.getCPtr(s));
  }

  public void setActivation(SWIGTYPE_p_SimTK__State s, double activation) {
    opensimModelJNI.Muscle_setActivation(swigCPtr, this, SWIGTYPE_p_SimTK__State.getCPtr(s), activation);
  }

  public double computeActuation(SWIGTYPE_p_SimTK__State s) {
    return opensimModelJNI.Muscle_computeActuation(swigCPtr, this, SWIGTYPE_p_SimTK__State.getCPtr(s));
  }

  public double computeIsometricForce(SWIGTYPE_p_SimTK__State s, double activation) {
    return opensimModelJNI.Muscle_computeIsometricForce(swigCPtr, this, SWIGTYPE_p_SimTK__State.getCPtr(s), activation);
  }

  public double evaluateForceLengthVelocityCurve(double aActivation, double aNormalizedLength, double aNormalizedVelocity) {
    return opensimModelJNI.Muscle_evaluateForceLengthVelocityCurve(swigCPtr, this, aActivation, aNormalizedLength, aNormalizedVelocity);
  }

  public double calcPennation(double aFiberLength, double aOptimalFiberLength, double aInitialPennationAngle) {
    return opensimModelJNI.Muscle_calcPennation(swigCPtr, this, aFiberLength, aOptimalFiberLength, aInitialPennationAngle);
  }

  public void equilibrate(SWIGTYPE_p_SimTK__State state) {
    opensimModelJNI.Muscle_equilibrate(swigCPtr, this, SWIGTYPE_p_SimTK__State.getCPtr(state));
  }

  public ArrayStr getRecordLabels() {
    return new ArrayStr(opensimModelJNI.Muscle_getRecordLabels(swigCPtr, this), true);
  }

  public ArrayDouble getRecordValues(SWIGTYPE_p_SimTK__State state) {
    return new ArrayDouble(opensimModelJNI.Muscle_getRecordValues(swigCPtr, this, SWIGTYPE_p_SimTK__State.getCPtr(state)), true);
  }

  public static boolean isKindOf(String type) {
    return opensimModelJNI.Muscle_isKindOf(type);
  }

  public boolean isA(String type) {
    return opensimModelJNI.Muscle_isA(swigCPtr, this, type);
  }

  public static Muscle safeDownCast(OpenSimObject obj) {
    long cPtr = opensimModelJNI.Muscle_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new Muscle(cPtr, false);
  }

  public void copy(OpenSimObject aObject) {
    opensimModelJNI.Muscle_copy__SWIG_1(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

}
