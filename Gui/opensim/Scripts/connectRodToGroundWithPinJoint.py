#connectRodToGroundWithPinJoint.py

# define Frames where joints will attach
P_in_g = addOffsetToFrame(ground, 'P_in_ground', modeling.Vec3(0,length,0))
C_in_rod = addOffsetToFrame(rod, 'C_in_rod')

# connect rod to the ground
pin = connectBodyWithJoint(model, P_in_g, C_in_rod, 'pin', 'PinJoint')
pin.getCoordinate().setName('theta')

model.finalizeConnections()

guiModel = model.clone()
guiModel.initSystem()

loadModel(guiModel)
