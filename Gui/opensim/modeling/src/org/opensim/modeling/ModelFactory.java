/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.9
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class ModelFactory {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  public ModelFactory(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  public static long getCPtr(ModelFactory obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimActuatorsAnalysesToolsJNI.delete_ModelFactory(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public static Model createNLinkPendulum(int numLinks) {
    return new Model(opensimActuatorsAnalysesToolsJNI.ModelFactory_createNLinkPendulum(numLinks), true);
  }

  public static Model createPendulum() {
    return new Model(opensimActuatorsAnalysesToolsJNI.ModelFactory_createPendulum(), true);
  }

  public static Model createDoublePendulum() {
    return new Model(opensimActuatorsAnalysesToolsJNI.ModelFactory_createDoublePendulum(), true);
  }

  public static Model createSlidingPointMass() {
    return new Model(opensimActuatorsAnalysesToolsJNI.ModelFactory_createSlidingPointMass(), true);
  }

  public static Model createPlanarPointMass() {
    return new Model(opensimActuatorsAnalysesToolsJNI.ModelFactory_createPlanarPointMass(), true);
  }

  public static void replaceMusclesWithPathActuators(Model model) {
    opensimActuatorsAnalysesToolsJNI.ModelFactory_replaceMusclesWithPathActuators(Model.getCPtr(model), model);
  }

  public static void removeMuscles(Model model) {
    opensimActuatorsAnalysesToolsJNI.ModelFactory_removeMuscles(Model.getCPtr(model), model);
  }

  public static void replaceJointWithWeldJoint(Model model, String jointName) {
    opensimActuatorsAnalysesToolsJNI.ModelFactory_replaceJointWithWeldJoint(Model.getCPtr(model), model, jointName);
  }

  public static void createReserveActuators(Model model, double optimalForce, double bound, boolean skipCoordinatesWithExistingActuators) {
    opensimActuatorsAnalysesToolsJNI.ModelFactory_createReserveActuators__SWIG_0(Model.getCPtr(model), model, optimalForce, bound, skipCoordinatesWithExistingActuators);
  }

  public static void createReserveActuators(Model model, double optimalForce, double bound) {
    opensimActuatorsAnalysesToolsJNI.ModelFactory_createReserveActuators__SWIG_1(Model.getCPtr(model), model, optimalForce, bound);
  }

  public static void createReserveActuators(Model model, double optimalForce) {
    opensimActuatorsAnalysesToolsJNI.ModelFactory_createReserveActuators__SWIG_2(Model.getCPtr(model), model, optimalForce);
  }

  public ModelFactory() {
    this(opensimActuatorsAnalysesToolsJNI.new_ModelFactory(), true);
  }

}
