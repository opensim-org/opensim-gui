OpenSim GUI [![Appveyor build status][buildstatus_image_appveyor]][appveyorci]
===========

**NOTE: This repository contains OpenSim 4.0 development User Interface and
cannot be used to build OpenSim 3.x or earlier.**

This repository contains the source code for OpenSim's Java GUI.
This repository does *not* include source code for the OpenSim core API,
for that go to [OpenSim-Core](https://github.com/opensim-org/opensim-core).

Due to the small development team working on the GUI, we provide very limited
support for building or altering the OpenSim GUI source code.


Building from the source code
-----------------------------

Currently, we only provide instructions for **Windows**. It *is* possible to
build and run the GUI on **OSX** and **Linux**; though it is not thoroughly
tested on these platforms. We will write instructions for OSX and Linux in the
future; for now, you can follow the Windows instructions as a rough guide.

#### Get the dependencies

* **operating system**: Windows 7, 8, or 10.
* **OpenSim core API**: [OpenSim-Core master branch](
  https://github.com/opensim-org/opensim-core#on-windows-using-visual-studio)
  * You must build the Java Bindings (CMake variable `BUILD_JAVA_WRAPPING=ON`).
* **Java Development Kit**: [JDK](
  http://www.oracle.com/technetwork/java/javase/downloads/index.html) >= 1.7.
* **Java Integrated Development Environment (IDE)**: [NetBeans](
  http://www.oracle.com/technetwork/java/javase/downloads/index.html) 8.0.2.
  * At this link, you can download a version of NetBeans that comes with a JDK.
* **Command-line build tool for Java**:
  [Ant](http://ant.apache.org/bindownload.cgi) >= 1.9.6.
  * You can use the Ant that comes with NetBeans (e.g.,
    `C:/Program Files/NetBeans 8.0.2/extide/ant/bin/ant.exe`).
* **Command-line build tool for C++**:
    [CMake](https://cmake.org/download/) >= 3.1.3
  * We do not use CMake to build any C++ code, but to copy files and run
    **Ant**.
* **C++ compiler**: [Visual Studio 2015](https://www.visualstudio.com/)
  * Again, we won't build any C++ code; CMake needs this to do its job.

You can obtain some of these dependencies using the Chocolatey package manager.
Get [Chocolatey](https://chocolatey.org/), open a new **PowerShell** window
(run as Administrator), and run the following command:

    choco install jdk8 netbeans-jee ant

#### Configure and generate Visual Studio project files

Invoke **CMake** with the root of this repository as the source directory,
and set the following CMake variables:
  * `OpenSim_DIR`: The directory containing `OpenSimConfig.cmake`.
    * Might look something like `.../opensim-core/cmake`.
  * `Simbody_DIR`: The directory containing `SimbodyConfig.cmake`.
    * Might look something like `.../opensim_dependencies_install/simbody/cmake`.
  * `Ant_EXECUTABLE`: If you want to use the Ant that comes with NetBeans, specify
    something like `C:/Program Files/NetBeans 8.0.2/extide/ant/bin/ant.exe`.

Use the CMake GUI to *Configure* and *Generate* project files for the *Visual Studio 14 2015* generator.

#### Open the OpenSim GUI project in NetBeans

Open **NetBeans** and open the NetBeans project in the repository located at
`Gui/opensim`. Even if you don't plan to use the NetBeans IDE to build the GUI,
this step is necessary to generate configuration files. The alternative is
to run Ant with the following additional command-line flags:

```
-Dnbplatform.default.netbeans.dest.dir="C:/Program Files/NetBeans 8.0.2" 
-Dnbplatform.default.harness.dir="C:/Program Files/NetBeans 8.0.2/harness"
```

We use these additional flags in our automated builds in [AppVeyor][appveyorci].

#### CMake targets and packaging the distribution

The Visual Studio "solution" you generated earlier provides the
following targets (none of which actually compile C++ code):
 * **CopyOpenSimCore**: copies `.java` files from OpenSim-Core to GUI folder(s).
   You should run this target even if you do not plan on packaging the
   distribution. It is necessary for making sure you are using the correct
   OpenSim-Core code.
 * Targets for packaging the OpenSim distribution:
   * **PrepareInstaller**: This runs the ant build script of GUI and generates
     a platform-specific installer under `Gui/opensim/dist` folder.
   * **CopyJRE**: Copies the runtime JRE to the install directory.
   * **CopyModels**: If you have the `opensim-models` repository and set the
     CMake variable `MODELS_REPO`, this copies standard models and the
     Geometry folder to the install folder.

If you plan to make changes to the GUI, you can now continue to use NetBeans to
edit the Java source code and build and run the GUI.


[buildstatus_image_appveyor]: https://ci.appveyor.com/api/projects/status/7irl68e7da8ryc38/branch/master?svg=true
[appveyorci]: https://ci.appveyor.com/project/opensim-org/opensim-gui/branch/master
