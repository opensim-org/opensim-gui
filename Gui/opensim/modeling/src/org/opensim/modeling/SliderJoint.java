/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.1.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 * A class implementing a Slider joint. The underlying implementation in Simbody<br>
 * is a SimTK::MobilizedBody::Slider. The Slider provides a single coordinate<br>
 * along the common X-axis of the parent and child joint frames.<br>
 * <br>
 * <img src="sliderJoint.gif"/><br>
 * <br>
 * @author Ajay Seth
 */
public class SliderJoint extends Joint {
  private transient long swigCPtr;

  public SliderJoint(long cPtr, boolean cMemoryOwn) {
    super(opensimSimulationJNI.SliderJoint_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(SliderJoint obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  public static long swigRelease(SliderJoint obj) {
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
        opensimSimulationJNI.delete_SliderJoint(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static SliderJoint safeDownCast(OpenSimObject obj) {
    long cPtr = opensimSimulationJNI.SliderJoint_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new SliderJoint(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimSimulationJNI.SliderJoint_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimSimulationJNI.SliderJoint_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimSimulationJNI.SliderJoint_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new SliderJoint(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimSimulationJNI.SliderJoint_getConcreteClassName(swigCPtr, this);
  }

  /**
   *  Convenience method to get a const reference to the Coordinate associated<br>
   *         with a single-degree-of-freedom Joint. If the Joint has more than one<br>
   *         Coordinate, you must use get_coordinates() or provide the appropriate<br>
   *         argument to the getCoordinate() method defined in the derived class. 
   */
  public Coordinate getCoordinate() {
    return new Coordinate(opensimSimulationJNI.SliderJoint_getCoordinate__SWIG_0_0(swigCPtr, this), false);
  }

  /**
   *  Convenience method to get a writable reference to the Coordinate<br>
   *         associated with a single-degree-of-freedom Joint. If the Joint has more<br>
   *         than one Coordinate, you must use upd_coordinates() or provide the<br>
   *         appropriate argument to the updCoordinate() method defined in the<br>
   *         derived class. 
   */
  public Coordinate updCoordinate() {
    return new Coordinate(opensimSimulationJNI.SliderJoint_updCoordinate__SWIG_0_0(swigCPtr, this), false);
  }

  /**
   *  Get a const reference to the Coordinate associated with this Joint.<br>
   *         @see Coord 
   */
  public Coordinate getCoordinate(SliderJoint.Coord idx) {
    return new Coordinate(opensimSimulationJNI.SliderJoint_getCoordinate__SWIG_1(swigCPtr, this, idx.swigValue()), false);
  }

  /**
   *  Get a writable reference to the Coordinate associated with this Joint.<br>
   *         @see Coord 
   */
  public Coordinate updCoordinate(SliderJoint.Coord idx) {
    return new Coordinate(opensimSimulationJNI.SliderJoint_updCoordinate__SWIG_1(swigCPtr, this, idx.swigValue()), false);
  }

  public SliderJoint() {
    this(opensimSimulationJNI.new_SliderJoint__SWIG_0(), true);
  }

  public SliderJoint(String name, PhysicalFrame parent, PhysicalFrame child) {
    this(opensimSimulationJNI.new_SliderJoint__SWIG_1(name, PhysicalFrame.getCPtr(parent), parent, PhysicalFrame.getCPtr(child), child), true);
  }

  public SliderJoint(String name, PhysicalFrame parent, Vec3 locationInParent, Vec3 orientationInParent, PhysicalFrame child, Vec3 locationInChild, Vec3 orientationInChild) {
    this(opensimSimulationJNI.new_SliderJoint__SWIG_2(name, PhysicalFrame.getCPtr(parent), parent, Vec3.getCPtr(locationInParent), locationInParent, Vec3.getCPtr(orientationInParent), orientationInParent, PhysicalFrame.getCPtr(child), child, Vec3.getCPtr(locationInChild), locationInChild, Vec3.getCPtr(orientationInChild), orientationInChild), true);
  }

  /**
   *  Index of Coordinate for use as an argument to getCoordinate() and<br>
   *         updCoordinate().<br>
   * <br>
   *         <b>C++ example</b><br>
   *         {@code 
          const auto& tx = mySliderJoint.
                           getCoordinate(SliderJoint::Coord::TranslationX);
          }<br>
   * <br>
   *         <b>Python example</b><br>
   *         {@code 
          import opensim
          tx = mySliderJoint.getCoordinate(opensim.SliderJoint.Coord_TranslationX)
          }<br>
   * <br>
   *         <b>Java example</b><br>
   *         {@code 
          tx = mySliderJoint.getCoordinate(SliderJoint.Coord.TranslationX);
          }<br>
   * <br>
   *         <b>MATLAB example</b><br>
   *         {@code 
          tx = mySliderJoint.get_coordinates(0);
          }
   */
  public final static class Coord {
    /**
     *  0
     */
    public final static SliderJoint.Coord TranslationX = new SliderJoint.Coord("TranslationX", opensimSimulationJNI.SliderJoint_Coord_TranslationX_get());

    public final int swigValue() {
      return swigValue;
    }

    public String toString() {
      return swigName;
    }

    public static Coord swigToEnum(int swigValue) {
      if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
        return swigValues[swigValue];
      for (int i = 0; i < swigValues.length; i++)
        if (swigValues[i].swigValue == swigValue)
          return swigValues[i];
      throw new IllegalArgumentException("No enum " + Coord.class + " with value " + swigValue);
    }

    private Coord(String swigName) {
      this.swigName = swigName;
      this.swigValue = swigNext++;
    }

    private Coord(String swigName, int swigValue) {
      this.swigName = swigName;
      this.swigValue = swigValue;
      swigNext = swigValue+1;
    }

    private Coord(String swigName, Coord swigEnum) {
      this.swigName = swigName;
      this.swigValue = swigEnum.swigValue;
      swigNext = this.swigValue+1;
    }

    private static Coord[] swigValues = { TranslationX };
    private static int swigNext = 0;
    private final int swigValue;
    private final String swigName;
  }

}
