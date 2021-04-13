/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 *  A path constraint to be enforced in the optimal control problem.<br>
 * The use of 'path' here is unrelated to muscle paths, GeometryPath,<br>
 * or file system paths (e.g., Path).<br>
 * <p alt="For developers"><br>
 * Every time the problem is solved, a copy of this constraint is used. An<br>
 * individual instance of a constraint is only ever used in a single problem.<br>
 * Therefore, there is no need to clear cache variables that you create in<br>
 * initializeImpl(). Also, information stored in this constraint does not<br>
 * persist across multiple solves.<br>
 * </p>
 */
public class MocoPathConstraint extends OpenSimObject {
  private transient long swigCPtr;

  public MocoPathConstraint(long cPtr, boolean cMemoryOwn) {
    super(opensimMocoJNI.MocoPathConstraint_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(MocoPathConstraint obj) {
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
        opensimMocoJNI.delete_MocoPathConstraint(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static MocoPathConstraint safeDownCast(OpenSimObject obj) {
    long cPtr = opensimMocoJNI.MocoPathConstraint_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new MocoPathConstraint(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimMocoJNI.MocoPathConstraint_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimMocoJNI.MocoPathConstraint_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimMocoJNI.MocoPathConstraint_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new MocoPathConstraint(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimMocoJNI.MocoPathConstraint_getConcreteClassName(swigCPtr, this);
  }

  public SWIGTYPE_p_MocoConstraintInfo getConstraintInfo() {
    return new SWIGTYPE_p_MocoConstraintInfo(opensimMocoJNI.MocoPathConstraint_getConstraintInfo(swigCPtr, this), false);
  }

  public SWIGTYPE_p_MocoConstraintInfo updConstraintInfo() {
    return new SWIGTYPE_p_MocoConstraintInfo(opensimMocoJNI.MocoPathConstraint_updConstraintInfo(swigCPtr, this), false);
  }

  public void setConstraintInfo(SWIGTYPE_p_MocoConstraintInfo cInfo) {
    opensimMocoJNI.MocoPathConstraint_setConstraintInfo(swigCPtr, this, SWIGTYPE_p_MocoConstraintInfo.getCPtr(cInfo));
  }

  /**
   *  For use by solvers. This index is the location of this<br>
   *     MocoPathConstraint's first error in the MocoProblem's full path<br>
   *     constraint errors vector. Since it is set by the MocoProblem, it is only<br>
   *     available after initialization. 
   */
  public int getPathConstraintIndex() {
    return opensimMocoJNI.MocoPathConstraint_getPathConstraintIndex(swigCPtr, this);
  }

  /**
   *  Calculate errors in the path constraint equations. The *errors* argument<br>
   *     represents the error vector for this MocoPathConstraint. The errors vector<br>
   *     is passed to calcPathConstraintErrorsImpl(), which is defined by derived<br>
   *     classes.<br>
   *     initializeOnModel() has been invoked. 
   */
  public void calcPathConstraintErrors(State state, Vector errors) {
    opensimMocoJNI.MocoPathConstraint_calcPathConstraintErrors(swigCPtr, this, State.getCPtr(state), state, Vector.getCPtr(errors), errors);
  }

  /**
   *  Calculate errors in the path constraint equations. The *errors* argument<br>
   *     represents the concatenated error vector for all path constraints in the<br>
   *     MocoProblem. This method creates a view into *errors* to access the<br>
   *     elements for this MocoPathConstraint and passes this view to<br>
   *     calcPathConstraintErrorsImpl().<br>
   *     initializeOnModel() has been invoked. 
   */
  public void calcPathConstraintErrorsView(State state, Vector errors) {
    opensimMocoJNI.MocoPathConstraint_calcPathConstraintErrorsView(swigCPtr, this, State.getCPtr(state), state, Vector.getCPtr(errors), errors);
  }

  /**
   *  Perform error checks on user input for this constraint, and cache<br>
   *     quantities needed when computing the constraint errors.<br>
   *     to efficiently evaluate the constraint.<br>
   *     This function must be invoked before invoking<br>
   *     calcPathConstraintErrors(). 
   */
  public void initializeOnModel(Model model, SWIGTYPE_p_OpenSim__MocoProblemInfo arg1, int pathConstraintIndex) {
    opensimMocoJNI.MocoPathConstraint_initializeOnModel(swigCPtr, this, Model.getCPtr(model), model, SWIGTYPE_p_OpenSim__MocoProblemInfo.getCPtr(arg1), pathConstraintIndex);
  }

}
