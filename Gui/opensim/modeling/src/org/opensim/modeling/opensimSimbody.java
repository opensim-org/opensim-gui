/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class opensimSimbody implements opensimSimbodyConstants {
  public static SpatialVec findRelativeVelocity(SWIGTYPE_p_Transform X_FA, SpatialVec V_FA, SWIGTYPE_p_Transform X_FB, SpatialVec V_FB) {
    return new SpatialVec(opensimSimbodyJNI.findRelativeVelocity(SWIGTYPE_p_Transform.getCPtr(X_FA), SpatialVec.getCPtr(V_FA), V_FA, SWIGTYPE_p_Transform.getCPtr(X_FB), SpatialVec.getCPtr(V_FB), V_FB), true);
  }

  public static SpatialVec findRelativeVelocityInF(Vec3 p_AB_F, SpatialVec V_FA, SpatialVec V_FB) {
    return new SpatialVec(opensimSimbodyJNI.findRelativeVelocityInF(Vec3.getCPtr(p_AB_F), p_AB_F, SpatialVec.getCPtr(V_FA), V_FA, SpatialVec.getCPtr(V_FB), V_FB), true);
  }

  public static SpatialVec findRelativeAcceleration(SWIGTYPE_p_Transform X_FA, SpatialVec V_FA, SpatialVec A_FA, SWIGTYPE_p_Transform X_FB, SpatialVec V_FB, SpatialVec A_FB) {
    return new SpatialVec(opensimSimbodyJNI.findRelativeAcceleration(SWIGTYPE_p_Transform.getCPtr(X_FA), SpatialVec.getCPtr(V_FA), V_FA, SpatialVec.getCPtr(A_FA), A_FA, SWIGTYPE_p_Transform.getCPtr(X_FB), SpatialVec.getCPtr(V_FB), V_FB, SpatialVec.getCPtr(A_FB), A_FB), true);
  }

  public static SpatialVec findRelativeAccelerationInF(Vec3 p_AB_F, SpatialVec V_FA, SpatialVec A_FA, SpatialVec V_FB, SpatialVec A_FB) {
    return new SpatialVec(opensimSimbodyJNI.findRelativeAccelerationInF(Vec3.getCPtr(p_AB_F), p_AB_F, SpatialVec.getCPtr(V_FA), V_FA, SpatialVec.getCPtr(A_FA), A_FA, SpatialVec.getCPtr(V_FB), V_FB, SpatialVec.getCPtr(A_FB), A_FB), true);
  }

  public static SpatialVec reverseRelativeVelocity(SWIGTYPE_p_Transform X_AB, SpatialVec V_AB) {
    return new SpatialVec(opensimSimbodyJNI.reverseRelativeVelocity(SWIGTYPE_p_Transform.getCPtr(X_AB), SpatialVec.getCPtr(V_AB), V_AB), true);
  }

  public static SpatialVec reverseRelativeVelocityInA(SWIGTYPE_p_Transform X_AB, SpatialVec V_AB) {
    return new SpatialVec(opensimSimbodyJNI.reverseRelativeVelocityInA(SWIGTYPE_p_Transform.getCPtr(X_AB), SpatialVec.getCPtr(V_AB), V_AB), true);
  }

  public static SpatialVec shiftVelocityBy(SpatialVec V_AB, Vec3 r_A) {
    return new SpatialVec(opensimSimbodyJNI.shiftVelocityBy(SpatialVec.getCPtr(V_AB), V_AB, Vec3.getCPtr(r_A), r_A), true);
  }

  public static SpatialVec shiftVelocityFromTo(SpatialVec V_A_BP, Vec3 fromP_A, Vec3 toQ_A) {
    return new SpatialVec(opensimSimbodyJNI.shiftVelocityFromTo(SpatialVec.getCPtr(V_A_BP), V_A_BP, Vec3.getCPtr(fromP_A), fromP_A, Vec3.getCPtr(toQ_A), toQ_A), true);
  }

  public static SpatialVec shiftForceBy(SpatialVec F_AP, Vec3 r_A) {
    return new SpatialVec(opensimSimbodyJNI.shiftForceBy(SpatialVec.getCPtr(F_AP), F_AP, Vec3.getCPtr(r_A), r_A), true);
  }

  public static SpatialVec shiftForceFromTo(SpatialVec F_AP, Vec3 fromP_A, Vec3 toQ_A) {
    return new SpatialVec(opensimSimbodyJNI.shiftForceFromTo(SpatialVec.getCPtr(F_AP), F_AP, Vec3.getCPtr(fromP_A), fromP_A, Vec3.getCPtr(toQ_A), toQ_A), true);
  }

  /**
   *  Shift a relative spatial acceleration measured at some point to that<br>
   * same relative spatial quantity but measured at a new point given by an offset<br>
   * from the old one.<br>
   * <br>
   * @param A_AB<br>
   *     The relative spatial acceleration of frame B in frame A, measured and <br>
   *     expressed in frame A.<br>
   * @param w_AB<br>
   *     The relative angular velocity of frame B in frame A, expressed in frame A.<br>
   * @param r_A<br>
   *     The vector offset, expressed in frame A, by which to change the point at <br>
   *     which the translational component of the relative spatial acceleration is <br>
   *     measured.<br>
   * @return A_A_BQ, the relative acceleration of frame B in frame A, but measured at<br>
   *     the point Q=Bo+r rather than at B's origin Bo.<br>
   * <br>
   * Given the spatial acceleration A_AB and angular velocity w_AB of frame B in A, <br>
   * measured at a point coincident with B's origin Bo, change it to the spatial <br>
   * acceleration A_A_BQ representing the same relationship but with the acceleration<br>
   * measured at a new point Q=Bo+r for some position vector r. All vectors are <br>
   * measured and expressed in frame A, including the vector r. Example:<br>
   * {@code 
      SpatialVec A_AB;     // assume these are known from somewhere
      Vec3       w_AB;
      Vec3       offset_A; // Q = Bo + offset
  
      SpatialVec A_A_BQ = shiftAccelerationBy(A_AB, w_AB, offset_A);
  }<br>
   * <br>
   * Note: The shift in location leaves the relative angular acceleration b the same<br>
   * but results in the linear acceleration changing by b X r + w X (w X r).<br>
   * <br>
   * Cost is 33 flops. @see shiftAccelerationFromTo() *
   */
  public static SpatialVec shiftAccelerationBy(SpatialVec A_AB, Vec3 w_AB, Vec3 r_A) {
    return new SpatialVec(opensimSimbodyJNI.shiftAccelerationBy(SpatialVec.getCPtr(A_AB), A_AB, Vec3.getCPtr(w_AB), w_AB, Vec3.getCPtr(r_A), r_A), true);
  }

  /**
   *  Shift a relative spatial acceleration measured at some point P to <br>
   * that same relative spatial quantity but measured at a new point Q given the <br>
   * points P and Q.<br>
   * <br>
   * @param A_A_BP<br>
   *     The relative spatial acceleration of frame B in frame A, measured and <br>
   *     expressed in frame A, with the linear component measured at a point P.<br>
   * @param w_AB<br>
   *     The relative angular velocity of frame B in frame A, expressed in frame A.<br>
   * @param fromP_A<br>
   *     The "from" point P at which the input linear acceleration was<br>
   *     measured, given as a vector from A's origin Ao to the point P, <br>
   *     expressed in A.<br>
   * @param toQ_A<br>
   *     The "to" point Q at which we want to re-measure the linear acceleration, <br>
   *     given as a vector from A's origin Ao to the point Q, expressed <br>
   *     in A.<br>
   * @return A_A_BQ, the relative acceleration of frame B in frame A, but measured at<br>
   *     the point Q rather than at point P.<br>
   * <br>
   * Given the spatial acceleration A_A_BP of frame B in A, measured at a point P,<br>
   * change it to the spatial acceleration A_A_BQ representing the same relationship <br>
   * but with the acceleration measured at a new point Q. Example:<br>
   * {@code 
  assume these are known from somewhere
      Transform  X_AB;    // contains the vector from Ao to Bo  
      SpatialVec A_AB;    // linear acceleration is measured at origin Bo of B
      Vec3       w_AB;
      Vec3       p_AQ;    // vector from Ao to some other point Q, in A
  
      SpatialVec A_A_BQ = shiftAccelerationFromTo(A_AB, w_AB, X_AB.p(), p_AQ);
  }<br>
   * <br>
   * Note: There is no way to know whether the supplied acceleration was<br>
   * actually measured at P; this method really just shifts the relative <br>
   * acceleration by the vector r=(to-from). Use it carefully.<br>
   * <br>
   * Cost is 36 flops. @see shiftAccelerationBy() *
   */
  public static SpatialVec shiftAccelerationFromTo(SpatialVec A_A_BP, Vec3 w_AB, Vec3 fromP_A, Vec3 toQ_A) {
    return new SpatialVec(opensimSimbodyJNI.shiftAccelerationFromTo(SpatialVec.getCPtr(A_A_BP), A_A_BP, Vec3.getCPtr(w_AB), w_AB, Vec3.getCPtr(fromP_A), fromP_A, Vec3.getCPtr(toQ_A), toQ_A), true);
  }

  /**
   *  Obtain version information for the currently-loaded SimTKcommon library. 
   */
  public static void SimTK_version_SimTKcommon(SWIGTYPE_p_int major, SWIGTYPE_p_int minor, SWIGTYPE_p_int build) {
    opensimSimbodyJNI.SimTK_version_SimTKcommon(SWIGTYPE_p_int.getCPtr(major), SWIGTYPE_p_int.getCPtr(minor), SWIGTYPE_p_int.getCPtr(build));
  }

  /**
   * Obtain "about" information for the currently-loaded SimTKcommon library.<br>
   * Available keywords are "version" (major.minor.build), "library", <br>
   * "type" (shared or static), "copyright", "svn_revision", "authors", <br>
   * "debug" (debug or release).
   */
  public static void SimTK_about_SimTKcommon(String key, int maxlen, String value) {
    opensimSimbodyJNI.SimTK_about_SimTKcommon(key, maxlen, value);
  }

  public static boolean canStoreInNonnegativeInt(boolean arg0) {
    return opensimSimbodyJNI.canStoreInNonnegativeInt__SWIG_0(arg0);
  }

  public static boolean canStoreInNonnegativeInt(char c) {
    return opensimSimbodyJNI.canStoreInNonnegativeInt__SWIG_1(c);
  }

  public static boolean canStoreInNonnegativeInt(short arg0) {
    return opensimSimbodyJNI.canStoreInNonnegativeInt__SWIG_2(arg0);
  }

  public static boolean canStoreInNonnegativeInt(byte c) {
    return opensimSimbodyJNI.canStoreInNonnegativeInt__SWIG_3(c);
  }

  public static boolean canStoreInNonnegativeInt(int arg0) {
    return opensimSimbodyJNI.canStoreInNonnegativeInt__SWIG_5(arg0);
  }

  public static boolean canStoreInNonnegativeInt(long l) {
    return opensimSimbodyJNI.canStoreInNonnegativeInt__SWIG_8(l);
  }

  public static boolean canStoreInNonnegativeInt(java.math.BigInteger u) {
    return opensimSimbodyJNI.canStoreInNonnegativeInt__SWIG_11(u);
  }

  public static boolean isSizeInRange(char sz, char mx) {
    return opensimSimbodyJNI.isSizeInRange__SWIG_0(sz, mx);
  }

  public static boolean isSizeInRange(byte sz, byte mx) {
    return opensimSimbodyJNI.isSizeInRange__SWIG_1(sz, mx);
  }

  public static boolean isSizeInRange(short sz, short mx) {
    return opensimSimbodyJNI.isSizeInRange__SWIG_2(sz, mx);
  }

  public static boolean isSizeInRange(int sz, int mx) {
    return opensimSimbodyJNI.isSizeInRange__SWIG_3(sz, mx);
  }

  public static boolean isSizeInRange(long sz, long mx) {
    return opensimSimbodyJNI.isSizeInRange__SWIG_5(sz, mx);
  }

  public static boolean isSizeInRange(java.math.BigInteger sz, java.math.BigInteger mx) {
    return opensimSimbodyJNI.isSizeInRange__SWIG_10(sz, mx);
  }

  public static boolean isIndexInRange(char ix, char sz) {
    return opensimSimbodyJNI.isIndexInRange__SWIG_0(ix, sz);
  }

  public static boolean isIndexInRange(byte ix, byte sz) {
    return opensimSimbodyJNI.isIndexInRange__SWIG_1(ix, sz);
  }

  public static boolean isIndexInRange(short ix, short sz) {
    return opensimSimbodyJNI.isIndexInRange__SWIG_2(ix, sz);
  }

  public static boolean isIndexInRange(int ix, int sz) {
    return opensimSimbodyJNI.isIndexInRange__SWIG_3(ix, sz);
  }

  public static boolean isIndexInRange(long ix, long sz) {
    return opensimSimbodyJNI.isIndexInRange__SWIG_5(ix, sz);
  }

  public static boolean isIndexInRange(java.math.BigInteger ix, java.math.BigInteger sz) {
    return opensimSimbodyJNI.isIndexInRange__SWIG_10(ix, sz);
  }

  public static boolean isNonnegative(boolean arg0) {
    return opensimSimbodyJNI.isNonnegative__SWIG_0(arg0);
  }

  public static boolean isNonnegative(char n) {
    return opensimSimbodyJNI.isNonnegative__SWIG_1(n);
  }

  public static boolean isNonnegative(byte n) {
    return opensimSimbodyJNI.isNonnegative__SWIG_2(n);
  }

  public static boolean isNonnegative(short n) {
    return opensimSimbodyJNI.isNonnegative__SWIG_3(n);
  }

  public static boolean isNonnegative(int n) {
    return opensimSimbodyJNI.isNonnegative__SWIG_4(n);
  }

  public static boolean isNonnegative(long n) {
    return opensimSimbodyJNI.isNonnegative__SWIG_6(n);
  }

  public static boolean isNonnegative(java.math.BigInteger arg0) {
    return opensimSimbodyJNI.isNonnegative__SWIG_11(arg0);
  }

  public static int getInvalidIndex() {
    return opensimSimbodyJNI.InvalidIndex_get();
  }

  /**
   *  RGB=( 0, 0, 0)
   */
  public static Vec3 getBlack() {
    long cPtr = opensimSimbodyJNI.Black_get();
    return (cPtr == 0) ? null : new Vec3(cPtr, false);
  }

  /**
   *  RGB=(.5,.5,.5)
   */
  public static Vec3 getGray() {
    long cPtr = opensimSimbodyJNI.Gray_get();
    return (cPtr == 0) ? null : new Vec3(cPtr, false);
  }

  /**
   *  RGB=( 1, 0, 0)
   */
  public static Vec3 getRed() {
    long cPtr = opensimSimbodyJNI.Red_get();
    return (cPtr == 0) ? null : new Vec3(cPtr, false);
  }

  /**
   *  RGB=( 0, 1, 0)
   */
  public static Vec3 getGreen() {
    long cPtr = opensimSimbodyJNI.Green_get();
    return (cPtr == 0) ? null : new Vec3(cPtr, false);
  }

  /**
   *  RGB=( 0, 0, 1)
   */
  public static Vec3 getBlue() {
    long cPtr = opensimSimbodyJNI.Blue_get();
    return (cPtr == 0) ? null : new Vec3(cPtr, false);
  }

  /**
   *  RGB=( 1, 1, 0)
   */
  public static Vec3 getYellow() {
    long cPtr = opensimSimbodyJNI.Yellow_get();
    return (cPtr == 0) ? null : new Vec3(cPtr, false);
  }

  /**
   *  RGB=( 1,.5, 0)
   */
  public static Vec3 getOrange() {
    long cPtr = opensimSimbodyJNI.Orange_get();
    return (cPtr == 0) ? null : new Vec3(cPtr, false);
  }

  /**
   *  RGB=( 1, 0, 1)
   */
  public static Vec3 getMagenta() {
    long cPtr = opensimSimbodyJNI.Magenta_get();
    return (cPtr == 0) ? null : new Vec3(cPtr, false);
  }

  /**
   *  RGB=(.5, 0,.5)
   */
  public static Vec3 getPurple() {
    long cPtr = opensimSimbodyJNI.Purple_get();
    return (cPtr == 0) ? null : new Vec3(cPtr, false);
  }

  /**
   *  RGB=( 0, 1, 1)
   */
  public static Vec3 getCyan() {
    long cPtr = opensimSimbodyJNI.Cyan_get();
    return (cPtr == 0) ? null : new Vec3(cPtr, false);
  }

  /**
   *  RGB=( 1, 1, 1)
   */
  public static Vec3 getWhite() {
    long cPtr = opensimSimbodyJNI.White_get();
    return (cPtr == 0) ? null : new Vec3(cPtr, false);
  }

}
