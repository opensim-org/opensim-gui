OpenSim GUI 
============

**NOTE: This repository contains OpenSim 4.0 development User Interface and cannot be used to build 
OpenSim 3.x or earlier. **

This repository contains the source code for OpenSim's Java GUI. This repository does *not*
include source code for the OpenSim core API, for that go to 
[OpenSim-Core](https://github.com/opensim-org/opensim-core).


Building from the source code
-----------------------------
Prerequisites:
- Build of OpenSim core with Java Bindings
- Java 1.7 or later
- Ant

Invoke CMake with top level CMakeLists.txt, and set the CMake variables for
  - OpenSim_DIR: e.g. ${INSTALL_DIR}/opensim-core/cmake
  - Simbody_DIR: e.g. ${INSTALL_DIR}/opensim-core-dependencies/simbody/cmake
  - Ant_EXECUTABLE: e.g. C:/Program Files/NetBeans 8.0.2/extide/ant/bin/ant

  This will generate a solution file, use that to execute the targets:
   - CopyOpenSimCore: copies java files from OpenSim_Dir to GUI folder(s)
   - PrepareInstaller: This runs the ant build script of GUI and generates a platform specific installer under Gui/opensim/dist folder
   - CopyJRE: Copies the runtime JRE to install directory
   - CopyModels: If you have the opensim-models repo and set the CMake variable MODELS_REPO this copies standard models and Geometry folder to install folder.
   

[buildstatus_image_travis]: https://travis-ci.org/opensim-org/opensim-core.svg?branch=master
[travisci]: https://travis-ci.org/opensim-org/opensim-core
[buildstatus_image_appveyor]: https://ci.appveyor.com/api/projects/status/i4wxnmx9jlk69kge/branch/master?svg=true
[appveyorci]: https://ci.appveyor.com/project/opensim-org/opensim-core/branch/master
[running_gif]: doc/images/opensim_running.gif
[simple_example_gif]: doc/images/opensim_double_pendulum_muscle.gif
[java]: http://www.oracle.com/technetwork/java/javase/downloads/index.html
