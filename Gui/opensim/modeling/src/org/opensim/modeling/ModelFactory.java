/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.1.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 *  This class provides utilities for creating OpenSim models.
 */
public class ModelFactory {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  public ModelFactory(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  public static long getCPtr(ModelFactory obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  public static long swigRelease(ModelFactory obj) {
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
        opensimActuatorsAnalysesToolsJNI.delete_ModelFactory(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  /**
   *  <br>
   *  Create a pendulum with the provided number of links.<br>
   *  For each link, there is a body `/bodyset/b#` (where `#` is the link<br>
   *  index starting at 0), a PinJoint `/jointset/j#` with coordinate<br>
   *  `/jointset/j#/q#`, a CoordinateActuator `/tau#`, a Marker<br>
   *  `/markerset/marker#` at the origin of the link's body, and a<br>
   *  PhysicalOffsetFrame <code>/b</code>#center at the center of the link.
   */
  public static Model createNLinkPendulum(int numLinks) {
    return new Model(opensimActuatorsAnalysesToolsJNI.ModelFactory_createNLinkPendulum(numLinks), true);
  }

  /**
   *  This is a convenience for `createNLinkPendulum(1)`.
   */
  public static Model createPendulum() {
    return new Model(opensimActuatorsAnalysesToolsJNI.ModelFactory_createPendulum(), true);
  }

  /**
   *  This is a convenience for `createNLinkPendulum(2)`.
   */
  public static Model createDoublePendulum() {
    return new Model(opensimActuatorsAnalysesToolsJNI.ModelFactory_createDoublePendulum(), true);
  }

  /**
   *  This model contains:<br>
   *  - 1 body: mass 1.0 kg, `/body`.<br>
   *  - 1 joint: SliderJoint along x axis, `/jointset/slider`, with<br>
   *             coordinate `/slider/position`.<br>
   *  - 1 actuator: CoordinateActuator, controls [-10, 10],<br>
   *               `/forceset/actuator`.<br>
   *  Gravity is default; that is, (0, -g, 0).
   */
  public static Model createSlidingPointMass() {
    return new Model(opensimActuatorsAnalysesToolsJNI.ModelFactory_createSlidingPointMass(), true);
  }

  /**
   *  This model contains:<br>
   *  - 2 bodies: a massless body "intermed", and "body" with mass 1.<br>
   *  - 2 slider joints: "tx" and "ty" (coordinates "tx" and "ty").<br>
   *  - 2 coordinate actuators: "force_x" and "force_y".<br>
   *  Gravity is default; that is, (0, -g, 0).
   */
  public static Model createPlanarPointMass() {
    return new Model(opensimActuatorsAnalysesToolsJNI.ModelFactory_createPlanarPointMass(), true);
  }

  /**
   *  <br>
   *  <br>
   *  Replace muscles in a model with a PathActuator of the same path,<br>
   *  optimal force, and min/max control defaults.<br>
   *  Note: This only replaces muscles within the model's ForceSet.
   */
  public static void replaceMusclesWithPathActuators(Model model) {
    opensimActuatorsAnalysesToolsJNI.ModelFactory_replaceMusclesWithPathActuators(Model.getCPtr(model), model);
  }

  /**
   *  Remove muscles from the model.<br>
   *  Note: This only removes muscles within the model's ForceSet.
   */
  public static void removeMuscles(Model model) {
    opensimActuatorsAnalysesToolsJNI.ModelFactory_removeMuscles(Model.getCPtr(model), model);
  }

  /**
   *  Replace a joint in the model with a WeldJoint.<br>
   *  Note: This assumes the joint is in the JointSet and that the joint's<br>
   *        connectees are PhysicalOffsetFrames.
   */
  public static void replaceJointWithWeldJoint(Model model, String jointName) {
    opensimActuatorsAnalysesToolsJNI.ModelFactory_replaceJointWithWeldJoint(Model.getCPtr(model), model, jointName);
  }

  /**
   *  Add CoordinateActuator%s for each unconstrained coordinate (e.g.,<br>
   *  `! Coordinate::isConstrained()`) in the model, using the provided optimal<br>
   *  force. Increasing the optimal force decreases the required control<br>
   *  signal to generate a given actuation level. The actuators are added to<br>
   *  the model's ForceSet and are named "reserve_<coordinate-path>" with<br>
   *  forward slashes converted to underscores. The `bound` argument, if<br>
   *  supplied, sets the min and max controls to `-bound` and `bound`,<br>
   *  respectively.<br>
   *  The fourth (optional) argument specifies whether or not to skip <br>
   *  coordinates that already have CoordinateActuator%s associated with <br>
   *  them (default: true).<br>
   *  The fifth (optional) argument specifies whether or not to skip<br>
   *  coordinates that are associated with joints whose parent body is Ground<br>
   *  (default: false).
   */
  public static void createReserveActuators(Model model, double optimalForce, double bound, boolean skipCoordinatesWithExistingActuators, boolean skipResidualCoordinates) {
    opensimActuatorsAnalysesToolsJNI.ModelFactory_createReserveActuators__SWIG_0(Model.getCPtr(model), model, optimalForce, bound, skipCoordinatesWithExistingActuators, skipResidualCoordinates);
  }

  /**
   *  Add CoordinateActuator%s for each unconstrained coordinate (e.g.,<br>
   *  `! Coordinate::isConstrained()`) in the model, using the provided optimal<br>
   *  force. Increasing the optimal force decreases the required control<br>
   *  signal to generate a given actuation level. The actuators are added to<br>
   *  the model's ForceSet and are named "reserve_<coordinate-path>" with<br>
   *  forward slashes converted to underscores. The `bound` argument, if<br>
   *  supplied, sets the min and max controls to `-bound` and `bound`,<br>
   *  respectively.<br>
   *  The fourth (optional) argument specifies whether or not to skip <br>
   *  coordinates that already have CoordinateActuator%s associated with <br>
   *  them (default: true).<br>
   *  The fifth (optional) argument specifies whether or not to skip<br>
   *  coordinates that are associated with joints whose parent body is Ground<br>
   *  (default: false).
   */
  public static void createReserveActuators(Model model, double optimalForce, double bound, boolean skipCoordinatesWithExistingActuators) {
    opensimActuatorsAnalysesToolsJNI.ModelFactory_createReserveActuators__SWIG_1(Model.getCPtr(model), model, optimalForce, bound, skipCoordinatesWithExistingActuators);
  }

  /**
   *  Add CoordinateActuator%s for each unconstrained coordinate (e.g.,<br>
   *  `! Coordinate::isConstrained()`) in the model, using the provided optimal<br>
   *  force. Increasing the optimal force decreases the required control<br>
   *  signal to generate a given actuation level. The actuators are added to<br>
   *  the model's ForceSet and are named "reserve_<coordinate-path>" with<br>
   *  forward slashes converted to underscores. The `bound` argument, if<br>
   *  supplied, sets the min and max controls to `-bound` and `bound`,<br>
   *  respectively.<br>
   *  The fourth (optional) argument specifies whether or not to skip <br>
   *  coordinates that already have CoordinateActuator%s associated with <br>
   *  them (default: true).<br>
   *  The fifth (optional) argument specifies whether or not to skip<br>
   *  coordinates that are associated with joints whose parent body is Ground<br>
   *  (default: false).
   */
  public static void createReserveActuators(Model model, double optimalForce, double bound) {
    opensimActuatorsAnalysesToolsJNI.ModelFactory_createReserveActuators__SWIG_2(Model.getCPtr(model), model, optimalForce, bound);
  }

  /**
   *  Add CoordinateActuator%s for each unconstrained coordinate (e.g.,<br>
   *  `! Coordinate::isConstrained()`) in the model, using the provided optimal<br>
   *  force. Increasing the optimal force decreases the required control<br>
   *  signal to generate a given actuation level. The actuators are added to<br>
   *  the model's ForceSet and are named "reserve_<coordinate-path>" with<br>
   *  forward slashes converted to underscores. The `bound` argument, if<br>
   *  supplied, sets the min and max controls to `-bound` and `bound`,<br>
   *  respectively.<br>
   *  The fourth (optional) argument specifies whether or not to skip <br>
   *  coordinates that already have CoordinateActuator%s associated with <br>
   *  them (default: true).<br>
   *  The fifth (optional) argument specifies whether or not to skip<br>
   *  coordinates that are associated with joints whose parent body is Ground<br>
   *  (default: false).
   */
  public static void createReserveActuators(Model model, double optimalForce) {
    opensimActuatorsAnalysesToolsJNI.ModelFactory_createReserveActuators__SWIG_3(Model.getCPtr(model), model, optimalForce);
  }

  /**
   *  Add CoordinateActuator%s for each unconstrained coordinate (e.g.,<br>
   *  `! Coordinate::isConstrained()`) in the model associated with joints <br>
   *  whose parent body is Ground. The optimal force for rotational and <br>
   *  translational coordinates can be set separately using the arguments<br>
   *  `rotationalOptimalForce` and `translationalOptimalForce`. The actuators<br>
   *  are added to the model's ForceSet and are named <br>
   *  "residual_<coordinate-path>" with forward slashes converted to<br>
   *  underscores. The `bound` argument, if supplied, sets the min and max<br>
   *  controls to `-bound` and `bound`, respectively.<br>
   *  The fifth (optional) argument specifies whether or not to skip<br>
   *  coordinates that already have CoordinateActuator%s associated with them<br>
   *  (default: true).
   */
  public static void createResidualActuators(Model model, double rotationalOptimalForce, double translationalOptimalForce, double bound, boolean skipCoordinatesWithExistingActuators) {
    opensimActuatorsAnalysesToolsJNI.ModelFactory_createResidualActuators__SWIG_0(Model.getCPtr(model), model, rotationalOptimalForce, translationalOptimalForce, bound, skipCoordinatesWithExistingActuators);
  }

  /**
   *  Add CoordinateActuator%s for each unconstrained coordinate (e.g.,<br>
   *  `! Coordinate::isConstrained()`) in the model associated with joints <br>
   *  whose parent body is Ground. The optimal force for rotational and <br>
   *  translational coordinates can be set separately using the arguments<br>
   *  `rotationalOptimalForce` and `translationalOptimalForce`. The actuators<br>
   *  are added to the model's ForceSet and are named <br>
   *  "residual_<coordinate-path>" with forward slashes converted to<br>
   *  underscores. The `bound` argument, if supplied, sets the min and max<br>
   *  controls to `-bound` and `bound`, respectively.<br>
   *  The fifth (optional) argument specifies whether or not to skip<br>
   *  coordinates that already have CoordinateActuator%s associated with them<br>
   *  (default: true).
   */
  public static void createResidualActuators(Model model, double rotationalOptimalForce, double translationalOptimalForce, double bound) {
    opensimActuatorsAnalysesToolsJNI.ModelFactory_createResidualActuators__SWIG_1(Model.getCPtr(model), model, rotationalOptimalForce, translationalOptimalForce, bound);
  }

  /**
   *  Add CoordinateActuator%s for each unconstrained coordinate (e.g.,<br>
   *  `! Coordinate::isConstrained()`) in the model associated with joints <br>
   *  whose parent body is Ground. The optimal force for rotational and <br>
   *  translational coordinates can be set separately using the arguments<br>
   *  `rotationalOptimalForce` and `translationalOptimalForce`. The actuators<br>
   *  are added to the model's ForceSet and are named <br>
   *  "residual_<coordinate-path>" with forward slashes converted to<br>
   *  underscores. The `bound` argument, if supplied, sets the min and max<br>
   *  controls to `-bound` and `bound`, respectively.<br>
   *  The fifth (optional) argument specifies whether or not to skip<br>
   *  coordinates that already have CoordinateActuator%s associated with them<br>
   *  (default: true).
   */
  public static void createResidualActuators(Model model, double rotationalOptimalForce, double translationalOptimalForce) {
    opensimActuatorsAnalysesToolsJNI.ModelFactory_createResidualActuators__SWIG_2(Model.getCPtr(model), model, rotationalOptimalForce, translationalOptimalForce);
  }

  /**
   *  Replace the paths of the forces in the model with the provided Set of<br>
   *  FunctionBasedPath%s. The name of each FunctionBasedPath should match the<br>
   *  component path (i.e., '/forceset/soleus_r') of the corresponding Force<br>
   *  in the model. The Force objects in the model must have a property named<br>
   *  'path' that stores an object derived from AbstractGeometryPath.
   */
  public static void replacePathsWithFunctionBasedPaths(Model model, SetFunctionBasedPaths functionBasedPaths) {
    opensimActuatorsAnalysesToolsJNI.ModelFactory_replacePathsWithFunctionBasedPaths(Model.getCPtr(model), model, SetFunctionBasedPaths.getCPtr(functionBasedPaths), functionBasedPaths);
  }

  public ModelFactory() {
    this(opensimActuatorsAnalysesToolsJNI.new_ModelFactory(), true);
  }

}
