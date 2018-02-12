/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class Mesh extends Geometry {
  private transient long swigCPtr;

  protected Mesh(long cPtr, boolean cMemoryOwn) {
    super(opensimSimulationJNI.Mesh_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected static long getCPtr(Mesh obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimSimulationJNI.delete_Mesh(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static Mesh safeDownCast(OpenSimObject obj) {
    long cPtr = opensimSimulationJNI.Mesh_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new Mesh(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimSimulationJNI.Mesh_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimSimulationJNI.Mesh_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimSimulationJNI.Mesh_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new Mesh(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimSimulationJNI.Mesh_getConcreteClassName(swigCPtr, this);
  }

  public void copyProperty_mesh_file(Mesh source) {
    opensimSimulationJNI.Mesh_copyProperty_mesh_file(swigCPtr, this, Mesh.getCPtr(source), source);
  }

  public String get_mesh_file(int i) {
    return opensimSimulationJNI.Mesh_get_mesh_file__SWIG_0(swigCPtr, this, i);
  }

  public SWIGTYPE_p_std__string upd_mesh_file(int i) {
    return new SWIGTYPE_p_std__string(opensimSimulationJNI.Mesh_upd_mesh_file__SWIG_0(swigCPtr, this, i), false);
  }

  public void set_mesh_file(int i, String value) {
    opensimSimulationJNI.Mesh_set_mesh_file__SWIG_0(swigCPtr, this, i, value);
  }

  public int append_mesh_file(String value) {
    return opensimSimulationJNI.Mesh_append_mesh_file(swigCPtr, this, value);
  }

  public void constructProperty_mesh_file(String initValue) {
    opensimSimulationJNI.Mesh_constructProperty_mesh_file(swigCPtr, this, initValue);
  }

  public String get_mesh_file() {
    return opensimSimulationJNI.Mesh_get_mesh_file__SWIG_1(swigCPtr, this);
  }

  public SWIGTYPE_p_std__string upd_mesh_file() {
    return new SWIGTYPE_p_std__string(opensimSimulationJNI.Mesh_upd_mesh_file__SWIG_1(swigCPtr, this), false);
  }

  public void set_mesh_file(String value) {
    opensimSimulationJNI.Mesh_set_mesh_file__SWIG_1(swigCPtr, this, value);
  }

  public Mesh() {
    this(opensimSimulationJNI.new_Mesh__SWIG_0(), true);
  }

  public Mesh(String geomFile) {
    this(opensimSimulationJNI.new_Mesh__SWIG_1(geomFile), true);
  }

  public String getGeometryFilename() {
    return opensimSimulationJNI.Mesh_getGeometryFilename(swigCPtr, this);
  }

}
