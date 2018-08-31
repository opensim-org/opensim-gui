# --------------------------------------------------------------------------- #
# OpenSim: runTutorialTwo.py                                                  #
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

## This example performs the steps of Tutorial Two in scripting form
import os.path

# Define the files and folders we will be using
resourceDir	= getResourcesDir()
modelFolder	= os.path.join(resourceDir, "Models", "WristModel")
modelName	= os.path.join(modelFolder, "wrist.osim")

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
##myModel.setName("Wrist Tendon Surgery.")

## Change the path points of the ECU_pre-surgery to match the existing ECU_post-surgery muscle
ECU_PRE_pps = myModel.getMuscles().get("ECU_pre-surgery").getGeometryPath().updPathPointSet()
ECU_POST_pps= myModel.getMuscles().get("ECU_post-surgery").getGeometryPath().getPathPointSet()
# Clear all path points from the ECU_pre-surgery path point set
ECU_PRE_pps.clearAndDestroy()
# Add path points from the  ECU_post-surgery path to the ECU_pre-surgery path
for i in range(ECU_POST_pps.getSize()):
    ECU_PRE_pps.cloneAndAppend(ECU_POST_pps.get(i))

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
