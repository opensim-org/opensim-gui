/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.1.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public final class BodyOrSpaceType {
  public final static BodyOrSpaceType BodyRotationSequence = new BodyOrSpaceType("BodyRotationSequence", opensimSimbodyJNI.BodyRotationSequence_get());
  public final static BodyOrSpaceType SpaceRotationSequence = new BodyOrSpaceType("SpaceRotationSequence", opensimSimbodyJNI.SpaceRotationSequence_get());

  public final int swigValue() {
    return swigValue;
  }

  public String toString() {
    return swigName;
  }

  public static BodyOrSpaceType swigToEnum(int swigValue) {
    if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
      return swigValues[swigValue];
    for (int i = 0; i < swigValues.length; i++)
      if (swigValues[i].swigValue == swigValue)
        return swigValues[i];
    throw new IllegalArgumentException("No enum " + BodyOrSpaceType.class + " with value " + swigValue);
  }

  private BodyOrSpaceType(String swigName) {
    this.swigName = swigName;
    this.swigValue = swigNext++;
  }

  private BodyOrSpaceType(String swigName, int swigValue) {
    this.swigName = swigName;
    this.swigValue = swigValue;
    swigNext = swigValue+1;
  }

  private BodyOrSpaceType(String swigName, BodyOrSpaceType swigEnum) {
    this.swigName = swigName;
    this.swigValue = swigEnum.swigValue;
    swigNext = this.swigValue+1;
  }

  private static BodyOrSpaceType[] swigValues = { BodyRotationSequence, SpaceRotationSequence };
  private static int swigNext = 0;
  private final int swigValue;
  private final String swigName;
}

