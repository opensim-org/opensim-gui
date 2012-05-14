# This example script demonstrates control of visuals. Loads a model 
# (Both Legs with Muscles), changes the color of the, makes the right 
# femur transparent, and aligns the model view with the x axis.

# Load model
addModel(getScriptsDir()+"/testData/BothLegs.osim")

# Get a handle to the current model (the one just loaded)
model = getCurrentModel()

# Color pelvis blue
pelvis = findObject(model, "Body", "pelvis") 
setObjectColor(pelvis, [0.0, 0.0, 1.0])

# Make femur half transparent
femur = findObject(model, "Body", "femur") 
setObjectOpacity(femur, 0.5)

# Orient view along x axis
gfxWindowSendKey('x')




