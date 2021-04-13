/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 * A class for specifying and computing parameters for tracking a point.<br>
 * <br>
 * @author Frank C. Anderson<br>
 * @version 1.0
 */
public class CMC_Point extends CMC_Task {
  private transient long swigCPtr;

  public CMC_Point(long cPtr, boolean cMemoryOwn) {
    super(opensimActuatorsAnalysesToolsJNI.CMC_Point_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(CMC_Point obj) {
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
        opensimActuatorsAnalysesToolsJNI.delete_CMC_Point(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static CMC_Point safeDownCast(OpenSimObject obj) {
    long cPtr = opensimActuatorsAnalysesToolsJNI.CMC_Point_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new CMC_Point(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimActuatorsAnalysesToolsJNI.CMC_Point_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimActuatorsAnalysesToolsJNI.CMC_Point_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimActuatorsAnalysesToolsJNI.CMC_Point_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new CMC_Point(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimActuatorsAnalysesToolsJNI.CMC_Point_getConcreteClassName(swigCPtr, this);
  }

  public CMC_Point(Vec3 aPoint) {
    this(opensimActuatorsAnalysesToolsJNI.new_CMC_Point__SWIG_0(Vec3.getCPtr(aPoint), aPoint), true);
  }

  public CMC_Point() {
    this(opensimActuatorsAnalysesToolsJNI.new_CMC_Point__SWIG_1(), true);
  }

  public CMC_Point(CMC_Point aTask) {
    this(opensimActuatorsAnalysesToolsJNI.new_CMC_Point__SWIG_2(CMC_Point.getCPtr(aTask), aTask), true);
  }

  public void setModel(Model aModel) {
    opensimActuatorsAnalysesToolsJNI.CMC_Point_setModel(swigCPtr, this, Model.getCPtr(aModel), aModel);
  }

  public void setPoint(Vec3 aPoint) {
    opensimActuatorsAnalysesToolsJNI.CMC_Point_setPoint(swigCPtr, this, Vec3.getCPtr(aPoint), aPoint);
  }

  public Vec3 getPoint() {
    return new Vec3(opensimActuatorsAnalysesToolsJNI.CMC_Point_getPoint(swigCPtr, this), true);
  }

  public void computeErrors(State s, double aT) {
    opensimActuatorsAnalysesToolsJNI.CMC_Point_computeErrors(swigCPtr, this, State.getCPtr(s), s, aT);
  }

  public void computeDesiredAccelerations(State s, double aT) {
    opensimActuatorsAnalysesToolsJNI.CMC_Point_computeDesiredAccelerations__SWIG_0(swigCPtr, this, State.getCPtr(s), s, aT);
  }

  public void computeDesiredAccelerations(State s, double aTI, double aTF) {
    opensimActuatorsAnalysesToolsJNI.CMC_Point_computeDesiredAccelerations__SWIG_1(swigCPtr, this, State.getCPtr(s), s, aTI, aTF);
  }

  public void computeAccelerations(State s) {
    opensimActuatorsAnalysesToolsJNI.CMC_Point_computeAccelerations(swigCPtr, this, State.getCPtr(s), s);
  }

  public void updateFromXMLNode(SWIGTYPE_p_SimTK__Xml__Element aNode, int versionNumber) {
    opensimActuatorsAnalysesToolsJNI.CMC_Point_updateFromXMLNode__SWIG_0(swigCPtr, this, SWIGTYPE_p_SimTK__Xml__Element.getCPtr(aNode), versionNumber);
  }

  public void updateFromXMLNode(SWIGTYPE_p_SimTK__Xml__Element aNode) {
    opensimActuatorsAnalysesToolsJNI.CMC_Point_updateFromXMLNode__SWIG_1(swigCPtr, this, SWIGTYPE_p_SimTK__Xml__Element.getCPtr(aNode));
  }

}
