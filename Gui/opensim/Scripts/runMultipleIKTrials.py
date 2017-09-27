# --------------------------------------------------------------------------- #
# OpenSim: runMultipleIKTrials.py                                             #
# --------------------------------------------------------------------------- #
# OpenSim is a toolkit for musculoskeletal modeling and simulation,           #
# developed as an open source project by a worldwide community. Development   #
# and support is coordinated from Stanford University, with funding from the  #
# U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file     #
# for more information including specific grant numbers.                      #
#                                                                             #
# Copyright (c) 2005-2017 Stanford University and the Authors                 #
# Author(s): Ayman Habib, Kevin Xu, Edith Arnold                              #
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


# Author(s): Ayman Habib, Edith Arnold
# Stanford University
#
# This example script runs multiple inverse kinematics trials for the leg39 model. To see the results 
# load the model and ik output in the GUI.
# The script prompts the user for input files.

# Import needed packages
# File input and output with native python os package
import os

# Utils contains tools to browse for files and folders
import org.opensim.utils as utils

# Browse for Directory containing trc files to process

print "Acquiring folders with marker data"

trcDataFolder = "thisdirectorydoesnotexist";
# Uncomment the following line to manually assign directory.
trcDataFolder = getScriptsDir()+"/GUI_Scripting/testData/Subject01/Marker_Data/"

# Prompts user to select directory if above does not exist
if not os.path.exists(trcDataFolder):
	trcDataFolder = utils.FileUtils.getInstance().browseForFolder("Select the folder that contains trc files");
print trcDataFolder

# Load the setup file
print "Acquiring setup file"

# Prompts user to select file if above does not exist
setupFile = getScriptsDir()+"/GUI_Scripting/testData/Subject01/Generic_Setup_leg39_InverseKinematics.xml"
if not os.path.exists(setupFile):
	setupFile = utils.FileUtils.getInstance().browseForFilename(".xml", "Select the setup file for the IK tool", 1)
print setupFile

# Select folder to store output results
print "Acquiring folder to store the IK results output files"

resultsFolder = getScriptsDir()+"/GUI_Scripting/testData/Subject01/IK_Results/"
if not os.path.exists(resultsFolder):
	resultsFolder = utils.FileUtils.getInstance().browseForFolder("Select the folder to store the IK results output files");
print resultsFolder

# Initialize InverseKinematicsTool from setup file
print "Creating InverseKinematicsTool"
ikTool = modeling.InverseKinematicsTool(setupFile)

# Load the model to be used and and initialize
print "Acquiring model"
modelFile = getScriptsDir()+"/GUI_Scripting/testData/Subject01/Subject01.osim"
if not os.path.exists(modelFile):
	modelFile = utils.FileUtils.getInstance().browseForFilename(".osim", "Select the Model", 1)

model = modeling.Model(modelFile)

print "Creating and initializing model"
model.initSystem()

# Display model
loadModel(modelFile)

# Tell the IK tool to use the model
ikTool.setModel(model)

# Cycle thru the trc files in trcDataFolder, set each as markerDataFileName
# change corresponding output file and repeat
      
for filename in os.listdir(trcDataFolder): 
    if filename.endswith(".trc"):

		# Set name of output motion
		print "Processing "+filename;
		outFileName = filename.replace('.trc', '_ik.sto')
		trialSetupFileName = filename.replace('.trc', 'ik_setup.xml')

		# Set name of input trc file and output motion in tool
		ikTool.setMarkerDataFileName(trcDataFolder+'\\'+filename)
		ikTool.setOutputMotionFileName(resultsFolder+'\\'+outFileName)

		# Use the trc file to get the start and end times
		markerData = modeling.MarkerData(trcDataFolder+'\\'+filename)
		ikTool.setStartTime(markerData.getStartFrameTime())
		ikTool.setEndTime(markerData.getLastFrameTime())

		# Run the tool
		print "Runnning trial file "+ikTool.getMarkerDataFileName()+" Output:"+ikTool.getOutputMotionFileName()
		ikTool.run()
		print "Finished processing trial "+filename

		# Save the setup file
		trialSetupFileName = filename.replace('.trc', '_IK_Setup.xml')
		ikTool.print(resultsFolder+'\\'+trialSetupFileName)
