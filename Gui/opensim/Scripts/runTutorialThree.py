# --------------------------------------------------------------------------- #
# OpenSim: runTutorialThree.py                                                #
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

# This example performs the steps of Tutorial Three in scripting form
import os.path

# Folder paths
modelFolder		=	os.path.join(getResourcesDir(),"Models", "Gait2354_Simbody");
scaleSetupPath	=	os.path.join(modelFolder, "subject01_Setup_Scale.xml");

# Models
modelName	    =	os.path.join(modelFolder,"gait2354_simbody.osim");
scaleModelName	=	os.path.join(modelFolder, "subject01_simbody.osim");

# input xml files
scaleSetup		=	os.path.join(modelFolder,"subject01_Setup_Scale.xml");
markerSetFile	=	os.path.join(modelFolder,"gait2354_Scale_MarkerSet.xml");
ikSetupFile		=	os.path.join(modelFolder,"subject01_Setup_IK.xml");
idSetupFile		=	os.path.join(modelFolder,"subject01_Setup_InverseDynamics.xml");
extLoadsFile	=	os.path.join(modelFolder,"subject01_walk1_grf.xml");

# Input data files 
staticMarkers	=	os.path.join(modelFolder,"subject01_static.trc");
walkingMarkers	=	os.path.join(modelFolder,"subject01_walk1.trc");

# Output data files
ikMotionFilePath=	os.path.join(modelFolder,"subject01_walk1_ik.mot");
idResultsFile	=	os.path.join(modelFolder,"inverse_dynamics.sto");

# Define some of the subject measurements
subjectMass		=	72.6

## load and define model
# Load model 
loadModel(modelName)
# Get a handle to the current model
myModel = getCurrentModel()
#initialize
myModel.initSystem()
myState = myModel.initSystem()

## Scaling Tool
# Create the scale tool object from existing xml
scaleTool = modeling.ScaleTool(scaleSetup)
scaleTool.run();

## load and Scaled Model
# Load model 
loadModel(scaleModelName)
# Get a handle to the current model
myModel = getCurrentModel()
#initialize
myModel.initSystem()
myState = myModel.initSystem()

## Inverse Kinematics tool
# Create the IK tool object from existing xml
ikTool = modeling.InverseKinematicsTool(ikSetupFile)
ikTool.setModel(myModel)
ikTool.run()
# Load a motion
loadMotion(ikMotionFilePath)
#initialize
myState = myModel.initSystem()

## Inverse Dynamics 
# Create the ID tool object from existing xml
idTool = modeling.InverseDynamicsTool(idSetupFile)
# Set the model to scaled model from above
idTool.setModel(myModel)
# Set the full path to the External Loads File
idTool.setExternalLoadsFileName(extLoadsFile)
# Run the tool
idTool.run()


