/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 *  This compliant contact force model is similar to HuntCrossleyForce, except<br>
 * that this model applies force even when not in contact. Unlike<br>
 * HuntCrossleyForce, the normal force is differentiable as a function of<br>
 * penetration depth. This component is designed for use in gradient-based<br>
 * optimizations, in which the model is required to be differentiable. This<br>
 * component models contact between a single sphere and a single half space.<br>
 * This force does NOT use ContactGeometry objects; the description of the<br>
 * contact geometries is done through properties of this component.<br>
 * <br>
 * ### Constant contact force<br>
 * <br>
 * This force applies a constant contact force even when the sphere and half-space<br>
 * are not contacting. This constant force is set with the constant_contact_force<br>
 * property. Its default value is appropriate for walking; the value may need to<br>
 * be adjusted for different contact scenarios or models with a very light mass.<br>
 * <br>
 * @see SimTK#SmoothSphereHalfSpaceForce<br>
 * <br>
 * The graph below compares the smooth approximation of the Hertz force to that<br>
 * from HuntCrossleyForce.<br>
 * <br>
 *  <style>div.image<br>
 * img[src="SmoothSphereHalfSpaceForce_HertzForce.png"]{width:750px;}</style><br>
 * <br>
 * <img src=SmoothSphereHalfSpaceForce_HertzForce.png alt="Curves produced using E=1e6, R=0.8, cf=1e-5, and bd=300"/><br>
 * <br>
 * The graph below compares the smooth approximation of the dissipative force to<br>
 *  that from HuntCrossleyForce.<br>
 * <br>
 *  <style>div.image<br>
 * img[src="SmoothSphereHalfSpaceForce_HuntCrossleyForce.png"]{width:750px;}</style><br>
 * <br>
 * <img src=SmoothSphereHalfSpaceForce_HuntCrossleyForce.png alt="Curves produced using x=0.1, E=1e6, R=0.8, c=2, cf=1e-5, bd=300, and bv=50"/><br>
 * <br>
 * Serrancoli, G., Falisse, A., Dembia, C., Vantilt, J., Tanghe, K., Lefeber, D.,<br>
 * Jonkers, I., De Schutter, J., De Groote, F. (2019). Subject-exoskeleton contact<br>
 * model calibration leads to accurate interaction force predictions. IEEE<br>
 * Transactions on Neural Systems and Rehabilitation Engineering, 1–1.<br>
 * doi:10.1109/tnsre.2019.2924536 
 */
public class SmoothSphereHalfSpaceForce extends Force {
  private transient long swigCPtr;

  public SmoothSphereHalfSpaceForce(long cPtr, boolean cMemoryOwn) {
    super(opensimSimulationJNI.SmoothSphereHalfSpaceForce_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(SmoothSphereHalfSpaceForce obj) {
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
        opensimSimulationJNI.delete_SmoothSphereHalfSpaceForce(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static SmoothSphereHalfSpaceForce safeDownCast(OpenSimObject obj) {
    long cPtr = opensimSimulationJNI.SmoothSphereHalfSpaceForce_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new SmoothSphereHalfSpaceForce(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimSimulationJNI.SmoothSphereHalfSpaceForce_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimSimulationJNI.SmoothSphereHalfSpaceForce_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new SmoothSphereHalfSpaceForce(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimSimulationJNI.SmoothSphereHalfSpaceForce_getConcreteClassName(swigCPtr, this);
  }

  public void copyProperty_stiffness(SmoothSphereHalfSpaceForce source) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_copyProperty_stiffness(swigCPtr, this, SmoothSphereHalfSpaceForce.getCPtr(source), source);
  }

  public double get_stiffness(int i) {
    return opensimSimulationJNI.SmoothSphereHalfSpaceForce_get_stiffness__SWIG_0(swigCPtr, this, i);
  }

  public SWIGTYPE_p_double upd_stiffness(int i) {
    return new SWIGTYPE_p_double(opensimSimulationJNI.SmoothSphereHalfSpaceForce_upd_stiffness__SWIG_0(swigCPtr, this, i), false);
  }

  public void set_stiffness(int i, double value) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_set_stiffness__SWIG_0(swigCPtr, this, i, value);
  }

  public int append_stiffness(double value) {
    return opensimSimulationJNI.SmoothSphereHalfSpaceForce_append_stiffness(swigCPtr, this, value);
  }

  public void constructProperty_stiffness(double initValue) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_constructProperty_stiffness(swigCPtr, this, initValue);
  }

  public double get_stiffness() {
    return opensimSimulationJNI.SmoothSphereHalfSpaceForce_get_stiffness__SWIG_1(swigCPtr, this);
  }

  public SWIGTYPE_p_double upd_stiffness() {
    return new SWIGTYPE_p_double(opensimSimulationJNI.SmoothSphereHalfSpaceForce_upd_stiffness__SWIG_1(swigCPtr, this), false);
  }

  public void set_stiffness(double value) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_set_stiffness__SWIG_1(swigCPtr, this, value);
  }

  public void copyProperty_dissipation(SmoothSphereHalfSpaceForce source) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_copyProperty_dissipation(swigCPtr, this, SmoothSphereHalfSpaceForce.getCPtr(source), source);
  }

  public double get_dissipation(int i) {
    return opensimSimulationJNI.SmoothSphereHalfSpaceForce_get_dissipation__SWIG_0(swigCPtr, this, i);
  }

  public SWIGTYPE_p_double upd_dissipation(int i) {
    return new SWIGTYPE_p_double(opensimSimulationJNI.SmoothSphereHalfSpaceForce_upd_dissipation__SWIG_0(swigCPtr, this, i), false);
  }

  public void set_dissipation(int i, double value) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_set_dissipation__SWIG_0(swigCPtr, this, i, value);
  }

