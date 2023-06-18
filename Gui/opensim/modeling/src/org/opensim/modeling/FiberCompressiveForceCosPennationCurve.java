/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.1.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 *  This class serves as a serializable FiberCompressiveForceCosPennationCurve, <br>
 *  which is used to ensure that the pennation angle approaches but never reaches<br>
 *  an angle of 90 degrees. Preventing the fibers from achieving a pennation <br>
 *  angle of 90 degrees is important for equilibrium muscle models which<br>
 *  have a singularity at this value.<br>
 * <br>
 *  This curve is designed to work with the muscle model<br>
 *  in such a way that it acts like a spring that the pennated muscle fibers <br>
 *  contact it as the fiber rotates (circled in red). When the spring engages it <br>
 *  will exert a force on the fiber that will prevent it from shortening further, <br>
 *  thus preventing the pennation angle from reaching 90 degrees.<br>
 * <br>
 *  Note that this object should be updated through the set methods provided. <br>
 *  These set methods will take care of rebuilding the curve correctly. If you<br>
 *  modify the properties directly, the curve will not be rebuilt, and upon<br>
 *  calling a function like calcValue, calcDerivative, or printCurveToCSVFile<br>
 *  an exception will be thrown because the curve is out of date with its <br>
 *  properties.<br>
 * <br>
 *  <img src="fig_FiberCompressiveForceCosPennationCurve.png"/><br>
 * <br>
 *   @author Matt Millard
 */
public class FiberCompressiveForceCosPennationCurve extends Function {
  private transient long swigCPtr;

