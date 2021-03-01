#!/bin/bash 
#xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
#  
#  Linux script to install openSim 4 in Ubuntu 18.04 
#
#
# the functions are used as a trick for using "cd path" 
# by Ibraheem Al-Dhamari, idhamari@uni-koblenz.de,  last update 3.8.2018
#
# TODOS: 
#   - problem in modifing .bashrc
#   - check if buildOpenSimGui is needed
#   - remove the need to sudo password in a secure way
#   - makes the paths more flexible
#   - optimize the script
#   - run some testing and examples

#xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
clear 
#---------------------------------------------------
#               Define Paths 
#---------------------------------------------------
# Note that these paths are hard coded in downloaded files as well!
openSim=~/sw/openSim-dev                                 # root development tree  
opeSimToolsSrc=~/sw/openSim-dev/srcOpenSim/dependencies  # opensim dependencies src
openSimToolsBuild=~/sw/openSim-dev/buildOpenSimTools     # opensim dependencies build
openSimToolsInstall=~/sw/openSim-dev/installOpenSimTools # opensim dependencies install
opeSimSrc=~/sw/openSim-dev/srcOpenSim                    # opensim-core src
openSimBuild=~/sw/openSim-dev/buildOpenSim               # opensim-core build 
openSimInstall=~/sw/openSim-dev/installOpenSim           # opensim-core install
opeSimGuiSrc=~/sw/openSim-dev/srcOpenSimGui              # opensim-gui src
openSimGuiBuild=~/sw/openSim-dev/buildOpenSimGui         # opensim-gui build 
netbeansHM=~/sw/netbeans-8.2                             # netbeans home  
opensimGuiPrg=$opeSimGuiSrc/Gui/opensim                  # opensim project
#---------------------------------------------------
# create the development tree 
#---------------------------------------------------
mkdir  -p  "$openSim"
mkdir  -p  "$openSimToolsBuild"
mkdir  -p  "$openSimToolsInstall"
mkdir  -p  "$openSimBuild"
mkdir  -p  "$openSimInstall"
mkdir  -p  "$openSimGuiBuild"
 
#---------------------------------------------------  
#            install required libs
#---------------------------------------------------
sudo apt -y update && sudo apt -y upgrade
sudo apt install -y subversion git-core git-svn  libx11-dev libxt-dev libgl1-mesa-dev libglu1-mesa-dev freeglut3-dev libfontconfig-dev libxrender-dev libncurses5-dev curl libssl-dev swig3.0   gnome-system-tools build-essential  libcanberra-gtk-module  libcanberra-gtk3-module  doxygen cmake cmake-qt-gui libblas-dev liblapack-dev  libxmu-dev libxi-dev

#install python 2.7
sudo add-apt-repository -y ppa:fkrull/deadsnakes-python2.7
sudo apt update -y
sudo apt install -y python2.7-dev

# download and install oracle java 8  
sudo add-apt-repository -y ppa:webupd8team/java
echo debconf shared/accepted-oracle-license-v1-1 select true | sudo debconf-set-selections
echo debconf shared/accepted-oracle-license-v1-1 seen true  | sudo debconf-set-selections
sudo apt install -y  oracle-java8-installer 


#---------------------------------------------------
#    download and extract source files 
#---------------------------------------------------
# jxbrowser
if [ ! -d $openSim/jxbrowser.6.14.2 ]; then
   echo " Downloading Jxbrowse .....!" 
   wget --no-check-certificate "https://mtixnat.uni-koblenz.de/owncloud/index.php/s/uqGgDa9MXOUi74l/download"  -P "$openSim"     
   unzip -o "$openSim/download"  -d  "$openSim"
   rm "$openSim/download" 
   wait
fi
# opensim-core
if [ ! -d $openSim/srcOpenSim ]; then
   echo " Downloading opensim-core .....!" 
   wget --no-check-certificate "https://mtixnat.uni-koblenz.de/owncloud/index.php/s/H9lLQbkUq736HVW/download"  -P "$openSim"     
   unzip -o "$openSim/download"  -d  "$openSim"
   rm "$openSim/download"
   wait
fi
# opensim-gui
if [ ! -d $openSim/srcOpenSimGui ]; then
   echo " Downloading opensim-gui .....!" 
   wget --no-check-certificate "https://mtixnat.uni-koblenz.de/owncloud/index.php/s/PcxwkKH1rLnI1R4/download"  -P "$openSim"     
   unzip -o "$openSim/download"  -d  "$openSim"
   rm "$openSim/download"
wait
fi
# netbeans
if [ ! -d $netbeansHM ]; then
   echo " Downloading netbeans .....!" 
   wget --no-check-certificate "https://mtixnat.uni-koblenz.de/owncloud/index.php/s/rn2VL5Ui6pmksYV/download"  -P ~/sw     
  unzip -o ~/sw/download -d  ~/sw
  rm  ~/download
  wait
fi
sudo chown -R $USER:$USER ~/sw
wait
sudo cp $openSim/jxbrowser.6.14.2/libjx* /usr/local/lib
sudo ldconfig -v

#---------------------------------------------------
# build and install openSim dependencies  
#-------------------------------------------------
toolsBuild() {
  echo "building opensim-dependencies..." 
  cd "$openSimToolsBuild" 
  cmake ../srcOpenSim/dependencies
  make -j 4
  cd simbody/build
  make
  make doxygen
  make install 
}
toolsBuild 

 # Update system variables
if ! grep -q "sw/netbeans-8.2/extide/ant/bin" ~/.bashrc; then
  echo "updating system variables ..."
  echo "export PATH=$PATH:~/sw/netbeans-8.2/bin:~/sw/netbeans-8.2/extide/ant/bin:~/sw/openSim-dev/installOpenSimTools/simbody" >> ~/.bashrc
  echo "export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:~/sw/openSim-dev/installOpenSimTools/simbody/lib" >> ~/.bashrc
  echo "export ANT_HOME=~/sw/netbeans-8.2/extide/ant" >> ~/.bashrc
  source ~/.bashrc
else
  echo "system variables up-to-date..."
fi
#---------------------------------------------------
# build and install openSim core  
#-------------------------------------------------
coreBuild() {
  echo "building opensim-core..." 
  cd "$openSimBuild" 
  cmake ../srcOpenSim
  make -j 4
  make doxygen
  make install  
}
coreBuild 

if ! grep -q "OPENSIM_HOME" ~/.bashrc; then
  echo "updating system variables ..."
  echo "export PATH=$PATH:~/sw/openSim-dev/installOpenSim" >> ~/.bashrc
  echo "export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:~/sw/openSim-dev/installOpenSim/lib" >> ~/.bashrc
  echo "export OPENSIM_HOME=~/sw/openSim-dev/installOpenSim" >> ~/.bashrc
  source ~/.bashrc
else
  echo "system variables up-to-date..."
fi
#---------------------------------------------------
# build and install openSimGui  
#-------------------------------------------------
#download and install jxbrowser libs
sudo apt install -y  chromium-browser  libgconf2-4
wait
guiBuild() {
  echo "building opensim-gui..." 
  cd "$openSimGuiBuild" 
  cmake ../srcOpenSimGui
}
guiBuild
# open opensim project using netbeans 
$netbeansHM/bin/netbeans $opensimGuiPrg 
 
# run some examples
# simbody
# opensim
echo ""
echo ""
echo ""
echo ""
echo ""
echo ""

echo "all tasks are completed! ignore the last error and build openSim using netbeans!"
