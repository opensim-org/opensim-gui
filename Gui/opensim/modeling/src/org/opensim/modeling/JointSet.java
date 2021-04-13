/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 * A class for holding a set of joints.<br>
 * <br>
 * @author Peter Loan<br>
 * @version 1.0
 */
public class JointSet extends ModelComponentSetJoints {
  private transient long swigCPtr;

  public JointSet(long cPtr, boolean cMemoryOwn) {
    super(opensimSimulationJNI.JointSet_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(JointSet obj) {
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
        opensimSimulationJNI.delete_JointSet(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static JointSet safeDownCast(OpenSimObject obj) {
    long cPtr = opensimSimulationJNI.JointSet_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new JointSet(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimSimulationJNI.JointSet_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimSimulationJNI.JointSet_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimSimulationJNI.JointSet_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new JointSet(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimSimulationJNI.JointSet_getConcreteClassName(swigCPtr, this);
  }

  public JointSet() {
    this(opensimSimulationJNI.new_JointSet__SWIG_0(), true);
  }

  public JointSet(String file, boolean updateFromXML) throws java.io.IOException {
    this(opensimSimulationJNI.new_JointSet__SWIG_1(file, updateFromXML), true);
  }

  public JointSet(String file) throws java.io.IOException {
    this(opensimSimulationJNI.new_JointSet__SWIG_2(file), true);
  }

}
