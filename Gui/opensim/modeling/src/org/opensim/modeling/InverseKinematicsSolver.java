/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.1.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 * Solve for the coordinates (degrees of freedom) of the model that satisfy the<br>
 * set of constraints imposed on the model and the set of desired coordinate<br>
 * values. The InverseKinematicsSolver provides the option to convert the<br>
 * problem to an approximate one where the constraint violations are treated as<br>
 * penalties to be minimized rather than strictly enforced. This can speed up<br>
 * the solution and can be used to seed the constrained problem closer to the<br>
 * solution.<br>
 * <br>
 * The InverseKinematicsSolver objective:<br>
 *  
  min: J = sum(Wm_i*(m_i-md_i)^T*(m_i-md_i)) + sum(Wq_j*(q_j-qd_j)^2) +
           [Wc*sum(c_{err})^2]
<br>
 * where m_i and md_i are the model and desired marker locations (Vec3); q_j<br>
 * and qd_j are model and desired joint coordinates. Wm_i and Wq_j are the<br>
 * marker and coordinate weightings, respectively, and Wc is the weighting on<br>
 * constraint errors. When Wc == Infinity, the second term is not included,<br>
 * but instead q is subject to the constraint equations:<br>
 *        c_{err} = G(q)-Go = 0 <br>
 * <br>
 * When the model (and the number of goals) is guaranteed not to change and<br>
 * the initial state is close to the InverseKinematics solution (e.g., from the <br>
 * initial assemble()), then track() is an efficient method for updating the<br>
 * configuration to determine the small change in coordinate values, q.<br>
 * <br>
 * See SimTK::Assembler for more algorithmic details of the underlying solver.<br>
 * <br>
 * @author Ajay Seth
 */
public class InverseKinematicsSolver extends AssemblySolver {
  private transient long swigCPtr;

