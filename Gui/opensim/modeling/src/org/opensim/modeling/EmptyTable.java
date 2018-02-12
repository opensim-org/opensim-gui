/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class EmptyTable extends OpenSimException {
  private transient long swigCPtr;

  protected EmptyTable(long cPtr, boolean cMemoryOwn) {
    super(opensimCommonJNI.EmptyTable_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected static long getCPtr(EmptyTable obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimCommonJNI.delete_EmptyTable(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public EmptyTable(String file, long line, String func) {
    this(opensimCommonJNI.new_EmptyTable(file, line, func), true);
  }

}
