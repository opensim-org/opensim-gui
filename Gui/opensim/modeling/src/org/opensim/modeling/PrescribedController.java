/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.1.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 * PrescribedController is a concrete Controller that specifies functions that <br>
 * prescribe the control values of its actuators as a function of time.<br>
 * <br>
 * The control functions are specified in the `ControlFunctions` property. Use <br>
 * `prescribeControlForActuator()` to assign a control function to an actuator <br>
 * based on the name or path of the actuator. After connecting the controller to <br>
 * the model, the added control function will be placed at the correct index in <br>
 * the `ControlFunctions` property. If modifying the `ControlFunctions` property<br>
 * directly, the number and order of functions must match the number and order <br>
 * of actuators connected to the controller. However, it is recommended to use<br>
 * `prescribeControlForActuator()` to ensure the correct mapping between <br>
 * actuator and control function.<br>
 * <br>
 * When loading from file, the order of the control functions in the file must<br>
 * match the order of actuators connected to the controller. If <br>
 * `prescribeControlForActuator()` is used to assign control functions, the <br>
 * control functions will be stored in the correct order in the <br>
 * `ControlFunctions` when saving the controller to file (since they are <br>
 * reordered as described above).<br>
 * <br>
 * A controls storage file can be specified in the `controls_file` property.<br>
 * Each column must be either the name or path of an actuator in the model. If<br>
 * the actuator name is used as the column label, the first actuator with a <br>
 * matching name will be connected to the controller and assigned a control <br>
 * function based on the column data. Using actuator paths in the column labels<br>
 * is recommended to avoid ambiguity. Finally, any actuators with existing <br>
 * control functions will be ignored when setting controls from file.<br>
 * <br>
 * Note: Prior to OpenSim 4.6, PrescribedController support setting a prescribed<br>
 *       control based on the actuator's index in the `ControlFunctions`<br>
 *       property. This interface is deprecated and will be removed in a future<br>
 *       release.<br>
 * <br>
 * @author Ajay Seth
 */
public class PrescribedController extends Controller {
  private transient long swigCPtr;

