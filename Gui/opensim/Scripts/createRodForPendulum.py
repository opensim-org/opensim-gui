#createRod1ForPendulum.py

# Create an empty model for pendulum
model = modeling.Model()
model.setName("pendulum")
ground = model.getGround()

# define rod dimensions
length = 1.0;
radius = 0.025;

# add body segments which are the rigid rods of the pendulum
rod = addBodyToModel(model, 'rod')
attachGeometryWithOffset(rod, modeling.Cylinder(radius, length/2))
