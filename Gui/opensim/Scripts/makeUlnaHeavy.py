# This example shows to to create a modified version of a model that is loaded in the GUI. 
# The script increases the mass of the ulna. The modified model is then loaded in the GUI.

# NOTE: The Arm26 model must be loaded and current in the GUI to run this script.

# Get handle to current model in GUI
oldModel = getCurrentModel()

# Create a fresh copy
myModel = modeling.Model(oldModel)

# Initialize the copy, if values need to be set in the model's state
# pass along the variable myState  returned by initSystem
myState = myModel.initSystem()

# Name the copy for later display in the GUI
oldName = oldModel.getName()
myModel.setName(oldName+"_heavier")

# A scale factor for mass of forarm
massScale = 3

# Change mass of forarm in the model
forearm = myModel.getBodySet().get("r_ulna_radius_hand")
forearm.setMass(forearm.getMass() * massScale)

# Get full path name of original.old model
fullPathName = oldModel.getInputFileName()

# Change the name of the modified model
newName = fullPathName.replace('.osim', '_heavier.osim')
myModel.print(newName)

# Load the model in the GUI
addModel(newName)




