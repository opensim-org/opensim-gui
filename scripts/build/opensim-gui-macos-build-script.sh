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
    echo "                   RelWithDebInfo: Debugger symbols. Optimized. Bigger than Release, but not slower."
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
        *) Help;;
    esac
done

# Check parameters are valid.
if [[ $NUM_JOBS -lt 1 ]]
then
    Help
fi

if [[ $DEBUG_TYPE != "Release" ]] && [[ $DEBUG_TYPE != "Debug" ]] && [[ $DEBUG_TYPE != "RelWithDebInfo" ]] && [[ $DEBUG_TYPE != "MinSizeRel" ]]
then
    Help
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
brew uninstall cmake || true
brew install cmake pkgconfig autoconf libtool automake wget pcre pcre2 doxygen llvm ant
brew reinstall gcc
pip3 install numpy==2.4
echo

# Install Java 17 (ARM-native; replaces broken Zulu 8).
echo "LOG: INSTALLING JDK 17..."
brew install --cask temurin@17
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

# Create workspace folder.
mkdir -p ~/opensim-workspace

# Download and install SWIG 4.1.1.
echo "LOG: INSTALLING SWIG 4.1.1..."
mkdir -p ~/opensim-workspace/swig-source
cd ~/opensim-workspace/swig-source
wget -nc -q --show-progress https://github.com/swig/swig/archive/refs/tags/v4.1.1.tar.gz
tar xzf v4.1.1.tar.gz
cd swig-4.1.1
sh autogen.sh
PATH="/opt/homebrew/bin:$PATH" ./configure --prefix=$HOME/swig --disable-ccache
make
make -j$NUM_JOBS install
echo

# Install NetBeans 17.
echo "LOG: INSTALLING NETBEANS 17..."
mkdir -p ~/opensim-workspace/Netbeans17
cd ~/opensim-workspace/Netbeans17
wget -nc -q --show-progress https://archive.apache.org/dist/netbeans/netbeans-installers/17/Apache-NetBeans-17-bin-macosx.dmg
hdiutil attach Apache-NetBeans-17-bin-macosx.dmg
sudo installer -pkg /Volumes/Apache\ NetBeans\ 17/Apache\ NetBeans\ 17.pkg -target /
sudo -k
echo

# Get opensim-core.
echo "LOG: CLONING OPENSIM-CORE..."
git -C ~/opensim-workspace/opensim-core-source pull || git clone https://github.com/opensim-org/opensim-core.git ~/opensim-workspace/opensim-core-source
cd ~/opensim-workspace/opensim-core-source
git checkout $CORE_BRANCH
echo

# Build opensim-core dependencies.
echo "LOG: BUILDING OPENSIM-CORE DEPENDENCIES..."
mkdir -p ~/opensim-workspace/opensim-core-dependencies-build
cd ~/opensim-workspace/opensim-core-dependencies-build

cmake ~/opensim-workspace/opensim-core-source/dependencies \
    -G"$GENERATOR" \
    -DCMAKE_BUILD_TYPE=$DEBUG_TYPE \
    -DCMAKE_INSTALL_PREFIX=~/opensim-workspace/opensim-core-dependencies-build/install \
    -DSUPERBUILD_simbody=ON \
    -DSUPERBUILD_casadi=$MOCO \
    -DSUPERBUILD_ipopt=$MOCO \
    -DSUPERBUILD_ezc3d=ON \
    -DOPENSIM_WITH_CASADI=$MOCO \
    -DOPENSIM_WITH_TROPTER=$MOCO

cmake --build . --config $DEBUG_TYPE -j$NUM_JOBS
echo

# Build opensim-core.
echo "LOG: BUILDING OPENSIM-CORE..."
mkdir -p ~/opensim-workspace/opensim-core-build
cd ~/opensim-workspace/opensim-core-build

cmake ~/opensim-workspace/opensim-core-source \
    -G"$GENERATOR" \
    -DOPENSIM_DEPENDENCIES_DIR=~/opensim-workspace/opensim-core-dependencies-build/install \
    -DBUILD_JAVA_WRAPPING=on \
    -DBUILD_PYTHON_WRAPPING=on \
    -DOPENSIM_C3D_PARSER=ezc3d \
    -DBUILD_TESTING=off \
    -DCMAKE_INSTALL_PREFIX=~/opensim-core \
    -DOPENSIM_INSTALL_UNIX_FHS=off \
    -DSWIG_DIR=~/swig/share/swig \
    -DSWIG_EXECUTABLE=~/swig/bin/swig \
    -DJAVA_HOME="$JAVA_HOME"

