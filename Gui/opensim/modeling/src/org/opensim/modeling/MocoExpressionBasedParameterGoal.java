/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.1.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 *  Minimize or constrain an arithmetic expression of parameters. <br>
 * <br>
 * This goal supports both "cost" and "endpoint constraint" modes and can be <br>
 * defined using any number of MocoParameters. The expression string should match <br>
 * the Lepton (lightweight expression parser) format.<br>
 * <br>
 * # Creating Expressions<br>
 * <br>
 * Expressions can be any string that represents a mathematical expression, e.g.,<br>
 * "x*sqrt(y-8)". Expressions can contain variables, constants, operations,<br>
 * parentheses, commas, spaces, and scientific "e" notation. The full list of<br>
 * operations is: sqrt, exp, log, sin, cos, sec, csc, tan, cot, asin, acos, atan, <br>
 * sinh, cosh, tanh, erf, erfc, step, delta, square, cube, recip, min, max, abs, <br>
 * +, -, *, /, and ^.<br>
 * <br>
 * # Examples<br>
 * <br>
 * {@code 
auto* spring1_parameter = mp.addParameter("spring_stiffness", "spring1",
                                       "stiffness", MocoBounds(0, 100));
auto* spring2_parameter = mp.addParameter("spring2_stiffness", "spring2",
                                       "stiffness", MocoBounds(0, 100));
auto* spring_goal = mp.addGoal<MocoExpressionBasedParameterGoal>();
double STIFFNESS = 100.0;
minimum is when p + q = STIFFNESS
spring_goal->setExpression(fmt::format("square(p+q-{})", STIFFNESS));
spring_goal->addParameter(*spring1_parameter, "p");
spring_goal->addParameter(*spring2_parameter, "q");
}<br>
 * <br>
 * 
 */
public class MocoExpressionBasedParameterGoal extends MocoGoal {
  private transient long swigCPtr;

  public MocoExpressionBasedParameterGoal(long cPtr, boolean cMemoryOwn) {
    super(opensimMocoJNI.MocoExpressionBasedParameterGoal_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(MocoExpressionBasedParameterGoal obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  public static long swigRelease(MocoExpressionBasedParameterGoal obj) {
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
        opensimMocoJNI.delete_MocoExpressionBasedParameterGoal(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static MocoExpressionBasedParameterGoal safeDownCast(OpenSimObject obj) {
    long cPtr = opensimMocoJNI.MocoExpressionBasedParameterGoal_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new MocoExpressionBasedParameterGoal(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimMocoJNI.MocoExpressionBasedParameterGoal_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimMocoJNI.MocoExpressionBasedParameterGoal_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimMocoJNI.MocoExpressionBasedParameterGoal_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new MocoExpressionBasedParameterGoal(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimMocoJNI.MocoExpressionBasedParameterGoal_getConcreteClassName(swigCPtr, this);
  }

  public MocoExpressionBasedParameterGoal() {
    this(opensimMocoJNI.new_MocoExpressionBasedParameterGoal__SWIG_0(), true);
  }

  public MocoExpressionBasedParameterGoal(String name) {
    this(opensimMocoJNI.new_MocoExpressionBasedParameterGoal__SWIG_1(name), true);
  }

  public MocoExpressionBasedParameterGoal(String name, double weight) {
    this(opensimMocoJNI.new_MocoExpressionBasedParameterGoal__SWIG_2(name, weight), true);
  }

  public MocoExpressionBasedParameterGoal(String name, double weight, String expression) {
    this(opensimMocoJNI.new_MocoExpressionBasedParameterGoal__SWIG_3(name, weight, expression), true);
  }

  /**
   *  Set the arithmetic expression to minimize or constrain. Variable <br>
   *     names should match the names set with addParameter(). See "Creating <br>
   *     Expressions" in the class documentation above for an explanation of how to <br>
   *     create expressions. 
   */
  public void setExpression(String expression) {
    opensimMocoJNI.MocoExpressionBasedParameterGoal_setExpression(swigCPtr, this, expression);
  }

  /**
   *  Add parameters with variable names that match the variables in the<br>
   *     expression string. All variables in the expression must have a corresponding<br>
   *     parameter, but parameters with variables that are not in the expression are<br>
   *     ignored. 
   */
  public void addParameter(MocoParameter parameter, String variable) {
    opensimMocoJNI.MocoExpressionBasedParameterGoal_addParameter(swigCPtr, this, MocoParameter.getCPtr(parameter), parameter, variable);
  }

}
