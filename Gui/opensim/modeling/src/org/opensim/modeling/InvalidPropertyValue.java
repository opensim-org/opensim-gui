/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.1.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 *  Property Exceptions
 */
public class InvalidPropertyValue extends OpenSimException {
  private transient long swigCPtr;

  public InvalidPropertyValue(long cPtr, boolean cMemoryOwn) {
    super(opensimCommonJNI.InvalidPropertyValue_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(InvalidPropertyValue obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  public static long swigRelease(InvalidPropertyValue obj) {
    long ptr = 0;
    if (obj != null) {
      if (!obj.swigCMemOwn)
        throw new RuntimeException("Cannot release ownership as memory is not owned");
      ptr = obj.swigCPtr;
      obj.swigCMemOwn = false;
      obj.delete();
    }
    return ptr;
  }

  @SuppressWarnings("deprecation")
  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimCommonJNI.delete_InvalidPropertyValue(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public InvalidPropertyValue(String file, long line, String func, OpenSimObject obj, String propertyName, String errorMsg) {
    this(opensimCommonJNI.new_InvalidPropertyValue__SWIG_0(file, line, func, OpenSimObject.getCPtr(obj), obj, propertyName, errorMsg), true);
  }

  public InvalidPropertyValue(String file, long line, String func, Component component, String propertyName, String errorMsg) {
    this(opensimCommonJNI.new_InvalidPropertyValue__SWIG_1(file, line, func, Component.getCPtr(component), component, propertyName, errorMsg), true);
  }

}
