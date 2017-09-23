# --------------------------------------------------------------------------- #
# OpenSim: alterTendonSlackLength.py                                          #
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
# Author: Ayman Habib, 
# Stanford University
#
# This example script shows how to change the attributes of the muscles for the current model 
# (tendonSlackLength in this example).

# Get handle to current model in GUI
oldModel = getCurrentModel()
 
if not oldModel:
	print "ERROR: Need to load a model first\n"
	
# Create a fresh copy
myModel = modeling.Model(oldModel)

# Initialize the copy, if values needed to be set in state
# pass along the variable myState returned by initSystem
myState = myModel.initSystem()

# Name the copy for later display in the GUI
oldName = oldModel.getName()
myModel.setName(oldName+"_longerTSL")

# A scale factor for tendonSlackLength of muscles
tendonSlackLengthScale = 1.5

# Change TendonSlackLength for all muscles in the model
for i in range(myModel.getMuscles().getSize()):
	currentMuscle = myModel.getMuscles().get(i)
	oldSL = currentMuscle.getTendonSlackLength()
	currentMuscle.setTendonSlackLength(oldSL * tendonSlackLengthScale)

#get full path name of original model
fullPathName = oldModel.getInputFileName()

#Change pathname to output file name
newPathName = fullPathName.replace('.osim', '_longerTSL.osim')
myModel.print(newPathName)

#Add model to GUI
loadModel(newPathName)
