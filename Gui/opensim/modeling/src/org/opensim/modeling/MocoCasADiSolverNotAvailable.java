/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.9
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class MocoCasADiSolverNotAvailable extends OpenSimException {
  private transient long swigCPtr;

  public MocoCasADiSolverNotAvailable(long cPtr, boolean cMemoryOwn) {
    super(opensimMocoJNI.MocoCasADiSolverNotAvailable_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(MocoCasADiSolverNotAvailable obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimMocoJNI.delete_MocoCasADiSolverNotAvailable(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public MocoCasADiSolverNotAvailable(String file, int line, String func) {
    this(opensimMocoJNI.new_MocoCasADiSolverNotAvailable(file, line, func), true);
  }

}
