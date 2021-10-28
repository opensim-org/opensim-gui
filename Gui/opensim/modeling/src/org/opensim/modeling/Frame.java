/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 * A Frame is an OpenSim representation of a reference frame. It consists of<br>
 * a right-handed set of three orthogonal axes and an origin point. Frames are<br>
 * intended to provide convenient reference frames for locating physical<br>
 * structures (such as joints and muscle attachments) as well as provide a<br>
 * convenient basis for performing spatial calculations. For example, if your<br>
 * system involves contact, you might define a Frame that is aligned with the<br>
 * normal direction of a contact surface and whose origin is at the<br>
 * center-of-pressure.<br>
 * <br>
 * Every Frame is capable of providing its SimTK::Transform (translation of<br>
 * the origin and the orientation of its axes) in the Ground frame as a<br>
 * function of the Model's (SimTK::MultibodySystem's) state.<br>
 * <br>
 * The Frame class also provides convenience methods for re-expressing vectors<br>
 * from one Frame to another.<br>
 * <br>
 * As already noted, Frames are useful for locating physical structures such as<br>
 * bodies, their joints, and the locations where constraints can be connected<br>
 * and forces can be applied. It is perhaps less evident that Frames can be<br>
 * extremely useful for relating a multitude of reference frames together to<br>
 * form chains and trees. For example, a Frame to specify muscle attachments<br>
 * (M) and a Frame to specify a joint location (J) could themselves be<br>
 * specified in an anatomical Frame (A) defined by bony landmarks identified<br>
 * by surface markers or tagged on CT or MRI images. The body (B), to which the<br>
 * anatomical frame (A) is attached, can be thought of as a "Base" frame or a<br>
 * root of a tree from which a set of descendant frames arise. In particular, a<br>
 * Base frame and all its descendants have the property that they share the<br>
 * same angular velocity, since they are affixed to the same underlying Frame<br>
 * (in this case a Body).<br>
 * <pre><br>
 *         M---muscle points<br>
 *        /<br>
 *   B---A<br>
 *        <br>
 *         J---joint axes<br>
 * </pre><br>
 * Therefore, a useful concept is that of a Base frame, and a Frame can always<br>
 * provide a Base frame. If a Frame is not affixed to another frame, its Base<br>
 * frame is itself.<br>
 * <br>
 * @see SimTK#Transform<br>
 * <br>
 * @author Matt DeMers<br>
 * @author Ajay Seth
 */
public class Frame extends ModelComponent {
  private transient long swigCPtr;

