# --------------------------------------------------------------------------- #
# OpenSim: runTutorialOne.py                                                  #
# --------------------------------------------------------------------------- #
# OpenSim is a toolkit for musculoskeletal modeling and simulation,           #
# developed as an open source project by a worldwide community. Development   #
# and support is coordinated from Stanford University, with funding from the  #
# U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file     #
# for more information including specific grant numbers.                      #
#                                                                             #
# Copyright (c) 2005-2017 Stanford University and the Authors                 #
# Author(s): Ayman Habib, James Dunne, Kevin Xu                               #
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

## This example performs the steps of Tutorial One in scripting form

# Define the files and folders we will be using
installDir 		= 	getInstallDir()
modelFolder		=	installDir+"\Models\Gait2392_Simbody" 
modelName		=	modelFolder+"\gait2392_simbody.osim"

# Data folder and file names
dataFolder		=	modelFolder+"\Tutorial1"
exampleWalk		=	dataFolder+"\\normal.mot"
normalWalk		=	dataFolder+"\\normal.mot"
crouch1Walk		=	dataFolder+"\crouch1.mot"
crouch2Walk		=	dataFolder+"\crouch2.mot"
crouch3Walk		=	dataFolder+"\crouch3.mot"
crouch4Walk		=	dataFolder+"\crouch4.mot"


# Load the model 
loadModel(modelName)

# Load a motion
loadMotion(exampleWalk)

# Get a handle to the current model
myModel = getCurrentModel()

# Put the model in the default pose
for i in range(myModel.getCoordinateSet().getSize()):
	coord = myModel.getCoordinateSet().get(i)
	setCoordinateValue(coord, coord.getDefaultValue())
	
# Get the number of Joints in the model (different from Dof's)
numberJoints= myModel.getJointSet().getSize()
	
# Get the number of Dof's	
numberCoords= myModel.getNumCoordinates()
	
## Plot fiber length VS Knee Angle	
	
# Plot the RF and VASINT fiber lengths with the model in the default pose
plotterPanel = createPlotterPanel("fiber-length VS Knee Angle ")
crv1 = addAnalysisCurve(plotterPanel, "fiber-length", "rect_fem_r", "knee_angle_r")
setCurveLegend(crv1, "RF")
crv2 = addAnalysisCurve(plotterPanel, "fiber-length", "vas_int_r", "knee_angle_r")
setCurveLegend(crv2, "VASINT")

# Plot the RF and VASINT fiber lengths with the model with the hip flexed to 45 deg
hip_coord = myModel.getCoordinateSet().get("hip_flexion_r")
setCoordinateValueDegrees(hip_coord,45)
crv3 = addAnalysisCurve(plotterPanel, "fiber-length", "rect_fem_r", "knee_angle_r")
setCurveLegend(crv3, "RF_hip45")
crv4 = addAnalysisCurve(plotterPanel, "fiber-length", "vas_int_r", "knee_angle_r")
setCurveLegend(crv4, "VASINT_hip45")


# Save the results to file
exportData(plotterPanel, dataFolder+"\RF_VASINT_FiberLengths.sto")

## Plot Moment Arms VS Knee Angle 

# Reset the model in the default pose
for i in range(myModel.getCoordinateSet().getSize()):
	coord = myModel.getCoordinateSet().get(i)
	setCoordinateValue(coord, coord.getDefaultValue())


# Plot the RF and VASINT knee extension moment arm vs knee angle for 
plotterPanel = createPlotterPanel("Moment Arm: Knee Extension ")
crv1 = addAnalysisCurve(plotterPanel, "momentArm.knee_angle_r", "rect_fem_r", "knee_angle_r")
setCurveLegend(crv1, "RF")
crv1 = addAnalysisCurve(plotterPanel, "momentArm.knee_angle_r", "vas_int_r", "knee_angle_r")
setCurveLegend(crv2, "VASINT")

# Plot the RF and VASINT fiber lengths with the model with the hip flexed to 45 deg
hip_coord = myModel.getCoordinateSet().get("hip_flexion_r")
setCoordinateValueDegrees(hip_coord,45)
crv3 = addAnalysisCurve(plotterPanel, "momentArm.knee_angle_r", "rect_fem_r", "knee_angle_r")
setCurveLegend(crv3, "RF_hip45")
crv4 = addAnalysisCurve(plotterPanel, "momentArm.knee_angle_r", "vas_int_r", "knee_angle_r")
setCurveLegend(crv4, "VASINT_hip45")


# Save the results to file
exportData(plotterPanel, dataFolder+"RF_VASINT_FiberLengths.sto")

## Knee angle during walking

# Create a plotter panel
plotterPanel = createPlotterPanel("Knee angle for Crouch Gait")

# Add data source (File) to the list of sources available to the passed in Plotter Panel
src = addDataSource(plotterPanel, normalWalk)
crv = addCurve(plotterPanel, src, "time", "knee_angle_r")
crv.setLegend("Normal")

# Add data source (File) to the list of sources available to the passed in Plotter Panel
src = addDataSource(plotterPanel, crouch1Walk)
crv = addCurve(plotterPanel, src, "time", "knee_angle_r")
crv.setLegend("crouch")

# Save the results to file
exportData(plotterPanel, dataFolder+"/normal_crouch_KneeAngle.sto")


## Hamstring length over the gait cycle

# Create a plot panel 
plotterPanel = createPlotterPanel("Hamstring length over the Gait Cycle")

# Create a motion object 
motSrc = addMotionSource(plotterPanel, normalWalk)
# Print the muscle length of the semiten during the motion 
crv1  = addMotionCurve(plotterPanel, "Length", "semiten_r", motSrc)
crv1.setLegend("normal")

# Create a motion object 
motSrc = addMotionSource(plotterPanel, crouch1Walk)
crv2  = addMotionCurve(plotterPanel, "Length", "semiten_r", motSrc)
crv2.setLegend("crouch")

# Save the results to file
exportData(plotterPanel, dataFolder+"/normal_crouch_HamstringMuscle-tendonLength.sto")