  public PrescribedController(long cPtr, boolean cMemoryOwn) {
    super(opensimSimulationJNI.PrescribedController_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(PrescribedController obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  public static long swigRelease(PrescribedController obj) {
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
        opensimSimulationJNI.delete_PrescribedController(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

    public void prescribeControlForActuator(int index, Function prescribedFunction) {
       prescribedFunction.markAdopted();
       prescribeControlForActuator_private(index, prescribedFunction);
    }

    public void prescribeControlForActuator(String name, Function prescribedFunction) {
       prescribedFunction.markAdopted();
       prescribeControlForActuator_private(name, prescribedFunction);
    }

  public static PrescribedController safeDownCast(OpenSimObject obj) {
    long cPtr = opensimSimulationJNI.PrescribedController_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new PrescribedController(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimSimulationJNI.PrescribedController_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimSimulationJNI.PrescribedController_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimSimulationJNI.PrescribedController_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new PrescribedController(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimSimulationJNI.PrescribedController_getConcreteClassName(swigCPtr, this);
  }

  public void copyProperty_ControlFunctions(PrescribedController source) {
    opensimSimulationJNI.PrescribedController_copyProperty_ControlFunctions(swigCPtr, this, PrescribedController.getCPtr(source), source);
  }

  public FunctionSet get_ControlFunctions(int i) {
    return new FunctionSet(opensimSimulationJNI.PrescribedController_get_ControlFunctions__SWIG_0(swigCPtr, this, i), false);
  }

  public FunctionSet upd_ControlFunctions(int i) {
    return new FunctionSet(opensimSimulationJNI.PrescribedController_upd_ControlFunctions__SWIG_0(swigCPtr, this, i), false);
  }

  public void set_ControlFunctions(int i, FunctionSet value) {
    opensimSimulationJNI.PrescribedController_set_ControlFunctions__SWIG_0(swigCPtr, this, i, FunctionSet.getCPtr(value), value);
  }

  public int append_ControlFunctions(FunctionSet value) {
    return opensimSimulationJNI.PrescribedController_append_ControlFunctions(swigCPtr, this, FunctionSet.getCPtr(value), value);
  }

  public void constructProperty_ControlFunctions(FunctionSet initValue) {
    opensimSimulationJNI.PrescribedController_constructProperty_ControlFunctions(swigCPtr, this, FunctionSet.getCPtr(initValue), initValue);
  }

  public FunctionSet get_ControlFunctions() {
    return new FunctionSet(opensimSimulationJNI.PrescribedController_get_ControlFunctions__SWIG_1(swigCPtr, this), false);
  }

  public FunctionSet upd_ControlFunctions() {
    return new FunctionSet(opensimSimulationJNI.PrescribedController_upd_ControlFunctions__SWIG_1(swigCPtr, this), false);
  }

  public void set_ControlFunctions(FunctionSet value) {
    opensimSimulationJNI.PrescribedController_set_ControlFunctions__SWIG_1(swigCPtr, this, FunctionSet.getCPtr(value), value);
  }

  public void copyProperty_controls_file(PrescribedController source) {
    opensimSimulationJNI.PrescribedController_copyProperty_controls_file(swigCPtr, this, PrescribedController.getCPtr(source), source);
  }

  public String get_controls_file(int i) {
    return opensimSimulationJNI.PrescribedController_get_controls_file__SWIG_0(swigCPtr, this, i);
  }

  public SWIGTYPE_p_std__string upd_controls_file(int i) {
    return new SWIGTYPE_p_std__string(opensimSimulationJNI.PrescribedController_upd_controls_file__SWIG_0(swigCPtr, this, i), false);
  }

  public void set_controls_file(int i, String value) {
    opensimSimulationJNI.PrescribedController_set_controls_file__SWIG_0(swigCPtr, this, i, value);
  }

  public int append_controls_file(String value) {
    return opensimSimulationJNI.PrescribedController_append_controls_file(swigCPtr, this, value);
  }

  public void constructProperty_controls_file() {
    opensimSimulationJNI.PrescribedController_constructProperty_controls_file__SWIG_0(swigCPtr, this);
  }

  public void constructProperty_controls_file(String initValue) {
    opensimSimulationJNI.PrescribedController_constructProperty_controls_file__SWIG_1(swigCPtr, this, initValue);
  }

  public String get_controls_file() {
    return opensimSimulationJNI.PrescribedController_get_controls_file__SWIG_1(swigCPtr, this);
  }

  public SWIGTYPE_p_std__string upd_controls_file() {
    return new SWIGTYPE_p_std__string(opensimSimulationJNI.PrescribedController_upd_controls_file__SWIG_1(swigCPtr, this), false);
  }

  public void set_controls_file(String value) {
    opensimSimulationJNI.PrescribedController_set_controls_file__SWIG_1(swigCPtr, this, value);
  }

  public void copyProperty_interpolation_method(PrescribedController source) {
    opensimSimulationJNI.PrescribedController_copyProperty_interpolation_method(swigCPtr, this, PrescribedController.getCPtr(source), source);
  }

  public int get_interpolation_method(int i) {
    return opensimSimulationJNI.PrescribedController_get_interpolation_method__SWIG_0(swigCPtr, this, i);
  }

  public SWIGTYPE_p_int upd_interpolation_method(int i) {
    return new SWIGTYPE_p_int(opensimSimulationJNI.PrescribedController_upd_interpolation_method__SWIG_0(swigCPtr, this, i), false);
  }

  public void set_interpolation_method(int i, int value) {
    opensimSimulationJNI.PrescribedController_set_interpolation_method__SWIG_0(swigCPtr, this, i, value);
  }

  public int append_interpolation_method(int value) {
    return opensimSimulationJNI.PrescribedController_append_interpolation_method(swigCPtr, this, value);
  }

  public void constructProperty_interpolation_method() {
    opensimSimulationJNI.PrescribedController_constructProperty_interpolation_method__SWIG_0(swigCPtr, this);
  }

  public void constructProperty_interpolation_method(int initValue) {
    opensimSimulationJNI.PrescribedController_constructProperty_interpolation_method__SWIG_1(swigCPtr, this, initValue);
  }

  public int get_interpolation_method() {
    return opensimSimulationJNI.PrescribedController_get_interpolation_method__SWIG_1(swigCPtr, this);
  }

  public SWIGTYPE_p_int upd_interpolation_method() {
    return new SWIGTYPE_p_int(opensimSimulationJNI.PrescribedController_upd_interpolation_method__SWIG_1(swigCPtr, this), false);
  }

  public void set_interpolation_method(int value) {
    opensimSimulationJNI.PrescribedController_set_interpolation_method__SWIG_1(swigCPtr, this, value);
  }

  /**
   *  Default constructor 
   */
  public PrescribedController() {
    this(opensimSimulationJNI.new_PrescribedController__SWIG_0(), true);
  }

  /**
   *  Convenience constructor get controls from file<br>
   * @param controlsFileName  string containing the controls storage (.sto) <br>
   * @param interpMethodType  int 0-constant, 1-linear, 3-cubic, 5-quintic<br>
   *                          defaults to linear.
   */
  public PrescribedController(String controlsFileName, int interpMethodType) {
    this(opensimSimulationJNI.new_PrescribedController__SWIG_1(controlsFileName, interpMethodType), true);
  }

  /**
   *  Convenience constructor get controls from file<br>
   * @param controlsFileName  string containing the controls storage (.sto) <br>
   * 
   */
  public PrescribedController(String controlsFileName) {
    this(opensimSimulationJNI.new_PrescribedController__SWIG_2(controlsFileName), true);
  }

  /**
   * Compute the control values for all actuators under the control of this<br>
   * Controller.<br>
   * <br>
   * @param s             system state <br>
   * @param controls      model controls  
   */
  public void computeControls(State s, Vector controls) {
    opensimSimulationJNI.PrescribedController_computeControls(swigCPtr, this, State.getCPtr(s), s, Vector.getCPtr(controls), controls);
  }

  /**
   *  Assign a prescribed control function for the desired actuator identified<br>
   *  by the provided label. The label can be either the name of the actuator,<br>
   *  or the absolute path to the actuator in the model.<br>
   *  @param actuLabel            label for the actuator in the controller<br>
   *  @param prescribedFunction   the actuator's control function<br>
   * <br>
   *  Note: As of OpenSim 4.6, PrescribedController no longer takes ownership<br>
   *        of the passed in Function and instead makes a copy.
   */
  public void prescribeControlForActuator_private(String actuLabel, Function prescribedFunction) {
    opensimSimulationJNI.PrescribedController_prescribeControlForActuator_private__SWIG_0(swigCPtr, this, actuLabel, Function.getCPtr(prescribedFunction), prescribedFunction);
  }

  public void prescribeControlForActuator_private(int index, Function prescribedFunction) {
    opensimSimulationJNI.PrescribedController_prescribeControlForActuator_private__SWIG_1(swigCPtr, this, index, Function.getCPtr(prescribedFunction), prescribedFunction);
  }

}
