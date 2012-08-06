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

# This line allows for more graceful handling of low level exceptions, just in case
modeling.OpenSimObject.setDebugLevel(3)

# Load the model BothLegs.osim
addModel(getScriptsDir()+"/testData/BothLegs.osim")

# Create a plotter panel and set the title
plotterPanel = createPlotterPanel("Plot Example")

# Add curves showing rectus femoris and vasti fiber lengths vs. right knee angle and set the legend
crv1 = addAnalysisCurve(plotterPanel, "fiber-length", "RF", "r_knee_angle")
crv2 = addAnalysisCurve(plotterPanel, "fiber-length", "VASINT", "r_knee_angle")
crv1.setLegend("RF_BothLegs")
crv2.setLegend("VASINT_BothLegs")

# Load data from an external data source and plot
src = addDataSource(plotterPanel, getScriptsDir()+"/testData/Subject01_FiberLengths.sto")
crv3 = addCurve(plotterPanel, src, "knee_angle_r", "rect_fem_r")
crv4 = addCurve(plotterPanel, src, "knee_angle_r", "vas_int_r")
crv3.setLegend("RF_Subject01")
crv4.setLegend("VASINT_Subject01") 

# Change the color of the first curve in the plot
setCurveColor(plotterPanel, 0, 0.0, 1.0, 1.0)

# Export the data in the plotter window to a file
exportData(plotterPanel, getScriptsDir()+"/testData/cvs_export.sto")
