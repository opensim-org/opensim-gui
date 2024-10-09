/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.1.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 * A concrete class representing a path (muscle, ligament, etc.) based on <br>
 * geometry objects in the model (e.g., PathPoints and PathWraps).<br>
 * <br>
 * @author Peter Loan
 */
public class GeometryPath extends AbstractGeometryPath {
  private transient long swigCPtr;

  public GeometryPath(long cPtr, boolean cMemoryOwn) {
    super(opensimSimulationJNI.GeometryPath_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(GeometryPath obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  public static long swigRelease(GeometryPath obj) {
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
        opensimSimulationJNI.delete_GeometryPath(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static GeometryPath safeDownCast(OpenSimObject obj) {
    long cPtr = opensimSimulationJNI.GeometryPath_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new GeometryPath(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimSimulationJNI.GeometryPath_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimSimulationJNI.GeometryPath_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimSimulationJNI.GeometryPath_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new GeometryPath(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimSimulationJNI.GeometryPath_getConcreteClassName(swigCPtr, this);
  }

  public GeometryPath() {
    this(opensimSimulationJNI.new_GeometryPath(), true);
  }

  public PathPointSet getPathPointSet() {
    return new PathPointSet(opensimSimulationJNI.GeometryPath_getPathPointSet(swigCPtr, this), false);
  }

  public PathPointSet updPathPointSet() {
    return new PathPointSet(opensimSimulationJNI.GeometryPath_updPathPointSet(swigCPtr, this), false);
  }

  public PathWrapSet getWrapSet() {
    return new PathWrapSet(opensimSimulationJNI.GeometryPath_getWrapSet(swigCPtr, this), false);
  }

  public PathWrapSet updWrapSet() {
    return new PathWrapSet(opensimSimulationJNI.GeometryPath_updWrapSet(swigCPtr, this), false);
  }

  public void addPathWrap(WrapObject aWrapObject) {
    opensimSimulationJNI.GeometryPath_addPathWrap(swigCPtr, this, WrapObject.getCPtr(aWrapObject), aWrapObject);
  }

  public AbstractPathPoint addPathPoint(State s, int index, PhysicalFrame frame) {
    long cPtr = opensimSimulationJNI.GeometryPath_addPathPoint(swigCPtr, this, State.getCPtr(s), s, index, PhysicalFrame.getCPtr(frame), frame);
    return (cPtr == 0) ? null : new AbstractPathPoint(cPtr, false);
  }

  public AbstractPathPoint appendNewPathPoint(String proposedName, PhysicalFrame frame, Vec3 locationOnFrame) {
    long cPtr = opensimSimulationJNI.GeometryPath_appendNewPathPoint(swigCPtr, this, proposedName, PhysicalFrame.getCPtr(frame), frame, Vec3.getCPtr(locationOnFrame), locationOnFrame);
    return (cPtr == 0) ? null : new AbstractPathPoint(cPtr, false);
  }

  public boolean canDeletePathPoint(int index) {
    return opensimSimulationJNI.GeometryPath_canDeletePathPoint(swigCPtr, this, index);
  }

  public boolean deletePathPoint(State s, int index) {
    return opensimSimulationJNI.GeometryPath_deletePathPoint(swigCPtr, this, State.getCPtr(s), s, index);
  }

  public void moveUpPathWrap(State s, int index) {
    opensimSimulationJNI.GeometryPath_moveUpPathWrap(swigCPtr, this, State.getCPtr(s), s, index);
  }

  public void moveDownPathWrap(State s, int index) {
    opensimSimulationJNI.GeometryPath_moveDownPathWrap(swigCPtr, this, State.getCPtr(s), s, index);
  }

  public void deletePathWrap(State s, int index) {
    opensimSimulationJNI.GeometryPath_deletePathWrap(swigCPtr, this, State.getCPtr(s), s, index);
  }

  public boolean replacePathPoint(State s, AbstractPathPoint oldPathPoint, AbstractPathPoint newPathPoint) {
    return opensimSimulationJNI.GeometryPath_replacePathPoint(swigCPtr, this, State.getCPtr(s), s, AbstractPathPoint.getCPtr(oldPathPoint), oldPathPoint, AbstractPathPoint.getCPtr(newPathPoint), newPathPoint);
  }

  /**
   *  %Set the value of the color cache variable owned by this %GeometryPath<br>
   *     object, in the cache of the given state. The value of this variable is used<br>
   *     as the color when the path is drawn, which occurs with the state realized <br>
   *     to Stage::Dynamics. So you must call this method during realizeDynamics() or <br>
   *     earlier in order for it to have any effect. *
   */
  public void setColor(State s, Vec3 color) {
    opensimSimulationJNI.GeometryPath_setColor(swigCPtr, this, State.getCPtr(s), s, Vec3.getCPtr(color), color);
  }

  /**
   *  Get the current value of the color cache entry owned by this<br>
   *     %GeometryPath object in the given state. You can access this value any time<br>
   *     after the state is initialized, at which point it will have been set to<br>
   *     the default color value specified in a call to setDefaultColor() earlier,<br>
   *     or it will have the default color value chosen by %GeometryPath.<br>
   *     @see setDefaultColor() *
   */
  public Vec3 getColor(State s) {
    return new Vec3(opensimSimulationJNI.GeometryPath_getColor(swigCPtr, this, State.getCPtr(s), s), true);
  }

  public double getLength(State s) {
    return opensimSimulationJNI.GeometryPath_getLength(swigCPtr, this, State.getCPtr(s), s);
  }

  public void setLength(State s, double length) {
    opensimSimulationJNI.GeometryPath_setLength(swigCPtr, this, State.getCPtr(s), s, length);
  }

  public ArrayPathPoint getCurrentPath(State s) {
    return new ArrayPathPoint(opensimSimulationJNI.GeometryPath_getCurrentPath(swigCPtr, this, State.getCPtr(s), s), false);
  }

  public double getLengtheningSpeed(State s) {
    return opensimSimulationJNI.GeometryPath_getLengtheningSpeed(swigCPtr, this, State.getCPtr(s), s);
  }

  public void setLengtheningSpeed(State s, double speed) {
    opensimSimulationJNI.GeometryPath_setLengtheningSpeed(swigCPtr, this, State.getCPtr(s), s, speed);
  }

  /**
   *  get the path as PointForceDirections directions, which can be used<br>
   *         to apply tension to bodies the points are connected to.
   */
  public void getPointForceDirections(State s, ArrayPointForceDirection rPFDs) {
    opensimSimulationJNI.GeometryPath_getPointForceDirections(swigCPtr, this, State.getCPtr(s), s, ArrayPointForceDirection.getCPtr(rPFDs), rPFDs);
  }

  /**
   * Requests forces resulting from applying a tension along its path and<br>
   * emits them into the supplied `ForceConsumer`.<br>
   * <br>
   * @param state         the state used to evaluate forces<br>
   * @param tension       scalar of the applied (+ve) tensile force<br>
   * @param forceConsumer a `ForceConsumer` shall receive each produced force
   */
  public void produceForces(State state, double tension, SWIGTYPE_p_OpenSim__ForceConsumer forceConsumer) {
    opensimSimulationJNI.GeometryPath_produceForces(swigCPtr, this, State.getCPtr(state), state, tension, SWIGTYPE_p_OpenSim__ForceConsumer.getCPtr(forceConsumer));
  }

  public boolean isVisualPath() {
    return opensimSimulationJNI.GeometryPath_isVisualPath(swigCPtr, this);
  }

  public double computeMomentArm(State s, Coordinate aCoord) {
    return opensimSimulationJNI.GeometryPath_computeMomentArm(swigCPtr, this, State.getCPtr(s), s, Coordinate.getCPtr(aCoord), aCoord);
  }

  /**
   *  Calculate the path length in the current body position and store it for<br>
   *         use after the Model has been scaled. 
   */
  public void extendPreScale(State s, ScaleSet scaleSet) {
    opensimSimulationJNI.GeometryPath_extendPreScale(swigCPtr, this, State.getCPtr(s), s, ScaleSet.getCPtr(scaleSet), scaleSet);
  }

  /**
   *  Recalculate the path after the Model has been scaled. 
   */
  public void extendPostScale(State s, ScaleSet scaleSet) {
    opensimSimulationJNI.GeometryPath_extendPostScale(swigCPtr, this, State.getCPtr(s), s, ScaleSet.getCPtr(scaleSet), scaleSet);
  }

  public void updateGeometry(State s) {
    opensimSimulationJNI.GeometryPath_updateGeometry(swigCPtr, this, State.getCPtr(s), s);
  }

}
