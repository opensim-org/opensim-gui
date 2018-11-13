# Create an empty model
model = modeling.Model()
model.setName("pendulum")
ground = model.getGround()

# rod dimensions
length = 1.0;
radius = 0.025;

# add body segments which are the rigid rods of the pendulum
rod1 = addBodyToModel(model, 'rod1')
attachGeometryWithOffset(rod1, modeling.Cylinder(radius, length/2))

# define Frames where joints will attach
j1_in_ground = addOffsetToFrame(ground, 'j1_in_ground', modeling.Vec3(0,2,0))
j1_in_rod1 = addOffsetToFrame(rod1, 'j1_in_rod1')
j2_in_rod1 = addOffsetToFrame(rod1, 'j2_in_rod1')

# repeat to add rod2
rod2 = addBodyToModel(model, 'rod2')
attachGeometryWithOffset(rod2, modeling.Cylinder(radius, length/2))
j2_in_rod2 = addOffsetToFrame(rod2, 'j2_in_rod2')

# connect rods to the ground and each other by Joints
j1 = connectBodyWithJoint(model, j1_in_ground, j1_in_rod1, 'j1', 'PinJoint')
j1.getCoordinate().setName('theta1')
j2 = connectBodyWithJoint(model, ground, j2_in_rod2, 'j2', 'PinJoint')
j2.getCoordinate().setName('theta2')

# create the underlying system of equations
state = model.initSystem()

loadModel(model)

