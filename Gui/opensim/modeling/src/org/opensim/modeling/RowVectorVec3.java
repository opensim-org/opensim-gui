/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 *  RowVectors are much less common than Vectors. However, if a Simmatrix user <br>
 *  wants one, this is the class intended to appear in user code. It can be a <br>
 *  fixed-size view of someone else's data, or can be a resizable data owner <br>
 *  itself, although of course it will always have just one row.
 */
public class RowVectorVec3 extends RowVectorBaseVec3 {
  private transient long swigCPtr;

  public RowVectorVec3(long cPtr, boolean cMemoryOwn) {
    super(opensimSimbodyJNI.RowVectorVec3_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(RowVectorVec3 obj) {
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
        opensimSimbodyJNI.delete_RowVectorVec3(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

    public static RowVectorVec3 createFromMat(double[][] data) throws Exception {
        int numRows = data.length;
        int numCols = 0;
        if (numRows > 0) {
            numCols = data[0].length;
        }
        if (numRows != 3) {
            throw new Exception("Number of rows must be 3.");
        }
        RowVectorVec3 v = new RowVectorVec3(numCols);
        for (int i = 0; i < numCols; ++i) {
            v.set(i, new Vec3(data[0][i], data[1][i], data[2][i]));
        }
        return v;
    }

  public RowVectorVec3() {
    this(opensimSimbodyJNI.new_RowVectorVec3__SWIG_0(), true);
  }

  public RowVectorVec3(RowVectorVec3 src) {
    this(opensimSimbodyJNI.new_RowVectorVec3__SWIG_1(RowVectorVec3.getCPtr(src), src), true);
  }

  public RowVectorVec3(RowVectorBaseVec3 src) {
    this(opensimSimbodyJNI.new_RowVectorVec3__SWIG_2(RowVectorBaseVec3.getCPtr(src), src), true);
  }

  public RowVectorVec3(int n) {
    this(opensimSimbodyJNI.new_RowVectorVec3__SWIG_3(n), true);
  }

  public RowVectorVec3(int n, Vec3 cppInitialValues) {
    this(opensimSimbodyJNI.new_RowVectorVec3__SWIG_4(n, Vec3.getCPtr(cppInitialValues), cppInitialValues), true);
  }

  public RowVectorVec3(StdVectorVec3 row) {
    this(opensimSimbodyJNI.new_RowVectorVec3__SWIG_6(StdVectorVec3.getCPtr(row), row), true);
  }

  public VectorVec3 transpose() {
    return new VectorVec3(opensimSimbodyJNI.RowVectorVec3_transpose(swigCPtr, this), true);
  }

}
