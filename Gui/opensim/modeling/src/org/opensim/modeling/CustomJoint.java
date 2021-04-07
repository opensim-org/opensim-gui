/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 * A class implementing a custom joint.  The underlying implementation in Simbody <br>
 * is a SimTK::MobilizedBody::FunctionBased. Custom joints offer a generic joint<br>
 * representation, which can be used to model both conventional (pins, slider,<br>
 * universal, etc.) as well as more complex biomechanical joints. The behavior of<br>
 * a custom joint is specified by its SpatialTransform. A SpatialTransform is com-<br>
 * prised of 6 TransformAxes (3 rotations and 3 translations) that define the<br>
 * spatial position of Child in Parent as a function of coordinates. Each transform<br>
 * axis has a function of joint coordinates that describes the motion about or along<br>
 * the transform axis. The order of the spatial transform is fixed with rotations<br>
 * first followed by translations. Subsequently, coupled motion (i.e., describing<br>
 * motion of two degrees of freedom as a function of one coordinate) is handled by<br>
 * transform axis functions that depend on the same coordinate(s).<br>
 * <br>
 * @author Ajay Seth, Frank C. Anderson
 */
public class CustomJoint extends Joint {
  private transient long swigCPtr;

  public CustomJoint(long cPtr, boolean cMemoryOwn) {
    super(opensimSimulationJNI.CustomJoint_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(CustomJoint obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  @SuppressWarnings("deprecation")
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

  /**
   *  Spatial transform defining how the child body moves with respect<br>
   *     to the parent body as a function of the generalized coordinates.<br>
   *     Motion over 6 (independent) spatial axes must be defined. 
   */
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

  /**
   *  Default Constructor 
   */
  public CustomJoint() {
    this(opensimSimulationJNI.new_CustomJoint__SWIG_0(), true);
  }

  /**
   *  Construct joint with supplied coordinates and transform axes 
   */
  public CustomJoint(String name, PhysicalFrame parent, PhysicalFrame child, SpatialTransform spatialTransform) {
    this(opensimSimulationJNI.new_CustomJoint__SWIG_1(name, PhysicalFrame.getCPtr(parent), parent, PhysicalFrame.getCPtr(child), child, SpatialTransform.getCPtr(spatialTransform), spatialTransform), true);
  }

  /**
   *  Joint constructor with explicit parent and child offsets in terms of<br>
   *         their location and orientation. 
   */
  public CustomJoint(String name, PhysicalFrame parent, Vec3 locationInParent, Vec3 orientationInParent, PhysicalFrame child, Vec3 locationInChild, Vec3 orientationInChild, SpatialTransform spatialTransform) {
    this(opensimSimulationJNI.new_CustomJoint__SWIG_2(name, PhysicalFrame.getCPtr(parent), parent, Vec3.getCPtr(locationInParent), locationInParent, Vec3.getCPtr(orientationInParent), orientationInParent, PhysicalFrame.getCPtr(child), child, Vec3.getCPtr(locationInChild), locationInChild, Vec3.getCPtr(orientationInChild), orientationInChild, SpatialTransform.getCPtr(spatialTransform), spatialTransform), true);
  }

  public SpatialTransform getSpatialTransform() {
    return new SpatialTransform(opensimSimulationJNI.CustomJoint_getSpatialTransform(swigCPtr, this), false);
  }

  public SpatialTransform updSpatialTransform() {
    return new SpatialTransform(opensimSimulationJNI.CustomJoint_updSpatialTransform(swigCPtr, this), false);
  }

  /**
   *  Convenience method to get a const reference to the Coordinate associated<br>
   *         with a single-degree-of-freedom Joint. If the Joint has more than one<br>
   *         Coordinate, you must use get_coordinates() or provide the appropriate<br>
   *         argument to the getCoordinate() method defined in the derived class. 
   */
  public Coordinate getCoordinate() {
    return new Coordinate(opensimSimulationJNI.CustomJoint_getCoordinate__SWIG_0_0(swigCPtr, this), false);
  }

  /**
   *  Convenience method to get a writable reference to the Coordinate<br>
   *         associated with a single-degree-of-freedom Joint. If the Joint has more<br>
   *         than one Coordinate, you must use upd_coordinates() or provide the<br>
   *         appropriate argument to the updCoordinate() method defined in the<br>
   *         derived class. 
   */
  public Coordinate updCoordinate() {
    return new Coordinate(opensimSimulationJNI.CustomJoint_updCoordinate__SWIG_0_0(swigCPtr, this), false);
  }

  /**
   *  Get a const reference to a Coordinate associated with this Joint. 
   */
  public Coordinate getCoordinate(long idx) {
    return new Coordinate(opensimSimulationJNI.CustomJoint_getCoordinate__SWIG_1(swigCPtr, this, idx), false);
  }

  /**
   *  Get a writable reference to a Coordinate associated with this Joint. 
   */
  public Coordinate updCoordinate(long idx) {
    return new Coordinate(opensimSimulationJNI.CustomJoint_updCoordinate__SWIG_1(swigCPtr, this, idx), false);
  }

  public void extendScale(State s, ScaleSet scaleSet) {
    opensimSimulationJNI.CustomJoint_extendScale(swigCPtr, this, State.getCPtr(s), s, ScaleSet.getCPtr(scaleSet), scaleSet);
  }

  /**
   *  Override of the default implementation to account for versioning. 
   */
  public void updateFromXMLNode(SWIGTYPE_p_SimTK__Xml__Element aNode, int versionNumber) {
    opensimSimulationJNI.CustomJoint_updateFromXMLNode__SWIG_0(swigCPtr, this, SWIGTYPE_p_SimTK__Xml__Element.getCPtr(aNode), versionNumber);
  }

  /**
   *  Override of the default implementation to account for versioning. 
   */
  public void updateFromXMLNode(SWIGTYPE_p_SimTK__Xml__Element aNode) {
    opensimSimulationJNI.CustomJoint_updateFromXMLNode__SWIG_1(swigCPtr, this, SWIGTYPE_p_SimTK__Xml__Element.getCPtr(aNode));
  }

}
