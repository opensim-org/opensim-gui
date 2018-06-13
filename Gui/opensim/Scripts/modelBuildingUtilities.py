import org.opensim.modeling

# Define a Body in terms of its inertial parameters and desired
# geometry (optional) and add it to the specified model.
# Arguments:
# 		model: the OpenSim Model to add the Body to
#		 name: the name (string) of the Body to be added
#		 mass: the mass (double) of the Body to be added
#  massCenter: Body center-of-mass (Vec3) in local coordinates
#	  inertia: Body inertia tensor (Inertia) about the mass-center
#		 geom: optional Geometry (Mesh or built-in) to affix to the Body
# return:
# 		Updated model with Body added.
def addBodyToModel(model, name, mass, massCenter, inertia, geom):
	body = modeling.Body(name, mass, massCenter, inertia)
	if (geom != None):
		body.attachGeometry(geom)
	model.addBody(body)
	return model

# Define a PhysicalOffsetFrame in terms of its translational and rotational
# offset with respect to a base (Physical) frame and add it to the model.
# Arguments:
#		model: the OpenSim Model to add the Body to
#  offSetName: the name (string) of the OffsetFrame to be added
#	 baseName: the name (string) of a PhysicalFrame in the model to offset.
#		trans: Translational offset (Vec3) in base frame coordinates
#		  rot: Rotational offset in the base frame as body fixed X-Y-Z Euler angles (Vec3)
# return:
# 		Updated model with PhysicalOffsetFrame added.
def addOffsetFrameToModel(model, offSetName, baseName, trans, rot):
	offset = modeling.PhysicalOffsetFrame()
	offset.setName(offSetName)
	offset.set_translation(trans)
	offset.set_orientation(rot)
	if (model.hasComponent(baseName)):
		b = model.getComponent(baseName)
		bf = modeling.PhysicalFrame.safeDownCast(b)
		offset.connectSocket_parent(bf)
		model.addComponent(offset)
	else:
		print("Base does not exist as a PhysicalFrame. No offset frame was added.")
	return offset

# Define a PinJoint in terms of its parent and child PhysicalFrames
# and add it to the model.
# Arguments:
#		model: the OpenSim Model to add the PinJoint to
#		 name: the name (string) of the PinJoint to be added
#	   parent: the name (string) of a parent (PhysicalFrame) of the Joint.
#	   parent: the name (string) of a child (PhysicalFrame) of the Joint.
# return:
# 		Updated model with the PinJoint added.
def addPinJointToModel(model, name, parent, child):
	p = model.getComponent(parent)
	c = model.getComponent(child)
	p = modeling.PhysicalFrame.safeDownCast(p)
	c = modeling.PhysicalFrame.safeDownCast(c)
	joint = modeling.PinJoint(name, p, c)
	model.addJoint(joint)
	return model

# Add a Geometry to the model with a desired offset with respect to a given
# base frame.
# Arguments:
#		model: the OpenSim Model to add the PinJoint to
#		 geom: optional Geometry (Mesh or built-in) to affix to the Body
#	 baseName: the name (string) of a PhysicalFrame in the model to offset.
#		trans: Translational offset (Vec3) in base frame coordinates
#		  rot: Rotational offset in the base frame as body fixed X-Y-Z Euler angles (Vec3)
# return:
# 		Updated model with the PinJoint added.
def addGeometryWithOffset(model, geom, baseName, trans, rot):
	geomOffsetName = geom.getName() + "_offset";
	offset = addOffsetFrameToModel(model, geomOffsetName, baseName, trans, rot)
	offset.attachGeometry(geom);
	return offset

