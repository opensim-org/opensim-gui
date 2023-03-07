#!/bin/bash

# Exit when an error happens instead of continue.
set -e

# Default values for flags.
DEBUG_TYPE="Release"
NUM_JOBS=4
MOCO="on"
CORE_BRANCH="main"
GUI_BRANCH="main"
GENERATOR="Unix Makefiles"

Help() {
    echo
    echo "This script builds and installs the last available version of OpenSim-Gui in your computer."
    echo "Usage: ./scriptName [OPTION]..."
    echo "Example: ./opensim-gui-build.sh -j 4 -d \"Release\""
    echo "    -d         Debug Type. Available Options:"
    echo "                   Release (Default): No debugger symbols. Optimized."
    echo "                   Debug: Debugger symbols. No optimizations (>10x slower). Library names ending with _d."
    echo "                   RelWithDefInfo: Debugger symbols. Optimized. Bigger than Release, but not slower."
    echo "                   MinSizeRel: No debugger symbols. Minimum size. Optimized."
    echo "    -j         Number of jobs to use when building libraries (>=1)."
    echo "    -s         Simple build without moco (Tropter and Casadi disabled)."
    echo "    -c         Branch for opensim-core repository."
    echo "    -g         Branch for opensim-gui repository."
    echo "    -n         Use the Ninja generator to build opensim-core. If not set, Unix Makefiles is used."
    echo
    exit
}

# Get flag values if exist.
while getopts 'j:d:sc:g:n' flag
do
    case "${flag}" in
        j) NUM_JOBS=${OPTARG};;
        d) DEBUG_TYPE=${OPTARG};;
        s) MOCO="off";;
        c) CORE_BRANCH=${OPTARG};;
        g) GUI_BRANCH=${OPTARG};;
        n) GENERATOR="Ninja";;
        *) Help;
    esac
done

# Check parameters are valid.
if [[ $NUM_JOBS -lt 1 ]]
then
    Help;
fi
if [[ $DEBUG_TYPE != "Release" ]] && [[ $DEBUG_TYPE != "Debug" ]] && [[ $DEBUG_TYPE != "RelWithDebInfo" ]] && [[ $DEBUG_TYPE != "MinSizeRel" ]]
then
    Help;
fi

# Show values of flags:
echo
echo "Build script parameters:"
echo "DEBUG_TYPE="$DEBUG_TYPE
echo "NUM_JOBS="$NUM_JOBS
echo "MOCO="$MOCO
echo "CORE_BRANCH="$CORE_BRANCH
echo "GUI_BRANCH="$GUI_BRANCH
echo "GENERATOR="$GENERATOR
echo ""

# Install brew package manager.
echo "LOG: INSTALLING BREW..."
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)" < /dev/null

# Install dependencies from package manager.
echo "LOG: INSTALLING DEPENDENCIES..."
brew install pkgconfig autoconf libtool automake wget pcre doxygen python git ninja cmake
brew reinstall gcc
pip3 install cython
pip3 install numpy
echo

# Install Java 8 from temurin repositories.
brew tap homebrew/cask-versions
brew install --cask temurin8
export JAVA_HOME=$(/usr/libexec/java_home -v 1.8)
echo $JAVA_HOME
 
# Create workspace folder.
mkdir ~/opensim-workspace || true

# Download and install SWIG 4.0.2.
echo "LOG: INSTALLING SWIG 4.0.2..."
mkdir -p ~/opensim-workspace/swig-source || true && cd ~/opensim-workspace/swig-source
wget -nc -q --show-progress https://github.com/swig/swig/archive/refs/tags/rel-4.0.2.tar.gz
tar xzf rel-4.0.2.tar.gz && cd swig-rel-4.0.2
sh autogen.sh && ./configure --prefix=$HOME/swig --disable-ccache
make && make -j$NUM_JOBS install  
echo

# Install Netbeans 12.3.
echo "LOG: INSTALLING NETBEANS 12.3..."
mkdir -p ~/opensim-workspace/Netbeans12.3 || true && cd ~/opensim-workspace/Netbeans12.3
wget -nc -q --show-progress https://archive.apache.org/dist/netbeans/netbeans/12.3/Apache-NetBeans-12.3-bin-macosx.dmg
hdiutil attach Apache-NetBeans-12.3-bin-macosx.dmg
sudo installer -pkg /Volumes/Apache\ NetBeans\ 12.3/Apache\ NetBeans\ 12.3.pkg -target /
sudo -k

