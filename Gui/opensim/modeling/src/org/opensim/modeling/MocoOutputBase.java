/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.1.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 *  This abstract base class provides convenience methods and common interfaces<br>
 * for all Output-related MocoGoal's. All MocoGoal's deriving from this class<br>
 * include the 'setOutputPath()', 'setSecondOutputPath()', 'setOperation()',<br>
 * 'setOutputIndex()', and 'setExponent()' methods and their corresponding Object<br>
 * properties. The convenience method 'initializeOnModelBase()' should be called at<br>
 * the top of 'initializeOnModelImpl()' within each derived class. Similarly,<br>
 * 'calcOutputValue()' can be used to retrieve the Output value with<br>
 * 'calcGoalImpl()' and/or 'calcIntegrandImpl()', as needed for each derived class.<br>
 * The method 'getDependsOnStage()' returns the SimTK::Stage that should be realized<br>
 * to to calculate Output values. The method 'setValueToExponent()' can be used to<br>
 * raise a value to the exponent provided via 'setExponent()'.<br>
 * <br>
 * Goals can be composed of one or two Outputs. The optional second Output can be<br>
 * included by using the methods 'setSecondOutputPath()' and 'setOperation()'. The<br>
 * Output values can be combined by addition, subtraction, multiplication, or<br>
 * division. The first Output is always on the left hand side of the operation and<br>
 * the second Output on the right hand side. The two Outputs can be different<br>
 * quantities, but they must be the same type.<br>
 * <br>
 * We support the following Output types:<br>
 * - double<br>
 * - SimTK::Vec3<br>
 * - SimTK::SpatialVec<br>
 * <br>
 * When using SimTK::Vec3 or SimTK::SpatialVec types, 'setOutputIndex()' may be<br>
 * used to select a specific element of the Output vector. If no index is<br>
 * specified, the norm of the vector will be used when calling 'calcOutputValue()'.<br>
 * <br>
 * If using two Outputs, the Output index will be used to select the same element<br>
 * from both Outputs before the operation. If two Outputs of type SimTK::Vec3 or<br>
 * SimTK::SpatialVec are provided and no index is specified, the operation will be<br>
 * applied elementwise before computing the norm. Elementwise<br>
 * multiplication and division operations are not supported when using two<br>
 * SimTK::SpatialVec Outputs (i.e., an index must be provided).<br>
 * 
 */
public class MocoOutputBase extends MocoGoal {
  private transient long swigCPtr;

  public MocoOutputBase(long cPtr, boolean cMemoryOwn) {
    super(opensimMocoJNI.MocoOutputBase_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(MocoOutputBase obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  public static long swigRelease(MocoOutputBase obj) {
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
        opensimMocoJNI.delete_MocoOutputBase(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static MocoOutputBase safeDownCast(OpenSimObject obj) {
    long cPtr = opensimMocoJNI.MocoOutputBase_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new MocoOutputBase(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimMocoJNI.MocoOutputBase_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimMocoJNI.MocoOutputBase_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimMocoJNI.MocoOutputBase_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new MocoOutputBase(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimMocoJNI.MocoOutputBase_getConcreteClassName(swigCPtr, this);
  }

  /**
   *  Set the absolute path to the Output in the model. The format is<br>
   *     "/path/to/component|output_name". 
   */
  public void setOutputPath(String path) {
    opensimMocoJNI.MocoOutputBase_setOutputPath(swigCPtr, this, path);
  }

  public String getOutputPath() {
    return opensimMocoJNI.MocoOutputBase_getOutputPath(swigCPtr, this);
  }

  /**
   *  Set the absolute path to the optional second Output in the model. The<br>
   *     format is "/path/to/component|output_name". This Output should have the same<br>
   *     type as the first Output. If providing a second Output, the user must also<br>
   *     provide an operation via `setOperation()`. 
   */
  public void setSecondOutputPath(String path) {
    opensimMocoJNI.MocoOutputBase_setSecondOutputPath(swigCPtr, this, path);
  }

  public String getSecondOutputPath() {
    return opensimMocoJNI.MocoOutputBase_getSecondOutputPath(swigCPtr, this);
  }

  /**
   *  Set the operation that combines Output values where two Outputs are<br>
   *     provided. The supported operations include "addition", "subtraction",<br>
   *     "multiplication", or "division". If providing an operation, the user<br>
   *     must also provide a second Output path. 
   */
  public void setOperation(String operation) {
    opensimMocoJNI.MocoOutputBase_setOperation(swigCPtr, this, operation);
  }

  public String getOperation() {
    return opensimMocoJNI.MocoOutputBase_getOperation(swigCPtr, this);
  }

  /**
   *  Set the exponent applied to the output value in the integrand. This<br>
   *     exponent is applied when minimizing the norm of a vector type output. The<br>
   *     default exponent is set to 1, meaning that the output can take on negative<br>
   *     values in the integrand. When the exponent is set to a value greater than<br>
   *     1, the absolute value function is applied to the output (before the<br>
   *     exponent is applied), meaning that odd numbered exponents (greater than 1)<br>
   *     do not take on negative values. 
   */
  public void setExponent(int exponent) {
    opensimMocoJNI.MocoOutputBase_setExponent(swigCPtr, this, exponent);
  }

  public int getExponent() {
    return opensimMocoJNI.MocoOutputBase_getExponent(swigCPtr, this);
  }

  /**
   *  Set the index to the value to be minimized when a vector type Output is<br>
   *     specified. For SpatialVec Outputs, indices 0, 1, and 2 refer to the<br>
   *     rotational components and indices 3, 4, and 5 refer to the translational<br>
   *     components. A value of -1 indicates to minimize the vector norm (which is the<br>
   *     default setting). If an index for a type double Output is provided, an<br>
   *     exception is thrown. 
   */
  public void setOutputIndex(int index) {
    opensimMocoJNI.MocoOutputBase_setOutputIndex(swigCPtr, this, index);
  }

  public int getOutputIndex() {
    return opensimMocoJNI.MocoOutputBase_getOutputIndex(swigCPtr, this);
  }

}
