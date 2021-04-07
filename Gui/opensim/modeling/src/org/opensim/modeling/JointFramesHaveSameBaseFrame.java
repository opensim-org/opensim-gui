/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class JointFramesHaveSameBaseFrame extends OpenSimException {
  private transient long swigCPtr;

  public JointFramesHaveSameBaseFrame(long cPtr, boolean cMemoryOwn) {
    super(opensimSimulationJNI.JointFramesHaveSameBaseFrame_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(JointFramesHaveSameBaseFrame obj) {
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
        opensimSimulationJNI.delete_JointFramesHaveSameBaseFrame(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public JointFramesHaveSameBaseFrame(String file, long line, String func, String thisName, String parentName, String childName, String baseName) {
    this(opensimSimulationJNI.new_JointFramesHaveSameBaseFrame(file, line, func, thisName, parentName, childName, baseName), true);
  }

}