# Get opensim-core.
echo "LOG: CLONING OPENSIM-CORE..."
git -C ~/opensim-workspace/opensim-core-source pull || git clone https://github.com/opensim-org/opensim-core.git ~/opensim-workspace/opensim-core-source
cd ~/opensim-workspace/opensim-core-source
git checkout $CORE_BRANCH
echo

# Build opensim-core dependencies.
echo "LOG: BUILDING OPENSIM-CORE DEPENDENCIES..."
mkdir -p ~/opensim-workspace/opensim-core-dependencies-build || true
cd ~/opensim-workspace/opensim-core-dependencies-build
cmake ~/opensim-workspace/opensim-core-source/dependencies -DCMAKE_INSTALL_PREFIX=~/opensim-workspace/opensim-core-dependencies-install/ -DSUPERBUILD_ezc3d=on -DOPENSIM_WITH_CASADI=$MOCO -DOPENSIM_WITH_TROPTER=$MOCO
cmake . -LAH
cmake --build . --config $DEBUG_TYPE -j$NUM_JOBS
echo

# Build opensim-core.
echo "LOG: BUILDING OPENSIM-CORE..."
mkdir -p ~/opensim-workspace/opensim-core-build || true
cd ~/opensim-workspace/opensim-core-build
cmake ~/opensim-workspace/opensim-core-source -G"$GENERATOR" -DOPENSIM_DEPENDENCIES_DIR=~/opensim-workspace/opensim-core-dependencies-install/ -DBUILD_JAVA_WRAPPING=on -DBUILD_PYTHON_WRAPPING=on -DOPENSIM_C3D_PARSER=ezc3d -DBUILD_TESTING=off -DCMAKE_INSTALL_PREFIX=~/opensim-core -DOPENSIM_INSTALL_UNIX_FHS=off -DSWIG_DIR=~/swig/share/swig -DSWIG_EXECUTABLE=~/swig/bin/swig -DJAVA_HOME=$(/usr/libexec/java_home -v 1.8) -DJAVA_INCLUDE_PATH=$(/usr/libexec/java_home -v 1.8)/include -DJAVA_INCLUDE_PATH2=$(/usr/libexec/java_home -v 1.8)/include/darwin -DJAVA_AWT_INCLUDE_PATH=$(/usr/libexec/java_home -v 1.8)/include
cmake . -LAH
cmake --build . --config $DEBUG_TYPE -j$NUM_JOBS
cmake --install .
echo

# Get opensim-gui.
echo "LOG: CLONING OPENSIM-GUI..."
git -C ~/opensim-workspace/opensim-gui-source pull || git clone https://github.com/opensim-org/opensim-gui.git ~/opensim-workspace/opensim-gui-source
cd ~/opensim-workspace/opensim-gui-source
git checkout $GUI_BRANCH
git submodule update --init --recursive -- opensim-models opensim-visualizer Gui/opensim/threejs
echo

# Build opensim-gui.
echo "LOG: BUILDING OPENSIM-GUI..."
mkdir -p ~/opensim-workspace/opensim-gui-build || true
cd ~/opensim-workspace/opensim-gui-build
cmake ~/opensim-workspace/opensim-gui-source -DCMAKE_PREFIX_PATH=~/opensim-core -DAnt_EXECUTABLE=/Applications/NetBeans/Apache\ NetBeans\ 12.3.app/Contents/Resources/NetBeans/netbeans/extide/ant/bin/ant -DANT_ARGS="-Dnbplatform.default.netbeans.dest.dir=/Applications/NetBeans/Apache NetBeans 12.3.app/Contents/Resources/NetBeans/netbeans;-Dnbplatform.default.harness.dir=/Applications/NetBeans/Apache NetBeans 12.3.app/Contents/Resources/NetBeans/netbeans/harness"
sudo make CopyOpenSimCore
sudo make PrepareInstaller
echo

# Install opensim-gui.
echo "LOG: INSTALLING OPENSIM-GUI..."
VERSION=`cmake -L . | grep OPENSIMGUI_BUILD_VERSION | cut -d "=" -f2`
echo $VERSION
cd ~/opensim-workspace/opensim-gui-source
sudo installer -pkg Gui/opensim/dist/OpenSim-$VERSION.pkg -target /