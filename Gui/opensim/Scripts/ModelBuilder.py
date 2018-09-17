import org.opensim.modeling
import sys

class ModelBuilder(object):
    """This class contains model building utilities."""
    @staticmethod
    def connectBodyWithJoint(model, bodyName, jointName, jointType):
        """Connect a Body in a model to Ground with a new Joint.
        Arguments:
        model: model to be modified.
        bodyName: name of the Body to be connected; any PhysicalFrame already
            in model should work.
        jointName: name to be given to the newly-created Joint.
        jointType is one of: 
            'PinJoint', 'FreeJoint', 'WeldJoint', 'PlanarJoint', 'SliderJoint',
            'UniversalJoint'
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
        if not model.hasComponent(bodyName):
            raise Exception('Body ' + bodyName + ' does not exist in model ' +
                    model.getName() + '. Ignoring.')
        c = model.getComponent(bodyName)
        c = modeling.PhysicalFrame.safeDownCast(c)
        p = model.getGround()
        module = sys.modules['org.opensim.modeling']
        JointClass = getattr(module, jointType)
        # Instantiate the user-requested Joint class.
        joint = JointClass(jointName, p, c)
        model.addJoint(joint)

    @staticmethod
    def addBody(model, bodyName):
        """Create a Body, with the provided bodyName, and add it to the
        provided model.
        The Body has a mass of 1 and inertia (1,1,1,0,0,0).
        If a body with the same name already exists in the model, then this
        body will be automatically renamed, and a warning will appear in the
        Scripting Shell.

        This function returns the new body added to the model.
        """
        body = modeling.Body(bodyName, 1.0, modeling.Vec3(1.0),
                modeling.Inertia(1, 1, 1, 0, 0, 0))
        model.addBody(body)
        return body

    @staticmethod
    def attachGeometryWithOffset(frame, geometry):
        """Attach geometry to provided frame. This function adds an
        intermediate offset frame between the provided frame to allow
        moving geometry around.
        
        This function returns the intermediate offset frame so that you can
        modify its location and orientation.
        """
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
    def addMuscle(model, muscleName, muscleType):
        """Add a muscle to the provided model, with the provided muscleName.
        muscleType is either 'Thelen2003Muscle' or
        'Millard2012EquilibriumMuscle'.
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
                modeling.Vec3(0,1,0));
        model.addForce(muscle)

# Here is an example of copying the GUI's current model, adding components to
# the copied model using ModelBuilder, and loading the copied model in the GUI.
#
#    modelCopy = getCurrentModel().clone()
#    newBody = ModelBuilder.addBody(modelCopy, 'ball')
#    sphere = modeling.Sphere(0.15)
#    ModelBuilder.attachGeometryWithOffset(newBody, sphere)
#    ModelBuilder.connectBodyWithJoint(modelCopy, 'ball', 'handle', 'SliderJoint')
#    loadModel(modelCopy)
