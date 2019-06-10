/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.9
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class ActuatorIterator {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  public ActuatorIterator(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  public static long getCPtr(ActuatorIterator obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimSimulationJNI.delete_ActuatorIterator(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public boolean equals(ActuatorIterator other) {
    return opensimSimulationJNI.ActuatorIterator_equals(swigCPtr, this, ActuatorIterator.getCPtr(other), other);
  }

  public Actuator __ref__() {
    return new Actuator(opensimSimulationJNI.ActuatorIterator___ref__(swigCPtr, this), false);
  }

  public Actuator deref() {
    return new Actuator(opensimSimulationJNI.ActuatorIterator_deref(swigCPtr, this), false);
  }

  public Actuator __deref__() {
    long cPtr = opensimSimulationJNI.ActuatorIterator___deref__(swigCPtr, this);
    return (cPtr == 0) ? null : new Actuator(cPtr, false);
  }

  public ActuatorIterator next() {
    return new ActuatorIterator(opensimSimulationJNI.ActuatorIterator_next(swigCPtr, this), false);
  }

  public OpenSimObject clone() {
    long cPtr = opensimSimulationJNI.ActuatorIterator_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new Actuator(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimSimulationJNI.ActuatorIterator_getConcreteClassName(swigCPtr, this);
  }

  public int numControls() {
    return opensimSimulationJNI.ActuatorIterator_numControls(swigCPtr, this);
  }

  public void getControls(Vector modelControls, Vector actuatorControls) {
    opensimSimulationJNI.ActuatorIterator_getControls(swigCPtr, this, Vector.getCPtr(modelControls), modelControls, Vector.getCPtr(actuatorControls), actuatorControls);
  }

  public void setControls(Vector actuatorControls, Vector modelControls) {
    opensimSimulationJNI.ActuatorIterator_setControls(swigCPtr, this, Vector.getCPtr(actuatorControls), actuatorControls, Vector.getCPtr(modelControls), modelControls);
  }

  public void addInControls(Vector actuatorControls, Vector modelControls) {
    opensimSimulationJNI.ActuatorIterator_addInControls(swigCPtr, this, Vector.getCPtr(actuatorControls), actuatorControls, Vector.getCPtr(modelControls), modelControls);
  }

  public double getPower(State s) {
    return opensimSimulationJNI.ActuatorIterator_getPower(swigCPtr, this, State.getCPtr(s), s);
  }

  public void computeEquilibrium(State s) {
    opensimSimulationJNI.ActuatorIterator_computeEquilibrium(swigCPtr, this, State.getCPtr(s), s);
  }

  public boolean get_appliesForce(int i) {
    return opensimSimulationJNI.ActuatorIterator_get_appliesForce__SWIG_0(swigCPtr, this, i);
  }

  public boolean get_appliesForce() {
    return opensimSimulationJNI.ActuatorIterator_get_appliesForce__SWIG_1(swigCPtr, this);
  }

  public boolean get_has_output_potential_energy() {
    return opensimSimulationJNI.ActuatorIterator__has_output_potential_energy_get(swigCPtr, this);
  }

  public boolean shouldBeParallelized() {
    return opensimSimulationJNI.ActuatorIterator_shouldBeParallelized(swigCPtr, this);
  }

  public boolean appliesForce(State s) {
    return opensimSimulationJNI.ActuatorIterator_appliesForce(swigCPtr, this, State.getCPtr(s), s);
  }

  public void setAppliesForce(State s, boolean applyForce) {
    opensimSimulationJNI.ActuatorIterator_setAppliesForce(swigCPtr, this, State.getCPtr(s), s, applyForce);
  }

  public ArrayStr getRecordLabels() {
    return new ArrayStr(opensimSimulationJNI.ActuatorIterator_getRecordLabels(swigCPtr, this), true);
  }

  public ArrayDouble getRecordValues(State state) {
    return new ArrayDouble(opensimSimulationJNI.ActuatorIterator_getRecordValues(swigCPtr, this, State.getCPtr(state), state), true);
  }

  public boolean hasGeometryPath() {
    return opensimSimulationJNI.ActuatorIterator_hasGeometryPath(swigCPtr, this);
  }

  public Model getModel() {
    return new Model(opensimSimulationJNI.ActuatorIterator_getModel(swigCPtr, this), false);
  }

  public boolean hasModel() {
    return opensimSimulationJNI.ActuatorIterator_hasModel(swigCPtr, this);
  }

  public void addToSystem(SWIGTYPE_p_SimTK__MultibodySystem system) {
    opensimSimulationJNI.ActuatorIterator_addToSystem(swigCPtr, this, SWIGTYPE_p_SimTK__MultibodySystem.getCPtr(system));
  }

  public void initStateFromProperties(State state) {
    opensimSimulationJNI.ActuatorIterator_initStateFromProperties(swigCPtr, this, State.getCPtr(state), state);
  }

  public void generateDecorations(boolean fixed, ModelDisplayHints hints, State state, ArrayDecorativeGeometry appendToThis) {
    opensimSimulationJNI.ActuatorIterator_generateDecorations(swigCPtr, this, fixed, ModelDisplayHints.getCPtr(hints), hints, State.getCPtr(state), state, ArrayDecorativeGeometry.getCPtr(appendToThis), appendToThis);
  }

  public SWIGTYPE_p_SimTK__MultibodySystem getSystem() {
    return new SWIGTYPE_p_SimTK__MultibodySystem(opensimSimulationJNI.ActuatorIterator_getSystem(swigCPtr, this), false);
  }

  public boolean hasSystem() {
    return opensimSimulationJNI.ActuatorIterator_hasSystem(swigCPtr, this);
  }

  public boolean isComponentInOwnershipTree(Component component) {
    return opensimSimulationJNI.ActuatorIterator_isComponentInOwnershipTree(swigCPtr, this, Component.getCPtr(component), component);
  }

  public String getAbsolutePathString() {
    return opensimSimulationJNI.ActuatorIterator_getAbsolutePathString(swigCPtr, this);
  }

  public ComponentPath getAbsolutePath() {
    return new ComponentPath(opensimSimulationJNI.ActuatorIterator_getAbsolutePath(swigCPtr, this), true);
  }

  public String getRelativePathString(Component wrt) {
    return opensimSimulationJNI.ActuatorIterator_getRelativePathString(swigCPtr, this, Component.getCPtr(wrt), wrt);
  }

  public ComponentPath getRelativePath(Component wrt) {
    return new ComponentPath(opensimSimulationJNI.ActuatorIterator_getRelativePath(swigCPtr, this, Component.getCPtr(wrt), wrt), true);
  }

  public boolean hasComponent(String pathname) {
    return opensimSimulationJNI.ActuatorIterator_hasComponent(swigCPtr, this, pathname);
  }

  public Component getComponent(String pathname) {
    return new Component(opensimSimulationJNI.ActuatorIterator_getComponent(swigCPtr, this, pathname), false);
  }

  public long printComponentsMatching(String substring) {
    return opensimSimulationJNI.ActuatorIterator_printComponentsMatching(swigCPtr, this, substring);
  }

  public int getNumStateVariables() {
    return opensimSimulationJNI.ActuatorIterator_getNumStateVariables(swigCPtr, this);
  }

  public ArrayStr getStateVariableNames() {
    return new ArrayStr(opensimSimulationJNI.ActuatorIterator_getStateVariableNames(swigCPtr, this), true);
  }

  public int getNumSockets() {
    return opensimSimulationJNI.ActuatorIterator_getNumSockets(swigCPtr, this);
  }

  public OpenSimObject getConnectee(String name) {
    return new OpenSimObject(opensimSimulationJNI.ActuatorIterator_getConnectee(swigCPtr, this, name), false);
  }

  public AbstractSocket getSocket(String name) {
    return new AbstractSocket(opensimSimulationJNI.ActuatorIterator_getSocket(swigCPtr, this, name), false);
  }

  public int getNumInputs() {
    return opensimSimulationJNI.ActuatorIterator_getNumInputs(swigCPtr, this);
  }

  public int getNumOutputs() {
    return opensimSimulationJNI.ActuatorIterator_getNumOutputs(swigCPtr, this);
  }

  public StdVectorString getInputNames() {
    return new StdVectorString(opensimSimulationJNI.ActuatorIterator_getInputNames(swigCPtr, this), true);
  }

  public StdVectorString getOutputNames() {
    return new StdVectorString(opensimSimulationJNI.ActuatorIterator_getOutputNames(swigCPtr, this), true);
  }

  public AbstractInput getInput(String name) {
    return new AbstractInput(opensimSimulationJNI.ActuatorIterator_getInput(swigCPtr, this, name), false);
  }

  public AbstractOutput getOutput(String name) {
    return new AbstractOutput(opensimSimulationJNI.ActuatorIterator_getOutput(swigCPtr, this, name), false);
  }

  public int getModelingOption(State state, String name) {
    return opensimSimulationJNI.ActuatorIterator_getModelingOption(swigCPtr, this, State.getCPtr(state), state, name);
  }

  public void setModelingOption(State state, String name, int flag) {
    opensimSimulationJNI.ActuatorIterator_setModelingOption(swigCPtr, this, State.getCPtr(state), state, name, flag);
  }

  public double getStateVariableValue(State state, String name) {
    return opensimSimulationJNI.ActuatorIterator_getStateVariableValue(swigCPtr, this, State.getCPtr(state), state, name);
  }

  public void setStateVariableValue(State state, String name, double value) {
    opensimSimulationJNI.ActuatorIterator_setStateVariableValue(swigCPtr, this, State.getCPtr(state), state, name, value);
  }

  public Vector getStateVariableValues(State state) {
    return new Vector(opensimSimulationJNI.ActuatorIterator_getStateVariableValues(swigCPtr, this, State.getCPtr(state), state), true);
  }

  public void setStateVariableValues(State state, Vector values) {
    opensimSimulationJNI.ActuatorIterator_setStateVariableValues(swigCPtr, this, State.getCPtr(state), state, Vector.getCPtr(values), values);
  }

  public double getStateVariableDerivativeValue(State state, String name) {
    return opensimSimulationJNI.ActuatorIterator_getStateVariableDerivativeValue(swigCPtr, this, State.getCPtr(state), state, name);
  }

  public double getDiscreteVariableValue(State state, String name) {
    return opensimSimulationJNI.ActuatorIterator_getDiscreteVariableValue(swigCPtr, this, State.getCPtr(state), state, name);
  }

  public void setDiscreteVariableValue(State state, String name, double value) {
    opensimSimulationJNI.ActuatorIterator_setDiscreteVariableValue(swigCPtr, this, State.getCPtr(state), state, name, value);
  }

  public void markCacheVariableValid(State state, String name) {
    opensimSimulationJNI.ActuatorIterator_markCacheVariableValid(swigCPtr, this, State.getCPtr(state), state, name);
  }

  public void markCacheVariableInvalid(State state, String name) {
    opensimSimulationJNI.ActuatorIterator_markCacheVariableInvalid(swigCPtr, this, State.getCPtr(state), state, name);
  }

  public boolean isCacheVariableValid(State state, String name) {
    return opensimSimulationJNI.ActuatorIterator_isCacheVariableValid(swigCPtr, this, State.getCPtr(state), state, name);
  }

  public void printSubcomponentInfo() {
    opensimSimulationJNI.ActuatorIterator_printSubcomponentInfo(swigCPtr, this);
  }

  public void printSocketInfo() {
    opensimSimulationJNI.ActuatorIterator_printSocketInfo(swigCPtr, this);
  }

  public void printInputInfo() {
    opensimSimulationJNI.ActuatorIterator_printInputInfo(swigCPtr, this);
  }

  public void printOutputInfo(boolean includeDescendants) {
    opensimSimulationJNI.ActuatorIterator_printOutputInfo__SWIG_0(swigCPtr, this, includeDescendants);
  }

  public void printOutputInfo() {
    opensimSimulationJNI.ActuatorIterator_printOutputInfo__SWIG_1(swigCPtr, this);
  }

  public Component getOwner() {
    return new Component(opensimSimulationJNI.ActuatorIterator_getOwner(swigCPtr, this), false);
  }

  public boolean hasOwner() {
    return opensimSimulationJNI.ActuatorIterator_hasOwner(swigCPtr, this);
  }

  public Component getRoot() {
    return new Component(opensimSimulationJNI.ActuatorIterator_getRoot(swigCPtr, this), false);
  }

  public Component findComponent(ComponentPath pathToFind) {
    long cPtr = opensimSimulationJNI.ActuatorIterator_findComponent__SWIG_2(swigCPtr, this, ComponentPath.getCPtr(pathToFind), pathToFind);
    return (cPtr == 0) ? null : new Component(cPtr, false);
  }

  public Component findComponent(String pathToFind) {
    long cPtr = opensimSimulationJNI.ActuatorIterator_findComponent__SWIG_3(swigCPtr, this, pathToFind);
    return (cPtr == 0) ? null : new Component(cPtr, false);
  }

  public ComponentsList getComponentsList() {
    return new ComponentsList(opensimSimulationJNI.ActuatorIterator_getComponentsList(swigCPtr, this), true);
  }

  public boolean isEqualTo(OpenSimObject aObject) {
    return opensimSimulationJNI.ActuatorIterator_isEqualTo(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public String getName() {
    return opensimSimulationJNI.ActuatorIterator_getName(swigCPtr, this);
  }

  public String getDescription() {
    return opensimSimulationJNI.ActuatorIterator_getDescription(swigCPtr, this);
  }

  public String getAuthors() {
    return opensimSimulationJNI.ActuatorIterator_getAuthors(swigCPtr, this);
  }

  public String getReferences() {
    return opensimSimulationJNI.ActuatorIterator_getReferences(swigCPtr, this);
  }

  public int getNumProperties() {
    return opensimSimulationJNI.ActuatorIterator_getNumProperties(swigCPtr, this);
  }

  public AbstractProperty getPropertyByIndex(int propertyIndex) {
    return new AbstractProperty(opensimSimulationJNI.ActuatorIterator_getPropertyByIndex(swigCPtr, this, propertyIndex), false);
  }

  public boolean hasProperty(String name) {
    return opensimSimulationJNI.ActuatorIterator_hasProperty(swigCPtr, this, name);
  }

  public AbstractProperty getPropertyByName(String name) {
    return new AbstractProperty(opensimSimulationJNI.ActuatorIterator_getPropertyByName(swigCPtr, this, name), false);
  }

  public boolean isObjectUpToDateWithProperties() {
    return opensimSimulationJNI.ActuatorIterator_isObjectUpToDateWithProperties(swigCPtr, this);
  }

  public void updateXMLNode(SWIGTYPE_p_SimTK__Xml__Element parent, AbstractProperty prop) {
    opensimSimulationJNI.ActuatorIterator_updateXMLNode__SWIG_0(swigCPtr, this, SWIGTYPE_p_SimTK__Xml__Element.getCPtr(parent), AbstractProperty.getCPtr(prop), prop);
  }

  public void updateXMLNode(SWIGTYPE_p_SimTK__Xml__Element parent) {
    opensimSimulationJNI.ActuatorIterator_updateXMLNode__SWIG_1(swigCPtr, this, SWIGTYPE_p_SimTK__Xml__Element.getCPtr(parent));
  }

  public boolean getInlined() {
    return opensimSimulationJNI.ActuatorIterator_getInlined(swigCPtr, this);
  }

  public String getDocumentFileName() {
    return opensimSimulationJNI.ActuatorIterator_getDocumentFileName(swigCPtr, this);
  }

  public int getDocumentFileVersion() {
    return opensimSimulationJNI.ActuatorIterator_getDocumentFileVersion(swigCPtr, this);
  }

  public boolean print(String fileName) {
    return opensimSimulationJNI.ActuatorIterator_print(swigCPtr, this, fileName);
  }

  public String dump() {
    return opensimSimulationJNI.ActuatorIterator_dump(swigCPtr, this);
  }

  public boolean isA(String type) {
    return opensimSimulationJNI.ActuatorIterator_isA(swigCPtr, this, type);
  }

  public String toString() {
    return opensimSimulationJNI.ActuatorIterator_toString(swigCPtr, this);
  }

}
