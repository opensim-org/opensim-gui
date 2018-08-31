# --------------------------------------------------------------------------- #
# OpenSim: runScaling.py                                                      #
# --------------------------------------------------------------------------- #
# OpenSim is a toolkit for musculoskeletal modeling and simulation,           #
# developed as an open source project by a worldwide community. Development   #
# and support is coordinated from Stanford University, with funding from the  #
# U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file     #
# for more information including specific grant numbers.                      #
#                                                                             #
# Copyright (c) 2005-2017 Stanford University and the Authors                 #
# Author(s): Ayman Habib, James Dunne                                         #
#                                                                             #
# Licensed under the Apache License, Version 2.0 (the "License"); you may     #
# not use this file except in compliance with the License. You may obtain a   #
# copy of the License at http://www.apache.org/licenses/LICENSE-2.0           #
#                                                                             #
# Unless required by applicable law or agreed to in writing, software         #
# distributed under the License is distributed on an "AS IS" BASIS,           #
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.    #
# See the License for the specific language governing permissions and         #
# limitations under the License.                                              #
# --------------------------------------------------------------------------- #

# Written by James Dunne, Stanford University
import os.path
  
# Define paths
modelFolder		=	os.path.join(getResourcesDir(),"Models", "Gait2354_Simbody");
scaleSetupPath	=	os.path.join(modelFolder, "subject01_Setup_Scale.xml");

## Run the Scale Tool with all the current settings from the setup file
scaleTool = modeling.ScaleTool(scaleSetupPath)
scaleTool.run();

## Load the original and scaled Models 
loadModel(os.path.join(modelFolder,"gait2354_simbody.osim"));
loadModel(os.path.join(modelFolder,"subject01_simbody.osim"));