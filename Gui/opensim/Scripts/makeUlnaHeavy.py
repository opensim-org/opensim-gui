# --------------------------------------------------------------------------- #
# OpenSim: makeUlnaHeavy.py                                                   #
# --------------------------------------------------------------------------- #
# OpenSim is a toolkit for musculoskeletal modeling and simulation,           #
# developed as an open source project by a worldwide community. Development   #
# and support is coordinated from Stanford University, with funding from the  #
# U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file     #
# for more information including specific grant numbers.                      #
#                                                                             #
# Copyright (c) 2005-2017 Stanford University and the Authors                 #
# Author(s): Ayman Habib, Kevin Xu                                            #
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


#
# Author(s): Ayman Habib
# Stanford University
#
# This example shows how to load a model and create a modified version of the model
# The script increases the mass of the ulna. The modified model is then loaded in the GUI.

# Get handle to the Arm26 model
oldModel = modeling.Model(getInstallDir() + "/Models/Arm26/arm26.osim")
# Create a fresh copy
myModel = modeling.Model(oldModel)

# Initialize the copy, if values need to be set in the model's state
# pass along the variable myState  returned by initSystem
myState = myModel.initSystem()

# Name the copy for later display in the GUI
oldName = oldModel.getName()
myModel.setName(oldName+"_heavier")

# A scale factor for mass of forarm
massScale = 3

# Change mass of forarm in the model
forearm = myModel.getBodySet().get("r_ulna_radius_hand")
forearm.setMass(forearm.getMass() * massScale)

# Get full path name of original.old model
fullPathName = oldModel.getInputFileName()

# Change the name of the modified model
newName = fullPathName.replace('.osim', '_heavier.osim')
myModel.print(newName)

# Load the model in the GUI
loadModel(newName)




