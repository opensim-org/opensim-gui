import javax.swing as swing
import org.opensim.modeling as modeling

modeling.OpenSimObject.setDebugLevel(3)
osimModel = modeling.Model()
osimModel.setName("tugOfWar")

# GROUND BODY

# Get a reference to the model's ground body
ground = osimModel.getGroundBody()

# Add display geometry to the ground to visualize in the GUI
ground.addDisplayGeometry("ground.vtp")
ground.addDisplayGeometry("anchor1.vtp")
ground.addDisplayGeometry("anchor2.vtp")

# BLOCK BODY
osimModel.print("tug_of_war01.osim")

block = modeling.Body()
block.setName("Block");
block.setMass(20)
block.addDisplayGeometry("block.vtp")
#FreeJoint(String name, Body parent, double[] locationInParent, double[] orientationInParent, Body body, double[] locationInBody, double[] orientationInBody, boolean reverse) {
zeroVec = [0, 0, 0]
jnt = modeling.FreeJoint("blockToGround", ground, zeroVec, zeroVec, block, zeroVec, zeroVec, 0)
jointCoordinateSet = jnt.getCoordinateSet();
jointCoordinateSet.get(0).setRange([-1.5, 1.5])
jointCoordinateSet.get(1).setRange([-1.5, 1.5])
jointCoordinateSet.get(2).setRange([-1.5, 1.5])
jointCoordinateSet.get(3).setRange([-1, 1])
jointCoordinateSet.get(4).setRange([-1, 1])
jointCoordinateSet.get(5).setRange([-1, 1])

osimModel.addBody(block);

osimModel.print("tug_of_war02.osim")

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
# Add muscle1 to model as Force
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

# Add the second muscle to the model
osimModel.addForce(muscle2);

osimModel.print("tug_of_war_muscles.osim")
