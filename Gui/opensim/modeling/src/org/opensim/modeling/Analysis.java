/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.1.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 * An abstract class for specifying the interface for an analysis<br>
 * plugin.<br>
 * <br>
 * @author Frank C. Anderson, Ajay Seth<br>
 * @version 1.0
 */
public class Analysis extends OpenSimObject {
  private transient long swigCPtr;

  public Analysis(long cPtr, boolean cMemoryOwn) {
    super(opensimSimulationJNI.Analysis_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(Analysis obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  public static long swigRelease(Analysis obj) {
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
        opensimSimulationJNI.delete_Analysis(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public static Analysis safeDownCast(OpenSimObject obj) {
    long cPtr = opensimSimulationJNI.Analysis_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new Analysis(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimSimulationJNI.Analysis_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimSimulationJNI.Analysis_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimSimulationJNI.Analysis_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new Analysis(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimSimulationJNI.Analysis_getConcreteClassName(swigCPtr, this);
  }

  public void set_model(Model value) {
    opensimSimulationJNI.Analysis__model_set(swigCPtr, this, Model.getCPtr(value), value);
  }

  public Model get_model() {
    long cPtr = opensimSimulationJNI.Analysis__model_get(swigCPtr, this);
    return (cPtr == 0) ? null : new Model(cPtr, false);
  }

  public void set_statesStore(Storage value) {
    opensimSimulationJNI.Analysis__statesStore_set(swigCPtr, this, Storage.getCPtr(value), value);
  }

  public Storage get_statesStore() {
    long cPtr = opensimSimulationJNI.Analysis__statesStore_get(swigCPtr, this);
    return (cPtr == 0) ? null : new Storage(cPtr, false);
  }

  public int begin(State s) {
    return opensimSimulationJNI.Analysis_begin(swigCPtr, this, State.getCPtr(s), s);
  }

  public int step(State s, int stepNumber) {
    return opensimSimulationJNI.Analysis_step(swigCPtr, this, State.getCPtr(s), s, stepNumber);
  }

  public int end(State s) {
    return opensimSimulationJNI.Analysis_end(swigCPtr, this, State.getCPtr(s), s);
  }

  /**
   * set pointer to model to be analyzed.<br>
   * @param aModel
   */
  public void setModel(Model aModel) {
    opensimSimulationJNI.Analysis_setModel(swigCPtr, this, Model.getCPtr(aModel), aModel);
  }

  /**
   * set states storage for analysis.<br>
   * @param aStatesStore
   */
  public void setStatesStore(Storage aStatesStore) {
    opensimSimulationJNI.Analysis_setStatesStore(swigCPtr, this, Storage.getCPtr(aStatesStore), aStatesStore);
  }

  public void setOn(boolean aTrueFalse) {
    opensimSimulationJNI.Analysis_setOn(swigCPtr, this, aTrueFalse);
  }

  public boolean getOn() {
    return opensimSimulationJNI.Analysis_getOn(swigCPtr, this);
  }

  public void setStartTime(double aStartTime) {
    opensimSimulationJNI.Analysis_setStartTime(swigCPtr, this, aStartTime);
  }

  public double getStartTime() {
    return opensimSimulationJNI.Analysis_getStartTime(swigCPtr, this);
  }

  public void setEndTime(double aEndTime) {
    opensimSimulationJNI.Analysis_setEndTime(swigCPtr, this, aEndTime);
  }

  public double getEndTime() {
    return opensimSimulationJNI.Analysis_getEndTime(swigCPtr, this);
  }

  /**
   * %Set whether or not to write the output of angles in degrees.<br>
   * This flag must be set before an analysis is performed to ensure that<br>
   * the results are in the proper format.<br>
   * @param aTrueFalse Output will be in degrees if "true" and in radians<br>
   * if "false".
   */
  public void setInDegrees(boolean aTrueFalse) {
    opensimSimulationJNI.Analysis_setInDegrees(swigCPtr, this, aTrueFalse);
  }

  public boolean getInDegrees() {
    return opensimSimulationJNI.Analysis_getInDegrees(swigCPtr, this);
  }

  public boolean proceed(int aStep) {
    return opensimSimulationJNI.Analysis_proceed__SWIG_0(swigCPtr, this, aStep);
  }

  public boolean proceed() {
    return opensimSimulationJNI.Analysis_proceed__SWIG_1(swigCPtr, this);
  }

  public void setStepInterval(int aStepInterval) {
    opensimSimulationJNI.Analysis_setStepInterval(swigCPtr, this, aStepInterval);
  }

  public int getStepInterval() {
    return opensimSimulationJNI.Analysis_getStepInterval(swigCPtr, this);
  }

  /**
   * %Set the column labels for this analysis.<br>
   * @param aLabels an Array of strings (labels).
   */
  public void setColumnLabels(ArrayStr aLabels) {
    opensimSimulationJNI.Analysis_setColumnLabels(swigCPtr, this, ArrayStr.getCPtr(aLabels), aLabels);
  }

  public ArrayStr getColumnLabels() {
    return new ArrayStr(opensimSimulationJNI.Analysis_getColumnLabels(swigCPtr, this), false);
  }

  public ArrayStorage getStorageList() {
    return new ArrayStorage(opensimSimulationJNI.Analysis_getStorageList(swigCPtr, this), false);
  }

  public void setPrintResultFiles(boolean aToWrite) {
    opensimSimulationJNI.Analysis_setPrintResultFiles(swigCPtr, this, aToWrite);
  }

  public boolean getPrintResultFiles() {
    return opensimSimulationJNI.Analysis_getPrintResultFiles(swigCPtr, this);
  }

  /**
   * Print the results of the analysis.<br>
   * <br>
   * @param aBaseName Base name of file to which to print the data.<br>
   * @param aDir      Directory name.<br>
   * @param aDT       Time interval between results (linear interpolation <br>
   *                  is used). If not supplied as an argument or negative, <br>
   *                  all time steps are printed without interpolation.<br>
   * @param aExtension    File extension if not the default ".sto".<br>
   * <br>
   * @return -1 on error, 0 otherwise.
   */
  public int printResults(String aBaseName, String aDir, double aDT, String aExtension) {
    return opensimSimulationJNI.Analysis_printResults__SWIG_0(swigCPtr, this, aBaseName, aDir, aDT, aExtension);
  }

  /**
   * Print the results of the analysis.<br>
   * <br>
   * @param aBaseName Base name of file to which to print the data.<br>
   * @param aDir      Directory name.<br>
   * @param aDT       Time interval between results (linear interpolation <br>
   *                  is used). If not supplied as an argument or negative, <br>
   *                  all time steps are printed without interpolation.<br>
   * <br>
   * <br>
   * @return -1 on error, 0 otherwise.
   */
  public int printResults(String aBaseName, String aDir, double aDT) {
    return opensimSimulationJNI.Analysis_printResults__SWIG_1(swigCPtr, this, aBaseName, aDir, aDT);
  }

  /**
   * Print the results of the analysis.<br>
   * <br>
   * @param aBaseName Base name of file to which to print the data.<br>
   * @param aDir      Directory name.<br>
   * <br>
   * <br>
   * <br>
   * @return -1 on error, 0 otherwise.
   */
  public int printResults(String aBaseName, String aDir) {
    return opensimSimulationJNI.Analysis_printResults__SWIG_2(swigCPtr, this, aBaseName, aDir);
  }

  /**
   * Print the results of the analysis.<br>
   * <br>
   * @param aBaseName Base name of file to which to print the data.<br>
   * <br>
   * <br>
   * <br>
   * <br>
   * @return -1 on error, 0 otherwise.
   */
  public int printResults(String aBaseName) {
    return opensimSimulationJNI.Analysis_printResults__SWIG_3(swigCPtr, this, aBaseName);
  }

}
