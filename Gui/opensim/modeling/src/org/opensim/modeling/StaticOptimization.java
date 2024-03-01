/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.1.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 * This class implements static optimization to compute Muscle Forces and <br>
 * activations. <br>
 * <br>
 * @author Jeff Reinbolt
 */
public class StaticOptimization extends Analysis {
  private transient long swigCPtr;

  public StaticOptimization(long cPtr, boolean cMemoryOwn) {
    super(opensimActuatorsAnalysesToolsJNI.StaticOptimization_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(StaticOptimization obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  public static long swigRelease(StaticOptimization obj) {
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
        opensimActuatorsAnalysesToolsJNI.delete_StaticOptimization(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static StaticOptimization safeDownCast(OpenSimObject obj) {
    long cPtr = opensimActuatorsAnalysesToolsJNI.StaticOptimization_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new StaticOptimization(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimActuatorsAnalysesToolsJNI.StaticOptimization_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimActuatorsAnalysesToolsJNI.StaticOptimization_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimActuatorsAnalysesToolsJNI.StaticOptimization_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new StaticOptimization(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimActuatorsAnalysesToolsJNI.StaticOptimization_getConcreteClassName(swigCPtr, this);
  }

  public StaticOptimization(Model aModel) {
    this(opensimActuatorsAnalysesToolsJNI.new_StaticOptimization__SWIG_0(Model.getCPtr(aModel), aModel), true);
  }

  public StaticOptimization() {
    this(opensimActuatorsAnalysesToolsJNI.new_StaticOptimization__SWIG_1(), true);
  }

  public StaticOptimization(StaticOptimization aObject) {
    this(opensimActuatorsAnalysesToolsJNI.new_StaticOptimization__SWIG_2(StaticOptimization.getCPtr(aObject), aObject), true);
  }

  public void setStorageCapacityIncrements(int arg0) {
    opensimActuatorsAnalysesToolsJNI.StaticOptimization_setStorageCapacityIncrements(swigCPtr, this, arg0);
  }

  public Storage getActivationStorage() {
    long cPtr = opensimActuatorsAnalysesToolsJNI.StaticOptimization_getActivationStorage(swigCPtr, this);
    return (cPtr == 0) ? null : new Storage(cPtr, false);
  }

  public Storage getForceStorage() {
    long cPtr = opensimActuatorsAnalysesToolsJNI.StaticOptimization_getForceStorage(swigCPtr, this);
    return (cPtr == 0) ? null : new Storage(cPtr, false);
  }

  public boolean getUseModelForceSet() {
    return opensimActuatorsAnalysesToolsJNI.StaticOptimization_getUseModelForceSet(swigCPtr, this);
  }

  public void setUseModelForceSet(boolean aUseModelActuatorSet) {
    opensimActuatorsAnalysesToolsJNI.StaticOptimization_setUseModelForceSet(swigCPtr, this, aUseModelActuatorSet);
  }

  public void setModel(Model aModel) {
    opensimActuatorsAnalysesToolsJNI.StaticOptimization_setModel(swigCPtr, this, Model.getCPtr(aModel), aModel);
  }

  public void setActivationExponent(double aExponent) {
    opensimActuatorsAnalysesToolsJNI.StaticOptimization_setActivationExponent(swigCPtr, this, aExponent);
  }

  public double getActivationExponent() {
    return opensimActuatorsAnalysesToolsJNI.StaticOptimization_getActivationExponent(swigCPtr, this);
  }

  public void setUseMusclePhysiology(boolean useIt) {
    opensimActuatorsAnalysesToolsJNI.StaticOptimization_setUseMusclePhysiology(swigCPtr, this, useIt);
  }

  public boolean getUseMusclePhysiology() {
    return opensimActuatorsAnalysesToolsJNI.StaticOptimization_getUseMusclePhysiology(swigCPtr, this);
  }

  public void setConvergenceCriterion(double tolerance) {
    opensimActuatorsAnalysesToolsJNI.StaticOptimization_setConvergenceCriterion(swigCPtr, this, tolerance);
  }

  public double getConvergenceCriterion() {
    return opensimActuatorsAnalysesToolsJNI.StaticOptimization_getConvergenceCriterion(swigCPtr, this);
  }

  public void setMaxIterations(int maxIt) {
    opensimActuatorsAnalysesToolsJNI.StaticOptimization_setMaxIterations(swigCPtr, this, maxIt);
  }

  public int getMaxIterations() {
    return opensimActuatorsAnalysesToolsJNI.StaticOptimization_getMaxIterations(swigCPtr, this);
  }

  public int begin(State s) {
    return opensimActuatorsAnalysesToolsJNI.StaticOptimization_begin(swigCPtr, this, State.getCPtr(s), s);
  }

  public int step(State s, int setNumber) {
    return opensimActuatorsAnalysesToolsJNI.StaticOptimization_step(swigCPtr, this, State.getCPtr(s), s, setNumber);
  }

  public int end(State s) {
    return opensimActuatorsAnalysesToolsJNI.StaticOptimization_end(swigCPtr, this, State.getCPtr(s), s);
  }

  public int printResults(String aBaseName, String aDir, double aDT, String aExtension) {
    return opensimActuatorsAnalysesToolsJNI.StaticOptimization_printResults__SWIG_0(swigCPtr, this, aBaseName, aDir, aDT, aExtension);
  }

  public int printResults(String aBaseName, String aDir, double aDT) {
    return opensimActuatorsAnalysesToolsJNI.StaticOptimization_printResults__SWIG_1(swigCPtr, this, aBaseName, aDir, aDT);
  }

  public int printResults(String aBaseName, String aDir) {
    return opensimActuatorsAnalysesToolsJNI.StaticOptimization_printResults__SWIG_2(swigCPtr, this, aBaseName, aDir);
  }

  public int printResults(String aBaseName) {
    return opensimActuatorsAnalysesToolsJNI.StaticOptimization_printResults__SWIG_3(swigCPtr, this, aBaseName);
  }

}
