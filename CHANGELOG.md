This document lists the changes to `opensim-gui` that are
introduced with each new version, starting with version 4.1. When possible, we provide the
GitHub issues or pull requests that
are related to the items below. If there is no issue or pull
request related to the change, then we may provide the commit.

v4.2
====
- Merge PR [#1181](https://github.com/opensim-org/opensim-gui/pull/1181): Add build support for Linux (beta)
- Fix issue [#1182](https://github.com/opensim-org/opensim-gui/issues/1182): Scaling model with custom geometry meshes loses custom geomerty
- Support new logging system utilizing spdlog introduced in opensim-core.
- Removed reference to kinematics of external loads from External Loads creation/editing dialog
- Added option to visualize sensor data (quaternions) in the application (File ->Load Sensor Data)
- Associate motion data now handles orientation sensors as well so that orientation triads follow motions
- When recording videos, if the user starts playing back a motion, recording is restarted to first animation frame.

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

