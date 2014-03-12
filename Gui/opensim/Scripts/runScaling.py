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
# Adapted from script donated by David Saxby, Griffith University 
# Written by James Dunne, Stanford University


## This example shows detailed steps for scaling a model.  

# Folder paths
installDir 		= 	getInstallDir()
modelFolder		=	installDir+"\Models\Gait2354_Simbody" 

# Input Model 
modelName		=	modelFolder+"\gait2354_simbody.osim"
genericModelName=	"gait2354_simbody.osim"	
#Output models
scaledModelName	=	"scaled_gait2392.osim"
scaleModelPath	=	modelFolder+"\scaled_gait2392.osim"
movedModelName	=	"moved_gait2392.osim"
movedModelPath	=	modelFolder+"\moved_gait2392.osim"

# input xml files
scaleSetup		=	modelFolder+"\subject01_Setup_Scale.xml"
markerSetFile	=	modelFolder+"\gait2354_Scale_MarkerSet.xml"
ikSetupFile		=	modelFolder+"\subject01_Setup_IK.xml"
idSetupFile		=	modelFolder+"\subject01_Setup_InverseDynamics.xml"
extLoadsFile	=	"subject01_walk1_grf.xml"
# ouput xml files
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
idResultsFile		=	"inverse_dynamics.sto"

# Define some of the subject measurements
subjectMass		=	72.6
subjectHeight	=	1803.4
subjectAge		=	99


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

## Get time array for .trc file
# Get trc data to determine time range
markerData = modeling.MarkerData(staticMarkers)
#Get initial and final time 
initial_time = markerData.getStartFrameTime()
final_time = markerData.getLastFrameTime()
# Create an array double and apply the time range
TimeArray=modeling.ArrayDouble()
TimeArray.set(0,initial_time)
TimeArray.set(1,final_time)  

## Scaling Tool
# Create the scale tool object from existing xml
scaleTool = modeling.ScaleTool(scaleSetup)

# Construct the scale tool from an existing xml
scaleTool.setSubjectMass(subjectMass)
scaleTool.setSubjectHeight(subjectHeight)
scaleTool.setSubjectHeight(subjectAge)

## GenericModelMaker-
# Tell scale tool to use the loaded model
scaleTool.getGenericModelMaker().setModelFileName(genericModelName)
# Set the Marker Set file (incase a markerset isnt attached to the model)
scaleTool.getGenericModelMaker().setMarkerSetFileName(markerSetFile)

## ModelScaler-
# Whether or not to use the model scaler during scale
scaleTool.getModelScaler().setApply(1)
# Set the marker file (.trc) to be used for scaling 
scaleTool.getModelScaler().setMarkerFileName(staticMarkerName)
# set a time range
scaleTool.getModelScaler().setTimeRange(TimeArray)
# Indicating whether or not to preserve relative mass between segments
scaleTool.getModelScaler().setPreserveMassDist(1)
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
# Whether or not to use the model scaler during scale
scaleTool.getMarkerPlacer().setApply(1)
# Set the marker placer time range
scaleTool.getMarkerPlacer().setTimeRange(TimeArray)
# Set the marker file (.trc) to be used for scaling 
scaleTool.getMarkerPlacer().setStaticPoseFileName(staticMarkerName)
# Return name to a variable for future use in functions  
scaledAdjustedModel = scaleTool.getMarkerPlacer().setOutputModelFileName(movedModelName)
# Set the output motion filename
scaleTool.getMarkerPlacer().setOutputMotionFileName(staticCoordinates)
# Set the output xml of the marker adjustments
scaleTool.getMarkerPlacer().setOutputMarkerFileName(movedMarkers)
# Maximum amount of movement allowed in marker data when averaging
scaleTool.getMarkerPlacer().setMaxMarkerMovement(-1.00000000)
# Run Marker Placer
scaleTool.getMarkerPlacer().processModel(myState,myModel,path2subject)
# Load the Marker Adjusted Mdoel
loadModel(movedModelPath)
