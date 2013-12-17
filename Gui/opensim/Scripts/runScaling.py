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
# This example performs a number of scaling steps.
# Modified from script written By David Saxby (Griffith University).

# Folder paths
installDir 		= 	getInstallDir()
modelFolder		=	installDir+"\Models\Gait2354_Simbody" 

# Input Model 
modelName		=	modelFolder+"\gait2354_simbody.osim"
genericModelName=	"gait2354_simbody.osim"	
#Output models
scaledModelName	=	"scaled_gait2392.osim"
movedModelName	=	"moved_gait2392.osim"

# inout xml files
scaleSetup		=	modelFolder+"\subject01_Setup_Scale.xml"
markerSetFile	=	modelFolder+"\gait2354_Scale_MarkerSet.xml"
# ouput xml files
appliedscaleSet	=	modelFolder+"\subject01_scaleSet_applied.xml"
movedMarkers	=	modelFolder+"\subject01_Markers_moved.xml"

# Input data files 
staticMarkerName	=	"subject01_static.trc"
staticMarkers		=	modelFolder+"\subject01_static.trc"
walkingMarkerName	=	"subject01_walk1_ik.trc"
walkingMarkers		=	modelFolder+"\subject01_walk1_ik.trc"
# Output data files
staticCoordinates	=	"staticCoordinates.mot"

# Define some of the subject measurements
subjectMass		=	72.6
subjectHeight	=	1803.4
subjectAge		=	99


## load and define model

# Load a model 
loadModel(modelName)
# Get a handle to the current model
myModel 	= 	getCurrentModel()
#initialize
myModel.initSystem()
myState 	= 	myModel.initSystem()

#	Create a MarkerSet object
newMarkers 	= 	modeling.MarkerSet(markerSetFile)
# 	Replace the markerSet to the model
myModel.replaceMarkerSet(myState,newMarkers)
#	Re-initialize State
myModel.initSystem()
myState 	= 	myModel.initSystem()

## Get time array for .trc file
# Get trc data to determine time range
markerData 	= 	modeling.MarkerData(staticMarkers)
#Get initial and final time 
initial_time= 	markerData.getStartFrameTime()
final_time 	= 	markerData.getLastFrameTime()
# Create an array double and apply the time range
TimeArray=modeling.ArrayDouble()
TimeArray.set(0,initial_time)
TimeArray.set(1,final_time)  

## Scaling Tool
# Create the scale tool object from existing xml
scaleTool 	= 	modeling.ScaleTool(scaleSetup)

# Construct the scale tool from an existing xml
scaleTool.setSubjectMass(subjectMass)
scaleTool.setSubjectHeight(subjectHeight)
scaleTool.setSubjectHeight(subjectAge)

## Inistialize GenericModelMaker
# Tell scale tool to use the loaded model
scaleTool.getGenericModelMaker().setModelFileName(genericModelName)
# Set the Marker Set file (encase a markerset isnt attached to the model)
scaleTool.getGenericModelMaker().setMarkerSetFileName(markerSetFile)

## Initialize & Run ModelScaler
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
scaleTool.getModelScaler().processModel(myState,myModel,path2subject,subjectMass)