cmake . -LAH
cmake --build . --config $DEBUG_TYPE -j$NUM_JOBS
cmake --install .
echo

# Get opensim-gui.
echo "LOG: CLONING OPENSIM-GUI..."
git -C ~/opensim-workspace/opensim-gui-source pull || git clone https://github.com/opensim-org/opensim-gui.git ~/opensim-workspace/opensim-gui-source
cd ~/opensim-workspace/opensim-gui-source
git checkout $GUI_BRANCH

# Update the same submodules used by macOS CI.
git submodule update --init --recursive -- opensim-models opensim-visualizer Gui/opensim/opensim-viewer
echo

# Build opensim-gui.
echo "LOG: BUILDING OPENSIM-GUI..."
mkdir -p ~/opensim-workspace/opensim-gui-build
cd ~/opensim-workspace/opensim-gui-build

cmake ~/opensim-workspace/opensim-gui-source \
    -DCMAKE_PREFIX_PATH=~/opensim-core \
    -DANT_ARGS="-Dnbplatform.default.netbeans.dest.dir=/Applications/NetBeans/Apache NetBeans 17.app/Contents/Resources/NetBeans/netbeans;-Dnbplatform.default.harness.dir=/Applications/NetBeans/Apache NetBeans 17.app/Contents/Resources/NetBeans/netbeans/harness"

cmake --build . --target CopyOpenSimCore --config $DEBUG_TYPE
cmake --build . --target PrepareInstaller --config $DEBUG_TYPE

# Read the value of the cache variable storing the GUI build version.
VERSION=`cmake -L . | grep OPENSIMGUI_BUILD_VERSION | cut -d "=" -f2`
echo $VERSION

# Add jxbrowser files to installer content.
echo "LOG: ADDING JXBROWSER FILES..."

ROOT="$HOME/opensim-workspace"
NBM_DIR="$ROOT/prebuilt_jxb"
EXTRACT_DIR="$NBM_DIR/extracted"
INSTALLER_CONTENT="$ROOT/opensim-gui-source/Gui/opensim/dist/installer/opensim/opensim"

# Create prebuilt directory.
mkdir -p "$NBM_DIR"

# Download the NBM file pinned to version v4.6.0.
curl -L "https://github.com/opensim-org/opensim-visualizer/releases/download/v4.6.0/org-opensim-javabrowser.nbm" \
     -o "$NBM_DIR/org-opensim-javabrowser.nbm"

# Extract the NBM (NBM is a ZIP).
mkdir -p "$EXTRACT_DIR"
unzip -o "$NBM_DIR/org-opensim-javabrowser.nbm" -d "$EXTRACT_DIR"

# Create installer modules directories.
mkdir -p "$INSTALLER_CONTENT/modules/"
mkdir -p "$INSTALLER_CONTENT/modules/ext/"

# Copy the main JAR.
cp "$EXTRACT_DIR/netbeans/modules/org-opensim-javabrowser.jar" \
   "$INSTALLER_CONTENT/modules/"

# Copy macOS-specific JARs with exact names.
cp "$EXTRACT_DIR/netbeans/modules/ext/jxbrowser-7.44.1.jar" \
   "$INSTALLER_CONTENT/modules/ext/"

cp "$EXTRACT_DIR/netbeans/modules/ext/jxbrowser-swing-7.44.1.jar" \
   "$INSTALLER_CONTENT/modules/ext/"

cp "$EXTRACT_DIR/netbeans/modules/ext/jxbrowser-mac-arm-7.44.1.jar" \
   "$INSTALLER_CONTENT/modules/ext/"

# Verify files copied.
echo "JAR files now in installer content:"
find "$INSTALLER_CONTENT" -name "*.jar"

# Install opensim-gui.
echo "LOG: INSTALLING OPENSIM-GUI..."
sudo installer -pkg "$ROOT/opensim-gui-source/Gui/opensim/dist/OpenSim-$VERSION.pkg" -target /
