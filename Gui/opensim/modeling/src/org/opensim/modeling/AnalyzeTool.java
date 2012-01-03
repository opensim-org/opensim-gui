/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.4
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class AnalyzeTool extends AbstractTool {
  private long swigCPtr;

  public AnalyzeTool(long cPtr, boolean cMemoryOwn) {
    super(opensimModelJNI.AnalyzeTool_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(AnalyzeTool obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimModelJNI.delete_AnalyzeTool(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public AnalyzeTool() {
    this(opensimModelJNI.new_AnalyzeTool__SWIG_0(), true);
  }

  public AnalyzeTool(String aFileName, boolean aLoadModelAndInput) throws java.io.IOException {
    this(opensimModelJNI.new_AnalyzeTool__SWIG_1(aFileName, aLoadModelAndInput), true);
  }

  public AnalyzeTool(String aFileName) throws java.io.IOException {
    this(opensimModelJNI.new_AnalyzeTool__SWIG_2(aFileName), true);
  }

  public AnalyzeTool(AnalyzeTool aObject) {
    this(opensimModelJNI.new_AnalyzeTool__SWIG_3(AnalyzeTool.getCPtr(aObject), aObject), true);
  }

  public AnalyzeTool(Model aModel) {
    this(opensimModelJNI.new_AnalyzeTool__SWIG_4(Model.getCPtr(aModel), aModel), true);
  }

  public OpenSimObject copy() {
    long cPtr = opensimModelJNI.AnalyzeTool_copy(swigCPtr, this);
    return (cPtr == 0) ? null : new OpenSimObject(cPtr, false);
  }

  public void updateFromXMLNode(SWIGTYPE_p_SimTK__Xml__Element aNode, int versionNumber) {
    opensimModelJNI.AnalyzeTool_updateFromXMLNode__SWIG_0(swigCPtr, this, SWIGTYPE_p_SimTK__Xml__Element.getCPtr(aNode), versionNumber);
  }

  public void updateFromXMLNode(SWIGTYPE_p_SimTK__Xml__Element aNode) {
    opensimModelJNI.AnalyzeTool_updateFromXMLNode__SWIG_1(swigCPtr, this, SWIGTYPE_p_SimTK__Xml__Element.getCPtr(aNode));
  }

  public void setStatesStorage(Storage aStore) {
    opensimModelJNI.AnalyzeTool_setStatesStorage(swigCPtr, this, Storage.getCPtr(aStore), aStore);
  }

  public static Storage createStatesStorageFromCoordinatesAndSpeeds(Model aModel, Storage aQStore, Storage aUStore) {
    long cPtr = opensimModelJNI.AnalyzeTool_createStatesStorageFromCoordinatesAndSpeeds(Model.getCPtr(aModel), aModel, Storage.getCPtr(aQStore), aQStore, Storage.getCPtr(aUStore), aUStore);
    return (cPtr == 0) ? null : new Storage(cPtr, false);
  }

  public Storage getStatesStorage() {
    return new Storage(opensimModelJNI.AnalyzeTool_getStatesStorage(swigCPtr, this), false);
  }

  public String getStatesFileName() {
    return opensimModelJNI.AnalyzeTool_getStatesFileName(swigCPtr, this);
  }

  public void setStatesFileName(String aFileName) {
    opensimModelJNI.AnalyzeTool_setStatesFileName(swigCPtr, this, aFileName);
  }

  public String getCoordinatesFileName() {
    return opensimModelJNI.AnalyzeTool_getCoordinatesFileName(swigCPtr, this);
  }

  public void setCoordinatesFileName(String aFileName) {
    opensimModelJNI.AnalyzeTool_setCoordinatesFileName(swigCPtr, this, aFileName);
  }

  public String getSpeedsFileName() {
    return opensimModelJNI.AnalyzeTool_getSpeedsFileName(swigCPtr, this);
  }

  public void setSpeedsFileName(String aFileName) {
    opensimModelJNI.AnalyzeTool_setSpeedsFileName(swigCPtr, this, aFileName);
  }

  public double getLowpassCutoffFrequency() {
    return opensimModelJNI.AnalyzeTool_getLowpassCutoffFrequency(swigCPtr, this);
  }

  public void setLowpassCutoffFrequency(double aLowpassCutoffFrequency) {
    opensimModelJNI.AnalyzeTool_setLowpassCutoffFrequency(swigCPtr, this, aLowpassCutoffFrequency);
  }

  public void setStatesFromMotion(SWIGTYPE_p_SimTK__State s, Storage aMotion, boolean aInDegrees) throws java.io.IOException {
    opensimModelJNI.AnalyzeTool_setStatesFromMotion(swigCPtr, this, SWIGTYPE_p_SimTK__State.getCPtr(s), Storage.getCPtr(aMotion), aMotion, aInDegrees);
  }

  public void loadStatesFromFile(SWIGTYPE_p_SimTK__State s) throws java.io.IOException {
    opensimModelJNI.AnalyzeTool_loadStatesFromFile(swigCPtr, this, SWIGTYPE_p_SimTK__State.getCPtr(s));
  }

  public void verifyControlsStates() {
    opensimModelJNI.AnalyzeTool_verifyControlsStates(swigCPtr, this);
  }

  public void setPrintResultFiles(boolean aToWrite) {
    opensimModelJNI.AnalyzeTool_setPrintResultFiles(swigCPtr, this, aToWrite);
  }

  public boolean run() throws java.io.IOException {
    return opensimModelJNI.AnalyzeTool_run__SWIG_0(swigCPtr, this);
  }

  public boolean run(boolean plotting) throws java.io.IOException {
    return opensimModelJNI.AnalyzeTool_run__SWIG_1(swigCPtr, this, plotting);
  }

}
