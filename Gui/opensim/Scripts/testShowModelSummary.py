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

#
# Author(s): Ayman Habib
# Stanford University
#
# This example script creates a standalone dialog window to 
# display information about the current model (name, number of 
# bodies, and number of joints). This script only works if a model 
# is currently loaded

# import needed packages
# swing is the generic GUI kit for Java, allows for I/O and creating windows if needed
import javax.swing as swing

# create a window
win = swing.JFrame("Welcome")

# access the current model
model = getCurrentModel()

# if there is no loaded model output message in window
if not model:
    win.getContentPane().add(swing.JLabel("No models"))
    win.setTitle("Example Window")
# otherwise display model summary statistics
else:
	# set the window layout
    win.getContentPane().setLayout(swing.BoxLayout(win.getContentPane(), 1))
	
	# display the model name
    dLabel = swing.JLabel("Model name: "+model.getName())
    win.add(dLabel)
	
	# display the number of bodies
    numBodiesString = "Number of bodies ="+lang.String.valueOf(model.getNumBodies())
    bLabel = swing.JLabel(numBodiesString)
    win.add(bLabel)
	
	#display the number of joints
    numJointsString = "Number of Joints ="+lang.String.valueOf(model.getNumJoints())
    njLabel = swing.JLabel(numJointsString)
    win.add(njLabel)
	
# Set title and size of the window
win.setTitle("Example Window")	
win.size = (200, 100)

# Display the window
win.show()