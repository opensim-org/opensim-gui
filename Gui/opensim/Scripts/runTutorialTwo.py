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

# This example performs the steps of Tutorial Two in scripting form
# Written by: James Dunne (2013)

# Define the files and folders we will be using
installDir 		= 	getInstallDir()
modelFolder		=	installDir+"\Models\WristModel" # Change to your locale path
modelName		=	modelFolder+"\wrist.osim"


# Load the model 
loadModel(modelName)

# Get a handle to the current model
oldModel = getCurrentModel()

# Create a fresh copy
myModel = modeling.Model(oldModel)

# Initialize the copy, if values needed to be set in state
# pass along the variable myState returned by initSystem
myState = myModel.initSystem()

# Change the name of the model
myModel.setName("Wrist - Tendon Surgery.")


## Change the path points of the ECU_pre-surgery to match the existing ECU_post-surgery muscle 

# Need to find the path points of the ECU muscle post surgery

for i in range(myModel.getMuscles().get("ECU_post-surgery").getGeometryPath().getPathPointSet().getSize()):
	# find the path point information for ECU_post-surgery (Body and location (in body) of PathPoint
	ECUPostpathPoint			=	myModel.getMuscles().get("ECU_post-surgery").getGeometryPath().getPathPointSet().get(i)
	ECUPostpathPoint_body		=	ECUPostpathPoint.getBody()
	ECUPostpathPoint_location	=	ECUPostpathPoint.getLocation()
	
	# Delete existing path point from ECU_pre-surgery
	if myModel.getMuscles().get("ECU_pre-surgery").getGeometryPath().canDeletePathPoint(i):
		myModel.getMuscles().get("ECU_pre-surgery").getGeometryPath().deletePathPoint(myState,i)
	
	# Create new path points for ECU_pre-surgery based on ECU_post-surgery data
	myModel.getMuscles().get("ECU_pre-surgery").getGeometryPath().addPathPoint(myState,i,ECUPostpathPoint_body)
	ECUpathPoint	=	myModel.getMuscles().get("ECU_pre-surgery").getGeometryPath().getPathPointSet().get(i)
	ECUpathPoint.setLocation(myState,ECUPostpathPoint_location)

# re-initialize the model now that you changed the path points	
myState = myModel.initSystem()
# Get full path name of myModel
fullPathName = myModel.getInputFileName()
# Change the name of the modified model
newName = fullPathName.replace('.osim', '_edited.osim')
myModel.print(newName)
# Load the model in the GUI
loadModel(newName)
	
	

## IV. Biomechanical Effects of Tendon Transfer 

loadModel(fullPathName)
currentModel = getCurrentModel()
myState = currentModel.initSystem()
# Plot the RF and VASINT fiber lengths with the model in the default pose
plotterPanel = createPlotterPanel("Wrist Deviation Moment vs. Deviation Angle. ")
crv1 = addAnalysisCurve(plotterPanel, "moment.flexion", "ECRB+ECRL+ECU_pre-surgery+EDCI+EDCL+EDCM+EDCR+EDM+EIP+EPL","flexion")
setCurveLegend(crv1, "Before Transfer")
crv2 = addAnalysisCurve(plotterPanel, "moment.flexion", "ECRB+ECRL+ECU_post-surgery+EDCI+EDCL+EDCM+EDCR+EDM+EIP+EPL","flexion")
setCurveLegend(crv2, "After Transfer")

## Effect of Tendon transfer on ECU muscle

# Wrist Moment VS Flexion
plotterPanel = createPlotterPanel("Wrist Moment VS Flexion Angle")
crv1 = addAnalysisCurve(plotterPanel, "moment.flexion", "ECU_pre-surgery","flexion")
setCurveLegend(crv1, "Pre-Surgery")
crv2 = addAnalysisCurve(plotterPanel, "moment.flexion", "ECU_post-surgery","flexion")
setCurveLegend(crv2, "post-surgery")


# Tendon force VS Flexion
plotterPane2 = createPlotterPanel("Tendon force VS Flexion Angle")
crv1 = addAnalysisCurve(plotterPane2, "tendon force", "ECU_pre-surgery","flexion")
setCurveLegend(crv1, "Pre-Surgery")
crv2 = addAnalysisCurve(plotterPane2, "tendon force", "ECU_post-surgery","flexion")
setCurveLegend(crv2, "post-surgery")


# flexion moment arm VS Flexion
plotterPane3 = createPlotterPanel("flexion moment arm VS Flexion Angle")
crv1 = addAnalysisCurve(plotterPane3, "momentArm.flexion", "ECU_pre-surgery","flexion")
setCurveLegend(crv1, "Pre-Surgery")
crv2 = addAnalysisCurve(plotterPane3, "momentArm.flexion", "ECU_post-surgery","flexion")
setCurveLegend(crv2, "post-surgery")


# Create muscle objects for both a ECU pre- & post- surgery 
ECUpresurgery 	=	 myModel.getMuscles().get("ECU_pre-surgery")
ECUpostsurgery 	=	 myModel.getMuscles().get("ECU_post-surgery")

# Find the optimal fibre length of that muscle
optLengthECUpre 	=	ECUpresurgery.getOptimalFiberLength()
optLengthECUpost 	=	ECUpostsurgery.getOptimalFiberLength()

 
##  The Effect of Tendon Slack Length 
myModel		= 	getCurrentModel()

# Plot the muscle properties with existing Tendon-slack Length
# Tendon force VS Flexion
plotterPane1 = createPlotterPanel("Tendon force VS Flexion Angle")
crv1 = addAnalysisCurve(plotterPane1, "tendon force", "ECRB","flexion")
setCurveLegend(crv1, "ECRB")
# Muscle-tendon length VS Flexion
plotterPane2 = createPlotterPanel("Muscle-tendon length VS Flexion Angle")
crv2 = addAnalysisCurve(plotterPane2, "muscle-tendon length", "ECRB","flexion")
setCurveLegend(crv2, "ECRB")
# Fibre length VS Flexion
plotterPane3 = createPlotterPanel("Fibre length VS Flexion Angle")
crv3 = addAnalysisCurve(plotterPane3, "fiber-length", "ECRB","flexion")
setCurveLegend(crv3, "ECRB")


# Changing the optimal fibre length
# Create the ECRB muscle object
ECRB 						=	myModel.getMuscles().get("ECRB")
# Back up the original tendon slack length (just in case)
backupTendonSlackLength		=	ECRB.getTendonSlackLength()
# Prescribe a new Tendon slack length
ECRB.setTendonSlackLength(0.2105)
# Re-initialize the states
myModel.initSystem()

# Plot the muscle properties with new Tendon-slack Length
# Tendon force VS Flexion
crv4 = addAnalysisCurve(plotterPane1, "tendon force", "ECRB","flexion")
setCurveLegend(crv4, "ECRB_0.210")
# Muscle-tendon length VS Flexion
crv5 = addAnalysisCurve(plotterPane2, "muscle-tendon length", "ECRB","flexion")
setCurveLegend(crv5, "ECRB_0.210")
# Fibre length VS Flexion
crv6 = addAnalysisCurve(plotterPane3, "fiber-length", "ECRB","flexion")
setCurveLegend(crv6, "ECRB_0.210")














