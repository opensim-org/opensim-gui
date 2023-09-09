/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 *     <br>
 *     Minimize the error between compliant contact force impulse from the model and<br>
 *     experimentally measured contact impulse for a single axis.<br>
 *     This class handles the contact impulses from a single contact group and a <br>
 *     single experimental external loads file. Tracking ground reaction impulses for<br>
 *     the left and right feet in gait requires separate instances of this goal.<br>
 *     Tracking ground reaction impulses for multiple axes requires separate <br>
 *     instances of this goal.<br>
 *     Note: The only contact element supported is SmoothSphereHalfSpaceForce.<br>
 *     Note: This goal does not include torques or centers of pressure.<br>
 *     This goal is computed as follows:<br>
 *      
    \frac{1}{mg} (\int_{t_i}^{t_f}
                 \vec{F}_{m,a} - \vec{F}_{e,a} ~dt)^2
    <br>
 *     We use the following notation:<br>
 *     -   t_i : the initial time of this phase.<br>
 *     -   t_f : the final time of this phase.<br>
 *     -   mg : the total weight of the system; replaced with<br>
 *           m  if   g = 0 .<br>
 *     -   a : the impulse axis to be tracked.<br>
 * <br>
 *     The impulse_axis property, a = {0, 1, 2}, specifies which component<br>
 *     of the contact impulse should be tracked.<br>
 * <br>
 *     ## Usage<br>
 *     To use this goal, specify the following:<br>
 *     - a single ExternalLoads file or object, which is a set of ExternalForces.<br>
 *     - a single contact group, which contains the names of an contact forces. <br>
 *     ### Configuring the ExternalLoads<br>
 *     The ExternalLoads class is the standard way to provide experimental contact<br>
 *     forces in OpenSim. This class is a set of ExternalForce objects. For gait,<br>
 *     typically the ExternalLoads contains 2 ExternalForces, one for each foot.<br>
 *     This goal uses the following information from ExternalLoads:<br>
 *     - **data_file**: This scalar file contains all experimental force data with<br>
 *       columns named according to each ExternalForce's force_identifier.<br>
 *     This goal uses the following information from each ExternalForce:<br>
 *     - **name**: We use the name of the ExternalForce to associate it with a<br>
 *       contact force group.<br>
 *     - **applied_on_body**: All contact forces in the group with which this<br>
 *       ExternalForce is associated must use this body as either the<br>
 *       sphere_frame's base frame or the half_space_frame's base frame.<br>
 *     - **force_expressed_in_body**: We use this to re-express the experimental<br>
 *       force in ground. This is either the absolute path to a PhysicalFrame in<br>
 *       the model, or the name of a Body in the model's BodySet.<br>
 *     - **force_identifier**: The ExternalLoads data_file must include the 3<br>
 *       columns<br>
 *       `&lt;force_identifier&gt;x`, `&lt;force_identifier&gt;y`, `&lt;force_identifier&gt;z`.<br>
 *     All other properties of ExternalLoads and ExternalForce are ignored by this<br>
 *     goal. This means that experimental forces are processed differently by this<br>
 *     goal than by other OpenSim tools such as Inverse Dynamics, Computed Muscle<br>
 *     Control, and Forward.<br>
 *     Note: The ExternalLoads used by this goal is separate from the model. Using<br>
 *     this goal implies that the model contains compliant contact forces, so<br>
 *     adding ExternalLoads to the model would be redundant. This class uses the<br>
 *     ExternalLoads *only* for computing the contact impulse error, not for <br>
 *     applying forces to the model.<br>
 *     ### Scale factors<br>
 *     Add a MocoParameter to the problem that will scale the tracking reference<br>
 *     data associated with a contact force group. Scale factors are applied<br>
 *     to the tracking error calculations based on the following equation:<br>
 *          error = modelValue - scaleFactor * referenceValue<br>
 *     In other words, the scale factor is applied when computing the contact<br>
 *     impulse tracking error for each contact force group, not to the reference <br>
 *     data directly. You must specify both the external force name associated <br>
 *     with the contact force group and the index corresponding to the direction<br>
 *     (i.e., X = 0, Y = 1, Z = 2) of the scaled force value. The direction is <br>
 *     applied in whatever frame the reference data is expressed in based on the <br>
 *     provided ExternalLoads in each contact group.<br>
 *     Adding a scale factor to a MocoContactImpulseTrackingGoal.<br>
 *     {@code 
    auto* contactImpulseTrackingGoal = problem.addGoal<MocoContactImpulseTrackingGoal>();
    ...
    contactImpulseTrackingGoal->addScaleFactor(
            'RightGRF_vertical_scale_factor', 'Right_GRF', 1, {0.5, 2.0});
    }<br>
 *     
 */
public class MocoContactImpulseTrackingGoal extends MocoGoal {
  private transient long swigCPtr;

