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

# This example script shows how to create and display a plot window. The script loads the 
# BothLegs OpenSim model adds curves of fiber length for the model. Then, it loads and plots 
# data from a storage file, which contains fiber lengths for the model Subject01_simbody
# that is included with the OpenSim distribution

# Load the model BothLegs.osim
filepath = getInstallDir() + "/Models/Gait2392_simbody/gait2392_simbody.osim";
loadModel(filepath);

# Create a plotter panel and set the title
plotterPanel = createPlotterPanel("Plot Example")

# Add curves showing rectus femoris and vasti fiber lengths vs. right knee angle and set the legend
crv1 = addAnalysisCurve(plotterPanel, "fiber-length", "rect_fem_r", "knee_angle_r")
crv2 = addAnalysisCurve(plotterPanel, "fiber-length", "vas_int_r", "knee_angle_r")
crv1.setLegend("RF_BothLegs")
crv2.setLegend("VASINT_BothLegs")

# Load data from an external data source and plot
src = addDataSource(plotterPanel, getScriptsDir()+"/GUI_Scripting/testData/Subject01_FiberLengths.sto")
crv3 = addCurve(plotterPanel, src, "knee_angle_r", "rect_fem_r")
crv4 = addCurve(plotterPanel, src, "knee_angle_r", "vas_int_r")
crv3.setLegend("RF_Subject01")
crv4.setLegend("VASINT_Subject01") 

crv5 =  addAnalysisCurve(plotterPanel, "MomentArm.r_knee_angle", "rect_fem_r", "r_knee_angle")

# Change the color of the first curve in the plot
setCurveColor(plotterPanel, 0, 0.0, 1.0, 1.0)

#Arbitrary OpenSim Function, name of curve is same as name of Function
threshold =  modeling.Constant(0.18)
threshold.setName('threshold')
cv6 = addFunctionCurve(plotterPanel, threshold)

# Changing the axes labels
plotterPanel.setXAxisLabel("Knee angle (deg)");
plotterPanel.setYAxisLabel("Fiber Length (m)");
# Use this for a quick way to set labels for both axes.
# plotterPanel.setAxesLabels("Knee angle (deg)","Fiber length (mm)")

# Export the data in the plotter window to a file
exportData(plotterPanel, getScriptsDir()+"/GUI_Scripting/testData/cvs_export.sto")
