/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.1.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 * A class implementing a ConstantCurvatureJoint joint. A ConstantCurvatureJoint<br>
 * connects two bodies by a line segment of a fixed length. The endpoint of the<br>
 * ConstantCurvatureJoint can be rotated by euler angles, and the offset is<br>
 * computed as a function of the euler angles and the fixed length of the line<br>
 * segment.<br>
 * <br>
 * This joint was originally designed as a lightweight way to model spine segments,<br>
 * which can be approximated without individual link segments be instead using 3 of<br>
 * these joints in series.<br>
 * <br>
 * @author Keenon Werling
 */
public class ConstantCurvatureJoint extends Joint {
  private transient long swigCPtr;

  public ConstantCurvatureJoint(long cPtr, boolean cMemoryOwn) {
    super(opensimSimulationJNI.ConstantCurvatureJoint_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(ConstantCurvatureJoint obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  public static long swigRelease(ConstantCurvatureJoint obj) {
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
        opensimSimulationJNI.delete_ConstantCurvatureJoint(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static ConstantCurvatureJoint safeDownCast(OpenSimObject obj) {
    long cPtr = opensimSimulationJNI.ConstantCurvatureJoint_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new ConstantCurvatureJoint(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimSimulationJNI.ConstantCurvatureJoint_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimSimulationJNI.ConstantCurvatureJoint_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimSimulationJNI.ConstantCurvatureJoint_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new ConstantCurvatureJoint(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimSimulationJNI.ConstantCurvatureJoint_getConcreteClassName(swigCPtr, this);
  }

  public void copyProperty_neutral_angle_x_z_y(ConstantCurvatureJoint source) {
    opensimSimulationJNI.ConstantCurvatureJoint_copyProperty_neutral_angle_x_z_y(swigCPtr, this, ConstantCurvatureJoint.getCPtr(source), source);
  }

  public Vec3 get_neutral_angle_x_z_y(int i) {
    return new Vec3(opensimSimulationJNI.ConstantCurvatureJoint_get_neutral_angle_x_z_y__SWIG_0(swigCPtr, this, i), false);
  }

  public Vec3 upd_neutral_angle_x_z_y(int i) {
    return new Vec3(opensimSimulationJNI.ConstantCurvatureJoint_upd_neutral_angle_x_z_y__SWIG_0(swigCPtr, this, i), false);
  }

  public void set_neutral_angle_x_z_y(int i, Vec3 value) {
    opensimSimulationJNI.ConstantCurvatureJoint_set_neutral_angle_x_z_y__SWIG_0(swigCPtr, this, i, Vec3.getCPtr(value), value);
  }

  public int append_neutral_angle_x_z_y(Vec3 value) {
    return opensimSimulationJNI.ConstantCurvatureJoint_append_neutral_angle_x_z_y(swigCPtr, this, Vec3.getCPtr(value), value);
  }

  public void constructProperty_neutral_angle_x_z_y(Vec3 initValue) {
    opensimSimulationJNI.ConstantCurvatureJoint_constructProperty_neutral_angle_x_z_y(swigCPtr, this, Vec3.getCPtr(initValue), initValue);
  }

  public Vec3 get_neutral_angle_x_z_y() {
    return new Vec3(opensimSimulationJNI.ConstantCurvatureJoint_get_neutral_angle_x_z_y__SWIG_1(swigCPtr, this), false);
  }

  public Vec3 upd_neutral_angle_x_z_y() {
    return new Vec3(opensimSimulationJNI.ConstantCurvatureJoint_upd_neutral_angle_x_z_y__SWIG_1(swigCPtr, this), false);
  }

  public void set_neutral_angle_x_z_y(Vec3 value) {
    opensimSimulationJNI.ConstantCurvatureJoint_set_neutral_angle_x_z_y__SWIG_1(swigCPtr, this, Vec3.getCPtr(value), value);
  }

  public void copyProperty_length(ConstantCurvatureJoint source) {
    opensimSimulationJNI.ConstantCurvatureJoint_copyProperty_length(swigCPtr, this, ConstantCurvatureJoint.getCPtr(source), source);
  }

  public double get_length(int i) {
    return opensimSimulationJNI.ConstantCurvatureJoint_get_length__SWIG_0(swigCPtr, this, i);
  }

  public SWIGTYPE_p_double upd_length(int i) {
    return new SWIGTYPE_p_double(opensimSimulationJNI.ConstantCurvatureJoint_upd_length__SWIG_0(swigCPtr, this, i), false);
  }

  public void set_length(int i, double value) {
    opensimSimulationJNI.ConstantCurvatureJoint_set_length__SWIG_0(swigCPtr, this, i, value);
  }

  public int append_length(double value) {
    return opensimSimulationJNI.ConstantCurvatureJoint_append_length(swigCPtr, this, value);
  }

  public void constructProperty_length(double initValue) {
    opensimSimulationJNI.ConstantCurvatureJoint_constructProperty_length(swigCPtr, this, initValue);
  }

  public double get_length() {
    return opensimSimulationJNI.ConstantCurvatureJoint_get_length__SWIG_1(swigCPtr, this);
  }

  public SWIGTYPE_p_double upd_length() {
    return new SWIGTYPE_p_double(opensimSimulationJNI.ConstantCurvatureJoint_upd_length__SWIG_1(swigCPtr, this), false);
  }

  public void set_length(double value) {
    opensimSimulationJNI.ConstantCurvatureJoint_set_length__SWIG_1(swigCPtr, this, value);
  }

  public ConstantCurvatureJoint() {
    this(opensimSimulationJNI.new_ConstantCurvatureJoint__SWIG_0(), true);
  }

  /**
   *  Convenience Joint like Constructor 
   */
  public ConstantCurvatureJoint(String name, PhysicalFrame parent, PhysicalFrame child, Vec3 neutralAngleXZY, double length) {
    this(opensimSimulationJNI.new_ConstantCurvatureJoint__SWIG_1(name, PhysicalFrame.getCPtr(parent), parent, PhysicalFrame.getCPtr(child), child, Vec3.getCPtr(neutralAngleXZY), neutralAngleXZY, length), true);
  }

  /**
   *  Deprecated Joint Constructor<br>
   *         NOTE(keenon): This constructor seems necessary to compile, but it has a<br>
   *         comment marking it deprecated in the EllipsoidJoint, so I copied that<br>
   *         comment over here as well. 
   */
  public ConstantCurvatureJoint(String name, PhysicalFrame parent, Vec3 locationInParent, Vec3 orientationInParent, PhysicalFrame child, Vec3 locationInChild, Vec3 orientationInChild, Vec3 neutralAngleXZY, double length) {
    this(opensimSimulationJNI.new_ConstantCurvatureJoint__SWIG_2(name, PhysicalFrame.getCPtr(parent), parent, Vec3.getCPtr(locationInParent), locationInParent, Vec3.getCPtr(orientationInParent), orientationInParent, PhysicalFrame.getCPtr(child), child, Vec3.getCPtr(locationInChild), locationInChild, Vec3.getCPtr(orientationInChild), orientationInChild, Vec3.getCPtr(neutralAngleXZY), neutralAngleXZY, length), true);
  }

  public void setNeutralAngleXZY(Vec3 neutralAngleXZY) {
    opensimSimulationJNI.ConstantCurvatureJoint_setNeutralAngleXZY(swigCPtr, this, Vec3.getCPtr(neutralAngleXZY), neutralAngleXZY);
  }

  public void setLength(double length) {
    opensimSimulationJNI.ConstantCurvatureJoint_setLength(swigCPtr, this, length);
  }

  /**
   *  Convenience method to get a const reference to the Coordinate associated<br>
   *         with a single-degree-of-freedom Joint. If the Joint has more than one<br>
   *         Coordinate, you must use get_coordinates() or provide the appropriate<br>
   *         argument to the getCoordinate() method defined in the derived class. 
   */
  public Coordinate getCoordinate() {
    return new Coordinate(opensimSimulationJNI.ConstantCurvatureJoint_getCoordinate__SWIG_0_0(swigCPtr, this), false);
  }

  /**
   *  Convenience method to get a writable reference to the Coordinate<br>
   *         associated with a single-degree-of-freedom Joint. If the Joint has more<br>
   *         than one Coordinate, you must use upd_coordinates() or provide the<br>
   *         appropriate argument to the updCoordinate() method defined in the<br>
   *         derived class. 
   */
  public Coordinate updCoordinate() {
    return new Coordinate(opensimSimulationJNI.ConstantCurvatureJoint_updCoordinate__SWIG_0_0(swigCPtr, this), false);
  }

  /**
   *  Get a const reference to a Coordinate associated with this Joint.<br>
   *         @see Coord 
   */
  public Coordinate getCoordinate(ConstantCurvatureJoint.Coord idx) {
    return new Coordinate(opensimSimulationJNI.ConstantCurvatureJoint_getCoordinate__SWIG_1(swigCPtr, this, idx.swigValue()), false);
  }

  /**
   *  Get a writable reference to a Coordinate associated with this Joint.<br>
   *         @see Coord 
   */
  public Coordinate updCoordinate(ConstantCurvatureJoint.Coord idx) {
    return new Coordinate(opensimSimulationJNI.ConstantCurvatureJoint_updCoordinate__SWIG_1(swigCPtr, this, idx.swigValue()), false);
  }

  public void extendScale(State s, ScaleSet scaleSet) {
    opensimSimulationJNI.ConstantCurvatureJoint_extendScale(swigCPtr, this, State.getCPtr(s), s, ScaleSet.getCPtr(scaleSet), scaleSet);
  }

  /**
   *  This method will clamp an input set of joint angles q to the limits of<br>
   * the joint, and return the clamped vector.
   */
  public static Vec3 clamp(Vec3 q) {
    return new Vec3(opensimSimulationJNI.ConstantCurvatureJoint_clamp(Vec3.getCPtr(q), q), true);
  }

  /**
   *  This method will convert a vector of X,Z,Y rotations into the<br>
   * corresponding SO3 rotation matrix.
   */
  public static SWIGTYPE_p_SimTK__Rotation_T_SimTK__Real_t eulerXZYToMatrix(Vec3 _angle) {
    return new SWIGTYPE_p_SimTK__Rotation_T_SimTK__Real_t(opensimSimulationJNI.ConstantCurvatureJoint_eulerXZYToMatrix(Vec3.getCPtr(_angle), _angle), true);
  }

  /**
   *  This method will convert an SO3 rotation matrix into a corresponding<br>
   * vector of X,Z,Y rotations.
   */
  public static Mat33 eulerXZYToMatrixGrad(Vec3 _angle, int index) {
    return new Mat33(opensimSimulationJNI.ConstantCurvatureJoint_eulerXZYToMatrixGrad(Vec3.getCPtr(_angle), _angle, index), true);
  }

  /**
   *  This method will return the Jacobian of a pure Euler joint (following<br>
   * the XZY convention), where each column gives the derivative of the<br>
   * spatial (SE3) coordinates for the joint transform wrt one degree of<br>
   * freedom of the joint (so there are 3, and each is of dimension 6).
   */
  public static SWIGTYPE_p_SimTK__MatT_6_3_double_6_1_t getEulerJacobian(Vec3 q) {
    return new SWIGTYPE_p_SimTK__MatT_6_3_double_6_1_t(opensimSimulationJNI.ConstantCurvatureJoint_getEulerJacobian(Vec3.getCPtr(q), q), true);
  }

  /**
   *  This method will return the derivative of the matrix returned by<br>
   * getEulerJacobian, with respect to changes to the `index` DOF of the<br>
   * joint. This is the same shape as the original matrix, because we take<br>
   * the derivative of every entry of the matrix separately.
   */
  public static SWIGTYPE_p_SimTK__MatT_6_3_double_6_1_t getEulerJacobianDerivWrtPos(Vec3 q, int index) {
    return new SWIGTYPE_p_SimTK__MatT_6_3_double_6_1_t(opensimSimulationJNI.ConstantCurvatureJoint_getEulerJacobianDerivWrtPos(Vec3.getCPtr(q), q, index), true);
  }

  /**
   *  This is much like getEulerJacobian(), because the rotational component<br>
   * is exactly the same, but the translational component is now non-zero.<br>
   * This takes as input the length of the line segment, `d`.
   */
  public static SWIGTYPE_p_SimTK__MatT_6_3_double_6_1_t getConstantCurveJacobian(Vec3 pos, double d) {
    return new SWIGTYPE_p_SimTK__MatT_6_3_double_6_1_t(opensimSimulationJNI.ConstantCurvatureJoint_getConstantCurveJacobian(Vec3.getCPtr(pos), pos, d), true);
  }

  /**
   *  This method will return the derivative of the matrix returned by<br>
   * getConstantCurveJacobian, with respect to changes to the `index` DOF of<br>
   * the joint. This is the same shape as the original matrix, because we<br>
   * take the derivative of every entry of the matrix separately.
   */
  public static SWIGTYPE_p_SimTK__MatT_6_3_double_6_1_t getConstantCurveJacobianDerivWrtPosition(Vec3 pos, double d, int index) {
    return new SWIGTYPE_p_SimTK__MatT_6_3_double_6_1_t(opensimSimulationJNI.ConstantCurvatureJoint_getConstantCurveJacobianDerivWrtPosition(Vec3.getCPtr(pos), pos, d, index), true);
  }

  /**
   *  This method will return the derivative of the matrix returned by<br>
   * getConstantCurveJacobian, with respect to time (changes to every element<br>
   * in `pos` at rate `dPos`). This is the same shape as the original matrix,<br>
   * because we take the derivative of every entry of the matrix separately.
   */
  public static SWIGTYPE_p_SimTK__MatT_6_3_double_6_1_t getConstantCurveJacobianDerivWrtTime(Vec3 pos, Vec3 dPos, double d) {
    return new SWIGTYPE_p_SimTK__MatT_6_3_double_6_1_t(opensimSimulationJNI.ConstantCurvatureJoint_getConstantCurveJacobianDerivWrtTime(Vec3.getCPtr(pos), pos, Vec3.getCPtr(dPos), dPos, d), true);
  }

  /**
   *  This computes a transform for a given DOF position (XZY euler rotation)<br>
   * and line segment length. 
   */
  public static Transform getTransform(Vec3 pos, double d) {
    return new Transform(opensimSimulationJNI.ConstantCurvatureJoint_getTransform(Vec3.getCPtr(pos), pos, d), true);
  }

  /**
   *  Indices of Coordinates for use as arguments to getCoordinate() and<br>
   *         updCoordinate().<br>
   * <br>
   *         <b>C++ example</b><br>
   *         {@code 
          const auto& rx = myConstantCurvatureJoint.getCoordinate(
                              ConstantCurvatureJoint::Coord::Rotation1X);
          }<br>
   * <br>
   *         <b>Python example</b><br>
   *         {@code 
          import opensim
          rx = myConstantCurvatureJoint.getCoordinate(
                      opensim.ConstantCurvatureJoint.Coord_Rotation1X)
          }<br>
   * <br>
   *         <b>Java example</b><br>
   *         {@code 
          rx = myConstantCurvatureJoint.getCoordinate(
                      ConstantCurvatureJoint.Coord.Rotation1X);
          }<br>
   * <br>
   *         <b>MATLAB example</b><br>
   *         {@code 
          rx = myConstantCurvatureJoint.get_coordinates(0);
          }<br>
   * <br>
   *         Note: <br>
   *         The joint has an **X-Z-Y** rotation ordering. We use this ordering<br>
   *         because that means the first two DOFs are rotation and translation,<br>
   *         while the last DOF is merely a "twist" of the segment without any<br>
   *         translation. This is also for compatibility with the original<br>
   *         implementation in Nimble Physics.
   */
  public final static class Coord {
    /**
     *  0
     */
    public final static ConstantCurvatureJoint.Coord RotationX = new ConstantCurvatureJoint.Coord("RotationX", opensimSimulationJNI.ConstantCurvatureJoint_Coord_RotationX_get());
    /**
     *  1
     */
    public final static ConstantCurvatureJoint.Coord RotationZ = new ConstantCurvatureJoint.Coord("RotationZ", opensimSimulationJNI.ConstantCurvatureJoint_Coord_RotationZ_get());
    /**
     *  2
     */
    public final static ConstantCurvatureJoint.Coord RotationY = new ConstantCurvatureJoint.Coord("RotationY", opensimSimulationJNI.ConstantCurvatureJoint_Coord_RotationY_get());

    public final int swigValue() {
      return swigValue;
    }

    public String toString() {
      return swigName;
    }

    public static Coord swigToEnum(int swigValue) {
      if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
        return swigValues[swigValue];
      for (int i = 0; i < swigValues.length; i++)
        if (swigValues[i].swigValue == swigValue)
          return swigValues[i];
      throw new IllegalArgumentException("No enum " + Coord.class + " with value " + swigValue);
    }

    private Coord(String swigName) {
      this.swigName = swigName;
      this.swigValue = swigNext++;
    }

    private Coord(String swigName, int swigValue) {
      this.swigName = swigName;
      this.swigValue = swigValue;
      swigNext = swigValue+1;
    }

    private Coord(String swigName, Coord swigEnum) {
      this.swigName = swigName;
      this.swigValue = swigEnum.swigValue;
      swigNext = this.swigValue+1;
    }

    private static Coord[] swigValues = { RotationX, RotationZ, RotationY };
    private static int swigNext = 0;
    private final int swigValue;
    private final String swigName;
  }

}
