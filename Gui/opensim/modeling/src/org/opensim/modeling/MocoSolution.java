/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.9
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class MocoSolution extends MocoTrajectory {
  private transient long swigCPtr;

  public MocoSolution(long cPtr, boolean cMemoryOwn) {
    super(opensimMocoJNI.MocoSolution_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(MocoSolution obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimMocoJNI.delete_MocoSolution(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public MocoTrajectory clone() {
    long cPtr = opensimMocoJNI.MocoSolution_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new MocoSolution(cPtr, true);
  }

  public boolean success() {
    return opensimMocoJNI.MocoSolution_success(swigCPtr, this);
  }

  public double getObjective() {
    return opensimMocoJNI.MocoSolution_getObjective(swigCPtr, this);
  }

  public String getStatus() {
    return opensimMocoJNI.MocoSolution_getStatus(swigCPtr, this);
  }

  public int getNumIterations() {
    return opensimMocoJNI.MocoSolution_getNumIterations(swigCPtr, this);
  }

  public double getSolverDuration() {
    return opensimMocoJNI.MocoSolution_getSolverDuration(swigCPtr, this);
  }

  public int getNumObjectiveTerms() {
    return opensimMocoJNI.MocoSolution_getNumObjectiveTerms(swigCPtr, this);
  }

  public StdVectorString getObjectiveTermNames() {
    return new StdVectorString(opensimMocoJNI.MocoSolution_getObjectiveTermNames(swigCPtr, this), true);
  }

  public double getObjectiveTerm(String name) {
    return opensimMocoJNI.MocoSolution_getObjectiveTerm(swigCPtr, this, name);
  }

  public double getObjectiveTermByIndex(int index) {
    return opensimMocoJNI.MocoSolution_getObjectiveTermByIndex(swigCPtr, this, index);
  }

  public void printObjectiveBreakdown() {
    opensimMocoJNI.MocoSolution_printObjectiveBreakdown(swigCPtr, this);
  }

  public MocoSolution unseal() {
    return new MocoSolution(opensimMocoJNI.MocoSolution_unseal(swigCPtr, this), false);
  }

  public MocoSolution seal() {
    return new MocoSolution(opensimMocoJNI.MocoSolution_seal(swigCPtr, this), false);
  }

  public boolean isSealed() {
    return opensimMocoJNI.MocoSolution_isSealed(swigCPtr, this);
  }

  public MocoSolution() {
    this(opensimMocoJNI.new_MocoSolution(), true);
  }

}