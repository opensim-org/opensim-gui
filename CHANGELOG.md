This document lists the changes to `opensim-gui` that are
introduced with each new version, starting with version 4.1. When possible, we provide the
GitHub issues or pull requests that
are related to the items below. If there is no issue or pull
request related to the change, then we may provide the commit.

v4.4.1
======
- Fix issue [#1378](https://github.com/opensim-org/opensim-gui/issues/1378): In Static Optimization Tool, dialog box has wrong title for "Directory" in output section.
- Fix issue [#1395](https://github.com/opensim-org/opensim-gui/issues/1395): Where visual selection and properties windows are not in sync.
- Fix issue [#1357](https://github.com/opensim-org/opensim-gui/issues/1357): Toolbar displaced and non visible after resizing window

v4.4
====
- Upgrade visualizer codebase to later threejs version 126.
- Upgrade jxBrowser to version 6.24 which was required to make visualizer code work/show.
- Fix issue [#1319](https://github.com/opensim-org/opensim-gui/issues/1319): Undo/redo support for show-COM, show-Axes
- Animate transition of camera to standard views.
- Fix issue [#1298] look & feel on linux 
- Fix visualization of muscle paths in the presense of ConditionalPathPoints, multiple wrap objects or mixes of them. Fix issues [#1330][#1331].
- Fix crash deleting external-force in ExternalLoads editing dialog [#1280]
- Propagate frame changes to all components in the model either owned or connected by sockets to the frame, fix issue [#1340]


v4.3
====
- Upgrade application to be based on the latest NetBeans platorm included with Apache NetBeans 12.3, from disconinued netbeans 8.2. The upgrade supports high DPI monitors.
- IMUs on a model are now shown under the Other Components node in the navigator where they can be selected, shown/hidden. 
- Tools that use Analyses (e.g. Analyze, ...) now have a new option for IMUDataReporter where Output signals are reported for IMUs on the model, or can be reported for user specified Frames.
- Upgrade bindings to use SWIG 4.0.2 and carry doxygen to java and python files as comments.
- Support display of Other components in the naviagtor view, including IMUs.
- Implement setting of "report orientation errors" checkbox from IMUInverseKinematicsTool GUI

v4.2
====
- Merge PR [#1181](https://github.com/opensim-org/opensim-gui/pull/1181): Add build support for Linux (beta)
- Fix issue [#1182](https://github.com/opensim-org/opensim-gui/issues/1182): Scaling model with custom geometry meshes loses custom geomerty
- Support new logging system utilizing spdlog introduced in opensim-core.
- Removed reference to kinematics of external loads from External Loads creation/editing dialog
- Added option to visualize sensor data (quaternions) in the application (File ->Load Sensor Data)
- Associate motion data now handles orientation sensors as well so that orientation triads follow motions
- When recording videos, if the user starts playing back a motion, recording is restarted to first animation frame.
- Allow users to change video format from scripting shell (support gif, jpg, png with setVisualizerOption("video_format", "png")). 
- Add Tools for Calibrating model based on IMU data and to solve Inverse Kinematics problem from IMU data.
- Fix issue where transforming trc marker data in the application always produces a file with "m" as units in header.

v4.1
====
- Fix issue [#1112](https://github.com/opensim-org/opensim-gui/issues/1112): TRC file written by GUI cannot be read by TRCFileAdapter
- Fix issue [#1105](https://github.com/opensim-org/opensim-gui/issues/1105): GUI adding offset to Experimental Data by default
- Fix issue [#1123](https://github.com/opensim-org/opensim-gui/issues/1123): GUI displays disabled muscles in red-blue range during/after StaticOptimization.
- Fix issue [#1127](https://github.com/opensim-org/opensim-gui/issues/1127): Change default time in the toolbar forward simulation tool to 5 seconds. 
- Fix issue [#1107](https://github.com/opensim-org/opensim-gui/issues/1107): Shading of model markers is not consistent with experimental markers
- Fix issue [#1139](https://github.com/opensim-org/opensim-gui/issues/1139): Fix visualization of PathActuators and Ligaments to mimic Muscle Path visualization
- Fix issue [#1141](https://github.com/opensim-org/opensim-gui/issues/1141): Handle moving path points properly when they appear first on GeometryPath for visualization purposes
- Fix issue [#1111](https://github.com/opensim-org/opensim-gui/issues/1111): Handle muscle paths that wrap around multiple wrap objects when wrapping disengages.
- Fix issue [#1153](https://github.com/opensim-org/opensim-gui/issues/1153): Handle "Applies Force" checkbox in External Force edit, so that the setting is persistent.
- Fix issue [#1165](https://github.com/opensim-org/opensim-gui/issues/1165): Handle user cancel from save external-loads dialog gracefully.

