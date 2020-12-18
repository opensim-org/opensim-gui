/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.9
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class MocoPhase extends OpenSimObject {
  private transient long swigCPtr;

  public MocoPhase(long cPtr, boolean cMemoryOwn) {
    super(opensimMocoJNI.MocoPhase_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(MocoPhase obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimMocoJNI.delete_MocoPhase(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

    public static MocoBounds convertArrayToMB(double[] arr) throws Exception {
        MocoBounds bounds = new MocoBounds();
        if (arr == null) {
            return bounds;
        } else if (arr.length > 2) {
            throw new RuntimeException(
                    "Bounds cannot have more than 2 elements.");
        } else if (arr.length == 1) {
            bounds = new MocoBounds(arr[0]);
        } else if (arr.length == 2) {
            bounds = new MocoBounds(arr[0], arr[1]);
        }
        return bounds;
    }
    public static MocoInitialBounds convertArrayToMIB(double[] arr)
            throws Exception {
        MocoInitialBounds bounds = new MocoInitialBounds();
        if (arr == null) {
            return bounds;
        } else if (arr.length > 2) {
            throw new RuntimeException(
                    "Bounds cannot have more than 2 elements.");
        } else if (arr.length == 1) {
            bounds = new MocoInitialBounds(arr[0]);
        } else if (arr.length == 2) {
            bounds = new MocoInitialBounds(arr[0], arr[1]);
        }
        return bounds;
    }
    public static MocoFinalBounds convertArrayToMFB(double[] arr)
            throws Exception {
        MocoFinalBounds bounds = new MocoFinalBounds();
        if (arr == null) {
            return bounds;
        } else if (arr.length > 2) {
            throw new RuntimeException(
                    "Bounds cannot have more than 2 elements.");
        } else if (arr.length == 1) {
            bounds = new MocoFinalBounds(arr[0]);
        } else if (arr.length == 2) {
            bounds = new MocoFinalBounds(arr[0], arr[1]);
        }
        return bounds;
    }

    public void setModel(Model model) {
        private_setModel(model);
        model.markAdopted();
    }
    public void addParameter(MocoParameter obj) {
        private_addParameter(obj);
        obj.markAdopted();
    }
    public void addGoal(MocoGoal obj) {
        private_addGoal(obj);
        obj.markAdopted();
    }
    public void addPathConstraint(MocoPathConstraint obj) {
        private_addPathConstraint(obj);
        obj.markAdopted();
    }
    public void setTimeBounds(double[] ib, double[] fb) throws Exception {
        setTimeBounds(this.convertArrayToMIB(ib), this.convertArrayToMFB(fb));
    }
    public void setStateInfo(String name, double[] b) throws Exception {
        setStateInfo(name, this.convertArrayToMB(b));
    }
    public void setStateInfo(String name, double[] b, double[] ib)
        throws Exception {
        setStateInfo(name, this.convertArrayToMB(b),
                this.convertArrayToMIB(ib));
    }
    public void setStateInfo(String name, double[] b, double[] ib, double[] fb)
        throws Exception {
        setStateInfo(name, this.convertArrayToMB(b),
                this.convertArrayToMIB(ib), this.convertArrayToMFB(fb));
    }
    public void setStateInfoPattern(String pattern, double[] b) 
        throws Exception {
        setStateInfoPattern(pattern, this.convertArrayToMB(b));
    }
    public void setStateInfoPattern(String pattern, double[] b, double[] ib)
        throws Exception {
        setStateInfoPattern(pattern, this.convertArrayToMB(b),
                this.convertArrayToMIB(ib));
    }
    public void 
    setStateInfoPattern(String pattern, double[] b, double[] ib, double[] fb) 
        throws Exception {
        setStateInfoPattern(pattern, this.convertArrayToMB(b),
                this.convertArrayToMIB(ib), this.convertArrayToMFB(fb));
    }

    public void setControlInfo(String name, double[] b) throws Exception {
        setControlInfo(name, this.convertArrayToMB(b));
    }
    public void setControlInfo(String name, double[] b, double[] ib)
        throws Exception {
        setControlInfo(name, this.convertArrayToMB(b),
                this.convertArrayToMIB(ib));
    }
    public void setControlInfo(String name, double[] b, double[] ib, double[] fb)
        throws Exception {
        setControlInfo(name, this.convertArrayToMB(b),
                this.convertArrayToMIB(ib), this.convertArrayToMFB(fb));
    }
    public void setControlInfoPattern(String pattern, double[] b) 
        throws Exception {
        setControlInfoPattern(pattern, this.convertArrayToMB(b));
    }
    public void setControlInfoPattern(String pattern, double[] b, double[] ib)
        throws Exception {
        setControlInfoPattern(pattern, this.convertArrayToMB(b),
                this.convertArrayToMIB(ib));
    }
    public void 
    setControlInfoPattern(String pattern, double[] b, double[] ib, double[] fb) 
        throws Exception {
        setControlInfoPattern(pattern, this.convertArrayToMB(b),
                this.convertArrayToMIB(ib), this.convertArrayToMFB(fb));
    }

  public static MocoPhase safeDownCast(OpenSimObject obj) {
    long cPtr = opensimMocoJNI.MocoPhase_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new MocoPhase(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimMocoJNI.MocoPhase_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimMocoJNI.MocoPhase_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimMocoJNI.MocoPhase_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new MocoPhase(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimMocoJNI.MocoPhase_getConcreteClassName(swigCPtr, this);
  }

  public MocoPhase() {
    this(opensimMocoJNI.new_MocoPhase(), true);
  }

  public Model setModelAsCopy(Model model) {
    long cPtr = opensimMocoJNI.MocoPhase_setModelAsCopy(swigCPtr, this, Model.getCPtr(model), model);
    return (cPtr == 0) ? null : new Model(cPtr, false);
  }

  public void setModelProcessor(ModelProcessor model) {
    opensimMocoJNI.MocoPhase_setModelProcessor(swigCPtr, this, ModelProcessor.getCPtr(model), model);
  }

  public ModelProcessor updModelProcessor() {
    return new ModelProcessor(opensimMocoJNI.MocoPhase_updModelProcessor(swigCPtr, this), false);
  }

  public void setTimeBounds(MocoInitialBounds arg0, MocoFinalBounds arg1) {
    opensimMocoJNI.MocoPhase_setTimeBounds(swigCPtr, this, MocoInitialBounds.getCPtr(arg0), arg0, MocoFinalBounds.getCPtr(arg1), arg1);
  }

  public void printStateNamesWithSubstring(String name) {
    opensimMocoJNI.MocoPhase_printStateNamesWithSubstring(swigCPtr, this, name);
  }

  public void setStateInfo(String name, MocoBounds bounds, MocoInitialBounds init, MocoFinalBounds arg3) {
    opensimMocoJNI.MocoPhase_setStateInfo__SWIG_0(swigCPtr, this, name, MocoBounds.getCPtr(bounds), bounds, MocoInitialBounds.getCPtr(init), init, MocoFinalBounds.getCPtr(arg3), arg3);
  }

  public void setStateInfo(String name, MocoBounds bounds, MocoInitialBounds init) {
    opensimMocoJNI.MocoPhase_setStateInfo__SWIG_1(swigCPtr, this, name, MocoBounds.getCPtr(bounds), bounds, MocoInitialBounds.getCPtr(init), init);
  }

  public void setStateInfo(String name, MocoBounds bounds) {
    opensimMocoJNI.MocoPhase_setStateInfo__SWIG_2(swigCPtr, this, name, MocoBounds.getCPtr(bounds), bounds);
  }

  public void setStateInfoPattern(String pattern, MocoBounds bounds, MocoInitialBounds init, MocoFinalBounds arg3) {
    opensimMocoJNI.MocoPhase_setStateInfoPattern__SWIG_0(swigCPtr, this, pattern, MocoBounds.getCPtr(bounds), bounds, MocoInitialBounds.getCPtr(init), init, MocoFinalBounds.getCPtr(arg3), arg3);
  }

  public void setStateInfoPattern(String pattern, MocoBounds bounds, MocoInitialBounds init) {
    opensimMocoJNI.MocoPhase_setStateInfoPattern__SWIG_1(swigCPtr, this, pattern, MocoBounds.getCPtr(bounds), bounds, MocoInitialBounds.getCPtr(init), init);
  }

  public void setStateInfoPattern(String pattern, MocoBounds bounds) {
    opensimMocoJNI.MocoPhase_setStateInfoPattern__SWIG_2(swigCPtr, this, pattern, MocoBounds.getCPtr(bounds), bounds);
  }

  public void printControlNamesWithSubstring(String name) {
    opensimMocoJNI.MocoPhase_printControlNamesWithSubstring(swigCPtr, this, name);
  }

  public void setControlInfo(String name, MocoBounds arg1, MocoInitialBounds arg2, MocoFinalBounds arg3) {
    opensimMocoJNI.MocoPhase_setControlInfo__SWIG_0(swigCPtr, this, name, MocoBounds.getCPtr(arg1), arg1, MocoInitialBounds.getCPtr(arg2), arg2, MocoFinalBounds.getCPtr(arg3), arg3);
  }

  public void setControlInfo(String name, MocoBounds arg1, MocoInitialBounds arg2) {
    opensimMocoJNI.MocoPhase_setControlInfo__SWIG_1(swigCPtr, this, name, MocoBounds.getCPtr(arg1), arg1, MocoInitialBounds.getCPtr(arg2), arg2);
  }

  public void setControlInfo(String name, MocoBounds arg1) {
    opensimMocoJNI.MocoPhase_setControlInfo__SWIG_2(swigCPtr, this, name, MocoBounds.getCPtr(arg1), arg1);
  }

  public void setDefaultSpeedBounds(MocoBounds bounds) {
    opensimMocoJNI.MocoPhase_setDefaultSpeedBounds(swigCPtr, this, MocoBounds.getCPtr(bounds), bounds);
  }

  public void setControlInfoPattern(String pattern, MocoBounds arg1, MocoInitialBounds arg2, MocoFinalBounds arg3) {
    opensimMocoJNI.MocoPhase_setControlInfoPattern__SWIG_0(swigCPtr, this, pattern, MocoBounds.getCPtr(arg1), arg1, MocoInitialBounds.getCPtr(arg2), arg2, MocoFinalBounds.getCPtr(arg3), arg3);
  }

  public void setControlInfoPattern(String pattern, MocoBounds arg1, MocoInitialBounds arg2) {
    opensimMocoJNI.MocoPhase_setControlInfoPattern__SWIG_1(swigCPtr, this, pattern, MocoBounds.getCPtr(arg1), arg1, MocoInitialBounds.getCPtr(arg2), arg2);
  }

  public void setControlInfoPattern(String pattern, MocoBounds arg1) {
    opensimMocoJNI.MocoPhase_setControlInfoPattern__SWIG_2(swigCPtr, this, pattern, MocoBounds.getCPtr(arg1), arg1);
  }

  public void setBoundActivationFromExcitation(boolean tf) {
    opensimMocoJNI.MocoPhase_setBoundActivationFromExcitation(swigCPtr, this, tf);
  }

  public void setKinematicConstraintBounds(MocoBounds bounds) {
    opensimMocoJNI.MocoPhase_setKinematicConstraintBounds(swigCPtr, this, MocoBounds.getCPtr(bounds), bounds);
  }

  public void setMultiplierBounds(MocoBounds bounds) {
    opensimMocoJNI.MocoPhase_setMultiplierBounds(swigCPtr, this, MocoBounds.getCPtr(bounds), bounds);
  }

  public Model getModel() {
    return new Model(opensimMocoJNI.MocoPhase_getModel(swigCPtr, this), false);
  }

  public Model updModel() {
    return new Model(opensimMocoJNI.MocoPhase_updModel(swigCPtr, this), false);
  }

  public ModelProcessor getModelProcessor() {
    return new ModelProcessor(opensimMocoJNI.MocoPhase_getModelProcessor(swigCPtr, this), false);
  }

  public MocoInitialBounds getTimeInitialBounds() {
    return new MocoInitialBounds(opensimMocoJNI.MocoPhase_getTimeInitialBounds(swigCPtr, this), true);
  }

  public MocoFinalBounds getTimeFinalBounds() {
    return new MocoFinalBounds(opensimMocoJNI.MocoPhase_getTimeFinalBounds(swigCPtr, this), true);
  }

  public MocoVariableInfo getStateInfo(String name) {
    return new MocoVariableInfo(opensimMocoJNI.MocoPhase_getStateInfo(swigCPtr, this, name), false);
  }

  public MocoVariableInfo getControlInfo(String name) {
    return new MocoVariableInfo(opensimMocoJNI.MocoPhase_getControlInfo(swigCPtr, this, name), false);
  }

  public MocoBounds getDefaultSpeedBounds() {
    return new MocoBounds(opensimMocoJNI.MocoPhase_getDefaultSpeedBounds(swigCPtr, this), false);
  }

  public boolean getBoundActivationFromExcitation() {
    return opensimMocoJNI.MocoPhase_getBoundActivationFromExcitation(swigCPtr, this);
  }

  public MocoBounds getKinematicConstraintBounds() {
    return new MocoBounds(opensimMocoJNI.MocoPhase_getKinematicConstraintBounds(swigCPtr, this), false);
  }

  public MocoBounds getMultiplierBounds() {
    return new MocoBounds(opensimMocoJNI.MocoPhase_getMultiplierBounds(swigCPtr, this), false);
  }

  public MocoParameter getParameter(String name) {
    return new MocoParameter(opensimMocoJNI.MocoPhase_getParameter(swigCPtr, this, name), false);
  }

  public MocoParameter updParameter(String name) {
    return new MocoParameter(opensimMocoJNI.MocoPhase_updParameter(swigCPtr, this, name), false);
  }

  public MocoGoal getGoal(String name) {
    return new MocoGoal(opensimMocoJNI.MocoPhase_getGoal(swigCPtr, this, name), false);
  }

  public MocoGoal updGoal(String name) {
    return new MocoGoal(opensimMocoJNI.MocoPhase_updGoal(swigCPtr, this, name), false);
  }

  public MocoPathConstraint getPathConstraint(String name) {
    return new MocoPathConstraint(opensimMocoJNI.MocoPhase_getPathConstraint(swigCPtr, this, name), false);
  }

  public MocoPathConstraint updPathConstraint(String name) {
    return new MocoPathConstraint(opensimMocoJNI.MocoPhase_updPathConstraint(swigCPtr, this, name), false);
  }

  private void private_setModel(Model model) {
    opensimMocoJNI.MocoPhase_private_setModel(swigCPtr, this, Model.getCPtr(model), model);
  }

  private void private_addParameter(MocoParameter ptr) {
    opensimMocoJNI.MocoPhase_private_addParameter(swigCPtr, this, MocoParameter.getCPtr(ptr), ptr);
  }

  private void private_addGoal(MocoGoal ptr) {
    opensimMocoJNI.MocoPhase_private_addGoal(swigCPtr, this, MocoGoal.getCPtr(ptr), ptr);
  }

  private void private_addPathConstraint(MocoPathConstraint ptr) {
    opensimMocoJNI.MocoPhase_private_addPathConstraint(swigCPtr, this, MocoPathConstraint.getCPtr(ptr), ptr);
  }

}
