/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 *  This class represents the physical shape of an object for use in contact<br>
 * modeling.  It is an abstract class, with subclasses for particular geometric<br>
 * representations. The geometry is attached to a PhysicalFrame, which is<br>
 * specified using a Socket named "frame".<br>
 * <br>
 * Note that ContactGeometry is not scaled with the Model.<br>
 * <br>
 * @author Peter Eastman
 */
public class ContactGeometry extends ModelComponent {
  private transient long swigCPtr;

  public ContactGeometry(long cPtr, boolean cMemoryOwn) {
    super(opensimSimulationJNI.ContactGeometry_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(ContactGeometry obj) {
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
        opensimSimulationJNI.delete_ContactGeometry(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static ContactGeometry safeDownCast(OpenSimObject obj) {
    long cPtr = opensimSimulationJNI.ContactGeometry_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new ContactGeometry(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimSimulationJNI.ContactGeometry_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimSimulationJNI.ContactGeometry_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimSimulationJNI.ContactGeometry_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new ContactGeometry(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimSimulationJNI.ContactGeometry_getConcreteClassName(swigCPtr, this);
  }

  public void copyProperty_location(ContactGeometry source) {
    opensimSimulationJNI.ContactGeometry_copyProperty_location(swigCPtr, this, ContactGeometry.getCPtr(source), source);
  }

  public Vec3 get_location(int i) {
    return new Vec3(opensimSimulationJNI.ContactGeometry_get_location__SWIG_0(swigCPtr, this, i), false);
  }

  public Vec3 upd_location(int i) {
    return new Vec3(opensimSimulationJNI.ContactGeometry_upd_location__SWIG_0(swigCPtr, this, i), false);
  }

  public void set_location(int i, Vec3 value) {
    opensimSimulationJNI.ContactGeometry_set_location__SWIG_0(swigCPtr, this, i, Vec3.getCPtr(value), value);
  }

  public int append_location(Vec3 value) {
    return opensimSimulationJNI.ContactGeometry_append_location(swigCPtr, this, Vec3.getCPtr(value), value);
  }

  public void constructProperty_location(Vec3 initValue) {
    opensimSimulationJNI.ContactGeometry_constructProperty_location(swigCPtr, this, Vec3.getCPtr(initValue), initValue);
  }

  public Vec3 get_location() {
    return new Vec3(opensimSimulationJNI.ContactGeometry_get_location__SWIG_1(swigCPtr, this), false);
  }

  public Vec3 upd_location() {
    return new Vec3(opensimSimulationJNI.ContactGeometry_upd_location__SWIG_1(swigCPtr, this), false);
  }

  public void set_location(Vec3 value) {
    opensimSimulationJNI.ContactGeometry_set_location__SWIG_1(swigCPtr, this, Vec3.getCPtr(value), value);
  }

  public void copyProperty_orientation(ContactGeometry source) {
    opensimSimulationJNI.ContactGeometry_copyProperty_orientation(swigCPtr, this, ContactGeometry.getCPtr(source), source);
  }

  public Vec3 get_orientation(int i) {
    return new Vec3(opensimSimulationJNI.ContactGeometry_get_orientation__SWIG_0(swigCPtr, this, i), false);
  }

  public Vec3 upd_orientation(int i) {
    return new Vec3(opensimSimulationJNI.ContactGeometry_upd_orientation__SWIG_0(swigCPtr, this, i), false);
  }

  public void set_orientation(int i, Vec3 value) {
    opensimSimulationJNI.ContactGeometry_set_orientation__SWIG_0(swigCPtr, this, i, Vec3.getCPtr(value), value);
  }

  public int append_orientation(Vec3 value) {
    return opensimSimulationJNI.ContactGeometry_append_orientation(swigCPtr, this, Vec3.getCPtr(value), value);
  }

  public void constructProperty_orientation(Vec3 initValue) {
    opensimSimulationJNI.ContactGeometry_constructProperty_orientation(swigCPtr, this, Vec3.getCPtr(initValue), initValue);
  }

  public Vec3 get_orientation() {
    return new Vec3(opensimSimulationJNI.ContactGeometry_get_orientation__SWIG_1(swigCPtr, this), false);
  }

  public Vec3 upd_orientation() {
    return new Vec3(opensimSimulationJNI.ContactGeometry_upd_orientation__SWIG_1(swigCPtr, this), false);
  }

  public void set_orientation(Vec3 value) {
    opensimSimulationJNI.ContactGeometry_set_orientation__SWIG_1(swigCPtr, this, Vec3.getCPtr(value), value);
  }

  public void copyProperty_Appearance(ContactGeometry source) {
    opensimSimulationJNI.ContactGeometry_copyProperty_Appearance(swigCPtr, this, ContactGeometry.getCPtr(source), source);
  }

  public Appearance get_Appearance(int i) {
    return new Appearance(opensimSimulationJNI.ContactGeometry_get_Appearance__SWIG_0(swigCPtr, this, i), false);
  }

  public Appearance upd_Appearance(int i) {
    return new Appearance(opensimSimulationJNI.ContactGeometry_upd_Appearance__SWIG_0(swigCPtr, this, i), false);
  }

  public void set_Appearance(int i, Appearance value) {
    opensimSimulationJNI.ContactGeometry_set_Appearance__SWIG_0(swigCPtr, this, i, Appearance.getCPtr(value), value);
  }

  public int append_Appearance(Appearance value) {
    return opensimSimulationJNI.ContactGeometry_append_Appearance(swigCPtr, this, Appearance.getCPtr(value), value);
  }

  public void constructProperty_Appearance(Appearance initValue) {
    opensimSimulationJNI.ContactGeometry_constructProperty_Appearance(swigCPtr, this, Appearance.getCPtr(initValue), initValue);
  }

  public Appearance get_Appearance() {
    return new Appearance(opensimSimulationJNI.ContactGeometry_get_Appearance__SWIG_1(swigCPtr, this), false);
  }

  public Appearance upd_Appearance() {
    return new Appearance(opensimSimulationJNI.ContactGeometry_upd_Appearance__SWIG_1(swigCPtr, this), false);
  }

  public void set_Appearance(Appearance value) {
    opensimSimulationJNI.ContactGeometry_set_Appearance__SWIG_1(swigCPtr, this, Appearance.getCPtr(value), value);
  }

  public void setPropertyIndex_socket_frame(SWIGTYPE_p_OpenSim__PropertyIndex value) {
    opensimSimulationJNI.ContactGeometry_PropertyIndex_socket_frame_set(swigCPtr, this, SWIGTYPE_p_OpenSim__PropertyIndex.getCPtr(value));
  }

  public SWIGTYPE_p_OpenSim__PropertyIndex getPropertyIndex_socket_frame() {
    return new SWIGTYPE_p_OpenSim__PropertyIndex(opensimSimulationJNI.ContactGeometry_PropertyIndex_socket_frame_get(swigCPtr, this), true);
  }

  public void connectSocket_frame(OpenSimObject object) {
    opensimSimulationJNI.ContactGeometry_connectSocket_frame(swigCPtr, this, OpenSimObject.getCPtr(object), object);
  }

  /**
   *  Get the PhysicalFrame this geometry is attached to. 
   */
  public PhysicalFrame getFrame() {
    return new PhysicalFrame(opensimSimulationJNI.ContactGeometry_getFrame(swigCPtr, this), false);
  }

  /**
   *  %Set the PhysicalFrame this geometry is attached to. 
   */
  public void setFrame(PhysicalFrame frame) {
    opensimSimulationJNI.ContactGeometry_setFrame(swigCPtr, this, PhysicalFrame.getCPtr(frame), frame);
  }

  /**
   *  Create a new SimTK::ContactGeometry based on this object. 
   */
  public SWIGTYPE_p_SimTK__ContactGeometry createSimTKContactGeometry() {
    return new SWIGTYPE_p_SimTK__ContactGeometry(opensimSimulationJNI.ContactGeometry_createSimTKContactGeometry(swigCPtr, this), true);
  }

  /**
   *  Get a Transform representing the position and orientation of the<br>
   * geometry relative to the PhysicalFrame `F` to which this geometry is<br>
   * connected.<br>
   * <br>
   * If you want the transform of this geometry relative to the Frame (or<br>
   * Ground) `B` in which this geometry is fixed, you can use the following<br>
   * code:<br>
   * {@code 
  const auto& X_BF = geom.getFrame().findTransformInBaseFrame();
  const auto X_FP = geom.getTransform();
  const auto X_BP = X_BF * X_FP;
  }<br>
   * <br>
   * Prior to OpenSim 4.0, there wwas no intermediate PhysicalFrame `F`, so<br>
   * this method essentially returned `X_BP`. 
   */
  public Transform getTransform() {
    return new Transform(opensimSimulationJNI.ContactGeometry_getTransform(swigCPtr, this), true);
  }

  /**
   * Scale a ContactGeometry based on XYZ scale factors for the bodies.<br>
   * <br>
   * @param aScaleSet Set of XYZ scale factors for the bodies.
   */
  public void scale(ScaleSet aScaleSet) {
    opensimSimulationJNI.ContactGeometry_scale(swigCPtr, this, ScaleSet.getCPtr(aScaleSet), aScaleSet);
  }

  /**
   *  <b>(Deprecated)</b> Use get_location() instead. 
   */
  public Vec3 getLocation() {
    return new Vec3(opensimSimulationJNI.ContactGeometry_getLocation(swigCPtr, this), false);
  }

  /**
   *  <b>(Deprecated)</b> Use set_location() instead. 
   */
  public void setLocation(Vec3 location) {
    opensimSimulationJNI.ContactGeometry_setLocation(swigCPtr, this, Vec3.getCPtr(location), location);
  }

  /**
   *  <b>(Deprecated)</b> Use get_orientation() instead. 
   */
  public Vec3 getOrientation() {
    return new Vec3(opensimSimulationJNI.ContactGeometry_getOrientation(swigCPtr, this), false);
  }

  /**
   *  <b>(Deprecated)</b> Use set_orientation() instead. 
   */
  public void setOrientation(Vec3 orientation) {
    opensimSimulationJNI.ContactGeometry_setOrientation(swigCPtr, this, Vec3.getCPtr(orientation), orientation);
  }

  /**
   *  <b>(Deprecated)</b> Use getFrame() instead.<br>
   * Get the Body this geometry is attached to. 
   */
  public PhysicalFrame getBody() {
    return new PhysicalFrame(opensimSimulationJNI.ContactGeometry_getBody(swigCPtr, this), false);
  }

  /**
   *  <b>(Deprecated)</b> Use setFrame() instead.<br>
   * %Set the Body this geometry is attached to. 
   */
  public void setBody(PhysicalFrame body) {
    opensimSimulationJNI.ContactGeometry_setBody(swigCPtr, this, PhysicalFrame.getCPtr(body), body);
  }

}
