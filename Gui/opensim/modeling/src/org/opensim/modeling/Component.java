/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class Component extends OpenSimObject {
  private transient long swigCPtr;

  protected Component(long cPtr, boolean cMemoryOwn) {
    super(opensimCommonJNI.Component_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected static long getCPtr(Component obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimCommonJNI.delete_Component(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public void addComponent(Component comp) {
      comp.markAdopted();
      private_addComponent(comp);
  }

  public static Component safeDownCast(OpenSimObject obj) {
    long cPtr = opensimCommonJNI.Component_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new Component(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimCommonJNI.Component_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimCommonJNI.Component_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimCommonJNI.Component_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new Component(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimCommonJNI.Component_getConcreteClassName(swigCPtr, this);
  }

  public void finalizeFromProperties() {
    opensimCommonJNI.Component_finalizeFromProperties(swigCPtr, this);
  }

  public void finalizeConnections(Component root) {
    opensimCommonJNI.Component_finalizeConnections(swigCPtr, this, Component.getCPtr(root), root);
  }

  public void clearConnections() {
    opensimCommonJNI.Component_clearConnections(swigCPtr, this);
  }

  public void addToSystem(SWIGTYPE_p_SimTK__MultibodySystem system) {
    opensimCommonJNI.Component_addToSystem(swigCPtr, this, SWIGTYPE_p_SimTK__MultibodySystem.getCPtr(system));
  }

  public void initStateFromProperties(State state) {
    opensimCommonJNI.Component_initStateFromProperties(swigCPtr, this, State.getCPtr(state), state);
  }

  public void setPropertiesFromState(State state) {
    opensimCommonJNI.Component_setPropertiesFromState(swigCPtr, this, State.getCPtr(state), state);
  }

  public void generateDecorations(boolean fixed, ModelDisplayHints hints, State state, ArrayDecorativeGeometry appendToThis) {
    opensimCommonJNI.Component_generateDecorations(swigCPtr, this, fixed, ModelDisplayHints.getCPtr(hints), hints, State.getCPtr(state), state, ArrayDecorativeGeometry.getCPtr(appendToThis), appendToThis);
  }

  public SWIGTYPE_p_SimTK__MultibodySystem getSystem() {
    return new SWIGTYPE_p_SimTK__MultibodySystem(opensimCommonJNI.Component_getSystem(swigCPtr, this), false);
  }

  public boolean hasSystem() {
    return opensimCommonJNI.Component_hasSystem(swigCPtr, this);
  }

  private void private_addComponent(Component subcomponent) {
    opensimCommonJNI.Component_private_addComponent(swigCPtr, this, Component.getCPtr(subcomponent), subcomponent);
  }

  public String getAbsolutePathString() {
    return opensimCommonJNI.Component_getAbsolutePathString(swigCPtr, this);
  }

  public SWIGTYPE_p_ComponentPath getAbsolutePath() {
    return new SWIGTYPE_p_ComponentPath(opensimCommonJNI.Component_getAbsolutePath(swigCPtr, this), true);
  }

  public String getRelativePathName(Component wrt) {
    return opensimCommonJNI.Component_getRelativePathName(swigCPtr, this, Component.getCPtr(wrt), wrt);
  }

  public boolean hasComponent(String pathname) {
    return opensimCommonJNI.Component_hasComponent(swigCPtr, this, pathname);
  }

  public Component getComponent(String pathname) {
    return new Component(opensimCommonJNI.Component_getComponent(swigCPtr, this, pathname), false);
  }

  public Component updComponent(String pathname) {
    return new Component(opensimCommonJNI.Component_updComponent(swigCPtr, this, pathname), false);
  }

  public long printComponentsMatching(String substring) {
    return opensimCommonJNI.Component_printComponentsMatching(swigCPtr, this, substring);
  }

  public int getNumStateVariables() {
    return opensimCommonJNI.Component_getNumStateVariables(swigCPtr, this);
  }

  public ArrayStr getStateVariableNames() {
    return new ArrayStr(opensimCommonJNI.Component_getStateVariableNames(swigCPtr, this), true);
  }

  public int getNumSockets() {
    return opensimCommonJNI.Component_getNumSockets(swigCPtr, this);
  }

  public StdVectorString getSocketNames() {
    return new StdVectorString(opensimCommonJNI.Component_getSocketNames(swigCPtr, this), true);
  }

  public OpenSimObject getConnectee(String name) {
    return new OpenSimObject(opensimCommonJNI.Component_getConnectee(swigCPtr, this, name), false);
  }

  public AbstractSocket getSocket(String name) {
    return new AbstractSocket(opensimCommonJNI.Component_getSocket(swigCPtr, this, name), false);
  }

  public AbstractSocket updSocket(String name) {
    return new AbstractSocket(opensimCommonJNI.Component_updSocket(swigCPtr, this, name), false);
  }

  public int getNumInputs() {
    return opensimCommonJNI.Component_getNumInputs(swigCPtr, this);
  }

  public int getNumOutputs() {
    return opensimCommonJNI.Component_getNumOutputs(swigCPtr, this);
  }

  public StdVectorString getInputNames() {
    return new StdVectorString(opensimCommonJNI.Component_getInputNames(swigCPtr, this), true);
  }

  public StdVectorString getOutputNames() {
    return new StdVectorString(opensimCommonJNI.Component_getOutputNames(swigCPtr, this), true);
  }

  public AbstractInput getInput(String name) {
    return new AbstractInput(opensimCommonJNI.Component_getInput(swigCPtr, this, name), false);
  }

  public AbstractInput updInput(String name) {
    return new AbstractInput(opensimCommonJNI.Component_updInput(swigCPtr, this, name), false);
  }

  public AbstractOutput getOutput(String name) {
    return new AbstractOutput(opensimCommonJNI.Component_getOutput(swigCPtr, this, name), false);
  }

  public AbstractOutput updOutput(String name) {
    return new AbstractOutput(opensimCommonJNI.Component_updOutput(swigCPtr, this, name), false);
  }

  public int getModelingOption(State state, String name) {
    return opensimCommonJNI.Component_getModelingOption(swigCPtr, this, State.getCPtr(state), state, name);
  }

  public void setModelingOption(State state, String name, int flag) {
    opensimCommonJNI.Component_setModelingOption(swigCPtr, this, State.getCPtr(state), state, name, flag);
  }

  public double getStateVariableValue(State state, String name) {
    return opensimCommonJNI.Component_getStateVariableValue(swigCPtr, this, State.getCPtr(state), state, name);
  }

  public void setStateVariableValue(State state, String name, double value) {
    opensimCommonJNI.Component_setStateVariableValue(swigCPtr, this, State.getCPtr(state), state, name, value);
  }

  public Vector getStateVariableValues(State state) {
    return new Vector(opensimCommonJNI.Component_getStateVariableValues(swigCPtr, this, State.getCPtr(state), state), true);
  }

  public void setStateVariableValues(State state, Vector values) {
    opensimCommonJNI.Component_setStateVariableValues(swigCPtr, this, State.getCPtr(state), state, Vector.getCPtr(values), values);
  }

  public double getStateVariableDerivativeValue(State state, String name) {
    return opensimCommonJNI.Component_getStateVariableDerivativeValue(swigCPtr, this, State.getCPtr(state), state, name);
  }

  public double getDiscreteVariableValue(State state, String name) {
    return opensimCommonJNI.Component_getDiscreteVariableValue(swigCPtr, this, State.getCPtr(state), state, name);
  }

  public void setDiscreteVariableValue(State state, String name, double value) {
    opensimCommonJNI.Component_setDiscreteVariableValue(swigCPtr, this, State.getCPtr(state), state, name, value);
  }

  public void markCacheVariableValid(State state, String name) {
    opensimCommonJNI.Component_markCacheVariableValid(swigCPtr, this, State.getCPtr(state), state, name);
  }

  public void markCacheVariableInvalid(State state, String name) {
    opensimCommonJNI.Component_markCacheVariableInvalid(swigCPtr, this, State.getCPtr(state), state, name);
  }

  public boolean isCacheVariableValid(State state, String name) {
    return opensimCommonJNI.Component_isCacheVariableValid(swigCPtr, this, State.getCPtr(state), state, name);
  }

  public void printSubcomponentInfo() {
    opensimCommonJNI.Component_printSubcomponentInfo(swigCPtr, this);
  }

  public void printSocketInfo() {
    opensimCommonJNI.Component_printSocketInfo(swigCPtr, this);
  }

  public void printInputInfo() {
    opensimCommonJNI.Component_printInputInfo(swigCPtr, this);
  }

  public void printOutputInfo(boolean includeDescendants) {
    opensimCommonJNI.Component_printOutputInfo__SWIG_0(swigCPtr, this, includeDescendants);
  }

  public void printOutputInfo() {
    opensimCommonJNI.Component_printOutputInfo__SWIG_1(swigCPtr, this);
  }

  public Component getOwner() {
    return new Component(opensimCommonJNI.Component_getOwner(swigCPtr, this), false);
  }

  public boolean hasOwner() {
    return opensimCommonJNI.Component_hasOwner(swigCPtr, this);
  }

  public ComponentsList getComponentsList() {
    return new ComponentsList(opensimCommonJNI.Component_getComponentsList(swigCPtr, this), true);
  }

}
