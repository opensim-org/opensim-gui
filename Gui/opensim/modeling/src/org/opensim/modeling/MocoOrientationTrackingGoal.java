/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 *  The squared difference between a model frame's orientation and a reference<br>
 * orientation value, summed over the frames for which a reference is provided,<br>
 * and integrated over the phase. This can be used to track orientation<br>
 * quantities in the model that don't correspond to model degrees of freedom.<br>
 * The reference can be provided as a trajectory of SimTK::Quaternion%s<br>
 * representing the orientation reference data, or as a states trajectory from<br>
 * which the tracked rotation reference is computed. Both rotation and states<br>
 * references can be provided as a file name to a STO or CSV file (or other<br>
 * file types for which there is a FileAdapter), or programmatically as a<br>
 * TimeSeriesTable_&lt;SimTK::Quaternion&gt; (for the rotation reference) or as a<br>
 * scalar TimeSeriesTable (for the states reference).<br>
 * <br>
 * This cost requires realization to SimTK::Stage::Position. The cost is<br>
 * computed by creating a SimTK::Rotation between the model frame and the<br>
 * reference data, and then converting the rotation to an angle-axis<br>
 * representation and minimizing the angle value. The angle value is<br>
 * equivalent to the orientation error between the model frame and the<br>
 * reference data, so we only need to minimize this single scalar value per<br>
 * tracked frame, compared to other more complicated approaches which could<br>
 * require multiple minimized error values (e.g. Euler angle errors, etc).<br>
 * <br>
 * 
 */
public class MocoOrientationTrackingGoal extends MocoGoal {
  private transient long swigCPtr;

  public MocoOrientationTrackingGoal(long cPtr, boolean cMemoryOwn) {
    super(opensimMocoJNI.MocoOrientationTrackingGoal_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(MocoOrientationTrackingGoal obj) {
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
        opensimMocoJNI.delete_MocoOrientationTrackingGoal(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static MocoOrientationTrackingGoal safeDownCast(OpenSimObject obj) {
    long cPtr = opensimMocoJNI.MocoOrientationTrackingGoal_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new MocoOrientationTrackingGoal(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimMocoJNI.MocoOrientationTrackingGoal_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimMocoJNI.MocoOrientationTrackingGoal_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimMocoJNI.MocoOrientationTrackingGoal_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new MocoOrientationTrackingGoal(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimMocoJNI.MocoOrientationTrackingGoal_getConcreteClassName(swigCPtr, this);
  }

  public MocoOrientationTrackingGoal() {
    this(opensimMocoJNI.new_MocoOrientationTrackingGoal__SWIG_0(), true);
  }

  public MocoOrientationTrackingGoal(String name) {
    this(opensimMocoJNI.new_MocoOrientationTrackingGoal__SWIG_1(name), true);
  }

  public MocoOrientationTrackingGoal(String name, double weight) {
    this(opensimMocoJNI.new_MocoOrientationTrackingGoal__SWIG_2(name, weight), true);
  }

  /**
   *  Set the rotations of individual frames in ground to be tracked<br>
   *     in the cost. The column labels of the provided reference must<br>
   *     be paths to frames in the model, e.g. `/bodyset/torso`. If the<br>
   *     frame_paths property is empty, all frames with data in this reference<br>
   *     will be tracked. Otherwise, only the frames specified via<br>
   *     setFramePaths() will be tracked. Calling this function clears the values<br>
   *     provided via setStatesReference(), setRotationReference(), or the<br>
   *     `states_reference_file` property, if any. 
   */
  public void setRotationReferenceFile(String filepath) {
    opensimMocoJNI.MocoOrientationTrackingGoal_setRotationReferenceFile(swigCPtr, this, filepath);
  }

  /**
   *  Each column label must be the path of a valid frame path (see<br>
   *     setRotationReferenceFile()). Calling this function clears the<br>
   *     `states_reference_file` and `rotation_reference_file` properties or the<br>
   *     table provided via setStatesReference(), if any. 
   */
  public void setRotationReference(TimeSeriesTableRotation ref) {
    opensimMocoJNI.MocoOrientationTrackingGoal_setRotationReference__SWIG_0(swigCPtr, this, TimeSeriesTableRotation.getCPtr(ref), ref);
  }

  /**
   *   TimeSeriesTable_&lt;SimTK::Rotation&gt;&amp; ref) 
   */
  public void setRotationReference(TimeSeriesTableQuaternion ref) {
    opensimMocoJNI.MocoOrientationTrackingGoal_setRotationReference__SWIG_1(swigCPtr, this, TimeSeriesTableQuaternion.getCPtr(ref), ref);
  }

  /**
   *  Provide a table containing values of model state variables. These data<br>
   *     are used to create a StatesTrajectory internally, from which the<br>
   *     rotation data for the frames specified in setFramePaths() are computed.<br>
   *     Each column label in the reference must be the path of a state variable,<br>
   *     e.g., `/jointset/ankle_angle_r/value`. Calling this function clears the<br>
   *     table provided via setRotationReference(), or the<br>
   *     `rotation_reference_file` property, if any. The table is not loaded<br>
   *     until the MocoProblem is initialized. 
   */
  public void setStatesReference(TableProcessor ref) {
    opensimMocoJNI.MocoOrientationTrackingGoal_setStatesReference(swigCPtr, this, TableProcessor.getCPtr(ref), ref);
  }

  /**
   *  Set the paths to frames in the model that this cost term will track. The<br>
   *     names set here must correspond to OpenSim::Component%s that derive from<br>
   *     OpenSim::Frame, which includes SimTK::Rotation as an output.<br>
   *     Replaces the frame path set if it already exists. 
   */
  public void setFramePaths(StdVectorString paths) {
    opensimMocoJNI.MocoOrientationTrackingGoal_setFramePaths(swigCPtr, this, StdVectorString.getCPtr(paths), paths);
  }

  /**
   *  Set the weight for an individual frame's rotation tracking. If a weight<br>
   *     is already set for the requested frame, then the provided weight<br>
   *     replaces the previous weight. An exception is thrown if a weight<br>
   *     for an unknown frame is provided. 
   */
  public void setWeightForFrame(String frameName, double weight) {
    opensimMocoJNI.MocoOrientationTrackingGoal_setWeightForFrame(swigCPtr, this, frameName, weight);
  }

  /**
   *  Provide a MocoWeightSet to weight frame rotation tracking in the cost.<br>
   *     Replaces the weight set if it already exists. 
   */
  public void setWeightSet(MocoWeightSet weightSet) {
    opensimMocoJNI.MocoOrientationTrackingGoal_setWeightSet(swigCPtr, this, MocoWeightSet.getCPtr(weightSet), weightSet);
  }

  /**
   *  If no states reference has been provided, this returns an empty<br>
   *     processor. 
   */
  public TableProcessor getStatesReference() {
    return new TableProcessor(opensimMocoJNI.MocoOrientationTrackingGoal_getStatesReference(swigCPtr, this), false);
  }

  /**
   *  If no rotation reference file has been provided, this returns an empty<br>
   *     string. 
   */
  public String getRotationReferenceFile() {
    return opensimMocoJNI.MocoOrientationTrackingGoal_getRotationReferenceFile(swigCPtr, this);
  }

}
