import org.opensim.modeling
import sys

class modelBuilder():
	"""Class to contain model building utilities."""
	@staticmethod
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
	def addBody(model, name):
		body = modeling.Body(name, 1.0, modeling.Vec3(1.0), modeling.Inertia(1, 1, 1, 0, 0, 0))
		model.addBody(body)
		return body

	@staticmethod
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
#
# example usage
# modelCopy = m.clone()
# newBody = modelBuilder.addBody(modelCopy, 'bucket')
# block = modeling.Brick(modeling.Vec3(0.1, 0.2, 0.3))
# modelBuilder.attachGeometryWithOffset(newBody, block)
# modelCopy = modelBuilder.connectBodyWithJoint(modelCopy, 'bucket', 'handle', 'PinJoint')
# loadModel(modelCopy)

