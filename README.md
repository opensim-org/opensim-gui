OpenSim GUI ![opensim-application](https://github.com/opensim-org/opensim-gui/workflows/opensim-application/badge.svg)
===========

OpenSim is software that lets users develop models of musculoskeletal
structures and create dynamic simulations of movement. 

This repository contains the source code for OpenSim's Java GUI, and does *not*
include source code for the
[OpenSim core API](https://github.com/opensim-org/opensim-core).

**NOTE: This repository contains version 4.0 GUI development and
cannot be used to build OpenSim 3.x or earlier.**

**NOTE: Due to the small development team working on the GUI, we do not provide
support for building or altering the OpenSim GUI source code. Advanced users
can refer to the appveyor.yml or .travis.yml scripts in this folder.**

More information can be found at our websites
 - [OpenSim website](http://opensim.stanford.edu)
 - [SimTK project website](https://simtk.org/home/opensim)


Licensing
---------
The OpenSim GUI uses the open source **Apache License 2.0** (see LICENSE.txt in
this directory), making it suitable for commercial, government,
academic, and personal use.

Third-party components have their own licenses; see LICENSE.txt, NOTICE.txt, and our
[Acknowledgements webpage](https://simtk-confluence.stanford.edu/display/OpenSim40/Acknowledgements)
for more information.

The OpenSim GUI's visualizer uses
[JxBrowser](https://www.teamdev.com/jxbrowser), which is a proprietary
software. The use of JxBrowser is governed by [JxBrowser Product Licence
Agreement](http://www.teamdev.com/jxbrowser-licence-agreement). If you would
like to use JxBrowser in your development, please contact TeamDev.


Funding
-------
The OpenSim project has received funding from the following grants and
contracts:

 - United States National Institutes of Health (NIH)
    - Simulation of Biological Structures (Simbios; U54 GM072970)
    - Simulation in Rehabilitation Research (NCSRR; R24 HD065690, P2C HD065690)
    - Mobilize Center (U54 EB020405)
 - United States Defense Advanced Research Projects Agency (DARPA)
    - Warrior Web (W911QX-12-C-0018)


How to acknowledge us
---------------------
Acknowledging the OpenSim project helps us and helps you. It allows us to track
our impact, which is essential for securing funding to improve the software and
provide support to our users (you). If you use OpenSim, we would be extremely
grateful if you acknowledge us by citing the following paper.

Delp SL, Anderson FC, Arnold AS, Loan P, Habib A, John CT, Guendelman E, Thelen
DG. OpenSim: Open-source Software to Create and Analyze Dynamic Simulations of
Movement. IEEE Transactions on Biomedical Engineering. (2007)

If you use plugins, models, or other components contributed by your fellow
researchers, you must acknowledge their work as described in the license that
accompanies each of these files. 

How to get the latest nightly build
-----------------------------------
See [OpenSim GUI Wiki](https://github.com/opensim-org/opensim-gui/wiki)


Building from the source code
-----------------------------

Currently, we only provide instructions for **Windows**. It *is* possible to
build and run the GUI on **OSX** and **Linux**; though it is not thoroughly
tested on these platforms. Linux builds are in beta. We will write instructions
for OSX in the future; for now, you can follow the Windows and Linux instructions
as a rough guide and/or refer to the GitHub Actions CI configs.

See the [OpenSim Confluence Wiki](https://simtk-confluence.stanford.edu/display/OpenSim40/Building+OpenSim+from+Source)
for additional information.

Disclaimer
----------
Instructions are provided below for building but may get out of date occasionally, the defacto instructions are those included/used
by the continuous integration (CI) build scripts available in this repository under 
https://github.com/opensim-org/opensim-gui/blob/master/.github/workflows/continuous-integration.yml

### Building on Windows

#### Get the dependencies

* **operating system**: Windows 7, 8, or 10.
* **OpenSim core API**: [OpenSim-Core master branch](
  https://github.com/opensim-org/opensim-core#on-windows-using-visual-studio)
  * You must build the Java Bindings (CMake variable `BUILD_JAVA_WRAPPING=ON`).
* **Java Development Kit**: [JDK](
  http://www.oracle.com/technetwork/java/javase/downloads/index.html) >= 1.8.
* **Java Integrated Development Environment (IDE)**: [NetBeans](
  https://netbeans.apache.org/download/nb123/nb123.html) 12.3.
  * At this link, you can download a version of NetBeans that comes with a JDK.
* **Command-line build tool for Java**:
  [Ant](http://ant.apache.org/bindownload.cgi) >= 1.9.6.
  * You can use the Ant that comes with NetBeans (e.g.,
    `C:/Program Files/NetBeans-12.0/netbeans/extide/ant.exe`).
* **Command-line build tool for C++**:
    [CMake](https://cmake.org/download/) >= 3.1.3
  * We do not use CMake to build any C++ code, but to copy files and run
    **Ant**.
* **C++ compiler**: [Visual Studio 2019](https://www.visualstudio.com/)
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
    something like `C:/Program Files/NetBeans-12.0/netbeans/extide/bin/ant.exe`.

Use the CMake GUI to *Configure* and *Generate* project files for the *Visual Studio 14 2015* generator.

#### Open the OpenSim GUI project in NetBeans

Open **NetBeans** and open the NetBeans project in the repository located at
`Gui/opensim`. Even if you don't plan to use the NetBeans IDE to build the GUI,
this step is necessary to generate configuration files. The alternative is
to run Ant with the following additional command-line flags:

```
-Dnbplatform.default.netbeans.dest.dir="C:/Program Files/NetBeans-12.0/netbeans" 
-Dnbplatform.default.harness.dir="C:/Program Files/NetBeans-12.0/netbeans/harness"
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

### Building on Linux (beta)

Building on Linux is considered beta, and has only been tested with Ubuntu 18.04 LTS, however, these instructions should work for other distributions with some modifications (eg package manager, package names, etc).

```bash
sudo apt install build-essentials git cmake openjdk-8-jdk liblapack3 libgconf-2-4

wget https://download.netbeans.org/netbeans/8.2/final/bundles/netbeans-8.2-javase-linux.sh
chmod 755 netbeans-8.2-javase-linux.sh
./netbeans-8.2-javase-linux.sh --silent

wget https://prdownloads.sourceforge.net/myosin/opensim-core/opensim-core-latest_linux_Release.zip
unzip -q opensim-core-latest_linux_Release.zip -d ~
git clone https://github.com/opensim-org/opensim-gui.git

mkdir build
cd build

cmake ../opensim-gui -DCMAKE_PREFIX_PATH=~/opensim-core \
    -DAnt_EXECUTABLE="~/netbeans-8.2/extide/ant/bin/ant" \
    -DANT_ARGS="-Dnbplatform.default.netbeans.dest.dir=~/netbeans-8.2;-Dnbplatform.default.harness.dir=~/netbeans-8.2/harness"

make CopyOpenSimCore
make PrepareInstaller

# Tarball found at ~/opensim-gui/Gui/opensim/dist/
# Alternately:
cd ~/opensim-gui/Gui/opensim/dist/installer/OpenSim
./INSTALL
```

#### Manual installation for untested 'nixes

Opensim-core and the GUI depend on the following shared libraries that are not installed by default and/or with Java (openjdk-8-jre) on Ubuntu 18.04:

- `libgconf`
- `liblapack`/`libblas`


[buildstatus_image_travisci]: https://travis-ci.org/opensim-org/opensim-gui.svg?branch=master
[travisci]: https://travis-ci.org/opensim-org/opensim-gui/branches
[buildstatus_image_appveyor]: https://ci.appveyor.com/api/projects/status/7irl68e7da8ryc38/branch/master?svg=true
[appveyorci]: https://ci.appveyor.com/project/opensim-org/opensim-gui/branch/master
