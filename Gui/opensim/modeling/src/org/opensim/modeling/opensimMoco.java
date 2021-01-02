/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.9
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class opensimMoco {
  public static String GetMocoVersionAndDate() {
    return opensimMocoJNI.GetMocoVersionAndDate();
  }

  public static String GetMocoVersion() {
    return opensimMocoJNI.GetMocoVersion();
  }

  public static void prescribeControlsToModel(MocoTrajectory trajectory, Model model, String functionType) {
    opensimMocoJNI.prescribeControlsToModel__SWIG_0(MocoTrajectory.getCPtr(trajectory), trajectory, Model.getCPtr(model), model, functionType);
  }

  public static void prescribeControlsToModel(MocoTrajectory trajectory, Model model) {
    opensimMocoJNI.prescribeControlsToModel__SWIG_1(MocoTrajectory.getCPtr(trajectory), trajectory, Model.getCPtr(model), model);
  }

  public static MocoTrajectory simulateTrajectoryWithTimeStepping(MocoTrajectory trajectory, Model model, double integratorAccuracy) {
    return new MocoTrajectory(opensimMocoJNI.simulateTrajectoryWithTimeStepping__SWIG_0(MocoTrajectory.getCPtr(trajectory), trajectory, Model.getCPtr(model), model, integratorAccuracy), true);
  }

  public static MocoTrajectory simulateTrajectoryWithTimeStepping(MocoTrajectory trajectory, Model model) {
    return new MocoTrajectory(opensimMocoJNI.simulateTrajectoryWithTimeStepping__SWIG_1(MocoTrajectory.getCPtr(trajectory), trajectory, Model.getCPtr(model), model), true);
  }

  public static MocoTrajectory createPeriodicTrajectory(MocoTrajectory halfPeriodTrajectory, StdVectorString addPatterns, StdVectorString negatePatterns, StdVectorString negateAndShiftPatterns, SWIGTYPE_p_std__vectorT_std__pairT_std__string_std__string_t_t symmetryPatterns) {
    return new MocoTrajectory(opensimMocoJNI.createPeriodicTrajectory__SWIG_0(MocoTrajectory.getCPtr(halfPeriodTrajectory), halfPeriodTrajectory, StdVectorString.getCPtr(addPatterns), addPatterns, StdVectorString.getCPtr(negatePatterns), negatePatterns, StdVectorString.getCPtr(negateAndShiftPatterns), negateAndShiftPatterns, SWIGTYPE_p_std__vectorT_std__pairT_std__string_std__string_t_t.getCPtr(symmetryPatterns)), true);
  }

  public static MocoTrajectory createPeriodicTrajectory(MocoTrajectory halfPeriodTrajectory, StdVectorString addPatterns, StdVectorString negatePatterns, StdVectorString negateAndShiftPatterns) {
    return new MocoTrajectory(opensimMocoJNI.createPeriodicTrajectory__SWIG_1(MocoTrajectory.getCPtr(halfPeriodTrajectory), halfPeriodTrajectory, StdVectorString.getCPtr(addPatterns), addPatterns, StdVectorString.getCPtr(negatePatterns), negatePatterns, StdVectorString.getCPtr(negateAndShiftPatterns), negateAndShiftPatterns), true);
  }

  public static MocoTrajectory createPeriodicTrajectory(MocoTrajectory halfPeriodTrajectory, StdVectorString addPatterns, StdVectorString negatePatterns) {
    return new MocoTrajectory(opensimMocoJNI.createPeriodicTrajectory__SWIG_2(MocoTrajectory.getCPtr(halfPeriodTrajectory), halfPeriodTrajectory, StdVectorString.getCPtr(addPatterns), addPatterns, StdVectorString.getCPtr(negatePatterns), negatePatterns), true);
  }

  public static MocoTrajectory createPeriodicTrajectory(MocoTrajectory halfPeriodTrajectory, StdVectorString addPatterns) {
    return new MocoTrajectory(opensimMocoJNI.createPeriodicTrajectory__SWIG_3(MocoTrajectory.getCPtr(halfPeriodTrajectory), halfPeriodTrajectory, StdVectorString.getCPtr(addPatterns), addPatterns), true);
  }

  public static MocoTrajectory createPeriodicTrajectory(MocoTrajectory halfPeriodTrajectory) {
    return new MocoTrajectory(opensimMocoJNI.createPeriodicTrajectory__SWIG_4(MocoTrajectory.getCPtr(halfPeriodTrajectory), halfPeriodTrajectory), true);
  }

  public static int getMocoParallelEnvironmentVariable() {
    return opensimMocoJNI.getMocoParallelEnvironmentVariable();
  }

  public static TimeSeriesTable createExternalLoadsTableForGait(Model model, StatesTrajectory trajectory, StdVectorString forcePathsRightFoot, StdVectorString forcePathsLeftFoot) {
    return new TimeSeriesTable(opensimMocoJNI.createExternalLoadsTableForGait__SWIG_0(Model.getCPtr(model), model, StatesTrajectory.getCPtr(trajectory), trajectory, StdVectorString.getCPtr(forcePathsRightFoot), forcePathsRightFoot, StdVectorString.getCPtr(forcePathsLeftFoot), forcePathsLeftFoot), true);
  }

  public static TimeSeriesTable createExternalLoadsTableForGait(Model model, MocoTrajectory trajectory, StdVectorString forcePathsRightFoot, StdVectorString forcePathsLeftFoot) {
    return new TimeSeriesTable(opensimMocoJNI.createExternalLoadsTableForGait__SWIG_1(Model.getCPtr(model), model, MocoTrajectory.getCPtr(trajectory), trajectory, StdVectorString.getCPtr(forcePathsRightFoot), forcePathsRightFoot, StdVectorString.getCPtr(forcePathsLeftFoot), forcePathsLeftFoot), true);
  }

  public static TimeSeriesTable analyzeMocoTrajectory(Model model, TimeSeriesTable statesTable, TimeSeriesTable controlsTable, StdVectorString outputPaths) {
    return new TimeSeriesTable(opensimMocoJNI.analyzeMocoTrajectory(Model.getCPtr(model), model, TimeSeriesTable.getCPtr(statesTable), statesTable, TimeSeriesTable.getCPtr(controlsTable), controlsTable, StdVectorString.getCPtr(outputPaths), outputPaths), true);
  }

  public static TimeSeriesTableVec3 analyzeMocoTrajectoryVec3(Model model, TimeSeriesTable statesTable, TimeSeriesTable controlsTable, StdVectorString outputPaths) {
    return new TimeSeriesTableVec3(opensimMocoJNI.analyzeMocoTrajectoryVec3(Model.getCPtr(model), model, TimeSeriesTable.getCPtr(statesTable), statesTable, TimeSeriesTable.getCPtr(controlsTable), controlsTable, StdVectorString.getCPtr(outputPaths), outputPaths), true);
  }

  public static TimeSeriesTableSpatialVec analyzeMocoTrajectorySpatialVec(Model model, TimeSeriesTable statesTable, TimeSeriesTable controlsTable, StdVectorString outputPaths) {
    return new TimeSeriesTableSpatialVec(opensimMocoJNI.analyzeMocoTrajectorySpatialVec(Model.getCPtr(model), model, TimeSeriesTable.getCPtr(statesTable), statesTable, TimeSeriesTable.getCPtr(controlsTable), controlsTable, StdVectorString.getCPtr(outputPaths), outputPaths), true);
  }

}