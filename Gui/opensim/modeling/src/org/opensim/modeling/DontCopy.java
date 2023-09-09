/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 *  This is a special type used for causing invocation of a particular<br>
 * constructor or method overload that will avoid making a copy of the source<br>
 * (that is, perform a "shallow" copy rather than a "deep" copy). Typically these<br>
 * methods will have some dangerous side effects so make sure you know what you're<br>
 * doing. *
 */
public class DontCopy {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  public DontCopy(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  public static long getCPtr(DontCopy obj) {
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
        opensimSimbodyJNI.delete_DontCopy(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public DontCopy() {
    this(opensimSimbodyJNI.new_DontCopy(), true);
  }

}
