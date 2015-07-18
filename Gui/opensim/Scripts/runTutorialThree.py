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
# Written by James Dunne, Stanford University

# This example performs the steps of Tutorial Three in scripting form

# Folder paths
installDir 		= 	getInstallDir()
modelFolder		=	installDir+"\Models\Gait2354_Simbody" 

# Input Model 
modelName		=	modelFolder+"\gait2354_simbody.osim"
genericModelName=	"gait2354_simbody.osim"	
#Output models
scaledModelName	=	"scaled_gait2354.osim"
scaleModelPath	=	modelFolder+"\scaled_gait2354.osim"
movedModelName	=	"subject01_simbody.osim"
movedModelPath	=	modelFolder+"\subject01_simbody.osim"

# input xml files
scaleSetup		=	modelFolder+"\subject01_Setup_Scale.xml"
markerSetFile	=	modelFolder+"\gait2354_Scale_MarkerSet.xml"
ikSetupFile		=	modelFolder+"\subject01_Setup_IK.xml"
idSetupFile		=	modelFolder+"\subject01_Setup_InverseDynamics.xml"
extLoadsFile	=	"subject01_walk1_grf.xml"
# output xml files
appliedscaleSet	=	modelFolder+"\subject01_scaleSet_applied.xml"
movedMarkers	=	"subject01_Markers_moved.xml"

# Input data files 
staticMarkerName	=	"subject01_static.trc"
staticMarkers		=	modelFolder+"\subject01_static.trc"
walk1MarkersName	=	"subject01_walk1.trc"
walk1MarkersPath	=	modelFolder+"\subject01_walk1.trc"
# Output data files
staticCoordinates	=	"staticCoordinates.mot"
ikMotionFile		=	"subject01_walk1_ik.mot"
ikMotionFilePath	=	modelFolder+"\subject01_walk1_ik.mot"
idResultsFile		=	"inverse_dynamics.sto"

# Define some of the subject measurements
subjectMass		=	72.6

## load and define model
# Load a model 
loadModel(modelName)
# Get a handle to the current model
myModel = getCurrentModel()
#initialize
myModel.initSystem()
myState = myModel.initSystem()

## Add a MarkerSet to the model
#	Create a MarkerSet object
newMarkers = modeling.MarkerSet(markerSetFile)
# 	Replace the markerSet to the model
myModel.replaceMarkerSet(myState,newMarkers)
#	Re-initialize State
myState = myModel.initSystem()

## Scaling Tool
# Create the scale tool object from existing xml
scaleTool = modeling.ScaleTool(scaleSetup)
## ModelScaler-
#Name of OpenSim model file (.osim) to write when done scaling.
scaleTool.getModelScaler().setOutputModelFileName(scaledModelName)
# Filename to write scale factors that were applied to the unscaled model (optional)
scaleTool.getModelScaler().setOutputScaleFileName(appliedscaleSet)
# Get the path to the subject
path2subject = scaleTool.getPathToSubject()
# Run model scaler Tool
scaleTool.getModelScaler().processModel(myState,myModel,path2subject,subjectMass);

## Load Scaled model and Initialize states-
# Load a model 
loadModel(scaleModelPath)
# Get a handle to the current model
myModel = getCurrentModel()
#initialize
myState = myModel.initSystem()

## ModelPlacer-
# Get the path to the subject
path2subject = scaleTool.getPathToSubject()
# Run Marker Placer
scaleTool.getMarkerPlacer().processModel(myState,myModel,path2subject)

## Load Scaled model and Initialize states-
# Load a model 
loadModel(movedModelPath)
# Get a handle to the current model
myModel = getCurrentModel()
#initialize
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
# Run the tool
idTool.run()


