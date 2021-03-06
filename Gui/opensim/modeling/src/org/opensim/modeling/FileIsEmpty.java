/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class FileIsEmpty extends IOError {
  private transient long swigCPtr;

  public FileIsEmpty(long cPtr, boolean cMemoryOwn) {
    super(opensimCommonJNI.FileIsEmpty_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(FileIsEmpty obj) {
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
        opensimCommonJNI.delete_FileIsEmpty(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public FileIsEmpty(String file, long line, String func, String filename) {
    this(opensimCommonJNI.new_FileIsEmpty(file, line, func, filename), true);
  }

}
