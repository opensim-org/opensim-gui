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

# This example script creates a model similar to the TugofWar API example.
# Two muscles are created and attached to a block. Linear controllers are
# defined for the muscles.
# Once the model is created the forward tool in the GUI is called to run
# a forward simulation of the "tug of war". Hit play to watch the simulated motion.

# Import needed packages
import org.opensim.tracking as tools

# This line allows for more graceful handling of low level exceptions, just in case
modeling.OpenSimObject.setDebugLevel(3)

# Create a blank OpenSim::Model object
osimModel = modeling.Model()
osimModel.setName("tugOfWar")

# GROUND BODY
# Get a reference to the model's ground body
ground = osimModel.getGroundBody()

# Add display geometry to the ground to visualize in the GUI
ground.addDisplayGeometry("ground.vtp")
ground.addDisplayGeometry("anchor1.vtp")
ground.addDisplayGeometry("anchor2.vtp")

# "BLOCK" BODY
# Specify properties of a 20kg point mass, centered at the middle of 0.1 m^3 massless block
block = modeling.Body()
block.setName("Block")
block.setMass(20);
# Set COM : block.setMassCenter(modeling.ArrayDouble.createVec3([0,0,0])
# Need to set inertia
block.addDisplayGeometry("block.vtp")

# Create a FreeJoint connecting Block and ground
zeroVec = [0, 0, 0]
zeroVec3 = modeling.ArrayDouble.createVec3(zeroVec)
blockSideLength = 0.1
locationInParent = modeling.ArrayDouble.createVec3([0, blockSideLength/2, 0])
blockToGround = modeling.FreeJoint("blockToGround", ground, locationInParent, zeroVec3, block, zeroVec3, zeroVec3, 0)
jointCoordinateSet = blockToGround.getCoordinateSet()

# Set gravity as 0 since there is no ground contact model
osimModel.setGravity(zeroVec3)

# Set bounds on coordinates
angleRange = [-lang.Math.PI/2, lang.Math.PI/2]
positionRange = [-1, 1]
jointCoordinateSet.get(0).setRange(angleRange)
jointCoordinateSet.get(1).setRange(angleRange)
jointCoordinateSet.get(2).setRange(angleRange)
jointCoordinateSet.get(3).setRange(positionRange)
jointCoordinateSet.get(4).setRange(positionRange)
jointCoordinateSet.get(5).setRange(positionRange)

# Add Body to the model
osimModel.addBody(block)

#Now setup attributes for the two muscles
maxIsometricForce = 1000.0
optimalFiberLength = 0.25
tendonSlackLength = 0.1
pennationAngle = 0.0

# Create new muscles
muscle1 = modeling.Thelen2003Muscle()
muscle1.setName("muscle1")
muscle1.setMaxIsometricForce(maxIsometricForce)
muscle1.setOptimalFiberLength(optimalFiberLength)
muscle1.setTendonSlackLength(tendonSlackLength)
muscle1.setPennationAngleAtOptimalFiberLength(pennationAngle)

# Path for muscle 1
muscle1.addNewPathPoint("muscle1-point1", ground, modeling.ArrayDouble.createVec3([0.0,0.05,-0.35]))
muscle1.addNewPathPoint("muscle1-point2", block, modeling.ArrayDouble.createVec3([0.0,0.0,-0.05]))

# Repeat for Muscle 2
muscle2 = modeling.Thelen2003Muscle()
muscle2.setName("muscle2")
muscle2.setMaxIsometricForce(maxIsometricForce)
muscle2.setOptimalFiberLength(optimalFiberLength)
muscle2.setTendonSlackLength(tendonSlackLength)
muscle2.setPennationAngleAtOptimalFiberLength(pennationAngle)

# Path for muscle 2
muscle2.addNewPathPoint("muscle2-point1", ground, modeling.ArrayDouble.createVec3([0.0,0.05,0.35]))
muscle2.addNewPathPoint("muscle2-point2", block, modeling.ArrayDouble.createVec3([0.0,0.0,0.05]))

# Add the two muscles (as forces) to the model
osimModel.addForce(muscle1)
osimModel.addForce(muscle2)

#Set up Controller
initialTime = 0.0
finalTime = 3.0
muscleController = modeling.PrescribedController()
muscleController.setName("LinearRamp Controller")
muscleController.setActuators(osimModel.updActuators())

# Define linear functions for the control values for the two muscles
slopeAndIntercept1 = modeling.ArrayDouble(0.0, 2)
slopeAndIntercept2 = modeling.ArrayDouble(0.0, 2)

# muscle1 control has slope of -1 starting 1 at t = 0
slopeAndIntercept1.setitem(0, -1.0/(finalTime-initialTime))
slopeAndIntercept1.setitem(1,  1.0)

# muscle2 control has slope of 0.95 starting 0.05 at t = 0
slopeAndIntercept2.setitem(0, 0.95/(finalTime-initialTime))
slopeAndIntercept2.setitem(1,  0.05)

# Set the indiviudal muscle control functions for the prescribed muscle controller
muscleController.prescribeControlForActuator("muscle1", modeling.LinearFunction(slopeAndIntercept1))
muscleController.prescribeControlForActuator("muscle2", modeling.LinearFunction(slopeAndIntercept2))

# Add the control set controller to the model
osimModel.addController(muscleController)

# Define the default states for the two muscles
# Activation
muscle1.setDefaultActivation(slopeAndIntercept1.getitem(1))
muscle2.setDefaultActivation(slopeAndIntercept2.getitem(1))

# Fiber length
fiberLength0 = 0.1
muscle2.setDefaultFiberLength(fiberLength0)
muscle1.setDefaultFiberLength(fiberLength0)

# Make sure when osimModel is deleted it doesn't try to delete
# objects owned by the interpreter
osimModel.disownAllComponents()

# Save the model to a file
osimModel.print(getScriptsDir()+"/testData/tug_of_war_muscles_controller.osim")

# Add model to GUI 
addModel(getScriptsDir()+"/testData/tug_of_war_muscles_controller.osim")

# Run a forward simulation
guiForwardTool = tools.ForwardToolModel(getCurrentModel())
guiForwardTool.execute()
