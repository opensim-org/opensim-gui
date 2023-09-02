/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 * TwoFrameLinker is a utility class to extend a Component such that it connects<br>
 * two Frames. For example, a WeldConstraint and BushingForces operate between<br>
 * two frames to restrict their motion. A TwoFrameLinker&lt;Force, PhysicalFrame&gt;,<br>
 * for example, is a Force that operates between two PhyscialFrames and it is <br>
 * the base class for BushingForces.<br>
 * (A class whose super class is a template parameter is called a mixin class.)<br>
 * <br>
 * {@code 
   class BushingForce : public TwoFrameLinker<Force, PhysicalFrame>
}<br>
 * <br>
 * <br>
 * <br>
 * <br>
 * @author Ajay Seth
 */
public class TwoFrameLinkerConstraint extends Constraint {
  private transient long swigCPtr;

  public TwoFrameLinkerConstraint(long cPtr, boolean cMemoryOwn) {
    super(opensimSimulationJNI.TwoFrameLinkerConstraint_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(TwoFrameLinkerConstraint obj) {
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
        opensimSimulationJNI.delete_TwoFrameLinkerConstraint(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static TwoFrameLinkerConstraint safeDownCast(OpenSimObject obj) {
    long cPtr = opensimSimulationJNI.TwoFrameLinkerConstraint_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new TwoFrameLinkerConstraint(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimSimulationJNI.TwoFrameLinkerConstraint_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimSimulationJNI.TwoFrameLinkerConstraint_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimSimulationJNI.TwoFrameLinkerConstraint_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new TwoFrameLinkerConstraint(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimSimulationJNI.TwoFrameLinkerConstraint_getConcreteClassName(swigCPtr, this);
  }

  /**
   *  Frames added to satisfy the sockets of this TwoFrameLinker Component
   */
  public void copyProperty_frames(TwoFrameLinkerConstraint source) {
    opensimSimulationJNI.TwoFrameLinkerConstraint_copyProperty_frames(swigCPtr, this, TwoFrameLinkerConstraint.getCPtr(source), source);
  }

  public PhysicalFrame get_frames(int i) {
    return new PhysicalFrame(opensimSimulationJNI.TwoFrameLinkerConstraint_get_frames(swigCPtr, this, i), false);
  }

  public PhysicalFrame upd_frames(int i) {
    return new PhysicalFrame(opensimSimulationJNI.TwoFrameLinkerConstraint_upd_frames(swigCPtr, this, i), false);
  }

  public void set_frames(int i, PhysicalFrame value) {
    opensimSimulationJNI.TwoFrameLinkerConstraint_set_frames(swigCPtr, this, i, PhysicalFrame.getCPtr(value), value);
  }

  public int append_frames(PhysicalFrame value) {
    return opensimSimulationJNI.TwoFrameLinkerConstraint_append_frames(swigCPtr, this, PhysicalFrame.getCPtr(value), value);
  }

  public void constructProperty_frames() {
    opensimSimulationJNI.TwoFrameLinkerConstraint_constructProperty_frames(swigCPtr, this);
  }

  public void setPropertyIndex_socket_frame1(SWIGTYPE_p_OpenSim__PropertyIndex value) {
    opensimSimulationJNI.TwoFrameLinkerConstraint_PropertyIndex_socket_frame1_set(swigCPtr, this, SWIGTYPE_p_OpenSim__PropertyIndex.getCPtr(value));
  }

  public SWIGTYPE_p_OpenSim__PropertyIndex getPropertyIndex_socket_frame1() {
    return new SWIGTYPE_p_OpenSim__PropertyIndex(opensimSimulationJNI.TwoFrameLinkerConstraint_PropertyIndex_socket_frame1_get(swigCPtr, this), true);
  }

  public void connectSocket_frame1(OpenSimObject object) {
    opensimSimulationJNI.TwoFrameLinkerConstraint_connectSocket_frame1(swigCPtr, this, OpenSimObject.getCPtr(object), object);
  }

  public void setPropertyIndex_socket_frame2(SWIGTYPE_p_OpenSim__PropertyIndex value) {
    opensimSimulationJNI.TwoFrameLinkerConstraint_PropertyIndex_socket_frame2_set(swigCPtr, this, SWIGTYPE_p_OpenSim__PropertyIndex.getCPtr(value));
  }

  public SWIGTYPE_p_OpenSim__PropertyIndex getPropertyIndex_socket_frame2() {
    return new SWIGTYPE_p_OpenSim__PropertyIndex(opensimSimulationJNI.TwoFrameLinkerConstraint_PropertyIndex_socket_frame2_get(swigCPtr, this), true);
  }

  public void connectSocket_frame2(OpenSimObject object) {
    opensimSimulationJNI.TwoFrameLinkerConstraint_connectSocket_frame2(swigCPtr, this, OpenSimObject.getCPtr(object), object);
  }

  /**
   *  Access the first frame the TwoFrameLinker component connects.<br>
   *         Note, if an offset was introduced at construction, then this will be<br>
   *         the offset frame. 
   */
  public PhysicalFrame getFrame1() {
    return new PhysicalFrame(opensimSimulationJNI.TwoFrameLinkerConstraint_getFrame1(swigCPtr, this), false);
  }

  /**
   *  Access the second frame the TwoFrameLinker component connects.<br>
   *         Note, if an offset was introduced at construction, then this will be<br>
   *         the offset frame. 
   */
  public PhysicalFrame getFrame2() {
    return new PhysicalFrame(opensimSimulationJNI.TwoFrameLinkerConstraint_getFrame2(swigCPtr, this), false);
  }

  /**
   *  Compute the relative offset Transform between the two frames linked by <br>
   *         this TwoFrameLinker component at a given State, expressed in frame1. 
   */
  public Transform computeRelativeOffset(State s) {
    return new Transform(opensimSimulationJNI.TwoFrameLinkerConstraint_computeRelativeOffset(swigCPtr, this, State.getCPtr(s), s), true);
  }

  /**
   *  Compute the relative spatial velocity between the two frames linked by<br>
   *         this TwoFrameLinker component at a given State, expressed in frame1. 
   */
  public SpatialVec computeRelativeVelocity(State s) {
    return new SpatialVec(opensimSimulationJNI.TwoFrameLinkerConstraint_computeRelativeVelocity(swigCPtr, this, State.getCPtr(s), s), true);
  }

  /**
   *  Compute the deflection (spatial separation) of the two frames connected<br>
   *         by the TwoFrameLinker. Angular deflections expressed as XYZ body-fixed <br>
   *         Euler angles of frame2 w.r.t frame1.<br>
   *         NOTE: When using deflections to compute spatial forces, these forces<br>
   *             may not be valid for large deflections, because Euler angles are <br>
   *             unable to uniquely distinguish an X rotation angle of +/-180 degs,<br>
   *             and subsequent rotations that are +/-90 degs. It is mainly useful<br>
   *             for calculating errors for constraints and forces for computing <br>
   *             restoration forces.<br>
   *      @return dq     Vec6 of (3) angular and (3) translational deflections. 
   */
  public Vec6 computeDeflection(State s) {
    return new Vec6(opensimSimulationJNI.TwoFrameLinkerConstraint_computeDeflection(swigCPtr, this, State.getCPtr(s), s), true);
  }

  /**
   *  Compute the deflection rate (dqdot) of the two frames connected by<br>
   *         this TwoFrameLinker component. Angular velocity is expressed as Euler<br>
   *         (XYZ body-fixed) angle derivatives. Note that the derivatives <br>
   *         become singular as the second Euler angle approaches 90 degs.<br>
   *     @return dqdot  Vec6 of (3) angular and (3) translational deflection rates. 
   */
  public Vec6 computeDeflectionRate(State s) {
    return new Vec6(opensimSimulationJNI.TwoFrameLinkerConstraint_computeDeflectionRate(swigCPtr, this, State.getCPtr(s), s), true);
  }

}
