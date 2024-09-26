/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.1.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 * A base class that represents a path that has a computable length and<br>
 * lengthening speed.<br>
 * <br>
 * This class is typically used in places where the model needs to simulate<br>
 * the changes in a path over time. For example, in `OpenSim::Muscle`s,<br>
 * `OpenSim::Ligament`s, etc.<br>
 * <br>
 * This class *only* defines a length and lengthening speed. We do not assume<br>
 * that an `OpenSim::AbstractGeometryPath` is a straight line between two points<br>
 * or assume that it is many straight lines between `n` points. The derived<br>
 * implementation may define a path using points, or it may define a path using<br>
 * a curve fit. It may also define a path based on analytical functions for the<br>
 * length and lengthening speed.
 */
public class AbstractGeometryPath extends ModelComponent {
  private transient long swigCPtr;

  public AbstractGeometryPath(long cPtr, boolean cMemoryOwn) {
    super(opensimSimulationJNI.AbstractGeometryPath_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(AbstractGeometryPath obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  public static long swigRelease(AbstractGeometryPath obj) {
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
        opensimSimulationJNI.delete_AbstractGeometryPath(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static AbstractGeometryPath safeDownCast(OpenSimObject obj) {
    long cPtr = opensimSimulationJNI.AbstractGeometryPath_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new AbstractGeometryPath(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimSimulationJNI.AbstractGeometryPath_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimSimulationJNI.AbstractGeometryPath_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimSimulationJNI.AbstractGeometryPath_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new AbstractGeometryPath(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimSimulationJNI.AbstractGeometryPath_getConcreteClassName(swigCPtr, this);
  }

  public void set_has_output_length(boolean value) {
    opensimSimulationJNI.AbstractGeometryPath__has_output_length_set(swigCPtr, this, value);
  }

  public boolean get_has_output_length() {
    return opensimSimulationJNI.AbstractGeometryPath__has_output_length_get(swigCPtr, this);
  }

  public void set_has_output_lengthening_speed(boolean value) {
    opensimSimulationJNI.AbstractGeometryPath__has_output_lengthening_speed_set(swigCPtr, this, value);
  }

  public boolean get_has_output_lengthening_speed() {
    return opensimSimulationJNI.AbstractGeometryPath__has_output_lengthening_speed_get(swigCPtr, this);
  }

  public void copyProperty_Appearance(AbstractGeometryPath source) {
    opensimSimulationJNI.AbstractGeometryPath_copyProperty_Appearance(swigCPtr, this, AbstractGeometryPath.getCPtr(source), source);
  }

  public Appearance get_Appearance(int i) {
    return new Appearance(opensimSimulationJNI.AbstractGeometryPath_get_Appearance__SWIG_0(swigCPtr, this, i), false);
  }

  public Appearance upd_Appearance(int i) {
    return new Appearance(opensimSimulationJNI.AbstractGeometryPath_upd_Appearance__SWIG_0(swigCPtr, this, i), false);
  }

  public void set_Appearance(int i, Appearance value) {
    opensimSimulationJNI.AbstractGeometryPath_set_Appearance__SWIG_0(swigCPtr, this, i, Appearance.getCPtr(value), value);
  }

  public int append_Appearance(Appearance value) {
    return opensimSimulationJNI.AbstractGeometryPath_append_Appearance(swigCPtr, this, Appearance.getCPtr(value), value);
  }

  public void constructProperty_Appearance(Appearance initValue) {
    opensimSimulationJNI.AbstractGeometryPath_constructProperty_Appearance(swigCPtr, this, Appearance.getCPtr(initValue), initValue);
  }

  public Appearance get_Appearance() {
    return new Appearance(opensimSimulationJNI.AbstractGeometryPath_get_Appearance__SWIG_1(swigCPtr, this), false);
  }

  public Appearance upd_Appearance() {
    return new Appearance(opensimSimulationJNI.AbstractGeometryPath_upd_Appearance__SWIG_1(swigCPtr, this), false);
  }

  public void set_Appearance(Appearance value) {
    opensimSimulationJNI.AbstractGeometryPath_set_Appearance__SWIG_1(swigCPtr, this, Appearance.getCPtr(value), value);
  }

  /**
   * Get the current length of the path.<br>
   * <br>
   * Internally, this may use a variety of methods to figure out how long the<br>
   * path is, such as using spline-fits, or computing the distance between<br>
   * points in space. It is up to concrete implementations (e.g.,<br>
   * `GeometryPath`) to provide a relevant implementation.
   */
  public double getLength(State s) {
    return opensimSimulationJNI.AbstractGeometryPath_getLength(swigCPtr, this, State.getCPtr(s), s);
  }

  /**
   * Get the lengthening speed of the path.<br>
   * <br>
   * Internally, this may use a variety of methods to figure out the<br>
   * lengthening speed. It might use the finite difference between two<br>
   * lengths, or an analytic solution, or always return `0.0`. It is up to<br>
   * concrete implementations (e.g., `GeometryPath`) to provide a relevant<br>
   * implementation.
   */
  public double getLengtheningSpeed(State s) {
    return opensimSimulationJNI.AbstractGeometryPath_getLengtheningSpeed(swigCPtr, this, State.getCPtr(s), s);
  }

  /**
   * Requests that the concrete implementation produces forces resulting from<br>
   * applying a tension along its path, emitting them into the supplied<br>
   * `ForceConsumer`.<br>
   * <br>
   * @param state         the state used to evaluate forces<br>
   * @param tension       scalar of the applied (+ve) tensile force<br>
   * @param forceConsumer a `ForceConsumer` shall receive each produced force
   */
  public void produceForces(State state, double tension, SWIGTYPE_p_OpenSim__ForceConsumer forceConsumer) {
    opensimSimulationJNI.AbstractGeometryPath_produceForces(swigCPtr, this, State.getCPtr(state), state, tension, SWIGTYPE_p_OpenSim__ForceConsumer.getCPtr(forceConsumer));
  }

  /**
   *  Add in the equivalent body and generalized forces to be applied to the<br>
   *  multibody system resulting from a tension along the AbstractGeometryPath.<br>
   * <br>
   * Note: this internally uses `produceForces`<br>
   * <br>
   *  @param state           state used to evaluate forces<br>
   *  @param tension         scalar of the applied (+ve) tensile force<br>
   *  @param bodyForces      Vector of forces (SpatialVec's) on bodies<br>
   *  @param mobilityForces  Vector of generalized forces
   */
  public void addInEquivalentForces(State state, double tension, VectorOfSpatialVec bodyForces, Vector mobilityForces) {
    opensimSimulationJNI.AbstractGeometryPath_addInEquivalentForces(swigCPtr, this, State.getCPtr(state), state, tension, VectorOfSpatialVec.getCPtr(bodyForces), bodyForces, Vector.getCPtr(mobilityForces), mobilityForces);
  }

  /**
   * Returns the moment arm of the path in the given state with respect to<br>
   * the specified coordinate.
   */
  public double computeMomentArm(State s, Coordinate aCoord) {
    return opensimSimulationJNI.AbstractGeometryPath_computeMomentArm(swigCPtr, this, State.getCPtr(s), s, Coordinate.getCPtr(aCoord), aCoord);
  }

  /**
   * Return whether or not a path can be visualized.<br>
   * <br>
   * Concrete implementations may be visualizable (e.g., `GeometryPath`) or<br>
   * they may not be and therefore must provide a relevant implementation. 
   */
  public boolean isVisualPath() {
    return opensimSimulationJNI.AbstractGeometryPath_isVisualPath(swigCPtr, this);
  }

  /**
   * Get the default color of the path.<br>
   * <br>
   * Returns the color that will be used to initialize the color cache<br>
   * at the next extendAddToSystem() call. Use `getColor` to retrieve the<br>
   * (potentially different) color that will be used to draw the path.
   */
  public Vec3 getDefaultColor() {
    return new Vec3(opensimSimulationJNI.AbstractGeometryPath_getDefaultColor(swigCPtr, this), false);
  }

  /**
   * Set the default color of the path.<br>
   * <br>
   * Sets the internal, default, color value for the path. This is the color<br>
   * that's used when the simulation is initialized (specifically, during the<br>
   * `extendAddToSystem` call).<br>
   * <br>
   * This color is not necessarily the *current* color of the path. Other code<br>
   * in the system (e.g. muscle implementations) may change the runtime color<br>
   * with `setColor`. Use `getColor`, with a particular simulation state, to<br>
   * get the color of the path in that state.
   */
  public void setDefaultColor(Vec3 color) {
    opensimSimulationJNI.AbstractGeometryPath_setDefaultColor(swigCPtr, this, Vec3.getCPtr(color), color);
  }

  /**
   * Get the current color of the path.<br>
   * <br>
   * This is the runtime, potentially state-dependent, color of the path. It<br>
   * is the color used to display the path in that state (e.g., for UI<br>
   * rendering).<br>
   * <br>
   * This color value is typically initialized with the default color (see:<br>
   * `getDefaultColor`), but the color can change between simulation states<br>
   * because downstream code (e.g. muscles) might call `setColor` to implement<br>
   * state-dependent path coloring.<br>
   * <br>
   * If not overridden in concrete implementations, this method returns the<br>
   * default color.
   */
  public Vec3 getColor(State s) {
    return new Vec3(opensimSimulationJNI.AbstractGeometryPath_getColor(swigCPtr, this, State.getCPtr(s), s), true);
  }

  /**
   * Set the current color of the path.<br>
   * <br>
   * Internally, sets the current color value of the path for the provided<br>
   * state (e.g. using cache variables).<br>
   * <br>
   * The value of this variable is used as the color when the path is drawn,<br>
   * which occurs with the state realized to Stage::Dynamics. Therefore, you<br>
   * must call this method during realizeDynamics() or earlier in order for it<br>
   * to have any effect.<br>
   * <br>
   * If not overridden in concrete implementations, this method does nothing.
   */
  public void setColor(State s, Vec3 color) {
    opensimSimulationJNI.AbstractGeometryPath_setColor(swigCPtr, this, State.getCPtr(s), s, Vec3.getCPtr(color), color);
  }

  /**
   * Get the current length of the path, *before* the last set of scaling<br>
   * operations were applied to it.<br>
   * <br>
   * Internally, the path stores the original length in a `double` during<br>
   * `extendPreScale`. Therefore, be *very* careful with this method, because<br>
   * the recorded length is dependent on the length as computed during<br>
   * `extendPreScale`, which may have been called with a different state.
   */
  public double getPreScaleLength(State s) {
    return opensimSimulationJNI.AbstractGeometryPath_getPreScaleLength(swigCPtr, this, State.getCPtr(s), s);
  }

  public void setPreScaleLength(State s, double preScaleLength) {
    opensimSimulationJNI.AbstractGeometryPath_setPreScaleLength(swigCPtr, this, State.getCPtr(s), s, preScaleLength);
  }

}
