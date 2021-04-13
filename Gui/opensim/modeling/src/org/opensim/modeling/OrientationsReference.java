/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 * Reference values for the Orientations of model frames that will be used to<br>
 * to compute tracking errors. An Orientation is specified by a Rotation<br>
 * matrix describing the frame orientation with respect to Ground. The <br>
 * reference also contains weightings that identifies the relative importance<br>
 * of achieving one orientation's reference value over another.<br>
 * <br>
 * @author Ajay Seth
 */
public class OrientationsReference extends StreamableReferenceRotation {
  private transient long swigCPtr;

  public OrientationsReference(long cPtr, boolean cMemoryOwn) {
    super(opensimSimulationJNI.OrientationsReference_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(OrientationsReference obj) {
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
        opensimSimulationJNI.delete_OrientationsReference(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static OrientationsReference safeDownCast(OpenSimObject obj) {
    long cPtr = opensimSimulationJNI.OrientationsReference_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new OrientationsReference(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimSimulationJNI.OrientationsReference_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimSimulationJNI.OrientationsReference_getClassName();
  }

  public OpenSimObject unused_clone() {
    long cPtr = opensimSimulationJNI.OrientationsReference_unused_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new OrientationsReference(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimSimulationJNI.OrientationsReference_getConcreteClassName(swigCPtr, this);
  }

  public void copyProperty_orientation_file(OrientationsReference source) {
    opensimSimulationJNI.OrientationsReference_copyProperty_orientation_file(swigCPtr, this, OrientationsReference.getCPtr(source), source);
  }

  public String get_orientation_file(int i) {
    return opensimSimulationJNI.OrientationsReference_get_orientation_file__SWIG_0(swigCPtr, this, i);
  }

  public SWIGTYPE_p_std__string upd_orientation_file(int i) {
    return new SWIGTYPE_p_std__string(opensimSimulationJNI.OrientationsReference_upd_orientation_file__SWIG_0(swigCPtr, this, i), false);
  }

  public void set_orientation_file(int i, String value) {
    opensimSimulationJNI.OrientationsReference_set_orientation_file__SWIG_0(swigCPtr, this, i, value);
  }

  public int append_orientation_file(String value) {
    return opensimSimulationJNI.OrientationsReference_append_orientation_file(swigCPtr, this, value);
  }

  public void constructProperty_orientation_file(String initValue) {
    opensimSimulationJNI.OrientationsReference_constructProperty_orientation_file(swigCPtr, this, initValue);
  }

  public String get_orientation_file() {
    return opensimSimulationJNI.OrientationsReference_get_orientation_file__SWIG_1(swigCPtr, this);
  }

  public SWIGTYPE_p_std__string upd_orientation_file() {
    return new SWIGTYPE_p_std__string(opensimSimulationJNI.OrientationsReference_upd_orientation_file__SWIG_1(swigCPtr, this), false);
  }

  public void set_orientation_file(String value) {
    opensimSimulationJNI.OrientationsReference_set_orientation_file__SWIG_1(swigCPtr, this, value);
  }

  public void copyProperty_orientation_weights(OrientationsReference source) {
    opensimSimulationJNI.OrientationsReference_copyProperty_orientation_weights(swigCPtr, this, OrientationsReference.getCPtr(source), source);
  }

  public SetOientationWeights get_orientation_weights(int i) {
    return new SetOientationWeights(opensimSimulationJNI.OrientationsReference_get_orientation_weights__SWIG_0(swigCPtr, this, i), false);
  }

  public SetOientationWeights upd_orientation_weights(int i) {
    return new SetOientationWeights(opensimSimulationJNI.OrientationsReference_upd_orientation_weights__SWIG_0(swigCPtr, this, i), false);
  }

  public void set_orientation_weights(int i, SetOientationWeights value) {
    opensimSimulationJNI.OrientationsReference_set_orientation_weights__SWIG_0(swigCPtr, this, i, SetOientationWeights.getCPtr(value), value);
  }

  public int append_orientation_weights(SetOientationWeights value) {
    return opensimSimulationJNI.OrientationsReference_append_orientation_weights(swigCPtr, this, SetOientationWeights.getCPtr(value), value);
  }

  public void constructProperty_orientation_weights(SetOientationWeights initValue) {
    opensimSimulationJNI.OrientationsReference_constructProperty_orientation_weights(swigCPtr, this, SetOientationWeights.getCPtr(initValue), initValue);
  }

  public SetOientationWeights get_orientation_weights() {
    return new SetOientationWeights(opensimSimulationJNI.OrientationsReference_get_orientation_weights__SWIG_1(swigCPtr, this), false);
  }

  public SetOientationWeights upd_orientation_weights() {
    return new SetOientationWeights(opensimSimulationJNI.OrientationsReference_upd_orientation_weights__SWIG_1(swigCPtr, this), false);
  }

  public void set_orientation_weights(SetOientationWeights value) {
    opensimSimulationJNI.OrientationsReference_set_orientation_weights__SWIG_1(swigCPtr, this, SetOientationWeights.getCPtr(value), value);
  }

  public void copyProperty_default_weight(OrientationsReference source) {
    opensimSimulationJNI.OrientationsReference_copyProperty_default_weight(swigCPtr, this, OrientationsReference.getCPtr(source), source);
  }

  public double get_default_weight(int i) {
    return opensimSimulationJNI.OrientationsReference_get_default_weight__SWIG_0(swigCPtr, this, i);
  }

  public SWIGTYPE_p_double upd_default_weight(int i) {
    return new SWIGTYPE_p_double(opensimSimulationJNI.OrientationsReference_upd_default_weight__SWIG_0(swigCPtr, this, i), false);
  }

  public void set_default_weight(int i, double value) {
    opensimSimulationJNI.OrientationsReference_set_default_weight__SWIG_0(swigCPtr, this, i, value);
  }

  public int append_default_weight(double value) {
    return opensimSimulationJNI.OrientationsReference_append_default_weight(swigCPtr, this, value);
  }

  public void constructProperty_default_weight(double initValue) {
    opensimSimulationJNI.OrientationsReference_constructProperty_default_weight(swigCPtr, this, initValue);
  }

  public double get_default_weight() {
    return opensimSimulationJNI.OrientationsReference_get_default_weight__SWIG_1(swigCPtr, this);
  }

  public SWIGTYPE_p_double upd_default_weight() {
    return new SWIGTYPE_p_double(opensimSimulationJNI.OrientationsReference_upd_default_weight__SWIG_1(swigCPtr, this), false);
  }

  public void set_default_weight(double value) {
    opensimSimulationJNI.OrientationsReference_set_default_weight__SWIG_1(swigCPtr, this, value);
  }

  public OrientationsReference() {
    this(opensimSimulationJNI.new_OrientationsReference__SWIG_0(), true);
  }

  /**
   *  Convenience load Orientations data from a file in the form of XYZ <br>
   *         body-fixed Euler angles. Units default to Radians.
   */
  public OrientationsReference(String orientationFileName, Units modelUnits) {
    this(opensimSimulationJNI.new_OrientationsReference__SWIG_1(orientationFileName, Units.getCPtr(modelUnits), modelUnits), true);
  }

  /**
   *  Convenience load Orientations data from a file in the form of XYZ <br>
   *         body-fixed Euler angles. Units default to Radians.
   */
  public OrientationsReference(String orientationFileName) {
    this(opensimSimulationJNI.new_OrientationsReference__SWIG_2(orientationFileName), true);
  }

  /**
   *  Form a Reference from TimeSeriesTable of Rotations and corresponding<br>
   *     orientation weights. The input orientatonWeightSet is used to initialize<br>
   *     Reference weightings for individual Orientations. Weights are associated<br>
   *     to Orientations by name.
   */
  public OrientationsReference(TimeSeriesTableRotation orientationData, SetOientationWeights orientationWeightSet) {
    this(opensimSimulationJNI.new_OrientationsReference__SWIG_3(TimeSeriesTableRotation.getCPtr(orientationData), orientationData, SetOientationWeights.getCPtr(orientationWeightSet), orientationWeightSet), true);
  }

  /**
   *  Form a Reference from TimeSeriesTable of Rotations and corresponding<br>
   *     orientation weights. The input orientatonWeightSet is used to initialize<br>
   *     Reference weightings for individual Orientations. Weights are associated<br>
   *     to Orientations by name.
   */
  public OrientationsReference(TimeSeriesTableRotation orientationData) {
    this(opensimSimulationJNI.new_OrientationsReference__SWIG_4(TimeSeriesTableRotation.getCPtr(orientationData), orientationData), true);
  }

  /**
   *  load the orientation data for this OrientationsReference from a file<br>
   *     containing Euler-angles in body-fixed XYZ order.
   */
  public void loadOrientationsEulerAnglesFile(String eulerAnglesXYZ, Units modelUnits) {
    opensimSimulationJNI.OrientationsReference_loadOrientationsEulerAnglesFile__SWIG_0(swigCPtr, this, eulerAnglesXYZ, Units.getCPtr(modelUnits), modelUnits);
  }

  /**
   *  load the orientation data for this OrientationsReference from a file<br>
   *     containing Euler-angles in body-fixed XYZ order.
   */
  public void loadOrientationsEulerAnglesFile(String eulerAnglesXYZ) {
    opensimSimulationJNI.OrientationsReference_loadOrientationsEulerAnglesFile__SWIG_1(swigCPtr, this, eulerAnglesXYZ);
  }

  public int getNumRefs() {
    return opensimSimulationJNI.OrientationsReference_getNumRefs(swigCPtr, this);
  }

  /**
   *  get the time range for which the OrientationsReference values are valid,<br>
   *         based on the loaded orientation data.
   */
  public Vec2 getValidTimeRange() {
    return new Vec2(opensimSimulationJNI.OrientationsReference_getValidTimeRange(swigCPtr, this), true);
  }

  /**
   *  get the times at which the OrientationsReference values are specified,<br>
   *         based on the loaded orientation data.
   */
  public StdVectorDouble getTimes() {
    return new StdVectorDouble(opensimSimulationJNI.OrientationsReference_getTimes(swigCPtr, this), false);
  }

  /**
   *  get the names of the Orientations serving as references 
   */
  public SimTKArrayString getNames() {
    return new SimTKArrayString(opensimSimulationJNI.OrientationsReference_getNames(swigCPtr, this), false);
  }

  /**
   *  get the value of the OrientationsReference 
   */
  public void getValuesAtTime(double time, SimTKArrayRotation values) {
    opensimSimulationJNI.OrientationsReference_getValuesAtTime(swigCPtr, this, time, SimTKArrayRotation.getCPtr(values), values);
  }

  /**
   *  Default implementation does not support streaming 
   */
  public void getNextValuesAndTime(SWIGTYPE_p_double time, SimTKArrayRotation values) {
    opensimSimulationJNI.OrientationsReference_getNextValuesAndTime(swigCPtr, this, SWIGTYPE_p_double.getCPtr(time), SimTKArrayRotation.getCPtr(values), values);
  }

  public boolean hasNext() {
    return opensimSimulationJNI.OrientationsReference_hasNext(swigCPtr, this);
  }

  /**
   *  get the weighting (importance) of meeting this OrientationsReference in the<br>
   *         same order as names
   */
  public void getWeights(State s, SimTKArrayDouble weights) {
    opensimSimulationJNI.OrientationsReference_getWeights(swigCPtr, this, State.getCPtr(s), s, SimTKArrayDouble.getCPtr(weights), weights);
  }

  public double getSamplingFrequency() {
    return opensimSimulationJNI.OrientationsReference_getSamplingFrequency(swigCPtr, this);
  }

  public SetOientationWeights updOrientationWeightSet() {
    return new SetOientationWeights(opensimSimulationJNI.OrientationsReference_updOrientationWeightSet(swigCPtr, this), false);
  }

  /**
   *  %Set the orientation weights from a set of OrientationWeights, which is<br>
   *     const and a copy of the Set is used internally. Therefore, subsequent changes<br>
   *     to the Set of OrientationWeights will have no effect on the orientation weights<br>
   *     associated with this Reference. You can, however, change the weightings on the<br>
   *     InverseKinematicsSolver prior to solving at any instant in time. 
   */
  public void setOrientationWeightSet(SetOientationWeights orientationWeights) {
    opensimSimulationJNI.OrientationsReference_setOrientationWeightSet(swigCPtr, this, SetOientationWeights.getCPtr(orientationWeights), orientationWeights);
  }

  public void setDefaultWeight(double weight) {
    opensimSimulationJNI.OrientationsReference_setDefaultWeight(swigCPtr, this, weight);
  }

}
