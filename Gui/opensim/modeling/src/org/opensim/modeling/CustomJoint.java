/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class CustomJoint extends Joint {
  private transient long swigCPtr;

  protected CustomJoint(long cPtr, boolean cMemoryOwn) {
    super(opensimSimulationJNI.CustomJoint_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected static long getCPtr(CustomJoint obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimSimulationJNI.delete_CustomJoint(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static CustomJoint safeDownCast(OpenSimObject obj) {
    long cPtr = opensimSimulationJNI.CustomJoint_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new CustomJoint(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimSimulationJNI.CustomJoint_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimSimulationJNI.CustomJoint_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimSimulationJNI.CustomJoint_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new CustomJoint(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimSimulationJNI.CustomJoint_getConcreteClassName(swigCPtr, this);
  }

  public void copyProperty_SpatialTransform(CustomJoint source) {
    opensimSimulationJNI.CustomJoint_copyProperty_SpatialTransform(swigCPtr, this, CustomJoint.getCPtr(source), source);
  }

  public SpatialTransform get_SpatialTransform(int i) {
    return new SpatialTransform(opensimSimulationJNI.CustomJoint_get_SpatialTransform__SWIG_0(swigCPtr, this, i), false);
  }

  public SpatialTransform upd_SpatialTransform(int i) {
    return new SpatialTransform(opensimSimulationJNI.CustomJoint_upd_SpatialTransform__SWIG_0(swigCPtr, this, i), false);
  }

  public void set_SpatialTransform(int i, SpatialTransform value) {
    opensimSimulationJNI.CustomJoint_set_SpatialTransform__SWIG_0(swigCPtr, this, i, SpatialTransform.getCPtr(value), value);
  }

  public int append_SpatialTransform(SpatialTransform value) {
    return opensimSimulationJNI.CustomJoint_append_SpatialTransform(swigCPtr, this, SpatialTransform.getCPtr(value), value);
  }

  public void constructProperty_SpatialTransform(SpatialTransform initValue) {
    opensimSimulationJNI.CustomJoint_constructProperty_SpatialTransform(swigCPtr, this, SpatialTransform.getCPtr(initValue), initValue);
  }

  public SpatialTransform get_SpatialTransform() {
    return new SpatialTransform(opensimSimulationJNI.CustomJoint_get_SpatialTransform__SWIG_1(swigCPtr, this), false);
  }

  public SpatialTransform upd_SpatialTransform() {
    return new SpatialTransform(opensimSimulationJNI.CustomJoint_upd_SpatialTransform__SWIG_1(swigCPtr, this), false);
  }

  public void set_SpatialTransform(SpatialTransform value) {
    opensimSimulationJNI.CustomJoint_set_SpatialTransform__SWIG_1(swigCPtr, this, SpatialTransform.getCPtr(value), value);
  }

  public CustomJoint() {
    this(opensimSimulationJNI.new_CustomJoint__SWIG_0(), true);
  }

  public CustomJoint(String name, PhysicalFrame parent, PhysicalFrame child, SpatialTransform spatialTransform) {
    this(opensimSimulationJNI.new_CustomJoint__SWIG_1(name, PhysicalFrame.getCPtr(parent), parent, PhysicalFrame.getCPtr(child), child, SpatialTransform.getCPtr(spatialTransform), spatialTransform), true);
  }

  public CustomJoint(String name, PhysicalFrame parent, Vec3 locationInParent, Vec3 orientationInParent, PhysicalFrame child, Vec3 locationInChild, Vec3 orientationInChild, SpatialTransform spatialTransform) {
    this(opensimSimulationJNI.new_CustomJoint__SWIG_2(name, PhysicalFrame.getCPtr(parent), parent, Vec3.getCPtr(locationInParent), locationInParent, Vec3.getCPtr(orientationInParent), orientationInParent, PhysicalFrame.getCPtr(child), child, Vec3.getCPtr(locationInChild), locationInChild, Vec3.getCPtr(orientationInChild), orientationInChild, SpatialTransform.getCPtr(spatialTransform), spatialTransform), true);
  }

  public SpatialTransform getSpatialTransform() {
    return new SpatialTransform(opensimSimulationJNI.CustomJoint_getSpatialTransform(swigCPtr, this), false);
  }

  public SpatialTransform updSpatialTransform() {
    return new SpatialTransform(opensimSimulationJNI.CustomJoint_updSpatialTransform(swigCPtr, this), false);
  }

  public Coordinate getCoordinate() {
    return new Coordinate(opensimSimulationJNI.CustomJoint_getCoordinate__SWIG_0_0(swigCPtr, this), false);
  }

  public Coordinate updCoordinate() {
    return new Coordinate(opensimSimulationJNI.CustomJoint_updCoordinate__SWIG_0_0(swigCPtr, this), false);
  }

  public Coordinate getCoordinate(long idx) {
    return new Coordinate(opensimSimulationJNI.CustomJoint_getCoordinate__SWIG_1(swigCPtr, this, idx), false);
  }

  public Coordinate updCoordinate(long idx) {
    return new Coordinate(opensimSimulationJNI.CustomJoint_updCoordinate__SWIG_1(swigCPtr, this, idx), false);
  }

  public void extendScale(State s, ScaleSet scaleSet) {
    opensimSimulationJNI.CustomJoint_extendScale(swigCPtr, this, State.getCPtr(s), s, ScaleSet.getCPtr(scaleSet), scaleSet);
  }

  public void updateFromXMLNode(SWIGTYPE_p_SimTK__Xml__Element aNode, int versionNumber) {
    opensimSimulationJNI.CustomJoint_updateFromXMLNode__SWIG_0(swigCPtr, this, SWIGTYPE_p_SimTK__Xml__Element.getCPtr(aNode), versionNumber);
  }

  public void updateFromXMLNode(SWIGTYPE_p_SimTK__Xml__Element aNode) {
    opensimSimulationJNI.CustomJoint_updateFromXMLNode__SWIG_1(swigCPtr, this, SWIGTYPE_p_SimTK__Xml__Element.getCPtr(aNode));
  }

}