  public MocoContactImpulseTrackingGoal(long cPtr, boolean cMemoryOwn) {
    super(opensimMocoJNI.MocoContactImpulseTrackingGoal_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(MocoContactImpulseTrackingGoal obj) {
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
        opensimMocoJNI.delete_MocoContactImpulseTrackingGoal(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static MocoContactImpulseTrackingGoal safeDownCast(OpenSimObject obj) {
    long cPtr = opensimMocoJNI.MocoContactImpulseTrackingGoal_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new MocoContactImpulseTrackingGoal(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimMocoJNI.MocoContactImpulseTrackingGoal_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimMocoJNI.MocoContactImpulseTrackingGoal_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimMocoJNI.MocoContactImpulseTrackingGoal_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new MocoContactImpulseTrackingGoal(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimMocoJNI.MocoContactImpulseTrackingGoal_getConcreteClassName(swigCPtr, this);
  }

  public MocoContactImpulseTrackingGoal() {
    this(opensimMocoJNI.new_MocoContactImpulseTrackingGoal__SWIG_0(), true);
  }

  public MocoContactImpulseTrackingGoal(String name) {
    this(opensimMocoJNI.new_MocoContactImpulseTrackingGoal__SWIG_1(name), true);
  }

  public MocoContactImpulseTrackingGoal(String name, double weight) {
    this(opensimMocoJNI.new_MocoContactImpulseTrackingGoal__SWIG_2(name, weight), true);
  }

  /**
   *  Set the ExternalLoads as an XML file. This clears the ExternalLoads<br>
   *  provided as an object, if one exists.
   */
  public void setExternalLoadsFile(String extLoadsFile) {
    opensimMocoJNI.MocoContactImpulseTrackingGoal_setExternalLoadsFile(swigCPtr, this, extLoadsFile);
  }

  /**
   *  Set the ExternalLoads as an object. This clears the ExternalLoads<br>
   *  XML file, if provided.
   */
  public void setExternalLoads(ExternalLoads extLoads) {
    opensimMocoJNI.MocoContactImpulseTrackingGoal_setExternalLoads(swigCPtr, this, ExternalLoads.getCPtr(extLoads), extLoads);
  }

  /**
   *  Add a single group of contact forces whose sum should track the ground <br>
   *  reaction force impulse data from a single axis of a single ExternalForce. <br>
   *  The externalForceName should be the name of an ExternalForce object<br>
   *  in the ExternalLoads.
   */
  public void addContactGroup(StdVectorString contactForcePaths, String externalForceName) {
    opensimMocoJNI.MocoContactImpulseTrackingGoal_addContactGroup__SWIG_0(swigCPtr, this, StdVectorString.getCPtr(contactForcePaths), contactForcePaths, externalForceName);
  }

  /**
   *  Add a single group of contact forces whose sum should track the ground<br>
   *  reaction force impulse data from a single axis of a single ExternalForce.<br>
   *  If the contact force elements associated with a single ExternalForce are<br>
   *  distributed across multiple bodies use this function instead of the<br>
   *  easier-to-use addContactGroup(), and set the group's<br>
   *  alternative_frame_paths property accordingly. See<br>
   *  MocoContactImpulseTrackingGoalGroup for more information.
   */
  public void addContactGroup(MocoContactImpulseTrackingGoalGroup group) {
    opensimMocoJNI.MocoContactImpulseTrackingGoal_addContactGroup__SWIG_1(swigCPtr, this, MocoContactImpulseTrackingGoalGroup.getCPtr(group), group);
  }

  /**
   *  Set the ground reaction force contact impulse axis to be tracked<br>
   *  (X = 0, Y = 1, Z = 2), where axis refers to the component of<br>
   *  the ground reaction force contact impulse, in the ground frame, to be<br>
   *  tracked.
   */
  public void setImpulseAxis(int impulseAxis) {
    opensimMocoJNI.MocoContactImpulseTrackingGoal_setImpulseAxis(swigCPtr, this, impulseAxis);
  }

  public int getImpulseAxis() {
    return opensimMocoJNI.MocoContactImpulseTrackingGoal_getImpulseAxis(swigCPtr, this);
  }

  /**
   *  Add a MocoParameter to the problem that will scale the tracking reference<br>
   *  data associated with a contact force group. Scale factors are applied<br>
   *  to the tracking error calculations based on the following equation:<br>
   * <br>
   *      error = modelValue - scaleFactor * referenceValue<br>
   * <br>
   *  In other words, the scale factor is applied when computing the tracking<br>
   *  error for each contact force group, not to the reference data directly.<br>
   *  You must specify both the external force name associated with the contact<br>
   *  force group and the index corresponding to the direction (i.e., X = 0,<br>
   *  Y = 1, Z = 2) of the scaled force value. The direction is applied in<br>
   *  whatever frame the reference data is expressed in based on the provided<br>
   *  ExternalLoads in each contact group.
   */
  public void addScaleFactor(String name, String externalForceName, MocoBounds bounds) {
    opensimMocoJNI.MocoContactImpulseTrackingGoal_addScaleFactor(swigCPtr, this, name, externalForceName, MocoBounds.getCPtr(bounds), bounds);
  }

}
