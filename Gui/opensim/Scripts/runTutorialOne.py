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

# This example performs some of the initial steps of Tutorial One

# This line allows for more graceful handling of low level exceptions, just in case
modeling.OpenSimObject.setDebugLevel(3)

# Load model
addModel(getInstallDir()+"/Models/BothLegs/BothLegs.osim")

# Load a motion
loadMotion(getInstallDir()+"/Models/BothLegs/BothLegsWalk.mot")

# Get a handle to the current model
myModel = getCurrentModel()

# Put the model in the default pose
for i in range(myModel.getCoordinateSet().getSize()):
	coord = myModel.getCoordinateSet().get(i)
	setCoordinateValue(coord, coord.getDefaultValue())

# Plot the RF and VASINT fiber lengths with the model in the default pose
plotterPanel = createPlotterPanel("Plot ")
crv1 = addAnalysisCurve(plotterPanel, "fiber-length", "RF", "r_knee_angle")
setCurveLegend(crv1, "RF")
crv2 = addAnalysisCurve(plotterPanel, "fiber-length", "VASINT", "r_knee_angle")
setCurveLegend(crv2, "VASINT")

# Plot the RF and VASINT fiber lengths with the model with the hip flexed to 45 deg
hip_coord = myModel.getCoordinateSet().get("r_hip_flexion")
setCoordinateValueDegrees(hip_coord,45)
crv3 = addAnalysisCurve(plotterPanel, "fiber-length", "RF", "r_knee_angle")
setCurveLegend(crv3, "RF_hip45")
crv4 = addAnalysisCurve(plotterPanel, "fiber-length", "VASINT", "r_knee_angle")
setCurveLegend(crv4, "VASINT_hip45")

# Save the results to file
exportData(plotterPanel, getScriptsDir()+"/testData/RF_VASINT_FiberLengths.sto")
