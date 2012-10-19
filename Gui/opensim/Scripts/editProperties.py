# ----------------------------------------------------------------------- #
# The OpenSim API is a toolkit for musculoskeletal modeling and           #
# simulation. See http://opensim.stanford.edu and the NOTICE file         #
# for more information. OpenSim is developed at Stanford University       #
# and supported by the US National Institutes of Health (U54 GM072970,    #
# R24 HD065690) and by DARPA through the Warrior Web program.             #
#                                                                         #   
# Copyright (c) 2005-2012 Stanford University and the Authors             #
#                                                                         #   
# Licensed under the Apache License, Version 2.0 (the "License");         #
# you may not use this file except in compliance with the License.        #
# You may obtain a copy of the License at                                 #
# http://www.apache.org/licenses/LICENSE-2.0.                             #
#                                                                         # 
# Unless required by applicable law or agreed to in writing, software     #
# distributed under the License is distributed on an "AS IS" BASIS,       #
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or         #
# implied. See the License for the specific language governing            #
# permissions and limitations under the License.                          #
# ----------------------------------------------------------------------- #

# This example shows how to use the Property Helper convenience method
# to access and modify model component properties. This method is 
# needed when you want to modify a property that does not have an 
# available custom "set" method.
#
# The model used is the simple Bouncing Block model included with
# the OpenSim distribution. It modifies the properties of one of
# the bushings in the model

# Load the model and get handle 
addModel(getInstallDir()+"/Models/BouncingBlock/bouncing_block.osim")
currentModel = getCurrentModel()

# Create a new copy
myModel = modeling.Model(currentModel)

# Name the copy for later display in the GUI
currentName = myModel.getName()
myModel.setName(currentName+"_edited")

# Initialize the copy, if values need to be set in the model's state
# pass along the variable myState  returned by initSystem
myState = myModel.initSystem()

# Change the mass of the block body. This property has a custom
# "set" function that we can use to change the mass
block = myModel.getBodySet().get("block")
block.setMass(100)

# Change the hip bushing stiffness using the property helper
# since a custom get method like used above is not available

# Get an editable handle to the property
hipBushing = myModel.getForceSet().get("hip_bushing")
hipStiffness = hipBushing.updPropertyByName("rotational_stiffness")

# Update the property (makes the spring 10X stiffer
modeling.PropertyHelper.setValueVec3(100, hipStiffness,0)
modeling.PropertyHelper.setValueVec3(100, hipStiffness,1)
modeling.PropertyHelper.setValueVec3(100, hipStiffness,2)

# Get full path name of original.old model
fullPathName = currentModel.getInputFileName()

# Change the name of the modified model
newName = fullPathName.replace('.osim', '_edited.osim')
myModel.print(newName)

# Load the model in the GUI
addModel(newName)
