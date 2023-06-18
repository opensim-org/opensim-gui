/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.1.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 *  This class manages runtime visualization of a Model that is being <br>
 * manipulated through the OpenSim API. You should not allocate one of these<br>
 * yourself; instead, call the Model's setUseVisualizer() method and let the<br>
 * Model allocate one for itself. You may find the defaults to be adequate, but<br>
 * you can also get access to the %ModelVisualizer if you need it by calling<br>
 * the Model's getVisualizer() method. <br>
 * <br>
 * The %ModelVisualizer consults the Model's ModelDisplayHints object for <br>
 * instructions on what to display.<br>
 * <br>
 * The Simbody visualizer binary needs to be found at runtime to create a<br>
 * visualizer. The search proceeds in the following order:<br>
 * Directory of the currently running executable/binary.<br>
 * Directories referred to by the environment variable PATH.<br>
 * Possible locations for simbody installations:<br>
 *   -- SIMBODY_HOME/bin if the environment variable SIMBODY_HOME exists.<br>
 *   -- SimTK_INSTALL_DIR/bin if the environment variable SIMBODY_HOME exists.<br>
 *   -- Platform specific default locations of binaries. For Linux/MacOS, this may<br>
 *      be /usr/bin, /usr/local/bin etc. For Windows, this set is empty.<br>
 * <br>
 * @author Michael Sherman<br>
 * <br>
 * @see ModelDisplayHints, Model *
 */
public class ModelVisualizer {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  public ModelVisualizer(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  public static long getCPtr(ModelVisualizer obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  public static long swigRelease(ModelVisualizer obj) {
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
        opensimSimulationJNI.delete_ModelVisualizer(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  /**
   * * Evaluate the geometry needed to visualize the given <i>state</i> and<br>
   *     use it to generate a new image in the Visualizer window. *
   */
  public void show(State state) {
    opensimSimulationJNI.ModelVisualizer_show(swigCPtr, this, State.getCPtr(state), state);
  }

  /**
   * ** If you want to poll for user input, you'll need access to the<br>
   *     SimTK::Visualizer::InputSilo maintained here. Writable access is required<br>
   *     to remove user input from the queues. *
   */
  public SimTKVisualizerInputSilo getInputSilo() {
    return new SimTKVisualizerInputSilo(opensimSimulationJNI.ModelVisualizer_getInputSilo(swigCPtr, this), false);
  }

  /**
   *  Get writable access to the InputSilo so you can remove user input<br>
   *     from the queues. *
   */
  public SimTKVisualizerInputSilo updInputSilo() {
    return new SimTKVisualizerInputSilo(opensimSimulationJNI.ModelVisualizer_updInputSilo(swigCPtr, this), false);
  }

  /**
   *  If you want access to the underlying Simbody SimTK::Visualizer, you<br>
   *     can get a const reference here. *
   */
  public SimTKVisualizer getSimbodyVisualizer() {
    return new SimTKVisualizer(opensimSimulationJNI.ModelVisualizer_getSimbodyVisualizer(swigCPtr, this), false);
  }

  /**
   *  If you want writable access to the underlying Simbody SimTK::Visualizer,<br>
   *     you can get a non-const reference here, provided that you have non-const<br>
   *     access to the %ModelVisualizer. *
   */
  public SimTKVisualizer updSimbodyVisualizer() {
    return new SimTKVisualizer(opensimSimulationJNI.ModelVisualizer_updSimbodyVisualizer(swigCPtr, this), false);
  }

  /**
   * ** Return a pointer to the DefaultGeometry decoration generator used by <br>
   *     this %ModelVisualizer. *
   */
  public SWIGTYPE_p_SimTK__DefaultGeometry getGeometryDecorationGenerator() {
    long cPtr = opensimSimulationJNI.ModelVisualizer_getGeometryDecorationGenerator(swigCPtr, this);
    return (cPtr == 0) ? null : new SWIGTYPE_p_SimTK__DefaultGeometry(cPtr, false);
  }

  /**
   *  Return a const reference to the Model for which this %ModelVisualizer<br>
   *     was constructed. *
   */
  public Model getModel() {
    return new Model(opensimSimulationJNI.ModelVisualizer_getModel(swigCPtr, this), false);
  }

  /**
   *  Return a writable reference to the Model for which this %ModelVisualizer<br>
   *     was constructed. *
   */
  public Model updModel() {
    return new Model(opensimSimulationJNI.ModelVisualizer_updModel(swigCPtr, this), false);
  }

  /**
   *  Given the name of a geometry file, this method will attempt to<br>
   *     find it in a series of locations using the same algorithm as is done<br>
   *     internally by the %ModelVisualizer. <br>
   * <br>
   *     @param model<br>
   *         Used to obtain the name of the file from which the model was loaded.<br>
   *     @param geoFile <br>
   *         Name of file to look for; can be absolute or relative path name or just<br>
   *         a file name and the extension must be supplied.<br>
   *     @param isAbsolute<br>
   *         This output parameter is set to true on return if the supplied <br>
   *         <i>geoFile</i> was an absolute path name; in that case no searching was<br>
   *         done.<br>
   *     @param attempts<br>
   *         On return, this is a list of the absolute path names that were tried.<br>
   *         If <i>geoFile</i> was found, attempts.back() (the last entry) is the<br>
   *         absolute path name of <i>geoFile</i>. The last entry of this array will be<br>
   *         the path that succeeded in finding the geometry file.<br>
   *     @return <code>true</code> if <i>geoFile</i> was located and is readable.<br>
   * <br>
   *     The search rule is as follows:<br>
   *       - If <i>geoFile</i> is an absolute pathname no search is done.<br>
   *       - Otherwise, define modelDir as the directory from which the current<br>
   *         Model file was read in, if any, otherwise the current directory.<br>
   *       - Try modelDir/geoFile, then modelDir/Geometry/geoFile.<br>
   *       - Otherwise, try the search paths added through <br>
   *         addDirToGeometrySearchPaths(). The paths are searched in <br>
   *         reverse-chronological order -- the latest path added is searched first.<br>
   *       - Otherwise a default installation directory. <br>
   * <br>
   *     No attempt is made to validate the contents of the file or whether it<br>
   *     has a supported extension; we're just looking for a file of the given<br>
   *     name that exists and is readable. *
   */
  public static boolean findGeometryFile(Model model, String geoFile, SWIGTYPE_p_bool isAbsolute, SimTKArrayString attempts) {
    return opensimSimulationJNI.ModelVisualizer_findGeometryFile(Model.getCPtr(model), model, geoFile, SWIGTYPE_p_bool.getCPtr(isAbsolute), SimTKArrayString.getCPtr(attempts), attempts);
  }

  /**
   *  Add a directory to the search path to be used by the function<br>
   *     findGeometryFile. The added paths are searched in the <br>
   *     reverse-chronological order -- the latest path added is searched first. 
   */
  public static void addDirToGeometrySearchPaths(String dir) {
    opensimSimulationJNI.ModelVisualizer_addDirToGeometrySearchPaths(dir);
  }

}
