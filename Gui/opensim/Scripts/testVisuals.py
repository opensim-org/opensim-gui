# import needed packages
# swing is the generic GUI kit for Java, allows for I/O and creating windows if needed
# lang is Java language core libraries e.g String, Math, ..
# modeling is the wrapping of the OpenSim API, so OpenSim::Model would be referenced here as modeling.Model
# view is some module in the GUI that exposes convenient inetrfaces for scripting

#load model
addModel("Scripts/testData/BothLegs.osim")

model = getCurrentModel()

#Color pelvis green
pelvis = findObject(model, "Body", "pelvis") 
setObjectColor(pelvis, [0.0, 0.0, 1.0])

#Make femur half transparent
femur = findObject(model, "Body", "femur") 
setObjectOpacity(femur, 0.5)

# orient view along x axis
gfxWindowSendKey('x')




