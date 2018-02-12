/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class SimTKVisualizerInputListener {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected SimTKVisualizerInputListener(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(SimTKVisualizerInputListener obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimSimbodyJNI.delete_SimTKVisualizerInputListener(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public boolean keyPressed(long key, long modifiers) {
    return opensimSimbodyJNI.SimTKVisualizerInputListener_keyPressed(swigCPtr, this, key, modifiers);
  }

  public boolean menuSelected(int menu, int item) {
    return opensimSimbodyJNI.SimTKVisualizerInputListener_menuSelected(swigCPtr, this, menu, item);
  }

  public boolean sliderMoved(int slider, double value) {
    return opensimSimbodyJNI.SimTKVisualizerInputListener_sliderMoved(swigCPtr, this, slider, value);
  }

  public SimTKVisualizerInputListener() {
    this(opensimSimbodyJNI.new_SimTKVisualizerInputListener(), true);
  }

  public final static class Modifier {
    public final static SimTKVisualizerInputListener.Modifier ShiftIsDown = new SimTKVisualizerInputListener.Modifier("ShiftIsDown", opensimSimbodyJNI.SimTKVisualizerInputListener_ShiftIsDown_get());
    public final static SimTKVisualizerInputListener.Modifier ControlIsDown = new SimTKVisualizerInputListener.Modifier("ControlIsDown", opensimSimbodyJNI.SimTKVisualizerInputListener_ControlIsDown_get());
    public final static SimTKVisualizerInputListener.Modifier AltIsDown = new SimTKVisualizerInputListener.Modifier("AltIsDown", opensimSimbodyJNI.SimTKVisualizerInputListener_AltIsDown_get());
    public final static SimTKVisualizerInputListener.Modifier IsSpecialKey = new SimTKVisualizerInputListener.Modifier("IsSpecialKey", opensimSimbodyJNI.SimTKVisualizerInputListener_IsSpecialKey_get());

    public final int swigValue() {
      return swigValue;
    }

    public String toString() {
      return swigName;
    }

    public static Modifier swigToEnum(int swigValue) {
      if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
        return swigValues[swigValue];
      for (int i = 0; i < swigValues.length; i++)
        if (swigValues[i].swigValue == swigValue)
          return swigValues[i];
      throw new IllegalArgumentException("No enum " + Modifier.class + " with value " + swigValue);
    }

    private Modifier(String swigName) {
      this.swigName = swigName;
      this.swigValue = swigNext++;
    }

    private Modifier(String swigName, int swigValue) {
      this.swigName = swigName;
      this.swigValue = swigValue;
      swigNext = swigValue+1;
    }

    private Modifier(String swigName, Modifier swigEnum) {
      this.swigName = swigName;
      this.swigValue = swigEnum.swigValue;
      swigNext = this.swigValue+1;
    }

    private static Modifier[] swigValues = { ShiftIsDown, ControlIsDown, AltIsDown, IsSpecialKey };
    private static int swigNext = 0;
    private final int swigValue;
    private final String swigName;
  }

  public final static long SpecialKeyOffset = opensimSimbodyJNI.SimTKVisualizerInputListener_SpecialKeyOffset_get();
  public final static class KeyCode {
    public final static SimTKVisualizerInputListener.KeyCode KeyControlC = new SimTKVisualizerInputListener.KeyCode("KeyControlC", opensimSimbodyJNI.SimTKVisualizerInputListener_KeyControlC_get());
    public final static SimTKVisualizerInputListener.KeyCode KeyBeep = new SimTKVisualizerInputListener.KeyCode("KeyBeep", opensimSimbodyJNI.SimTKVisualizerInputListener_KeyBeep_get());
    public final static SimTKVisualizerInputListener.KeyCode KeyBackspace = new SimTKVisualizerInputListener.KeyCode("KeyBackspace", opensimSimbodyJNI.SimTKVisualizerInputListener_KeyBackspace_get());
    public final static SimTKVisualizerInputListener.KeyCode KeyTab = new SimTKVisualizerInputListener.KeyCode("KeyTab", opensimSimbodyJNI.SimTKVisualizerInputListener_KeyTab_get());
    public final static SimTKVisualizerInputListener.KeyCode KeyLF = new SimTKVisualizerInputListener.KeyCode("KeyLF", opensimSimbodyJNI.SimTKVisualizerInputListener_KeyLF_get());
    public final static SimTKVisualizerInputListener.KeyCode KeyReturn = new SimTKVisualizerInputListener.KeyCode("KeyReturn", opensimSimbodyJNI.SimTKVisualizerInputListener_KeyReturn_get());
    public final static SimTKVisualizerInputListener.KeyCode KeyEnter = new SimTKVisualizerInputListener.KeyCode("KeyEnter", opensimSimbodyJNI.SimTKVisualizerInputListener_KeyEnter_get());
    public final static SimTKVisualizerInputListener.KeyCode KeyEsc = new SimTKVisualizerInputListener.KeyCode("KeyEsc", opensimSimbodyJNI.SimTKVisualizerInputListener_KeyEsc_get());
    public final static SimTKVisualizerInputListener.KeyCode KeyDelete = new SimTKVisualizerInputListener.KeyCode("KeyDelete", opensimSimbodyJNI.SimTKVisualizerInputListener_KeyDelete_get());
    public final static SimTKVisualizerInputListener.KeyCode KeyF1 = new SimTKVisualizerInputListener.KeyCode("KeyF1", opensimSimbodyJNI.SimTKVisualizerInputListener_KeyF1_get());
    public final static SimTKVisualizerInputListener.KeyCode KeyF2 = new SimTKVisualizerInputListener.KeyCode("KeyF2", opensimSimbodyJNI.SimTKVisualizerInputListener_KeyF2_get());
    public final static SimTKVisualizerInputListener.KeyCode KeyF3 = new SimTKVisualizerInputListener.KeyCode("KeyF3", opensimSimbodyJNI.SimTKVisualizerInputListener_KeyF3_get());
    public final static SimTKVisualizerInputListener.KeyCode KeyF4 = new SimTKVisualizerInputListener.KeyCode("KeyF4", opensimSimbodyJNI.SimTKVisualizerInputListener_KeyF4_get());
    public final static SimTKVisualizerInputListener.KeyCode KeyF5 = new SimTKVisualizerInputListener.KeyCode("KeyF5", opensimSimbodyJNI.SimTKVisualizerInputListener_KeyF5_get());
    public final static SimTKVisualizerInputListener.KeyCode KeyF6 = new SimTKVisualizerInputListener.KeyCode("KeyF6", opensimSimbodyJNI.SimTKVisualizerInputListener_KeyF6_get());
    public final static SimTKVisualizerInputListener.KeyCode KeyF7 = new SimTKVisualizerInputListener.KeyCode("KeyF7", opensimSimbodyJNI.SimTKVisualizerInputListener_KeyF7_get());
    public final static SimTKVisualizerInputListener.KeyCode KeyF8 = new SimTKVisualizerInputListener.KeyCode("KeyF8", opensimSimbodyJNI.SimTKVisualizerInputListener_KeyF8_get());
    public final static SimTKVisualizerInputListener.KeyCode KeyF9 = new SimTKVisualizerInputListener.KeyCode("KeyF9", opensimSimbodyJNI.SimTKVisualizerInputListener_KeyF9_get());
    public final static SimTKVisualizerInputListener.KeyCode KeyF10 = new SimTKVisualizerInputListener.KeyCode("KeyF10", opensimSimbodyJNI.SimTKVisualizerInputListener_KeyF10_get());
    public final static SimTKVisualizerInputListener.KeyCode KeyF11 = new SimTKVisualizerInputListener.KeyCode("KeyF11", opensimSimbodyJNI.SimTKVisualizerInputListener_KeyF11_get());
    public final static SimTKVisualizerInputListener.KeyCode KeyF12 = new SimTKVisualizerInputListener.KeyCode("KeyF12", opensimSimbodyJNI.SimTKVisualizerInputListener_KeyF12_get());
    public final static SimTKVisualizerInputListener.KeyCode KeyLeftArrow = new SimTKVisualizerInputListener.KeyCode("KeyLeftArrow", opensimSimbodyJNI.SimTKVisualizerInputListener_KeyLeftArrow_get());
    public final static SimTKVisualizerInputListener.KeyCode KeyUpArrow = new SimTKVisualizerInputListener.KeyCode("KeyUpArrow", opensimSimbodyJNI.SimTKVisualizerInputListener_KeyUpArrow_get());
    public final static SimTKVisualizerInputListener.KeyCode KeyRightArrow = new SimTKVisualizerInputListener.KeyCode("KeyRightArrow", opensimSimbodyJNI.SimTKVisualizerInputListener_KeyRightArrow_get());
    public final static SimTKVisualizerInputListener.KeyCode KeyDownArrow = new SimTKVisualizerInputListener.KeyCode("KeyDownArrow", opensimSimbodyJNI.SimTKVisualizerInputListener_KeyDownArrow_get());
    public final static SimTKVisualizerInputListener.KeyCode KeyPageUp = new SimTKVisualizerInputListener.KeyCode("KeyPageUp", opensimSimbodyJNI.SimTKVisualizerInputListener_KeyPageUp_get());
    public final static SimTKVisualizerInputListener.KeyCode KeyPageDown = new SimTKVisualizerInputListener.KeyCode("KeyPageDown", opensimSimbodyJNI.SimTKVisualizerInputListener_KeyPageDown_get());
    public final static SimTKVisualizerInputListener.KeyCode KeyHome = new SimTKVisualizerInputListener.KeyCode("KeyHome", opensimSimbodyJNI.SimTKVisualizerInputListener_KeyHome_get());
    public final static SimTKVisualizerInputListener.KeyCode KeyEnd = new SimTKVisualizerInputListener.KeyCode("KeyEnd", opensimSimbodyJNI.SimTKVisualizerInputListener_KeyEnd_get());
    public final static SimTKVisualizerInputListener.KeyCode KeyInsert = new SimTKVisualizerInputListener.KeyCode("KeyInsert", opensimSimbodyJNI.SimTKVisualizerInputListener_KeyInsert_get());

    public final int swigValue() {
      return swigValue;
    }

    public String toString() {
      return swigName;
    }

    public static KeyCode swigToEnum(int swigValue) {
      if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
        return swigValues[swigValue];
      for (int i = 0; i < swigValues.length; i++)
        if (swigValues[i].swigValue == swigValue)
          return swigValues[i];
      throw new IllegalArgumentException("No enum " + KeyCode.class + " with value " + swigValue);
    }

    private KeyCode(String swigName) {
      this.swigName = swigName;
      this.swigValue = swigNext++;
    }

    private KeyCode(String swigName, int swigValue) {
      this.swigName = swigName;
      this.swigValue = swigValue;
      swigNext = swigValue+1;
    }

    private KeyCode(String swigName, KeyCode swigEnum) {
      this.swigName = swigName;
      this.swigValue = swigEnum.swigValue;
      swigNext = this.swigValue+1;
    }

    private static KeyCode[] swigValues = { KeyControlC, KeyBeep, KeyBackspace, KeyTab, KeyLF, KeyReturn, KeyEnter, KeyEsc, KeyDelete, KeyF1, KeyF2, KeyF3, KeyF4, KeyF5, KeyF6, KeyF7, KeyF8, KeyF9, KeyF10, KeyF11, KeyF12, KeyLeftArrow, KeyUpArrow, KeyRightArrow, KeyDownArrow, KeyPageUp, KeyPageDown, KeyHome, KeyEnd, KeyInsert };
    private static int swigNext = 0;
    private final int swigValue;
    private final String swigName;
  }

}
