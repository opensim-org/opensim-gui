/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.9
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class ModOpReplaceMusclesWithDeGrooteFregly2016 extends ModelOperator {
  private transient long swigCPtr;

  public ModOpReplaceMusclesWithDeGrooteFregly2016(long cPtr, boolean cMemoryOwn) {
    super(opensimMocoJNI.ModOpReplaceMusclesWithDeGrooteFregly2016_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(ModOpReplaceMusclesWithDeGrooteFregly2016 obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimMocoJNI.delete_ModOpReplaceMusclesWithDeGrooteFregly2016(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static ModOpReplaceMusclesWithDeGrooteFregly2016 safeDownCast(OpenSimObject obj) {
    long cPtr = opensimMocoJNI.ModOpReplaceMusclesWithDeGrooteFregly2016_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new ModOpReplaceMusclesWithDeGrooteFregly2016(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimMocoJNI.ModOpReplaceMusclesWithDeGrooteFregly2016_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimMocoJNI.ModOpReplaceMusclesWithDeGrooteFregly2016_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimMocoJNI.ModOpReplaceMusclesWithDeGrooteFregly2016_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new ModOpReplaceMusclesWithDeGrooteFregly2016(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimMocoJNI.ModOpReplaceMusclesWithDeGrooteFregly2016_getConcreteClassName(swigCPtr, this);
  }

  public void operate(Model model, String arg1) {
    opensimMocoJNI.ModOpReplaceMusclesWithDeGrooteFregly2016_operate(swigCPtr, this, Model.getCPtr(model), model, arg1);
  }

  public ModOpReplaceMusclesWithDeGrooteFregly2016() {
    this(opensimMocoJNI.new_ModOpReplaceMusclesWithDeGrooteFregly2016(), true);
  }

}
