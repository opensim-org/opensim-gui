import org.opensim.modeling
import sys

class modelBuilder():
	"""Class to contain model building utilities."""
	@staticmethod
	# Connect a Body in passed in model to Ground with a Joint 
	# Arguments:
	# model: model to be modified
	# bodyName: name of the Body to be connected, any PhysicalFrame already in model should work
	# jointName: name to be given to newly created Joint
	# jointType is one of: 
	# 'PinJoint', 'FreeJoint', 'WeldJoint', 'PlanarJoint', 'SliderJoint', 'UniversalJoint'
	def connectBodyWithJoint(model, bodyName, jointName, jointType):
		c = model.getComponent(bodyName)
		c = modeling.PhysicalFrame.safeDownCast(c)
		p = model.getGround()
		module = sys.modules['org.opensim.modeling']
		JointClass = getattr(module, jointType)
		# Instantiate the class (pass arguments to the constructor, if needed)
		joint = JointClass(jointName, p, c)
		model.addJoint(joint)
		return model

	@staticmethod
	# create a Body with passed in name and add it to passed in model
	# created Body has mass of 1 and inertia (1,1,1,0,0,0)
	#
	def addBody(model, bodyName):
		body = modeling.Body(bodyName, 1.0, modeling.Vec3(1.0), modeling.Inertia(1, 1, 1, 0, 0, 0))
		model.addBody(body)
		return body

	@staticmethod
	# Attach geometry to passed in frame using an offset frame to allow moving geometry around
	def attachGeometryWithOffset(frame, geometry):
		offset = modeling.PhysicalOffsetFrame()
		offset.setName(frame.getName()+ '_offset')
		offset.set_translation(modeling.Vec3(0.))
		offset.set_orientation(modeling.Vec3(0.))
		frame.addComponent(offset);
		bf = modeling.PhysicalFrame.safeDownCast(frame)
		offset.connectSocket_parent(bf)
		offset.attachGeometry(geometry)
		return frame

	@staticmethod
	# Add muscle to current model, muscle name and type are passed in
	# muscleType is one of 'Thelen2003Muscle', 
	def addMuscle(model, muscleName, muscleType):
		module = sys.modules['org.opensim.modeling']
		MuscleClass = getattr(module, muscleType)
		# Instantiate the class (pass arguments to the constructor, if needed)
		muscle = MuscleClass()
		muscle.setName(muscleName);
		muscle.addNewPathPoint("muscle-pt1", model.getGround(), Vec3(0,0,0));
		muscle.addNewPathPoint("muscle-pt2", model.getGround(), Vec3(0,1,0));
		model.addForce(muscle)
#
# example usage
# modelCopy = m.clone()
# newBody = modelBuilder.addBody(modelCopy, 'bucket')
# block = modeling.Brick(modeling.Vec3(0.1, 0.2, 0.3))
# modelBuilder.attachGeometryWithOffset(newBody, block)
# modelCopy = modelBuilder.connectBodyWithJoint(modelCopy, 'bucket', 'handle', 'PinJoint')
# loadModel(modelCopy)

