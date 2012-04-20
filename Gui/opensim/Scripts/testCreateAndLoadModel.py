# import needed packages
# swing is the generic GUI kit for Java, allows for I/O and creating windows if needed
# modeling is the wrapping of the OpenSim API, so OpenSim::Model would be referenced here as modeling.Model
# view is some module in the GUI that exposes convenient inetrfaces for scripting
# lang is Java language core libraries e.g String, Math, ..

import javax.swing as swing
import org.opensim.modeling as modeling
import org.opensim.view.pub as view
import java.lang as lang

# This line allows for more graceful handling of low level exceptions, just in case
modeling.OpenSimObject.setDebugLevel(3)

# create a blank OpenSim::Model object
osimModel = modeling.Model()
osimModel.setName("tugOfWar")

# GROUND BODY
# Get a reference to the model's ground body
ground = osimModel.getGroundBody()

# Add display geometry to the ground to visualize in the GUI
ground.addDisplayGeometry("ground.vtp")
ground.addDisplayGeometry("anchor1.vtp")
ground.addDisplayGeometry("anchor2.vtp")

# print model to file in case we want to load it in the application
osimModel.print("tug_of_war01.osim")

# BLOCK BODY
block = modeling.Body()
block.setName("Block");
block.setMass(20)
block.addDisplayGeometry("block.vtp")

# Create a FreeJoint connecting Block and ground
# FreeJoint(String name, Body parent, double[] locationInParent, double[] orientationInParent, Body body, double[] locationInBody, double[] orientationInBody, boolean reverse) {
zeroVec = [0, 0, 0]
blockSideLength = 0.1
locationInParent = [0, blockSideLength/2, 0]
jnt = modeling.FreeJoint("blockToGround", ground, locationInParent, zeroVec, block, zeroVec, zeroVec, 0)
jointCoordinateSet = jnt.getCoordinateSet();

# set bounds on coordinates
angleRange = [-lang.Math.PI/2, lang.Math.PI/2]
positionRange = [-1, 1]
jointCoordinateSet.get(0).setRange(angleRange)
jointCoordinateSet.get(1).setRange(angleRange)
jointCoordinateSet.get(2).setRange(angleRange)
jointCoordinateSet.get(3).setRange(positionRange)
jointCoordinateSet.get(4).setRange(positionRange)
jointCoordinateSet.get(5).setRange(positionRange)

# add Body to list of bodies
osimModel.addBody(block);

#Now setup attributes for the two muscles
maxIsometricForce = 1000.0
optimalFiberLength = 0.1
tendonSlackLength = 0.2
pennationAngle = 0.0

# Create new muscles
muscle1 = modeling.Thelen2003Muscle()
muscle1.setName("muscle1")
muscle1.setMaxIsometricForce(maxIsometricForce)
muscle1.setOptimalFiberLength(optimalFiberLength)
muscle1.setTendonSlackLength(tendonSlackLength);
muscle1.setPennationAngleAtOptimalFiberLength(pennationAngle)

# Path for muscle 1
muscle1.addNewPathPoint("muscle1-point1", ground, modeling.ArrayDouble.createVec3([0.0,0.05,-0.35]))
muscle1.addNewPathPoint("muscle1-point2", block, modeling.ArrayDouble.createVec3([0.0,0.0,-0.05]))

osimModel.addForce(muscle1);

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
osimModel.addForce(muscle2)

osimModel.print("tug-of-war.osim")

#Set up Controller
initialTime = 0.0
finalTime = 4.0

muscleController = modeling.PrescribedController()
muscleController.setName("LinearRamp Controller")
muscleController.setActuators(osimModel.updActuators())
# Define linear functions for the control values for the two muscles
slopeAndIntercept1=modeling.ArrayDouble(0.0, 2)
slopeAndIntercept2=modeling.ArrayDouble(0.0, 2)
# muscle1 control has slope of -1 starting 1 at t = 0
slopeAndIntercept1.setitem(0, -1.0/(finalTime-initialTime))
slopeAndIntercept1.setitem(1,  1.0)
# muscle2 control has slope of 1 starting 0.05 at t = 0
slopeAndIntercept2.setitem(0, 1.0/(finalTime-initialTime))
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
muscle2.setDefaultFiberLength(0.1)
muscle1.setDefaultFiberLength(0.1)

# Make sure when osimModel is deleted it doesn't try to delete
# objects owned by the interpreter
osimModel.disownAllComponents()

osimModel.print("tug_of_war_muscles_controller.osim")

# Add model to GUI thru the file and not using the live model 
# since the scripting shell owns the memory of osimModel and will free it anytime
gui.addModel("tug_of_war_muscles_controller.osim")


