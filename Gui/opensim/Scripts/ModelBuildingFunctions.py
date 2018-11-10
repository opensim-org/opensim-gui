import org.opensim.modeling
import sys

"""Use the Model Building functions to augment a copy of the current model
   or to build a model from scratch in the GUI scripting shell.
   
   This file provides convenenient model building functions which invoke
   related API calls and supply default property values. The API requires 
   component attributes to be specified: e.g. mass, Inertia, location vectors,
   ..., of specific types that can be cumbersome to translate between
   scripting and the C++ API. Instead, you can use these functions and
   edit the related properties in the GUI's property editor.
   """

def addBodyToModel(model, bodyName):
	"""Create a Body, with the provided bodyName, and add it to the
	provided model.
	The Body has a mass of 1 and inertia (0.1, 0.1, 0.1, 0,0,0) with
	the mass center at the origin of the Body frame.
	If a body with the same name already exists in the model, then this
	body will be automatically renamed, and a warning will appear in the
	Scripting Shell.

	This function returns the new body added to the model.
	"""
	body = modeling.Body(bodyName, 1.0, modeling.Vec3(0.0),
			modeling.Inertia(0.1, 0.1, 0.1, 0, 0, 0))
	model.addBody(body)
	return body

def connectBodyWithJoint(model, parentFrame, childOnBody, jointName, jointType):
	"""Connect a Body in the model according to specified parent and child
	(on Body) frames by a joint of the specified joint type.
	Arguments:
	model: model to be modified.
	parentFrame: the Body (or affixed Frame) to be connected as the parent frame;
				 any PhysicalFrame already in the model is suitable.
	childOnBody: the child Body (or offset) Frame to be connected as the child frame;
				 can be any PhysicalFrame that is not the parent Frame.
	jointName:   name to be given to the newly-created Joint.
	jointType is one of: 
		'PinJoint', 'FreeJoint', 'WeldJoint', 'PlanarJoint', 'SliderJoint',
		'UniversalJoint'
	returns the Joint added to connect the Body to the model
	"""
	validJointTypes = [
			'PinJoint',
			'FreeJoint',
			'WeldJoint',
			'PlanarJoint',
			'SliderJoint',
			'UniversalJoint',
			]
	if not jointType in validJointTypes:
		raise Exception('Provided jointType %s is not valid.' %
				jointType)
	module = sys.modules['org.opensim.modeling']
	JointClass = getattr(module, jointType)
	# Instantiate the user-requested Joint class.
	joint = JointClass(jointName, parentFrame, childOnBody)
	model.addJoint(joint)
	return joint

def addOffsetToFrame(baseFrame, offSetName, trans=None, rot=None):
	""" Define a PhysicalOffsetFrame in terms of its translational and rotational
		offset with respect to a base (Physical) frame and add it to the model.
		Arguments:
			baseFrame: the PhysicalFrame in the model to offset.
		   offSetName: the name (string) of the OffsetFrame to be added
				trans: Translational offset (Vec3) in base frame coordinates
				  rot: Rotational offset in the base frame as body fixed X-Y-Z Euler angles (Vec3)
		return:
			offset: the PhysicalOffsetFrame added to the model
	"""
	offset = modeling.PhysicalOffsetFrame()
	offset.setName(offSetName)
	if not trans is None:
		offset.set_translation(trans)
		
	if not rot is None:
		offset.set_orientation(rot)
		
	if (model.hasComponent(baseFrame.getAbsolutePathString())):
		offset.connectSocket_parent(baseFrame)
		baseFrame.addComponent(offset)
	else:
		print("baseFrame does not exist as a PhysicalFrame. No offset frame was added.")
	return offset

def attachGeometryWithOffset(frame, geometry):
	"""Attach geometry to provided frame. This function adds an
	intermediate offset frame between the geometry and the provided frame
	to permit editing its properties to move the geometry around.
	
	This function returns the intermediate offset frame so that you can
	modify its location and orientation.
	"""
	offset = modeling.PhysicalOffsetFrame()
	offset.setName(frame.getName() + '_offset')
	offset.set_translation(modeling.Vec3(0.))
	offset.set_orientation(modeling.Vec3(0.))
	frame.addComponent(offset);
	bf = modeling.PhysicalFrame.safeDownCast(frame)
	offset.connectSocket_parent(bf)
	offset.attachGeometry(geometry)
	return offset

def addMuscleToModel(model, muscleName, muscleType):
	"""Add a muscle to the model with the provided muscleName and
	specified muscleType, which is either 'Thelen2003Muscle' or
	'Millard2012EquilibriumMuscle'.
	Returns the muscle that was added to the model.
	"""
	validMuscleTypes = [
			'Thelen2003Muscle',
			'Millard2012EquilibriumMuscle'
			]
	if not muscleType in validMuscleTypes:
		raise Exception('Provided muscleType %s is not valid.' %
				muscleType)
	module = sys.modules['org.opensim.modeling']
	MuscleClass = getattr(module, muscleType)
	# Instantiate the requested muscle class.
	muscle = MuscleClass()
	muscle.setName(muscleName);
	muscle.addNewPathPoint("muscle-pt1", model.getGround(),
			modeling.Vec3(0,0,0));
	muscle.addNewPathPoint("muscle-pt2", model.getGround(),
			modeling.Vec3(0,0.5,0));
	model.addForce(muscle)
	return muscle

""" Here is an example of copying the GUI's current model, adding components to
    the copied model, and loading the copied model in the GUI"""
#   modelCopy = getCurrentModel().clone()
#   ball = addBodyToModel(modelCopy, 'ball')
#   sphere = modeling.Sphere(0.15)
#   attachGeometryWithOffset(newBody, sphere)
#   handle = connectBodyWithJoint(modelCopy, ball, 'handle', 'SliderJoint')
#    loadModel(modelCopy)