  public int append_dissipation(double value) {
    return opensimSimulationJNI.SmoothSphereHalfSpaceForce_append_dissipation(swigCPtr, this, value);
  }

  public void constructProperty_dissipation(double initValue) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_constructProperty_dissipation(swigCPtr, this, initValue);
  }

  public double get_dissipation() {
    return opensimSimulationJNI.SmoothSphereHalfSpaceForce_get_dissipation__SWIG_1(swigCPtr, this);
  }

  public SWIGTYPE_p_double upd_dissipation() {
    return new SWIGTYPE_p_double(opensimSimulationJNI.SmoothSphereHalfSpaceForce_upd_dissipation__SWIG_1(swigCPtr, this), false);
  }

  public void set_dissipation(double value) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_set_dissipation__SWIG_1(swigCPtr, this, value);
  }

  public void copyProperty_static_friction(SmoothSphereHalfSpaceForce source) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_copyProperty_static_friction(swigCPtr, this, SmoothSphereHalfSpaceForce.getCPtr(source), source);
  }

  public double get_static_friction(int i) {
    return opensimSimulationJNI.SmoothSphereHalfSpaceForce_get_static_friction__SWIG_0(swigCPtr, this, i);
  }

  public SWIGTYPE_p_double upd_static_friction(int i) {
    return new SWIGTYPE_p_double(opensimSimulationJNI.SmoothSphereHalfSpaceForce_upd_static_friction__SWIG_0(swigCPtr, this, i), false);
  }

  public void set_static_friction(int i, double value) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_set_static_friction__SWIG_0(swigCPtr, this, i, value);
  }

  public int append_static_friction(double value) {
    return opensimSimulationJNI.SmoothSphereHalfSpaceForce_append_static_friction(swigCPtr, this, value);
  }

  public void constructProperty_static_friction(double initValue) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_constructProperty_static_friction(swigCPtr, this, initValue);
  }

  public double get_static_friction() {
    return opensimSimulationJNI.SmoothSphereHalfSpaceForce_get_static_friction__SWIG_1(swigCPtr, this);
  }

  public SWIGTYPE_p_double upd_static_friction() {
    return new SWIGTYPE_p_double(opensimSimulationJNI.SmoothSphereHalfSpaceForce_upd_static_friction__SWIG_1(swigCPtr, this), false);
  }

  public void set_static_friction(double value) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_set_static_friction__SWIG_1(swigCPtr, this, value);
  }

  public void copyProperty_dynamic_friction(SmoothSphereHalfSpaceForce source) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_copyProperty_dynamic_friction(swigCPtr, this, SmoothSphereHalfSpaceForce.getCPtr(source), source);
  }

  public double get_dynamic_friction(int i) {
    return opensimSimulationJNI.SmoothSphereHalfSpaceForce_get_dynamic_friction__SWIG_0(swigCPtr, this, i);
  }

  public SWIGTYPE_p_double upd_dynamic_friction(int i) {
    return new SWIGTYPE_p_double(opensimSimulationJNI.SmoothSphereHalfSpaceForce_upd_dynamic_friction__SWIG_0(swigCPtr, this, i), false);
  }

  public void set_dynamic_friction(int i, double value) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_set_dynamic_friction__SWIG_0(swigCPtr, this, i, value);
  }

  public int append_dynamic_friction(double value) {
    return opensimSimulationJNI.SmoothSphereHalfSpaceForce_append_dynamic_friction(swigCPtr, this, value);
  }

  public void constructProperty_dynamic_friction(double initValue) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_constructProperty_dynamic_friction(swigCPtr, this, initValue);
  }

  public double get_dynamic_friction() {
    return opensimSimulationJNI.SmoothSphereHalfSpaceForce_get_dynamic_friction__SWIG_1(swigCPtr, this);
  }

  public SWIGTYPE_p_double upd_dynamic_friction() {
    return new SWIGTYPE_p_double(opensimSimulationJNI.SmoothSphereHalfSpaceForce_upd_dynamic_friction__SWIG_1(swigCPtr, this), false);
  }

  public void set_dynamic_friction(double value) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_set_dynamic_friction__SWIG_1(swigCPtr, this, value);
  }

  public void copyProperty_viscous_friction(SmoothSphereHalfSpaceForce source) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_copyProperty_viscous_friction(swigCPtr, this, SmoothSphereHalfSpaceForce.getCPtr(source), source);
  }

  public double get_viscous_friction(int i) {
    return opensimSimulationJNI.SmoothSphereHalfSpaceForce_get_viscous_friction__SWIG_0(swigCPtr, this, i);
  }

  public SWIGTYPE_p_double upd_viscous_friction(int i) {
    return new SWIGTYPE_p_double(opensimSimulationJNI.SmoothSphereHalfSpaceForce_upd_viscous_friction__SWIG_0(swigCPtr, this, i), false);
  }

  public void set_viscous_friction(int i, double value) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_set_viscous_friction__SWIG_0(swigCPtr, this, i, value);
  }

  public int append_viscous_friction(double value) {
    return opensimSimulationJNI.SmoothSphereHalfSpaceForce_append_viscous_friction(swigCPtr, this, value);
  }

  public void constructProperty_viscous_friction(double initValue) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_constructProperty_viscous_friction(swigCPtr, this, initValue);
  }

  public double get_viscous_friction() {
    return opensimSimulationJNI.SmoothSphereHalfSpaceForce_get_viscous_friction__SWIG_1(swigCPtr, this);
  }

  public SWIGTYPE_p_double upd_viscous_friction() {
    return new SWIGTYPE_p_double(opensimSimulationJNI.SmoothSphereHalfSpaceForce_upd_viscous_friction__SWIG_1(swigCPtr, this), false);
  }

  public void set_viscous_friction(double value) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_set_viscous_friction__SWIG_1(swigCPtr, this, value);
  }

  public void copyProperty_transition_velocity(SmoothSphereHalfSpaceForce source) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_copyProperty_transition_velocity(swigCPtr, this, SmoothSphereHalfSpaceForce.getCPtr(source), source);
  }

  public double get_transition_velocity(int i) {
    return opensimSimulationJNI.SmoothSphereHalfSpaceForce_get_transition_velocity__SWIG_0(swigCPtr, this, i);
  }

  public SWIGTYPE_p_double upd_transition_velocity(int i) {
    return new SWIGTYPE_p_double(opensimSimulationJNI.SmoothSphereHalfSpaceForce_upd_transition_velocity__SWIG_0(swigCPtr, this, i), false);
  }

  public void set_transition_velocity(int i, double value) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_set_transition_velocity__SWIG_0(swigCPtr, this, i, value);
  }

  public int append_transition_velocity(double value) {
    return opensimSimulationJNI.SmoothSphereHalfSpaceForce_append_transition_velocity(swigCPtr, this, value);
  }

  public void constructProperty_transition_velocity(double initValue) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_constructProperty_transition_velocity(swigCPtr, this, initValue);
  }

  public double get_transition_velocity() {
    return opensimSimulationJNI.SmoothSphereHalfSpaceForce_get_transition_velocity__SWIG_1(swigCPtr, this);
  }

  public SWIGTYPE_p_double upd_transition_velocity() {
    return new SWIGTYPE_p_double(opensimSimulationJNI.SmoothSphereHalfSpaceForce_upd_transition_velocity__SWIG_1(swigCPtr, this), false);
  }

  public void set_transition_velocity(double value) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_set_transition_velocity__SWIG_1(swigCPtr, this, value);
  }

  public void copyProperty_constant_contact_force(SmoothSphereHalfSpaceForce source) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_copyProperty_constant_contact_force(swigCPtr, this, SmoothSphereHalfSpaceForce.getCPtr(source), source);
  }

  public double get_constant_contact_force(int i) {
    return opensimSimulationJNI.SmoothSphereHalfSpaceForce_get_constant_contact_force__SWIG_0(swigCPtr, this, i);
  }

  public SWIGTYPE_p_double upd_constant_contact_force(int i) {
    return new SWIGTYPE_p_double(opensimSimulationJNI.SmoothSphereHalfSpaceForce_upd_constant_contact_force__SWIG_0(swigCPtr, this, i), false);
  }

  public void set_constant_contact_force(int i, double value) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_set_constant_contact_force__SWIG_0(swigCPtr, this, i, value);
  }

  public int append_constant_contact_force(double value) {
    return opensimSimulationJNI.SmoothSphereHalfSpaceForce_append_constant_contact_force(swigCPtr, this, value);
  }

  public void constructProperty_constant_contact_force(double initValue) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_constructProperty_constant_contact_force(swigCPtr, this, initValue);
  }

  public double get_constant_contact_force() {
    return opensimSimulationJNI.SmoothSphereHalfSpaceForce_get_constant_contact_force__SWIG_1(swigCPtr, this);
  }

  public SWIGTYPE_p_double upd_constant_contact_force() {
    return new SWIGTYPE_p_double(opensimSimulationJNI.SmoothSphereHalfSpaceForce_upd_constant_contact_force__SWIG_1(swigCPtr, this), false);
  }

  public void set_constant_contact_force(double value) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_set_constant_contact_force__SWIG_1(swigCPtr, this, value);
  }

  public void copyProperty_hertz_smoothing(SmoothSphereHalfSpaceForce source) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_copyProperty_hertz_smoothing(swigCPtr, this, SmoothSphereHalfSpaceForce.getCPtr(source), source);
  }

  public double get_hertz_smoothing(int i) {
    return opensimSimulationJNI.SmoothSphereHalfSpaceForce_get_hertz_smoothing__SWIG_0(swigCPtr, this, i);
  }

  public SWIGTYPE_p_double upd_hertz_smoothing(int i) {
    return new SWIGTYPE_p_double(opensimSimulationJNI.SmoothSphereHalfSpaceForce_upd_hertz_smoothing__SWIG_0(swigCPtr, this, i), false);
  }

  public void set_hertz_smoothing(int i, double value) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_set_hertz_smoothing__SWIG_0(swigCPtr, this, i, value);
  }

  public int append_hertz_smoothing(double value) {
    return opensimSimulationJNI.SmoothSphereHalfSpaceForce_append_hertz_smoothing(swigCPtr, this, value);
  }

  public void constructProperty_hertz_smoothing(double initValue) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_constructProperty_hertz_smoothing(swigCPtr, this, initValue);
  }

  public double get_hertz_smoothing() {
    return opensimSimulationJNI.SmoothSphereHalfSpaceForce_get_hertz_smoothing__SWIG_1(swigCPtr, this);
  }

  public SWIGTYPE_p_double upd_hertz_smoothing() {
    return new SWIGTYPE_p_double(opensimSimulationJNI.SmoothSphereHalfSpaceForce_upd_hertz_smoothing__SWIG_1(swigCPtr, this), false);
  }

  public void set_hertz_smoothing(double value) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_set_hertz_smoothing__SWIG_1(swigCPtr, this, value);
  }

  public void copyProperty_hunt_crossley_smoothing(SmoothSphereHalfSpaceForce source) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_copyProperty_hunt_crossley_smoothing(swigCPtr, this, SmoothSphereHalfSpaceForce.getCPtr(source), source);
  }

  public double get_hunt_crossley_smoothing(int i) {
    return opensimSimulationJNI.SmoothSphereHalfSpaceForce_get_hunt_crossley_smoothing__SWIG_0(swigCPtr, this, i);
  }

  public SWIGTYPE_p_double upd_hunt_crossley_smoothing(int i) {
    return new SWIGTYPE_p_double(opensimSimulationJNI.SmoothSphereHalfSpaceForce_upd_hunt_crossley_smoothing__SWIG_0(swigCPtr, this, i), false);
  }

  public void set_hunt_crossley_smoothing(int i, double value) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_set_hunt_crossley_smoothing__SWIG_0(swigCPtr, this, i, value);
  }

  public int append_hunt_crossley_smoothing(double value) {
    return opensimSimulationJNI.SmoothSphereHalfSpaceForce_append_hunt_crossley_smoothing(swigCPtr, this, value);
  }

  public void constructProperty_hunt_crossley_smoothing(double initValue) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_constructProperty_hunt_crossley_smoothing(swigCPtr, this, initValue);
  }

  public double get_hunt_crossley_smoothing() {
    return opensimSimulationJNI.SmoothSphereHalfSpaceForce_get_hunt_crossley_smoothing__SWIG_1(swigCPtr, this);
  }

  public SWIGTYPE_p_double upd_hunt_crossley_smoothing() {
    return new SWIGTYPE_p_double(opensimSimulationJNI.SmoothSphereHalfSpaceForce_upd_hunt_crossley_smoothing__SWIG_1(swigCPtr, this), false);
  }

  public void set_hunt_crossley_smoothing(double value) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_set_hunt_crossley_smoothing__SWIG_1(swigCPtr, this, value);
  }

  public void copyProperty_force_visualization_radius(SmoothSphereHalfSpaceForce source) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_copyProperty_force_visualization_radius(swigCPtr, this, SmoothSphereHalfSpaceForce.getCPtr(source), source);
  }

  public double get_force_visualization_radius(int i) {
    return opensimSimulationJNI.SmoothSphereHalfSpaceForce_get_force_visualization_radius__SWIG_0(swigCPtr, this, i);
  }

  public SWIGTYPE_p_double upd_force_visualization_radius(int i) {
    return new SWIGTYPE_p_double(opensimSimulationJNI.SmoothSphereHalfSpaceForce_upd_force_visualization_radius__SWIG_0(swigCPtr, this, i), false);
  }

  public void set_force_visualization_radius(int i, double value) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_set_force_visualization_radius__SWIG_0(swigCPtr, this, i, value);
  }

  public int append_force_visualization_radius(double value) {
    return opensimSimulationJNI.SmoothSphereHalfSpaceForce_append_force_visualization_radius(swigCPtr, this, value);
  }

  public void constructProperty_force_visualization_radius(double initValue) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_constructProperty_force_visualization_radius(swigCPtr, this, initValue);
  }

  public double get_force_visualization_radius() {
    return opensimSimulationJNI.SmoothSphereHalfSpaceForce_get_force_visualization_radius__SWIG_1(swigCPtr, this);
  }

  public SWIGTYPE_p_double upd_force_visualization_radius() {
    return new SWIGTYPE_p_double(opensimSimulationJNI.SmoothSphereHalfSpaceForce_upd_force_visualization_radius__SWIG_1(swigCPtr, this), false);
  }

  public void set_force_visualization_radius(double value) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_set_force_visualization_radius__SWIG_1(swigCPtr, this, value);
  }

  public void copyProperty_force_visualization_scale_factor(SmoothSphereHalfSpaceForce source) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_copyProperty_force_visualization_scale_factor(swigCPtr, this, SmoothSphereHalfSpaceForce.getCPtr(source), source);
  }

  public double get_force_visualization_scale_factor(int i) {
    return opensimSimulationJNI.SmoothSphereHalfSpaceForce_get_force_visualization_scale_factor__SWIG_0(swigCPtr, this, i);
  }

  public SWIGTYPE_p_double upd_force_visualization_scale_factor(int i) {
    return new SWIGTYPE_p_double(opensimSimulationJNI.SmoothSphereHalfSpaceForce_upd_force_visualization_scale_factor__SWIG_0(swigCPtr, this, i), false);
  }

  public void set_force_visualization_scale_factor(int i, double value) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_set_force_visualization_scale_factor__SWIG_0(swigCPtr, this, i, value);
  }

  public int append_force_visualization_scale_factor(double value) {
    return opensimSimulationJNI.SmoothSphereHalfSpaceForce_append_force_visualization_scale_factor(swigCPtr, this, value);
  }

  public void constructProperty_force_visualization_scale_factor() {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_constructProperty_force_visualization_scale_factor__SWIG_0(swigCPtr, this);
  }

  public void constructProperty_force_visualization_scale_factor(double initValue) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_constructProperty_force_visualization_scale_factor__SWIG_1(swigCPtr, this, initValue);
  }

  public double get_force_visualization_scale_factor() {
    return opensimSimulationJNI.SmoothSphereHalfSpaceForce_get_force_visualization_scale_factor__SWIG_1(swigCPtr, this);
  }

  public SWIGTYPE_p_double upd_force_visualization_scale_factor() {
    return new SWIGTYPE_p_double(opensimSimulationJNI.SmoothSphereHalfSpaceForce_upd_force_visualization_scale_factor__SWIG_1(swigCPtr, this), false);
  }

  public void set_force_visualization_scale_factor(double value) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_set_force_visualization_scale_factor__SWIG_1(swigCPtr, this, value);
  }

  public void setPropertyIndex_socket_sphere(SWIGTYPE_p_PropertyIndex value) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_PropertyIndex_socket_sphere_set(swigCPtr, this, SWIGTYPE_p_PropertyIndex.getCPtr(value));
  }

  public SWIGTYPE_p_PropertyIndex getPropertyIndex_socket_sphere() {
    return new SWIGTYPE_p_PropertyIndex(opensimSimulationJNI.SmoothSphereHalfSpaceForce_PropertyIndex_socket_sphere_get(swigCPtr, this), true);
  }

  public void connectSocket_sphere(OpenSimObject object) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_connectSocket_sphere(swigCPtr, this, OpenSimObject.getCPtr(object), object);
  }

  public void setPropertyIndex_socket_half_space(SWIGTYPE_p_PropertyIndex value) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_PropertyIndex_socket_half_space_set(swigCPtr, this, SWIGTYPE_p_PropertyIndex.getCPtr(value));
  }

  public SWIGTYPE_p_PropertyIndex getPropertyIndex_socket_half_space() {
    return new SWIGTYPE_p_PropertyIndex(opensimSimulationJNI.SmoothSphereHalfSpaceForce_PropertyIndex_socket_half_space_get(swigCPtr, this), true);
  }

  public void connectSocket_half_space(OpenSimObject object) {
    opensimSimulationJNI.SmoothSphereHalfSpaceForce_connectSocket_half_space(swigCPtr, this, OpenSimObject.getCPtr(object), object);
  }

  public SmoothSphereHalfSpaceForce() {
    this(opensimSimulationJNI.new_SmoothSphereHalfSpaceForce__SWIG_0(), true);
  }

  public SmoothSphereHalfSpaceForce(String name, ContactSphere contactSphere, ContactHalfSpace contactHalfSpace) {
    this(opensimSimulationJNI.new_SmoothSphereHalfSpaceForce__SWIG_1(name, ContactSphere.getCPtr(contactSphere), contactSphere, ContactHalfSpace.getCPtr(contactHalfSpace), contactHalfSpace), true);
  }

  /**
   *  Obtain names of the quantities (column labels) of the force values to<br>
   *  be reported. The order is the three forces (XYZ) and three torques (XYZ)<br>
   *  applied on the sphere followed by the three forces (XYZ) and three<br>
   *  torques (XYZ) applied on the half space. Forces and torques are<br>
   *  expressed in the ground frame.
   */
  public ArrayStr getRecordLabels() {
    return new ArrayStr(opensimSimulationJNI.SmoothSphereHalfSpaceForce_getRecordLabels(swigCPtr, this), true);
  }

  /**
   *  Obtain the values to be reported that correspond to the labels. The<br>
   *  values are expressed in the ground frame.
   */
  public ArrayDouble getRecordValues(State state) {
    return new ArrayDouble(opensimSimulationJNI.SmoothSphereHalfSpaceForce_getRecordValues(swigCPtr, this, State.getCPtr(state), state), true);
  }

}
