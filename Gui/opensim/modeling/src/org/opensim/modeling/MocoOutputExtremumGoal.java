/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.1.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 *  This goal permits the integration of only positive or negative values from a<br>
 * model Output. This goal allows you to use model Outputs of type double, or a <br>
 * single specified element from an Output of type SimTK::Vec3 or <br>
 * SimTK::SpatialVec in the integrand of a goal. You can also specify the exponent<br>
 * of the value in the integrand via 'setExponent()'. <br>
 * <br>
 * This goal performs a smooth approximation of the common 'minimum' or 'maximum'<br>
 * extremum functions and then calculates the resulting integral. <br>
 * The goal is computed as follows:<br>
 * <br>
 *  
\frac{1}{dm} \int_{t_i}^{t_f} 
    w_v\beta((\frac{1}{s} (\ln (1 + \exp (s\betav))))^p) ~dt
<br>
 * We use the following notation:<br>
 * -   d : displacement of the system, if `divide_by_displacement` is<br>
 *   true; 1 otherwise.<br>
 * -   m : mass of the system, if `divide_by_mass` is<br>
 *   true; 1 otherwise.<br>
 * -   v : the output variable of choice.<br>
 * -   w_v : the weight for output variable   v .<br>
 * -   \beta : the approximate extremum to be taken (== -1 for<br>
 *   minimum; == 1 for maximum).<br>
 * -   s : the smoothing factor for approximating the extremum. With<br>
 *     s  == 1 the approximation is closer to the true extremum taken.<br>
 *   For   v  with potentially large magnitudes (&gt; 2000) during a simulation<br>
 *   it is recommended to set this property as 0.2 to avoid Inf.<br>
 * -   p : the `exponent`.
 */
public class MocoOutputExtremumGoal extends MocoOutputBase {
  private transient long swigCPtr;

  public MocoOutputExtremumGoal(long cPtr, boolean cMemoryOwn) {
    super(opensimMocoJNI.MocoOutputExtremumGoal_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(MocoOutputExtremumGoal obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  public static long swigRelease(MocoOutputExtremumGoal obj) {
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
        opensimMocoJNI.delete_MocoOutputExtremumGoal(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static MocoOutputExtremumGoal safeDownCast(OpenSimObject obj) {
    long cPtr = opensimMocoJNI.MocoOutputExtremumGoal_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new MocoOutputExtremumGoal(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimMocoJNI.MocoOutputExtremumGoal_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimMocoJNI.MocoOutputExtremumGoal_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimMocoJNI.MocoOutputExtremumGoal_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new MocoOutputExtremumGoal(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimMocoJNI.MocoOutputExtremumGoal_getConcreteClassName(swigCPtr, this);
  }

  public MocoOutputExtremumGoal() {
    this(opensimMocoJNI.new_MocoOutputExtremumGoal__SWIG_0(), true);
  }

  public MocoOutputExtremumGoal(String name) {
    this(opensimMocoJNI.new_MocoOutputExtremumGoal__SWIG_1(name), true);
  }

  public MocoOutputExtremumGoal(String name, double weight) {
    this(opensimMocoJNI.new_MocoOutputExtremumGoal__SWIG_2(name, weight), true);
  }

  /**
   *  Set if the goal should be divided by the displacement of the system's<br>
   *     center of mass over the phase. 
   */
  public void setDivideByDisplacement(boolean tf) {
    opensimMocoJNI.MocoOutputExtremumGoal_setDivideByDisplacement(swigCPtr, this, tf);
  }

  public boolean getDivideByDisplacement() {
    return opensimMocoJNI.MocoOutputExtremumGoal_getDivideByDisplacement(swigCPtr, this);
  }

  /**
   *  Set if the goal should be divided by the total mass of the model. 
   */
  public void setDivideByMass(boolean tf) {
    opensimMocoJNI.MocoOutputExtremumGoal_setDivideByMass(swigCPtr, this, tf);
  }

  public boolean getDivideByMass() {
    return opensimMocoJNI.MocoOutputExtremumGoal_getDivideByMass(swigCPtr, this);
  }

  /**
   *  Set the type of extremum ('minimum' or 'maximum') to be applied to the <br>
   *     output variable of choice. 
   */
  public void setExtremumType(String extremum_type) {
    opensimMocoJNI.MocoOutputExtremumGoal_setExtremumType(swigCPtr, this, extremum_type);
  }

  public String getExtremumType() {
    return opensimMocoJNI.MocoOutputExtremumGoal_getExtremumType(swigCPtr, this);
  }

  /**
   *  Set the smoothing factor used for the extremum approximation<br>
   *     (default = 1.0). This property can be set between [0.2, 1.0]. For Outputs<br>
   *     that may take on large values (&gt; ~2000) during a simulation, it is<br>
   *     recommended to set this property closer to 0.2.
   */
  public void setSmoothingFactor(double smoothing_factor) {
    opensimMocoJNI.MocoOutputExtremumGoal_setSmoothingFactor(swigCPtr, this, smoothing_factor);
  }

  public double getSmoothingFactor() {
    return opensimMocoJNI.MocoOutputExtremumGoal_getSmoothingFactor(swigCPtr, this);
  }

}
