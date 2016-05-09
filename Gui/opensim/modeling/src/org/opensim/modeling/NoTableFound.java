/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.7
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class NoTableFound extends InvalidArgument {
  private transient long swigCPtr;

  public NoTableFound(long cPtr, boolean cMemoryOwn) {
    super(opensimModelCommonJNI.NoTableFound_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(NoTableFound obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimModelCommonJNI.delete_NoTableFound(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public NoTableFound(String file, long line, String func) {
    this(opensimModelCommonJNI.new_NoTableFound(file, line, func), true);
  }

}