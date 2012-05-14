# This example increases the maxIsometricForce of all the muscles in the currently loaded model.
# A pop-up dialog displays a confirmation with the name of the new model.

# Get handle to current model in GUI
oldModel = getCurrentModel()

# Create a fresh copy
myModel = modeling.Model(oldModel)

# Initialize the copy
myModel.initSystem()

# Name the copy for later showing in GUI
oldName = oldModel.getName()
myModel.setName(oldName+"_Stronger")

# Define a scale factor for MaxIsometricForce of muscles
scaleFactor = 1.2

# Apply scale factor to MaxIsometricForce
for i in range(myModel.getMuscles().getSize()):
	currentMuscle = myModel.getMuscles().get(i)
	currentMuscle.setMaxIsometricForce(currentMuscle.getMaxIsometricForce()*scaleFactor)

# Save resulting model
fullName = oldModel.getInputFileName()
newName = fullName.replace('.osim', '_stronger.osim')
myModel.print(newName)

#Popup a dialog to show the file name used to save the model
win = swing.JFrame("Confirm")
dLabel = swing.JLabel("Wrote stronger model to file "+newName)
win.getContentPane().add(dLabel)
win.pack()
win.show()