  public InverseKinematicsSolver(long cPtr, boolean cMemoryOwn) {
    super(opensimSimulationJNI.InverseKinematicsSolver_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(InverseKinematicsSolver obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  public static long swigRelease(InverseKinematicsSolver obj) {
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
        opensimSimulationJNI.delete_InverseKinematicsSolver(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public InverseKinematicsSolver(Model model, SharedMarkersReference markersReference, SimTKArrayCoordinateReference coordinateReferences, double constraintWeight) {
    this(opensimSimulationJNI.new_InverseKinematicsSolver__SWIG_0(Model.getCPtr(model), model, SharedMarkersReference.getCPtr(markersReference), markersReference, SimTKArrayCoordinateReference.getCPtr(coordinateReferences), coordinateReferences, constraintWeight), true);
  }

  public InverseKinematicsSolver(Model model, SharedMarkersReference markersReference, SimTKArrayCoordinateReference coordinateReferences) {
    this(opensimSimulationJNI.new_InverseKinematicsSolver__SWIG_1(Model.getCPtr(model), model, SharedMarkersReference.getCPtr(markersReference), markersReference, SimTKArrayCoordinateReference.getCPtr(coordinateReferences), coordinateReferences), true);
  }

  public InverseKinematicsSolver(Model model, SharedMarkersReference markersReference, SharedOrientationsReference orientationsReference, SimTKArrayCoordinateReference coordinateReferences, double constraintWeight) {
    this(opensimSimulationJNI.new_InverseKinematicsSolver__SWIG_2(Model.getCPtr(model), model, SharedMarkersReference.getCPtr(markersReference), markersReference, SharedOrientationsReference.getCPtr(orientationsReference), orientationsReference, SimTKArrayCoordinateReference.getCPtr(coordinateReferences), coordinateReferences, constraintWeight), true);
  }

  public InverseKinematicsSolver(Model model, SharedMarkersReference markersReference, SharedOrientationsReference orientationsReference, SimTKArrayCoordinateReference coordinateReferences) {
    this(opensimSimulationJNI.new_InverseKinematicsSolver__SWIG_3(Model.getCPtr(model), model, SharedMarkersReference.getCPtr(markersReference), markersReference, SharedOrientationsReference.getCPtr(orientationsReference), orientationsReference, SimTKArrayCoordinateReference.getCPtr(coordinateReferences), coordinateReferences), true);
  }

  public InverseKinematicsSolver(Model model, MarkersReference markersReference, SimTKArrayCoordinateReference coordinateReferences, double constraintWeight) {
    this(opensimSimulationJNI.new_InverseKinematicsSolver__SWIG_4(Model.getCPtr(model), model, MarkersReference.getCPtr(markersReference), markersReference, SimTKArrayCoordinateReference.getCPtr(coordinateReferences), coordinateReferences, constraintWeight), true);
  }

  public InverseKinematicsSolver(Model model, MarkersReference markersReference, SimTKArrayCoordinateReference coordinateReferences) {
    this(opensimSimulationJNI.new_InverseKinematicsSolver__SWIG_5(Model.getCPtr(model), model, MarkersReference.getCPtr(markersReference), markersReference, SimTKArrayCoordinateReference.getCPtr(coordinateReferences), coordinateReferences), true);
  }

  public InverseKinematicsSolver(Model model, MarkersReference markersReference, OrientationsReference orientationsReference, SimTKArrayCoordinateReference coordinateReferences, double constraintWeight) {
    this(opensimSimulationJNI.new_InverseKinematicsSolver__SWIG_6(Model.getCPtr(model), model, MarkersReference.getCPtr(markersReference), markersReference, OrientationsReference.getCPtr(orientationsReference), orientationsReference, SimTKArrayCoordinateReference.getCPtr(coordinateReferences), coordinateReferences, constraintWeight), true);
  }

  public InverseKinematicsSolver(Model model, MarkersReference markersReference, OrientationsReference orientationsReference, SimTKArrayCoordinateReference coordinateReferences) {
    this(opensimSimulationJNI.new_InverseKinematicsSolver__SWIG_7(Model.getCPtr(model), model, MarkersReference.getCPtr(markersReference), markersReference, OrientationsReference.getCPtr(orientationsReference), orientationsReference, SimTKArrayCoordinateReference.getCPtr(coordinateReferences), coordinateReferences), true);
  }

  /**
   *  Return the number of markers used to solve for model coordinates.<br>
   *         It is a count of the number of markers in the intersection of <br>
   *         the reference markers and model markers.<br>
   *         This number is guaranteed not to change after assemble() is called<br>
   *         (i.e. during subsequent calls to track()).
   */
  public int getNumMarkersInUse() {
    return opensimSimulationJNI.InverseKinematicsSolver_getNumMarkersInUse(swigCPtr, this);
  }

  /**
   *  Return the number of orientation sensors used to solve for model<br>
   *     coordinates. It is a count of the number of orientation sensors that<br>
   *     intersect the reference orientations and model reference frames with<br>
   *     the same name. This number is guaranteed not to change after assemble()<br>
   *     is called (i.e. during subsequent calls to track()).
   */
  public int getNumOrientationSensorsInUse() {
    return opensimSimulationJNI.InverseKinematicsSolver_getNumOrientationSensorsInUse(swigCPtr, this);
  }

  /**
   *  Change the weighting of a marker, given the marker's name. Takes effect<br>
   *         when assemble() or track() is called next. 
   */
  public void updateMarkerWeight(String markerName, double value) {
    opensimSimulationJNI.InverseKinematicsSolver_updateMarkerWeight(swigCPtr, this, markerName, value);
  }

  /**
   *  Change the weighting of all markers. Takes effect when assemble() or<br>
   *         track() is called next. Marker weights are specified in the same order<br>
   *         as they appear in the MarkersReference that was passed in when the<br>
   *         solver was constructed. 
   */
  public void updateMarkerWeights(SimTKArrayDouble weights) {
    opensimSimulationJNI.InverseKinematicsSolver_updateMarkerWeights(swigCPtr, this, SimTKArrayDouble.getCPtr(weights), weights);
  }

  /**
   *  Change the weighting of an orientation sensor, given its name. Takes<br>
   *     effect when assemble() or track() is called next. 
   */
  public void updateOrientationWeight(String orientationName, double value) {
    opensimSimulationJNI.InverseKinematicsSolver_updateOrientationWeight(swigCPtr, this, orientationName, value);
  }

  /**
   *  Change the weighting of all orientation sensors. Takes effect when<br>
   *     assemble() or track() is called next. Orientation weights are specified<br>
   *     in the same order as they appear in the OrientationsReference that was<br>
   *     passed in when the solver was constructed. 
   */
  public void updateOrientationWeights(SimTKArrayDouble weights) {
    opensimSimulationJNI.InverseKinematicsSolver_updateOrientationWeights(swigCPtr, this, SimTKArrayDouble.getCPtr(weights), weights);
  }

  /**
   *  Compute and return a marker's spatial location in the ground frame,<br>
   *         given the marker's name. 
   */
  public Vec3 computeCurrentMarkerLocation(String markerName) {
    return new Vec3(opensimSimulationJNI.InverseKinematicsSolver_computeCurrentMarkerLocation(swigCPtr, this, markerName), true);
  }

  /**
   *  Compute and return the spatial locations of all markers, expressed in<br>
   *         the ground frame. 
   */
  public void computeCurrentMarkerLocations(SimTKArrayVec3 markerLocations) {
    opensimSimulationJNI.InverseKinematicsSolver_computeCurrentMarkerLocations(swigCPtr, this, SimTKArrayVec3.getCPtr(markerLocations), markerLocations);
  }

  /**
   *  Compute and return the distance error between a model marker and its<br>
   *         observation, given the marker's name. 
   */
  public double computeCurrentMarkerError(String markerName) {
    return opensimSimulationJNI.InverseKinematicsSolver_computeCurrentMarkerError(swigCPtr, this, markerName);
  }

  /**
   *  Compute and return the distance errors between all model markers and<br>
   *         their observations. 
   */
  public void computeCurrentMarkerErrors(SimTKArrayDouble markerErrors) {
    opensimSimulationJNI.InverseKinematicsSolver_computeCurrentMarkerErrors(swigCPtr, this, SimTKArrayDouble.getCPtr(markerErrors), markerErrors);
  }

  /**
   *  Compute and return the squared-distance error between a model marker and<br>
   *         its observation, given the marker's name. This method is cheaper than<br>
   *         squaring the value returned by computeCurrentMarkerError(). 
   */
  public double computeCurrentSquaredMarkerError(String markerName) {
    return opensimSimulationJNI.InverseKinematicsSolver_computeCurrentSquaredMarkerError(swigCPtr, this, markerName);
  }

  /**
   *  Compute and return the squared-distance errors between all model markers<br>
   *         and their observations. This method is cheaper than squaring the values<br>
   *         returned by computeCurrentMarkerErrors(). 
   */
  public void computeCurrentSquaredMarkerErrors(SimTKArrayDouble markerErrors) {
    opensimSimulationJNI.InverseKinematicsSolver_computeCurrentSquaredMarkerErrors(swigCPtr, this, SimTKArrayDouble.getCPtr(markerErrors), markerErrors);
  }

  /**
   *  Marker locations and errors may be computed in an order that is different<br>
   *         from tasks file or listed in the model. Return the corresponding marker<br>
   *         name for an index in the list of marker locations/errors returned by the<br>
   *         solver. 
   */
  public String getMarkerNameForIndex(int markerIndex) {
    return opensimSimulationJNI.InverseKinematicsSolver_getMarkerNameForIndex(swigCPtr, this, markerIndex);
  }

  /**
   *  Compute and return an orientation sensor's spatial orientation in the<br>
   *     ground frame, given the o-sensor's name. 
   */
  public SWIGTYPE_p_SimTK__Rotation_T_SimTK__Real_t computeCurrentSensorOrientation(String osensorName) {
    return new SWIGTYPE_p_SimTK__Rotation_T_SimTK__Real_t(opensimSimulationJNI.InverseKinematicsSolver_computeCurrentSensorOrientation(swigCPtr, this, osensorName), true);
  }

  /**
   *  Compute and return the spatial orientations of all o-sensors, expressed in<br>
   *     the ground frame. 
   */
  public void computeCurrentSensorOrientations(SWIGTYPE_p_SimTK__Array_T_SimTK__Rotation_T_SimTK__Real_t_unsigned_int_t osensorOrientations) {
    opensimSimulationJNI.InverseKinematicsSolver_computeCurrentSensorOrientations(swigCPtr, this, SWIGTYPE_p_SimTK__Array_T_SimTK__Rotation_T_SimTK__Real_t_unsigned_int_t.getCPtr(osensorOrientations));
  }

  /**
   *  Compute and return the orientation error between the model orientation<br>
   *     sensor and its observation, given the o-sensor's name. 
   */
  public double computeCurrentOrientationError(String osensorName) {
    return opensimSimulationJNI.InverseKinematicsSolver_computeCurrentOrientationError(swigCPtr, this, osensorName);
  }

  /**
   *  Compute all the orientation errors between the model orientation<br>
   *     sensors and their observations. 
   */
  public void computeCurrentOrientationErrors(SimTKArrayDouble osensorErrors) {
    opensimSimulationJNI.InverseKinematicsSolver_computeCurrentOrientationErrors(swigCPtr, this, SimTKArrayDouble.getCPtr(osensorErrors), osensorErrors);
  }

  /**
   *  Orientation sensor locations and errors may be computed in an order that<br>
   *     may be different from tasks file or listed in the model. Return the <br>
   *     corresponding orientation sensor name for an index in the list of<br>
   *     orientations returned by the solver. 
   */
  public String getOrientationSensorNameForIndex(int osensorIndex) {
    return opensimSimulationJNI.InverseKinematicsSolver_getOrientationSensorNameForIndex(swigCPtr, this, osensorIndex);
  }

  /**
   *  indicate whether time is provided by Reference objects or driver program 
   */
  public void setAdvanceTimeFromReference(boolean newValue) {
    opensimSimulationJNI.InverseKinematicsSolver_setAdvanceTimeFromReference(swigCPtr, this, newValue);
  }

}
