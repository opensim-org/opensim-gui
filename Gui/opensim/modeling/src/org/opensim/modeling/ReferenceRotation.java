/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 * This base (abstract) class defines the interface for a Reference signals to<br>
 * be achieved/tracked via optimization and/or tracking controller. Combines<br>
 * weightings that identifies the relative importance of achieving one<br>
 * Reference value relative to the others. The specific value type is defined<br>
 * by the concrete References. For example, a MarkerRefrence is of type Vec3,<br>
 * for the 3D location coordinates of a marker. Correspondence with model<br>
 * values are established via the Reference names.<br>
 * <br>
 * @author Ajay Seth
 */
public class ReferenceRotation extends OpenSimObject {
  private transient long swigCPtr;

  public ReferenceRotation(long cPtr, boolean cMemoryOwn) {
    super(opensimSimulationJNI.ReferenceRotation_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(ReferenceRotation obj) {
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
        opensimSimulationJNI.delete_ReferenceRotation(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static ReferenceRotation safeDownCast(OpenSimObject obj) {
    long cPtr = opensimSimulationJNI.ReferenceRotation_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new ReferenceRotation(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimSimulationJNI.ReferenceRotation_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimSimulationJNI.ReferenceRotation_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimSimulationJNI.ReferenceRotation_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new ReferenceRotation(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimSimulationJNI.ReferenceRotation_getConcreteClassName(swigCPtr, this);
  }

  /**
   *  get the number of referettes (individual signals) in this Reference. All<br>
   *         return arrays are guaranteed to be this length 
   */
  public int getNumRefs() {
    return opensimSimulationJNI.ReferenceRotation_getNumRefs(swigCPtr, this);
  }

  /**
   *  get the time range for which the Reference is valid, which can and will<br>
   *         be finite if the reference encapsulates experimental data. By default<br>
   *         they are infinite 
   */
  public Vec2 getValidTimeRange() {
    return new Vec2(opensimSimulationJNI.ReferenceRotation_getValidTimeRange(swigCPtr, this), true);
  }

  /**
   *  get the name(s) of the reference or its referettes 
   */
  public SimTKArrayString getNames() {
    return new SimTKArrayString(opensimSimulationJNI.ReferenceRotation_getNames(swigCPtr, this), false);
  }

  /**
   *  get the weighting (importance) of meeting this Reference 
   */
  public void getWeights(State s, SimTKArrayDouble weights) {
    opensimSimulationJNI.ReferenceRotation_getWeights__SWIG_0(swigCPtr, this, State.getCPtr(s), s, SimTKArrayDouble.getCPtr(weights), weights);
  }

  /**
   *  Indicate whether this Reference can provide discretized data or not 
   */
  public boolean hasNext() {
    return opensimSimulationJNI.ReferenceRotation_hasNext(swigCPtr, this);
  }

  public SimTKArrayDouble getWeights(State s) {
    return new SimTKArrayDouble(opensimSimulationJNI.ReferenceRotation_getWeights__SWIG_1(swigCPtr, this, State.getCPtr(s), s), true);
  }

  /**
   *  get the values of the Reference signals as a function<br>
   *     of the passed in time 
   */
  public void getValuesAtTime(double time, SimTKArrayRotation values) {
    opensimSimulationJNI.ReferenceRotation_getValuesAtTime(swigCPtr, this, time, SimTKArrayRotation.getCPtr(values), values);
  }

  public SimTKArrayRotation getValues(double time) {
    return new SimTKArrayRotation(opensimSimulationJNI.ReferenceRotation_getValues(swigCPtr, this, time), true);
  }

}
