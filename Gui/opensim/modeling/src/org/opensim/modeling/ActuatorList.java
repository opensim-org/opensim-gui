/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class ActuatorList {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected ActuatorList(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(ActuatorList obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimSimulationJNI.delete_ActuatorList(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public ActuatorList(Component root, ComponentFilter f) {
    this(opensimSimulationJNI.new_ActuatorList__SWIG_0(Component.getCPtr(root), root, ComponentFilter.getCPtr(f), f), true);
  }

  public ActuatorList(Component root) {
    this(opensimSimulationJNI.new_ActuatorList__SWIG_1(Component.getCPtr(root), root), true);
  }

  public ActuatorIterator begin() {
    return new ActuatorIterator(opensimSimulationJNI.ActuatorList_begin__SWIG_0(swigCPtr, this), true);
  }

  public SWIGTYPE_p_OpenSim__ComponentListIteratorT_std__add_constT_OpenSim__Actuator_const_t__type_t cbegin() {
    return new SWIGTYPE_p_OpenSim__ComponentListIteratorT_std__add_constT_OpenSim__Actuator_const_t__type_t(opensimSimulationJNI.ActuatorList_cbegin(swigCPtr, this), true);
  }

  public ActuatorIterator end() {
    return new ActuatorIterator(opensimSimulationJNI.ActuatorList_end__SWIG_0(swigCPtr, this), true);
  }

  public SWIGTYPE_p_OpenSim__ComponentListIteratorT_std__add_constT_OpenSim__Actuator_const_t__type_t cend() {
    return new SWIGTYPE_p_OpenSim__ComponentListIteratorT_std__add_constT_OpenSim__Actuator_const_t__type_t(opensimSimulationJNI.ActuatorList_cend(swigCPtr, this), true);
  }

  public void setFilter(ComponentFilter filter) {
    opensimSimulationJNI.ActuatorList_setFilter(swigCPtr, this, ComponentFilter.getCPtr(filter), filter);
  }

}