  public FiberCompressiveForceCosPennationCurve(long cPtr, boolean cMemoryOwn) {
    super(opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(FiberCompressiveForceCosPennationCurve obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  public static long swigRelease(FiberCompressiveForceCosPennationCurve obj) {
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
        opensimSimulationJNI.delete_FiberCompressiveForceCosPennationCurve(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static FiberCompressiveForceCosPennationCurve safeDownCast(OpenSimObject obj) {
    long cPtr = opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new FiberCompressiveForceCosPennationCurve(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new FiberCompressiveForceCosPennationCurve(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_getConcreteClassName(swigCPtr, this);
  }

  public void copyProperty_engagement_angle_in_degrees(FiberCompressiveForceCosPennationCurve source) {
    opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_copyProperty_engagement_angle_in_degrees(swigCPtr, this, FiberCompressiveForceCosPennationCurve.getCPtr(source), source);
  }

  public double get_engagement_angle_in_degrees(int i) {
    return opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_get_engagement_angle_in_degrees__SWIG_0(swigCPtr, this, i);
  }

  public SWIGTYPE_p_double upd_engagement_angle_in_degrees(int i) {
    return new SWIGTYPE_p_double(opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_upd_engagement_angle_in_degrees__SWIG_0(swigCPtr, this, i), false);
  }

  public void set_engagement_angle_in_degrees(int i, double value) {
    opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_set_engagement_angle_in_degrees__SWIG_0(swigCPtr, this, i, value);
  }

  public int append_engagement_angle_in_degrees(double value) {
    return opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_append_engagement_angle_in_degrees(swigCPtr, this, value);
  }

  public void constructProperty_engagement_angle_in_degrees(double initValue) {
    opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_constructProperty_engagement_angle_in_degrees(swigCPtr, this, initValue);
  }

  public double get_engagement_angle_in_degrees() {
    return opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_get_engagement_angle_in_degrees__SWIG_1(swigCPtr, this);
  }

  public SWIGTYPE_p_double upd_engagement_angle_in_degrees() {
    return new SWIGTYPE_p_double(opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_upd_engagement_angle_in_degrees__SWIG_1(swigCPtr, this), false);
  }

  public void set_engagement_angle_in_degrees(double value) {
    opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_set_engagement_angle_in_degrees__SWIG_1(swigCPtr, this, value);
  }

  public void copyProperty_stiffness_at_perpendicular(FiberCompressiveForceCosPennationCurve source) {
    opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_copyProperty_stiffness_at_perpendicular(swigCPtr, this, FiberCompressiveForceCosPennationCurve.getCPtr(source), source);
  }

  public double get_stiffness_at_perpendicular(int i) {
    return opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_get_stiffness_at_perpendicular__SWIG_0(swigCPtr, this, i);
  }

  public SWIGTYPE_p_double upd_stiffness_at_perpendicular(int i) {
    return new SWIGTYPE_p_double(opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_upd_stiffness_at_perpendicular__SWIG_0(swigCPtr, this, i), false);
  }

  public void set_stiffness_at_perpendicular(int i, double value) {
    opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_set_stiffness_at_perpendicular__SWIG_0(swigCPtr, this, i, value);
  }

  public int append_stiffness_at_perpendicular(double value) {
    return opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_append_stiffness_at_perpendicular(swigCPtr, this, value);
  }

  public void constructProperty_stiffness_at_perpendicular() {
    opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_constructProperty_stiffness_at_perpendicular__SWIG_0(swigCPtr, this);
  }

  public void constructProperty_stiffness_at_perpendicular(double initValue) {
    opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_constructProperty_stiffness_at_perpendicular__SWIG_1(swigCPtr, this, initValue);
  }

  public double get_stiffness_at_perpendicular() {
    return opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_get_stiffness_at_perpendicular__SWIG_1(swigCPtr, this);
  }

  public SWIGTYPE_p_double upd_stiffness_at_perpendicular() {
    return new SWIGTYPE_p_double(opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_upd_stiffness_at_perpendicular__SWIG_1(swigCPtr, this), false);
  }

  public void set_stiffness_at_perpendicular(double value) {
    opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_set_stiffness_at_perpendicular__SWIG_1(swigCPtr, this, value);
  }

  public void copyProperty_curviness(FiberCompressiveForceCosPennationCurve source) {
    opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_copyProperty_curviness(swigCPtr, this, FiberCompressiveForceCosPennationCurve.getCPtr(source), source);
  }

  public double get_curviness(int i) {
    return opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_get_curviness__SWIG_0(swigCPtr, this, i);
  }

  public SWIGTYPE_p_double upd_curviness(int i) {
    return new SWIGTYPE_p_double(opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_upd_curviness__SWIG_0(swigCPtr, this, i), false);
  }

  public void set_curviness(int i, double value) {
    opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_set_curviness__SWIG_0(swigCPtr, this, i, value);
  }

  public int append_curviness(double value) {
    return opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_append_curviness(swigCPtr, this, value);
  }

  public void constructProperty_curviness() {
    opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_constructProperty_curviness__SWIG_0(swigCPtr, this);
  }

  public void constructProperty_curviness(double initValue) {
    opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_constructProperty_curviness__SWIG_1(swigCPtr, this, initValue);
  }

  public double get_curviness() {
    return opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_get_curviness__SWIG_1(swigCPtr, this);
  }

  public SWIGTYPE_p_double upd_curviness() {
    return new SWIGTYPE_p_double(opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_upd_curviness__SWIG_1(swigCPtr, this), false);
  }

  public void set_curviness(double value) {
    opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_set_curviness__SWIG_1(swigCPtr, this, value);
  }

  /**
   *  Default constructor creates an curve with the default property values,<br>
   *     and assigns it a default name *
   */
  public FiberCompressiveForceCosPennationCurve() {
    this(opensimSimulationJNI.new_FiberCompressiveForceCosPennationCurve__SWIG_0(), true);
  }

  /**
   *  Constructs a C2 continuous compressive fiber force cos pennation curve.<br>
   *     The sole purpose of this curve is to prevent the pennation angle from<br>
   *     reaching an angle of 90 degrees. Details to appear in Millard et al. 2012.<br>
   * <br>
   *     @param engagementAngleInDegrees<br>
   *             The pennation angle engagement angle of the fiber compressive<br>
   *             force pennation curve. Making the spring engage too<br>
   *             far from 90 degrees may unrealistically limit the force<br>
   *             production capability of the muscle. An engagement angle of<br>
   *             80 degrees is a good place to start.<br>
   * <br>
   *     @param stiffnessAtPerpendicular<br>
   *             This is the stiffness of the compressive elastic force length<br>
   *             spring when the pennation angle reaches 90 degrees. Note that<br>
   *             the units of this stiffness are<br>
   *             (normalized force) / cos(engagmentAngleInDegrees). If the<br>
   *             engagement angle is 80 degrees, a good stiffness to start with<br>
   *             is -2*(1/cosd(engagementAngleInDegrees))<br>
   * <br>
   *     @param curviness<br>
   *             A dimensionless parameter between [0-1] that controls how<br>
   *             the curve is drawn: 0 will create a curve that is<br>
   *             very close to a straight line segment while a value of 1 will<br>
   *             create a curve that smoothly fills the corner formed by the<br>
   *             linear extrapolation of 'stiffnessAtPerpendicularFiber' and the<br>
   *             x axis as shown in the figure. A good curviness parameter value<br>
   *             to start with is 0.5.<br>
   * <br>
   *     @param muscleName<br>
   *             The name of the muscle this curve belongs to. This name is used<br>
   *             to create the name of this curve, which is formed simply by<br>
   *             appending "_FiberCompressiveForceCosPennationCurve" to the<br>
   *             string in muscleName. This name is used for making intelligible<br>
   *             error messages and also for naming the XML version of this curve<br>
   *             when it is serialized.<br>
   * <br>
   *     <b>Conditions</b><br>
   *     {@literal 
          0 < engagmentAngleInDegrees < 90
          stiffnessAtPerpendicular < -1/engagmentAngleInDegrees
          0 <= curviness <= 1
      }<br>
   * <br>
   *     <b>Computational Costs</b><br>
   *     {@literal 
          ~174,100 flops
      }<br>
   * <br>
   *     <b>Default Parameter Values</b><br>
   *     {@literal 
          engagmentAngleInDegrees = 80
      }
   */
  public FiberCompressiveForceCosPennationCurve(double engagementAngleInDegrees, double stiffnessAtPerpendicular, double curviness, String muscleName) {
    this(opensimSimulationJNI.new_FiberCompressiveForceCosPennationCurve__SWIG_1(engagementAngleInDegrees, stiffnessAtPerpendicular, curviness, muscleName), true);
  }

  /**
   *      Constructs a C2 continuous compressive fiber force cos pennation curve <br>
   *      using only the mandatory property, engagmentAngleInDegrees. The<br>
   *      sole purpose of this curve is to prevent the pennation angle from reaching<br>
   *      an angle of 90 degrees. Details to appear in Millard et al. 2012.<br>
   * <br>
   *     @param engagementAngleInDegrees<br>
   *                 The pennation angle engagement angle of the fiber compressive<br>
   *                 force pennation curve. Making the spring engage too<br>
   *                 far from 90 degrees may unrealistically limit the force <br>
   *                 production capability of the muscle. An engagement angle of <br>
   *                 80 degrees is a good place to start.<br>
   * <br>
   *     @param muscleName<br>
   *                 The name of the muscle this curve belongs to. This name is used<br>
   *                 to create the name of this curve, which is formed simply by <br>
   *                 appending "_FiberCompressiveForceCosPennationCurve" to the <br>
   *                 string in muscleName. This name is used for making intelligible <br>
   *                 error messages and also for naming the XML version of this curve <br>
   *                 when it is serialized.<br>
   * <br>
   *     <b> Optional Parameters </b><br>
   *         If the optional parameters have not yet been set, they are computed when<br>
   *         functions getStiffnessAtPerpendicularInUse(), and getCurvinessInUse()<br>
   *         are called. See the documentation for these functions for details<br>
   * <br>
   *     <b>Conditions:</b><br>
   *         {@literal 
              0 < engagmentAngleInDegrees < 90
          }<br>
   * <br>
   *     <b>Computational Costs</b><br>
   *         {@literal  
              ~174,100 flops
          }<br>
   * <br>
   *     <b> Default Parameter Values </b><br>
   * <br>
   * <br>
   *          {@literal 
               engagmentAngleInDegrees = 80 
           }<br>
   * <br>
   *     <b>Example:</b>
   */
  public FiberCompressiveForceCosPennationCurve(double engagementAngleInDegrees, String muscleName) {
    this(opensimSimulationJNI.new_FiberCompressiveForceCosPennationCurve__SWIG_2(engagementAngleInDegrees, muscleName), true);
  }

  /**
   *     @return The pennation angle engagement angle of the fiber compressive<br>
   *                 force pennation curve. 
   */
  public double getEngagementAngleInDegrees() {
    return opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_getEngagementAngleInDegrees(swigCPtr, this);
  }

  /**
   *      @return This is the stiffness of the compressive elastic force length<br>
   *                 spring when the pennation angle reaches 90 degrees. If this <br>
   *                 property has been set, the property value is returned. If this<br>
   *                 property is empty, then a value is computed and returned. The<br>
   *                 value is computed using the following:<br>
   * <br>
   *                 {@literal 
                  stiffnessAtPerpendicular = -2 * 1/cosd(engagementAngleInDegrees)        
                  }<br>
   * <br>
   *                 where cosd is a cosine function that takes its argument in units<br>
   *                 of degrees
   */
  public double getStiffnessAtPerpendicularInUse() {
    return opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_getStiffnessAtPerpendicularInUse(swigCPtr, this);
  }

  /**
   *      @return A dimensionless parameter between [0-1] that controls how <br>
   *                 the curve is drawn: 0 will create a curve that is<br>
   *                 very close to a straight line segment while a value of 1 will <br>
   *                 create a curve that smoothly fills the corner formed by the <br>
   *                 linear extrapolation of 'StiffnessAtPerpendicularFiber'.<br>
   * <br>
   *                 If this property is empty, then a value is computed and <br>
   *                 returned. The value is computed using the following:<br>
   * <br>
   *                 {@literal 
                  curviness = 0.1       
                  }
   */
  public double getCurvinessInUse() {
    return opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_getCurvinessInUse(swigCPtr, this);
  }

  /**
   *      @return true if the internal fitting routine (which takes only one <br>
   *      argument, the engagementAngleInDegrees) is being used. False is returned if<br>
   *      the user has set the optional parameters.
   */
  public boolean isFittedCurveBeingUsed() {
    return opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_isFittedCurveBeingUsed(swigCPtr, this);
  }

  /**
   *     @param aEngagementAngleInDegrees<br>
   *             Sets the pennation angle engagement angle of the fiber compressive<br>
   *             force pennation curve. <br>
   *     <b>Cost </b><br>
   *      The curve is rebuilt at a cost of ~174,100 flops
   */
  public void setEngagementAngleInDegrees(double aEngagementAngleInDegrees) {
    opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_setEngagementAngleInDegrees(swigCPtr, this, aEngagementAngleInDegrees);
  }

  /**
   *      @param aStiffnessAtPerpendicular<br>
   *             This is the stiffness of the compressive elastic force length<br>
   *             spring when the pennation angle reaches 90 degrees.<br>
   * <br>
   *      @param aCurviness  <br>
   *                 A dimensionless parameter between [0-1] that controls how <br>
   *                 the curve is drawn: 0 will create a curve that is<br>
   *                 very close to a straight line segment while a value of 1 will <br>
   *                 create a curve that smoothly fills the corner formed by the <br>
   *                 linear extrapolation of 'stiffnessAtOneNormForce' and the<br>
   *                 x axis as shown in the figure.<br>
   * <br>
   *      <b>Cost </b><br>
   *      The curve is rebuilt at a cost of ~174,100 flops
   */
  public void setOptionalProperties(double aStiffnessAtPerpendicular, double aCurviness) {
    opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_setOptionalProperties(swigCPtr, this, aStiffnessAtPerpendicular, aCurviness);
  }

  /**
   *     Calculates the value of the curve evaluated at cosPennationAngle.     <br>
   *     @param cosPennationAngle: The cosine of the fiber pennation angle  <br>
   * <br>
   *     @return the normalized force generated by the compressive force element <br>
   * <br>
   * <br>
   *     <b>Computational Costs</b><br>
   *     {@literal 
          x in curve domain  : ~282 flops
          x in linear section:   ~5 flops
      }
   */
  public double calcValue(double cosPennationAngle) {
    return opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_calcValue__SWIG_0(swigCPtr, this, cosPennationAngle);
  }

  /**
   *  Implement the generic OpenSim::Function interface *
   */
  public double calcValue(Vector x) {
    return opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_calcValue__SWIG_1(swigCPtr, this, Vector.getCPtr(x), x);
  }

  /**
   *     Calculates the derivative of the fiber compressive force pennation angle <br>
   *     curve w.r.t. to cosPennationAngle. <br>
   * <br>
   *     @param cosPennationAngle: <br>
   *                 The cosine of the fiber pennation angle  <br>
   * <br>
   *     @param order: the order of the derivative. Only values of 0,1 and 2 are <br>
   *                   acceptable.<br>
   * <br>
   *     @return the derivative of the fiber compressive force pennation angle <br>
   *     curve w.r.t. to cosPennationAngle<br>
   * <br>
   *     <b>Computational Costs</b>       <br>
   *     {@literal 
          x in curve domain  : ~391 flops
          x in linear section:   ~2 flops       
      }
   */
  public double calcDerivative(double cosPennationAngle, int order) {
    return opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_calcDerivative__SWIG_0(swigCPtr, this, cosPennationAngle, order);
  }

  /**
   *  If possible, use the simpler overload above.
   */
  public double calcDerivative(StdVectorInt derivComponents, Vector x) {
    return opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_calcDerivative__SWIG_1(swigCPtr, this, StdVectorInt.getCPtr(derivComponents), derivComponents, Vector.getCPtr(x), x);
  }

  /**
   *     @param cosPennationAngle<br>
   *                 The cosine of the pennation angle<br>
   * <br>
   *     @return Computes the normalized area under the curve. For this curve, <br>
   *             this quantity corresponds to the normalized potential energy stored <br>
   *             in the fiber compressive force cos pennation spring - simply <br>
   *             multiply this quantity by the number of NormForce<br>
   *             (where NormForce corresponds to the number of<br>
   *             Newtons that 1 normalized force corresponds to) to obtain <br>
   *             the potential energy stored in the fiber in units of Joules. Note <br>
   *             that NormDistance is omitted because the length dimension of this <br>
   *             curve is not normalized, only the force dimension.<br>
   * <br>
   *     <b>Computational Costs</b>    <br>
   * <br>
   *     {@literal 
          x in curve domain  : ~13 flops
          x in linear section: ~19 flops
      }
   */
  public double calcIntegral(double cosPennationAngle) {
    return opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_calcIntegral(swigCPtr, this, cosPennationAngle);
  }

  /**
   *        This function returns a SimTK::Vec2 that contains in its 0th element<br>
   *        the lowest value of the curve domain, and in its 1st element the highest<br>
   *        value in the curve domain of the curve. Outside of this domain the curve<br>
   *        is approximated using linear extrapolation.<br>
   * <br>
   *        @return The minimum and maximum value of the domain, x, of the curve <br>
   *                   y(x). Within this range y(x) is a curve, outside of this range<br>
   *                   the function y(x) is a C2 (continuous to the second <br>
   *                   derivative) linear extrapolation
   */
  public Vec2 getCurveDomain() {
    return new Vec2(opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_getCurveDomain(swigCPtr, this), true);
  }

  /**
   * This function will generate a csv file with a name that matches the <br>
   *        curve name (e.g. "bicepfemoris_FiberCompressiveForceCosPennationCurve.csv").<br>
   *       This function is not const to permit the curve to be rebuilt if it is out of<br>
   *        date with its properties.<br>
   * <br>
   *        @param path The full path to the location. Note '/' slashes must be used,<br>
   *             and do not put a '/' after the last folder.<br>
   * <br>
   *        The file will contain the following columns:<br>
   * <br>
   *        {@literal 
         Col# 1, 2,     3,       4,  
              x, y, dy/dx, d2y/dx2,
         }<br>
   * <br>
   *        The curve will be sampled from its linear extrapolation region<br>
   *        (the region with normalized fiber velocities &lt; -1), through <br>
   *        the curve, out to the other linear extrapolation region<br>
   *        (the region with normalized fiber velocities &gt; 1). The width of <br>
   *        each linear extrapolation region is 10% of the entire range of x, or <br>
   *        0.1*(x1-x0).<br>
   * <br>
   *        The curve is sampled quite densely: there are 200+20 rows    <br>
   * <br>
   *        <b>Computational Costs</b><br>
   *        {@literal 
              ~194,800 flops
         }<br>
   * <br>
   *        <b>Example</b><br>
   *        To read the csv file with a header in from Matlab, you need to use <br>
   *        csvread set so that it will ignore the header row. This is accomplished<br>
   *        by using the extra two numerical arguments for csvread to tell the <br>
   *        function to begin reading from the 1st row, and the 0th index (csvread<br>
   *        is 0 indexed). This is necessary to skip reading in the text header<br>
   *        {@literal 
          data=csvread('bicepfemoris_FiberCompressiveForceCosPennationCurve.csv',1,0);
         }
   */
  public void printMuscleCurveToCSVFile(String path) {
    opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_printMuscleCurveToCSVFile(swigCPtr, this, path);
  }

  public void ensureCurveUpToDate() {
    opensimSimulationJNI.FiberCompressiveForceCosPennationCurve_ensureCurveUpToDate(swigCPtr, this);
  }

}
