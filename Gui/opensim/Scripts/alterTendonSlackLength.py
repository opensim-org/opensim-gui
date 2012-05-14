# This example script shows how to change the attributes of the muscles for the current model 
# (tendonSlackLength in this example).

# Get handle to current model in GUI
oldModel = getCurrentModel()

# Create a fresh copy
myModel = modeling.Model(oldModel)

# Initialize the copy, if values needed to be set in state
# pass along the variable myState returned by initSystem
myState = myModel.initSystem()

# Name the copy for later display in the GUI
oldName = oldModel.getName()
myModel.setName(oldName+"_longerTSL")

# A scale factor for tendonSlackLength of muscles
tendonSlackLengthScale = 1.5

# Change TendonSlackLength for all muscles in the model
for i in range(myModel.getMuscles().getSize()):
	currentMuscle = myModel.getMuscles().get(i)
	oldSL = currentMuscle.getTendonSlackLength()
	currentMuscle.setTendonSlackLength(oldSL * tendonSlackLengthScale)

#get full path name of original model
fullPathName = oldModel.getInputFileName()

#Change pathname to output file name
newPathName = fullPathName.replace('.osim', '_longerTSL.osim')
myModel.print(newPathName)

#Add model to GUI
addModel(newPathName)