  public Frame(long cPtr, boolean cMemoryOwn) {
    super(opensimSimulationJNI.Frame_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(Frame obj) {
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
        opensimSimulationJNI.delete_Frame(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public void attachGeometry(Geometry geom) {
      geom.markAdopted();
      private_attachGeometry(geom);
  }

  public static Frame safeDownCast(OpenSimObject obj) {
    long cPtr = opensimSimulationJNI.Frame_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new Frame(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimSimulationJNI.Frame_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimSimulationJNI.Frame_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimSimulationJNI.Frame_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new Frame(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimSimulationJNI.Frame_getConcreteClassName(swigCPtr, this);
  }

  public void copyProperty_frame_geometry(Frame source) {
    opensimSimulationJNI.Frame_copyProperty_frame_geometry(swigCPtr, this, Frame.getCPtr(source), source);
  }

  public FrameGeometry get_frame_geometry(int i) {
    return new FrameGeometry(opensimSimulationJNI.Frame_get_frame_geometry__SWIG_0(swigCPtr, this, i), false);
  }

  public FrameGeometry upd_frame_geometry(int i) {
    return new FrameGeometry(opensimSimulationJNI.Frame_upd_frame_geometry__SWIG_0(swigCPtr, this, i), false);
  }

  public void set_frame_geometry(int i, FrameGeometry value) {
    opensimSimulationJNI.Frame_set_frame_geometry__SWIG_0(swigCPtr, this, i, FrameGeometry.getCPtr(value), value);
  }

  public int append_frame_geometry(FrameGeometry value) {
    return opensimSimulationJNI.Frame_append_frame_geometry(swigCPtr, this, FrameGeometry.getCPtr(value), value);
  }

  public void constructProperty_frame_geometry(FrameGeometry initValue) {
    opensimSimulationJNI.Frame_constructProperty_frame_geometry(swigCPtr, this, FrameGeometry.getCPtr(initValue), initValue);
  }

  public FrameGeometry get_frame_geometry() {
    return new FrameGeometry(opensimSimulationJNI.Frame_get_frame_geometry__SWIG_1(swigCPtr, this), false);
  }

  public FrameGeometry upd_frame_geometry() {
    return new FrameGeometry(opensimSimulationJNI.Frame_upd_frame_geometry__SWIG_1(swigCPtr, this), false);
  }

  public void set_frame_geometry(FrameGeometry value) {
    opensimSimulationJNI.Frame_set_frame_geometry__SWIG_1(swigCPtr, this, FrameGeometry.getCPtr(value), value);
  }

  public void copyProperty_attached_geometry(Frame source) {
    opensimSimulationJNI.Frame_copyProperty_attached_geometry(swigCPtr, this, Frame.getCPtr(source), source);
  }

  public Geometry get_attached_geometry(int i) {
    return new Geometry(opensimSimulationJNI.Frame_get_attached_geometry(swigCPtr, this, i), false);
  }

  public Geometry upd_attached_geometry(int i) {
    return new Geometry(opensimSimulationJNI.Frame_upd_attached_geometry(swigCPtr, this, i), false);
  }

  public void set_attached_geometry(int i, Geometry value) {
    opensimSimulationJNI.Frame_set_attached_geometry(swigCPtr, this, i, Geometry.getCPtr(value), value);
  }

  public int append_attached_geometry(Geometry value) {
    return opensimSimulationJNI.Frame_append_attached_geometry(swigCPtr, this, Geometry.getCPtr(value), value);
  }

  public void constructProperty_attached_geometry() {
    opensimSimulationJNI.Frame_constructProperty_attached_geometry(swigCPtr, this);
  }

  public void set_has_output_position(boolean value) {
    opensimSimulationJNI.Frame__has_output_position_set(swigCPtr, this, value);
  }

  public boolean get_has_output_position() {
    return opensimSimulationJNI.Frame__has_output_position_get(swigCPtr, this);
  }

  public void set_has_output_rotation(boolean value) {
    opensimSimulationJNI.Frame__has_output_rotation_set(swigCPtr, this, value);
  }

  public boolean get_has_output_rotation() {
    return opensimSimulationJNI.Frame__has_output_rotation_get(swigCPtr, this);
  }

  public void set_has_output_transform(boolean value) {
    opensimSimulationJNI.Frame__has_output_transform_set(swigCPtr, this, value);
  }

  public boolean get_has_output_transform() {
    return opensimSimulationJNI.Frame__has_output_transform_get(swigCPtr, this);
  }

  public void set_has_output_velocity(boolean value) {
    opensimSimulationJNI.Frame__has_output_velocity_set(swigCPtr, this, value);
  }

  public boolean get_has_output_velocity() {
    return opensimSimulationJNI.Frame__has_output_velocity_get(swigCPtr, this);
  }

  public void set_has_output_angular_velocity(boolean value) {
    opensimSimulationJNI.Frame__has_output_angular_velocity_set(swigCPtr, this, value);
  }

  public boolean get_has_output_angular_velocity() {
    return opensimSimulationJNI.Frame__has_output_angular_velocity_get(swigCPtr, this);
  }

  public void set_has_output_linear_velocity(boolean value) {
    opensimSimulationJNI.Frame__has_output_linear_velocity_set(swigCPtr, this, value);
  }

  public boolean get_has_output_linear_velocity() {
    return opensimSimulationJNI.Frame__has_output_linear_velocity_get(swigCPtr, this);
  }

  public void set_has_output_acceleration(boolean value) {
    opensimSimulationJNI.Frame__has_output_acceleration_set(swigCPtr, this, value);
  }

  public boolean get_has_output_acceleration() {
    return opensimSimulationJNI.Frame__has_output_acceleration_get(swigCPtr, this);
  }

  public void set_has_output_angular_acceleration(boolean value) {
    opensimSimulationJNI.Frame__has_output_angular_acceleration_set(swigCPtr, this, value);
  }

  public boolean get_has_output_angular_acceleration() {
    return opensimSimulationJNI.Frame__has_output_angular_acceleration_get(swigCPtr, this);
  }

  public void set_has_output_linear_acceleration(boolean value) {
    opensimSimulationJNI.Frame__has_output_linear_acceleration_set(swigCPtr, this, value);
  }

  public boolean get_has_output_linear_acceleration() {
    return opensimSimulationJNI.Frame__has_output_linear_acceleration_get(swigCPtr, this);
  }

  /**
   * *<br>
   *     Get the transform of this frame (F) relative to the ground frame (G).<br>
   *     It transforms quantities expressed in F into quantities expressed<br>
   *     in G. This is mathematically stated as:<br>
   *         vec_G = X_GF*vec_F ,<br>
   *     where X_GF is the transform returned by getTransformInGround.<br>
   * <br>
   *     @param state       The state applied to the model when determining the<br>
   *                        transform.<br>
   *     @return transform  The transform between this frame and the ground frame
   */
  public Transform getTransformInGround(State state) {
    return new Transform(opensimSimulationJNI.Frame_getTransformInGround(swigCPtr, this, State.getCPtr(state), state), false);
  }

  /**
   *  The spatial velocity V_GF {omega; v} of this Frame, measured with<br>
   *         respect to and expressed in the ground frame. It can be used to compute<br>
   *         the velocity of any stationary point on F, located at r_F (Vec3), in<br>
   *         ground, G, as:<br>
   *             v_G = V_GF[1] + SimTK::cross(V_GF[0], r_F);<br>
   *         Is only valid at Stage::Velocity or higher. 
   */
  public SpatialVec getVelocityInGround(State state) {
    return new SpatialVec(opensimSimulationJNI.Frame_getVelocityInGround(swigCPtr, this, State.getCPtr(state), state), false);
  }

  /**
   *  The angular velocity of this Frame, measured with respect to and<br>
   *         expressed in the ground frame (i.e., the first half of the SpatialVec<br>
   *         returned by getVelocityInGround()). 
   */
  public Vec3 getAngularVelocityInGround(State state) {
    return new Vec3(opensimSimulationJNI.Frame_getAngularVelocityInGround(swigCPtr, this, State.getCPtr(state), state), false);
  }

  /**
   *  The linear velocity of this Frame, measured with respect to and<br>
   *         expressed in the ground frame (i.e., the second half of the SpatialVec<br>
   *         returned by getVelocityInGround()). 
   */
  public Vec3 getLinearVelocityInGround(State state) {
    return new Vec3(opensimSimulationJNI.Frame_getLinearVelocityInGround(swigCPtr, this, State.getCPtr(state), state), false);
  }

  /**
   *  The spatial acceleration A_GF {alpha; a} of this Frame, measured with<br>
   *         respect to and expressed in the ground frame. It can also be used to<br>
   *         compute the acceleration of any stationary point on F, located at r_F<br>
   *         (Vec3), in ground, G, as:<br>
   *             a_G = A_GF[1] + SimTK::cross(A_GF[0], r_F) + <br>
   *                   SimTK::cross(V_GF[0], SimTK::cross(V_GF[0], r_F));<br>
   *         Is only valid at Stage::Acceleration or higher. 
   */
  public SpatialVec getAccelerationInGround(State state) {
    return new SpatialVec(opensimSimulationJNI.Frame_getAccelerationInGround(swigCPtr, this, State.getCPtr(state), state), false);
  }

  /**
   *  The angular acceleration of this Frame, measured with respect to and<br>
   *         expressed in the ground frame (i.e., the first half of the SpatialVec<br>
   *         returned by getAccelerationInGround()). 
   */
  public Vec3 getAngularAccelerationInGround(State state) {
    return new Vec3(opensimSimulationJNI.Frame_getAngularAccelerationInGround(swigCPtr, this, State.getCPtr(state), state), false);
  }

  /**
   *  The linear acceleration of this Frame, measured with respect to and<br>
   *         expressed in the ground frame (i.e., the second half of the SpatialVec<br>
   *         returned by getAccelerationInGround()). 
   */
  public Vec3 getLinearAccelerationInGround(State state) {
    return new Vec3(opensimSimulationJNI.Frame_getLinearAccelerationInGround(swigCPtr, this, State.getCPtr(state), state), false);
  }

  /**
   *     Find the transform that describes this frame (F) relative to another<br>
   *     frame (A). It transforms quantities expressed in F to quantities expressed<br>
   *     in A. This is mathematically stated as:<br>
   *         vec_A = X_AF*vec_F ,<br>
   *     where X_AF is the transform returned by this method.<br>
   * <br>
   *     @param state       The state applied to the model when determining the<br>
   *                        transform.<br>
   *     @param otherFrame  a second frame<br>
   *     @return transform  The transform between this frame and otherFrame
   */
  public Transform findTransformBetween(State state, Frame otherFrame) {
    return new Transform(opensimSimulationJNI.Frame_findTransformBetween(swigCPtr, this, State.getCPtr(state), state, Frame.getCPtr(otherFrame), otherFrame), true);
  }

  /**
   *     Take a vector expressed in this frame (F) and re-express the same vector<br>
   *     in another frame (A). This re-expression accounts for the difference<br>
   *     in orientation between the frames. This is mathematically stated as:<br>
   *         vec_A = R_AF*vec_F<br>
   *     which does not translate the vector. This is intended to re-express<br>
   *     physical vector quantities such as a frame's angular velocity or an<br>
   *     applied force, from one frame to another without changing the physical<br>
   *     quantity. If you have a position vector and want to change the point from<br>
   *     which the position is measured, you want findStationLocationInAnotherFrame().<br>
   * <br>
   *     @param state       The state of the model.<br>
   *     @param vec_F       The vector to be re-expressed.<br>
   *     @param otherFrame  The frame in which the vector will be re-expressed<br>
   *     @return vec_A      The expression of the vector in otherFrame.
   */
  public Vec3 expressVectorInAnotherFrame(State state, Vec3 vec_F, Frame otherFrame) {
    return new Vec3(opensimSimulationJNI.Frame_expressVectorInAnotherFrame(swigCPtr, this, State.getCPtr(state), state, Vec3.getCPtr(vec_F), vec_F, Frame.getCPtr(otherFrame), otherFrame), true);
  }

  /**
   *     Take a vector in this frame (F) and re-express the same vector<br>
   *     in Ground (G). This method is equivalent to expressVectorInAnotherFrame()<br>
   *     where the "other Frame" is always Ground.<br>
   *     @param state       The state of the model.<br>
   *     @param vec_F       The vector to be re-expressed.<br>
   *     @return vec_G      The expression of the vector in Ground.
   */
  public Vec3 expressVectorInGround(State state, Vec3 vec_F) {
    return new Vec3(opensimSimulationJNI.Frame_expressVectorInGround(swigCPtr, this, State.getCPtr(state), state, Vec3.getCPtr(vec_F), vec_F), true);
  }

  /**
   *     Take a station located and expressed in this frame (F) and determine<br>
   *     its location relative to and expressed in another frame (A). The transform<br>
   *     accounts for the difference in orientation and translation between the <br>
   *     frames.<br>
   *     This is mathematically stated as: <br>
   *         loc_A = X_AF*station_F<br>
   * <br>
   *     @param state       The state of the model.<br>
   *     @param station_F   The position Vec3 from frame F's origin to the station.<br>
   *     @param otherFrame  The frame (A) in which the station's location <br>
   *                        will be relative to and expressed.<br>
   *     @return loc_A      The location of the station in another frame (A).
   */
  public Vec3 findStationLocationInAnotherFrame(State state, Vec3 station_F, Frame otherFrame) {
    return new Vec3(opensimSimulationJNI.Frame_findStationLocationInAnotherFrame(swigCPtr, this, State.getCPtr(state), state, Vec3.getCPtr(station_F), station_F, Frame.getCPtr(otherFrame), otherFrame), true);
  }

  /**
   *     Take a station located and expressed in this frame (F) and determine<br>
   *     its location relative to and expressed in Ground (G). This method is<br>
   *     equivalent to findStationLocationInAnotherFrame() where the "other Frame" is<br>
   *     always Ground.<br>
   * <br>
   *     Note that if you have added an OpenSim::Station, you should use the<br>
   *     Station's %getLocationInGround() method instead.<br>
   * <br>
   *     @param state       The state of the model.<br>
   *     @param station_F   The position Vec3 from frame F's origin to the station.<br>
   *     @return loc_G      The location of the station in Ground.
   */
  public Vec3 findStationLocationInGround(State state, Vec3 station_F) {
    return new Vec3(opensimSimulationJNI.Frame_findStationLocationInGround(swigCPtr, this, State.getCPtr(state), state, Vec3.getCPtr(station_F), station_F), true);
  }

  /**
   *     Take a station located and expressed in this frame (F) and determine<br>
   *     its velocity relative to and expressed in Ground (G).<br>
   * <br>
   *     Note that if you have added an OpenSim::Station, you should use the<br>
   *     Station's %getVelocityInGround() method instead.<br>
   * <br>
   *     @param state       The state of the model.<br>
   *     @param station_F   The position Vec3 from frame F's origin to the station.<br>
   *     @return vel_G      The velocity of the station in Ground.
   */
  public Vec3 findStationVelocityInGround(State state, Vec3 station_F) {
    return new Vec3(opensimSimulationJNI.Frame_findStationVelocityInGround(swigCPtr, this, State.getCPtr(state), state, Vec3.getCPtr(station_F), station_F), true);
  }

  /**
   *     Take a station located and expressed in this frame (F) and determine<br>
   *     its acceleration relative to and expressed in Ground (G).<br>
   * <br>
   *     Note that if you have added an OpenSim::Station, you should use the<br>
   *     Station's %getAccelerationInGround() method instead.<br>
   * <br>
   *     @param state       The state of the model.<br>
   *     @param station_F   The position Vec3 from frame F's origin to the station.<br>
   *     @return acc_G      The acceleration of the station in Ground.
   */
  public Vec3 findStationAccelerationInGround(State state, Vec3 station_F) {
    return new Vec3(opensimSimulationJNI.Frame_findStationAccelerationInGround(swigCPtr, this, State.getCPtr(state), state, Vec3.getCPtr(station_F), station_F), true);
  }

  /**
   * *<br>
   * <br>
   *     Find this Frame's base Frame. See the "Advanced" note, above.<br>
   * <br>
   *     @return baseFrame     The Frame that is the base for this Frame.
   */
  public Frame findBaseFrame() {
    return new Frame(opensimSimulationJNI.Frame_findBaseFrame(swigCPtr, this), false);
  }

  /**
   *     Find the equivalent Transform of this Frame (F) in its base (B) Frame.<br>
   *     That is find X_BF, such that vecB = X_BF*vecF<br>
   *     For a Frame that is itself a base, this returns the identity Transform.<br>
   *     @return X_BF     The Transform of F in B
   */
  public Transform findTransformInBaseFrame() {
    return new Transform(opensimSimulationJNI.Frame_findTransformInBaseFrame(swigCPtr, this), true);
  }

  /**
   *  Accessor for position of the origin of the Frame in Ground. 
   */
  public Vec3 getPositionInGround(State state) {
    return new Vec3(opensimSimulationJNI.Frame_getPositionInGround(swigCPtr, this, State.getCPtr(state), state), true);
  }

  /**
   *  Accessor for Rotation matrix of the Frame in Ground. 
   */
  public SWIGTYPE_p_SimTK__Rotation_T_SimTK__Real_t getRotationInGround(State state) {
    return new SWIGTYPE_p_SimTK__Rotation_T_SimTK__Real_t(opensimSimulationJNI.Frame_getRotationInGround(swigCPtr, this, State.getCPtr(state), state), true);
  }

  /**
   * Attach Geometry to this Frame and have this Frame take ownership of<br>
   *         it by adding it to this Frame's &lt;attached_geometry&gt; property list.<br>
   *         The Geometry is treated as being fixed to this Frame such that the<br>
   *         transform used to position the Geometry is that of this Frame. 
   */
  private void private_attachGeometry(Geometry geom) {
    opensimSimulationJNI.Frame_private_attachGeometry(swigCPtr, this, Geometry.getCPtr(geom), geom);
  }

  public void scaleAttachedGeometry(Vec3 scaleFactors) {
    opensimSimulationJNI.Frame_scaleAttachedGeometry(swigCPtr, this, Vec3.getCPtr(scaleFactors), scaleFactors);
  }

  /**
   *  Scales Geometry components that reside in the Frame's<br>
   *         `attached_geometry` list property. Note that Geometry residing elsewhere<br>
   *         (e.g., in the `components` list property of a Frame or any other<br>
   *         Component) will not be scaled. Note also that ContactGeometry derives<br>
   *         from ModelComponent so the classes derived from ContactGeometry are<br>
   *         responsible for scaling themselves. (However, `scale()` is not currently<br>
   *         implemented on ContactGeometry or classes derived therefrom so they will<br>
   *         not scale with the Model.) 
   */
  public void extendScale(State s, ScaleSet scaleSet) {
    opensimSimulationJNI.Frame_extendScale(swigCPtr, this, State.getCPtr(s), s, ScaleSet.getCPtr(scaleSet), scaleSet);
  }

}
