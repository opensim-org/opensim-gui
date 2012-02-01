import javax.swing as swing
import org.opensim.modeling as modeling
import org.opensim.view.pub as view;
import org.opensim.plotter as plotter;
import org.opensim.utils as utils;

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
osimModel.initSystem()
view.OpenSimDB.getInstance().addModel(osimModel)

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

