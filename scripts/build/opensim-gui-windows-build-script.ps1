#Requires -RunAsAdministrator
param (
  [switch]$s=$false,
  [switch]$h=$false,
  [string]$d="Release",
  [string]$c="main",
  [string]$g="main",
  [int]$j=[int]4
)

# Default values for variables.
$DEBUG_TYPE="Release"
$NUM_JOBS=4
$MOCO="on"
$CORE_BRANCH="main"
$GUI_BRANCH="master"

function Help {
    Write-Output "This script builds the last available version of OpenSim-Gui in your computer."
    Write-Output "Usage: ./scriptName [OPTION]..."
    Write-Output "Example: ./opensim-gui-build.sh -j4 -dRelease"
    Write-Output "    -d        Debug Type. Available Options:"
    Write-Output "                  Release (Default): No debugger symbols. Optimized."
    Write-Output "                  Debug: Debugger symbols. No optimizations (>10x slower). Library names ending with _d."
    Write-Output "                  RelWithDebInfo: Debugger symbols. Optimized. Bigger than Release, but not slower."
    Write-Output "                  MinSizeRel: No debugger symbols. Minimum size. Optimized."
    Write-Output "    -j        Number of jobs to use when building libraries (>=1)."
    Write-Output "    -s        Simple build without moco (Tropter and Casadi disabled)."
    Write-Output "    -c        Branch for opensim-core repository."
    Write-Output "    -g        Branch for opensim-gui repository."
    Write-Output ""
    exit
}

# Get flag values if exist.

if ($h) {
    Help
}
if ($s) {
    $MOCO="off"
}
if ($d -ne "Release" -and $d -ne "Debug" -and $d -ne "RelWithDebInfo" -and $d -ne "MinSizeRel") {
    Write-Error "Value for parameter -d not valid."
} else {
    $DEBUG_TYPE=$d
}
if ($c) {
    $CORE_BRANCH=$c
}
if ($g) {
    $GUI_BRANCH=$g
}
if ($j -lt [int]1) {
    Write-Error "Value for parameter -j not valid."
    Help
} else {
    $NUM_JOBS=$j
}

Write-Output "DEBUG_TYPE $DEBUG_TYPE"
Write-Output "NUM_JOBS $NUM_JOBS"
Write-Output "MOCO $MOCO"
Write-Output "CORE_BRANCH $CORE_BRANCH"
Write-Output "GUI_BRANCH $GUI_BRANCH"

# Install chocolatey
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

# Install Microsoft Visual Studio 2022 Community
choco install visualstudio2022community -y
choco install visualstudio2022-workload-nativedesktop -y
choco install visualstudio2022buildtools -y

# Install cmake 3.23.2
choco install cmake.install --version 3.23.3 --installargs '"ADD_CMAKE_TO_PATH=System"' -y

# Install git
choco install git.install -y

# Install dependencies of opensim-core
choco install python3  -y
choco install temurin8 --params="/ADDLOCAL=FeatureMain,FeatureEnvironment,FeatureJavaHome" -y
choco install swig  -y
choco install nsis  -y
py -m pip install numpy

# Java environment variables.
$JAVA_HOME=$Env:Programfiles + "\Temurin\bin"
$JAVA_INCLUDE_PATH=$JAVA_HOME + "\include"
$JAVA_INCLUDE_PATH2=$JAVA_HOME + "\include\win32"
$JAVA_AWT_INCLUDE_PATH=$JAVA_HOME + "\include"

# Download Netbeans 12.8
md C:/opensim-workspace/netbeans-12.3/
chdir C:/opensim-workspace/netbeans-12.3/
$WebClient = New-Object System.Net.WebClient
$WebClient.DownloadFile("https://archive.apache.org/dist/netbeans/netbeans/12.3/Apache-NetBeans-12.3-bin-windows-x64.exe","C:/opensim-workspace/netbeans-12.3/Apache-NetBeans-12.3-bin-windows-x64.exe")
$expectedHash = "0695d87d9c72dcf3738672ba83eb273dda02066fa5eee80896cb6ccf79175840367a13d22ab3cb9838dffaa9a219dd1f73aee0e27c085e5310da2e3bbbc92b2c"
$hashFromFile = Get-FileHash -Algorithm SHA512 -Path C:/opensim-workspace/netbeans-12.3/Apache-NetBeans-12.3-bin-windows-x64.exe
if (($hashFromFile.Hash) -ne ($expectedHash)) { Write-Error "Hash doesn't match." }
C:/opensim-workspace/netbeans-12.3/Apache-NetBeans-12.3-bin-windows-x64.exe --silent | Out-Null # This installer is gregarious.

# Clone opensim-core
chdir C:/opensim-workspace/
&"$Env:Programfiles\Git\bin\git.exe" clone https://github.com/opensim-org/opensim-core.git C:/opensim-workspace/opensim-core-source
chdir C:/opensim-workspace/opensim-core-source
&"$Env:Programfiles\Git\bin\git.exe" checkout $CORE_BRANCH

