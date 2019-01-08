# Create an empty model for pendulum
model = modeling.Model()
model.setName("pendulum")

# get the model's Ground (inertial) reference frame
ground = model.getGround()

# define rod dimensions
length = 1.0;
radius = 0.025;

# add rigid rod (Body) of the pendulum with cylinder geometry
rod = addBodyToModel(model, 'rod')
attachGeometryWithOffset(rod, modeling.Cylinder(radius, length/2))
