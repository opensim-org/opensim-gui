/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class opensimSimulation {
  /**
   *  Simulate a model from an initial state and return the final state.<br>
   *     If the model's useVisualizer flag is true, the user is repeatedly prompted<br>
   *     to either begin simulating or quit. The provided state is not updated but<br>
   *     the final state is returned at the end of the simulation, when finalTime is<br>
   *     reached. %Set saveStatesFile=true to save the states to a storage file as:<br>
   *     "<model_name>_states.sto".<br>
   *     
   */
  public static State simulate(Model model, State initialState, double finalTime, boolean saveStatesFile) {
    return new State(opensimSimulationJNI.simulate__SWIG_0(Model.getCPtr(model), model, State.getCPtr(initialState), initialState, finalTime, saveStatesFile), true);
  }

  /**
   *  Simulate a model from an initial state and return the final state.<br>
   *     If the model's useVisualizer flag is true, the user is repeatedly prompted<br>
   *     to either begin simulating or quit. The provided state is not updated but<br>
   *     the final state is returned at the end of the simulation, when finalTime is<br>
   *     reached. %Set saveStatesFile=true to save the states to a storage file as:<br>
   *     "<model_name>_states.sto".<br>
   *     
   */
  public static State simulate(Model model, State initialState, double finalTime) {
    return new State(opensimSimulationJNI.simulate__SWIG_1(Model.getCPtr(model), model, State.getCPtr(initialState), initialState, finalTime), true);
  }

  /**
   *  Update a vector of state labels (in place) to use post-4.0 state paths<br>
   *  instead of pre-4.0 state names. For example, this converts labels as<br>
   *  follows:<br>
   *    - `pelvis_tilt` -&gt; `/jointset/ground_pelvis/pelvis_tilt/value`<br>
   *    - `pelvis_tilt_u` -&gt; `/jointset/ground_pelvis/pelvis_tilt/speed`<br>
   *    - `soleus.activation` -&gt; `/forceset/soleus/activation`<br>
   *    - `soleus.fiber_length` -&gt; `/forceset/soleus/fiber_length`<br>
   *  This can also be used to update the column labels of an Inverse<br>
   *  Kinematics Tool solution MOT file so that the data can be used as<br>
   *  states. If a label does not identify a state in the model, the column<br>
   *  label is not changed.<br>
   *  @throws Exception if labels are not unique.<br>
   *  
   */
  public static void updateStateLabels40(Model model, StdVectorString labels) {
    opensimSimulationJNI.updateStateLabels40(Model.getCPtr(model), model, StdVectorString.getCPtr(labels), labels);
  }

  /**
   *  This function can be used to upgrade MOT files generated with versions<br>
   *     before 4.0 in which some data columns are associated with coordinates<br>
   *     that were incorrectly marked as Rotational (rather than Coupled). Specific<br>
   *     instances of the issue are the patella coordinate in the Rajagopal 2015 and<br>
   *     leg6dof9musc models. In these cases, the patella will visualize incorrectly<br>
   *     in the GUI when replaying the kinematics from the MOT file, and Static<br>
   *     Optimization will yield incorrect results.<br>
   * <br>
   *     The new files are written to the same directories as the original files,<br>
   *     but with the provided suffix (before the file extension). To overwrite your<br>
   *     original files, set the suffix to an emtpty string.<br>
   * <br>
   *     If the file does not need to be updated, no new file is written.<br>
   * <br>
   *     Conversion of the data only occurs for files in degrees ("inDegrees=yes"<br>
   *     in the header).<br>
   * <br>
   *     Do not use this function with MOT files generated by 4.0 or later; doing<br>
   *     so will cause your data to be altered incorrectly. We do not detect whether<br>
   *     or not your MOT file is pre-4.0.<br>
   * <br>
   *     In OpenSim 4.0, MotionTypes for<br>
   *     Coordinates are now determined strictly by the coordinates' owning Joint.<br>
   *     In older models, the MotionType, particularly for CustomJoints, were user-<br>
   *     specified. That entailed in some cases, incorrectly labeling a Coordinate<br>
   *     as being Rotational, for example, when it is in fact Coupled. For the above<br>
   *     models, for example, the patella Coordinate had been user-specified to be<br>
   *     Rotational, but the angle of the patella about the Z-axis of the patella<br>
   *     body, is a spline function (e.g. coupled function) of the patella<br>
   *     Coordinate. Thus, the patella Coordinate is not an angle measurement<br>
   *     and is not classified as Rotational. Use this utility to remove any unit<br>
   *     conversions from Coordinates that were incorrectly labeled<br>
   *     as Rotational in the past. For these Coordinates only, the utility will undo<br>
   *     the incorrect radians to degrees conversion.<br>
   *     
   */
  public static void updatePre40KinematicsFilesFor40MotionType(Model model, StdVectorString filePaths, String suffix) {
    opensimSimulationJNI.updatePre40KinematicsFilesFor40MotionType__SWIG_0(Model.getCPtr(model), model, StdVectorString.getCPtr(filePaths), filePaths, suffix);
  }

  /**
   *  This function can be used to upgrade MOT files generated with versions<br>
   *     before 4.0 in which some data columns are associated with coordinates<br>
   *     that were incorrectly marked as Rotational (rather than Coupled). Specific<br>
   *     instances of the issue are the patella coordinate in the Rajagopal 2015 and<br>
   *     leg6dof9musc models. In these cases, the patella will visualize incorrectly<br>
   *     in the GUI when replaying the kinematics from the MOT file, and Static<br>
   *     Optimization will yield incorrect results.<br>
   * <br>
   *     The new files are written to the same directories as the original files,<br>
   *     but with the provided suffix (before the file extension). To overwrite your<br>
   *     original files, set the suffix to an emtpty string.<br>
   * <br>
   *     If the file does not need to be updated, no new file is written.<br>
   * <br>
   *     Conversion of the data only occurs for files in degrees ("inDegrees=yes"<br>
   *     in the header).<br>
   * <br>
   *     Do not use this function with MOT files generated by 4.0 or later; doing<br>
   *     so will cause your data to be altered incorrectly. We do not detect whether<br>
   *     or not your MOT file is pre-4.0.<br>
   * <br>
   *     In OpenSim 4.0, MotionTypes for<br>
   *     Coordinates are now determined strictly by the coordinates' owning Joint.<br>
   *     In older models, the MotionType, particularly for CustomJoints, were user-<br>
   *     specified. That entailed in some cases, incorrectly labeling a Coordinate<br>
   *     as being Rotational, for example, when it is in fact Coupled. For the above<br>
   *     models, for example, the patella Coordinate had been user-specified to be<br>
   *     Rotational, but the angle of the patella about the Z-axis of the patella<br>
   *     body, is a spline function (e.g. coupled function) of the patella<br>
   *     Coordinate. Thus, the patella Coordinate is not an angle measurement<br>
   *     and is not classified as Rotational. Use this utility to remove any unit<br>
   *     conversions from Coordinates that were incorrectly labeled<br>
   *     as Rotational in the past. For these Coordinates only, the utility will undo<br>
   *     the incorrect radians to degrees conversion.<br>
   *     
   */
  public static void updatePre40KinematicsFilesFor40MotionType(Model model, StdVectorString filePaths) {
    opensimSimulationJNI.updatePre40KinematicsFilesFor40MotionType__SWIG_1(Model.getCPtr(model), model, StdVectorString.getCPtr(filePaths), filePaths);
  }

  /**
   *  This function attempts to update the connectee path for any Socket anywhere<br>
   * in the model whose connectee path does not point to an existing component.<br>
   * The paths are updated by searching the model for a component with the<br>
   * correct name. For example, a connectee path like<br>
   * `../../some/invalid/path/to/foo` will be updated to `/bodyset/foo` if a Body<br>
   * named `foo` exists in the Model's BodySet. If a socket specifies a Body `foo` and<br>
   * more than one Body `foo` exists in the model, we emit a warning and the<br>
   * socket that specified `foo` is not altered.<br>
   * <br>
   * This method is intended for use with models loaded from version-30516 XML<br>
   * files to bring them up to date with the 4.0 interface.<br>
   * 
   */
  public static void updateSocketConnecteesBySearch(Model model) {
    opensimSimulationJNI.updateSocketConnecteesBySearch(Model.getCPtr(model), model);
  }

  /**
   *  The map provides the index of each state variable in<br>
   *  SimTK::State::getY() from its each state variable path string.<br>
   *  Empty slots in Y (e.g., for quaternions) are ignored.<br>
   *  
   */
  public static StdVectorString createStateVariableNamesInSystemOrder(Model model) {
    return new StdVectorString(opensimSimulationJNI.createStateVariableNamesInSystemOrder(Model.getCPtr(model), model), true);
  }

  /**
   *  Create a vector of control names based on the actuators in the model for<br>
   *  which appliesForce == True. For actuators with one control (e.g.<br>
   *  ScalarActuator) the control name is simply the actuator name. For actuators<br>
   *  with multiple controls, each control name is the actuator name appended by<br>
   *  the control index (e.g. "/actuator_0"); modelControlIndices has length equal<br>
   *  to the number of controls associated with actuators that apply a force<br>
   *  (appliesForce == True). Its elements are the indices of the controls in the<br>
   *  Model::updControls() that are associated with actuators that apply a force.<br>
   *  
   */
  public static StdVectorString createControlNamesFromModel(Model model, StdVectorInt modelControlIndices) {
    return new StdVectorString(opensimSimulationJNI.createControlNamesFromModel__SWIG_0(Model.getCPtr(model), model, StdVectorInt.getCPtr(modelControlIndices), modelControlIndices), true);
  }

  /**
   *  Same as above, but when there is no mapping to the modelControlIndices.<br>
   *  
   */
  public static StdVectorString createControlNamesFromModel(Model model) {
    return new StdVectorString(opensimSimulationJNI.createControlNamesFromModel__SWIG_1(Model.getCPtr(model), model), true);
  }

  /**
   *  The map provides the index of each control variable in the SimTK::Vector<br>
   *  returned by Model::getControls(), using the control name as the<br>
   *  key.<br>
   *  @throws Exception if the order of actuators in the model does not match<br>
   *      the order of controls in Model::getControls(). This is an internal<br>
   *      error, but you may be able to avoid the error by ensuring all Actuator%s<br>
   *      are in the Model's ForceSet.<br>
   *  
   */
  public static SWIGTYPE_p_std__unordered_mapT_std__string_int_t createSystemControlIndexMap(Model model) {
    return new SWIGTYPE_p_std__unordered_mapT_std__string_int_t(opensimSimulationJNI.createSystemControlIndexMap(Model.getCPtr(model), model), true);
  }

  /**
   *  Throws an exception if the order of the controls in the model is not the<br>
   *  same as the order of the actuators in the model.<br>
   *  
   */
  public static void checkOrderSystemControls(Model model) {
    opensimSimulationJNI.checkOrderSystemControls(Model.getCPtr(model), model);
  }

  /**
   *  Throws an exception if any label in the provided list does not match any<br>
   *  state variable names in the model.<br>
   *  
   */
  public static void checkLabelsMatchModelStates(Model model, StdVectorString labels) {
    opensimSimulationJNI.checkLabelsMatchModelStates(Model.getCPtr(model), model, StdVectorString.getCPtr(labels), labels);
  }

  /**
   *  Calculate "synthetic" acceleration signals equivalent to signals recorded<br>
   *  from inertial measurement units (IMUs). First, this utility computes the<br>
   *  linear acceleration for each frame included in 'framePaths' using Frame's<br>
   *  'linear_acceleration' Output. Then, to mimic acceleration signals measured<br>
   *  from IMUs, the model's gravitational acceleration vector is subtracted from<br>
   *  the linear accelerations and the resulting accelerations are re-expressed in<br>
   *  the bases of the associated Frame%s.<br>
   * <br>
   *  Note: The linear acceleration Output%s are computed using the analyze()<br>
   *  simulation utility, and therefore the 'statesTable' and 'controlsTable'<br>
   *  arguments must contain the same time points and we assume that the states<br>
   *  obey any kinematic constraints in the Model.<br>
   * <br>
   *  Note: The passed in model must have the correct mass and inertia properties<br>
   *  included, since computing accelerations requires realizing to<br>
   *  SimTK::Stage::Acceleration which depends on SimTK::Stage::Dynamics.<br>
   * <br>
   *  
   */
  public static TimeSeriesTableVec3 createSyntheticIMUAccelerationSignals(Model model, TimeSeriesTable statesTable, TimeSeriesTable controlsTable, StdVectorString framePaths) {
    return new TimeSeriesTableVec3(opensimSimulationJNI.createSyntheticIMUAccelerationSignals(Model.getCPtr(model), model, TimeSeriesTable.getCPtr(statesTable), statesTable, TimeSeriesTable.getCPtr(controlsTable), controlsTable, StdVectorString.getCPtr(framePaths), framePaths), true);
  }

  /**
   *  Calculate the requested outputs using the model in the problem and the<br>
   *  provided states and controls tables.<br>
   *  The controls table is used to set the model's controls vector.<br>
   *  We assume the states and controls tables contain the same time points.<br>
   *  The output paths can be regular expressions. For example,<br>
   *  ".*activation" gives the activation of all muscles.<br>
   * <br>
   *  The output paths must correspond to outputs that match the type provided in<br>
   *  the template argument, otherwise they are not included in the report.<br>
   * <br>
   *  Controls missing from the controls table are given a value of 0.<br>
   * <br>
   *  If you analysis depends on the values of discrete variables in the state,<br>
   *  you may provide those values via the optional argument<br>
   *  "discreteVariablesTable". This table should contain column labels with the<br>
   *  following format: &lt;path_to_component&gt;/&lt;discrete_var_name&gt;. For example,<br>
   *  "/forceset/muscle/implicitderiv_normalized_tendon_force".<br>
   * <br>
   *  Note: The provided trajectory is not modified to satisfy kinematic<br>
   *  constraints, but SimTK::Motions in the Model (e.g., PositionMotion) are<br>
   *  applied. Therefore, this function expects that you've provided a trajectory<br>
   *  that already satisfies kinematic constraints. If your provided trajectory<br>
   *  does not satisfy kinematic constraints, many outputs will be incorrect.<br>
   *  For example, in a model with a patella whose location is determined by a<br>
   *  CoordinateCouplerConstraint, the length of a muscle that crosses the patella<br>
   *  will be incorrect.<br>
   *  
   */
  public static TimeSeriesTable analyze(Model model, TimeSeriesTable statesTable, TimeSeriesTable controlsTable, StdVectorString outputPaths, TimeSeriesTable discreteVariablesTable) {
    return new TimeSeriesTable(opensimSimulationJNI.analyze__SWIG_2(Model.getCPtr(model), model, TimeSeriesTable.getCPtr(statesTable), statesTable, TimeSeriesTable.getCPtr(controlsTable), controlsTable, StdVectorString.getCPtr(outputPaths), outputPaths, TimeSeriesTable.getCPtr(discreteVariablesTable), discreteVariablesTable), true);
  }

  /**
   *  Calculate the requested outputs using the model in the problem and the<br>
   *  provided states and controls tables.<br>
   *  The controls table is used to set the model's controls vector.<br>
   *  We assume the states and controls tables contain the same time points.<br>
   *  The output paths can be regular expressions. For example,<br>
   *  ".*activation" gives the activation of all muscles.<br>
   * <br>
   *  The output paths must correspond to outputs that match the type provided in<br>
   *  the template argument, otherwise they are not included in the report.<br>
   * <br>
   *  Controls missing from the controls table are given a value of 0.<br>
   * <br>
   *  If you analysis depends on the values of discrete variables in the state,<br>
   *  you may provide those values via the optional argument<br>
   *  "discreteVariablesTable". This table should contain column labels with the<br>
   *  following format: &lt;path_to_component&gt;/&lt;discrete_var_name&gt;. For example,<br>
   *  "/forceset/muscle/implicitderiv_normalized_tendon_force".<br>
   * <br>
   *  Note: The provided trajectory is not modified to satisfy kinematic<br>
   *  constraints, but SimTK::Motions in the Model (e.g., PositionMotion) are<br>
   *  applied. Therefore, this function expects that you've provided a trajectory<br>
   *  that already satisfies kinematic constraints. If your provided trajectory<br>
   *  does not satisfy kinematic constraints, many outputs will be incorrect.<br>
   *  For example, in a model with a patella whose location is determined by a<br>
   *  CoordinateCouplerConstraint, the length of a muscle that crosses the patella<br>
   *  will be incorrect.<br>
   *  
   */
  public static TimeSeriesTable analyze(Model model, TimeSeriesTable statesTable, TimeSeriesTable controlsTable, StdVectorString outputPaths) {
    return new TimeSeriesTable(opensimSimulationJNI.analyze__SWIG_3(Model.getCPtr(model), model, TimeSeriesTable.getCPtr(statesTable), statesTable, TimeSeriesTable.getCPtr(controlsTable), controlsTable, StdVectorString.getCPtr(outputPaths), outputPaths), true);
  }

  /**
   *  Calculate the requested outputs using the model in the problem and the<br>
   *  provided states and controls tables.<br>
   *  The controls table is used to set the model's controls vector.<br>
   *  We assume the states and controls tables contain the same time points.<br>
   *  The output paths can be regular expressions. For example,<br>
   *  ".*activation" gives the activation of all muscles.<br>
   * <br>
   *  The output paths must correspond to outputs that match the type provided in<br>
   *  the template argument, otherwise they are not included in the report.<br>
   * <br>
   *  Controls missing from the controls table are given a value of 0.<br>
   * <br>
   *  If you analysis depends on the values of discrete variables in the state,<br>
   *  you may provide those values via the optional argument<br>
   *  "discreteVariablesTable". This table should contain column labels with the<br>
   *  following format: &lt;path_to_component&gt;/&lt;discrete_var_name&gt;. For example,<br>
   *  "/forceset/muscle/implicitderiv_normalized_tendon_force".<br>
   * <br>
   *  Note: The provided trajectory is not modified to satisfy kinematic<br>
   *  constraints, but SimTK::Motions in the Model (e.g., PositionMotion) are<br>
   *  applied. Therefore, this function expects that you've provided a trajectory<br>
   *  that already satisfies kinematic constraints. If your provided trajectory<br>
   *  does not satisfy kinematic constraints, many outputs will be incorrect.<br>
   *  For example, in a model with a patella whose location is determined by a<br>
   *  CoordinateCouplerConstraint, the length of a muscle that crosses the patella<br>
   *  will be incorrect.<br>
   *  
   */
  public static TimeSeriesTableVec3 analyzeVec3(Model model, TimeSeriesTable statesTable, TimeSeriesTable controlsTable, StdVectorString outputPaths, TimeSeriesTable discreteVariablesTable) {
    return new TimeSeriesTableVec3(opensimSimulationJNI.analyzeVec3__SWIG_0(Model.getCPtr(model), model, TimeSeriesTable.getCPtr(statesTable), statesTable, TimeSeriesTable.getCPtr(controlsTable), controlsTable, StdVectorString.getCPtr(outputPaths), outputPaths, TimeSeriesTable.getCPtr(discreteVariablesTable), discreteVariablesTable), true);
  }

  /**
   *  Calculate the requested outputs using the model in the problem and the<br>
   *  provided states and controls tables.<br>
   *  The controls table is used to set the model's controls vector.<br>
   *  We assume the states and controls tables contain the same time points.<br>
   *  The output paths can be regular expressions. For example,<br>
   *  ".*activation" gives the activation of all muscles.<br>
   * <br>
   *  The output paths must correspond to outputs that match the type provided in<br>
   *  the template argument, otherwise they are not included in the report.<br>
   * <br>
   *  Controls missing from the controls table are given a value of 0.<br>
   * <br>
   *  If you analysis depends on the values of discrete variables in the state,<br>
   *  you may provide those values via the optional argument<br>
   *  "discreteVariablesTable". This table should contain column labels with the<br>
   *  following format: &lt;path_to_component&gt;/&lt;discrete_var_name&gt;. For example,<br>
   *  "/forceset/muscle/implicitderiv_normalized_tendon_force".<br>
   * <br>
   *  Note: The provided trajectory is not modified to satisfy kinematic<br>
   *  constraints, but SimTK::Motions in the Model (e.g., PositionMotion) are<br>
   *  applied. Therefore, this function expects that you've provided a trajectory<br>
   *  that already satisfies kinematic constraints. If your provided trajectory<br>
   *  does not satisfy kinematic constraints, many outputs will be incorrect.<br>
   *  For example, in a model with a patella whose location is determined by a<br>
   *  CoordinateCouplerConstraint, the length of a muscle that crosses the patella<br>
   *  will be incorrect.<br>
   *  
   */
  public static TimeSeriesTableVec3 analyzeVec3(Model model, TimeSeriesTable statesTable, TimeSeriesTable controlsTable, StdVectorString outputPaths) {
    return new TimeSeriesTableVec3(opensimSimulationJNI.analyzeVec3__SWIG_1(Model.getCPtr(model), model, TimeSeriesTable.getCPtr(statesTable), statesTable, TimeSeriesTable.getCPtr(controlsTable), controlsTable, StdVectorString.getCPtr(outputPaths), outputPaths), true);
  }

  /**
   *  Calculate the requested outputs using the model in the problem and the<br>
   *  provided states and controls tables.<br>
   *  The controls table is used to set the model's controls vector.<br>
   *  We assume the states and controls tables contain the same time points.<br>
   *  The output paths can be regular expressions. For example,<br>
   *  ".*activation" gives the activation of all muscles.<br>
   * <br>
   *  The output paths must correspond to outputs that match the type provided in<br>
   *  the template argument, otherwise they are not included in the report.<br>
   * <br>
   *  Controls missing from the controls table are given a value of 0.<br>
   * <br>
   *  If you analysis depends on the values of discrete variables in the state,<br>
   *  you may provide those values via the optional argument<br>
   *  "discreteVariablesTable". This table should contain column labels with the<br>
   *  following format: &lt;path_to_component&gt;/&lt;discrete_var_name&gt;. For example,<br>
   *  "/forceset/muscle/implicitderiv_normalized_tendon_force".<br>
   * <br>
   *  Note: The provided trajectory is not modified to satisfy kinematic<br>
   *  constraints, but SimTK::Motions in the Model (e.g., PositionMotion) are<br>
   *  applied. Therefore, this function expects that you've provided a trajectory<br>
   *  that already satisfies kinematic constraints. If your provided trajectory<br>
   *  does not satisfy kinematic constraints, many outputs will be incorrect.<br>
   *  For example, in a model with a patella whose location is determined by a<br>
   *  CoordinateCouplerConstraint, the length of a muscle that crosses the patella<br>
   *  will be incorrect.<br>
   *  
   */
  public static TimeSeriesTableSpatialVec analyzeSpatialVec(Model model, TimeSeriesTable statesTable, TimeSeriesTable controlsTable, StdVectorString outputPaths, TimeSeriesTable discreteVariablesTable) {
    return new TimeSeriesTableSpatialVec(opensimSimulationJNI.analyzeSpatialVec__SWIG_0(Model.getCPtr(model), model, TimeSeriesTable.getCPtr(statesTable), statesTable, TimeSeriesTable.getCPtr(controlsTable), controlsTable, StdVectorString.getCPtr(outputPaths), outputPaths, TimeSeriesTable.getCPtr(discreteVariablesTable), discreteVariablesTable), true);
  }

  /**
   *  Calculate the requested outputs using the model in the problem and the<br>
   *  provided states and controls tables.<br>
   *  The controls table is used to set the model's controls vector.<br>
   *  We assume the states and controls tables contain the same time points.<br>
   *  The output paths can be regular expressions. For example,<br>
   *  ".*activation" gives the activation of all muscles.<br>
   * <br>
   *  The output paths must correspond to outputs that match the type provided in<br>
   *  the template argument, otherwise they are not included in the report.<br>
   * <br>
   *  Controls missing from the controls table are given a value of 0.<br>
   * <br>
   *  If you analysis depends on the values of discrete variables in the state,<br>
   *  you may provide those values via the optional argument<br>
   *  "discreteVariablesTable". This table should contain column labels with the<br>
   *  following format: &lt;path_to_component&gt;/&lt;discrete_var_name&gt;. For example,<br>
   *  "/forceset/muscle/implicitderiv_normalized_tendon_force".<br>
   * <br>
   *  Note: The provided trajectory is not modified to satisfy kinematic<br>
   *  constraints, but SimTK::Motions in the Model (e.g., PositionMotion) are<br>
   *  applied. Therefore, this function expects that you've provided a trajectory<br>
   *  that already satisfies kinematic constraints. If your provided trajectory<br>
   *  does not satisfy kinematic constraints, many outputs will be incorrect.<br>
   *  For example, in a model with a patella whose location is determined by a<br>
   *  CoordinateCouplerConstraint, the length of a muscle that crosses the patella<br>
   *  will be incorrect.<br>
   *  
   */
  public static TimeSeriesTableSpatialVec analyzeSpatialVec(Model model, TimeSeriesTable statesTable, TimeSeriesTable controlsTable, StdVectorString outputPaths) {
    return new TimeSeriesTableSpatialVec(opensimSimulationJNI.analyzeSpatialVec__SWIG_1(Model.getCPtr(model), model, TimeSeriesTable.getCPtr(statesTable), statesTable, TimeSeriesTable.getCPtr(controlsTable), controlsTable, StdVectorString.getCPtr(outputPaths), outputPaths), true);
  }

}