# Generate dependencies project and build dependencies using superbuild
md C:/opensim-workspace/opensim-core-dependencies-build
chdir C:/opensim-workspace/opensim-core-dependencies-build
&"$Env:Programfiles\CMake\bin\cmake.exe" C:/opensim-workspace/opensim-core-source/dependencies/ -G"Visual Studio 17 2022" -A x64 -DCMAKE_INSTALL_PREFIX="C:/opensim-workspace/opensim-core-dependencies-install" -DSUPERBUILD_ezc3d:BOOL=on -DOPENSIM_WITH_CASADI:BOOL=$MOCO -DOPENSIM_WITH_TROPTER:BOOL=$MOCO 
&"$Env:Programfiles\CMake\bin\cmake.exe" . -LAH
&"$Env:Programfiles\CMake\bin\cmake.exe" --build . --config $DEBUG_TYPE -- /maxcpucount:$NUM_JOBS

# Generate opensim-core build and build it
md C:/opensim-workspace/opensim-core-build -Force
chdir C:/opensim-workspace/opensim-core-build
$env:CXXFLAGS = "/W0"
&"$Env:Programfiles\CMake\bin\cmake.exe" C:/opensim-workspace/opensim-core-source/ -G"Visual Studio 17 2022" -A x64 -DOPENSIM_DEPENDENCIES_DIR="C:/opensim-workspace/opensim-core-dependencies-install" -DBUILD_JAVA_WRAPPING=on -DBUILD_PYTHON_WRAPPING=on -DOPENSIM_C3D_PARSER=ezc3d -DBUILD_TESTING=off -DCMAKE_INSTALL_PREFIX="C:/opensim-core" -DOPENSIM_WITH_CASADI:BOOL=$MOCO -DOPENSIM_WITH_TROPTER:BOOL=$MOCO -DJAVA_HOME=$JAVA_HOME -DJAVA_INCLUDE_PATH=$JAVA_INCLUDE_PATH -DJAVA_INCLUDE_PATH2=$JAVA_INCLUDE_PATH2 -DJAVA_AWT_INCLUDE_PATH=$JAVA_AWT_INCLUDE_PATH
&"$Env:Programfiles\CMake\bin\cmake.exe" . -LAH
&"$Env:Programfiles\CMake\bin\cmake.exe" --build . --config $DEBUG_TYPE -- /maxcpucount:$NUM_JOBS
&"$Env:Programfiles\CMake\bin\cmake.exe" --install .

# Clone opensim-gui and submodules
&"$Env:Programfiles\Git\bin\git.exe" clone https://github.com/opensim-org/opensim-gui.git C:/opensim-workspace/opensim-gui-source
chdir C:/opensim-workspace/opensim-gui-source
&"$Env:Programfiles\Git\bin\git.exe" checkout $GUI_BRANCH
&"$Env:Programfiles\Git\bin\git.exe" submodule update --init --recursive -- opensim-models opensim-visualizer Gui/opensim/threejs

# Build opensim-gui
md C:/opensim-workspace/opensim-gui-build
chdir C:/opensim-workspace/opensim-gui-build
md C:/opensim-gui
&"$Env:Programfiles\CMake\bin\cmake.exe" C:/opensim-workspace/opensim-gui-source/ -G"Visual Studio 17 2022" -A x64 -DCMAKE_PREFIX_PATH=C:/opensim-core -DAnt_EXECUTABLE="C:\Program Files\NetBeans-12.3\netbeans\extide\ant\bin\ant" -DANT_ARGS="-Dnbplatform.default.netbeans.dest.dir=C:\Program Files\NetBeans-12.3\netbeans;-Dnbplatform.default.harness.dir=C:\Program Files\NetBeans-12.3\netbeans\harness"
&"$Env:Programfiles\CMake\bin\cmake.exe" --build . --target CopyOpenSimCore --config $DEBUG_TYPE
&"$Env:Programfiles\CMake\bin\cmake.exe" --build . --target CopyModels --config $DEBUG_TYPE
&"$Env:Programfiles\CMake\bin\cmake.exe" --build . --target PrepareInstaller --config $DEBUG_TYPE
&"$Env:Programfiles\CMake\bin\cmake.exe" --build . --target CopyJRE --config $DEBUG_TYPE
&"$Env:Programfiles\CMake\bin\cmake.exe" --build . --target CopyVisualizer --config $DEBUG_TYPE
# Read the value of the cache variable storing the GUI build version.
$env:match = &"$Env:Programfiles\CMake\bin\cmake.exe" -L . | Select-String -Pattern OPENSIMGUI_BUILD_VERSION
$version = $env:match.split('=')[1]
echo $version

# Create installer.
cd C:/opensim-workspace/opensim-gui-source/Gui/opensim/dist/installer
&"$Env:ProgramFiles (x86)\NSIS\makensis.exe" make_installer.nsi