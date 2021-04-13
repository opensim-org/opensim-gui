/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class StreamableReferenceRotation extends ReferenceRotation {
  private transient long swigCPtr;

  public StreamableReferenceRotation(long cPtr, boolean cMemoryOwn) {
    super(opensimSimulationJNI.StreamableReferenceRotation_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(StreamableReferenceRotation obj) {
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
        opensimSimulationJNI.delete_StreamableReferenceRotation(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static StreamableReferenceRotation safeDownCast(OpenSimObject obj) {
    long cPtr = opensimSimulationJNI.StreamableReferenceRotation_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new StreamableReferenceRotation(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimSimulationJNI.StreamableReferenceRotation_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimSimulationJNI.StreamableReferenceRotation_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimSimulationJNI.StreamableReferenceRotation_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new StreamableReferenceRotation(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimSimulationJNI.StreamableReferenceRotation_getConcreteClassName(swigCPtr, this);
  }

  public void getNextValuesAndTime(SWIGTYPE_p_double time, SimTKArrayRotation values) {
    opensimSimulationJNI.StreamableReferenceRotation_getNextValuesAndTime(swigCPtr, this, SWIGTYPE_p_double.getCPtr(time), SimTKArrayRotation.getCPtr(values), values);
  }

  public boolean hasNext() {
    return opensimSimulationJNI.StreamableReferenceRotation_hasNext(swigCPtr, this);
  }

}
