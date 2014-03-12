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

# Author(s): Ayman Habib, Edith Arnold
# Stanford University
#
# This example script shows how to create and display a plot window. The script loads the 
# BothLegs OpenSim model adds curves of fiber length for the model. Then, it loads and plots 
# data from a storage file, which contains fiber lengths for the model Subject01_simbody
# that is included with the OpenSim distribution

# Load the model BothLegs.osim
filepath = getInstallDir() + "/Models/Gait2392_Simbody/gait2392_simbody.osim";
loadModel(filepath);

# Create a plotter panel and set the title
plotterPanel = createPlotterPanel("Plot Example")

motionfilePath = getInstallDir() + "/Models/Gait2392_Simbody/subject01_walk1.mot"
motSrc = addMotionSource(plotterPanel, motionfilePath)

# Plot MomentArm of rect_fem_r, vas_int_r about r_knee_angle through the motion
crv1 = addMotionCurve(plotterPanel, 'momentArm.knee_angle_r', 'rect_fem_r', motSrc)
crv2 = addMotionCurve(plotterPanel, 'Length', 'rect_fem_r', motSrc)
# addMotionCurve(plotterPanel, 'momentArm.knee_angle_r', 'vas_int_r', motSrc)

crv1.setLegend("Moment Arm")
crv2.setLegend("Muscle-Tendon Length")